package engine;

public class Perft {
    static int perftTotalMoveCounter = 0;
    static int perftMoveCounter = 0;
    static int perftMaxDepth = 5;

    public static String moveToAlgebra(String move) {
        StringBuilder moveString = new StringBuilder();

        if (Character.isDigit(move.charAt(3))) {//'regular' move
            moveString.append((char) (move.charAt(1) + 49));
            moveString.append(('8' - move.charAt(0)));
            moveString.append((char) (move.charAt(3) + 49));
            moveString.append(('8' - move.charAt(2)));
        } else if (move.charAt(3) == 'E') {//en passant
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
        } else if (move.charAt(3) == 'P') {//promotion
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

    public static void perftRoot(long WP, long WN, long WB, long WR, long WQ, long WK, long BP, long BN, long BB, long BR, long BQ, long BK, long EP, boolean CWK, boolean CWQ, boolean CBK, boolean CBQ, boolean WhiteToMove, int depth) {
        String moves;
        if (WhiteToMove) {
            moves = Moves.possibleMovesWhite(WP, WN, WB, WR, WQ, WK, BP, BN, BB, BR, BQ, BK, EP, CWK, CWQ, CBK, CBQ);
        } else {
            moves = Moves.possibleMovesBlack(WP, WN, WB, WR, WQ, WK, BP, BN, BB, BR, BQ, BK, EP, CWK, CWQ, CBK, CBQ);
        }
        int length = moves.length();
        for (int i = 0; i < length; i += 4) {
            char move1 = moves.charAt(i);
            char move2 = moves.charAt(i + 1);
            char move3 = moves.charAt(i + 2);
            char move4 = moves.charAt(i + 3);

            long WPt = Moves.makeMove(WP, move1, move2, move3, move4, 'P'), WNt = Moves.makeMove(WN, move1, move2, move3, move4, 'N'),
                    WBt = Moves.makeMove(WB, move1, move2, move3, move4, 'B'), WRt = Moves.makeMove(WR, move1, move2, move3, move4, 'R'),
                    WQt = Moves.makeMove(WQ, move1, move2, move3, move4, 'Q'), WKt = Moves.makeMove(WK, move1, move2, move3, move4, 'K'),
                    BPt = Moves.makeMove(BP, move1, move2, move3, move4, 'p'), BNt = Moves.makeMove(BN, move1, move2, move3, move4, 'n'),
                    BBt = Moves.makeMove(BB, move1, move2, move3, move4, 'b'), BRt = Moves.makeMove(BR, move1, move2, move3, move4, 'r'),
                    BQt = Moves.makeMove(BQ, move1, move2, move3, move4, 'q'), BKt = Moves.makeMove(BK, move1, move2, move3, move4, 'k'),
                    EPt = Moves.makeMoveEP(WP | BP, String.valueOf(new char[]{move1, move2, move3, move4}));
            WRt=Moves.makeMoveCastle(WRt, WK|BK, String.valueOf(new char[]{move1, move2, move3, move4}), 'R');
            BRt=Moves.makeMoveCastle(BRt, WK|BK, String.valueOf(new char[]{move1, move2, move3, move4}), 'r');
            boolean CWKt = CWK, CWQt = CWQ, CBKt = CBK, CBQt = CBQ;
            if (Character.isDigit(moves.charAt(i + 3))) {//'regular' move
                int start = (Character.getNumericValue(moves.charAt(i)) * 8) + (Character.getNumericValue(moves.charAt(i + 1)));

                if (((1L << start) & WK) != 0) {
                    CWKt = false;
                    CWQt = false;
                } else if (((1L << start) & BK) != 0) {
                    CBKt = false;
                    CBQt = false;
                } else if (((1L << start) & WR & (1L << 63)) != 0) {
                    CWKt = false;
                } else if (((1L << start) & WR & (1L << 56)) != 0) {
                    CWQt = false;
                } else if (((1L << start) & BR & (1L << 7)) != 0) {
                    CBKt = false;
                } else if (((1L << start) & BR & 1L) != 0) {
                    CBQt = false;
                }
            }
            if (((WKt & Moves.unsafeForWhite(WPt, WNt, WBt, WRt, WQt, WKt, BPt, BNt, BBt, BRt, BQt, BKt)) == 0 && WhiteToMove) ||
                    ((BKt & Moves.unsafeForBlack(WPt, WNt, WBt, WRt, WQt, WKt, BPt, BNt, BBt, BRt, BQt, BKt)) == 0 && !WhiteToMove)) {
                perft(WPt, WNt, WBt, WRt, WQt, WKt, BPt, BNt, BBt, BRt, BQt, BKt, EPt, CWKt, CWQt, CBKt, CBQt, !WhiteToMove, depth + 1);
                System.out.println(moveToAlgebra(moves.substring(i, i + 4)) + " " + perftMoveCounter);
                perftTotalMoveCounter += perftMoveCounter;
                perftMoveCounter = 0;
            }
        }
    }

    public static void perft(long WP, long WN, long WB, long WR, long WQ, long WK, long BP, long BN, long BB, long BR, long BQ, long BK, long EP, boolean CWK, boolean CWQ, boolean CBK, boolean CBQ, boolean WhiteToMove, int depth) {
        if (depth < perftMaxDepth) {
            String moves;
            if (WhiteToMove) {
                moves = Moves.possibleMovesWhite(WP, WN, WB, WR, WQ, WK, BP, BN, BB, BR, BQ, BK, EP, CWK, CWQ, CBK, CBQ);
            } else {
                moves = Moves.possibleMovesBlack(WP, WN, WB, WR, WQ, WK, BP, BN, BB, BR, BQ, BK, EP, CWK, CWQ, CBK, CBQ);
            }
            int length = moves.length();
            for (int i = 0; i < length; i += 4) {
                char move1 = moves.charAt(i);
                char move2 = moves.charAt(i + 1);
                char move3 = moves.charAt(i + 2);
                char move4 = moves.charAt(i + 3);

                long WPt = Moves.makeMove(WP, move1, move2, move3, move4, 'P'), WNt = Moves.makeMove(WN, move1, move2, move3, move4, 'N'),
                        WBt = Moves.makeMove(WB, move1, move2, move3, move4, 'B'), WRt = Moves.makeMove(WR, move1, move2, move3, move4, 'R'),
                        WQt = Moves.makeMove(WQ, move1, move2, move3, move4, 'Q'), WKt = Moves.makeMove(WK, move1, move2, move3, move4, 'K'),
                        BPt = Moves.makeMove(BP, move1, move2, move3, move4, 'p'), BNt = Moves.makeMove(BN, move1, move2, move3, move4, 'n'),
                        BBt = Moves.makeMove(BB, move1, move2, move3, move4, 'b'), BRt = Moves.makeMove(BR, move1, move2, move3, move4, 'r'),
                        BQt = Moves.makeMove(BQ, move1, move2, move3, move4, 'q'), BKt = Moves.makeMove(BK, move1, move2, move3, move4, 'k'),
                        EPt = Moves.makeMoveEP(WP | BP, String.valueOf(new char[]{move1, move2, move3, move4}));
                WRt=Moves.makeMoveCastle(WRt, WK|BK, String.valueOf(new char[]{move1, move2, move3, move4}), 'R');
                BRt=Moves.makeMoveCastle(BRt, WK|BK, String.valueOf(new char[]{move1, move2, move3, move4}), 'r');
                boolean CWKt = CWK, CWQt = CWQ, CBKt = CBK, CBQt = CBQ;
                if (Character.isDigit(moves.charAt(3))) {//'regular' move
                    int start = (Character.getNumericValue(moves.charAt(i)) * 8) + (Character.getNumericValue(moves.charAt(i + 1)));
                    if (((1L << start) & WK) != 0) {
                        CWKt = false;
                        CWQt = false;
                    }
                    if (((1L << start) & BK) != 0) {
                        CBKt = false;
                        CBQt = false;
                    }
                    if (((1L << start) & WR & (1L << 63)) != 0) {
                        CWKt = false;
                    }
                    if (((1L << start) & WR & (1L << 56)) != 0) {
                        CWQt = false;
                    }
                    if (((1L << start) & BR & (1L << 7)) != 0) {
                        CBKt = false;
                    }
                    if (((1L << start) & BR & 1L) != 0) {
                        CBQt = false;
                    }
                }
                if (((WKt & Moves.unsafeForWhite(WPt, WNt, WBt, WRt, WQt, WKt, BPt, BNt, BBt, BRt, BQt, BKt)) == 0 && WhiteToMove) ||
                        ((BKt & Moves.unsafeForBlack(WPt, WNt, WBt, WRt, WQt, WKt, BPt, BNt, BBt, BRt, BQt, BKt)) == 0 && !WhiteToMove)) {
                    if (depth + 1 == perftMaxDepth) {
                        perftMoveCounter++;
                    }
                    perft(WPt, WNt, WBt, WRt, WQt, WKt, BPt, BNt, BBt, BRt, BQt, BKt, EPt, CWKt, CWQt, CBKt, CBQt, !WhiteToMove, depth + 1);
                }
            }
        }
    }
}