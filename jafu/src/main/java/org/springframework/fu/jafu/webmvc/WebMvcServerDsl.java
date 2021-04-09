package org.springframework.fu.jafu.webmvc;

import java.util.function.Consumer;
import java.util.function.Supplier;

import org.springframework.beans.factory.config.BeanDefinitionCustomizer;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.servlet.AtomConverterInitializer;
import org.springframework.boot.autoconfigure.web.servlet.FormConverterInitializer;
import org.springframework.boot.autoconfigure.web.servlet.JacksonJsonConverterInitializer;
import org.springframework.boot.autoconfigure.web.servlet.ResourceConverterInitializer;
import org.springframework.boot.autoconfigure.web.servlet.RssConverterInitializer;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerInitializer;
import org.springframework.boot.autoconfigure.web.servlet.StringConverterInitializer;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties;
import org.springframework.boot.web.embedded.jetty.JettyServletWebServerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.fu.jafu.AbstractDsl;
import org.springframework.fu.jafu.FeatureFunction;
import org.springframework.fu.jafu.templating.MustacheDsl;
import org.springframework.fu.jafu.templating.ThymeleafDsl;
import org.springframework.fu.jafu.web.JacksonDsl;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;

/**
 * Jafu DSL for WebMvc server.
 *
 * This DSL to be used with {@link org.springframework.fu.jafu.Jafu#webApplication(Consumer)}
 * configures a <a href="https://docs.spring.io/spring/docs/current/spring-framework-reference/web.html#webmvc-fn"></a>WebMvc server</a>.
 *
 * When no converter is configured, {@code String} and {@code Resource} ones are configured by default.
 * When a {@code converters} block is declared, the one specified are configured by default.
 *
 * Required dependencies can be retrieve using {@code org.springframework.boot:spring-boot-starter-web}.
 *
 * @see org.springframework.fu.jafu.Jafu#webApplication(Consumer)
 * @see WebMvcServerDsl#converters(Consumer)
 * @author Sebastien Deleuze
 */
public class WebMvcServerDsl extends AbstractDsl {

	private ServerProperties serverProperties = new ServerProperties();

	private ResourceProperties resourceProperties = new ResourceProperties();

	private WebProperties webProperties = new WebProperties();

	private WebMvcProperties webMvcProperties = new WebMvcProperties();

	private ConfigurableServletWebServerFactory engine = null;

	private boolean convertersConfigured = false;

	private int port = 8080;

	private WebMvcServerDsl(GenericApplicationContext applicationContext) {
		super(applicationContext);
	}

	public static FeatureFunction<WebMvcServerDsl> webMvc() {
		return FeatureFunction.of(WebMvcServerDsl::new, WebMvcServerDsl::afterConfiguration);
	}

	private void afterConfiguration() {
		applicationContext.registerBean(BeanDefinitionReaderUtils.uniqueBeanName(RouterFunction.class.getName(), applicationContext), RouterFunction.class, () ->
				RouterFunctions.route().resources("/**", new ClassPathResource("static/")).build()
		);
		serverProperties.setPort(port);
		if (engine == null) {
			engine = new TomcatDelegate().get();
		}
		engine.setPort(port);
		serverProperties.getServlet().setRegisterDefaultServlet(false);
		if (!convertersConfigured) {
			new StringConverterInitializer().initialize(applicationContext);
			new ResourceConverterInitializer().initialize(applicationContext);
		}
		if (applicationContext.containsBeanDefinition("webHandler")) {
			throw new IllegalStateException("Only one webFlux per application is supported");
		}
		new ServletWebServerInitializer(serverProperties, webMvcProperties, resourceProperties, webProperties, engine).initialize(applicationContext);
	}

	/**
	 * Define the listening port of the webFlux.
	 */
	public WebMvcServerDsl port(int port) {
		this.port = port;
		return this;
	}

	/**
	 * Enable Tomcat engine.
	 */
	public WebMvcServerDsl tomcat() {
		this.engine = new TomcatDelegate().get();
		return this;
	}

	/**
	 * Enable Jetty engine.
	 */
	public WebMvcServerDsl jetty() {
		this.engine = new JettyDelegate().get();
		return this;
	}

	/**
	 * Enable Undertow engine.
	 */
	public WebMvcServerDsl undertow() {
		this.engine = new UndertowDelegate().get();
		return this;
	}

	/**
	 * Configure routes via {@link RouterFunctions.Builder}.
	 * @see org.springframework.fu.jafu.BeanDefinitionDsl#bean(Class, BeanDefinitionCustomizer...)
	 */
	public WebMvcServerDsl router(Consumer<RouterFunctions.Builder> routerDsl) {
		RouterFunctions.Builder builder = RouterFunctions.route();
		applicationContext
				.registerBean(BeanDefinitionReaderUtils.uniqueBeanName(RouterFunction.class.getName(), applicationContext), RouterFunction.class, () -> {
			routerDsl.accept(builder);
			return builder.build();
		});
		return this;
	}

