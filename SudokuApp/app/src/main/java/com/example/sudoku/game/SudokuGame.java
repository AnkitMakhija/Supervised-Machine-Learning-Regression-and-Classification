package com.example.sudoku.game;

public class SudokuGame {
    public static final int GRID_SIZE = 9;
    private Cell[][] board;

    private int selectedRow = -1; // To keep track of the selected cell
    private int selectedCol = -1;

    public SudokuGame() {
        board = new Cell[GRID_SIZE][GRID_SIZE];
        initializeEmptyBoard();
    }

    public void initializeEmptyBoard() {
        for (int r = 0; r < GRID_SIZE; r++) {
            for (int c = 0; c < GRID_SIZE; c++) {
                board[r][c] = new Cell(r, c, 0); // Initialize with empty, editable cells
            }
        }
        selectedRow = -1;
        selectedCol = -1;
    }

    public Cell getCell(int row, int col) {
        if (row >= 0 && row < GRID_SIZE && col >= 0 && col < GRID_SIZE) {
            return board[row][col];
        }
        return null; // Or throw an exception
    }

    // Sets the value of a cell, if it's editable
    public boolean setCellValue(int row, int col, int value) {
        if (row >= 0 && row < GRID_SIZE && col >= 0 && col < GRID_SIZE) {
            Cell cell = board[row][col];
            if (cell.isEditable()) {
                cell.setValue(value);
                return true;
            }
        }
        return false;
    }

    // Overload to directly take a Cell object (e.g. from UI selection)
    public boolean setCellValue(Cell cell, int value) {
        if (cell != null && cell.isEditable()) {
            cell.setValue(value);
            return true;
        }
        return false;
    }
    
    // Method to load a puzzle from a 2D integer array
    public void loadPuzzle(int[][] puzzle) {
        if (puzzle == null || puzzle.length != GRID_SIZE || puzzle[0].length != GRID_SIZE) {
            // Optionally throw an IllegalArgumentException or handle error
            initializeEmptyBoard(); // Fallback to empty board
            return;
        }
        for (int r = 0; r < GRID_SIZE; r++) {
            for (int c = 0; c < GRID_SIZE; c++) {
                board[r][c] = new Cell(r, c, puzzle[r][c]);
            }
        }
        selectedRow = -1;
        selectedCol = -1;
    }

    public int getSelectedRow() {
        return selectedRow;
    }

    public void setSelectedRow(int selectedRow) {
        this.selectedRow = selectedRow;
    }

    public int getSelectedCol() {
        return selectedCol;
    }

    public void setSelectedCol(int selectedCol) {
        this.selectedCol = selectedCol;
    }

    public void selectCell(int row, int col) {
        if (row >= 0 && row < GRID_SIZE) {
            selectedRow = row;
        } else {
            selectedRow = -1;
        }
        if (col >= 0 && col < GRID_SIZE) {
            selectedCol = col;
        } else {
            selectedCol = -1;
        }
    }
    
    public Cell getSelectedCell() {
        if (selectedRow != -1 && selectedCol != -1) {
            return getCell(selectedRow, selectedCol);
        }
        return null;
    }

    // Placeholder for a simple puzzle for testing
    public static int[][] getSamplePuzzle() {
        return new int[][]{
            {5, 3, 0, 0, 7, 0, 0, 0, 0},
            {6, 0, 0, 1, 9, 5, 0, 0, 0},
            {0, 9, 8, 0, 0, 0, 0, 6, 0},
            {8, 0, 0, 0, 6, 0, 0, 0, 3},
            {4, 0, 0, 8, 0, 3, 0, 0, 1},
            {7, 0, 0, 0, 2, 0, 0, 0, 6},
            {0, 6, 0, 0, 0, 0, 2, 8, 0},
            {0, 0, 0, 4, 1, 9, 0, 0, 5},
            {0, 0, 0, 0, 8, 0, 0, 7, 9}
        };
    }

    // --- Add these methods to the existing SudokuGame.java class ---

    public boolean isValidMove(int row, int col, int value) {
        if (value == 0) return true; // Clearing a cell is always valid if it's editable
        // Check if the number is already in the row, column, or 3x3 subgrid,
        // excluding the cell itself if it's the one being checked.
        for (int i = 0; i < GRID_SIZE; i++) {
            // Check row
            if (i != col && board[row][i].getValue() == value) return false;
            // Check column
            if (i != row && board[i][col].getValue() == value) return false;
        }

        // Check 3x3 subgrid
        int subgridStartRow = row - row % 3;
        int subgridStartCol = col - col % 3;
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                int currentRow = subgridStartRow + r;
                int currentCol = subgridStartCol + c;
                if (currentRow == row && currentCol == col) continue; // Skip the cell itself
                if (board[currentRow][currentCol].getValue() == value) return false;
            }
        }
        return true;
    }

    // More specific checks if needed, though isValidMove covers these.
    // These can be useful for providing specific feedback to the user.

    private boolean isNumberInRow(int row, int colToExclude, int number) {
        for (int c = 0; c < GRID_SIZE; c++) {
            if (c == colToExclude) continue;
            if (board[row][c].getValue() == number) {
                return true;
            }
        }
        return false;
    }

    private boolean isNumberInCol(int col, int rowToExclude, int number) {
        for (int r = 0; r < GRID_SIZE; r++) {
            if (r == rowToExclude) continue;
            if (board[r][col].getValue() == number) {
                return true;
            }
        }
        return false;
    }

    private boolean isNumberInSubgrid(int startRow, int startCol, int rowToExclude, int colToExclude, int number) {
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                int currentRow = startRow + r;
                int currentCol = startCol + c;
                if (currentRow == rowToExclude && currentCol == colToExclude) continue;
                if (board[currentRow][currentCol].getValue() == number) {
                    return true;
                }
            }
        }
        return false;
    }
    
    public boolean isBoardSolved() {
        for (int r = 0; r < GRID_SIZE; r++) {
            for (int c = 0; c < GRID_SIZE; c++) {
                int value = board[r][c].getValue();
                if (value == 0) {
                    return false; // Found an empty cell, so not solved
                }
                // Check if this number is valid in its row, column, and subgrid
                // considering other numbers but not itself initially
                for (int i = 0; i < GRID_SIZE; i++) {
                    // Check row (excluding itself)
                    if (i != c && board[r][i].getValue() == value) return false;
                    // Check col (excluding itself)
                    if (i != r && board[i][c].getValue() == value) return false;
                }
                // Check 3x3 subgrid (excluding itself)
                int subgridStartRow = r - r % 3;
                int subgridStartCol = c - c % 3;
                for (int sr = 0; sr < 3; sr++) {
                    for (int sc = 0; sc < 3; sc++) {
                        int currentRow = subgridStartRow + sr;
                        int currentCol = subgridStartCol + sc;
                        if (currentRow == r && currentCol == c) continue;
                        if (board[currentRow][currentCol].getValue() == value) return false;
                    }
                }
            }
        }
        return true; // All cells are filled and valid according to Sudoku rules
    }

    public void clearEditableCells() { // Renamed for clarity
        for (int r = 0; r < GRID_SIZE; r++) {
            for (int c = 0; c < GRID_SIZE; c++) {
                if (board[r][c].isEditable()) {
                    board[r][c].setValue(0);
                }
            }
        }
        selectedRow = -1;
        selectedCol = -1;
    }

    // --- End of methods to add ---
}
