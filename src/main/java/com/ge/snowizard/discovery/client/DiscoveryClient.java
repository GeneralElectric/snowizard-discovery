package com.ge.snowizard.discovery.client;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkArgument;
import java.util.Collection;
import javax.annotation.concurrent.ThreadSafe;
import org.apache.curator.x.discovery.DownInstancePolicy;
import org.apache.curator.x.discovery.ProviderStrategy;
import org.apache.curator.x.discovery.ServiceCache;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.ServiceProvider;
import com.ge.snowizard.discovery.core.InstanceMetadata;

@ThreadSafe
public class DiscoveryClient {

    private final ServiceDiscovery<InstanceMetadata> discovery;
    private final ServiceProvider<InstanceMetadata> provider;

    /**
     * Constructor
     *
     * @param serviceName
     *            name of the service to monitor
     * @param discovery
     *            {@link ServiceDiscovery}
     * @param downInstancePolicy
     *            {@link DownInstancePolicy} to use when marking instances as
     *            down
     * @param providerStrategy
     *            {@link ProviderStrategy} to use when selecting an instance
     */
    public DiscoveryClient(final String serviceName,
            final ServiceDiscovery<InstanceMetadata> discovery,
            final DownInstancePolicy downInstancePolicy,
            final ProviderStrategy<InstanceMetadata> providerStrategy) {
        checkNotNull(serviceName);
        checkArgument(!serviceName.isEmpty(), "serviceName cannot be empty");
        checkNotNull(providerStrategy);

        this.discovery = checkNotNull(discovery);

        this.provider = discovery.serviceProviderBuilder()
                .serviceName(serviceName)
                .downInstancePolicy(downInstancePolicy)
                .providerStrategy(providerStrategy).build();
    }

    /**
     * Return a list of discoverable services
     *
     * @return Collection of service names
     */
    public Collection<String> getServices() throws Exception {
        return discovery.queryForNames();
    }

    /**
     * Return the running instances for the service.
     *
     * @return Collection of service instances
     */
    public Collection<ServiceInstance<InstanceMetadata>> getInstances(
            final String serviceName) throws Exception {
        return discovery.queryForInstances(serviceName);
    }

    /**
     * Return an instance of this service.
     *
     * @return ServiceInstance
     * @throws Exception
     */
    public ServiceInstance<InstanceMetadata> getInstance() throws Exception {
        return provider.getInstance();
    }

    /**
     * Note an error when connecting to a service instance.
     *
     * @param instance
     *            {@link ServiceInstance} that is causing the error.
     */
    public void noteError(final ServiceInstance<InstanceMetadata> instance) {
        provider.noteError(instance);
    }

    /**
     * Start the internal {@link ServiceProvider} and {@link ServiceCache}
     *
     * @throws Exception
     */
    public void start() throws Exception {
        provider.start();
    }

    /**
     * Stop the internal {@link ServiceProvider} and {@link ServiceCache}
     *
     * @throws Exception
     */
    public void close() throws Exception {
        provider.close();
    }
}
