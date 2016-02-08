/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SabotageTanks.Tanks;

import java.awt.Color;
import java.awt.Polygon;
import java.awt.Point;
import SabotageTanks.GameObject;

/**
 *
 * @author YTokmakov
 */
    public class Tank extends GameObject       // объект для управления - танк
    {
        public int id;
        public final static int WIDTH = 40;
        public final static int HEIGHT = 40;
        public final static double rotationSpeed = 0.02D;
        public static boolean moveStatus = false;
        public final TankArea area;
        
        private static double speed = 1.5D;
        private final static double START_SPEED = 1.5D;
        private final static double ACCELERATION = 0.01D;
        private final static double MAX_SPEED = 3.5D;
        private final static double circumscribedRadius = HEIGHT / Math.sqrt(2);        // радиус описанной окружности
        private double rotation = Math.PI / 2;      // поворот по умолчанию        
        private double x,      // координата размещения по оси Х (центр)
                       y;      // координата размещения по оси У (центр)
        private boolean damaged;
        
        public int XbarrelTip,     // Х координата вершины ствола
                   YbarrelTip;     // У координата вершины ствола
        
        private Color color;        // цвет квадрата
        
        public Tank(Color color, int Xaxis, int Yaxis, int id)
        {
            this.id = id;
            this.color = color;
            this.x = Xaxis + WIDTH / 2;
            this.y = Yaxis + HEIGHT / 2;
            
            area = new TankArea(Xaxis, Yaxis);
            move(new TankMovement());
            
            XbarrelTip = getX();
            YbarrelTip = Yaxis;
        }
        public void rotateBarrel(Point target)
        {
            if (target != null)
            {
                calculateBarrel(target.x, target.y);
            } else
            {
                calculateBarrel(XbarrelTip, YbarrelTip);
            }
        }        
        private void calculateBarrel(int Xtarget, int Ytarget)
        {
            int Xdelta = Xtarget - getX();
            int Ydelta = Ytarget - getY();
            double S = Math.sqrt( Math.pow( Xdelta, 2 ) +
                                  Math.pow( Ydelta, 2 )
                                );
            double radius = WIDTH / 2;            
            XbarrelTip = getX() + (int)(radius * Xdelta / S);
            YbarrelTip = getY() + (int)(radius * Ydelta / S);
        }
        public BurstingTank setDamaged()
        {
            damaged = true;
            return new BurstingTank(getX(), getY(), WIDTH, color);
        }
        public void restore()
        {
            damaged = false;
            rotation = Math.PI / 2;
            x = 10 + id * 5 + id * 40;
            y = 30;
            move(new TankMovement());
            XbarrelTip = getX();
            YbarrelTip = 30;
        }
        public boolean isDamaged()
        {
            return damaged;
        }
        // перемещает квадрат на новые координаты
        public void move(TankMovement movement)
        {
            rotation += movement.rotationShift;
            x += area.calculateXshift(rotation, movement.movementShift);
            y += area.calculateYshift(rotation, movement.movementShift);
            area.calculateNewLocation();
        }
        // перекрывают ли границы квадрата указанные границы другого квадрата
        public Polygon assumeMove(double movementShift, double rotationShift)
        {
            return area.assumeNewLocation(movementShift, rotationShift);
        }
        public boolean isCrossing(TankMovement movement, TankArea testingArea)
        {
            TankArea assumedTankArea = area.assumeNewLocation(movement.movementShift, movement.rotationShift);
            
                return assumedTankArea.contains(testingArea.xpoints, testingArea.ypoints) ||
                       testingArea.contains(assumedTankArea.xpoints, assumedTankArea.ypoints)
                       ;
        }
        public boolean containsXY(int x, int y)
        {
            return area.contains(x, y);
        }       
        public int getX()        // возвращает Х координату квадрата
        {
            return (int)this.x;
        }
        public int getY()        // возвращает Y координату квадрата
        {
            return (int)this.y;
        }
        public double getXabsolute()
        {
            return x;
        }
        public double getYabsolute()
        {
            return y;
        }
        public double speed()
        {   if ( speed < MAX_SPEED )
            {
                speed += ACCELERATION;
            }
            return speed;
        }
        public double getSpeed()
        {
            return speed;
        }
        public void stopAcceleration()
        {
            speed = START_SPEED;
        }
        public double getRotation()
        {
            return this.rotation;
        }
        public Color getColor()     // возвращает цвет квадрата
        {
            return color;
        }
        public String getName()
        {
            return "tank" + id;
        }
        @Override
        public double getCircumscribedRadius()
        {
            return circumscribedRadius;
        }
        private class TankArea extends Polygon
        {
            TankArea(double x, double y)
            {
                this.addPoint((int)(x - (HEIGHT / 2)), (int)(y - (HEIGHT / 2)));
                this.addPoint((int)(x + (HEIGHT / 2)), (int)(y - (HEIGHT / 2)));
                this.addPoint((int)(x + (HEIGHT / 2)), (int)(y + (HEIGHT / 2)));
                this.addPoint((int)(x - (HEIGHT / 2)), (int)(y + (HEIGHT / 2)));
            }
            TankArea(Point[] points)
            {
                for (Point point: points)
                {
                    this.addPoint(point.x, point.y);
                }
            }
            void calculateNewLocation()
            {
                refreshPoints(getPoints(circumscribedRadius, rotation, getX(), getY()));
            }
            private Point[] getPoints(double areaRadius, double areaRotation, int Xcenter, int Ycenter)
            {
                Point[] returnPoints = new Point[4];
                
                int x0 = (int)(Math.cos(areaRotation - Math.PI / 4) * areaRadius);
                int y0 = (int)(Math.sin(areaRotation - Math.PI / 4) * areaRadius);

                int x1 = (int)(Math.cos(areaRotation + Math.PI / 4) * areaRadius);
                int y1 = (int)(Math.sin(areaRotation + Math.PI / 4) * areaRadius);

                int x2 = (int)(Math.cos(areaRotation + Math.PI / 4 * 3) * areaRadius);
                int y2 = (int)(Math.sin(areaRotation + Math.PI / 4 * 3) * areaRadius);

                int x3 = (int)(Math.cos(areaRotation - Math.PI / 4 * 3) * areaRadius);
                int y3 = (int)(Math.sin(areaRotation - Math.PI / 4 * 3) * areaRadius);
                
                returnPoints[0] = new Point(Xcenter + x0, Ycenter + y0);
                returnPoints[1] = new Point(Xcenter + x1, Ycenter + y1);
                returnPoints[2] = new Point(Xcenter + x2, Ycenter + y2);            
                returnPoints[3] = new Point(Xcenter + x3, Ycenter + y3);
                
                return returnPoints;
            }
            boolean contains(int x[], int y[])
            {
                for (int i = 0; i < x.length; i++)
                {
                    if (this.contains(x[i], y[i]))
                    {
                        return true;
                    }
                }
                return false;
            } 
            double calculateXshift(double rotation, double movementShift)
            {
                return Math.cos(rotation) * movementShift;
            }
            double calculateYshift(double rotation, double movementShift)
            {
                return Math.sin(rotation) * movementShift;
            }
            private void refreshPoints(Point[] newPoints)
            {
                this.reset();
                for (Point point: newPoints)
                {
                    this.addPoint(point.x, point.y);
                }
            }
            private TankArea assumeNewLocation(double movementShift, double rotationShift)
            {
                double newRotation = rotation + rotationShift;
                
                double Xnew = x + calculateXshift(newRotation, movementShift);
                double Ynew = y + calculateYshift(newRotation, movementShift);
                
                return new TankArea(getPoints(circumscribedRadius, newRotation,(int)Xnew,(int)Ynew));
            }
        }
    }


