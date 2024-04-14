package engine;

import java.security.*;

import engine.datastructs.ChessBoard;

import java.math.*;

import static engine.utils.BitBoardConstants.FileMasks8;

public class Zobrist {
    static long[][][] zArray = new long[2][6][64];
    static long[] zEnPassant = new long[8];
    static long[] zCastle = new long[4];
    static long zBlackMove;

    public static long random64() {
        //SecureRandom is the best random number generator for this purpose as it
        //provides the best distribution of random numbers
        //
        //Note: even distribution is important to prevent Zobrist Hash Collisions; there
        //will always be a collision risk, but a good random number generator can minimize this to
        //a negligible level
        SecureRandom random = new SecureRandom();
        return random.nextLong();
    }

    /**
     * Fill the zobrist attributes with evenly distributed random 64-bit numbers
     */
    public static void zobristFillArray() {
        //Note: performance does not matter here, as this is only done once at the start of the program
        for (int color = 0; color < 2; color++) {
            for (int pieceType = 0; pieceType < 6; pieceType++) {
                for (int square = 0; square < 64; square++) {
                    zArray[color][pieceType][square] = random64();
                }
            }
        }
        for (int column = 0; column < 8; column++) {
            zEnPassant[column] = random64();
        }
        for (int i = 0; i < 4; i++) {
            zCastle[i] = random64();
        }
        zBlackMove = random64();
    }

    /**
     * Zobrist hashing algorithm to generate a unique hash for a given board state
     *
     * @param board
     * @return
     */
    //Note: this method is called frequently, so performance is key
    //Note: 2 unique board states can have the same zobrist key, but the probability of this is near extremely low
    public static long getZobristHash(ChessBoard board) {
        long returnZKey = 0;

        //Begin generating zobrist hash by (XOR)ing the random numbers for each piece on the board
        for (int square = 0; square < 64; square++) {
            if (((board.getWP() >> square) & 1) == 1) {
                returnZKey ^= zArray[0][0][square];
            } else if (((board.getBP() >> square) & 1) == 1) {
                returnZKey ^= zArray[1][0][square];
            } else if (((board.getWN() >> square) & 1) == 1) {
                returnZKey ^= zArray[0][1][square];
            } else if (((board.getBN() >> square) & 1) == 1) {
                returnZKey ^= zArray[1][1][square];
            } else if (((board.getWB() >> square) & 1) == 1) {
                returnZKey ^= zArray[0][2][square];
            } else if (((board.getBB() >> square) & 1) == 1) {
                returnZKey ^= zArray[1][2][square];
            } else if (((board.getWR() >> square) & 1) == 1) {
                returnZKey ^= zArray[0][3][square];
            } else if (((board.getBR() >> square) & 1) == 1) {
                returnZKey ^= zArray[1][3][square];
            } else if (((board.getWQ() >> square) & 1) == 1) {
                returnZKey ^= zArray[0][4][square];
            } else if (((board.getBQ() >> square) & 1) == 1) {
                returnZKey ^= zArray[1][4][square];
            } else if (((board.getWK() >> square) & 1) == 1) {
                returnZKey ^= zArray[0][5][square];
            } else if (((board.getBK() >> square) & 1) == 1) {
                returnZKey ^= zArray[1][5][square];
            }
        }

        //Continue by (XOR)ing the en passant, castling, and side to move random numbers
        for (int column = 0; column < 8; column++) {
            if (board.getEP() == FileMasks8[column]) {
                returnZKey ^= zEnPassant[column];
            }
        }
        if (board.getCWK())
            returnZKey ^= zCastle[0];
        if (board.getCWQ())
            returnZKey ^= zCastle[1];
        if (board.getCBK())
            returnZKey ^= zCastle[2];
        if (board.getCBQ())
            returnZKey ^= zCastle[3];
        if (!board.getWhiteToMove())
            returnZKey ^= zBlackMove;

        //TODO figure out how to work around negative zobrist keys by implementing a better hash function
        return Math.abs(returnZKey);
    }

    /**
     * Debug method to test the distribution of the random number generator (copy/paste into a spreadsheet and create a histogram)
     */
    public static void testDistribution() {
        //Debug method
        int sampleSize = 2000;
        int sampleSeconds = 10;
        long startTime = System.currentTimeMillis();
        long endTime = startTime + (sampleSeconds * 1000);
        int[] distArray;
        distArray = new int[sampleSize];
        while (System.currentTimeMillis() < endTime) {
            for (int i = 0; i < 10000; i++) {
                distArray[(int) (random64() % (sampleSize / 2)) + (sampleSize / 2)]++;
            }
        }
        for (int i = 0; i < sampleSize; i++) {
            System.out.println(distArray[i]);
        }
    }
}