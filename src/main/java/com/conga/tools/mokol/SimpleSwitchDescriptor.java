package com.conga.tools.mokol;

import java.lang.reflect.Method;
import java.util.List;

/**
 * A trivial implementation of {@link SwitchDescriptor}
 * 
 * @author Todd Fast
 */
public class SimpleSwitchDescriptor implements SwitchDescriptor {

	/**
	 *
	 *
	 */
	public SimpleSwitchDescriptor() {
		super();
	}


	/**
	 *
	 *
	 */
	public String getName() {
		return name;
	}

	/**
	 *
	 *
	 */
	public void  setName(String value) {
		name=value;
	}


	/**
	 *
	 *
	 */
	public String getAbbreviation() {
		return abbreviation;
	}


	/**
	 *
	 *
	 */
	public void setAbbreviation(String value) {
		abbreviation=value;
	}


	/**
	 *
	 *
	 */
	public List<Parameter> getParameters() {
		return parameters;
	}


	/**
	 *
	 *
	 */
	public void setParameters(List<Parameter> value) {
		parameters=value;
	}


	/**
	 *
	 *
	 */
	public String getHelp() {
		return help;
	}


	/**
	 *
	 *
	 */
	public void setHelp(String value) {
		help=value;
	}


	/**
	 *
	 *
	 */
	public Method getMethod() {
		return method;
	}


	/**
	 *
	 *
	 */
	public void setMethod(Method value) {
		method=value;
	}




	////////////////////////////////////////////////////////////////////////////
	// Fields
	////////////////////////////////////////////////////////////////////////////

	private String name;
	private String abbreviation;
	private List<Parameter> parameters;
	private String help;
	private Method method;
}
