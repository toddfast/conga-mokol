package com.conga.tools.mokol.plugin.base;

import com.conga.tools.mokol.spi.Command;
import com.conga.tools.mokol.ShellException;
import com.conga.tools.mokol.CommandContext;
import com.conga.tools.mokol.spi.annotation.Help;
import java.util.List;

/**
 * Shows the last error caught by the shell
 *
 * @author Todd Fast
 */
@Help("Show the stack trace of the last error")
public class ShowLastErrorCommand extends Command {

	/**
	 *
	 * 
	 */
	@Override
	public void execute(CommandContext context, List<String> args)
			throws ShellException {

		Throwable e=context.getShell().getLastError();

		if (e!=null) {
			e.printStackTrace(context.writer());
			context.printf("\n");
		}
	}
}
