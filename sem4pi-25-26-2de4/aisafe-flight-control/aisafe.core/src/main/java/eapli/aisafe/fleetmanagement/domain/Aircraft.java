package eapli.aisafe.fleetmanagement.domain;

import eapli.framework.domain.model.AggregateRoot;
import eapli.framework.domain.model.DomainEntities;
import eapli.aisafe.aircraftmodelmanagement.domain.AircraftModel;
import eapli.aisafe.organization.domain.AirTransportCompany;

import jakarta.persistence.*;
import java.time.Year;

@Entity
@Table(name = "T_AIRCRAFT")
public class Aircraft implements AggregateRoot<String> {

    @Id
    @Column(name = "REGISTRATION_NUMBER", nullable = false, unique = true)
    private String registration;

    @ManyToOne(optional = false)
    @JoinColumns({
            @JoinColumn(name = "MODEL_ID", referencedColumnName = "model_id"),
            @JoinColumn(name = "MAKER_NAME", referencedColumnName = "maker_name")
    })
    private AircraftModel model;

    @ManyToOne(optional = false)
    @JoinColumn(name = "COMPANY_ID")
    private AirTransportCompany company;

    @Column(name = "COUNTRY", nullable = false)
    private String country;

    @Embedded
    private CabinConfiguration cabinConfiguration;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false)
    private OperationalStatus status;

    @Column(name = "MANUFACTURING_YEAR", nullable = false)
    private Integer manufacturingYear;

    protected Aircraft() {
        // For ORM only
    }

    public Aircraft(String registration, AircraftModel model, AirTransportCompany company,
                    String country, CabinConfiguration cabinConfiguration, OperationalStatus status, Integer manufacturingYear) {

        // Validações de Nulls e Vazios PRIMEIRO (Evita NullPointerExceptions)
        if (registration == null || registration.trim().isEmpty()) {
            throw new IllegalArgumentException("Aircraft registration cannot be empty.");
        }
        if (model == null || company == null) {
            throw new IllegalArgumentException("Aircraft Model and Air Transport Company are mandatory.");
        }
        if (country == null || country.trim().isEmpty()) {
            throw new IllegalArgumentException("Registration country is obligatory.");
        }
        if (cabinConfiguration == null) {
            throw new IllegalArgumentException("Cabin configuration cannot be null.");
        }
        if (status == null) {
            throw new IllegalArgumentException("Operational status cannot be null.");
        }

        if (manufacturingYear == null || manufacturingYear > Year.now().getValue() || manufacturingYear < 1900) {
            throw new IllegalArgumentException("Manufacturing year must be valid and cannot be in the future.");
        }

        // Só depois de garantir que NÃO são nulos é que chamamos os métodos
        if (cabinConfiguration.totalSeats() > model.maxPassengerCapacity()) {
            throw new IllegalArgumentException("Total number of seats exceeds the model's maximum capacity.");
        }

        this.registration = registration.trim().toUpperCase();
        this.model = model;
        this.company = company;
        this.country = country;
        this.cabinConfiguration = cabinConfiguration;
        this.status = status;
        this.manufacturingYear = manufacturingYear;
    }

    public Aircraft(String registration, AircraftModel model, AirTransportCompany company,
                    String country, CabinConfiguration cabinConfiguration, OperationalStatus status) {
        this(registration, model, company, country, cabinConfiguration, status, Year.now().getValue());
    }

    @Override
    public boolean sameAs(Object other) {
        return DomainEntities.areEqual(this, other);
    }

    @Override
    public String identity() {
        return this.registration;
    }

    public void decommission() {
        if (this.status == OperationalStatus.RETIRED) {
            throw new IllegalStateException("Aircraft is already retired.");
        }
        this.status = OperationalStatus.RETIRED;
    }

    public OperationalStatus status() {
        return this.status;
    }

    public int age() {
        return Year.now().getValue() - this.manufacturingYear;
    }

    public AircraftModel model() { return this.model; }
    public CabinConfiguration cabinConfiguration() { return this.cabinConfiguration; }
    public AirTransportCompany company() { return this.company; }
}