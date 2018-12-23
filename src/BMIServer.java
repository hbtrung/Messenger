/**********************************************

 Bao Trung Ho

 August 10th, 2018

 **********************************************/

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class BMIServer {

    public static void main(String[] args) {
        try(ServerSocket serverSocket = new ServerSocket(6000)){
            System.out.println("BMICalculatorServer started at " + new Date());

            Socket socket = serverSocket.accept();
            System.out.println("Connected to a client started at " + new Date());

            BufferedReader input = new BufferedReader( new InputStreamReader(socket.getInputStream()));
            PrintWriter output = new PrintWriter(socket.getOutputStream(),true);

            System.out.println("Waiting for height and weight from client:");

            // get height, input is validated on client side
            String sHeight = input.readLine();
            double height = Double.parseDouble(sHeight);

            // get weight, input is validated on client side
            String sWeight = input.readLine();
            double weight = Double.parseDouble(sWeight);

            System.out.println("Calculating BMI...:");

            String result;
            double bmi = weight/(height*height);
            if (bmi < 18.5)
                result = "Underweight";
            else if (bmi >= 18.5 && bmi < 25)
                result = "Normal";
            else if (bmi >= 25 && bmi < 30)
                result = "Overweight";
            else
                result = "Obese";

            output.println(result);
        } catch(IOException e){
            System.out.println("Server exception: " + e.getMessage());
        }
    }
}
