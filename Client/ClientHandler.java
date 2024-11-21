package Client;

import java.net.*;
import java.io.*;
import Server.Pedina;
import Server.Posizione;
import java.util.ArrayList;
import java.util.Arrays;

public class ClientHandler extends Thread {
    private Boolean partitaFinita = false;
    private final int MAX = 8;
    private Campo campo = null;

    @Override
    public void run(){
        try{
            InetAddress serverAddress = InetAddress.getByName("localhost");
            //Connessione al server

            Socket socket = new Socket(serverAddress,50000);
            System.out.println("Sono connesso al server: "+serverAddress);

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            PrintWriter out = new PrintWriter(socket.getOutputStream(),true);

            while(!partitaFinita){
                String result = in.readLine();

                //Formato generale: NomeComando#Data1#Data2#Data3....
                if (result != null) {
                    // Devo decodificare la stringa che ho appena ricevuto
                    System.out.println("Result: " + result);
                
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
                        case "pieceMoved":
                            break;
                        case "updateBoard":
                            if (words.length > 0 )
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
            String[] split = pedinaStr.split(",");
            String colore = split[0];
            int x = Integer.parseInt(split[1]);
            int y = Integer.parseInt(split[2]);
            
            Pedina pedina = new Pedina(x, y, colore);
            board[y][x] = pedina;
        }

        // Crea un campo grafico basato sul campo su lato server
        this.campo = new Campo(board, out);
        
        //Settiamo il nostro colore
        System.out.println("Il tuo colore + "+messageSplitted[2]);
        this.campo.setColor(messageSplitted[2]);

        this.campo.drawBoard();

    }

    public void handlePossibleMoves(String words) {
        String[] coppiaDati = words.split(";");
        PedinaGrafica piece = this.campo.getPedinaCliccata();
        
        // Prendo tutte le posizioni possibili
        ArrayList<Posizione> allPossibleMoves = new ArrayList<>();
        //Rimuovo i possibili posti dove andare
        campo.removeSquares();
        for (String coppia : coppiaDati) {
            String[] coords = coppia.split(",");
            if (coords.length == 2) {
                int x = Integer.parseInt(coords[0]);
                int y = Integer.parseInt(coords[1]);
                Posizione pos = new Posizione(x, y);

                allPossibleMoves.add(pos);
    
                System.out.println("Mosse possibili: " + pos);
                campo.showSquares(x, y);
            }
        }

        // Setto le nuove mosse possibili di una pedina
        if (!allPossibleMoves.isEmpty()) {
            piece.setPawnPossibleMoves(allPossibleMoves);
        }
    }

    public void handleUpdateBoard(String[] words) {
        System.out.println("DEVO UPDTAR LA FOWFDASDASDAS->"+Arrays.toString(words));
        Posizione startPosition = getPositionFromString(words[1]);
        Posizione endPosition = getPositionFromString(words[2]);

        int startX = startPosition.getX();
        int startY = startPosition.getY();
        int endX = endPosition.getX();
        int endY = endPosition.getY();

        // Non sposta la pedina ma mette solo a null nel campo dell' avversario
        campo.movePiece(startY, startX, endY, endX, false);
    }
    //Ritorna la posizone a partire da una stringa
    public Posizione getPositionFromString(String message){
        String coppiaDati = message;
        String[] split = coppiaDati.split(",");
        int x = Integer.parseInt(split[0]);
        int y = Integer.parseInt(split[1]);

        if(this.campo.getColor().equals("black")){
            x = MAX - x - 1;
            y = MAX - y - 1;
        }
        
        return new Posizione(x, y);
    }
}