/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SabotageTanks.Control;

/**
 *
 * @author YTokmakov
 */
public class TankMovement {
    
        public double movementShift = 0.0D;     // перемещение вперед-назад
        public double rotationShift = 0.0D;
        public int cursorX = 0;
        public int cursorY = 0;
    
    public boolean isNoMove()
    {
        return ( movementShift == 0.0D && rotationShift == 0.0D );
    }
    public boolean isNoRotation()
    {
        return ( rotationShift == 0.0D );
    }
    public boolean isNoMovement()
    {
        return ( movementShift == 0.0D );
    }
}
