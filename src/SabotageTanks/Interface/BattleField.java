/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SabotageTanks.Interface;

import SabotageTanks.GraphicObjects.GameObject;
import SabotageTanks.Game;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;
import javax.swing.JFrame;

public class BattleField extends JFrame {
    
    private Canvas canvas;
    
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

                
    }

    @Override
    public synchronized void addMouseListener(MouseListener l) {
        canvas.addMouseListener(l);
    }

    @Override
    public synchronized void addKeyListener(KeyListener l) {
        canvas.addKeyListener(l);
    }

        
    public Point getCursorPosition()
    {
        return canvas.getMousePosition();
    }
    
    public void draw(ArrayList<GameObject> objectArray, Game game)
    {
        BufferStrategy bs = canvas.getBufferStrategy();
        if (bs == null)
        {
            canvas.createBufferStrategy(3);
            return;
        }
        
        Graphics2D graph = (Graphics2D) bs.getDrawGraphics();
        graph.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
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
        graph.drawString("shells:" + game.getShellsQuantity(), game.getWidth() - 125, 10);
        graph.drawString("speed: " + game.getPlayerSpeed(), game.getWidth() - 70, game.getHeight()- 60);            
        graph.drawString("x: " + game.getPlayerX(), game.getWidth() - 50, game.getHeight() - 50);
        graph.drawString("y: " + game.getPlayerY(), game.getWidth() - 50, game.getHeight() - 40);
        graph.drawString("To restore tanks press wheel mouse button", 10, 10);
        
        
        if (objectArray != null)
        {
            for (GameObject object:objectArray)
            {
                if (object != null)
                {
                    object.draw(graph);
                }
            }
        }

        
        graph.dispose();
        bs.show();
    }
}
