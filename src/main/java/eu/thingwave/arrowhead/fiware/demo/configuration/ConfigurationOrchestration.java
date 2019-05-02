package eu.thingwave.arrowhead.fiware.demo.configuration;

import com.google.gson.JsonObject;
import eu.thingwave.arrowhead.aio.common.ConfigurationOrchestrationFlags;
import java.util.ArrayList;

/**
 *
 * @author Pablo Puñal Pereira <pablo.punal@thingwave.eu>
 */
public class ConfigurationOrchestration {
    private String systemName = "demo_Temperature_Consumer";
    private String authenticationInfo = "null";
    private final ConfigurationOrchestrationFlags orchestrationFlags = new ConfigurationOrchestrationFlags();
    private final ArrayList<String> preferredProviders = new ArrayList();
    private final JsonObject requestedQoS = new JsonObject();
    private final JsonObject commands = new JsonObject();
    
    public ConfigurationOrchestration() {}
    
    public String getSystemName() {
        return systemName;
    }
    
    public String getAuthenticationInfo() {
        return authenticationInfo;
    }
    
    public ConfigurationOrchestrationFlags getOrchestrationFlags() {
        return orchestrationFlags;
    }
    
    public ArrayList<String> getPreferredProviders() {
        return preferredProviders;
    }
    
    public JsonObject getRequestedQoS() {
        return requestedQoS;
    }
    
    public JsonObject getCommands() {
        return commands;
    }
}
