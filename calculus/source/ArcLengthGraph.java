import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;


public class ArcLengthGraph extends Graph{

	
	public ArcLengthGraph( CalculusApplet applet, String f, double a, double b, int n ){
		super();

		this.applet = applet;
		this.a = a;
		this.b = b;
		this.n = Math.max( n,0 );

		F.parseExpression( f );
	}
		
		
	public void draw( Graphics2D g ){
		double delta = Math.abs(b-a)/n;
		double min = Math.min(a,b);
		double y0;
		double y1;

		if ( newStat ) stat = 0.0;
		g.setColor( Color.red );
		if ( b < a ) g.setColor( Color.cyan );
		g.setStroke( curve );
		
		F.addVariable( "x", min );
		y0 = F.getValue();
		for ( int i=0; i<n; i++ ){
			F.addVariable( "x", min + (i+1)*delta );
			y1 = F.getValue();
			if ( newStat ) stat += Math.sqrt( delta*delta + (y1-y0)*(y1-y0) );
			drawLine( g, min + i*delta, y0, min+(i+1)*delta, y1 );
			y0 = y1;
		}
		if ( n == 0 ) stat = 0.0;
		if ( newStat && b < a ) stat *= -1.0;
		if ( stat < 0 ) applet.setStat( "Length \u2248 ", stat, Color.blue );
		else applet.setStat( "Length \u2248 ", stat, Color.red );
		newStat = false;
	}
}