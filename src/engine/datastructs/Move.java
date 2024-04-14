package engine.datastructs;

import engine.UCI;

public class Move {
    String move;
    int score;
    int depth;

    /**
     * Constructor
     *
     * @param move  Move made
     * @param score Score of the move
     * @param depth Depth of the move
     */
    public Move(String move, int score, int depth) {
        this.move = move;
        this.score = score;
        this.depth = depth;
    }

    /**
     * Getter
     *
     * @return Move made
     */
    public String getMoveString() {
        return move;
    }

    /**
     * Getter
     *
     * @return Score of the move
     */
    public int getScore() {
        return score;
    }

    /**
     * Setter
     *
     * @param score Score of the move
     */
    public void setScore(int score) {
        this.score = score;
    }

    /**
     * Getter
     *
     * @return Depth of the move
     */
    public int getDepth() {
        return depth;
    }

    /**
     * Setter
     *
     * @param depth Depth of the move
     */
    public void setDepth(int depth) {
        this.depth = depth;
    }

    /**
     * Setter
     *
     * @param move Move made
     */
    public void setMove(String move) {
        this.move = move;
    }

    @Override
    public String toString() {
        return UCI.moveToAlgebra(move) + " " + score + " " + depth;
    }

    @Override
    public boolean equals(Object move) {
        if (move instanceof Move) {
            return this.move.equals(((Move) move).getMoveString());
        }
        return false;
    }

    /**
     * Compares a move to a string
     *
     * @param move
     * @return True if the move is equal to the string, false otherwise
     */
    public boolean equals(String move) {
        return this.move.equals(move);
    }
}
