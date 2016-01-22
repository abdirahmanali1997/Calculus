import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.*;

public class VolumeViaShellsGraph extends Graph3D{
	
	//double[] topLeft = new double[2];
	double[] topRight = new double[2];
	double[] bottomLeft = new double[2];
	//double[] bottomRight = new double[2];
	
	GeneralPath topTop = new GeneralPath();
	GeneralPath topBottom = new GeneralPath();
	GeneralPath bottomTop = new GeneralPath();
	GeneralPath bottomBottom = new GeneralPath();
	

	public VolumeViaShellsGraph( CalculusApplet applet, String f, String g, double a, double b, double c, int n ){
		super();

		this.applet = applet;
		this.a = a;						// lower limit of integration
		this.b = b;						// upper limit of integration
		this.c = c;						// axis of rotation
		this.n = Math.max( n,0 );		// number of cross-sections

		F.parseExpression( f );			// boundary curve
		G.parseExpression( g );			// boundary curve
	}

	
	public void draw( Graphics2D g ){
		delta = Math.abs(b-a)/n;
		if ( newStat ) stat = 0.0;
		g.setStroke( hairline );
		
		// eye focused on middle of panel
		int dir = 1;
		double A = Math.min( a, b );
		if ( c < Math.min(a,b) ){
			dir = -1;
			A = Math.max(a,b);
		} else {
			if ( c < (a+b)/2 ){
				dir = -1;
				A = Math.max( a,b );
			}
		}
		A += dir*delta/2.0;
		
		//double A = Math.max(Math.abs(a-c),Math.abs(b-c)) - delta/2.0;
		if ( showGrid ) drawGridLines( g );

		GeneralPath back;
		GeneralPath[] front = new GeneralPath[n];
		GeneralPath[] frontTop = new GeneralPath[n];
		GeneralPath[] frontBottom = new GeneralPath[n];
		double[] eye = { originX, originY, Z }; 
		double[] center;
		double[] T;
		double[] B;
		double top;				// top
		double bot;				// bottom
		double dot;
		for ( int i=0; i<n; i++ ){
			try{
				F.addVariable( "x", A );
				G.addVariable( "x", A );
				top = Math.max( F.getValue(), G.getValue() );
				bot = Math.min( F.getValue(), G.getValue() );
								
				if ( newStat ) stat += Math.abs((top - bot)*Math.abs(c-A));

				setPaths( getHorizontalDisk(top,Math.abs(c-A)), getHorizontalDisk(bot,Math.abs(c-A)) );
				T = toScreenPoint( topRight[0], topRight[1] );
				B = toScreenPoint( bottomLeft[0], bottomLeft[1] );

				back = new GeneralPath();
				back.moveTo( (float)T[0], (float)T[1] );
				front[i] = new GeneralPath();
				front[i].moveTo( (float)T[0], (float)T[1] );
				
				g.setColor( Color.black );
				center = rotatePoint( 0, top, 0 );
				dot = yaxis[0]*(eye[0]-center[0]) + yaxis[1]*(eye[1]-center[1]) + yaxis[2]*(eye[2]-center[2]);
				if ( yaxis[1]*dot > 0 ){ // front is lower portion of ellipse
					back.append( topTop, true );
					back.lineTo( (float)B[0], (float)B[1] );
					g.draw( topTop );
					front[i].append( topBottom, true );
					frontTop[i] = topBottom;
					front[i].lineTo( (float)B[0], (float)B[1] );
				} else { // front is top portion of ellipse
					back.append( topBottom, true );
					back.lineTo( (float)B[0], (float)B[1] );
					g.draw( topBottom );
					front[i].append( topTop, true );
					frontTop[i] = topTop;
					front[i].lineTo( (float)B[0], (float)B[1] );
				}

				center = rotatePoint( 0, bot, 0 );
				dot = yaxis[0]*(eye[0]-center[0]) + yaxis[1]*(eye[1]-center[1]) + yaxis[2]*(eye[2]-center[2]);
				if ( yaxis[1]*dot > 0 ){ // front is lower portion of ellipse
					back.append( bottomTop, true );
					back.lineTo( (float)T[0], (float)T[1]);
					g.draw( bottomTop );
					front[i].append( bottomBottom, true );
					frontBottom[i] = bottomBottom;
					front[i].lineTo( (float)T[0], (float)T[1] );
				} else { // front is top portion of ellipse
					back.append( bottomBottom, true );
					back.lineTo( (float)T[0], (float)T[1] );
					g.draw( bottomBottom );
					front[i].append( bottomTop, true );
					frontBottom[i] = bottomTop;
					front[i].lineTo( (float)T[0], (float)T[1] );
				}

				g.setColor( red );
				if ( b < a ) g.setColor( yellow);
				g.fill( back );
			} catch ( Exception e ){
			}
			A += dir*delta;
		}
		
		if ( !shift ) drawAxes( g );
		if ( !shift ) drawAxisOfRotation( g );
		if ( !shift ) drawFunction( g );

		g.setStroke( hairline );
		for ( int i=0; i<n; i++ ){
			if ( front[n-i-1] != null ){
				g.setColor( Color.black );
				//g.draw( front[ n-i-1] );
				g.draw( frontBottom[ n-i-1 ] );
				g.draw( frontTop[ n-i-1 ] );
				g.setColor( red );
				if (this.b < this.a ) g.setColor( yellow);
				g.fill( front[ n-i-1] );
			}
		}
		if ( newStat ){
			stat *= 2*Math.PI*delta;
			if ( this.b < this.a ) stat *= -1.0;
		}
		newStat = false;
		if ( n == 0 ) stat = 0.0;
		if ( stat < 0 ) applet.setStat( "Volume \u2248 ", stat, Color.cyan );
		else applet.setStat( "Volume \u2248 ", stat, Color.red );
	}


