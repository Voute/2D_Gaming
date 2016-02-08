/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SabotageTanks.Tanks;

import java.awt.Color;
import SabotageTanks.GameObject;

/**
 *
 * @author YTokmakov
 */
    public class Shell extends GameObject
    {
        public final int RADIUS = 5;
        public final int DIAMETER = RADIUS * 2;
        public final int speed = 2;        // скорость полета: пиксель/кадр
        public final Color color = Color.DARK_GRAY;
        public final int parent;
        private double x;       // координата по оси Х
        private double y;       // координата по оси У
        private double Xstep;       // величина изменения по оси Х в кадр
        private double Ystep;       // величина изменения по оси У в кадр
        
        public Shell(int straightX1,    // две точки на прямой
                     int straightX2,
                     int straightY1,
                     int straightY2,
                     int startX,        // позиция вылета
                     int startY,
                     int parent
                     ) throws Exception
        {
            x = startX;
            y = startY;
            
            this.parent = parent;
            
            // проверяем, не в центр ли объекта в фокусе сделан выстрел
            if (straightY1 == straightY2 && straightX1 == straightX2)
            {
                throw new Exception();
            } else {
                // вычисляем разницу точки выстрела и цели выстрела
                int deltaX = straightX2-straightX1;
                int deltaY = straightY2-straightY1;
                // вычисляем расстояние от точки выстрела до цели выстрела
                double S = Math.sqrt(Math.pow(deltaX,2) + Math.pow(deltaY, 2));
                // вычисляем изменение по оси на кадр
                Xstep = deltaX * speed / S;
                Ystep = deltaY * speed / S;                
            }
        }
        public int nextY()      // координата У в следующем кадре
        {
            y += Ystep;
            return (int)y;
        }
        public int nextX()      // координата Х в следующем кадре
        {
            x += Xstep;
            return (int)x;
        }
        @Override
        public int getX()
        {
            return (int)x;
        }
        @Override
        public int getY()
        {
            return (int)y;
        }
        public int getXdraw()
        {
            return (int)x - RADIUS;
        } 
        public int getYdraw()
        {
            return (int)y - RADIUS;
        } 
        @Override
        public double getCircumscribedRadius() {
            return RADIUS;
        }

    @Override
    public String getName() {
        return "shell";
    }
    }
