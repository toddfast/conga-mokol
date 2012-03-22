package com.conga.tools.mokol.plugin.base;

import com.conga.tools.mokol.spi.annotation.Plugin;
import com.conga.tools.mokol.spi.annotation.Command;

/**
 * Initializes the base commands
 *
 * @author Todd Fast
 */
@Plugin(
	commands={
		@Command(alias="about", command=AboutCommand.class),
		@Command(alias="exit", command=ExitCommand.class),
		@Command(alias="quit", command=ExitCommand.class),
		@Command(alias="help", command=HelpCommand.class),
		@Command(alias="error", command=ShowLastErrorCommand.class)
	})
public class BasePlugin extends com.conga.tools.mokol.spi.Plugin {

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
}