	/**
	 * Configure converters via a dedicated DSL.
	 * @see WebMvcServerConverterDsl#jackson
	 */
	public WebMvcServerDsl converters(Consumer<WebMvcServerConverterDsl> init) {
		init.accept(new WebMvcServerConverterDsl(applicationContext));
		this.convertersConfigured = true;
		return this;
	}

	/**
	 * Enable an external codec.
	 */
	@Override
	public <T> WebMvcServerDsl enable(FeatureFunction<T> feature) {
		super.enable(feature);
		return this;
	}

	/**
	 * Enable an external codec.
	 */
	@Override
	protected <T> WebMvcServerDsl enable(FeatureFunction<T> feature, Consumer<T> configuration) {
		super.enable(feature, configuration);
		return this;
	}


	private class TomcatDelegate implements Supplier<ConfigurableServletWebServerFactory> {
		@Override
		public ConfigurableServletWebServerFactory get() {
			return new TomcatServletWebServerFactory();
		}
	}

	private class JettyDelegate implements Supplier<ConfigurableServletWebServerFactory> {
		@Override
		public ConfigurableServletWebServerFactory get() {
			return new JettyServletWebServerFactory();
		}
	}

	private class UndertowDelegate implements Supplier<ConfigurableServletWebServerFactory> {
		@Override
		public ConfigurableServletWebServerFactory get() {
			return new UndertowServletWebServerFactory();
		}
	}

	/**
	 * @see #thymeleaf(Consumer)
	 */
	public WebMvcServerDsl thymeleaf() {
		thymeleaf(dsl -> {});
		return this;
	}

	/**
	 * Configure Thymeleaf view resolver.
	 *
	 * Require {@code org.springframework.boot:spring-boot-starter-thymeleaf} dependency.
	 */
	public WebMvcServerDsl thymeleaf(Consumer<ThymeleafDsl> dsl) {
		enable(ThymeleafDsl.thymeleaf(WebApplicationType.SERVLET), dsl);
		return this;
	}

	/**
	 * @see #mustache(Consumer)
	 */
	public WebMvcServerDsl mustache() {
		return mustache(dsl -> {});
	}

	/**
	 * Configure Mustache view resolver.
	 *
	 * Require {@code org.springframework.boot:spring-boot-starter-mustache} dependency.
	 */
	public WebMvcServerDsl mustache(Consumer<MustacheDsl> dsl) {
		enable(MustacheDsl.mustache(WebApplicationType.SERVLET), dsl);
		return this;
	}


	/**
	 * Jafu DSL for WebMvc server codecs.
	 */
	static public class WebMvcServerConverterDsl extends AbstractDsl {

		WebMvcServerConverterDsl(GenericApplicationContext applicationContext) {
			super(applicationContext);
		}

		@Override
		public <T> WebMvcServerConverterDsl enable(FeatureFunction<T> feature) {
			super.enable(feature);
			return this;
		}

		@Override
		public  <T> WebMvcServerConverterDsl enable(FeatureFunction<T> feature, Consumer<T> configuration) {
			super.enable(feature, configuration);
			return this;
		}

		/**
		 * Enable {@link org.springframework.http.converter.StringHttpMessageConverter} for all media types
		 */
		public WebMvcServerConverterDsl string() {
			new StringConverterInitializer().initialize(this.applicationContext);
			return this;
		}

		/**
		 * Enable {@link org.springframework.http.converter.ResourceHttpMessageConverter} and {@link org.springframework.http.converter.ResourceRegionHttpMessageConverter}
		 */
		public WebMvcServerConverterDsl resource() {
			new ResourceConverterInitializer().initialize(applicationContext);
			return this;
		}

		/**
		 * Enable {@link org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter}
		 */
		public WebMvcServerConverterDsl form() {
			new FormConverterInitializer().initialize(applicationContext);
			return this;
		}

		/**
		 * @see #jackson(Consumer)
		 */
		public WebMvcServerConverterDsl jackson() {
			jackson(dsl -> {});
			return this;
		}

		/**
		 * Register an `ObjectMapper` bean and configure a [Jackson](https://github.com/FasterXML/jackson)
		 * JSON converter on WebMvc server via a [dedicated DSL][JacksonDsl].
		 *
		 * Required dependencies can be retrieve using `org.springframework.boot:spring-boot-starter-json`
		 * (included by default in `spring-boot-starter-web`).
		 */
		public WebMvcServerConverterDsl jackson(Consumer<JacksonDsl> dsl) {
			enable(JacksonDsl.jackson(false), dsl);
			new JacksonJsonConverterInitializer().initialize(applicationContext);
			return this;
		}

		/**
		 * Enable {@link org.springframework.http.converter.feed.AtomFeedHttpMessageConverter}
		 */
		public WebMvcServerConverterDsl  atom() {
			new AtomConverterInitializer().initialize(applicationContext);
			return this;
		}

		/**
		 * Enable {@link org.springframework.http.converter.feed.RssChannelHttpMessageConverter}
		 */
		public WebMvcServerConverterDsl  rss() {
			new RssConverterInitializer().initialize(applicationContext);
			return this;
		}
	}

}
