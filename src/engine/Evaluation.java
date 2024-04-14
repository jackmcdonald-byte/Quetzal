package engine;

import NNUE.nnue_probe.nnue.NNUEJNIBridge;
import NNUE.nnue_probe.nnue.NNUEProbeUtils;

public class Evaluation {
    /**
     * Evaluate the position using the NNUE evaluation function
     *
     * @param input Input to the NNUE evaluation function
     * @return Evaluation score
     */
    public static int evaluateNNUE(NNUEProbeUtils.Input input) {
        return NNUEJNIBridge.eval(input.color, input.pieces, input.squares);
    }
}