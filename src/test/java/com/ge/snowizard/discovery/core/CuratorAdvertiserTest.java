package com.ge.snowizard.discovery.core;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.mockito.Mockito.*;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceInstance;
import org.junit.Before;
import org.junit.Test;
import com.ge.snowizard.discovery.DiscoveryFactory;

public class CuratorAdvertiserTest {

    @SuppressWarnings("unchecked")
    private final ServiceDiscovery<InstanceMetadata> discovery = mock(ServiceDiscovery.class);
    private final DiscoveryFactory factory = new DiscoveryFactory();
    private final CuratorAdvertiser advertiser = new CuratorAdvertiser(factory,
            discovery);

    @Before
    public void setUp() {
        factory.setServiceName("test-service");
    }

    @Test
    public void testInitListenInfo() throws Exception {
        advertiser.initListenInfo(8080);
        assertThat(advertiser.getListenPort()).isEqualTo(8080);
        assertThat(advertiser.getListenAddress()).isNotEqualTo("");
    }

    @Test
    public void testCheckInitialized() throws Exception {
        try {
            advertiser.checkInitialized();
            failBecauseExceptionWasNotThrown(IllegalStateException.class);
        } catch (final IllegalStateException ignore) {
        }

        advertiser.initListenInfo(8080);
        advertiser.checkInitialized();
    }

    @Test
    public void testRegisterAvailability() throws Exception {
        try {
            advertiser.registerAvailability();
            failBecauseExceptionWasNotThrown(IllegalStateException.class);
        } catch (final IllegalStateException ignore) {
        }

        advertiser.initListenInfo(8080);
        final ServiceInstance<InstanceMetadata> instance = advertiser
                .getInstance();
        advertiser.registerAvailability(instance);
        verify(discovery).registerService(instance);
    }

    @Test
    public void testUnregisterAvailability() throws Exception {
        try {
            advertiser.unregisterAvailability();
            failBecauseExceptionWasNotThrown(IllegalStateException.class);
        } catch (final IllegalStateException ignore) {
        }

        advertiser.initListenInfo(8080);
        final ServiceInstance<InstanceMetadata> instance = advertiser
                .getInstance();
        advertiser.unregisterAvailability(instance);
        verify(discovery).unregisterService(instance);
    }
}
