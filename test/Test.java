import NNUE.nnue_probe.nnue.NNUEJNIBridge;
import NNUE.nnue_probe.nnue.NNUEProbeUtils;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import engine.*;
import NNUE.nnue_probe.nnue.NNUEJNIBridge;

class ChessEngineTest {

    @Test
    void random64_generatesRandomNumber() {
        long random1 = Zobrist.random64();
        long random2 = Zobrist.random64();
        assertNotEquals(random1, random2);
    }

    @Test
    void zobristFillArray_fillsArray() {
        Zobrist.zobristFillArray();
        BoardGenerator.importFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
        assertNotEquals(0, Zobrist.getZobristHash(Quetzal.WP, Quetzal.WN, Quetzal.WB, Quetzal.WR, Quetzal.WQ, Quetzal.WK, Quetzal.BP, Quetzal.BN, Quetzal.BB, Quetzal.BR, Quetzal.BQ, Quetzal.BK, Quetzal.EP, Quetzal.CWK, Quetzal.CWQ, Quetzal.CBK, Quetzal.CBQ, Quetzal.WhiteToMove));
    }

    @Test
    void nullWindowSearch_returnsScore() {
        BoardGenerator.importFEN("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq -");
        int score = PVSAlgorithm.nullWindowSearch(-1000, Quetzal.WP, Quetzal.WN, Quetzal.WB, Quetzal.WR, Quetzal.WQ, Quetzal.WK, Quetzal.BP, Quetzal.BN, Quetzal.BB, Quetzal.BR, Quetzal.BQ, Quetzal.BK, Quetzal.EP, Quetzal.CWK, Quetzal.CWQ, Quetzal.CBK, Quetzal.CBQ, Quetzal.WhiteToMove, 0);
        assertEquals(Integer.MIN_VALUE, score);
    }

    @Test
    void principleVariationSearch_returnsBestScore() {
        BoardGenerator.importFEN("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq -");
        int bestScore = PVSAlgorithm.principleVariationSearch(-1000, 1000, Quetzal.WP, Quetzal.WN, Quetzal.WB, Quetzal.WR, Quetzal.WQ, Quetzal.WK, Quetzal.BP, Quetzal.BN, Quetzal.BB, Quetzal.BR, Quetzal.BQ, Quetzal.BK, Quetzal.EP, Quetzal.CWK, Quetzal.CWQ, Quetzal.CBK, Quetzal.CBQ, Quetzal.WhiteToMove, 0);
        assertEquals(0, bestScore);
    }

    @Test
    void evaluate_returnsScore() {
        BoardGenerator.importFEN("r3k2r/p2pqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq -");
        UCI.inputPrint();
//        int score = Evaluation.evaluate(Quetzal.WP, Quetzal.WN, Quetzal.WB, Quetzal.WR, Quetzal.WQ, Quetzal.WK, Quetzal.BP, Quetzal.BN, Quetzal.BB, Quetzal.BR, Quetzal.BQ, Quetzal.BK, Quetzal.WhiteToMove);
//        assertEquals(1, score);
    }

    @Test
    void principleVariationSearch_returnsLegalMove() {
        BoardGenerator.importFEN("k6Q/pppp4/3np3/8/8/8/PPPPPPP1/KB6 b - - 0 1");
        int bestScore = PVSAlgorithm.principleVariationSearch(-1000, 1000, Quetzal.WP, Quetzal.WN, Quetzal.WB, Quetzal.WR, Quetzal.WQ, Quetzal.WK, Quetzal.BP, Quetzal.BN, Quetzal.BB, Quetzal.BR, Quetzal.BQ, Quetzal.BK, Quetzal.EP, Quetzal.CWK, Quetzal.CWQ, Quetzal.CBK, Quetzal.CBQ, Quetzal.WhiteToMove, 0);
        assertNotEquals("d6f7", UCI.moveToAlgebra(PVSAlgorithm.bestMove[0]));
    }

