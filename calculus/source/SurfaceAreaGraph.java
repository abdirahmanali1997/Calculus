import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.*;

public class SurfaceAreaGraph extends Graph3D implements KeyListener{

	public SurfaceAreaGraph( CalculusApplet applet, String f, double a, double b, int n ){
		super();

		this.applet = applet;
		this.a = a;
		this.b = b;
		this.n = Math.max( n,0 );

		F.parseExpression( f );
	}


	// returns solutions to A tan(t) + sec(t) = B
	// I asked Maple to solve this equation for me
	// and this is what it had to say
	public double[] solve( double A, double B ){
		double[] out = new double[2];
		double s = Math.sqrt(B*B*B*B-B*B+A*A*B*B);
		out[0] = Math.atan((B*s-A*B)/(A*s+B*B));
		out[1] = Math.atan((B*s+A*B)/(A*s-B*B)) + Math.PI;
		return out;
	}

	// returns right tangent and angle to left tangent
	// e is ellipse
	// P is apex in cartesian coordinates
	// T are potential tangents
	public double[] getAngles( double[] e , double[] P, double[] T ){
		double[] out = new double[2];
		
		if ( Double.isNaN(T[0]) || Double.isNaN(T[1]) ){
			out[0] = 0;
			out[1] = Math.PI;
			return out;
		}
		
		double c = Math.cos( e[2] );
		double s = Math.sin( e[2] );
		
		double x = e[0] + e[3]*Math.cos(T[0])*c - e[4]*Math.sin(T[0])*s;
		double y = e[1] + e[3]*Math.cos(T[0])*s + e[4]*Math.sin(T[0])*c;
		double dx = -e[3]*Math.sin(T[0])*c - e[4]*Math.cos(T[0])*s;
		double dy = -e[3]*Math.sin(T[0])*s + e[4]*Math.cos(T[0])*c;
		double cross = (P[0]-x)*dy - (P[1]-y)*dx;
		cross /= Math.sqrt((dx*dx + dy*dy)*((P[0]-x)*(P[0]-x) + (P[1]-y)*(P[1]-y)));
		double R = T[0];
		if ( Math.abs( cross ) > 0.1 ) R += Math.PI;

		x = e[0] + e[3]*Math.cos(T[1])*c - e[4]*Math.sin(T[1])*s;
		y = e[1] + e[3]*Math.cos(T[1])*s + e[4]*Math.sin(T[1])*c;
		dx = -e[3]*Math.sin(T[1])*c - e[4]*Math.cos(T[1])*s;
		dy = -e[3]*Math.sin(T[1])*s + e[4]*Math.cos(T[1])*c;
		cross = (P[0]-x)*dy - (P[1]-y)*dx;
		cross /= Math.sqrt((dx*dx + dy*dy)*((P[0]-x)*(P[0]-x) + (P[1]-y)*(P[1]-y)));
		double S = T[1];
		if ( Math.abs( cross ) > 0.01 ) S += Math.PI;
		
		out[0] = R;
		out[1] = S-R;
		//out[1] = out[1] - 2*Math.PI*(int)(out[1]/(2*Math.PI));
		if ( out[1] < 0 ) out[1] += 2*Math.PI;

		if ( e[3]*Math.cos(R)*c - e[4]*Math.sin(R)*s < 0 ){
			out[0] = S;
			out[1] = R-S;
			//out[1] = out[1] - 2*Math.PI*(int)(out[1]/(2*Math.PI));
			if ( out[1] < 0 ) out[1] += 2*Math.PI;
		}
		
		return out;
	}

	//double[] topLeft = new double[2];
	double[] topRight = new double[2];
	double[] bottomLeft = new double[2];
	//double[] bottomRight = new double[2];
	
