package com.ge.snowizard.discovery.core;

import static com.google.common.base.Preconditions.checkNotNull;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.yammer.dropwizard.lifecycle.ServerLifecycleListener;

public class CuratorAdvertisementListener implements ServerLifecycleListener {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(CuratorAdvertisementListener.class);
    private static final String MAIN_CONNECTOR = "main";
    private final CuratorAdvertiser advertiser;

    /**
     * Constructor
     *
     * @param advertiser
     *            {@link CuratorAdvertiser}
     */
    public CuratorAdvertisementListener(final CuratorAdvertiser advertiser) {
        this.advertiser = checkNotNull(advertiser);
    }

    @Override
    public void serverStarted(final Server server) {
        // Detect the port Jetty is listening on - works with configured and
        // random port
        for (Connector connector : server.getConnectors()) {
            if (MAIN_CONNECTOR.equals(connector.getName())) {
                advertiser.initListenInfo(connector.getLocalPort());
                try {
                    advertiser.registerAvailability();
                } catch (Exception e) {
                    LOGGER.error("Unable to register service in ZK", e);
                }
            }
        }
    }
}
