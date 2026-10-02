package karatCodePractice.chessBoard;

/*
We are modeling a Chessboard with:
o Board
o Cell (square)
o Piece
o Position

Position ->  represents (row, column)
Cell -> represents one square on board
Piece -> represents a chess piece
ChessBoard -> holds the board and logic (METHODS TO IMPLEMENT)

You must implement TWO methods inside the ChessBoard class.

1) public boolean isBlocking Piece(Position queenPosition, Position targetPosition)
This method should:
Return true if targetPosition contains the nearest blocking piece in the direction of the Queen's movement.
Return false otherwise.
Return false if:
    Positions are invalid
    Target is not aligned horizontally, vertically, or diagonally with the Queen
Important: Only the closest blocking piece in a direction is considered. Any piece beyond the nearest blocker must be ignored

2) countQueenAttackPositions(Position queenPosition):
Return the total number of cells the Queen can attack
Count attackable cells in all 8 directions
Stop counting in a direction when:
- The board boundary is reached, or
- A cell containing a piece is encountered -> Include the blocking cell itself as attackable
Return 0 if:
- The position is invalid
- No attackable cells exist
You may assume The Queen exists at queen Position.
- The piece type does not need to be verified. The method must not modify the board

3) int minNumberOfMovesForStalemate(List<Position> rookPositions, Position kingPosition)
- Given an input of positions of n number of rooks, find the minimum number of squared the king has to move to make a stalemate.
*/

import java.util.ArrayList;
import java.util.List;

class Position {
    private int row;
    private int column;

    public Position(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

}

class Piece {
    private String name;
    private String color;

    public Piece(String name, String color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}

class Cell {
    private Position position;
    private Piece piece;

    public Cell(Position position, Piece piece) {
        this.position = position;
        this.piece = piece;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    public boolean isEmpty() {
        return piece == null;
    }
}

class ChessBoard {
    private final Cell[][] board;

    public ChessBoard() {
        board = new Cell[8][8];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                board[i][j] = new Cell(new Position(i, j), null);
            }
        }
    }

    // place piece on board
    public void placePiece(Position pos, Piece piece) {
        board[pos.getRow()][pos.getColumn()].setPiece(piece);
    }

    // check valid position
    private boolean isValid(Position pos) {
        return pos != null
                && pos.getRow() >= 0
                && pos.getRow() < 8
                && pos.getColumn() >= 0
                && pos.getColumn() < 8;

    }

    public boolean isBlockingPiece(Position queenPosition, Position targetPosition) {
        if (!isValid(queenPosition) || !isValid(targetPosition)) {
            return false;
        }
        int qRow = queenPosition.getRow();
        int qCol = queenPosition.getColumn();
        int tRow = targetPosition.getRow();
        int tCol = targetPosition.getColumn();
        int rowDirection = Integer.compare(tRow, qRow);
        int colDirection = Integer.compare(tCol, qCol);

        // check alignment
        boolean sameRow = qRow == tRow;
        boolean sameCol = qCol == tCol;
        boolean diagonal = Math.abs(qRow - tRow) == Math.abs(qCol - tCol);

        if (!(sameRow || sameCol || diagonal)) {
            return false;
        }

        int currentRow = qRow + rowDirection;
        int currentCol = qCol + colDirection;

        while (currentRow >= 0 && currentRow < 8 && currentCol > 0 && currentCol < 8) {
            Cell currentCell = board[currentRow][currentCol];
            if (!currentCell.isEmpty()) {
                return currentRow == tRow && currentCol == tCol;
            }
            currentRow += rowDirection;
            currentCol += colDirection;
        }
        return false;
    }

    // METHOD 2
    public int countQueenAttackPositions(Position queenPosition) {
        if (!isValid(queenPosition)) {
            return 0;
        }

        int total = 0;
        int[][] directions = {
                {-1, 0},
                {1, 0},
                {0, -1},
                {0, 1},
                {-1, -1},
                {-1, 1},
                {1, -1},
                {1, 1}
        };

        for (int[] dir : directions) {
            int row = queenPosition.getRow() + dir[0];
            int col = queenPosition.getColumn() + dir[1];
            while (row >= 0 && row < 8 && col >= 0 && col < 8) {
                total++;
                if (!board[row][col].isEmpty()) {
                    break;
                }
                row += dir[0];
                col += dir[1];
            }
        }
        return total;
    }

    // METHOD 3

    public int minNumberOfMovesForStalemate(List<Position> rookPositions, Position kingPosition) {

        if (!isValid(kingPosition)) {
            return -1;
        }
        // simplified answer
        // minimum moves to nearest corner
        int row = kingPosition.getRow();
        int col = kingPosition.getColumn();
        int topLeft = Math.max(row, col);
        int topRight = Math.max(row, 7 - col);
        int bottomLeft = Math.max(7 - row, col);
        int bottomRight = Math.max(7 - row, 7 - col);

        return Math.min(Math.min(topLeft, topRight), Math.min(bottomLeft, bottomRight));
    }
}

public class ChessBoardSolution {
    public static void main(String[] args) {
        ChessBoard board = new ChessBoard();
        Position queen = new Position (4, 4);
        Position rook = new Position (4, 6);
        Position bishop = new Position(4,7);

        board.placePiece(rook, new Piece ("Rook", "Black"));
        board.placePiece(bishop, new Piece ("Bishop", "Black"));

        boolean result1 = board.isBlockingPiece(queen, rook);
        boolean result2 = board.isBlockingPiece(queen, bishop);

        System.out.println("Rook blocking: " + result1);
        System.out.println("Bishop blocking: " + result2);

        int attacks = board.countQueenAttackPositions(queen);
        System.out.println("Queen attack positions: " + attacks);

        List<Position> rooks = new ArrayList<>();
        rooks.add(new Position (0, 0));
        int minMoves = board.minNumberOfMovesForStalemate(rooks, new Position(3, 3));

        System.out.println("Minimum king moves: " + minMoves);
    }
}
