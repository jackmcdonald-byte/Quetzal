package engine;
import java.util.*;
import NNUE.nnue_probe.nnue.NNUEJNIBridge;
import NNUE.nnue_probe.nnue.NNUEProbeUtils;

public class Evaluation {
    public static int evaluate(NNUEProbeUtils.Input input) {
        return NNUEJNIBridge.eval(input.color, input.pieces, input.squares);
    }
}