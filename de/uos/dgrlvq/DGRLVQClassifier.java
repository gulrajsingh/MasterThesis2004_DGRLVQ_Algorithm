package de.uos.dgrlvq;
import java.util.*;
import de.uos.dgrlvq.distance.*;
import de.uos.dgrlvq.distance.DistanceGeneralizedWeightedEuclidean;
/** This class implements the Dynamic version of GRLVQ algoritm as DGRLVQ classifier with many more
 * additions.
 *
 * Prototypes can be generated and killed in a dynamic enviornment.
 *@author Gulraj Singh Mrok, University Osnabrück, Germany
 */
public class DGRLVQClassifier
{
    /** number of prortypes */
    protected int num_prototypes = 0;
    /** vector continer of all prototypes of all categories */
    protected Vector prototypes = new Vector ();
    /** distance referance */
    protected DistanceInterface distanceRef=new DistanceGeneralizedWeightedEuclidean ();
    /** index shuffler of categories */
    protected IndexShuffler siForCategory = new IndexShuffler (2);
    /** index shuffler of pattern */
    protected IndexShuffler siForPattern [];
    /** 3D pattern */
    protected double pattern_of_each_class[][][]=new double[2][2][2];
    
    /** pattern lenght */
    protected int pattern_length=2;
    /** pattern dimention */
    protected int pattern_dimension=2;
    
    /** valid number of pattern per category */
    protected int [] validNumberOfPatternPerClass=new int[2];
    /** qualify winner count */
    protected int qualifyWinnerCount=0;
    /** number of categories */
    protected int num_categories=2;
    /** number of prototypes per category */
    protected int num_prototypes_per_category= 1;
    
    /** max persentage of prototype poputation that is allowed to get killed */
    protected int maxPersentageOfPoputationAllowedToDie=10;
    
    /**total modulus*/
    protected int total_mode=5000;
    /**total cycles*/
    protected int total_cycles=20;
    
    /** learning rate of global lambda */
    protected double learning_rate_lambda = 0.00001;
    /** learning rate decay in percentage */
    protected int learning_persent_decrease=0;
    /** learning rate correct */
    protected double learning_rate_correct = 0.1;
    /** learning rate wrong */
    protected double learning_rate_wrong = 0.00001;
    
    /** tells if dynamic allocation and addition of new prototypes are based upon mean
     * misclassified input vectors
     */
    protected boolean withMeanDynamicAllocation= true;
    /** tells if killing of loser prototypes is allowed */
    protected boolean isKillingLoserPrototypesAllowed= false;
    /** normalize lambda flag */
    protected boolean normalizeLambda=true;
    
    protected boolean isPointAmbiguous=true;
    
