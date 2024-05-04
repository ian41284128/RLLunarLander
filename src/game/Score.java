package game;

public class Score
{
   static final int MIN_LENGTH = 6;
   static final int MAX_SCORE = 6;

   public int x;
   public int y;
   public int score;
   public int length;
   
   /**
    * Constructor takes an x position. Initializes other variables to 0.
    * @param x The x position to put the score.
    */
   public Score(int x)
   {
      this.x = x;
      this.y = 0;
      length = 0;
      score = 0;
   }
   
   /**
    * Overload of constructor takes x and y and initializes other variables. Intended to be called after terrain generation is complete.
    * @param x X position of score.
    * @param y Y position of score.
    */
   public Score(int x, int y)
   {
      this.x = x;
      this.y = y;
      score = (int)( (y - Game.WINDOW_HEIGHT / 3d) / (Game.WINDOW_HEIGHT - 100 - Game.WINDOW_HEIGHT / 3d) * MAX_SCORE );
      length = (int)( MIN_LENGTH * (MAX_SCORE - score) );
   }
   
   /**
    * Used for printing the score values on the terrain.
    * @return Returns the string to print on the terrain.
    */
   public String toString()
   {
      return score + "x";
   }
   
   /**
    * Checks if pos is inside this score object.
    * @param pos The position to check.
    * @return Returns true if pos is inside this score.
    */
   public boolean landed(int pos)
   {
      if(pos >= x && pos <= x + length)
         return true;
      return false;
   }
}