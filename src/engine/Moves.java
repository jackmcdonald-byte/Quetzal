package engine;

import engine.datastructs.ChessBoard;

import java.util.Arrays;

import static engine.utils.BitBoardConstants.*;

public class Moves {
    static long WHITE_PIECES; //Doesn't represent white king
    static long BLACK_PIECES; //Doesn't represent black king
    static long AVAILABLE_SQUARES_WHITE; //Represents where white can move
    static long AVAILABLE_SQUARES_BLACK; //Represents where black can move
    static long OCCUPIED;
    static long EMPTY;

    /**
     * Generates all legal moves for the current board
     *
     * @param board Current board
     * @return String of all legal moves
     */
    public static String generateMoves(ChessBoard board) {
        String moves;
        if (board.getWhiteToMove()) {
            moves = possibleMovesWhite(board);
        } else {
            moves = possibleMovesBlack(board);
        }

        return legalMoves(moves, board);
    }

    //General formula for moves along a mask = (o&m - 2s) ^ ((o&m)' - 2s')'
    //where o is OCCUPIED, m is mask, s is slider, and ' is the reverse operation

    /**
     * Calculates all possible horizontal and vertical moves for a given slider
     *
     * @param slider Slider piece
     * @return Bitboard of all possible moves
     */
    public static long horizontalAndVerticalMoves(int slider) {
        long binarySlider = 1L << slider;

        long possibilitiesHorizontal = (OCCUPIED - (binarySlider << 1)
                ^ Long.reverse(Long.reverse(OCCUPIED) - (Long.reverse(binarySlider) << 1)));

        long possibilitiesVertical = ((OCCUPIED & FileMasks8[slider % 8]) - (binarySlider << 1))
                ^ Long.reverse(Long.reverse(OCCUPIED & FileMasks8[slider % 8])
                - (Long.reverse(binarySlider) << 1));

        return (possibilitiesHorizontal & RankMasks8[slider / 8])
                | (possibilitiesVertical & FileMasks8[slider % 8]);
    }

    /**
     * Calculates all possible diagonal and anti-diagonal moves for a given slider
     *
     * @param slider Slider piece
     * @return Bitboard of all possible moves
     */
    public static long diagonalAndAntiDiagonalMoves(int slider) {
        long binarySlider = 1L << slider;

        long possibilitiesDiagonal =
                ((OCCUPIED & DiagonalMasks8[(slider / 8) + (slider % 8)]) - (binarySlider << 1))
                        ^ Long.reverse(Long.reverse(OCCUPIED & DiagonalMasks8[(slider / 8)
                        + (slider % 8)]) - (Long.reverse(binarySlider) << 1));

        long possibilitiesAntiDiagonal =
                ((OCCUPIED & AntiDiagonalMasks8[(slider / 8) + 7 - (slider % 8)]) - (binarySlider << 1))
                        ^ Long.reverse(Long.reverse(OCCUPIED & AntiDiagonalMasks8[(slider / 8) + 7 - (slider % 8)])
                        - (Long.reverse(binarySlider) << 1));

        return (possibilitiesDiagonal & DiagonalMasks8[(slider / 8) + (slider % 8)])
                | (possibilitiesAntiDiagonal & AntiDiagonalMasks8[(slider / 8) + 7 - (slider % 8)]);
    }

