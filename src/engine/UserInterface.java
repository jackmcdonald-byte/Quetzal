package engine;

import java.awt.*;
import java.awt.event.*;
import java.util.Objects;
import javax.swing.*;

//temporary class until Universal Chess Interface is implemented

public class UserInterface extends JPanel {
    public static long WP = 0L, WN = 0L, WB = 0L, WR = 0L, WQ = 0L, WK = 0L, BP = 0L, BN = 0L, BB = 0L, BR = 0L, BQ = 0L, BK = 0L, EP = 0L;
    static boolean CWK = true, CWQ = true, CBK = true, CBQ = true, whiteToMove = true; //castling rights
    static long UniversalWP, UniversalWN, UniversalWB, UniversalWR, UniversalWQ, UniversalWK, UniversalBP, UniversalBN, UniversalBB, UniversalBR, UniversalBQ, UniversalBK;
    static int humanIsWhite, rating;
    static int border;
    static double squareSize;
    static JFrame javaF;
    static UserInterface javaUI;

    static {
        humanIsWhite = 1;
        rating = 0;
        border = 10;
        squareSize = 64;
        javaF = new JFrame("Quetzal v1.0 created by Jack McDonald, 2024");
        javaUI = new UserInterface();
    }

    public static void main(String[] args) {
        javaF.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        javaF.add(javaUI);
        javaF.setSize(757, 570);
        javaF.setLocation((Toolkit.getDefaultToolkit().getScreenSize().width - javaF.getWidth()) / 2,
                (Toolkit.getDefaultToolkit().getScreenSize().height - javaF.getHeight()) / 2);
        javaF.setVisible(true);
        newGame();
//        BoardGenerator.importFEN("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq -");
//        BoardGenerator.importFEN("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
        BoardGenerator.importFEN("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq -");
//        BoardGenerator.importFEN("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - -");
//        BoardGenerator.importFEN("r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1");
//        BoardGenerator.importFEN("1k6/1b6/8/8/7R/8/8/4K2R b K - 0 1");
//        BoardGenerator.importFEN("1k6/8/8/8/7R/8/8/4K2b w - - 0 2");
//        BoardGenerator.importFEN("3k4/3p4/8/K1P4r/8/8/8/8 b - - 0 1");
//        BoardGenerator.importFEN("8/8/4k3/8/2p5/8/B2P2K1/8 w - - 0 1");
//        BoardGenerator.importFEN("8/8/1k6/2b5/2pP4/8/5K2/8 b - d3 0 1");
//        BoardGenerator.importFEN("5k2/8/8/8/8/8/8/4K2R w K - 0 1");
//        BoardGenerator.importFEN("3k4/8/8/8/8/8/8/R3K3 w Q - 0 1");
//        BoardGenerator.importFEN("r3k2r/1b4bq/8/8/8/8/7B/R3K2R w KQkq - 0 1");
//        BoardGenerator.importFEN("r3k2r/8/3Q4/8/8/5q2/8/R3K2R b KQkq - 0 1");
//        BoardGenerator.importFEN("2K2r2/4P3/8/8/8/8/8/3k4 w - - 0 1");
//        BoardGenerator.importFEN("8/8/1P2K3/8/2n5/1q6/8/5k2 b - - 0 1");
//        BoardGenerator.importFEN("4k3/1P6/8/8/8/8/K7/8 w - - 0 1");
//        BoardGenerator.importFEN("8/P1k5/K7/8/8/8/8/8 w - - 0 1");
//        BoardGenerator.importFEN("K1k5/8/P7/8/8/8/8/8 w - - 0 1");
//        BoardGenerator.importFEN("8/k1P5/8/1K6/8/8/8/8 w - - 0 1");
//        BoardGenerator.importFEN("8/8/2k5/5q2/5n2/8/5K2/8 b - - 0 1");
//        BoardGenerator.importFEN("");
//        BoardGenerator.importFEN("");
//        BoardGenerator.importFEN("");
//        BoardGenerator.initiateStandardBoard();
        BoardGenerator.drawArray(WP,WN,WB,WR,WQ,WK,BP,BN,BB,BR,BQ,BK);
        long startTime=System.currentTimeMillis();
        Perft.perftRoot(WP,WN,WB,WR,WQ,WK,BP,BN,BB,BR,BQ,BK,EP,CWK,CWQ,CBK,CBQ,whiteToMove,0);
        long endTime=System.currentTimeMillis();
        System.out.println("Nodes: "+Perft.perftTotalMoveCounter);
        System.out.println("That took "+(endTime-startTime)+" milliseconds");
        System.out.println("Nodes Per Second: "+(int)(Perft.perftTotalMoveCounter/((endTime-startTime)/1000.0)));
        javaF.repaint();
    }

