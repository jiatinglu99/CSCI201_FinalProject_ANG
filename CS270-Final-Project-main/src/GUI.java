import jexer.TApplication;

public class GUI{
    public GUI(){
        try {
            TApplication.BackendType backendType = TApplication.BackendType.SWING;
            Application app = new Application(backendType);
            (new Thread(app)).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void main(String [] args){
        new GUI();
    }
}