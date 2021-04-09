package org.springframework.fu.jafu;

import java.util.function.Consumer;

import org.springframework.boot.autoconfigure.context.MessageSourceInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.fu.arjen.App;

/**
 * Jafu top level DSL for application which allows to configure a Spring Boot
 * application using Jafu and functional bean registration.
 *
 * @author Sebastien Deleuze
 * @see org.springframework.fu.jafu.Jafu#application
 */
public class ApplicationDsl extends ConfigurationDsl {

	ApplicationDsl(GenericApplicationContext context) {
		super(context);
	}

	@Override
	public <T> ApplicationDsl enable(FeatureFunction<T> feature) {
		super.enable(feature);
		return this;
	}

	@Override
	public <T> ApplicationDsl enable(FeatureFunction<T> feature, Consumer<T> configuration) {
		super.enable(feature, configuration);
		return this;
	}
}
