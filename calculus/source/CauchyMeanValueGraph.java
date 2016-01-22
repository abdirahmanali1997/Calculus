import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import org.nfunk.jep.*;

public class CauchyMeanValueGraph extends Graph{

	
	public CauchyMeanValueGraph( CalculusApplet applet, String f, String g, double a, double b, double c ){
		super();

		this.applet = applet;
		this.a = a;
		this.b = b;
		this.c = c;

		F.parseExpression( f );
		G.parseExpression( g );
	}

	
	public void draw( Graphics2D g ){
		F.addVariable( "x", a );
		double fa = F.getValue();
		F.addVariable( "x", b );
		double fb = F.getValue();
		F.addVariable( "x", c );
		double fc = F.getValue();

		G.addVariable( "x", a );
		double ga = G.getValue();
		G.addVariable( "x", b );
		double gb = G.getValue();
		G.addVariable( "x", c );
		double gc = G.getValue();

		double m;
		double[] P = toCartesianPoint( 0, 0 );
		double[] Q = toCartesianPoint( w, 0 );
		g.setStroke( new BasicStroke(3.0f) );
		
		// draw Tangent line
		if ( Math.min(a,b) < c && c < Math.max(a,b) ){
			try{
				m = Double.parseDouble( F.evaluate( F.differentiate( F.getTopNode(), "x" ) ).toString() );
				m /= Double.parseDouble( G.evaluate( G.differentiate( G.getTopNode(), "x" ) ).toString() );
				drawLine( g, P[0], fc + m*(P[0] - gc), Q[0], fc + m*(Q[0] - gc), Color.gray );
			} catch( ParseException e ){
			} catch( Exception e ){
			}
		}

		// draw Secant line
		drawLine( g, ga, fa, gb, fb, Color.red );
		m = (fb-fa)/(gb-ga);
		g.setStroke( dashed );
		drawLine( g, gc+(ga-gb)/2, fc + (fa-fb)/2, gc-(ga-gb)/2, fc - (fa-fb)/2, Color.red );
	}


	public void drawEndpoints( Graphics2D g ){
		F.addVariable( "x", b );
		G.addVariable( "x", b );
		drawPoint( g, G.getValue(), F.getValue(), Color.green, overB );

		F.addVariable( "x", a );
		G.addVariable( "x", a );
		drawPoint( g, G.getValue(), F.getValue(), Color.blue, overA );

		F.addVariable( "x", c );
		G.addVariable( "x", c );
		drawPoint( g, G.getValue(), F.getValue(), Color.red, overC );
	}


	// this should be similar to how parametric curves are drawn...see if we can reuse some code
	public void drawFunction( Graphics2D g ){
		double x;
		double y;
		int grn;
		
		GeneralPath path = new GeneralPath();
		double min = Math.min( a, Math.min( b, c) );
		double max = Math.max( a, Math.max( b, c) );
		F.addVariable( variable, min );
		G.addVariable( variable, min );
		x = G.getValue();
		y = F.getValue();
		double i = min;
		double delta = units[zoom]/(zoom+1)/(zoom+1);
		
		while ( i < max ){
			while ( i < max && (Double.isNaN(x) || Double.isNaN(y)) ){
				i += delta;
				F.addVariable( variable, i );
				G.addVariable( variable, i );
				x = G.getValue();
				y = F.getValue();
			}
			if ( i<max ) path.moveTo( (float)(w/2+(x-originX)*scale), (float)(h/2-(y-originY)*scale) );
			i += delta;
			F.addVariable( variable, i );
			G.addVariable( variable, i );
			x = G.getValue();
			y = F.getValue();
			while ( i<max && !Double.isNaN(x) && !Double.isNaN(y) ){
				path.lineTo( (float)(w/2+(x-originX)*scale), (float)(h/2-(y-originY)*scale) );
				i += delta;
				F.addVariable( variable, i );
				G.addVariable( variable, i );
				x = G.getValue();
				y = F.getValue();
			}
			if ( i >= max ){
				F.addVariable( variable, max );
				G.addVariable( variable, max );
				x = G.getValue();
				y = F.getValue();
				if ( !Double.isNaN(x) && !Double.isNaN(y) ){
					path.lineTo( (float)(w/2+(x-originX)*scale), (float)(h/2-(y-originY)*scale) );
				}
			}
		}
		
		drawPoint( g, x, y, Color.black, false );
		g.setColor( Color.black );
		g.setStroke( curve );
		g.draw( path );
		
		F.addVariable( variable, min );
		G.addVariable( variable, min );
		drawPoint( g, G.getValue(), F.getValue(), Color.black, false );
	}


	public void mousePressed(MouseEvent me){
		POINT = me.getPoint();
		requestFocus();
	}


	public void mouseDragged(MouseEvent me){
		Point p = me.getPoint();
		originX -= (me.getPoint().x - POINT.x)/scale;
		originY += (me.getPoint().y - POINT.y)/scale;
		POINT = me.getPoint();
		newBackground = true;
		repaint();
	}	
}