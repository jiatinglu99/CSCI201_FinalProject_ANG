import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

public class Client {
    private ClientThread t;
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
        if (t == null) return false;
        return t.getIsConnected();
    }
    
    public Boolean isLoggedin(){
        if (t == null) return false;
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

    public void guess(int num){
        t.guess(num);
    }
}

class ClientThread extends Thread{
    Boolean isConnected = false;
    Boolean isLoggedIn = false;
    Boolean isGuest = false;
    Socket socket;
    BufferedReader br;
    PrintWriter pw;
    String username;
    String password;
    String currentLobby;
    String currentRoom;
    
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
        pw.println("join!"+rn);
    }

    public void exitRoom(String rn){
        pw.println("Exit!"+rn);
    }

    public void guess(int num){
        pw.println("Guess!"+Integer.toString(num));
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

    private Boolean isForMe(String s){
        return s.contains(username+"!");
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
        // Connect to host
        while (true) {
            try {
                socket=new Socket("localhost",5677);
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
                        String roomName = extract(line);
                        // TODO
                    }
                    else if (line.contains("GoodExit!")){
                        // TODO
                    }
                    else if (line.contains("Someone!")){
                        // means that someone guessed
                        // TODO
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
}

