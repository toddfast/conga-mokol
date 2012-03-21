package com.conga.tools.mokol.spi;

import com.conga.tools.mokol.util.TypeConverter;
import com.conga.tools.mokol.CommandIntrospector;
import com.conga.tools.mokol.ShellException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A command that uses metadata in annotations to generate usage information,
 * as well as set argument values on the command instance prior to execution in
 * {@link #doExecute}
 *
 * @author Todd Fast
 */
public abstract class AnnotatedCommand extends Command {

	/**
	 * Introspects the current command instance for switch metadata and sets
	 * switch values on corresponding mutator methods before calling
	 * {@link #doExecute} to execute the command proper.
	 *
	 */
	public final void execute(CommandContext context,
			List<String> switchesAndArgs)
			throws ShellException {

		// Get all the switch descriptors
		Usage usageDescriptor=
			CommandIntrospector.getUsageDescriptor(context,this.getClass());
		assert usageDescriptor!=null:
			"Usage descriptor should not be null";

		Map<SwitchDescriptor,List<String>> switchArgs=
			new HashMap<SwitchDescriptor,List<String>>();

		boolean switchesDone=false;

		List<String> reducedArgs=new ArrayList<String>();

		for (int i=0; i<switchesAndArgs.size(); i++) {
			String arg=switchesAndArgs.get(i);

			// Detect if this is a switch
			String switchName=null;
			SwitchDescriptor switchDescriptor=null;

			if (!switchesDone && arg.startsWith("--")) {
				switchName=arg.substring(2);
				switchDescriptor=
					usageDescriptor.getSwitchesByName().get(switchName);
			}
			else
			if (!switchesDone && arg.startsWith("-")) {
				switchName=arg.substring(1);
				switchDescriptor=
					usageDescriptor.getSwitchesByAbbreviation().get(switchName);
			}

			// Handle the switch
			if (switchName!=null) {
				if (switchDescriptor==null) {
					throw new IllegalArgumentException(
						"Unknown switch \""+arg+"\"");
				}

				// Consume the remaining args
				List<String> group=new ArrayList<String>();
				for (int j=0; j<switchDescriptor.getParameters().size(); j++) {
					group.add(switchesAndArgs.get(++i));
				}

				switchArgs.put(switchDescriptor,group);
			}
			else {
				// Note that we're done with switches
				switchesDone=true;
				reducedArgs.add(arg);
			}
		}

		// Process the switch args
		for (Map.Entry<SwitchDescriptor,List<String>> entry:
				switchArgs.entrySet()) {

			SwitchDescriptor descriptor=entry.getKey();

			List<SwitchDescriptor.Parameter> paramDescriptors=
				descriptor.getParameters();
			List<String> paramStrings=entry.getValue();
			List<Object> params=new ArrayList<Object>();

			// Convert each of the string parameters to the target
			// parameter type
			for (int i=0; i<paramDescriptors.size(); i++) {
				Object convertedType=TypeConverter.asType(
					paramDescriptors.get(i).getType(),paramStrings.get(i));

//				System.out.println("Converted param "+i+" to "+
//					paramDescriptors.get(i).getType()+": "+convertedType+" ("+
//					convertedType.getClass().getName()+")");

				params.add(convertedType);
			}

			// Call the switch method
			Method method=descriptor.getMethod();
			try {
				method.invoke(this,(Object[])params.toArray());
			}
			catch (Exception e) {
				throw new ShellException(String.format(
					"Error setting switch --%s (-%s): %s",descriptor.getName(),
						descriptor.getAbbreviation(),e.getMessage()),
					e);
			}
		}

		// Now execute the command
		doExecute(context,reducedArgs);
	}


	/**
	 * The primary command entry point. Command authors should implement this
	 * method to perform whatever work the command is designed to do.
	 *
	 * @param context
	 * @param args
	 */
	protected abstract void doExecute(CommandContext context,
		List<String> args)
		throws ShellException;


	/**
	 * Returns usage information introspected from the annotations on
	 * this class
	 *
	 */
	@Override
	public final Usage getUsage(CommandContext context) {
		if (usageDescriptor==null) {
			usageDescriptor=
				CommandIntrospector.getUsageDescriptor(context,this.getClass());
		}

		return usageDescriptor;
	}




	////////////////////////////////////////////////////////////////////////////
	// Fields
	////////////////////////////////////////////////////////////////////////////

	private	Usage usageDescriptor;
}
