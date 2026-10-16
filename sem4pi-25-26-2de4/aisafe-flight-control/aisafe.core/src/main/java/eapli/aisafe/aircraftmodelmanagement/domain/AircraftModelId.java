package eapli.aisafe.aircraftmodelmanagement.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

/**
 * Value object representing the unique identity of an AircraftModel.
 * Uniqueness is enforced by the combination of modelId + makerName.
 *
 * <p>Must implement {@link Serializable} as required by JPA for
 * classes used as {@code @EmbeddedId}.
 */
@Embeddable
public class AircraftModelId implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "model_id", nullable = false)
    private String modelId;

    @Column(name = "maker_name", nullable = false)
    private String makerName;

    protected AircraftModelId() {}

    private AircraftModelId(final String modelId, final String makerName) {
        this.modelId = modelId;
        this.makerName = makerName;
    }

    public static AircraftModelId of(final String modelId, final String makerName) {
        if (modelId == null || modelId.isBlank())
            throw new IllegalArgumentException("Model ID must not be empty.");
        if (makerName == null || makerName.isBlank())
            throw new IllegalArgumentException("Maker name must not be empty.");
        return new AircraftModelId(modelId.trim(), makerName.trim());
    }

    public String modelId()   { return modelId; }
    public String makerName() { return makerName; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AircraftModelId)) return false;
        AircraftModelId other = (AircraftModelId) o;
        return modelId.equalsIgnoreCase(other.modelId)
                && makerName.equalsIgnoreCase(other.makerName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(modelId.toLowerCase(), makerName.toLowerCase());
    }

    @Override
    public String toString() {
        return modelId + " (" + makerName + ")";
    }
}