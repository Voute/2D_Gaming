/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SabotageTanks;

import SabotageTanks.Control.TankMovement;
import SabotageTanks.GraphicObjects.Shell;
import SabotageTanks.GraphicObjects.Tank;
import SabotageTanks.Net.ConnectionServer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author ytokmakov
 */
public class GameServer extends Game {
    
    private List<Tank> tanksToUpdate;
    private List<Shell> shellsToAdd;
    
    public GameServer(String playerName, ConnectionServer connectionServer)
    {
        super(playerName, connectionServer);
        shellsToAdd = new ArrayList<>();
        tanksToUpdate = new ArrayList<>();
    }

    @Override
    protected void tick() {
        
        if (playerState.tank != null)
        {
            TankMovement playerMovement = control.getPlayerMovement();
        
            playerState.tank.move(playerMovement);      
            gameState.updateTank(playerState.tank);
        }
        
        if (tanksToUpdate != null)
        {
            for(Tank tank:tanksToUpdate)
            {
                gameState.updateTank(tank);
            }
            tanksToUpdate.clear();
        }

        if (shellsToAdd != null && !shellsToAdd.isEmpty())
        {
            gameState.getShells().addAll(shellsToAdd);
            shellsToAdd.clear();
        }
        
        // массив для снарядов, которые вышли за границы фрейма
        ArrayList<Shell> removeShellList = new ArrayList<Shell>();

        for (Shell shell: gameState.getShells())
        {
            int shellX = shell.nextX();
            int shellY = shell.nextY();
            
            if (shellX <= getWidth() && shellY <= getHeight()
                && shellX >= 0 && shellY >= 0)
            {
                for (Tank tank: gameState.getTanks())
                {
                    if ( tank.containsXY(shell.getX(), shell.getY()) &&
                         !tank.getId().matches(shell.getId())  &&
                         !tank.getBursting()
                       )
                    {
                        removeShellList.add(shell);
                        tank.setBursting(true);
                        break;
                    }
                }
            } else {
                // добавляем к удалению из массива
                removeShellList.add(shell);
            }
        }
        // удаляем из массива снарядов вышедшие за границы экрана
        gameState.getShells().removeAll(removeShellList);  
    
    }

    @Override
    protected void sendState()
    {
        connection.sendState(gameState);
    }

    @Override
    protected void receiveState()
    {
        try {
            StatePlayer statePlayer = (StatePlayer)connection.receiveState();
            
            shellsToAdd.addAll(statePlayer.shellList);
                    
            tanksToUpdate.add(statePlayer.tank);
            
        } catch (IOException ex) {
            GameLog.write(ex);
        }
        
        if (playerState.tank != null)
        {
            Tank stateTank = gameState.getTank(playerState.tank.getId());
            if (stateTank != null)
            {
                playerState.tank.updateStats(stateTank);
            }
            
            shellsToAdd.addAll(playerState.shellList);
            playerState.shellList.clear();
            
        }
    }
    
}
