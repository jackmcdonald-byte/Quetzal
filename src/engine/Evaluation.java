package engine;

import NNUE.nnue_probe.nnue.NNUEJNIBridge;
import NNUE.nnue_probe.nnue.NNUEProbeUtils;

public class Evaluation {
    public static int evaluateNNUE(NNUEProbeUtils.Input input) {
        return NNUEJNIBridge.eval(input.color, input.pieces, input.squares);
    }
}