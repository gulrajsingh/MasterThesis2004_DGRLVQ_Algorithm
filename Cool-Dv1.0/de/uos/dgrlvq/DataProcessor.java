package de.uos.dgrlvq;
import java.io.*;
import java.util.*;

/** This class is a helper for the generation of pattern matrices.
 *
 * An object of this class can parse the pattern file, allocates system variables,
 * construct the 3D pattern matries, construct relevance vector lambda etc.
 * @author Gulraj Singh Mrok, University Osnabrück, Germany
 */
public class DataProcessor
{
    /** Name of file to parse */
    protected String fileName="";
    /** the relevance factor lambda */
    protected double [] lambdas;
    /** number of prototypes( this would be used for future versions) */
    protected int prototypes_no=9;
    /** rate eta( this would be used for future versions) */
    protected float rate_eta=0.25f;  //coord_adapt_rate
    /** rate alpha( this would be used for future versions) */
    protected float rate_alpha=1e-6f;  //lambda_adapt_rate
    /** rate of decay of alpha( this would be used for future versions) */
    protected float rate_alphadecay=0.0f;  //lambda_weight_decay
    /** rate of neighborhood annealing( this would be used for future versions) */
    protected float rate_neighborhood_annealing=0.95f;//neighborhood_decay_rate
    /** neighborhood size( this would be used for future versions) */
    protected float neighborhood_size=1e-5f;  //neighborhood_inititial_size
    /** neural gas fader start( this would be used for future versions) */
    protected float ng_fader_start=0.0f;  //ng2grlvq_fader_start
    /** neural gas fader end( this would be used for future versions) */
    protected float ng_fader_end=0.5f;  //ng_fader_end
    /** random seed( this would be used for future versions) */
    protected int randseed=0;  //random_seed
    
    /** length of pattern */
    protected int pattern_length=2;
    /** dimension of pattern */
    protected int pattern_dimension=2;
    /** valid number of pattern per class */
    protected int [] validNumberOfPatternPerClass;
    /** 3D pattern matrix */
    protected double pattern_of_each_class[][][];
    
    /** list of all prototypes */
    protected Vector prototypePropertiesList=new Vector ();
    /** list of all vectors to test */
    protected Vector testVectors=new Vector ();
    /** list of vectors to classifiy */
    protected Vector classifyVectors=new Vector ();
    
    /** total number of categories */
    protected int num_categories=2;
    
    /** Creates a new instance of DataReader
     *
     * an object of this class will parse and sets the system variables
     * @param file file to parse
     */
    public DataProcessor (String file)
    {
        this.fileName=file;
    }
    
