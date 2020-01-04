package de.uos.dgrlvq.distance;

/** This class implements simple Euclidean distance for the calculation of distance matrices environment.
 * @author Gulraj Singh Mrok, University Osnabrück, Germany
 */
public class SimpleEuclideanDistance implements DistanceInterface
{
    /** The global lambda vector. */    
    protected double[] lambdas;
    /** Exponent of Lambda */    
    protected int expo_lmb=2;
    /** Coordinate exponent. */    
    protected int expo_coord=2;
    /** Creates new empty object with default values. */    
    public SimpleEuclideanDistance()
    {
    }
    /** Creates new object.
     * @param lmd Lambda vector
     * @param ex_lmb exponent of lambda
     * @param exp_cord exponent of coordinate
     */    
    public SimpleEuclideanDistance(double [] lmd, int ex_lmb, int exp_cord)
    {
        this.lambdas=lmd;
        this.expo_lmb=ex_lmb;
        this.expo_coord=exp_cord;
    }
    
    public void setLambdas(double [] lambdas)
    {
        this.lambdas=lambdas;
    }
    public void setExpoLambda(int expoLambda)
    {
        this.expo_lmb= expoLambda;
    }
    public void setExpoCoordinate(int expoCoord)
    {
        this.expo_coord=expoCoord;
    }
    public double[] getLambdas() 
    {
        return lambdas;
    }
    public int getExpoLambda()
    {
        return expo_lmb;
    }
    public int getExpoCoordinate()
    {
        return expo_coord;
    }
    public void normalizeLambdas()
    {
        double sum_lambda = 0.;
        boolean readfail = lambdas[0] < 0.;
        for (int i = 0; i < lambdas.length; i++)
        {
            sum_lambda += ((readfail) ? lambdas[i] = 1. : lambdas[i]);
        }
        //System.out.print(" - "+sum_lambda);
        sum_lambda = 1. / sum_lambda;
        //System.out.print(" + "+sum_lambda);
        for (int i = 0; i < lambdas.length; i++)
        {
            lambdas[i] *= sum_lambda;
            //System.out.print(" - "+lambdas[i]);
        }
        
    } 
    /** Calcules distance between two vectors.
     *
     * <CODE>
     * for (int i = 0; i < a.length; i++)
     * {
     *        diff = a[i]-b[i];
     *        sum += Math.pow(diff,2);
     * }
     * return Math.sqrt(sum);
     * </CODE>
     * @param a first vector
     * @param b second vector
     * @return Euclidean distance
     */    
    public double calculateDistance(double[] a, double[] b)
    {
        double diff=0.0;
        double sum = 0.0;
        try
        {
            for (int i = 0; i < a.length; i++)
            {
                diff = a[i]-b[i];
                sum += Math.pow(diff, 2) ;
            } // {lmb_i^expo_lmb (w_i - x_i)^expo_coord} generic
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return Math.sqrt(sum);
        
    }
    
    /** Returns one of the simplest derivative form wrt lambda.
     *
     * <CODE>
     * return (inputs[i]-weights[i]);
     * </CODE>
     * @param inputs input vector
     * @param weights weight vector
     * @param i index
     * @return returns just the differance between input and weight.
     */    
    public double derivativeLamdba(double[] inputs, double[] weights, int i)
    {
        return (inputs[i]-weights[i]);
    }
    
    /** Returns one of the simplest derivative form wrt weights.
     *
     * <CODE>
     * return (x[comp_idx]-w[comp_idx]);
     * </CODE>
     * @param x input vector
     * @param w weight vector
     * @param comp_idx index
     * @return differance between two vectors.
     */    
    public double derivativeWeights(double[] x, double[] w, int comp_idx)
    {
        return (x[comp_idx]-w[comp_idx]);
    }
    
    public String getName()
    {
        return "SimpleEuclideanDistance";
    }
    
}
