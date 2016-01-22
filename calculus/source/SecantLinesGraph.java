import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import org.nfunk.jep.*;

public class SecantLinesGraph extends Graph{

	public SecantLinesGraph( CalculusApplet applet, String f, double a, double b ){
		super();

		this.applet = applet;
		this.a = a;
		this.b = b;

		F.parseExpression( f );
	}

	public void draw( Graphics2D g ){
		F.addVariable( "x", a );
		double fa = F.getValue();
		String c = applet.choice.getSelectedItem().toString();
		String c2 = applet.choice2.getSelectedItem().toString();
		
		// draw tangent line
		double m;
		double[] P = toCartesianPoint( 0, 0 );
		double[] Q = toCartesianPoint( w, 0 );
		g.setStroke( new BasicStroke(3.0f) );
		if ( c.equals("Show Both") || c.equals("Show Tangent Line") ){
			try{
				m = Double.parseDouble( F.evaluate( F.differentiate( F.getTopNode(), "x" ) ).toString() );
				drawLine( g, P[0], fa + m*(P[0] - a), Q[0], fa + m*(Q[0] - a), Color.gray );
			} catch( ParseException e ){
			} catch( Exception e ){
			}
			applet.stat.setText( "" );
		}
		
		double B = b;
		if ( c2.equals( " h=" ) ){
			B = a + b;
		}

		// draw secant line
		if ( c.equals("Show Both") || c.equals("Show Secant Line") ){
			F.addVariable( "x", B );
			double fb = F.getValue();
			m = (fa-fb)/(a-B);
			if ( a == B ){
				applet.stat.setText( "" );
			} else {
				drawLine( g, P[0], fa + m*(P[0] - a), Q[0], fa + m*(Q[0] - a), Color.red );
				applet.setStat( "Slope \u2248 ", m, Color.red );
			}
		}
		
		if ( c.equals( "Show Neither" ) ){
			applet.stat.setText( "" );
		}
	}


	public void drawEndpoints( Graphics2D g ){
		String c2 = applet.choice2.getSelectedItem().toString();
		double B = b;
		if ( c2.equals( " h=" ) ){
			B = a + b;
		}

		F.addVariable( "x", B );
		drawPoint( g, B, F.getValue(), Color.green, overB );

		F.addVariable( "x", a );
		drawPoint( g, a, F.getValue(), Color.blue, overA );
	}


	public void mousePressed(MouseEvent me){
		String c2 = applet.choice2.getSelectedItem().toString();
		double B = b;
		if ( c2.equals( " h=" ) ){
			B = a + b;
		}

		POINT = me.getPoint();
		double xa = POINT.x - w/2 - (a - originX)*scale;
		double xb = POINT.x - w/2 - (B - originX)*scale;
		F.addVariable( "x", a );
		double ya = POINT.y - h/2 + (F.getValue() - originY)*scale;
		F.addVariable( "x", B );
		double yb = POINT.y - h/2 + (F.getValue() - originY)*scale;
		if ( xa*xa + ya*ya < rr ){
			newA = true;
		} else if ( xb*xb + yb*yb < rr ) {
			newB = true;
		}
		requestFocus();
	}

	public void mouseDragged(MouseEvent me){
		double[] P = toCartesianPoint( me.getPoint().x, me.getPoint().y );
		if ( newA ){
			a = P[0];
			applet.a.setValue( a );
			applet.updateGraphs( this );
		} else if ( newB ){
			String c2 = applet.choice2.getSelectedItem().toString();
			b = P[0];
			if ( c2.equals( " h=" ) ){
				b -= a;
			}
			applet.b.setValue( b );
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