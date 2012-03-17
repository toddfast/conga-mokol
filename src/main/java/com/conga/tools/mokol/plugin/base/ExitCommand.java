package com.conga.tools.mokol.plugin.base;

import com.conga.tools.mokol.Command;
import com.conga.tools.mokol.Shell.CommandContext;
import com.conga.tools.mokol.ShellException;
import java.util.List;

/**
 *
 * @author Todd Fast
 */
public class ExitCommand extends Command {

	/**
	 *
	 *
	 */
	@Override
	public void execute(CommandContext context, List<String> args)
			throws ShellException {
		context.getShell().end();
	}


	/**
	 *
	 *
	 */
	public String getUsage() {
		return "Exit this utility";
	}
}
