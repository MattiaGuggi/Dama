package Client;

import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import Server.Pedina;
import Server.Posizione;

public class Campo {
    private final int MAX = 8;
    private Pedina[][] board;
    private JPanel[][] cells = new JPanel[MAX][MAX];
    private ArrayList<PedinaGrafica> allPedineGrafiche = new ArrayList<>();
    private PedinaGrafica pedinaCliccata = null; // Tiene traccia della pedina cliccata prima che deve essere spostata
    private PrintWriter out;
    private ArrayList<Posizione> allPossibleMoves = new ArrayList<>();

    public Campo(Pedina[][] board, PrintWriter out) {
        this.board = board;
        this.out = out;
    }

    public void drawBoard() {
        JFrame frame = new JFrame("Dama");

        frame.setLayout(new GridLayout(MAX, MAX));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        for (int i = 0; i < MAX; i++) {
            for (int j = 0; j < MAX; j++) {
                cells[i][j] = new JPanel();

                // Alterna i colori della scacchiera
                if ((i + j) % 2 == 0) {
                    cells[i][j].setBackground(Color.WHITE);
                }
                else {
                    cells[i][j].setBackground(Color.BLACK);
                }
                
                // Se c'é una pedina
                if (board[i][j] != null) {
                    PedinaGrafica piece = new PedinaGrafica(new Posizione(i, j));

                    // Listener per click sulla pedina
                    piece.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            pedinaCliccata = piece;
                            // Resetta l'opacità di tutte le pedine
                            if (allPedineGrafiche != null) {
                                for (PedinaGrafica pedina : allPedineGrafiche) {
                                    pedina.setOpacity(1.0f);
                                }
                            }
                            // Imposta l'opacità della pedina cliccata
                            piece.setOpacity(0.5f);

                            out.println("showPossibleMoves#" + pedinaCliccata.toString());
                        }
                    });
                    allPedineGrafiche.add(piece);
                    piece.setColor(board[i][j].getColor().equals("black") ? Color.DARK_GRAY : Color.LIGHT_GRAY);
                    piece.setPreferredSize(new Dimension(60, 60));
                    cells[i][j].setLayout(new BorderLayout());
                    cells[i][j].add(piece, BorderLayout.CENTER);
                }

                // Listener per click sulla cella
                final Posizione position = new Posizione(i, j);
                final int row = i;
                final int col = j;
                cells[i][j].addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        // Controlli
                        
                        if (allPossibleMoves.size() > 0) {
                            for (Posizione pos : allPossibleMoves) {
                                if (pos.getX() == row && pos.getY() == col) {
                                    movePiece(row, col, new PedinaGrafica(position));
                                }
                            }
                        }
                        out.println("movePiece#" + position.toString());
                    }
                });

                frame.add(cells[i][j]);
            }
        }

        frame.pack();
        frame.setResizable(true);
        frame.setVisible(true);
    }

    public void movePiece(int row, int col, PedinaGrafica piece) {
        Posizione oldPosition = piece.getPosition();
        int oldRow = oldPosition.getX();
        int oldCol = oldPosition.getY();

        // Rimuovi pedina dalla cella attuale
        cells[oldRow][oldCol].remove(piece);
        cells[oldRow][oldCol].revalidate();
        cells[oldRow][oldCol].repaint();

        // Aggiungi una pedina alla cella cliccata
        cells[row][col].setLayout(new BorderLayout());
        cells[row][col].add(piece, BorderLayout.CENTER);
        cells[row][col].revalidate();
        cells[row][col].repaint();

        // Rimuovo pedina con posizione vecchia
        allPedineGrafiche.remove(piece);

        // Aggiorna posizione della pedina
        piece.setPosition(new Posizione(row, col));

        // Rimette pedina aggiornata in ArrayList
        allPedineGrafiche.add(piece);

        // Aggiorna logicamente scacchiera (lato server)
        board[oldRow][oldCol] = null;
        board[row][col] = new Pedina(row, col, piece.getColor().equals(Color.DARK_GRAY) ? "black" : "white");
    }

    public void setPossibleMoves (ArrayList<Posizione> allPossibleMoves) {
        this.allPossibleMoves = allPossibleMoves;
    }
}
