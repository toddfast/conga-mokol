package com.conga.tools.mokol.plugin.base;

import com.conga.tools.mokol.Shell;
import com.conga.tools.mokol.spi.Command;
import com.conga.tools.mokol.ShellException;
import com.conga.tools.mokol.spi.CommandContext;
import com.conga.tools.mokol.spi.annotation.Help;
import java.util.List;
import org.fusesource.jansi.Ansi;

/**
 * Shows information about Mokol
 *
 * @author Todd Fast
 */
@Help("About this utility")
public class AboutCommand extends Command {

	@Override
	public void execute(CommandContext context, List<String> args)
			throws ShellException {

		context.printf(
			Ansi.ansi()
				.bold()
				.fg(Ansi.Color.YELLOW)
				.format("=")
				.fg(Ansi.Color.YELLOW)
				.format("^")
				.fg(Ansi.Color.CYAN)
				.format("@")
				.fg(Ansi.Color.MAGENTA)
				.format(".")
				.fg(Ansi.Color.CYAN)
				.format("@")
				.fg(Ansi.Color.YELLOW)
				.format("^")
				.fg(Ansi.Color.YELLOW)
				.format("=")
				.fg(Ansi.Color.DEFAULT)
				.bg(Ansi.Color.DEFAULT)
				.boldOff()
			.toString()+"\n");
		context.printf("Version %s\n",
			Shell.class.getPackage().getImplementationVersion());

		context.printf("http://bit.ly/whoismokol\n\n");
	}
}
