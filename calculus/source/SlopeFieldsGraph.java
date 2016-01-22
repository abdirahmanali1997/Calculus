import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;

import org.nfunk.jep.*;

public class SlopeFieldsGraph extends Graph{

	public SlopeFieldsGraph( CalculusApplet applet, String f, double a, double b ){
		super();

		//showGrid = false;
		
		this.applet = applet;
		this.a = a;
		this.b = b;

		F.parseExpression( f );
	}
	

	public void draw( Graphics2D g ){
		GeneralPath path = new GeneralPath();
		g.setColor( Color.red );
		g.setStroke( curve );
		double x0 = a;
		double y0 = b;
		double m;
		double d;
		
		double[] P = toScreenPoint( a, b );
		path.moveTo( (float)P[0], (float)P[1] );
		int i = 0;
		F.addVariable( "x", x0 );
		F.addVariable( "y", y0 );
		m = F.getValue();
		while ( i< 1000 && !Double.isNaN( m ) ){
			d = 0.01/Math.sqrt(1+m*m);
			P = toScreenPoint( x0 = x0 + d, y0 = y0 + d*m );
			path.lineTo( (float)P[0], (float)P[1] );
			F.addVariable( "x", x0 );
			F.addVariable( "y", y0 );
			m = F.getValue();
			i++;
		}
		g.draw( path );

		x0 = a;
		y0 = b;
		path = new GeneralPath();
		P = toScreenPoint( a, b );
		path.moveTo( (float)P[0], (float)P[1] );
		i = 0;
		F.addVariable( "x", x0 );
			F.addVariable( "y", y0 );
		m = F.getValue();
		while ( i< 1000 && !Double.isNaN( m ) ){
			d = 0.01/Math.sqrt(1+m*m);
			P = toScreenPoint( x0 = x0 - d, y0 = y0 - d*m );
			path.lineTo( (float)P[0], (float)P[1] );
			F.addVariable( "x", x0 );
			F.addVariable( "y", y0 );
			m = F.getValue();
			i++;
		}
		g.draw( path );

		applet.stat.setText( "" );
	}
	
	
	public void drawEndpoints( Graphics2D g ){
		drawPoint( g, a, b, Color.red, overA);
	}

	
	public void drawFunction( Graphics2D g ){
	}

	
	// draw slope field
	public void drawGridLines( Graphics2D g ){
		double m;
		double d;
		double[] P;
		g.setColor( Color.blue );
		g.setStroke( endline );
		GeneralPath path= new GeneralPath();
		for ( double B = (int)((originY-h/2/scale)/units[zoom])-1; B < (originY+h/2/scale)/units[zoom]+1; B=B+0.25 ){
			for ( double A = (int)((originX-w/2/scale)/units[zoom])-1; A < (originX+w/2/scale)/units[zoom]+1; A=A+0.25 ){
				F.addVariable( "x", A*units[zoom] );
				F.addVariable( "y", B*units[zoom] );
				m = F.getValue();
				if ( !Double.isNaN( m ) ){
					d = 5/Math.sqrt(1+m*m);
					P = toScreenPoint( A*units[zoom], B*units[zoom] );
					path.moveTo( (float)(P[0] - d), (float)(P[1] + d*m) );
					path.lineTo( (float)(P[0] + d), (float)(P[1] - d*m) );
				}
			}
		}
		g.draw( path );
	}
	

	public void mousePressed(MouseEvent me){
		POINT = me.getPoint();
		double[] P = toScreenPoint( a, b );
		double x = POINT.x - P[0];
		double y = POINT.y - P[1];
		if ( x*x + y*y < rr ){
			newA = true;
		}
		requestFocus();
	}

	public void mouseDragged(MouseEvent me){
		double[] P = toCartesianPoint( me.getPoint().x, me.getPoint().y );
		if ( newA ){
			a = P[0];
			b = P[1];
			applet.a.setValue( a );
			applet.b.setValue( b );
			applet.stat.setText( "Graphing..." );
		} else {
			originX -= (me.getPoint().x - POINT.x)/scale;
			originY += (me.getPoint().y - POINT.y)/scale;
			POINT = me.getPoint();
			newBackground = true;
		}
		repaint();
	}
}