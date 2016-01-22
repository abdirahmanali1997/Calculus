import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.util.*;


class Projectile{
	
	long start;
	double a;	// initial height
	double vx;	// initial velocity (x direction)
	double vy;	// initial velocity (y direction)
	double t;	// time in the air
	
	Projectile prev;
	Projectile next;
	
	Projectile( double a, double vx, double vy ){
		start = Calendar.getInstance().getTimeInMillis();
		this.a = a;
		this.vx = vx;
		this.vy = vy;
		t = (vy + Math.sqrt(vy*vy+64*a))/32.0;
	}
	
}

public class ParabolicMotionGraph extends Graph implements Runnable{

	private int LAUNCH_COUNT = 0;
	
	private Projectile FIRST_PROJECTILE;
	
	Thread thread;

	public ParabolicMotionGraph( CalculusApplet applet, double a, double b, double c ){
		super();

		this.applet = applet;
		this.a = a;
		this.b = b;
		this.c = c;
	}

	
	public void draw( Graphics2D g ){
	}

	
	public void drawEndpoints( Graphics2D g ){	
		drawLine( g, 0, a, Rb*Math.cos(c)/scale, a + Rb*Math.sin(c)/scale, Color.black );
		drawArrow( g, 0, a, Rb*Math.cos(c)/scale, a + Rb*Math.sin(c)/scale, Color.blue, overC );
		drawPoint( g, 0, a, Color.red, overA );
	}
	
	
	public void drawFunction( Graphics2D g ){
		double x = 0;
		double y = a;
		double vx = b*Math.cos(c);
		double vy = b*Math.sin(c);	
		double t;
		double m2;
		double[] tmp;
			
		GeneralPath path = new GeneralPath();
		path.moveTo( (float)(w/2+(x-originX)*scale), (float)(h/2-(y-originY)*scale) );
		t = (vy + Math.sqrt(vy*vy+64*a))/32.0;
		m2 = vy/vx - 32*t/vx;
		tmp = getIntersection( vy/vx, a, m2, -1*m2*vx*t);
		x = vx*t;
		y = 0;
		if ( tmp != null && !(vy<0 && a <0) ){
			path.quadTo( (float)(w/2+(tmp[0]-originX)*scale), (float)(h/2-(tmp[1]-originY)*scale), (float)(w/2+(x-originX)*scale), (float)(h/2-(y-originY)*scale) );
			g.setColor( Color.lightGray );
			g.setStroke( curve );
			g.draw( path );
		}
			
		if ( active ){
			Projectile p = FIRST_PROJECTILE;
			while ( p != null ){
				t = Math.min( p.t, (Calendar.getInstance().getTimeInMillis() - p.start)/1000.0 );
				x = p.vx*t;
				y = a + p.vy*t - 16*t*t;
				drawPoint( g, x, y, Color.red, false );
				
				if ( t >= p.t ){
					if ( p.prev != null && p.next != null){
						p.prev.next = p.next;
						p.next.prev = p.prev;
					} else if ( p.prev != null && p.next == null ) {
						p.prev.next = null;
					} else if ( p.prev == null && p.next != null ) {
						p.next.prev = null;
						FIRST_PROJECTILE = p.next;
					} else  {
						FIRST_PROJECTILE = null;
					}
					LAUNCH_COUNT--;
				}
				
				p = p.next;
			}
		}	
			
		t = (vy + Math.sqrt(vy*vy+64*a))/32.0;
		x = vx*t;
		if ( !Double.isNaN(t) && t>0 ){
			double xa = w/2 - originX*scale;
			double xb = w/2 + (x - originX)*scale;
			y = h/2 + originY*scale + 0.4*pixels;
			float hh = (float)(H+1)+g.getFontMetrics().getHeight();
			//double Fa = h/2 - (fa - originY)*scale;
			//double Fb = h/2 - (fb - originY)*scale;
			//double Fstat = h/2 - (stat - originY)*scale;

			// label horizontal distance
			String s = ""+(float)x;
			int d = g.getFontMetrics().stringWidth(s)/2;
			int e = g.getFontMetrics().getHeight()/3;
			g.setStroke( new BasicStroke(1.0f) );
			g.setColor( Color.black );
			g.draw( new Line2D.Double(xb, H+2, xb, H-2) );
			g.setColor( new Color(255,255,255,200) );
			g.fill( new Rectangle2D.Double( xb-d-10, hh-10, 2*d+20, 12) );
			g.setColor( Color.black );
			g.drawString(s,(float)(xb-d), hh );
			
			// label maximum height
			
			// label total distance
			
			// label time
			
			//applet.stat.setText( "Horizontal Distance: " + x );
		}
		applet.stat.setText( "" );
	}

	
    public void launch(){
		double vx = b*Math.cos(c);
		double vy = b*Math.sin(c);
		double t = vy + Math.sqrt(vy*vy+64*a);

		if ( !Double.isNaN( t ) && t > 0 ){
			LAUNCH_COUNT++;
			if ( FIRST_PROJECTILE == null ){
				FIRST_PROJECTILE = new Projectile(a,vx,vy);
			} else {
				FIRST_PROJECTILE.prev = new Projectile(a,vx,vy);
				FIRST_PROJECTILE.prev.next = FIRST_PROJECTILE;
				FIRST_PROJECTILE = FIRST_PROJECTILE.prev;
			}
			if ( !active ){
				active = true;
				thread = new Thread(this);
				thread.start();
			}
		}
    }

    
	public void run(){
		while ( LAUNCH_COUNT>0 ){
			repaint();
			try {
				Thread.sleep(50);
			} catch (InterruptedException e){
			}
		}
		repaint();
		active = false;
		newBackground = true;
		repaint();
    }

	
	public void drawVerticalLines( Graphics2D g ){
	}


	public void mousePressed(MouseEvent me){
		POINT = me.getPoint();
		double[] P = toScreenPoint( 0, a );
		double xa = POINT.x- P[0];
		double ya = POINT.y- P[1];
		double xc = POINT.x- (P[0] + Rb*Math.cos(c));
		double yc = POINT.y- (P[1] - Rb*Math.sin(c));
		if ( xa*xa + ya*ya < rr ){
			newA = true;
		} else if ( xc*xc + yc*yc < rr ){
			newC = true;
		}
		requestFocus();
	}

	public void mouseDragged(MouseEvent me){
		Point p = me.getPoint();
		double[] P = toScreenPoint( 0, a );
		double[] Q = toCartesianPoint( p.x, p.y );
		if ( newA ){
			a = Q[1];
			applet.a.setValue( a );
			newStat = true;
			newBackground = true;
			applet.updateGraphs( this );
		} else if ( newC ){
			c += getAngle( Rb*Math.cos(c), Rb*Math.sin(c), p.x - P[0], P[1] - p.y );
			applet.c.setValue( c*applet.D );
			newStat = true;
			newBackground = true;
			applet.updateGraphs( this );
		} else {
			originX -= (me.getPoint().x - POINT.x)/scale;
			originY += (me.getPoint().y - POINT.y)/scale;
			POINT = me.getPoint();
			newBackground = true;
		}
		repaint();
	}	
}