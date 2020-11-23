import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.*;

public class Server {
    static ServerSocket ss;

    public static void main(String[] args) {
        try {
            Properties pps=new Properties();
            pps.load(new FileInputStream("server.properties"));
            ss = new ServerSocket(Integer.parseInt(pps.getProperty("serverPort")));
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
            try{
                ss.close();
            }
            catch(Exception e){}
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
    int low = 0;
    int high = 1000;
    String score = "0";

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
        this.sendPlayerListUpdate(rn);
        requestGuess();
        return true;
    }

    public Boolean joinRoom(String rn){
        AbstractMap.SimpleEntry<Integer,List<ServerThread>> room = roomList.get(rn);
        if (room == null) {
            return false;
        }
        roomName = rn;
        roomGoal = room.getKey();
        roomMembers = room.getValue();
        roomMembers.add(this);
        //roomList.put(roomName, new AbstractMap.SimpleEntry<>(roomGoal, roomMembers));
        for (ServerThread t:roomMembers) {
            t.sendPlayerListUpdate(rn);
        }
        return true;
    }

    public Boolean exitRoom(String rn){
        AbstractMap.SimpleEntry<Integer,List<ServerThread>> room = roomList.get(rn);
        if (room == null) {
            return false;
        }
        roomMembers.remove(this);
        if (roomMembers.size() == 0){
            roomList.remove(rn);
        }
        roomName = null;
        roomGoal = null;
        roomMembers = null;
        high = 1000;
        low = 0;
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

    void sendPlayerListUpdate(String rn){
        StringBuilder str = new StringBuilder();
        str.append("UpdateRoom!");
        for (ServerThread sThread : roomList.get(rn).getValue()){
            str.append(sThread.username+"?"+sThread.score+"!");
        }
        System.out.println("    "+str.toString());
        pw.println(str.toString());
    }

    @Override
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
                        score = database.lookUpScore(username);
                        pw.println("GoodLogin!"+score);
                    }
                }
                else if (line.contains("TryRegister!")){
                    username = extract(line);
                    password = extractPassword(line);
                    if(!database.register(username,password)){
                        pw.println("BadRegister!");
                    } else {
                        pw.println("GoodRegister!");
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
                else if (line.contains("RequestLobby!")){
                    StringBuilder str = new StringBuilder();
                    str.append("UpdateLobby!");
                    for (String rm : roomList.keySet()){
                        str.append(rm+"?"+Integer.toString(roomList.get(rm).getValue().size())+"!");
                    }
                    System.out.println("    "+str.toString());
                    pw.println(str.toString());
                }
                else if (line.contains("RequestRoom!")){
                    StringBuilder str = new StringBuilder();
                    String rn = extract(line);
                    str.append("UpdateRoom!");
                    for (ServerThread sThread : roomList.get(rn).getValue()){
                        str.append(sThread.username+"?"+sThread.score+"!");
                    }
                    System.out.println("    "+str.toString());
                    pw.println(str.toString());
                }
                else if (line.contains("Guess!")){
                    Integer guess;
                    try{
                        guess=Integer.parseInt(extract(line));
                    }
                    catch(NumberFormatException nfe){
                        roomMembers.forEach(s->s.broadcast("Someone!"+extract(line)+"!INVALID!"+username));
                        // Ask next person in the room
                        nextPlayer().requestGuess();
                        continue;
                    }
                    if (guess>roomGoal){
                        roomMembers.forEach(s->s.broadcast("Someone!"+Integer.toString(guess)+"!TOOBIG!"+username));
                    } 
                    else if (guess<roomGoal){
                        roomMembers.forEach(s->s.broadcast("Someone!"+Integer.toString(guess)+"!TOOSMALL!"+username));
                    } else{
                        database.addScore(username, roomMembers.size());
                        roomMembers.forEach(s->s.broadcast("Someone!"+Integer.toString(guess)+"!CORRECT!"+username+"!"+
                                                                    Integer.toString(roomMembers.size())));
                        //roomMembers.forEach(s->s.exitRoom(roomName));
                        continue;
                    }

                    // Ask next person in the room
                    nextPlayer().requestGuess();
                }
            } catch (SocketException se){
                break;
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void requestGuess(){
        String temp = "YourTurn!";
        broadcast(temp);
    }

    public ServerThread nextPlayer(){
        int total = roomMembers.size();
        int curr = roomMembers.indexOf(this);
        return roomMembers.get((curr+1)%total);
    }

    public void broadcast(String message){
        System.out.println(message);
        pw.println(message);
    }
}

