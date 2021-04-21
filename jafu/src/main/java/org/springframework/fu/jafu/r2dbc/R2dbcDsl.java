package org.springframework.fu.jafu.r2dbc;

import org.springframework.boot.autoconfigure.r2dbc.ConnectionFactoryOptionsBuilderCustomizer;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcInitializer;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcProperties;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.fu.jafu.AbstractDsl;
import org.springframework.fu.jafu.FeatureFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Jafu DSL for R2DBC (Reactive SQL) configuration.
 *
 * Enable and configure Reactive SQL support
 */
public class R2dbcDsl extends AbstractDsl {

    private final R2dbcProperties properties = new R2dbcProperties();

    private final List<ConnectionFactoryOptionsBuilderCustomizer> optionsCustomizers = new ArrayList<>();

    private boolean transactional = false;

    private R2dbcDsl(GenericApplicationContext applicationContext) {
        super(applicationContext);
    }

    public static FeatureFunction<R2dbcDsl> r2dbc() {
        return FeatureFunction.of(R2dbcDsl::new, R2dbcDsl::afterConfiguration);
    }

    private void afterConfiguration() {
        new R2dbcInitializer(this.properties, this.optionsCustomizers, this.transactional)
            .initialize(this.applicationContext);
    }

    public R2dbcDsl url(String url){
        properties.setUrl(url);
        return this;
    }

    public R2dbcDsl name(String name){
        properties.setName(name);
        return this;
    }

    public R2dbcDsl username(String username){
        properties.setUsername(username);
        return this;
    }

    public R2dbcDsl password(String password){
        properties.setPassword(password);
        return this;
    }

    public R2dbcDsl generateUniqueName(Boolean generate){
        properties.setGenerateUniqueName(generate);
        return this;
    }

    public R2dbcDsl optionsCustomizer(ConnectionFactoryOptionsBuilderCustomizer customizer){
        optionsCustomizers.add(customizer);
        return this;
    }

    public R2dbcDsl transactional(boolean transactional) {
        this.transactional = transactional;
        return this;
    }
}