    /**
     * Returns new board after making a move
     *
     * @param board Current board
     * @param move1 Start x coordinate
     * @param move2 End x coordinate
     * @param move3 Start y coordinate
     * @param move4 End y coordinate
     * @param type  Type of move
     * @return New board
     */
    public static long makeMove(long board, char move1, char move2, char move3, char move4, char type) {
        if (Character.isDigit(move4)) { //'Regular' move
            int start = ((move1 - '0') * 8) + (move2 - '0');
            int end = ((move3 - '0') * 8) + (move4 - '0');

            if (((board >>> start) & 1) == 1) { //Move piece on its bitboard
                board &= ~(1L << start);
                board |= (1L << end);
            } else {
                board &= ~(1L << end); //Remove captured piece if it exists
            }
        } else if (move4 == 'P') { //Pawn promotion
            int start, end;

            if (Character.isUpperCase(move3)) { //White promotion
                start = Long.numberOfTrailingZeros(FileMasks8[move1 - '0'] & RankMasks8[1]);
                end = Long.numberOfTrailingZeros(FileMasks8[move2 - '0'] & RankMasks8[0]);
            } else { //Black promotion
                start = Long.numberOfTrailingZeros(FileMasks8[move1 - '0'] & RankMasks8[6]);
                end = Long.numberOfTrailingZeros(FileMasks8[move2 - '0'] & RankMasks8[7]);
            }
            if (type == move3) { //Add new piece if promotion matches type and remove pawn
                board |= (1L << end);
            } else { //Remove captured piece if it exists
                board &= ~(1L << start);
                board &= ~(1L << end);
            }
        } else if (move4 == 'E') { //En passant
            int start, end;

            if (move3 == 'W') {
                start = Long.numberOfTrailingZeros(FileMasks8[move1 - '0'] & RankMasks8[3]);
                end = Long.numberOfTrailingZeros(FileMasks8[move2 - '0'] & RankMasks8[2]);
                board &= ~(FileMasks8[move2 - '0'] & RankMasks8[3]);
            } else {
                start = Long.numberOfTrailingZeros(FileMasks8[move1 - '0'] & RankMasks8[4]);
                end = Long.numberOfTrailingZeros(FileMasks8[move2 - '0'] & RankMasks8[5]);
                board &= ~(FileMasks8[move2 - '0'] & RankMasks8[4]);
            }
            if (((board >>> start) & 1) == 1) {
                board &= ~(1L << start);
                board |= (1L << end);
            }
        }

        return board;
    }

    /**
     * Returns rook bitboard after castling
     *
     * @param rookBoard Start rook bitboard
     * @param kingBoard Start king bitboard
     * @param move      Move made
     * @param type      Type of move
     * @return New rook bitboard
     */
    public static long makeMoveCastle(long rookBoard, long kingBoard, String move, char type) {
        int start = (Character.getNumericValue(move.charAt(0)) * 8) + (Character.getNumericValue(move.charAt(1)));
        //Check if king castled and updated rook board accordingly (kingBoard is updated in makeMove method)
        if ((((kingBoard >>> start) & 1) == 1) && (("0402".equals(move)) || ("0406".equals(move))
                || ("7472".equals(move)) || ("7476".equals(move)))) {//'regular' move
            if (type == 'R') {
                switch (move) {
                    case "7472":
                        rookBoard &= ~(1L << CASTLE_ROOKS[1]);
                        rookBoard |= (1L << (CASTLE_ROOKS[1] + 3));
                        break;
                    case "7476":
                        rookBoard &= ~(1L << CASTLE_ROOKS[0]);
                        rookBoard |= (1L << (CASTLE_ROOKS[0] - 2));
                        break;
                }
            } else {
                switch (move) {
                    case "0402":
                        rookBoard &= ~(1L << CASTLE_ROOKS[3]);
                        rookBoard |= (1L << (CASTLE_ROOKS[3] + 3));
                        break;
                    case "0406":
                        rookBoard &= ~(1L << CASTLE_ROOKS[2]);
                        rookBoard |= (1L << (CASTLE_ROOKS[2] - 2));
                        break;
                }
            }
        }
        return rookBoard;
    }

    /**
     * Returns EP bitboard after making a move
     *
     * @param board EP bitboard
     * @param move  Move made
     * @return New EP bitboard
     */
    public static long makeMoveEP(long board, String move) {
        //Masks EP file if double pawn push
        if (Character.isDigit(move.charAt(3))) {
            int start = (Character.getNumericValue(move.charAt(0)) * 8) + (Character.getNumericValue(move.charAt(1)));
            if ((Math.abs(move.charAt(0) - move.charAt(2)) == 2) && (((board >>> start) & 1) == 1)) { //Double pawn push
                return FileMasks8[move.charAt(1) - '0'];
            }
        }
        return 0;
    }

    /**
     * Returns string of all possible white moves (not necessarily legal)
     *
     * @param board Current board
     * @return String of all possible white moves
     */
    public static String possibleMovesWhite(ChessBoard board) {
        AVAILABLE_SQUARES_WHITE = ~(board.getWP() | board.getWN() | board.getWB() | board.getWR() | board.getWQ() | board.getWK() | board.getBK()); //Added BK to avoid illegal capture
        BLACK_PIECES = board.getBP() | board.getBN() | board.getBB() | board.getBR() | board.getBQ(); //Omitted BK to avoid illegal capture
        EMPTY = ~(board.getWP() | board.getWN() | board.getWB() | board.getWR() | board.getWQ() | board.getWK() | board.getBP() | board.getBN() | board.getBB() | board.getBR() | board.getBQ() | board.getBK());
        OCCUPIED = ~(EMPTY);

        return possibleWP(board.getWP(), board.getBP(), board.getEP())
                + possibleN(AVAILABLE_SQUARES_WHITE, board.getWN())
                + possibleB(AVAILABLE_SQUARES_WHITE, board.getWB())
                + possibleR(AVAILABLE_SQUARES_WHITE, board.getWR())
                + possibleQ(AVAILABLE_SQUARES_WHITE, board.getWQ())
                + possibleK(AVAILABLE_SQUARES_WHITE, board.getWK())
                + possibleCW(board);
    }

