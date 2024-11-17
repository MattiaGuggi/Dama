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
                                setPedinaCliccata(piece);

                                if (piece != null) {
                                    piece.setOpacity(1.0f);
                                }

                                System.out.println("Nuova pedina selezionata: " + piece);
                                
                                piece.setOpacity(0.5f);
                                
                                if (piece != null) {
                                    out.println("showPossibleMoves#" + piece);
                                }
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
                        PedinaGrafica localPedinaCliccata = getPedinaCliccata();

                        System.out.println("Hai cliccato sulla casella: " + col + "," + row);
                        // Controllo che sia una posizione valida
                        if (localPedinaCliccata != null) {
                            for (Posizione pos : localPedinaCliccata.getPawnPossibleMoves()) {
                                if (pos.getX() == col && pos.getY() == row) {
                                    movePiece(row, col, localPedinaCliccata, position);
                                    setPedinaCliccata(null);
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
    public void movePiece(int row, int col, PedinaGrafica piece, Posizione position) {
        Posizione oldPosition = piece.getPosition();
        int oldRow = oldPosition.getY();
        int oldCol = oldPosition.getX();
        piece.setOpacity(1.0f);

        // Rimuovi pedina dalla cella attuale
        cells[oldRow][oldCol].remove(piece);
        cells[oldRow][oldCol].revalidate();
        cells[oldRow][oldCol].repaint();

        // Rimuovo pedina con posizione vecchia
        allPedineGrafiche.remove(piece);

        // Rimette pedina aggiornata in ArrayList
        PedinaGrafica newPiece = new PedinaGrafica(new Posizione(col, row));
        
        newPiece.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                setPedinaCliccata(newPiece);

                if (newPiece != null) {
                    newPiece.setOpacity(1.0f);
                }

                System.out.println("Nuova pedina selezionata: " + newPiece);
                
                newPiece.setOpacity(0.5f);
                
                if (newPiece != null) {
                    out.println("showPossibleMoves#" + newPiece);
                }
            }
        });

        allPedineGrafiche.add(newPiece);

        // Aggiungi una pedina alla cella cliccata
        cells[row][col].setLayout(new BorderLayout());
        cells[row][col].add(newPiece, BorderLayout.CENTER);
        cells[row][col].revalidate();
        cells[row][col].repaint();

        // Aggiorna logicamente scacchiera (lato server)
        this.board[oldRow][oldCol] = null;
        this.board[row][col] = new Pedina(row, col, piece.getColor().equals(Color.DARK_GRAY) ? "black" : "white");

        // Aggiornare a lato server
        out.println("movePiece#" + oldRow + "," + oldCol + "#" + col + "," + row);
    }

    public void setPedinaCliccata(PedinaGrafica piece) {
        this.pedinaCliccata = piece;
    }

    public PedinaGrafica getPedinaCliccata() {
        return this.pedinaCliccata;
    }
    
}