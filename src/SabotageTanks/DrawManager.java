/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SabotageTanks;

import java.awt.Color;
import java.util.ArrayList;
import SabotageTanks.Tanks.Tank;
import SabotageTanks.Tanks.Shell;
import java.awt.Graphics2D;
import SabotageTanks.Tanks.BurstingTank;
import java.awt.BasicStroke;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.io.File;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.net.URL;
import javax.swing.JApplet;
/**
 *
 * @author YTokmakov
 */
public final class DrawManager {
    
    private int gameWidth,
                gameHeight;
    private TankControl control;
    private BattleField battleField;
    private String ipAddress;
    
    public DrawManager(int gameWidth,
                       int gameHeight,
                       BattleField battleField,
                       TankControl control,
                       String ipAddress
                        )
    {
        this.ipAddress = ipAddress;
        this.control = control;
        this.battleField = battleField;
        this.gameWidth = gameWidth;
        this.gameHeight = gameHeight;
    }
    
    synchronized public void drawField(Graphics2D graph)
    {
        
        graph.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graph.setColor(Color.WHITE);
        graph.fillRect(0, 0, gameWidth, gameHeight);
        graph.setColor(Color.red);
        graph.setStroke(new BasicStroke(2));
        
        drawTanks(graph);
//        graph.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
//        ((AlphaComposite)gr.getComposite()).derive(0.1f);
//        gr.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);        
        drawShells(graph);
        drawBursts(graph);
        //draw coordinates
        graph.setColor(Color.red);
        try
        {
//            ArrayList<GameObject> nearObjects = getNearObjects(control.getFocusedTank());
//            String s = "";
//            for (GameObject obj: nearObjects)
//            {
//                s += obj.getName() + " ";
//            }
            graph.drawString("ip:" + ipAddress, gameWidth - 125, 10);
            graph.drawString("speed: " + control.getFocusedTank().getSpeed(), gameWidth - 70, gameHeight - 60);            
            graph.drawString("x: " + control.getFocusedTank().getX(), gameWidth - 50, gameHeight - 50);
            graph.drawString("y: " + control.getFocusedTank().getY(), gameWidth - 50, gameHeight - 40);
            graph.drawString("To restore tanks press wheel mouse button", 10, 10);
        } catch (NullPointerException ex) { }
        //draw coordinates
        
    }
    
    synchronized public void drawShells(Graphics2D graph)       // рисуем снаряды
    {
        for (Shell shell:battleField.getShells())
        {
            graph.setColor(shell.color);
            graph.fillOval(shell.getXdraw(), shell.getYdraw(), shell.DIAMETER, shell.DIAMETER);
        }
    }
    synchronized public void drawBursts(Graphics2D graph)
    {
        ArrayList<BurstingTank> burstList = battleField.getBurstingTanks();
        
        for (BurstingTank burst: burstList)
        {
            try
            {
                ArrayList<Polygon> pieces = (ArrayList<Polygon>)burst.getRenderedPieces().clone();
                for (Polygon polygon: pieces)
                {
                    graph.setColor(burst.color);
                    graph.fillPolygon(polygon);
                }
            } catch(NullPointerException ex)
            {
                
            }
        }
    }
    synchronized public void drawTanks(Graphics2D graph)     // рисуем танки
    {
        for (Tank tank: battleField.getTanks())
        {
            if (!tank.isDamaged())
            {
                // рисуем танк
                graph.setColor(tank.getColor());
                graph.fillPolygon(tank.area);
                graph.setColor(tank.getColor());           
                if (tank == control.getFocusedTank())      
                {            
                    // рисуем границы - визуализацию фокуса
                    graph.setColor(Color.green);
                    graph.drawPolygon(tank.area);                    
                } else
                {
                    graph.setColor(Color.BLACK);
                }
                int[] xx = {tank.getX(), tank.XbarrelTip};
                int[] yy = {tank.getY(), tank.YbarrelTip};

                graph.drawPolyline(xx, yy, 2);

            }
        }       
    }
}
