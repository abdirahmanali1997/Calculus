import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.*;
import org.nfunk.jep.*;
import org.lsmp.djep.djep.*;
import org.lsmp.djep.xjep.*;

public class Graph extends JPanel implements MouseListener, MouseMotionListener, KeyListener{

	DerivativeList dList;
	
	DJep F;
	DJep G;
	DJep J;
	String variable = "x";

	CalculusApplet applet;

	double stat;
	double a;						// left endpoint
	double b;						// right/middle endpoint
	double c;						// right endpoint
	double d;						// right endpoint
	double[] coef;					// coefficients of Taylor polynomial

	boolean newA = false;			// has the value a changed via dragging?
	boolean newB = false;			// has the value b changed via dragging?
	boolean newC = false;			// has the value c changed via dragging?
	boolean newD = false;			// has the value d changed via dragging?
	boolean overA = false;			// is the mouse over the point A?
	boolean overB = false;			// is the mouse over the point B?
	boolean overC = false;			// is the mouse over the point C?
	boolean overD = false;			// is the mouse over the point D?
	boolean newStat = true;			// does stat need to be recomputed?
	boolean newBackground = true;   // if true, redraws the background image
	boolean shift = false;			// used by volumes applet, for rotating solids and by sequences
	boolean control = false;		// 
	boolean showGrid = true;		// draw grid lines or not, currently this is always true
	boolean radians = false;		// label axes with multiples of pi

	static boolean active = false;  // used in animations
	static double time;				// used in animation and/or drawing portions of washers

	int n;							// degree of Taylor poly, number of subintervals, y-scale in Sequence, number of data points
	int m;							// x-scale in Sequence

	double w;						// width of panel
	double h;						// height of panel
	double W;						// x-coordinate of y-axis (in terms of pixels)
	double H;						// y-coordinate of x-axis
	double originX = 0.0;			// x-coordinate of center of panel (in terms of units on axis, not pixels)
	double originY = 0.0;			// y-coordinate of center of panel
	double Ra = 60.0;				// used by Polar Graphs
	double Rb = 60.0;				// used by Polar Graphs
	double r = 4.0;					// radius of points
	double rr = 64.0;				// used to determine if mouse is over a point (square of distance from center)
	double pixels = 80.0;			// represents number of pixels per unit on graph
	double scale;					// pixels per unit on screen
	double Z = 20;					// distance of viewer from plane of screen, for perspective purposes
	int zoom = 12;
	double[] units = {0.0001, 0.0002, 0.0005, 0.001, 0.002, 0.005, 0.01, 0.02, 0.05, 0.1, 0.2, 0.5, 1.0, 2.0, 5.0, 10.0, 20.0, 50.0, 100.0, 200.0, 500.0, 1000.0};
	double[][] radianunits = {{1,18000}, {1,12000}, {1,6000}, {1,1800}, {1,1200}, {1,600}, {1,180}, {1,120}, {1,60}, {1,36}, {1,12}, {1,6}, {1,3}, {1,2}, {1,1}, {3,1}, {6,1}, {12,1}, {36,1}, {60,1}, {120,1}, {360,1}};
	
	Cursor hand =  new Cursor( Cursor.HAND_CURSOR );
	Cursor arrow =  new Cursor( Cursor.DEFAULT_CURSOR );
	
	String xlabel = "";
	String ylabel = "";

	Color red = new Color( 255, 0, 0, 175 );
	//Color yellow = new Color( 0, 200, 200, 175 );
	Color yellow = new Color( 0, 0, 255, 175 );
	Color blue = new Color( 0, 0, 255, 175 );
	Color colorA = Color.black;
	Color colorB = Color.black;
	
	float[] dash = {10.0f,5.0f};						//used for drawing dashed lines

	BasicStroke hairline = new BasicStroke( 0.25f );	// edges of 3D cross-sections
	BasicStroke gridline = new BasicStroke( 0.25f );	// gridlines
	BasicStroke endline = new BasicStroke( 1.0f );		// verticals lines at a,b,etc and slope fields
	BasicStroke boldline = new BasicStroke( 2.0f );		// verticals lines at a,b,etc and slope fields
	BasicStroke axes = new BasicStroke( 1.0f );			// axes
	BasicStroke curve = new BasicStroke( 3.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND );		// functions
	BasicStroke dashed = new BasicStroke( 2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 10.0f, dash, 0.0f );

