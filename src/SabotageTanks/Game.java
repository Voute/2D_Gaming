/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SabotageTanks;

import SabotageTanks.Control.GameControl;
import SabotageTanks.Control.TankMovement;
import SabotageTanks.GraphicObjects.GameObject;
import SabotageTanks.GraphicObjects.Shell;
import SabotageTanks.Net.Connection;
import SabotageTanks.Interface.BattleField;
import java.util.ArrayList;
import SabotageTanks.GraphicObjects.Tank;
import java.awt.Color;
import java.util.List;

public abstract class Game implements Runnable
{   
    protected final StateServer gameState;
    protected final StatePlayer playerState;
    protected final Player player;
    protected final Connection connection;
    protected final GameControl control;
    protected final BattleField battleField;
    
    private static final String ARTICLE = "SabotageTanks";        // заголовок окна
    
    private static final int WIDTH = 800;       // ширина игрового поля
    private static final int HEIGHT = 600;      // высота игрового поля

    

    
    
    public Game(String playerName, Connection connection)
    {
        String title = ARTICLE + " - " + connection.getLocalIp() + ":" + connection.getLocalPort();
        
        this.connection = connection;
        
        battleField = new BattleField(WIDTH, HEIGHT, title);
        control = new GameControl(this);
        battleField.addKeyListener(control.getKeyListener());      
        battleField.addMouseListener(control.getMouseListener());
        
        Color playerColor = Player.TANK_COLORS[ (int)(Math.random()*7) ];
        player = new Player(playerName, playerColor);
        
        gameState = new StateServer();
        playerState = new StatePlayer();
    }
    
    public void start()
    {
        new Thread(this).start();       // стартуем игру в новом потоке        
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
                
                // receiving data
                receiveState();
                
                tick();
                
                // sending data
                sendState();
                
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
    
    public Tank generatePlayerTank()
    {
        playerState.tank = Tank.generate(player, this);
        return playerState.tank;
    }
    
    public int getWidth()
    {
        return WIDTH;
    }
    
    public int getHeight()
    {
        return HEIGHT;
    }
    
    public List<Shell> getShellList()
    {
        return gameState.shellList;
    }
    
    public List<Shell> getPlayerShellList()
    {
        return playerState.shellList;
    }
    
    protected abstract void tick();
    protected abstract void sendState();
    protected abstract void receiveState();
    
    protected boolean checkPlayerCanMove(Tank playerTank, TankMovement movement)
    {
        
    }
    
    // рисуем фрейм
    private void render()
    {
        battleField.draw(gameState.getObjectsToDraw());
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
