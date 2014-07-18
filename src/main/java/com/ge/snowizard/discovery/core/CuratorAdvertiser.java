package com.ge.snowizard.discovery.core;

import static com.google.common.base.Preconditions.checkNotNull;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Collection;
import java.util.UUID;
import javax.annotation.concurrent.GuardedBy;
import javax.annotation.concurrent.ThreadSafe;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.ServiceInstanceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ge.snowizard.discovery.DiscoveryFactory;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;

@ThreadSafe
public class CuratorAdvertiser implements ConnectionStateListener {
    private static final Logger LOGGER = LoggerFactory
            .getLogger(CuratorAdvertiser.class);

    private static final UUID instanceId = UUID.randomUUID();
    private final ServiceDiscovery<InstanceMetadata> discovery;
    private final DiscoveryFactory configuration;

    @GuardedBy("this")
    private String listenAddress;

    @GuardedBy("this")
    private int listenPort = 0;

    @GuardedBy("this")
    private ServiceInstance<InstanceMetadata> instance;

    /**
     * Constructor
     * 
     * @param configuration
     *            {@link DiscoveryFactory}
     * @param discovery
     *            {@link ServiceDiscovery}
     */
    public CuratorAdvertiser(final DiscoveryFactory configuration,
            final ServiceDiscovery<InstanceMetadata> discovery) {
        this.configuration = checkNotNull(configuration);
        this.discovery = checkNotNull(discovery);
    }

    /**
     * This must be called before other methods are used.
     * 
     * @param port
     *            port this instance is listening on
     * @throws Exception
     */
    public synchronized void initListenInfo(final int port) {
        try {
            final Collection<InetAddress> ips = ServiceInstanceBuilder
                    .getAllLocalIPs();
            if (Iterables.size(ips) > 0) {
                listenAddress = String.valueOf(Iterables.get(ips, 0))
                        .substring(1);
                LOGGER.debug("Found Local IP Addresses: {}, using {}", ips,
                        listenAddress);
            }
        } catch (final SocketException e) {
            LOGGER.error("Error getting local IP addresses", e);
        }

        if (Strings.isNullOrEmpty(listenAddress)) {
            LOGGER.debug("Using listenAddress from configuration file");
            listenAddress = configuration.getListenAddress();
        }

        listenPort = port;
    }

    /**
     * Register the instance in Zookeeper
     * 
     * @throws Exception
     */
    public synchronized void registerAvailability() throws Exception {
        registerAvailability(getInstance());
    }

    /**
     * Register a specific instance in Zookeeper
     * 
     * @param instance
     *            Service Instance
     * @throws Exception
     */
    public synchronized void registerAvailability(
            final ServiceInstance<InstanceMetadata> instance) throws Exception {
        checkInitialized();
        LOGGER.info("Registering service ({}) at <{}:{}>",
                configuration.getServiceName(), listenAddress, listenPort);

        discovery.registerService(instance);
        LOGGER.debug("Successfully registered service ({}) in ZK",
                configuration.getServiceName());
    }

    /**
     * Remove the instance from Zookeeper
     * 
     * @throws Exception
     */
    public synchronized void unregisterAvailability() throws Exception {
        unregisterAvailability(getInstance());
    }

    /**
     * Remove the specific instance from Zookeeper
     * 
     * @param instance
     *            Service Instance
     * @throws Exception
     */
    public synchronized void unregisterAvailability(
            final ServiceInstance<InstanceMetadata> instance) throws Exception {
        checkInitialized();
        LOGGER.info("Unregistering service ({}) at <{}:{}>",
                configuration.getServiceName(), listenAddress, listenPort);

        discovery.unregisterService(instance);
        LOGGER.debug("Successfully unregistered service ({}) from ZK",
                configuration.getServiceName());
    }

    /**
     * Return the instance ID
     * 
     * @return {@link UUID}
     */
    public UUID getInstanceId() {
        return instanceId;
    }

    /**
     * Return the listening port
     * 
     * @return port number
     */
    public int getListenPort() {
        return listenPort;
    }

    /**
     * Return the listening IP address
     * 
     * @return IP address
     */
    public String getListenAddress() {
        return listenAddress;
    }

    /**
     * Return the {@link ServiceInstance} that will be registered with the
     * {@link ServiceDiscovery} instance.
     * 
     * @return {@link ServiceInstance}
     * @throws Exception
     */
    public synchronized ServiceInstance<InstanceMetadata> getInstance()
            throws Exception {
        if (instance != null) {
            return instance;
        }

        final InstanceMetadata metadata = new InstanceMetadata(instanceId,
                listenAddress, listenPort);

        instance = ServiceInstance.<InstanceMetadata> builder()
                .name(configuration.getServiceName()).address(listenAddress)
                .port(listenPort).id(instanceId.toString()).payload(metadata)
                .build();
        return instance;
    }

    /**
     * Check that the {@link #initListenInfo} method has been called by
     * validating that the listenPort is greater than 1.
     * 
     * @throws IllegalStateException
     */
    public void checkInitialized() {
        if (Strings.isNullOrEmpty(listenAddress) || listenPort < 1) {
            throw new IllegalStateException("Not initialized");
        }
    }

    /**
     * TODO - figure out how to register this listener
     */
    @Override
    public void stateChanged(final CuratorFramework client,
            final ConnectionState newState) {
        if (newState == ConnectionState.RECONNECTED) {
            try {
                registerAvailability();
            } catch (final Exception e) {
                LOGGER.error("Unable to register service", e);
            }
        }
    }
}
