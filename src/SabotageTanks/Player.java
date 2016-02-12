/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SabotageTanks;

import SabotageTanks.GraphicObjects.Shell;
import SabotageTanks.GraphicObjects.Tank;
import java.awt.Color;
import java.util.ArrayList;

/**
 *
 * @author ytokmakov
 */
public final class Player {
    
    //  цвета квадратов    
    public static final Color[] TANK_COLORS = {Color.GRAY,
                                                Color.YELLOW,
                                                Color.BLUE,
                                                Color.MAGENTA, 
                                                Color.ORANGE,
                                                Color.CYAN,
                                                Color.pink
                                                };
    
    private final String name;
    private final Color color;
    
    public Player(String playerName, Color playerColor)
    {
        name = playerName;
        color = playerColor;
    }
    
    public String getName()
    {
        return name;
    }
    public Color getColor()
    {
        return color;
    }
}
