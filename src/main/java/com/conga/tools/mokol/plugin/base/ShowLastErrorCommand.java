package com.conga.tools.mokol.plugin.base;

import com.conga.tools.mokol.Command;
import com.conga.tools.mokol.Environment;
import com.conga.tools.mokol.Shell;
import com.conga.tools.mokol.ShellException;
import java.util.List;

/**
 *
 * @author Todd Fast
 */
public class ShowLastErrorCommand extends Command {

	@Override
	public void execute(Shell.CommandContext context, List<String> args)
			throws ShellException {

		Exception e=context.getShell().getEnvironmentValue(
			Environment.ENV_LAST_ERROR,Exception.class);

		if (e!=null) {
			e.printStackTrace(context.writer());
			context.printf("\n");
		}
	}

	public String getUsage() {
		return "Show the stack trace of the last error";
	}
}
