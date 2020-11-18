package org.springframework.fu.arjen;

import java.util.function.Consumer;
import java.util.function.Function;

import org.springframework.context.support.GenericApplicationContext;

/**
 * @author Arjen Poutsma
 */
public class AppDsl extends DefaultDsl {

	public AppDsl(GenericApplicationContext context) {
		super(context);
	}


	public <T> AppDsl enable(Function<GenericApplicationContext, T> feature, Consumer<T> consumer) {
		consumer.accept(feature.apply(context()));
		return this;
	}


}
