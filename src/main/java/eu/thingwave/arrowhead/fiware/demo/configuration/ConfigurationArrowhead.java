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
public class ConfigurationArrowhead {
    private ConfigurationURL serviceRegistry = new ConfigurationURL("http", "0.0.0.0", 8442, "serviceregistry");
    private ConfigurationURL orchestration = new ConfigurationURL("http", "0.0.0.0", 8440, "orchestrator");
    
    public ConfigurationArrowhead() {}
    
    public ConfigurationURL getServiceRegistryURL() {
        return serviceRegistry;
    }
    
    public ConfigurationURL getOrchestrationURL() {
        return orchestration;
    }
}
