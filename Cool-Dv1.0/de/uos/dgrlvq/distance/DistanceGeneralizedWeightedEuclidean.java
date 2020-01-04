package de.uos.dgrlvq.distance;

/** This class implements gradient descent method for the calculation of distance matrices environment.
 * @author Gulraj Singh Mrok, University Osnabrück, Germany
 */
public class DistanceGeneralizedWeightedEuclidean implements DistanceInterface
{
    /** The global lambda vector. */
    protected double[] lambdas=new double[2];
    /** Exponent of Lambda */
    protected int expo_lmb=2;
    /** Coordinate exponent. */
    protected int expo_coord=2;
    
    public static boolean flag=false;
    
    /** Creates new empty object with default values. */
    public DistanceGeneralizedWeightedEuclidean ()
    {
    }
    /** Creates new object.
     * @param lmd Lambda vector
     * @param ex_lmb exponent of lambda
     * @param exp_cord exponent of coordinate
     */
    public DistanceGeneralizedWeightedEuclidean (double [] lmd, int ex_lmb, int exp_cord)
    {
        this.lambdas=lmd;
        this.expo_lmb=ex_lmb;
        this.expo_coord=exp_cord;
    }
    public void normalizeLambdas ()
    {
        double sum_lambda = 0.;
        boolean readfail = lambdas[0] < 0.;
        for (int i = 0; i < lambdas.length; i++)
        {
            sum_lambda += ((readfail) ? lambdas[i] = 1. : lambdas[i]);
        }
        sum_lambda = 1. / sum_lambda;
        
        for (int i = 0; i < lambdas.length; i++)
        {
            lambdas[i] *= sum_lambda;
        }
        
        if (flag)
        {
            double th=0.01;
            System.out.print ("\nThreshold="+th);            
            StringBuffer sb=new StringBuffer (" [Value:Dimension]");
            for(int x=0;x<lambdas.length;x++)
            {
                //System.out.println (" "+lambdas[i]);
                if(lambdas[x]>th)
                {
                    sb.append ("["+lambdas[x]+" : "+x+"]");
                }
            }
            System.out.println (sb.toString ());
        }
        flag=false;
    }
    /** Calculates distance between two vectors.
     *
     * <CODE>
     * for (int i = 0; i < a.length; i++)
     * {
     *       diff = a[i]-b[i];
     *       sum += Math.pow(lambdas[i], expo_lmb)*Math.pow(diff, expo_coord) ;
     * }
     * return sum;
     * </CODE>
     * @param a first vector
     * @param b second vector
     * @return the distance
     */
    public double calculateDistance (double[] a, double[] b)
    {
        double sum = 0.0;
        double diff=0.0;
        for (int i = 0; i < a.length; i++)
        {
            diff = a[i]-b[i];
            sum += Math.pow (lambdas[i], expo_lmb)*Math.pow (diff, expo_coord) ;
        }
        return sum;
    }
    
  
    /** Derivative wrt to lambda.
     *
     * <CODE>
     * return expo_lmb * Math.pow((inputs[i]-weights[i]), expo_coord) * Math.pow(lambdas[i], expo_lmb-1);
     * </CODE>
     * @param inputs input vector
     * @param weights weight vector
     * @param i index
     * @return derivative
     */
    public double derivativeLamdba (double[] inputs, double[] weights, int i)
    {
        return expo_lmb * Math.pow ((inputs[i]-weights[i]), expo_coord) * Math.pow (lambdas[i], expo_lmb-1);
    }
    
    /** Derivative wrt weights
     *
     * <CODE>
     * return -expo_coord * Math.pow((x[comp_idx]-w[comp_idx]), expo_coord-1) * Math.pow(lambdas[comp_idx], expo_lmb);
     * </CODE>
     * @param x input
     * @param w weight
     * @param comp_idx index
     * @return derivative
     */
    public double derivativeWeights (double[] x, double[] w, int comp_idx)
    {
        return -expo_coord * Math.pow ((x[comp_idx]-w[comp_idx]), expo_coord-1) * Math.pow (lambdas[comp_idx], expo_lmb);
    }
    
    public double[] getLambdas ()
    {
        return lambdas;
    }
    public int getExpoLambda ()
    {
        return expo_lmb;
    }
    public int getExpoCoordinate ()
    {
        return expo_coord;
    }
    public String getName ()
    {
        return "DistanceGeneralizedWeightedEuclidean";
    }
    public void setLambdas (double [] lmd)
    {
        this.lambdas=lmd;
    }
    public void setExpoLambda (int expoL)
    {
        this.expo_lmb= expoL;
    }
    public void setExpoCoordinate (int expoC)
    {
        this.expo_coord=expoC;
    }
    
}
