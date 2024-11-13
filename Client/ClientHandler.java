package Client;

import java.net.*;
import java.io.*;
import Server.Pedina;

public class ClientHandler extends Thread{
    private Boolean partitaFinita = false;
    private final int MAX = 8;


    public void run(){
        try{
            InetAddress serverAddress = InetAddress.getByName("localhost");
            //Connessione al server

            Socket socket = new Socket(serverAddress,50000);
            System.out.println("Sono connesso al server: "+serverAddress);

            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            PrintWriter out = new PrintWriter(socket.getOutputStream(),true);

            Campo campo = null;

            while(!partitaFinita){
                String result = in.readLine();

                if(result != null){
                    // Inizio partita
                    if(result.startsWith("createGame#")){
                        System.out.println("La partita sta per iniziare!");
                        Thread.sleep(2000);

                        //Devo decodificare il messaggio che mi viene inviato dal server
                        String boardData = result.substring(11);  // Rimuovi "createGame#"
                        String[] pedineData = boardData.split("#");
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
                        campo = new Campo(board);
                        campo.drawBoard();
                    }
                    // System.out.println(result);
                }
            }
            

            socket.close();
            
        }
        catch(Exception e){
            e.printStackTrace();
        }

    }

}