	Font bold = new Font("Helvetica",Font.BOLD,18);

	public Graph(){
		setBackground( Color.white );

		setBorder( BorderFactory.createEtchedBorder() );

		F = new DJep();
		F.addStandardFunctions();
		F.addStandardConstants();
		F.setImplicitMul( true );
		F.setAllowUndeclared(true);
		F.setAllowAssignment(true);
		F.addVariable(variable, 0 );		
		F.parseExpression( "1" );
		F.addStandardDiffRules();

		G = new DJep();
		G.addStandardFunctions();
		G.addStandardConstants();
		G.setImplicitMul( true );
		G.setAllowUndeclared(true);
		G.setAllowAssignment(true);
		G.addVariable( variable, 0 );		
		G.parseExpression( "1" );
		G.addStandardDiffRules();

		J = new DJep();
		J.addStandardFunctions();
		J.addStandardConstants();
		J.setImplicitMul( true );
		J.setAllowUndeclared(true);
		J.setAllowAssignment(true);
		J.addVariable( variable, 0 );		
		J.parseExpression( "1" );
		J.addStandardDiffRules();

		addKeyListener( this );
		addMouseListener( this );
		addMouseMotionListener( this );
	}
	
	
	public void setZoom( int zoom ){
		this.zoom = zoom;
		Z = 20*units[zoom];
		newBackground = true;
		repaint();
	}
	
	
	public void zoomIn(){
		zoom = Math.max( zoom-1, 0 );
		Z = 20*units[zoom];
		scale = pixels/units[zoom];
		newBackground = true;
	}


	public void zoomOut(){
		zoom = Math.min( zoom+1, units.length-1 );
		Z = 20*units[zoom];
		scale = pixels/units[zoom];
		newBackground = true;
	}


    Image backImage;
	Graphics2D backGraphics;
	
