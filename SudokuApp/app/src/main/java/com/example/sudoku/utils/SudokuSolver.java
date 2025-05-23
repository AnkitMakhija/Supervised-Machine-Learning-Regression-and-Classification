package com.example.sudoku.utils;

import com.example.sudoku.game.Cell;
import com.example.sudoku.game.SudokuGame;

public class SudokuSolver {

    private static final int GRID_SIZE = SudokuGame.GRID_SIZE;

    public boolean solve(SudokuGame game) {
        if (game == null || game.getBoard() == null) {
            return false;
        }
        // This method should operate on the Cell[][] array directly from the game.
        // It is crucial that SudokuSolver modifies the actual Cell objects within the SudokuGame's board,
        // so that the game state reflects the solution.
        // The SudokuGame.getBoard() method should return the reference to its internal Cell[][] board.
        return solveSudoku(game.getBoard());
    }

    private boolean solveSudoku(Cell[][] board) {
        int[] emptyLoc = findEmptyLocation(board);
        if (emptyLoc == null) {
            return true; // Board is already solved (no empty cells)
        }

        int row = emptyLoc[0];
        int col = emptyLoc[1];

        Cell currentCell = board[row][col];

        // Important: Solver should only try to fill cells that are originally empty or user-filled.
        // It should not overwrite pre-filled (isStartingCell) numbers.
        // However, the current findEmptyLocation only looks for cells with value 0.
        // If a user incorrectly fills a cell and then asks for a solve, the solver might
        // get stuck or produce an incorrect result if it doesn't respect original puzzle cells.
        // For now, we assume findEmptyLocation correctly identifies cells that can be part of solving.
        // The provided Cell.setValue() method already checks 'isEditable'. If a cell is a starting cell,
        // setValue will not change it. This is good.
        // So, if findEmptyLocation returns a starting cell (because its value somehow became 0, which shouldn't happen),
        // setValue would prevent modification.
        // The current logic is: find cell with value 0. If it's a starting cell (immutable), setValue does nothing.
        // This would lead to solveSudoku returning false for that path, which is correct.

        for (int num = 1; num <= GRID_SIZE; num++) {
            if (isSafe(board, row, col, num)) {
                // Before setting value, check if the cell is actually editable.
                // While findEmptyLocation implies it's empty (value 0), and Cell constructor
                // would make value 0 cells editable, this check adds robustness.
                // However, the current Cell.setValue() already handles this.
                // currentCell.setValue(num); // Try placing the number

                // We directly call setValue on the cell object.
                // The Cell's own logic will determine if it can be set (i.e. if it's editable).
                // If it's a starting cell, setValue will do nothing, and the condition below will fail.
                // This is the correct behavior.
                
                // Check if the cell is editable (not a starting cell) before attempting to set a value
                // findEmptyLocation should only return cells with value 0, which are by default editable
                // unless they were starting cells that somehow got reset to 0 (which is unlikely).
                // The Cell.setValue() method itself checks for editability.
                
                // currentCell.setValue(num);
                // if (currentCell.getValue() != num) { // If setValue failed (e.g. not editable)
                //    continue; // Try next number or backtrack if this was the only way
                // }
                // The above check is redundant if setValue correctly handles non-editable cells by not changing them.

                // Let's assume currentCell is indeed empty and thus editable.
                currentCell.setValue(num);


                if (solveSudoku(board)) { // Recurse
                    return true; // Solution found
                }

                // If solution not found with this number, backtrack.
                // Reset the cell only if it was this instance of solveSudoku that set it.
                // And only if it's editable.
                currentCell.setValue(0); // Backtrack
            }
        }
        return false; // No number from 1-9 works for this cell, trigger backtrack
    }

    private int[] findEmptyLocation(Cell[][] board) {
        for (int r = 0; r < GRID_SIZE; r++) {
            for (int c = 0; c < GRID_SIZE; c++) {
                // Only consider cells that are empty (value 0).
                // The solver should fill these empty cells.
                // By design, a cell with value 0 is considered editable by the Cell class.
                if (board[r][c].getValue() == 0) {
                    return new int[]{r, c};
                }
            }
        }
        return null; // No empty cell found
    }

    // isSafe needs to check the values from the Cell objects in the board
    private boolean isSafe(Cell[][] board, int row, int col, int num) {
        // Check row
        for (int c = 0; c < GRID_SIZE; c++) {
            if (board[row][c].getValue() == num) {
                return false;
            }
        }

        // Check column
        for (int r = 0; r < GRID_SIZE; r++) {
            if (board[r][col].getValue() == num) {
                return false;
            }
        }

        // Check 3x3 subgrid
        int subgridStartRow = row - row % 3;
        int subgridStartCol = col - col % 3;
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                if (board[subgridStartRow + r][subgridStartCol + c].getValue() == num) {
                    return false;
                }
            }
        }
        return true; // Number is safe to place
    }
}
