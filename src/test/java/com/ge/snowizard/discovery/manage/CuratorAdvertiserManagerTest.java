package com.ge.snowizard.discovery.manage;

import static org.mockito.Mockito.*;
import org.junit.Test;
import com.ge.snowizard.discovery.core.CuratorAdvertiser;

public class CuratorAdvertiserManagerTest {

    private final CuratorAdvertiser advertiser = mock(CuratorAdvertiser.class);
    private final CuratorAdvertiserManager manager = new CuratorAdvertiserManager(
            advertiser);

    @Test
    public void testStop() throws Exception {
        manager.stop();
        verify(advertiser).unregisterAvailability();
    }
}
