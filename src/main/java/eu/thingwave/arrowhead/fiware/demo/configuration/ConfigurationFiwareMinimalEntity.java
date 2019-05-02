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
public class ConfigurationFiwareMinimalEntity {
    private final String id;
    private final String type;
    
    public ConfigurationFiwareMinimalEntity(String id, String type) {
        this.id = id;
        this.type = type;
    }
    
    public String getId() {
        return id;
    }
    
    public String getType() {
        return type;
    }
}
