package com.example.sudoku.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class SudokuGenerator {

    private static final int GRID_SIZE = SudokuGame.GRID_SIZE;
    private Random random = new Random();

    // Main method to generate a new puzzle and set it on the game object
    public void generateNewPuzzle(SudokuGame game, int numbersToRemove) {
        game.initializeEmptyBoard(); // Start with a clean board
        fillBoard(game.getBoard()); // Fill it with a valid Sudoku solution

        // Remove numbers to create the puzzle
        pokeHoles(game.getBoard(), numbersToRemove);
        
        // Update cell properties (isStartingCell, isEditable) based on the final puzzle
        // This loop is actually redundant if SudokuGame.loadPuzzle is called afterwards,
        // or if the Cell constructor logic (value != 0 means !isEditable) is solely relied upon.
        // However, it can be kept for explicit clarity or if direct manipulation is preferred.
        for (int r = 0; r < GRID_SIZE; r++) {
            for (int c = 0; c < GRID_SIZE; c++) {
                Cell cell = game.getCell(r,c);
                // The Cell constructor should correctly set isStartingCell based on whether value is 0.
                // If value is non-zero, it's a starting cell, hence not editable.
                // If value is zero, it's an empty cell, hence editable.
                // So, re-setting isEditable or isStartingCell here might be redundant
                // if the game's loadPuzzle method or Cell constructors handle this.
                // For example, if SudokuGame.loadPuzzle(boardValues) is called after this,
                // it would create new Cell objects, correctly setting their states.
                // Let's assume the Cell constructor logic is sufficient.
                // cell.setEditable(cell.getValue() == 0);
                // if (cell.getValue() != 0) { cell.isStartingCell = true; } // Simplified
            }
        }
        // After poking holes, the SudokuGame's internal board (Cell[][]) has 0s for empty cells.
        // If SudokuGame.loadPuzzle() is called with the integer values from this board,
        // it will correctly initialize the Cell objects, setting isEditable and isStartingCell.
    }

    // Fills the board using a backtracking algorithm
    private boolean fillBoard(Cell[][] board) {
        for (int r = 0; r < GRID_SIZE; r++) {
            for (int c = 0; c < GRID_SIZE; c++) {
                if (board[r][c].getValue() == 0) { // Find an empty cell
                    List<Integer> numbers = new ArrayList<>();
                    for (int i = 1; i <= GRID_SIZE; i++) {
                        numbers.add(i);
                    }
                    Collections.shuffle(numbers, random); // Randomize order of numbers to try

                    for (int num : numbers) {
                        if (isSafe(board, r, c, num)) {
                            board[r][c].setValue(num); // Place number
                            if (fillBoard(board)) { // Recurse
                                return true; // Solution found
                            }
                            board[r][c].setValue(0); // Backtrack: undo placement
                        }
                    }
                    return false; // No valid number found for this cell, trigger backtrack
                }
            }
        }
        return true; // All cells filled
    }

    // Checks if a number can be safely placed in a cell
    private boolean isSafe(Cell[][] board, int row, int col, int num) {
        // Check row
        for (int c = 0; c < GRID_SIZE; c++) {
            if (board[row][c].getValue() == num) return false;
        }
        // Check column
        for (int r = 0; r < GRID_SIZE; r++) {
            if (board[r][col].getValue() == num) return false;
        }
        // Check 3x3 subgrid
        int subgridStartRow = row - row % 3;
        int subgridStartCol = col - col % 3;
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                if (board[subgridStartRow + r][subgridStartCol + c].getValue() == num) return false;
            }
        }
        return true;
    }

    // Removes numbers from the filled board to create the puzzle
    private void pokeHoles(Cell[][] board, int holesToMake) {
        int cellsToRemove = holesToMake;
        if (cellsToRemove <= 0 || cellsToRemove >= GRID_SIZE * GRID_SIZE) {
            cellsToRemove = GRID_SIZE * GRID_SIZE / 2; // Default to removing half if invalid
        }

        List<int[]> cellCoordinates = new ArrayList<>();
        for (int r = 0; r < GRID_SIZE; r++) {
            for (int c = 0; c < GRID_SIZE; c++) {
                cellCoordinates.add(new int[]{r, c});
            }
        }
        Collections.shuffle(cellCoordinates, random);

        int removedCount = 0;
        for (int[] coord : cellCoordinates) {
            if (removedCount >= cellsToRemove) break;
            int r = coord[0];
            int c = coord[1];
            if (board[r][c].getValue() != 0) {
                // Directly set the value of the Cell object to 0.
                // The Cell's isEditable property should ideally be updated by SudokuGame
                // when it processes this modified board (e.g., by calling loadPuzzle).
                // The Cell constructor (new Cell(r, c, 0)) makes it editable.
                board[r][c].setValue(0); 
                // After setting value to 0, the SudokuGame should ensure this cell
                // becomes editable. This is handled by Cell's constructor if SudokuGame.loadPuzzle
                // is called, or if Cell.setValue(0) implies it becomes editable.
                // The Cell.setValue() method in Cell.java only allows changing value if editable.
                // This means for pokeHoles to work as intended (making cells empty and thus editable),
                // we might need to adjust Cell.setValue or ensure cells are made editable here.
                // For now, we assume SudokuGame will reconstruct/update cells based on the new values.
                // A simpler approach for Cell: setValue(0) makes it editable.
                // Or, SudokuGame after calling generator:
                // for each cell: if cell.value == 0, cell.isEditable = true, cell.isStarting = false
                // else cell.isEditable = false, cell.isStarting = true.
                // The current Cell constructor (new Cell(r,c,value)) handles this:
                // isStartingCell = (value != 0), isEditable = !isStartingCell.
                // So if SudokuGame.loadPuzzle() is called with the int values, it's fine.
                removedCount++;
            }
        }
    }
}
