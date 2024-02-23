import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import engine.*;

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
        int score = Evaluation.evaluate(Quetzal.WP, Quetzal.WN, Quetzal.WB, Quetzal.WR, Quetzal.WQ, Quetzal.WK, Quetzal.BP, Quetzal.BN, Quetzal.BB, Quetzal.BR, Quetzal.BQ, Quetzal.BK, Quetzal.WhiteToMove);
        assertEquals(1, score);
    }

    @Test
    void legalMoves_returnsMoves() {
        BoardGenerator.importFEN("k6Q/pppp4/3np3/8/8/8/PPPPPPP1/KB6 b - - 0 1");
        UCI.inputPrint();
        String moves = Moves.possibleMovesBlack(Quetzal.WP, Quetzal.WN, Quetzal.WB, Quetzal.WR, Quetzal.WQ, Quetzal.WK, Quetzal.BP, Quetzal.BN, Quetzal.BB, Quetzal.BR, Quetzal.BQ, Quetzal.BK, Quetzal.EP, Quetzal.CWK, Quetzal.CWQ, Quetzal.CBK, Quetzal.CBQ);
        int i = PVSAlgorithm.getFirstLegalMove(moves, Quetzal.WP, Quetzal.WN, Quetzal.WB, Quetzal.WR, Quetzal.WQ, Quetzal.WK, Quetzal.BP, Quetzal.BN, Quetzal.BB, Quetzal.BR, Quetzal.BQ, Quetzal.BK, Quetzal.EP, Quetzal.CWK, Quetzal.CWQ, Quetzal.CBK, Quetzal.CBQ, Quetzal.WhiteToMove);
        assertNotEquals(-1, i);
        assertNotEquals("a7a6", UCI.moveToAlgebra(moves.substring(i, i + 4)));
        System.out.println(UCI.moveToAlgebra(moves.substring(i, i + 4)));
        moves = moves.substring(i + 4);
        i = PVSAlgorithm.getFirstLegalMove(moves, Quetzal.WP, Quetzal.WN, Quetzal.WB, Quetzal.WR, Quetzal.WQ, Quetzal.WK, Quetzal.BP, Quetzal.BN, Quetzal.BB, Quetzal.BR, Quetzal.BQ, Quetzal.BK, Quetzal.EP, Quetzal.CWK, Quetzal.CWQ, Quetzal.CBK, Quetzal.CBQ, Quetzal.WhiteToMove);
        assertNotEquals("a7a6", UCI.moveToAlgebra(moves.substring(i, i + 4)));
        System.out.println(UCI.moveToAlgebra(moves.substring(i, i + 4)));
        moves = moves.substring(i + 4);
        i = PVSAlgorithm.getFirstLegalMove(moves, Quetzal.WP, Quetzal.WN, Quetzal.WB, Quetzal.WR, Quetzal.WQ, Quetzal.WK, Quetzal.BP, Quetzal.BN, Quetzal.BB, Quetzal.BR, Quetzal.BQ, Quetzal.BK, Quetzal.EP, Quetzal.CWK, Quetzal.CWQ, Quetzal.CBK, Quetzal.CBQ, Quetzal.WhiteToMove);
        assertNotEquals("a7a6", UCI.moveToAlgebra(moves.substring(i, i + 4)));
        System.out.println(UCI.moveToAlgebra(moves.substring(i, i + 4)));
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
        String moves = Moves.possibleMovesBlack(Quetzal.WP, Quetzal.WN, Quetzal.WB, Quetzal.WR, Quetzal.WQ, Quetzal.WK, Quetzal.BP, Quetzal.BN, Quetzal.BB, Quetzal.BR, Quetzal.BQ, Quetzal.BK, Quetzal.EP, Quetzal.CWK, Quetzal.CWQ, Quetzal.CBK, Quetzal.CBQ);
        String legalMoves = Moves.legalMoves(moves, Quetzal.WP, Quetzal.WN, Quetzal.WB, Quetzal.WR, Quetzal.WQ, Quetzal.WK, Quetzal.BP, Quetzal.BN, Quetzal.BB, Quetzal.BR, Quetzal.BQ, Quetzal.BK, Quetzal.EP, Quetzal.CWK, Quetzal.CWQ, Quetzal.CBK, Quetzal.CBQ, Quetzal.WhiteToMove);
        assertEquals(moves, legalMoves);
    }

    @Test
    void testPVSFromMidGame() {
        BoardGenerator.importFEN("1r1qr1k1/2p2pp1/2nb1n2/1p4N1/3pPB2/3P3P/1P1N1P1K/R2Q2R1 w - - 2 23");
        int bestScore = PVSAlgorithm.principleVariationSearch(-1000, 1000, Quetzal.WP, Quetzal.WN, Quetzal.WB, Quetzal.WR, Quetzal.WQ, Quetzal.WK, Quetzal.BP, Quetzal.BN, Quetzal.BB, Quetzal.BR, Quetzal.BQ, Quetzal.BK, Quetzal.EP, Quetzal.CWK, Quetzal.CWQ, Quetzal.CBK, Quetzal.CBQ, Quetzal.WhiteToMove, 0);
        assertEquals("e5f7", UCI.moveToAlgebra(PVSAlgorithm.bestMove[0]));
    }
}