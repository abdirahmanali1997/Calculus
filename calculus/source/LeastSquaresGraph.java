import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;

public class LeastSquaresGraph extends Graph{
	

	static double[] x;
	static double[] y;
	static int I = -1; // current data point
	static int clickI = -1;
	boolean overI;
	boolean newI;
	
	//public LeastSquaresGraph( CalculusApplet applet, double[] x, double[] y, double a, double b, int n ){
	public LeastSquaresGraph( CalculusApplet applet, double a, double b, int n ){
		super();

		this.applet = applet;
		this.a = a;				// a
		this.b = b;				// L
		this.n = n;
		
		x = new double[100];
		for ( int i=0; i<100; i++ ) x[i] = i;
		
		y = new double[100];
		for ( int i=0; i<100; i++ ) y[i] = i/3 + 0.5 + (2*Math.random()-1);
		
		//this.x = x;
		//this.y = y;
		
		Rb = 100;
	}
	
	public void draw( Graphics2D g ){
		String str = applet.choice.getSelectedItem().toString();

		double xx = 0;
		double xy = 0;
		double sx = 0;
		double sy = 0;
		stat = 0;
		
		// sum data
		for ( int i=0; i<n; i++ ){
			sx += x[i];
			sy += y[i];
			xx += x[i]*x[i];
			xy += x[i]*y[i];
			stat += (a*x[i] + b - y[i])*(a*x[i] + b - y[i]);
		}

		// draw least squares line
		double[] P = toCartesianPoint( 0, 0 );
		double[] Q = toCartesianPoint( w, 0 );
		if ( str.equals("Show Least Squares Line") ){
			double lsm = ( xy - sx*sy/n)/(xx - sx*sx/n);
			double lsb = (sy - sx*lsm)/n;
			g.setStroke( dashed );
			drawLine( g, P[0], lsb + lsm*P[0], Q[0], lsb + lsm*Q[0], Color.gray );
		}
		
		// draw data
		for ( int i=0; i<n; i++ ){
			g.setStroke( new BasicStroke(2.0f) );
			drawLine( g, x[i], y[i], x[i], a*x[i] + b, Color.red );
			drawPoint( g, x[i], y[i], Color.blue, false );
		}
		if ( I != -1 ){
			drawPoint( g, x[I], y[I], Color.blue, true );
		} else if ( clickI != -1  && clickI < n ){
			drawPoint( g, x[clickI], y[clickI], Color.blue, true );
		}
		
		// draw line
		g.setStroke( new BasicStroke(3.0f) );
		drawLine( g, P[0], b + a*P[0], Q[0], b + a*Q[0], Color.black );
		
		applet.setStat( "Sum of squares = ", stat, Color.red );
	}
	

	public void drawEndpoints( Graphics2D g ){
		// draw m as an arrow from the point (0,b)
		GeneralPath path = new GeneralPath();
		double[] P = toScreenPoint( 0, b );
		double theta = Math.atan( a );
		g.setStroke( endline );		
		g.draw( new Line2D.Double( W, P[1], W + Rb*Math.cos(theta), P[1] - Rb*Math.sin(theta) ) );
		
		P[0] = W + Rb*Math.cos(theta);
		P[1] -= Rb*Math.sin(theta);
		int A = 6;
		int B = 12;
		path.moveTo( (float)(P[0] - A*Math.sin( theta + Math.PI/4 )), (float)(P[1] - A*Math.cos( theta + Math.PI/4 )) );
		path.lineTo( (float)(P[0] + B*Math.cos( theta )), (float)(P[1] - B*Math.sin( theta )) );
		path.lineTo( (float)(P[0] - A*Math.cos( theta + Math.PI/4 )), (float)(P[1] + A*Math.sin( theta + Math.PI/4 )) );
		path.quadTo( (float)P[0], (float)P[1], (float)(P[0] - A*Math.sin( theta + Math.PI/4 )), (float)(P[1] - A*Math.cos( theta + Math.PI/4 )) );
		g.setColor( Color.green );
		g.fill( path );
		g.setColor( Color.black );
		g.draw( path );
		

		// draw b
		drawPointOnYAxis( g, b, Color.green, overB );
	}
	
	
	public void drawFunction( Graphics2D g ){
	}