    /** Sets system variables for training mode */
    public void setSystemVariablesForTraining ()
    {
        if(true)
        {
            Vector fileVector=new Vector ();
            try
            {
                RandomAccessFile file = new RandomAccessFile (fileName, "r");
                String str = new String ();
                int noOfclasses=0;
                System.out.println ("\nReading file to train: "+fileName);
                System.out.println ("Please wait while file is loaded: ");
                while(file.getFilePointer () < file.length ())
                {
                    str = file.readLine ();
                    System.out.print ("I");
                    if(str!=null && str.length ()>5)
                    {
                        String values[]= str.split (" ");
                        if(noOfclasses<Integer.valueOf (values[values.length-1]).intValue ())
                        {
                            noOfclasses=Integer.valueOf (values[values.length-1]).intValue ();
                        }
                        fileVector.addElement (str);
                    }
                }
                file.close ();
                num_categories=noOfclasses+1;
                System.out.println ("\nNumber of classes= "+num_categories+" Pattern size="+fileVector.size ());
            }
            catch (Exception e)
            {
                e.printStackTrace ();
            }
            
            Vector v[]=new Vector[num_categories];
            for(int i=0;i<v.length;i++)
            {
                v[i]=new Vector ();
            }
            System.out.println("Catagory containers created. Filling up containers:");
            for(int i=0;i<fileVector.size ();i++)
            {
                System.out.print ("I");
                String values[]= fileVector.elementAt (i).toString ().split (" ");
                v[Integer.valueOf (values[values.length-1]).intValue ()].addElement (fileVector.elementAt (i).toString ());
            }
            System.out.println ();
            int largest=0;
            int temp=0;
            for(int i=0;i<v.length;i++)
            {
                System.out.println ("Size of container["+i+"]="+v[i].size ());
                if(temp<v[i].size ())
                {
                    temp=v[i].size ();
                    largest=i;
                }
            }
            int largestSize=v[largest].size ();
            System.out.println ("<---Largest container["+largest+"]="+largestSize);
            
            for(int i=0;i<v.length;i++)
            {
                int diff=largestSize-v[i].size ();
                System.out.println ("Equalization of container["+i+"] from "+v[i].size ()+" to "+largestSize+" diff="+diff);
                for(int j=0;j<diff;j++)
                {
                    System.out.print ("I");
                    fileVector.addElement (v[i].elementAt (j%(v[i].size ())).toString ());
                }
            }
            System.out.println ("\nNumber of classes= "+num_categories+" Pattern size="+fileVector.size ());
            System.out.println ("Allocating memory for pattern Matrix: ");
            try
            {
                pattern_length=fileVector.size ();
                StringTokenizer st=new StringTokenizer (fileVector.firstElement ().toString (), " ");
                pattern_dimension=st.countTokens ()-1;
                String fileLine= "";
                
                double [][] pattern=new double[pattern_dimension+1][pattern_length];
                for(int x=0; 0<fileVector.size (); x++)
                {
                    fileLine= (String)fileVector.firstElement ();
                    fileVector.removeElementAt (0);
                    System.out.print ("I");
                    if(!fileLine.startsWith ("#") || !fileLine.startsWith ("@") )
                    {
                        String values[]=fileLine.split (" ");
                        
                        for(int j=0;j<values.length;j++)
                        {
                            pattern[j][x]= (double) Double.valueOf (values[j]).doubleValue ();
                            //System.out.print(" "+pattern[j][x]);
                        }
                        //System.out.println();
                    }
                }
                
                fileVector=null;
                
                System.out.println ("\nPattern Matrix[ "+(pattern_dimension+1)+" X "+pattern_length+" ]");
                
                int numberOfElementsPerClass[]=new int[num_categories];
                for(int i=0;i<pattern_length; i++)
                {
                    numberOfElementsPerClass[(int)pattern [pattern_dimension][i]]++;
                    //System.out.println(numberOfElementsPerClass[(int)pattern [pattern_dimension][i]]);
                }
                largest=0;
                temp=0;
                for(int i=0;i<numberOfElementsPerClass.length;i++)
                {
                    if(temp<numberOfElementsPerClass[i])
                    {
                        temp=numberOfElementsPerClass[i];
                        largest=i;
                    }
                }
                pattern_of_each_class=new double[num_categories][pattern_dimension][numberOfElementsPerClass[largest]];
                
                validNumberOfPatternPerClass=new int[num_categories];
                
                for(int i=0;i<numberOfElementsPerClass.length;i++)
                {
                    validNumberOfPatternPerClass[i]=numberOfElementsPerClass[i];
                }
                
                for(int i=0;i<validNumberOfPatternPerClass.length;i++)
                {
                    System.out.println ("Category= "+i+"  numberOfElements= "+validNumberOfPatternPerClass[i]);
                }
                System.out.println ("num_categories="+num_categories+" pattern_dimension="+pattern_dimension+" pattern_length="+pattern_length);
                System.out.println (largest+" <--largest");
                System.out.println ("3D Pattern Matrix["+ pattern_of_each_class.length+" X "+pattern_of_each_class[0].length+" X "+pattern_of_each_class[0][0].length+" ]");
                
                try
                {
                    for(int k=0 ; k<pattern_length ;k++)
                    {
                        int category=(int)pattern[pattern_dimension][k];
                        int i=numberOfElementsPerClass[category]-1;
                        if(i>=0)
                        {
                            for(int j=0; j<pattern_dimension; j++)
                            {
                                double value=pattern[j][k];
                                pattern_of_each_class[category][j][i]=value;
                                //pattern_of_each_class[category][j][i]=StrictMath.log (value+20.);
                                //pattern_of_each_class[category][j][i]=StrictMath.pow ((value+20.),2.7);
                                //System.out.print(" "+pattern_of_each_class[category][j][i]+" "+category);
                                
                            }
                            numberOfElementsPerClass[category]--;
                        }
                        //System.out.println(" ");
                    }
                    
                    if(lambdas!=null)
                    {
                        if(lambdas[0]<0. || lambdas[0]>1.)
                        {
                            lambdas= new double[pattern_dimension];
                            //System.out.print("Lambda= ");
                            for(int i=0;i<pattern_dimension;i++)
                            {
                                lambdas[i]=Math.sqrt (1.0/pattern_dimension);
                                //System.out.print(" "+lambdas[i]);
                            }
                            //System.out.println();
                        }
                    }
                    if(lambdas==null)
                    {
                        lambdas= new double[pattern_dimension];
                        //System.out.print("Lambda= ");
                        for(int i=0;i<pattern_dimension;i++)
                        {
                            lambdas[i]=Math.sqrt (1.0/pattern_dimension);
                            //System.out.print(" "+lambdas[i]);
                        }
                        //System.out.println();
                    }
                    
                }
                catch(Exception e)
                {
                    e.printStackTrace ();
                }
            }
            catch(Exception e)
            {
                e.printStackTrace ();
            }
        }
        else
        {
            Vector fileVector=new Vector ();
            try
            {
                RandomAccessFile file = new RandomAccessFile (fileName, "r");
                String str = new String ();
                int noOfclasses=0;
                System.out.println ("\nReading file to train: "+fileName);
                System.out.println ("Please wait while file is loaded: ");
                while(file.getFilePointer () < file.length ())
                {
                    str = file.readLine ();
                    System.out.print ("I");
                    if(str.startsWith ("@"))
                    {
                        try
                        {
                            str=str.replace ('@', ' ');
                            StringTokenizer stoken=new StringTokenizer (str, " ");
                            prototypes_no=(int)Integer.valueOf (stoken.nextToken ()).intValue ();
                            rate_eta=(float)Float.valueOf (stoken.nextToken ()).floatValue ();
                            rate_alpha=(float)Float.valueOf (stoken.nextToken ()).floatValue ();
                            rate_alphadecay=(float)Float.valueOf (stoken.nextToken ()).floatValue ();
                            rate_neighborhood_annealing=(float)Float.valueOf (stoken.nextToken ()).floatValue ();
                            neighborhood_size=(float)Float.valueOf (stoken.nextToken ()).floatValue ();
                            ng_fader_start=(float)Float.valueOf (stoken.nextToken ()).floatValue ();
                            ng_fader_end=(float)Float.valueOf (stoken.nextToken ()).floatValue ();
                            randseed=(int)Integer.valueOf (stoken.nextToken ()).intValue ();
                        /*
                        System.out.println("prototypes_no="+prototypes_no+
                        " rate_eta="+rate_eta+
                        " rate_alpha="+rate_alpha+
                        " rate_alphadecay="+rate_alphadecay+
                        " rate_neighborhood_annealing="+rate_neighborhood_annealing+
                        " neighborhood_size="+neighborhood_size+
                        " ng_fader_start="+ng_fader_start+
                        " ng_fader_end="+ng_fader_end+
                        " randseed="+randseed);
                         */
                        }
                        catch(Exception e)
                        {
                            e.printStackTrace ();
                        }
                    }
                    else if(str.startsWith ("#"))
                    {
                        try
                        {
                            str=str.replace ('#', ' ');
                            StringTokenizer stoken=new StringTokenizer (str, " ");
                            int i=0;
                            lambdas= new double[stoken.countTokens ()];
                            //System.out.print("Lambdas Vector= ");
                            while(stoken.hasMoreTokens ())
                            {
                                lambdas[i]= (double)(Double.valueOf (stoken.nextToken ()).doubleValue ());
                                //System.out.print(" "+lambdas[i]);
                                i++;
                            }
                            //System.out.println();
                        }
                        catch(Exception e)
                        {
                            e.printStackTrace ();
                        }
                    }
                    else if(str!=null && str.length ()>5)
                    {
                        String values[]= str.split (" ");
                        if(noOfclasses<Integer.valueOf (values[values.length-1]).intValue ())
                        {
                            noOfclasses=Integer.valueOf (values[values.length-1]).intValue ();
                        }
                        fileVector.addElement (str);
                    }
                }
                file.close ();
                num_categories=noOfclasses+1;
                System.out.println ("\nNumber of classes= "+num_categories);
            }
            catch (Exception e)
            {
                e.printStackTrace ();
            }
            
            System.out.println ("Allocating memory for pattern Matrix: ");
            try
            {
                pattern_length=fileVector.size ();
                StringTokenizer st=new StringTokenizer (fileVector.firstElement ().toString (), " ");
                pattern_dimension=st.countTokens ()-1;
                String fileLine= "";
                
                double [][] pattern=new double[pattern_dimension+1][pattern_length];
                for(int x=0; 0<fileVector.size (); x++)
                {
                    fileLine= (String)fileVector.firstElement ();
                    fileVector.removeElementAt (0);
                    System.out.print ("I");
                    if(!fileLine.startsWith ("#") || !fileLine.startsWith ("@") )
                    {
                        String values[]=fileLine.split (" ");
                        
                        for(int j=0;j<values.length;j++)
                        {
                            pattern[j][x]= (double) Double.valueOf (values[j]).doubleValue ();
                            //System.out.print(" "+pattern[j][x]);
                        }
                        //System.out.println();
                    }
                }
                
                fileVector=null;
                
                System.out.println ("\nPattern Matrix[ "+(pattern_dimension+1)+" X "+pattern_length+" ]");
                
                int numberOfElementsPerClass[]=new int[num_categories];
                for(int i=0;i<pattern_length; i++)
                {
                    numberOfElementsPerClass[(int)pattern [pattern_dimension][i]]++;
                    //System.out.println(numberOfElementsPerClass[(int)pattern [pattern_dimension][i]]);
                }
                int largest=0;
                int temp=0;
                for(int i=0;i<numberOfElementsPerClass.length;i++)
                {
                    if(temp<numberOfElementsPerClass[i])
                    {
                        temp=numberOfElementsPerClass[i];
                        largest=i;
                    }
                }
                pattern_of_each_class=new double[num_categories][pattern_dimension][numberOfElementsPerClass[largest]];
                
                validNumberOfPatternPerClass=new int[num_categories];
                
                for(int i=0;i<numberOfElementsPerClass.length;i++)
                {
                    validNumberOfPatternPerClass[i]=numberOfElementsPerClass[i];
                }
                
                for(int i=0;i<validNumberOfPatternPerClass.length;i++)
                {
                    System.out.println ("Category= "+i+"  numberOfElements= "+validNumberOfPatternPerClass[i]);
                }
                System.out.println ("num_categories="+num_categories+" pattern_dimension="+pattern_dimension+" pattern_length="+pattern_length);
                System.out.println (largest+" <--largest");
                System.out.println ("3D Pattern Matrix["+ pattern_of_each_class.length+" X "+pattern_of_each_class[0].length+" X "+pattern_of_each_class[0][0].length+" ]");
                
                try
                {
                    for(int k=0 ; k<pattern_length ;k++)
                    {
                        int category=(int)pattern[pattern_dimension][k];
                        int i=numberOfElementsPerClass[category]-1;
                        if(i>=0)
                        {
                            for(int j=0; j<pattern_dimension; j++)
                            {
                                double value=pattern[j][k];
                                pattern_of_each_class[category][j][i]=value;
                                //System.out.print(" "+pattern_of_each_class[category][j][i]+" "+category);
                                
                            }
                            numberOfElementsPerClass[category]--;
                        }
                        //System.out.println(" ");
                    }
                    
                    if(lambdas!=null)
                    {
                        if(lambdas[0]<0. || lambdas[0]>1.)
                        {
                            lambdas= new double[pattern_dimension];
                            //System.out.print("Lambda= ");
                            for(int i=0;i<pattern_dimension;i++)
                            {
                                lambdas[i]=Math.sqrt (1.0/pattern_dimension);
                                //System.out.print(" "+lambdas[i]);
                            }
                            //System.out.println();
                        }
                    }
                    if(lambdas==null)
                    {
                        lambdas= new double[pattern_dimension];
                        //System.out.print("Lambda= ");
                        for(int i=0;i<pattern_dimension;i++)
                        {
                            lambdas[i]=Math.sqrt (1.0/pattern_dimension);
                            //System.out.print(" "+lambdas[i]);
                        }
                        //System.out.println();
                    }
                    
                }
                catch(Exception e)
                {
                    e.printStackTrace ();
                }
            }
            catch(Exception e)
            {
                e.printStackTrace ();
            }
        }
        
        System.out.println (" ----------------- ");
        
    }
    
