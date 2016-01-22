 import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;

public class AreaBetweenCurvesGraph extends Graph{

	
	public AreaBetweenCurvesGraph( CalculusApplet applet, String f, String g, double a, double b, int n ){
		super();

		this.applet = applet;
		this.a = a;
		this.b = b;
		this.n = Math.max( n,0 );
		
		F.parseExpression( f );
		G.parseExpression( g );
	}


	public void draw( Graphics2D g ){
		draw( g, 0.5 );
	}


	public void draw( Graphics2D g, double shift ){
		double delta = Math.abs(b-a)/n;
		double min = Math.min( a, b );
		double y;
		double[] P;
		Rectangle2D rect;
		
		if ( newStat ) stat = 0.0;
		
		g.setColor( red );
		if ( b < a ) g.setColor( yellow );
		for ( int i=0; i<n; i++ ){
			F.addVariable( "x", min + (i+shift)*delta );
			G.addVariable( "x", min + (i+shift)*delta );
			y = Math.abs( F.getValue() - G.getValue() );
			if ( newStat ) stat += y;
			y *= scale;
			P = toScreenPoint(min+i*delta,Math.max(F.getValue(),G.getValue()));
			rect = new Rectangle2D.Double( P[0], P[1], delta*scale, Math.abs(y) );
			g.fill( rect );
		}
		
		if ( newStat ){
			stat *= delta;
			if ( b < a ) stat *= -1.0;
		}
		
		if ( n == 0 ) stat = 0.0;
		if ( stat < 0 ) applet.setStat( "Area \u2248 ", stat, Color.cyan );
		else applet.setStat( "Area \u2248 ", stat, Color.red );
		newStat = false;
	}


	public void drawEndpoints( Graphics2D g ){
		g.setStroke( endline );
		F.addVariable( variable, b );
		G.addVariable( variable, b );
		drawLine( g, b, 0, b, F.getValue(), colorB );
		drawLine( g, b, 0, b, G.getValue(), colorB );

		F.addVariable( variable, a );
		G.addVariable( variable, a );
		drawLine( g, a, 0, a, F.getValue(), colorA );
		drawLine( g, a, 0, a, G.getValue(), colorA );

		drawPointOnXAxis( g, b, Color.green, overB );
		drawPointOnXAxis( g, a, Color.blue, overA );
	}
		

	public void drawFunction( Graphics2D g ){
		drawFunction( g, G, Color.black );
		drawFunction( g, F, Color.black );
	}
}