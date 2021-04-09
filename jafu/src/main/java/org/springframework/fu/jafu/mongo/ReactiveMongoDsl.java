package org.springframework.fu.jafu.mongo;

import java.util.function.Consumer;

import de.flapdoodle.embed.mongo.distribution.IFeatureAwareVersion;

import org.springframework.boot.autoconfigure.data.mongo.MongoReactiveDataInitializer;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.autoconfigure.mongo.MongoReactiveInitializer;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoInitializer;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoProperties;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.fu.jafu.AbstractDsl;
import org.springframework.fu.jafu.FeatureFunction;

/**
 * Jafu DSL for Reactive MongoDB configuration.
 *
 * Enable and configure Reactive MongoDB support by registering a {@link org.springframework.data.mongodb.core.ReactiveMongoTemplate} bean.
 *
 * Required dependencies can be retrieve using {@code org.springframework.boot:spring-boot-starter-data-mongodb-reactive}.
 *
 * @author Sebastien Deleuze
 */
public class ReactiveMongoDsl extends AbstractDsl {

	private final MongoProperties properties = new MongoProperties();

	private boolean embedded = false;

	private ReactiveMongoDsl(GenericApplicationContext applicationContext) {
		super(applicationContext);
	}

	/**
	 * Configure Reactive MongoDB support with customized properties.
	 * @see org.springframework.fu.jafu.ConfigurationDsl#enable(ApplicationContextInitializer)
	 * @see org.springframework.data.mongodb.core.ReactiveMongoTemplate
	 */
	public static FeatureFunction<ReactiveMongoDsl> reactiveMongo() {
		return FeatureFunction.of(ReactiveMongoDsl::new, ReactiveMongoDsl::afterConfiguration);
	}

	private void afterConfiguration() {
		if (this.properties.getUri() == null) {
			this.properties.setUri(MongoProperties.DEFAULT_URI);
		}
		new MongoReactiveDataInitializer(this.properties).initialize(this.applicationContext);
		new MongoReactiveInitializer(this.properties, this.embedded).initialize(this.applicationContext);
	}

	/**
	 * Configure the database uri. By default set to `mongodb://localhost/test`.
	 */
	public ReactiveMongoDsl uri(String uri) {
		this.properties.setUri(uri);
		return this;
	}

	/**
	 * Enable MongoDB embedded webFlux with default properties.
	 *
	 * Require {@code de.flapdoodle.embed:de.flapdoodle.embed.mongo} dependency.
	 */
	public ReactiveMongoDsl embedded() {
		return embedded(dsl -> {});
	}

	/**
	 * Enable MongoDB embedded webFlux with customized properties.
	 *
	 * Require {@code de.flapdoodle.embed:de.flapdoodle.embed.mongo} dependency.
	 */
	public ReactiveMongoDsl embedded(Consumer<EmbeddedMongoDsl> dsl) {
		EmbeddedMongoDsl embeddedMongoDsl = new EmbeddedMongoDsl(properties, applicationContext);
		dsl.accept(embeddedMongoDsl);
		embeddedMongoDsl.afterConfiguration();
		this.embedded = true;
		return this;
	}

	/**
	 * Jafu DSL for embedded MongoDB configuration.
	 */
	public static class EmbeddedMongoDsl extends AbstractDsl {

		private final MongoProperties mongoProperties;
		private final EmbeddedMongoProperties embeddedMongoProperties = new EmbeddedMongoProperties();

		EmbeddedMongoDsl(MongoProperties mongoProperties, GenericApplicationContext applicationContext) {
			super(applicationContext);
			this.mongoProperties = mongoProperties;
		}

		/**
		 * Version of Mongo to use
		 */
		public EmbeddedMongoDsl version(IFeatureAwareVersion version) {
			this.embeddedMongoProperties.setVersion(version.asInDownloadPath());
			return this;
		}

		void afterConfiguration() {
			new EmbeddedMongoInitializer(this.mongoProperties, this.embeddedMongoProperties).initialize(applicationContext);
		}

	}
}
