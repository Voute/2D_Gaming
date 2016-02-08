/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SabotageTanks;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

/**
 *
 * @author YTokmakov
 */
public class StartFrame extends JFrame implements ActionListener
{
    
    private final int FRAME_HEIGHT = 100
                     ,FRAME_WIDTH = 230
                     ;
    
    private JPanel rootPanel;
    private ButtonGroup radioGroup;
    private JRadioButton serverRadio
                        ,clientRadio
                        ;
    private JButton startButton;
    private JTextField portField
                      ,ipField
                      ;
    
    ConnectionManager connectManager;
    
    public StartFrame(String title)
    {
        super(title);
        setPreferredSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        
        portField = new JTextField();
        portField.setColumns(4);
        portField.setText("5005");
        
        ipField = new JTextField();
        ipField.setColumns(13);
        ipField.setText("192.168.16.5");
        
        serverRadio = new JRadioButton("server");
        serverRadio.setSelected(true);
        clientRadio = new JRadioButton("client");
        
        radioGroup = new ButtonGroup();
        radioGroup.add(serverRadio);
        radioGroup.add(clientRadio);     
        
        startButton = new JButton("start");
        startButton.addActionListener(this);
        
        rootPanel = new JPanel(new FlowLayout());
        rootPanel.add(ipField);
        rootPanel.add(portField);
        rootPanel.add(serverRadio);
        rootPanel.add(clientRadio);
        rootPanel.add(startButton);
        
        setContentPane(rootPanel);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {   
        connectManager = new ConnectionManager(serverRadio.isSelected(),
                                               ipField.getText(),
                                               Integer.valueOf(portField.getText()),
                                               new Game()
                                               );
        setVisible(false);
    }
    
}
