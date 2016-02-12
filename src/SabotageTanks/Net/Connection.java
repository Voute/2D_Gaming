/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SabotageTanks.Net;

import SabotageTanks.State;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 *
 * @author ytokmakov
 */
public abstract class Connection
{
    protected final Gson gson;
    private InputStreamReader streamReader;
    private BufferedReader bufferedReader;
    private PrintWriter printWriter;
    private Socket socket;
    private String localIp;
    private int localPort;
    
    Connection()
    {
        gson = new Gson();
    }
    
    public abstract void sendState(State state);
    public abstract State receiveState() throws IOException;
    
    public String getLocalIp()
    {
        return localIp;
    }
    
    public int getLocalPort()
    {
        return localPort;
    }
    
    protected void send(String data)
    {
        if (printWriter != null)
        {
            printWriter.println(data);
            printWriter.flush();
        }
    }
    protected String receive() throws IOException
    {
        if (bufferedReader != null && bufferedReader.ready())
        {
            String s;
            if ( (s = bufferedReader.readLine()) != null )
            {
                return s;
            } else
            {
                throw new IOException();
            }
        } else
        {
            throw new IOException();
        }
    }
    protected void initiateNegotiation(Socket createdSocket, String localIp, int localPort) throws IOException
    {
        this.localIp = localIp;
        this.localPort = localPort;
        socket = createdSocket;
        streamReader = new InputStreamReader(socket.getInputStream());
        bufferedReader = new BufferedReader(streamReader);
        printWriter = new PrintWriter(socket.getOutputStream());
    }
}
