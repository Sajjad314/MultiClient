package multipleclientscket;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;

public class ClientHandler implements Runnable {

    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    //broadcast messages to all clients instead of just the server.
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String clientUsername;

    //constructor
    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.clientUsername = bufferedReader.readLine(); //catch username
            clientHandlers.add(this);//adding client to ArrayList
            //sendMessage();
            //broadcastMessage();
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    @Override
    public void run() {
        //everything in this method is run on a separate thread
        String messageFromClient;

        while (socket.isConnected()) {
            try {
                messageFromClient = bufferedReader.readLine();
                System.out.println(messageFromClient);

                //broadcastMessage();
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
                break;
            }

        }
    }

    public void broadcastMessage() {

        //String[] args = messageToSend.split("0");
    }

    public void removeClientHandler() {
        clientHandlers.remove(this);
//        broadcastMessage("SERVER: " + clientUsername + " has left the chat!");

    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        removeClientHandler();
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                Scanner input = new Scanner(System.in);
                while (true) {
                    String s = input.nextLine();
                    System.out.println(s);
                    if (s.equalsIgnoreCase("all")) {
                        String messageToSend = input.nextLine();
                        for (ClientHandler clientHandler : clientHandlers) {
                            try {

                                clientHandler.bufferedWriter.write("Server : " + messageToSend);
                                clientHandler.bufferedWriter.newLine();
                                clientHandler.bufferedWriter.flush();

                            } catch (IOException e) {
                                //closeEverything(socket, bufferedReader, bufferedWriter);
                            }
                        }
                    } else {
                        String messageToSend = input.nextLine();
                        for (ClientHandler clientHandler : clientHandlers) {
                            try {
                                if (clientHandler.clientUsername.equals(s)) {

                                    clientHandler.bufferedWriter.write("Server : " + messageToSend);
                                    clientHandler.bufferedWriter.newLine();
                                    clientHandler.bufferedWriter.flush();
                                }
                            } catch (IOException e) {
                                //closeEverything(socket, bufferedReader, bufferedWriter);
                            }
                        }

                    }

                }
            }
        }).start();
    }
}