    /**
     * Returns string of all possible black moves (not necessarily legal)
     *
     * @param board Current board
     * @return String of all possible black moves
     */
    public static String possibleMovesBlack(ChessBoard board) {
        AVAILABLE_SQUARES_BLACK = ~(board.getBP() | board.getBN() | board.getBB() | board.getBR() | board.getBQ() | board.getBK() | board.getWK()); //Added WK to avoid illegal capture
        WHITE_PIECES = board.getWP() | board.getWN() | board.getWB() | board.getWR() | board.getWQ(); //Omitted WK to avoid illegal capture
        EMPTY = ~(board.getWP() | board.getWN() | board.getWB() | board.getWR() | board.getWQ() | board.getWK() | board.getBP() | board.getBN() | board.getBB() | board.getBR() | board.getBQ() | board.getBK());
        OCCUPIED = ~(EMPTY);

        return possibleBP(board.getBP(), board.getWP(), board.getEP())
                + possibleN(AVAILABLE_SQUARES_BLACK, board.getBN())
                + possibleB(AVAILABLE_SQUARES_BLACK, board.getBB())
                + possibleR(AVAILABLE_SQUARES_BLACK, board.getBR())
                + possibleQ(AVAILABLE_SQUARES_BLACK, board.getBQ())
                + possibleK(AVAILABLE_SQUARES_BLACK, board.getBK())
                + possibleCB(board);
    }

