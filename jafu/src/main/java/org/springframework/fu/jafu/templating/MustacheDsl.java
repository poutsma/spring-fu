package org.springframework.fu.jafu.templating;

import java.util.function.Consumer;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.mustache.MustacheInitializer;
import org.springframework.boot.autoconfigure.mustache.MustacheProperties;
import org.springframework.boot.autoconfigure.mustache.MustacheReactiveWebInitializer;
import org.springframework.boot.autoconfigure.mustache.MustacheServletWebInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.fu.jafu.AbstractDsl;
import org.springframework.fu.jafu.FeatureFunction;

/**
 * Jafu DSL for Mustache template engine.
 *
 * Configure a <a href="https://github.com/samskivert/jmustache">Mustache</a> view resolver.
 *
 * Required dependencies can be retrieve using {@code org.springframework.boot:spring-boot-starter-mustache}.
 *
 * @author Sebastien Deleuze
 */
public class MustacheDsl extends AbstractDsl {

	private final MustacheProperties properties = new MustacheProperties();

	private MustacheDsl(GenericApplicationContext applicationContext) {
		super(applicationContext);
	}

	public static FeatureFunction<MustacheDsl> mustache(WebApplicationType type) {
		return FeatureFunction.of(MustacheDsl::new, dsl -> dsl.afterConfiguration(type));
	}

	private void afterConfiguration(WebApplicationType type) {
		new MustacheInitializer(this.properties).initialize(this.applicationContext);
		if (type == WebApplicationType.SERVLET) {
			new MustacheServletWebInitializer(this.properties).initialize(this.applicationContext);
		}
		else if (type == WebApplicationType.REACTIVE) {
			new MustacheReactiveWebInitializer(this.properties).initialize(this.applicationContext);
		}
	}

	/**
	 * Prefix to apply to template names.
	 */
	public MustacheDsl prefix(String prefix) {
		this.properties.setPrefix(prefix);
		return this;
	}

	/**
	 * Suffix to apply to template names.
	 */
	public MustacheDsl suffix(String suffix) {
		this.properties.setSuffix(suffix);
		return this;
	}
}
