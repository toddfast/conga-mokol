package com.conga.tools.mokol.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
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


//	/**
//	 * The number of arguments to the switch
//	 *
//	 */
//	public int length();
}
