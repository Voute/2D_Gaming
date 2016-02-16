/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SabotageTanks.Control;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import SabotageTanks.GraphicObjects.Tank;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.Point;
import SabotageTanks.Game;
import SabotageTanks.Player;
import java.awt.Color;
/**
 *
 * @author YTokmakov
 */
public class GameControl {
    
    private boolean upPressed,
                    downPressed,
                    leftPressed,
                    rightPressed;
//    private MouseMotionListener mouseMovingHandler;
    private Game game;
    private Tank playerTank = null;
    
    public GameControl(Game game)
    {
        this.game = game;
    }
    
    public KeyListener getKeyListener()      // обработчик клавиш клавы
    {
        return new KeyListener()
        {
            @Override
            public void keyTyped(KeyEvent e) {  }
            @Override
            public void keyPressed(KeyEvent e)      // кнопка клавы зажата
            {
                if (playerTank != null)     // если квадрат в фокусе
                {
                    if (e.getKeyCode() == KeyEvent.VK_UP)
                    {
                        upPressed = true;
                    }
                    if (e.getKeyCode() == KeyEvent.VK_LEFT)
                    {
                        leftPressed = true;
                    }
                    if (e.getKeyCode() == KeyEvent.VK_RIGHT)
                    {
                        rightPressed = true;
                    }
                    if (e.getKeyCode() == KeyEvent.VK_DOWN)
                    {
                        downPressed = true;
                    }
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {       // кнопка клавы отпущена
                switch (e.getKeyCode())
                {
                    case KeyEvent.VK_UP: upPressed = false;
                                         break;
                    case KeyEvent.VK_LEFT: leftPressed = false;
                                           break;
                    case KeyEvent.VK_RIGHT: rightPressed = false;
                                            break;
                    case KeyEvent.VK_DOWN: downPressed = false;
                                           break;
                }
            }
        };
    }
    public MouseListener getMouseListener()
    {
        return new MouseListener()      // обработчик клавиш мыши
        {
            @Override
            public void mouseClicked(MouseEvent e) { }
            @Override
            synchronized public void mousePressed(MouseEvent e)     // кнопка мыши зажата
            {
                if (e.getButton() == 1)     // левая кнопка мыши (shot)
                {
                    if (playerTank != null && !playerTank.getBursting())
                    {
                        playerTank.shot(e.getX(), e.getY());
                    }
                }
                else if (e.getButton() == 3 &&        // правая кнопка мыши (смена цвета)
                           playerTank != null)
                {  
                    Color newColor;
                    do
                    {
                        newColor = Player.getRandomColor();
                    } while (playerTank.getColor() == newColor);
                    
                    playerTank.changeColor(newColor);
                }
                else if (e.getButton() == 2)        // средняя кнопка мыши (ресаем игрока)
                {
                    if (playerTank == null || playerTank.getReadyToReset())
                    {
                        playerTank = game.generatePlayerTank();
                    }
                }
            }
            @Override
            public void mouseReleased(MouseEvent e) { }
            @Override
            public void mouseEntered(MouseEvent e) { }
            @Override
            public void mouseExited(MouseEvent e) { }
        };
    }
    public boolean getUpPressed()
    {
        return upPressed;
    }
    public boolean getDownPressed()
    {
        return downPressed;
    }
    public boolean getLeftPressed()
    {
        return leftPressed;
    }
    public boolean getRightPressed()
    {
        return rightPressed;
    }
    public Tank getFocusedTank()
    {
        return playerTank;
    }
    public TankMovement getPlayerMovement()
    {
        TankMovement movement = new TankMovement();
        
        if (playerTank != null)
        {
            if (getRightPressed())      // поворот вправо
            {
                movement.rotationShift += playerTank.rotationSpeed;
            }
            if (getLeftPressed())       // поворот влево
            {
                movement.rotationShift -= playerTank.rotationSpeed;                
            } 
            if (getUpPressed())     // движение вперед
            {
                movement.movementShift -= playerTank.speed();
            }
            if (getDownPressed())   // движение назад
            {
                movement.movementShift += playerTank.speed();
                if (!movement.isNoRotation())      // если зажат поворот, инвертируем поворот
                {
                    movement.rotationShift = -movement.rotationShift;
                }
            }
            
            if ( movement.isNoMovement() )
            {
                playerTank.stopAcceleration();
            }
            
            Point cursorPoint = game.getCursorPosition();
            if (cursorPoint != null)
            {
                movement.cursorX = cursorPoint.x;
                movement.cursorY = cursorPoint.y;
            }
            
        }
        
        return movement;
    }
}
