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
import jexer.TRadioGroup;
import jexer.TTableWindow;
import jexer.TTimer;
import jexer.TWidget;
import jexer.TWindow;
import jexer.event.TCommandEvent;
import jexer.layout.StretchLayoutManager;
import static jexer.TCommand.*;
import static jexer.TKeypress.*;
import jexer.demos.*;
import jexer.TRadioButton;

/**
 * This is the main "demo" application window.  It makes use of the TTimer,
 * TProgressBox, TLabel, TButton, and TField widgets.
 */
public class LobbyWindow extends TWindow {
    private static final ResourceBundle i18n = ResourceBundle.getBundle(DemoMainWindow.class.getName());
    TRadioGroup group;
    
    public LobbyWindow(final TApplication parent, Client client) {
        this(parent, CENTERED | RESIZABLE, client);
    }

    private LobbyWindow(final TApplication parent, final int flags, final Client client) {
        super(parent, "Lobby", 0, 0, 40, 16, flags);
        setLayoutManager(new StretchLayoutManager(getWidth() - 2,
                getHeight() - 2));

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
        if (client.isGuest())
        row+=2;
        group = addRadioGroup(1, row, "List of Rooms:");
        // update rooms;
        refreshRoom(group);

        addButton("Refresh", 1, 14, 
            new TAction(){
                public void DO(){
                    group = addRadioGroup(1, 3, "List of Rooms:");
                    refreshRoom(group);
                }
            }
        );

        addButton("Enter Room", 8, 14, 
            new TAction(){
                public void DO(){
                    
                }
            }
        );

        addButton("Create Room", 17, 14, 
            new TAction(){
                public void DO(){
                    group = addRadioGroup(1, 3, "List of Rooms:");
                    refreshRoom(group);
                }
            }
        );
        
    }

    public void refreshRoom(TRadioGroup group){

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
