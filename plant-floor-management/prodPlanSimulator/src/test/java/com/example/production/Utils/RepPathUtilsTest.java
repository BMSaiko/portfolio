package com.example.production.Utils;

import org.junit.jupiter.api.Test;

import com.example.production.Utils.RepPathUtils;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class RepPathUtilsTest {
    @Test
    public void testGetRepBasePath() {
        try {
            Path basePath = RepPathUtils.getRepBasePath();
            assertNotNull(basePath, "Repository base path should not be null");
            assertTrue(basePath.toFile().exists(), "Base path should exist");
        } catch (IllegalStateException e) {
            fail("Failed to determine the repository base path: " + e.getMessage());
        }
    }

    @Test
    public void testGetPathRelativeToRep() {
        String relativePath = "documentation/ESINF/USEI01/articles.csv";
        Path fullPath = RepPathUtils.getPathRelativeToRep(relativePath);
        assertNotNull(fullPath, "Full path should not be null");
        assertTrue(fullPath.toFile().exists() || fullPath.startsWith(RepPathUtils.getRepBasePath()), "Full path should either exist or be properly resolved relative to repository base path");
    }

    @Test
    public void testGetAbsolutePath() {
        String relativePath = "documentation/ESINF/USEI01/articles.csv";
        String absolutePath = RepPathUtils.getAbsolutePath(relativePath);

        assertNotNull(absolutePath, "Absolute path should not be null");

        Path absolutePathAsPath = Path.of(absolutePath);
        assertTrue(absolutePathAsPath.toFile().exists() || absolutePath.endsWith(relativePath),
                "Absolute path should either exist or end with the relative path");
    }

}
