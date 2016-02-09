/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SabotageTanks;

import SabotageTanks.Tanks.BurstingTank;
import SabotageTanks.Tanks.Shell;
import SabotageTanks.Tanks.Tank;
import java.util.ArrayList;

/**
 *
 * @author ytokmakov
 */
public class BattleFieldState {
    
    public final ArrayList<Tank> tankList;     // массив квадратов
    public final ArrayList<BurstingTank> burstList;     // массив разрывающихся танков
    public final ArrayList<Shell> shellList;        // массив снарядов
    public final Tank playerTank;
    
    private BattleFieldState(ArrayList<Tank> tankList,
                            ArrayList<BurstingTank> burstList,
                            ArrayList<Shell> shellList,
                            Tank playerTank)
    {
        this.tankList = tankList;
        this.burstList = burstList;
        this.shellList = shellList;
        this.playerTank = playerTank;
    }
    
    public static BattleFieldState createServerState(ArrayList<Tank> tankList,
                                                     ArrayList<BurstingTank> burstList,
                                                     ArrayList<Shell> shellList)
    {
        return new BattleFieldState(tankList, burstList, shellList, null);
    }
    
    public static BattleFieldState createClientState(Tank playerTank)
    {
        return new BattleFieldState(null, null, null, playerTank);
    }
}
