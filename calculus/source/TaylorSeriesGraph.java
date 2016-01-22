import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;

import org.nfunk.jep.*;

public class TaylorSeriesGraph extends Graph{

	public TaylorSeriesGraph( CalculusApplet applet, String f, double a, double b, int n ){
		super();

		this.applet = applet;
		this.a = a;
		this.b = b;
		this.n = Math.max( n, 0 );
		newStat = true;		

		F.parseExpression( f );

		dList = new DerivativeList( F.getTopNode() ); 
	}

	public void draw( Graphics2D g ){
		F.addVariable( "x", a );
		DerivativeList tmp = dList;

		double fac = 1.0;
		if ( newStat ){
			coef = new double[n+1];
			try {
				coef[0] = Double.parseDouble( F.evaluate( tmp.f ).toString() );
			} catch( ParseException e ){
			} catch( Exception e ){
			}

			for ( int i=1; i<n+1; i++ ){
				try {
					if ( tmp.next == null ){
						tmp.next = new DerivativeList( F.simplify( F.differentiate( tmp.f, "x" ) ) );
					}
					tmp = tmp.next;
					fac *= i;
					coef[i] = Double.parseDouble( F.evaluate( tmp.f ).toString() )/fac;
				} catch( ParseException e ){
				} catch( Exception e ){
				}
			}
			newStat= false;
		}
/*
		// replace
			for ( int i=1; i<n+1; i++ ){
				fac *= i;
				y1 += coef[i]*Math.pow((x+1.0-originX*w)*scale - a,i)/fac;
			}
		// with
			taylor.addVariable("x"
			y1 = taylor.getValue();
*/
		double y0;
		double y1;
		double r0;
		double r1;
		double xx = originX - w/2/scale - a;
		y0 = 0.0;
		for ( int i=n; i>-1; i-- ){
			y0 = coef[i] + y0*xx; // Horner's Rule
		}
		F.addVariable( "x", originX - w/2/scale );
		r0 = h/2 - (F.getValue() - y0 - originY)*scale;
		y0 = h/2 - (y0 - originY)*scale;

		g.setStroke( curve );
		if ( applet.choice.getSelectedItem().equals("Show Taylor Polynomial Only") ){
			g.setColor( Color.red );
			for ( double x = 0.0; x<w; x++ ){
				y1 = 0.0;
				xx = originX + (x+1-w/2)/scale - a;
				for ( int i=n; i>-1; i-- ){
					y1 = coef[i] + y1*xx; // Horner's Rule
				}
				y1 = h/2 - (y1 - originY)*scale;
				g.draw( new Line2D.Double(x,y0,x+1,y1) );
				y0 = y1;
			}
		} else if ( applet.choice.getSelectedItem().equals("Show Remainder Only") ){
			g.setColor( Color.blue );
			for ( double x = 0.0; x<w; x++ ){
				y1 = 0.0;
				xx = originX + (x+1-w/2)/scale - a;
				for ( int i=n; i>-1; i-- ){
					y1 = coef[i] + y1*xx; // Horner's Rule
				}
				F.addVariable( "x", originX + (x+1-w/2)/scale );
				r1 = h/2 - (F.getValue() - y1 - originY)*scale;
				g.draw( new Line2D.Double(x,r0,x+1,r1) );
				r0 = r1;
			}
		} else {
			for ( double x = 0.0; x<w; x++ ){
				y1 = 0.0;
				xx = originX + (x+1-w/2)/scale - a;
				for ( int i=n; i>-1; i-- ){
					y1 = coef[i] + y1*xx; // Horner's Rule
				}
				F.addVariable( "x", originX + (x+1-w/2)/scale );
				r1 = h/2 - (F.getValue() - y1 - originY)*scale;
				g.setColor( Color.blue );
				g.draw( new Line2D.Double(x,r0,x+1,r1) );
				r0 = r1;
				g.setColor( Color.red );
				y1 = h/2 - (y1 - originY)*scale;
				g.draw( new Line2D.Double(x,y0,x+1,y1) );
				y0 = y1;
			}
		}

		stat = 0.0;
		xx = b - a;
		for ( int i=n; i>-1; i-- ){
			stat = coef[i] + stat*xx; // Horner's Rule
		}
		
		String sub = "";
		int o = n % 10;
		int t = (n - o)/10;

		if ( t == 1 ) sub = "\u2081";
		else if ( t == 2 ) sub = "\u2082";
		else if ( t == 3 ) sub = "\u2083";
		else if ( t == 4 ) sub = "\u2084";
		else if ( t == 5 ) sub = "\u2085";
		else if ( t == 6 ) sub = "\u2086";
		else if ( t == 7 ) sub = "\u2087";
		else if ( t == 8 ) sub = "\u2088";
		else if ( t == 9 ) sub = "\u2089";

		if ( o == 0 ) sub += "\u2080";
		else if ( o == 1 ) sub += "\u2081";
		else if ( o == 2 ) sub += "\u2082";
		else if ( o == 3 ) sub += "\u2083";
		else if ( o == 4 ) sub += "\u2084";
		else if ( o == 5 ) sub += "\u2085";
		else if ( o == 6 ) sub += "\u2086";
		else if ( o == 7 ) sub += "\u2087";
		else if ( o == 8 ) sub += "\u2088";
		else if ( o == 9 ) sub += "\u2089";
		
		if ( applet.choice.getSelectedItem().equals("Show Remainder Only") ){
			F.addVariable( "x", b );
			applet.setStat( "R"+ sub + "(b) \u2248 ", F.getValue() - stat, Color.blue );
		} else {
			applet.setStat( "T"+ sub + "(b) \u2248 ", stat, Color.red );
		}
	}
	
	public void drawEndpoints( Graphics2D g ){
		if ( applet.choice.getSelectedItem().equals("Show Remainder Only") ){
			F.addVariable( "x", b );
			drawPoint( g, b, F.getValue() - stat, Color.green, overB );
		} else {
			drawPoint( g, b, stat, Color.green, overB );
		}
		F.addVariable( "x", a );
		drawPoint( g, a, F.getValue(), Color.red, overA );
	}


	public void mousePressed(MouseEvent me){
		POINT = me.getPoint();
		double xa = POINT.x - w/2 - (a - originX)*scale;
		double xb = POINT.x - w/2 - (b - originX)*scale;
		F.addVariable( "x", a );
		double ya = POINT.y - h/2 + (F.getValue() - originY)*scale;
		double yb = POINT.y - h/2 + (stat - originY)*scale;
		if ( applet.choice.getSelectedItem().equals("Show Remainder Only") ){
			F.addVariable( "x", b );
			yb = POINT.y - h/2 + (F.getValue() - stat - originY)*scale;
		}
		if ( xa*xa + ya*ya < rr ){
			newA = true;
		} else if ( xb*xb + yb*yb < rr ) {
			newB = true;
		}
		requestFocus();
	}
}