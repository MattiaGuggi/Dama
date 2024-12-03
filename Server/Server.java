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
            String board2 = game.stringifyBoard(false);Boolean player1Ready = false, player2Ready = false, endGame = false;

            // Aspetta che entrambi mandano request di giocare
            while (!player1Ready || !player2Ready) {
                if (!player1Ready) {
                    String result = in.readLine();
                    if (result.equals("searchGame")) {
                        player1Ready = true;
                        System.out.println("Player 1 is ready!");
                    }
                }

                if (!player2Ready) {
                    String result = in1.readLine();
                    if (result.equals("searchGame")) {
                        player2Ready = true;
                        System.out.println("Player 2 is ready!");
                    }
                }
            }

            out.println("createGame#" + board1 + "#black");
            out1.println("createGame#" + board2 + "#white");

            //Adesso ho i due giocatori
            //Posso iniziare la partita
            while(!endGame){
                PrintWriter you = out;
                PrintWriter other = out1;
                String result = ""; 
                String playerColor = "black";
                int turn = game.getTurn();

                if (turn == 0)
                    result = in.readLine();
                else if (turn == 1) {
                    result = in1.readLine();
                    you = out1;
                    other = out;
                    playerColor = "white";
                }
                
                if(result != null){
                    String[] words = result.split("#");

                    System.out.println("Words: "+result);
                    switch(words[0]) {
                        case "movePiece":
                                manageMovePiece(words,you,other,turn,playerColor);
                            break;
                        case "showPossibleMoves":
                                manageShowPossibleMoves(words,you,playerColor);
                            break;
                        case "resa":
                            manageResa(words,you,other,playerColor);
                            endGame = true; // Finisco il gioco
                            //Lascio il tempo ai client di ricevere il messaggio
                            Thread.sleep(2000);
                            break;
                        case "patta":
                            managePatta(words,you,other,playerColor);
                            game.changeTurn(); // Cosi l'altro client puó rispondere (ho barato)
                            break;
                        case "leaveGame":
                            //Chi ha perso
                            String color = words[1];
                            if (color.equals("white")) {
                                out.println("gameEnd#winner#" + "L'avversario si è disconnesso!");
                            } else {
                                out1.println("gameEnd#winner#" + "L'avversario si è disconnesso!");
                            }
                            endGame = true;
                            break;
                        
                    }
                    //Cerchiamo di capire se la partita è finita
                    String[] gameEnd = game.checkFinishGame();
                    String winner = gameEnd[1];
                    if(gameEnd[0].equals("true")){
                        //Se è finita devo comunicare ai client l'esito
                        //Ricordando che out = black; out1 = white
                        String reason = gameEnd[2];

                        if(winner.equals("white")){
                            out.println("gameEnd#loser#"+reason);
                            out1.println("gameEnd#winner#"+reason);
                        }
                        else{
                            out.println("gameEnd#winner#" + reason);
                            out1.println("gameEnd#loser#" + reason);
                        }
                        //Lascio il tempo ai client di ricevere il messaggio
                        Thread.sleep(2000);
                        endGame = true;

                    }
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        finally{
            System.out.println("Fine partita!!");
        }
    }
  
    //MessageFromClient conterrà la path che il pezzo ha seguito nel muoversi
    static public void manageMovePiece(String[] messageFromClient,PrintWriter you,PrintWriter other,int turn,String pColor){
        String msg = messageFromClient[1];
        //Ricavo il percorso che la mia pedina deve seguire
        ArrayList<Node> path = Node.convertStringToArray(msg,game.getTurn() == 0,game.getMax());

        Node firstElement = path.get(0);
        Node lastElement  = path.get(path.size()-1);

        String color = board[firstElement.y][firstElement.x].getColor();
        Boolean wasDama = board[firstElement.y][firstElement.x].getIsDama();
        board[lastElement.y][lastElement.x] = new Pedina(lastElement.x,lastElement.y,color);
        board[firstElement.y][firstElement.x] = null; // Rimuove la pedina dalla posizione precedente


        //Controllo se la mia mossa ha causato mangiate
        for(int i = 1;i<path.size();i++){
            if(path.get(i).pieceEaten != null){
                //Ricorda di reversare le coordinate
                int x = path.get(i).pieceEaten.getPosizione().getX();
                int y = path.get(i).pieceEaten.getPosizione().getY();

                //Tolgo i pezzi dalla matrice
                board[y][x] = null;
            }
        }

        //Controllo se qualcuno è diventato una dama
        if (color.equals("white") && lastElement.y == 0 || wasDama) {
            board[lastElement.y][lastElement.x].setIsDama();
        }
        if (color.equals("black") && lastElement.y == game.getMax() - 1 || wasDama) {
            board[lastElement.y][lastElement.x].setIsDama();
        }


        System.out.println("Scacchiera aggiornata lato server (unica):");
        for (int i=0 ; i<8 ; i++) {
            for (int j=0 ; j<8 ; j++) {
                if (board[i][j] == null)
                    System.out.print("---- ");
                else
                    System.out.print(board[i][j] + " "+board[i][j].getIsDama());
            }
            System.out.println("");
        }

        // Cambia turno
        game.changeTurn();
        // Notifica l'altro client di spostare anche nella sua board
        //L'altro è sempre reversato rispetto a te
        other.println("updateBoard#" + msg);

    }

    static public void manageShowPossibleMoves(String[] messageFromClient, PrintWriter out, String color){
        Posizione posizione = getPositionFromString(messageFromClient[1]);
        int y = posizione.getY();
        int x = posizione.getX();

        // Controllare se dobbiam ore
        Node allPossibleMoves = board[y][x].getPossibleMoves(board); // Cerchiamo mosse possibili


     
        //Se è una pedina nera, devo reversare le coordinate
        if(color.equals("black"))
            reverseCoordinates(allPossibleMoves,game);
        
        //Ora che ho le coordinate reversate, devo convertire il messaggio in stringa così che il client puo riceverlo
        String msg = Node.convertTreeToString(allPossibleMoves);
        
        if (msg.length() > 0) {
            out.println("showPossibleMoves#" + msg);
        }
    }

    static public void manageResa(String[] messageFromClient, PrintWriter you, PrintWriter other, String color) {
        String pColor = messageFromClient[1];

        // Se matchano i colori allora ha richiesto la resa e quindi perso
        if(!pColor.equals(color)){
            you.println("gameEnd#loser#Ti sei arreso");
            other.println("gameEnd#winner#L' avversario si é arreso");
        }
        else{
            you.println("gameEnd#winner#L' avversario si é arreso");
            other.println("gameEnd#loser#Ti sei arreso");
        }
    }

    static public void managePatta(String[] messageFromClient, PrintWriter you, PrintWriter other, String color) {
        String reason = messageFromClient[1];

        // Se un client vuole fare patta
        if (reason.equals("request"))
            other.println("patta#request");
        else if (reason.equals("accept")){
            you.println("gameEnd#patta#Pareggio!");
            other.println("gameEnd#patta#Pareggio!");
        }
        else if (reason.equals("denied")) {
            other.println("patta#denied");
        }
    }
   
    static public void manageLeaveGame(String message){
        
    }



    static public void reverseCoordinates(Node root,Game game){
        
        if(root != null){
            //Inverto le coordinate
            root.x =  game.getMax() - 1 - root.x;
            root.y = game.getMax() - 1 - root.y;

            if(root.pieceEaten != null){
                root.pieceEaten.getPosizione().setX(game.getMax() - 1 - root.pieceEaten.getPosizione().getX() );
                root.pieceEaten.getPosizione().setY(game.getMax() - 1 - root.pieceEaten.getPosizione().getY() );
            }

            reverseCoordinates(root.dl, game);
            reverseCoordinates(root.dr, game);
            reverseCoordinates(root.ul, game);
            reverseCoordinates(root.ur, game);
            
        }
    }


    //Ritorna la posizone a partire da una stringa
    static public Posizione getPositionFromString(String message){
        String coppiaDati = message;
        String[] split = coppiaDati.split("-");
        int x = Integer.parseInt(split[0]);
        int y = Integer.parseInt(split[1]);
        if (game.getTurn() == 0) {
            // Reverse le coordinate perchè:
            // turn = 0 -> Vuol dire che è turno del nero
            // Se è nero, la matrice lato client ha il bianco sotto. Pero dal server il bianco è sopra
            // Per questo devo reversare
            x = game.getMax() - x - 1;
            y = game.getMax() - y - 1;
        }
        return new Posizione(x, y);
    }
}