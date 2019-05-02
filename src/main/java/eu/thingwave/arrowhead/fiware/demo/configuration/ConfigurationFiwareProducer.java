/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.thingwave.arrowhead.fiware.demo.configuration;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 *
 * @author Pablo Puñal Pereira <pablo.punal@thingwave.eu>
 */
public class ConfigurationFiwareProducer {
    private final ConfigurationURL fiwareBrokerURL = new ConfigurationURL("http", "0.0.0.0", 8462, "");
    private final ConfigurationFiwareMinimalEntity producerEntity =
            new ConfigurationFiwareMinimalEntity("fiware_demo_temperature_producer", "temperature");
    
    public ConfigurationFiwareProducer() {}
    
    public ConfigurationURL getFiwareBrokerURL() {
        return fiwareBrokerURL;
    }
    
    public JsonObject getEntity() {
        return new JsonParser().parse(new Gson().toJson(producerEntity)).getAsJsonObject();
    }
    
}
