/**********************************************

 Bao Trung Ho

 August 10th, 2018

 **********************************************/

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class BMIClient {

    public static void main(String[] args){
        Scanner scanner = new Scanner(System.in);
        String line;
        String height;
        String weight;

        try(Socket socket = new Socket("localhost", 6000)){

            BufferedReader cInput = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter cOutput = new PrintWriter(socket.getOutputStream(), true);

            do{
                try{
                    System.out.print("Write your height in meters (ex: 1.80): ");
                    double tmp;
                    line = scanner.nextLine();
                    if(line.length() == 0)
                        throw new Exception();
                    tmp = Double.parseDouble(line);
                    height = line;
                    break;
                } catch(Exception e){
                    System.out.println("Invalid input. Please try again.");
                }
            } while(true);

            do{
                try{
                    System.out.print("Write your weight in kilograms (ex: 70.5): ");
                    double tmp;
                    line = scanner.nextLine();
                    if(line.length() == 0)
                        throw new Exception();
                    tmp = Double.parseDouble(line);
                    weight = line;
                    break;
                } catch(Exception e){
                    System.out.println("Invalid input. Please try again.");
                }
            } while(true);

            cOutput.println(height);
            cOutput.println(weight);

            System.out.println("Waiting for BMI from server...");
            System.out.println("Your BMI is: " + cInput.readLine());
        }catch (IOException e) {
            System.out.println("Client Exception: " + e.getMessage());
        }
    }
}