	/* overwritten by:
	 CompoundInterest, LimitSequences, Sequences, Graph3D
	 */
	public void paintComponent( Graphics graphics ){
		w = getWidth();
		h = getHeight();

		Graphics2D g = (Graphics2D)graphics;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		
		if (backImage==null || backImage.getWidth(this) != w || backImage.getHeight(this) != h){
			backImage = this.createImage( (int)w, (int)h );
			backGraphics = (Graphics2D)(backImage.getGraphics());
			backGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
			newBackground = true;
		}

		scale = pixels/units[zoom];

		// draw graph of function f
		if ( newBackground || active ){			
			backGraphics.setColor( getBackground() );
			backGraphics.fillRect( 0, 0, (int)w, (int)h );
			if ( showGrid ) drawGridLines( backGraphics );
			drawAxes( backGraphics );
			drawFunction( backGraphics );
			newBackground = false;
		}
		g.drawImage( backImage, 0, 0, this );
		
		draw( g );	
		drawEndpoints( g );
		drawCrosshair( g );
/*
g.setColor( Color.black );
for ( int j=0; j<50; j++ ){
	g.drawString( "" + (char)(50*(n-1) + j ), 20*j, 20*1 );
}
*/
	}
	
	
	// should be overwritten by EVERY extension of Graph
	public void draw( Graphics2D g ){
	}
	
	
	public void drawArrow( Graphics2D g, double x, double y, Color color, boolean over ){
		drawArrow( g, 0, 0, x, y, color, over );
	}
	
	
	// draws a vector from (x,y) to (w,z) in Cartesian Coordinates
	public void drawArrow( Graphics2D g, double x, double y, double w, double z, Color color, boolean over ){
		GeneralPath path = new GeneralPath();
		double t = Math.atan((z-y)/(w-x));
		if ( w == x ){
			t = Math.PI/2;
			if ( z < y ) t = -Math.PI/2;
		} else if ( w < x ){
			t += Math.PI;
		}
		
		double[] p = toScreenPoint( w, z );
		double[] q = toScreenPoint( x, y );
		//P[0] += Rb*Math.cos(t);
		//P[1] -= Rb*Math.sin(t);
		double A = 6;
		double B = 12;
		path.moveTo( (float)(p[0] - A*Math.sin( t + Math.PI/4 )), (float)(p[1] - A*Math.cos( t + Math.PI/4 )) );
		path.lineTo( (float)(p[0] + B*Math.cos( t )), (float)(p[1] - B*Math.sin( t )) );
		path.lineTo( (float)(p[0] - A*Math.cos( t + Math.PI/4 )), (float)(p[1] + A*Math.sin( t + Math.PI/4 )) );
		path.quadTo( (float)p[0], (float)p[1], 
					(float)(p[0] - A*Math.sin( t + Math.PI/4 )), (float)(p[1] - A*Math.cos( t + Math.PI/4 )) );
		g.setStroke( endline );
		g.setColor( color );
		g.fill( path );
		g.setColor( Color.black );
		g.draw( path );
		
		if ( over ){
			A = 8;
			B = 16;
			path = new GeneralPath();
			path.moveTo( (float)(p[0] - A*Math.sin( t + Math.PI/4 )), (float)(p[1] - A*Math.cos( t + Math.PI/4 )) );
			path.lineTo( (float)(p[0] + B*Math.cos( t )), (float)(p[1] - B*Math.sin( t )) );
			path.lineTo( (float)(p[0] - A*Math.cos( t + Math.PI/4 )), (float)(p[1] + A*Math.sin( t + Math.PI/4 )) );
			path.quadTo( (float)p[0], (float)p[1], 
						(float)(p[0] - A*Math.sin( t + Math.PI/4 )), (float)(p[1] - A*Math.cos( t + Math.PI/4 )) );
			g.setStroke( new BasicStroke(1.5f) );
			g.setColor( Color.black );
			g.draw( path );
		}
	}
	
	
	/* overwritten by:
	 LimitSequences, Sequences
	 */
	public void drawAxes( Graphics2D g ){
		g.setColor( Color.black );
		g.setStroke( axes );

		double[] P = toScreenPoint( 0, 0 );
		W = P[0];
		if ( P[0] < 10 ) W = 10;
		else if ( P[0] > w - 10 ) W = w - 10;

		H = P[1];
		if ( P[1] < 10 ) H = 10;
		else if ( P[1] > h - 20 ) H = h - 20;

		// draw y-axis
		g.draw( new Line2D.Double(W,0,W,h) );
		
		// draw x-axis
		g.draw( new Line2D.Double(0,H,w,H) );

		// label y-axis
		float ww = (float)(W+4);
		String str;
		for ( int A = (int)((originY-h/2/scale)/units[zoom])-1; A < (originY+h/2/scale)/units[zoom]+1; A++){
			g.draw( new Line2D.Double( W-2,h/2 - (A*units[zoom]-originY)*scale,W+2,h/2 - (A*units[zoom]-originY)*scale ) );
			if ( A != 0 ){
				if ( (int)(A*units[zoom]) == A*units[zoom] ) str = "" + (int)(A*units[zoom]);
				else str = "" + (float)(A*units[zoom]);
				//str = "" + (float)(A*units[zoom]);
				if ( W != 10 ) ww = Math.max( 14, (float)(W-3) - g.getFontMetrics().stringWidth(str) );
				g.drawString( str, ww, (float)(h/2 - (A*units[zoom]-originY)*scale) + g.getFontMetrics().getHeight()/3 );
			}
		}
		ww = (float)(W+4);
		if ( W == w - 1 ) ww = (float)(W-3) - g.getFontMetrics().stringWidth(ylabel);
		g.drawString( ylabel, ww, g.getFontMetrics().getHeight() );
		

		// label x-axis
		float hh = (float)(H+1)+g.getFontMetrics().getHeight();
		if ( H == h - 1 ){
			hh = (float)(H-4);
		}
		if ( radians ){
			double u = radianunits[zoom][0]*Math.PI/radianunits[zoom][1];
			int gcd;
			for ( int A = (int)((originX-w/2/scale)/u)-1; A < (originX+w/2/scale)/u+1; A++){
				gcd = gcd(Math.abs(A),(int)(radianunits[zoom][1]));
				g.draw( new Line2D.Double(w/2 + (A*u-originX)*scale,H-2,w/2 + (A*u-originX)*scale,H+2 ) );
				if ( A != 0 ){
					str =  Character.toString( (char)960 );
					if ( (int)((A/gcd)*radianunits[zoom][0]) == -1 ) str = "-" + str;
					else if ( (int)((A/gcd)*radianunits[zoom][0]) != 1 )  str = (int)((A/gcd)*radianunits[zoom][0]) + str;
					if ( (int)(radianunits[zoom][1]/gcd) != 1 ) str = str + "/" + (int)(radianunits[zoom][1]/gcd);
					g.drawString( str, (float)(w/2 + (A*u-originX)*scale) - g.getFontMetrics().stringWidth(str)/2, hh );
				}
			}
		} else {
			for ( int A = (int)((originX-w/2/scale)/units[zoom])-1; A < (originX+w/2/scale)/units[zoom]+1; A++){
				g.draw( new Line2D.Double(w/2 + (A*units[zoom]-originX)*scale,H-2,w/2 + (A*units[zoom]-originX)*scale,H+2 ) );
				if ( A != 0 ){
					if ( (int)(A*units[zoom]) == A*units[zoom] ) str = "" + (int)(A*units[zoom]);
					else str = "" + (float)(A*units[zoom]);
					g.drawString( str, (float)(w/2 + (A*units[zoom]-originX)*scale) - g.getFontMetrics().stringWidth(str)/2, hh );
				}
			}
		}
		drawXLabels( g, hh );
		hh = (float)(H-4);
		if ( H == 1 ) hh = (float)(H+1)+g.getFontMetrics().getHeight();
		g.drawString( xlabel, (float)(w - g.getFontMetrics().stringWidth(xlabel) - 3), hh );
	}

	
	/* overwritten by:
	 ChainRule
	 */
	public void drawXLabels( Graphics2D g, float hh ){
	}


