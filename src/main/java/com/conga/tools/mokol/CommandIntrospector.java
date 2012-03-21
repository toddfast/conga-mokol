package com.conga.tools.mokol;

import com.conga.tools.mokol.spi.ExampleDescriptor;
import com.conga.tools.mokol.spi.SwitchDescriptor;
import com.conga.tools.mokol.spi.Usage;
import com.conga.tools.mokol.spi.Command;
import com.conga.tools.mokol.spi.CommandContext;
import com.conga.tools.mokol.spi.annotation.Example;
import com.conga.tools.mokol.spi.annotation.Help;
import com.conga.tools.mokol.spi.annotation.Switch;
import java.beans.Introspector;
import java.lang.annotation.Annotation;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Introspects commands and returns usage information described by annotations
 *
 * @author Todd Fast
 */
public class CommandIntrospector {

	/**
	 * Can't instantiate
	 *
	 */
	private CommandIntrospector() {
		super();
	}


	/**
	 *
	 *
	 * @param context
	 * @param commandClass
	 * @return
	 * @throws SwitchDefinitionException
	 */
	public static Usage getUsageDescriptor(CommandContext context,
			Class<? extends Command> commandClass)
			throws SwitchDefinitionException {

		// Get the list of superclasses starting with the subclass
		Class<?> chainClass=commandClass;
		List<Class<?>> superclasses=new ArrayList<Class<?>>();
		do {
			superclasses.add(chainClass);
		}
		while ((chainClass=chainClass.getSuperclass())!=null);

		// Reverse so that we start with the superclass
		Collections.reverse(superclasses);

		Map<String,SwitchDescriptor> abbreviations=
			new TreeMap<String,SwitchDescriptor>();
		Map<String,SwitchDescriptor> descriptors=
			new TreeMap<String,SwitchDescriptor>();

		String commandHelp=null;
		List<ExampleDescriptor> examples=new ArrayList<ExampleDescriptor>();

		// Get the help for the command
		Help commandHelpAnnotation=commandClass.getAnnotation(Help.class);
		if (commandHelpAnnotation!=null) {
			commandHelp=commandHelpAnnotation.value();

			// Collect the examples
			if (commandHelpAnnotation.examples()!=null) {
				for (Example example:
						Arrays.asList(commandHelpAnnotation.examples())) {
					examples.add(new IntrospectedExampleDescriptor(
						example.value(),example.description()));
				}
			}
		}

		// Check that all fields are either static or static final
		for (Class<?> clazz: superclasses) {
			Method[] methods=clazz.getDeclaredMethods();
			for (Method method: methods) {
				// Ignore fields created by the VM
				if (method.isSynthetic() || method.isBridge())
					continue;

				if (!Modifier.isPublic(method.getModifiers()))
					continue;

				Switch switchAnnotation=method.getAnnotation(Switch.class);
				if (switchAnnotation==null)
					continue;

				// Check the naming convention
				String methodName=method.getName();
				if (!methodName.startsWith("set")) {
					// Illegal method value
					throw new SwitchDefinitionException(
						String.format("Command switch setter method %s.%s() "+
							"must start with \"set\"",
							clazz.getName(),methodName));
				}

				// Check the arity
				Class<?>[] paramTypes=method.getParameterTypes();

				// Get the number of arguments for the switch
				int numArgs=paramTypes.length;

				List<SwitchDescriptor.Parameter> parameters=
					new ArrayList<SwitchDescriptor.Parameter>();

				Annotation[][] parameterAnnotations=
					method.getParameterAnnotations();

				// Check the types
				for (int j=0; j<paramTypes.length; j++) {
					Class<?> paramClass=paramTypes[j];

					String paramHelp=paramClass.getSimpleName().toLowerCase();

					// Find the Help annotation, if any
					Annotation[] annotations=parameterAnnotations[j];
					for (Annotation annotation: annotations) {
						if (Help.class.isAssignableFrom(
								annotation.annotationType())) {
							paramHelp=((Help)annotation).value();
						}
					}

					parameters.add(
						new IntrospectedSwitchParameter(paramClass,paramHelp));

//					if (paramClass!=String.class) {
//						throw new SwitchDefinitionException(
//							String.format("Command switch setter method "+
//								"%s.%s() must take only parameters of type %s",
//								clazz.getName(),methodName,
//								String.class.getName()));
//					}
				}

				String name=switchAnnotation.name();
				String abbreviation=switchAnnotation.abbreviation();

				if (name==null || name.trim().isEmpty()) {

					// Method value starts with "set"
					assert methodName.startsWith("set"):
						"Method name should have started with \"set\"";

					// Make a friendly value
					name=Introspector.decapitalize(methodName.substring(3));
					if (Character.isUpperCase(name.charAt(0))) {
						// The whole value is uppercase, so convert to lowercase
						name=name.toLowerCase();
					}
				}

				// Make sure this name isn't already taken
				if (descriptors.containsKey(name)) {
					Method conflictingMethod=descriptors.get(name).getMethod();
					throw new SwitchDefinitionException(
						String.format("Name \"%s\" for command switch setter "+
							"method %s.%s() conflicts with an existing "+
							"switch method %s.%s(). Specify a switch name "+
							"explicitly to avoid this error.",
							name,clazz.getName(),methodName,
							conflictingMethod.getDeclaringClass().getName(),
							conflictingMethod.getName()));
				}

				// Make an abbreviation if necessary
				if (abbreviation==null || abbreviation.trim().isEmpty()) {
					char[] nameChars=name.toCharArray();

					// Use the first character
					StringBuilder letters=new StringBuilder(""+
						Character.toLowerCase(nameChars[0]));

					// Add any other uppercase letters
					for (int i=1; i<nameChars.length; i++) {
						char ch=nameChars[i];
						if (Character.isUpperCase(ch))
							letters.append(Character.toLowerCase(ch));
					}

					abbreviation=letters.toString();

					// We can just use the first character if it's not
					// already taken
					if (!abbreviations.containsKey(
							abbreviation.substring(0,1))) {
						abbreviation=abbreviation.substring(0,1);
					}
				}

				// Make sure this abbreviation isn't already taken
				if (abbreviations.containsKey(abbreviation)) {
//					// Make a unique abbreviation
//					int suffix=1;
//					String baseAbbreviation=abbreviation;
//					while (abbreviations.contains(abbreviation)) {
//						abbreviation=baseAbbreviation+suffix;
//					}

					Method conflictingMethod=
						abbreviations.get(abbreviation).getMethod();
					throw new SwitchDefinitionException(
						String.format("Abbreviation \"%s\" for command "+
							"switch setter method %s.%s() conflicts with "+
							"an existing switch method %s.%s(). Specify "+
							"an abbreviation explicitly to avoid this "+
							"error.",abbreviation,clazz.getName(),methodName,
							conflictingMethod.getDeclaringClass().getName(),
							conflictingMethod.getName()));
				}

				// Get the help description
				String help=null;
				Help helpAnnotation=method.getAnnotation(Help.class);
				if (helpAnnotation!=null) {
					help=helpAnnotation.value();
				}

				// Create the descriptor
				IntrospectedSwitchDescriptor descriptor=
					new IntrospectedSwitchDescriptor(name,abbreviation,
						parameters,help,method);

				abbreviations.put(abbreviation,descriptor);
				descriptors.put(name,descriptor);
			}
		}


		return new IntrospectedUsageDescriptor(commandHelp,examples,
			abbreviations,descriptors);

		// Return the result sorted by abbreviation
//		List<SwitchDescriptor> result=new ArrayList<SwitchDescriptor>();
//		for (Map.Entry<String,SwitchDescriptor> entry:
//				abbreviations.entrySet()) {
//			result.add(entry.getValue());
//		}
//
//		return result;
	}