    /**
     * Returns string of all possible white pawn moves
     *
     * @param WP White pawn bitboard
     * @param BP Black pawn bitboard
     * @param EP EP bitboard
     * @return String of all possible white pawn moves
     */
    public static String possibleWP(long WP, long BP, long EP) {
        StringBuilder list = new StringBuilder();

        //Basic captures and pawn pushes: y1,x1,y2,x2
        long PAWN_MOVES = (WP >> 7) & BLACK_PIECES & ~RANK_8 & ~FILE_A; //Capture right
        long possibility = PAWN_MOVES & -PAWN_MOVES;
        while (possibility != 0) {
            int index = Long.numberOfTrailingZeros(possibility);
            list.append(index / 8 + 1).append(index % 8 - 1).append(index / 8).append(index % 8);
            PAWN_MOVES &= ~possibility;
            possibility = PAWN_MOVES & -PAWN_MOVES;
        }
        PAWN_MOVES = (WP >> 9) & BLACK_PIECES & ~RANK_8 & ~FILE_H; //Capture left
        possibility = PAWN_MOVES & -PAWN_MOVES;
        while (possibility != 0) {
            int index = Long.numberOfTrailingZeros(possibility);
            list.append(index / 8 + 1).append(index % 8 + 1).append(index / 8).append(index % 8);
            PAWN_MOVES &= ~possibility;
            possibility = PAWN_MOVES & -PAWN_MOVES;
        }
        PAWN_MOVES = (WP >> 8) & EMPTY & ~RANK_8; //Single push
        possibility = PAWN_MOVES & -PAWN_MOVES;
        while (possibility != 0) {
            int index = Long.numberOfTrailingZeros(possibility);
            list.append(index / 8 + 1).append(index % 8).append(index / 8).append(index % 8);
            PAWN_MOVES &= ~possibility;
            possibility = PAWN_MOVES & -PAWN_MOVES;
        }
        PAWN_MOVES = (WP >> 16) & EMPTY & (EMPTY >> 8) & RANK_4; //Double push
        possibility = PAWN_MOVES & -PAWN_MOVES;
        while (possibility != 0) {
            int index = Long.numberOfTrailingZeros(possibility);
            list.append(index / 8 + 2).append(index % 8).append(index / 8).append(index % 8);
            PAWN_MOVES &= ~possibility;
            possibility = PAWN_MOVES & -PAWN_MOVES;
        }

        //Promoting captures and pawn pushes: x1,x2,Promotion Type,"P"
        PAWN_MOVES = (WP >> 7) & BLACK_PIECES & RANK_8 & ~FILE_A; //Pawn promotion by capture right
        possibility = PAWN_MOVES & -PAWN_MOVES;
        while (possibility != 0) {
            int index = Long.numberOfTrailingZeros(possibility);
            list.append(index % 8 - 1).append(index % 8).append("QP").append(index % 8 - 1)
                    .append(index % 8).append("RP").append(index % 8 - 1).append(index % 8)
                    .append("BP").append(index % 8 - 1).append(index % 8).append("NP");
            PAWN_MOVES &= ~possibility;
            possibility = PAWN_MOVES & -PAWN_MOVES;
        }
        PAWN_MOVES = (WP >> 9) & BLACK_PIECES & RANK_8 & ~FILE_H; //Pawn promotion by capture left
        possibility = PAWN_MOVES & -PAWN_MOVES;
        while (possibility != 0) {
            int index = Long.numberOfTrailingZeros(possibility);
            list.append(index % 8 + 1).append(index % 8).append("QP").append(index % 8 + 1)
                    .append(index % 8).append("RP").append(index % 8 + 1).append(index % 8)
                    .append("BP").append(index % 8 + 1).append(index % 8).append("NP");
            PAWN_MOVES &= ~possibility;
            possibility = PAWN_MOVES & -PAWN_MOVES;
        }
        PAWN_MOVES = (WP >> 8) & EMPTY & RANK_8; //Pawn promotion by push
        possibility = PAWN_MOVES & -PAWN_MOVES;
        while (possibility != 0) {
            int index = Long.numberOfTrailingZeros(possibility);
            list.append(index % 8).append(index % 8).append("QP").append(index % 8)
                    .append(index % 8).append("RP").append(index % 8).append(index % 8)
                    .append("BP").append(index % 8).append(index % 8).append("NP");
            PAWN_MOVES &= ~possibility;
            possibility = PAWN_MOVES & -PAWN_MOVES;
        }

        //En passant: x1,x2,"WE"
        possibility = (WP << 1) & BP & RANK_5 & ~FILE_A & EP; //En passant right
        if (possibility != 0) {
            int index = Long.numberOfTrailingZeros(possibility);
            list.append(index % 8 - 1).append(index % 8).append("WE");
        }
        possibility = (WP >> 1) & BP & RANK_5 & ~FILE_H & EP; //En passant left
        if (possibility != 0) {
            int index = Long.numberOfTrailingZeros(possibility);
            list.append(index % 8 + 1).append(index % 8).append("WE");
        }

        return list.toString();
    }

