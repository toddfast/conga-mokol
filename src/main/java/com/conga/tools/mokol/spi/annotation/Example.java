package com.conga.tools.mokol.spi.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A single example of command usage
 *
 * @author Todd Fast
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Example {

	/**
	 *
	 *
	 */
	public String value() default "";


	/**
	 *
	 *
	 */
	public String description() default "";
}
