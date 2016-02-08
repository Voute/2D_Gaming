/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SabotageTanks;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferStrategy;
import javax.swing.JFrame;
import java.util.ArrayList;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import SabotageTanks.Tanks.Tank;
import SabotageTanks.Tanks.Shell;
import java.awt.BasicStroke;

public class Game extends Canvas implements Runnable
{

    private static final int WIDTH = 800;       // ширина игрового поля
    private static final int HEIGHT = 600;      // высота игрового поля
    private static final String ARTICLE = "2d game";        // заголовок окна
    private static JFrame frame;        // окно
    
//    private Tank focusedTank;     // квадрат в фокусе
    private ArrayList<Tank> tankList;     // массив квадратов
    private ArrayList<Shell> shellList;        // массив снарядов
    private ArrayList<ArrayList<GameObject>> objectsArrays;
    
    private Control control;
    private Brush brush;
    
//  цвета квадратов    
    private final Color[] TANK_COLORS = {Color.GRAY,
                                         Color.YELLOW,
                                         Color.BLUE,
                                         Color.MAGENTA,
                                         Color.ORANGE,
                                         Color.CYAN,
                                         Color.pink
                                         };
    
    private boolean win = false;        // успешная парковка в парковку
    
    public Game()
    {
        tankList = new ArrayList<Tank>();
        for (int i = 0; i <= 6; i++)
        {
            tankList.add(new Tank(TANK_COLORS[i], 10 + i * 5 + i * 40, 30, i));
        }
        shellList = new ArrayList<Shell>();
        
//        objectsArrays = new ArrayList<>();
//        objectsArrays.add(shellList);
        
        control = new Control(this,tankList,shellList);
        brush = new Brush(WIDTH,
                          HEIGHT,
                          shellList,
                          tankList,
                          control);
        
        frame = new JFrame(ARTICLE);
        
        frame.setSize(new Dimension(WIDTH, HEIGHT));
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);
        frame.add(this, BorderLayout.CENTER);
        addKeyListener(control.getKeyListener());      
        addMouseListener(control.getMouseListener());
        
        frame.setVisible(true);
    }
    
    public void start()
    {
        new Thread(this).start();       // стартуем игру в новом потоке        
    }
    
    public static void main(String[] args)
    {
        new StartFrame(ARTICLE).setVisible(true);
        
//        new Game().start();     // создаем игру и сразу стартуем
    }

    @Override
    public void run()
    {
        long lastTime = System.nanoTime();      // время виртуальной машины (накапливаемая)
        double nsPerFrame = 1000000000D/60D;        // сколько наносекунд на один кадр, если 60 кадров в секунду
        long lastClockTime = System.currentTimeMillis();        // текущее время
        
        int frames = 0;     // количество отрисованных фреймов
        double delta = 0D;      // накопленные промежутки времени в наносекундах
        
        while (true)
        {
            long newTime = System.nanoTime();       // берем новое время виртуальной машины
            delta += (newTime - lastTime) / nsPerFrame;     // вычисляем разницу с предыдущим, делим, получаем количество
                                                            // фреймов, которое можно отрисовать
            lastTime = newTime;     // присваиваем новое время
            
            long newClockTime = System.currentTimeMillis();     // новое текущее время
            
            boolean shouldRender = false;       // надо ли рисовать фрейм
            
            while (delta >= 1)      // рисуем вычисленное количество фреймов
            {
                frames++;
                delta--;
                shouldRender = true;
            }
            
            if (shouldRender)       // рисуем фрейм, если можно
            {
                render();
            }
            // каждую секунду выводим сообщение о количестве нарисованных фреймов
            if (newClockTime - lastClockTime >= 1000)       
            {
                System.out.println("framesRendered: " + frames);
                
                //tests
//                String s = "";
//                for (Tank tank: tankList)
//                {
//                    int[] xx = tank.Xget();
//                    int[] yy = tank.Yget();
//                    s += "tank" + tank.id + ": ";
//                    System.out.println(s);
//                    System.out.println(tank.getX() + "." + tank.getY());
//                    s = "";
//                    for (int i = 0; i < xx.length; i++)
//                    {
//                        s += "x" + i + "=" + xx[i] + "; y" + i + "=" + yy[i] + ";";
//                        System.out.println(s);
//                        s = "";
//                    }
//                }
                //tests
                
                frames = 0;
                lastClockTime = newClockTime;
            }
        }
    }
    // рисуем фрейм
    private void render()
    {
        BufferStrategy bs = getBufferStrategy();
        if (bs == null)
        {
            createBufferStrategy(3);
            return;
        }
        
        Graphics2D graph = (Graphics2D) bs.getDrawGraphics();
        graph.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graph.setColor(Color.WHITE);
        graph.fillRect(0, 0, WIDTH, HEIGHT);
        graph.setColor(Color.red);
        graph.setStroke(new BasicStroke(2));
        graph.drawString("Park the box!", 200, 200);
        control.calculateFocusedTankMove();
        brush.drawTanks(graph);
//        graph.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
//        ((AlphaComposite)gr.getComposite()).derive(0.1f);
//        gr.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);        
        brush.drawShells(graph);
        brush.drawBursts(graph);
        //draw coordinates
        graph.setColor(Color.red);
        try
        {
            ArrayList<GameObject> nearObjects = getNearObjects(control.getFocusedTank());
            String s = "";
            for (GameObject obj: nearObjects)
            {
                s += obj.getName() + " ";
            }
            graph.drawString(s, WIDTH - 150, HEIGHT - 60);
            graph.drawString("speed: " + control.getFocusedTank().getSpeed(), WIDTH - 70, HEIGHT - 60);            
            graph.drawString("x: " + control.getFocusedTank().getX(), WIDTH - 50, HEIGHT - 50);
            graph.drawString("y: " + control.getFocusedTank().getY(), WIDTH - 50, HEIGHT - 40);
            graph.drawString("To restore tanks press wheel mouse button", 10, 10);
        } catch (NullPointerException ex) { }
        //draw coordinates
        graph.dispose();
        bs.show();
    }
    public ArrayList<GameObject> getNearObjects(GameObject testingObject)
    {
        ArrayList<GameObject> returnArray = new ArrayList<>();
        
        for (Tank tank: tankList)
        {
            if (tank != testingObject &&
                Relations.areNear(testingObject, tank)
                )
            {
                returnArray.add(tank);
            }
        }
        
        return returnArray;
    }
    private static class Relations
    {
        static boolean areNear(GameObject obj1, GameObject obj2)
        {
            return Math.sqrt( Math.pow(obj1.getX() - obj2.getX(), 2) + Math.pow(obj1.getY() - obj2.getY(), 2) )
                   <= obj1.getCircumscribedRadius() + obj2.getCircumscribedRadius();
        }
    }

}
