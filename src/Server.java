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
import java.util.*;

public class Server {

    public static void main(String[] args) {
        try {
            ServerSocket ss = new ServerSocket(5677);
            Map<String, AbstractMap.SimpleEntry<Integer,List<ServerThread>>> roomList=new HashMap<String, AbstractMap.SimpleEntry<Integer,List<ServerThread>>>();
            List<ServerThread> threads = new ArrayList<ServerThread>();
            ServerThread.roomList = roomList;
            ServerThread.threads = threads;
            
            while (true) {
                Socket s=ss.accept();
                System.out.println("Connection from "+s.getInetAddress());
                threads.add(new ServerThread(s));
            }
        } catch (IOException ignored){
            System.out.println(ignored.getMessage());
        }
    }
}

class ServerThread extends Thread{
    PrintWriter pw;
    BufferedReader br;
    static Map<String, AbstractMap.SimpleEntry<Integer,List<ServerThread>>> roomList;
    static List<ServerThread> threads;
    String username;
    String roomName;
    Integer roomGoal;
    List<ServerThread> roomMembers;
    Database database;

    ServerThread(Socket s) throws IOException {
        pw = new PrintWriter(s.getOutputStream(),true);
        pw.println("Connected!");
        br = new BufferedReader(new InputStreamReader(s.getInputStream()));
        this.start();
    }

    public Boolean createRoom(String rn){
        roomName = rn;
        roomGoal = ThreadLocalRandom.current().nextInt(1000);
        roomMembers = new ArrayList<ServerThread>();
        roomMembers.add(this);
        roomList.put(roomName, new AbstractMap.SimpleEntry<>(roomGoal, roomMembers));
        return true;
    }

    public Boolean joinRoom(String rn){
        AbstractMap.SimpleEntry<Integer,List<ServerThread>> room = roomList.get(rn);
        if (room == null) return false;
        roomName = rn;
        roomGoal = room.getKey();
        roomMembers = room.getValue();
        roomMembers.add(this);
        roomList.put(roomName, new AbstractMap.SimpleEntry<>(roomGoal, roomMembers));
        return true;
    }

    public Boolean exitRoom(String rn){
        AbstractMap.SimpleEntry<Integer,List<ServerThread>> room = roomList.get(rn);
        if (room == null) return false;
        roomMembers.remove(this);
        if (roomMembers.size() == 0){
            // TODO if the room is empty, delete room
            
        }
        roomName = null;
        roomGoal = null;
        roomMembers = null;
        return true;
    }

    String extract(String data){
        String[] arr = data.split("!");
        return arr[1];
    }

    String extractPassword(String data){
        String[] arr = data.split("!");
        return arr[2];
    }

    public void run(){
        String password;

        database = new Database();
        while(true){
            try {
                String line=br.readLine();
                System.out.println(line);
                if (line.contains("TryLogin!")){
                    username = extract(line);
                    password = extractPassword(line);
                    if(!database.login(username,password)){
                        pw.println("BadLogin!");
                    } else {
                        pw.println("GoodLogin");
                    }
                }
                else if (line.contains("TryRegister!")){
                    username = extract(line);
                    password = extractPassword(line);
                    if(!database.register(username,password)){
                        pw.println("BadRegister!");
                    } else {
                        pw.println("GoodRegister");
                    }
                }
                else if (line.contains("Guest!")){
                    username = extract(line);
                    pw.println("GoodGuest!"+username);
                }
                else if (line.contains("Create!")){
                    String rn = extract(line);
                    if (createRoom(rn)){
                        pw.println("GoodJoin!"+rn);
                    }
                    else{
                        pw.println("BadJoin!"+rn);
                    }
                }
                else if (line.contains("Join!")){
                    String rn = extract(line);
                    if (joinRoom(rn)){
                        pw.println("GoodJoin!"+rn);
                    }
                    else{
                        pw.println("BadJoin!"+rn);
                    }
                }
                else if (line.contains("Exit!")){
                    String rn = extract(line);
                    if (exitRoom(rn)){
                        pw.println("GoodExit!"+rn);
                    }
                    else{
                        pw.println("BadExit"+rn);
                    }
                }
                else if (line.contains("Guess!")){
                    Integer guess;
                    try{
                        guess=Integer.parseInt(extract(line));
                    }
                    catch(NumberFormatException nfe){
                        roomMembers.forEach(s->s.broadcast("Someone!"+extract(line)+"!INVALID!"+username));
                        continue;
                    }
                    if (guess>roomGoal){
                        roomMembers.forEach(s->s.broadcast("Someone!"+Integer.toString(guess)+"!TOOBIG!"+username));
                    } 
                    else if (guess<roomGoal){
                        roomMembers.forEach(s->s.broadcast("Someone!"+Integer.toString(guess)+"!TOOSMALL!"+username));
                    } else{
                        // TODO Add Score
                        roomMembers.forEach(s->s.broadcast("Someone!"+Integer.toString(guess)+"!EQUAL!"+username));
                    }
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