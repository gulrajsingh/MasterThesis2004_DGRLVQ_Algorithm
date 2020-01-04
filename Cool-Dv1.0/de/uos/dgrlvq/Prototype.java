package de.uos.dgrlvq;
import de.uos.dgrlvq.distance.*;
/** An object of this class has a capability to lean and adapt according assigned
 * distance matrices over the given cluster of vectors of same category and to
 * classify vectors for catagory.
 * @author Gulraj Singh Mrok, University Osnabrück, Germany
 */
public class Prototype
{
    /** weight vector to all input vectors. */    
    protected double[] weights=new double[2]; 
    /** activation value. */    
    protected double activation=0.0; 
    /** output value */    
    protected double output=0.0;
    /** Category of prototype */    
    protected int category_of_this_prototype=0;
    /** Distance referance that this prototype uses. */    
    protected DistanceInterface distance=new DistanceGeneralizedWeightedEuclidean();
    /** Winner Count of prototype.
     *
     * this values increases and decreases as prototype is winner or loser respt.
     *
     * this value is used to decide if the prototype is mostly loser and we can kill
     * it.
     */    
    protected int winnerCount=0;
    /** Learning rate correct */    
    protected double learning_rate_correct = 0.01;
    /** learning rate wrong */    
    protected double learning_rate_wrong = 0.001;
    
    /** creates new object with default values. */    
    public Prototype()
    {
    }
    
