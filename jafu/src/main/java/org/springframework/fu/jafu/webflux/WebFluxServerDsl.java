package org.springframework.fu.jafu.webflux;

import static org.springframework.beans.factory.support.BeanDefinitionReaderUtils.uniqueBeanName;

import java.util.function.Consumer;
import java.util.function.Supplier;

import org.springframework.beans.factory.config.BeanDefinitionCustomizer;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.FormCodecInitializer;
import org.springframework.boot.autoconfigure.web.reactive.JacksonJsonCodecInitializer;
import org.springframework.boot.autoconfigure.web.reactive.MultipartCodecInitializer;
import org.springframework.boot.autoconfigure.web.reactive.ProtobufCodecInitializer;
import org.springframework.boot.autoconfigure.web.reactive.ReactiveWebServerInitializer;
import org.springframework.boot.autoconfigure.web.reactive.ResourceCodecInitializer;
import org.springframework.boot.autoconfigure.web.reactive.StringCodecInitializer;
import org.springframework.boot.autoconfigure.web.reactive.WebFluxProperties;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.boot.web.reactive.server.ConfigurableReactiveWebServerFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.fu.jafu.AbstractDsl;
import org.springframework.fu.jafu.FeatureFunction;
import org.springframework.fu.jafu.templating.MustacheDsl;
import org.springframework.fu.jafu.templating.ThymeleafDsl;
import org.springframework.fu.jafu.web.JacksonDsl;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.server.WebFilter;

/**
 * Jafu DSL for WebFlux server.
 *
 * This DSL to be used with {@link org.springframework.fu.jafu.Jafu#reactiveWebApplication(java.util.function.Consumer)}
 * configures a <a href="https://docs.spring.io/spring/docs/current/spring-framework-reference/web-reactive.html#spring-webflux"></a>WebFlux server</a>.
 *
 * When no codec is configured, {@code String} and {@code Resource} ones are configured by default.
 * When a {@code codecs} block is declared, the one specified are configured by default.
 *
 * You can chose the underlying engine via the {@link WebFluxServerDsl#engine(ConfigurableReactiveWebServerFactory)} parameter.
 *
 * Required dependencies can be retrieve using {@code org.springframework.boot:spring-boot-starter-webflux}.
 *
 * @see org.springframework.fu.jafu.Jafu#reactiveWebApplication(java.util.function.Consumer)
 * @see WebFluxServerDsl#codecs(Consumer)
 * @see WebFluxServerDsl#mustache()
 * @author Sebastien Deleuze
 */
@SuppressWarnings("deprecation")
public class WebFluxServerDsl extends AbstractDsl {

	private ServerProperties serverProperties = new ServerProperties();

	private ResourceProperties resourceProperties = new ResourceProperties();

	private WebProperties webProperties = new WebProperties();

	private WebFluxProperties webFluxProperties = new WebFluxProperties();

	private boolean codecsConfigured = false;

	private int port = 8080;

	private ConfigurableReactiveWebServerFactory engine = null;

	private WebFluxServerDsl(GenericApplicationContext applicationContext) {
		super(applicationContext);
	}
	
	public static FeatureFunction<WebFluxServerDsl> webFlux() {
		return FeatureFunction.of(WebFluxServerDsl::new, WebFluxServerDsl::afterConfiguration);
	}

	private void afterConfiguration() {
		if (engine == null) {
			engine = new NettyDelegate().get();
		}
		engine.setPort(port);

		if (!codecsConfigured) {
			new StringCodecInitializer(false, false).initialize(applicationContext);
			new ResourceCodecInitializer(false).initialize(applicationContext);
		}
		if (applicationContext.containsBeanDefinition("webHandler")) {
			throw new IllegalStateException("Only one webFlux per application is supported");
		}
		new ReactiveWebServerInitializer(serverProperties, resourceProperties, webProperties, webFluxProperties, engine).initialize(applicationContext);

	}

	/**
	 * Define the listening port of the webFlux.
	 */
	public WebFluxServerDsl port(int port) {
		this.port = port;
		return this;
	}

	/**
	 * Define the underlying engine used.
	 */
	public WebFluxServerDsl engine(ConfigurableReactiveWebServerFactory engine) {
		this.engine = engine;
		return this;
	}

	/**
	 * Configure routes via {@link RouterFunctions.Builder}.
	 * @see org.springframework.fu.jafu.BeanDefinitionDsl#bean(Class, BeanDefinitionCustomizer...)
	 */
	public WebFluxServerDsl router(Consumer<RouterFunctions.Builder> routerDsl) {
		RouterFunctions.Builder builder = RouterFunctions.route();
		applicationContext
				.registerBean(BeanDefinitionReaderUtils.uniqueBeanName(RouterFunction.class.getName(), applicationContext), RouterFunction.class, () -> {
			routerDsl.accept(builder);
			return builder.build();
		});
		return this;
	}

	/**
	 * Configure codecs via a [dedicated DSL][WebFluxServerCodecDsl].
	 * @see WebFluxServerCodecDsl#jackson
	 */
	public WebFluxServerDsl codecs(Consumer<WebFluxServerCodecDsl> init) {
		init.accept(new WebFluxServerCodecDsl(applicationContext));
		this.codecsConfigured = true;
		return this;
	}

