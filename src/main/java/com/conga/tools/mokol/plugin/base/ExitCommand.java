package com.conga.tools.mokol.plugin.base;

import com.conga.tools.mokol.spi.Command;
import com.conga.tools.mokol.ShellException;
import com.conga.tools.mokol.CommandContext;
import com.conga.tools.mokol.spi.annotation.Help;
import java.util.List;

/**
 * Does what it says: exits the shell
 *
 * @author Todd Fast
 */
@Help("Exit this utility")
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
}
