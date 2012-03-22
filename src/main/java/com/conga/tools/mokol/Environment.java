package com.conga.tools.mokol;

import com.conga.tools.mokol.util.TypeConverter;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/**
 *
 * @author Todd Fast
 */
public class Environment {

	/**
	 *
	 *
	 */
	protected Environment(Shell shell) {
		super();
		if (shell==null) {
			throw new IllegalArgumentException("Parameter \"shell\" "+
				"cannot be null");
		}

		this.shell=shell;
	}


	/**
	 *
	 *
	 */
	protected Map<String,Object> mutableMap() {
		return environment;
	}


	/**
	 *
	 *
	 */
	public Map<String,Object> map() {
		return Collections.unmodifiableMap(mutableMap());
	}


	/**
	 *
	 *
	 */
	public <T> T get(String key, Class<T> clazz) {
		return TypeConverter.as(clazz,mutableMap().get(key));
	}


	/**
	 *
	 *
	 */
	public void put(String key, Object value) {
		if (key==null || key.trim().isEmpty()) {
			throw new IllegalArgumentException("Parameter \"key\" cannot be "+
				"null or an empty string");
		}

		mutableMap().put(key,value);
	}


	/**
	 *
	 *
	 */
	public void remove(String key) {
		if (key==null || key.trim().isEmpty()) {
			throw new IllegalArgumentException("Parameter \"key\" cannot be "+
				"null or an empty string");
		}

		mutableMap().remove(key);
	}

	
	
	
	////////////////////////////////////////////////////////////////////////////
	// Fields
	////////////////////////////////////////////////////////////////////////////

	private Shell shell;
	private Map<String,Object> environment=Collections.synchronizedMap(
		new TreeMap<String,Object>());
}
