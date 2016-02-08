/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SabotageTanks;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author YTokmakov
 */
public class ConnectionManager {
    
    Server server;
    Client client;
    Game game;
    
    public ConnectionManager(boolean isServer, String ip, int port, Game game)
    {
        this.game = game;
        
        if (isServer)
        {
            try {
                
                server = new Server(port);
                
            } catch (IOException ex) {
                Logger.getLogger(ConnectionManager.class.getName()).log(Level.SEVERE, null, ex);
                JOptionPane.showMessageDialog(null, "Не удалось создать сервер", "Ошибка подключения", JOptionPane.ERROR_MESSAGE);
            }
            
        } else
        {
            
            try {
                
                client = new Client(ip, port);
                
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "Не удалось подключиться к серверу", "Ошибка подключения", JOptionPane.ERROR_MESSAGE);
            }
            
        }
    }
    
    private class Server extends ServerSocket implements Runnable
    {
        Socket clientConnection;
        
        Server(int port) throws IOException
        {
            super(port);
        }

        @Override
        public void run()
        {
            while (true)
            {
                while (clientConnection == null)
                {
                    try {

                        clientConnection = accept();

                    } catch (IOException ex) {
                        Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                
                // Player is connected to the game!
            }
        }
    }
    
    private class Client extends Socket implements Runnable
    {
        Client(String ip, int port) throws IOException
        {
            super(ip, port);
        }

        @Override
        public void run() {
            
            
            
        }
    }
}
