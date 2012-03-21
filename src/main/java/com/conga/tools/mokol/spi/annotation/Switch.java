package com.conga.tools.mokol.spi.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Describes a command switch
 *
 * @author Todd Fast
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Switch {

	/**
	 * The long name of the switch
	 *
	 */
	public String name() default "";


	/**
	 * The abbreviation
	 *
	 */
	public String abbreviation() default "";
}
