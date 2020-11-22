import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

//public class Server {
//
//    public static void main(String[] args) {
//        Integer goal = ThreadLocalRandom.current().nextInt(1000);
//        System.out.println("The passcode is "+goal);
//        try {
//            ServerSocket ss = new ServerSocket(5677);
//            List<ServerThread> threads=new ArrayList<>();
//            ServerThread.threads=threads;
//            ServerThread.goal=goal;
//            while (true) {
//                Socket s=ss.accept();
//                System.out.println("Connection from "+s.getInetAddress());
//                threads.add(new ServerThread(s));
//            }
//        } catch (IOException ignored){}
//    }
//
//}

class ServerThread extends Thread{
    PrintWriter pw;
    BufferedReader br;
    Room r;
    //static List<ServerThread> threads;
    static Integer goal;


    public ServerThread(Socket s, Room r) throws IOException {
        goal = ThreadLocalRandom.current().nextInt(1000);
        //System.out.println("The passcode is "+goal);
        pw = new PrintWriter(s.getOutputStream(),true);
        pw.println("The passcode is "+goal);
        this.r = r;
        pw.println("The rule is simple:\n" +
                "There is a random Integer as passcode, and you can enter an Integer to guess it.\n" +
                "Whoever guesses right first wins, and you will be notified whether the guess is right.");
        br = new BufferedReader(new InputStreamReader(s.getInputStream()));
        this.start();
    }

    public void run(){

            try {
                //semaphore.acquire();
                //System.out.println("Acquire a permit");
                //line = br.readLine();
                //System.out.println(line);
                //r.broadcast(line,this);
                while(true){
                    String line = br.readLine();
                    System.out.println(line);
                    Integer guess;
                    try{guess=Integer.parseInt(line);}
                    catch(NumberFormatException nfe){
                        pw.println("Please enter an Integer");
                        continue;
                    }
                    if (guess>goal){
                        //threads.forEach(s->s.sendMessage("Someone guessed: "+guess+"\nAnd it is greater than the passcode."));
                        r.broadcast("Someone guessed: "+guess+"\nAnd it is greater than the passcode.", this);
                    } else if (guess<goal){
                        //threads.forEach(s->s.sendMessage("Someone guessed: "+guess+"\nAnd it is less than the passcode."));
                        r.broadcast("Someone guessed: "+guess+"\nAnd it is less than the passcode.", this);
                    } else{
                        //threads.forEach(s->s.sendMessage("Someone guessed: "+guess+"\nAnd it is correct!\nSomeone Wins!"));
                        r.broadcast("Someone guessed: "+guess+"\nAnd it is correct!\nSomeone Wins!", this);
                        //threads.forEach(Thread::interrupt);
                        return;
                    }//Sent text back to the clients

                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                try {
                    //semaphore.release();
                    System.out.println("Release a permit");
                    pw.close();
                    br.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
    }
    public void sendMessage(String message)
    {
        pw.println(message);
        pw.flush();
    }
//    public void broadcast(String message){
//        System.out.println(message);
//        pw.println(message);
//    }
}