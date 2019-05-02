package eu.thingwave.arrowhead.fiware.demo.consumer;

import com.google.gson.Gson;
import eu.thingwave.arrowhead.aio.ArrowheadAIO;
import eu.thingwave.arrowhead.aio.common.OrchestrationResponse;
import eu.thingwave.arrowhead.aio.common.OrchestrationService;
import eu.thingwave.arrowhead.aio.common.ServiceConsumer;
import eu.thingwave.arrowhead.aio.common.ServiceInfo;
import eu.thingwave.arrowhead.aio.common.ServiceRegistryEntry;
import eu.thingwave.arrowhead.aio.common.ServiceRequestForm;
import eu.thingwave.arrowhead.fiware.demo.common.SenML;
import eu.thingwave.arrowhead.fiware.demo.configuration.ConfigurationArrowhead;
import eu.thingwave.arrowhead.fiware.demo.configuration.ConfigurationHttpConsumer;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
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
public class HttpConsumer extends Server {
    private static final Logger LOG = LoggerFactory.getLogger(HttpConsumer.class.getName());
    private final ArrowheadAIO arrowheadAIO;
    private final ServiceRegistryEntry ahServiceRegistryEntry;
    private final Gson gson = new Gson();
    private final ConfigurationHttpConsumer configurationConsumer;
    private final ConfigurationArrowhead configArrowhead;
    private OrchestrationService orchertratedService;
    private final CloseableHttpClient httpClient;
    
    public HttpConsumer(ConfigurationArrowhead configArrowhead, ConfigurationHttpConsumer configurationConsumer) throws MalformedURLException {
        this.configurationConsumer = configurationConsumer;
        this.configArrowhead = configArrowhead;
        HttpParams httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, 1000);
        httpClient = new DefaultHttpClient(httpParams);
        
        // Server conf
        ServerConnector httpConnector = new ServerConnector(this);
        LOG.debug("Listening port: "+configurationConsumer.getURL().getPort());
        httpConnector.setPort(configurationConsumer.getURL().getPort());
        this.addConnector(httpConnector);
        ServletHandler servletHandler = new ServletHandler();
        servletHandler.addServletWithMapping(new ServletHolder(new HttpServletConsumerArrowhead()), "/"+configurationConsumer.getURL().getPath());
        setHandler(servletHandler);
        
        // Arrowhead
        arrowheadAIO = new ArrowheadAIO(
                configArrowhead.getServiceRegistryURL().toURL(),
                configArrowhead.getOrchestrationURL().toURL()
        );
        
        ahServiceRegistryEntry = new ServiceRegistryEntry(
                configurationConsumer.getURL().toURL(),
                configurationConsumer.getOrchestration().getSystemName(),
                "consumer",
                null,
                null
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
    
    /** Arrowhead's Methods. */
    public boolean registerServiceOnArrowhead() {
        return arrowheadAIO.getProducer().registerService(ahServiceRegistryEntry);
    }
    
    public boolean unregisterServiceFromArrowhead() {
        return arrowheadAIO.getProducer().unregisterService(ahServiceRegistryEntry);
    }
    
    
    public OrchestrationService requestOrchestration() throws MalformedURLException {
        ServiceConsumer requesterSystem = new ServiceConsumer(
                configurationConsumer.getURL().toURL(),
                configurationConsumer.getOrchestration().getSystemName(),
                configurationConsumer.getOrchestration().getAuthenticationInfo()
        );
        
        ServiceInfo requestedService = new ServiceInfo(
                configurationConsumer.getServiceToConsume().getServiceDefinition(),
                configurationConsumer.getServiceToConsume().getInterfaces(),
                configurationConsumer.getServiceToConsume().getMetadata()
        );
        ServiceRequestForm serviceRequestForm = new ServiceRequestForm(
                requesterSystem,
                requestedService,
                configurationConsumer.getOrchestration().getOrchestrationFlags(),
                configurationConsumer.getOrchestration().getPreferredProviders(),
                configurationConsumer.getOrchestration().getRequestedQoS(),
                configurationConsumer.getOrchestration().getCommands());
        
        OrchestrationResponse orchRes = arrowheadAIO.getConsumer().orchestration(serviceRequestForm);
        if (orchRes.isEmpty()) {
            LOG.debug("No orchestrated Service!");
            return null;
        }
        orchertratedService = orchRes.getService(0);
        return orchertratedService;
    }
    
    
    public String requestService() {
        try {
            HttpGet get = new HttpGet(orchertratedService.getServiceURL().toURI());
            CloseableHttpResponse response = httpClient.execute(get);
            
            
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(
                            response.getEntity().getContent()
                    )
            );
            String line;
            StringBuilder sb = new StringBuilder();
            while((line = br.readLine()) != null) {
                sb.append(line);
            }
            get.releaseConnection();
            return sb.toString();
            
        } catch (URISyntaxException ex) {
            LOG.error("URISyntaxException: "+ex.getLocalizedMessage());
        } catch (MalformedURLException ex) {
            LOG.error("MalformedURLException: "+ex.getLocalizedMessage());
        } catch (IOException ex) {
            LOG.error("IOException: "+ex.getLocalizedMessage());
        }
        return "ERROR";
    }
    
    public SenML requestServiceSenML() {
        return new SenML().fromJSON(requestService());
    }
    
}
