/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SabotageTanks.GraphicObjects;

import java.awt.Graphics2D;

/**
 *
 * @author YTokmakov
 */
public interface GameObject {
    
    public double getCircumscribedRadius();
    public int getX();
    public int getY();
    public String getId();
    public void draw(Graphics2D graph);
    
}