    public static void newGame() {
        BoardGenerator.initiateStandardBoard();
        Moves.possibleMovesWhite(WP, WN, WB, WR, WQ, WK, BP, BN, BB, BR, BQ, BK, EP, CWK, CWQ, CBK, CBQ);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        this.setBackground(new Color(200, 100, 0));
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                squareSize = (double) (Math.min(getHeight(), getWidth() - 200 - border) - 2 * border) / 8;
            }
        });
        drawBorders(g);
        drawBoard(g);
        drawPieces(g);
    }

    public void drawBoard(Graphics g) {
        for (int i = 0; i < 64; i += 2) {//draw chess board
            g.setColor(new Color(255, 200, 100));
            g.fillRect((int) ((i % 8 + (i / 8) % 2) * squareSize) + border, (int) ((i / 8) * squareSize) + border, (int) squareSize, (int) squareSize);
            g.setColor(new Color(150, 50, 30));
            g.fillRect((int) (((i + 1) % 8 - ((i + 1) / 8) % 2) * squareSize) + border, (int) (((i + 1) / 8) * squareSize) + border, (int) squareSize, (int) squareSize);
        }
    }

    public void drawPieces(Graphics g) {
        //TODO swap K/Q if humanIsWhite = 0
        Image chessPieceImage;
        chessPieceImage = new ImageIcon(Objects.requireNonNull(UserInterface.class.getResource("ChessPiecesArray.png"))).getImage();
        for (int i = 0; i < 64; i++) {
            int j = -1, k = -1;
            long[] pieces = {WP, BP, WB, BB, WN, BN, WQ, BQ, WR, BR, WK, BK};
            int[] pieceValues = {5, 5, 4, 4, 3, 3, 0, 0, 2, 2, 1, 1};
            int[] colorValues = {humanIsWhite, 1 - humanIsWhite};

            for (int p = 0; p < pieces.length; p++) {
                if (((pieces[p] >> i) & 1) == 1) {
                    j = pieceValues[p];
                    k = colorValues[p % 2];
                    break;
                }
            }
            g.drawImage(chessPieceImage, (int) ((i % 8) * squareSize) + border, (int) ((i / 8) * squareSize) + border,
                    (int) ((i % 8 + 1) * squareSize) + border, (int) ((i / 8 + 1) * squareSize) + border, j * 60,
                    k * 60, (j + 1) * 60, (k + 1) * 60, this);
        }
    }

    public void drawBorders(Graphics g) {
        g.setColor(new Color(100, 0, 0));
        g.fill3DRect(0, border, border, (int) (8 * squareSize), true);
        g.fill3DRect((int) (8 * squareSize) + border, border, border, (int) (8 * squareSize), true);
        g.fill3DRect(border, 0, (int) (8 * squareSize), border, true);
        g.fill3DRect(border, (int) (8 * squareSize) + border, (int) (8 * squareSize), border, true);

        g.setColor(Color.BLACK);
        g.fill3DRect(0, 0, border, border, true);
        g.fill3DRect((int) (8 * squareSize) + border, 0, border, border, true);
        g.fill3DRect(0, (int) (8 * squareSize) + border, border, border, true);
        g.fill3DRect((int) (8 * squareSize) + border, (int) (8 * squareSize) + border, border, border, true);
        g.fill3DRect((int) (8 * squareSize) + 2 * border + 200, 0, border, border, true);
        g.fill3DRect((int) (8 * squareSize) + 2 * border + 200, (int) (8 * squareSize) + border, border, border, true);

        g.setColor(new Color(0, 100, 0));
        g.fill3DRect((int) (8 * squareSize) + 2 * border, 0, 200, border, true);
        g.fill3DRect((int) (8 * squareSize) + 2 * border + 200, border, border, (int) (8 * squareSize), true);
        g.fill3DRect((int) (8 * squareSize) + 2 * border, (int) (8 * squareSize) + border, 200, border, true);
    }
}