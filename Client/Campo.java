package Client;

import java.io.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import Server.Pedina;
import Server.Posizione;
import Server.Node;
import java.awt.*;
import javax.swing.*;

public class Campo {
    private final int MAX = 8;
    private Pedina[][] board;
    private JPanel[][] cells = new JPanel[MAX][MAX];
    private ArrayList<PedinaGrafica> allPedineGrafiche = new ArrayList<>();
    private PedinaGrafica pedinaCliccata = null; // Tiene traccia della pedina cliccata prima che deve essere spostata
    private PrintWriter out = null;
    private String myColor;
    private int turn = 1;
    private ArrayList<Square> squareList = new ArrayList<>(); // Lista con tutti i posti consigliati possibili
    private JFrame frame = null;
    private JButton patta = new JButton("Richiedi patta");
    private JButton resa = new JButton("Arrenditi");
    
    //Colori utilizzati -> ColorsDama
    private Boolean blocked = false; //Non farti cliccare mentre sta avvenendo un'animazione


    public Campo(Pedina[][] board, PrintWriter out, JFrame frame) {
        this.board = board;
        this.out = out;
        this.frame = frame;
    }

    public void drawBoard() {
        this.frame.getContentPane().removeAll();
        this.frame.getContentPane().setLayout(new BorderLayout());
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new GridLayout(1, 2));

        setUpButtons(patta, "patta");
        setUpButtons(resa, "resa");
    
        buttonsPanel.add(this.patta);
        buttonsPanel.add(this.resa);

        JPanel boardPanel = new JPanel(new GridLayout(MAX, MAX));
        boardPanel.setPreferredSize(new Dimension(480, 480));
        for (int i = 0; i < MAX; i++) {
            for (int j = 0; j < MAX; j++) {
                cells[i][j] = new JPanel();

                // Alterna i colori della scacchiera
                if ((i + j) % 2 == 0)
                    cells[i][j].setBackground(ColorsDama.CASELLA_CHIARA);
                else
                    cells[i][j].setBackground(ColorsDama.CASELLA_SCURA);
                
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
                    piece.setColor(board[i][j].getColor().equals("black") ? ColorsDama.PEDINA_NERA : ColorsDama.PEDINA_BIANCA);
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
                boardPanel.add(cells[i][j]);
            }
        }

