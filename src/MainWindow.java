import java.util.ResourceBundle;

import jexer.TAction;
import jexer.TApplication;
import jexer.TLabel;
import jexer.TProgressBar;
import jexer.TTimer;
import jexer.TWidget;
import jexer.TWindow;
import jexer.layout.StretchLayoutManager;
import static jexer.TCommand.*;
import static jexer.TKeypress.*;
import jexer.demos.*;
import jexer.TMessageBox;
import jexer.TInputBox;
import java.util.concurrent.ThreadLocalRandom;

/**
 * This is the main "demo" application window.  It makes use of the TTimer,
 * TProgressBox, TLabel, TButton, and TField widgets.
 */
public class MainWindow extends TWindow {
    private static final ResourceBundle i18n = ResourceBundle.getBundle(DemoMainWindow.class.getName());
    private TTimer timer1;
    TLabel timerLabel;
    int timer1I = 0;
    TProgressBar progressBar1;
    String username, password;
    Client client = new Client();

    // ------------------------------------------------------------------------
    // Constructors -----------------------------------------------------------
    // ------------------------------------------------------------------------

    /**
     * Construct demo window.  It will be centered on screen.
     *
     * @param parent the main application
     */
    public MainWindow(final TApplication parent) {
        this(parent, CENTERED | RESIZABLE);
    }

