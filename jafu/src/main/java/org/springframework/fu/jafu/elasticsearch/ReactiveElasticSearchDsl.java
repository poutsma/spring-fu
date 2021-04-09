package org.springframework.fu.jafu.elasticsearch;

import org.springframework.boot.autoconfigure.data.elasticsearch.ReactiveElasticSearchDataInitializer;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.fu.jafu.FeatureFunction;

import java.util.function.Consumer;

public class ReactiveElasticSearchDsl extends AbstractElasticSearchDsl<ReactiveElasticSearchDsl> {

    private ReactiveElasticSearchDsl(GenericApplicationContext applicationContext) {
        super(applicationContext);
    }

    /**
     * Configure Spring-data ElasticSearch with customized properties.
     * @see org.springframework.fu.jafu.ConfigurationDsl#enable(ApplicationContextInitializer)
     * @see org.springframework.fu.jafu.elasticsearch.ReactiveElasticSearchDsl
     */
    public static FeatureFunction<ReactiveElasticSearchDsl> reactiveElasticSearch() {
        return FeatureFunction.of(ReactiveElasticSearchDsl::new, ReactiveElasticSearchDsl::afterConfiguration);
    }

    private void afterConfiguration() {
        new ReactiveElasticSearchDataInitializer(createClientConfiguration()).initialize(this.applicationContext);
    }

    @Override
    protected ReactiveElasticSearchDsl getSelf() {
        return this;
    }
}
