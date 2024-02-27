package NNUE.nnue_probe.nnue;
import java.io.File;
import engine.*;

//import bagaturchess.bitboard.api.BoardUtils;
//import bagaturchess.bitboard.api.IBitBoard;
//import bagaturchess.bitboard.impl.Constants;


public class ProbeMain {


    public static void main(String[] args) {

        File dll = new File("src/NNUE/nnue_probe/JNNUE.dll");

        System.load(dll.getAbsolutePath());

        //File nnue = new File("nn-04cf2b4ed1da.nnue");
        //File nnue = new File("nn-53d4417222df.nnue");//user: vdv 21-01-01 21:24:06
        File nnue = new File("nn-6b4236f2ec01.nnue");//user: vdv 21-05-01 10:24:00

        NNUEJNIBridge.init(nnue.getName());

//        IBitBoard bitboard = BoardUtils.createBoard_WithPawnsCache(Constants.INITIAL_BOARD);
//
//        String fen = bitboard.toEPD();
//        System.out.println("fen=" + fen);

        String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";

        int score_fen = NNUEJNIBridge.eval(fen);

        System.out.println("score by fen = " + score_fen);

//        NNUEProbeUtils.Input input = new NNUEProbeUtils.Input();
//        NNUEProbeUtils.fillInput(bitboard, input);

//        int score = NNUEJNIBridge.eval(input.color, input.pieces, input.squares);

//        System.out.println("score = " + score);
        BoardGenerator.importFEN(fen);

        System.out.println(NNUEProbeUtils.boardToFen(Quetzal.WP, Quetzal.WN, Quetzal.WB, Quetzal.WR, Quetzal.WQ, Quetzal.WK, Quetzal.BP, Quetzal.BN, Quetzal.BB, Quetzal.BR, Quetzal.BQ, Quetzal.BK, Quetzal.EP, Quetzal.CWK, Quetzal.CWQ, Quetzal.CBK, Quetzal.CBQ, Quetzal.WhiteToMove));
    }
}