    /**
     * Constructor.
     */
    private MainWindow(final TApplication parent, final int flags) {
        // Construct a demo window.  X and Y don't matter because it will be
        // centered on screen.
        super(parent, "Main Page", 0, 0, 64, 32, flags);

        setLayoutManager(new StretchLayoutManager(getWidth() - 2,
                getHeight() - 2));

        int row = 0;
        int startP = 3;

        addLabel("               _                                     _ ", startP, ++row);
        addLabel("     /\\       | |                                   | |", startP, ++row);
        addLabel("    /  \\    __| |__   __ __ _  _ __    ___  ___   __| |", startP, ++row);
        addLabel("   / /\\ \\  / _` |\\ \\ / // _` || '_ \\  / __|/ _ \\ / _` |", startP, ++row);
        addLabel("  / ____ \\| (_| | \\ V /| (_| || | | || (__|  __/| (_| |", startP, ++row);
        addLabel(" /_/    \\_\\\\__,_|  \\_/  \\__,_||_| |_| \\___|\\___| \\__,_|", startP, ++row);
        addLabel("  _   _                    _                           ", startP, ++row);
        addLabel(" | \\ | |                  | |                          ", startP, ++row);
        addLabel(" |  \\| | _   _  _ __ ___  | |__    ___  _ __           ", startP, ++row);
        addLabel(" | . ` || | | || '_ ` _ \\ | '_ \\  / _ \\| '__|          ", startP, ++row);
        addLabel(" | |\\  || |_| || | | | | || |_) ||  __/| |             ", startP, ++row);
        addLabel(" |_|_\\_| \\__,_||_| |_| |_||_.__/ _\\___||_|             ", startP, ++row);
        addLabel("  / ____|                       (_)                    ", startP, ++row);
        addLabel(" | |  __  _   _   ___  ___  ___  _  _ __    __ _       ", startP, ++row);
        addLabel(" | | |_ || | | | / _ \\/ __|/ __|| || '_ \\  / _` |      ", startP, ++row);
        addLabel(" | |__| || |_| ||  __/\\__ \\\\__ \\| || | | || (_| |      ", startP, ++row);
        addLabel("  \\_____| \\__,_| \\___||___/|___/|_||_| |_| \\__, |      ", startP, ++row);
        addLabel("                                            __/ |      ", startP, ++row);
        addLabel("                                           |___/       ", startP, ++row);
        row -= 0;

        TWidget first = addButton("Login", 28, row,
            new TAction() {
                public void DO() {
                    if (client.isConnected()){
                        TInputBox in1 = getApplication().inputBox("Login",
                            "Enter Your Username Here:\n(alphanumeric characters only)",
                            "",
                            TInputBox.Type.OKCANCEL);
                        username = in1.getText();
                        if (in1.getResult() != TMessageBox.Result.CANCEL){
                            TInputBox in2 = getApplication().inputBox("Login",
                                "Enter Your Password Here:\n(alphanumeric characters only)",
                                "",
                                TInputBox.Type.OKCANCEL);
                            password = in2.getText();

                            // both data entered
                            if (in2.getResult() != TMessageBox.Result.CANCEL){
                                client.login(username, password);
                                
                                try{
                                    Thread.sleep(100);
                                }
                                catch(Exception e){
                                }
                                // result from login
                                if (client.isLoggedin()){
                                    // lobby page
                                    new LobbyWindow(getApplication(), client);
                                }
                                else{
                                    getApplication().messageBox(
                                        "Bad Login",
                                        "The combination of username and passcode does not exist. Try Again!",
                                        TMessageBox.Type.OK);
                                }
                            }
                        }
                    }
                }
            }
        );


        row += 2;
        addButton("Register", 27, row,
            new TAction() {
                public void DO() {
                    if (client.isConnected()){
                        TInputBox in1 = getApplication().inputBox("Register",
                            "Enter Your Desired Username Here:\n(alphanumeric characters only)",
                            "",
                            TInputBox.Type.OKCANCEL);
                        username = in1.getText();
                        if (in1.getResult() != TMessageBox.Result.CANCEL){
                            TInputBox in2 = getApplication().inputBox("Register",
                                "Enter Your Desired Password Here:\n(alphanumeric characters only)",
                                "",
                                TInputBox.Type.OKCANCEL);
                            password = in2.getText();

                            // both data entered
                            if (in2.getResult() != TMessageBox.Result.CANCEL){
                                client.register(username, password);
                                
                                try{
                                    Thread.sleep(100);
                                }
                                catch(Exception e){
                                }
                                // result from login
                                if (client.isLoggedin()){
                                    // lobby page
                                    new LobbyWindow(getApplication(), client);
                                }
                                else{
                                    getApplication().messageBox(
                                        "Bad Registration",
                                        "User already exists. Try another one.",
                                        TMessageBox.Type.OK);
                                }
                            }
                        }
                    }
                }
            }
        );

        row += 2;
        addButton("Guest", 28, row,
            new TAction() {
                public void DO() {
                    if (client.isConnected()){
                        // Generate random username
                        username = "GUEST" + Integer.toString(ThreadLocalRandom.current().nextInt(10000));
                        client.guest(username);
                        // result from login
                        if (client.isLoggedin()){
                            // lobby page
                            new LobbyWindow(getApplication(), client);
                        }
                    }
                }
            }
        );

        row += 2;
        addButton("Help!", 28, row,
            new TAction() {
                public void DO() {
                    getApplication().messageBox("Help",
                        "User Login/Registration\n"+
                        "  *Click Register\n"+
                        "  *Enter your username\n"+
                        "  *Enter your password\n"+
                        "  *Click login/register\n"+
                       
                        "Guests Login\n"+
                        "  *Click Guest\n"+
                        "  *You will be provided a random username\n"+
                       
                        "Rule\n"+
                        "1.Guess a number between 0 to 1000. \n"+
                        "2.You may guess the number all by yourself or\n"+
                        "   take turns with others in the same room.\n"+
                        "3.You may join or create a room in the lobby.\n"+
                        "   Ask your friends to join and play!\n"+
                        "3.The computer will tell you how the number\n"+
                        "   compares to the secret passcode.\n"+
                        "4.Then the next user take turns to guess.\n"+
                        "5.The winner(who guessed it right) will receive\n"+
                        "   as many points as there are players in the room!\n"+
                        "   You earned it.\n"+
                        "6.Players then return to the lobby and decide if\n"+
                        "   they want to start a new round.\n",
                        TMessageBox.Type.OK);
                }
            }
        );

        client.connect();
        row = 27;
        progressBar1 = addProgressBar(26, row, 12, 0);
        row+=1;
        timerLabel = addLabel(i18n.getString("timerLabel"), 25, row);
        timer1 = getApplication().addTimer(80, true,
            new TAction() {
                public void DO() {
                    if (timer1I < 99) {
                        timer1I+=1;
                    }
                    if (client.isConnected()){
                        timer1I+=5;
                        if (timer1I > 100) timer1I = 100;
                    }
                    if (timer1I == 100){
                        timer1.setRecurring(false);
                        progressBar1.setValue(timer1I);
                        timerLabel.setLabel("Connected! Enjoy");
                        timerLabel.setWidth(timerLabel.getLabel().length());
                    }
                    else{
                        progressBar1.setValue(timer1I);
                        timerLabel.setLabel(String.format(
                                            "Connecting %d%c", timer1I, '%'));
                        timerLabel.setWidth(timerLabel.getLabel().length());
                    }
                }
            }
        );

 
        activate(first);

        statusBar = newStatusBar(i18n.getString("statusBar"));
        statusBar.addShortcutKeypress(kbF1, cmHelp,
            i18n.getString("statusBarHelp"));
        statusBar.addShortcutKeypress(kbF2, cmShell,
            i18n.getString("statusBarShell"));
        statusBar.addShortcutKeypress(kbF3, cmOpen,
            i18n.getString("statusBarOpen"));
        statusBar.addShortcutKeypress(kbF10, cmExit,
            i18n.getString("statusBarExit"));
    }
}
