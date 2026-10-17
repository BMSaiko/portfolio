package eapli.aisafe.organization.domain;

import jakarta.persistence.*;
import java.util.regex.Pattern;

/**
 * Domain entity representing an air transport company.
 *
 * <p>
 * This entity supports US060 - Register an air transport company. An air
 * transport company has a company name, a unique IATA code and a unique ICAO
 * code.
 * </p>
 *
 * <p>
 * Air transport companies can also act as customers in the customer
 * collaborator workflow used by US061, US062, US063 and US064.
 * </p>
 */
@Entity
@Table(
        name = "T_AIR_TRANSPORT_COMPANY",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "IATA_CODE"),
                @UniqueConstraint(columnNames = "ICAO_CODE")
        }
)
public class AirTransportCompany {

    private static final Pattern IATA_PATTERN = Pattern.compile("^[A-Z]{2}$");
    private static final Pattern ICAO_PATTERN = Pattern.compile("^[A-Z]{2,3}$");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String companyName;

    @Column(name = "IATA_CODE", nullable = false, unique = true, length = 2)
    private String iataCode;

    @Column(name = "ICAO_CODE", nullable = false, unique = true, length = 3)
    private String icaoCode;

    /**
     * Protected constructor required by JPA.
     */
    protected AirTransportCompany() {
    }

    /**
     * Creates an air transport company.
     *
     * <p>
     * IATA and ICAO codes are normalized to uppercase before being validated and
     * stored.
     * </p>
     *
     * @param companyName company name
     * @param iataCode IATA code with exactly two letters
     * @param icaoCode ICAO code with two or three letters
     * @throws IllegalArgumentException if the company name, IATA code or ICAO
     *                                  code is invalid
     */
    public AirTransportCompany(String companyName, String iataCode, String icaoCode) {
        if (companyName == null || companyName.trim().isEmpty()) {
            throw new IllegalArgumentException("Company name is mandatory");
        }

        String normalizedIata = iataCode == null ? "" : iataCode.trim().toUpperCase();
        String normalizedIcao = icaoCode == null ? "" : icaoCode.trim().toUpperCase();

        if (!IATA_PATTERN.matcher(normalizedIata).matches()) {
            throw new IllegalArgumentException("IATA code must have exactly 2 letters");
        }

        if (!ICAO_PATTERN.matcher(normalizedIcao).matches()) {
            throw new IllegalArgumentException("ICAO code must have 2 or 3 letters");
        }

        this.companyName = companyName.trim();
        this.iataCode = normalizedIata;
        this.icaoCode = normalizedIcao;
    }

    /**
     * Returns the internal persistence identifier.
     *
     * @return entity identifier
     */
    public Long id() {
        return id;
    }

    /**
     * Returns the company name.
     *
     * @return company name
     */
    public String companyName() {
        return companyName;
    }

    /**
     * Returns the IATA code.
     *
     * @return IATA code
     */
    public String iataCode() {
        return iataCode;
    }

    /**
     * Returns the ICAO code.
     *
     * @return ICAO code
     */
    public String icaoCode() {
        return icaoCode;
    }

    /**
     * Returns the customer code used when this company is referenced as a
     * customer.
     *
     * <p>
     * For air transport companies, the customer code is the ICAO code.
     * </p>
     *
     * @return customer code
     */
    public String customerCode() {
        return icaoCode;
    }

    /**
     * Returns a readable representation of the air transport company.
     *
     * @return formatted company description
     */
    @Override
    public String toString() {
        return companyName + " | IATA: " + iataCode + " | ICAO: " + icaoCode;
    }
}
