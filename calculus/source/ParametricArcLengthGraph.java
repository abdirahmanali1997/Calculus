import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;

public class ParametricArcLengthGraph extends ParametricGraph{

	public ParametricArcLengthGraph( CalculusApplet applet, String f, String g, double a, double b, int n ){
		super();

		this.applet = applet;
		this.a = a;
		this.b = b;
		this.n = n;

		F.parseExpression( f );
		G.parseExpression( g );
	}

	public void draw( Graphics2D g ){
		double delta = (b-a)/n;
		F.addVariable( "t", a );
		double x0 = F.getValue();
		G.addVariable( "t", a );
		double y0 = G.getValue();
		double x1;
		double y1;
		Line2D line;
		if ( newStat ) stat = 0.0;
		g.setColor( Color.red );
		g.setStroke( new BasicStroke(3.0f) );
		for ( int i=0; i<n; i++ ){
			F.addVariable( variable, a + (i+1)*delta );
			x1 = F.getValue();
			G.addVariable( variable, a + (i+1)*delta );
			y1 = G.getValue();
			if ( newStat ) stat += Math.sqrt( (x0-x1)*(x0-x1) + (y0-y1)*(y0-y1) );
			drawLine( g, x0, y0, x1, y1 );
			x0 = x1;
			y0 = y1;
		}
		newStat = false;
		if ( n == 0 ) stat = 0.0;
		applet.setStat( "Length \u2248 ", stat, Color.red );
	}
}