        mainPanel.add(boardPanel, BorderLayout.CENTER);
        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);
    
        this.frame.setPreferredSize(new Dimension(600, 700));
        this.frame.add(mainPanel, BorderLayout.CENTER);
        this.frame.pack();
        this.frame.revalidate();
        this.frame.repaint();
        this.frame.setResizable(false);
        this.frame.setVisible(true);
    }

    public void findAllPath(Node tree,int x,int y,ArrayList<Node> currentPath,ArrayList<ArrayList<Node>> allPath) {
        if(tree == null) 
            return;
    
        // Crea una nuova copia del percorso corrente e aggiungi il nodo corrente
        ArrayList<Node> newPath = new ArrayList<>(currentPath);
        newPath.add(tree);


        //Se il nodo corrente è il target, salva il percorso
        if (tree.x == x && tree.y == y) {
            allPath.add(newPath);
        }
        else {
            //Esplora tutte le direzioni disponibili
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
        this.blocked = true;
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
        
        String normalColor = piece.getColor().equals(ColorsDama.PEDINA_NERA) ? "black" : "white";
        
        // Rimuovi la pedina originale
        cells[startPiece.y][startPiece.x].remove(piece);
        cells[startPiece.y][startPiece.x].revalidate();
        cells[startPiece.y][startPiece.x].repaint();

        allPedineGrafiche.remove(piece);
        //Ora faccio in modo di spostare il pezzo gradualmente 
        try{
            for(int i = 1;i<pathChosen.size()-1;++i){
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
            //L'ultimo lo faccio fuori
            Node nodo = pathChosen.get(pathChosen.size() - 1);

            final PedinaGrafica lastPiece = addPieceToBoard(nodo.x, nodo.y, piece.getColor(),normalColor,wasDama);

            Thread.sleep(50);
            // Dopo che lo aggiungo, rimuovo la pedina da mangiare
            if (nodo.pieceEaten != null){
                removePedina(getPedinaFromPosition( new Posizione(nodo.pieceEaten.getPosizione().getX(), nodo.pieceEaten.getPosizione().getY())));
            }
            
            // Aggiungi il listener alla ultima pedina creata
            if (this.myColor.equals(normalColor)) {
                lastPiece.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (!isMyTurn())
                            return;

                        removeSquares();
                        setPedinaCliccata(lastPiece);

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
            String color = piece.getColor().equals(ColorsDama.PEDINA_NERA) ? "black" : "white";
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

        this.blocked = false;
    }

    //Aggiunge una pedina alla checkerboard
    private PedinaGrafica addPieceToBoard(int x,int y,Color color,String normalColor,Boolean wasDama){
        Boolean dama = false;
        // Devo capire se devo upgradare il pezzo
        if (normalColor.equals(this.myColor) ) {
            // Se a muoversi è stato un mio pezzo, viene upgradato quando raggiunge lo zero
            if (y == 0)
                dama = true;
        }
        else if (y == MAX - 1) {
            dama = true;
        }

        if(!dama)
            dama = wasDama;

        PedinaGrafica newPiece = new PedinaGrafica(new Posizione(x, y),dama);

        allPedineGrafiche.add(newPiece);
        newPiece.setColor(normalColor.equals("black") ? ColorsDama.PEDINA_NERA : ColorsDama.PEDINA_BIANCA);
        newPiece.setOpacity(1.0f);

        // Aggiungi la nuova pedina alla cella di destinazione
        SwingUtilities.invokeLater(()->{
            cells[y][x].setLayout(new BorderLayout());
            cells[y][x].add(newPiece, BorderLayout.CENTER);
            cells[y][x].revalidate();
            cells[y][x].repaint();
        });
        return newPiece;
    }

    //Mostra graficamente i posti in cui puoi muoverti
    public void showSquares(int x, int y){
        Square div = new Square(x, y);

        squareList.add(div);
        SwingUtilities.invokeLater(()->{
            cells[y][x].setLayout(new BorderLayout());
            cells[y][x].add(div, BorderLayout.CENTER);
            cells[y][x].revalidate();
            cells[y][x].repaint(); 
        });
    }

    public void removeSquares(){
        for(Square square : squareList){
            cells[square.getY()][square.getX()].remove(square);
            cells[square.getY()][square.getX()].revalidate();
            cells[square.getY()][square.getX()].repaint(); 
        }
    }

    public void removePedina(PedinaGrafica piece) {
        allPedineGrafiche.remove(piece); // Aggiorno ArrayList 
        board[piece.getPosition().getY()][piece.getPosition().getX()] = null; // Aggiorno board
        SwingUtilities.invokeLater(()->{
            cells[piece.getPosition().getY()][piece.getPosition().getX()].remove(piece);
            cells[piece.getPosition().getY()][piece.getPosition().getX()].revalidate();
            cells[piece.getPosition().getY()][piece.getPosition().getX()].repaint();
        });
    }

    public void setUpButtons(JButton button, String message) {
        button.addActionListener(e -> {
            //Non puoi fare niente se non è il tuo turno
            if(!isMyTurn())
                return;
            out.println(message + "#request");
            //Se invio un messaggio
            blocked = true;
            button.setEnabled(false);
            if (message.equals("patta"))
                button.setText("Request sent...");
            else if (message.equals("resa"))
                button.setText("");
        });



        button.setBorderPainted(false);

        button.setFocusPainted(false);
        button.setBackground(Color.CYAN);
        button.setForeground(Color.BLACK);
        button.setPreferredSize(new Dimension(300, 75));

        //button.setBorder(new MatteBorder(0, 2, 0, 0, Color.RED)); // Top, Left, Bottom, Right


        button.setBorder(null);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if(!isMyTurn())
                    return;
                SwingUtilities.invokeLater(()->{
                    button.setBorderPainted(false);

                    button.setOpaque(true);
                    button.setForeground(Color.WHITE);

                    button.setBackground(Color.BLUE);
                    //button.setBackground(new Color(0, 0, 0, 175));
                    button.setCursor(new Cursor(Cursor.HAND_CURSOR));

                    button.repaint();
                    button.revalidate();
                });
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!isMyTurn())
                    return;
                SwingUtilities.invokeLater(()->{
                    button.setBorderPainted(false);

                    button.setOpaque(true);
                    button.setForeground(Color.BLACK);

                    button.setBackground(new Color(0, 255, 255, 255));
                    button.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));


                    button.repaint();
                    button.revalidate();
                });
            }
        });
    }

    public JButton getPattaButton(){
        return this.patta;
    }
    public void resetButton()  {
        this.patta.setText("Richiedi patta");
        this.patta.setEnabled(true);
        
        this.patta.setForeground(Color.BLACK);
        this.patta.setBackground(new Color(0, 255, 255, 255));
        this.patta.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        
        this.patta.repaint();
        this.patta.revalidate();
        //Ora puoi muoverti liberamente
        blocked = false;
    }

    
    public void changeTurn() {
        this.turn = (turn + 1) % 2;
    }

    public Boolean isMyTurn(){
        if(this.blocked)
            return false;
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