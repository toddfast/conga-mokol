package com.conga.tools.mokol.spi;

import com.conga.tools.mokol.CommandIntrospector;
import com.conga.tools.mokol.ShellException;
import java.util.List;

/**
 * A command that can be executed by the shell
 *
 * @author Todd Fast
 */
public abstract class Command
{
	/**
	 * The primary command entry point. Command authors should implement this
	 * method to perform whatever work the command is designed to do.
	 *
	 *
	 */
	public abstract void execute(CommandContext context,
		List<String> args)
		throws ShellException;


//	/**
//	 *
//	 *
//	 */
//	protected void wrongNumberOfParameters(int min, int max, int actual) {
//		throw new IllegalArgumentException("Wrong number of parameters (min="+
//			min+", max="+max+", actual="+actual+")");
//	}


	/**
	 * Returns the usage information for this command. Note, a command will
	 * be instantiated and then discarded to provide this information.
	 *
	 *
	 */
	public Usage getUsage(CommandContext context) {
		if (usage==null) {
			usage=CommandIntrospector.getUsageDescriptor(
				context,this.getClass());
		}

		return usage;
	}




	////////////////////////////////////////////////////////////////////////////
	// Fields
	////////////////////////////////////////////////////////////////////////////

	private Usage usage;
}
