package engine;

import NNUE.nnue_probe.nnue.NNUEProbeUtils;

public class PVSAlgorithm {
    public static String[] bestMove = new String[20];

    public static int nullWindowSearch(int beta, long WP, long WN, long WB, long WR, long WQ, long WK, long BP,
                                       long BN, long BB, long BR, long BQ, long BK, long EP, boolean CWK,
                                       boolean CWQ, boolean CBK, boolean CBQ, boolean WhiteToMove, int depth) {
        //Alpha == beta - 1; this is either a cut- or all-node

        int score = Quetzal.NULL_INT;
        NNUEProbeUtils.Input input = new NNUEProbeUtils.Input();
        NNUEProbeUtils.fillInput(input, WP, WN, WB, WR, WQ, WK, BP, BN, BB, BR, BQ, BK);

        if (depth == Quetzal.searchDepth) {
            score = Evaluation.evaluateNNUE(input);
            return score;
        }

        String moves = Moves.generateMoves(WP, WN, WB, WR, WQ, WK, BP, BN, BB, BR, BQ, BK,
                EP, CWK, CWQ, CBK, CBQ, WhiteToMove);

        //Check for checkmate or stalemate
        //TODO add 3 fold repetition and stalemate recognition
        if (moves.isEmpty()) {
            return WhiteToMove ? Quetzal.MATE_SCORE : -Quetzal.MATE_SCORE;
        }

        for (int i = 0; i < moves.length(); i += 4) {
            char move1 = moves.charAt(i);
            char move2 = moves.charAt(i + 1);
            char move3 = moves.charAt(i + 2);
            char move4 = moves.charAt(i + 3);

            long WPt = Moves.makeMove(WP, move1, move2, move3, move4, 'P'),
                    WNt = Moves.makeMove(WN, move1, move2, move3, move4, 'N'),
                    WBt = Moves.makeMove(WB, move1, move2, move3, move4, 'B'),
                    WRt = Moves.makeMove(WR, move1, move2, move3, move4, 'R'),
                    WQt = Moves.makeMove(WQ, move1, move2, move3, move4, 'Q'),
                    WKt = Moves.makeMove(WK, move1, move2, move3, move4, 'K'),
                    BPt = Moves.makeMove(BP, move1, move2, move3, move4, 'p'),
                    BNt = Moves.makeMove(BN, move1, move2, move3, move4, 'n'),
                    BBt = Moves.makeMove(BB, move1, move2, move3, move4, 'b'),
                    BRt = Moves.makeMove(BR, move1, move2, move3, move4, 'r'),
                    BQt = Moves.makeMove(BQ, move1, move2, move3, move4, 'q'),
                    BKt = Moves.makeMove(BK, move1, move2, move3, move4, 'k'),
                    EPt = Moves.makeMoveEP(WP | BP, String.valueOf(new char[]{move1, move2, move3, move4}));
            WRt = Moves.makeMoveCastle(WRt, WK | BK,
                    String.valueOf(new char[]{move1, move2, move3, move4}), 'R');
            BRt = Moves.makeMoveCastle(BRt, WK | BK,
                    String.valueOf(new char[]{move1, move2, move3, move4}), 'r');
            boolean CWKt = CWK, CWQt = CWQ, CBKt = CBK, CBQt = CBQ;

            if (Character.isDigit(moves.charAt(i + 3))) { //'Regular' move
                int start = (Character.getNumericValue(moves.charAt(i)) * 8)
                        + (Character.getNumericValue(moves.charAt(i + 1)));
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

            //May be unnecessary
            //TODO test later
            if (((WKt & Moves.unsafeForWhite(WPt, WNt, WBt, WRt, WQt, WKt, BPt, BNt, BBt, BRt, BQt, BKt)) == 0
                    && WhiteToMove) ||
                    ((BKt & Moves.unsafeForBlack(WPt, WNt, WBt, WRt, WQt, WKt, BPt, BNt, BBt, BRt, BQt, BKt)) == 0
                            && !WhiteToMove)) {
                score = -nullWindowSearch(1 - beta, WPt, WNt, WBt, WRt, WQt, WKt, BPt, BNt, BBt, BRt,
                        BQt, BKt, EPt, CWKt, CWQt, CBKt, CBQt, !WhiteToMove, depth + 1);
            }
            if (score >= beta) {
                return score; //Fail-hard beta-cutoff
            }
        }
        return beta - 1; //Fail-hard, return alpha
    }

    public static int principleVariationSearch(int alpha, int beta, long WP, long WN, long WB, long WR, long WQ,
                                               long WK, long BP, long BN, long BB, long BR, long BQ, long BK,
                                               long EP, boolean CWK, boolean CWQ, boolean CBK, boolean CBQ,
                                               boolean WhiteToMove, int depth) {
        int bestScore;

        NNUEProbeUtils.Input input = new NNUEProbeUtils.Input();
        NNUEProbeUtils.fillInput(input, WP, WN, WB, WR, WQ, WK, BP, BN, BB, BR, BQ, BK);
        if (depth == Quetzal.searchDepth) {
            bestScore = Evaluation.evaluateNNUE(input);
            return bestScore;
        }

        String moves = Moves.generateMoves(WP, WN, WB, WR, WQ, WK, BP, BN, BB, BR, BQ, BK,
                EP, CWK, CWQ, CBK, CBQ, WhiteToMove);

        //Check for checkmate or stalemate
        //TODO add 3 fold repetition and stalemate recognition
        if (moves.isEmpty()) {
            bestMove[depth] = "checkmate";
            return WhiteToMove ? Quetzal.MATE_SCORE : -Quetzal.MATE_SCORE;
        }

        bestMove[depth] = moves.substring(0, 4);

        char move1 = moves.charAt(0);
        char move2 = moves.charAt(1);
        char move3 = moves.charAt(2);
        char move4 = moves.charAt(3);

        long WPt = Moves.makeMove(WP, move1, move2, move3, move4, 'P'),
                WNt = Moves.makeMove(WN, move1, move2, move3, move4, 'N'),
                WBt = Moves.makeMove(WB, move1, move2, move3, move4, 'B'),
                WRt = Moves.makeMove(WR, move1, move2, move3, move4, 'R'),
                WQt = Moves.makeMove(WQ, move1, move2, move3, move4, 'Q'),
                WKt = Moves.makeMove(WK, move1, move2, move3, move4, 'K'),
                BPt = Moves.makeMove(BP, move1, move2, move3, move4, 'p'),
                BNt = Moves.makeMove(BN, move1, move2, move3, move4, 'n'),
                BBt = Moves.makeMove(BB, move1, move2, move3, move4, 'b'),
                BRt = Moves.makeMove(BR, move1, move2, move3, move4, 'r'),
                BQt = Moves.makeMove(BQ, move1, move2, move3, move4, 'q'),
                BKt = Moves.makeMove(BK, move1, move2, move3, move4, 'k'),
                EPt = Moves.makeMoveEP(WP | BP, String.valueOf(new char[]{move1, move2, move3, move4}));
        WRt = Moves.makeMoveCastle(WRt, WK | BK,
                String.valueOf(new char[]{move1, move2, move3, move4}), 'R');
        BRt = Moves.makeMoveCastle(BRt, WK | BK,
                String.valueOf(new char[]{move1, move2, move3, move4}), 'r');
        boolean CWKt = CWK, CWQt = CWQ, CBKt = CBK, CBQt = CBQ;

        if (Character.isDigit(moves.charAt(3))) { //'Regular' move
            int start = (Character.getNumericValue(moves.charAt(0)) * 8) + (Character.getNumericValue(moves.charAt(1)));
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

        bestScore = -principleVariationSearch(-beta, -alpha, WPt, WNt, WBt, WRt, WQt, WKt, BPt, BNt, BBt, BRt, BQt,
                BKt, EPt, CWKt, CWQt, CBKt, CBQt, !WhiteToMove, depth + 1);
        Quetzal.moveCounter++;

        if (Math.abs(bestScore) > Quetzal.MATE_SCORE) {
            return bestScore;
        }
        if (bestScore > alpha) {
            if (bestScore >= beta) {
                //This is a refutation move
                //It is not a PV move
                //However, it will usually cause a cutoff, so it can
                //be considered the best move if no other move is found
                return bestScore;
            }
            alpha = bestScore;
        }
        for (int i = 4; i < moves.length(); i += 4) {
            int score;
            Quetzal.moveCounter++;

            //Legal, non-castle move
            move1 = moves.charAt(i);
            move2 = moves.charAt(i + 1);
            move3 = moves.charAt(i + 2);
            move4 = moves.charAt(i + 3);

            WPt = Moves.makeMove(WP, move1, move2, move3, move4, 'P');
            WNt = Moves.makeMove(WN, move1, move2, move3, move4, 'N');
            WBt = Moves.makeMove(WB, move1, move2, move3, move4, 'B');
            WRt = Moves.makeMove(WR, move1, move2, move3, move4, 'R');
            WQt = Moves.makeMove(WQ, move1, move2, move3, move4, 'Q');
            WKt = Moves.makeMove(WK, move1, move2, move3, move4, 'K');
            BPt = Moves.makeMove(BP, move1, move2, move3, move4, 'p');
            BNt = Moves.makeMove(BN, move1, move2, move3, move4, 'n');
            BBt = Moves.makeMove(BB, move1, move2, move3, move4, 'b');
            BRt = Moves.makeMove(BR, move1, move2, move3, move4, 'r');
            BQt = Moves.makeMove(BQ, move1, move2, move3, move4, 'q');
            BKt = Moves.makeMove(BK, move1, move2, move3, move4, 'k');
            EPt = Moves.makeMoveEP(WP | BP, String.valueOf(new char[]{move1, move2, move3, move4}));
            WRt = Moves.makeMoveCastle(WRt, WK | BK,
                    String.valueOf(new char[]{move1, move2, move3, move4}), 'R');
            BRt = Moves.makeMoveCastle(BRt, WK | BK,
                    String.valueOf(new char[]{move1, move2, move3, move4}), 'r');

            if (Character.isDigit(moves.charAt(i + 3))) { //'Regular' move
                int start = (Character.getNumericValue(moves.charAt(i)) * 8)
                        + (Character.getNumericValue(moves.charAt(i + 1)));
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

            //Lightweight search for move stronger than alpha
            score = -nullWindowSearch(-alpha, WPt, WNt, WBt, WRt, WQt, WKt, BPt, BNt, BBt, BRt, BQt, BKt, EPt, CWKt,
                    CWQt, CBKt, CBQt, !WhiteToMove, depth + 1);
            if ((score > alpha) && (score < beta)) {
                //Research with window [alpha;beta] if the move is promising
                score = -principleVariationSearch(-beta, -alpha, WPt, WNt, WBt, WRt, WQt, WKt, BPt, BNt, BBt, BRt, BQt,
                        BKt, EPt, CWKt, CWQt, CBKt, CBQt, !WhiteToMove, depth + 1);
                if (score > alpha) {
                    bestMove[depth] = moves.substring(i, i + 4);
                    alpha = score;
                }
            }

            //Check for cutoff
            if ((score != Quetzal.NULL_INT) && (score > bestScore)) {
                bestMove[depth] = moves.substring(i, i + 4);
                if (score >= beta) {
                    return score;
                }
                bestScore = score;
                //TODO tweak mate recognition to work with NNUE
                if (Math.abs(bestScore) == Quetzal.MATE_SCORE) {
                    return bestScore;
                }
            }
        }
        return bestScore;
    }
}
