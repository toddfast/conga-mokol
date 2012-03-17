package com.conga.tools.mokol.plugin.base;

import com.conga.tools.mokol.CommandClassFactory;
import com.conga.tools.mokol.Plugin;
import com.conga.tools.mokol.Shell;
import com.conga.tools.mokol.ShellException;

/**
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
		return getClass().getName()
			.substring(0,getClass().getName().lastIndexOf("."));
	}


	/**
	 *
	 *
	 */
	@Override
	public String getVersion() {
		return "N/A";
	}


	/**
	 *
	 *
	 */
	@Override
	public void initialize(Shell shell) throws ShellException {
		shell.aliasCommand("exit",new CommandClassFactory(ExitCommand.class));
		shell.aliasCommand("quit",new CommandClassFactory(ExitCommand.class));
		shell.aliasCommand("help",new CommandClassFactory(HelpCommand.class));
		shell.aliasCommand("?",new CommandClassFactory(HelpCommand.class));
		shell.aliasCommand("error",
			new CommandClassFactory(ShowLastErrorCommand.class));
	}
}
