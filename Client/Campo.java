package Client;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.*;
import Server.Pedina;
import Server.Posizione;

public class Campo implements PedinaClickListener {
    private final int MAX = 8;
    private Pedina[][] board;
    private JPanel[][] cells = new JPanel[MAX][MAX];
    private ArrayList<PedinaGrafica> allPedineGrafiche = new ArrayList<>();
    private PedinaGrafica pedinaCliccata = null; // Tiene traccia della pedina cliccata prima che deve essere spostata

    public Campo(Pedina[][] board) {
        this.board = board;
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

                // If there's a piece
                if (board[i][j] != null) {
                    PedinaGrafica piece = new PedinaGrafica(new Posizione(i, j), this.board);
                    allPedineGrafiche.add(piece);
                    piece.setArrayAllPedineGrafiche(allPedineGrafiche);
                    piece.setColor(board[i][j].getColor().equals("black") ? Color.DARK_GRAY : Color.LIGHT_GRAY);
                    piece.setPreferredSize(new Dimension(60, 60));
                    piece.setClickListener(this); // Setta la classe come listener per i click
                    cells[i][j].setLayout(new BorderLayout());
                    cells[i][j].add(piece, BorderLayout.CENTER);
                }

                // Bisogna usare variabili final nell' event listener
                final int row = i;
                final int col = j;
                cells[i][j].addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (pedinaCliccata != null) {
                            Posizione validPosition = null;
                            ArrayList<Posizione> allPossibleMoves = pedinaCliccata.getPossibleMoves(); // Per ora logica non ancora implementata -> ritorna un ArrayList vuoto

                            // Trova se la cella cliccata é una valida (dovrebbe essere colorata)
                            if (allPossibleMoves.size() > 0) {
                                for (Posizione pos : allPossibleMoves) {
                                    // Controllo che la cella cliccata abbia coordinate di una delle celle valide da muoevere
                                    if(pos.getX() == row && pos.getY() == col) {
                                        validPosition = pos;
                                    }
                                }
                            }

                            // Se si ha cliccato una cella valida
                            if (validPosition != null) {
                                movePiece(row, col, pedinaCliccata);
                                pedinaCliccata.setOpacity(1.0f);; // Reset to original color and opacity
                                pedinaCliccata = null; // Pedina spostata, nessuna pedina selezionata di default
                            }
                        }
                    }
                });

                frame.add(cells[i][j]);
            }
        }

        frame.pack();
        frame.setResizable(true);
        frame.setVisible(true);
    }

    @Override
    public void onPedinaClicked(PedinaGrafica pedina) {
        this.pedinaCliccata = pedina; // Setta pedina cliccata grazie all' interfaccia
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
}
