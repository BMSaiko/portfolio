package eapli.aisafe.usermanagement.domain;

import eapli.aisafe.generic.domain.UserName;
import eapli.aisafe.generic.domain.PhoneNumber;

import jakarta.persistence.*;
import eapli.framework.domain.model.AggregateRoot;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "T_USER")
public class User implements AggregateRoot<Email> {

    @EmbeddedId
    private Email email;

    private String passwordHash;


    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "USER_NAME", nullable = false))
    private UserName name;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "PHONE_NUMBER", nullable = false))
    private PhoneNumber phoneNumber;

    @Temporal(TemporalType.DATE)
    private Date securityClearanceExpiry;

    @Temporal(TemporalType.DATE)
    private Date lastSkillsAssessment;

    private boolean active;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "T_USER_ROLES", joinColumns = @JoinColumn(name = "EMAIL"))
    @Column(name = "ROLE_NAME")
    private Set<Role> roles = new HashSet<>();

    @Version
    private Long version;

    protected User() {}

    public User(Email email, String plainPassword, UserName name, PhoneNumber phoneNumber, Set<Role> roles) {
        this.email = email;
        setPassword(plainPassword);
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.active = true;
        this.roles.addAll(roles);
    }

    public void setPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.length() < 8)
            throw new IllegalArgumentException("Password must be at least 8 characters");
        boolean hasDigit = false;
        boolean hasUppercase = false;
        for (char c : plainPassword.toCharArray()) {
            if (Character.isDigit(c)) hasDigit = true;
            if (Character.isUpperCase(c)) hasUppercase = true;
        }
        if (!hasDigit)
            throw new IllegalArgumentException("Password must contain at least one digit");
        if (!hasUppercase)
            throw new IllegalArgumentException("Password must contain at least one uppercase letter");
        this.passwordHash = PasswordHasher.hashPassword(plainPassword);
    }

    public boolean passwordMatches(String plain) {
        return PasswordHasher.verifyPassword(plain, this.passwordHash);
    }

    public void activate() { this.active = true; }
    public void deactivate() { this.active = false; }
    public boolean isActive() { return active; }

    public boolean hasRole(Role role) { return roles.contains(role); }

    public Email email() { return email; }
    public UserName name() { return name; }
    public PhoneNumber phoneNumber() { return phoneNumber; }
    public Date securityClearanceExpiry() { return securityClearanceExpiry; }
    public Date lastSkillsAssessment() { return lastSkillsAssessment; }

    public Set<Role> roles() {
        return Collections.unmodifiableSet(roles);
    }

    @Override
    public Email identity() {
        return email;
    }

    @Override
    public boolean sameAs(Object other) {
        if (this == other) return true;
        if (!(other instanceof User)) return false;
        User that = (User) other;
        return email.equals(that.email);
    }

    public void changePhoneNumber(PhoneNumber phoneNumber) {
        if (phoneNumber == null)
            throw new IllegalArgumentException("Phone number is mandatory");
        this.phoneNumber = phoneNumber;
    }

    public User copyWithNewEmailAndPhone(Email newEmail, PhoneNumber newPhoneNumber) {
        User copy = new User();
        copy.email = newEmail;
        copy.passwordHash = this.passwordHash;
        copy.name = this.name;
        copy.phoneNumber = newPhoneNumber;
        copy.securityClearanceExpiry = this.securityClearanceExpiry;
        copy.lastSkillsAssessment = this.lastSkillsAssessment;
        copy.active = this.active;
        copy.roles = new HashSet<>(this.roles);
        return copy;
    }
}
