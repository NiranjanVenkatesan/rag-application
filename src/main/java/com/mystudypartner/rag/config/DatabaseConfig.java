// ...existing code...
package com.mystudypartner.rag.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * Configuration class for database settings.
 * Provides custom database configuration, connection pooling, and JPA settings.
 * 
 * @author MyStudyPartner Development Team
 * @version 1.0.0
 */
@Slf4j
@Configuration
@EnableJpaRepositories(basePackages = "com.mystudypartner.rag.repository")
public class DatabaseConfig {
    
    @Value("${spring.datasource.url}")
    private String databaseUrl;
    
    @Value("${spring.datasource.username}")
    private String databaseUsername;
    
    @Value("${spring.datasource.password}")
    private String databasePassword;
    
    @Value("${spring.datasource.driver-class-name}")
    private String databaseDriver;
    
    @Value("${spring.jpa.hibernate.ddl-auto:validate}")
    private String hibernateDdlAuto;
    
    @Value("${spring.jpa.hibernate.dialect:org.hibernate.dialect.PostgreSQLDialect}")
    private String hibernateDialect;
    
    @Value("${spring.jpa.show-sql:false}")
    private boolean showSql;
    
    @Value("${spring.jpa.properties.hibernate.format_sql:false}")
    private boolean formatSql;
    
    @Value("${spring.jpa.properties.hibernate.jdbc.batch_size:20}")
    private int batchSize;
    
    @Value("${spring.jpa.properties.hibernate.order_inserts:true}")
    private boolean orderInserts;
    
    @Value("${spring.jpa.properties.hibernate.order_updates:true}")
    private boolean orderUpdates;
    
    /**
     * Configure primary data source.
     * 
     * @return configured DataSource
     */
    @Bean
    @Primary
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        
        dataSource.setDriverClassName(databaseDriver);
        dataSource.setUrl(databaseUrl);
        dataSource.setUsername(databaseUsername);
        dataSource.setPassword(databasePassword);
        
        log.info("DataSource configured for URL: {}", databaseUrl);
        
        return dataSource;
    }
    
    /**
     * Configure entity manager factory with custom settings.
     * 
     * @param dataSource the data source
     * @return configured LocalContainerEntityManagerFactoryBean
     */
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
        
        em.setDataSource(dataSource);
        em.setPackagesToScan("com.mystudypartner.rag.model");
        
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        em.setJpaVendorAdapter(vendorAdapter);
        
        // Set JPA properties
        Properties properties = new Properties();
        properties.setProperty("hibernate.hbm2ddl.auto", hibernateDdlAuto);
        properties.setProperty("hibernate.dialect", hibernateDialect);
        properties.setProperty("hibernate.show_sql", String.valueOf(showSql));
        properties.setProperty("hibernate.format_sql", String.valueOf(formatSql));
        properties.setProperty("hibernate.jdbc.batch_size", String.valueOf(batchSize));
        properties.setProperty("hibernate.order_inserts", String.valueOf(orderInserts));
        properties.setProperty("hibernate.order_updates", String.valueOf(orderUpdates));
        
        // Connection pool settings
        properties.setProperty("hibernate.connection.provider_class", 
                "org.hibernate.hikari.HikariConnectionProvider");
        properties.setProperty("hibernate.hikari.maximum_pool_size", "20");
        properties.setProperty("hibernate.hikari.minimum_idle", "5");
        properties.setProperty("hibernate.hikari.idle_timeout", "300000");
        properties.setProperty("hibernate.hikari.connection_timeout", "20000");
        properties.setProperty("hibernate.hikari.max_lifetime", "1200000");
        
        // Performance settings
        properties.setProperty("hibernate.jdbc.fetch_size", "50");
        properties.setProperty("hibernate.jdbc.batch_versioned_data", "true");
        properties.setProperty("hibernate.cache.use_second_level_cache", "false");
        properties.setProperty("hibernate.cache.use_query_cache", "false");
        
        // JSONB support for PostgreSQL - temporarily disabled
        // properties.setProperty("hibernate.types.json.type", "com.vladmihalcea.hibernate.type.json.JsonType");
        
        em.setJpaProperties(properties);
        
        log.info("EntityManagerFactory configured with dialect={}, ddlAuto={}, showSql={}",
                hibernateDialect, hibernateDdlAuto, showSql);
        
        return em;
    }
    
    /**
     * Configure transaction manager.
     * 
     * @param entityManagerFactory the entity manager factory
     * @return configured PlatformTransactionManager
     */
    @Bean
    public PlatformTransactionManager transactionManager(
            LocalContainerEntityManagerFactoryBean entityManagerFactory) {
        
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory.getObject());
        
        log.info("TransactionManager configured");
        
        return transactionManager;
    }
    
    /**
     * Configure development data source (only for dev profile).
     * 
     * @return configured DataSource for development
     */
    @Bean
    @ConditionalOnProperty(name = "spring.profiles.active", havingValue = "dev")
    public DataSource devDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        
        // Use H2 for development if needed
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE");
        dataSource.setUsername("sa");
        dataSource.setPassword("");
        
        log.info("Development DataSource configured with H2 in-memory database");
        
        return dataSource;
    }
    
    /**
     * Validate database connection on startup.
     * 
     * @param dataSource the data source to validate
     */
    @Bean
    public void validateDatabaseConnection(DataSource dataSource) {
        try {
            dataSource.getConnection().close();
            log.info("Database connection validated successfully");
        } catch (Exception e) {
            log.error("Database connection validation failed", e);
            throw new RuntimeException("Database connection validation failed", e);
        }
    }
}
