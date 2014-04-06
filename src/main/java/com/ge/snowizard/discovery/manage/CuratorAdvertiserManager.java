package com.ge.snowizard.discovery.manage;

import static com.google.common.base.Preconditions.checkNotNull;
import io.dropwizard.lifecycle.Managed;
import com.ge.snowizard.discovery.core.CuratorAdvertiser;

public class CuratorAdvertiserManager implements Managed {

    private final CuratorAdvertiser advertiser;

    /**
     * Constructor
     * 
     * @param advertiser
     *            {@link CuratorAdvertiser}
     */
    public CuratorAdvertiserManager(final CuratorAdvertiser advertiser) {
        this.advertiser = checkNotNull(advertiser);
    }

    @Override
    public void start() throws Exception {
        // the {@link CuratorAdvertisementListener will register the service}
    }

    @Override
    public void stop() throws Exception {
        advertiser.unregisterAvailability();
    }
}
