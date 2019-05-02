/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.thingwave.arrowhead.fiware.demo.configuration;

/**
 *
 * @author Pablo Puñal Pereira <pablo.punal@thingwave.eu>
 */
public class Configuration {
    private final ConfigurationLogger logger;
    private final ConfigurationHttpConsumer httpConsumer;
    private final ConfigurationHttpConsumer httpFiwareConsumer;
    private final ConfigurationProducer httpProducer;
    private final ConfigurationArrowhead arrowhead;
    private final ConfigurationFiwareProducer fiwareProducer;
    private final ConfigurationFiwareMinimalEntity fiwareConsumer;
    private final ConfigurationFiwareMinimalEntity fiwareHttpConsumer;
    private int numberOfSteps = 40;
    private int sleepTime = 4000;
    
    public Configuration() {
        // Create default configuration
        logger = new ConfigurationLogger();
        httpConsumer = new ConfigurationHttpConsumer(
                new ConfigurationURL("http", "0.0.0.0", 8888, ""),
                new ConfigurationOrchestration(),
                new ConfigurationServiceToConsume("http-temperature", "SenML", "unit-celsius")
        );
        httpFiwareConsumer = new ConfigurationHttpConsumer(
                new ConfigurationURL("http", "0.0.0.0", 8889, ""),
                new ConfigurationOrchestration(),
                new ConfigurationServiceToConsume("FIWARE-temperature", "FIWARE", "unit-celsius")
        );
        httpProducer = new ConfigurationProducer();
        arrowhead = new ConfigurationArrowhead();
        fiwareProducer = new ConfigurationFiwareProducer();
        fiwareConsumer = new ConfigurationFiwareMinimalEntity("", "temperature");
        fiwareHttpConsumer = new ConfigurationFiwareMinimalEntity("", "http-temperature");
    }
    
    public ConfigurationLogger getLogger() {
        return logger;
    }
    
    public ConfigurationHttpConsumer getHttpConsumer() {
        return httpConsumer;
    }
    
    public ConfigurationHttpConsumer getHttpFiwareConsumer() {
        return httpFiwareConsumer;
    }
    
    public ConfigurationProducer getHttpProducer() {
        return httpProducer;
    }
    
    public ConfigurationArrowhead getArrowhead() {
        return arrowhead;
    }
    
    public ConfigurationFiwareProducer getFiwareProducer() {
        return fiwareProducer;
    }
    
    public ConfigurationFiwareMinimalEntity getFiwareConsumer() {
        return fiwareConsumer;
    }
    
    public ConfigurationFiwareMinimalEntity getFiwareHttpConsumer() {
        return fiwareHttpConsumer;
    }
    
    public int getNumberOfSteps() {
        return numberOfSteps;
    }
    
    public int getSleepTime() {
        return sleepTime;
    }
}
