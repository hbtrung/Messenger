/**********************************************

 Bao Trung Ho

 August 10th, 2018

 **********************************************/

package com.example.hbtrung;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class MultiThreadClient {
    final static int serverPort = 6789;
    private static Socket socket;
    private static BufferedReader input;
    private static PrintWriter output;

    public static void main(String[] args){
        Scanner scanner = new Scanner(System.in);
        String name;

        do {
            System.out.print("Enter name: ");
            String tmp = scanner.nextLine();
            if (tmp.matches("^[a-zA-Z][a-zA-Z0-9_.\\-]*$")) {
                name = tmp;
                break;
            }
            System.out.println("Name must start with a letter and can only contain letters, numbers, underscores('_'), dashs('-') or dots('.')");
        } while (true);
        try {

            socket = new Socket("localhost", serverPort);

            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);

            output.println(name);

            // thread to send message to server
            Thread sendMessage = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (!socket.isClosed()) {
                        String s = scanner.nextLine();
                        if (!socket.isClosed()) // in case server crashes
                            output.println(s);
                        if (s.equalsIgnoreCase("/quit")) {
                            System.out.println("You have left the chat room");
                        }

                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                        }
                    }
                }
            });

            // thread to read message from server
            Thread readMessage = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (true) {
                            String s = input.readLine();
                            if (s != null)
                                System.out.println(s);
                            else
                                break;
                        }
                    }
                    catch (IOException e) {
                        System.out.println("Error getting message from server");
                    }
                    try {
                        socket.close();
                        input.close();
                        output.close();
                    } catch (IOException e) {
                        System.out.println("Error closing IO: " + e.getMessage());
                    }
                }
            });

            sendMessage.start();
            readMessage.start();
        } catch (IOException e) {
            System.out.println("Client exception: " + e.getMessage());
        }

    }
}
