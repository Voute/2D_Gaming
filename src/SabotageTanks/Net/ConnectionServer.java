/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SabotageTanks.Net;

import SabotageTanks.GameLog;
import SabotageTanks.State;
import SabotageTanks.StatePlayer;
import SabotageTanks.StateServer;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author ytokmakov
 */
public final class ConnectionServer extends Connection implements Runnable {

    private final ServerSocket serverSocket;
    
    public ConnectionServer(int port) throws IOException
    {
        super();
        serverSocket = new ServerSocket(port);
        new Thread(this).start();
    }
    
    @Override
    public void run()
    {
        try {
            Socket socket = serverSocket.accept();
            String message = "client connected, ip " + socket.getInetAddress().getHostAddress();
            System.out.println(message);
            GameLog.write(message);
            initiateNegotiation(socket, serverSocket.getInetAddress().getHostAddress(), serverSocket.getLocalPort());
        } catch (IOException ex) {
            GameLog.write(ex);
        }
    }

    @Override
    public void sendState(State state)
    {
        send( gson.toJson((StateServer)state) ); 
    }

    @Override
    public State receiveState() throws IOException
    {
        return gson.fromJson(receive(), StatePlayer.class);
    }
    
}
