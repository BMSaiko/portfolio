package pt.ipp.isep.dei.session;

import pt.ipp.isep.dei.domain.Player;
import pt.ipp.isep.dei.domain.locomotive.Locomotive;

import java.util.Date;

/**
 * Singleton class to manage user session state
 */
public class UserSession {
    private static UserSession instance;
    private Player currentPlayer;
    private String userEmail;

    public UserSession() {
        // Private constructor for singleton
    }

    public Date getCurrentDate() {
        return null;
    }

    /**
     * Gets the singleton instance of UserSession
     * 
     * @return the UserSession instance
     */
    public static synchronized UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    /**
     * Gets the current player
     * 
     * @return the current player, or null if no player is logged in
     */
    public Player getPlayer() {
        return currentPlayer;
    }

    /**
     * Sets the current player
     * 
     * @param player the player to set
     */
    public void setPlayer(Player player) {
        this.currentPlayer = player;
    }

    /**
     * Gets the current user email
     * 
     * @return the user email, or null if not set
     */
    public String getUserEmail() {
        return userEmail;
    }

    /**
     * Sets the current user email
     * 
     * @param email the email to set
     */
    public void setUserEmail(String email) {
        this.userEmail = email;
    }

    /**
     * Checks if a user is currently logged in
     * 
     * @return true if a user is logged in, false otherwise
     */
    public boolean isLoggedIn() {
        return currentPlayer != null;
    }

    /**
     * Logs out the current user
     */
    public void logout() {
        this.currentPlayer = null;
        this.userEmail = null;
    }


    public double updateBudget(double amount) {
        return 0.0;
    }


    public boolean addLocomotiveToFleet(Locomotive locomotive) {
        return false;
    }
}
