package com.mystudypartner.rag.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;

/**
 * Configuration class for asynchronous processing.
 * Provides custom thread pool configuration for document processing tasks.
 * 
 * @author MyStudyPartner Development Team
 * @version 1.0.0
 */
@Slf4j
@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {
    
    /**
     * Configure the default async executor for document processing.
     * 
     * @return configured ThreadPoolTaskExecutor
     */
    @Bean(name = "documentProcessingExecutor")
    public Executor documentProcessingExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        
        // Core pool size - number of threads to keep alive
        executor.setCorePoolSize(5);
        
        // Maximum pool size - maximum number of threads to create
        executor.setMaxPoolSize(10);
        
        // Queue capacity - number of tasks to queue when all threads are busy
        executor.setQueueCapacity(25);
        
        // Thread name prefix for easier debugging
        executor.setThreadNamePrefix("doc-process-");
        
        // Allow core threads to timeout and be replaced
        executor.setAllowCoreThreadTimeOut(true);
        
        // Keep alive time for idle threads (in seconds)
        executor.setKeepAliveSeconds(60);
        
        // Wait for tasks to complete on shutdown
        executor.setWaitForTasksToCompleteOnShutdown(true);
        
        // Await termination timeout (in seconds)
        executor.setAwaitTerminationSeconds(30);
        
        // Initialize the executor
        executor.initialize();
        
        log.info("Document processing executor configured with core={}, max={}, queue={}",
                executor.getCorePoolSize(), executor.getMaxPoolSize(), executor.getQueueCapacity());
        
        return executor;
    }
    
    /**
     * Configure the default async executor for general async operations.
     * 
     * @return configured ThreadPoolTaskExecutor
     */
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        
        // Core pool size
        executor.setCorePoolSize(3);
        
        // Maximum pool size
        executor.setMaxPoolSize(8);
        
        // Queue capacity
        executor.setQueueCapacity(15);
        
        // Thread name prefix
        executor.setThreadNamePrefix("async-");
        
        // Allow core threads to timeout
        executor.setAllowCoreThreadTimeOut(true);
        
        // Keep alive time for idle threads (in seconds)
        executor.setKeepAliveSeconds(60);
        
        // Wait for tasks to complete on shutdown
        executor.setWaitForTasksToCompleteOnShutdown(true);
        
        // Await termination timeout (in seconds)
        executor.setAwaitTerminationSeconds(30);
        
        // Initialize the executor
        executor.initialize();
        
        log.info("Default async executor configured with core={}, max={}, queue={}",
                executor.getCorePoolSize(), executor.getMaxPoolSize(), executor.getQueueCapacity());
        
        return executor;
    }
    
    /**
     * Configure custom exception handler for async operations.
     * 
     * @return AsyncUncaughtExceptionHandler
     */
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new AsyncUncaughtExceptionHandler() {
            @Override
            public void handleUncaughtException(Throwable ex, Method method, Object... params) {
                log.error("Uncaught exception in async method: {} with params: {}", 
                        method.getName(), params, ex);
                
                // Log additional context for debugging
                log.error("Method details: class={}, method={}", 
                        method.getDeclaringClass().getSimpleName(), method.getName());
                
                // You could add additional error reporting here
                // e.g., send to monitoring service, alert system, etc.
            }
        };
    }
}
