/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.thingwave.arrowhead.fiware.demo.configuration;

import com.google.gson.JsonObject;
import java.util.ArrayList;

/**
 *
 * @author Pablo Puñal Pereira <pablo.punal@thingwave.eu>
 */
public class ConfigurationServiceToConsume {
    private final String serviceDefinition;
    private final ArrayList<String> interfaces;
    private final JsonObject metadata;
    
    public ConfigurationServiceToConsume(
            String serviceDefinition,
            String interfaces, 
            String metadata) {
        this.serviceDefinition = serviceDefinition;
        this.interfaces = new ArrayList<>();
        for (String inter: interfaces.split(",")) {
            this.interfaces.add(inter);
        }
        this.metadata = new JsonObject();
        for (String part: metadata.split(",")) {
            String[] pair = part.split("-");
            if (pair.length == 2) {
                this.metadata.addProperty(pair[0], pair[1]);
            }
        }
    }
    
    public String getServiceDefinition() {
        return serviceDefinition;
    }
    
    public ArrayList<String> getInterfaces() {
        return interfaces;
    }
    
    public JsonObject getMetadata() {
        return metadata;
    }
}
