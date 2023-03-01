/*
 * Created by JFormDesigner on Fri Jan 06 11:02:21 CST 2023
 */

package org.example;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 * @author Jian
 */
public class FormMain extends JFrame {
    public FormMain() {
        initComponents();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        ResourceBundle bundle = ResourceBundle.getBundle("form");
        menuBar2 = new JMenuBar();
        menuButtonFile = new JMenu();
        menuButtonNew = new JMenuItem();
        menuButtonOpen = new JMenuItem();
        menuButtonSave = new JMenuItem();
        menuButtonSaveAs = new JMenuItem();
        menuButtonExit = new JMenuItem();
        menuButtonEdit = new JMenu();
        menuButtonView = new JMenu();
        menuButtonExamples = new JMenu();
        dialogPane = new JPanel();
        buttonBar = new JPanel();
        okButton = new JButton();
        cancelButton = new JButton();
        topToolStripPanel = new JSplitPane();
        standardToolBar = new JToolBar();
        toolbarButtonNew = new JButton();
        toolbarButtonOpen = new JButton();
        toolbarButtonSave = new JButton();
        button8toolbarButtonSaveAs = new JButton();
        toolbarButtonCut = new JButton();
        toolbarButtonCopy = new JButton();
        toolbarButtonPaste = new JButton();
        toolbarButtonUndo = new JButton();
        toolbarButtonRedo = new JButton();
        functionalToolBar = new JToolBar();
        toolbarButtonSpecification = new JButton();
        toolbarButtonCheckGrammar = new JButton();
        toolbarButtonSimulation = new JButton();
        toolbarButtonVerification = new JButton();

        //======== this ========
        setTitle("Timed CSP-J Model Checker");
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== menuBar2 ========
        {

            //======== menuButtonFile ========
            {
                menuButtonFile.setText(bundle.getString("menuButtonFile.text"));

                //---- menuButtonNew ----
                menuButtonNew.setText(bundle.getString("menuButtonNew.text"));
                menuButtonFile.add(menuButtonNew);

                //---- menuButtonOpen ----
                menuButtonOpen.setText(bundle.getString("menuButtonOpen.text"));
                menuButtonFile.add(menuButtonOpen);

                //---- menuButtonSave ----
                menuButtonSave.setText(bundle.getString("menuButtonSave.text"));
                menuButtonFile.add(menuButtonSave);

                //---- menuButtonSaveAs ----
                menuButtonSaveAs.setText(bundle.getString("menuButtonSaveAs.text"));
                menuButtonFile.add(menuButtonSaveAs);

                //---- menuButtonExit ----
                menuButtonExit.setText(bundle.getString("menuButtonExit.text"));
                menuButtonFile.add(menuButtonExit);
            }
            menuBar2.add(menuButtonFile);

            //======== menuButtonEdit ========
            {
                menuButtonEdit.setText(bundle.getString("menuButtonEdit.text"));
            }
            menuBar2.add(menuButtonEdit);

            //======== menuButtonView ========
            {
                menuButtonView.setText(bundle.getString("menuButtonView.text"));
            }
            menuBar2.add(menuButtonView);

            //======== menuButtonExamples ========
            {
                menuButtonExamples.setText(bundle.getString("menuButtonExamples.text"));
            }
            menuBar2.add(menuButtonExamples);
        }
        setJMenuBar(menuBar2);

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
            dialogPane.setLayout(new BorderLayout());

            //======== buttonBar ========
            {
                buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
                buttonBar.setLayout(new GridBagLayout());
                ((GridBagLayout)buttonBar.getLayout()).columnWidths = new int[] {0, 85, 80};
                ((GridBagLayout)buttonBar.getLayout()).columnWeights = new double[] {1.0, 0.0, 0.0};

                //---- okButton ----
                okButton.setText(bundle.getString("okButton.text"));
                buttonBar.add(okButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 5), 0, 0));

                //---- cancelButton ----
                cancelButton.setText(bundle.getString("cancelButton.text"));
                buttonBar.add(cancelButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(buttonBar, BorderLayout.SOUTH);
        }
        contentPane.add(dialogPane, BorderLayout.SOUTH);

