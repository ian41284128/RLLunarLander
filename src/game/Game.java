package game;

import reinforcement.Action;
import reinforcement.Agent;
import reinforcement.State;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Set;
import java.util.HashSet;


public class Game extends JPanel implements ActionListener, KeyListener
{
   static final Set<Integer> pressed = new HashSet<Integer>();

   static final int WINDOW_WIDTH = 500;
   static final int WINDOW_HEIGHT = 300;
   
   static final int FRAMERATE = 1;
   
   static final int STAR_COUNT = 50;
   static final int SCORE_COUNT = 4;
   static final int GROUND_RES = (int)(WINDOW_WIDTH / 5);
   
   //TERRAIN GENERATION SETTINGS
   static final int BASE_HEIGHT = (int)(WINDOW_HEIGHT * 2d/3d);
   static final double SIN1_SCALE = 40d;
   static final int SIN1_AMPLITUDE = 50;
   static final double SIN2_SCALE = 30d;
   static final int SIN2_AMPLITUDE = 40;
   static final double NOISE_AMPLITUDE = 20d;
   
   static final double GRAVITY = 0.002;
   static final int LANDING_SPEED = 30;
   
   static String[][] landingMessages = new String[][] {new String[] {"THAT LOOKED EXPENSIVE", "THAT LANDER COST 100 MEGABUCKS", "YOU JUST MADE A 2 KILOMETER WIDE CRATER", "SWEET JESUS ARE THOSE ASTRONAUTS OKAY"}, 
                                                        new String[] {"LANDED", "PASSABLE LANDING", "YOU HAVE ACHIEVED TOUCHDOWN"}, 
                                                        new String[] {"PERFECT LANDING", "PERFECTION", "EXCEPTIONAL LANDING", "COMPLETE SUCCESS", "THIS WAS A TRIUMPH"}
                                                        };
   
   static Lander lander;
   static Polygon ground;

   static int[][] stars;
   static int[][] groundPoints;
   static Score[] scores;
   static boolean gameOver = false;
   static boolean startGame = true;
   static boolean startOver = false;
   
   static String[] messages;
   
   static int score = 0;
   static double time = 0;

   static Agent agent = null;

   /**
    * Initializes the game window, clock, and control listener.
    * @param args
    */
   public static void main(String[] args)
   {      
      Game game = new Game();
      
      JFrame frame = new JFrame("Lunar game.Lander");
      frame.getContentPane().add( game );
      frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
      frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
      frame.setVisible(true);

      game.addKeyListener(game);
      if(args.length > 0 && args[0].equals("--ai-agent")){
         agent = new Agent();
      }

      Timer clock = new Timer(FRAMERATE, game);			
      clock.start();
   }
   
   /**
    * Calls on startGame.
    */
   public Game()
   {
      //Sets jframe to the focused window, so keylistener works
      setFocusable( true );
   
      startGame = true;
      startGame();
   }
   
   /**
    * Gets called every time the timer triggers. Loads information for and paints each frame (such as moving the lander). Also reads through user input.
    */
   public void actionPerformed(ActionEvent e)
   {
      //Time should only increase when a game is being played.
      if(gameOver && agent != null){
         startGame();
      }
      if(!gameOver && !startGame)
         time += 1d / FRAMERATE;
      if (pressed.size() > 0 && agent == null)
      {
         for(int input : pressed)
         {
            switch (input){
               case KeyEvent.VK_UP:
                  lander.control(Action.ACCELERATE);
                  break;
               case KeyEvent.VK_LEFT:
                  lander.control(Action.TURN_LEFT);
                  break;
               case KeyEvent.VK_RIGHT:
                  lander.control(Action.TURN_RIGHT);
                  break;
            }
         }
      }

      State state = new State(getAltitude(), getVelocity(), getFuel(), getAngleDeg(), lander.x);
      Action action = agent.getAction(state);
      if(agent != null){
         lander.control(action);
      }
   
      lander.move();

      State statePrime = new State(getAltitude(), getVelocity(), getFuel(), getAngleDeg(), lander.x);
      agent.update(state, action, statePrime, getReward());

      repaint();
   }

   public float getReward(){
      if(gameOver){
         if(getAltitude() > 0)
            return -100;
         if(getLandingMessage() > 0){
            return 100;
         }
         return -100;
      }
      return -1;
   }

