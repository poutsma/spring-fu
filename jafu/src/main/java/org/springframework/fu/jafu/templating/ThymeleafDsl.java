package org.springframework.fu.jafu.templating;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafInitializer;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafProperties;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafReactiveWebInitializer;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafServletWebInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.fu.jafu.AbstractDsl;
import org.springframework.fu.jafu.FeatureFunction;

import java.util.function.Consumer;

/**
 * Jafu DSL for Thymeleaf template engine.
 *
 * Configure a <a href="https://github.com/samskivert/jmustache">Mustache</a> view resolver.
 *
 * Required dependencies can be retrieve using {@code org.springframework.boot:spring-boot-starter-mustache}.
 *
 * @author Sebastien Deleuze
 */
public class ThymeleafDsl extends AbstractDsl {

	private final ThymeleafProperties properties = new ThymeleafProperties();

	private ThymeleafDsl(GenericApplicationContext applicationContext) {
		super(applicationContext);
	}

	public static FeatureFunction<ThymeleafDsl> thymeleaf(WebApplicationType type) {
		return FeatureFunction.of(ThymeleafDsl::new, dsl -> dsl.afterConfiguration(type));
	}

	private void afterConfiguration(WebApplicationType type) {
		new ThymeleafInitializer(this.properties).initialize(this.applicationContext);
		if (type == WebApplicationType.SERVLET) {
			new ThymeleafServletWebInitializer(this.properties).initialize(this.applicationContext);
		}
		else if (type == WebApplicationType.REACTIVE) {
			new ThymeleafReactiveWebInitializer(this.properties).initialize(this.applicationContext);
		}
	}

	/**
	 * Prefix to apply to template names.
	 */
	public ThymeleafDsl prefix(String prefix) {
		this.properties.setPrefix(prefix);
		return this;
	}

	/**
	 * Suffix to apply to template names.
	 */
	public ThymeleafDsl suffix(String suffix) {
		this.properties.setSuffix(suffix);
		return this;
	}

}