    /** sets system variables for classify mode */
    public void setSystemVariablesToClassify ()
    {
        String str = new String ();
        if(classifyVectors!=null)
        {
            classifyVectors.removeAllElements ();
        }
        else
        {
            classifyVectors=new Vector ();
        }
        try
        {
            RandomAccessFile file = new RandomAccessFile (fileName, "r");
            System.out.println ("\nReading file to classify: "+fileName);
            System.out.println ("Please wait while file is loaded: ");
            
            while(file.getFilePointer () < file.length ())
            {
                str = file.readLine ();
                System.out.print ("I");
                if(!(str.startsWith ("#") || str.startsWith ("@") || str==null || str.length ()<6) )
                {
                    classifyVectors.addElement (str);
                    //System.out.println(str);
                }
            }
            file.close ();
            
        }
        catch (Exception e)
        {
            e.printStackTrace ();
        }
        
    }
    
    /** set system variables for test mode */
    public void setSystemVariablesToTest ()
    {
        String str = new String ();
        if(testVectors!=null)
        {
            testVectors.removeAllElements ();
        }
        else
        {
            testVectors=new Vector ();
        }
        try
        {
            RandomAccessFile file = new RandomAccessFile (fileName, "r");
            System.out.println ("\nReading file to test: "+fileName);
            System.out.println ("Please wait while file is loaded: ");
            
            while(file.getFilePointer () < file.length ())
            {
                str = file.readLine ();
                System.out.print ("I");
                
                if(!(str.startsWith ("#") || str.startsWith ("@") || str==null || str.length ()<6) )
                {
                    testVectors.addElement (str);
                    //System.out.println(str);
                }
            }
            file.close ();
            System.out.println ();
        }
        catch (Exception e)
        {
            e.printStackTrace ();
        }
    }
    
