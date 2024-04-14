package NNUE.nnue_probe.nnue;


import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;


public class NNUEJNIBridge {


    public static final String NET_NAME = "nn-6b4236f2ec01.nnue";
    //Note: for some reason this is the latest NNUE file that is compatible with the JNNUE library
    //
    //For this reason and several others, I will eventually rewrite this project in C/C++

    /**
     * Initialize the NNUE bridge with the default NNUE file
     */
    public static void init() {
        init(NET_NAME);
    }

    /**
     * Initialize the NNUE bridge with a custom NNUE file
     *
     * @param filename Name of the NNUE file
     */
    public static native void init(String filename);

    /**
     * Evaluate the position using the NNUE evaluation function
     *
     * @param fen FEN string of the position
     * @return Evaluation score
     */
    @Deprecated //this is extremely slow
    public static native int eval(String fen);

    /**
     * Evaluate the position using the NNUE evaluation function
     *
     * @param color   Color to move
     * @param pieces  Array of pieces
     * @param squares Array of squares
     * @return Evaluation score
     */
    public static native int eval(int color, int[] pieces, int[] squares);

    /**
     * Temporary setup method for loading the NNUE and JNNUE library
     */
    public static void setup() {
        //Temporary method for loading the NNUE and JNNUE library
        File dll = new File("src/NNUE/nnue_probe/JNNUE.dll");
        System.load(dll.getAbsolutePath());
        File nnue = new File("nn-6b4236f2ec01.nnue"); //user: vdv 21-05-01 10:24:00
        NNUEJNIBridge.init(nnue.getName());
    }
}