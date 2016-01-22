import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;

import org.nfunk.jep.*;

public class LinearizationGraph extends Graph{

	double fa;
	double fb;

	public LinearizationGraph( CalculusApplet applet, String f, double a, double b ){
		super();

		this.applet = applet;
		this.a = a;
		this.b = b;
		newStat = true;		

		F.parseExpression( f );

		dList = new DerivativeList( F.getTopNode() ); 
	}

	public void draw( Graphics2D g ){
		
		if ( newStat ){
			newStat= false;
		}

		g.setStroke( curve );
		F.addVariable( "x", b );
		fb = F.getValue();
		F.addVariable( "x", a );
		fa = F.getValue();
		double m;
		double[] P = toCartesianPoint( 0, 0 );
		double[] Q = toCartesianPoint( w, 0 );
		try{
			m = Double.parseDouble( F.evaluate( F.differentiate( F.getTopNode(), "x" ) ).toString() );
			stat = fa + m*(b-a);
			drawLine( g, P[0], fa + m*(P[0] - a), Q[0], fa + m*(Q[0] - a), Color.red );
			if ( applet.choice.getSelectedItem().equals("Show Differentials") ){
				g.setStroke( new BasicStroke(1.5f) );
				g.setColor( Color.darkGray );
				drawLine( g, a, fa, b, fa );
				drawLine( g, b, fa, b, stat );
				drawLine( g, b, fa, b, fb );
				
				double xa = w/2 + (a - originX)*scale;
				double xb = w/2 + (b - originX)*scale;
				double Fa = h/2 - (fa - originY)*scale;
				double Fb = h/2 - (fb - originY)*scale;
				double Fstat = h/2 - (stat - originY)*scale;

				String s = "\u0394x = dx";
				int sign = 1;
				if ( Fb > Fa ) sign = -1;
				int d = g.getFontMetrics().stringWidth(s)/2;
				int e = g.getFontMetrics().getHeight()/3;
				if ( Math.abs( xa - xb ) > 2*d + 4 ){
					g.drawString(s,(float)((xa+xb)/2)-d,(float)(Fa+sign*15-(sign-1)*e) );
					g.draw( new Line2D.Double( Math.min(xa,xb), Fa + sign*8, (xa+xb)/2 - (d+2) , Fa + sign*8 ) );
					g.draw( new Line2D.Double( Math.max(xa,xb), Fa + sign*8, (xa+xb)/2 + (d+2) , Fa + sign*8 ) );
				} else {
					g.draw( new Line2D.Double( xa, Fa + sign*8, xb , Fa + sign*8 ) );
				}
				g.draw( new Line2D.Double( xa, Fa + sign*12, xa, Fa + sign*4 ) );
				g.draw( new Line2D.Double( xb, Fa + sign*12, xb, Fa + sign*4 ) );
				
				
				sign = 1;
				if ( b < a ) sign = -1;
				s = "\u0394y";
				d = g.getFontMetrics().stringWidth(s)/2;
				if ( Math.abs( Fa - Fb ) > 3*e + 4 ){
					g.drawString(s,(float)(xb+sign*5+(sign-1)*d),(float)((Fa+Fb)/2)+e );
					g.draw( new Line2D.Double( xb+sign*5+sign*d, Math.max(Fa,Fb), xb+sign*5+sign*d, (Fa+Fb)/2+(e+2) ) );
					g.draw( new Line2D.Double( xb+sign*5+sign*d, Math.min(Fa,Fb), xb+sign*5+sign*d, (Fa+Fb)/2-(e+2) ) );
				} else {
					g.draw( new Line2D.Double( xb+sign*5+sign*d, Fa, xb+sign*5+sign*d, Fb ) );
				}
				g.draw( new Line2D.Double( xb+sign*5+sign*d-4, Fa, xb+sign*5+sign*d+4, Fa) );
				g.draw( new Line2D.Double( xb+sign*5+sign*d-4, Fb, xb+sign*5+sign*d+4, Fb) );

				s = "dy";
				d = g.getFontMetrics().stringWidth(s)/2;
				if ( Math.abs( Fa - Fstat ) > 3*e + 4 ){
					g.drawString(s,(float)(xb+sign*25+(sign-1)*d),(float)((Fa+Fstat)/2)+e );
					g.draw( new Line2D.Double( xb+sign*25+sign*d, Math.max(Fa,Fstat), xb+sign*25+sign*d, (Fa+Fstat)/2+(e+2)) );
					g.draw( new Line2D.Double( xb+sign*25+sign*d, Math.min(Fa,Fstat), xb+sign*25+sign*d, (Fa+Fstat)/2-(e+2) ) );
				} else {
					g.draw( new Line2D.Double( xb+sign*25+sign*d, Fa, xb+sign*25+sign*d, Fstat ) );
				}
				g.draw( new Line2D.Double( xb+sign*25+sign*d-4, Fa, xb+sign*25+sign*d+4, Fa) );
				g.draw( new Line2D.Double( xb+sign*25+sign*d-4, Fstat, xb+sign*25+sign*d+4, Fstat) );
			}
		} catch( ParseException e ){
		} catch( Exception e ){
		}

		applet.setStat( "L(b) \u2248 ", stat, Color.red );
	}
	
	public void drawEndpoints( Graphics2D g ){
		F.addVariable( "x", b );
		drawPoint( g, b, F.getValue(), Color.black, false );

		drawPoint( g, b, stat, Color.green, overB );

		F.addVariable( "x", a );
		drawPoint( g, a, F.getValue(), Color.red, overA );
		
		if ( applet.choice.getSelectedItem().equals("Show Differentials") ){
			String str = "dy = L(b) - f(a) = f'(a)dx \u2248 " + (float)(stat-fa);
			g.setColor( new Color(255,255,255,150) );
			g.fill( new Rectangle2D.Double( 0, 0, g.getFontMetrics().stringWidth( str )+10, 50) );
			g.setColor( Color.darkGray );
			g.drawString( "\u0394x = dx = b - a \u2248 " + (float)(b-a), 5, 15 );
			g.drawString( "\u0394y = f(b) - f(a) \u2248 " + (float)(fb-fa), 5, 30 );
			g.drawString( str, 5, 45 );
		}
	}


	public void mousePressed(MouseEvent me){
		POINT = me.getPoint();
		double xa = POINT.x - w/2 - (a - originX)*scale;
		double xb = POINT.x - w/2 - (b - originX)*scale;
		F.addVariable( "x", a );
		double ya = POINT.y - h/2 + (F.getValue() - originY)*scale;
		double yb = POINT.y - h/2 + (stat - originY)*scale;
		if ( xa*xa + ya*ya < rr ){
			newA = true;
		} else if ( xb*xb + yb*yb < rr ) {
			newB = true;
		}
		requestFocus();
	}
}