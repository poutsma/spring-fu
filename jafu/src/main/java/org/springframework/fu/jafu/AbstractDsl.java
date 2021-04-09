package org.springframework.fu.jafu;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.Environment;

/**
 * Base class for Jafu DSL.
 *
 * Make sure to invoke {@code super.initialize(context)} from {@link #initialize(GenericApplicationContext)} in
 * inherited classes to get the context initialized.
 *
 * @author Sebastien Deleuze
 */
public abstract class AbstractDsl {

	protected final GenericApplicationContext applicationContext;

	protected AbstractDsl(GenericApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	/**
	 * Get a reference to the bean by type.
	 * @param beanClass type the bean must match, can be an interface or superclass
	 */
	public <T> T ref(Class<T> beanClass) {
		return this.applicationContext.getBean(beanClass);
	}

	/**
	 * Get a reference to the bean by type + name.
	 * @param beanClass type the bean must match, can be an interface or superclass
	 */
	public <T> T ref(Class<T> beanClass, String name) {
		return this.applicationContext.getBean(name, beanClass);
	}

	/**
	 * Shortcut the get the environment.
	 */
	public Environment env() {
		return applicationContext.getEnvironment();
	}

	/**
	 * Shortcut the get the active profiles.
	 */
	public List<String> profiles() {
		return Arrays.asList(applicationContext.getEnvironment().getActiveProfiles());
	}

	protected <T> AbstractDsl enable(FeatureFunction<T> feature) {
		T t = feature.initialize(this.applicationContext);
		feature.afterConfiguration(t);
		return this;
	}

	protected <T> AbstractDsl enable(FeatureFunction<T> feature, Consumer<T> configuration) {
		T t = feature.initialize(this.applicationContext);
		configuration.accept(t);
		feature.afterConfiguration(t);
		return this;
	}


}