	public void drawCrosshair( Graphics2D g ){
		g.setStroke( new BasicStroke(0.5f) );
		g.setColor( Color.gray );
		g.draw( new Line2D.Double( w/2 - 5, h/2, w/2 + 5, h/2 ) );
		g.draw( new Line2D.Double( w/2, h/2 - 5, w/2, h/2 + 5 ) );
	}
	
	
	/* overwritten by:
	 Antiderivative, AreaBetweenCurves, AreaCircle,
	 CauchyMeanValue, ChainRule, CompoundInterest, Derivative, DoubleIntegral, 
	 EulersMethod, GeneralDerivative, Integral, LHospital, Limits, LimitSequences, Linearization, MeanValue,
	 NewtonsMethod, ParabolicMotion, Parametric, ParametricCurves, Polar, PolarCurves, 
	 SecantLines, Sequences, SlopeFields, Substitution, Surfaces, 
	 TangentCircles, TaylorSeries, UniformConvergence, VolumeViaWashers, VolumeViaShells
	 */
	public void drawEndpoints( Graphics2D g ){
		g.setStroke( endline );
		if ( active ){
			F.addVariable( variable, originX );
			drawLine( g, originX, 0, originX, F.getValue(), colorB );
			drawPointOnXAxis( g, originX, Color.black, false );
		} else {
			F.addVariable( variable, b );
			drawLine( g, b, 0, b, F.getValue(), colorB );
			F.addVariable( variable, a );
			drawLine( g, a, 0, a, F.getValue(), colorA );

			drawPointOnXAxis( g, b, Color.green, overB );
			drawPointOnXAxis( g, a, Color.blue, overA );
		}
	}


