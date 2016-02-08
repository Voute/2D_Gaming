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
import java.awt.Polygon;
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
public class Brush {
    
    private int gameWidth,
                gameHeight;
    private Control control;
    private ArrayList<Tank> tankList;
    private ArrayList<Shell> shellList;
    private ArrayList<BurstingTank> burstList = new ArrayList<BurstingTank>();
    
    public Brush(int gameWidth,
                 int gameHeight,
                 ArrayList<Shell> shellList,
                 ArrayList<Tank> tankList,
                 Control control
                )
    {
        this.control = control;
        this.tankList = tankList;
        this.shellList = shellList;
        this.gameWidth = gameWidth;
        this.gameHeight = gameHeight;
    }
    
    synchronized public void drawShells(Graphics2D graph)       // рисуем снаряды
    {
        // клонируем массив снарядов для прорисовки
        ArrayList<Shell> shellsListToDraw = (ArrayList<Shell>)shellList.clone();
        
        if (!shellsListToDraw.isEmpty())
        {
            // массив для снарядов, которые вышли за границы фрейма
            ArrayList<Shell> removeList = new ArrayList<Shell>();
            for (Shell bullet: shellsListToDraw)
            {
                if (bullet.nextX() <= gameWidth && bullet.nextY() <= gameHeight)
                {
                    for (Tank tank: tankList)
                    {
                        if ( tank.containsXY(bullet.getX(), bullet.getY()) &&
                             tank.id != bullet.parent   &&
                             !tank.isDamaged()
                           )
                        {
                            removeList.add(bullet);
                            burstList.add(tank.setDamaged());
                            break;
                        }
                    }
                    graph.setColor(bullet.color);
                    graph.fillOval(bullet.getXdraw(), bullet.getYdraw(), bullet.DIAMETER, bullet.DIAMETER);
                } else {
                    // добавляем к удалению из массива
                    removeList.add(bullet);
                }
            }
            // удаляем из массива снарядов вышедшие за границы экрана
            shellList.removeAll(removeList);  
        }
    }
    synchronized public void drawBursts(Graphics2D graph)
    {
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
        for (Tank tank: tankList)
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
                    tank.rotateBarrel(control.getCursorLocation());
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
