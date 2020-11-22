import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;

//public class Client {
//
//    public static void main(String[] args) {
//        new ClientThread("localhost",1234);
////        Scanner sc=new Scanner(System.in);
////        //String Addr=sc.nextLine();
////        //Socket s;
////        try{
////            //s=new Socket("localhost",1234);
////            //PrintWriter pw=new PrintWriter(s.getOutputStream(),true);
////            Thread t=new ClientThread("localhost",1234);
//////            t.start();
//////            while(true){
//////                String input=sc.nextLine();
//////                pw.println(input);
//////                if (!t.isAlive())
//////                    return;
//////            }
////        }
////        catch (IOException e) {
////            e.printStackTrace();
////        }
//    }
//}

class ClientThread extends Thread{
    private BufferedReader br;
    private Socket s;
    private PrintWriter pw;
    //String username;

    public ClientThread(String hostname, int port) throws IOException {
        try {
            System.out.println("Starting Client");
            s = new Socket(hostname, port);
            System.out.println("Client Started");
            br = new BufferedReader(new InputStreamReader(s.getInputStream()));
            pw = new PrintWriter(s.getOutputStream(), true);
            this.start();
            Scanner scan = new Scanner(System.in);
            //System.out.print("Enter your username: ");
            //username = scan.nextLine();
            while (true) {
                String line = scan.nextLine();
                if (line.equals("quit")) {
                    try {
                        pw.close();
                        br.close();
                        s.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
                pw.println(line);
            }
        }
        catch (Exception ex) {}
    }

    public void run(){
        while (true) {
            try{
                String line = br.readLine();
                System.out.println(line);
                if (line.contains("correct")) {
                    System.out.println("Enter quit to exit");
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

    public static void main(String[] args) {
        try {
            new ClientThread("localhost",1234);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
