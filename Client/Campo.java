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
    private int clientNumber; // Come il turn in Game per identificare i client

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
                                System.out.println("Nuova pedina selezionata: " + piece);

                                for (PedinaGrafica p : allPedineGrafiche) {
                                    p.setOpacity(1.0f);
                                }
                                
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
                final int row = i;
                final int col = j;
                cells[i][j].addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        PedinaGrafica localPedinaCliccata = getPedinaCliccata();
                        
                        Posizione oldPosition = localPedinaCliccata.getPosition();
                        int oldRow = oldPosition.getY();
                        int oldCol = oldPosition.getX();
            
                        for (Posizione pos : localPedinaCliccata.getPawnPossibleMoves()) {
                            if (pos.getX() == col && pos.getY() == row) {
                                movePiece(oldRow, oldCol, row, col, clientNumber);
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

    public PedinaGrafica getPedinaFromPosition(Posizione position) {
        for (PedinaGrafica p : allPedineGrafiche) {
            if (p.getPosition().getX() == position.getX() && p.getPosition().getY() == position.getY()) {
                return p;
            }
        }

        System.out.println("Posizione da cercare e non trovata: " + position.getX() + "," + position.getY());
        return null;
    }

    // Coordinate sballate
    public void movePiece(int oldRow, int oldCol, int row, int col, int turn) {
        // Bisogna capire se é chiamato per spostare propria pedina o dell' avversario
        PedinaGrafica piece = getPedinaFromPosition(new Posizione(oldCol, oldRow));
    
        // Rimuovi pedina dalla cella precedente
        cells[oldRow][oldCol].remove(piece);
        cells[oldRow][oldCol].revalidate();
        cells[oldRow][oldCol].repaint();
    
        // Crea una nuova pedina grafica per la posizione di destinazione
        PedinaGrafica newPiece = new PedinaGrafica(new Posizione(col, row));
        newPiece.setColor(piece.getColor());
        newPiece.setOpacity(1.0f);
    
        // Rimuovi la vecchia pedina dall'ArrayList
        allPedineGrafiche.remove(piece);
    
        // Aggiungi il listener alla nuova pedina
        newPiece.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("Nuova pedina selezionata: " + newPiece);
                setPedinaCliccata(newPiece);
    
                newPiece.setOpacity(0.5f);
    
                if (newPiece != null) {
                    out.println("showPossibleMoves#" + newPiece);
                }
            }
        });
    
        // Aggiungi la nuova pedina all'ArrayList
        allPedineGrafiche.add(newPiece);
    
        // Aggiungi la nuova pedina alla cella di destinazione
        cells[row][col].setLayout(new BorderLayout());
        cells[row][col].add(newPiece, BorderLayout.CENTER);
        cells[row][col].revalidate();
        cells[row][col].repaint();
    
        // Aggiorna logicamente la scacchiera
        this.board[oldRow][oldCol] = null;
        this.board[row][col] = new Pedina(row, col, piece.getColor().equals(Color.DARK_GRAY) ? "black" : "white");

        // Deseleziona la pedina cliccata
        setPedinaCliccata(null);

        System.out.println("Coordinate da passare al server:" + oldCol + "," + oldRow + "#" + col + "," + row);
        // Comunica il movimento al server
        out.println("movePiece#" + oldCol + "," + oldRow + "#" + col + "," + row);
    }    

    public void setPedinaCliccata(PedinaGrafica piece) {
        this.pedinaCliccata = piece;
    }

    public PedinaGrafica getPedinaCliccata() {
        return this.pedinaCliccata;
    }
    
}