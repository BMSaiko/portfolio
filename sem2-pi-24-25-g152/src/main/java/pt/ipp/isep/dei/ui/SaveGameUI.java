package pt.ipp.isep.dei.ui;

import pt.ipp.isep.dei.controller.SaveGameController;
import pt.ipp.isep.dei.domain.savegame.SaveGameData;

import java.util.List;
import java.util.Scanner;

/**
 * User interface for save game operations
 */
public class SaveGameUI implements Runnable {

    private final SaveGameController controller;
    private final Scanner scanner;

    public SaveGameUI() {
        this.controller = new SaveGameController();
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void run() {
               throw new UnsupportedOperationException("Not implemented yet.");

    }

    /**
     * Shows the save game menu and handles user input
     */
    private void showSaveGameMenu() {
          throw new UnsupportedOperationException("Not implemented yet.");
}

    /**
     * Handles saving the current game
     */
    private void handleSaveGame() {
           throw new UnsupportedOperationException("Not implemented yet.");

    }

    /**
     * Handles loading a game from save file
     */
    private void handleLoadGame() {
                throw new UnsupportedOperationException("Not implemented yet.");

    }

    /**
     * Handles listing all save files
     */
    private void handleListSaveFiles() {
               throw new UnsupportedOperationException("Not implemented yet.");

    }

    /**
     * Handles deleting a save file
     */
    private void handleDeleteSaveFile() {
                throw new UnsupportedOperationException("Not implemented yet.");

    }

    /**
     * Displays information about a loaded save game
     * 
     * @param saveData the save game data to display
     */
    private void displaySaveGameInfo(SaveGameData saveData) {
                throw new UnsupportedOperationException("Not implemented yet.");
    }
}
