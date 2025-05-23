package com.example.sudokuapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent; // Import MotionEvent
import android.view.View;

import androidx.annotation.Nullable;

import com.example.sudoku.game.Cell; // Import from our game logic package
import com.example.sudoku.game.SudokuGame; // Import from our game logic package

public class SudokuBoardView extends View {

    private SudokuGame game;
    private Paint majorGridPaint;
    private Paint minorGridPaint;
    private Paint numberTextPaint;
    private Paint startingNumberTextPaint; // For pre-filled numbers
    private Paint selectedCellPaint;
    private Paint highlightedCellPaint; // For row/col/subgrid of selected cell

    private int cellSize;
    private final Rect textBounds = new Rect(); // For centering text

    // (Interface can be outside or a static inner interface)
    public interface OnCellSelectedListener {
        void onCellSelected(int row, int col);
    }

    private OnCellSelectedListener cellSelectedListener;

    public void setOnCellSelectedListener(OnCellSelectedListener listener) {
        this.cellSelectedListener = listener;
    }

    public SudokuBoardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initPaints();
    }

    private void initPaints() {
        majorGridPaint = new Paint();
        majorGridPaint.setColor(Color.BLACK);
        majorGridPaint.setStyle(Paint.Style.STROKE);
        majorGridPaint.setStrokeWidth(4f); // Thicker lines for major grid lines

        minorGridPaint = new Paint();
        minorGridPaint.setColor(Color.GRAY);
        minorGridPaint.setStyle(Paint.Style.STROKE);
        minorGridPaint.setStrokeWidth(1f); // Thinner lines for minor grid lines

        numberTextPaint = new Paint();
        numberTextPaint.setColor(Color.BLUE); // User entered numbers
        numberTextPaint.setTextAlign(Paint.Align.CENTER);
        // Text size will be set in onSizeChanged or onMeasure

        startingNumberTextPaint = new Paint();
        startingNumberTextPaint.setColor(Color.BLACK); // Pre-filled numbers
        startingNumberTextPaint.setTextAlign(Paint.Align.CENTER);
        startingNumberTextPaint.setFakeBoldText(true);
        // Text size will be set in onSizeChanged or onMeasure

        selectedCellPaint = new Paint();
        selectedCellPaint.setColor(getContext().getColor(R.color.selected_cell_background)); // From colors.xml
        selectedCellPaint.setStyle(Paint.Style.FILL);

        highlightedCellPaint = new Paint();
        highlightedCellPaint.setColor(getContext().getColor(R.color.highlighted_cell_background)); // From colors.xml
        highlightedCellPaint.setStyle(Paint.Style.FILL);
    }

    public void setGame(SudokuGame game) {
        this.game = game;
        invalidate(); // Redraw the view with the new game state
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int size = Math.min(width, height);
        setMeasuredDimension(size, size); // Make the view square

        // Calculate cell size and text size here, after view dimensions are known
        cellSize = size / SudokuGame.GRID_SIZE;
        float textSize = cellSize * 0.6f; // Example: 60% of cell size
        numberTextPaint.setTextSize(textSize);
        startingNumberTextPaint.setTextSize(textSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (canvas == null) return;

        // Draw cell highlights (selected and related)
        drawCellHighlights(canvas);

        // Draw grid lines
        drawGridLines(canvas);

        // Draw numbers
        drawNumbers(canvas);
    }

    private void drawCellHighlights(Canvas canvas) {
        if (game == null || game.getSelectedRow() == -1 || game.getSelectedCol() == -1) {
            return;
        }

        int selRow = game.getSelectedRow();
        int selCol = game.getSelectedCol();

        // Highlight the selected cell itself
        canvas.drawRect(selCol * cellSize, selRow * cellSize,
                       (selCol + 1) * cellSize, (selRow + 1) * cellSize,
                       selectedCellPaint);

        // Highlight row, column, and subgrid
        for (int i = 0; i < SudokuGame.GRID_SIZE; i++) {
            // Row & Col (excluding selected cell itself)
            if (i != selCol) canvas.drawRect(i * cellSize, selRow * cellSize, (i + 1) * cellSize, (selRow + 1) * cellSize, highlightedCellPaint);
            if (i != selRow) canvas.drawRect(selCol * cellSize, i * cellSize, (selCol + 1) * cellSize, (i + 1) * cellSize, highlightedCellPaint);
        }
        // Subgrid (excluding selected cell itself and already highlighted row/col cells)
        int subgridStartRow = selRow - selRow % 3;
        int subgridStartCol = selCol - selCol % 3;
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                int curR = subgridStartRow + r;
                int curC = subgridStartCol + c;
                if (curR != selRow && curC != selCol) { // Avoid double-highlighting direct row/col
                     canvas.drawRect(curC * cellSize, curR * cellSize,
                                   (curC + 1) * cellSize, (curR + 1) * cellSize,
                                   highlightedCellPaint);
                }
            }
        }
    }


    private void drawGridLines(Canvas canvas) {
        int width = getWidth(); // or getHeight(), since it's square

        // Draw minor lines
        for (int i = 0; i <= SudokuGame.GRID_SIZE; i++) {
            float position = i * cellSize;
            Paint currentPaint = (i % 3 == 0) ? majorGridPaint : minorGridPaint;
            canvas.drawLine(0, position, width, position, currentPaint); // Horizontal
            canvas.drawLine(position, 0, position, width, currentPaint); // Vertical
        }
    }

    private void drawNumbers(Canvas canvas) {
        if (game == null) return;

        for (int r = 0; r < SudokuGame.GRID_SIZE; r++) {
            for (int c = 0; c < SudokuGame.GRID_SIZE; c++) {
                Cell cell = game.getCell(r, c);
                if (cell != null && cell.getValue() != 0) {
                    String text = String.valueOf(cell.getValue());
                    Paint currentTextPaint = cell.isStartingCell() ? startingNumberTextPaint : numberTextPaint;

                    // Center text in cell
                    currentTextPaint.getTextBounds(text, 0, text.length(), textBounds);
                    float x = c * cellSize + (cellSize / 2f);
                    float y = r * cellSize + (cellSize / 2f) - textBounds.exactCenterY();
                    canvas.drawText(text, x, y, currentTextPaint);
                }
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (game == null) {
            return super.onTouchEvent(event);
        }

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            int x = (int) event.getX();
            int y = (int) event.getY();

            if (cellSize > 0) { // Ensure cellSize is initialized
                int col = x / cellSize;
                int row = y / cellSize;

                if (col < SudokuGame.GRID_SIZE && row < SudokuGame.GRID_SIZE) {
                    game.selectCell(row, col);
                    if (cellSelectedListener != null) {
                        cellSelectedListener.onCellSelected(row, col);
                    }
                    invalidate(); // Request a redraw to show selection
                    return true; // Event handled
                }
            }
        }
        return super.onTouchEvent(event);
    }

    // --- End of additions ---
}
