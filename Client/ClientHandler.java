package Client;

import java.net.*;
import java.io.*;
import java.util.ArrayList;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import Server.Node;
import Server.Pedina;


public class ClientHandler extends Thread {
    private Boolean partitaFinita = false;
    private final int MAX = 8;
    private final int dim = MAX * MAX * 10;
    private Campo campo = null;
    private JFrame frame = null;
    
    private Socket socket;
    BufferedReader in ;

    PrintWriter out;


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

            socket = new Socket(serverAddress,50000);
            System.out.println("Sono connesso al server: " + serverAddress);

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out = new PrintWriter(socket.getOutputStream(),true);
            
            this.frame.getContentPane().setBackground(Color.decode("#121212"));
            this.frame.setSize(this.dim, this.dim);
            this.frame.setLayout(null);
            this.frame.setTitle("Dama");
            this.frame.setResizable(false);
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
                    //button.setBackground(new Color(0, 0, 0, 175));

                    button.setBackground(Color.BLUE);

                    button.setForeground(Color.WHITE);
                    button.setCursor(new Cursor(Cursor.HAND_CURSOR));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    button.setBackground(new Color(0, 255, 255, 255));
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
            
            //Aggiungo l'evento che gestisce la chiusura della finestra
            frame.addWindowListener(new WindowAdapter() {
                // Gestisco cosa accade quanto chiudo la finestra
                public void windowClosing(WindowEvent e) {
                    if(campo != null && !campo.isMyTurn()){
                        return;
                    }
                    try{
                        if(campo != null)
                            out.println("leaveGame#"+campo.getColor());
                        else 
                            out.println("leaveGame");

                    }
                    catch(Exception exce){
                        exce.printStackTrace();
                    }

                    frame.dispose();
                    System.exit(0);

                }
            });


            this.frame.setVisible(true);
            while(!partitaFinita){
                
                String result = null;
                try{
                    result = in.readLine();
                }
                catch(Exception e){
                    partitaFinita = true;
                }
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

                            break;
                        case "updateBoard":
                            if (words.length > 0)
                                this.handleUpdateBoard(words);
                            break;
                        case "patta":
                            this.handlePatta(words);
                            break;
                        case "gameEnd":
                            this.handleGameEnd(words);
                            break;
                    }
                }
            }
            System.out.println("Partita finita!");
            //Partita finita!
            socket.close();
            in.close();
            out.close();

            //Chiudo la finestra
            frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));

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
        
        //Modifico il titolo
        this.frame.setTitle("Dama-" + messageSplitted[2]);

        //Settiamo il nostro colore
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

        String stringa = words[1];

        ArrayList<Node> path = Node.convertStringToArray(stringa,true,MAX);

        //In path adesso ho il percorso con le coordianate da seguire dalla pedina
        campo.movePiece(path, false);

    }

    //Gestice la fine della partita
    //winner|loser#reason
    public void handleGameEnd(String[] words){
        String state = words[1];
        String reason = words[2];
        partitaFinita = true;

        if(state.equals("winner"))
            JOptionPane.showMessageDialog(this.frame, "Hai vinto!!\n"+reason, "Fine della Partita (Vittoria)", JOptionPane.INFORMATION_MESSAGE);
        else if (state.equals("loser"))
            JOptionPane.showMessageDialog(this.frame, "Hai perso!!\n" + reason, "Fine della Partita (Sconfitta)",JOptionPane.WARNING_MESSAGE);
        else if (state.equals("patta"))
            JOptionPane.showMessageDialog(this.frame, "Pareggio!!", "Fine della Partita (Pareggio)", JOptionPane.INFORMATION_MESSAGE);
        System.exit(0);
    }

    public void handlePatta(String[] words) {
        String reason = words[1];

        if (reason.equals("request")) {

            JOptionPane optionPane = new JOptionPane(
                    "L'avversario richiede una patta. Accettare?",
                    JOptionPane.QUESTION_MESSAGE,
                    JOptionPane.YES_NO_OPTION);


            JDialog dialog = optionPane.createDialog(frame, "Patta");


            dialog.setSize(350, 150); // Imposta la dimensione del dialogo


            dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE); // Impedisce la chiusura automatica
            dialog.setVisible(true);

            //Ora faccio in modo che se in 5 secondi non rispondi, mandi automaticamente no!


            Integer response = (Integer)optionPane.getValue();

            if (response == JOptionPane.YES_OPTION) {
                out.println("patta#accept");
            }
            else if (response == JOptionPane.NO_OPTION) {
                out.println("patta#denied");
            }
        }
        else if (reason.equals("denied")) {
            JOptionPane.showMessageDialog(this.frame, "Patta rifiutata!!\n" + reason, "Richiesta di patta (Rifiutata)",JOptionPane.WARNING_MESSAGE);
            campo.resetButton();
        }
    }
}