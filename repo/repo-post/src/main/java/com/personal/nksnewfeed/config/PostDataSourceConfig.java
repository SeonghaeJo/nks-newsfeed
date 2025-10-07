package com.personal.nksnewfeed.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Objects;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        basePackages = "com.personal.nksnewfeed.repository.post",
        entityManagerFactoryRef = "postEntityManagerFactory",
        transactionManagerRef = "postTransactionManager"
)
public class PostDataSourceConfig {

    @Bean(name = "postDataSourceProperties")
    @ConfigurationProperties("spring.datasource.post")
    public DataSourceProperties postDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "postDataSource")
    public DataSource postDataSource(@Qualifier("postDataSourceProperties") final DataSourceProperties properties) {
        return properties.initializeDataSourceBuilder().build();
    }

    @Bean(name = "postEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean postEntityManagerFactory(
            final EntityManagerFactoryBuilder builder,
            @Qualifier("postDataSource") final DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("com.personal.nksnewfeed.entity.post")
                .persistenceUnit("post")
                .properties(Map.of("hibernate.default_schema", "postDB"))
                .build();
    }

    @Bean(name = "postTransactionManager")
    public PlatformTransactionManager postTransactionManager(
            @Qualifier("postEntityManagerFactory") final LocalContainerEntityManagerFactoryBean entityManagerFactory) {
        return new JpaTransactionManager(Objects.requireNonNull(entityManagerFactory.getObject()));
    }
}