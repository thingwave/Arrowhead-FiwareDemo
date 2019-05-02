package eu.thingwave.arrowhead.fiware.demo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import eu.thingwave.arrowhead.aio.common.OrchestrationService;
import eu.thingwave.arrowhead.fiware.demo.configuration.Configuration;
import eu.thingwave.arrowhead.fiware.demo.consumer.FiwareConsumer;
import eu.thingwave.arrowhead.fiware.demo.consumer.HttpConsumer;
import eu.thingwave.arrowhead.fiware.demo.producer.FiwareProducer;
import eu.thingwave.arrowhead.fiware.demo.producer.HttpProducer;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Properties;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * @author Pablo Puñal Pereira <pablo.punal@thingwave.eu>
 */
public class Main {
    private static final Logger LOG = LoggerFactory.getLogger(Main.class.getName());
    private static final String CONFIG_FILE_NAME = "demo.config";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static Configuration config;
    
    private static HttpProducer httpProducer;
    private static HttpConsumer httpConsumer;
    private static HttpConsumer httpFiwareConsumer;
    private static FiwareProducer fiwareProducer;
    private static FiwareConsumer fiwareConsumer;
    private static FiwareConsumer fiwareHttpConsumer;
    
    private static double httpProducerTemp = 0;
    private static double httpConsumerTemp = 0;
    private static double fiwareProducerTemp = 0;
    private static double fiwareConsumerTemp = 0;
    private static double httpFiwareConsumerTemp = 0;
    private static double fiwareHttpConsumerTemp = 0;
    
