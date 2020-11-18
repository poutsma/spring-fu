package org.springframework.fu.arjen;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.Environment;

/**
 * @author Arjen Poutsma
 */
public class DefaultDsl {

	private GenericApplicationContext context;

	protected DefaultDsl(GenericApplicationContext context) {
		this.context = context;
	}

	protected GenericApplicationContext context() {
		return this.context;
	}

	/**
	 * Get a reference to the bean by type.
	 * @param beanClass type the bean must match, can be an interface or superclass
	 */
	public <T> T ref(Class<T> beanClass) {
		return this.context.getBean(beanClass);
	}

	/**
	 * Get a reference to the bean by type + name.
	 * @param beanClass type the bean must match, can be an interface or superclass
	 */
	public <T> T ref(Class<T> beanClass, String name) {
		return this.context.getBean(name, beanClass);
	}

	/**
	 * Shortcut the get the environment.
	 */
	public Environment env() {
		return context.getEnvironment();
	}

	/**
	 * Shortcut the get the active profiles.
	 */
	public List<String> profiles() {
		return Arrays.asList(context.getEnvironment().getActiveProfiles());
	}

}