   /**
    * Stores key presses. If the game is over or needs to be started, this method will start a new game.
    */
   @Override
   public synchronized void keyPressed(KeyEvent e) 
   {
      if(lander.fuel <= 0 && startOver && pressed.size() == 0)
      {
         startGame = true;
         gameOver = false;
      }
      else if(startOver && pressed.size() == 0)
      {
         startGame();
      }
   
      pressed.add(e.getKeyCode());
   }
   
   /**
    * Removes keys which are no longer being pressed.
    * @param e
    */
   @Override
   public synchronized void keyReleased(KeyEvent e) 
   {
      pressed.remove(e.getKeyCode());
   }
   
   /**
    * 
    * @param e
    */
   @Override
   public void keyTyped(KeyEvent e) 
   { /* Method not intended for use */ }
   
   
   /**
    * Paints all parts of game.
    * @param g
    */
   public void paintComponent(Graphics g)
   {
      super.paintComponent(g);
   
      //Background
      g.setColor(Color.black);
      g.fillRect(0, 0, WINDOW_WIDTH, WINDOW_HEIGHT);
         
      drawStars(g);
      lander.draw(g);
      drawGround(g);
      drawStats(g);
      fuelWarning(g);
      
      if(startGame)
      {
         startGame(g);
      }
      if(gameOver)
      {
         endGame(g);
      }
   }
   
   /**
    * Draws in stars from an array of positions.
    * @param g
    */
   public void drawStars(Graphics g)
   {
      g.setColor(Color.white);
   
      for(int star = 0; star < stars.length; star++)
      {
         g.fillOval(stars[star][0], stars[star][1], 2, 2);
      }
   }
   
   /**
    * Draws the ground and puts in text for the positions of score points.
    * @param g
    */
   public void drawGround(Graphics g)
   {
      g.setColor(Color.black);
      g.fillPolygon(ground);
      g.setColor(Color.white);
      g.drawPolygon(ground);
      
      //Draw in scores
      for(int i = 0; i < scores.length; i++)
      {
         g.drawString(scores[i].toString(), scores[i].x, scores[i].y + 10);
      }
   }
   
   /**
    * Draws info text in the corners of the screen.
    * @param g
    */
   public void drawStats(Graphics g)
   {
      g.drawString("SCORE " + String.format("%2s%04d", "", score), 30, 30);
      g.drawString("TIME " + String.format("%7s%01d:%02d", "", (int)time/60, (int)time%60), 30, 50);
      
      if (lander.fuel < 0)
         lander.fuel = 0;
      g.drawString("FUEL " + String.format("%6s%04d", "", getFuel() ), 30, 70);
      
      g.drawString("ALTITUDE " + String.format( "%23s%04d", "", getAltitude() ), WINDOW_WIDTH - 200, 30);
      g.drawString("HORIZONTAL SPEED " + String.format( "%6s%03d", "", Math.abs((int)(lander.dx * 100)) ), WINDOW_WIDTH - 200, 50);
      g.drawString("VERTICAL SPEED " + String.format( "%12s%03d", "", Math.abs((int)(lander.dy * 100)) ), WINDOW_WIDTH - 200, 70);
   }

   public int getAltitude(){
      return (getY((int)lander.x) - (int)lander.y) - Lander.RADIUS;
   }

   public int getFuel(){
      return (int) Lander.fuel;
   }

   public int getVelocity(){
      return (int)(lander.dx * 100f);
   }

   public int getAngleDeg(){
      return (int)Math.toDegrees(lander.rotation) % 360;
   }

   /**
    * Gets the height of the terrain at xPos.
    * @param xPos The x position to check.
    * @return Returns an integer height of the terrain at xPos.
    */
   public static int getY(int xPos)
   {
      int height = 0;
      
      while(height < WINDOW_HEIGHT)
      {
         if(ground.contains(xPos, height))
            return height;
         height++;
      }
      return WINDOW_HEIGHT;
   }
   
   /**
    * Overload of getY that takes a Polygon to check for height, instead of the ground object. Intended for use before ground is initialized.
    * @param xPos The x position to get the height at.
    * @param poly the Polygon to check for height.
    * @return Returns the height of poly at xPos.
    */
   public static int getY(int xPos, Polygon poly)
   {
      int height = 0;
      
      while(height < WINDOW_HEIGHT)
      {
         if(poly.contains(xPos, height))
            return height;
         height++;
      }
      return WINDOW_HEIGHT;
   }
   
