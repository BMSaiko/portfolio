package com.example.production.Utils;

import com.example.production.Domain.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Utility class to read data from CSV files and populate lists of articles, workstations, and production trees.
 */
public class CSVReader {

    /**
     * Reads a CSV file and processes the data based on the specified data type.
     *
     * @param filePath       The path of the CSV file to be read.
     * @param dataType       The type of data contained in the file ("article", "workstation", or "production").
     * @param articles       The list to store Article objects if data type is "article".
     * @param workstations   The list to store Workstation objects if data type is "workstation".
     * @param productionTrees The list to store ProductionTree objects if data type is "production".
     * @throws IOException If an error occurs while reading the file.
     */
    public static void readCSVFile(String filePath, String dataType, List<Article> articles, List<Workstation> workstations, List<ProductionTree> productionTrees) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            br.readLine();

            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(";");
                if (dataType.equalsIgnoreCase("article")) {
                    processArticleLine(tokens, articles);
                } else if (dataType.equalsIgnoreCase("workstation")) {
                    processWorkstationLine(tokens, workstations);
                } else if (dataType.equalsIgnoreCase("production")) {
                } else {
                    System.err.println("Unknown data type: " + dataType);
                }
            }
        }
    }

      /**
     * Reads and constructs ProductionTrees from the specified CSV files.
     *
     * @param booFilePath       Path to boo.csv
     * @param itemsFilePath     Path to items.csv
     * @param operationsFilePath Path to operations.csv
     * @return Map of ProductionTree objects keyed by product/item ID.
     * @throws IOException If an error occurs while reading the files.
     */
    public static Map<String, ProductionTree> readProductionTrees(String booFilePath, String itemsFilePath, String operationsFilePath) throws IOException {
        Map<String, String> itemsMap = readItemsCSV(itemsFilePath);
        Map<String, String> operationsMap = readOperationsCSV(operationsFilePath);
        Map<String, BooEntry> booEntriesMap = readBooCSV(booFilePath);

        Map<String, ProductionTree> productionTrees = new HashMap<>();

        for (BooEntry entry : booEntriesMap.values()) {
            String rootId = entry.getItemId();
            if (!productionTrees.containsKey(rootId)) {
                ProductionTree tree = new ProductionTree();
                ProductionNode rootNode = buildProductionTree(entry, itemsMap, operationsMap, booEntriesMap, tree);
                tree.setRoot(rootNode);
                productionTrees.put(rootId, tree);
            }
        }

        return productionTrees;
    }
      /**
     * Recursively builds the production tree starting from the given BooEntry.
     *
     * @param entry           The BooEntry to start from.
     * @param itemsMap        Map of item IDs to item names.
     * @param operationsMap   Map of operation IDs to operation names.
     * @param booEntriesMap   Map of BooEntry objects keyed by opId.
     * @param tree            The ProductionTree being built.
     * @return The root ProductionNode of the tree.
     */
    private static ProductionNode buildProductionTree(BooEntry entry, Map<String, String> itemsMap,
    Map<String, String> operationsMap, Map<String, BooEntry> booEntriesMap,
    ProductionTree tree) {
String itemId = entry.getItemId();
String itemName = itemsMap.getOrDefault(itemId, "Unknown Item");
ProductionNode rootNode = new ProductionNode(itemId, itemName, ProductionNode.NodeType.MATERIAL, entry.getItemQuantity());
tree.addNode(rootNode);


for (BooSubOperation subOp : entry.getSubOperations()) {
String opId = subOp.getOpId();
if (opId == null || opId.isEmpty()) {
continue;
}
String opName = operationsMap.getOrDefault(opId, "Unknown Operation");
ProductionNode opNode = new ProductionNode(opId, opName, ProductionNode.NodeType.OPERATION, subOp.getOpQuantity());
tree.addNode(opNode);


BooEntry subEntry = booEntriesMap.get(opId);
if (subEntry != null) {

ProductionNode subTreeRoot = buildProductionTree(subEntry, itemsMap, operationsMap, booEntriesMap, tree);
opNode.addChild(subTreeRoot);
}

rootNode.addChild(opNode);
}


for (BooSubItem subItem : entry.getSubItems()) {
String subItemId = subItem.getItemId();
if (subItemId == null || subItemId.isEmpty()) {
continue;
}
String subItemName = itemsMap.getOrDefault(subItemId, "Unknown Item");
ProductionNode subItemNode = new ProductionNode(subItemId, subItemName, ProductionNode.NodeType.MATERIAL, subItem.getItemQuantity());
tree.addNode(subItemNode);
rootNode.addChild(subItemNode);
}

return rootNode;
}

     /**
     * Reads items from items.csv and returns a map of item ID to item name.
     *
     * @param itemsFilePath Path to items.csv
     * @return Map of item IDs to item names.
     * @throws IOException If an error occurs while reading the file.
     */
    private static Map<String, String> readItemsCSV(String itemsFilePath) throws IOException {
        Map<String, String> itemsMap = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(itemsFilePath))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(";");
                if (tokens.length < 2) continue;
                String idItem = tokens[0].trim();
                String itemName = tokens[1].trim();
                itemsMap.put(idItem, itemName);
            }
        }
        return itemsMap;
    }


     /**
     * Reads operations from operations.csv and returns a map of operation ID to operation name.
     *
     * @param operationsFilePath Path to operations.csv
     * @return Map of operation IDs to operation names.
     * @throws IOException If an error occurs while reading the file.
     */
    private static Map<String, String> readOperationsCSV(String operationsFilePath) throws IOException {
        Map<String, String> operationsMap = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(operationsFilePath))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(";");
                if (tokens.length < 2) continue;
                String opId = tokens[0].trim();
                String opName = tokens[1].trim();
                operationsMap.put(opId, opName);
            }
        }
        return operationsMap;
    }

    /**
     * Reads boo.csv and returns a map of BooEntry objects keyed by opId.
     *
     * @param booFilePath Path to boo.csv
     * @return Map of BooEntry objects keyed by opId.
     * @throws IOException If an error occurs while reading the file.
     */
    private static Map<String, BooEntry> readBooCSV(String booFilePath) throws IOException {
        Map<String, BooEntry> booEntriesMap = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(booFilePath))) {
            String line;
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(";");
                if (tokens.length < 3) continue;

                String opId = tokens[0].trim();
                String itemId = tokens[1].trim();
                int itemQtd;
                try {
                    String qtyStr = tokens[2].trim().replace(",", ".");
                    itemQtd = (int) Double.parseDouble(qtyStr);
                } catch (NumberFormatException e) {
                    itemQtd = 1;
                }

                List<BooSubOperation> subOperations = new ArrayList<>();
                List<BooSubItem> subItems = new ArrayList<>();

                int index = 3;


                while (index < tokens.length && (tokens[index].trim().isEmpty() || tokens[index].trim().equals("("))) {
                    index++;
                }


                while (index + 1 < tokens.length && !tokens[index].trim().equals(")") && !tokens[index].trim().isEmpty()) {
                    String subOpId = tokens[index].trim();
                    if (subOpId.isEmpty()) {
                        index++;
                        continue;
                    }
                    String qtyStr = tokens[index + 1].trim().replace(",", ".");
                    int subOpQtd;
                    try {
                        subOpQtd = (int) Double.parseDouble(qtyStr);
                    } catch (NumberFormatException e) {
                        subOpQtd = 1;
                    }
                    subOperations.add(new BooSubOperation(subOpId, subOpQtd));
                    index += 2;
                }


                while (index < tokens.length && !tokens[index].trim().equals("(")) {
                    index++;
                }


                if (index < tokens.length && tokens[index].trim().equals("(")) {
                    index++;
                }


                while (index + 1 < tokens.length && !tokens[index].trim().equals(")") && !tokens[index].trim().isEmpty()) {
                    String subItemId = tokens[index].trim();
                    if (subItemId.isEmpty()) {
                        index++;
                        continue;
                    }
                    String qtyStr = tokens[index + 1].trim().replace(",", ".");
                    int subItemQtd;
                    try {
                        subItemQtd = (int) Double.parseDouble(qtyStr);
                    } catch (NumberFormatException e) {
                        subItemQtd = 1;
                    }
                    subItems.add(new BooSubItem(subItemId, subItemQtd));
                    index += 2;
                }

                booEntriesMap.put(opId, new BooEntry(opId, itemId, itemQtd, subOperations, subItems));
            }
        }
        return booEntriesMap;
    }
    /**
     * Processes a line from articles.csv and adds an Article to the list.
     *
     * @param tokens   The split line data from the CSV file.
     * @param articles The list to which the created Article object will be added.
     */
    private static void processArticleLine(String[] tokens, List<Article> articles) {
        if (tokens.length < 3) {
            return;
        }
        int articleId;
        try {
            articleId = Integer.parseInt(tokens[0]);
        } catch (NumberFormatException e) {
            articleId = 0;
        }
        String priority = tokens[1];
        List<String> nameOperations = new ArrayList<>();
        for (int i = 2; i < tokens.length; i++) {
            if (!tokens[i].isEmpty()) {
                nameOperations.add(tokens[i]);
            }
        }
        Article article = new Article(articleId, priority, nameOperations);
        articles.add(article);
    }

    /**
     * Processes a line from workstations.csv and adds a Workstation to the list.
     *
     * @param tokens       The split line data from the CSV file.
     * @param workstations The list to which the created Workstation object will be added.
     */
    private static void processWorkstationLine(String[] tokens, List<Workstation> workstations) {
        if (tokens.length < 3) {
            return;
        }
        String workstationId = tokens[0];
        String operationName = tokens[1];
        int time;
        try {
            time = Integer.parseInt(tokens[2]);
        } catch (NumberFormatException e) {
            time = 1;
        }
        Workstation workstation = new Workstation(workstationId, operationName, time);
        workstations.add(workstation);
    }
}
