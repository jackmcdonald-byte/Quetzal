package engine;

import engine.datastructs.ChessBoard;

public class Perft {
    static int perftTotalMoveCounter = 0;
    static int perftMoveCounter = 0;
    static int perftMaxDepth = 5; //Change this to the desired depth

    /**
     * Convert a move from internal notation to algebraic notation
     *
     * @param move Move in internal notation
     * @return Move in algebraic notation
     */
    public static String moveToAlgebra(String move) {
        StringBuilder moveString = new StringBuilder();

        if (Character.isDigit(move.charAt(3))) { //'Regular' move
            moveString.append((char) (move.charAt(1) + 49));
            moveString.append(('8' - move.charAt(0)));
            moveString.append((char) (move.charAt(3) + 49));
            moveString.append(('8' - move.charAt(2)));
        } else if (move.charAt(3) == 'E') { //En passant
            if (move.charAt(2) == 'W') {
                moveString.append((char) (move.charAt(0) + 49));
                moveString.append("5");
                moveString.append((char) (move.charAt(1) + 49));
                moveString.append("6");
            } else {
                moveString.append((char) (move.charAt(0) + 49));
                moveString.append("4");
                moveString.append((char) (move.charAt(1) + 49));
                moveString.append("3");
            }
        } else if (move.charAt(3) == 'P') { //Promotion
            if (Character.isUpperCase(move.charAt(2))) {
                moveString.append((char) (move.charAt(0) + 49));
                moveString.append("7");
                moveString.append((char) (move.charAt(1) + 49));
                moveString.append("8");
                moveString.append(move.charAt(2));
            } else {
                moveString.append((char) (move.charAt(0) + 49));
                moveString.append("2");
                moveString.append((char) (move.charAt(1) + 49));
                moveString.append("1");
                moveString.append(Character.toUpperCase(move.charAt(2)));
            }
        }

        return moveString.toString();
    }

    /**
     * Run the perft test from the root position, dividing the search, and printing the move and move count
     *
     * @param board ChessBoard class containing all important values to the chess game (e.g. check, castling rights, piece position)
     * @param depth Depth of the search
     */
    public static void perftRoot(ChessBoard board, int depth) {
        String moves;

        if (board.getWhiteToMove()) {
            moves = Moves.possibleMovesWhite(board);
        } else {
            moves = Moves.possibleMovesBlack(board);
        }

        int length = moves.length();
        ChessBoard tempBoard = new ChessBoard();

        //Loop over moves
        for (int i = 0; i < length; i += 4) {
            tempBoard.cloneBoard(board);
            tempBoard.makeMove(moves, i, true);

            //Verify move legality
            if (((tempBoard.getWK() & Moves.unsafeForWhite(tempBoard)) == 0 && board.getWhiteToMove())
                    || ((tempBoard.getBK() & Moves.unsafeForBlack(tempBoard)) == 0 && !board.getWhiteToMove())) {
                //Continue to next depth
                perft(tempBoard, depth + 1);
                //Print move and move count
                System.out.println(moveToAlgebra(moves.substring(i, i + 4)) + " " + perftMoveCounter);
                perftTotalMoveCounter += perftMoveCounter;
                perftMoveCounter = 0;
            }
        }
    }

    /**
     * Run the perft test from a given position
     *
     * @param board ChessBoard class containing all important values to the chess game (e.g. check, castling rights, piece position)
     * @param depth Depth of the search
     */
    public static void perft(ChessBoard board, int depth) {
        if (depth < perftMaxDepth) {
            String moves;

            if (board.getWhiteToMove()) {
                moves = Moves.possibleMovesWhite(board);
            } else {
                moves = Moves.possibleMovesBlack(board);
            }

            int length = moves.length();
            ChessBoard tempBoard = new ChessBoard();

            for (int i = 0; i < length; i += 4) {
                tempBoard.cloneBoard(board);
                tempBoard.makeMove(moves, i, true);

                //Verify move legality
                if (((tempBoard.getWK() & Moves.unsafeForWhite(tempBoard)) == 0
                        && board.getWhiteToMove())
                        || ((tempBoard.getBK() & Moves.unsafeForBlack(tempBoard))
                        == 0 && !board.getWhiteToMove())) {
                    if (depth + 1 == perftMaxDepth) { //Stop recursion
                        perftMoveCounter++;
                    }
                    //Continue to next depth
                    perft(tempBoard, depth + 1);
                }
            }
        }
    }
}