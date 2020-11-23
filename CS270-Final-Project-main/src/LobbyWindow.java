import java.util.ResourceBundle;

import jexer.TAction;
import jexer.TApplication;
import jexer.TRadioGroup;
import jexer.TWindow;
import jexer.layout.StretchLayoutManager;
import static jexer.TCommand.*;
import static jexer.TKeypress.*;
import jexer.demos.*;
import java.lang.Thread;
import java.util.*;

/**
 * This is the main "demo" application window.  It makes use of the TTimer,
 * TProgressBox, TLabel, TButton, and TField widgets.
 */
public class LobbyWindow extends TWindow {
    private static final ResourceBundle i18n = ResourceBundle.getBundle(DemoMainWindow.class.getName());
    TRadioGroup group;
    List<String> lobbyList = new ArrayList<String>();
    List<String> lobbyNum = new ArrayList<String>();
    Client client;
    
    public LobbyWindow(final TApplication parent, Client client) {
        this(parent, CENTERED | RESIZABLE, client);
    }

    private LobbyWindow(final TApplication parent, final int flags, final Client c) {
        super(parent, "Lobby", 0, 0, 40, 20, flags);
        setLayoutManager(new StretchLayoutManager(getWidth() - 2,
                getHeight() - 2));

        client = c;

        statusBar = newStatusBar(i18n.getString("statusBar"));
        statusBar.addShortcutKeypress(kbF1, cmHelp,
            i18n.getString("statusBarHelp"));
        statusBar.addShortcutKeypress(kbF2, cmShell,
            i18n.getString("statusBarShell"));
        statusBar.addShortcutKeypress(kbF3, cmOpen,
            i18n.getString("statusBarOpen"));
        statusBar.addShortcutKeypress(kbF10, cmExit,
            i18n.getString("statusBarExit"));

        int row = 1;

        // Add some widgets
        addLabel("What's up! "+client.getName()+",", 1, row);
        //if (client.isGuest())
        row+=2;
        addLabel("Join or Create a room.", 1, row);
        row+=2;
        group = addRadioGroup(1, row, "List of Rooms:");
        
        // update rooms;
        refreshRoom(client, group);

        addButton("Refresh", 2, 16, 
            new TAction(){
                public void DO(){
                    group = addRadioGroup(1, 5, "List of Rooms:");
                    refreshRoom(client, group);
                }
            }
        );

        addButton("Join", 13, 16, 
            new TAction(){
                public void DO(){
                    int selection = group.getSelected();
                    if (selection == 0){
                        // do nothing
                    }
                    else{
                        String roomName = lobbyList.get(selection-1);
                        client.joinRoom(roomName);
                        try{
                            Thread.sleep(500);
                        }
                        catch(Exception e){
                        }
                        if (client.isInRoom()){
                            new GamePage(getApplication(), client, roomName);
                        }
                    }
                }
            }
        );

        addButton("Create Room", 21, 16, 
            new TAction(){
                public void DO(){
                    String roomName = client.getName()+"-Room";
                    client.createRoom(roomName);
                    // join as well
                    try{
                        Thread.sleep(500);
                    }
                    catch(Exception e){
                    }
                    group = addRadioGroup(1, 5, "List of Rooms:");
                    refreshRoom(client, group);
                    if (client.isInRoom()){
                        new GamePage(getApplication(), client, roomName);
                    }
                }
            }
        );
        
    }

    public void refreshRoom(Client client, TRadioGroup group){
        client.t.requestLobby();
        try{
            Thread.sleep(200);
        }
        catch(Exception e){
        }
        String update = client.t.getRequestLobby();
        System.out.println("    Received: "+update);
        if (update == null || update == ""){
            // do nothing
        }
        else{
            if (update.contains("UpdateLobby!")){ 
                    lobbyList = new ArrayList<String>();
                    lobbyNum = new ArrayList<String>();
                // parse lobby list
                for (String sRoom:update.split("!")){
                    if (sRoom.contains("-Room")){
                        String[] pair = sRoom.split("\\?");
                        lobbyList.add(pair[0]);
                        lobbyNum.add(pair[1]);
                    }
                }

                // add radio group
                for (int i = 0; i < lobbyList.size();i++){
                    String title = lobbyList.get(i)+" Now "+lobbyNum.get(i)+" people";
                    group.addRadioButton(title);
                    System.out.println(title);
                }
            }
        }
    }


    @Override
    public void onClose() {
        // exit room when closed
        client.exitRoom(client.t.currentRoomName);
    }
}
