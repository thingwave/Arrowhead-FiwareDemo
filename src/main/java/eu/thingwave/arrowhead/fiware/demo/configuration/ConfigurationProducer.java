package eu.thingwave.arrowhead.fiware.demo.configuration;

import com.google.gson.JsonObject;
import java.util.ArrayList;

/**
 *
 * @author Pablo Puñal Pereira <pablo.punal@thingwave.eu>
 */
public class ConfigurationProducer {
    private final ConfigurationURL url = new ConfigurationURL("http", "0.0.0.0", 8080, "temperature");
    private String systemName = "demo_system_http-temperature_provider";
    private String temperatureServiceDefinition = "http-temperature";
    private final ArrayList<String> interfaces = new ArrayList();
    private JsonObject metadata = new JsonObject();
    
    public ConfigurationProducer() {
        if (interfaces.isEmpty()) {
            interfaces.add("SenML");
        }
        if (metadata.size() == 0) {
            metadata.addProperty("unit", "celsius");
        }
    }
    
    
    public ConfigurationURL getURL() {
        return url;
    }
    
    public String getSystemName() {
        return systemName;
    }
    
    public String getTemperatureServiceDefinition() {
        return temperatureServiceDefinition;
    }
    
    public ArrayList<String> getInterfaces() {
        return interfaces;
    }
    
    public JsonObject getMetadata() {
        return metadata;
    }
}
