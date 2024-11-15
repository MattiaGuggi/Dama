package Client;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.*;
import Server.Pedina;
import Server.Posizione;

public class Campo {
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
                
                // Bisogna usare variabili final nell' event listener
                final Pedina[][] finalBoard = this.board;
                // If there's a piece
                if (board[i][j] != null) {
                    PedinaGrafica piece = new PedinaGrafica(new Posizione(i, j));

                    // Listener per click sulla pedina
                    // Potremmo aggiungere qui la logica di controllo del colore della propria pedina per evitare si selezionino pedine sbagliate
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
                            
                            piece.showPossibleMoves(finalBoard);
                        }
                    });
                    allPedineGrafiche.add(piece);
                    piece.setColor(board[i][j].getColor().equals("black") ? Color.DARK_GRAY : Color.LIGHT_GRAY);
                    piece.setPreferredSize(new Dimension(60, 60));
                    cells[i][j].setLayout(new BorderLayout());
                    cells[i][j].add(piece, BorderLayout.CENTER);
                }

                // Bisogna usare variabili final nell' event listener
                final int row = i;
                final int col = j;

                // Listener per click sulla cella
                cells[i][j].addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        // Se é il suo turno
                        if (isTurn()) {
                            // Se ho selezionato una pedina
                            if (pedinaCliccata != null) {
                                Posizione validPosition = null;
                                ArrayList<Posizione> allPossibleMoves = pedinaCliccata.getPossibleMoves(finalBoard); // Cerchiamo mosse possibili

                                // Trova se la cella cliccata é una valida (dovrebbe essere colorata diversamente)
                                if (allPossibleMoves.size() > 0) {
                                    for (Posizione pos : allPossibleMoves) {
                                        // Controllo che la cella cliccata abbia coordinate di una delle celle valide da muovere
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

                                System.out.println("Hai cliccato sulla cella: " + row + "," + col);
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

    public Boolean isTurn() {
        return true;
    }
}
