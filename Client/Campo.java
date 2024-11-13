package Client;

import java.awt.*;
import javax.swing.*;
import Server.Pedina;

public class Campo {
    private final int MAX = 8;

    public Campo(Pedina[][] board) {
        new JFrame1(board); // Draw the game board immediately
    }
}

class JFrame1 {
    private final int MAX = 8;
    private JButton[][] buttons = new JButton[MAX][MAX];

    public JFrame1(Pedina[][] board) {
        JFrame frame = new JFrame("Dama");

        frame.setSize(800, 800);
        frame.setLayout(new GridLayout(MAX, MAX));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        for (int i = 0; i < MAX; i++) {
            for (int j = 0; j < MAX; j++) {
                buttons[i][j] = new JButton();
                buttons[i][j].setLayout(new BorderLayout());
                
                // Per alternare i colori di sfondo
                if ((i + j) % 2 == 0) {
                    buttons[i][j].setBackground(Color.WHITE);
                } else {
                    buttons[i][j].setBackground(Color.BLACK);
                }

                // Se é presente una pedina
                if (board[i][j] != null) {
                    PedinaGrafica piece = new PedinaGrafica();

                    piece.setColor(board[i][j].getColor().equals("black") ? Color.DARK_GRAY : Color.LIGHT_GRAY);
                    buttons[i][j].add(piece, BorderLayout.CENTER);
                }

                frame.add(buttons[i][j]);
            }
        }

        frame.setVisible(true);
    }
}