    /**
     * Returns string of all possible black pawn moves
     *
     * @param BP Black pawn bitboard
     * @param WP White pawn bitboard
     * @param EP EP bitboard
     * @return String of all possible black pawn moves
     */
    public static String possibleBP(long BP, long WP, long EP) {
        StringBuilder list = new StringBuilder();

        //Basic captures and pawn pushes: y1,x1,y2,x2
        long PAWN_MOVES = (BP << 7) & WHITE_PIECES & ~RANK_1 & ~FILE_H; //Capture right
        long possibility = PAWN_MOVES & -PAWN_MOVES;
        while (possibility != 0) {
            int index = Long.numberOfTrailingZeros(possibility);
            list.append(index / 8 - 1).append(index % 8 + 1).append(index / 8).append(index % 8);
            PAWN_MOVES &= ~possibility;
            possibility = PAWN_MOVES & -PAWN_MOVES;
        }
        PAWN_MOVES = (BP << 9) & WHITE_PIECES & ~RANK_1 & ~FILE_A; //Capture left
        possibility = PAWN_MOVES & -PAWN_MOVES;
        while (possibility != 0) {
            int index = Long.numberOfTrailingZeros(possibility);
            list.append(index / 8 - 1).append(index % 8 - 1).append(index / 8).append(index % 8);
            PAWN_MOVES &= ~possibility;
            possibility = PAWN_MOVES & -PAWN_MOVES;
        }
        PAWN_MOVES = (BP << 8) & EMPTY & ~RANK_1; //Single push
        possibility = PAWN_MOVES & -PAWN_MOVES;
        while (possibility != 0) {
            int index = Long.numberOfTrailingZeros(possibility);
            list.append(index / 8 - 1).append(index % 8).append(index / 8).append(index % 8);
            PAWN_MOVES &= ~possibility;
            possibility = PAWN_MOVES & -PAWN_MOVES;
        }
        PAWN_MOVES = (BP << 16) & EMPTY & (EMPTY << 8) & RANK_5; //Double push
        possibility = PAWN_MOVES & -PAWN_MOVES;
        while (possibility != 0) {
            int index = Long.numberOfTrailingZeros(possibility);
            list.append(index / 8 - 2).append(index % 8).append(index / 8).append(index % 8);
            PAWN_MOVES &= ~possibility;
            possibility = PAWN_MOVES & -PAWN_MOVES;
        }

        //Promoting captures and pawn pushes: x1,x2,Promotion Type,"P"
        PAWN_MOVES = (BP << 7) & WHITE_PIECES & RANK_1 & ~FILE_H; //Pawn promotion by capture right
        possibility = PAWN_MOVES & -PAWN_MOVES;
        while (possibility != 0) {
            int index = Long.numberOfTrailingZeros(possibility);
            list.append(index % 8 + 1).append(index % 8).append("qP").append(index % 8 + 1)
                    .append(index % 8).append("rP").append(index % 8 + 1).append(index % 8)
                    .append("bP").append(index % 8 + 1).append(index % 8).append("nP");
            PAWN_MOVES &= ~possibility;
            possibility = PAWN_MOVES & -PAWN_MOVES;
        }
        PAWN_MOVES = (BP << 9) & WHITE_PIECES & RANK_1 & ~FILE_A; //Pawn promotion by capture left
        possibility = PAWN_MOVES & -PAWN_MOVES;
        while (possibility != 0) {
            int index = Long.numberOfTrailingZeros(possibility);
            list.append(index % 8 - 1).append(index % 8).append("qP").append(index % 8 - 1)
                    .append(index % 8).append("rP").append(index % 8 - 1).append(index % 8)
                    .append("bP").append(index % 8 - 1).append(index % 8).append("nP");
            PAWN_MOVES &= ~possibility;
            possibility = PAWN_MOVES & -PAWN_MOVES;
        }
        PAWN_MOVES = (BP << 8) & EMPTY & RANK_1; //Pawn promotion by push
        possibility = PAWN_MOVES & -PAWN_MOVES;
        while (possibility != 0) {
            int index = Long.numberOfTrailingZeros(possibility);
            list.append(index % 8).append(index % 8).append("qP").append(index % 8)
                    .append(index % 8).append("rP").append(index % 8).append(index % 8)
                    .append("bP").append(index % 8).append(index % 8).append("nP");
            PAWN_MOVES &= ~possibility;
            possibility = PAWN_MOVES & -PAWN_MOVES;
        }

        //En passant: x1,x2,"BE"
        possibility = (BP >> 1) & WP & RANK_4 & ~FILE_H & EP; //En passant right
        if (possibility != 0) {
            int index = Long.numberOfTrailingZeros(possibility);
            list.append(index % 8 + 1).append(index % 8).append("BE");
        }
        possibility = (BP << 1) & WP & RANK_4 & ~FILE_A & EP; //En passant left
        if (possibility != 0) {
            int index = Long.numberOfTrailingZeros(possibility);
            list.append(index % 8 - 1).append(index % 8).append("BE");
        }

        return list.toString();
    }

    /**
     * Returns string of all possible knight moves
     *
     * @param AVAILABLE_SQUARES Available squares for a given color
     * @param N                 Knight bitboard
     * @return String of all possible knight moves
     */
    public static String possibleN(long AVAILABLE_SQUARES, long N) {
        StringBuilder list = new StringBuilder();
        long i = N & -N;
        long possibility;

        //Loop through each knight
        while (i != 0) {
            int iLocation = Long.numberOfTrailingZeros(i);

            //KNIGHT_SPAN is centered at 18th bit, so we must shift it accordingly
            if (iLocation > 18) {
                possibility = KNIGHT_SPAN << (iLocation - 18);
            } else {
                possibility = KNIGHT_SPAN >> (18 - iLocation);
            }

            //Remove moves that wrap around the board and self-captures
            if (iLocation % 8 < 4) {
                possibility &= ~FILE_GH & AVAILABLE_SQUARES;
            } else {
                possibility &= ~FILE_AB & AVAILABLE_SQUARES;
            }

            //Loop through legal moves and captures: y1,x1,y2,x2
            long j = possibility & -possibility;
            while (j != 0) {
                int index = Long.numberOfTrailingZeros(j);
                list.append(iLocation / 8).append(iLocation % 8).append(index / 8).append(index % 8);
                possibility &= ~j;
                j = possibility & -possibility;
            }
            N &= ~i;
            i = N & -N;
        }
        return list.toString();
    }

