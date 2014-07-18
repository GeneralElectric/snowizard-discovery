package com.ge.snowizard.discovery.client;

import static com.google.common.base.Preconditions.checkNotNull;
import io.dropwizard.lifecycle.Managed;

public class DiscoveryClientManager implements Managed {

    private final DiscoveryClient client;

    /**
     * Constructor
     * 
     * @param client
     *            {@link DiscoveryClient}
     */
    public DiscoveryClientManager(final DiscoveryClient client) {
        this.client = checkNotNull(client);
    }

    @Override
    public void start() throws Exception {
        client.start();
    }

    @Override
    public void stop() throws Exception {
        client.close();
    }
}
