import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;

import org.nfunk.jep.*;

public class IntegralGraph extends Graph{
	
	public IntegralGraph( CalculusApplet applet, String f, double a, double b, int n ){
		super();

		this.applet = applet;
		this.a = a;
		this.b = b;
		//this.b = Double.MAX_VALUE;
		this.n = Math.max( n, 0 );
		newStat = true;		

		F.parseExpression( f );
	}

	public void draw( Graphics2D g ){
		g.setColor( Color.red );
		g.setStroke( curve );

		double x = originX - w/2/scale;
		double delta = (x-a)/n;
		double y0 = 0.0;
		for ( int j = 0; j<n; j++ ){
			F.addVariable( variable, a + j*delta + delta/2.0 );
			y0 += F.getValue( );
		}
		y0 = h/2 - (y0*delta - originY)*scale;
		
		double y1;
		for ( double i = 0.0; i<w; i += 1.0 ){
			y1 = 0.0;
			x = originX + (i+1-w/2)/scale;
			delta = (x-a)/n;
			for ( int j = 0; j<n; j++ ){
				F.addVariable( variable, a + j*delta + delta/2.0 );
				y1 += F.getValue( );
			}
			y1 = h/2 - (y1*delta - originY)*scale;
			g.draw( new Line2D.Double(i,y0,i+1,y1) );
			y0 = y1;
		}
		
		stat = 0;
		delta = (b-a)/n;
		for ( int j = 0; j<n; j++ ){
			F.addVariable( variable, a + j*delta + delta/2.0 );
			stat += F.getValue( );
		}
		stat *= delta;
		
		applet.setStat( "Area \u2248 ", stat, Color.red );
		//applet.stat.setText( " " + stat );
	}
	
	public void drawEndpoints( Graphics2D g ){
		drawPoint( g, a, 0, Color.blue, overA );
		drawPoint( g, b, stat, Color.green, overB );
	}

	public void mousePressed(MouseEvent me){
		POINT = me.getPoint();
		double[] P = toScreenPoint( 0, 0 );
		double xa = POINT.x - w/2 - (a - originX)*scale;
		double xb = POINT.x - w/2 - (b - originX)*scale;
		double ya = POINT.y - P[1];
		double yb = POINT.y - h/2 + (stat - originY)*scale;
		if ( xa*xa + ya*ya < rr ){
			newA = true;
		} else if ( xb*xb + yb*yb < rr ){
			newB = true;
		}
		requestFocus();
	}
}