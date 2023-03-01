/*
 * Created by JFormDesigner on Sun Feb 26 10:36:15 CST 2023
 */

package org.example;

import java.awt.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import com.jgoodies.forms.factories.*;

/**
 * @author Jian
 */
public class verify extends JFrame {
    public verify() {
        initComponents();
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents  @formatter:off
        ResourceBundle bundle = ResourceBundle.getBundle("form");
        DefaultComponentFactory compFactory = DefaultComponentFactory.getInstance();
        dialogPane = new JPanel();
        contentPanel = new JPanel();
        buttonBar = new JPanel();
        okButton = new JButton();
        cancelButton = new JButton();
        helpButton = new JButton();
        scrollPane1 = new JScrollPane();
        panel2 = new JPanel();
        table1 = new JTable();
        panel1 = new JPanel();
        panel4 = new JPanel();
        panel5 = new JPanel();
        formattedTextField1 = new JFormattedTextField();
        button1 = new JButton();
        scrollPane2 = new JScrollPane();
        textArea1 = new JTextArea();
        panel3 = new JPanel();
        separator1 = compFactory.createSeparator(bundle.getString("separator1.text"));

        //======== this ========
        setTitle(bundle.getString("this.title_2"));
        setIconImage(new ImageIcon(getClass().getResource("/ver3.png")).getImage());
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());

        //======== dialogPane ========
        {
            dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
            dialogPane.setLayout(new BorderLayout());

            //======== contentPanel ========
            {
                contentPanel.setLayout(new FlowLayout());
            }
            dialogPane.add(contentPanel, BorderLayout.CENTER);

            //======== buttonBar ========
            {
                buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
                buttonBar.setLayout(new GridBagLayout());
                ((GridBagLayout)buttonBar.getLayout()).columnWidths = new int[] {492, 85, 85, 80};
                ((GridBagLayout)buttonBar.getLayout()).columnWeights = new double[] {1.0, 0.0, 0.0, 0.0};

                //---- okButton ----
                okButton.setText(bundle.getString("okButton.text_3"));
                buttonBar.add(okButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 5), 0, 0));

                //---- cancelButton ----
                cancelButton.setText(bundle.getString("cancelButton.text_3"));
                buttonBar.add(cancelButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 5), 0, 0));

                //---- helpButton ----
                helpButton.setText(bundle.getString("helpButton.text_2"));
                buttonBar.add(helpButton, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
                    GridBagConstraints.CENTER, GridBagConstraints.BOTH,
                    new Insets(0, 0, 0, 0), 0, 0));
            }
            dialogPane.add(buttonBar, BorderLayout.SOUTH);

            //======== scrollPane1 ========
            {

                //======== panel2 ========
                {
                    panel2.setLayout(new BorderLayout());

                    //---- table1 ----
                    table1.setModel(new DefaultTableModel(
                        new Object[][] {
                            {"\u2460", "System deadlockfree;", true},
                            {"\u2461", "System divergencefree;", false},
                            {"\u2462", "System |= []guard;", false},
                            {"\u2463", "System |= [](turnOn -> <> turnOff);", false},
                            {"\u2464", "System reaches firststate;", false},
                            {"\u2465", "System reaches secondstate;", false},
                        },
                        new String[] {
                            " No.", "Assertions", " "
                        }
                    ) {
                        Class<?>[] columnTypes = new Class<?>[] {
                            Object.class, Object.class, Boolean.class
                        };
                        @Override
                        public Class<?> getColumnClass(int columnIndex) {
                            return columnTypes[columnIndex];
                        }
                    });
                    {
                        TableColumnModel cm = table1.getColumnModel();
                        cm.getColumn(0).setResizable(false);
                        cm.getColumn(0).setMaxWidth(25);
                        cm.getColumn(2).setMaxWidth(40);
                    }
                    table1.setToolTipText(" ");
                    table1.setCellSelectionEnabled(true);
                    table1.setFont(UIManager.getFont("Button.font"));
                    table1.setPreferredScrollableViewportSize(new Dimension(450, 120));
                    panel2.add(table1, BorderLayout.CENTER);
                }
                scrollPane1.setViewportView(panel2);
            }
            dialogPane.add(scrollPane1, BorderLayout.NORTH);

            //======== panel1 ========
            {
                panel1.setLayout(new BorderLayout());

                //======== panel4 ========
                {
                    panel4.setLayout(new BorderLayout());

                    //======== panel5 ========
                    {
                        panel5.setLayout(new BorderLayout());

                        //---- formattedTextField1 ----
                        formattedTextField1.setText(bundle.getString("formattedTextField1.text"));
                        formattedTextField1.setPreferredSize(new Dimension(160, 30));
                        panel5.add(formattedTextField1, BorderLayout.CENTER);

                        //---- button1 ----
                        button1.setIcon(new ImageIcon(getClass().getResource("/veri.png")));
                        button1.setPreferredSize(new Dimension(34, 30));
                        panel5.add(button1, BorderLayout.WEST);
                    }
                    panel4.add(panel5, BorderLayout.NORTH);

                    //======== scrollPane2 ========
                    {

                        //---- textArea1 ----
                        textArea1.setText(bundle.getString("textArea1.text_2"));
                        textArea1.setPreferredSize(new Dimension(785, 19));
                        textArea1.setFont(new Font("JetBrains Mono Medium", textArea1.getFont().getStyle() & ~Font.BOLD, textArea1.getFont().getSize()));
                        textArea1.setForeground(Color.darkGray);
                        textArea1.setBackground(Color.white);
                        scrollPane2.setViewportView(textArea1);
                    }
                    panel4.add(scrollPane2, BorderLayout.CENTER);
                }
                panel1.add(panel4, BorderLayout.CENTER);

                //======== panel3 ========
                {
                    panel3.setLayout(new BorderLayout());
                    panel3.add(separator1, BorderLayout.CENTER);
                }
                panel1.add(panel3, BorderLayout.NORTH);
            }
            dialogPane.add(panel1, BorderLayout.WEST);
        }
        contentPane.add(dialogPane, BorderLayout.CENTER);
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents  @formatter:on
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables  @formatter:off
    private JPanel dialogPane;
    private JPanel contentPanel;
    private JPanel buttonBar;
    private JButton okButton;
    private JButton cancelButton;
    private JButton helpButton;
    private JScrollPane scrollPane1;
    private JPanel panel2;
    private JTable table1;
    private JPanel panel1;
    private JPanel panel4;
    private JPanel panel5;
    private JFormattedTextField formattedTextField1;
    private JButton button1;
    private JScrollPane scrollPane2;
    private JTextArea textArea1;
    private JPanel panel3;
    private JComponent separator1;
    // JFormDesigner - End of variables declaration  //GEN-END:variables  @formatter:on
}