	/* overwritten by:
	 Antiderivative, AreaBetweenCurves, AreaCircle, 
	 CauchyMeanValue, ChainRule, CompoundInterest, Derivative, EulersMethod, 
	 LHospital, LimitSequences, ParabolicMotion, Parametric, Polar, Sequences, 
	 SlopeFields, SpaceCurves, Surfaces, 
	 UniformConvergence, VolumeViaWashers, VolumeViaShells
	 */
	public void drawFunction( Graphics2D g ){
		drawFunction( g, F, Color.black );
		//drawFunctionOLD( g );
	}

	
	/* overwritten by:
	 EulersMethod
	 */
	public void drawFunction( Graphics2D g, DJep F, Color color ){
		GeneralPath path = new GeneralPath();
		double i = 0.0;
		double j;
		double k;
		F.addVariable( variable, originX-w/2/scale );
		double y = F.getValue();
		double oldy = y;
		while ( i < w ){
			// find first point that is in viewable portion of xy-plane
			while ( i<=w && (Double.isNaN(y=F.getValue()) || Math.abs(y-originY)>h/scale) ){
			//while ( i<=w && Double.isNaN(y=F.getValue()) ){
				F.addVariable( variable, originX + (++i-w/2)/scale );
			}
			//find a slightly better place to moveto
			oldy = y;
			k = i - 1.0;
			j = i;
			for ( int l=0; l<20; l++ ){
				F.addVariable( variable, originX + ((k + j)/2 - w/2)/scale );
				if ( Double.isNaN(F.getValue()) ){
					k = (k + j)/2;
				} else {
					oldy = F.getValue();
					j = (k + j)/2;
				}
			}
			path.moveTo( (float)j, (float)(h/2 - (oldy-originY)*scale) );
			path.lineTo( (float)i, (float)(h/2 - (y-originY)*scale) );

			// keep going until off the screen or not in domain
			F.addVariable( variable, originX + (++i-w/2)/scale );
			while ( i<=w && Math.abs(y-originY)<h/scale && !Double.isNaN(y=F.getValue()) ){
				path.lineTo( (float)i, (float)(h/2 - (y-originY)*scale) );
				oldy = y;
				F.addVariable( variable, originX + (++i-w/2)/scale );
			}
			// backup a little bit
			k = i;
			j = i - 1.0;
			for ( int l=0; l<20; l++ ){
				F.addVariable( variable, originX + ((k + j)/2 - w/2)/scale );
				if ( Double.isNaN(F.getValue()) ){
					k = (k + j)/2;
				} else {
					oldy = F.getValue();
					j = (k + j)/2;
				}
			}
			path.lineTo( (float)j, (float)(h/2 - (oldy-originY)*scale) );
		}
		g.setColor( color );
		g.setStroke( curve );
		g.draw( path );
	}


	/*
	very primitive drawFunction method
	doesn't check to see if function is defined
	 */
	public void drawFunctionOLD( Graphics2D g ){
		g.setColor( Color.black );
		g.setStroke( curve );
		F.addVariable( variable, originX - w/2/scale );
		double y0 = h/2 - (F.getValue()-originY)*scale;
		double y1;
		for ( double i = 0.0; i<w; i += 1.0 ){
			F.addVariable( variable, originX + (i+1-w/2)/scale );
			y1 = h/2 - (F.getValue()-originY)*scale;
			g.draw( new Line2D.Double(i,y0,i+1,y1) );
			y0 = y1;
		}
	}

	/* overwritten by:
	Antiderivative, EulersMethod, LimitSequences, Polar, Sequences, SlopeFields
	 */
	public void drawGridLines( Graphics2D g ){
		g.setColor( Color.cyan );
		g.setStroke( gridline );
		for ( double A = (int)((originY-h/2/scale)/units[zoom])-1; A < (originY+h/2/scale)/units[zoom]+1; A=A+0.2){
			g.draw( new Line2D.Double( 0,h/2 - (A*units[zoom]-originY)*scale,w,h/2 - (A*units[zoom]-originY)*scale ) );
		}
		for ( double A = (int)((originX-w/2/scale)/units[zoom])-1; A < (originX+w/2/scale)/units[zoom]+1; A=A+0.2){
			g.draw( new Line2D.Double(w/2 + (A*units[zoom]-originX)*scale,0,w/2 + (A*units[zoom]-originX)*scale,h ) );
		}
	}
	

	/* draw line segment from cartesian points (a,b) to (x,y) */
	public void drawLine( Graphics2D g, double a, double b, double x, double y ){
		if ( !Double.isNaN(a) && !Double.isNaN(b) && !Double.isNaN(x) && !Double.isNaN(y) ){
			GeneralPath path = new GeneralPath();
			path.moveTo( (float)(w/2 + (a - originX)*scale), (float)(h/2 - (b - originY)*scale) );
			path.lineTo( (float)(w/2 + (x - originX)*scale), (float)(h/2 - (y - originY)*scale) );
			g.draw( path );
		}
	}
	

	public void drawLine( Graphics2D g, double a, double b, double x, double y, Color color ){
		g.setColor( color );
		drawLine( g, a, b, x, y );
	}


