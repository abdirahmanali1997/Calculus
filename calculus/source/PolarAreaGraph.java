import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;

public class PolarAreaGraph extends PolarGraph{

	public PolarAreaGraph( CalculusApplet applet, String f, double a, double b, int n ){
		super();

		this.applet = applet;
		this.a = a;
		this.b = b;
		this.n = Math.max( n,0 );

		F.addVariable( "t", 0 );		
		F.parseExpression( f );
	}

	public void draw( Graphics2D g ){
		double delta = (b-a)/n;
		double r;
		Arc2D wedge;
		double[] P = toScreenPoint( 0, 0 );

		if ( newStat) stat = 0.0;
		
		g.setColor( red );
		g.setStroke( new BasicStroke( 1.0f ) );
		for ( int i=0; i<n; i++ ){
			F.addVariable( "t", a + (i+0.5)*delta );
			r = F.getValue();
			if ( newStat ) stat += r*r;
			r *= scale;
			wedge = new Arc2D.Double( P[0] - Math.abs(r), P[1] - Math.abs(r), Math.abs(2*r), Math.abs(2*r), 180*(a + i*delta)/Math.PI + (r<0?180.0:0), 180*delta/Math.PI, Arc2D.PIE );
			g.fill( wedge );
		}
		if ( newStat ){
			stat *= delta/2.0;
		}
		newStat = false;
		if ( n == 0 ) stat = 0.0;
		applet.setStat( "Area \u2248 ", stat, Color.red );
	}
}