package org.springframework.fu.jafu.webflux;

import java.util.function.Consumer;

import org.springframework.boot.autoconfigure.web.reactive.FormCodecInitializer;
import org.springframework.boot.autoconfigure.web.reactive.JacksonJsonCodecInitializer;
import org.springframework.boot.autoconfigure.web.reactive.MultipartCodecInitializer;
import org.springframework.boot.autoconfigure.web.reactive.ProtobufCodecInitializer;
import org.springframework.boot.autoconfigure.web.reactive.ResourceCodecInitializer;
import org.springframework.boot.autoconfigure.web.reactive.StringCodecInitializer;
import org.springframework.boot.autoconfigure.web.reactive.function.client.ReactiveWebClientBuilderInitializer;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.fu.jafu.AbstractDsl;
import org.springframework.fu.jafu.FeatureFunction;
import org.springframework.fu.jafu.web.JacksonDsl;

/**
 * Jafu DSL for WebFlux webClient.
 *
 * Register a {@link org.springframework.web.reactive.function.client.WebClient.Builder} bean.
 *
 * When no codec is configured, {@code String} and {@code Resource} ones are configured by default.
 * When a {@code codecs} block is declared, the one specified are configured by default.
 *
 * Required dependencies can be retrieve using {@code org.springframework.boot:spring-boot-starter-webflux}.
 *
 * @author Sebastien Deleuze
 */
public class WebFluxClientDsl extends AbstractDsl {

	private boolean codecsConfigured = false;

	private String baseUrl = null;

	private WebFluxClientDsl(GenericApplicationContext applicationContext) {
		super(applicationContext);
	}

	/**
	 * Configure a WebFlux webClient builder with default properties.
	 * @see org.springframework.fu.jafu.ConfigurationDsl#enable(ApplicationContextInitializer)
	 * @see org.springframework.web.reactive.function.client.WebClient.Builder
	 */
	public static FeatureFunction<WebFluxClientDsl> webClient() {
		return FeatureFunction.of(WebFluxClientDsl::new, WebFluxClientDsl::afterConfiguration);
	}
	
	private void afterConfiguration() {
		if (!this.codecsConfigured) {
			new StringCodecInitializer(true, false).initialize(applicationContext);
			new ResourceCodecInitializer(true).initialize(applicationContext);
		}
		new ReactiveWebClientBuilderInitializer(baseUrl).initialize(applicationContext);
	}

	/**
	 * Configure a base URL for requests performed through the webClient.
	 */
	public WebFluxClientDsl baseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
		return this;
	}

	/**
	 * Configure codecs via a {@link WebFluxClientCodecDsl dedicated DSL}.
	 */
	public WebFluxClientDsl codecs(Consumer<WebFluxClientCodecDsl> init) {
		init.accept(new WebFluxClientCodecDsl(applicationContext));
		this.codecsConfigured = true;
		return this;
	}

	/**
	 * Jafu DSL for WebFlux webClient codecs.
	 */
	static public class WebFluxClientCodecDsl extends AbstractDsl {

		WebFluxClientCodecDsl(GenericApplicationContext applicationContext) {
			super(applicationContext);
		}

		/**
		 * Enable {@link org.springframework.core.codec.CharSequenceEncoder} and {@link org.springframework.core.codec.StringDecoder} for all media types
		 * @see #string(boolean)
		 */
		public WebFluxClientCodecDsl string() {
			new StringCodecInitializer(true, false).initialize(applicationContext);
			return this;
		}

		/**
		 * Enable {@link org.springframework.core.codec.CharSequenceEncoder} and {@link org.springframework.core.codec.StringDecoder}
		 */
		public WebFluxClientCodecDsl string(boolean textPlainOnly) {
			new StringCodecInitializer(true, textPlainOnly).initialize(applicationContext);
			return this;
		}

		/**
		 * Enable {@link org.springframework.http.codec.ResourceHttpMessageWriter} and {@link org.springframework.core.codec.ResourceDecoder}
		 */
		public WebFluxClientCodecDsl resource() {
			new ResourceCodecInitializer(true).initialize(applicationContext);
			return this;
		}

		/**
		 * Enable {@link org.springframework.http.codec.protobuf.ProtobufEncoder} and {@link org.springframework.http.codec.protobuf.ProtobufDecoder}
		 *
		 * This codec requires Protobuf 3 or higher with the official `com.google.protobuf:protobuf-java` dependency, and
		 * supports `application/x-protobuf` and `application/octet-stream`.
		 */
		public WebFluxClientCodecDsl protobuf() {
			new ProtobufCodecInitializer(true).initialize(applicationContext);
			return this;
		}

		/**
		 * Enable {@link org.springframework.http.codec.FormHttpMessageWriter} and {@link org.springframework.http.codec.FormHttpMessageReader}
		 */
		public WebFluxClientCodecDsl form() {
			new FormCodecInitializer(true).initialize(applicationContext);
			return this;
		}

		/**
		 * Enable {@link org.springframework.http.codec.multipart.MultipartHttpMessageWriter} and
		 * {@link org.springframework.http.codec.multipart.MultipartHttpMessageReader}
		 */
		public WebFluxClientCodecDsl multipart() {
			new MultipartCodecInitializer(true).initialize(applicationContext);
			return this;
		}

		/**
		 * @see #jackson(Consumer)
		 */
		public WebFluxClientCodecDsl jackson() {
			return jackson(dsl -> {});
		}

		/**
		 * Register an `ObjectMapper` bean and configure a [Jackson](https://github.com/FasterXML/jackson)
		 * JSON codec on WebFlux webClient via a [dedicated DSL][JacksonDsl].
		 *
		 * Required dependencies can be retrieve using `org.springframework.boot:spring-boot-starter-json`
		 * (included by default in `spring-boot-starter-webflux`).
		 */
		public WebFluxClientCodecDsl jackson(Consumer<JacksonDsl> dsl) {
			enable(JacksonDsl.jackson(true), dsl);
			return this;
		}
	}

}
