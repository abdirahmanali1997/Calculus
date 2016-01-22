import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import org.nfunk.jep.*;

public class MeanValueGraph extends Graph{

	public MeanValueGraph( CalculusApplet applet, String f, double a, double b, double c ){
		super();

		this.applet = applet;
		this.a = a;
		this.b = b;
		this.c = c;

		F.parseExpression( f );
	}

	public void draw( Graphics2D g ){
		F.addVariable( "x", a );
		double fa = F.getValue();
		F.addVariable( "x", b );
		double fb = F.getValue();
		F.addVariable( "x", c );
		double fc = F.getValue();
		double m;
		double[] P = toCartesianPoint( 0, 0 );
		double[] Q = toCartesianPoint( w, 0 );
		g.setStroke( new BasicStroke(3.0f) );
		
		// draw Tangent line
		if ( Math.min(a,b) < c && c < Math.max(a,b) ){
			try{
				m = Double.parseDouble( F.evaluate( F.differentiate( F.getTopNode(), "x" ) ).toString() );
				drawLine( g, P[0], fc + m*(P[0] - c), Q[0], fc + m*(Q[0] - c), Color.gray );
				//applet.stat2.setForeground( Color.gray );
				//applet.setStat2( "Slope = ", (float)m);
			} catch( ParseException e ){
			} catch( Exception e ){
			}
		}

		// draw Secant line
		drawLine( g, a, fa, b, fb, Color.red );
		if ( Math.min(a,b) < c && c < Math.max(a,b) ){
			m = (fb-fa)/(b-a);
			g.setStroke( dashed );
			drawLine( g, a, fc + m*(a-c), b, fc + m*(b-c), Color.red );
		}
		//applet.setStat( "Slope \u2248 ", m );
		//applet.stat.setText( "" );
		//if ( c < Math.min(a,b) || c > Math.max(a,b) ) applet.stat.setText( "The value c must be between a and b." );

	}


	public void drawEndpoints( Graphics2D g ){
		F.addVariable( "x", b );
		drawPoint( g, b, F.getValue(), Color.green, overB );

		F.addVariable( "x", a );
		drawPoint( g, a, F.getValue(), Color.blue, overA );

		F.addVariable( "x", c );
		drawPoint( g, c, F.getValue(), Color.red, overC );
	}


	public void mousePressed(MouseEvent me){
		POINT = me.getPoint();
		double xa = POINT.x - w/2 - (a - originX)*scale;
		double xb = POINT.x - w/2 - (b - originX)*scale;
		double xc = POINT.x - w/2 - (c - originX)*scale;
		F.addVariable( "x", a );
		double ya = POINT.y - h/2 + (F.getValue() - originY)*scale;
		F.addVariable( "x", b );
		double yb = POINT.y - h/2 + (F.getValue() - originY)*scale;
		F.addVariable( "x", c );
		double yc = POINT.y - h/2 + (F.getValue() - originY)*scale;
		if ( xa*xa + ya*ya < rr ){
			newA = true;
		} else if ( xb*xb + yb*yb < rr ) {
			newB = true;
		} else if ( xc*xc + yc*yc < rr ) {
			newC = true;
		}
		requestFocus();
	}
}