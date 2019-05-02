package eu.thingwave.arrowhead.fiware.demo.configuration;


/**
 *
 * @author Pablo Puñal Pereira <pablo.punal@thingwave.eu>
 */
public class ConfigurationHttpConsumer {
    private final ConfigurationURL url;
    private final ConfigurationOrchestration orchestration;
    private final ConfigurationServiceToConsume serviceToConsume;
    
    public ConfigurationHttpConsumer(
            ConfigurationURL url,
            ConfigurationOrchestration orchestration,
            ConfigurationServiceToConsume serviceToConsume) {
        this.url = url;
        this.orchestration = orchestration;
        this.serviceToConsume = serviceToConsume;
    }
    
    public ConfigurationURL getURL() {
        return url;
    }
    
    public ConfigurationOrchestration getOrchestration() {
        return orchestration;
    }
    
    public ConfigurationServiceToConsume getServiceToConsume() {
        return serviceToConsume;
    }
}
