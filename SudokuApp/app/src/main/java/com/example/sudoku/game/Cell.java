package com.example.sudoku.game;

public class Cell {
    private int row;
    private int col;
    private int value; // 0 represents an empty cell
    private boolean isStartingCell; // Was this cell part of the initial puzzle?
    private boolean isEditable;

    public Cell(int row, int col, int value) {
        this.row = row;
        this.col = col;
        this.value = value;
        this.isStartingCell = (value != 0); // If a value is provided initially, it's a starting cell
        this.isEditable = !this.isStartingCell;
    }

    public Cell(int row, int col) {
        this(row, col, 0); // Constructor for an empty cell
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        if (isEditable) {
            this.value = value;
        }
        // Optionally, throw an exception or log if trying to set a non-editable cell
    }

    public boolean isStartingCell() {
        return isStartingCell;
    }

    public boolean isEditable() {
        return isEditable;
    }

    public void setEditable(boolean editable) {
        // This might be used if puzzle generation logic needs to change editability after creation
        this.isEditable = editable;
        if (editable) {
            this.isStartingCell = false; // If it becomes editable, it's not a starting cell anymore
        }
    }
}
