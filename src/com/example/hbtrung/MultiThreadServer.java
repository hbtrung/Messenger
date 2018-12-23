/**********************************************

 Bao Trung Ho

 August 10th, 2018

 **********************************************/

package com.example.hbtrung;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MultiThreadServer {
    static List<ClientHandler> clients = new ArrayList<>();
    static final int serverPort = 6789;
    static final int maxConnection = 3; // the maximum number of connections
    static int connection = 0; // number of current connections

    public static void closeConnection(Socket socket, BufferedReader input, PrintWriter output) throws IOException {
        socket.close();
        input.close();
        output.close();
    }

    public static void main(String[] agrs){
        try(ServerSocket serverSocket = new ServerSocket(serverPort)){
            System.out.println("MultiThreadServer started at " + new Date());

            Connect: while(true) {
                Socket socket = serverSocket.accept();

                System.out.println("Connection from Socket[addr=" + socket.getInetAddress() +
                        ",port=" + socket.getPort() + ",localport=" + socket.getLocalPort() +
                        "] at " + new Date());

                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter output = new PrintWriter(socket.getOutputStream(), true);

                String name = input.readLine();
                int index = 0; // gets the index of the ClientHandler Thread in the ArrayList

                // checks if user exists using name
                boolean userExist = false;
                for(ClientHandler c : clients){
                    if(c.getClientName().equals(name)){
                        index = c.getIndex();
                        userExist = true;
                        break;
                    }
                }

                // denies access if user exists and is in the chat room
                if(userExist && clients.get(index).isLogin() == true){
                    output.println("User " + clients.get(index).getClientName() + " is already in the chat room");
                    closeConnection(socket, input, output);
                    continue;
                }

                // adds new client to the array if user doesn't exist
                if(!userExist){
                    clients.add(new ClientHandler(name));
                    index = clients.size() - 1;
                }

                // rejects the user if server reaches maximum connection
                if(connection == maxConnection){
                    output.println("Server is busy");
                    closeConnection(socket, input, output);
                    continue;
                }

                // sets up the connection
                clients.get(index).setConnection(socket, input, output);

                // sends a message to all login users informing the new user
                for(ClientHandler c : clients){
                    if(c.isLogin())
                        c.output.println("User " + name + " has joined the chat room");
                }

                // the ClientHandler class implements Runnable interface so that
                // it can use a new thread to start again if an old user reconnects to the chat room
                new Thread(clients.get(index)).start();

                connection++;
            }

        } catch(IOException e){
            System.out.println("Server exception: " + e.getMessage());
        }
    }
}

/*
 * this class manipulates the receiving input from client
 * for normal message it will add the name of the user and forward it to all the users that have login status (isLogin = true)
 * for quit message, or if an error occurs, it will break the input manipulation loop
 * after that it closes the connection and sets login status to false, plus sending a message to other login users informing the disconnected user
 */
class ClientHandler implements Runnable {
    private String name;
    BufferedReader input;
    PrintWriter output;
    Socket socket;
    private boolean isLogin;
    private int index; // saves the index of this client in the clients ArrayList
    private static int curIndex = 0; // counts the total number of clients

    public ClientHandler(String name){
        this.name = name;
        this.isLogin = false;
        index = curIndex;
        curIndex++;
    }

    public void setConnection(Socket socket, BufferedReader input, PrintWriter output){
        this.socket = socket;
        this.input = input;
        this.output = output;
        isLogin = true;
    }

    public String getClientName(){
        return name;
    }

    public int getIndex(){ return index;}

    public boolean isLogin() { return isLogin;}

    @Override
    public void run(){
        String line;

        while(true){
            try{
                line = input.readLine();

                if(line.equalsIgnoreCase("/quit")){
                    break;
                }

                line = name + " says: " + line;

                System.out.println(line);
                for(ClientHandler c : MultiThreadServer.clients){
                    if(c.isLogin == false)
                        continue;
                    c.output.println(line);
                }
            } catch(IOException e){
                System.out.println("Error reading message from client: " + this.name);
                break;
            }
        }

        try{
            MultiThreadServer.closeConnection(socket, input, output);
        } catch(IOException e){
            System.out.println("Error closing IO: " + e.getMessage());
        }

        isLogin = false;

        MultiThreadServer.connection--;

        for(ClientHandler c : MultiThreadServer.clients){
            if(c.isLogin == false)
                continue;
            c.output.println("User " + name + " has left the chat room");
        }

        System.out.println("Connection to user " + name + " ended");
    }
}
