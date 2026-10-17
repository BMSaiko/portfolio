package pt.ipp.isep.dei.service;

import pt.ipp.isep.dei.domain.Player;
import pt.ipp.isep.dei.domain.scenario.Scenario;
import pt.ipp.isep.dei.session.UserSession;

/**
 * Service to manage the current game session state
 */
public class GameSession {
    private Scenario currentScenario;
    private boolean isGameActive;

    public GameSession() {
        this.isGameActive = false;
    }

    /**
     * Gets the currently logged in player from the user session
     * 
     * @return the logged in player, or null if no player is logged in
     */
    public Player getLoggedInPlayer() {
        UserSession userSession = UserSession.getInstance();
        return userSession.getPlayer();
    }

    /**
     * Gets the current scenario
     * 
     * @return the current scenario, or null if no scenario is active
     */
    public Scenario getCurrentScenario() {
        return currentScenario;
    }

    /**
     * Sets the current scenario
     * 
     * @param scenario the scenario to set as current
     */
    public void setCurrentScenario(Scenario scenario) {
        this.currentScenario = scenario;
    }

    /**
     * Checks if a game is currently active
     * 
     * @return true if a game is active, false otherwise
     */
    public boolean isGameActive() {
        return isGameActive && getLoggedInPlayer() != null;
    }

    /**
     * Starts a new game session
     * 
     * @param scenario the scenario to start the game with
     */
    public void startGame(Scenario scenario) {
        this.currentScenario = scenario;
        this.isGameActive = true;
    }

    /**
     * Ends the current game session
     */
    public void endGame() {
        this.currentScenario = null;
        this.isGameActive = false;
    }

    /**
     * Restores a game session from saved data
     * 
     * @param player the player data to restore
     * @param scenario the scenario data to restore
     */
    public void restoreGameSession(Player player, Scenario scenario) {
        UserSession userSession = UserSession.getInstance();
        userSession.setPlayer(player);
        this.currentScenario = scenario;
        this.isGameActive = true;
    }
}
