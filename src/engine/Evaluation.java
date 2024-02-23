package engine;
import java.util.*;

public class Evaluation {
    public static int evaluate(long WP, long WN, long WB, long WR, long WQ, long WK, long BP, long BN, long BB, long BR, long BQ, long BK, boolean WhiteToMove) {
        Random rand = new Random();
        int materialEval = 0;

        if (WhiteToMove) {
            for (int i = 0; i < 64; i++) {
                if (((WP >> i) & 1) == 1) {
                    materialEval += 1;
                } else if (((WN >> i) & 1) == 1) {
                    materialEval += 3;
                } else if (((WB >> i) & 1) == 1) {
                    materialEval += 3;
                } else if (((WR >> i) & 1) == 1) {
                    materialEval += 5;
                } else if (((WQ >> i) & 1) == 1) {
                    materialEval += 9;
                } else if (((BP >> i) & 1) == 1) {
                    materialEval -= 1;
                } else if (((BN >> i) & 1) == 1) {
                    materialEval -= 3;
                } else if (((BB >> i) & 1) == 1) {
                    materialEval -= 3;
                } else if (((BR >> i) & 1) == 1) {
                    materialEval -= 5;
                } else if (((BQ >> i) & 1) == 1) {
                    materialEval -= 9;
                }
            }
        } else {
            for (int i = 0; i < 64; i++) {
                if (((BP >> i) & 1) == 1) {
                    materialEval += 1;
                } else if (((BN >> i) & 1) == 1) {
                    materialEval += 3;
                } else if (((BB >> i) & 1) == 1) {
                    materialEval += 3;
                } else if (((BR >> i) & 1) == 1) {
                    materialEval += 5;
                } else if (((BQ >> i) & 1) == 1) {
                    materialEval += 9;
                } else if (((WP >> i) & 1) == 1) {
                    materialEval -= 1;
                } else if (((WN >> i) & 1) == 1) {
                    materialEval -= 3;
                } else if (((WB >> i) & 1) == 1) {
                    materialEval -= 3;
                } else if (((WR >> i) & 1) == 1) {
                    materialEval -= 5;
                } else if (((WQ >> i) & 1) == 1) {
                    materialEval -= 9;
                }
            }
        }

        return (materialEval);
    }
}