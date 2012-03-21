package com.conga.tools.mokol.spi.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author Todd Fast
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,ElementType.TYPE,ElementType.PARAMETER})
public @interface Help {

	/**
	 *
	 *
	 */
	public String value();


	/**
	 *
	 *
	 */
	public Example[] examples() default {};
}
