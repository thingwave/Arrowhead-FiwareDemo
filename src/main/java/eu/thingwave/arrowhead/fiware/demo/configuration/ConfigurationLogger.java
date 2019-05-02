package eu.thingwave.arrowhead.fiware.demo.configuration;

/**
 *
 * @author Pablo Puñal Pereira <pablo.punal@thingwave.eu>
 */
public class ConfigurationLogger {
    private String demo = "INFO";
    private String aio = "INFO";
    
    public ConfigurationLogger() {}
    
    public String getDemo() {
        return demo;
    }
    
    public String getAIO() {
        return aio;
    }
}
