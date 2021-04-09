package org.springframework.fu.jafu;

import java.util.function.Consumer;
import java.util.function.Function;

import org.springframework.context.support.GenericApplicationContext;

/**
 * @author Arjen Poutsma
 * @see ApplicationDsl#enable(FeatureFunction, Consumer)
 */
@FunctionalInterface
public interface FeatureFunction<T> {

	T initialize(GenericApplicationContext applicationContext);

	default void afterConfiguration(T t) {
	}

	static <T> FeatureFunction<T> of(Function<GenericApplicationContext, T> factory, Consumer<T> afterConfig) {
		return new DefaultFeatureFunction<>(factory, afterConfig);
	}

}
