package eu.thingwave.arrowhead.fiware.demo.configuration;


import java.net.MalformedURLException;
import java.net.URL;
/**
 *
 * @author Pablo Puñal Pereira <pablo.punal@thingwave.eu>
 */
public class ConfigurationURL {
    private String protocol;
    private String host;
    private int port;
    private String path;
    
    public ConfigurationURL(String protocol, String host, int port, String path) {
        this.protocol = protocol;
        this.host = host;
        this.port = port;
        this.path = path;
    }
    
    public String getProtocol() {
        return protocol;
    }
    
    public String getHost() {
        return host;
    }
    
    public int getPort() {
        return port;
    }
    
    public String getPath() {
        return path;
    }
    
    public String toURLString() {
        return protocol+"://"+host+":"+port+"/"+path;
    }
    
    public URL toURL() throws MalformedURLException {
        return new URL(toURLString());
    }
}
