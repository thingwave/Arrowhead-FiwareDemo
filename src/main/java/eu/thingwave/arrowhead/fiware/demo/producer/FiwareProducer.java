package eu.thingwave.arrowhead.fiware.demo.producer;

import com.google.gson.JsonObject;
import eu.thingwave.arrowhead.fiware.demo.configuration.Configuration;
import eu.thingwave.arrowhead.fiware.demo.fiware.FiwareClient;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Pablo Puñal Pereira <pablo.punal@thingwave.eu>
 */
public class FiwareProducer {
    private static final Logger LOG = LoggerFactory.getLogger(FiwareProducer.class.getName());
    private final Configuration configuration;
    private final FiwareClient fiwareClient;
    private ScheduledExecutorService sesAutoUpdateTemperature = null;
    private final Random random = new Random();
    private double temperature = 20;
    
    public FiwareProducer(Configuration configuration) {
        this.configuration = configuration;
        fiwareClient = new FiwareClient(configuration.getFiwareProducer().getFiwareBrokerURL().toURLString());
        
        // Adding Hook to auto-unregister the service
        Runtime.getRuntime().addShutdownHook(
                new Thread() {
                    @Override
                    public void run() {
                        unregisterEntity();
                    }
                });
    }
    
    /** Service Methods. */
    public void startAutoUpdateTemperature() {
        sesAutoUpdateTemperature = Executors.newSingleThreadScheduledExecutor();
        sesAutoUpdateTemperature.scheduleAtFixedRate(
                ()-> {
                    updateTemperature();
                }
                , 2, 2, TimeUnit.SECONDS);
    }
    
    public void stopAutoUpdateTemperature() {
        if (sesAutoUpdateTemperature != null) {
            sesAutoUpdateTemperature.shutdown();
        }
    }
        
    public double updateTemperature() {
        temperature += ((double)random.nextInt(100)-50)/100;
        temperature = (double)Math.round(temperature*100)/100;
        LOG.debug("New Temperature Value: "+temperature);
        updateFiwareValue();
        return temperature;
    }
    
    public double updateTemperature(double temperature) {
        this.temperature = temperature;
        LOG.debug("New Temperature Value: "+temperature);
        updateFiwareValue();
        return temperature;
    }
    
    /** FIWARE's Methods. */
    private void updateFiwareValue() {
        try {
            fiwareClient.updateAttributeValue(getEntityId(), "temperature", null, "text/plain", ""+temperature);
        } catch (URISyntaxException ex) {
            LOG.error("URISyntaxException: "+ex.getLocalizedMessage());
        } catch (IOException ex) {
            LOG.error("IOException: "+ex.getLocalizedMessage());
        }
    }
    
    public boolean registerEntity(){
        JsonObject json = getEntityWithTemp();
        try {
            int status = fiwareClient.createEntity(json);
            LOG.debug("Status: "+status);
            if (status == 422) {
                LOG.warn("Entity already registered");
                return true;
            }
            return (status == 201);
        } catch (IOException ex) {
            LOG.error("IOException: "+ex.getLocalizedMessage());
        } catch (URISyntaxException ex) {
            LOG.error("IOException: "+ex.getLocalizedMessage());
        }
        return false;
    }
    
    public boolean unregisterEntity() {
        JsonObject json = getEntityWithTemp();
        try {
            int status = fiwareClient.removeEntity(getEntityId(),configuration.getFiwareProducer().getEntity().get("type").getAsString());
            return status==204;
        } catch (URISyntaxException ex) {
            LOG.error("IOException: "+ex.getLocalizedMessage());
        } catch (IOException ex) {
            LOG.error("IOException: "+ex.getLocalizedMessage());
        }
        return false;
    }
    
    private JsonObject getEntityWithTemp() {
        JsonObject json = configuration.getFiwareProducer().getEntity();
        JsonObject jTemp = new JsonObject();
        jTemp.addProperty("value", temperature);
        jTemp.addProperty("type", "Number");
        json.add("temperature", jTemp);
        return json;
    }
    
    private String getEntityId() {
        JsonObject json = configuration.getFiwareProducer().getEntity();
        if (!json.has("id"))
            return "";
        return json.get("id").getAsString();
    }
    
}
