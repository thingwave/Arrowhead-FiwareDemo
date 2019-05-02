package eu.thingwave.arrowhead.fiware.demo.producer;

import eu.thingwave.arrowhead.aio.ArrowheadAIO;
import eu.thingwave.arrowhead.aio.common.ServiceRegistryEntry;
import eu.thingwave.arrowhead.fiware.demo.common.SenML;
import eu.thingwave.arrowhead.fiware.demo.common.SenML.SenMLElement;
import eu.thingwave.arrowhead.fiware.demo.configuration.Configuration;
import java.net.MalformedURLException;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Pablo Puñal Pereira <pablo.punal@thingwave.eu>
 */
public class HttpProducer extends Server {
    private static final Logger LOG = LoggerFactory.getLogger(HttpProducer.class.getName());
    private final ArrowheadAIO arrowheadAIO;
    private final ServiceRegistryEntry ahServiceRegistryEntry;
    private final SenML senMLtemperature;
    private final SenMLElement senMLtempElement;
    private ScheduledExecutorService sesAutoUpdateTemperature = null;
    private final Random random = new Random();
        
    public HttpProducer(Configuration configuration) throws MalformedURLException {
        // Init Temp senml
        senMLtemperature = new SenML();
        senMLtempElement = new SenMLElement();
        senMLtemperature.addElement(senMLtempElement);
        senMLtempElement.setBaseName("temperature");
        senMLtempElement.setBaseUnit("Cel");
        senMLtempElement.setBaseVersion(0.1);
        senMLtempElement.setBaseTime(System.currentTimeMillis());
        senMLtempElement.setValue(20);
        
        // Server conf
        ServerConnector httpConnector = new ServerConnector(this);
        LOG.debug("Listening port: "+configuration.getHttpProducer().getURL().getPort());
        httpConnector.setPort(configuration.getHttpProducer().getURL().getPort());
        this.addConnector(httpConnector);
        ServletHandler servletHandler = new ServletHandler();
        LOG.debug("Temperature path: "+configuration.getHttpProducer().getURL().getPath());
        servletHandler.addServletWithMapping(new ServletHolder(new HttpServletTemperature(senMLtemperature)), "/"+configuration.getHttpProducer().getURL().getPath());
        setHandler(servletHandler);
        
        // Arrowhead
        arrowheadAIO = new ArrowheadAIO(
                configuration.getArrowhead().getServiceRegistryURL().toURL(),
                configuration.getArrowhead().getOrchestrationURL().toURL()
        );
        
        ahServiceRegistryEntry = new ServiceRegistryEntry(
                configuration.getHttpProducer().getURL().toURL(),
                configuration.getHttpProducer().getSystemName(),
                configuration.getHttpProducer().getTemperatureServiceDefinition(),
                configuration.getHttpProducer().getInterfaces(),
                configuration.getHttpProducer().getMetadata()
        );
        
        
        // Adding Hook to auto-unregister the service
        Runtime.getRuntime().addShutdownHook(
                new Thread() {
                    @Override
                    public void run() {
                        unregisterServiceFromArrowhead();
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
        double temp = senMLtempElement.getValue().doubleValue();
        temp += ((double)random.nextInt(100)-50)/100;
        temp = (double)Math.round(temp*100)/100;
        senMLtempElement.setValue(temp);
        senMLtempElement.setBaseTime(System.currentTimeMillis());
        LOG.debug("New Temperature Value: "+senMLtempElement.getValue());
        return temp;
    }
    
    public double updateTemperature(double temperature) {
        senMLtempElement.setValue(temperature);
        senMLtempElement.setBaseTime(System.currentTimeMillis());
        LOG.debug("New Temperature Value: "+senMLtempElement.getValue());
        return temperature;
    }
    
    /** Arrowhead's Methods. */
    public boolean registerServiceOnArrowhead() {
        return arrowheadAIO.getProducer().registerService(ahServiceRegistryEntry);
    }
    
    public boolean unregisterServiceFromArrowhead() {
        return arrowheadAIO.getProducer().unregisterService(ahServiceRegistryEntry);
    }
    
}
