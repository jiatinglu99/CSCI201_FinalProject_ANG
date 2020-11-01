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

public class Server {

    public static void main(String[] args) {
        Integer goal = ThreadLocalRandom.current().nextInt(1000);
        System.out.println("The passcode is "+goal);
        try {
            ServerSocket ss = new ServerSocket(5677);
            List<ServerThread> threads=new ArrayList<>();
            ServerThread.threads=threads;
            ServerThread.goal=goal;
            while (true) {
                Socket s=ss.accept();
                System.out.println("Connection from "+s.getInetAddress());
                threads.add(new ServerThread(s));
            }
        } catch (IOException ignored){}
    }

}

class ServerThread extends Thread{
    PrintWriter pw;
    BufferedReader br;
    static List<ServerThread> threads;
    static Integer goal;

    ServerThread(Socket s) throws IOException {
        pw = new PrintWriter(s.getOutputStream(),true);
        pw.println("The rule is simple:\n" +
                "There is a random Integer as passcode, and you can enter an Integer to guess it.\n" +
                "Whoever guesses right first wins, and you will be notified whether the guess is right.");
        br = new BufferedReader(new InputStreamReader(s.getInputStream()));
        this.start();
    }

    public void run(){
        while(true){
            try {
                String line=br.readLine();
                System.out.println(line);
                Integer guess;
                try{guess=Integer.parseInt(line);}
                catch(NumberFormatException nfe){
                    pw.println("Please enter an Integer");
                    continue;
                }
                if (guess>goal){
                    threads.forEach(s->s.broadcast("Someone guessed: "+guess+"\nAnd it is greater than the passcode."));
                } else if (guess<goal){
                    threads.forEach(s->s.broadcast("Someone guessed: "+guess+"\nAnd it is less than the passcode."));
                } else{
                    threads.forEach(s->s.broadcast("Someone guessed: "+guess+"\nAnd it is correct!\nSomeone Wins!"));
                    threads.forEach(Thread::interrupt);
                    return;
                }
            } catch (SocketException se){
                break;
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void broadcast(String message){
        System.out.println(message);
        pw.println(message);
    }
}