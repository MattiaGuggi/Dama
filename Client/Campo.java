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
    private PedinaGrafica pedinaCliccata; // Tiene traccia della pedina cliccata prima che deve essere spostata
    private PrintWriter out = null;
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
                    PedinaGrafica piece = new PedinaGrafica(new Posizione(j, i));

                    // Listener per click sulla pedina solo se del colore giusto
                    if (i > MAX / 2) {
                        piece.addMouseListener(new MouseAdapter() {
                            @Override
                            public void mouseClicked(MouseEvent e) {
                                if (pedinaCliccata != null) {
                                    pedinaCliccata.setOpacity(1.0f);
                                }

                                pedinaCliccata = piece;
                                System.out.println("Nuova pedina selezionata: " + pedinaCliccata);
                                
                                piece.setOpacity(0.5f);
                        
                                if (pedinaCliccata != null) {
                                    out.println("showPossibleMoves#" + pedinaCliccata.toString());
                                }
                                else {
                                    System.err.println("Non hai selezionato una pedina.");
                                }
                                allPossibleMoves.clear(); // Resetta le mosse possibili
                            }
                        });
                    }
                    allPedineGrafiche.add(piece);
                    piece.setColor(board[i][j].getColor().equals("black") ? Color.DARK_GRAY : Color.LIGHT_GRAY);
                    piece.setPreferredSize(new Dimension(60, 60));
                    cells[i][j].setLayout(new BorderLayout());
                    cells[i][j].add(piece, BorderLayout.CENTER);
                }

                // Listener per click sulla cella
                final Posizione position = new Posizione(j, i);
                final int row = i;
                final int col = j;
                cells[i][j].addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        System.out.println("Hai cliccato sulla casella: " + col + "," + row);
                        // Controllo che sia una posizione valida
                        if (pedinaCliccata != null) {
                            for (Posizione pos : allPossibleMoves) {
                                if (pos.getX() == col && pos.getY() == row) {
                                    movePiece(row, col, pedinaCliccata);
                                    out.println("movePiece#" + position.toString());
                                    pedinaCliccata = null;
                                }
                            }
                        }
                        else {
                            System.out.println("ArrayList vuoto");
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

    // Coordinate sballate
    public void movePiece(int row, int col, PedinaGrafica piece) {
        Posizione oldPosition = piece.getPosition();
        int oldRow = oldPosition.getY();
        int oldCol = oldPosition.getX();

        // Rimuovi pedina dalla cella attuale
        cells[oldRow][oldCol].remove(piece);
        cells[oldRow][oldCol].revalidate();
        cells[oldRow][oldCol].repaint();

        // Rimuovo pedina con posizione vecchia
        allPedineGrafiche.remove(piece);

        // Rimette pedina aggiornata in ArrayList
        PedinaGrafica newPiece = new PedinaGrafica(new Posizione(col, row));
        newPiece.setOpacity(1.0f);
        allPedineGrafiche.add(newPiece);

        // Aggiungi una pedina alla cella cliccata
        cells[row][col].setLayout(new BorderLayout());
        cells[row][col].add(newPiece, BorderLayout.CENTER);
        cells[row][col].revalidate();
        cells[row][col].repaint();

        // Aggiorna logicamente scacchiera (lato server)
        this.board[oldRow][oldCol] = null;
        this.board[row][col] = new Pedina(row, col, piece.getColor().equals(Color.DARK_GRAY) ? "black" : "white");
    }

    public void setPossibleMoves(ArrayList<Posizione> allPossibleMoves) {
        this.allPossibleMoves.clear();
        if (allPossibleMoves != null && !allPossibleMoves.isEmpty()) {
            for (Posizione pos : allPossibleMoves) {
                System.out.println("Possible move: " + pos);
            }
            this.allPossibleMoves.addAll(allPossibleMoves);
        } else {
            System.out.println("No possible moves received.");
        }
    }
    
}