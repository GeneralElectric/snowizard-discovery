package com.ge.snowizard.discovery.health;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.ExistsBuilder;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import org.apache.zookeeper.data.Stat;
import org.junit.Before;
import org.junit.Test;
import com.codahale.metrics.health.HealthCheck.Result;

public class CuratorHealthCheckTest {

    private final CuratorFramework framework = mock(CuratorFramework.class);
    private final ExistsBuilder exists = mock(ExistsBuilder.class);
    private final CuratorHealthCheck health = new CuratorHealthCheck(framework);

    @Before
    public void setUp() {
        when(framework.checkExists()).thenReturn(exists);
    }

    @Test
    public void testCheckHealthy() throws Exception {
        when(framework.getState()).thenReturn(CuratorFrameworkState.STARTED);
        when(exists.forPath(anyString())).thenReturn(new Stat());
        assertThat(health.check()).isEqualTo(Result.healthy());
    }

    @Test
    public void testCheckNotStarted() throws Exception {
        final Result expected = Result.unhealthy("Client not started");
        assertThat(health.check()).isEqualTo(expected);
    }

    @Test
    public void testCheckMissingRoot() throws Exception {
        when(framework.getState()).thenReturn(CuratorFrameworkState.STARTED);
        when(exists.forPath(anyString())).thenReturn(null);
        final Result expected = Result
                .unhealthy("Root for namespace does not exist");
        assertThat(health.check()).isEqualTo(expected);
    }
}