	public void keyPressed( KeyEvent ke ){
		int code = ke.getKeyCode();
		if ( code == KeyEvent.VK_SHIFT ){// holding down shift key
			shift = true;
		} else if ( code == KeyEvent.VK_R ){
			radians = !radians;
			newBackground = true;
			repaint();
		}
	}
	
	public void keyReleased( KeyEvent ke ){
		shift = false;
	}

	
	public void mousePressed(MouseEvent me){
		POINT = me.getPoint();

		double[] P = toScreenPoint( 0, b );
		double theta = Math.atan( a );
		P[0] = W + Rb*Math.cos(theta);
		P[1] -= Rb*Math.sin(theta);

		double xb = POINT.y - h/2 + (b - originY)*scale;

		
		if ( (P[0] - POINT.x)*(P[0] - POINT.x) + (P[1] - POINT.y)*(P[1] - POINT.y) < rr ){
			newA = true;
		} else if ( xb*xb + (POINT.x-W)*(POINT.x-W) < rr ){
			newB = true;
		} else {
			I = -1;
			if ( me.getClickCount() == 1 ) clickI = -1;
			newI = false;
			int i=0;
			while ( i<n && I<0 ){
				P = toScreenPoint( x[i],y[i] );
				if ( (P[0] - POINT.x)*(P[0] - POINT.x) + (P[1] - POINT.y)*(P[1] - POINT.y) < rr ){
					I = i;
					if ( me.getClickCount() == 1 ) clickI = I;
					newI = true;
					applet.c.setValue( x[I] );
					applet.d.setValue( y[I] );
				} else {
					i++;
				}
			}
		}
		
		if ( I == -1 && clickI != -1 ){
			applet.c.setValue( x[clickI] );
			applet.d.setValue( y[clickI] );
		}
		requestFocus();
	}

	
	public void mouseDragged(MouseEvent me){
		double[] P = toCartesianPoint( me.getPoint().x, me.getPoint().y );
		double[] Q = toScreenPoint( 0, b );
		if ( newA ){
			a = -(Q[1] - me.getPoint().y)/(W - me.getPoint().x);
			applet.a.setValue( a );
			newStat = true;
			applet.updateGraphs( this );
		} else if ( newB ){
			b = P[1];
			newStat = true;
			applet.b.setValue( b );
		} else if ( newI ){
			if ( shift ) x[I] = P[0];
			y[I] = P[1];
			newStat = true;
			if ( shift ) applet.c.setValue( x[I] );
			applet.d.setValue( y[I] );
		} else {
			originX -= (me.getPoint().x - POINT.x)/scale;
			originY += (me.getPoint().y - POINT.y)/scale;
			POINT = me.getPoint();
			newBackground = true;
		}
		repaint();
	}	
	 
	
	public void mouseMoved( MouseEvent me ){
		mousePressed( me );
		if ( overA ){
			if ( !newA ){
				overA = false;
				repaint();
				setCursor( arrow );
			}
		} else if ( overB ){
			if ( !newB ){
				overB = false;
				repaint();
				setCursor( arrow );
			}
		} else if ( overC ){
			if ( !newC ){
				overC = false;
				repaint();
				setCursor( arrow );
			}
		} else if ( overD ){
			if ( !newD ){
				overD = false;
				repaint();
				setCursor( arrow );
			}
		} else if ( overI ){
			if ( !newI ){
				overI = false;
				repaint();
				setCursor( arrow );
			}
		} else if ( newA ){
			overA = true;
			setCursor( hand );
			repaint();
		} else if ( newB ){
			overB = true;
			setCursor( hand );
			repaint();
		} else if ( newC ){
			overC = true;
			setCursor( hand );
			repaint();
		} else if ( newD ){
			overD = true;
			setCursor( hand );
			repaint();
		} else if ( newI ){
			overI = true;
			setCursor( hand );
			repaint();
		}
		
		mouseReleased( me );
	}	
	
	public void mouseReleased(MouseEvent me){
		newA = false;
		newB = false;
		newC = false;
		newD = false;
		newI = false;
	}	
	
}