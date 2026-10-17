package eapli.aisafe.usermanagement.domain;

/**
 * Roles supported by the AISafe system.
 *
 * <p>
 * Roles are used by the authentication and authorization layer to restrict
 * access to application functionalities.
 * </p>
 *
 * <p>
 * A user may have one or more roles.
 * </p>
 */
public enum Role {

    /**
     * System administrator.
     */
    ADMIN,

    /**
     * Backoffice operator responsible for administrative backoffice operations.
     */
    BACKOFFICE_OPERATOR,

    /**
     * Generic customer collaborator role.
     *
     * <p>
     * Users with this role can be associated with customers through the customer
     * collaborator workflow.
     * </p>
     */
    COLLABORATOR,

    /**
     * Air transport company collaborator role.
     *
     * <p>
     * Kept for compatibility with the initial domain model and other user
     * stories.
     * </p>
     */
    AIR_TRANSPORT_COMPANY_COLLABORATOR,

    /**
     * Pilot role.
     */
    PILOT,

    /**
     * Flight control operator role.
     */
    FLIGHT_CONTROL_OPERATOR,

    /**
     * Weather person role.
     */
    WEATHER_PERSON
}