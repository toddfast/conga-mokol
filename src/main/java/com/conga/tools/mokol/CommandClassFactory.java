package com.conga.tools.mokol;

import com.conga.tools.mokol.Shell.CommandContext;

/**
 * 
 *
 * @author Todd Fast
 */
public class CommandClassFactory extends CommandFactory {

	/**
	 *
	 *
	 */
	public CommandClassFactory(Class<? extends Command> commandClass) {
		super();
		if (commandClass==null) {
			throw new IllegalArgumentException(
				"Parameter \"commandClass\" cannot be null");
		}

		this.commandClass=commandClass;
	}


	/**
	 *
	 *
	 */
	@Override
	public Class<? extends Command> getCommandClass(CommandContext context) {
		return commandClass;
	}


	/**
	 *
	 *
	 */
	@Override
	public Command newInstance(CommandContext context)
			throws ShellException {

		// Execute the command
		try {
			Command command=commandClass.newInstance();
			return command;
		}
		catch (Exception e) {
			throw new ShellException("Could not instantiate command instance "+
				"from class "+commandClass.getName(),e);
		}
	}




	////////////////////////////////////////////////////////////////////////////
	// Fields
	////////////////////////////////////////////////////////////////////////////

	private Class<? extends Command> commandClass;
}