	public void drawPoint( Graphics2D g, double x, double y, Color color, boolean over ){
		double[] P = toScreenPoint( x, y );
		Ellipse2D arc = new Ellipse2D.Double( P[0] - r, P[1] - r, 2*r, 2*r );
		g.setColor( Color.black );
		g.setStroke( new BasicStroke(1.5f) );
		if ( over ) g.draw( new Ellipse2D.Double( P[0] - r - 3, P[1] - r - 3, 2*r + 6, 2*r + 6 ) );
		g.setStroke( endline );
		g.setColor( color );
		g.fill( arc );
		g.setColor( Color.black );
		g.draw( arc );
	}


	// only used by Sequences and LinearTransformation
	public void drawPoint( Graphics2D g, double x, double y, double r, Color color ){
		double[] P = toScreenPoint( x, y );
		Ellipse2D arc = new Ellipse2D.Double( P[0] - r, P[1] - r, 2*r, 2*r );
		g.setStroke( endline );
		g.setColor( color );
		g.fill( arc );
		g.setColor( Color.black );
		g.draw( arc );
	}


	public void drawPointOnXAxis( Graphics2D g, double x, Color color, boolean over ){
		double[] P = toScreenPoint( x, 0 );
		Ellipse2D arc = new Ellipse2D.Double( P[0] - r, H - r, 2*r, 2*r );
		g.setColor( Color.black );
		g.setStroke( new BasicStroke(1.5f) );
		if ( over ) g.draw( new Ellipse2D.Double( P[0] - r - 3, H - r - 3, 2*r + 6, 2*r + 6 ) );
		g.setStroke( endline );
		g.setColor( color );
		g.fill( arc );
		g.setColor( Color.black );
		g.draw( arc );
	}


	public void drawPointOnYAxis( Graphics2D g, double y, Color color, boolean over ){
		double[] P = toScreenPoint( 0, y );
		Ellipse2D arc = new Ellipse2D.Double( W - r, P[1] - r, 2*r, 2*r );
		g.setColor( Color.black );
		g.setStroke( new BasicStroke(1.5f) );
		if ( over ) g.draw( new Ellipse2D.Double( W - r - 3, P[1] - r - 3, 2*r + 6, 2*r + 6 ) );
		g.setStroke( endline );
		g.setColor( color );
		g.fill( arc );
		g.setColor( Color.black );
		g.draw( arc );
	}


	public int gcd( int a, int b ){
		if ( a%b == 0 ) return b;
		return gcd( b, a%b );
	}


	Point POINT;

	public double getAngle( double x0, double y0, double x1, double y1 ){
		double z = x0*y1 - y0*x1;
		double C = x0*x1 + y0*y1;
		double phi = Math.asin(z/Math.sqrt((x0*x0+y0*y0)*(x1*x1+y1*y1)));
		if ( C < 0 ){
			if ( z < 0 ) return (-Math.PI - phi);
			else return (Math.PI - phi); 
		}
		return phi;
	}
	
	// returns the intersection of two lines
	public double[] getIntersection( double m1, double b1, double m2, double b2 ){
		if ( m1 != m2 ){
			double[] out = new double[2];
			out[0] = (b2 - b1)/(m1 - m2);
			out[1] = m1*out[0] + b1;
			return out;
		} 
		return null;
	}
	
	
	/* overwritten by:
	 LimitSequences, Sequences
	 */
	public double[] toCartesianPoint( double a, double b ){
		double[] out = { originX + (a-w/2)/scale, originY - (b-h/2)/scale };
		return out;
	}
	
	
	/* overwritten by:
	 LimitSequences, Sequences
	 */
	public double[] toScreenPoint( double a, double b ){
		double[] out = { w/2 + (a - originX)*scale, h/2 - (b - originY)*scale };
		return out;
	}
	
	
	public void keyTyped( KeyEvent ke ){
	}
	
	
	/* overwritten by:
	 CoumpoundInterest, Graph3D, LeastSquares, LimitSequences, Sequences
	 */
	public void keyPressed( KeyEvent ke ){
		int code = ke.getKeyCode();
		if ( code == KeyEvent.VK_CONTROL ){
			control = true;
		} else if ( control && code == KeyEvent.VK_R ){
			radians = !radians;
			newBackground = true;
			repaint();
		}
	}
	
	
	/* overwritten by:
	 CompoundInterest, Graph3D, LeastSquares, LimitSequences, Sequences
	 */
	public void keyReleased( KeyEvent ke ){
		int code = ke.getKeyCode();
		//shift = false;
		if ( code == KeyEvent.VK_CONTROL ){
			control = false;
		}
	}

	
	/* overwritten by:
	 LimitSequences, Sequences
	*/
	public void mouseClicked( MouseEvent me ){
		POINT = me.getPoint();
		double[] P = toCartesianPoint( POINT.x, POINT.y );
		if ( me.getClickCount() > 1 ){
			// set center so that P stays in same place
			int z = Math.max( 0, zoom-1 );
			originX = P[0] - units[z]*(POINT.x-w/2)/pixels;
			originY = P[1] + units[z]*(POINT.y-h/2)/pixels;
			// zoom in on point P
			applet.actionPerformed( new ActionEvent(applet.zoomin,ActionEvent.ACTION_PERFORMED,"zoom") );
		} 
	}

	
	public void mouseEntered( MouseEvent me ){
		requestFocus();
	}