    //for taining
    /** creates new classifier object
     * @param nppc initial number of prototypes per category
     * @param pattern pattern
     * @param validNumberOfPattern valid number of pattern per class
     * @param lrc learning rate correct
     * @param lrw learing rate wrong
     * @param lrl learing rate lambda
     * @param lpd decay of learning in persentage
     * @param nl normlize lambda if true
     * @param withMeanAllocation allocation with by mean method
     * @param isKillingAllowed true if killing of prototype is allowed
     * @param wc winner count
     * @param maxPoputationAllowedToDie persentage of prototype population allowed to die
     * @param numOfCategories number of categories
     * @param pl pattern length
     * @param pd pattern dimention
     * @param d distance reference
     */
    public DGRLVQClassifier (int nppc, double[][][] pattern, int[] validNumberOfPattern, double lrc, double lrw, double lrl, int lpd, boolean nl, boolean withMeanAllocation, boolean isKillingAllowed, int wc, int maxPoputationAllowedToDie, int numOfCategories, int pl, int pd, DistanceInterface d)
    {
        this.num_prototypes_per_category = nppc;
        this.pattern_of_each_class=pattern;
        this.validNumberOfPatternPerClass=validNumberOfPattern;
        this.learning_rate_correct=lrc;
        this.learning_rate_lambda=lrl;
        this.learning_rate_wrong=lrw;
        this.learning_persent_decrease=lpd;
        this.normalizeLambda=nl;
        this.withMeanDynamicAllocation= withMeanAllocation;
        this.isKillingLoserPrototypesAllowed= isKillingAllowed;
        this.qualifyWinnerCount=wc;
        this.maxPersentageOfPoputationAllowedToDie=maxPoputationAllowedToDie;
        this.num_categories=numOfCategories;
        this.pattern_length=pl;
        this.pattern_dimension=pd;
        this.distanceRef=d;
        prototypes=new Vector ();
        siForCategory = new IndexShuffler (num_categories); /* number of categories */
        siForPattern= new IndexShuffler[num_categories];
        //System.out.println(siForPattern.length+" ");
        
        for(int i=0;i<siForPattern.length; i++)
        {
            //System.out.println(i+" "+validNumberOfPatternPerClass[i]);
            siForPattern[i]=new IndexShuffler (validNumberOfPatternPerClass[i]);
        }
        try
        {
            int [] numberOfWronglyClassified =new int[num_categories];
            double[][] cordMean= new double[num_categories][pattern_dimension];
            double  inputs[] = new double[pattern_of_each_class[0].length];
            for(int i=0; i<pattern_of_each_class.length; i++)
            {
                for(int j=0;j<validNumberOfPatternPerClass[i];j++)
                {
                    for(int k=0;k<pattern_of_each_class[0].length;k++)
                    {
                        inputs[k]=pattern_of_each_class[i][k][j];
                    }
                    for(int m=0; m<pattern_of_each_class[0].length; m++)
                    {
                        //System.out.print("cordMean["+i+"]["+m+"]="+cordMean[i][m]+" + inputs["+m+"]="+inputs[m]+" --> ");
                        cordMean[i][m] = (cordMean[i][m] + inputs[m]);
                        //System.out.println("cordMean["+i+"]["+m+"]="+cordMean[i][m]);
                    }
                    numberOfWronglyClassified[i]++;
                    
                }
            }
            
            for(int i=0;i< cordMean.length; i++)
            {
                for(int j=0; j<pattern_dimension; j++)
                {
                    if(numberOfWronglyClassified[i]>0)
                    {
                        //System.out.print("cordMean["+i+"]["+j+"]="+cordMean[i][j]+" / numberOfWronglyClassified["+i+"]="+numberOfWronglyClassified[i]+" --> ");
                        cordMean[i][j] = cordMean[i][j]/numberOfWronglyClassified[i];
                        //System.out.println("cordMean["+i+"]["+j+"]="+cordMean[i][j]);
                    }
                }
            }
            
            for(int i=0;i< cordMean.length; i++)
            {
                if(numberOfWronglyClassified[i]>0)
                {
                    for (int n = 0; n < num_prototypes_per_category; n++)
                    {
                        addPrototype ( i ,cordMean[i], distanceRef, this.learning_rate_correct,this.learning_rate_wrong);
                        System.out.println ("A prototype is added with Mean vlaue in cluster "+ i);
                    }
                }
            }
        }
        catch(Exception e)
        {
            e.printStackTrace ();
        }
        
        /*
        for (int c = 0; c < num_categories; c++)
        {
            for (int n = 0; n < num_prototypes_per_category; n++)
            {
                addPrototype ( c ,inputs, distanceRef, this.learning_rate_correct,this.learning_rate_wrong);
                //addPrototype (pattern_dimension, c, this.distanceRef, learning_rate_correct,learning_rate_wrong);
            }
        }
         */
        //System.out.println (this.getAllPrototypeProperties ());
    }
    //testing
    /** creates new object for testing.
     * a vector containing all information to construct a classifier is sent as
     * argument. from this vector all the system variable information is attained and
     * prototypes are assigned.
     * @param prototypePropertiesList properties list
     */
    public DGRLVQClassifier (Vector prototypePropertiesList)
    {
        prototypes=new Vector ();
        num_prototypes=0;
        for (Enumeration e = prototypePropertiesList.elements (); e.hasMoreElements (); )
        {
            String str= e.nextElement ().toString ();
            try
            {
                if(str.startsWith ("#"))
                {
                    str=str.replace ('#', ' ');
                    StringTokenizer st=new StringTokenizer (str,"|");
                    StringTokenizer lambdas= new StringTokenizer (st.nextToken ()," ");
                    double l[]=new double[lambdas.countTokens ()];
                    
                    int i=0;
                    while(lambdas.hasMoreTokens ())
                    {
                        try
                        {
                            l[i++]= Double.valueOf (lambdas.nextToken ()).doubleValue ();
                            //System.out.print ( " "+l[0]);
                        }
                        catch(Exception ex)
                        {
                            ex.printStackTrace ();
                        }
                    }
                    int lm_expo= Integer.valueOf (st.nextToken ()).intValue ();
                    int cord= Integer.valueOf (st.nextToken ()).intValue ();
                    String distanceClassName=st.nextToken ();
                    try
                    {
                        //System.out.println (">"+distanceClassName+"<");
                        this.distanceRef = (DistanceInterface)Class.forName ("de.uos.dgrlvq.distance."+distanceClassName).newInstance ();
                    }
                    catch(Exception ex)
                    {
                        ex.printStackTrace ();
                    }
                    distanceRef.setLambdas (l);
                    distanceRef.setExpoLambda (lm_expo);
                    distanceRef.setExpoCoordinate (cord);
                    this.learning_rate_lambda=Double.valueOf (st.nextToken ()).doubleValue ();
                    this.learning_persent_decrease=Integer.valueOf (st.nextToken ()).intValue ();
                    this.learning_rate_correct=Double.valueOf (st.nextToken ()).doubleValue ();
                    this.learning_rate_wrong=Double.valueOf (st.nextToken ()).doubleValue ();
                    this.maxPersentageOfPoputationAllowedToDie=Integer.valueOf (st.nextToken ()).intValue ();
                    this.qualifyWinnerCount= Integer.valueOf (st.nextToken ()).intValue ();
                    this.isKillingLoserPrototypesAllowed=st.nextToken ().startsWith ("true");
                    this.normalizeLambda=st.nextToken ().startsWith ("true");
                    this.withMeanDynamicAllocation=st.nextToken ().startsWith ("true");
                    this.total_mode=Integer.valueOf (st.nextToken ()).intValue ();
                    this.total_cycles=Integer.valueOf (st.nextToken ()).intValue ();
                }
                else
                {
                    StringTokenizer st=new StringTokenizer (str,"|");
                    StringTokenizer weights= new StringTokenizer (st.nextToken ()," ");
                    double w[]=new double[weights.countTokens ()];
                    int i=0;
                    while(weights.hasMoreTokens ())
                    {
                        try
                        {
                            w[i++]= Double.valueOf (weights.nextToken ()).doubleValue ();
                        }
                        catch(Exception ex)
                        {
                            ex.printStackTrace ();
                        }
                    }
                    double activation= Double.valueOf (st.nextToken ()).doubleValue ();
                    double output=Double.valueOf (st.nextToken ()).doubleValue ();
                    int category_of_this_prototype= Integer.valueOf (st.nextToken ()).intValue ();
                    int winnerCount= Integer.valueOf (st.nextToken ()).intValue ();
                    double lrc=Double.valueOf (st.nextToken ()).doubleValue ();
                    double lrw=Double.valueOf (st.nextToken ()).doubleValue ();
                    prototypes.addElement (new Prototype ( w, activation, output, category_of_this_prototype, winnerCount, distanceRef, lrc, lrw));
                    num_prototypes++;
                }
            }
            catch(Exception ex)
            {
                ex.printStackTrace ();
            }
        }
    }
    /** tests the vectors
     * @param testVectors test vector
     * @return log of errors
     */
    public String testPattern (Vector testVectors)
    {
        int errorPatternCount=0;
        double errorPersentage=0;
        int totalVectors=testVectors.size ();
        StringBuffer log =new StringBuffer ();
        StringBuffer logErrorPattern =new StringBuffer ();
        StringBuffer missclassifiedPoints =new StringBuffer ();
        int counter=-1;
        int tp=0;
        int tn=0;
        int fp=0;
        int fn=0;
        int healthySamples=0;
        int diseaseSamples=0;
        missclassifiedPoints.append ("\n#Points missclassified:\n");
        log.append ("#Error Patterns are as follow:\n");
        int spanOfPrototype[]= new int[this.num_prototypes];
        while(testVectors.size ()>0)
        {
            counter++;
            try
            {
                String str= (String) testVectors.firstElement ();
                testVectors.removeElementAt (0);
                int category=-1;
                String values[]=str.split (" ");
                double w[]=new double[values.length-1];
                try
                {
                    category=Integer.valueOf (values[values.length-1]).intValue ();
                }
                catch(Exception ex)
                {}
                for(int i=0; i< w.length; i++)
                {
                    try
                    {
                        w[i]= Double.valueOf (values[i]).doubleValue ();
                    }
                    catch(Exception ex)
                    {
                        ex.printStackTrace ();
                    }
                }
                if(category==0)
                {
                    healthySamples++;
                    if(!isInputVectorCorrectlyClassified (w,category))
                    {
                        missclassifiedPoints.append ("test line "+counter+" with category "+category+ " is missclassified\n");
                        errorPatternCount++;
                        fp++;
                    }
                    else
                    {
                        spanOfPrototype[getNearestPrototype (w)]++;
                        tn++;
                    }
                }
                else
                {
                    diseaseSamples++;
                    if(!isInputVectorCorrectlyClassified (w,category))
                    {
                        missclassifiedPoints.append ("test line "+counter+" with category "+category+ " is missclassified\n");
                        errorPatternCount++;
                        fn++;
                    }
                    else
                    {
                        spanOfPrototype[getNearestPrototype (w)]++;
                        tp++;
                    }
                }
                
            }
            catch(Exception ex)
            {
                ex.printStackTrace ();
            }
            
        }
        double sensitivity=((double)tp) / ((double)tp + (double)fn);
        double specificity=((double)tn) / ((double)tn + (double)fp);
        double PPV= (double)tp/((double)tp+(double)fp);
        double NPV= (double) tn/((double)fn+(double)tn);
        errorPersentage= ((double)errorPatternCount/(double)totalVectors)*100;
        double successRate=100.-errorPersentage;
        log.append ("#Error Pattern Count= "+errorPatternCount+"\n#Total Pattern= "+totalVectors+"\n#Error= "+errorPersentage+" %\n#Success= "+successRate+" %\n#Sensitivity= "+(sensitivity*100)+" %\n#Specificity= "+(specificity*100)+" %\n#PPV= "+(PPV*100)+" %\n#NPV= "+(NPV*100)+" %");
        //log.append ("\nError Pattern:\n"+logErrorPattern);
        int i=0;
        for (Enumeration e = prototypes.elements (); e.hasMoreElements (); i++)
        {
            log.append ("\nPrototype of Category "+((Prototype) e.nextElement ()).getCategory ()+" has span over "+spanOfPrototype[i]+" points");
            
        }
        log.append (missclassifiedPoints.toString ());
        return log.toString ();
    }
    
    /** tests the vectors
     * @param testVectors test vector
     * @return log of errors
     */
    public String testPatternWithNoDetails (Vector testVectors)
    {
        /*
        double lamb[]=this.distanceRef.getLambdas ();
        System.out.println();
        for (int i = 0; i < lamb.length; i++)
        {
            System.out.print(" "+lamb[i]);
            if(lamb[i] <= 0.0001)
            {
                lamb[i]=0.;
            }
        }
        this.distanceRef.setLambdas (lamb);
        System.out.println("\n\n\nThreshold 0.0001:: ");
        for (int i = 0; i < lamb.length; i++)
        {
            System.out.print(" "+lamb[i]);
        }
        System.out.println();
        */ 
        int errorPatternCount=0;
        double errorPersentage=0;
        int totalVectors=testVectors.size ();
        StringBuffer log =new StringBuffer ();
        int tp=0;
        int tn=0;
        int fp=0;
        int fn=0;
        int healthySamples=0;
        int diseaseSamples=0;
        while(testVectors.size ()>0)
        {
            try
            {
                String str= (String) testVectors.firstElement ();
                testVectors.removeElementAt (0);
                int category=-1;
                String values[]=str.split (" ");
                double w[]=new double[values.length-1];
                try
                {
                    category=Integer.valueOf (values[values.length-1]).intValue ();
                }
                catch(Exception ex)
                {}
                for(int i=0; i< w.length; i++)
                {
                    try
                    {
                        w[i]= Double.valueOf (values[i]).doubleValue ();
                    }
                    catch(Exception ex)
                    {
                        ex.printStackTrace ();
                    }
                }
                if(category==0)
                {
                    healthySamples++;
                    if(!isInputVectorCorrectlyClassified (w,category))
                    {
                        errorPatternCount++;
                        fp++;
                    }
                    else
                    {
                        tn++;
                    }
                }
                else
                {
                    diseaseSamples++;
                    if(!isInputVectorCorrectlyClassified (w,category))
                    {
                        errorPatternCount++;
                        fn++;
                    }
                    else
                    {
                        tp++;
                    }
                }
                
            }
            catch(Exception ex)
            {
                ex.printStackTrace ();
            }
            
        }
        double sensitivity=((double)tp) / ((double)tp + (double)fn);
        double specificity=((double)tn) / ((double)tn + (double)fp);
        double PPV= (double)tp/((double)tp+(double)fp);
        double NPV= (double) tn/((double)fn+(double)tn);
        errorPersentage= ((double)errorPatternCount/(double)totalVectors)*100;
        double successRate=100.-errorPersentage;
        log.append (totalVectors+"\t"+errorPatternCount+"\t"+errorPersentage+"\t"+successRate+"\t"+(sensitivity*100)+"\t"+(specificity*100)+"\t"+(PPV*100)+"\t"+(NPV*100));
        
        return log.toString ();
    }
    