	/**
	 * Define a request filter for this webFlux
	 */
	public WebFluxServerDsl filter(Class<? extends WebFilter> clazz) {
		applicationContext.registerBean(uniqueBeanName(clazz.getName(), applicationContext), clazz);
		return this;
	}

	/**
	 * @see #thymeleaf(Consumer)
	 */
	public WebFluxServerDsl thymeleaf() {
		return thymeleaf(dsl -> {});
	}

	/**
	 * Configure Thymeleaf view resolver.
	 *
	 * Require {@code org.springframework.boot:spring-boot-starter-thymeleaf} dependency.
	 */
	public WebFluxServerDsl thymeleaf(Consumer<ThymeleafDsl> dsl) {
		FeatureFunction<ThymeleafDsl> feature = ThymeleafDsl.thymeleaf();
		ThymeleafDsl thymeleafDsl = feature.initialize(this.applicationContext);
		dsl.accept(thymeleafDsl);
		feature.afterConfiguration(thymeleafDsl);
		thymeleafDsl.initializeReactive(applicationContext);
		return this;
	}

	/**
	 * @see #mustache(Consumer)
	 */
	public WebFluxServerDsl mustache() {
		return mustache(dsl -> {});
	}

	/**
	 * Configure Mustache view resolver.
	 *
	 * Require {@code org.springframework.boot:spring-boot-starter-mustache} dependency.
	 */
	public WebFluxServerDsl mustache(Consumer<MustacheDsl> dsl) {
		new MustacheDsl(dsl).initializeReactive(applicationContext);
		return this;
	}

	/**
	 * Enable an external codec.
	 */
	@Override
	public WebFluxServerDsl enable(ApplicationContextInitializer<GenericApplicationContext> dsl) {
		return (WebFluxServerDsl) super.enable(dsl);
	}

	/**
	 * Jafu DSL for WebFlux server codecs.
	 */
	static public class WebFluxServerCodecDsl extends AbstractDsl {

		WebFluxServerCodecDsl(GenericApplicationContext applicationContext) {
			super(applicationContext);
		}

		@Override
		public WebFluxServerCodecDsl enable(ApplicationContextInitializer<GenericApplicationContext> dsl) {
			return (WebFluxServerCodecDsl) super.enable(dsl);
		}

		/**
		 * Enable {@link org.springframework.core.codec.CharSequenceEncoder} and {@link org.springframework.core.codec.StringDecoder} for all media types
		 */
		public WebFluxServerCodecDsl string() {
			new StringCodecInitializer(false, false).initialize(applicationContext);
			return this;
		}

		/**
		 * Enable {@link org.springframework.core.codec.CharSequenceEncoder} and {@link org.springframework.core.codec.StringDecoder}
		 */
		public WebFluxServerCodecDsl string(boolean textPlainOnly) {
			new StringCodecInitializer(false, textPlainOnly).initialize(applicationContext);
			return this;
		}

		/**
		 * Enable {@link org.springframework.http.codec.ResourceHttpMessageWriter} and {@link org.springframework.core.codec.ResourceDecoder}
		 */
		public WebFluxServerCodecDsl resource() {
			new ResourceCodecInitializer(false).initialize(applicationContext);
			return this;
		}

		/**
		 * Enable {@link org.springframework.http.codec.protobuf.ProtobufEncoder} and {@link org.springframework.http.codec.protobuf.ProtobufDecoder}
		 *
		 * This codec requires Protobuf 3 or higher with the official `com.google.protobuf:protobuf-java` dependency, and
		 * supports `application/x-protobuf` and `application/octet-stream`.
		 */
		public WebFluxServerCodecDsl protobuf() {
			new ProtobufCodecInitializer(false).initialize(applicationContext);
			return this;
		}

		/**
		 * Enable {@link org.springframework.http.codec.FormHttpMessageWriter} and {@link org.springframework.http.codec.FormHttpMessageReader}
		 */
		public WebFluxServerCodecDsl form() {
			new FormCodecInitializer(false).initialize(applicationContext);
			return this;
		}

		/**
		 * Enable {@link org.springframework.http.codec.multipart.MultipartHttpMessageWriter} and
		 * {@link org.springframework.http.codec.multipart.MultipartHttpMessageReader}
		 */
		public WebFluxServerCodecDsl multipart() {
			new MultipartCodecInitializer(false).initialize(applicationContext);
			return this;
		}

		/**
		 * @see #jackson(Consumer)
		 */
		public WebFluxServerCodecDsl jackson() {
			return jackson(dsl -> {});
		}

		/**
		 * Register an `ObjectMapper` bean and configure a [Jackson](https://github.com/FasterXML/jackson)
		 * JSON codec on WebFlux server via a [dedicated DSL][JacksonDsl].
		 *
		 * Required dependencies can be retrieve using `org.springframework.boot:spring-boot-starter-json`
		 * (included by default in `spring-boot-starter-webflux`).
		 */
		public WebFluxServerCodecDsl jackson(Consumer<JacksonDsl> dsl) {
			dsl.accept(new JacksonDsl(false, applicationContext));
			new JacksonJsonCodecInitializer(false).initialize(applicationContext);
			return this;
		}
	}

	private static class NettyDelegate implements Supplier<ConfigurableReactiveWebServerFactory> {
		@Override
		public ConfigurableReactiveWebServerFactory get() {
			return new NettyReactiveWebServerFactory();
		}
	}

}
