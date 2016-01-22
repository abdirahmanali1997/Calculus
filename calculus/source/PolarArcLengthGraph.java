import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;

public class PolarArcLengthGraph extends PolarGraph{

	public PolarArcLengthGraph( CalculusApplet applet, String f, double a, double b, int n ){
		super();

		this.applet = applet;
		this.a = a;
		this.b = b;
		this.n = Math.max( n,0 );

		F.parseExpression( f );
	}

	public void draw( Graphics2D g ){
		// draw graph of Riemann Sums
		double delta = (b-a)/n;
		
		F.addVariable( "t", a );
		double x0 = F.getValue()*Math.cos(a);
		double y0 = F.getValue()*Math.sin(a);
		double x1;
		double y1;
		
		if ( newStat ) stat = 0.0;
		g.setColor( Color.red );
		g.setStroke( new BasicStroke(3.0f) );
		for ( int i=0; i<n; i++ ){
			F.addVariable( "t", a + (i+1)*delta );
			x1 = F.getValue()*Math.cos(a + (i+1)*delta);
			y1 = F.getValue()*Math.sin(a + (i+1)*delta);
			if ( newStat ) stat += Math.sqrt( (x1-x0)*(x1-x0) + (y1-y0)*(y1-y0));
			drawLine( g, x0, y0, x1, y1 );
			x0 = x1;
			y0 = y1;
		}
		
		newStat = false;
		if ( n == 0 ) stat = 0.0;
		applet.setStat( "Length \u2248 ", stat, Color.red );
	}
}