    /** tests the vectors with probability classification threshold
     * @param testVectors test vector
     * @return log of errors
     */
    public String testPatternWithThreshold (Vector testVectors,double thr)
    {
        int errorPatternCount=0;
        double errorPersentage=0;
        int totalVectors=testVectors.size ();
        StringBuffer log =new StringBuffer ();
        StringBuffer logErrorPattern =new StringBuffer ();
        StringBuffer missclassifiedPoints =new StringBuffer ();
        int counter=-1;
        int tp=0;
        int tn=0;
        int fp=0;
        int fn=0;
        int healthySamples=0;
        int diseaseSamples=0;
        missclassifiedPoints.append ("\n#Points missclassified:\n");
        log.append ("#Error Patterns are as follow:\n");
        int spanOfPrototype[]= new int[this.num_prototypes];
        while(testVectors.size ()>0)
        {
            counter++;
            try
            {
                String str= (String) testVectors.firstElement ();
                testVectors.removeElementAt (0);
                int category=-1;
                String values[]=str.split (" ");
                double w[]=new double[values.length-1];
                try
                {
                    category=Integer.valueOf (values[values.length-1]).intValue ();
                }
                catch(Exception ex)
                {}
                for(int i=0; i< w.length; i++)
                {
                    try
                    {
                        w[i]= Double.valueOf (values[i]).doubleValue ();
                    }
                    catch(Exception ex)
                    {
                        ex.printStackTrace ();
                    }
                }
                if(category==0)
                {
                    healthySamples++;
                    if(!isInputVectorProbaballyClassified (w,category,thr))
                    {
                        if(!isPointAmbiguous)
                        {
                            missclassifiedPoints.append ("test line "+counter+" with category "+category+ " is missclassified\n");
                            errorPatternCount++;
                            fp++;
                        }
                    }
                    else
                    {
                        if(!isPointAmbiguous)
                        {
                            spanOfPrototype[getNearestPrototype (w)]++;
                            tn++;
                        }
                    }
                }
                else
                {
                    diseaseSamples++;
                    if(!isInputVectorProbaballyClassified (w,category,thr))
                    {
                        if(!isPointAmbiguous)
                        {
                            missclassifiedPoints.append ("test line "+counter+" with category "+category+ " is missclassified\n");
                            errorPatternCount++;
                            fn++;
                        }
                    }
                    else
                    {
                        if(!isPointAmbiguous)
                        {
                            spanOfPrototype[getNearestPrototype (w)]++;
                            tp++;
                        }
                    }
                }
            }
            catch(Exception ex)
            {
                ex.printStackTrace ();
            }
        }
        double sensitivity=((double)tp) / ((double)tp + (double)fn);
        double specificity=((double)tn) / ((double)tn + (double)fp);
        double PPV= (double)tp/((double)tp+(double)fp);
        double NPV= (double) tn/((double)fn+(double)tn);
        int relativeTotalVectors=tp+tn+fp+fn;
        errorPersentage= ((double)errorPatternCount/(double)relativeTotalVectors)*100;
        double successRate=100.-errorPersentage;
        log.append ("#Error Pattern Count= "+errorPatternCount+"\n#Total Pattern= "+totalVectors+"\n#Total Relative Pattern= "+relativeTotalVectors+"\n#Error= "+errorPersentage+" %\n#Success= "+successRate+" %\n#Sensitivity= "+(sensitivity*100)+" %\n#Specificity= "+(specificity*100)+" %\n#PPV= "+(PPV*100)+" %\n#NPV= "+(NPV*100)+" %");
        //log.append ("\nError Pattern:\n"+logErrorPattern);
        int i=0;
        for (Enumeration e = prototypes.elements (); e.hasMoreElements (); i++)
        {
            log.append ("\nPrototype of Category "+((Prototype) e.nextElement ()).getCategory ()+" has span over "+spanOfPrototype[i]+" points");
            
        }
        log.append (missclassifiedPoints.toString ());
        return log.toString ();
    }
    
    
    /** tests the vectors with probability classification threshold
     * @param testVectors test vector
     * @return log of errors
     */
    public String testPatternWithThresholdAndNoDetails (Vector testVectors,double thr)
    {
        int errorPatternCount=0;
        double errorPersentage=0;
        int totalVectors=testVectors.size ();
        StringBuffer log =new StringBuffer ();
        int tp=0;
        int tn=0;
        int fp=0;
        int fn=0;
        int healthySamples=0;
        int diseaseSamples=0;
        while(testVectors.size ()>0)
        {
            try
            {
                String str= (String) testVectors.firstElement ();
                testVectors.removeElementAt (0);
                int category=-1;
                String values[]=str.split (" ");
                double w[]=new double[values.length-1];
                try
                {
                    category=Integer.valueOf (values[values.length-1]).intValue ();
                }
                catch(Exception ex)
                {}
                for(int i=0; i< w.length; i++)
                {
                    try
                    {
                        w[i]= Double.valueOf (values[i]).doubleValue ();
                    }
                    catch(Exception ex)
                    {
                        ex.printStackTrace ();
                    }
                }
                if(category==0)
                {
                    healthySamples++;
                    if(!isInputVectorCorrectlyClassified (w,category,thr))
                    {
                        if(!isPointAmbiguous)
                        {
                            errorPatternCount++;
                            fp++;
                        }
                    }
                    else
                    {
                        if(!isPointAmbiguous)
                        {
                            tn++;
                        }
                    }
                }
                else
                {
                    diseaseSamples++;
                    if(!isInputVectorCorrectlyClassified(w,category,thr))
                    {
                        if(!isPointAmbiguous)
                        {
                            errorPatternCount++;
                            fn++;
                        }
                    }
                    else
                    {
                        if(!isPointAmbiguous)
                        {
                            tp++;
                        }
                    }
                }
            }
            catch(Exception ex)
            {
                ex.printStackTrace ();
            }
        }
        double sensitivity=((double)tp) / ((double)tp + (double)fn);
        double specificity=((double)tn) / ((double)tn + (double)fp);
        double PPV= (double)tp/((double)tp+(double)fp);
        double NPV= (double) tn/((double)fn+(double)tn);
        int relativeTotalVectors=tp+tn+fp+fn;
        errorPersentage= ((double)errorPatternCount/(double)relativeTotalVectors)*100;
        double successRate=100.-errorPersentage;
        log.append (relativeTotalVectors+"\t"+errorPatternCount+"\t"+errorPersentage+"\t"+successRate+"\t"+(sensitivity*100)+"\t"+(specificity*100)+"\t"+(PPV*100)+"\t"+(NPV*100));
        return log.toString ();
    }
    
    /** classify vectors
     * @param classifyVectors vectors to classify
     * @return classified vectors
     */
    public String classifyPattern (Vector classifyVectors)
    {
        StringBuffer log=new StringBuffer ();
        log.append ("$Classified Patterns are as follow:\n");
        while(classifyVectors.size ()>0)
        {
            String str= (String) classifyVectors.firstElement ();
            classifyVectors.removeElementAt (0);
            String values[]=str.split (" ");
            
            try
            {
                //StringTokenizer pattern= new StringTokenizer(str," ");
                double w[]=new double[values.length];
                
                for(int i=0; i< w.length; i++)
                {
                    try
                    {
                        w[i]= Double.valueOf (values[i]).doubleValue ();
                    }
                    catch(Exception ex)
                    {
                        ex.printStackTrace ();
                    }
                }
                
                int category= classifyVector (w);
                log.append (str+" "+category+"\n");
            }
            catch(Exception ex)
            {
                ex.printStackTrace ();
            }
        }
        return log.toString ();
    }
    
