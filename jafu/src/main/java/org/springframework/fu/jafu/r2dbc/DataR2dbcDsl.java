package org.springframework.fu.jafu.r2dbc;

import org.springframework.boot.autoconfigure.data.r2dbc.R2dbcDataInitializer;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.fu.jafu.AbstractDsl;
import org.springframework.fu.jafu.FeatureFunction;

import java.util.function.Consumer;

public class DataR2dbcDsl extends AbstractDsl {

    public DataR2dbcDsl(GenericApplicationContext context) {
        super(context);
    }

    public static FeatureFunction<DataR2dbcDsl> dataR2dbc() {
        return DataR2dbcDsl::new;
    }

    public DataR2dbcDsl r2dbc(Consumer<R2dbcDsl> r2dbcDsl) {
        new R2dbcDsl(r2dbcDsl).initialize(applicationContext);
        return this;
    }

    @Override
    public void initialize(GenericApplicationContext context) {
        super.initialize(context);
        dsl.accept(this);
        new R2dbcDataInitializer().initialize(context);
    }
}