	////////////////////////////////////////////////////////////////////////////
	// Inner types
	////////////////////////////////////////////////////////////////////////////

	/**
	 *
	 *
	 */
	private static class IntrospectedExampleDescriptor
			implements ExampleDescriptor {

		/**
		 *
		 *
		 * @param name
		 * @param abbreviation
		 * @param method
		 */
		private IntrospectedExampleDescriptor(String example,
				String description) {
			super();
			this.example=example;
			this.description=description;
		}

		@Override
		public String getExample() {
			return example;
		}

		@Override
		public String getDescription() {
			return description;
		}

		private String example;
		private String description;
	}


	/**
	 *
	 *
	 */
	private static class IntrospectedSwitchDescriptor
			implements SwitchDescriptor {

		/**
		 *
		 *
		 * @param name
		 * @param abbreviation
		 * @param method
		 */
		private IntrospectedSwitchDescriptor(String name, String abbreviation,
				List<Parameter> parameters, String help, Method method) {
			super();
			this.name=name;
			this.abbreviation=abbreviation;
			this.parameters=parameters;
			this.help=help;
			this.methodRef=new WeakReference<Method>(method);
		}


		/**
		 *
		 *
		 */
		@Override
		public String getName() {
			return name;
		}


		/**
		 *
		 *
		 */
		@Override
		public String getAbbreviation() {
			return abbreviation;
		}


		/**
		 *
		 *
		 */
		@Override
		public List<Parameter> getParameters() {
			return parameters;
		}


		/**
		 *
		 *
		 */
		@Override
		public String getHelp() {
			return help;
		}


		/**
		 *
		 *
		 */
		@Override
		public Method getMethod() {
			return methodRef.get();
		}


		private String name;
		private String abbreviation;
		private List<Parameter> parameters;
		private String help;
		private WeakReference<Method> methodRef;
	}


	/**
	 *
	 *
	 */
	private static class IntrospectedSwitchParameter
			implements SwitchDescriptor.Parameter {

		/**
		 *
		 *
		 */
		private IntrospectedSwitchParameter(Class<?> type, String help) {
			super();
			this.type=type;
			this.help=help;
		}

		/**
		 *
		 *
		 */
		@Override
		public Class<?> getType() {
			return type;
		}


		/**
		 *
		 *
		 */
		@Override
		public String getHelp() {
			return help;
		}

		private Class<?> type;
		private String help;
	}


	/**
	 *
	 *
	 */
	private static class IntrospectedUsageDescriptor
			implements Usage {

		/**
		 *
		 *
		 * @param description
		 * @param abbreviation
		 * @param method
		 */
		private IntrospectedUsageDescriptor(String description,
				List<ExampleDescriptor> examples,
				Map<String,SwitchDescriptor> switchesByAbbreviation,
				Map<String,SwitchDescriptor> switchesByName) {
			super();
			this.description=description;
			this.examples=examples;
			this.switchesByAbbreviation=switchesByAbbreviation;
			this.switchesByName=switchesByName;
		}


		/**
		 *
		 *
		 */
		@Override
		public String getShortDescription() {
			return description;
		}


		/**
		 *
		 *
		 */
		public List<ExampleDescriptor> getExamples() {
			return examples;
		}


		/**
		 *
		 *
		 */
		@Override
		public Map<String,SwitchDescriptor> getSwitchesByAbbreviation() {
			return switchesByAbbreviation;
		}


		/**
		 *
		 *
		 */
		@Override
		public Map<String,SwitchDescriptor> getSwitchesByName() {
			return switchesByName;
		}


		private String description;
		private List<ExampleDescriptor> examples;
		private Map<String,SwitchDescriptor> switchesByAbbreviation;
		private Map<String,SwitchDescriptor> switchesByName;
	}
}