    /** tests the vectors
     * @param testVectors test vector
     * @return log of errors
     */
    public int[] getSpanIndexOfPrototypes ()
    {
        int spanOfPrototype[]= new int[prototypes.size ()];
        double  inputs[] = new double[pattern_of_each_class[0].length];
        for(int i=0; i<pattern_of_each_class.length; i++)
        {
            for(int j=0;j<validNumberOfPatternPerClass[i];j++)
            {
                for(int k=0;k<pattern_of_each_class[0].length;k++)
                {
                    inputs[k]=pattern_of_each_class[i][k][j];
                    //System.out.print (" "+inputs[k]);
                    //System.out.print (" "+pattern_of_each_class[i][k][j]);
                }
                //System.out.println (" "+i);
                
                if(isInputVectorCorrectlyClassified (inputs,i))
                {
                    spanOfPrototype[getNearestPrototype (inputs)]++;
                }
            }
            
        }
        return spanOfPrototype;
    }
    
    /** updates the learning of all prototypes, lambdas and learing decay. */
    public void updateLearning ()
    {
        for (Enumeration e = prototypes.elements (); e.hasMoreElements (); )
        {
            Prototype p= ((Prototype) e.nextElement ());
            p.setLearningRateCorrect (p.getLearningRateCorrect () - ((double)learning_persent_decrease/100.0)*(p.getLearningRateCorrect ()));
            p.setLearningRateWorng (p.getLearningRateWorng () - ((double)learning_persent_decrease/100.0)*(p.getLearningRateWorng ()));
        }
        
        //System.out.print ("learning_persent_decrease="+learning_persent_decrease+" learning_rate_lambda="+learning_rate_lambda);
        learning_rate_lambda -= ((double)learning_persent_decrease/100.0)*learning_rate_lambda;
        //System.out.println (" -> "+learning_rate_lambda);
    }
    
    /** removes loser prototypes */
    public void removeLosers ()
    {
        if(isKillingLoserPrototypesAllowed)
        {
            System.out.println ("Killed prototypes \n");
            Vector p= (Vector) removeLoserPrototypes (qualifyWinnerCount, null,maxPersentageOfPoputationAllowedToDie);
            for (Enumeration e = p.elements (); e.hasMoreElements (); )
            {
                Prototype proto= ((Prototype) e.nextElement ());
                System.out.println ("category= "+proto.getCategory ()+" qualifying count= "+proto.getWinnerCount ());
            }
        }
        int span[]=this.getSpanIndexOfPrototypes ();
        Vector temp=new Vector ();
        for(int i=0;i<span.length;i++)
        {
            if(span[i]!=0)
            {
                try
                {
                    temp.addElement (prototypes.elementAt (i));
                }
                catch(Exception e)
                {
                    e.printStackTrace ();
                }
            }
            else
            {
                Prototype proto= (Prototype)prototypes.elementAt (i);
                System.out.println ("###########Prototype of category "+proto.getCategory ()+" at index "+i+" is removed as it spanned no point");
            }
        }
        prototypes=temp;
        this.num_prototypes=prototypes.size ();
    }
    
    /** trains the prtotypes.
     *
     * trains one closest correct and one wrong prototype
     * @param i input
     * @param c category
     * @param lrl learning rate lambdas
     * @param nl normalize lambdas
     */
    public void train (double[] i, int c, double lrl, boolean nl)
    {
        Prototype closest_prototypes[] = activate (i, c);
        closest_prototypes[0].learn (i, c, lrl, nl);
        closest_prototypes[1].learn (i, c, lrl, nl);
    }
    
    /** tests input
     * @param i input
     * @param c category
     */
    public void test (double[] i, int c)
    {
        Prototype closest_prototypes[] = activate (i, c);
    }
    
    /** avtivate all prototypes for input
     * @param i input
     * @param c category
     * @return array of one closest correct and one closest wrong prototype
     */
    public Prototype[] activate (double[] i, int c)
    {
        for (Enumeration e = prototypes.elements (); e.hasMoreElements (); )
            ((Prototype) e.nextElement ()).activate (i);
        return getClosestPrototypes (i, c);
    }
    
    /** adds new prototypes
     * @param pd pattern dimention
     * @param c category
     * @param d distance reference
     * @param lrc learning rate correct
     * @param lrw learning rate wrong
     */
    public void addPrototype (int pd, int c, DistanceInterface d, double lrc, double lrw)
    {
        prototypes.addElement (new Prototype (pd, c, d, lrc, lrw));
        num_prototypes++;
    }
    /** adds new prototype
     * @param c category
     * @param w weight vector
     * @param d distance reference
     * @param lrc learning rate correct
     * @param lrw learning rate wrong
     */
    public void addPrototype ( int c, double[] w, DistanceInterface d, double lrc, double lrw)
    {
        prototypes.addElement (new Prototype ( c, w, d, lrc, lrw));
        num_prototypes++;
    }
    
    /** removes loser prototype with qualify winner count less then theshold value
     * @param qc qualify winner count
     */
    public void removeLoserPrototypes (int qc)
    {
        int i = 0;
        for (Enumeration e = prototypes.elements (); e.hasMoreElements (); i++)
        {
            if(((Prototype) e.nextElement ()).getWinnerCount ()< qc)
            {
                prototypes.removeElementAt (i);
                this.num_prototypes--;
            }
        }
    }
    /** remove loser prototypes
     * @param qc qualify winner count
     * @param losers vector in which removed loser prototypes to add
     * @return vector of loser prototypes
     */
    public Vector removeLoserPrototypes (int qc, Vector losers)
    {
        int i = 0;
        if(losers==null)
        {
            losers=new Vector ();
        }
        for (Enumeration e = prototypes.elements (); e.hasMoreElements (); i++)
        {
            if(((Prototype) e.nextElement ()).getWinnerCount ()< qc)
            {
                losers.addElement ((Prototype)prototypes.remove (i));
                this.num_prototypes--;
            }
        }
        return losers;
    }
    /** removes loser prototypes
     * @param qc qualify winner count
     * @param losers vector in which removed prototypes to add
     * @param maxPoputationAllowedToDie maximum prototype population allowed to die
     * @return vector of removed prototypes
     */
    public Vector removeLoserPrototypes (int qc, Vector losers,int maxPoputationAllowedToDie)
    {
        int i = 0;
        int numberOfPrototypesToRemove=(int)(((double)maxPoputationAllowedToDie)/100.)*prototypes.size ();
        if(losers==null)
        {
            losers=new Vector ();
        }
        for (Enumeration e = prototypes.elements (); e.hasMoreElements (); i++)
        {
            if(((Prototype) e.nextElement ()).getWinnerCount ()< qc && numberOfPrototypesToRemove>0 )
            {
                losers.addElement ((Prototype)prototypes.remove (i));
                this.num_prototypes--;
                numberOfPrototypesToRemove--;
            }
        }
        return losers;
    }
    
    /** classifies a vector
     * @param cord vector to classify
     * @return category of vector
     */
    public int classifyVector (double [] cord)
    {
        int i = 0;
        double d;
        double min = Double.MAX_VALUE;
        //System.out.println (min+" =min");
        Prototype prototype;
        int category=-1;
        //Vector sum_correct =new Vector();
        //Vector sum_wrong   =new Vector();
        for (Enumeration e = prototypes.elements (); e.hasMoreElements (); i++)
        {
            prototype = (Prototype) e.nextElement ();
            d =  prototype.getDistance ().calculateDistance (cord, prototype.getWeights ());
            if(d < min)
            {
                min = d;
                category= prototype.getCategory ();
                //sum_correct.add(prototype);
            }
            else
            {
                //sum_wrong.add(prototype);
            }
        }
        return category;
        
    }
    
