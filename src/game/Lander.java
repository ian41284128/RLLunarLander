package game;

import reinforcement.Action;

import java.awt.*;
import java.awt.event.*;


public class Lander
{   
   final static int RADIUS = 6;
   
   public final static double ROTATION_SPEED = 0.1;
   public final static double ACCEL = 0.006;

   public double x, y;
   public double dx, dy;
   double rotation = Math.PI / 2;
   
   int footX, footY, foot2X, foot2Y = 0;
   boolean thrusting;
   
   static double fuel = Float.POSITIVE_INFINITY;
   static int altitude;

   
   /**
    * Constructor initializes position, sets velocity to default value.
    * @param x Horizontal spawn position.
    * @param y Vertical spawn position.
    */
   public Lander(int x, int y)
   {
      this.x = x;
      this.y = y;
      //Start lander with a bit of horizontal momentum so it moves into screen
      dx = 0.5;
      dy = 0;
   }
   
   /**
    * Reads e and controls the ship if e maps to any defined key.
    * @param e The keycode of a keystroke.
    */
   public void control(Action e)
   {
      Game.startGame = false;
      if(!Game.gameOver)
      {
         if(e == Action.ACCELERATE)
         {
            throttle();
         }
         else if(e == Action.TURN_LEFT)
         {
            rotate(-1);
         }
         else if(e == Action.TURN_RIGHT)
         {
            rotate(1);
         }
      }
   }
   
   /**
    * Accelerates if there is fuel left.
    */
   public void throttle()
   {
      if(fuel > 0)
      {
         dy -= Math.sin(rotation) * ACCEL;
         dx -= Math.cos(rotation) * ACCEL;
         fuel -= 0.6;
      }
   }
   
   /**
    * Rotates the lander.
    * @param direction The direction to rotate. Between -1 and 1.
    */
   public void rotate(int direction)
   {
      rotation += direction * ROTATION_SPEED;
   }
   
   /**
    * Moves the ship if the game hasn't ended yet.
    */
   public void move()
   {   
      if(Game.pressed.contains((int)(KeyEvent.VK_UP)))
         thrusting = true;
      else
         thrusting = false;
   
      //Freeze if game has ended
      if(!Game.gameOver && !Game.startGame)
      {
         if(Game.agent != null && (x < 0 || y < 0 || x > Game.WINDOW_WIDTH || y > Game.WINDOW_HEIGHT)){
            Game.endGame();
         }
         if(isGrounded(Game.ground)){
            Game.endGame();
         } else {
            x += dx;
            y += dy;
         
            dy += Game.GRAVITY;
         }

      }
   }
   
   /**
    * Checks if the lander has hit the ground.
    * @param ground The polygon to check.
    * @return Returns true if the lander has touched the ground.
    */
   public boolean isGrounded(Polygon ground)
   {
      for(int i = 0; i < 360; i+=1)
      {
         if(ground.contains( x + Math.cos(Math.toRadians(i) * RADIUS * 1.4), y + Math.sin(Math.toRadians(i)) * RADIUS * 1.4)
         || ground.contains( footX, footY )
         || ground.contains( foot2X, foot2Y ) )
            return true;
      }
      return false;
   }
   
   /**
    * Draws the lander and the thruster fire if the ship is accelerating
    * @param g
    */
   public void draw(Graphics g)
   {
      g.setColor(Color.white);
      
      //LANDER LEGS
      int legx = (int)((x + Math.cos(rotation + 0.5) * RADIUS));
      int legy = (int)((y + Math.sin(rotation + 0.5) * RADIUS));
      footX = (int)((x + Math.cos(rotation + 0.5) * RADIUS * 2));
      footY = (int)((y + Math.sin(rotation + 0.5) * RADIUS * 2));
      g.drawLine(getLegX(RADIUS, 0.5), getLegY(RADIUS, 0.5), getLegX(RADIUS * 2, 0.5), getLegY(RADIUS * 2, 0.5));
      
      g.drawLine(getLegX(RADIUS, -0.5), getLegY(RADIUS, -0.5), getLegX(RADIUS * 2, -0.5), getLegY(RADIUS * 2, -0.5));
      
      //LANDER BODY
      g.setColor(Color.black);
      g.fillOval((int)x - RADIUS, (int)y - RADIUS, RADIUS * 2, RADIUS * 2);
      g.setColor(Color.white);
      g.drawOval((int)x - RADIUS, (int)y - RADIUS, RADIUS * 2, RADIUS * 2);  
      
      //THRUSTER FIRE
      if(thrusting && fuel > 0)
      {
         int firePointX = getLegX(RADIUS * 3 + Math.random() * 10, 0);
         int firePointY = getLegY(RADIUS * 3 + Math.random() * 10, 0);
         g.drawLine(getLegX(RADIUS, 0.5), getLegY(RADIUS, 0.5), firePointX, firePointY);
         g.drawLine(getLegX(RADIUS, -0.5), getLegY(RADIUS, -0.5), firePointX, firePointY);
      }
   }
   
   /**
    * Returns the x position of a point on a circle of radius rad with an offset to rotation offset.
    * @param rad The distance from the center of the lander to have the point.
    * @param offset The offset from the lander's rotation to get the point.
    * @return Returns the x position.
    */
   int getLegX(double rad, double offset)
   {
      return (int)((x + Math.cos(rotation + offset) * rad));
   }
   
   /**
    * Returns the y position of a point on a circle of radius rad with an offset to rotation offset.
    * @param rad The distance from the center of the lander to have the point.
    * @param offset The offset from the lander's rotation to get the point.
    * @return Returns the y position.
    */
   int getLegY(double rad, double offset)
   {
      return (int)((y + Math.sin(rotation + offset) * rad));
   }
}