	GeneralPath topTop = new GeneralPath();
	GeneralPath topBottom = new GeneralPath();
	GeneralPath bottomTop = new GeneralPath();
	GeneralPath bottomBottom = new GeneralPath();
	// returns the four points that represent the common tangents between ellipse top and ellipse bottom
	// apex is the top of the cone, that is, if the ellipses come from a cone
	// presumbably, the object is a truncated cone
	public void setPaths( double[] top, double[] bottom, double[] apex ){
		double P[] = toCartesianPoint( apex );
		
		double c = Math.cos( top[2] );
		double s = Math.sin( top[2] );
		double[] T = solve( ((P[0]-top[0])*s-(P[1]-top[1])*c)/top[4], ((P[0]-top[0])*c+(P[1]-top[1])*s)/top[3] );
		T = getAngles( top, P, T );
		double t = T[0];
		topRight[0] = top[0] + top[3]*Math.cos(t)*c - top[4]*Math.sin(t)*s;
		topRight[1] = top[1] + top[3]*Math.cos(t)*s + top[4]*Math.sin(t)*c;
		topTop = getArc( top, t, T[1] );
		topBottom = getArc( top, t, T[1]-2*Math.PI);

		c = Math.cos( bottom[2] );
		s = Math.sin( bottom[2] );
		T = solve( ((P[0]-bottom[0])*s-(P[1]-bottom[1])*c)/bottom[4], ((P[0]-bottom[0])*c+(P[1]-bottom[1])*s)/bottom[3] );
		T = getAngles( bottom, P, T );
		t = T[0] + T[1];
		bottomLeft[0] = bottom[0] + bottom[3]*Math.cos(t)*c - bottom[4]*Math.sin(t)*s;
		bottomLeft[1] = bottom[1] + bottom[3]*Math.cos(t)*s + bottom[4]*Math.sin(t)*c;
		bottomTop = getArc( bottom, t, -T[1] );
		bottomBottom = getArc( bottom, t, 2*Math.PI - T[1]);
	}
	
	
	public void draw( Graphics2D g ){
		delta = (b-a)/n;
		if ( showGrid ) drawGridLines( g );
		if ( newStat ) stat = 0.0;
		g.setStroke( new BasicStroke(0.4f) );
		
		// eye focused on middle of panel
		GeneralPath back;
		GeneralPath[] front = new GeneralPath[n];
		GeneralPath[] frontTop = new GeneralPath[n];
		GeneralPath[] frontBottom = new GeneralPath[n];
		double[] top;
		double[] bottom;
		double[] apex = {0,0,0};
		double[] eye = { originX , originY, Z }; 
		double[] center;
		double[] T;
		double[] B;
		double s;
		double t;
		double dot;
		
		for ( int i=0; i<n; i++ ){
			try{
				F.addVariable( "x", a + i*delta );
				s = F.getValue();
				F.addVariable( "x", a + (i+1)*delta );
				t = F.getValue();
				
				if ( applet.choice.getSelectedItem().equals("Rotate about x-axis") ){
					top = getVerticalDisk( a + (i+1)*delta, t );
					bottom = getVerticalDisk( a + i*delta, s );
					apex[0] = a + (i+1)*delta + t*delta/(s-t);
					apex[1] = 0;
					if ( newStat ) stat += (s+t)*Math.sqrt((t-s)*(t-s) + delta*delta)/2.0;
				} else {
					top = getHorizontalDisk( t, a + (i+1)*delta );
					bottom = getHorizontalDisk( s, a + i*delta );
					apex[0] = 0;
					apex[1] = t + (a + (i+1)*delta)*(s-t)/delta;
					if ( newStat ) stat += (a + (i+0.5)*delta)*Math.sqrt((t-s)*(t-s) + delta*delta);
				}

//top = getHorizontalDisk( 2, a );
//bottom = getHorizontalDisk( -3, b );
//apex[0] = 0;
//apex[1] = -3 + b*(2+3)/(b-a);
				setPaths( top, bottom, apex );

//float P[] = toScreenPoint( apex );
//g.setColor( Color.black );
//g.fill( new Ellipse2D.Double( P[0]-5,P[1]-5,10,10) );
//g.draw( new Line2D.Double( P[0], P[1], originX*w + topRight[0]*scale, originY*h - topRight[1]*scale ) );
//g.draw( new Line2D.Double( P[0], P[1], originX*w + bottomLeft[0]*scale, originY*h - bottomLeft[1]*scale ) );

				back = new GeneralPath();
				T = toScreenPoint( topRight[0], topRight[1] );
g.setColor( Color.blue );
//g.fill( new Ellipse2D.Double( T[0]-3,T[1]-3,7,7 ) );
				B = toScreenPoint( bottomLeft[0], bottomLeft[1] );
g.setColor( Color.green );
//g.fill( new Ellipse2D.Double( B[0]-3,B[1]-3,7,7 ) );

				back.moveTo( (float)T[0], (float)T[1] );
				front[i] = new GeneralPath();
				front[i].moveTo( (float)T[0], (float)T[1] );
				
				g.setColor( Color.black );
				center = rotatePoint( 0, t, 0 );
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

				center = rotatePoint( 0, b, 0 );
				dot = yaxis[0]*(eye[0]-center[0]) + yaxis[1]*(eye[1]-center[1]) + yaxis[2]*(eye[2]-center[2]);
				if ( yaxis[1]*dot > 0 ){ // front is lower portion of ellipse
					back.append( bottomTop, true );
					back.lineTo( (float)T[0], (float)T[1] );
					g.draw( bottomTop );
					front[i].append( bottomBottom, true );
					frontBottom[i] = bottomBottom;
					front[i].lineTo( (float)T[0], (float)T[1]);
				} else { // front is top portion of ellipse
					back.append( bottomBottom, true );
					back.lineTo( (float)T[0], (float)T[1] );
					g.draw( bottomBottom );
					front[i].append( bottomTop, true );
					frontBottom[i] = bottomTop;
					front[i].lineTo( (float)T[0], (float)T[1] );
				}

				g.setColor( red );
				g.fill( back );
			} catch ( Exception e ){
			}
		}
		
		if ( !shift ) applet.graph.drawAxes( g );
		if ( !shift ) applet.graph.drawFunction( g );

		g.setStroke( new BasicStroke(0.4f) );
		for ( int i=0; i<n; i++ ){
			if ( front[n-i-1] != null ){
				g.setColor( Color.black );
				g.draw( frontBottom[ n-i-1 ] );
				g.draw( frontTop[ n-i-1 ] );
				g.setColor( Color.red );
				g.fill( front[ n-i-1] );
			}
		}
		if ( newStat ){
			stat *= 2*Math.PI;
		}
		newStat = false;
		if ( n == 0 ) stat = 0.0;
		applet.setStat( "Surface Area \u2248 ", stat, Color.red );
	}
	
		
			
