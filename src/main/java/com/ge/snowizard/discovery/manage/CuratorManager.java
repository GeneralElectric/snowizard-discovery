package com.ge.snowizard.discovery.manage;

import static com.google.common.base.Preconditions.checkNotNull;
import io.dropwizard.lifecycle.Managed;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.imps.CuratorFrameworkState;
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
        // start framework directly to allow other bundles to interact with zookeeper
        // during their run() method.
        if (framework.getState() != CuratorFrameworkState.STARTED) {
            framework.start();
        }
    }

    @Override
    public void start() throws Exception {
        // framework was already started in ctor,
        // ensure that the root path is available
        new EnsurePath("/").ensure(framework.getZookeeperClient());
    }

    @Override
    public void stop() throws Exception {
        framework.close();
    }
}