        //======== topToolStripPanel ========
        {
            topToolStripPanel.setOrientation(JSplitPane.VERTICAL_SPLIT);

            //======== standardToolBar ========
            {
                standardToolBar.setMinimumSize(new Dimension(446, 34));
                standardToolBar.setMaximumSize(new Dimension(446, 34));
                standardToolBar.setPreferredSize(new Dimension(446, 34));

                //---- toolbarButtonNew ----
                toolbarButtonNew.setToolTipText("New");
                toolbarButtonNew.setIcon(new ImageIcon(getClass().getResource("/newfile.png")));
                standardToolBar.add(toolbarButtonNew);

                //---- toolbarButtonOpen ----
                toolbarButtonOpen.setText(" ");
                toolbarButtonOpen.setIcon(new ImageIcon(getClass().getResource("/openfile.png")));
                standardToolBar.add(toolbarButtonOpen);

                //---- toolbarButtonSave ----
                toolbarButtonSave.setIcon(new ImageIcon(getClass().getResource("/save.png")));
                standardToolBar.add(toolbarButtonSave);

                //---- button8toolbarButtonSaveAs ----
                button8toolbarButtonSaveAs.setIcon(new ImageIcon(getClass().getResource("/saveas.png")));
                standardToolBar.add(button8toolbarButtonSaveAs);

                //---- toolbarButtonCut ----
                toolbarButtonCut.setIcon(new ImageIcon(getClass().getResource("/cut.png")));
                standardToolBar.add(toolbarButtonCut);

                //---- toolbarButtonCopy ----
                toolbarButtonCopy.setIcon(new ImageIcon(getClass().getResource("/copy.png")));
                standardToolBar.add(toolbarButtonCopy);

                //---- toolbarButtonPaste ----
                toolbarButtonPaste.setIcon(new ImageIcon(getClass().getResource("/paste.png")));
                standardToolBar.add(toolbarButtonPaste);

                //---- toolbarButtonUndo ----
                toolbarButtonUndo.setIcon(new ImageIcon(getClass().getResource("/undo.png")));
                standardToolBar.add(toolbarButtonUndo);

                //---- toolbarButtonRedo ----
                toolbarButtonRedo.setIcon(new ImageIcon(getClass().getResource("/redo.png")));
                standardToolBar.add(toolbarButtonRedo);
            }
            topToolStripPanel.setTopComponent(standardToolBar);

            //======== functionalToolBar ========
            {

                //---- toolbarButtonSpecification ----
                toolbarButtonSpecification.setText(bundle.getString("toolbarButtonSpecification.text"));
                functionalToolBar.add(toolbarButtonSpecification);

                //---- toolbarButtonCheckGrammar ----
                toolbarButtonCheckGrammar.setText(bundle.getString("toolbarButtonCheckGrammar.text"));
                functionalToolBar.add(toolbarButtonCheckGrammar);

                //---- toolbarButtonSimulation ----
                toolbarButtonSimulation.setText(bundle.getString("toolbarButtonSimulation.text"));
                functionalToolBar.add(toolbarButtonSimulation);

                //---- toolbarButtonVerification ----
                toolbarButtonVerification.setText(bundle.getString("toolbarButtonVerification.text"));
                functionalToolBar.add(toolbarButtonVerification);
            }
            topToolStripPanel.setBottomComponent(functionalToolBar);
        }
        contentPane.add(topToolStripPanel, BorderLayout.NORTH);
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    private JMenuBar menuBar2;
    private JMenu menuButtonFile;
    private JMenuItem menuButtonNew;
    private JMenuItem menuButtonOpen;
    private JMenuItem menuButtonSave;
    private JMenuItem menuButtonSaveAs;
    private JMenuItem menuButtonExit;
    private JMenu menuButtonEdit;
    private JMenu menuButtonView;
    private JMenu menuButtonExamples;
    private JPanel dialogPane;
    private JPanel buttonBar;
    private JButton okButton;
    private JButton cancelButton;
    private JSplitPane topToolStripPanel;
    private JToolBar standardToolBar;
    private JButton toolbarButtonNew;
    private JButton toolbarButtonOpen;
    private JButton toolbarButtonSave;
    private JButton button8toolbarButtonSaveAs;
    private JButton toolbarButtonCut;
    private JButton toolbarButtonCopy;
    private JButton toolbarButtonPaste;
    private JButton toolbarButtonUndo;
    private JButton toolbarButtonRedo;
    private JToolBar functionalToolBar;
    private JButton toolbarButtonSpecification;
    private JButton toolbarButtonCheckGrammar;
    private JButton toolbarButtonSimulation;
    private JButton toolbarButtonVerification;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
    public static void main(String[] args) {
        new FormMain().setVisible(true);
    }
}
