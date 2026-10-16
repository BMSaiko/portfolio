package com.example.production.Domain;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ArticleTest {

    @Test
    void testArticleCreation() {
        List<String> operations = Arrays.asList("CUT", "POLISH", "VARNISH");
        Article article = new Article(1, "HIGH", operations);

        assertEquals(1, article.getArticleId());
        assertEquals("HIGH", article.getPriority());
        assertEquals(operations, article.getNameOperations());
    }

    @Test
    void testGetCurrentOperation() {
        List<String> operations = Arrays.asList("CUT", "POLISH");
        Article article = new Article(2, "NORMAL", operations);

        assertEquals("CUT", article.getCurrentOperation());
        article.moveToNextOperation();
        assertEquals("POLISH", article.getCurrentOperation());
        article.moveToNextOperation();
        assertNull(article.getCurrentOperation());
    }

    @Test
    void testGetPriorityLevel() {
        Article highPriorityArticle = new Article(3, "HIGH", Arrays.asList("CUT"));
        Article normalPriorityArticle = new Article(4, "NORMAL", Arrays.asList("CUT"));
        Article lowPriorityArticle = new Article(5, "LOW", Arrays.asList("CUT"));
        Article unknownPriorityArticle = new Article(6, "UNKNOWN", Arrays.asList("CUT"));

        assertEquals(3, highPriorityArticle.getPriorityLevel());
        assertEquals(2, normalPriorityArticle.getPriorityLevel());
        assertEquals(1, lowPriorityArticle.getPriorityLevel());
        assertEquals(1, unknownPriorityArticle.getPriorityLevel());
    }

    @Test
    void testMoveToNextOperation() {
        Article article = new Article(7, "NORMAL", Arrays.asList("CUT", "POLISH"));
        assertTrue(article.moveToNextOperation()); // Moves to POLISH
        assertFalse(article.moveToNextOperation()); // No more operations
        assertFalse(article.moveToNextOperation()); // Should still be false
    }

}
