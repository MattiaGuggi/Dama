package Client;

import java.net.*;
import java.io.*;
import Server.Pedina;
import Server.Posizione;
import java.util.ArrayList;

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
                if(result != null){
                    //Devo decodificare la stringa che ho appena ricevuto
                    System.out.println("Result: " + result);

                    String[] words = result.split("#");
                    ArrayList<Posizione> allPossibleMoves = new ArrayList<>();

                    if(words[0].equals("createGame")){
                        // Formato result: comando#dati#colore dati: x,y;
                        System.out.println(words[1]);
                        this.createGame(result, out);
                    }
                    else if (words[0].equals("showPossibleMoves")) {
                        if (words.length == 0) break;
                        String[] coppiaDati = words[1].split(";");

                        // Prendo tutte le posizioni possibili
                        for (int i=0 ; i<coppiaDati.length ; i++) {
                            String[] coords = coppiaDati[i].split(","); 
                            int x = Integer.parseInt(coords[0]);
                            int y = Integer.parseInt(coords[1]);

                            allPossibleMoves.add(new Posizione(x, y));
                        }

                        // Le passo al campo
                        if (allPossibleMoves.size() > 0) {
                            campo.setPossibleMoves(allPossibleMoves);
                        }
                    }
                    else if (words[0].equals("muoviAncheTe")) {
                        
                    }
                    else if (words[0].equals("mangiata")) {

                    }
                    else if (words[0].equals("isDama")) {

                    }
                    else if (words[0].equals("win")) {

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
        this.campo.drawBoard();
    }

}