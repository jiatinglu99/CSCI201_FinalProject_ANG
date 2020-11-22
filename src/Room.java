import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Room
{
    private ArrayBlockingQueue<ServerThread> serverThreads;
    //static Integer goal;
    Integer goal;

    public Room(int port)
    {
        goal = ThreadLocalRandom.current().nextInt(1000);
        System.out.println("The passcode is "+goal);
        try{
            // establish a port
            System.out.println("Binding to port " + port);
            ServerSocket ss = new ServerSocket(port);
            System.out.println("Bound to port " + port);
            serverThreads = new ArrayBlockingQueue<ServerThread>(10);
            ServerThread.goal=goal;
            while(true){
                Socket s = ss.accept();  // accept incoming request
                System.out.println("Connection from: " + s.getInetAddress());
                ServerThread st = new ServerThread(s,this);
                System.out.println("Adding this client to active client list");
                serverThreads.add(st);
            }
        } catch (Exception ex) {}

    }

    public void broadcast(String message, ServerThread st)
    {
        if (message != null) {
            System.out.println("Broadcasting... "+message);
            for(ServerThread threads : serverThreads) {
                if (st != threads) {
                    threads.sendMessage("Someone"+message);
                } else {
                    threads.sendMessage("You"+message);
                }
            }
        }
    }

    //public static void main(String [] args){new Room(1234);}
    public static void main(String [] args)
    {
        ArrayList<Integer> roomNum = new ArrayList<Integer> ();
        roomNum.add(1234);
        roomNum.add(2345);
        roomNum.add(5678);
        Scanner scan = new Scanner(System.in);
        System.out.print("Enter any number: ");
        Integer a = scan.nextInt();
        while(a>=0)
        {
            int n = roomNum.get(a);
            new Room(n);
            a = scan.nextInt();
        }
    }
}

//
//class Room{
//    ArrayList<Socket> socketList = new ArrayList<Socket>();
//    private final Lock aLock = new ReentrantLock();
//
//    public void Room()
//    {
//    }
//    public void addSocket(Socket s)
//    {
//        socketList.add(s);
//    }
//    public void start() throws IOException
//    {
//        //	PrintWriter pw = new PrintWriter(socketList.get(0).getOutputStream());
//        //	pw.println("It's your turn, Player 1");
//        //	pw.flush();
//    }
//
//}
