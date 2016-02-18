/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SabotageTanks;

import SabotageTanks.Control.GameControl;
import SabotageTanks.GraphicObjects.GameObject;
import SabotageTanks.Net.Connection;
import SabotageTanks.Interface.BattleField;
import java.util.ArrayList;
import SabotageTanks.GraphicObjects.Tank;
import java.awt.Color;
import java.awt.Point;

public abstract class Game implements Runnable
{   
    protected StateServer gameState;
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
        
        player = new Player(playerName, Player.getRandomColor());
        
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
        double nsPerFrame = 1000000000D/50D;        // сколько наносекунд на один кадр, если 60 кадров в секунду
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
        Tank newPlayerTank = null;
        boolean possibleTank = false;
        
        if (playerState.tank != null)
        {
            gameState.getTanks().remove(playerState.tank);
        }
        
        while (!possibleTank)
        {
            newPlayerTank = Tank.generate(player, this, playerState.shellList);
            possibleTank = gameState.tankCanMove(newPlayerTank);
        }

        playerState.tank = newPlayerTank;
        
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
    public Point getCursorPosition()
    {
        return battleField.getCursorPosition();
    }
    public double getPlayerSpeed()
    {
        if (playerState.tank != null)
        {
            return playerState.tank.getSpeed();
        } else
        {
            return 0.0d;
        }
    }
    public int getShellsQuantity()
    {
        return gameState.getShells().size();
    }
    public int getPlayerX()
    {
        if (playerState.tank != null)
        {
            return playerState.tank.getX();
        } else
        {
            return 0;
        }
    }
    public int getPlayerY()
    {
        if (playerState.tank != null)
        {
            return playerState.tank.getY();
        } else
        {
            return 0;
        }
    }
    
    protected abstract void tick();
    protected abstract void sendState();
    protected abstract void receiveState();
    
    // рисуем фрейм
    private void render()
    {
        battleField.draw(gameState.getObjectsToDraw(), this);
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
