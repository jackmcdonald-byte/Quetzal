package engine.datastructs;

import java.util.Comparator;
import java.util.LinkedList;

public class MoveTree {
    private MoveNode root;

    /**
     * Constructor
     */
    public MoveTree() {
        root = new MoveNode("", null);
    }

    /**
     * Adds a move to the tree
     *
     * @param move   Move to be the root
     * @param parent Parent of the root
     */
    public void addMove(Move move, MoveNode parent) {
        parent.addChild(move);
    }

    /**
     * Adds a move to the tree
     *
     * @param move   Move to be added
     * @param parent Parent of the move
     */
    public void addMove(MoveNode move, MoveNode parent) {
        parent.addChild(move.getMove());
    }

    /**
     * Adds a move to the tree
     *
     * @param move   Move to be added
     * @param parent Parent of the move
     */
    public void addMove(String move, MoveNode parent) {
        parent.addChild(new Move(move, 0, 0));
    }

    /**
     * Adds a move to the tree
     *
     * @param move   Move to be added
     * @param score  Score of the move
     * @param parent Parent of the move
     */
    public void addMove(String move, int score, MoveNode parent) {
        parent.addChild(new Move(move, score, 0));
    }

    /**
     * Adds a move to the tree
     *
     * @param move   Move to be added
     * @param score  Score of the move
     * @param depth  Depth of the move
     * @param parent Parent of the move
     */
    public void addMove(String move, int score, int depth, MoveNode parent) {
        parent.addChild(new Move(move, score, depth));
    }

    /**
     * Getter
     *
     * @return Root of the tree
     */
    public MoveNode getRoot() {
        return root;
    }

    /**
     * Getter
     *
     * @return Children of the root
     */
    public LinkedList<MoveNode> getNextMoves(MoveNode node) {
        return node.getChildren();
    }

    /**
     * Getter
     *
     * @return Best move of the tree at the root
     */
    public String getBestMove() {
        return root.getBestMove().getMove().getMoveString();
    }

    /**
     * Getter
     *
     * @return Score of the best move
     */
    public int getBestMoveScore() {
        return root.getBestMove().getMove().getScore();
    }

    /**
     * Sorts the children of the parent node by score
     *
     * @param parent Parent node
     */
    public void sortMovesByScore(MoveNode parent) {
        LinkedList<MoveNode> children = parent.getChildren();
        children.sort(Comparator.comparingInt((MoveNode a) -> -a.getMove().getScore()));
    }

    /**
     * Looks up a move in a parent node by move string
     *
     * @param move   Move to be looked up
     * @param parent Parent of the move
     * @return MoveNode if found, null otherwise
     */
    public MoveNode lookup(String move, MoveNode parent) {
        LinkedList<MoveNode> children = parent.getChildren();
        for (MoveNode child : children) {
            if (child.getMove().equals(move)) {
                return child;
            }
        }
        return null;
    }

    /**
     * MoveNode - Used to store a move and its relevant information in the move tree
     */
    public static class MoveNode {
        private Move move;
        private MoveNode parent;
        private LinkedList<MoveNode> children;

        /**
         * Constructor
         *
         * @param move   Move to be added
         * @param parent Parent of the move
         */
        public MoveNode(Move move, MoveNode parent) {
            this.move = move;
            this.parent = parent;
            children = new LinkedList<MoveNode>();
        }

        /**
         * Constructor
         *
         * @param move   Move to be added
         * @param parent Parent of the move
         */
        public MoveNode(String move, MoveNode parent) {
            this.move = new Move(move, 0, 0);
            this.parent = null;
            children = new LinkedList<MoveNode>();
        }

        /**
         * Adds a move to the head of the children list
         *
         * @param move Move to be added
         */
        private void addChild(Move move) {
            MoveNode child = new MoveNode(move, this);
            children.addFirst(child);
        }

        /**
         * Removes all children from the node
         */
        public void clearChildren() {
            children = new LinkedList<MoveNode>();
        }

        /**
         * Getter
         *
         * @return Move of the node
         */
        public Move getMove() {
            return move;
        }

        /**
         * Getter
         *
         * @return Move of the node
         */
        public String getMoveString() {
            return move.getMoveString();
        }

        /**
         * Getter
         *
         * @return Parent of the node
         */
        public MoveNode getParent() {
            return parent;
        }

        /**
         * Getter
         *
         * @return Children of the node
         */
        public LinkedList<MoveNode> getChildren() {
            return children;
        }

        /**
         * Getter
         *
         * @return Best move of the node
         */
        public MoveNode getBestMove() {
            return children.getFirst();
        }

        /**
         * Getter
         *
         * @return Score of the move
         */
        public int getScore() {
            return move.getScore();
        }

        /**
         * Setter
         *
         * @param score Score of the move
         */
        public void setMoveScore(int score) {
            move.setScore(score);
        }

        @Override
        public boolean equals(Object node) {
            if (node instanceof MoveNode) {
                return move.equals(((MoveNode) node).getMove());
            }
            return false;
        }

        @Override
        public String toString() {
            return move.toString();
        }
    }
}
