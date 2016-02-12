/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SabotageTanks.Interface;

import SabotageTanks.GraphicObjects.BurstingTank;
import SabotageTanks.GraphicObjects.GameObject;
import SabotageTanks.GraphicObjects.Shell;
import SabotageTanks.GraphicObjects.Tank;
import SabotageTanks.Control.TankMovement;
import SabotageTanks.ShellManager;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import javax.swing.JFrame;

public class BattleField extends JFrame {
    
    private Canvas canvas;
    private TankList tankList;     // массив квадратов
    private ArrayList<BurstingTank> burstList;
    private ArrayList<Shell> shellList;        // массив снарядов
    private Tank playerTank;
    

    private final ShellManager shellManager;    

    
    
    public BattleField(int gameWidth, int gameHeight, String title)
    {
        super(title);
        canvas = new Canvas();
        
        setSize(new Dimension(gameWidth, gameHeight));
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        add(canvas, BorderLayout.CENTER);
        setVisible(true);
        
        tankList = new TankList();        
        
        shellList = new ArrayList<Shell>();
        shellManager = new ShellManager(shellList);

        burstList = new ArrayList<BurstingTank>();
                
    }
    
    public void tick()
    {
        

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
    
//    public int getWidth()
//    {
//        return fieldWidth;
//    }
//    
//    public int getHeight()
//    {
//        return fieldHeight;
//    }
    
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
            if (testTank.id != checkingTank.id && !testTank.getIsBursting())
            {
                if (checkingTank.isCrossing(movement, testTank.area))
                {
                    return false;
                }
            }
        }
        return true;
    }
      
    public Point getCursonPosition()
    {
        return canvas.getMousePosition();
    }
    
    public BattleFieldState getBattleFieldState()
    {
        return BattleFieldState.createServerState(tankList, burstList, shellList);
    }
    public BattleFieldState getPlayerState()
    {
        return BattleFieldState.createClientState(tankControl.getFocusedTank());  
    }
    public void updateClientState(BattleFieldClientState state)
    {
        tankList.addOrUpdate(state.playerTank);
    }
    public void updateServerState(BattleFieldState state)
    {
        tankList = state.tankList;
        shellList = state.shellList;
        burstList = state.burstList;
    }
    public void draw(ArrayList<GameObject> objectArray)
    {
        BufferStrategy bs = canvas.getBufferStrategy();
        if (bs == null)
        {
            canvas.createBufferStrategy(3);
            return;
        }
        
        Graphics2D graph = (Graphics2D) bs.getDrawGraphics();
        graph.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        for (GameObject object:objectArray)
        {
            object.draw(graph);
        }
        
        graph.setColor(Color.WHITE);
        graph.fillRect(0, 0, getWidth(), getHeight());
        graph.setColor(Color.red);
        graph.setStroke(new BasicStroke(2));
        

//        graph.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
//        ((AlphaComposite)gr.getComposite()).derive(0.1f);
//        gr.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);        

        //draw coordinates
        graph.setColor(Color.red);

//            ArrayList<GameObject> nearObjects = getNearObjects(control.getFocusedTank());
//            String s = "";
//            for (GameObject obj: nearObjects)
//            {
//                s += obj.getName() + " ";
//            }
//        graph.drawString("ip:" + ipAddress, gameWidth - 125, 10);
        graph.drawString("speed: " + control.getFocusedTank().getSpeed(), gameWidth - 70, gameHeight - 60);            
        graph.drawString("x: " + control.getFocusedTank().getX(), gameWidth - 50, gameHeight - 50);
        graph.drawString("y: " + control.getFocusedTank().getY(), gameWidth - 50, gameHeight - 40);
        graph.drawString("To restore tanks press wheel mouse button", 10, 10);
        
        graph.dispose();
        bs.show();
    }
}