    @Test
    void legalMovesGeneratesLegalMoves() {
        BoardGenerator.importFEN("k6Q/pppp4/3np3/8/8/8/PPPPPPP1/KB6 b - - 0 1");
        String moves = Moves.possibleMovesBlack(Quetzal.WP, Quetzal.WN, Quetzal.WB, Quetzal.WR, Quetzal.WQ, Quetzal.WK, Quetzal.BP, Quetzal.BN, Quetzal.BB, Quetzal.BR, Quetzal.BQ, Quetzal.BK, Quetzal.EP, Quetzal.CBK, Quetzal.CBQ);
        String legalMoves = Moves.legalMoves(moves, Quetzal.WP, Quetzal.WN, Quetzal.WB, Quetzal.WR, Quetzal.WQ, Quetzal.WK, Quetzal.BP, Quetzal.BN, Quetzal.BB, Quetzal.BR, Quetzal.BQ, Quetzal.BK, Quetzal.WhiteToMove);
        assertEquals(moves, legalMoves);
    }

    @Test
    void testPVSFromMidGame() {
        BoardGenerator.importFEN("1r1qr1k1/2p2pp1/2nb1n2/1p4N1/3pPB2/3P3P/1P1N1P1K/R2Q2R1 w - - 2 23");
        int bestScore = PVSAlgorithm.principleVariationSearch(-1000, 1000, Quetzal.WP, Quetzal.WN, Quetzal.WB, Quetzal.WR, Quetzal.WQ, Quetzal.WK, Quetzal.BP, Quetzal.BN, Quetzal.BB, Quetzal.BR, Quetzal.BQ, Quetzal.BK, Quetzal.EP, Quetzal.CWK, Quetzal.CWQ, Quetzal.CBK, Quetzal.CBQ, Quetzal.WhiteToMove, 0);
        assertEquals("e5f7", UCI.moveToAlgebra(PVSAlgorithm.bestMove[0]));
    }

    @Test
    void testEval() {
        NNUEJNIBridge.setup();

        NNUEProbeUtils.Input input = new NNUEProbeUtils.Input();
        BoardGenerator.importFEN("r1bqkbr1/ppp1p3/2n5/3p1p2/2PPnB1N/3BP2P/PP4P1/RN1Q1RK1 b q - 0 12");
        UCI.inputPrint();
        NNUEProbeUtils.fillInput(input, Quetzal.WP, Quetzal.WN, Quetzal.WB, Quetzal.WR, Quetzal.WQ, Quetzal.WK, Quetzal.BP, Quetzal.BN, Quetzal.BB, Quetzal.BR, Quetzal.BQ, Quetzal.BK);
        int score = NNUEJNIBridge.eval(input.color, input.pieces, input.squares);
        System.out.println(score);

        BoardGenerator.importFEN("rnbqkbnr/ppp1p1pp/5p2/3p4/3P1B2/4P3/PPP2PPP/RN1QKBNR b KQkq - 0 3");
        UCI.inputPrint();
        NNUEProbeUtils.fillInput(input, Quetzal.WP, Quetzal.WN, Quetzal.WB, Quetzal.WR, Quetzal.WQ, Quetzal.WK, Quetzal.BP, Quetzal.BN, Quetzal.BB, Quetzal.BR, Quetzal.BQ, Quetzal.BK);
        score = NNUEJNIBridge.eval(input.color, input.pieces, input.squares);
        System.out.println(score);

        BoardGenerator.importFEN("7k/8/6Q1/8/8/6R1/8/K7 w - - 0 1");
        UCI.inputPrint();
        NNUEProbeUtils.fillInput(input, Quetzal.WP, Quetzal.WN, Quetzal.WB, Quetzal.WR, Quetzal.WQ, Quetzal.WK, Quetzal.BP, Quetzal.BN, Quetzal.BB, Quetzal.BR, Quetzal.BQ, Quetzal.BK);
        score = NNUEJNIBridge.eval(input.color, input.pieces, input.squares);
        System.out.println(score);
    }

    @Test
    void illegalMoveDebug() {
        NNUEJNIBridge.setup();
        UCI.inputPosition("position startpos moves d2d4 a7a6 c1f4 b7b6 e2e3 c7c6 g1f3 d7d6 f1d3 e7e6 e1g1 f7f6 b1c3 g7g6 d4d5 e6d5 e3e4 d5e4 d3e4 h7h6 d1d2 a6a5 f1d1\n");
        UCI.inputGo();

        UCI.inputPosition("position startpos");
        UCI.inputGo();

        return;
    }
}