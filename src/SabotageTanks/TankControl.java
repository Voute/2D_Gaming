/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SabotageTanks;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import SabotageTanks.Tanks.Tank;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.Point;
import SabotageTanks.Tanks.TankMovement;
/**
 *
 * @author YTokmakov
 */
public class TankControl {
    
    private boolean upPressed,
                    downPressed,
                    leftPressed,
                    rightPressed;
//    private MouseMotionListener mouseMovingHandler;
    private Tank focusedTank;
    private BattleField battleField;
    
    TankControl(BattleField battleField)
    {
        this.battleField = battleField;
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
                if (focusedTank != null)     // если квадрат в фокусе
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
                if (e.getButton() == 1)     // левая кнопка мыши (фокус)
                {
                    focusedTank = battleField.click(e.getX(), e.getY());   
                }
                else if (e.getButton() == 3 &&        // правая кнопка мыши (выстрел)
                           focusedTank != null)
                {   // создаем снаряд
                    battleField.shellManager.makeShell(focusedTank, e.getX(), e.getY());
                }
                else if (e.getButton() == 2)        // средняя кнопка мыши
                {
                    battleField.restoreTanks();
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
        return focusedTank;
    }
    public Point getCursorLocation()
    {
        return battleField.getMousePosition();
    }
    public void calculateFocusedTankMove()
    {
        TankMovement movement = new TankMovement();
        
        if (focusedTank != null)
        {
            if (getRightPressed())      // поворот вправо
            {
                movement.rotationShift += focusedTank.rotationSpeed;
            }
            if (getLeftPressed())       // поворот влево
            {
                movement.rotationShift -= focusedTank.rotationSpeed;                
            } 
            if (getUpPressed())     // движение вперед
            {
                movement.movementShift -= focusedTank.speed();
            }
            if (getDownPressed())   // движение назад
            {
                movement.movementShift += focusedTank.speed();
                if (!movement.isNoRotation())      // если зажат поворот, инвертируем поворот
                {
                    movement.rotationShift = -movement.rotationShift;
                }
            }
            
            if ( !movement.isNoMove() )
            {
                if (battleField.tankCanMove(focusedTank, movement))
                { 
                    focusedTank.move(movement);
                }
            }
            if ( movement.isNoMovement() )
            {
                focusedTank.stopAcceleration();
            }
        }
    }
}
