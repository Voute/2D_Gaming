/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SabotageTanks;

import SabotageTanks.Tanks.BurstingTank;
import SabotageTanks.Tanks.Tank;
import SabotageTanks.Tanks.TankMovement;
import java.awt.Color;
import java.util.ArrayList;

public class BattleField {
    
    //  цвета квадратов    
    private final Color[] TANK_COLORS = {Color.GRAY,
                                         Color.YELLOW,
                                         Color.BLUE,
                                         Color.MAGENTA, 
                                         Color.ORANGE,
                                         Color.CYAN,
                                         Color.pink
                                         };
    
    private ArrayList<Tank> tankList;     // массив квадратов
    private ArrayList<BurstingTank> burstList;
    private ShellManager shellManager;
    
    public BattleField()
    {
        
        shellManager = new ShellManager();
        tankList = new ArrayList<Tank>();
        burstList = new ArrayList<BurstingTank>();
                
        for (int i = 0; i <= 6; i++)
        {
            tankList.add(new Tank(TANK_COLORS[i], 10 + i * 5 + i * 40, 30, i));
        }
        
        
    }
    
    public ArrayList<Tank> getTanks()
    {
        return tankList;
    }
    
    public ArrayList<BurstingTank> getBurstingTanks()
    {
        return burstList;
    }
    
    public ShellManager getShellManager()
    {
        return shellManager;
    }
    
    public Tank click(int x, int y)
    {
        for (Tank tank: tankList) {        // проверяем, какой квадрат в точке клика
            if (tank.containsXY(x, y))
            {
                return tank;
            }
        } 
        return null;
    }
    public Tank getTank(int tankId)
    {
        for (Tank tank: tankList) {
            if (tank.id == tankId) return tank;
        }
        return null;
    }
    
    public void restoreTanks()
    {
        for (Tank tank: tankList)
        {
            tank.restore();
        }
    }
    
    public boolean tankCanMove(Tank checkingTank, TankMovement movement)
    {
        // для квадрата в фокусе проверяем возможность перекрытия других квадратов после сдвига
        for (Tank testTank: tankList)
        {
            if (testTank != checkingTank && !testTank.isDamaged())
            {
                if (checkingTank.isCrossing(movement, testTank.area))
                {
                    return false;
                }
            }
        }
        return true;
    }
}
