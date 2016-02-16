/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SabotageTanks.Interface;

import SabotageTanks.Game;
import SabotageTanks.GameClient;
import SabotageTanks.GameLog;
import SabotageTanks.GameServer;
import SabotageTanks.Net.Connection;
import SabotageTanks.Net.ConnectionClient;
import SabotageTanks.Net.ConnectionServer;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.URLDecoder;
import java.net.UnknownHostException;
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
    private final String TITLE = "Welcome to Sabotage";
    private final int FRAME_HEIGHT = 120,
                      FRAME_WIDTH = 250;
                     
    
    private JPanel rootPanel;
    private ButtonGroup radioGroup;
    private JRadioButton serverRadio,
                         clientRadio;
                        
    private JButton startButton;
    private JTextField portField,
                       nameField,
                       ipField;
                      
    private Game game;
    
    public static void main(String[] args)       
    {
        new StartFrame().setVisible(true);
    }
    
    public StartFrame()
    {
        super();
        
        try {
            String jarPath = StartFrame.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
            jarPath = URLDecoder.decode(jarPath, "UTF-8");
            jarPath = jarPath.substring(1);
            GameLog.initiate(jarPath);
        } catch (Exception ex) {
            GameLog.write(ex);
            ShowMessage.initiatingLogFail();
        }
        
        setTitle(TITLE);
        setPreferredSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        
        nameField = new JTextField();
        nameField.setColumns(20);
        nameField.setText("playerName");
        
        portField = new JTextField();
        portField.setColumns(4);
        portField.setText("5005");
        
        ipField = new JTextField();
        ipField.setColumns(14);
        try {
            ipField.setText(Inet4Address.getLocalHost().getHostAddress());
        } catch (UnknownHostException ex) {
            GameLog.write(ex);
        }
        
        
        serverRadio = new JRadioButton("server");
        serverRadio.setSelected(true);
        clientRadio = new JRadioButton("client");
        
        radioGroup = new ButtonGroup();
        radioGroup.add(serverRadio);
        radioGroup.add(clientRadio);     
        
        startButton = new JButton("start");
        startButton.addActionListener(this);
        
        rootPanel = new JPanel(new FlowLayout());
        rootPanel.add(nameField);
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
        String port = portField.getText();
        String ip = ipField.getText();
        String playerName = nameField.getText();
        
        if      (playerName == "") { ShowMessage.nameIsEmpty(); }
        else if (port == "" || portField.getColumns() != port.length())
        {
            ShowMessage.nameIsEmpty();
        }
        else if (ip == "" 
//                || 14 != ip.length()
                )
        {
            ShowMessage.ipIsEmpty();
        }
        else 
        {
            int intPort = Integer.getInteger(port, 5005);
            
            if (serverRadio.isSelected())
            {
                try {
                    ConnectionServer connectionServer = new ConnectionServer(intPort);
                    game = new GameServer(playerName, connectionServer);
                } catch (IOException ex) {
                    GameLog.write(ex);
                    ShowMessage.startingServerFail();
                }
            } else
            {
                try {
                    ConnectionClient connectionClient = new ConnectionClient(ip, intPort);
                    game = new GameClient(playerName, connectionClient);
                } catch (IOException ex) {
                    GameLog.write(ex);
                    ShowMessage.connectingServerFail();
                }
            }
            
            if (game != null)
            {
                setVisible(false);
                game.start();
            }
            
        } 
    }
    
}
