package Server;

import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    //Serve per capire se un utente si è già connesso al server
    private static Boolean isConnected = false;
    private static Socket previousSocket = null;
    private static Socket newSocket = null;
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
            
            PrintWriter out = new PrintWriter(previousSocket.getOutputStream(),true);
            PrintWriter out1 = new PrintWriter(newSocket.getOutputStream(),true);

            Game game = new Game();
            Pedina[][] board = game.getBoard();

            // Conversione array in stringa di formato colore#x#y
            StringBuilder boardData = new StringBuilder();
            for (int i=0 ; i<board.length ; i++) {
                for (int j=0 ; j<board[i].length ; j++) {
                    if (board[i][j] != null) {
                        boardData.append(board[i][j].toString()).append("#");
                    }
                }
            }

            // Rimuovi l'ultimo separatore
            if (boardData.length() > 0) {
                boardData.setLength(boardData.length() - 1);
            }
            
            // Da concatenare con # e la board da passare in qualche modo
            out.println("createGame#" + boardData.toString());
            out1.println("createGame#" + boardData.toString());
            //Adesso ho i due giocatori
            //Posso iniziare la partita
            Boolean endGame = false;

            while(!endGame){
                endGame = game.checkWin();
            }

            System.out.println("Fine partita!!");
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