    private int getNearestPrototype (double [] cord)
    {
        int i = 0;
        double d, min=Double.MAX_VALUE;
        Prototype prototype;
        int index=0;
        for (Enumeration e = prototypes.elements (); e.hasMoreElements (); i++)
        {
            prototype = (Prototype) e.nextElement ();
            d =  prototype.getDistance ().calculateDistance (cord, prototype.getWeights ());
            if(d < min)
            {
                min = d;
                index=i;
            }
        }
        return index;
    }
    /** checks if certain input vector belongs to certain category
     * @param cord input vector
     * @param cat category
     * @return true if input vector belongs to category
    */
    public boolean isInputVectorCorrectlyClassified (double [] cord, int cat)
    {
        int i = 0;
        double d, min=Double.MAX_VALUE;
        //System.out.println (min+" =min");
        Prototype prototype;
        int category=-1;
        //Vector sum_correct =new Vector();
        //Vector sum_wrong   =new Vector();
        for (Enumeration e = prototypes.elements (); e.hasMoreElements (); i++)
        {
            prototype = (Prototype) e.nextElement ();
            d =  prototype.getDistance ().calculateDistance (cord, prototype.getWeights ());
            if(d < min)
            {
                min = d;
                category= prototype.getCategory ();
                //sum_correct.add(prototype);
            }
            else
            {
                //sum_wrong.add(prototype);
            }
        }
        return category==cat;
    }
     
    /** checks if certain input vector belongs to certain category
     * @param cord input vector
     * @param cat category
     * @return true if input vector belongs to category
     */
    
    public boolean isInputVectorCorrectlyClassified (double [] cord, int cat,double thr)
    {
        double min_wrong = 0.;
        double min_correct = 0.;
        int i = 0;
        double d=0;
        double min_mean=0.;
        double max_mean=0.;
        int no_of_correct_prototypes=0;
        int no_of_wrong_prototypes=0;
        double min=Double.MAX_VALUE;
        isPointAmbiguous=false;
        Prototype prototype=null,nearestPrototype=null,awayPrototype=null;
        int categoryCorrect=-1;
        int categoryWrong=-1;
        //Vector sum_correct =new Vector();
        //Vector sum_wrong   =new Vector();
        for (Enumeration e = prototypes.elements (); e.hasMoreElements (); i++)
        {
            prototype = (Prototype) e.nextElement ();
            d =  prototype.getDistance ().calculateDistance (cord, prototype.getWeights ());
            
            if(prototype.getCategory () == cat)
            {
                min_correct = min_correct+d;
                no_of_correct_prototypes++;
                    categoryCorrect= prototype.getCategory ();
                    nearestPrototype=prototype;
             
                
            }
            else
            {
                min_wrong = min_wrong+d;
                no_of_wrong_prototypes++;
                    categoryWrong= prototype.getCategory ();
                    awayPrototype=prototype;
              
            }
        }
        min_mean=min_correct/no_of_correct_prototypes;
        max_mean=min_wrong/no_of_wrong_prototypes;
        
       // if(categoryCorrect==cat)
        //{
          //  return true;
        //}
        //else
        {
            //d= distance from classMean to point.
            //dT= distance of classMean to threshold.
            double beta=1.;
            double sig=(min_mean-max_mean)/(min_mean+max_mean);
            double probality=(1. / (1. + Math.exp (-beta * sig)));
        /*
        double thrCord[]=new double[nearestPrototype.getWeights ().length];
         
        for(int j=0;j<thrCord.length;j++)
        {
            thrCord[j]=thr;
        }
         
        double dT= nearestPrototype.getDistance ().calculateDistance (thrCord, nearestPrototype.getWeights ());
        double sigSqr= -(dT*dT)/StrictMath.log (0.5);
         
        double probality= StrictMath.exp (-(d*d)/sigSqr);
        System.out.println ("d="+d+" dT="+dT+" sigSqr="+sigSqr+" thr="+thr+" probility="+probality+"  exp(d)="+(-(d*d)/sigSqr));
         */
            System.out.println ("PC="+cat+" m+="+min_mean+" m-="+max_mean+" d+-="+(min_mean-max_mean)+" sig="+sig+" thr="+thr+" probility="+probality);
            
            if(probality<=thr)
            {
                isPointAmbiguous=false;
                return min_mean<=max_mean;
            }
            else
            {
                isPointAmbiguous=true;
                return min_mean<=max_mean;                  
            }
        }
    }
    
    /** checks if certain input vector belongs to certain category
     * @param cord input vector
     * @param cat category
     * @return true if input vector belongs to category
     */
    public boolean isInputVectorProbaballyClassified (double [] cord, int cat,double thr)
    {
        double min_wrong = Double.MAX_VALUE;
        double min_correct = Double.MAX_VALUE;
        int i = 0;
        double d=0;
        double min=Double.MAX_VALUE;
        isPointAmbiguous=false;
        Prototype prototype=null,nearestPrototype=null,awayPrototype=null;
        int categoryCorrect=-1;
        int categoryWrong=-1;
        //Vector sum_correct =new Vector();
        //Vector sum_wrong   =new Vector();
        for (Enumeration e = prototypes.elements (); e.hasMoreElements (); i++)
        {
            prototype = (Prototype) e.nextElement ();
            d =  prototype.getDistance ().calculateDistance (cord, prototype.getWeights ());
            /*
             if(d < min)
            {
                min = d;
                categoryCorrect= prototype.getCategory ();
                nearestPrototype=prototype;
            }
             */
            if(prototype.getCategory () == cat)
            {
                if(d < min_correct)
                {
                    min_correct = d;
                    categoryCorrect= prototype.getCategory ();
                    nearestPrototype=prototype;
                }
                
            }
            else
            {
                if(d < min_wrong)
                {
                    min_wrong = d;
                    categoryWrong= prototype.getCategory ();
                    awayPrototype=prototype;
                }
            }
        }
        
       // if(categoryCorrect==cat)
        //{
          //  return true;
        //}
        //else
        {
            //d= distance from classMean to point.
            //dT= distance of classMean to threshold.
            double beta=1.;
            double sig=(min_correct-min_wrong)/(min_correct+min_wrong);
            double probality=(1. / (1. + Math.exp (-beta * sig)));
        /*
        double thrCord[]=new double[nearestPrototype.getWeights ().length];
         
        for(int j=0;j<thrCord.length;j++)
        {
            thrCord[j]=thr;
        }
         
        double dT= nearestPrototype.getDistance ().calculateDistance (thrCord, nearestPrototype.getWeights ());
        double sigSqr= -(dT*dT)/StrictMath.log (0.5);
         
        double probality= StrictMath.exp (-(d*d)/sigSqr);
        System.out.println ("d="+d+" dT="+dT+" sigSqr="+sigSqr+" thr="+thr+" probility="+probality+"  exp(d)="+(-(d*d)/sigSqr));
         */
            System.out.println ("PC="+cat+" d+="+min_correct+" d-="+min_wrong+" d+-="+(min_correct-min_wrong)+" sig="+sig+" thr="+thr+" probility="+probality);
            
            if(probality<=thr)
            {
                isPointAmbiguous=false;
                return min_correct<=min_wrong;
            }
            else
            {
                isPointAmbiguous=true;
                return min_correct<=min_wrong;                  
            }
        }
    }
    
    
    
    /** updates classifier */
    public void update ()
    {
        updateClassifier (pattern_of_each_class, learning_rate_lambda, normalizeLambda);
    }
    /** update classifier
     * @param pattern pattern
     * @param lrl learning rate lambda
     * @param nl normalize lambda
     */
    public void updateClassifier (double [][][] pattern, double lrl, boolean nl)
    {
        double  inputs[] = new double[pattern[0].length];
        
        int proballySelectedCategory= siForCategory.next ();
        int proballySelectedPattern = siForPattern[proballySelectedCategory].next ();
        //System.out.println("--------------");
        //System.out.print("[ "+proballySelectedCategory+" X "+proballySelectedPattern+" ] =");
        
        for(int k=0;k<pattern_of_each_class[0].length;k++)
        {
            inputs[k]=pattern_of_each_class[proballySelectedCategory][k][proballySelectedPattern];
            //System.out.print(" "+inputs[k]);
        }
        //System.out.println();
        train (inputs  , proballySelectedCategory, lrl, nl);
        
    }
    
