import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.*;
//import java.math.*;

public class TangentCirclesGraph extends Graph{

	
	public TangentCirclesGraph( CalculusApplet applet, String f, double a, double b, double c ){
		super();

		this.applet = applet;
		this.a = a;
		this.b = b;
		this.c = c;

		F.parseExpression( f );
	}
	
	
	public void draw( Graphics2D g ){
		F.addVariable( variable, a );
		double ya = F.getValue();
		F.addVariable( variable, b );
		double yb = F.getValue();
		F.addVariable( variable, c );
		double yc = F.getValue();

		// draw secant circle
		double s = ((a-c)*(b-c) + (ya-yc)*(yb-yc))/((a-c)*(ya-yb)-(a-b)*(ya-yc));
		double x = (a + b - (ya-yb)*s)/2.0;     // x coordinate of center of circle
		double y = (ya + yb + (a-b)*s)/2.0;		// y coordinate of center of circle
		double r = (x-b)*(x-b) + (y-yb)*(y-yb); // square of the radius of circle

/*
BigDecimal A = new BigDecimal( ""+a );
BigDecimal B = new BigDecimal( ""+b );
BigDecimal C = new BigDecimal( ""+c );
BigDecimal YA = new BigDecimal( ""+ya );
BigDecimal YB = new BigDecimal( ""+yb );
BigDecimal YC = new BigDecimal( ""+yc );
BigDecimal S = (A.subtract(C)).multiply(B.subtract(C));
S = S.add((YA.subtract(YC)).multiply(YB.subtract(YC)));
BigDecimal T =  (A.subtract(C)).multiply(YA.subtract(YB));
T = T.subtract((A.subtract(B)).multiply(YA.subtract(YC)));
S = S.divide(T,BigDecimal.ROUND_UP);
BigDecimal X = (A.add(B)).add(S.multiply(YB.subtract(YA)));
X = X.multiply( new BigDecimal("0.5") );
BigDecimal Y = (YA.add(YB)).add(S.multiply(A.subtract(B)));
Y = Y.multiply( new BigDecimal("0.5") );
BigDecimal R = X.subtract(A).multiply(X.subtract(A)).add(Y.subtract(YA).multiply(Y.subtract(YA)));
double r = Math.sqrt(R.doubleValue());
*/
/*
F.addVariable( "x", a );
double ya = F.getValue();
F.addVariable( "x", a+b );
double yp = F.getValue();
F.addVariable( "x", a-b );
double ym = F.getValue();

double m = (ya-ym)/b;
double n = (yp-ya)/b;
double l = (yp-ym)/b/2.0;
double x = a + b*l*(1+m*n)/(m-n);
double y = ya - b*(m*m+n*n+2)/(m-n)/2.0;
double r = Math.sqrt((1+l*l)*(1+m*m)*(1+n*n))/Math.abs((m-n)/b);
*/

		g.setStroke( new BasicStroke(3.0f) );
		if ( a == b || a == c || b == c ){
			applet.stat.setText( "" );
		} else {
			// built in Ellips2D.Double is not so accurate with large values
			G.parseExpression( y + " + sqrt(" + r + "- (x-" + x + ")^2 )" );
			drawFunction( g, G, Color.red );
			G.parseExpression( y + " - sqrt(" + r + "- (x-" + x + ")^2 )" );
			drawFunction( g, G, Color.red );

			//g.draw( new Ellipse2D.Double( w/2 + (x - originX - r)*scale, h/2 - (y - originY + r)*scale, 2.0*r*scale, 2.0*r*scale ) );
			applet.setStat( "Curvature \u2248 ", 1/Math.sqrt(r), Color.red );
		}
	}

	
	public void drawEndpoints( Graphics2D g ){
		F.addVariable( variable, c );
		drawPoint( g, c, F.getValue(), Color.magenta, overC );

		F.addVariable( variable, b );
		drawPoint( g, b, F.getValue(), Color.green, overB );

		F.addVariable( variable, a );
		drawPoint( g, a, F.getValue(), Color.blue, overA );
	}

	
	public void mousePressed(MouseEvent me){
		POINT = me.getPoint();
		double xa = POINT.x - w/2 - (a - originX)*scale;
		double xb = POINT.x - w/2 - (b - originX)*scale;
		double xc = POINT.x - w/2 - (c - originX)*scale;
		
		F.addVariable( variable, a );
		double ya = POINT.y - h/2 + (F.getValue() - originY)*scale;
		F.addVariable( variable, b );
		double yb = POINT.y - h/2 + (F.getValue() - originY)*scale;
		F.addVariable( variable, c );
		double yc = POINT.y - h/2 + (F.getValue() - originY)*scale;
		
		if ( xa*xa + ya*ya < rr ){
			newA = true;
		} else if ( xb*xb + yb*yb < rr ) {
			newB = true;
		} else if ( xc*xc + yc*yc < rr ) {
			newC = true;
		}
		
		requestFocus();
	}
}