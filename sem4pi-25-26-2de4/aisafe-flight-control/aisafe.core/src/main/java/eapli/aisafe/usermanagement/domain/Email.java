package eapli.aisafe.usermanagement.domain;

import jakarta.persistence.Embeddable;
import eapli.framework.domain.model.ValueObject;
import java.io.Serializable;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value object representing a user's email address.
 *
 * <p>
 * The email is used as the user identity in the current domain model and is
 * embedded in JPA entities.
 * </p>
 *
 * <p>
 * Email values are normalized to lowercase to avoid duplicates caused by
 * letter casing.
 * </p>
 */
@Embeddable
public class Email implements ValueObject, Comparable<Email>, Serializable {
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private String address;

    /**
     * Protected constructor required by JPA.
     */
    protected Email() {}

    /**
     * Creates an email value object.
     *
     * @param address email address
     * @throws IllegalArgumentException if the email address is null or invalid
     */
    public Email(String address) {
        if (address == null || !EMAIL_PATTERN.matcher(address).matches())
            throw new IllegalArgumentException("Invalid email address");
        this.address = address.toLowerCase();
    }


    /**
     * Returns the email address value.
     *
     * @return email address
     */
    public String value() { return address; }

    /**
     * Compares emails by their normalized address.
     *
     * @param o object to compare
     * @return {@code true} if both emails have the same address
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Email)) return false;
        Email email = (Email) o;
        return Objects.equals(address, email.address);
    }

    /**
     * Returns the hash code based on the normalized address.
     *
     * @return hash code
     */
    @Override
    public int hashCode() { return Objects.hash(address); }

    /**
     * Returns the email address as text.
     *
     * @return email address
     */
    @Override
    public String toString() { return address; }

    @Override
    public int compareTo(Email other) {
        return this.address.compareTo(other.address);
    }
}
