import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;

public class ParametricAreaGraph extends ParametricGraph{

	public ParametricAreaGraph( CalculusApplet applet, String f, String g, double a, double b, int n ){
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
		double x1;
		double y;
		Rectangle2D rect;
		double[] P;
		if ( newStat ) stat = 0.0;
		g.setColor( red );
		for ( int i=0; i<n; i++ ){
			F.addVariable( "t", a + (i+1)*delta );
			x1 = F.getValue();
			G.addVariable( "t", a + (i+0.5)*delta );
			y = G.getValue();
			if ( newStat ) stat += y*(x1-x0);
			y *= scale;
			P = toScreenPoint( Math.min(x0,x1), 0 );
			rect = new Rectangle2D.Double( P[0], P[1]-(y>0?y:0), Math.abs(x1-x0)*scale, Math.abs(y) );
			g.fill( rect );
			x0 = x1;
		}
		newStat = false;
		if ( n == 0 ) stat = 0.0;
		applet.setStat( "Area \u2248 ", stat, Color.red );
	}
}