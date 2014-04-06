package com.ge.snowizard.discovery.manage;

import static com.google.common.base.Preconditions.checkNotNull;
import io.dropwizard.lifecycle.Managed;
import org.apache.curator.x.discovery.ServiceDiscovery;

public class ServiceDiscoveryManager<T> implements Managed {

    private final ServiceDiscovery<T> discovery;

    /**
     * Constructor
     * 
     * @param discovery
     *            {@link ServiceDiscovery}
     */
    public ServiceDiscoveryManager(final ServiceDiscovery<T> discovery) {
        this.discovery = checkNotNull(discovery);
    }

    @Override
    public void start() throws Exception {
        discovery.start();
    }

    @Override
    public void stop() throws Exception {
        discovery.close();
    }
}
