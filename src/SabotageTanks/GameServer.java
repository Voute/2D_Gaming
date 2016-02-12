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

/**
 *
 * @author ytokmakov
 */
public class GameServer extends Game {
    
    public GameServer(String playerName, ConnectionServer connectionServer)
    {
        super(playerName, connectionServer);
    }

    @Override
    protected void tick() {
        
        TankMovement playerMovement = control.getPlayerMovement();
        
        if (checkPlayerCanMove(playerState.tank, playerMovement))
        {
            playerState.tank.move(playerMovement);
        }
        
        playerState.tank.rotateBarrel(battleField.getCursonPosition());
                
        if (!shellList.isEmpty())
        {
            // клонируем массив снарядов для прорисовки
            ArrayList<Shell> shellsListToDraw = (ArrayList<Shell>)shellList.clone();

            // массив для снарядов, которые вышли за границы фрейма
            ArrayList<Shell> removeList = new ArrayList<Shell>();
            
            for (Shell bullet: shellsListToDraw)
            {
                if (bullet.nextX() <= battleField.gameWidth && bullet.nextY() <= battleField.gameHeight)
                {
                    for (Tank tank: battleField.getTanks())
                    {
                        if ( tank.containsXY(bullet.getX(), bullet.getY()) &&
                             tank.id != bullet.tankId   &&
                             !tank.getIsBursting()
                           )
                        {
                            removeList.add(bullet);
                            battleField.getBurstingTanks().add(tank.setDamaged());
                            break;
                        }
                    }
                } else {
                    // добавляем к удалению из массива
                    removeList.add(bullet);
                }
            }
            // удаляем из массива снарядов вышедшие за границы экрана
            shellList.removeAll(removeList);  
        }
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
            
            for (Shell playerShell:statePlayer.shellList)
            {
                synchronized (gameState.shellList)
                {
                    gameState.shellList.add(playerShell);
                }
            }
            
            synchronized (gameState.tankList)
            {

                Iterator it = gameState.tankList.iterator();
                while (it.hasNext())
                {
                    Tank tank = (Tank)it.next();
                    if (statePlayer.tank.getId() == tank.getId())
                    {
                        // должна быть проверка передвижений клиента!
                        tank.updateStats(statePlayer.tank);
                        break;
                    }
                }

            }
            
        } catch (IOException ex) {
            GameLog.write(ex);
        }
    }
    
}
