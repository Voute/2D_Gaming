/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SabotageTanks;

import java.awt.event.KeyEvent;
import java.awt.Polygon;
import java.awt.event.KeyListener;
import SabotageTanks.Tanks.Tank;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.Point;
import java.util.ArrayList;
import SabotageTanks.Tanks.Shell;
/**
 *
 * @author YTokmakov
 */
public class Control {
    
    private boolean upPressed,
                    downPressed,
                    leftPressed,
                    rightPressed;
//    private MouseMotionListener mouseMovingHandler;
    private Tank focusedTank;
    private final ArrayList<Tank> tankList;
    private final ArrayList<Shell> shellList;    
    private Game game;
    
    Control(Game game,
            ArrayList<Tank> tankList,
            ArrayList<Shell> shellList)
    {
        this.game = game;
        this.tankList = tankList;
        this.shellList = shellList;
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
                    focusedTank = null;
//                    mouseMovingHandler = null;
                    for (Tank tank: tankList) {        // проверяем, какой квадрат в точке клика
                        if (tank.containsXY(e.getX(), e.getY()))
                        {
                            focusedTank = tank;
                            break;
                        }
                    }     
                } else if (e.getButton() == 3 &&        // правая кнопка мыши (выстрел)
                           focusedTank != null)
                {   // создаем снаряд
                    try {
                        shellList.add(new Shell(focusedTank.getX(),
                                                 e.getX(),
                                                 focusedTank.getY(),
                                                 e.getY(),
                                                 focusedTank.XbarrelTip,
                                                 focusedTank.YbarrelTip,
                                                 focusedTank.id
                        ));
                    } catch (Exception ex) { }      // если выстрел был совершен в центре квадрата
                } else if (e.getButton() == 2)
                {
                    for (Tank tank: tankList)
                    {
                        tank.restore();
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
        return focusedTank;
    }
    public Point getCursorLocation()
    {
        return game.getMousePosition();
    }
    public void calculateFocusedTankMove()
    {
        double movementShift = 0.0D;     // перемещение вперед-назад
        
        double rotationShift = 0;
        boolean noMove = false;     // если движение запрещено
        
        if (focusedTank != null)
        {
            if (getRightPressed())      // поворот вправо
            {
                rotationShift += focusedTank.rotationSpeed;
            }
            if (getLeftPressed())       // поворот влево
            {
                rotationShift -= focusedTank.rotationSpeed;                
            } 
            if (getUpPressed())     // движение вперед
            {
                movementShift -= focusedTank.speed();
            }
            if (getDownPressed())   // движение назад
            {
                movementShift += focusedTank.speed();
                if (rotationShift != 0.0D)      // если зажат поворот, инвертируем поворот
                {
                    rotationShift = -rotationShift;
                }
            }
            
            if ( movementShift != 0.0D || rotationShift != 0.0D )
            {
                // для квадрата в фокусе проверяем возможность перекрытия других квадратов после сдвига
                for (Tank testTank: tankList)
                {
                    if (testTank != focusedTank && !testTank.isDamaged())
                    {
                        if (focusedTank.isCrossing(movementShift, rotationShift, testTank.area))
                        {
                            noMove = true;
                            break;
                        }
                    }
                }
                if (!noMove)
                { 
                    focusedTank.move(movementShift,
                                     rotationShift
                                     );
                }
            }
            if ( movementShift == 0.0D )
            {
                focusedTank.stopAcceleration();
            }
        }
    }
}
