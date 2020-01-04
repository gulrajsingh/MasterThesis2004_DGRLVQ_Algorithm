package de.uos.dgrlvq;
import java.applet.*;
import java.awt.*;
import javax.swing.*;
import java.util.*;

/** This class is visualization Engine.
 *
 * With the help of this class the insight of overall assigned DGLVQ classifier can be achived.
 *
 * The object of this class can draw a scatter plot between any two dimentions of
 * pattern and any coordinate space.
 * @author Gulraj Singh Mrok, University Osnabrück, Germany
 */
public class Painting extends java.awt.Canvas implements Runnable
{
    /** output value from each prototype */
    protected double[] outputs = null;
    /** weight vector of each prototype */
    protected double[][] weights = null;
    /** lambda vector of each prototype */
    protected double[] lambda = null;
    /** category vector of each vector */
    protected int[] categories = null;
    /** thread reference of this Engine. */
    protected Thread runner = null;
    /** the grid dimention */
    protected Dimension grid_size = new Dimension (40, 40);
    /** number of sites */
    protected int NUM_SITES;
    /** storage width */
    protected int STORAGEWIDTH;
    /** site x */
    protected double sx[];
    /** site y */
    protected double sy[];
    /** edge x */
    protected double ex[];
    /** edge y */
    protected double ey[];
    /** simple pi value */
    protected double PI = Math.PI;
    /** pi*pi */
    protected double PI2 = 2 * PI;
    /** paint background flag */
    protected boolean paint_background = true;
    /** paint voronoi background flag */
    protected boolean paint_voronoi_background = false;
    /** paint grid flag */
    protected boolean paint_grid = false;
    /** paint voronoi flag */
    protected boolean paint_voronoi = false;
    /** paint prototype flag */
    protected boolean paint_prototypes = true;
    /** paint input vectors flag */
    protected boolean paint_input = true;
    /** paint border flag */
    protected boolean paint_border = true;
    /** paint lambda flag */
    protected boolean paint_lambda = true;
    /** dark red color */
    protected Color darkRed = new Color (127, 31, 31);
    /** dark green color */
    protected Color darkGreen = new Color (31, 127, 31);
    /** dark blue color */
    protected Color darkBlue = new Color (31, 31, 127);
    /** dark yellow color */
    protected Color darkYellow = new Color (127, 127, 0);
    /** light red color */
    protected Color lightRed = new Color (255, 159, 159);
    /** light green color */
    protected Color lightGreen = new Color (159, 255, 159);
    /** light blue color */
    protected Color lightBlue = new Color (159, 159, 255);
    /** light yellow color */
    protected Color lightYellow = new Color (255, 255, 63);
    /** color vector for different categories */
    protected Color[] category_colors = new Color[]
    {darkBlue, darkRed, darkGreen, darkYellow,lightBlue, lightRed, lightGreen, lightYellow, Color.blue, Color.red, Color.green, Color.yellow, Color.black,Color.cyan,Color.darkGray,Color.gray,Color.lightGray,Color.magenta,Color.orange,Color.pink};
    /** foreground color */
    protected Color[] category_fgcolors = new Color[]
    {darkBlue, darkRed, darkGreen, darkYellow,lightBlue, lightRed, lightGreen, lightYellow, Color.blue, Color.red, Color.green, Color.yellow, Color.black,Color.cyan,Color.darkGray,Color.gray,Color.lightGray,Color.magenta,Color.orange,Color.pink};
    /** background color */
    protected Color[] category_bgcolors = new Color[]
    {Color.pink,darkBlue, darkRed, Color.orange,darkGreen, darkYellow,lightBlue, lightRed, lightGreen, lightYellow, Color.blue, Color.red, Color.green, Color.yellow, Color.black,Color.cyan,Color.darkGray,Color.gray,Color.lightGray,Color.magenta,};
    /** Symbols for input points*/
    protected char inputPoint[]=
    {'x','o','+','*','-','<','#','!','§','$','%','&','~','.'};
    /** width of plot */
    protected int width=300;
    /** height of plot */
    protected int height=200;
    /** grid factor for grid ploting */
    protected int grid_factor=10;
    /** dimention from pattern to be used to plot as x axis */
    protected int xAxis=0;
    /** dimention from pattern to be used to plot as y axis */
    protected int yAxis=1;
    /** 3D pattern array.
     * D1= total number of categories.
     * D2= dimention of pattern.
     * D3= length of pattern.
     */
    protected double pattern_of_each_class[][][];
    /** valid number of pattern per class */
    protected int [] validNumberOfPatternPerClass;
    /** transformation factor of plot */
    protected int transformation=1;
    /** the allocated classifier for which plot has to been drawn. */
    protected DGRLVQClassifier classifier;
    /** refresh rate of plot */
    protected int delay=100;
    /** dimention of pattern */
    protected int pattern_dimension=2;
    /** length of pattern */
    protected int pattern_length=2;
    /** to draw plot or not */
    protected boolean updatingPainting=true;
    /** the coordinate region to draw plot in. */
    protected int [] coordinateRegion=
    { 1, 1};
    
