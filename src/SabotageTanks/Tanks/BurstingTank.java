/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SabotageTanks.Tanks;

import SabotageTanks.GameObject;
import java.util.ArrayList;
import java.awt.Polygon;
import java.awt.Color;
/**
 *
 * @author YTokmakov
 */
public class BurstingTank extends GameObject
{
    private int x, y;
    private ArrayList<Piece> pieces;
    private int renders;
    public Color color;

    BurstingTank(int x, int y, int verge, Color color)
    {
        pieces = new ArrayList<>();
        this.x = x;
        this.y = y;
        this.color = color;
        int[] xx = new int[9];
        int[] yy = new int[9];
        int half = (int)(verge / 2);
        
        xx[0] = x - half;
        xx[1] = x;
        xx[2] = x + half;
        xx[3] = x - half;
        xx[4] = x;
        xx[5] = x + half;
        xx[6] = x - half;
        xx[7] = x;
        xx[8] = x + half;
        
        yy[0] = y - half;
        yy[1] = y - half;
        yy[2] = y - half;
        yy[3] = y;
        yy[4] = y;
        yy[5] = y;
        yy[6] = y + half;
        yy[7] = y + half;
        yy[8] = y + half;
        
        Piece polygon = new Piece(-1,-1);
        polygon.addPoint(xx[0], yy[0]);
        polygon.addPoint(xx[1], yy[1]);
        polygon.addPoint(xx[4], yy[4]);
        polygon.addPoint(xx[3], yy[3]);
        pieces.add(polygon);
        
        polygon = new Piece( 1,-1);
        polygon.addPoint(xx[1], yy[1]);
        polygon.addPoint(xx[2], yy[2]);
        polygon.addPoint(xx[5], yy[5]);
        polygon.addPoint(xx[4], yy[4]);
        pieces.add(polygon);
        
        polygon = new Piece( -1, 1);
        polygon.addPoint(xx[3], yy[3]);
        polygon.addPoint(xx[4], yy[4]);
        polygon.addPoint(xx[7], yy[7]);
        polygon.addPoint(xx[6], yy[6]);
        pieces.add(polygon);   
        
        polygon = new Piece( 1, 1);
        polygon.addPoint(xx[4], yy[4]);
        polygon.addPoint(xx[5], yy[5]);
        polygon.addPoint(xx[8], yy[8]);
        polygon.addPoint(xx[7], yy[7]);
        pieces.add(polygon);
    }
    
    public ArrayList<Polygon> getRenderedPieces()
    {
        ArrayList<Polygon> returnList = new ArrayList<>();
        for (Piece piece: pieces)
        {
            piece.render();
            returnList.add((Polygon)piece);
        }
        renders++;
        if (renders >= 10)
        {
            return null;
        } else
        {
            return returnList;
        }
    }
    
    @Override
    public double getCircumscribedRadius() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public String getName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    private class Piece extends Polygon
    {
        private int Xshift;
        private int Yshift;
        
        Piece(int Xshift, int Yshift)
        {
            this.Xshift = Xshift;
            this.Yshift = Yshift;
        }
        void render()
        {
            reset();
            for (int i = 0; i < 4; i++)
            {
                addPoint(xpoints[i] + Xshift, ypoints[i] + Yshift);
            }
        }
    }
}
