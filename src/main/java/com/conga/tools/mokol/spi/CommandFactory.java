package com.conga.tools.mokol.spi;

import com.conga.tools.mokol.ShellException;

/**
 *
 * @author Todd Fast
 */
public abstract class CommandFactory {

	/**
	 *
	 *
	 */
	public abstract Class<? extends Command> getCommandClass(
		CommandContext context);


	/**
	 *
	 *
	 */
	public abstract Command newInstance(CommandContext context)
		throws ShellException;
}
