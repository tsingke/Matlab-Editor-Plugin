package at.justin.matlab;

import at.justin.debug.Debug;
import at.justin.matlab.gui.bookmarks.Bookmarks;
import at.justin.matlab.gui.bookmarks.BookmarksViewer;
import at.justin.matlab.gui.clipboardStack.ClipboardStack;
import at.justin.matlab.gui.fileStructure.FileStructure;
import at.justin.matlab.prefs.Settings;
import at.justin.matlab.util.KeyStrokeUtil;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Andreas Justin on 2016 - 02 - 09.
 */
public class KeyReleasedHandler {
    private static String opEqString;
    private static Pattern opEqPattern = Pattern.compile("[\\+]"); // "[\\+\\-\\*/]"
    private static Pattern opEqLeftArgPattern = Pattern.compile("(\\s*)(.*?)(?=\\s*[\\+\\-\\*/]{2})");
    private static boolean ctrlf2 = false; // if ctrl is released before F2. there may be a better way to fix the keyevent issue

    private KeyReleasedHandler() {
    }

    public static void doYourThing(KeyEvent e) {
        boolean ctrlFlag = e.isControlDown() | e.getKeyCode() == KeyEvent.VK_CONTROL;
        boolean shiftFlag = e.isShiftDown();
        boolean altFlag = e.isAltDown();
        boolean ctrlShiftFlag = ctrlFlag && shiftFlag && !altFlag;
        boolean ctrlShiftAltFlag = ctrlFlag && shiftFlag && altFlag;
        boolean ctrlOnlyFlag = ctrlFlag && !shiftFlag && !altFlag;
        boolean altOnlyFlag = !ctrlFlag && !shiftFlag && altFlag;

        if (ctrlFlag && e.getKeyCode() == KeyEvent.VK_C) {
            ClipboardStack.getInstance().add(EditorWrapper.getInstance().getSelectedTxt());
            // try {
            //     String data = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
            //     if (data != null) clipboardStack.getInstance().add(data);
            // } catch (UnsupportedFlavorException | IOException ignored) {
            //     e.printStackTrace();
            // }
        }
        if (ctrlShiftFlag && e.getKeyCode() == KeyEvent.VK_V) {
            ClipboardStack.getInstance().setVisible(true);
        }

        if (ctrlOnlyFlag && e.getKeyCode() == KeyEvent.VK_F12) {
            FileStructure.getINSTANCE().populate(EditorWrapper.getInstance());
            FileStructure.getINSTANCE().setVisible(true);
        }

        if (Settings.DEBUG && ctrlShiftFlag && e.getKeyCode() == KeyEvent.VK_E) {
            Debug.assignObjectsToMatlab();
        }

        // bookmark thing
        KeyStroke ksC = KeyStrokeUtil.getMatlabKeyStroke(MatlabKeyStrokesCommands.CTRL_PRESSED_F2);
        KeyStroke ksD = KeyStrokeUtil.getKeyStroke(e.getKeyCode(), ctrlFlag, shiftFlag, false);
        KeyStroke ksDN = KeyStrokeUtil.getKeyStroke(e.getKeyCode(), ctrlFlag, !shiftFlag, false);

        if (ksC != null && ksD != null && ksC.toString().equals(ksD.toString())) {
            ctrlf2 = true;
            // Doing the bookmarks here is error prone and won't work correctly.
            // this function ist called every time (afaik) before Matlab's keyRelease
        } else if (ksC != null && ksDN != null && ksC.toString().equals(ksDN.toString())) {
            BookmarksViewer.getInstance().showDialog();
        }
    }

    private static void operatorEqualsThing(KeyEvent e) {
        if (opEqString.length() != 2) return;
        EditorWrapper ew = EditorWrapper.getInstance();
        String keyStr = Character.toString(e.getKeyChar());
        String currentLineStr = ew.getCurrentLineText() + keyStr; // current line does not include currently pressed character
        Matcher matcher = opEqLeftArgPattern.matcher(currentLineStr);
        String newLine;
        if (!matcher.find()) return;

        newLine = matcher.group(2) + " = " + matcher.group(2) + " " + keyStr + "  ";
        int currentLine = ew.getCurrentLine();
        ew.goToLine(currentLine, true);
        ew.setSelectedTxt(newLine);
        ew.goToLineCol(currentLine, newLine.length() + matcher.group(1).length() + 2);
    }

    public static void doOperatorThing(KeyEvent e) {
        String keyString = Character.toString(e.getKeyChar());
        Matcher matcher = opEqPattern.matcher(keyString);
        if (matcher.find()) {
            opEqString = opEqString + keyString;
        } else {
            opEqString = "";
        }
        operatorEqualsThing(e);
    }

    public static void doBookmarkThing(KeyEvent e) {
        if (ctrlf2) {
            ctrlf2 = false;
            Bookmarks.getInstance().setBookmarks(EditorWrapper.getInstance());
            if (BookmarksViewer.getInstance().isVisible()) {
                BookmarksViewer.getInstance().updateList();
            }
            Bookmarks.getInstance().save();
        }
    }

}
