/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SabotageTanks;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author YTokmakov
 */
public class ConnectionManager
{
    Gson gson;
    Server server;
    Client client;
    BattleField battleField;
    
    public ConnectionManager(boolean isServer, String ip, int port, BattleField battleField)
    {
        gson = new Gson();
        this.battleField = battleField;
        
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
    
    public void sendToServerBattlefieldState(BattleFieldState state)
    {
        client.send(gson.toJson(state));
    }
    
    public void sendToClientBattlefieldState(BattleFieldState state)
    {
        server.send(gson.toJson(state));
    }   
    
    public BattleFieldState receiveServerBattlefieldState()
    {
        try
        {
            return gson.fromJson(client.receive(), BattleFieldState.class);
        } catch (NullPointerException ex)
        {
//            System.out.println("returned null from Server");
            return null;
        }
    }
    
    public BattleFieldState receiveClientBattlefieldState()
    {
        try
        {
            BattleFieldState state = gson.fromJson(server.receive(), BattleFieldState.class);
            if (state != null)
            {
                return state;
            } else return null;
        } catch (NullPointerException ex)
        {
//            System.out.println("returned null from Client");
            return null;
        }
    }   
    
    private class Server extends ServerSocket implements Runnable
    {      
        private InputStreamReader streamReader;
        private BufferedReader bufferedReader;
        private PrintWriter printWriter;
        
        private Socket clientConnection;
        
        Server(int port) throws IOException
        {
            super(port);
            new Thread(this).start();
        }

        @Override
        public void run()
        {
//            while (true)
//            {
//                while (clientConnection == null)
//                {
                    try {

                        clientConnection = accept();
                        System.out.println("client connected");
                        streamReader = new InputStreamReader(clientConnection.getInputStream());
                        bufferedReader = new BufferedReader(streamReader);
                        printWriter = new PrintWriter(clientConnection.getOutputStream()); 
                        
                    } catch (IOException ex) {
                        Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
                    }
//                }
                
                // Player is connected to the game!
//            }
        }
        
        public void send(String str)
        {
            if (printWriter != null)
            {
                printWriter.println(str);
                printWriter.flush();
            }
        }
        
        public String receive()
        {
            try {
                if (bufferedReader.ready())
                {
                    String s = bufferedReader.readLine();
                    System.out.println("from client:" + s);
                    return s;
                }
            } catch (IOException ex) {
                Logger.getLogger(ConnectionManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null;
        }
    }
    
    private class Client extends Socket implements Runnable
    {
        private InputStreamReader streamReader;
        private BufferedReader bufferedReader;
        private PrintWriter printWriter;
        
        Client(String ip, int port) throws IOException
        {
            super(ip, port);
            streamReader = new InputStreamReader(getInputStream());
            bufferedReader = new BufferedReader(streamReader);
            printWriter = new PrintWriter(getOutputStream()); 
        }

        @Override
        public void run() {
            
            
            
        }
        
        public void send(String str)
        {
            if (printWriter != null)
            {
                printWriter.println(str);
                printWriter.flush();
            }
        }
        
        public String receive()
        {
            try {
                return bufferedReader.readLine();
            } catch (IOException ex) {
                Logger.getLogger(ConnectionManager.class.getName()).log(Level.SEVERE, null, ex);
            }
            return null;
        }
    }
}
