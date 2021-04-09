package org.springframework.fu.jafu;

import java.util.function.Consumer;
import java.util.function.Function;

import org.springframework.context.support.GenericApplicationContext;
import org.springframework.util.Assert;

/**
 * @author Arjen Poutsma
 */
final class DefaultFeatureFunction<T> implements FeatureFunction<T> {

	private final Function<GenericApplicationContext, T> factory;

	private final Consumer<T> afterConfiguration;

	public DefaultFeatureFunction(Function<GenericApplicationContext, T> factory, Consumer<T> afterConfiguration) {
		Assert.notNull(factory, "Factory must not be null");
		Assert.notNull(afterConfiguration, "Factory must not be null");
		this.factory = factory;
		this.afterConfiguration = afterConfiguration;
	}

	@Override
	public T initialize(GenericApplicationContext applicationContext) {
		return this.factory.apply(applicationContext);
	}

	@Override
	public void afterConfiguration(T t) {
		this.afterConfiguration.accept(t);
	}
}
