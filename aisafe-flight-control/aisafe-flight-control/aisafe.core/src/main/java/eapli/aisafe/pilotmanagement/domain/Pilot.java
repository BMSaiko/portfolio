package eapli.aisafe.pilotmanagement.domain;

import eapli.aisafe.aircraftmodelmanagement.domain.AircraftModel;
import eapli.aisafe.organization.domain.AirTransportCompany;
import eapli.aisafe.usermanagement.domain.Email;
import eapli.aisafe.usermanagement.domain.User;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "T_PILOT")
public class Pilot {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "SYSTEM_USER_ID", unique = true)
    private User systemUser;

    @Embedded
    @AttributeOverride(name = "emailAddress", column = @Column(name = "EMAIL", unique = true, nullable = false))
    private Email email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String position;

    @ManyToOne(optional = false)
    @JoinColumn(name = "COMPANY_ID")
    private AirTransportCompany company;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "T_PILOT_CERTIFICATIONS",
            joinColumns = @JoinColumn(name = "PILOT_ID"),
            inverseJoinColumns = {
                    @JoinColumn(name = "AIRCRAFT_MODEL_ID", referencedColumnName = "model_id"),
                    @JoinColumn(name = "AIRCRAFT_MAKER_NAME", referencedColumnName = "maker_name")
            })
    private Set<AircraftModel> certifications = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PilotStatus status;

    protected Pilot() {
        // for ORM only
    }

    public Pilot(User systemUser, Email email, String name, String position, AirTransportCompany company, Set<AircraftModel> certifications) {
        if (systemUser == null || email == null || name == null || name.isBlank() || position == null || position.isBlank() || company == null) {
            throw new IllegalArgumentException("Pilot attributes cannot be null or empty.");
        }
        if (certifications == null || certifications.isEmpty()) {
            throw new IllegalArgumentException("A pilot must be certified for at least one aircraft model.");
        }

        this.systemUser = systemUser;
        this.email = email;
        this.name = name;
        this.position = position;
        this.company = company;
        this.certifications.addAll(certifications);
        this.status = PilotStatus.ACTIVE;
    }

    public Email email() {
        return email;
    }

    public AirTransportCompany company() {
        return company;
    }

    public void deactivate() {
        this.status = PilotStatus.INACTIVE;
    }

    public boolean isActive() {
        return this.status == PilotStatus.ACTIVE;
    }

    public String getName() { return this.name; }

    public String getPosition() { return this.position; }
}