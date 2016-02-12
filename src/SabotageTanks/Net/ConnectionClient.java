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
import java.net.Socket;

/**
 *
 * @author ytokmakov
 */
public final class ConnectionClient extends Connection {

    public ConnectionClient(String ip, int port) throws IOException
    {
        super();
        Socket socket =  new Socket(ip, port);
        String message = "connected to server, ip " + socket.getInetAddress().getHostAddress();
        System.out.println(message);
        GameLog.write(message);
        initiateNegotiation(socket, socket.getInetAddress().getHostAddress(), socket.getLocalPort());
    }

    @Override
    public void sendState(State state)
    {
        send(gson.toJson((StatePlayer)state) );
    }

    @Override
    public State receiveState() throws IOException
    {
        return gson.fromJson(receive(), StateServer.class);
    }
    
}
