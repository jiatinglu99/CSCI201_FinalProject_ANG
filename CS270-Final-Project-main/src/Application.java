import java.io.File;
import java.io.IOException;
import java.util.ResourceBundle;

import jexer.TApplication;
import jexer.TEditColorThemeWindow;
import jexer.TEditorWindow;
import jexer.event.TMenuEvent;
import jexer.menu.TMenu;
import jexer.backend.SwingTerminal;
import jexer.demos.*;

public class Application extends TApplication {

    /**
     * Translated strings.
     */
    private static final ResourceBundle i18n = ResourceBundle.getBundle(DemoApplication.class.getName());

    /**
     * Public constructor.
     */
    public Application(final BackendType backendType) throws Exception {
        // Default window size is 82x28
        super(backendType, (backendType == BackendType.SWING ? 82 : -1),
            (backendType == BackendType.SWING ? 36 : -1), 20);
        addAllWidgets();
        getBackend().setTitle("Advanced Number Guessing");
    }

    /**
     * Handle menu events.
     */
    @Override
    public boolean onMenu(final TMenuEvent menu) {

        if (menu.getId() == 3000) {
            // Bigger +2
            assert (getScreen() instanceof SwingTerminal);
            SwingTerminal terminal = (SwingTerminal) getScreen();
            terminal.setFontSize(terminal.getFontSize() + 2);
            return true;
        }
        if (menu.getId() == 3001) {
            // Smaller -2
            assert (getScreen() instanceof SwingTerminal);
            SwingTerminal terminal = (SwingTerminal) getScreen();
            terminal.setFontSize(terminal.getFontSize() - 2);
            return true;
        }

        if (menu.getId() == 2050) {
            new TEditColorThemeWindow(this);
            return true;
        }

        if (menu.getId() == TMenu.MID_OPEN_FILE) {
            try {
                String filename = fileOpenBox(".");
                 if (filename != null) {
                     try {
                         new TEditorWindow(this, new File(filename));
                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return true;
        }
        return super.onMenu(menu);
    }

    /**
     * Add all the widgets of the demo.
     */
    private void addAllWidgets() {
        new MainWindow(this);

        // Add the menus
        addToolMenu();
        
        if (getScreen() instanceof SwingTerminal) {
            TMenu swingMenu = addMenu(i18n.getString("swing"));
            swingMenu.addItem(3000, i18n.getString("bigger"));
            swingMenu.addItem(3001, i18n.getString("smaller"));
        }
        addWindowMenu();
    }

}
