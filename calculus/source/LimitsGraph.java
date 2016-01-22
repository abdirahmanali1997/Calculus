import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;

public class LimitsGraph extends Graph{

	public LimitsGraph( CalculusApplet applet, String f, double a, double b, double c, double d ){
		super();

		this.applet = applet;
		this.a = a;				// a
		this.b = b;				// L
		this.c = c;				// epsilon
		this.d = d;				// delta

		F.parseExpression( f );
	}
	
	public void draw( Graphics2D g ){
		String str = applet.choice.getSelectedItem().toString();

		// draw yellow horizontal strip
		g.setColor( new Color(255,255,0,100) );
		g.fill( new Rectangle2D.Double( 0, h/2 - (b+c-originY)*scale, w, 2*c*scale ) );
		
		// draw blue/green vertical strip
		double[] P = toScreenPoint( a-d, b+c );
		if ( str.equals("Two-Sided Limit") || str.equals("One-Sided Limit from the Left") ){
			g.setColor( new Color(0,0,255,100) );
			g.fill( new Rectangle2D.Double( P[0], 0, d*scale, h ) );
			g.setColor( new Color(0,255,0,100) );
			g.fill( new Rectangle2D.Double( P[0], P[1], d*scale, 2*c*scale ) );

			g.setColor( Color.black );
			g.setStroke( new BasicStroke(0.66f) );		
			g.draw( new Line2D.Double( P[0], 0, P[0], h) );

		}

		if ( str.equals("Two-Sided Limit") || str.equals("One-Sided Limit from the Right") ){
			P = toScreenPoint( a, b+c );
			g.setColor( new Color(0,0,255,100) );
			g.fill( new Rectangle2D.Double( P[0], 0, d*scale, h ) );
			g.setColor( new Color(0,255,0,100) );
			g.fill( new Rectangle2D.Double( P[0], P[1], d*scale, 2*c*scale ) );

			P = toScreenPoint( a+d, b+c );
			g.setColor( Color.black );
			g.setStroke( new BasicStroke(0.66f) );		
			g.draw( new Line2D.Double( P[0], 0, P[0], h) );
		}

		// draw remaining lines		
		P = toScreenPoint( a, b-c );
		g.draw( new Line2D.Double( P[0], 0, P[0], h ) );
		g.draw( new Line2D.Double( 0, P[1], w, P[1] ) );
		P = toScreenPoint( a, b );
		g.draw( new Line2D.Double( 0, P[1], w, P[1] ) );
		P = toScreenPoint( a, b+c );
		g.draw( new Line2D.Double( 0, P[1], w, P[1] ) );
		
		applet.stat.setText( "" );
	}
	

	public void drawEndpoints( Graphics2D g ){
		drawPointOnXAxis( g, a, Color.blue, overA );
		
		if ( applet.choice.getSelectedItem().toString().equals("One-Sided Limit from the Left") ){
			drawPointOnXAxis( g, a-d, Color.magenta, overD );
		} else {
			drawPointOnXAxis( g, a+d, Color.magenta, overD );
		}

		drawPointOnYAxis( g, b, Color.green, overB );
		drawPointOnYAxis( g, b+c, Color.red, overC );
	}

	public void mousePressed(MouseEvent me){
		POINT = me.getPoint();
		double xa = POINT.x - w/2 - (a - originX)*scale;
		double xb = POINT.y - h/2 + (b - originY)*scale;
		double xc = POINT.y - h/2 + (b + c - originY)*scale;
		double xd = POINT.x - w/2 - (a + d - originX)*scale;
		if ( applet.choice.getSelectedItem().toString().equals("One-Sided Limit from the Left") ){
			xd = POINT.x - w/2 - (a - d - originX)*scale;
		}
		if ( xc*xc + (POINT.x-W)*(POINT.x-W) < rr ){
			newC = true;
		} else if ( xd*xd + (POINT.y-H)*(POINT.y-H) < rr ){
			newD = true;
		} else if ( xa*xa + (POINT.y-H)*(POINT.y-H) < rr ){
			newA = true;
		} else if ( xb*xb + (POINT.x-W)*(POINT.x-W) < rr ){
			newB = true;
		}
		requestFocus();
	}

	public void mouseDragged(MouseEvent me){
		double[] P = toCartesianPoint( me.getPoint().x, me.getPoint().y );
		if ( newA ){
			a = P[0];
			applet.a.setValue( a );
		} else if ( newB ){
			b = P[1];
			applet.b.setValue( b );
		} else if ( newC ){
			c = Math.max( 0, P[1] - b );
			applet.c.setValue( c );
		} else if ( newD ){
			d = Math.max( 0, P[0] - a );
			if ( applet.choice.getSelectedItem().toString().equals("One-Sided Limit from the Left") ){
				d = Math.max( 0, a - P[0] );
			}
			applet.d.setValue( d );
		} else {
			originX -= (me.getPoint().x - POINT.x)/scale;
			originY += (me.getPoint().y - POINT.y)/scale;
			POINT = me.getPoint();
			newBackground = true;
		}
		repaint();
	}	
}