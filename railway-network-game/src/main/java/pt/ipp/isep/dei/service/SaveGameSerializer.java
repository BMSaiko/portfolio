package pt.ipp.isep.dei.service;

import pt.ipp.isep.dei.domain.savegame.SaveGameData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import java.time.LocalDateTime;

/**
 * Service responsible for serializing and deserializing game save data
 */
public class SaveGameSerializer {
    private final Gson gson;

    public SaveGameSerializer() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    /**
     * Serializes SaveGameData to JSON string
     * 
     * @param saveData the save data to serialize
     * @return JSON string representation of the save data
     * @throws IllegalArgumentException if saveData is null
     */
    public String serializeToJson(SaveGameData saveData) {
               throw new UnsupportedOperationException("Not implemented yet.");

    }

    /**
     * Deserializes JSON string to SaveGameData
     * 
     * @param jsonData the JSON string to deserialize
     * @return SaveGameData object
     * @throws IllegalArgumentException if jsonData is null or empty
     * @throws RuntimeException if deserialization fails
     */
    public SaveGameData deserializeFromJson(String jsonData) {
                throw new UnsupportedOperationException("Not implemented yet.");

    }

    /**
     * Validates that the given JSON string is valid save game data
     * 
     * @param jsonData the JSON string to validate
     * @return true if valid, false otherwise
     */
    public boolean isValidSaveData(String jsonData) {
              throw new UnsupportedOperationException("Not implemented yet.");

    }
}
