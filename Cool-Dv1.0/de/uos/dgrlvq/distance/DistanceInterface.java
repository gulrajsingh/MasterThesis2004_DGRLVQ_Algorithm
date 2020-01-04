package de.uos.dgrlvq.distance;

/** The DistanceInterface should be implemented by any class whose instances are intended to be used by Prototypes to calculate distance matrices environment.
 *
 * The class must define all methods and procedures in this interface for effective working of Prototypes.
 *
 * This interface is designed to provide a common protocol to all prototypes for distance calculations while they are active.
 *
 * For example, DistanceInterface is implemented by class DistanceGeneralizedWeightedEuclidean and DistanceGeneralizedWeightedEuclidean defines the
 * methods by which distance matrices environment will be calculated.
 * @author Gulraj Singh Mrok, University Osnabrück, Germany
 */
public interface DistanceInterface
{
    /** Sets the global lambda relevance vector to distance matrices.
     *
     * The relevance vector lambda L1, L2… Ln is fixed with square(L1) + square(L2)+ … + square(Ln)=1 where n=dimension of training vector.
     *
     * The lambda relevance vector tells the relevance factor of certain dimention. in
     * other words it tells how important certain dimention is.
     * @param lambdas The lambda relevance vector
     */    
    public void setLambdas(double [] lambdas);
    /** Sets the exponent/power of lambdas
     * @param expoLambda exponent of lambda
     */    
    public void setExpoLambda(int expoLambda);
    /** Sets the exponent/power of coordinate
     * @param expoCoord exponent cooardinate
     */    
    public void setExpoCoordinate(int expoCoord);
    /** Return the lambda vector
     * @return lambda vector
     */    
    public double[] getLambdas();
    /** Returns exponent of Lambda vector
     * @return exponent of lambda
     */    
    public int getExpoLambda();
    /** returns exponent of lambda
     * @return exponent of lambda
     */    
    public int getExpoCoordinate();    
    /** Normalizes Lambda relevance vector.
     *
     * This means the relevance vector lambda L1, L2… Ln must stay as square(L1) + square(L2)+ … + square(Ln)=1 where n=dimension of training vector.
     */    
    public void normalizeLambdas();    
    /** Calculates the distance between training and weight vector.
     * @param x the input vector
     * @param w the weight vector
     * @return the distance
     */    
    public double calculateDistance(double[] x, double[] w);    
    /** Returns the devivate wrt lambda.
     * @param inputs the input vector
     * @param weights weight vector
     * @param i index
     * @return derivate value
     */    
    public double derivativeLamdba(double inputs[], double weights[],int i);
    /** Returns derivate wrt weight.
     * @param x input vector
     * @param w weight vector
     * @param comp_idx index
     * @return the derivate value
     */    
    public double derivativeWeights(double[] x, double[] w, int comp_idx);     
    /** returns the exact name of interface implementing class.
     * @return the exact name of interface implementing class
     */    
    public String getName();
}
