package com.ge.snowizard.discovery.manage;

import static org.mockito.Mockito.*;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.junit.Test;

public class ServiceDiscoveryManagerTest {

    private final ServiceDiscovery<?> discovery = mock(ServiceDiscovery.class);
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private final ServiceDiscoveryManager<?> manager = new ServiceDiscoveryManager(
            discovery);

    @Test
    public void testStart() throws Exception {
        manager.start();
        verify(discovery).start();
    }

    @Test
    public void testStop() throws Exception {
        manager.stop();
        verify(discovery).close();
    }
}
