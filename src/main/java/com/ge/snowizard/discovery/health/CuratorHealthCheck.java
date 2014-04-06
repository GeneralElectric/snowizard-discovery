package com.ge.snowizard.discovery.health;

import static com.google.common.base.Preconditions.checkNotNull;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.imps.CuratorFrameworkState;
import com.codahale.metrics.health.HealthCheck;

public class CuratorHealthCheck extends HealthCheck {

    private final CuratorFramework framework;

    /**
     * Constructor
     * 
     * @param framework
     *            {@link CuratorFramework}
     */
    public CuratorHealthCheck(final CuratorFramework framework) {
        this.framework = checkNotNull(framework);
    }

    /**
     * Checks that the {@link CuratorFramework} instance is started and that the
     * configured root namespace exists.
     * 
     * @return {@link Result#unhealthy(String)} if the {@link CuratorFramework}
     *         is not started or the configured root namespace does not exist;
     *         otherwise, {@link Result#healthy()}.
     * @throws Exception
     *             if an error occurs checking the health of the ZooKeeper
     *             ensemble.
     */
    @Override
    protected Result check() throws Exception {
        if (framework.getState() != CuratorFrameworkState.STARTED) {
            return Result.unhealthy("Client not started");
        } else if (framework.checkExists().forPath("/") == null) {
            return Result.unhealthy("Root for namespace does not exist");
        }

        return Result.healthy();
    }
}
