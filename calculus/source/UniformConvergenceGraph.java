import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;

import org.nfunk.jep.*;
import org.lsmp.djep.djep.*;
import org.lsmp.djep.xjep.*;

public class UniformConvergenceGraph extends Graph{

	public UniformConvergenceGraph( CalculusApplet applet, String f, String g, double a, double b, double c, int n ){
		super();

		this.applet = applet;
		this.a = a;				// a
		this.b = b;				// b
		this.c = c;				// epsilon
		this.n = n;

		F.parseExpression( f );
		G.parseExpression( g );
	}
	
	public void draw( Graphics2D g ){		
		// draw shaded region
		drawShadedRegions( g, G, Color.black );
		//g.setColor( new Color(0,0,255,100) );
		//g.fill( new Rectangle2D.Double( w/2 + (Math.min(a,b) - originX)*scale,0, Math.abs(a-b)*scale, h ) );
		
		// draw f_n(x)
		F.addVariable( "n", n );
		drawFunction( g, F, Color.red );

		// draw remaining lines		
		double[] P = toScreenPoint( a, 0 );
		g.setColor( Color.black );
		g.setStroke( new BasicStroke(0.66f) );		
		g.draw( new Line2D.Double( P[0], 0, P[0], h) );
		P = toScreenPoint( b, 0 );
		g.draw( new Line2D.Double( P[0], 0, P[0], h) );

		
		
		applet.stat.setText( "" );
	}
	

	public void drawEndpoints( Graphics2D g ){
		drawPointOnXAxis( g, a, Color.blue, overA );
		drawPointOnXAxis( g, b, Color.green, overB );
		drawPointOnYAxis( g, c, Color.red, overC );
	}


	public void drawFunction( Graphics2D g ){
	}


	public void drawShadedRegions( Graphics2D g, DJep F, Color color ){
		GeneralPath path = new GeneralPath();
		g.setStroke( new BasicStroke(1.0f) );		
		double i = 0.0;
		double j;
		double k;
		F.addVariable( variable, originX-w/2/scale );
		double y = F.getValue();
		double oldy = y;
		while ( i < w ){
			// find first point that is in viewable portion of xy-plane
			while ( i<=w && (Double.isNaN(y=F.getValue()) || Math.abs(y-originY)>h/scale + 2*c) ){
			//while ( i<=w && Double.isNaN(y=F.getValue()) ){
				if (originX + (i-w/2)/scale>Math.min(a,b) && originX + (i-w/2)/scale<Math.max(a,b) ){
					g.setColor( new Color(0,0,255,100) );
					g.draw( new Line2D.Double( i, 0, i, h ) );
				}
				F.addVariable( variable, originX + (++i-w/2)/scale );
			}
			//find a slightly better place to moveto
			oldy = y;
			k = i - 1.0;
			j = i;
			for ( int l=0; l<20; l++ ){
				F.addVariable( variable, originX + ((k + j)/2 - w/2)/scale );
				if ( Double.isNaN(F.getValue()) ){
					k = (k + j)/2;
				} else {
					oldy = F.getValue();
					j = (k + j)/2;
				}
			}
			path.moveTo( (float)j, (float)(h/2 - (oldy-originY)*scale) );
			path.lineTo( (float)i, (float)(h/2 - (y-originY)*scale) );

			// keep going until off the screen or not in domain
			F.addVariable( variable, originX + (++i-w/2)/scale );
			while ( i<=w && Math.abs(y-originY)<h/scale + 2*c && !Double.isNaN(y=F.getValue()) ){
				path.lineTo( (float)i, (float)(h/2 - (y-originY)*scale) );

				g.setColor( new Color(255,255,0,100) );  // yellow
				g.draw( new Line2D.Double( i, h/2 - (y-c-originY)*scale, i, h/2 - (y+c-originY)*scale ) );
				if (originX + (i-w/2)/scale>Math.min(a,b) && originX + (i-w/2)/scale<Math.max(a,b) ){
					g.setColor( new Color(0,0,255,100) );  // blue
					g.draw( new Line2D.Double( i, 0, i, h ) );
					g.setColor( new Color(0,255,0,100) );  // green
					g.draw( new Line2D.Double( i, h/2 - (y-c-originY)*scale, i, h/2 - (y+c-originY)*scale ) );
				}
				g.setColor( Color.black );
				g.setStroke( new BasicStroke(0.66f) );		
				g.draw( new Line2D.Double( i-1, h/2 - (oldy+c-originY)*scale, i, h/2 - (y+c-originY)*scale + 0.5 ) );
				g.draw( new Line2D.Double( i-1, h/2 - (oldy-c-originY)*scale, i, h/2 - (y-c-originY)*scale + 0.5 ) );
				g.setStroke( new BasicStroke(1.0f) );		

				oldy = y;
				F.addVariable( variable, originX + (++i-w/2)/scale );
			}
			// backup a little bit
			k = i;
			j = i - 1.0;
			for ( int l=0; l<20; l++ ){
				F.addVariable( variable, originX + ((k + j)/2 - w/2)/scale );
				if ( Double.isNaN(F.getValue()) ){
					k = (k + j)/2;
				} else {
					oldy = F.getValue();
					j = (k + j)/2;
				}
			}
			path.lineTo( (float)j, (float)(h/2 - (oldy-originY)*scale) );
		}
		g.setColor( color );
		g.setStroke( curve );
		g.draw( path );
	}


	public void mousePressed(MouseEvent me){
		POINT = me.getPoint();
		double xa = POINT.x - w/2 - (a - originX)*scale;
		double xb = POINT.x - w/2 - (b - originX)*scale;
		double xc = POINT.y - h/2 + (c - originY)*scale;
		if ( xc*xc + (POINT.x-W)*(POINT.x-W) < rr ){
			newC = true;
		} else if ( xa*xa + (POINT.y-H)*(POINT.y-H) < rr ){
			newA = true;
		} else if ( xb*xb + (POINT.y-H)*(POINT.y-H) < rr ){
			newB = true;
		}
		requestFocus();
	}

	public void mouseDragged(MouseEvent me){
		double[] P = toCartesianPoint( me.getPoint().x, me.getPoint().y );
		if ( newA ){
			a = P[0];
			applet.a.setValue( a );
		} else if ( newB ){
			b = P[0];
			applet.b.setValue( b );
		} else if ( newC ){
			c = Math.max( 0, P[1] );
			applet.c.setValue( c );
		} else {
			originX -= (me.getPoint().x - POINT.x)/scale;
			originY += (me.getPoint().y - POINT.y)/scale;
			POINT = me.getPoint();
			newBackground = true;
		}
		repaint();
	}	
}