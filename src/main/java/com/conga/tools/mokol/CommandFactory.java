package com.conga.tools.mokol;

import com.conga.tools.mokol.Shell.CommandContext;
import java.lang.reflect.Method;

/**
 *
 * @author Todd Fast
 */
public abstract class CommandFactory {

	/**
	 *
	 *
	 */
	public abstract Class<? extends Command> getCommandClass(CommandContext context);


	/**
	 *
	 *
	 */
	public abstract Command newInstance(CommandContext context)
		throws ShellException;




	////////////////////////////////////////////////////////////////////////////
	// Fields
	////////////////////////////////////////////////////////////////////////////

	private SimpleUsage usage;
}
