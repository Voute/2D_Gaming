/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SabotageTanks;

import SabotageTanks.Tanks.Shell;
import SabotageTanks.Tanks.Tank;
import java.util.ArrayList;

/**
 *
 * @author YTokmakov
 */
public class ShellManager {
    
    private ArrayList<Shell> shellList;        // массив снарядов
    
    public ShellManager ()
    {
        shellList = new ArrayList<Shell>();
    }
    
    public void makeShell(Tank tank, int targetX, int targetY)
    {
        try {
            shellList.add(new Shell(tank.getX(),
                                     targetX,
                                     tank.getY(),
                                     targetY,
                                     tank.XbarrelTip,
                                     tank.YbarrelTip,
                                     tank.id
            ));
        } catch (Exception ex) { }      // если выстрел был совершен в центре квадрата
    }
    
//    public ArrayList<Shell> getShells()
//    {
//        return shellList;
//    }
    public ArrayList<Shell> getShellsToDraw(int gameWidth,
                                            int gameHeight,
                                            BattleField battleField
                                            )
    {
        if (!shellList.isEmpty())
        {
            // клонируем массив снарядов для прорисовки
            ArrayList<Shell> shellsListToDraw = (ArrayList<Shell>)shellList.clone();

            // массив для снарядов, которые вышли за границы фрейма
            ArrayList<Shell> removeList = new ArrayList<Shell>();
            
            for (Shell bullet: shellsListToDraw)
            {
                if (bullet.nextX() <= gameWidth && bullet.nextY() <= gameHeight)
                {
                    for (Tank tank: battleField.getTanks())
                    {
                        if ( tank.containsXY(bullet.getX(), bullet.getY()) &&
                             tank.id != bullet.tankId   &&
                             !tank.isDamaged()
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
            
            return shellsListToDraw;
                    
        } else
        {
            return null;
        }
    }
}
