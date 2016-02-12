/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SabotageTanks;

import SabotageTanks.GraphicObjects.Tank;
import SabotageTanks.Net.ConnectionClient;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ytokmakov
 */
public class GameClient extends Game{

    public GameClient(String playerName, ConnectionClient connectionServer)
    {
        super(playerName, connectionServer);
    }
    
    @Override
    protected void tick()  
    {
        //nothing to do on the client side
    }

    @Override
    protected void sendState()
    {
        connection.sendState(playerState);
        playerState.shellList.clear();
    }

    @Override
    protected void receiveState()
    {
        try {
            gameState = (StateServer)connection.receiveState();
        } catch (IOException ex) {
            GameLog.write(ex);
        }
    }
    
}