    /** adds prototype dynamically
     * @return true if done correctly
     */
    public boolean addPrototypeDynamically ()
    {
        System.out.println ("---------------------------");
        try
        {
            //int new_category=0;
            if(!withMeanDynamicAllocation)
            {
                Vector tester=new Vector ();
                double  inputs[] = new double[pattern_of_each_class[0].length];
                //System.out.println ("##### adding by FCFS ######");
                for(int i=0; i<pattern_of_each_class.length; i++)
                {
                    for(int j=0;j<validNumberOfPatternPerClass[i];j++)
                    {
                        for(int k=0;k<pattern_of_each_class[0].length;k++)
                        {
                            inputs[k]=pattern_of_each_class[i][k][j];
                            //System.out.print (" "+inputs[k]);
                            //System.out.print (" "+pattern_of_each_class[i][k][j]);
                        }
                        //System.out.println (" "+i);
                        
                        if(!isInputVectorCorrectlyClassified (inputs, i) && !tester.contains (String.valueOf (i)))
                        {
                            //System.out.println (" <-- NOT classified correctly");
                            tester.addElement (String.valueOf (i));
                            addPrototype ( i,inputs,distanceRef, this.learning_rate_correct,this.learning_rate_wrong);
                            System.out.println ("A prototype is added with FCFS in cluster "+ i);
                        }
                        else
                        {
                            //System.out.println (" <-- classified correctly");
                        }
                        
                    }
                    //System.out.println ("---------------------------");
                }
                
                return true;
            }
            else
            {
                int [] numberOfWronglyClassified =new int[num_categories];
                double[][] cordMean= new double[num_categories][pattern_dimension];
                double  inputs[] = new double[pattern_of_each_class[0].length];
                //System.out.println ("##### adding by Mean Method ######");
                for(int i=0; i<pattern_of_each_class.length; i++)
                {
                    for(int j=0;j<validNumberOfPatternPerClass[i];j++)
                    {
                        for(int k=0;k<pattern_of_each_class[0].length;k++)
                        {
                            inputs[k]=pattern_of_each_class[i][k][j];
                            //System.out.print(" "+inputs[k]);
                            //System.out.print (" "+pattern_of_each_class[i][k][j]);
                        }
                        //System.out.print (" "+i);
                        
                        if(!isInputVectorCorrectlyClassified (inputs, i))
                        {
                            //System.out.println(" <-- NOT classified correctly");
                            for(int m=0; m<pattern_of_each_class[0].length; m++)
                            {
                                //System.out.print("cordMean["+i+"]["+m+"]="+cordMean[i][m]+" + inputs["+m+"]="+inputs[m]+" --> ");
                                cordMean[i][m] = (cordMean[i][m] + inputs[m]);
                                //System.out.println("cordMean["+i+"]["+m+"]="+cordMean[i][m]);
                            }
                            numberOfWronglyClassified[i]++;
                            //System.out.println("numberOfWronglyClassified["+i+"]="+numberOfWronglyClassified[i]);
                        }
                        else
                        {
                            //System.out.println(" <-- classified correctly");
                        }
                    }
                    //System.out.println ("------------------------");
                }
                
                //System.out.println(num_categories+"=num_categories pattern_dimension="+pattern_dimension);
                //System.out.println(cordMean.length+"= cordMean.lenght numberOfWronglyClassified.length="+numberOfWronglyClassified.length);
                //System.out.println(Array.getLength(cordMean)+"= cordMean[].lenght");
                for(int i=0;i< cordMean.length; i++)
                {
                    for(int j=0; j<pattern_dimension; j++)
                    {
                        if(numberOfWronglyClassified[i]>0)
                        {
                            //System.out.print("cordMean["+i+"]["+j+"]="+cordMean[i][j]+" / numberOfWronglyClassified["+i+"]="+numberOfWronglyClassified[i]+" --> ");
                            cordMean[i][j] = cordMean[i][j]/numberOfWronglyClassified[i];
                            //System.out.println("cordMean["+i+"]["+j+"]="+cordMean[i][j]);
                        }
                    }
                }
                
                for(int i=0;i< cordMean.length; i++)
                {
                    if(numberOfWronglyClassified[i]>0)
                    {
                        addPrototype ( i ,cordMean[i], distanceRef, this.learning_rate_correct,this.learning_rate_wrong);
                        System.out.println ("A prototype is added with Mean vlaue in cluster "+ i);
                    }
                }
            }
            return true;
        }
        catch(Exception e)
        {
            e.printStackTrace ();
            return false;
        }
        
    }
    
    public double getMissclassificationErrorValue ()
    {
        double  inputs[] = new double[pattern_of_each_class[0].length];
        int [] numberOfWronglyClassified =new int[num_categories];
        int totalNumberOfPoints=0;
        for(int i=0; i<pattern_of_each_class.length; i++)
        {
            for(int j=0;j<validNumberOfPatternPerClass[i];j++)
            {
                for(int k=0;k<pattern_of_each_class[0].length;k++)
                {
                    inputs[k]=pattern_of_each_class[i][k][j];
                }
                totalNumberOfPoints++;
                if(!isInputVectorCorrectlyClassified (inputs, i))
                {
                    numberOfWronglyClassified[i]++;
                    
                }
            }
        }
        int counter=0;
        for(int i=0;i< numberOfWronglyClassified.length; i++)
        {
            counter=counter+numberOfWronglyClassified[i];
            
        }
        return ((double)counter)/((double)totalNumberOfPoints);
    }
    
    public boolean isAllVectorsCorrectlyClassified (boolean debug)
    {
        double  inputs[] = new double[pattern_of_each_class[0].length];
        int [] numberOfWronglyClassified =new int[num_categories];
        boolean isAllVectorsCorrectlyClassified=true;
        for(int i=0; i<pattern_of_each_class.length; i++)
        {
            for(int j=0;j<validNumberOfPatternPerClass[i];j++)
            {
                for(int k=0;k<pattern_of_each_class[0].length;k++)
                {
                    inputs[k]=pattern_of_each_class[i][k][j];
                }
                
                if(!isInputVectorCorrectlyClassified (inputs, i))
                {
                    if(debug)
                    {
                        numberOfWronglyClassified[i]++;
                        isAllVectorsCorrectlyClassified=false;
                    }
                    else
                    {
                        return false;
                    }
                }
            }
        }
        if(debug)
        {
            System.out.print ("[");
            for(int i=0;i< numberOfWronglyClassified.length; i++)
            {
                //if(numberOfWronglyClassified[i]>0)
                {
                    System.out.print (numberOfWronglyClassified[i]+"|");
                }
            }
            System.out.print ("}");
        }
        return isAllVectorsCorrectlyClassified;
    }
    /** sets number of prototypes
     * @param np number of prototypes
     */
    public void setNumberOfPrototypes (int np)
    {
        this.num_prototypes=np;
    }
    /** sets initial number of prototypes per category
     * @param nppc number of prototypes per category
     */
    public void setPrototypesPerCategory ( int nppc)
    {
        this.num_prototypes_per_category=nppc;
    }
    /** sets 3D pattern
     * @param pattern pattern
     */
    public void setPatternOfEachClass (double [][][] pattern)
    {
        this.pattern_of_each_class=pattern;
    }
    /** sets valid number of pattern per category
     * @param validNumberOfPattern valid number of pattern
     */
    public void setValidNumberOfPatternPerClass (int [] validNumberOfPattern)
    {
        this.validNumberOfPatternPerClass=validNumberOfPattern;
    }
    /** sets qualify winner count
     * @param qwc qualify winner count
     */
    public void setQualifyWinnerCount (int qwc)
    {
        this.qualifyWinnerCount=qwc;
        for (Enumeration e = prototypes.elements (); e.hasMoreElements (); )
            ((Prototype) e.nextElement ()).setWinnerCount (qwc);
    }
    /** sets number of categories
     * @param nc number of categories
     */
    public void setNumberOfCategories (int nc)
    {
        this.num_categories=nc;
    }
    /** sets pattern length
     * @param pl pattern length
     */
    public void setPatternLength (int pl)
    {
        this.pattern_length=pl;
    }
    /** sets pattern dimention
     * @param pd pattern dimention
     */
    public void setPatternDimension (int pd)
    {
        this.pattern_dimension=pd;
    }
    /** sets learning values
     * @param lrc learning rate correct
     * @param lrw learning rate wrong
     * @param lrl learning rate lambda
     * @param lpd persentage of learning decay
     */
    public void setLearning (double lrc, double lrw, double lrl, int lpd)
    {
        this.learning_rate_correct=lrc;
        this.learning_rate_wrong=lrw;
        this.learning_rate_lambda=lrl;
        this.learning_persent_decrease=lpd;
    }
    
