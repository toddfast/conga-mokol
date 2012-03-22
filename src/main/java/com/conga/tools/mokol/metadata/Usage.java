package com.conga.tools.mokol.metadata;

import com.conga.tools.mokol.metadata.ExampleDescriptor;
import com.conga.tools.mokol.metadata.SwitchDescriptor;
import java.util.List;
import java.util.Map;

/**
 * Command usage metadata
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
	public List<ExampleDescriptor> getExamples();


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
