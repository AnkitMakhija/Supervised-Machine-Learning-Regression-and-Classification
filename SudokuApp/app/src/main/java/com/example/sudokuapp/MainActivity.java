package com.example.sudokuapp;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast; // For showing messages
// No need to import View.OnClickListener if using lambdas or anonymous inner classes for listeners
// However, if we were to implement OnClickListener on MainActivity, it would be needed.
// The provided solution uses lambdas, so it's not strictly necessary here.
// Import game logic classes using their fully qualified names or direct imports
import com.example.sudoku.game.SudokuGame;
import com.example.sudoku.game.SudokuGenerator;

public class MainActivity extends AppCompatActivity {

    private SudokuBoardView boardView;
    private SudokuGame game;
    private SudokuGenerator generator;

    // Number buttons
    private Button button1, button2, button3, button4, button5;
    private Button button6, button7, button8, button9, buttonErase;
    private Button buttonNewGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boardView = findViewById(R.id.sudokuBoardView);
        buttonNewGame = findViewById(R.id.buttonNewGame);
        
        // Initialize number pad buttons
        initializeNumberButtons(); // Finds views for number buttons
        setupNumberButtonListeners(); // Sets listeners for number buttons

        game = new SudokuGame();
        generator = new SudokuGenerator();

        startNewGame(); // Start a new game on creation

        buttonNewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewGame();
            }
        });

        // Optional: Listener for cell selection from SudokuBoardView
        // boardView.setOnCellSelectedListener(new SudokuBoardView.OnCellSelectedListener() {
        //     @Override
        //     public void onCellSelected(int row, int col) {
        //         // Handle cell selection if needed (e.g., update UI to show selected number)
        //         // For now, SudokuBoardView handles visual selection, game state is updated.
        //     }
        // });
        // Call to setupNumberButtonListeners() already added above as per instruction
    }

    private void startNewGame() {
        // Difficulty: number of cells to remove. Higher is harder.
        // Max is GRID_SIZE*GRID_SIZE. Sensible might be 20-50.
        int numbersToRemove = 30; // Example difficulty
        
        // The generator modifies the 'game' object's board directly.
        // So, we first need to ensure the game object has a fresh board structure if needed.
        game.initializeEmptyBoard(); // Ensure cells are fresh for the generator
        generator.generateNewPuzzle(game, numbersToRemove);
        
        // After generation, the game object's board is filled with the puzzle.
        // SudokuBoardView needs this game object to draw.
        boardView.setGame(game);
        boardView.invalidate(); // Ensure redraw
    }
    
    private void initializeNumberButtons() {
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        button4 = findViewById(R.id.button4);
        button5 = findViewById(R.id.button5);
        button6 = findViewById(R.id.button6);
        button7 = findViewById(R.id.button7);
        button8 = findViewById(R.id.button8);
        button9 = findViewById(R.id.button9);
        buttonErase = findViewById(R.id.buttonErase);
        // Listeners for these will be set up in Part 2 of MainActivity implementation
    }

    private void setupNumberButtonListeners() {
        View.OnClickListener numberClickListener = v -> {
            if (game == null || game.getSelectedCell() == null) return;
            // Ensure the cell is editable (not a starting cell)
            if (!game.getSelectedCell().isEditable()) {
                 Toast.makeText(this, "This cell is not editable.", Toast.LENGTH_SHORT).show();
                return;
            }

            Button clickedButton = (Button) v;
            // Ensure tag is not null before parsing. XML tags are set to "1" through "9".
            if (clickedButton.getTag() == null) return; 
            int number = Integer.parseInt(clickedButton.getTag().toString());

            // Optional: Add validation using game.isValidMove(row, col, number) before setting
            // if (!game.isValidMove(game.getSelectedRow(), game.getSelectedCol(), number)) {
            //     Toast.makeText(this, "Invalid move.", Toast.LENGTH_SHORT).show();
            //     return;
            // }

            game.setCellValue(game.getSelectedCell(), number);
            boardView.invalidate(); // Redraw board

            if (game.isBoardSolved()) {
                Toast.makeText(this, getString(R.string.game_solved_message), Toast.LENGTH_LONG).show();
            }
        };

        button1.setOnClickListener(numberClickListener);
        button2.setOnClickListener(numberClickListener);
        button3.setOnClickListener(numberClickListener);
        button4.setOnClickListener(numberClickListener);
        button5.setOnClickListener(numberClickListener);
        button6.setOnClickListener(numberClickListener);
        button7.setOnClickListener(numberClickListener);
        button8.setOnClickListener(numberClickListener);
        button9.setOnClickListener(numberClickListener);

        buttonErase.setOnClickListener(v -> {
            if (game == null || game.getSelectedCell() == null) return;
            if (!game.getSelectedCell().isEditable()) {
                Toast.makeText(this, "This cell is not editable.", Toast.LENGTH_SHORT).show();
                return;
            }
            game.setCellValue(game.getSelectedCell(), 0); // 0 means erase
            boardView.invalidate(); // Redraw board
        });
    }
}