    /** sets flag if prototypes to be added with mean dynamic allocation
     * @param wmda true if if with mean dynamic allocation
     */
    public void setWithMeanDynamicAllocation (boolean wmda)
    {
        this.withMeanDynamicAllocation=wmda;
        //System.out.println ("withMeanMethod="+ withMeanDynamicAllocation);
    }
    /** sets flags if killing of prototypes is allowed
     * @param isKillingAllowed true if allowed
     */
    public void setKillingLoserPrototypesAllowed (boolean isKillingAllowed)
    {
        this.isKillingLoserPrototypesAllowed=isKillingAllowed;
    }
    /** sets max population of prototypes allowed to die
     * @param maxPersentage persentage
     */
    public void setMaxPersentageOfPoputationAllowedToDie (int maxPersentage)
    {
        this.maxPersentageOfPoputationAllowedToDie= maxPersentage;
    }
    /** set lambda normalization flag
     * @param nl true if yes
     */
    public void setLambdaNormalization (boolean nl)
    {
        this.normalizeLambda= nl;
    }
    /** sets a new distance reference to all prototypes in enviorment
     * @param d distance reference
     */
    public void setDistanceRefToAllPrototypes (DistanceInterface d)
    {
        this.distanceRef=d;
        for (Enumeration e = prototypes.elements (); e.hasMoreElements (); )
            ((Prototype) e.nextElement ()).setDistance (distanceRef);
    }
    /** gets array of two prototypes.
     *
     * one closest correct prototype and one closest wrong prototype
     * @param inputs input
     * @return closest correct and wrong prototype
     */
    public Prototype[] getClosestPrototypes (double[] inputs, int cat)
    {
        double min_wrong = Double.MAX_VALUE;
        double min_correct = Double.MAX_VALUE;
        double d=Double.MAX_VALUE;
        Prototype prototype;
        Prototype twoCorrectAndWrongClosePrototype[]=new Prototype[2];
        
        for (Enumeration e = prototypes.elements (); e.hasMoreElements (); )
        {
            prototype = (Prototype) e.nextElement ();
            d =  prototype.getActivation ();
            
            if(prototype.getCategory () == cat)
            {
                if(d < min_correct)
                {
                    min_correct = d;
                    twoCorrectAndWrongClosePrototype[0]=prototype;
                }
                
            }
            else
            {
                if(d < min_wrong)
                {
                    min_wrong = d;
                    twoCorrectAndWrongClosePrototype[1]=prototype;
                }
            }
        }
        /*
        twoCorrectAndWrongClosePrototype[0] = (Prototype) prototypes.firstElement ();
        double d,d0,d1;
        int c,c0,c1;
        try
        {
            twoCorrectAndWrongClosePrototype[1] = (Prototype) prototypes.firstElement ();//(Prototype) new Prototype(pattern_dimension, -1);
         
        }
        catch(Exception e)
        {
            e.printStackTrace ();
        }
         
        for (Enumeration e = prototypes.elements (); e.hasMoreElements (); )
        {
            prototype = (Prototype) e.nextElement ();
            //d =  prototype.distance.calculateDistance(inputs, prototype.weights);
            //d0= twoCorrectAndWrongClosePrototype[0].distance.calculateDistance(inputs, prototype.weights);
            //d1= twoCorrectAndWrongClosePrototype[1].distance.calculateDistance(inputs, prototype.weights);
            d =  prototype.getActivation ();
            d0= twoCorrectAndWrongClosePrototype[0].getActivation ();
            d1= twoCorrectAndWrongClosePrototype[1].getActivation ();
         
            c=prototype.getCategory ();
            c0= twoCorrectAndWrongClosePrototype[0].getCategory ();
            c1= twoCorrectAndWrongClosePrototype[1].getCategory ();
            if(c==c0)
            {
                if(d<d0)
                {
                    twoCorrectAndWrongClosePrototype[0]=prototype;
                }
            }
            if(c==c1)
            {
                if(d<d1)
                {
                    twoCorrectAndWrongClosePrototype[1]=prototype;
                }
            }
            else
            {
                if(d1>d0)
                {
                    twoCorrectAndWrongClosePrototype[1]=prototype;
                }
                else
                {
                    twoCorrectAndWrongClosePrototype[0]=prototype;
                }
            }
            //System.out.println(c1+" activation= "+d1);
            //System.out.println(c0+" activation= "+d0);
         
        }
        //System.out.println("+++++++");
        //System.out.println(c1+" activation= "+d1);
        //System.out.println(c0+" activation= "+d0);
        //System.out.println("--------");
         */
        return twoCorrectAndWrongClosePrototype;
    }
    
    /** gets array of categories of all prottypes
     * @return categories
     */
    public int[] getCategories ()
    {
        return getCategories (new int[num_prototypes]);
    }
    
    /** gets array of categories of all prottypes
     * @param dst_categories array in which categories to store
     * @return array of categories
     */
    public int[] getCategories (int[] dst_categories)
    {
        if (dst_categories == null || dst_categories.length != num_prototypes)
            dst_categories = new int[num_prototypes];
        
        int i = 0;
        for (Enumeration e = prototypes.elements (); e.hasMoreElements (); i++)
            dst_categories[i] = ((Prototype) e.nextElement ()).getCategory ();
        
        return dst_categories;
    }
    
    /** gets array of outputs of all prottypes
     * @return array of output
     */
    public double[] getOutputs ()
    {
        return getOutputs (new double[num_prototypes]);
    }
    
    /** gets array of output of all prottypes
     * @param dst_outputs array in which outputs to store
     * @return array of outputs
     */
    public double[] getOutputs (double[] dst_outputs)
    {
        if (dst_outputs == null || dst_outputs.length != num_prototypes)
            dst_outputs = new double[num_prototypes];
        
        int i = 0;
        for (Enumeration e = prototypes.elements (); e.hasMoreElements (); i++)
            dst_outputs[i] = ((Prototype) e.nextElement ()).getOutput ();
        
        return dst_outputs;
    }
    
    /** gets array of weights of all prottypes
     * @return array of weights
     */
    public double[][] getWeights ()
    {
        return getWeights (new double[num_prototypes][pattern_dimension]);
    }
    
    /** gets array of weights of all prottypes
     * @param dst_weights array in which weights to store
     * @return array of weights
     */
    public double[][] getWeights (double[][] dst_weights)
    {
        if (dst_weights == null || dst_weights.length != num_prototypes || dst_weights[0].length != pattern_dimension)
            dst_weights = new double[num_prototypes][pattern_dimension];
        
        int i = 0;
        Prototype prototype;
        for (Enumeration e = prototypes.elements (); e.hasMoreElements (); i++)
        {
            prototype = (Prototype) e.nextElement ();
            System.arraycopy (prototype.getWeights (), 0, dst_weights[i], 0,  Math.min (dst_weights[i].length, prototype.getWeights ().length));
        }
        
        return dst_weights;
    }
    
    /** gets the category of prototype at certain index
     * @return category
     * @param i index
     */
    public int getCategory (int i)
    {
        return ((Prototype)prototypes.get (i)).getCategory ();
    }
    
    /** gets all the properties of certain prototype as a string
     * @param prototype prototype
     * @return string of properties information of prototypes
     */
    public String getPrototypeProperties (Prototype prototype)
    {
        StringBuffer properties=new StringBuffer ();
        double[] weights=prototype.getWeights ();
        for(int i=0; i< weights.length; i++)
        {
            properties.append (weights[i]+" ");
        }
        properties.append ("|");
        properties.append (prototype.getActivation ());
        properties.append ("|");
        properties.append (prototype.getOutput ());
        properties.append ("|");
        properties.append (prototype.getCategory ());
        properties.append ("|");
        properties.append (prototype.getWinnerCount ());
        properties.append ("|");
        properties.append (prototype.getLearningRateCorrect ());
        properties.append ("|");
        properties.append (prototype.getLearningRateWorng ());
        return properties.toString ();
    }
    
