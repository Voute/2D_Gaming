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

public class Game implements Runnable
{
    private static final int WIDTH = 800;       // ширина игрового поля
    private static final int HEIGHT = 600;      // высота игрового поля
    private static final String ARTICLE = "2d game";        // заголовок окна
    private static JFrame frame;        // окно
    private final String ipAddress;
    private final boolean isServer;
    
    private DrawManager drawManager;
    private BattleField battleField;
    private ConnectionManager connectManager;
    
    
    private boolean win = false;        // успешная парковка в парковку
    
    public Game(boolean isServer, String ip, int port)
    {
        this.isServer = isServer;
        ipAddress = ip + ":" + port;
        
        battleField = new BattleField(WIDTH, HEIGHT);
        
        connectManager = new ConnectionManager(isServer, ip, port, battleField);
        
//        objectsArrays = new ArrayList<>();
//        objectsArrays.add(shellList);
        
        drawManager = new DrawManager(WIDTH,
                          HEIGHT,
                          battleField,
                          battleField.tankControl,
                          ipAddress
                            );
        
        frame = new JFrame(ARTICLE);
        
        frame.setSize(new Dimension(WIDTH, HEIGHT));
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);
        frame.add(battleField, BorderLayout.CENTER);
        
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
                
                if (isServer)
                {
                    BattleFieldState clientState = connectManager.receiveClientBattlefieldState();
                    if (clientState != null)
                    {
                        battleField.updateClientState(clientState);
                    }
                }
                else
                {
                    BattleFieldState serverStateState = connectManager.receiveServerBattlefieldState();
                    if (serverStateState != null)
                    {
                        battleField.updateServerState(serverStateState);
                    }
                }
                
                battleField.tick();
                
                if (isServer)
                    
                {
                    connectManager.sendToClientBattlefieldState(battleField.getBattleFieldState());
                }
                else
                {
                    connectManager.sendToServerBattlefieldState(battleField.getPlayerState());
                }
                
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
                
                frames = 0;
                lastClockTime = newClockTime;
            }
        }
    }
    // рисуем фрейм
    private void render()
    {
        BufferStrategy bs = battleField.getBufferStrategy();
        if (bs == null)
        {
            battleField.createBufferStrategy(3);
            return;
        }
        
        Graphics2D graph = (Graphics2D) bs.getDrawGraphics();
        drawManager.drawField(graph);
        graph.dispose();
        bs.show();
    }
    public ArrayList<GameObject> getNearObjects(GameObject testingObject)
    {
        ArrayList<GameObject> returnArray = new ArrayList<>();
        
        for (Tank tank: battleField.getTanks())
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
