package Client;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.*;
import Server.Pedina;
import Server.Posizione;

public class Campo {
    private final int MAX = 8;
    private Pedina[][] board;

    public Campo(Pedina[][] board) {
        this.board = board;
    }

    public void drawBoard() {
        JPanel[][] cells = new JPanel[MAX][MAX];
        JFrame frame = new JFrame("Dama");
        PedinaGrafica pedinaCliccata = null; // Per tenere conto di quale pedina si ha cliccato e poi muovere

        frame.setLayout(new GridLayout(MAX, MAX));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        for (int i=0 ; i<MAX ; i++) {
            for (int j=0 ; j<MAX ; j++) {
                cells[i][j] = new JPanel();
                
                // Per alternare i colori di sfondo
                if ((i + j) % 2 == 0) {
                    cells[i][j].setBackground(Color.WHITE);
                }
                else {
                    cells[i][j].setBackground(Color.BLACK);
                }

                // Se é presente una pedina
                if (board[i][j] != null) {
                    PedinaGrafica piece = new PedinaGrafica(new Posizione(i, j));
                    piece.setColor(board[i][j].getColor().equals("black") ? Color.DARK_GRAY : Color.LIGHT_GRAY); // Cambia colore pedina in base al giocatore
                    piece.setPreferredSize(new Dimension(60, 60)); // Adjust as needed for the right size
                    cells[i][j].setLayout(new BorderLayout());
                    cells[i][j].add(piece, BorderLayout.CENTER);
                    // Dovrebbe farlo solo se si clicca
                    pedinaCliccata = piece;
                }
                
                // Aggiunge un listener per il click sulla cella
                // Mettere variabili final sennó da errore (non so perché)
                final int row = i;
                final int col = j;
                final PedinaGrafica pedinaCliccataFinal = pedinaCliccata;
                cells[i][j].addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        movePiece(row, col, pedinaCliccataFinal);
                    }
                });

                frame.add(cells[i][j]);
            }
        }

        frame.pack(); // Ridimensiona frame in base al contenuto
        frame.setResizable(true);
        frame.setVisible(true);
    }

    // Dovrebbe prendere in input la classe PedinaGrafica
    public void movePiece(int row, int col, PedinaGrafica piece) {
        System.out.println("Cella cliccata: " + row + ", " + col);
        System.out.println("Pedina da muovere: " + piece.getPosition());
    }
}
