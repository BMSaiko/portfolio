package eapli.aisafe.pilotmanagement.dto;

import java.io.Serializable;
import java.util.List;

/**
 * DTO para transportar os dados de registo de um piloto
 * entre a aplicação cliente (TCP) e o servidor.
 */
public class PilotRegistrationDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private String email;
    private String phoneNumber;
    private String position;
    private String password;
    private List<String> aircraftModelIds;

    public PilotRegistrationDTO(String name, String email, String phoneNumber, String position, String password, List<String> aircraftModelIds) {
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.position = position;
        this.password = password;
        this.aircraftModelIds = aircraftModelIds;
    }

    public String getName() { return name; }

    public String getEmail() { return email; }

    public String getPhoneNumber() { return phoneNumber; }

    public String getPosition() { return position; }

    public String getPassword() { return password; }

    public List<String> getAircraftModelIds() { return aircraftModelIds; }
}