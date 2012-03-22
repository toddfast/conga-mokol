package com.conga.tools.mokol.spi.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Describes a command 
 *
 * @author Todd Fast
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Command {

	/**
	 * The alias of the command
	 *
	 */
	public String alias();


	/**
	 *
	 *
	 */
	public Class<? extends com.conga.tools.mokol.spi.Command> command()
		default com.conga.tools.mokol.spi.Command.class;
}
