import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ResourceBundle;

import jexer.TAction;
import jexer.TApplication;
import jexer.TEditColorThemeWindow;
import jexer.TEditorWindow;
import jexer.TLabel;
import jexer.TProgressBar;
import jexer.TTableWindow;
import jexer.TTimer;
import jexer.TWidget;
import jexer.TWindow;
import jexer.event.TCommandEvent;
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
        row -= 1;

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
                                
                                // result from login
                                if (client.isLoggedin()){
                                    // lobby page
                                    new LobbyWindow(getApplication());
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
                                
                                // result from login
                                if (client.isLoggedin()){
                                    // lobby page
                                    new LobbyWindow(getApplication());
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
                        username = "USER" + Integer.toString(ThreadLocalRandom.current().nextInt(10000));
                        client.guest(username);
                        // result from login
                        if (client.isLoggedin()){
                            // lobby page
                            new LobbyWindow(getApplication());
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
                        "Need to enter how to play this game here",
                        TMessageBox.Type.OK);
                }
            }
        );

        client.connect();
        row = 26;
        progressBar1 = addProgressBar(26, row, 12, 0);
        row+=2;
        timerLabel = addLabel(i18n.getString("timerLabel"), 25, row);
        timer1 = getApplication().addTimer(80, true,
            new TAction() {
                public void DO() {
                    if (timer1I < 99) {
                        timer1I+=1;
                    }
                    if (client.isConnected()){
                        timer1I+=3;
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

    // ------------------------------------------------------------------------
    // TWindow ----------------------------------------------------------------
    // ------------------------------------------------------------------------

    /**
     * We need to override onClose so that the timer will no longer be called
     * after we close the window.  TTimers currently are completely unaware
     * of the rest of the UI classes.
     */
    @Override
    public void onClose() {
        getApplication().removeTimer(timer1);
    }

    /**
     * Method that subclasses can override to handle posted command events.
     *
     * @param command command event
     */
    @Override
    public void onCommand(final TCommandEvent command) {
        if (command.equals(cmOpen)) {
            try {
                String filename = fileOpenBox(".");
                if (filename != null) {
                    try {
                        new TEditorWindow(getApplication(),
                            new File(filename));
                    } catch (IOException e) {
                        messageBox(i18n.getString("errorTitle"),
                            MessageFormat.format(i18n.
                                getString("errorReadingFile"), e.getMessage()));
                    }
                }
            } catch (IOException e) {
                        messageBox(i18n.getString("errorTitle"),
                            MessageFormat.format(i18n.
                                getString("errorOpeningFile"), e.getMessage()));
            }
            return;
        }

        // Didn't handle it, let children get it instead
        super.onCommand(command);
    }

}