	public void drawAxisOfRotation( Graphics2D g ){
		g.setStroke( axes );
		g.setColor( red );
		if ( b < a ) g.setColor( yellow );
		if ( overC ){
			g.setStroke( boldline );
			g.setColor( Color.red );
			if ( b < a ) g.setColor( Color.blue );
		}
		double[] P = toScreenPoint( c, 0 );
		g.draw( new Line2D.Double(P[0],0,P[0],h) );
	}


	public void drawEndPoints( Graphics2D g ){
		g.setStroke( endline );
		F.addVariable( variable, b );
		G.addVariable( variable, b );
		drawLine( g, b, 0, b, F.getValue(), colorB );
		drawLine( g, b, 0, b, G.getValue(), colorB );

		F.addVariable( variable, a );
		G.addVariable( variable, a );
		drawLine( g, a, 0, a, F.getValue(), colorA );
		drawLine( g, a, 0, a, G.getValue(), colorA );
	}


	public void drawFunction( Graphics2D g ){
		drawFunction( g, G, Color.black );
		drawFunction( g, F, Color.black );
	}
	

	// returns the four points that represent the common tangents between ellipse e and ellipse f
	// presumably, the object is a cylinder
	public void setPaths( double[] top, double[] bottom ){
		topRight[0] = top[0] + Math.cos( top[2] )*top[3];
		topRight[1] = top[1] + Math.sin( top[2] )*top[3];
		//topLeft[0] = top[0] - cos*top[3];
		//topLeft[1] = top[1] - sin*top[3];

		//bottomRight[0] = bottom[0] + cos*bottom[3];
		//bottomRight[1] = bottom[1] + sin*bottom[3];
		bottomLeft[0] = bottom[0] - Math.cos( bottom[2] )*bottom[3];
		bottomLeft[1] = bottom[1] - Math.sin( bottom[2] )*bottom[3];
		
		/*
		double right = 180*Math.acos( x/top[3] )/Math.PI;
		double left = 180*Math.acos( -x/top[3] )/Math.PI;
		if ( y < 0 ){
			right = 360 - right;
			left = 360 - left;
		}
										
		double start;
		double length;
		*/

		topTop = getArc( top, 0, Math.PI );
		topBottom = getArc( top, 0, -Math.PI);
		
		bottomTop = getArc( bottom, Math.PI, -Math.PI );
		bottomBottom = getArc( bottom, Math.PI, Math.PI );
	}
	
	
	public void mouseDragged(MouseEvent me){
		Point p = me.getPoint();
		if ( shift ){			
			if ( !p.equals(POINT) ){
				double degrees = Math.sqrt((p.x-POINT.x)*(p.x-POINT.x)+(p.y-POINT.y)*(p.y-POINT.y))/20;
				// rotate axes about the vector (p.y-point.y,p.x-point.x,0)
				Point vec = new Point( p.y - POINT.y, p.x - POINT.x );
				xaxis = rotate( xaxis, degrees, vec );
				yaxis = rotate( yaxis, degrees, vec );
				zaxis = rotate( zaxis, degrees, vec );
			}
		} else {
			double[] P = toCartesianPoint( p.x, p.y );
			if ( newA ){
				//a = Math.min( P[0], b );
				a = P[0];
				applet.a.setValue( a );
				newStat = true;
				applet.stat.setText( "Calculating..." );
			} else if ( newB ){
				//b = Math.max( a, P[0] );
				b = P[0];
				applet.b.setValue( b );
				newStat = true;
				applet.stat.setText( "Calculating..." );
			} else if ( newC ){
				c = P[0];
				applet.c.setValue( c );
				newStat = true;
				applet.stat.setText( "Calculating..." );
			} else {
				originX -= (p.x - POINT.x)/scale;
				originY += (p.y - POINT.y)/scale;
				newBackground = true;
			}
		}
		POINT = p;
		repaint();
	}
	

	public void mousePressed( MouseEvent me ){
		POINT = me.getPoint();
		double xa = POINT.x - w/2 - (a - originX)*scale;
		double xb = POINT.x - w/2 - (b - originX)*scale;
		double xc = POINT.x - w/2 - (c - originX)*scale;
		double y = POINT.y - H;
		if ( xa*xa + y*y < rr ){
			newA = true;
		} else if ( xb*xb + y*y < rr ){
			newB = true;
		} else if ( xc*xc < rr ){
			newC = true;
		}
		requestFocus();
	}
}