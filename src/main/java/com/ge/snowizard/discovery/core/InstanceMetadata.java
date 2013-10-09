package com.ge.snowizard.discovery.core;

import java.util.UUID;
import javax.annotation.concurrent.Immutable;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotEmpty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Objects;
import com.yammer.dropwizard.validation.PortRange;

@Immutable
public final class InstanceMetadata {

    @NotNull
    private final UUID instanceId;

    @NotEmpty
    private final String listenAddress;

    @PortRange
    private final int listenPort;

    @JsonCreator
    public InstanceMetadata(@JsonProperty("instanceId") UUID instanceId,
            @JsonProperty("listenAddress") String listenAddress,
            @JsonProperty("listenPort") int listenPort) {
        this.instanceId = instanceId;
        this.listenAddress = listenAddress;
        this.listenPort = listenPort;
    }

    @JsonProperty
    public UUID getInstanceId() {
        return instanceId;
    }

    @JsonProperty
    public String getListenAddress() {
        return listenAddress;
    }

    @JsonProperty
    public int getListenPort() {
        return listenPort;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }

        final InstanceMetadata other = (InstanceMetadata) obj;
        return Objects.equal(instanceId, other.instanceId)
                && Objects.equal(listenAddress, other.listenAddress)
                && Objects.equal(listenPort, other.listenPort);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(instanceId, listenAddress, listenPort);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("instanceId", instanceId)
                .add("listenAddress", listenAddress)
                .add("listenPort", listenPort).toString();
    }
}