    /** sets system variables from classifier */
    public void setSystemVariablesForClassifier ()
    {
        if(prototypePropertiesList!=null)
        {
            prototypePropertiesList.removeAllElements ();
        }
        else
        {
            prototypePropertiesList=new Vector ();
        }
        try
        {
            RandomAccessFile file = new RandomAccessFile (fileName, "r");
            while(file.getFilePointer () < file.length ())
            {
                prototypePropertiesList.addElement (file.readLine ());
                
            }
            file.close ();
            
        }
        catch (Exception e)
        {
            e.printStackTrace ();
        }
    }
    /** saves file
     * @param str file name
     */
    public void doSave (String str)
    {
        FileOutputStream fos = null;
        try
        {
            fos = new FileOutputStream (fileName);
            fos.write (str.getBytes ());
        }
        catch (IOException e)
        {
            e.printStackTrace ();
        }
        finally
        {
            try
            {
                fos.close ();
            }
            catch (IOException e2)
            {
                e2.printStackTrace ();
            }
        }
    }
    /** gets pattern dimension
     * @return dimension
     */
    public int getPatternDimention ()
    {
        return this.pattern_dimension;
    }
    /** gets pattern length
     * @return length
     */
    public int getPatternLength ()
    {
        return this.pattern_length;
    }
    /** gets the relevance lambda vector
     * @return lambda vector
     */
    public double [] getLambdas ()
    {
        return lambdas;
    }
    /** gets number of prototypes( this would be used for future versions)
     * @return number of prototypes
     */
    public int get_prototypes_no ()
    {
        return prototypes_no;
    }
    
