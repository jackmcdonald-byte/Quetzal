package engine;

import NNUE.nnue_probe.nnue.NNUEProbeUtils;
import engine.datastructs.ChessBoard;
import engine.datastructs.Move;
import engine.datastructs.TranspositionTable;
import engine.datastructs.MoveTree;

import static engine.Quetzal.NULL_INT;
import static engine.Quetzal.globalBoard;

public class PVSAlgorithm {
    public static final int ITERATIVE = 1;
    public static final int FIXED = 2;
    public static final int SMART = 3;
    public static final int INFINITE = 4;
    public static final int TIME = 5;
    public static MoveTree bestMoves = new MoveTree();
    static ChessBoard board = globalBoard;
    public static TranspositionTable tt = new TranspositionTable(board, 12800000);

    /**
     * Null window search
     *
     * @param beta        beta cutoff
     * @param depth       depth of the search
     * @param plyFromRoot depth from the root
     * @return score of the position
     */
    public static int nullWindowSearch(int beta, int depth, int plyFromRoot) {
        //Alpha == beta - 1; this is either a cut- or all-node

        int score = NULL_INT;

        if (plyFromRoot > 0) {
            if (board.isRepeatPosition(board.getZobristKey())) {
                return 0;
            }
        }

        int t = tt.lookupEvaluation(depth, beta - 1, beta);
        if (t != Integer.MIN_VALUE) {
            score = tt.getCurrentEval();
            return t;
        }

        //TODO add quiescence search
        if (depth == 0) {
            NNUEProbeUtils.Input input = new NNUEProbeUtils.Input();
            NNUEProbeUtils.fillInput(input, board);
            score = Evaluation.evaluateNNUE(input);
            return plyFromRoot % 2 == 1 ? score : -score;
        }

        String moves = Moves.generateMoves(board);

        //Check for checkmate or stalemate
        //TODO add 3 fold repetition and stalemate recognition
        //TODO fix checkmate bug
        if (moves.isEmpty()) {
            if ((board.getWK() & Moves.unsafeForWhite(board)) != 0 && board.getWhiteToMove()) {
                return -(Quetzal.MATE_SCORE - plyFromRoot);
            } else if ((board.getBK() & Moves.unsafeForBlack(board)) != 0 && !board.getWhiteToMove()) {
                return Quetzal.MATE_SCORE - plyFromRoot;
            } else {
                return 0;
            }
        }

        for (int i = 0; i < moves.length(); i += 4) {

            board.makeMove(moves, i, true);

            //TODO add unsafe for white and unsafe for black as attributes to Board class
            if (((board.getWK() & Moves.unsafeForWhite(board)) == 0 && board.getWhiteToMove())
                    || ((board.getBK() & Moves.unsafeForBlack(board)) == 0 && !board.getWhiteToMove())) {
                score = -nullWindowSearch(1 - beta, depth - 1, plyFromRoot + 1);
            }

            board.undoMove(true);

            if (score >= beta) {
                tt.storeEvaluation(depth, TranspositionTable.LOWER, score, moves.substring(i, i + 4));
                return score; //Fail-hard beta-cutoff
            }
        }
        return beta - 1; //Fail-hard, return alpha
    }

    /**
     * Principle variation search
     *
     * @param alpha       alpha cutoff
     * @param beta        beta cutoff
     * @param depth       depth of the search
     * @param plyFromRoot depth from the root
     * @param parent      parent node
     * @return score of the position
     */
    public static int principleVariationSearch(int alpha, int beta, int depth, int plyFromRoot, MoveTree.MoveNode parent) {
        int bestScore;

        if (plyFromRoot > 0) {
            if (board.isRepeatPosition(globalBoard.getZobristKey())) {
                return 0;
            }
        }

        //TODO depth issue
        int t = tt.lookupEvaluation(depth, beta - 1, beta);
        if (t != Integer.MIN_VALUE) {
            bestMoves.addMove(tt.getCurrentMove(), t, parent);
            return t;
        }

        if (depth == 0) {
            NNUEProbeUtils.Input input = new NNUEProbeUtils.Input();
            NNUEProbeUtils.fillInput(input, board);
            bestScore = Evaluation.evaluateNNUE(input);
            return plyFromRoot % 2 == 1 ? bestScore : -bestScore;
        }

        String moves = Moves.generateMoves(board);
        int hashFlag = TranspositionTable.UPPER;

        //Check for checkmate or stalemate
        //TODO add 3 fold repetition recognition
        if (moves.isEmpty()) {
            if ((board.getWK() & Moves.unsafeForWhite(board)) != 0 && board.getWhiteToMove()) {
                return -(Quetzal.MATE_SCORE - plyFromRoot);
            } else if ((board.getBK() & Moves.unsafeForBlack(board)) != 0 && !board.getWhiteToMove()) {
                return Quetzal.MATE_SCORE - plyFromRoot;
            } else {
                return 0;
            }
        }

        Move current = new Move(moves.substring(0, 4), NULL_INT, depth);
        bestMoves.addMove(current, parent);

        board.makeMove(moves, 0, true);

        bestScore = -principleVariationSearch(-beta, -alpha, depth - 1, plyFromRoot + 1, parent.getBestMove());
        current.setScore(bestScore);
        Quetzal.moveCounter++;

        board.undoMove(true);

        if (bestScore > alpha) {
            if (bestScore >= beta) {
                //This is a refutation move
                //It is not a PV move
                //However, it will usually cause a cutoff, so it can
                //be considered the best move if no other move is found
                tt.storeEvaluation(depth, TranspositionTable.LOWER, bestScore, current.getMoveString());
                return bestScore;
            }
            alpha = bestScore;
            hashFlag = TranspositionTable.EXACT;
        }

        for (int i = 4; i < moves.length(); i += 4) {
            int score;
            Quetzal.moveCounter++;
            current = new Move(moves.substring(i, i + 4), NULL_INT, depth);

            board.makeMove(moves, i, true);

            //Lightweight search for move stronger than alpha
            score = -nullWindowSearch(-alpha, depth - 1, plyFromRoot + 1);

            if ((score > alpha) && (score < beta)) {
                //Research with window [alpha;beta] if the move is promising
                score = -principleVariationSearch(-beta, -alpha, depth - 1, plyFromRoot + 1, parent.getBestMove());
                if (score > alpha) {
                    bestMoves.addMove(current, parent);
                    current.setScore(score);
                    alpha = score;
                    hashFlag = TranspositionTable.EXACT;
                }
            }

            board.undoMove(true);

            //Check for cutoff
            if ((score != NULL_INT) && (score > bestScore)) {
                if (!parent.getBestMove().getMove().equals(current)) {
                    bestMoves.addMove(current, parent);
                    current.setScore(score);
                }
                if (score >= beta) {
                    tt.storeEvaluation(depth, TranspositionTable.LOWER, score, current.getMoveString());
                    return score;
                }
                bestScore = score;
            }
            tt.storeEvaluation(depth, hashFlag, bestScore, parent.getBestMove().getMoveString());
        }
        return bestScore;
    }

