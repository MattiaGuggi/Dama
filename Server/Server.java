package Server;

import java.io.*;
import java.util.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    //Serve per capire se un utente si è già connesso al server
    private static Boolean isConnected = false;
    private static Socket previousSocket = null;
    private static Socket newSocket = null;
    private static PrintWriter you = null;
    private static PrintWriter other = null;
    private static Game game = null;
    private static Pedina[][] board = null;
    public static void main(String[] args) {
        try{
            ServerSocket serverSocket = new ServerSocket(50000);
            System.out.println("In attessa di connesioni sulla porta 50000");

            newSocket = null;

            //Accetto una connessione
            // Se ne manca anche solo uno
            while(previousSocket == null || newSocket == null){
                //Se nessun socket si è ancora connesso
                if(!isConnected){
                    System.out.println("Aspetto il primo giocatore");
                    previousSocket = serverSocket.accept();
                    isConnected = true;
                }
                //Se un altro socket è gia connesso, posso iniziare la partita
                else{
                    System.out.println("Aspettiamo un altro giocatore");
                    newSocket = serverSocket.accept();
                }
            }
            
            BufferedReader in = new BufferedReader(new InputStreamReader(previousSocket.getInputStream()));
            BufferedReader in1 = new BufferedReader(new InputStreamReader(newSocket.getInputStream()));
            
            PrintWriter out = new PrintWriter(previousSocket.getOutputStream(),true);
            PrintWriter out1 = new PrintWriter(newSocket.getOutputStream(),true);

            //Posso iniziare il game
            startGame(in,in1,out,out1);
            
            //Se arrivo qui, vuol dire che il game è finito
            serverSocket.close();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void startGame(BufferedReader in,BufferedReader in1,PrintWriter out,PrintWriter out1){
        try{
            game = new Game();
            board = game.getBoard();

            String board1 = game.stringifyBoard(true);
            String board2 = game.stringifyBoard(false);
            
            // Passo al client cosa fare e la matrice di Pedine in formato comando#matrice#turno
            out.println("createGame#" + board1.toString() + "#black");
            out1.println("createGame#" + board2.toString() + "#white");

            //Adesso ho i due giocatori
            //Posso iniziare la partita
            Boolean endGame = false;

            while(!endGame){
                String result = ""; 
                int turn = game.getTurn();

                if (turn == 0) {
                    result = in.readLine();
                }
                else if (turn == 1) {
                    result = in1.readLine();
                }
                
                if(result != null){
                    String[] words = result.split("#");
                    
                    switch(words[0]) {
                        case "movePiece":
                            if(turn == 0)
                                manageMovePiece(words,out,out1,turn);
                            manageMovePiece(words, out1, out,turn);
                            break;
                        case "showPossibleMoves":
                            if(turn == 0)
                                manageShowPossibleMoves(words,out);
                            else
                                manageShowPossibleMoves(words, out1);
                            break;
                    }
                }
                endGame = game.checkWin();
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally{
            System.out.println("Fine partita!!");
        }
    }

    static public void manageMovePiece(String[] messageFromClient,PrintWriter you,PrintWriter other,int turn){
        Posizione startPosition = getPositionFromString(messageFromClient[1]);
        Posizione endPosition = getPositionFromString(messageFromClient[2]);

        int startX = startPosition.getX();
        int startY = startPosition.getY();
        int endX = endPosition.getX();
        int endY = endPosition.getY();

        board[endY][endX] = board[startY][startX]; // Sposta la pedina
        board[startY][startX] = null;             // Rimuove la pedina dalla posizione precedente

        // other.println("updateBoard#" + startPosition + "#" + endPosition);

        // Da controllare cosa succese alla board

        // Cambia turno
        // game.changeTurn((turn+1)%2);

        // Stampa le conseguenze della mossa
        you.println("pieceMoved#");
    }


    static public void manageShowPossibleMoves(String[] messageFromClient, PrintWriter out){
        Posizione posizione = getPositionFromString(messageFromClient[1]);

        System.out.println("Posizione arrivata al server: " + posizione.toString());

        int y = posizione.getY();
        int x = posizione.getX();
        String msg = "";

        // Controllare se dobbiam ore
        ArrayList<Posizione> allPossibleMoves = board[y][x].getPossibleMoves(board); // Cerchiamo mosse possibili

        // Reverso coordinate
        for (Posizione pos : allPossibleMoves) {
            msg += pos.getY() + "," + pos.getX() + ";";
        }

        if (msg.length() > 0) {
            msg = msg.substring(0, msg.length() - 1);
            out.println("showPossibleMoves#" + msg);
        }
        else
            out.print("showPossibleMoves#");
    }

    //Ritorna la posizone a partire da una stringa
    static public Posizione getPositionFromString(String message){
        String coppiaDati = message;
        String[] split = coppiaDati.split(",");
        int x = Integer.parseInt(split[0]);
        int y = Integer.parseInt(split[1]);
        if (game.getTurn() == 0) {
            // Reverse le coordinate perchè:
            // turn = 0 -> Vuol dire che è turno del nero
            // Se è nero, la matrice lato client ha il nero sotto. Pero dal server il nero è sopra
            // Per questo devo reversare
            x = game.getMax() - x - 1;
            y = game.getMax() - y - 1;
        }
        return new Posizione(x, y);
    }
}