    /** Creates new object
     * @param number_of_inputs_for_this_prototype dimention of pattern
     * @param category category of this prototype
     * @param distanceRef distance referance this prototype uses
     * @param lrc learning rate correct
     * @param lrw learing rate wrong
     */    
    public Prototype(int number_of_inputs_for_this_prototype, int category, DistanceInterface distanceRef, double lrc, double lrw)
    {
        try
        {
            this.distance= distanceRef;
            this.category_of_this_prototype = category;
            this.weights = new double[number_of_inputs_for_this_prototype];
            this.learning_rate_correct=lrc;
            this.learning_rate_wrong=lrw;
            for (int i = 0; i < number_of_inputs_for_this_prototype; i++)
            {
                weights[i] = Math.random();
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    /** create new object
     * @param category category of this prototype
     * @param w weight vector assigned to input vector
     * @param distanceRef distance reference
     * @param lrc learning rate correct
     * @param lrw learing rate wrong
     */    
    public Prototype(int category, double[] w, DistanceInterface distanceRef, double lrc, double lrw)
    {
        try
        {
            this.distance= distanceRef;
            this.category_of_this_prototype = category;
            this.weights = w;
            this.learning_rate_correct=lrc;
            this.learning_rate_wrong=lrw;
                        
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    /** Create new object
     * @param w weight vector
     * @param actv activation
     * @param op output
     * @param category category
     * @param wc winner count
     * @param distanceRef distance reference
     * @param lrc learning rate correct
     * @param lrw learing rate wrong
     */    
    public Prototype( double[] w, double actv, double op, int category,int wc, DistanceInterface distanceRef, double lrc, double lrw)
    {
        try
        {
            this.distance= distanceRef;
            this.weights = w;
            this.activation=actv;
            this.output=op;
            this.category_of_this_prototype = category;
            this.winnerCount=wc;
            this.learning_rate_correct=lrc;
            this.learning_rate_wrong=lrw;
            
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }   
    
    /** activates the input vector for this prototype.
     *
     * this function calcultes the activation value.
     * @param inputs input vector
     */    
    public void activate(double[] inputs)
    {
        activation = distance.calculateDistance(inputs, weights);
        output = 0.0;
    } 
    
    /** this prototype will learn and adapt.
     * @param inputs input vector
     * @param c category
     * @param learning_rate_lambda learning rate of lambdas
     * @param normalizeLambda normalizes lambda if true
     */    
    public void learn(double[] inputs, int c, double learning_rate_lambda, boolean normalizeLambda)
    {
        double [] lambdas= distance.getLambdas();
        /*
         System.out.println ("-----------------------");
        for (int i = 0; i < lambdas.length; i++)
        {
            System.out.print (" "+lambdas[i]);
        }
        System.out.println ();
        */
         if (c == category_of_this_prototype)
        {
            for (int i = 0; i < inputs.length; i++)
            {
                weights[i] = weights[i] - learning_rate_correct * distance.derivativeWeights(inputs, weights,i);
                if(lambdas!=null)
                {
                    lambdas[i] = lambdas[i] - learning_rate_lambda * distance.derivativeLamdba(inputs, weights,i) ;
                }
            }
            winnerCount++;
        }
        else
        {
            for (int i = 0; i < inputs.length; i++)
            {
                weights[i] = weights[i] + learning_rate_wrong*  distance.derivativeWeights(inputs, weights,i);
                if(lambdas!=null)
                {
                    lambdas[i] = lambdas[i] + learning_rate_lambda *  distance.derivativeLamdba(inputs, weights,i) ;
                }
            }
            winnerCount--; 
        }
        
        distance.setLambdas(lambdas);
        /*
        for (int i = 0; i < lambdas.length; i++)
        {
            System.out.print (" "+lambdas[i]);
        }
        System.out.println ();
        */
        if(normalizeLambda)
        {
            distance.normalizeLambdas();
        }
        /*
        for (int i = 0; i < lambdas.length; i++)
        {
            System.out.print (" "+lambdas[i]);
        }
        System.out.println ();
        
        System.out.println ("+++++++++++++++++++++++++++++");
        */
        
        output = 1.0;
    }
    /** returns the distance reference.
     * @return distance reference of prtotype
     */    
    public DistanceInterface getDistance()
    {
        return distance;
    }
    /** returns correct learning rate.
     * @return correct learning rate
     */    
    public double getLearningRateCorrect()
    {
        return this.learning_rate_correct;
    }
    /** returns wrong learning rate
     * @return learing rate wrong
     */    
    public double getLearningRateWorng()
    {
        return this.learning_rate_wrong;
    }
    /** returns the weight vector
     * @return weights
     */    
    public double[] getWeights()
    {
        return weights;
    }
    /** returns the lambda vector
     * @return lambda
     */    
    public double[] getLambda()
    {
        return this.distance.getLambdas();
    }
    /** returns activation
     * @return activation
     */    
    public double getActivation()
    {
        return activation;
    }
    /** returns output
     * @return output
     */    
    public double getOutput()
    {
        return output;
    }
    /** returns the category
     * @return category
     */    
    public int getCategory()
    {
        return this.category_of_this_prototype;
    }
    /** returns winner count
     * @return winner count
     */    
    public int getWinnerCount()
    {
        return this.winnerCount;
    }
    /** sets the distance reference to this prototype
     * @param d distance referance
     */    
    public void setDistance(DistanceInterface d)
    {
        this.distance= d;
    }
    /** set learing rate correct
     * @param lrc learing rate correct.
     */    
    public void setLearningRateCorrect(double lrc)
    {
        this.learning_rate_correct=lrc;
    }
    /** sets learing rate wrong
     * @param lrw learing rate wrong
     */    
    public void setLearningRateWorng(double lrw)
    {
        this.learning_rate_wrong=lrw;
    }
    
    /** sets the weight vector
     * @param w weight vector
     */    
    public void setWeights(double [] w)
    {
        weights=w;
    }
    /** sets activation
     * @param a activation
     */    
    public void setActivation(double a)
    {
        activation=a;
    }
    
    /** set output
     * @param o output
     */    
    public void setOutput(double o)
    {
        output=o;
    }
    /** sets category
     * @param c category
     */    
    public void setCategory(int c)
    {
        this.category_of_this_prototype=c;
    }
    /** sets winner count
     * @param wc winner count
     */    
    public void setWinnerCount(int wc)
    {
        this.winnerCount=wc;
    }
    
}
