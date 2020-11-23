import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.Properties;

public class Client {
    public ClientThread t;
    public Client(){
    }
    
    public void login(String username, String password){
        t.login(username, password);
    }

    public void guest(String username){
        t.guest(username);
    }

    public void register(String username, String password){
        t.register(username, password);
    }

    public Boolean isConnected(){
        if (t == null) {
            return false;
        }
        return t.getIsConnected();
    }
    
    public Boolean isLoggedin(){
        if (t == null) {
            return false;
        }
        return t.getIsLoggedIn();
    }
    
    public Boolean connect(){
        t=new ClientThread();
        t.start();
        return t.getIsConnected();
    }

    public String getName(){
        return t.getUsername();
    }


    public Boolean isGuest(){
        return t.isGuest();
    }

    public void createRoom(String rn){
        t.createRoom(rn);
    }

    public void joinRoom(String rn){
        t.joinRoom(rn);
    }

    public void exitRoom(String rn){
        t.exitRoom(rn);
    }

    public void guess(String num){
        if (num.equals("")||num == "") {
            num = "NUL";
        }
        t.guess(num);
    }

    public Boolean isInRoom(){
        return t.isInRoom;
    }
}

class ClientThread extends Thread{
    Boolean isConnected = false;
    Boolean isLoggedIn = false;
    Boolean isGuest = false;
    Boolean isInRoom = false;
    Socket socket;
    BufferedReader br;
    PrintWriter pw;
    String username;
    String password;
    String currentLobby;
    String currentRoom = new String();
    String currentRoomName;
    String currentMessage;
    Boolean hasNewMembers = false;
    Boolean hasNewMessage = false;
    Boolean timeToGuess = false;
    
    ClientThread(){
    }

    public void login(String us, String pd){
        username = us;
        password = pd;
        // intentional delay to get login result
        pw.println("TryLogin!"+username+"!"+password);
        try
        {
            sleep(500);
        }
        catch(InterruptedException ex)
        {
            currentThread().interrupt();
        }
    }

    public void guest(String us){
        username = us;
        isGuest = true;
        pw.println("Guest!"+username);
        try
        {
            sleep(500);
            isLoggedIn = true;
        }
        catch(InterruptedException ex)
        {
            currentThread().interrupt();
        }
    }

    public void register(String us, String pd){
        username = us;
        password = pd;
        pw.println("TryRegister!"+username+"!"+password);
        try
        {
            sleep(200);
        }
        catch(InterruptedException ex)
        {
            currentThread().interrupt();
        }
        // intentional delay to get login result
        pw.println("TryLogin!"+username+"!"+password);
        
        try
        {
            sleep(300);
        }
        catch(InterruptedException ex)
        {
            currentThread().interrupt();
        }
        // //TEMP
        // isLoggedIn = true;
    }

    public void createRoom(String rn){
        pw.println("Create!"+rn);
    }

    public void joinRoom(String rn){
        pw.println("Join!"+rn);
    }

    public void exitRoom(String rn){
        currentRoom = "";
        pw.println("Exit!"+rn);
    }

    public void guess(String num){
        timeToGuess = false;
        pw.println("Guess!"+num);
    }

    public Boolean getIsConnected(){
        return isConnected;
    }

    public Boolean getIsLoggedIn(){
        return isLoggedIn;
    }

    public String getUsername(){
        return username;
    }

    public Boolean isGuest(){
        return isGuest;
    }

    String extract(String data){
        String[] arr = data.split("!");
        return arr[1];
    }

    String extractPassword(String data){
        String[] arr = data.split("!");
        return arr[2];
    }

    String extract(String data, int num){
        String[] arr = data.split("!");
        return arr[num];
    }

    void requestLobby(){
        pw.println("RequestLobby!");
    }

    void requestRoom(){
        pw.println("RequestRoom!"+currentRoomName);
    }

    // Manual request
    String getRequestLobby(){
        if (currentLobby == null) {
            return "";
        } else{
            String temp = currentLobby;
            currentLobby = null;
            return temp;
        }
    }

    // Automatic update
    String getRequestRoom(){
        hasNewMembers = false;
        if (currentRoom == null) {
            return "";
        } else{
            String temp = currentRoom;
            //currentRoom = null;
            return temp;
        }
    }

    @Override
    public void run(){
        // Connect to host
        try{
            while (true) {
                try {
                    Properties pps=new Properties();
                    pps.load(new FileInputStream("client.properties"));
                    socket=new Socket(pps.getProperty("address"), Integer.parseInt(pps.getProperty("clientPort")));
                    br=new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    pw=new PrintWriter(socket.getOutputStream(),true);
                    break;
                }
                catch(IOException e){
                    System.out.println(e.getMessage());
                }
            }
            
            // Host is connected, now login/register
            while (true) {
                try{
                    String line = br.readLine();
                    System.out.println(line);
                    if (isConnected == false && line.contains("Connected!")){
                        isConnected = true;
                    }
                    if (isLoggedIn == false){
                        if (line.contains("GoodRegister!")){
                            isLoggedIn = true;
                            isGuest = false;
                        }
                        if (line.contains("GoodLogin!")){
                            isLoggedIn = true;
                            isGuest = false;
                        }
                        if (line.contains("GoodGuest!")){
                            isLoggedIn = true;
                            isGuest = true;
                        }
                    }
                    if (isLoggedIn){
                        if (line.contains("GoodJoin!")){
                            currentRoomName = extract(line);
                            isInRoom = true;
                        }
                        else if (line.contains("GoodExit!")){
                            isInRoom = false;
                        }
                        else if (line.contains("UpdateLobby!")){
                            currentLobby = line;
                        }
                        else if (line.contains("UpdateRoom!")){
                            if (!currentRoom.equals(line)){
                                // System.out.println("1"+currentRoom);
                                // System.out.println("2"+line);
                                hasNewMembers = true;
                                currentRoom = line;
                            }
                        }
                        else if (line.contains("Someone!")){
                            // means that someone guessed
                            String guess = extract(line,1);
                            String state = extract(line,2);//INVALID,TOOBIG,TOOSMALL,CORRECT
                            String who =  extract(line,3);
                            hasNewMessage = true;
                            if (state.contains("CORRECT")){
                                String points = extract(line,4);
                                currentMessage = who+ " guessed "+guess+" and it's " +state +"!+"+points+" points";
                            }
                            else{
                                currentMessage = who+ " guessed "+guess+" and it's " +state +"!";
                            }
                        }
                        else if (line.contains("YourTurn!")){
                            timeToGuess = true;
                        }
                    }
                } catch (SocketException se){
                    System.out.println("Connection error. Enter any key to exit.");
                    return;
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        }
        catch(Exception e){
            try{
                exitRoom(currentRoomName);
                socket.close();
            }
            catch(Exception ee){}
        }
    }
    
    public static void main(String [] args){
        new GUI();
    }
}

