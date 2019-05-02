package eu.thingwave.arrowhead.fiware.demo.consumer;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import eu.thingwave.arrowhead.fiware.demo.configuration.Configuration;
import eu.thingwave.arrowhead.fiware.demo.configuration.ConfigurationFiwareMinimalEntity;
import eu.thingwave.arrowhead.fiware.demo.fiware.FiwareClient;
import eu.thingwave.arrowhead.fiware.demo.producer.FiwareProducer;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.logging.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Pablo Puñal Pereira <pablo.punal@thingwave.eu>
 */
public class FiwareConsumer {
    private static final Logger LOG = LoggerFactory.getLogger(FiwareProducer.class.getName());
    private final ConfigurationFiwareMinimalEntity configurationFiwareConsumer;
    private final FiwareClient fiwareClient;
    private String producerEntityId;
    private String producerEntityType;
    
    public FiwareConsumer(String orionBrokerURL, ConfigurationFiwareMinimalEntity configurationFiwareConsumer) {
        this.configurationFiwareConsumer = configurationFiwareConsumer;
        fiwareClient = new FiwareClient(orionBrokerURL);
    }
    
    /** FIWARE's Methods. */
    public boolean requestProducer() {
        try {
            JsonObject queryParams = new JsonObject();
            queryParams.addProperty("type", configurationFiwareConsumer.getType());
            ArrayList<JsonObject> response = fiwareClient.listEntities(queryParams);
            if (response.isEmpty()) {
                LOG.warn("No entity found!!");
                return false;
            }
            producerEntityId = response.get(0).get("id").getAsString();
            producerEntityType = response.get(0).get("type").getAsString();
            LOG.debug("EndPoint found with id: "+producerEntityId);
            return true;
        } catch (IOException ex) {
            LOG.error("IOException: "+ex.getLocalizedMessage());
        } catch (URISyntaxException ex) {
            LOG.error("URISyntaxException: "+ex.getLocalizedMessage());
        }
        return false;
    }
    
    public String requestValue() {
        try {
            return fiwareClient.getAttributeValue(producerEntityId, producerEntityType);
        } catch (IOException ex) {
            LOG.error("IOException: "+ex.getLocalizedMessage());
        } catch (URISyntaxException ex) {
            LOG.error("URISyntaxException: "+ex.getLocalizedMessage());
        }
        return "Unknown";
    }
    
    public double requestValueAsDouble() {
        return Double.valueOf(requestValue());
    }
    
}
