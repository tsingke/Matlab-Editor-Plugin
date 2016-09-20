package at.justin.matlab;

import at.justin.matlab.gui.bookmarks.Bookmarks;
import at.justin.matlab.installer.Install;
import at.justin.matlab.prefs.Settings;
import com.mathworks.mlwidgets.prefs.PrefsChanger;

import javax.swing.*;
import java.io.File;
import java.io.IOException;

/** Created by Andreas Justin on 2016-08-25. */
public class Start {
    public static void start(String customSettings, String defaultSettings) {
        if (customSettings != null && defaultSettings != null) {
            loadSettings(customSettings, defaultSettings);
        }
        try {
            setEditorCallbacks();
            setCmdWinCallbacks();
            addPrefs();
            MatlabKeyStrokesCommands.setCustomKeyStrokes();
            Bookmarks.getInstance().load();
            addShortcut();
            setReplacementPath();
            Settings.store();
        } catch (Exception | NoClassDefFoundError e) {
            try {
                EditorApp.getInstance().removeCallbacks();
            } catch (Exception ignored) {
            }
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                    new JFrame(""),
                    e.getMessage() + "\nIt might be that There is no Editor or CommandWindow Open",
                    "something went wrong, very very wrong",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void setReplacementPath() {
        if (Settings.getPropertyBoolean("feature.enableReplacements") && Settings.getProperty("path.mepr.rep").length() > 1) return;
        try {
            File fileRep = new File(Install.getJarFile().getParentFile().getPath() + "\\Replacements");
            File fileVar = new File(Install.getJarFile().getParentFile().getPath() + "\\Replacements\\Variables");
            if (!fileRep.exists() || !fileVar.exists()) {
                System.out.println(
                        "Live Templates has been disabled for one or both following reasons:"
                                + "\n  * No Replacement folder found in \"" + fileRep.getParent()
                                + "\n  * No Variables folder found in \"" + fileVar.getParent() + "\""
                                + "\nplease set \"path.mepr.rep\" and \"path.mepr.var\" manually");
                Settings.setPropertyBoolean("feature.enableReplacements", false);
                return;
            }
            Settings.setProperty("path.mepr.rep", fileRep.getPath());
            Settings.setProperty("path.mepr.var", fileVar.getPath());
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                    new JFrame(""),
                    e.getMessage() + "\nplease set \"path.mepr.rep\" and \"path.mepr.var\" manually",
                    "something went wrong, very very wrong",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void start() {
        File customFile;
        File defaultFile;
        try {
            defaultFile = Install.getDefaultPropertyFile();
            customFile = Install.getCustomPropertyFile();
            start(defaultFile.getAbsolutePath(), customFile.getAbsolutePath());
        } catch (IOException ignored) {
        }
        start(null, null);
    }


    private static void loadSettings(String customFile, String defaultFile) {
        Settings.loadSettings(customFile, defaultFile);
    }

    private static void setEditorCallbacks() {
        EditorApp.getInstance().setCallbacks();
    }

    private static void setCmdWinCallbacks() {
        CommandWindow.setCallbacks();
    }

    private static void addPrefs() {
        try {
            PrefsChanger.addPrefs();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void addShortcut() {
        String label = "MEP Help";
        String callback = "fprintf('MEP HELP')";
        String icon = "";
        String category = "";
        String isEditable = "false";
        // ShortcutUtils.addShortcutToBottom(label, callback, icon, category, isEditable);
    }
}
