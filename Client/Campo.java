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
    private PrintWriter out = null;

    private String myColor;

    private int turn = 1;

    //Lista con tutti i posti consigliati possibili
    private ArrayList<Square> squareList = new ArrayList<>();
    
    public Campo(Pedina[][] board, PrintWriter out) {
        this.board = board;
        this.out = out;
    }

    public void drawBoard() {
        JFrame frame = new JFrame("Dama-"+this.myColor);

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
                    if (this.myColor.equals(board[i][j].getColor())) {
                        piece.addMouseListener(new MouseAdapter() {
                            @Override
                            public void mouseClicked(MouseEvent e) {
                                //Se quando clicco non è il mio turno, non faccio niente
                                if(!isMyTurn())
                                    return;
                                removeSquares();
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
                        if(localPedinaCliccata == null || !isMyTurn())
                            return;
                        
                        removeSquares();
                        
                        for (PedinaGrafica p : allPedineGrafiche) {
                            p.setOpacity(1.0f);
                        }

                        Posizione oldPosition = localPedinaCliccata.getPosition();
                        int oldRow = oldPosition.getY();
                        int oldCol = oldPosition.getX();

                        for (Posizione pos : localPedinaCliccata.getPawnPossibleMoves()) {
                            if (pos.getX() == col && pos.getY() == row) {
                                    movePiece(oldRow, oldCol, row, col,true);
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

        return null;
    }

    public void movePiece(int oldRow, int oldCol, int row, int col,Boolean callServer) {
        //Quando clicco 
        this.removeSquares();
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
                if (!isMyTurn())
                    return;
                
                setPedinaCliccata(newPiece);

                System.out.println("Nuova pedina selezionata: " + newPiece);

                for (PedinaGrafica p : allPedineGrafiche) {
                    p.setOpacity(1.0f);
                }
    
                newPiece.setOpacity(0.5f);
    
                if (newPiece != null) {
                    out.println("showPossibleMoves#" + newPiece);
                }
            }
        });
    
        // Aggiungi la nuova pedina all'ArrayList
        allPedineGrafiche.add(newPiece);
    
        // Aggiorna logicamente la scacchiera
        this.board[oldRow][oldCol] = null;
        this.board[row][col] = new Pedina(col, row, piece.getColor().equals(Color.DARK_GRAY) ? "black" : "white");

        // Mettere nuova pedina Dama se é della prima riga
        if (row == 0) {
            this.board[row][col].setIsDama();
            newPiece.setIsDama();
        }

        // Aggiungi la nuova pedina alla cella di destinazione
        cells[row][col].setLayout(new BorderLayout());
        cells[row][col].add(newPiece, BorderLayout.CENTER);
        cells[row][col].revalidate();
        cells[row][col].repaint();

        System.out.println("Campo attuale in Campo:");
        for (int i=0 ; i<8 ; i++) {
            for (int j=0 ; j<8 ; j++) {
                if (this.board[i][j] == null)
                    System.out.print("---- ");
                else
                    System.out.print(this.board[i][j] + " ");
            }
            System.out.println("");
        }

        //Modifico il mio turno, cosi non posso piu muovere
        this.changeTurn();

        // Deseleziona la pedina cliccata
        setPedinaCliccata(null);

        // Comunica il movimento al server
        if(callServer)
            out.println("movePiece#" + oldCol + "," + oldRow + "#" + col + "," + row);
    }

    //Mostra graficamente i posti in cui puoi muoverti
    public void showSquares(int x, int y){
        Square div = new Square(x, y);

        squareList.add(div);

        cells[y][x].setLayout(new BorderLayout());
        cells[y][x].add(div, BorderLayout.CENTER);
        cells[y][x].revalidate();
        cells[y][x].repaint(); 
    }

    public void removeSquares(){
        for(Square square : squareList){
            cells[square.getY()][square.getX()].remove(square);
            cells[square.getY()][square.getX()].revalidate();
            cells[square.getY()][square.getX()].repaint(); 
        }
    }

    public void removePedina(PedinaGrafica piece) {
        System.out.println("Pedina mangiata, " + piece);
        allPedineGrafiche.remove(piece); // Aggiorno ArrayList
        board[piece.getPosition().getY()][piece.getPosition().getX()] = null; // Aggiorno board
        cells[piece.getPosition().getY()][piece.getPosition().getX()].remove(piece);
        cells[piece.getPosition().getY()][piece.getPosition().getX()].revalidate();
        cells[piece.getPosition().getY()][piece.getPosition().getX()].repaint();
    }
    
    public void changeTurn() {
        this.turn = (turn + 1) % 2;
    }

    public Boolean isMyTurn(){
        if(this.turn == 0 && this.myColor.equals("black"))
            return true;
        if(this.turn == 1 && this.myColor.equals("white"))
            return true;
        return false;
    }
    
    public void setColor(String color){
        this.myColor = color;
    }
    public String getColor(){
        return this.myColor;
    }
    public void setPedinaCliccata(PedinaGrafica piece) {
        this.pedinaCliccata = piece;
    }

    public PedinaGrafica getPedinaCliccata() {
        return this.pedinaCliccata;
    }
}
