package eapli.aisafe.flightplanmanagement.domain;

import eapli.framework.domain.model.ValueObject;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Value object representing the result of a file format validation.
 *
 * <p>Created by {@link FileFormatValidator#validate(java.nio.file.Path)}.</p>
 */
public class FormatValidationResult implements ValueObject {

    private final boolean valid;
    private final FileFormatType formatType;
    private final List<String> errors;

    public FormatValidationResult(final boolean valid,
                                  final FileFormatType formatType,
                                  final List<String> errors) {
        this.valid = valid;
        this.formatType = formatType;
        this.errors = errors != null ? List.copyOf(errors) : List.of();
    }

    public boolean isValid() {
        return valid;
    }

    public FileFormatType formatType() {
        return formatType;
    }

    public List<String> errors() {
        return Collections.unmodifiableList(errors);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof FormatValidationResult)) return false;
        final FormatValidationResult that = (FormatValidationResult) o;
        return valid == that.valid && formatType == that.formatType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(valid, formatType);
    }

    @Override
    public String toString() {
        return "FormatValidationResult{valid=" + valid + ", formatType=" + formatType + ", errors=" + errors + "}";
    }
}