    /**
     * Returns string of all possible bishop moves
     *
     * @param AVAILABLE_SQUARES Available squares for a given color
     * @param B                 Bishop bitboard
     * @return String of all possible bishop moves
     */
    public static String possibleB(long AVAILABLE_SQUARES, long B) {
        StringBuilder list = new StringBuilder();
        long i = B & -B;
        long possibility;

        //Loop through each bishop
        while (i != 0) {
            int iLocation = Long.numberOfTrailingZeros(i);
            possibility = diagonalAndAntiDiagonalMoves(iLocation) & AVAILABLE_SQUARES;
            long j = possibility & -possibility;

            //Loop through legal moves and captures for given bishop: y1,x1,y2,x2
            while (j != 0) {
                int index = Long.numberOfTrailingZeros(j);
                list.append(iLocation / 8).append(iLocation % 8).append(index / 8).append(index % 8);
                possibility &= ~j;
                j = possibility & -possibility;
            }
            B &= ~i;
            i = B & -B;
        }
        return list.toString();
    }

    /**
     * Returns string of all possible rook moves
     *
     * @param AVAILABLE_SQUARES Available squares for a given color
     * @param R                 Rook bitboard
     * @return String of all possible rook moves
     */
    public static String possibleR(long AVAILABLE_SQUARES, long R) {
        StringBuilder list = new StringBuilder();
        long i = R & -R;
        long possibility;

        //Loop through each rook
        while (i != 0) {
            int iLocation = Long.numberOfTrailingZeros(i);
            possibility = horizontalAndVerticalMoves(iLocation) & AVAILABLE_SQUARES;
            long j = possibility & -possibility;

            //Loop through legal moves and captures for given rook: y1,x1,y2,x2
            while (j != 0) {
                int index = Long.numberOfTrailingZeros(j);
                list.append(iLocation / 8).append(iLocation % 8).append(index / 8).append(index % 8);
                possibility &= ~j;
                j = possibility & -possibility;
            }
            R &= ~i;
            i = R & -R;
        }
        return list.toString();
    }

    /**
     * Returns string of all possible queen moves
     *
     * @param AVAILABLE_SQUARES Available squares for a given color
     * @param Q                 Queen bitboard
     * @return String of all possible queen moves
     */
    public static String possibleQ(long AVAILABLE_SQUARES, long Q) {
        StringBuilder list = new StringBuilder();
        long i = Q & -Q;
        long possibility;

        //Loop through each queen
        while (i != 0) {
            int iLocation = Long.numberOfTrailingZeros(i);
            possibility = (diagonalAndAntiDiagonalMoves(iLocation) | horizontalAndVerticalMoves(iLocation))
                    & AVAILABLE_SQUARES;
            long j = possibility & -possibility;

            //Loop through legal moves and captures for given queen: y1,x1,y2,x2
            while (j != 0) {
                int index = Long.numberOfTrailingZeros(j);
                list.append(iLocation / 8).append(iLocation % 8).append(index / 8).append(index % 8);
                possibility &= ~j;
                j = possibility & -possibility;
            }
            Q &= ~i;
            i = Q & -Q;
        }
        return list.toString();
    }

    /**
     * Returns string of all possible king moves
     *
     * @param AVAILABLE_SQUARES Available squares for a given color
     * @param K                 King bitboard
     * @return String of all possible king moves
     */
    public static String possibleK(long AVAILABLE_SQUARES, long K) {
        StringBuilder list = new StringBuilder();
        long possibility;
        int iLocation = Long.numberOfTrailingZeros(K);

        //KING_SPAN is centered at 9th bit, so we must shift it accordingly
        if (iLocation > 9) {
            possibility = KING_SPAN << (iLocation - 9);
        } else {
            possibility = KING_SPAN >> (9 - iLocation);
        }

        //Remove moves that wrap around the board and self-captures
        if (iLocation % 8 < 4) {
            possibility &= ~FILE_GH & AVAILABLE_SQUARES;
        } else {
            possibility &= ~FILE_AB & AVAILABLE_SQUARES;
        }

        //Loop through legal moves and captures for given king: y1,x1,y2,x2
        long j = possibility & -possibility;
        while (j != 0) {
            int index = Long.numberOfTrailingZeros(j);
            list.append(iLocation / 8).append(iLocation % 8).append(index / 8).append(index % 8);
            possibility &= ~j;
            j = possibility & -possibility;
        }

        return list.toString();
    }

