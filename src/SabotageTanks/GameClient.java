/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SabotageTanks;

import SabotageTanks.Control.TankMovement;
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
        super(playerName + "1", connectionServer);
    }
    
    @Override
    protected void tick()  
    {
        TankMovement playerMovement = control.getPlayerMovement();
        if (playerState.tank != null)
        {
            playerState.tank.move(playerMovement); 
        }
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
            if (gameState != null && playerState.tank != null)
            {
                Tank updatedTank = gameState.getTank(playerState.tank.getId());
                if (updatedTank != null)
                {
                    playerState.tank.updateStats(updatedTank);               
                }
            }
            
        } catch (IOException ex) {
            GameLog.write(ex);
        }
    }
    
}
