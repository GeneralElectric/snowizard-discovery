package com.ge.snowizard.discovery;

import com.yammer.dropwizard.config.Configuration;

public interface DiscoveryConfiguration<T extends Configuration> {
    DiscoveryFactory getDiscoveryFactory(T configuration);
}