    /**
     * Root search
     *
     * @param alpha alpha cutoff
     * @param beta  beta cutoff
     * @param depth depth of the search
     * @param flag  flag for the search
     * @return score of the position
     */
    public static int rootSearch(int alpha, int beta, int depth, int flag) {
        int bestScore = NULL_INT;
        tt.clear();

        if (flag == ITERATIVE) {
            for (int i = 1; i <= depth; i++) {
                int bestEval = principleVariationSearch(-10000, 10000, i, 0, bestMoves.getRoot());

//                if (abortSearch) break;

                String best = bestMoves.getBestMove();
                int finalDepth = i;

                System.out.println("info depth " + finalDepth + " score cp " + bestEval + " pv " + UCI.moveToAlgebra(best));

                // Stop the search if mate is found
                if (Math.abs(bestEval) == Quetzal.MATE_SCORE && finalDepth > 2) {
                    break;
                }
            }
        } else if (flag == FIXED) {

            return Integer.max(principleVariationSearch(alpha, beta, depth, 0, bestMoves.getRoot()), bestScore);

        } else if (flag == SMART) {

            bestScore = principleVariationSearch(alpha, beta, 5, 0, bestMoves.getRoot());

            for (int i = 1; i <= 9; i++) {
                smartSearch(alpha, beta, 5, 0, bestMoves.getRoot(), i);
                System.out.println("best move: " + UCI.moveToAlgebra(bestMoves.getBestMove()));
            }

            bestScore = Math.max(bestScore, bestMoves.getBestMoveScore());

        } else {
            System.out.println("Invalid flag");
        }
        return bestScore;
    }

    public static void smartSearch(int alpha, int beta, int depth, int plyFromRoot, MoveTree.MoveNode parent, int MaxPly) {

//        if (plyFromRoot > MaxPly) {
//            return;
//        }
//
//        int counter = 0;
//        for (MoveTree.MoveNode child : bestMoves.getNextMoves(parent)) {
//            if (counter > 3) {
//                break;
//            }
//
//            board.makeMove(child.getMoveString(), 0, true);
//            child.clearChildren();
//            int score = -principleVariationSearch(alpha, beta, 3, plyFromRoot + 1, child);
//            board.undoMove(true);
////            if (board.getWhiteToMove()) { score = -score; }
//            System.out.println("info depth " + plyFromRoot + " score cp " + score + " pv " + UCI.moveToAlgebra(bestMoves.getBestMove()) + " " + UCI.moveToAlgebra(child.getMoveString()));
//            child.setMoveScore(score);
//            counter++;
//        }
//
//        bestMoves.sortMovesByScore(parent);
////        System.out.println("info depth " + plyFromRoot + " score cp " + bestMoves.getBestMoveScore() + " pv " + UCI.moveToAlgebra(bestMoves.getBestMove()));
//
//        counter = 0;
//        for (MoveTree.MoveNode child : bestMoves.getNextMoves(parent)) {
//            board.makeMove(child.getMoveString(), 0, true);
//            smartSearch(alpha, beta, depth, plyFromRoot + 1, child, MaxPly);
//            board.undoMove(true);
//
//            if (counter >= (1 - plyFromRoot) * 3) {
//                break;
//            }
//            counter++;
//        }

        return;
    }
}