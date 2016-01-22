import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;

public class PolarGraph extends Graph{

	public PolarGraph(){
		super();
		variable = "t";
		xlabel = "x(t)";
		ylabel = "y(t)";
	}

	public void drawEndpoints( Graphics2D g ){		
		g.setStroke( endline );
		F.addVariable( "t", b );
		double val = F.getValue();
		drawLine( g, 0, 0, val*Math.cos(b), val*Math.sin(b), Color.black );

		F.addVariable( "t", a );
		val = F.getValue();
		drawLine( g, 0, 0, val*Math.cos(a), val*Math.sin(a), Color.black );

		drawArrow( g, 0, 0, Rb*Math.cos(b)/scale, Rb*Math.sin(b)/scale, Color.green, overB );
		drawArrow( g, 0, 0, Rb*Math.cos(a)/scale, Rb*Math.sin(a)/scale, Color.blue, overA );
}


	public void drawFunction( Graphics2D g ){
		double y;
		int grn;
		
		if ( active ){
			double t = time;
			double s = a;
			for ( int i=0; i<=10; i++ ){
				s = Math.min( b, Math.max( a, t - 1.0 + i/10.0 ) );
				F.addVariable( variable, s );
				y = F.getValue();
				grn = (int)(255*(s-a)/(b-a));
				drawPoint( g, Math.cos(s)*y, Math.sin(s)*y, new Color(0,grn,255-grn,20*i+55), false );
				//drawPoint( g, Math.cos(s)*y, Math.sin(s)*y, new Color(0,0,0,20*i+55), false );
			}
			t = Math.min( b, t );
			grn = (int)(255*(t-a)/(b-a));
			((PolarApplet)applet).rtheta.originX = t;
			((PolarApplet)applet).rtheta.repaint();
			
			F.addVariable( variable, t );
			y = F.getValue();
			g.setStroke( new BasicStroke(2.0f) );
			drawLine( g, Math.cos(s)*y, Math.sin(s)*y, 0, 0, new Color(0,grn,255-grn) );
			drawPoint( g, Math.cos(s)*y, Math.sin(s)*y, new Color(0,grn,255-grn), false );
			//drawPoint( g, Math.cos(s)*y, Math.sin(s)*y, Color.black, false );
		} else {
			GeneralPath path = new GeneralPath();
			F.addVariable( variable, a );
			y = F.getValue();
			double i = a;
			double delta = units[zoom]/(zoom+1)/(zoom+1);
			while ( i < b ){
				while ( i < b && Double.isNaN(y) ){
					i += delta;
					F.addVariable( variable, i );
					y = F.getValue();
				}
				if ( i<b ) path.moveTo( (float)(w/2+(y*Math.cos(i)-originX)*scale), (float)(h/2-(y*Math.sin(i)-originY)*scale) );
				i += delta;
				F.addVariable( variable, i );
				y = F.getValue();
				while ( i < b && !Double.isNaN(y) ){
					path.lineTo( (float)(w/2+(y*Math.cos(i)-originX)*scale), (float)(h/2-(y*Math.sin(i)-originY)*scale) );
					i += delta;
					F.addVariable( variable, i );
					y = F.getValue();
				}
				if ( i >= b ){
					F.addVariable( variable, b );
					y = F.getValue();
					if ( !Double.isNaN(y) ){
						path.lineTo( (float)(w/2+(y*Math.cos(b)-originX)*scale), (float)(h/2-(y*Math.sin(b)-originY)*scale) );
					}
				}
			}
			drawPoint( g, y*Math.cos(b), y*Math.sin(b), Color.black, false );
			g.setColor( Color.black );
			g.setStroke( curve );
			g.draw( path );

			F.addVariable( variable, a );
			y = F.getValue();
			drawPoint( g, y*Math.cos(a), y*Math.sin(a), Color.black, false );
		}
	}


	public void drawGridLines( Graphics2D g ){
		double[] P = toScreenPoint( 0, 0 );		
		double R = Math.max( Math.sqrt(P[0]*P[0]+P[1]*P[1]), Math.sqrt(P[0]*P[0] + (h-P[1])*(h-P[1])) );
		if ( P[0] < w/2 ){
			R = Math.max( Math.sqrt((w-P[0])*(w-P[0])+P[1]*P[1]), Math.sqrt((w-P[0])*(w-P[0]) + (h-P[1])*(h-P[1])) );
		}

		g.setColor( Color.cyan );
		g.setStroke( gridline );
		for ( double A = 0.0; A < R/pixels; A=A+0.2){
			g.draw( new Ellipse2D.Double( P[0]-A*pixels,P[1]-A*pixels,2*A*pixels,2*A*pixels ) );
		}
		for ( double A = 0.0; A < Math.PI; A=A+Math.PI/12.0){
			g.draw( new Line2D.Double( P[0]-R*Math.cos(A),P[1]+R*Math.sin(A),P[0]+R*Math.cos(A),P[1]-R*Math.sin(A) ) );
		}
	}

	public void mousePressed(MouseEvent me){
		POINT = me.getPoint();
		double[] P = toScreenPoint( 0, 0 );
		double xa = POINT.x- (P[0] + Ra*Math.cos(a));
		double ya = POINT.y- (P[1] - Ra*Math.sin(a));
		double xb = POINT.x- (P[0] + Rb*Math.cos(b));
		double yb = POINT.y- (P[1] - Rb*Math.sin(b));
		if ( xa*xa + ya*ya < rr ){
			newA = true;
		} else if ( xb*xb + yb*yb < rr ){
			newB = true;
		}
		requestFocus();
	}

	public void mouseDragged(MouseEvent me){
		Point p = me.getPoint();
		double[] P = toScreenPoint( 0, 0 );
		if ( newA ){
			a += getAngle( Ra*Math.cos(a), Ra*Math.sin(a), p.x - P[0], P[1] - p.y );
			a = Math.min(a,b);
			applet.a.setValue( a );
			newStat = true;
			newBackground = true;
			applet.stat.setText( "Calculating..." );
			applet.updateGraphs( this );
		} else if ( newB ){
			b += getAngle( Rb*Math.cos(b), Rb*Math.sin(b), p.x - P[0], P[1] - p.y );
			b = Math.max(a,b);
			applet.b.setValue( b );
			newStat = true;
			newBackground = true;
			applet.stat.setText( "Calculating..." );
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