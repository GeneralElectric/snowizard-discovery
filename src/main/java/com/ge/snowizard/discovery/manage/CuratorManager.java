package com.ge.snowizard.discovery.manage;

import static com.google.common.base.Preconditions.checkNotNull;
import io.dropwizard.lifecycle.Managed;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.utils.EnsurePath;

public class CuratorManager implements Managed {

    private final CuratorFramework framework;

    /**
     * Constructor
     * 
     * @param framework
     *            {@link CuratorFramework}
     */
    public CuratorManager(final CuratorFramework framework) {
        this.framework = checkNotNull(framework);
    }

    @Override
    public void start() throws Exception {
        framework.start();
        // ensure that the root path is available
        new EnsurePath("/").ensure(framework.getZookeeperClient());
    }

    @Override
    public void stop() throws Exception {
        framework.close();
    }
}
