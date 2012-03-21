package com.conga.tools.mokol.plugin.base;

import com.conga.tools.mokol.spi.CommandClassFactory;
import com.conga.tools.mokol.spi.Plugin;
import com.conga.tools.mokol.Shell;
import com.conga.tools.mokol.ShellException;

/**
 * Initializes the basic commands
 *
 * @author Todd Fast
 */
public class BasePlugin implements Plugin {

	/**
	 *
	 *
	 */
	@Override
	public String getName() {
		return "Mokol Base Commands";
	}


	/**
	 *
	 *
	 */
	@Override
	public String getVersion() {
		return getClass().getPackage().getImplementationVersion();
	}


	/**
	 *
	 *
	 */
	@Override
	public void initialize(Shell shell) throws ShellException {
		shell.aliasCommand("about",new CommandClassFactory(AboutCommand.class));
		shell.aliasCommand("exit",new CommandClassFactory(ExitCommand.class));
		shell.aliasCommand("quit",new CommandClassFactory(ExitCommand.class));
		shell.aliasCommand("help",new CommandClassFactory(HelpCommand.class));
		shell.aliasCommand("error",new CommandClassFactory(
			ShowLastErrorCommand.class));
	}
}
