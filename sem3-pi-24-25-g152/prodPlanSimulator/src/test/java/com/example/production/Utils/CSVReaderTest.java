package com.example.production.Utils;

import com.example.production.Domain.Article;
import com.example.production.Domain.ProductionTree;
import com.example.production.Domain.Workstation;
import org.junit.jupiter.api.Test;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import com.example.production.Utils.CSVReader;

class CSVReaderTest {

    @Test
    void testReadCSVFileArticles() throws Exception {
        List<Article> articles = new ArrayList<>();
        List<Workstation> workstations = new ArrayList<>();

        String articlesFilePath = "src/test/resources/articles_test.csv";
        CSVReader.readCSVFile(articlesFilePath, "article", articles, workstations, null);

        assertEquals(3, articles.size());
        Article article1 = articles.get(0);
        assertEquals(1, article1.getArticleId());
        assertEquals("HIGH", article1.getPriority());
        assertEquals(Arrays.asList("CUT", "POLISH"), article1.getNameOperations());
    }

    @Test
    void testReadCSVFileWorkstations() throws Exception {
        List<Article> articles = new ArrayList<>();
        List<Workstation> workstations = new ArrayList<>();

        String workstationsFilePath = "src/test/resources/workstations_test.csv";
        CSVReader.readCSVFile(workstationsFilePath, "workstation", articles, workstations, null);

        assertEquals(3, workstations.size());
        Workstation ws1 = workstations.get(0);
        assertEquals("ws1", ws1.getWorkstationId());
        assertEquals("CUT", ws1.getOperationName());
        assertEquals(10, ws1.getTime());
    }

    @Test
    void testReadProductionTrees() throws Exception {
        String booFilePath = "src/test/resources/boo_test.csv";
        String itemsFilePath = "src/test/resources/items_test.csv";
        String operationsFilePath = "src/test/resources/operations_test.csv";

        Map<String, ProductionTree> productionTrees = CSVReader.readProductionTrees(booFilePath, itemsFilePath, operationsFilePath);

        assertEquals(2, productionTrees.size());
        ProductionTree tree = productionTrees.get("200");
        assertNotNull(tree);
        assertEquals("200", tree.getRoot().getId());
        assertEquals("Finished Product", tree.getRoot().getName());
    }
}