		/*		
	public void draw( Graphics2D g ){
		Z = 10*units[zoom];

		double[] eye = {Z*(yaxis[0]*zaxis[1] - yaxis[1]*zaxis[0])+(w/2 - originX*w)/scale,
						Z*(zaxis[0]*xaxis[1] - zaxis[1]*xaxis[0])-(h/2 - originY*h)/scale,
						Z*(xaxis[0]*yaxis[1] - xaxis[1]*yaxis[0])};

		if ( applet.choice.getSelectedItem().equals("Rotate about x-axis") ){
			double[] x = {1,0,0};
			perp = cross( x, eye );
			qerp = cross( perp, x );
			drawVerticalBands( g );
		} else if ( applet.choice.getSelectedItem().equals("Rotate about y-axis") ){
			double[] y = {0,1,0};
			perp = cross( y, eye );
			qerp = cross( perp, y );
			drawHorizontalBands( g );
		}

		applet.stat.setText( "Surface Area = " + stat );
	}*/


/*
// need to fix which way vertical bands are drawn
// right to left or left to right
// depends on direction of x-axis
	public void drawVerticalBands( Graphics2D g ){
		delta = (b-a)/(n/scale);
		if ( !shift ) stat = 0.0;
		g.setStroke( new BasicStroke(0.25f) );

		// eye focused on middle of panel
		double A = b - delta/scale/2.0;
		while ( A > a ){
			try {
				drawBackVerticalShell( A, g );
			} catch ( Exception e ){
			}
			A -= delta/scale;
		}
		
		if ( !shift ) applet.graph.drawAxes( g );
		if ( !shift ) applet.graph.drawFunction( g );

		g.setStroke( new BasicStroke(0.25f) );		
		A = a + delta/scale/2.0;
		while ( A < b ){
			try {
				drawFrontVerticalShell( A, g );
			} catch ( Exception e ){
			}
			A += delta/scale;
		}
		if ( !shift ) stat *= 2*Math.PI*delta/scale;
	}
*/
}