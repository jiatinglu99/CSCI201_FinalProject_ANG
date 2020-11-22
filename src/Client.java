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

    public static void main(String [] args){
        Client client = new Client();
        System.out.println(client.connect());
    }
}

class ClientThread extends Thread{
    Boolean isConnected = false;
    Boolean isLoggedIn = false;
    Socket socket;
    BufferedReader br;
    PrintWriter pw;
    String username;
    String password;
    
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
        //TEMP
        isLoggedIn = true;
    }

    public Boolean getIsConnected(){
        return isConnected;
    }

    public Boolean getIsLoggedIn(){
        return isLoggedIn;
    }

    private Boolean isForMe(String s){
        return s.contains(username+"!");
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
                if (isLoggedIn == false && isForMe(line)){
                    if (line.contains("Registered!")){
                        isLoggedIn = true;
                    }
                    if (line.contains("LoggedIn!")){
                        isLoggedIn = true;
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

