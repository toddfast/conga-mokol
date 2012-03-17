package com.conga.tools.mokol;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A trivial implementation of {@link Usage}
 *
 * @author Todd Fast
 */
public class SimpleUsage implements Usage {

	/**
	 * 
	 * 
	 */
	public SimpleUsage() {
		super();
	}


	/**
	 * 
	 * 
	 */
	@Override
	public String getShortDescription() {
		return description;
	}


	/**
	 *
	 *
	 */
	public void setShortDescription(String value) {
		description=value;
	}


	/**
	 *
	 *
	 */
	@Override
	public Map<String,SwitchDescriptor> getSwitchesByAbbreviation() {
		return Collections.unmodifiableMap(abbreviationDescriptors);
	}


	/**
	 *
	 *
	 */
	@Override
	public Map<String,SwitchDescriptor> getSwitchesByName() {
		return Collections.unmodifiableMap(nameDescriptors);
	}


	/**
	 *
	 *
	 */
	public void addSwitch(SwitchDescriptor descriptor) {
		if (descriptor==null) {
			throw new IllegalArgumentException(
				"Parameter \"descriptor\" cannot be null");
		}

		if (descriptor.getAbbreviation()==null) {
			throw new IllegalArgumentException(
				"Parameter \"descriptor.getAbbreviation()\" cannot be null");
		}

		if (descriptor.getName()==null) {
			throw new IllegalArgumentException(
				"Parameter \"descriptor.getName()\" cannot be null");
		}

		abbreviationDescriptors.put(descriptor.getAbbreviation(),descriptor);
		nameDescriptors.put(descriptor.getName(),descriptor);
	}




	////////////////////////////////////////////////////////////////////////////
	// Fields
	////////////////////////////////////////////////////////////////////////////

	private String description;
	private Map<String,SwitchDescriptor> nameDescriptors=
		new HashMap<String,SwitchDescriptor>();
	private Map<String,SwitchDescriptor> abbreviationDescriptors=
		new HashMap<String,SwitchDescriptor>();
}
