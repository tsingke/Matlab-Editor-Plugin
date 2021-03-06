package at.mep.gui.fileStructure;

import at.mep.util.ColorUtils;
import at.mep.util.EIconDecorator;
import com.mathworks.widgets.text.mcode.MTree;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;
import java.util.ArrayList;
import java.util.EnumSet;

/** Created by Andreas Justin on 2016 - 02 - 25. */
class TreeRenderer extends DefaultTreeCellRenderer {
    private static final Color INVALID_COLOR = new Color(133, 133, 133);
    private static final Color HIDDEN_COLOR = new Color(180, 100, 115);
    private static final Color TRANSIENT_COLOR = new Color(180, 179, 172, 125);
    private static final Color META_COLOR = new Color(100, 115, 180);
    // private DefaultTreeCellRenderer defaultRenderer = new DefaultTreeCellRenderer();

    TreeRenderer() {
        // Color backgroundSelectionColor = defaultRenderer.getBackgroundSelectionColor();
        // Color backgroundNonSelectionColor = defaultRenderer.getBackgroundNonSelectionColor();
    }
    
    @Override
    public Component getTreeCellRendererComponent(
            JTree jTree, Object value, boolean sel, boolean exp, boolean leaf, int row, boolean hasFocus) {
        Component c = super.getTreeCellRendererComponent(jTree, value, sel, exp, leaf, row, hasFocus);
        NodeFS nodeFS = (NodeFS) value;
        String nodeStringU = nodeFS.nodeText();
        String nodeStringL = nodeStringU.toLowerCase();

        if (row == 0 && nodeStringL.endsWith(".m")) {
            setIcon(EIconsFileStructure.MFILE.getIcon());
        } else if (nodeFS.getType() == MTree.NodeType.CLASSDEF) {
            // CLASS
            setIcon(EIconsFileStructure.CLASS.getIcon());
        } else if (EnumSet.of(MTree.NodeType.FUNCTION, MTree.NodeType.EQUALS).contains(nodeFS.getType())) {
            // METHOD, FUNCTION, PROPERTY
            setFunctionPropertyIcon(nodeFS);
        } else if (nodeFS.getType() == MTree.NodeType.CELL_TITLE) {
            // SECTION
            setIcon(EIconsFileStructure.CELL.getIcon());
        }
        setText(nodeStringU);
        setFont(new Font("Courier New", Font.PLAIN, 11));
        if (nodeFS.isInherited()) {
            setForeground(new Color(100,100,100));
        } else {
            setForeground(new Color(0,0,0));
        }

        return c;
    }

    private void setFunctionPropertyIcon(NodeFS nodeFS) {
        java.util.List<Icon> decorators = new ArrayList<>(2);
        java.util.List<Color> colors = new ArrayList<>(2);
        java.util.List<EIconDecorator> positions = new ArrayList<>(2);

        switch (nodeFS.getAccess()) {
            case INVALID: {
                decorators.add(EIconsFileStructure.DECORATOR_INVALID.getIcon());
                colors.add(INVALID_COLOR);
                positions.add(EIconDecorator.EAST_OUTSIDE);
                break;
            }
            case PRIVATE: {
                decorators.add(EIconsFileStructure.DECORATOR_PRIVATE.getIcon());
                positions.add(EIconDecorator.EAST_OUTSIDE);
                break;
            }
            case PROTECTED: {
                decorators.add(EIconsFileStructure.DECORATOR_PROTECTED.getIcon());
                positions.add(EIconDecorator.EAST_OUTSIDE);
                break;
            }
            case PUBLIC: {
                decorators.add(EIconsFileStructure.DECORATOR_PUBLIC.getIcon());
                positions.add(EIconDecorator.EAST_OUTSIDE);
                break;
            }
            case META: {
                decorators.add(EIconsFileStructure.DECORATOR_META.getIcon());
                colors.add(META_COLOR);
                positions.add(EIconDecorator.EAST_OUTSIDE);
                break;
            }
        }

        if (nodeFS.isHidden() && colors.size() < decorators.size()) {
            colors.add(HIDDEN_COLOR);
        } else if (nodeFS.isHidden() && colors.size() == 1) {
            ColorUtils.mixColors(HIDDEN_COLOR, colors.get(0), 0.5f);
        } else {
            if (colors.size() < decorators.size()) {
                colors.add(null);
            }
        }

        if (nodeFS.isStatic()) {
            decorators.add(EIconsFileStructure.DECORATOR_STATIC.getIcon());
            colors.add(null);
            positions.add(EIconDecorator.SOUTH_WEST_INSIDE);
        }

        if (decorators.size() != colors.size() || decorators.size() != positions.size()) {
            StringBuilder sb = new StringBuilder(200);
            sb.append("TreeRenderer:DimensionMismatch ").append("nodeText: ").append(nodeFS.nodeText());
            sb.append("\ndecorators size: ").append(decorators.size());
            sb.append("\ncolors size: ").append(colors.size());
            sb.append("\npositions size: ").append(positions.size());
            System.out.println(sb.toString());
        }

        Icon icon;
        if (nodeFS.isProperty()) {
            if (nodeFS.isTransient()) {
                icon = EIconsFileStructure.PROPERTY.getIcon(TRANSIENT_COLOR, decorators, colors, positions);
            } else {
                icon = EIconsFileStructure.PROPERTY.getIcon(null, decorators, colors, positions);
            }
        } else {
            icon = EIconsFileStructure.METHOD.getIcon(null, decorators, colors, positions);
        }
        setIcon(icon);
    }
}
