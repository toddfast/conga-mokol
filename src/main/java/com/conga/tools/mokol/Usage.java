package com.conga.tools.mokol;

import java.util.Map;

/**
 *
 * @author Todd Fast
 */
public interface Usage {

	/**
	 * 
	 * 
	 */
	public String getShortDescription();


	/**
	 *
	 *
	 */
	public Map<String,SwitchDescriptor> getSwitchesByAbbreviation();


	/**
	 *
	 *
	 */
	public Map<String,SwitchDescriptor> getSwitchesByName();
}
