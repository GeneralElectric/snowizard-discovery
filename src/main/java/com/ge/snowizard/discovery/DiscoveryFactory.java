package com.ge.snowizard.discovery;

import io.dropwizard.util.Duration;
import io.dropwizard.validation.PortRange;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.api.CompressionProvider;
import org.apache.curator.framework.imps.GzipCompressionProvider;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.hibernate.validator.constraints.NotEmpty;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Joiner;

public class DiscoveryFactory {

    /**
     * An enumeration of the available compression codecs available for
     * compressed entries.
     * 
     * @see #getCompressionProvider()
     * @see CompressionProvider
     */
    enum CompressionCodec {

        /**
         * GZIP compression.
         * 
         * @see GzipCompressionProvider
         */
        GZIP(new GzipCompressionProvider());

        final private CompressionProvider provider;

        CompressionCodec(final CompressionProvider provider) {
            this.provider = provider;
        }

        /**
         * Gets the {@link CompressionProvider} for this codec.
         * 
         * @return the provider for this codec.
         */
        public CompressionProvider getProvider() {
            return provider;
        }
    }

    @NotEmpty
    private String[] hosts = new String[] { "localhost" };

    @PortRange
    private int port = 2181;

    @NotEmpty
    private String serviceName;

    @NotEmpty
    private String listenAddress = "127.0.0.1";

    @NotEmpty
    private String namespace = "snowizard";

    @NotEmpty
    private String basePath = "service";

    @NotNull
    private Duration connectionTimeout = Duration.seconds(6);

    @NotNull
    private Duration sessionTimeout = Duration.seconds(6);

    @NotNull
    private Duration baseSleepTime = Duration.seconds(1);

    @Min(0)
    @Max(29)
    private int maxRetries = 5;

    @NotNull
    private CompressionCodec compression = CompressionCodec.GZIP;

    @NotNull
    private Boolean isReadOnly = false;

    @JsonProperty
    public String[] getHosts() {
        return hosts;
    }

    @JsonProperty
    public int getPort() {
        return port;
    }

    /**
     * Retrieves a formatted specification of the ZooKeeper quorum..
     * 
     * The specification is formatted as: host1:port,host2:port[,hostN:port]
     * 
     * @return a specification of the ZooKeeper quorum, formatted as a String
     */
    @JsonIgnore
    public String getQuorumSpec() {
        return Joiner.on(":" + getPort() + ",").skipNulls()
                .appendTo(new StringBuilder(), getHosts()).append(':')
                .append(getPort()).toString();
    }

    @JsonProperty
    public String getServiceName() {
        return serviceName;
    }

    @JsonProperty
    public String getNamespace() {
        return namespace;
    }

    @JsonProperty
    public String getBasePath() {
        return basePath;
    }

    @JsonProperty
    public String getListenAddress() {
        return listenAddress;
    }

    @JsonProperty
    public Duration getConnectionTimeout() {
        return connectionTimeout;
    }

    @JsonProperty
    public Duration getSessionTimeout() {
        return sessionTimeout;
    }

    @JsonProperty
    public boolean isReadOnly() {
        return isReadOnly;
    }

    @JsonProperty
    public int getMaxRetries() {
        return maxRetries;
    }

    @JsonProperty
    public Duration getBaseSleepTime() {
        return baseSleepTime;
    }

    /**
     * Returns a {@link RetryPolicy} for handling failed connection attempts.
     * 
     * Always configures an {@link ExponentialBackoffRetry} based on the
     * {@link #getMaxRetries() maximum retries} and {@link #getBaseSleepTime()
     * initial back-off} configured.
     * 
     * @return a {@link RetryPolicy} for handling failed connection attempts.
     * 
     * @see #getMaxRetries()
     * @see #getBaseSleepTime()
     */
    @JsonIgnore
    public RetryPolicy getRetryPolicy() {
        return new ExponentialBackoffRetry((int) getBaseSleepTime()
                .toMilliseconds(), getMaxRetries());
    }

    /**
     * Returns a {@link CompressionProvider} to compress values with.
     * 
     * @return the compression provider used to compress values.
     * 
     * @see #CompressionCodec
     */
    @JsonIgnore
    public CompressionProvider getCompressionProvider() {
        return compression.getProvider();
    }
}