   /**
    * Generates a ground polygon with flat spots where scores are.
    * @param points the array of points to store ground data in.
    * @return Returns a polygon object of the terrain.
    */
   public static Polygon generateGround(int[][] points)
   {
      //Initialize random seed to apply to sin values
      double seed1 = Math.random() * 2 * Math.PI;
      double seed2 = Math.random() * 2 * Math.PI;
      
      //Generate game.Score objects at even intervals
      for(int i = 0; i < scores.length; i++)
      {
         int x = WINDOW_WIDTH * (i+1) / (SCORE_COUNT+1);
         scores[i] = new Score(x);
      }
      
      //First point of polygon should be in the bottom left corner
      points[0] = new int[] {0, WINDOW_HEIGHT};
      //Iterate through rest of points
      for(int i = 1; i < points.length-1; i++)
      {
         //x is evenly distributed across screen
         double x = (i-1d) / (points.length-2d) * WINDOW_WIDTH;
         //y generation: Base height + sin wave + second wave with smaller influence + randomness
         double y = BASE_HEIGHT + Math.sin(x / SIN1_SCALE + seed1) * SIN2_AMPLITUDE + Math.sin(x / SIN2_SCALE + seed2) * SIN2_AMPLITUDE + Math.random() * NOISE_AMPLITUDE;
         
         points[i] = new int[] {(int)x, (int)y};
      }
      //Last point should be in bottom right corner (To complete the polygon)
      points[points.length - 1] = new int[] {WINDOW_WIDTH, WINDOW_HEIGHT};
      
      //Average each point with the points next to it for more smoothness
      for(int i = 2; i < points.length - 2; i++)
      {
         points[i][1] = (points[i-1][1] + points[i][1] + points[i+1][1]) / 3;
      }
      
      //Create a polygon out of this data. This is done now so that getY can be called on it. This is not the final version of the terrain.
      Polygon p = new Polygon();
      for(int point = 0; point < points.length; point++)
      {
         p.addPoint(points[point][0], points[point][1]);
      }
      
      //Get the height of the terrain at each score location
      for(int s = 0; s < scores.length; s++)
      {
         scores[s] = new Score(scores[s].x, getY(scores[s].x, p));
      }
      
      //Level out the groud at each score
      for(int[] point : points)
      {
         for(Score score : scores)
         {
            if(point[0] >= score.x && point[0] <= score.x + score.length)
               point[1] = score.y;
         }
      }
      
      //Create the final polygon and return it.
      p = new Polygon();
      for(int point = 0; point < points.length; point++)
      {
         p.addPoint(points[point][0], points[point][1]);
      }
      return p;
   }
   
   /**
    * Generates stars at random points on the screen.
    * @param starPositions The array of positions to store stars in
    */
   public static void generateStars(int[][] starPositions)
   {
      for(int s = 0; s < stars.length; s++)
      {
         stars[s][0] = (int)(Math.random() * WINDOW_WIDTH);
         stars[s][1] = (int)(Math.random() * WINDOW_HEIGHT);
      }
   }
   
   /**
    * Displays a fuel warning in the middle of the screen if appropriate.
    * @param g
    */
   public static void fuelWarning(Graphics g)
   {
      if((int)time % 2 == 0 && !gameOver)
      {
         g.setColor(Color.white);
         if(lander.fuel <= 0)
         {
            g.drawString("OUT OF FUEL", WINDOW_WIDTH / 2 - 80, WINDOW_HEIGHT / 2 - 100);
         }
         else if(lander.fuel <= 200)
         {
            g.drawString("LOW FUEL", WINDOW_WIDTH / 2 - 50, WINDOW_HEIGHT / 2 - 100);
         }
      }
   }
   
   /**
    * Initializes the game.
    */
   public static void startGame()
   {
      startOver = false;
      gameOver = false;
   
      //reset variables if game has ended (fuel is out)
      if(lander.fuel <= 0)
      {
         lander.fuel = 1000;
         score = 0;
         time = 0;
      }
      
      //regenerate terrain, stars, score positions
      groundPoints = new int[GROUND_RES][2];
      scores = new Score[SCORE_COUNT];
      ground = generateGround(groundPoints);
      
      stars = new int[STAR_COUNT][2];
      generateStars(stars);
      
      //spawn the lander
      lander = new Lander(lander.RADIUS, WINDOW_HEIGHT / 3);
   }
   
