package at.justin.matlab.prefs;

import at.justin.matlab.EditorApp;
import com.mathworks.mwswing.MJPanel;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Andreas Justin on 2016-08-12.
 */

// import com.mathworks.mlwidgets.prefs.MATLABProductNodePrefsPanel;

public class PrefsPanel extends MJPanel {
    private static java.util.List<Component> componentList = new ArrayList<>(20);
    private static java.util.List<PropertyType> componentTypeList = new ArrayList<>(20);
    private static java.util.List<String> componentKeyList = new ArrayList<>(20);
    private JScrollPane jsp;
    private JPanel jp;

    /**
     * @param dim it seems matlab passes in Dimension in Constructor
     */
    public PrefsPanel(Dimension dim) {
        this.setName("MatlabEditorPluginSettings");
        this.setSize(dim);
        setLayout();
    }

    public PrefsPanel() {
        this(new Dimension(100, 200));
    }

    public static MJPanel createPrefsPanel() {
        return new PrefsPanel();
    }

    public static void commitPrefsChanges(boolean save) {
        if (!save) return;
        for (int i = 0; i < componentList.size(); i++) {
            Component c = componentList.get(i);
            String key = componentKeyList.get(i);
            switch (componentTypeList.get(i)) {
                case BOOLEAN:
                    JCheckBox jcb1 = (JCheckBox) c;
                    Settings.setPropertyBoolean(key, jcb1.isSelected());
                    break;
                case INTEGER:
                    break;
                case STRING:
                    break;
                case ONOFF:
                    JCheckBox jcb2 = (JCheckBox) c;
                    Settings.setProperty(key, jcb2.isSelected() ? "on" : "off");
                    break;
                case PATH:
                    JTextField jtf1 = (JTextField) c;
                    Settings.setProperty(key, jtf1.getText());
                    break;
                case COLOR:
                    JPanel jp1 = (JPanel) c;
                    Settings.setPropertyColor(key, jp1.getBackground());
                    if (key.equals("bpColor")) {
                        EditorApp.getInstance().colorizeBreakpointView(jp1.getBackground());
                    }
                    break;
            }
        }
        try {
            Settings.store();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setLayout() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0;
        gbc.gridx = 0;
        gbc.weightx = 1;
        gbc.weighty = 1;
        gbc.insets = new Insets(1, 1, 1, 1);
        gbc.fill = GridBagConstraints.BOTH;

        jp = new JPanel();
        jp.setLayout(new BoxLayout(jp, BoxLayout.Y_AXIS));

        jsp = new JScrollPane(jp);
        jsp.setLayout(new ScrollPaneLayout());
        jsp.getVerticalScrollBar().setUnitIncrement(20);
        jsp.getHorizontalScrollBar().setUnitIncrement(20);
        jsp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        jsp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(jsp, gbc);

        addMEPProps();
        if (Settings.getPropertyBoolean("isPublicUser")) {
            return;
        }
        addOtherProps();
        addLMProps();
        // addTTHListProps();
    }

    private void addPropsToPanel(JPanel ps, String[] properties, PropertyType[] types, String[] helptxt) {
        for (int i = 0; i < properties.length; i++) {
            Component[] components = PrefsPanelUtil.getComponentsForSetting(properties[i], types[i], helptxt[i]);

            GridBagConstraints gbc1 = new GridBagConstraints();
            gbc1.gridy = i;
            gbc1.gridx = 0;
            gbc1.weightx = 1;
            gbc1.insets = new Insets(10, 10, 0, 0);
            gbc1.fill = GridBagConstraints.HORIZONTAL;

            GridBagConstraints gbc2 = new GridBagConstraints();
            gbc2.gridy = i;
            gbc2.gridx = 1;
            gbc2.weightx = 1;
            gbc2.insets = new Insets(10, 10, 0, 0);
            gbc2.fill = GridBagConstraints.HORIZONTAL;

            ps.add(components[0], gbc1);
            ps.add(components[1], gbc2);

            componentList.add(components[0]);
            componentTypeList.add(types[i]);
            componentKeyList.add(properties[i]);
        }
    }

    private void addOtherProps() {
        JPanel ps = new JPanel();
        ps.setLayout(new GridBagLayout());
        ps.setBorder(BorderFactory.createTitledBorder("Other"));

        String[] properties = {"ExtendedCommand", "AutoLoadShortcuts", "EnableStartup", "EnableMethodsView", "UseLDS",
                "showQuotes", "FreeCommander", "DefaultFigureGraphicsSmoothing"};
        PropertyType[] types = {PropertyType.BOOLEAN, PropertyType.BOOLEAN, PropertyType.BOOLEAN, PropertyType.BOOLEAN, PropertyType.BOOLEAN,
                PropertyType.BOOLEAN, PropertyType.PATH, PropertyType.ONOFF};
        String[] helptxt = {Settings.getProperty("ExtendedCommandHELP"),
                Settings.getProperty("AutoLoadShortcutsHELP"),
                Settings.getProperty("EnableStartupHELP"),
                Settings.getProperty("EnableMethodsViewHELP"),
                Settings.getProperty("UseLDSHELP"),
                Settings.getProperty("showQuotesHELP"),
                Settings.getProperty("FreeCommanderHELP"),
                Settings.getProperty("DefaultFigureGraphicsSmoothingHELP"),
        };

        addPropsToPanel(ps, properties, types, helptxt);
        jp.add(ps);
    }

    private void addMEPProps() {
        JPanel ps = new JPanel();
        ps.setLayout(new GridBagLayout());
        ps.setBorder(BorderFactory.createTitledBorder("Extend Matlab Editor Functionality"));

        String[] properties = {"ExtendedEditor", "EnableAutoBrackets", "EnableOperator", "enableAlphaPhase",
                "verbose", "autoReloadProps", "enableDoubleOperator", "autoDetailViewer", "bpColor"};
        PropertyType[] types = {PropertyType.BOOLEAN, PropertyType.BOOLEAN, PropertyType.BOOLEAN, PropertyType.BOOLEAN,
                PropertyType.BOOLEAN, PropertyType.BOOLEAN, PropertyType.BOOLEAN, PropertyType.BOOLEAN, PropertyType.COLOR};
        String[] helptxt = {Settings.getProperty("ExtendedEditorHELP"),
                Settings.getProperty("EnableAutoBracketsHELP"),
                Settings.getProperty("EnableOperatorHELP"),
                Settings.getProperty("enableAlphaPhaseHELP"),
                Settings.getProperty("verboseHELP"),
                Settings.getProperty("autoReloadPropsHELP"),
                Settings.getProperty("enableDoubleOperatorHELP"),
                Settings.getProperty("autoDetailViewerHELP"),
                Settings.getProperty("bpColorHELP")};

        addPropsToPanel(ps, properties, types, helptxt);

        jp.add(ps);
    }

    private void addLMProps() {
        JPanel ps = new JPanel();
        ps.setLayout(new GridBagLayout());
        ps.setBorder(BorderFactory.createTitledBorder("LoadMessDaten"));

        String[] properties = {"LoadMessDaten_Shortener", "LoadMessDaten_NoExprManipulate", "LoadMessDaten_EnableSimpleView"};
        PropertyType[] types = {PropertyType.BOOLEAN, PropertyType.BOOLEAN, PropertyType.BOOLEAN};
        String[] helptxt = {Settings.getProperty("LoadMessDaten_ShortenerHELP"),
                Settings.getProperty("LoadMessDaten_NoExprManipulateHELP"),
                Settings.getProperty("LoadMessDaten_EnableSimpleViewHELP")};

        addPropsToPanel(ps, properties, types, helptxt);

        jp.add(ps);
    }

    private void addTTHListProps() {
        JPanel ps = new JPanel();
        ps.setLayout(new GridBagLayout());
        ps.setBorder(BorderFactory.createTitledBorder("TT-StundenListe"));

        jp.add(ps);
    }

}