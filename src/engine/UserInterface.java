package engine;

import java.awt.*;
import java.awt.event.*;
import java.util.Objects;
import javax.swing.*;

//temporary class until Universal Chess Interface is implemented

public class UserInterface extends JPanel {
    static long WP, WN, WB, WR, WQ, WK, BP, BN, BB, BR, BQ, BK;
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
        javaF.repaint();
    }

    public static void newGame() {
        BoardGenerator.initiateStandardBoard();
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
            if (j != -1 && k != -1) {
                g.drawImage(chessPieceImage, (int) ((i % 8) * squareSize) + border, (int) ((i / 8) * squareSize) + border,
                        (int) ((i % 8 + 1) * squareSize) + border, (int) ((i / 8 + 1) * squareSize) + border, j * 60,
                        k * 60, (j + 1) * 60, (k + 1) * 60, this);
            }
            // rest of your code
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