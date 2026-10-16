package eapli.aisafe.enginemanagement.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

/**
 * Value object representing the unique identity of an EngineModel.
 * Uniqueness is enforced by the combination of name + makerId.
 *
 * <p>Must implement {@link Serializable} as required by JPA for
 * classes used as {@code @EmbeddedId}.
 */
@Embeddable
public class EngineModelId implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "engine_name", nullable = false)
    private String name;

    @Column(name = "engine_maker_id", nullable = false)
    private String makerId;

    protected EngineModelId() {}

    private EngineModelId(final String name, final String makerId) {
        this.name = name;
        this.makerId = makerId;
    }

    public static EngineModelId of(final String name, final String makerId) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Engine model name must not be empty.");
        if (makerId == null || makerId.isBlank())
            throw new IllegalArgumentException("Maker ID must not be empty.");
        return new EngineModelId(name.trim(), makerId.trim());
    }

    public String name() { return name; }
    public String makerId() { return makerId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EngineModelId)) return false;
        EngineModelId other = (EngineModelId) o;
        return name.equalsIgnoreCase(other.name) && makerId.equals(other.makerId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name.toLowerCase(), makerId);
    }

    @Override
    public String toString() {
        return name + " (maker: " + makerId + ")";
    }
}