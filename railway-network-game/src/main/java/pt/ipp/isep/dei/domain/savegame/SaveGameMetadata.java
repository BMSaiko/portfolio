package pt.ipp.isep.dei.domain.savegame;

import java.time.LocalDateTime;

/**
 * Data structure representing game save metadata
 */
public class SaveGameMetadata {
    private LocalDateTime saveDate;
    private String gameVersion;
    private String playerName;
    private String saveName;

    public SaveGameMetadata(String saveName, String playerName, String gameVersion) {
        this.saveName = saveName;
        this.playerName = playerName;
        this.gameVersion = gameVersion;
        this.saveDate = LocalDateTime.now();
    }

    // Getters
    public LocalDateTime getSaveDate() {
        return saveDate;
    }

    public String getGameVersion() {
        return gameVersion;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getSaveName() {
        return saveName;
    }

    // Setters
    public void setSaveDate(LocalDateTime saveDate) {
        this.saveDate = saveDate;
    }

    public void setGameVersion(String gameVersion) {
        this.gameVersion = gameVersion;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public void setSaveName(String saveName) {
        this.saveName = saveName;
    }
}
