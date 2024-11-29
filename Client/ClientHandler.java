package Client;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JFrame;

import Server.Node;
import Server.Pedina;
import java.awt.*;
import java.awt.event.*;


public class ClientHandler extends Thread {
    private Boolean partitaFinita = false;
    private final int MAX = 8;
    private final int dim = MAX * MAX * 10;
    private Campo campo = null;
    private JFrame frame = null;
    private JButton button = new JButton("Search Game!") {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
            // Background
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
        
            // Draw text
            g2.setColor(getForeground());
            FontMetrics fm = g2.getFontMetrics();
            int stringWidth = fm.stringWidth(getText());
            int stringHeight = fm.getAscent();
            g2.drawString(getText(), (getWidth() - stringWidth) / 2, (getHeight() + stringHeight) / 2 - 2);
        
            g2.dispose();
        }
        
    };

    public ClientHandler(JFrame frame) {
        this.frame = frame;
    }

    @Override
    public void run(){
        try{
            InetAddress serverAddress = InetAddress.getByName("localhost");
            //Connessione al server

            Socket socket = new Socket(serverAddress,50000);
            System.out.println("Sono connesso al server: " + serverAddress);

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            PrintWriter out = new PrintWriter(socket.getOutputStream(),true);
            
            this.frame.getContentPane().setBackground(Color.decode("#121212"));
            this.frame.setSize(this.dim, this.dim);
            this.frame.setLayout(null);

            this.button.setBackground(Color.CYAN);
            this.button.setForeground(Color.BLACK);
            this.button.setBounds((this.dim - 300) / 2, (this.dim - 75) / 2, 300, 75);
            this.button.setBorder(null);

            this.frame.add(this.button);

            // Stili
            this.button.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    button.setOpaque(true);
                    button.setBackground(new Color(0, 0, 0, 175)); // Set translucent black background (alpha = 50/255)
                    button.setForeground(Color.WHITE);
                    button.setCursor(new Cursor(Cursor.HAND_CURSOR));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    button.setBackground(new Color(0, 255, 255, 255)); // Restore original opaque cyan color
                    button.setForeground(Color.BLACK);
                    button.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }
            });

            // Manda messaggio al server
            this.button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    out.println("searchGame");
                    button.setEnabled(false);
                    button.setText("Searching...");
                }
            });
            
            this.frame.setResizable(false);
            this.frame.setVisible(true);

            while(!partitaFinita){
                String result = in.readLine();

                //Formato generale: NomeComando#Data1#Data2#Data3....
                if (result != null) {
                    // Devo decodificare la stringa che ho appena ricevuto
                
                    String[] words = result.split("#");

                    switch(words[0]) {
                        case "createGame":
                            this.createGame(result, out);
                            break;
                        case "showPossibleMoves":
                            if (words.length > 0)
                                this.handlePossibleMoves(words[1]);
                            else
                                System.out.println("Non ci sono mosse possibili");

                            break;
                        case "updateBoard":
                            if (words.length > 0)
                                this.handleUpdateBoard(words);
                            break;
                        case "mangiata":
                            break;
                        case "isDama":
                            break;
                        case "win":
                            break;
                    }
                }
            }
            
            socket.close();
            
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public void createGame (String result, PrintWriter out) {
        System.out.println("La partita sta per iniziare!");

        String[] messageSplitted = result.split("#");
        
        String boardData = messageSplitted[1];  // Prendo i dati relativi alla posizione dei pezzi nella scacchiera
        // boardData : "colore,x,y;colore,x,y;"
        String[] pedineData = boardData.split(";");

        Pedina[][] board = new Pedina[MAX][MAX];

        for (String pedinaStr : pedineData) {
            // Recupero valori passati dal server (posizione di ogni pedina)
            String[] split = pedinaStr.split("-");
            String colore = split[0];
            int x = Integer.parseInt(split[1]);
            int y = Integer.parseInt(split[2]);
            
            Pedina pedina = new Pedina(x, y, colore);
            board[y][x] = pedina;
        }
        
        // Togli il pulsante
        this.frame.getContentPane().removeAll();
        this.frame.repaint();

        // Crea un campo grafico basato sul campo su lato server
        this.campo = new Campo(board, out, this.frame);
        
        //Settiamo il nostro colore
        System.out.println("Il tuo colore + "+messageSplitted[2]);
        this.campo.setColor(messageSplitted[2]);
        this.campo.drawBoard();
    }

    //words (x#y#null) (ul: (x#y#null) ur: (x#y#null) .... )
    public void handlePossibleMoves(String words) {
        
        Node tree = Node.convertStringToTree(words);
        //La testa del tree corrisponde alla pedina stessa e va quindi tolta
        campo.removeSquares();
        
        iterateTree(tree.dl);
        iterateTree(tree.dr);
        iterateTree(tree.ul);
        iterateTree(tree.ur);


        PedinaGrafica piece = this.campo.getPedinaCliccata();
        // Setto le nuove mosse possibili di una pedina
        if (tree.dl != null || tree.dr != null || tree.ul != null || tree.ur != null) {
            piece.setPawnPossibleMoves(tree);
        }
    }

    public void iterateTree(Node tree){
        if(tree != null){
            int x = tree.x;
            int y = tree.y;

            iterateTree(tree.dl);
            iterateTree(tree.dr);
            iterateTree(tree.ul);
            iterateTree(tree.ur);
            campo.showSquares(x, y);
        }
    }

    //words[1] contiene il path che la mia pedina deve seguire
    //Dato che il percorso mi è stato inviato dall'altro client, devo sicuramente invertire le coordinate
    //formato: [x;y;pieceEaten,x2;y2;pieceEaten2]
    //La virgola divide gli elementi del vettore
    //pieceEaten è nel formato: colorx-y 
    public void handleUpdateBoard(String[] words) {
        System.out.println("Messaggio ricevuto: "+words[1]);

        String stringa = words[1];

        ArrayList<Node> path = Node.convertStringToArray(stringa,true,MAX);

        //In path adesso ho il percorso con le coordianate da seguire dalla pedina

        campo.movePiece(path, false);

    }
}
