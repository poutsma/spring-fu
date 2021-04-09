package org.springframework.fu.jafu.jdbc;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.springframework.boot.autoconfigure.jdbc.DataSourceInitializerInvokerInitializer;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfigurationInitializer;
import org.springframework.boot.autoconfigure.jdbc.EmbeddedDataSourceConfigurationInitializer;
import org.springframework.boot.autoconfigure.jdbc.JdbcProperties;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateConfigurationInitializer;
import org.springframework.boot.jdbc.DataSourceInitializationMode;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.fu.jafu.AbstractDsl;
import org.springframework.fu.jafu.FeatureFunction;

/**
 * Jafu DSL for JDBC configuration.
 */
public class JdbcDsl extends AbstractDsl {

    private final JdbcProperties jdbcProperties = new JdbcProperties();

    private final DataSourceProperties dataSourceProperties = new DataSourceProperties();

    private JdbcDsl(GenericApplicationContext applicationContext) {
        super(applicationContext);
    }

    public static FeatureFunction<JdbcDsl> jdbc() {
        return FeatureFunction.of(JdbcDsl::new, JdbcDsl::afterConfiguration);
    }

    public JdbcDsl url(String url){
        this.dataSourceProperties.setUrl(url);
        return this;
    }

    public JdbcDsl name(String name){
        this.dataSourceProperties.setName(name);
        return this;
    }

    public JdbcDsl username(String username){
        this.dataSourceProperties.setUsername(username);
        return this;
    }

    public JdbcDsl password(String password){
        this.dataSourceProperties.setPassword(password);
        return this;
    }

    public JdbcDsl generateUniqueName(Boolean generate){
        this.dataSourceProperties.setGenerateUniqueName(generate);
        return this;
    }

    public JdbcDsl driverClassName(String driverClassName){
        this.dataSourceProperties.setDriverClassName(driverClassName);
        return this;
    }

    public JdbcDsl schema(String schema){
        if (this.dataSourceProperties.getSchema() == null) {
            List<String> schemaList = new ArrayList<>();
            schemaList.add(schema);
            this.dataSourceProperties.setSchema(schemaList);
        }
        else {
            this.dataSourceProperties.getSchema().add(schema);
        }
        return this;
    }

    public JdbcDsl initializationMode(DataSourceInitializationMode initializationMode) {
        this.dataSourceProperties.setInitializationMode(initializationMode);
        return this;
    }

    public JdbcDsl data(String data) {
        if (this.dataSourceProperties.getData() == null) {
            List<String> dataList = new ArrayList<>();
            dataList.add(data);
            this.dataSourceProperties.setData(dataList);
        }
        else {
            this.dataSourceProperties.getData().add(data);
        }
        return this;
    }

    private void afterConfiguration() {
        new EmbeddedDataSourceConfigurationInitializer(this.dataSourceProperties).initialize(this.applicationContext);
        new JdbcTemplateConfigurationInitializer(this.jdbcProperties).initialize(this.applicationContext);
        new DataSourceTransactionManagerAutoConfigurationInitializer().initialize(this.applicationContext);
        new DataSourceInitializerInvokerInitializer(this.dataSourceProperties).initialize(this.applicationContext);
    }
}
