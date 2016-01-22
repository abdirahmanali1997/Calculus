import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;

import org.nfunk.jep.*;

public class NewtonsMethodGraph extends Graph{

	public NewtonsMethodGraph( CalculusApplet applet, String f, double a, int n ){
		super();

		this.applet = applet;
		this.a = a;
		this.b = Double.MAX_VALUE;
		this.n = Math.max( n, 0 );

		F.parseExpression( f );
	}


	public void draw( Graphics2D g ){
		g.setColor( Color.red );
		g.setStroke( new BasicStroke(3.0f) );

		F.addVariable( "x", a );
		DerivativeList tmp = dList;

		double an = a;
		double y;
		double m = 1.0;
		int i=0;
		while ( i < n & m != 0 ){	
			try{
				F.addVariable( "x", an );
				y = F.getValue();
				g.setColor( Color.gray );
				g.setStroke( dashed );
				drawLine( g, an, 0, an, y );
//applet.statusbar.setText("Calculating new m:" + m);
				m = Double.parseDouble( F.evaluate( F.differentiate( F.getTopNode(), "x" ) ).toString() );
//applet.statusbar.setText("" + m);
				if ( Math.abs(m) > 0.00000000000001 ){
					g.setColor( Color.red );
					g.setStroke( new BasicStroke(3.0f) );
					drawLine( g, an, y, an-y/m, 0 );
					an -= y/m;
				} else {
					g.setColor( Color.red );
					g.setStroke( new BasicStroke(3.0f) );
					g.draw( new Line2D.Double( 0, h/2 - (y-originY)*scale, w, h/2 - (y-originY)*scale ) );
					m = 0;
				}
			} catch( ParseException e ){
				i = n;
			} catch( Exception e ){
				i = n;
			}
			i++;
		}
		if ( m != 0 ) applet.setStat( "Root: " , an, Color.red );
		else applet.stat.setText( "Horizontal Tangent" );
	}

	public void drawEndpoints( Graphics2D g ){
		drawPointOnXAxis( g, a, Color.red, overA );
	}

	public void mousePressed(MouseEvent me){
		POINT = me.getPoint();
		double x = POINT.x - w/2 - (a - originX)*scale;
		double y = POINT.y-H;
		if ( x*x + y*y < rr ){
			newA = true;
		} else {
			newA = false;
		}
		requestFocus();
	}
}