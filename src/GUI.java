import jexer.TApplication;

public class GUI{
    public static void main(String [] args){
        try {
            TApplication.BackendType backendType = TApplication.BackendType.SWING;
            Application app = new Application(backendType);
            (new Thread(app)).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}