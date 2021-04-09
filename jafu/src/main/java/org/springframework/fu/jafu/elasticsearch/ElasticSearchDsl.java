package org.springframework.fu.jafu.elasticsearch;

import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticSearchDataInitializer;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.fu.jafu.AbstractDsl;
import org.springframework.fu.jafu.FeatureFunction;

import java.util.Optional;
import java.util.function.Consumer;

public class ElasticSearchDsl extends AbstractElasticSearchDsl<ElasticSearchDsl> {

    private ElasticSearchDsl(GenericApplicationContext applicationContext) {
        super(applicationContext);
    }

    /**
     * Configure Spring-data ElasticSearch support.
     * @see org.springframework.fu.jafu.ConfigurationDsl#enable(ApplicationContextInitializer)
     * @see org.springframework.fu.jafu.elasticsearch.ElasticSearchDsl
     */
    public static FeatureFunction<ElasticSearchDsl> elasticSearch() {
        return FeatureFunction.of(ElasticSearchDsl::new, ElasticSearchDsl::afterConfiguration);
    }


    public void afterConfiguration() {
        new ElasticSearchDataInitializer(createClientConfiguration()).initialize(this.applicationContext);
    }

    @Override
    protected ElasticSearchDsl getSelf() {
        return this;
    }
}
