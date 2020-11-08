import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) {
        Scanner sc=new Scanner(System.in);
        //String Addr=sc.nextLine();
        Socket s;
        try{
            s=new Socket("localhost",5677);
            PrintWriter pw=new PrintWriter(s.getOutputStream(),true);
            Thread t=new ClientThread(s);
            t.start();
            while(true){
                String input=sc.nextLine();
                pw.println(input);
                if (!t.isAlive())
                    return;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ClientThread extends Thread{
    BufferedReader br;

    ClientThread(Socket s) throws IOException {
        br=new BufferedReader(new InputStreamReader(s.getInputStream()));
    }

    public void run(){
        while (true) {
            try{
                String line = br.readLine();
                System.out.println(line);
                if (line.contains("correct")) {
                    System.out.println("Enter any key to exit");
                    return;
                }
            } catch (SocketException se){
                System.out.println("Connection error. Enter any key to exit.");
                return;
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }
}
