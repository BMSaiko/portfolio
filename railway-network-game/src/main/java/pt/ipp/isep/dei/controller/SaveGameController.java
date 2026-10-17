package pt.ipp.isep.dei.controller;

import pt.ipp.isep.dei.domain.Player;
import pt.ipp.isep.dei.domain.scenario.Scenario;
import pt.ipp.isep.dei.domain.savegame.SaveGameData;
import pt.ipp.isep.dei.service.AuthenticationService;
import pt.ipp.isep.dei.service.GameSession;
import pt.ipp.isep.dei.service.SaveGameService;
import pt.ipp.isep.dei.session.UserSession;

import java.util.List;

/**
 * Controller for save game operations
 */
public class SaveGameController {
    
    private final SaveGameService saveGameService;
    private final AuthenticationService authService;
    private final GameSession gameSession;
    private final UserSession userSession;

    public SaveGameController() {
        this.saveGameService = new SaveGameService();
        this.authService = new AuthenticationService();
        this.gameSession = new GameSession();
        this.userSession = UserSession.getInstance();
    }

    /**
     * Saves the current game state to a file
     * 
     * @param fileName the name for the save file
     * @return true if save was successful, false otherwise
     */
    public boolean saveCurrentGame(String fileName) {
        throw new UnsupportedOperationException("Not implemented yet.");
       
    }

    /**
     * Loads a game from a save file
     * 
     * @param fileName the name of the save file to load
     * @return the loaded save game data, or null if loading failed
     */
    public SaveGameData loadGame(String fileName) {
              throw new UnsupportedOperationException("Not implemented yet.");

    }

    /**
     * Gets a list of all available save files
     * 
     * @return list of save file names
     */
    public List<String> getAvailableSaveFiles() {
        return saveGameService.getAvailableSaveFiles();
    }

    /**
     * Checks if a save file with the given name already exists
     * 
     * @param fileName the name of the save file
     * @return true if the file exists, false otherwise
     */
    public boolean saveFileExists(String fileName) {
        return saveGameService.saveFileExists(fileName);
    }

    /**
     * Deletes a save file
     * 
     * @param fileName the name of the save file to delete
     * @return true if deletion was successful, false otherwise
     */
    public boolean deleteSaveFile(String fileName) {
        return saveGameService.deleteSaveFile(fileName);
    }

    /**
     * Validates a filename for saving
     * 
     * @param fileName the filename to validate
     * @return true if valid, false otherwise
     */
    public boolean isValidFileName(String fileName) {
                throw new UnsupportedOperationException("Not implemented yet.");

    }

    /**
     * Gets the currently logged in player
     * 
     * @return the current player, or null if no player is logged in
     */
    private Player getCurrentPlayer() {
        try {
            // This would typically get the player from the game session or user session
            return gameSession.getLoggedInPlayer();
        } catch (UnsupportedOperationException e) {
            // For now, return null since the method is not implemented
            System.err.println("Player retrieval not implemented yet");
            return null;
        }
    }    /**
     * Gets the current scenario
     * 
     * @return the current scenario, or null if no scenario is active
     */
    private Scenario getCurrentScenario() {
        try {
            return gameSession.getCurrentScenario();
        } catch (Exception e) {
            System.err.println("Error retrieving current scenario: " + e.getMessage());
            return null;
        }
    }

    /**
     * Checks if there is an active game session
     * 
     * @return true if there is an active game session, false otherwise
     */
    public boolean hasActiveGameSession() {
        try {
            return getCurrentPlayer() != null;
        } catch (Exception e) {
            return false;
        }
    }
}