    /**
     * Returns string of all possible white castling moves
     *
     * @param board Current board
     * @return String of all possible castling moves
     */
    public static String possibleCW(ChessBoard board) {
        String list = "";
        long UNSAFE = unsafeForWhite(board);

        //Check if castling path is clear and king is not in check
        if ((UNSAFE & board.getWK()) == 0) {
            if (board.getCWK() && (((1L << CASTLE_ROOKS[0]) & board.getWR()) != 0)) {
                if (((OCCUPIED | UNSAFE) & ((1L << 61) | (1L << 62))) == 0) {
                    list += "7476";
                }
            }
            if (board.getCWQ() && (((1L << CASTLE_ROOKS[1]) & board.getWR()) != 0)) {
                if (((OCCUPIED | (UNSAFE & ~(1L << 57))) & ((1L << 57) | (1L << 58) | (1L << 59))) == 0) {
                    list += "7472";
                }
            }
        }
        return list;
    }

    /**
     * Returns string of all possible black castling moves
     *
     * @param board Current board
     * @return String of all possible castling moves
     */
    public static String possibleCB(ChessBoard board) {
        String list = "";
        long UNSAFE = unsafeForBlack(board);

        //Check if castling path is clear and king is not in check
        if ((UNSAFE & board.getBK()) == 0) {
            if (board.getCBK() && (((1L << CASTLE_ROOKS[2]) & board.getBR()) != 0)) {
                if (((OCCUPIED | UNSAFE) & ((1L << 5) | (1L << 6))) == 0) {
                    list += "0406";
                }
            }
            if (board.getCBQ() && (((1L << CASTLE_ROOKS[3]) & board.getBR()) != 0)) {
                if (((OCCUPIED | (UNSAFE & ~(1L << 1))) & ((1L << 1) | (1L << 2) | (1L << 3))) == 0) {
                    list += "0402";
                }
            }
        }
        return list;
    }

    /**
     * Returns a bitboard of all unsafe squares for a black
     *
     * @param board Current board
     * @return Bitboard of all unsafe squares for black
     */
    public static long unsafeForBlack(ChessBoard board) {
        long WP = board.getWP();
        long WN = board.getWN();
        long WB = board.getWB();
        long WR = board.getWR();
        long WQ = board.getWQ();
        long WK = board.getWK();
        long BP = board.getBP();
        long BN = board.getBN();
        long BB = board.getBB();
        long BR = board.getBR();
        long BQ = board.getBQ();
        long BK = board.getBK();

        long unsafe;
        OCCUPIED = WP | WN | WB | WR | WQ | WK | BP | BN | BB | BR | BQ | BK;
        long possibility;

        //Pawn
        unsafe = ((WP >>> 7) & ~FILE_A); //Pawn capture right
        unsafe |= ((WP >>> 9) & ~FILE_H); //Pawn capture left

        //Knight
        long i = WN & -WN;
        while (i != 0) {
            int iLocation = Long.numberOfTrailingZeros(i);
            if (iLocation > 18) {
                possibility = KNIGHT_SPAN << (iLocation - 18);
            } else {
                possibility = KNIGHT_SPAN >> (18 - iLocation);
            }
            if (iLocation % 8 < 4) {
                possibility &= ~FILE_GH;
            } else {
                possibility &= ~FILE_AB;
            }
            unsafe |= possibility;
            WN &= ~i;
            i = WN & -WN;
        }

        //Bishop/queen
        long QB = WQ | WB;
        i = QB & -QB;
        while (i != 0) {
            int iLocation = Long.numberOfTrailingZeros(i);
            possibility = diagonalAndAntiDiagonalMoves(iLocation);
            unsafe |= possibility;
            QB &= ~i;
            i = QB & -QB;
        }

        //Rook/queen
        long QR = WQ | WR;
        i = QR & -QR;
        while (i != 0) {
            int iLocation = Long.numberOfTrailingZeros(i);
            possibility = horizontalAndVerticalMoves(iLocation);
            unsafe |= possibility;
            QR &= ~i;
            i = QR & -QR;
        }

        //King
        int iLocation = Long.numberOfTrailingZeros(WK);
        if (iLocation > 9) {
            possibility = KING_SPAN << (iLocation - 9);
        } else {
            possibility = KING_SPAN >> (9 - iLocation);
        }
        if (iLocation % 8 < 4) {
            possibility &= ~FILE_GH;
        } else {
            possibility &= ~FILE_AB;
        }
        unsafe |= possibility;

        return unsafe;
    }