    /** gets rate of eta( this would be used for future versions)
     * @return eta rate
     */
    public float get_rate_eta ()
    {
        return rate_eta;
    }
    /** gets rate of alpha( this would be used for future versions)
     * @return rate alpha
     */
    public float get_rate_alpha ()
    {
        return rate_alpha;
    }
    /** get alpha decay rate( this would be used for future versions)
     * @return alpha decay
     */
    public float get_rate_alphadecay ()
    {
        return rate_alphadecay;
    }
    /** gets rate of neighborhood annealing( this would be used for future versions)
     * @return neighborhood annealing
     */
    public float get_rate_neighborhood_annealing ()
    {
        return rate_neighborhood_annealing;
    }
    /** gets neighborhood size( this would be used for future versions)
     * @return neighborhood size
     */
    public float get_neighborhood_size ()
    {
        return neighborhood_size;
    }
    /** gets neural gas fader start( this would be used for future versions)
     * @return fader
     */
    public float get_ng_fader_start ()
    {
        return ng_fader_start;
    }
    /** gets neural gas fader end( this would be used for future versions)
     * @return fader
     */
    public float get_ng_fader_end ()
    {
        return ng_fader_end;
    }
    /** gets random seed
     * @return seed
     */
    public int get_randseed ()
    {
        return randseed;
    }
    
    /** gets 3D pattern matrix
     * @return 3D matrix
     */
    public double[][][] get3DPattern ()
    {
        return pattern_of_each_class;
    }
    /** gets list of properties of prototypes
     * @return properties
     */
    public Vector getPrototypePropertiesList ()
    {
        return prototypePropertiesList;
    }
    /** gets test pattern
     * @return test vectors
     */
    public Vector getTestVectors ()
    {
        return testVectors;
    }
    /** get vector to classify
     * @return classify vectors
     */
    public Vector getClassifyVectors ()
    {
        return classifyVectors;
    }
    /** gets number of categories
     * @return categories
     */
    public int getNumberOfCategories ()
    {
        return num_categories;
    }
    /** gets valid number of pattern per class
     * @return valid number of pattern per class
     */
    public int [] getValidNumberOfPatternPerClass ()
    {
        return this.validNumberOfPatternPerClass;
    }
}
