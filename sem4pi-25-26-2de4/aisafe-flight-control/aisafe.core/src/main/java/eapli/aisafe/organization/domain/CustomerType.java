package eapli.aisafe.organization.domain;

/**
 * Customer types supported by the organization/customer collaborator module.
 *
 * <p>
 * This enum is used to identify the kind of customer associated with a
 * {@link CustomerCollaborator}.
 * </p>
 */
public enum CustomerType {

    /**
     * Customer is an air transport company.
     */
    AIR_TRANSPORT_COMPANY,

    /**
     * Customer is an air control area.
     */
    AIR_CONTROL_AREA
}