    /** creates new object with default values */
    public Painting ()
    {
    }
    /** creates new object
     * @param clfr classifier for which plot has to been drawn.
     * @param patternOfEachClass 3D pattern
     * @param vnoppc valid number of patterns per class
     * @param pd pattern dimention
     * @param pl pattern length
     * @param w width of plot
     * @param h height of plot
     * @param gf grid factor
     * @param x x axis
     * @param y y axis
     */
    public Painting (DGRLVQClassifier clfr, double [][][] patternOfEachClass,int [] vnoppc, int pd, int pl, int w, int h, int gf, int x, int y)
    {
        this.classifier=clfr;
        this.pattern_of_each_class=patternOfEachClass;
        this.validNumberOfPatternPerClass=vnoppc;
        this.pattern_dimension=pd;
        this.pattern_length= pl;
        this.width=w;
        this.height=h;
        this.grid_factor=gf;
        this.xAxis=x;
        this.yAxis=y;
        initGrid ();
        setBackground (Color.WHITE);
    }
    
    /** init grid for plot */
    public void initGrid ()
    {
        grid_size.width = width / grid_factor;
        grid_size.height = height / grid_factor;
    }
    /** creates thread, assign proirity to normal and runs the thread */
    public void start ()
    {
        runner = new Thread (this);
        runner.setPriority (Thread.NORM_PRIORITY);
        runner.start ();
    }
    /** plots the plot till updatingPainting=true with time delay in milliseconds. */
    public void run ()
    {
        while(updatingPainting)
        {
            try
            {
                Thread.currentThread ().sleep (delay);
            }
            catch(InterruptedException e)
            {
                e.printStackTrace ();
            }
            
            this.repaint (0,0, width,height+500);
            //this.repaint (0,0, 500,600);
            
        }
    }
    
    /** stops the thread and plot */
    public void stop ()
    {
        updatingPainting=false;
        runner.stop ();
        runner = null;
    }
    
    /** update the plot
     * @param g graphics
     */
    public void update (Graphics g)
    {
        paint (g);
    }
    
