/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SabotageTanks.GraphicObjects;

import SabotageTanks.Control.TankMovement;
import SabotageTanks.Game;
import SabotageTanks.GameLog;
import SabotageTanks.Player;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author YTokmakov
 */
    public class Tank implements GameObject     // объект для управления - танк
    {
        private final String id;
        
        public final static int WIDTH = 40;
        public final static int HEIGHT = 40;
        public final static double rotationSpeed = 0.02D;
        public TankArea area;
        
        
        public List<BurstPiece> burstPieces;
        private int burstRenders;
        private static double speed = 1.5D;
        private final static double START_SPEED = 1.5D;
        private final static double ACCELERATION = 0.01D;
        private final static double MAX_SPEED = 3.5D;
        private final static double circumscribedRadius = HEIGHT / Math.sqrt(2);        // радиус описанной окружности
        private double rotation = Math.PI / 2;      // поворот по умолчанию        
        private double x,      // координата размещения по оси Х (центр)
                       y;      // координата размещения по оси У (центр)
        private boolean bursting;
        private boolean readyToReset;
        
        public int XbarrelTip,     // Х координата вершины ствола
                   YbarrelTip;     // У координата вершины ствола
        
        private Color color;        // цвет квадрата
        private List<Shell> shellList;
        
        public Tank(Color color, int Xaxis, int Yaxis, String tankId, List<Shell> shellList)
        {
            this.bursting = false;
            this.id = tankId;
            this.color = color;
            this.shellList = shellList;
            this.x = Xaxis + WIDTH / 2;
            this.y = Yaxis + HEIGHT / 2;
            
            readyToReset = false;
            
            area = new TankArea(Xaxis, Yaxis);
            burstPieces = new ArrayList<>();
            move(new TankMovement());
            
            XbarrelTip = getX();
            YbarrelTip = Yaxis;
        }  
        public void shot(int targetX, int targetY)
        {
            try {
                shellList.add(new Shell(getX(), getY(), targetX, targetY, XbarrelTip, YbarrelTip, getId()));
            } catch (Exception ex) {
                GameLog.write(ex);
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
//        public void setX(double newX)
//        {
//            x = newX;
//        }
//        public void setY(double newY)
//        {
//            y = newY;
//        }
        public void setBursting(boolean bursting)
        {
            this.bursting = bursting;
        }
        public boolean getBursting()
        {
            return bursting;
        }
        public boolean getReadyToReset()
        {
            return readyToReset;
        }
        // перемещает квадрат на новые координаты
        public void move(TankMovement movement)
        {
            if (!bursting)
            {
                if ( movement.isNoMovement() )
                {
                    stopAcceleration();
                }


                rotation += movement.rotationShift;
                x += area.calculateXshift(rotation, movement.movementShift);
                y += area.calculateYshift(rotation, movement.movementShift);
                area.calculateNewLocation(getX(), getY());

                calculateBarrel(movement.cursorX, movement.cursorY);
            } else
            {
                if (!burstPieces.isEmpty())
                {
                    for (BurstPiece piece:burstPieces)
                    {
                        piece.tick();
                    }
                }
            }
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
        public boolean isCrossing(Tank checkingTank)
        {
            return checkingTank.getArea().contains(area.xpoints, area.ypoints);
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
        public TankArea getArea()
        {
            return area;
        }
        public void changeColor(Color newColor)
        {
            color = newColor;
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
        
        public String getId()
        {
            return id;
        }
        public boolean sameId(GameObject gameObject)
        {
            return (gameObject.getId().matches(getId()));
        }
        public static Tank generate(Player owner, Game game, List<Shell> ownerShellList)
        {
            int randomX = (int) ( Math.random()*(game.getWidth() - WIDTH) + (int)(WIDTH / 2) );
            int randomY = (int) ( Math.random()*(game.getHeight() - HEIGHT) + (int)(HEIGHT / 2) );
            return new Tank(owner.getColor(), randomX, randomY, owner.getName(), ownerShellList);
        }
        public void updateStats(Tank updatingTank)
        {
//            this.bursting = updatingTank.bursting;
            if (updatingTank.bursting)
            {
                this.bursting = true;
            }
            if (!bursting)
            {
                this.XbarrelTip = updatingTank.XbarrelTip;
                this.YbarrelTip = updatingTank.YbarrelTip;
                this.x = updatingTank.x;
                this.y = updatingTank.y;
                this.rotation = updatingTank.rotation;
                this.area = updatingTank.area;        
                this.color = updatingTank.color;
            }
        }
        
        @Override
        public double getCircumscribedRadius()
        {
            return circumscribedRadius;
        }
        
//        public static Tank generateRandom(int fieldWidth, int fieldHeight, String owner)
//        {
//            Color randomColor = TANK_COLORS[ (int)(Math.random()*7) ];
//            int randomX = (int) ( Math.random()*(fieldWidth - Tank.WIDTH) + (int)Tank.WIDTH / 2 );
//            int randomY = (int) ( Math.random()*(fieldHeight - Tank.HEIGHT) + (int)Tank.HEIGHT / 2 );
//            return new Tank(randomColor, randomX, randomY, owner);
//        }

    @Override
    public void draw(Graphics2D graph)
    {
        if (!bursting)
        {
            // рисуем танк
            graph.setColor(color);
            graph.fillPolygon(area);       
            graph.setColor(Color.BLACK);
            
            int[] xx = {getX(), XbarrelTip};
            int[] yy = {getY(), YbarrelTip};
            graph.drawPolyline(xx, yy, 2);

        } else if (!readyToReset)
        {
            if (burstPieces.isEmpty())
            {
                burstRenders = 0;
                
                int[] xx = new int[9];
                int[] yy = new int[9];
                int half = (int)(HEIGHT / 2);

                xx[0] = getX() - half;
                xx[1] = getX();
                xx[2] = getX() + half;
                xx[3] = getX() - half;
                xx[4] = getX();
                xx[5] = getX() + half;
                xx[6] = getX() - half;
                xx[7] = getX();
                xx[8] = getX() + half;

                yy[0] = getY() - half;
                yy[1] = getY() - half;
                yy[2] = getY() - half;
                yy[3] = getY();
                yy[4] = getY();
                yy[5] = getY();
                yy[6] = getY() + half;
                yy[7] = getY() + half;
                yy[8] = getY() + half;

                BurstPiece polygon = new BurstPiece(-1,-1);
                polygon.addPoint(xx[0], yy[0]);
                polygon.addPoint(xx[1], yy[1]);
                polygon.addPoint(xx[4], yy[4]);
                polygon.addPoint(xx[3], yy[3]);
                burstPieces.add(polygon);

                polygon = new BurstPiece( 1,-1);
                polygon.addPoint(xx[1], yy[1]);
                polygon.addPoint(xx[2], yy[2]);
                polygon.addPoint(xx[5], yy[5]);
                polygon.addPoint(xx[4], yy[4]);
                burstPieces.add(polygon);

                polygon = new BurstPiece( -1, 1);
                polygon.addPoint(xx[3], yy[3]);
                polygon.addPoint(xx[4], yy[4]);
                polygon.addPoint(xx[7], yy[7]);
                polygon.addPoint(xx[6], yy[6]);
                burstPieces.add(polygon);   

                polygon = new BurstPiece( 1, 1);
                polygon.addPoint(xx[4], yy[4]);
                polygon.addPoint(xx[5], yy[5]);
                polygon.addPoint(xx[8], yy[8]);
                polygon.addPoint(xx[7], yy[7]);
                burstPieces.add(polygon);
            }
            
            if (burstRenders <= 15)
            {
                graph.setColor(color);
                for (BurstPiece piece:burstPieces)
                {
                    graph.fill(piece);
                }
                burstRenders++;
            } else
            {
                burstPieces.clear();
                readyToReset = true;
            }
            
        }
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
            void calculateNewLocation(int x, int y)
            {
                refreshPoints(getPoints(circumscribedRadius, rotation, x, y));
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
        private class BurstPiece extends Polygon
    {
        private int Xshift;
        private int Yshift;
        
        BurstPiece(int Xshift, int Yshift)
        {
            this.Xshift = Xshift;
            this.Yshift = Yshift;
        }
        void tick()
        {
            reset();
            for (int i = 0; i < 4; i++)
            {
                addPoint(xpoints[i] + Xshift, ypoints[i] + Yshift);
            }
        }
    }
    }