	public void mouseExited( MouseEvent me ){
	}

	
	/* overwritten by:
	 Antiderivative, AreaCircle, CauchyMeanValue, ChainRule, Derivative, EulersMethod, GeneralDerivative, Graph3D, Integral, 
	 LHospital, Limits, LimitSequences, Linearization, MeanValue, NewtonsMethod, 
	 ParabolicMotion, Parametric, Polar, SecantLines, Sequences, SlopeFields, Surfaces,
	 TangentCircles, TaylorSeries, UniformConvergence, VolumeViaWashers
	 */
	public void mousePressed( MouseEvent me ){
		POINT = me.getPoint();
		double xa = POINT.x - w/2 - (a - originX)*scale;
		double xb = POINT.x - w/2 - (b - originX)*scale;
		double y = POINT.y-H;
		if ( xa*xa + y*y < rr ){
			newA = true;
		} else if ( xb*xb + y*y < rr ){
			newB = true;
		}
		requestFocus();
	}

	
	/* overwritten by:
	 LeastSquares, Surfaces
	 */
	public void mouseReleased(MouseEvent me){
		newA = false;
		newB = false;
		newC = false;
		newD = false;
	}	

	
	/* overwritten by:
	 AreaCircle, CompoundInterest, LeastSquares, LimitSequences, Sequences
	 */
	public void mouseMoved( MouseEvent me ){
		mousePressed( me );
		if ( overA ){
			if ( !newA ){
				overA = false;
				repaint();
				setCursor( arrow );
			}
		} else if ( overB ){
			if ( !newB ){
				overB = false;
				repaint();
				setCursor( arrow );
			}
		} else if ( overC ){
			if ( !newC ){
				overC = false;
				repaint();
				setCursor( arrow );
			}
		} else if ( overD ){
			if ( !newD ){
				overD = false;
				repaint();
				setCursor( arrow );
			}
		} else if ( newA ){
			overA = true;
			setCursor( hand );
			repaint();
		} else if ( newB ){
			overB = true;
			setCursor( hand );
			repaint();
		} else if ( newC ){
			overC = true;
			setCursor( hand );
			repaint();
		} else if ( newD ){
			overD = true;
			setCursor( hand );
			repaint();
		}
		
		mouseReleased( me );
	}

	
	/* overwritten by:
	 Antiderivative, CauchyMeanValue, ChainRule, EulersMethod, Graphs3D, LHospital, Limits, LimitSequences,
	 ParabolicMotion, Parametric, Polar, SecantLines, Sequences, SlopeFields, Substitution, Surfaces,
	 UniformConvergence
	 */
	public void mouseDragged(MouseEvent me){
		double[] P = toCartesianPoint( me.getPoint().x, me.getPoint().y );
		if ( newA ){
			a = P[0];
			applet.a.setValue( a );
			applet.updateGraphs( this );
		} else if ( newB ){
			b = P[0];
			applet.b.setValue( b );
			applet.updateGraphs( this );
		} else if ( newC ){
			c = P[0];
			applet.c.setValue( c );		
			applet.updateGraphs( this );
		} else {
			originX -= (me.getPoint().x - POINT.x)/scale;
			originY += (me.getPoint().y - POINT.y)/scale;
			POINT = me.getPoint();
			newBackground = true;
			repaint();
		}
	}	
}