    /** paint the plot
     * @param g graphics
     */
    public void paint (Graphics g)
    {
        try
        {
            if(classifier!=null)
            {
                outputs=classifier.getOutputs (outputs);
                weights=classifier.getWeights (weights);
                categories=classifier.getCategories (categories);
                lambda=classifier.getLambda (lambda);
                calculateVoronoi (weights);
                if (paint_background) paintBackground (g);
                if (paint_voronoi_background) paintVoronoiBackground (g);
                if (paint_grid) paintGrid (g);
                if (paint_voronoi) paintVoronoi (g);
                if (paint_border) paintBorder (g);
                if (paint_input) paintInput (g);
                if (paint_prototypes) paintPrototypes (g);
                if (paint_lambda) paintLambda (g);
            }
            else
            {
                System.out.println ("No Classifer set. Classifier= "+classifier);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace ();
        }
    }
    
    /** paints background on graphics of plot
     * @param g graphics
     */
    protected void paintBackground (Graphics g)
    {
        g.clearRect (0, 0, width-1, height-1);
    }
    
    /** paints voronoi background on graphics of plot
     * @param g graphics
     */
    protected void paintVoronoiBackground (Graphics g)
    {
        for (int i = 0; i < weights.length; i++)
        {
            fillVoronoiCell (g, i, category_bgcolors[categories[i]%category_bgcolors.length]);
        }
    }
    
    /** fills voronoi cell with color of category
     * @param g graphics
     * @param cell cell
     * @param c category
     */
    protected void fillVoronoiCell (Graphics g, int cell, Color c)
    {
        int i, j, k, n;
        double a, x, y;
        int indices[] = new int[STORAGEWIDTH];
        double angles[] = new double[STORAGEWIDTH];
        Polygon p;
        
        if (cell < 0) return;
        
        p = new Polygon ();
        x = (coordinateRegion[0]*weights[cell][xAxis])/transformation;
        y = (coordinateRegion[1]*weights[cell][yAxis])/transformation;
        
        angles[0] = 100;
        
        for (i = 0; i < NUM_SITES + 4; i++)
        {
            if (i != cell)
            {
                if (i > cell)
                    n = cell * STORAGEWIDTH + i;
                else
                    n = i * STORAGEWIDTH + cell;
                
                if (sx[n] > -Double.MAX_VALUE)
                {
                    a = getAngle ((sx[n]+ex[n])/2 - x, (sy[n]+ey[n])/2 - y);
                    
                    for (j = 0; j < STORAGEWIDTH; j++)
                    {
                        if (angles[j] > a)
                        {
                            for (k = NUM_SITES + 4 - 1; k > j; k--)
                            {
                                angles[k] = angles[k-1];
                                indices[k] = indices[k-1];
                            }
                            
                            angles[j] = a;
                            indices[j] = i;
                            break;
                        }
                    }
                }
                n++;
            }
        }
        
        for (i = 0; i < STORAGEWIDTH; i++)
        {
            if (angles[i] == 100) break;
            
            j = indices[i];
            
            if (cell < j)
                n = cell * STORAGEWIDTH + j;
            else
                n = j * STORAGEWIDTH + cell;
            
            if (angleDifference (getAngle (sx[n]-x, sy[n]-y), getAngle (ex[n]- x, ey[n]-y)) < 0)
            {
                p.addPoint ((int)(ex[n]*(width-1)), (int)(ey[n]*(height-1)));
            }
            else
            {
                p.addPoint ((int)(sx[n]*(width-1)), (int)(sy[n]*(height-1)));
            }
        }
        
        g.setColor (c);
        g.fillPolygon (p);
        
        // System.err.println("Polygon p == " + toString(p));
    }
    
    /** calculates angle
     * @param vx vertex x
     * @param vy vertex y
     * @return angle
     */
    protected double getAngle (double vx, double vy)
    {
        double a;
        
        if (vy == 0)
            if (vx < 0)
                return PI;
            else
                return 0;
        
        a = Math.acos (vx/Math.sqrt (vx*vx + vy*vy));
        
        if (vy > 0)
            return a;
        else
            return (PI2-a);
    }
    
    /** calculate angle difference using PI value
     * @param t cord 1
     * @param s cord2
     * @return difference
     */
    protected double angleDifference (double t, double s)
    {
        double a;
        
        a = t - s;
        
        if(a > PI)
            return(a - PI2);
        if(a < -PI)
            return(a + PI2);
        
        return a;
    }
    
    /** translates the voronoi cell to string
     * @param p voronoi polygon
     * @return String values
     */
    protected String toString (Polygon p)
    {
        StringBuffer buffer = new StringBuffer ();
        buffer.append ("[");
        for (int i = 0; i < p.npoints; i++)
        {
            buffer.append ("(");
            buffer.append (p.xpoints[i]);
            buffer.append (",");
            buffer.append (p.ypoints[i]);
            buffer.append (")");
            if (i < p.npoints-1) buffer.append (" ");
        }
        buffer.append ("]");
        
        return buffer.toString ();
    }
    
    /** paints grid on graphics of plot
     * @param g graphics
     */
    protected void paintGrid (Graphics g)
    {
        g.setColor (Color.lightGray);
        for (int i = 0; i < width; i += grid_size.width)
            g.drawLine (i, 0, i, height-1);
        for (int j = 0; j < height; j += grid_size.height)
            g.drawLine (0, j, width, j);
    }
        
    /** paints voronoi on graphics of plot
     * @param g graphics
     */
    protected void paintVoronoi (Graphics g)
    {
        if (sx == null) return;
        
        g.setColor (Color.gray);
        
        int n;
        for (int i = 0; i < NUM_SITES; i++)
        {
            n = i*STORAGEWIDTH + i + 1;
            for(int j = i+1; j < NUM_SITES+4; j++)
            {
                if (sx[n] > -Double.MAX_VALUE)
                    g.drawLine ((int)(sx[n]*(width-1))/transformation, (int)(sy[n]*(height-1))/transformation, (int)(ex[n]*(width-1))/transformation, (int)(ey[n]*(height-1))/transformation);
                n++;
            }
        }
    }
    
    /** paints prototypes on graphics of plot
     * @param g graphics
     */
    protected void paintPrototypes (Graphics g)
    {
        //System.out.println ("weights.length=="+weights.length);
        int[] x = new int[weights.length];
        int[] y = new int[weights.length];
        
        for (int i = 0; i < weights.length; i++)
        {
            x[i] = (int)(coordinateRegion[0]*weights[i][xAxis] * (width-1));
            y[i] = (int)(coordinateRegion[1]*weights[i][yAxis] * (height-1));
        }
        
        int n;
        for (int i = 0; i < weights.length; i++)
        {
            n = 3;
            for (int j = 0; j < weights.length; j++)
                if (x[i] == x[j] && y[i] == y[j]) n += 3;
            
            g.setColor (category_colors[categories[i]%category_colors.length]);
            g.fillOval ((x[i]/transformation-n), (y[i]/transformation-n), (2*n+1), (2*n+1));
            
            g.setColor (Color.black);
            g.fillOval (x[i]/transformation-1, y[i]/transformation-1, 3, 3);
            g.fillOval (x[i]/transformation-4, y[i]/transformation-4, 3, 3);
            g.fillOval (x[i]/transformation+2, y[i]/transformation-4, 3, 3);
            g.fillArc (x[i]/transformation-2, y[i]/transformation+2, 4, 2, 180, 180);
            
            
            if (outputs[i] == 1.0)
            {
                g.setColor (Color.WHITE);
                g.fillOval (x[i]/transformation-1, y[i]/transformation-1, 3, 3);
                g.fillOval (x[i]/transformation-1, y[i]/transformation-1, 3, 3);
                g.fillOval (x[i]/transformation-4, y[i]/transformation-4, 3, 3);
                g.fillOval (x[i]/transformation+2, y[i]/transformation-4, 3, 3);
                g.fillArc (x[i]/transformation-3, y[i]/transformation-1, 7, 7, 180, 180);
                
            }
            //g.fill3DRect(x[i]-n, y[i]-n, 2*n+1, 2*n+1,true);
        }
    }
    
    /** paints all input vectors on graphics of plot
     * @param g graphics
     */
    protected void paintInput (Graphics g)
    {
        //System.out.println("in print inputs");
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
                //System.out.print("# "+input[0]+"  "+ input[1]);
                int x1 = (int)(coordinateRegion[0]*inputs[xAxis]*(width-1));
                int y1 = (int)(coordinateRegion[1]*inputs[yAxis]*(height-1));
                //System.out.println ("> "+x1+"  "+ y1);
                g.setColor (category_fgcolors[i%category_fgcolors.length]);
                //g.drawLine(x1-2, y1-2, x1+2, y1+2);
                //g.drawLine(x1-2, y1+2, x1+2, y1-2);
                //g.fill3DRect (x1/transformation, y1/transformation, 2, 2, true);
                g.drawString (String.valueOf (inputPoint[i%inputPoint.length]),x1/transformation, y1/transformation);
                
                //g.drawLine(x1-2, y1+2, x1+2, y1-2);
                
            }
            //System.out.println ("------------------------");
        }
        
    }
    /** paints lambda plot
     * @param g graphics
     */
    protected void paintLambda (Graphics g)
    {
        
        double w=0.,h=0.,x1=0.,y1=0.;
        for(int i=0; i<lambda.length; i++)
        {
            g.setColor (category_fgcolors[i%category_fgcolors.length]);
            x1+=w;
            y1 = height-1;
            w=width/lambda.length;
            h=lambda[i]*lambda.length*100;
            g.fillRect ((int)x1, (int)y1,(int)w,(int)h);
            g.setColor (Color.BLACK);
            //g.drawString (String.valueOf (i),(int)x1,(int)y1);
            
            //System.out.println (lambda[i]*lambda.length*100+"="+lambda[i]+" * "+lambda.length+" * 100");
            //System.out.println ((int)x1+" "+(int)y1+" "+(int)w+" "+(int)h);
            //System.out.println ();
        }
        
    }
    /** paints border on graphics of plot
     * @param g graphics
     */
    protected void paintBorder (Graphics g)
    {
        g.setColor (Color.lightGray);
        g.drawRect (0, 0, width-1, height-1);
    }
    
    /** calcultes voronoi on sites
     * @param sites sites vector
     */
    public void calculateVoronoi (double[][] sites)
    {
        if (sites == null) return;
        if (sites.length == 0) return;
        
        NUM_SITES = sites.length;
        STORAGEWIDTH = NUM_SITES + 4;
        sx = new double[NUM_SITES * STORAGEWIDTH];
        sy = new double[NUM_SITES * STORAGEWIDTH];
        ex = new double[NUM_SITES * STORAGEWIDTH];
        ey = new double[NUM_SITES * STORAGEWIDTH];
        
        int i, j, k, m, n;
        double a, b, a0, b0, a1, b1, x, y, x0, y0, x1, y1;
        
        for (i = 0; i < NUM_SITES; i++)
        {
            //System.out.println(coordinateRegion[0]+" "+coordinateRegion[1]);
            x0 = (coordinateRegion[0]*sites[i][xAxis])/transformation;
            //System.out.println(x0+" ");
            y0 = (coordinateRegion[1]*sites[i][yAxis])/transformation;
            //System.out.println(y0+" ");
            n = i * STORAGEWIDTH + i + 1;
            
            for (j = i + 1; j < NUM_SITES; j++)
            {
                x1 = (coordinateRegion[0]*sites[j][xAxis])/transformation;
                y1 = (coordinateRegion[1]*sites[j][yAxis])/transformation;
                
                if (x1 == x0) a = 0;
                else if (y1 == y0) a = 10000;
                else a = -1/((y1-y0)/(x1-x0));
                b = (y0+y1)/2 - a*(x0+x1)/2;
                
                if (a > -1 && a <= 1)
                {
                    sx[n] = 0.0;
                    sy[n] = a * sx[n] + b;
                    ex[n] = 1.0;
                    ey[n] = a * ex[n] + b;
                }
                else
                {
                    sy[n] = 0.0;
                    sx[n] = ( sy[n] - b) / a;
                    ey[n] = 1.0;
                    ex[n] = ( ey[n] - b) / a;
                }
                n++;
            }
            sx[n] = 0.0; sy[n] = 0.0; ex[n] = 1.0; ey[n] = 0.0;
            n++;
            sx[n] = 0.0; sy[n] = 0.0; ex[n] = 0.0; ey[n] = 1.0;
            n++;
            sx[n] = 1.0; sy[n] = 0.0; ex[n] = 1.0; ey[n] = 1.0;
            n++;
            sx[n] = 0.0; sy[n] = 1.0; ex[n] = 1.0; ey[n] = 1.0;
        }
        
        for (i = 0; i < NUM_SITES; i++)
        {
            x0 = (coordinateRegion[0]*sites[i][xAxis])/transformation;
            y0 = (coordinateRegion[1]*sites[i][yAxis])/transformation;
            
            for (j = 0; j < NUM_SITES + 4; j++)
            {
                if (j != i)
                {
                    if (j > i)
                        n = i*STORAGEWIDTH + j;
                    else
                        n = j*STORAGEWIDTH + i;
                    
                    if (sx[n] > -Double.MAX_VALUE)
                    {
                        a0 = (ey[n]-sy[n])/(ex[n]-sx[n]);
                        b0 = sy[n] - a0*sx[n];
                        
                        for (k = i + 1; k < NUM_SITES + 4; k++)
                        {
                            if (k != j)
                            {
                                m = i*STORAGEWIDTH + k;
                                if (sx[m] > -Double.MAX_VALUE)
                                {
                                    a1 = (ey[m]-sy[m])/(ex[m]-sx[m]);
                                    b1 = sy[m] - a1*sx[m];
                                    
                                    x = -(b1-b0)/(a1-a0);
                                    y = a0*x + b0;
                                    
                                    if ((a0*x0 + b0-y0)*(a0*sx[m] + b0-sy[m]) < 0)
                                    {
                                        sx[m] = x;
                                        sy[m] = y;
                                    }
                                    
                                    if ((a0*x0 + b0-y0)*(a0*ex[m] + b0-ey[m]) < 0)
                                    {
                                        if( sx[m] == x)
                                        {
                                            sx[m] = -Double.MAX_VALUE;
                                        }
                                        else
                                        {
                                            ex[m] = x;
                                            ey[m] = y;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    /** gets width of plot
     * @return width
     */
    public int getWidth ()
    {
        return this.width;
    }
    
    /** gets the coordinate region values
     * @return coordinates
     */
    public int [] getCoordinateRegion ()
    {
        return coordinateRegion;
    }
    
    /** gets ploted y axis
     * @return y axis
     */
    public int getYAxis ()
    {
        return this.yAxis;
    }
    /** gets get ploted x axis
     * @return y axis
     */
    public int getXAxis ()
    {
        return this.xAxis;
    }
    /** gets pattern dimention
     * @return dimention
     */
    public int getPatternDimension ()
    {
        return this.pattern_dimension;
    }
    /** get pattern length
     * @return length
     */
    public int getPatternLength ()
    {
        return this.pattern_length;
    }
    /** gets grid factor
     * @return grid factor
     */
    public int getGridFactor ()
    {
        return  this.grid_factor;
    }
    /** gets assigned classifer
     * @return classifier
     */
    public DGRLVQClassifier getClassifier ()
    {
        return this.classifier;
    }
    /** gets delay
     * @return delay
     */
    public int getDelay ()
    {
        return delay;
    }
    /** gets transforation factor
     * @return transforamtion
     */
    public int getTransformation ()
    {
        return this.transformation;
    }
    /** sets transformation factor
     * @param trans
     */
    public void setTransformation (int trans)
    {
        this.transformation=trans;
    }
    
    /** gets the 3D pattern
     * @return pattern
     */
    public double[][][] getPattern ()
    {
        return this.pattern_of_each_class;
    }
    /** sets ploting on or off
     * @param up true if ON
     */
    public void setUpdatPaintingON (boolean up)
    {
        this.updatingPainting=up;
    }
    /** set x and y axis to plot
     * @param x axis x
     * @param y axis y
     */
    public void setAxis (int x, int y)
    {
        this.xAxis=x;
        this.yAxis=y;
    }
    /** sets the dimension of pattern
     * @param pd pattern dimension
     */
    public void setPatternDimension (int pd)
    {
        this.pattern_dimension=pd;
    }
    /** sets the pattern length
     * @param pl pattern length
     */
    public void setPatternLength (int pl)
    {
        this.pattern_length=pl;
    }
    
    /** set grid factor
     * @param gf grid factor
     */
    public void setGridFactor (int gf)
    {
        this.grid_factor=gf;
    }
    /** assignes the classifier for whic plot has to be drawn
     * @param clfr classifier
     */
    public void setClassifier (DGRLVQClassifier clfr)
    {
        this.classifier=clfr;
    }
    /** set delay
     * @param milliseconds delay in milliseconds
     */
    public void setDelay (int milliseconds)
    {
        delay=milliseconds;
    }
    /** set 3D pattern
     * @param pattern pattern
     */
    public void setPattern (double[][][] pattern)
    {
        this.pattern_of_each_class=pattern;
    }
    
    /** set painting options ON=true or OFF=false
     * @param pb paint background
     * @param pvb paint voronoi background
     * @param pg paint grid
     * @param pv paint voronoi
     * @param pbo paint border
     * @param pi paint inputs
     * @param pp paint prototypes
     * @param pl paint lambda plot
     */
    public void setPaintingOptions (boolean pb,  boolean pvb, boolean pg, boolean pv, boolean pbo, boolean pi, boolean pp, boolean pl)
    {
        this.paint_background=pb;
        this.paint_voronoi_background=pvb;
        this.paint_grid=pg;
        this.paint_voronoi=pv;
        this.paint_border=pbo;
        this.paint_input=pi;
        this.paint_prototypes=pp;
        this.paint_lambda=pl;
    }
    /** set size of plot
     * @param w width
     * @param h height
     */
    public void setSize (int w,int h)
    {
        this.width=w;
        this.height=h;
    }
    /** set the coordinate in which plot has to be drawn
     * @param x x=1 in positive side x=-1 in negative
     * @param y y=1 in positive side y=-1 in negative
     */
    public void setCoordinateRegion (int x,int y)
    {
        if(x<0 && y<0)
        {
            coordinateRegion[0]=-1;
            coordinateRegion[1]=-1;
        }
        else if(x<0 && y>0)
        {
            coordinateRegion[0]=-1;
            coordinateRegion[1]=1;
        }
        else if(x>0 && y<0)
        {
            coordinateRegion[0]=1;
            coordinateRegion[1]=-1;
        }
        else
        {
            coordinateRegion[0]=1;
            coordinateRegion[1]=1;
        }
    }
    /** checks if plot drawing is ON
     * @return true if ON
     */
    public boolean isUpdatPaintingON ()
    {
        return this.updatingPainting;
    }
    
    /** checks if painting prototypes is ON
     * @return true if ON
     */
    public boolean isPrototypePaintingON ()
    {
        return this.paint_input;
    }
    /** checks if painting of input vectors is ON
     * @return true if ON
     */
    public boolean isInputPaintingON ()
    {
        return this.paint_input;
    }
    /** checks if painting of border is ON
     * @return true if ON
     */
    public boolean isBorderPaintingON ()
    {
        return this.paint_border;
    }
    /** checks id painting og voronoi is ON
     * @return true if ON
     */
    public boolean isVoronoiPaintingON ()
    {
        return this.paint_voronoi;
    }
    /** checks if painting grid is ON
     * @return true if ON
     */
    public boolean isGridPaintingON ()
    {
        return this.paint_grid;
    }
    /** checks if painting of voronoi background is ON
     * @return true if ON
     */
    public boolean isVoronoiBackgroundPaintingON ()
    {
        return this.paint_voronoi_background;
    }
    /** checks if painting of background is ON
     * @return true if ON
     */
    public boolean isBackgroundPaintingON ()
    {
        return this.paint_background;
    }
    
}