    public static void main(String[] args) throws Exception {
        PropertyConfigurator.configure(Main.class.getClassLoader().getResourceAsStream("logger.properties"));
        int step = 0;
        LOG.info("Arrowhead Fiware Demo Consumer & Producer");
        
        /** Configuration. */
        LOG.info(String.format("[Step %02d] Load Configuration", step));
        if (!loadConfiguration()) {
            LOG.warn("No config file -> Create default");
            defaultConfiguration();
        }
        saveConfiguration();
        // Set slf4j logger
        Properties props = new Properties();
        props.setProperty("log4j.logger.eu.thingwave.arrowhead.fiware", config.getLogger().getDemo());
        props.setProperty("log4j.logger.eu.thingwave.arrowhead.aio", config.getLogger().getAIO());
        PropertyConfigurator.configure(props);
        
        LOG.debug("Steps: "+config.getNumberOfSteps());
        LOG.debug("SleepTime: "+config.getSleepTime()+" ms");
        Thread.sleep(config.getSleepTime());
        
        
        while(step++ < config.getNumberOfSteps()) {
            System.out.println(""); // Add line to see the steps easily
            switch(step) {
                case 1:
                    LOG.info(String.format("[Step %02d] Http Temperature Producer Create", step));
                    try {
                    httpProducer = new HttpProducer(config);
                    } catch(MalformedURLException ex) {
                        LOG.error("Malformed URL, check configuration! "+ex.getLocalizedMessage());
                        System.exit(1);
                    }
                    break;
                    
                case 2:
                    LOG.info(String.format("[Step %02d] Http Temperature Producer Start", step));
                    httpProducer.start();
                    break;
                    
                case 3:
                    LOG.info(String.format("[Step %02d] Http Temperature Producer Register in Arrowhead Service Registry", step));
                    if (httpProducer.registerServiceOnArrowhead()) {
                        LOG.info("Registered in Arrowhead!");
                    } else {
                        LOG.warn("No registered in Arrowhead!");
                    }
                    break;
                    
                case 4:
                    LOG.info(String.format("[Step %02d] Http Temperature Consumer Create", step));
                    httpConsumer = new HttpConsumer(
                            config.getArrowhead(),
                            config.getHttpConsumer());
                    break;
                    
                case 5:
                    LOG.info(String.format("[Step %02d] Http Temperature Consumer Start", step));
                    httpConsumer.start();
                    break;
                    
                case 6:
                    LOG.info(String.format("[Step %02d] Http Temperature Consumer Register in Arrowhead Service Registry", step));
                    if (httpConsumer.registerServiceOnArrowhead()) {
                        LOG.info("Registered in Arrowhead!");
                    } else {
                        LOG.warn("No registered in Arrowhead!");
                    }
                    break;
                    
                case 7:
                    LOG.info(String.format("[Step %02d] Http Temperature Consumer Request Arrowhead Orchestration", step));
                    OrchestrationService orchSer = httpConsumer.requestOrchestration();
                    if (orchSer != null) {
                        LOG.info("Orchestration endpoint received! "+orchSer.getServiceURL().toString());
                    } else {
                        LOG.warn("Orchestration failed!");
                    }
                    break;
                    
                case 8:
                    LOG.info(String.format("[Step %02d] Fiware Temperature Producer Create", step));
                    fiwareProducer = new FiwareProducer(config);
                    break;
                    
                case 9:
                    LOG.info(String.format("[Step %02d] Fiware Temperature Producer Register into Broker", step));
                    if (fiwareProducer.registerEntity()) {
                        LOG.info("Registered in FIWARE-Broker!");
                    } else {
                        LOG.warn("No registered in FIWARE-Broker!");
                    }
                    break;
                    
                case 10:
                    LOG.info(String.format("[Step %02d] Fiware Temperature Consumer Create", step));
                    fiwareConsumer = new FiwareConsumer(
                            config.getFiwareProducer().getFiwareBrokerURL().toURLString(),
                            config.getFiwareConsumer());
                    break;
                    
                case 11:
                    LOG.info(String.format("[Step %02d] Fiware Temperature Consumer Request Producer to Broker", step));
                    fiwareConsumer.requestProducer();
                    break;
                    
                case 12:
                    LOG.info(String.format("[Step %02d] Http Temperature Consumer Create", step));
                    httpFiwareConsumer = new HttpConsumer(
                            config.getArrowhead(),
                            config.getHttpFiwareConsumer());
                    break;
                    
                case 13:
                    LOG.info(String.format("[Step %02d] HttpFiware Temperature Consumer Start", step));
                    //httpFiwareConsumer.start();
                    break;
                    
                case 14:
                    LOG.info(String.format("[Step %02d] HttpFiware Temperature Consumer Register in Arrowhead Service Registry", step));
                    if (httpFiwareConsumer.registerServiceOnArrowhead()) {
                        LOG.info("Registered in Arrowhead!");
                    } else {
                        LOG.warn("No registered in Arrowhead!");
                    }
                    break;
                    
                case 15:
                    LOG.info(String.format("[Step %02d] HttpFiware Temperature Consumer Request Arrowhead Orchestration", step));
                    orchSer = httpFiwareConsumer.requestOrchestration();
                    if (orchSer != null) {
                        LOG.info("Orchestration endpoint received! "+orchSer.getServiceURL().toString());
                    } else {
                        LOG.warn("Orchestration failed!");
                    }
                    break;
                
                case 16:
                    LOG.info(String.format("[Step %02d] FiwareHttp Temperature Consumer", step));
                    fiwareHttpConsumer = new FiwareConsumer(
                            config.getFiwareProducer().getFiwareBrokerURL().toURLString(),
                            config.getFiwareHttpConsumer());
                    break;
                    
                case 17:
                    LOG.info(String.format("[Step %02d] FiwareHttp Temperature Consumer Request Producer to Broker", step));
                    fiwareHttpConsumer.requestProducer();
                    break;
                    
                case 18:
                    LOG.info(String.format("[Step %02d] Nothing", step));
                    break;
                    
                case 19:
                    LOG.info(String.format("[Step %02d] Nothing", step));
                    break;
                    
                default:
                    if (step == config.getNumberOfSteps()) {
                        LOG.info(String.format("[Step %02d] Shuttingdown", step));
                        LOG.debug("Unregister Http Temperature Producer");
                        httpProducer.unregisterServiceFromArrowhead();
                        LOG.debug("Stop Http Temperature Producer");
                        httpProducer.stop();
                        LOG.debug("Unregister Http Temperature Consumer");
                        httpConsumer.unregisterServiceFromArrowhead();
                        LOG.debug("Stop Http Temperature Consumer");
                        httpConsumer.stop();
                    } else {
                        
                        try {
                            switch(step%6) {
                                case 0:
                                    LOG.info(String.format("[Step %02d] HttpProducer Update Temperature: %.2f C", step, httpProducerTemp = httpProducer.updateTemperature()));
                                    break;
                                case 1:
                                    LOG.info(String.format("[Step %02d] HttpConsumer Request Temperature: %.2f C", step, httpConsumerTemp = httpConsumer.requestServiceSenML().getLastest().getValue().doubleValue()));
                                    break;
                                case 2:
                                    LOG.info(String.format("[Step %02d] FiwareHttpConsumer Update Temperature: %.2f C", step, fiwareHttpConsumerTemp = fiwareHttpConsumer.requestValueAsDouble()));
                                    break;
                                case 3:
                                    LOG.info(String.format("[Step %02d] FiwareProducer Update Temperature: %.2f C", step, fiwareProducerTemp = fiwareProducer.updateTemperature()));
                                    break;
                                case 4:
                                    LOG.info(String.format("[Step %02d] FiwareConsumer Request Temperature: %.2f C", step, fiwareConsumerTemp = fiwareConsumer.requestValueAsDouble()));
                                    break; 
                                case 5:
                                    LOG.info(String.format("[Step %02d] HttpFiwareConsumer Request Temperature: %.2f C", step, httpFiwareConsumerTemp = Double.valueOf(httpFiwareConsumer.requestServiceSenML().getLastest().getStringValue())));
                                    break;                               
                                default:
                                    LOG.error(String.format("[Step %02d] Unexpected", step));
                                    break;
                            }
                            System.out.println(String.format("| %12s | %12s | %18s | %14s | %18s | %18s |", "HttpProducer", "HttpConsumer", "FiwareHttpConsumer", "FiwareProducer", "FiwareConsumer", "HttpFiwareConsumer"));
                            System.out.println(String.format("| %12.2f | %12.2f | %18.2f | %14.2f | %18.2f | %18.2f |", httpProducerTemp, httpConsumerTemp, fiwareHttpConsumerTemp, fiwareProducerTemp, fiwareConsumerTemp, httpFiwareConsumerTemp));
                        } catch(Exception ex) {
                            LOG.warn("Exception: "+ex.getLocalizedMessage());
                        }
                    }
                    break;
            }
            Thread.sleep(config.getSleepTime());
        }
        
        LOG.warn("EXIT");
    }
    
    private static boolean loadConfiguration() {
        LOG.debug("Loading Configuration File "+CONFIG_FILE_NAME);
        boolean statusOK = true;        
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(CONFIG_FILE_NAME));
            config = gson.fromJson(br, Configuration.class);
            if (config == null) {
                statusOK = false;
            }
        } catch (FileNotFoundException ex) {
            LOG.error("There are problems reading the config file. "+ex.getLocalizedMessage());
            statusOK = false;
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException ex) {}
            }
        }
        return statusOK;
    }
    
    private static void defaultConfiguration() {
        LOG.warn("Creating Default Configuration");
        config = new Configuration();
    }
    
    private static void saveConfiguration() {
        FileWriter writer = null;
        try {
            writer = new FileWriter(CONFIG_FILE_NAME);
            gson.toJson(config,writer);
            writer.flush();
        } catch (IOException ex) {
            LOG.error("There are problems saving the config file. "+ex.getLocalizedMessage());
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ex) {}
            }
        }
    }
}
