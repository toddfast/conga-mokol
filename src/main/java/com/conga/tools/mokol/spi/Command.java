package com.conga.tools.mokol.spi;

import com.conga.tools.mokol.CommandContext;
import com.conga.tools.mokol.CommandBase;
import com.conga.tools.mokol.ShellException;
import java.util.List;

/**
 * A command that can be executed by the shell
 *
 * @author Todd Fast
 */
public abstract class Command extends CommandBase
{
	/**
	 * The primary command entry point. Command authors should implement this
	 * method to perform whatever work the command is designed to do.
	 *
	 */
	protected abstract void execute(CommandContext context,
		List<String> args)
		throws ShellException;
}
