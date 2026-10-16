package pt.isep.sem4pi.lprog.model;

public class Fuel {
    private double quantity;
    private String unit;
    private int line;
    private int column;

    public Fuel(double quantity, String unit, int line, int column) {
        this.quantity = quantity;
        this.unit = unit;
        this.line = line;
        this.column = column;
    }

    public double getQuantity() { return quantity; }
    public String getUnit() { return unit; }
    public int getLine() { return line; }
    public int getColumn() { return column; }
}