   /**
    * Draws start game text on the screen.
    * @param g
    */
   public static void startGame(Graphics g)
   {  
      g.setColor(Color.white);   
      g.drawString("INSERT COINS", WINDOW_WIDTH / 2 - 60, WINDOW_HEIGHT / 2 - 100);
      g.drawString("PRESS ANY BUTTON TO PLAY", WINDOW_WIDTH / 2 - 100, WINDOW_HEIGHT / 2 - 100 + 30);
      g.drawString("ARROW KEYS TO MOVE", WINDOW_WIDTH / 2 - 85, WINDOW_HEIGHT / 2 - 100 + 50);
   }
   
   /**
    * Gets game over information.
    */
   public static void endGame()
   {
      gameOver = true;
      messages = new String[3];
      messages[0] = landingMessages[getLandingMessage()][ (int)( Math.random() * landingMessages[getLandingMessage()].length ) ];
      messages[1] = getPointsMessage();
      messages[2] = getFuelMessage();
   }
   
   /**
    * Draws game over information.
    * @param g
    */
   public static void endGame(Graphics g)
   {   
      g.setColor(Color.white);
      g.drawString(messages[0], WINDOW_WIDTH / 2 - (int)(messages[0].length() * 3.5) - 8, WINDOW_HEIGHT / 2 - 100);
      g.drawString(messages[1], WINDOW_WIDTH / 2 - (int)(messages[1].length() * 3.5) - 8, WINDOW_HEIGHT / 2 - 100 + 30);
      g.drawString(messages[2], WINDOW_WIDTH / 2 - (int)(messages[2].length() * 3.5) - 8, WINDOW_HEIGHT / 2 - 100 + 60);
      
      if(lander.fuel > 0)
      {
         g.drawString("PRESS ANY KEY TO CONTINUE", WINDOW_WIDTH / 2 - 100, WINDOW_HEIGHT / 2 - 100 + 100);
      }
      else
      {
         g.drawString("GAME OVER", WINDOW_WIDTH / 2 - 40, WINDOW_HEIGHT / 2 - 100 + 120);
         g.drawString("FINAL SCORE " + score, WINDOW_WIDTH / 2 - 50, WINDOW_HEIGHT / 2 - 100 + 140);
         g.drawString("PRESS ANY KEY TO PLAY AGAIN", WINDOW_WIDTH / 2 - 100, WINDOW_HEIGHT / 2 -100 + 160);
      }
      
      //Freeze game for one second to prevent stray input restarting the game instantly
      try
      {
         Thread.sleep(1000);
      }
      catch(InterruptedException ex)
      {}
      
      startOver = true;
   }
   
   /**
    * Returns the quality of the landing
    * @return Returns an int between 0 and 2, 0 being a crash and 2 being a perfect landing
    */
   static int getLandingMessage()
   {
      int landingScore;
      if(lander.rotation > 0 && lander.rotation < 2.5)
      {
         if(Math.abs(lander.dy) * 100 < LANDING_SPEED * 0.5)
            landingScore = 2;
         else if(Math.abs(lander.dy) * 100 < LANDING_SPEED)
            landingScore = 1;
         else
            landingScore = 0;
      }
      else
      {
         landingScore = 0;
      }
      
      return landingScore;
   }
   
   /**
    * Returns a string showing how many points the player got.
    * @return Returns a string showing how many points the player got.
    */
   static String getPointsMessage()
   {
      for(Score s : scores)
      {
         if(s.landed((int)lander.x))
         {
            int points = getLandingMessage() * 100 * s.score;
            score += points;
            return "GOT " + points + " POINTS";
         }
      }
      if(getLandingMessage() > 0)
      {
         score += 50;
         return "50 POINTS (DID NOT LAND IN DESIGNATED AREA)";
      }
      else
      {
         return "TIP: THE LANDING LEGS ARE ON THE OTHER SIDE OF THE LANDER";
      }
   }
   
   /**
    * Returns a message showing if the player got any fuel.
    * @return Returns a message showing if the player got any fuel.
    */
   static String getFuelMessage()
   {
      if(Math.abs(lander.dy) * 100 < LANDING_SPEED * 0.5)
      {
         lander.fuel += 50;
         return "GAINED 50 FUEL";
      }
      else if(Math.abs(lander.dy) * 100 < LANDING_SPEED)
      {
         return "";
      }
      lander.fuel -= 100;
      return "LOST 100 FUEL";
   }
}