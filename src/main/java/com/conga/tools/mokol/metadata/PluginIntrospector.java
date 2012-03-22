package com.conga.tools.mokol.metadata;

import com.conga.tools.mokol.CommandClassFactory;
import com.conga.tools.mokol.CommandContext;
import com.conga.tools.mokol.CommandFactory;
import com.conga.tools.mokol.ShellException;
import com.conga.tools.mokol.spi.Command;
import com.conga.tools.mokol.spi.Plugin;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * 
 * 
 * @author Todd Fast
 */
public class PluginIntrospector {

	/**
	 * Can't instantiate
	 *
	 */
	private PluginIntrospector() {
		super();
	}


	/**
	 * 
	 *
	 * @param	context
	 * @param	plugin
	 * @return
	 * @throws	SwitchDefinitionException
	 */
	public static Map<String,CommandDescriptor> getCommandDescriptors(
			Plugin plugin)
			throws SwitchDefinitionException {

		if (plugin==null) {
			throw new IllegalArgumentException(
				"Parameter \"plugin\" cannot be null");
		}

		final Class<? extends Plugin> pluginClass=plugin.getClass();

		// Get the list of superclasses starting with the subclass
		Class<?> chainClass=pluginClass;
		List<Class<?>> superclasses=new ArrayList<Class<?>>();
		do {
			superclasses.add(chainClass);
		}
		while ((chainClass=chainClass.getSuperclass())!=null);

		// Reverse so that we start with the superclass
		Collections.reverse(superclasses);

		Map<String,CommandDescriptor> descriptors=
			new TreeMap<String,CommandDescriptor>();

		// Check that all fields are either static or static final
		for (Class<?> clazz: superclasses) {

			// First, look for class-level command annotations
			com.conga.tools.mokol.spi.annotation.Plugin pluginAnnotation=
				clazz.getAnnotation(
					com.conga.tools.mokol.spi.annotation.Plugin.class);
			if (pluginAnnotation!=null) {
				// Get the command annotations
				com.conga.tools.mokol.spi.annotation.Command[]
					commandAnnotations=pluginAnnotation.commands();
				for (int i=0; i<commandAnnotations.length; i++) {
					String alias=commandAnnotations[i].alias();
					Class<? extends Command> commandClass=
						commandAnnotations[i].command();

					if (commandClass==Command.class) {
						throw new PluginDefinitionException(
							String.format("Command declaration for alias "+
							"\"%s\" on plugin class %s must also specify a "+
							"command class",
							alias,pluginClass.getName()));
					}
					else {
						// Make sure this name isn't already taken
						if (descriptors.containsKey(alias)) {
							throw new PluginDefinitionException(
								String.format("Alias \"%s\" specified by "+
									"command declaration on plugin class "+
									"%s.%s() conflicts with an existing alias",
									alias,pluginClass.getName()));
						}
						else {
							// Save the command
							descriptors.put(alias,
								new IntrospectedCommandDescriptor(alias,
									new CommandClassFactory(commandClass)));
						}
					}
				}
			}

			// Look for method-level command annotations
			Method[] methods=clazz.getDeclaredMethods();
			for (Method method: methods) {
				// Ignore fields created by the VM
				if (method.isSynthetic() || method.isBridge())
					continue;

				if (!Modifier.isPublic(method.getModifiers()))
					continue;

				com.conga.tools.mokol.spi.annotation.Command commandAnnotation=
					method.getAnnotation(
						com.conga.tools.mokol.spi.annotation.Command.class);
				if (commandAnnotation==null)
					continue;

				// Check the naming convention
				String methodName=method.getName();

				// Check the arity and parameter type
				Class<?>[] paramTypes=method.getParameterTypes();

				if (paramTypes.length!=1 ||
						!paramTypes[0].equals(CommandContext.class)) {
					throw new PluginDefinitionException(String.format(
						"Command factory method %s.%s() must take a single "+
						"parameter of type %s",
						pluginClass.getName(),methodName,
						CommandContext.class.getName()));
				}

				String alias=commandAnnotation.alias();
				if (alias==null || alias.trim().isEmpty()) {
					// Make a friendly value
					alias=methodName;
					if (Character.isUpperCase(alias.charAt(0))) {
						// The whole value is uppercase, so convert to lowercase
						alias=alias.toLowerCase();
					}
				}

				// Make sure this name isn't already taken
				if (descriptors.containsKey(alias)) {
					throw new PluginDefinitionException(
						String.format("Alias \"%s\" specified by command "+
							"factory method %s.%s() conflicts with an "+
							"existing alias",
							alias,pluginClass.getName(),methodName));
				}

				// Get the help description
//				String help=null;
//				Help helpAnnotation=method.getAnnotation(Help.class);
//				if (helpAnnotation!=null) {
//					help=helpAnnotation.value();
//				}

				// Create the factory
				CommandFactory factory=
					new ReflectiveCommandFactory(plugin,method);

				// Create the descriptor
				IntrospectedCommandDescriptor descriptor=
					new IntrospectedCommandDescriptor(alias,factory);

				descriptors.put(alias,descriptor);
			}
		}

		return descriptors;
	}




	////////////////////////////////////////////////////////////////////////////
	// Inner types
	////////////////////////////////////////////////////////////////////////////

	/**
	 *
	 *
	 */
	public static class ReflectiveCommandFactory
			extends CommandFactory {

		/**
		 *
		 *
		 */
		public ReflectiveCommandFactory(Plugin plugin, Method method) {
			super();

			if (!Command.class.isAssignableFrom(method.getReturnType())) {
				throw new IllegalArgumentException(
					String.format("Return type of method %s.%s() must "+
					"extend %s",method.getDeclaringClass().getName(),
					method.getName(),Command.class.getName()));
			}

			this.plugin=plugin;
			this.method=method;
		}

		/**
		 *
		 *
		 */
		public Plugin getPlugin() {
			return plugin;
		}

		/**
		 *
		 *
		 */
		public Method getMethod() {
			return method;
		}

		/**
		 *
		 *
		 */
		@Override
		public Class<? extends Command> getCommandClass(
				CommandContext context) {
			return (Class<? extends Command>)
				getMethod().getReturnType();
		}

		/**
		 *
		 * 
		 */
		@Override
		public Command newInstance(CommandContext context)
				throws ShellException {

			try {
				return Command.class.cast(
					getMethod().invoke(getPlugin(),context));
			}
			catch (Exception e) {
				throw new ShellException("Could not create command",e);
			}
		}




		////////////////////////////////////////////////////////////////////////
		// Fields
		////////////////////////////////////////////////////////////////////////

		private Plugin plugin;
		private Method method;
	}


	/**
	 *
	 *
	 */
	public static class IntrospectedCommandDescriptor
			implements CommandDescriptor {

		/**
		 *
		 *
		 */
		private IntrospectedCommandDescriptor(String alias,
				CommandFactory factory) {
			super();
			this.alias=alias;
			this.factory=factory;
		}


		/**
		 *
		 *
		 */
		@Override
		public String getAlias() {
			return alias;
		}


		/**
		 *
		 *
		 */
		@Override
		public CommandFactory getFactory() {
			return factory;
		}




		////////////////////////////////////////////////////////////////////////
		// Fields
		////////////////////////////////////////////////////////////////////////

		private String alias;
		private CommandFactory factory;
	}
}
