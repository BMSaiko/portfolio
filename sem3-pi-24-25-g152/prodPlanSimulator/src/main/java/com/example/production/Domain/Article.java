package com.example.production.Domain;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Represents an article in production.
 * This class manages the lifecycle and operations of an article in the production line.
 * It handles the article's unique identification, priority level, and sequence of operations.
 */
public class Article {
    private static final Logger logger = LogManager.getLogger(Article.class);

    private final int articleId;
    private final String priority;
    private final List<String> nameOperations;
    private int currentOperationIndex;

    /**
     * Constructs an Article with the specified ID, priority, and list of operations.
     * The article starts at the first operation in the sequence.
     *
     * @param articleId      The unique identifier of the article
     * @param priority       The priority level of the article (high, normal, or low)
     * @param nameOperations The list of operation names that need to be performed on the article
     */
    public Article(int articleId, String priority, List<String> nameOperations) {
        this.articleId = articleId;
        this.priority = priority;
        this.nameOperations = new CopyOnWriteArrayList<>(nameOperations);
        this.currentOperationIndex = 0;
        logger.debug("Article {} created with priority '{}'. Operations: {}", articleId, priority, nameOperations);
    }

    /**
     * Returns the unique identifier of the article.
     *
     * @return The article's ID
     */
    public int getArticleId() {
        return articleId;
    }

    /**
     * Returns the priority string of the article.
     *
     * @return The priority level as a string (high, normal, or low)
     */
    public String getPriority() {
        return priority;
    }

    /**
     * Returns the list of operations that need to be performed on the article.
     *
     * @return The list of operation names
     */
    public List<String> getNameOperations() {
        return nameOperations;
    }

    public synchronized boolean moveToNextOperation() {
        if (currentOperationIndex < nameOperations.size() - 1) {
            currentOperationIndex++;
            logger.debug("Article {} moved to next operation: '{}'", articleId, getCurrentOperation());
            return true;
        } else if (currentOperationIndex == nameOperations.size() - 1) {
            currentOperationIndex++;
            logger.info("Article {} has completed all operations.", articleId);
        }
        return false;
    }
    
    public String getCurrentOperation() {
        if (currentOperationIndex < nameOperations.size()) {
            return nameOperations.get(currentOperationIndex);
        }
        return null;
    }
    

    /**
     * Converts the priority string to a numeric priority level.
     * Higher numbers indicate higher priority.
     * Priority levels are:
     * - High: 3
     * - Normal: 2
     * - Low: 1 (default)
     *
     * @return The numeric priority level
     */
    public int getPriorityLevel() {
        switch (priority.toLowerCase()) {
            case "high":
                return 3; // High priority
            case "normal":
                return 2; // Normal priority
            case "low":
            default:
                return 1; // Low priority
        }
    }
}
