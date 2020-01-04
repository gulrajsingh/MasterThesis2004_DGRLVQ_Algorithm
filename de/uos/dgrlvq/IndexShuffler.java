package de.uos.dgrlvq;

import java.util.Random;

/** This class shuffle the index of series.
 *
 * Given a series of numbers, the object of this class can be used to
 * shuffle the index of series on random. On request, the random index can be
 * generated only once.
 *
 * For Example,
 * <CODE>
 * ShuffleIndex si = new ShuffleIndex (10);
 *        for(int i = 0; i < 100; i++)
 *            System.out.println ("" + i + ": " + si.next ());
 * </CODE>
 * will print series from 0 to 9 randomly and only once.
 * @author Gulraj Singh Mrok, University Osnabrück, Germany
 */
public class IndexShuffler
{
    /** Random seed */    
    protected long randomSeed;
    /** current index of series */    
    protected int index;
    /** maximum index of series */    
    protected int maximum;
    /** Contains the suffled index of series */    
    protected int[] shuffleIndex;
    /** used for random generation of index. */    
    protected Random random;
    
    /** Creates new object with maximum index
     * @param noind maximum value of index
     */    
    public IndexShuffler (int noind)
    {
        this (noind, System.currentTimeMillis ());
    }
    
    /** Creates new object with maximum value of index and random seed
     * @param noind maximum index
     * @param sd seed
     */    
    public IndexShuffler (int noind, long sd)
    {
        this.maximum = noind;
        this.randomSeed = sd;
        this.shuffleIndex = new int[maximum];
        reset ();
    }
    
    /** Resets the index */    
    public void reset ()
    {
        random = new Random (randomSeed);
        index = Integer.MAX_VALUE-1;
        for(int i = 0; i < maximum; i++)
            shuffleIndex[i] = i;
    }
    
    /** resuffles the index */    
    public void reshuffle ()
    {
        int index;
        int tmp;
        
        for(int cnt = 0; cnt < maximum; cnt++)
        {
            index = (int)(random.nextDouble () * maximum);
            tmp = shuffleIndex[cnt];
            shuffleIndex[cnt] = shuffleIndex[index];
            shuffleIndex[index] = tmp;
        }
    }
    /** returns the next random index
     * @return next random index
     */    
    public int next ()
    {
        
        if(++index >= maximum)
        {
            reshuffle ();
            index = 0;
        }
        return shuffleIndex[index];
    }
    /*
    public static void main (String argv[])
    {
        ShuffleIndex si = new ShuffleIndex (10);         
        for(int i = 0; i < 100; i++)
            System.out.println ("" + i + ": " + si.next ());
    }
     */
}


