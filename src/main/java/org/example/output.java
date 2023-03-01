/*
 * Created by JFormDesigner on Sat Feb 25 02:43:35 CST 2023
 */

package org.example;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 * @author Jian
 */
public class output extends JFrame {
    public output() {
        initComponents();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        ResourceBundle bundle = ResourceBundle.getBundle("form");
        dialogPane = new JPanel();
        buttonBar = new JPanel();
        okButton = new JButton();
        helpButton = new JButton();
        cancelButton2 = new JButton();
        cancelButton = new JButton();
        cancelButton3 = new JButton();
        scrollPane2 = new JScrollPane();
        textArea1 = new JTextArea();

        //======== this ========
        setTitle(bundle.getString("this.title"));
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
            dialogPane.setLayout(new BorderLayout());

            //======== buttonBar ========
            {
                buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
                buttonBar.setLayout(new BoxLayout(buttonBar, BoxLayout.X_AXIS));

                //---- okButton ----
                okButton.setText(bundle.getString("okButton.text_2"));
                buttonBar.add(okButton);

                //---- helpButton ----
                helpButton.setText(bundle.getString("helpButton.text"));
                buttonBar.add(helpButton);

                //---- cancelButton2 ----
                cancelButton2.setText(bundle.getString("cancelButton2.text"));
                buttonBar.add(cancelButton2);

                //---- cancelButton ----
                cancelButton.setText(bundle.getString("cancelButton.text_2"));
                buttonBar.add(cancelButton);

                //---- cancelButton3 ----
                cancelButton3.setText(bundle.getString("cancelButton3.text"));
                buttonBar.add(cancelButton3);
            }
            dialogPane.add(buttonBar, BorderLayout.SOUTH);

            //======== scrollPane2 ========
            {

                //---- textArea1 ----
                textArea1.setRows(3);
                textArea1.setLineWrap(true);
                textArea1.setBackground(Color.white);
                textArea1.setText(bundle.getString("textArea1.text"));
                scrollPane2.setViewportView(textArea1);
            }
            dialogPane.add(scrollPane2, BorderLayout.CENTER);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    private JPanel dialogPane;
    private JPanel buttonBar;
    private JButton okButton;
    private JButton helpButton;
    private JButton cancelButton2;
    private JButton cancelButton;
    private JButton cancelButton3;
    private JScrollPane scrollPane2;
    private JTextArea textArea1;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
