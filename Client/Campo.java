package Client;

import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import Server.Pedina;
import Server.Posizione;
import Server.Node;

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
                    PedinaGrafica piece = new PedinaGrafica(new Posizione(j, i),false);

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
                        new SwingWorker<Void,Void>() {
                            protected Void doInBackground() throws Exception{
                                PedinaGrafica localPedinaCliccata = getPedinaCliccata();
                                if (localPedinaCliccata == null || !isMyTurn())
                                    return null;

                                removeSquares();

                                for (PedinaGrafica p : allPedineGrafiche) {
                                    p.setOpacity(1.0f);
                                }

                                Node node = localPedinaCliccata.getPawnPossibleMoves();

                                ArrayList<ArrayList<Node>> allPath = new ArrayList<ArrayList<Node>>();

                                findAllPath(node, col, row, new ArrayList<Node>(), allPath);

                                int max = -1;
                                ArrayList<Node> pathChosen = new ArrayList<>();
                                for (int i = 0; i < allPath.size(); ++i) {
                                    if (allPath.size() > max) {
                                        max = allPath.size();
                                        pathChosen = allPath.get(i);
                                    }
                                }

                                if (allPath.size() > 0)
                                    movePiece(pathChosen, true);
                                return null;
                            }

                        }.execute();
                        
                    }
                });
                

                frame.add(cells[i][j]);
            }
        }

        frame.pack();
        frame.setResizable(false);
        frame.setVisible(true);
    }


    public void findAllPath(Node tree,int x,int y,ArrayList<Node> currentPath,ArrayList<ArrayList<Node>> allPath) {
        //If tree is null
        if(tree == null) 
            return;
    
        // Crea una nuova copia del percorso corrente e aggiungi il nodo corrente

        ArrayList<Node> newPath = new ArrayList<>(currentPath);
        newPath.add(tree);


        // Se il nodo corrente è il target, salva il percorso
        if (tree.x == x && tree.y == y) {
            allPath.add(newPath);
        } else {
            // Esplora tutte le direzioni disponibili
            findAllPath(tree.dl, x,y, newPath, allPath);
            findAllPath(tree.dr, x,y, newPath, allPath);
            findAllPath(tree.ul, x,y, newPath, allPath);
            findAllPath(tree.ur, x,y, newPath, allPath);
        }
    }


    public PedinaGrafica getPedinaFromPosition(Posizione position) {
        for (PedinaGrafica p : allPedineGrafiche) {
            if (p.getPosition().getX() == position.getX() && p.getPosition().getY() == position.getY()) {
                return p;
            }
        }

        return null;
    }

    //Muove un pezzo nel campo, seguendo un movimento
    public void movePiece(ArrayList<Node> pathChosen,Boolean callServer) {

        // Comunica il movimento al server
        if (callServer)
            out.println("movePiece#" + pathChosen);

        //Di base quando clicco rimuovo gli altri suggerimenti presenti nel campo
        this.removeSquares();

        //Il primo elemento nel mio vettore, è la pedina di partenza

        Node startPiece = pathChosen.get(0);


        //Ora devo ricavare la pedina grafica da muovere

        PedinaGrafica piece = getPedinaFromPosition(new Posizione(startPiece.x, startPiece.y));
        Boolean wasDama = piece.getIsDama();
        
        String normalColor = piece.getColor().equals(Color.DARK_GRAY) ? "black" : "white";
        
        // Rimuovi la pedina originale
        cells[startPiece.y][startPiece.x].remove(piece);
        cells[startPiece.y][startPiece.x].revalidate();
        cells[startPiece.y][startPiece.x].repaint();

        allPedineGrafiche.remove(piece);
        //Ora faccio in modo di spostare il pezzo gradualmente 
        try{
            for(int i = 1;i<pathChosen.size()-1;++i){
                System.out.println("Dentro il ciclo!!");
                Node nodo = pathChosen.get(i);


                addPieceToBoard(nodo.x,nodo.y,piece.getColor(),normalColor, wasDama);

                Thread.sleep(40);
                //Dopo che lo aggiungo, rimuovo la pedina da mangiare
                if(nodo.pieceEaten != null)
                    removePedina(getPedinaFromPosition(new Posizione(nodo.pieceEaten.getPosizione().getX(), nodo.pieceEaten.getPosizione().getY())));
                Thread.sleep(500);
                //Ora rimuovo la pedina che avevo aggiunto

                removePedina(getPedinaFromPosition(new Posizione(nodo.x, nodo.y)));

            }
            System.out.println("Fine animazioni");
            //L'ultimo lo faccio fuori
            Node nodo = pathChosen.get(pathChosen.size() - 1);

            final PedinaGrafica lastPiece = addPieceToBoard(nodo.x, nodo.y, piece.getColor(),normalColor,wasDama);

            System.out.println("lastPiece:"+lastPiece.getPosition().getX()+"--"+lastPiece.getPosition().getY());
            Thread.sleep(50);
            // Dopo che lo aggiungo, rimuovo la pedina da mangiare
            if (nodo.pieceEaten != null){
                System.out.println("Devi mangiare: "+nodo.pieceEaten);
                removePedina(getPedinaFromPosition( new Posizione(nodo.pieceEaten.getPosizione().getX(), nodo.pieceEaten.getPosizione().getY())));
            }
            
            // Aggiungi il listener alla ultima pedina creata
            if (this.myColor.equals(normalColor)) {
                lastPiece.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (!isMyTurn())
                            return;
                        
                        setPedinaCliccata(lastPiece);
                        System.out.println("Nuova pedina selezionata: " + lastPiece);

                        for (PedinaGrafica p : allPedineGrafiche) {
                            p.setOpacity(1.0f);
                        }
            
                        lastPiece.setOpacity(0.5f);
            
                        if (lastPiece != null) {
                            out.println("showPossibleMoves#" + lastPiece);
                        }
                    }
                });
            }
            //Aggiungi la nuova pedina all'ArrayList
            
            
            String color = piece.getColor().equals(Color.DARK_GRAY) ? "black" : "white";
            // Aggiorna logicamente la scacchiera
            this.board[startPiece.y][startPiece.x] = null;
            this.board[lastPiece.getPosition().getY()][lastPiece.getPosition().getX()] = new Pedina(lastPiece
                    .getPosition().getX(), lastPiece.getPosition().getY(),color);
        

        
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    }


    //Aggiunge una pedina alla checkerboard
    private PedinaGrafica addPieceToBoard(int x,int y,Color color,String normalColor,Boolean wasDama){

        Boolean dama = false;
        // Devo capire se devo upgradare il pezzo
        if (normalColor.equals(this.myColor) ) {
            // Se a muoversi è stato un mio pezzo, viene upgradato quando raggiunge lo zero
            if (y == 0)
                dama = true;

        } else if (y == MAX - 1) {
            dama = true;
        }

        if(!dama)
            dama = wasDama;



        PedinaGrafica newPiece = new PedinaGrafica(new Posizione(x, y),dama);

        allPedineGrafiche.add(newPiece);
        newPiece.setColor(normalColor.equals("black") ? Color.DARK_GRAY : Color.LIGHT_GRAY);
        newPiece.setOpacity(1.0f);

        // Aggiungi la nuova pedina alla cella di destinazione
        cells[y][x].setLayout(new BorderLayout());
        cells[y][x].add(newPiece, BorderLayout.CENTER);
        cells[y][x].revalidate();
        cells[y][x].repaint();

        return newPiece;
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
        allPedineGrafiche.remove(piece); // Aggiorno ArrayList {
        board[piece.getPosition().getY()][piece.getPosition().getX()] = null; // Aggiorno board
        cells[piece.getPosition().getY()][piece.getPosition().getX()].remove(piece);
        cells[piece.getPosition().getY()][piece.getPosition().getX()].revalidate();
        cells[piece.getPosition().getY()][piece.getPosition().getX()].repaint();
        System.out.println("Pedina eliminata!!!");
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