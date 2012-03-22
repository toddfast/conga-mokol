package com.conga.tools.mokol.metadata;

import java.lang.reflect.Method;
import java.util.List;

/**
 *
 * @author Todd Fast
 */
public interface SwitchDescriptor {

	/**
	 *
	 *
	 */
	public String getName();


	/**
	 *
	 *
	 */
	public String getAbbreviation();


	/**
	 *
	 *
	 */
	public List<Parameter> getParameters();


	/**
	 *
	 *
	 */
	public String getHelp();


	/**
	 *
	 *
	 */
	public Method getMethod();




	////////////////////////////////////////////////////////////////////////////
	// Inner types
	////////////////////////////////////////////////////////////////////////////

	/**
	 *
	 *
	 */
	public static interface Parameter {

		/**
		 *
		 *
		 */
		public Class<?> getType();


		/**
		 *
		 *
		 */
		public String getHelp();
	}
}