    /** gets a log of properties of all prototypes
     * @return string log
     */
    public String getAllPrototypeProperties ()
    {//gutter
        //System.out.println(this.learning_rate_correct+" | "+this.learning_rate_wrong);
        StringBuffer properties=new StringBuffer ();
        properties.append ("#");
        double[] lambdas=this.distanceRef.getLambdas ();
        for(int i=0; i< lambdas.length; i++)
        {
            properties.append (lambdas[i]+" ");
        }
        properties.append ("|");
        properties.append (this.distanceRef.getExpoLambda ());
        properties.append ("|");
        properties.append (this.distanceRef.getExpoCoordinate ());
        properties.append ("|");
        properties.append (this.distanceRef.getName ());
        properties.append ("|");
        properties.append (this.learning_rate_lambda);
        properties.append ("|");
        properties.append (this.learning_persent_decrease);
        properties.append ("|");
        properties.append (this.learning_rate_correct);
        properties.append ("|");
        properties.append (this.learning_rate_wrong);
        properties.append ("|");
        properties.append (this.maxPersentageOfPoputationAllowedToDie);
        properties.append ("|");
        properties.append (this.qualifyWinnerCount);
        properties.append ("|");
        properties.append (this.isKillingLoserPrototypesAllowed);
        properties.append ("|");
        properties.append (this.normalizeLambda);
        properties.append ("|");
        properties.append (this.withMeanDynamicAllocation);
        properties.append ("\n");
        
        for (Enumeration e = prototypes.elements (); e.hasMoreElements (); )
        {
            properties.append (getPrototypeProperties ((Prototype) e.nextElement ())+"\n");
        }
        return properties.toString ();
    }
    /** gets a log of properties of all prototypes. This method will append "modulus" & "cycles" properties to classifier.
     * @param modulus modulus
     * @param cycles cycles
     * @return string log
     */
    public Vector getAllPrototypePropertiesVector (int modulus, int cycles)
    {//gutter
        Vector v=new Vector ();
        StringBuffer properties=new StringBuffer ();
        properties.append ("#");
        double[] lambdas=this.distanceRef.getLambdas ();
        for(int i=0; i< lambdas.length; i++)
        {
            properties.append (lambdas[i]+" ");
        }
        properties.append ("|");
        properties.append (this.distanceRef.getExpoLambda ());
        properties.append ("|");
        properties.append (this.distanceRef.getExpoCoordinate ());
        properties.append ("|");
        properties.append (this.distanceRef.getName ());
        properties.append ("|");
        properties.append (this.learning_rate_lambda);
        properties.append ("|");
        properties.append (this.learning_persent_decrease);
        properties.append ("|");
        properties.append (this.learning_rate_correct);
        properties.append ("|");
        properties.append (this.learning_rate_wrong);
        properties.append ("|");
        properties.append (this.maxPersentageOfPoputationAllowedToDie);
        properties.append ("|");
        properties.append (this.qualifyWinnerCount);
        properties.append ("|");
        properties.append (this.isKillingLoserPrototypesAllowed);
        properties.append ("|");
        properties.append (this.normalizeLambda);
        properties.append ("|");
        properties.append (this.withMeanDynamicAllocation);
        properties.append ("|");
        properties.append (modulus);
        properties.append ("|");
        properties.append (cycles);
        v.addElement (properties);
        
        for (Enumeration e = prototypes.elements (); e.hasMoreElements (); )
        {
            v.addElement (getPrototypeProperties ((Prototype) e.nextElement ()));
        }
        return v;
    }
    /** gets a log of properties of all prototypes. This method will append "modulus" & "cycles" properties to classifier.
     * @param modulus modulus
     * @param cycles cycles
     * @return string log
     */
    public String getAllPrototypeProperties (int modulus, int cycles)
    {//gutter
        StringBuffer properties=new StringBuffer ();
        properties.append ("#");
        double[] lambdas=this.distanceRef.getLambdas ();
        for(int i=0; i< lambdas.length; i++)
        {
            properties.append (lambdas[i]+" ");
        }
        properties.append ("|");
        properties.append (this.distanceRef.getExpoLambda ());
        properties.append ("|");
        properties.append (this.distanceRef.getExpoCoordinate ());
        properties.append ("|");
        properties.append (this.distanceRef.getName ());
        properties.append ("|");
        properties.append (this.learning_rate_lambda);
        properties.append ("|");
        properties.append (this.learning_persent_decrease);
        properties.append ("|");
        properties.append (this.learning_rate_correct);
        properties.append ("|");
        properties.append (this.learning_rate_wrong);
        properties.append ("|");
        properties.append (this.maxPersentageOfPoputationAllowedToDie);
        properties.append ("|");
        properties.append (this.qualifyWinnerCount);
        properties.append ("|");
        properties.append (this.isKillingLoserPrototypesAllowed);
        properties.append ("|");
        properties.append (this.normalizeLambda);
        properties.append ("|");
        properties.append (this.withMeanDynamicAllocation);
        properties.append ("|");
        properties.append (modulus);
        properties.append ("|");
        properties.append (cycles);
        properties.append ("\n");
        
        for (Enumeration e = prototypes.elements (); e.hasMoreElements (); )
        {
            properties.append (getPrototypeProperties ((Prototype) e.nextElement ())+"\n");
        }
        return properties.toString ();
    }
    /** gets number of prototypes
     * @return number of prototypes
     */
    public int getNumberOfPrototypes ()
    {
        return this.num_prototypes;
    }
    /** gets inital number of prototypes per category
     * @return inital number of prototypes per category
     */
    public int getPrototypesPerCategory ()
    {
        return this.num_prototypes_per_category;
    }
    /** gets the pattern
     * @return pattern
     */
    public double[][][] getPatternOfEachClass ()
    {
        return this.pattern_of_each_class;
    }
    /** gets valid number of pattern per class
     * @return valid number of pattern per class
     */
    public int [] getValidNumberOfPatternPerClass ()
    {
        return this.validNumberOfPatternPerClass;
    }
    /** gets qualify winner count
     * @return qualify winner count
     */
    public int getQualifyWinnerCount ()
    {
        return this.qualifyWinnerCount;
    }
    /** gets number of categories
     * @return number of categories
     */
    public int getNumberOfCategories ()
    {
        return this.num_categories;
    }
    /** gets pattern length
     * @return pattern length
     */
    public int getPatternLength ()
    {
        return this.pattern_length;
    }
    /** gets pattern dimention
     * @return pattern dimention
     */
    public int getPatternDimension ()
    {
        return this.pattern_dimension;
    }
    /** gets persentage in learning decay
     * @return learning decay
     */
    public int getLearningRateDecay ()
    {
        return this.learning_persent_decrease;
    }
    /** gets learning rate of lambdas
     * @return learning rate of lambdas
     */
    public double getLearningRateLambda ()
    {
        return this.learning_rate_lambda;
    }
    /** gets learning rate wrong
     * @return learning rate wrong
     */
    public double getLearningRateWrong ()
    {
        return this.learning_rate_wrong;
    }
    /** gets learning rate correct
     * @return learing rate correct
     */
    public double getLearningRateCorrect ()
    {
        return this.learning_rate_correct;
    }
    
    /** checks if with mean dynamic allocation is ON
     * @return true if ON
     */
    public boolean isWithMeanDynamicAllocation ()
    {
        return this.withMeanDynamicAllocation;
    }
    /** checks if killing of loser prototypes is allowed
     * @return true if allowd
     */
    public boolean isKillingLoserPrototypesAllowed ()
    {
        return this.isKillingLoserPrototypesAllowed;
    }
    /** gets the percentage of prototypes allowed to die
     * @return persentage to die
     */
    public int getMaxPersentageOfPoputationAllowedToDie ()
    {
        return this.maxPersentageOfPoputationAllowedToDie;
    }
    /** gets total modulus
     * @return modulus
     */
    public int getTotalModulus ()
    {
        return this.total_mode;
    }
    /** gets total cycles
     * @return cycles
     */
    public int getTotalCycles ()
    {
        return this.total_cycles;
    }
    
    /** checks if normalization of lambda is ON
     * @return true if ON
     */
    public boolean isLambdaNormalization ()
    {
        return this.normalizeLambda;
    }
    /** gets distance reference assigned to all prototypes
     * @return distance reference
     */
    public DistanceInterface getDistanceRefToAllPrototypes ()
    {
        return this.distanceRef;
    }
    /** gets distance reference assigned to all prototypes
     * @return distance reference
     */
    public double [] getLambda ( double[] lamd)
    {
        return distanceRef.getLambdas ();
    }
}