/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SabotageTanks;

import SabotageTanks.Tanks.BurstingTank;
import SabotageTanks.Tanks.Shell;
import SabotageTanks.Tanks.Tank;
import SabotageTanks.Tanks.TankMovement;
import java.awt.Canvas;
import java.awt.Color;
import java.util.ArrayList;

public class BattleField extends Canvas {
    
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
    private ArrayList<Shell> shellList;        // массив снарядов

    public final TankControl tankControl;  
    public final ShellManager shellManager;    
    public final int gameWidth, gameHeight;
    
    public BattleField(int gameWidth, int gameHeight)
    {
        this.gameWidth = gameWidth;
        this.gameHeight = gameHeight;
        tankList = new ArrayList<Tank>();        
        
        for (int i = 0; i <= 6; i++)
        {
            tankList.add(new Tank(TANK_COLORS[i], 10 + i * 5 + i * 40, 30, i));
        }
        
        shellList = new ArrayList<Shell>();
        shellManager = new ShellManager(shellList);

        burstList = new ArrayList<BurstingTank>();
                
        tankControl = new TankControl(this);
        addKeyListener(tankControl.getKeyListener());      
        addMouseListener(tankControl.getMouseListener());
    }
    
    public void tick()
    {
        
        tankControl.calculateFocusedTankMove();
        for (Tank tank: tankList)
        {
            if (tank == tankControl.getFocusedTank())
            {
                tank.rotateBarrel(tankControl.getCursorLocation());
            }
        }
        shellManager.tickShells(this);
    }
    
    public ArrayList<Tank> getTanks()
    {
        return tankList;
    }
    
    public ArrayList<BurstingTank> getBurstingTanks()
    {
        return burstList;
    }
    
    public ArrayList<Shell> getShells()
    {
        return shellList;
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
    
    public BattleFieldState getBattleFieldState()
    {
        return BattleFieldState.createServerState(tankList, burstList, shellList);
    }
    
    public BattleFieldState getPlayerState()
    {
        return BattleFieldState.createClientState(tankControl.getFocusedTank());  
    }
    
    public void updateClientState(BattleFieldState state)
    {
        if (state.playerTank != null)
        {
            Tank tankState = state.playerTank;

            for (Tank tank:tankList)
            {
                if (tankState.id == tank.id)
                {
                    tank = tankState;
                    break;
                }
            }
        }
    }
    
    public void updateServerState(BattleFieldState state)
    {
        tankList = state.tankList;
        shellList = state.shellList;
        burstList = state.burstList;
    }
}