    /**
     * Returns a bitboard of all unsafe squares for a white
     *
     * @param board Current board
     * @return Bitboard of all unsafe squares for white
     */
    public static long unsafeForWhite(ChessBoard board) {
        long WP = board.getWP();
        long WN = board.getWN();
        long WB = board.getWB();
        long WR = board.getWR();
        long WQ = board.getWQ();
        long WK = board.getWK();
        long BP = board.getBP();
        long BN = board.getBN();
        long BB = board.getBB();
        long BR = board.getBR();
        long BQ = board.getBQ();
        long BK = board.getBK();

        long unsafe;
        OCCUPIED = WP | WN | WB | WR | WQ | WK | BP | BN | BB | BR | BQ | BK;
        long possibility;

        //Pawn
        unsafe = ((BP << 7) & ~FILE_H); //Pawn capture right
        unsafe |= ((BP << 9) & ~FILE_A); //Pawn capture left

        //Knight
        long i = BN & -BN;
        while (i != 0) {
            int iLocation = Long.numberOfTrailingZeros(i);
            if (iLocation > 18) {
                possibility = KNIGHT_SPAN << (iLocation - 18);
            } else {
                possibility = KNIGHT_SPAN >> (18 - iLocation);
            }
            if (iLocation % 8 < 4) {
                possibility &= ~FILE_GH;
            } else {
                possibility &= ~FILE_AB;
            }
            unsafe |= possibility;
            BN &= ~i;
            i = BN & -BN;
        }

        //Bishop/queen
        long QB = BQ | BB;
        i = QB & -QB;
        while (i != 0) {
            int iLocation = Long.numberOfTrailingZeros(i);
            possibility = diagonalAndAntiDiagonalMoves(iLocation);
            unsafe |= possibility;
            QB &= ~i;
            i = QB & -QB;
        }

        //Rook/queen
        long QR = BQ | BR;
        i = QR & -QR;
        while (i != 0) {
            int iLocation = Long.numberOfTrailingZeros(i);
            possibility = horizontalAndVerticalMoves(iLocation);
            unsafe |= possibility;
            QR &= ~i;
            i = QR & -QR;
        }

        //King
        int iLocation = Long.numberOfTrailingZeros(BK);
        if (iLocation > 9) {
            possibility = KING_SPAN << (iLocation - 9);
        } else {
            possibility = KING_SPAN >> (9 - iLocation);
        }
        if (iLocation % 8 < 4) {
            possibility &= ~FILE_GH;
        } else {
            possibility &= ~FILE_AB;
        }
        unsafe |= possibility;

        return unsafe;
    }

    /**
     * Returns a String of all legal moves for a given board
     *
     * @param moves List of possible moves
     * @param board Current board
     * @return String of all legal moves
     */
    public static String legalMoves(String moves, ChessBoard board) {
        //TODO remove board creation
        StringBuilder legalMoves = new StringBuilder();

        //Primitives instead of strings for performance
        for (int i = 0; i < moves.length(); i += 4) {
            board.makeMove(moves, i, true);
            if (((board.getWK() & Moves.unsafeForWhite(board)) == 0 && !board.getWhiteToMove())
                    || ((board.getBK() & Moves.unsafeForBlack(board)) == 0 && board.getWhiteToMove())) {
                legalMoves.append(moves, i, i + 4);
            }
            board.undoMove(true);
        }
        return legalMoves.toString();
    }

    /**
     * Prints bitboard to console (for debugging)
     *
     * @param bitBoard Bitboard to print
     */
    public static void drawBitboard(long bitBoard) {
        //Debug method to print bitboards
        //TODO remove later

        String[][] chessBoard = new String[8][8];
        for (int i = 0; i < 64; i++) {
            chessBoard[i / 8][i % 8] = "";
        }
        for (int i = 0; i < 64; i++) {
            if (((bitBoard >>> i) & 1) == 1) {
                chessBoard[i / 8][i % 8] = "P";
            }
            if ("".equals(chessBoard[i / 8][i % 8])) {
                chessBoard[i / 8][i % 8] = " ";
            }
        }
        for (int i = 0; i < 8; i++) {
            System.out.println(Arrays.toString(chessBoard[i]));
        }
    }
}
