import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import jexer.TAction;
import jexer.TApplication;
import jexer.TMessageBox;
import jexer.TRadioGroup;
import jexer.TWindow;
import jexer.TTimer;
import jexer.layout.StretchLayoutManager;
import static jexer.TCommand.*;
import static jexer.TKeypress.*;
import jexer.demos.*;
import java.util.*;
import jexer.TField;

public class GamePage extends TWindow {
    private static final ResourceBundle i18n = ResourceBundle.getBundle(DemoCheckBoxWindow.class.getName());
    private TTimer t1, t2;
    Client client;
    String roomName;
    TRadioGroup group;
    Boolean waitForGuess = false;
    TField messageBoard;
    TField timeToGuessBoard;
    TField guessField;

    GamePage(final TApplication parent, Client client, String roomName) {
        this(parent, CENTERED | RESIZABLE, client, roomName);
    }

    GamePage(final TApplication parent, final int flags, Client c, String rm) {
        // Construct a demo window.  X and Y don't matter because it will be
        // centered on screen.
        super(parent, c.t.currentRoomName, 0, 0, 60, 16, flags);
        client = c;
        roomName = rm;

        setLayoutManager(new StretchLayoutManager(getWidth() - 2,
                getHeight() - 2));

        // a box to remind gameplay
        getApplication().messageBox(
            "Reminder",
            " - You may start gameplay immediately,\nor wait for players to join.\n - More players = more points for each win.\nHave Fun.",
            TMessageBox.Type.OK);

        group = addRadioGroup(1, 1,"Current Players(Leaderboard)");
        refreshMembers();

        messageBoard = addField(1, 9, 55, false, "Message Board");
        guessField = addField(35, 5, 10, false, "");
        timeToGuessBoard = addField(1, 10, 55, false, "");


        addButton("Submit",46, 5,
            new TAction(){
                public void DO(){
                    if (waitForGuess)
                        // ask for a number and send whatever it it;
                        client.t.guess(guessField.getText());
                        waitForGuess = false;
                        timeToGuessBoard.setText("");
                        guessField.setText("");
                }
            }
        );

        addButton("Exit",
            (getWidth() - 14) / 2, getHeight() - 4,
            new TAction() {
                public void DO() {
                    client.t.exitRoom(client.t.currentRoomName);
                    getApplication().closeWindow(GamePage.this);
                }
            }
        );

        // fast polling for game status
        t1 = getApplication().addTimer(200, true,
            new TAction(){
                public void DO(){
                    refreshMembers();
                    refreshMessage();
                    pollingGuess();
                }
            }
        );

        // slow polling
        t2 = getApplication().addTimer(2000, true,
            new TAction(){
                public void DO(){
                    askForRefreshMembers();
                }
            }
        );

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

    public void askForRefreshMembers(){
        client.t.requestRoom();
    }

    public void refreshMembers(){
        if (client.t.hasNewMembers){
            group = addRadioGroup(1, 1,"Current Players(Leaderboard)");
            String update = client.t.getRequestRoom();
            System.out.println("   NewMember "+update);
            if (update == null || update == "") return;
            else{
                if (update.contains("UpdateRoom!")){
                    List<String> playerList = new ArrayList<String>();
                    
                    // parse member list here
                    for (String combo:update.split("!")){
                        if (combo.contains("?")){
                            // we need this info
                            String[] l = combo.split("\\?");
                            String line = l[1]+ " points - "+ l[0];
                            playerList.add(line);
                        }
                    }

                    // sort the list
                    Collections.sort(playerList);

                    // add each player button
                    for (String line : playerList){
                        group.addRadioButton(line);
                    }
                }
            }
        }
        else{
            // do nothing
        }
    }

    public void refreshMessage(){
        if (client.t.hasNewMessage){
            client.t.hasNewMessage = false;
            String message = client.t.currentMessage;
            messageBoard.setText(message);
            System.out.println("NEWMESSAGE "+message);

            // if someone wins
            if (message.contains("CORRECT"))
                timeToGuessBoard.setText("Session Finished, You May Exit Now");
        }
        else{
            // do nothing
        }
    }

    public void pollingGuess(){
        if (client.t.timeToGuess){
            client.t.timeToGuess = false;
            waitForGuess = true;
            timeToGuessBoard.setText("It's Your Turn! Enter in the box and hit Submit");
        }
        else{
            // do nothing
        }
    }
    @Override
    public void onClose() {
        getApplication().removeTimer(t1);
        getApplication().removeTimer(t2);
        client.t.exitRoom(client.t.currentRoomName);
    }
}
