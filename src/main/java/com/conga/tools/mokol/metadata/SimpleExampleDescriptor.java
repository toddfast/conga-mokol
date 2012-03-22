package com.conga.tools.mokol.metadata;

/**
 * Trivial implementation of {@link ExampleDescriptor}
 *
 * @author Todd Fast
 */
public class SimpleExampleDescriptor implements ExampleDescriptor {

	/**
	 *
	 *
	 */
	public SimpleExampleDescriptor() {
		super();
	}


	/**
	 *
	 *
	 */
	public SimpleExampleDescriptor(String description, String example) {
		this();
		this.description=description;
		this.example=example;
	}


	/**
	 *
	 *
	 */
	@Override
	public String getDescription() {
		return description;
	}


	/**
	 *
	 *
	 */
	@Override
	public String getExample() {
		return example;
	}




	////////////////////////////////////////////////////////////////////////////
	// Fields
	////////////////////////////////////////////////////////////////////////////

	private String description;
	private String example;
}
