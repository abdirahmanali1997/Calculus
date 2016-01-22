import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.*;

public class Graph3D extends Graph{
	
	double[] xaxis = {1,0,0};
	double[] yaxis = {0,1,0};
	double[] zaxis = {0,0,1};
	double[] perp;				// represents x-axis when drawing shells // represents y-axis when drawing washers
	double[] qerp;				// represents z-axis when drawing shells // represents z-axis when drawing washers
	
	double delta;
	
	//double P = 0.544;			// used in constructing a circle using bezier curves
	//double P = (2*Math.sqrt(7) + Math.sqrt(5) - 1)/12.0;  // 0.5439642166357...

	Color red = new Color( 255, 0, 0, 100 );
	Color darkred = new Color( 255, 0, 0, 100 );
	//Color red = new Color( 255, 0, 0 );
	
	AffineTransform transform;

	public Graph3D(){
		super();
	}

	
	public void paintComponent( Graphics graphics ){
		w = getWidth();
		h = getHeight();

		Graphics2D g = (Graphics2D)graphics;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		g.setColor( getBackground() );
		g.fill( new Rectangle2D.Double( 0, 0, w, h ) );
		
		scale = pixels/units[zoom];
		
		draw( g );
		
		if ( !shift ){	
			drawEndpoints( g );		
			drawCrosshair( g );
		}
	}

	// Overwritten by VolumeViaShells
	public void drawAxisOfRotation( Graphics2D g ){
		g.setStroke( axes );
		g.setColor( red );
		if ( b < a ) g.setColor( yellow );
		if ( overC ){
			g.setStroke( boldline );
			g.setColor( Color.red );
			if ( b < a ) g.setColor( Color.cyan );
		}
		double[] P = toScreenPoint( 0, c );
		g.draw( new Line2D.Double(0,P[1],w,P[1]) );
	}


	public void drawDisk( Graphics2D g, double[] e ){
		if ( e != null ){
			double[] P = toScreenPoint( e[0]-e[3], e[1] + e[4] );
			Arc2D arc = new Arc2D.Double( P[0], P[1], 2*e[3]*scale, 2*e[4]*scale, 0, 360, Arc2D.OPEN);

			P = toScreenPoint( e[0], e[1] );
			transform = g.getTransform();
			g.rotate( -e[2], P[0], P[1] );
			g.setStroke( hairline );
			g.setColor( red );
			if ( b < a ) g.setColor( yellow );
			g.fill( arc );
			g.setColor( Color.black );
			g.draw( arc );
			g.setTransform( transform );
		}
	}


	// draws a vertical disk (width 0) in the plane X=A, with radius B, centered on the horizontal line y=c
	// t is a number between 0 and 2pi, corresponding to the portion of the disk to be drawn
	public void drawDisk( Graphics2D g, double A, double B, double t ){
		try{
			GeneralPath path = new GeneralPath();
			float[] P = toScreenPoint( A, c, 0 );
			path.moveTo( P[0], P[1] );
			P = toScreenPoint( A, c+B, 0 );
			path.lineTo( P[0], P[1] );
			for ( int i=0; i<101; i++ ){
				P = toScreenPoint( A, c+Math.cos(i*t/100.0)*B, Math.sin(i*t/100.0)*B );
				path.lineTo( (float)P[0], (float)P[1] );
			}
			path.closePath();

			g.setStroke( hairline );
			g.setColor( red );
			if ( b < a ) g.setColor( yellow );
			g.fill( path );
			g.setColor( Color.black );
			g.draw( path );
		} catch ( Exception e ){
		}
	}
	
/*	
	// only by the next function
	public void drawPolygon( Graphics2D g, float[] P, float[] Q, float[] R, float[] S ){
		// test to see if vertices P, Q, and R, are in the correct order (i.e., polygon is visible)
		if ( ( Q[0]-P[0])*(R[1]-P[1]) - (Q[1]-P[1])*(R[0]-P[0]) < 0 ){
			GeneralPath path = new GeneralPath();
			path.moveTo( P[0], P[1] );
			path.lineTo( Q[0], Q[1] );
			path.lineTo( R[0], R[1] );
			path.lineTo( S[0], S[1] );
			path.closePath();

			g.fill( path );
			//g.setColor( Color.black );
			//g.draw( new Line2D.Float(P[0],P[1],S[0],S[1]) );
			//g.draw( new Line2D.Float(Q[0],Q[1],R[0],R[1]) );
		}
	}


	// draws a vertical disk (width w) in the plane X=A, with radius B, centered on the horizontal line y=c
	// t is a number between 0 and 2pi, corresponding to the portion of the disk to be drawn
	public void drawDisk( Graphics2D g, double A, double B, double t, double w ){
		//if ( e != null ){

		g.setStroke( hairline );

		try{
			GeneralPath path = new GeneralPath();
			GeneralPath side = new GeneralPath();
			
			double[] p = { A+w, c+B, 0 };
			double[] q = { A-w, c, 0 };
			double[] r = { A-w, c, 0 };
			double[] s = { A+w, c+B, 0 };
			
			//float[] P = toScreenPoint( A+w, c+B, 0 );
			//float[] Q = toScreenPoint( A-w, c+B, 0 );
			//float[] R = toScreenPoint( A-w, c, 0 );
			//float[] S = toScreenPoint( A+w, c, 0 );
			
			float[] P = toScreenPoint( p );
			float[] Q = toScreenPoint( q );
			float[] R = toScreenPoint( r );
			float[] S = toScreenPoint( s );

			g.setColor( red );
			if ( b < a ) g.setColor( yellow );
			drawPolygon( g, P, S, R, Q );
			
			side.moveTo( S[0], S[1] );
			side.lineTo( P[0], P[1] );

			for ( int i=0; i<101; i++ ){
				//R = toScreenPoint( A - w, c + B*Math.cos(i*t/100.0), B*Math.sin(i*t/100.0) );
				//S = toScreenPoint( A + w, c + B*Math.cos(i*t/100.0), B*Math.sin(i*t/100.0) );

				r[1] = c + B*Math.cos(i*t/100.0);
				r[2] = B*Math.sin(i*t/100.0);
				R = toScreenPoint( r );
				s[1] = c + B*Math.cos(i*t/100.0);
				s[2] = B*Math.sin(i*t/100.0);
				S = toScreenPoint( s );

				//check to see if side is visible
				if ( ( Q[0]-P[0])*(R[1]-P[1]) - (Q[1]-P[1])*(R[0]-P[0]) < 0 ){
					g.setColor( getColor( p,q,r,Color.red ) );
					drawPolygon( g, P, Q, R, S );
				}
				side.lineTo( S[0], S[1] );

				p[1] = s[1]; p[2] = s[2];
				q[1] = r[1]; q[2] = r[2];
				Q = R;
				P = S;
			}
			R = toScreenPoint( A-w, c, 0 );
			S = toScreenPoint( A+w, c, 0 );
			side.lineTo( S[0], S[1] );

			side.closePath();
			g.setColor( red );
			if ( b < a ) g.setColor( yellow );
			g.fill( side );
			g.setColor( Color.black );
			g.draw( side );


			g.setColor( red );
			if ( b < a ) g.setColor( yellow );
			drawPolygon( g, P, Q, R, S );

		//}
		} catch ( Exception e ){
		}
	}
*/

	// used only by SpaceCurves
	public void drawLine( Graphics2D g, double a, double b, double c, double x, double y, double z ){
		if ( !Double.isNaN(a) && !Double.isNaN(b) && !Double.isNaN(c) && !Double.isNaN(x) && !Double.isNaN(y) && !Double.isNaN(z) ){
			try{
				//GeneralPath path = new GeneralPath();
				float[] A = toScreenPoint( a, b, c );
				float[] X = toScreenPoint( x, y, z );
				//path.moveTo( A[0], A[1] );
				//path.lineTo( X[0], X[1] );
				//g.draw( path );
				g.draw( new Line2D.Double(A[0], A[1], X[0], X[1]) );
			} catch ( Exception e ){
			}
		}
	}
	

	// used only by SpaceCurves
	public void drawLine( Graphics2D g, double a, double b, double c, double x, double y, double z, Color color ){
		g.setColor( color );
		drawLine( g, a, b, c, x, y, z );
	}
	

	public void drawPoint3D( Graphics2D g, double x, double y, double z, Color color, boolean over ){
		int R = color.getRed();
		int G = color.getGreen();
		int B = color.getBlue();

		double s;
		double t;
		try{
			double rad = 5*Z/(Z-(xaxis[2]*x + yaxis[2]*y + zaxis[2]*z));
			float[] p = toScreenPoint( x, y, z );
			g.setColor( Color.black );
			for ( float k=0; k<1; k=k+(float)(1/rad) ){
				s = k*k;
				t = k*k*rad;
				g.setColor( new Color(R + (int)(s*(255-R)),G + (int)(s*(255-G)),B + (int)(s*(255-B))) );
				g.fill( new Ellipse2D.Double( p[0] - rad + t/2, p[1] - rad + t/2, 2*(rad-t), 2*(rad-t) ) );
			}
		} catch ( Exception e ){
		}
	}


	// e is outer ellipse (x,y,theta,R,r)
	// f is inner ellipse
	public void drawWasher( Graphics2D g, double[] e, double[] f ){
		if ( e != null && f != null ){
			double[] P = toScreenPoint( e[0]-e[3], e[1] + e[4] );
			Arc2D R = new Arc2D.Double( P[0], P[1], 2*e[3]*scale, 2*e[4]*scale, 0, 360, Arc2D.OPEN);
			P = toScreenPoint( f[0]-f[3], f[1] + f[4] );
			Arc2D r = new Arc2D.Double( P[0], P[1], 2*f[3]*scale, 2*f[4]*scale, 0, -360, Arc2D.OPEN);
			
			GeneralPath path = new GeneralPath();
			path.moveTo( (float)f[3], 0.0f );
			path.append( R, true );
			path.lineTo( (float)f[3], 0.0f );
			path.append( r, true );
			
			P = toScreenPoint( e[0], e[1] );
			transform = g.getTransform();
			g.rotate( -e[2], P[0], P[1] );
			g.setStroke( new BasicStroke( 0.25f ) );
			g.setColor( red );
			if ( b < a ) g.setColor( yellow );
try {
			g.fill( path ); // causing crash problems
} catch ( Exception ex ){
System.out.println( "CRASH --- CRASH --- CRASH" );
}
			g.setColor( Color.black );
			g.draw( R );
			g.draw( r );
			g.setTransform( transform );
		}
	}


	public void drawWasher( Graphics2D g, double A, double B, double C, double t ){
		try{
			GeneralPath path = new GeneralPath();
			float[] P = toScreenPoint( A, c+B, 0 );
			path.moveTo( P[0], P[1] );
			for ( int i=0; i<101; i++ ){
				P = toScreenPoint( A, c+Math.cos(i*t/100.0)*B, Math.sin(i*t/100.0)*B );
				path.lineTo( (float)P[0], (float)P[1] );
			}
			P = toScreenPoint( A, c+Math.cos(t)*C, Math.sin(t)*C );
			path.lineTo( P[0], P[1] );
			for ( int i=100; i>-1; i-- ){
				P = toScreenPoint( A, c+Math.cos(i*t/100.0)*C, Math.sin(i*t/100.0)*C );
				path.lineTo( (float)P[0], (float)P[1] );
			}
			path.closePath();

			g.setStroke( hairline );
			g.setColor( red );
			if ( b < a ) g.setColor( yellow );
			g.fill( path );
			g.setColor( Color.black );
			g.draw( path );
		} catch ( Exception e ){
		}
	}


	// returns point at the intersection of the lines L0 and L1
	// line L0 goes through points P and Q
	// line L1 goes through points R and S
	// returns null if lines are parallel or identical
	public double[] intersection( double[] P, double[] Q, double[] R, double[] S ){
		double[] out = new double[2];
		double cross = (Q[0]-P[0])*(S[1]-R[1]) - (Q[1]-P[1])*(S[0]-R[0]);
		if ( cross == 0.0 ){
			return null;
		}
		double s = ((R[0]-P[0])*(S[1]-R[1])+(P[1]-R[1])*(S[0]-R[0]))/cross;
		out[0] = P[0] + s*(Q[0]-P[0]);
		out[1] = P[1] + s*(Q[1]-P[1]);
		return out;
	}


	// returns the midpoint of points P and Q
	public double[] midpoint( double[] P, double[] Q ){
		double[] out = { (P[0]+Q[0])/2.0, (P[1]+Q[1])/2.0 };
		return out;
	}
	

	// returns distance between points P and Q
	public double distance( double[] P, double[] Q ){
		return Math.sqrt( (P[0]-Q[0])*(P[0]-Q[0]) + (P[1]-Q[1])*(P[1]-Q[1]) );
	}
	
	
	// returns the path on ellipse e from point A to point B in the counterclockwise direction
	// USE curveTo BEZIER CURVES to approximate ellipse INSTEAD of lines!!!!!!!!!!!!!!!!!!!!
	public GeneralPath getArc( double[] e, double angle, double length ){
		GeneralPath path = new GeneralPath();
		double c = Math.cos( e[2] );
		double s = Math.sin( e[2] );
		double alpha = angle;
		double x = e[0] + e[3]*Math.cos(alpha)*c - e[4]*Math.sin(alpha)*s;
		double y = e[1] + e[3]*Math.cos(alpha)*s + e[4]*Math.sin(alpha)*c;
		double[] P = toScreenPoint( x, y );
		path.moveTo( (float)P[0], (float)P[1] );
		for ( int i=0; i<101; i++ ){
			alpha = angle + i*length/100.0;
			x = e[0] + e[3]*Math.cos(alpha)*c - e[4]*Math.sin(alpha)*s;
			y = e[1] + e[3]*Math.cos(alpha)*s + e[4]*Math.sin(alpha)*c;
			P = toScreenPoint( x, y );
			path.lineTo( (float)P[0], (float)P[1] );
		}
		return path; 

// I CAN'T HAVE A SINGLE PATH FORMED FROM TWO PATHS THAT USE DIFFERENT TRANSFORMATIONS !@!$? 
//transform = g.getTransform();
//g.rotate( -top[2], originX*w + top[0]*scale, originY*h - top[1]*scale );
//Arc2D arc = new Arc2D.Double( Arc2D.OPEN );
//arc.setFrame( originX*w + (top[0]-top[3])*scale, originY*h - (top[1]+top[4])*scale, 2*top[3]*scale, 2*top[4]*scale );
//arc.setAngleStart( start );
//arc.setAngleExtent( length );
//front.append( arc, false );
//g.draw( front );
//g.setTransform( transform );
	}
	
		
	public Color getColor( double[] p, double[] q, double[] r, Color color ){
		return getColor( p, q, r, color, 255 );
	}
		
	
	public Color getColor( double[] p, double[] q, double[] r, Color color, int trans ){
		// vector normal to swatch
		double[] c = cross( diff(q,p), diff(r,p) );
		double[] d = { -10-p[0], 10-p[1], 10-p[2] };
		
		double F = (c[0]*d[0] + c[1]*d[1] + c[2]*d[2])/Math.sqrt(d[0]*d[0] + d[1]*d[1] + d[2]*d[2]);				
		F = (1+Math.abs(F))/2;
		double G = Math.pow(F,50.0);

		int red = (int)(color.getRed()*(F-G) + G*255);
		int grn = (int)(color.getGreen()*(F-G) + G*255);
		int blu = (int)(color.getBlue()*(F-G) + G*255);
		
		return new Color( red, grn, blu, trans );
	}
	
	
	// x is the x-coordinate of vertical disk
	// r is the radius of the disk
	// called by Disks and Washers
	// disk is automatically centered on horizontal axis of rotation y=c
	public double[] getVerticalDisk( double x, double r ){
		double[] p1 = {x,c+r,0};
		double[] p2 = {x,c,r};
		double[] p3 = {x,c-r,0};
		double[] p4 = {x,c,-r};
		double[] c1 = {x,c+r,r};
		double[] c2 = {x,c+r,-r};

		return getEllipse( p1, p2, p3, p4, c1, c2 );
	}


	// called by Shells
	// disk is automatically centered on vertical axis of rotation x=c
	public double[] getHorizontalDisk( double y, double r ){
		double[] p1 = {c,y,r};
		double[] p2 = {c+r,y,0};
		double[] p3 = {c,y,-r};
		double[] p4 = {c-r,y,0};
		double[] c1 = {c+r,y,r};
		double[] c2 = {c-r,y,r};

		return getEllipse( p1, p2, p3, p4, c1, c2 );
	}
	

/*
	public double[] toABCDEF( double[] e ){
		if ( e[4] != 0 ){
			double[] out = new double[6];
			double c = Math.cos( e[2] );
			double s = Math.sin( e[2] );
			double RR = e[3]*e[3];
			double rr = e[4]*e[4];
		
			out[0] = c*c/RR + s*s/rr;
			out[1] = c*s*(1/rr - 1/RR);
			out[2] = s*s/RR + c*c/rr;
			out[3] = -2*(e[0]*c/RR + e[1]*s/rr);
			out[4] = 2*(e[0]*s/RR - e[1]*c/rr);
			out[5] = e[0]*e[0]/RR + e[1]*e[1]/rr - 1;
		
			return out;
		}
		return null;
	}
*/

	// returns ( x, y, theta, R, r )
	// x is x-coordinate of center of ellipse
	// y is y-coordinate of center of ellipse
	// theta is angle of major axis from horizontal ( between -pi/2 to pi/2 )
	// R is major-radius of ellipse
	// r is minor-radius of ellipse
	// p1, p2, p3, p4 are points on ellipse
	// c1, c2 are points that give lines tangent to ellipse
	// p1 and p3 should be polar opposites in case projection is horizontal line
	public double[] getEllipse( double[] p1, double[] p2, double[] p3, double[] p4, double[] c1, double[] c2 ){
		try {
			double[] s1 = toCartesianPoint( p1 );
			double[] s2 = toCartesianPoint( p2 );
			double[] s3 = toCartesianPoint( p3 );
			double[] s4 = toCartesianPoint( p4 );
			double[] t1 = toCartesianPoint( c1 );
			double[] t2 = toCartesianPoint( c2 );
			

			// center of ellipse is intersection of line through t1 and midpoint s1,s2
			// and line through t2 and midpoint s1,s4
			// See Problem 81 from Eagles Constructive Geometry of Planar Curves
			double[] C = intersection( t1, midpoint(s1,s2), t2, midpoint(s1,s4) );
//if ( C == null ) System.out.println( "C is null" );
			
			// find S1 such that diameter through S1 is conjugate to diameter through s1
			// See Problem 70 from Eagles Constructive Geometry of Planar Curves
			double[] T1 = new double[2];
			double[] P1 = new double[2];
			double[] t = new double[2];
			double[] R1 = new double[2];
			double[] L = new double[2];
			
			if ( C != null ){
				T1[0] = 2*C[0] - t1[0];
				T1[1] = 2*C[1] - t1[1];
				P1[0] = T1[0] + t1[0] - s2[0];
				P1[1] = T1[1] + t1[1] - s2[1];
				t = intersection( t1, s1, T1, P1 );
//if ( t == null ) System.out.println( "t is null" );
				R1[0] = 2*C[0] - s3[0];
				R1[1] = 2*C[1] - s3[1];
				L = intersection( t1, s1, C, s3 );
//if ( L == null ) System.out.println( "L is null" );
			}

			if ( C == null || t == null || L == null ){
//System.out.println( "---------- LINE ----------" );
				
				// find angle theta
				double theta;
				if ( s2[0] == s1[0] ){
					theta = Math.atan((s3[1] - s1[1])/(s3[0] - s1[0]));
				} else {
					theta = Math.atan((s2[1] - s1[1])/(s2[0] - s1[0]));
				}

				// find center of circle
				C = transform( (p1[0]+p3[0])/2.0, (p1[1]+p3[1])/2.0, (p1[2]+p3[2])/2.0 );
				C[0] = (p1[0]+p3[0])/2.0;
				C[1] = (p1[1]+p3[1])/2.0;
				C[2] = (p1[2]+p3[2])/2.0;
				
				// compute radius of circle
				double r = 0.5*Math.sqrt((p1[0]-p3[0])*(p1[0]-p3[0])+(p1[1]-p3[1])*(p1[1]-p3[1])+(p1[2]-p3[2])*(p1[2]-p3[2]));
				// compute distance of center from (0,0,Z)
				//double[] eye = { w*(0.5 - originX)/scale, h*(0.5 - originY)/scale, Z };
double[] eye = { originX, originY, Z };
				double d = Math.sqrt((C[0]-eye[0])*(C[0]-eye[0])+(C[1]-eye[1])*(C[1]-eye[1])+(C[2]-eye[2])*(C[2]-eye[2]));
				// find point on circle closest to eye
				double[] R = { -r*(C[0]-eye[0])/d, -r*(C[1]-eye[1])/d, -r*(C[2]-eye[2])/d };
				double B = (R[2]*r*Math.cos(theta)*Math.sqrt(R[0]*R[0]+R[2]*R[2]) - R[2]*R[2] - R[0]*R[0])/R[0]/R[1];
				double[] S = { 1, B, -(R[0] + B*R[1])/R[2] };
				double l = r/Math.sqrt(1+B*B+S[2]*S[2]);
				S[0] *= l;
				S[1] *= l;
				S[2] *= l;
				// rotate this point to find points on circle whose tangent goes through (0,0,Z)
				double c = r/d;
				double s = Math.sqrt(1-c*c);
				double[] P = { C[0] + c*R[0] + s*S[0], C[1] + c*R[1] + s*S[1], C[2] + c*R[2] + s*S[2] };
				double[] Q = { C[0] + c*R[0] - s*S[0], C[1] + c*R[1] - s*S[1], C[2] + c*R[2] - s*S[2] };
				// project these points onto screen
				P = toCartesianPoint( P );
				Q = toCartesianPoint( Q );
				
				C = midpoint( P, Q );
				double[] out = { C[0], C[1], theta, distance(P,Q)/2.0, 0 };
				return out;
			}
			
			// find S1 such that diameter through S1 is conjugate to diameter through s1
			double m = Math.sqrt(distance(L,s3)*distance(L,R1));
			double d = distance(s1,t1);
			double[] M = {L[0] + m*(s1[0]-t1[0])/d, L[1] + m*(s1[1]-t1[1])/d };
			m = distance(C,s3);
			double[] r1 = { M[0] + m*(s1[1]-t1[1])/d, M[1] - m*(s1[0]-t1[0])/d };
			m = distance( t1,t );
			double e = distance( s1, midpoint(t,t1) );
			m = Math.sqrt( m*m/4.0 - e*e);
			double[] S1 = {C[0] + m*(s1[0]-t1[0])/d, C[1] + m*(s1[1]-t1[1])/d };

			// find major axes based on conjugate diameters
			// See Problem 62 from Eagles Constructive Geometry of Planar Curves
			double[] P = { s1[0] + S1[1] - C[1], s1[1] + C[0] - S1[0] };
			double[] Q = { s1[0] - S1[1] + C[1], s1[1] - C[0] + S1[0] };
			double PC = distance(P,C);
			double QC = distance(Q,C);
			double Rmajor = (PC + QC)/2.0;
			double Rminor = Math.abs( PC - QC )/2.0;

			double[] major = { (C[0] + (P[0]-C[0])*(QC/PC) + Q[0])/2.0 - C[0] , (C[1] + (P[1]-C[1])*(QC/PC) + Q[1])/2.0 - C[1] };
			if ( p1[1] == p2[1] && Math.abs(originX) < 0.000000000001 
				&& ( Math.abs(zaxis[0]) == 0.0 && Math.abs(zaxis[2]-1.0) < 0.05 ) ){
				major[0] = 1;
				major[1] = 0;
			} else if ( p1[0] == p2[0] && Math.abs(originY) < 0.000000000001
				&& ( Math.abs(zaxis[0]) == 0.0 && Math.abs(zaxis[2]-1.0) < 0.05 ) ){
				major[0] = 0;
				major[1] = 1;
			}
			double theta = Math.PI/2;
			if ( major[0] != 0 ) theta = Math.atan( major[1]/major[0] );
		
			double[] out = { C[0], C[1], theta, Rmajor, Rminor };
			return out;
		} catch (Exception e ){
//System.out.println("Graph3D.getEllipse() caught: " + e.toString() );
		}
		return null;
	}


	// to u from v
	public double[] diff( double[] u, double[] v ){
		double[] out = {u[0]-v[0],u[1]-v[1],u[2]-v[2]};
		return out;
	}


	// returns a unit vector perpendicular to u and v
	public double[] cross( double[] u, double[] v ){
		double[] out = new double[3];
		out[0] = u[1]*v[2] - u[2]*v[1];
		out[1] = u[2]*v[0] - u[0]*v[2];
		out[2] = u[0]*v[1] - u[1]*v[0];
		double l = Math.sqrt( out[0]*out[0] + out[1]*out[1] + out[2]*out[2] );
		out[0] /= l;
		out[1] /= l;
		out[2] /= l;
		return out;
	}


	public double[] toCartesianPoint( double[] X ){
		double x = X[0];
		double y = X[1];
		double z = X[2];

		// shift x and y so that it's rotated around middle of panel
		x -= originX;
		y -= originY;

		// compute vertical distance of (x,y,z) from screen
		double zz = x*xaxis[2] + y*yaxis[2] + z*zaxis[2];

		// eye directly above middle of panel
		double[] out = new double[2];
		out[0] = originX + Z*(x*xaxis[0] + y*yaxis[0] + z*zaxis[0])/(Z-zz);
		out[1] = originY + Z*(x*xaxis[1] + y*yaxis[1] + z*zaxis[1])/(Z-zz);
		
		return out;
	}


	// takes a point (x,y,z) in cartesian coordinates
	// returns the corresponding point on the screen
	public float[] toScreenPoint( double[] X ) throws Exception{
		double x = X[0];
		double y = X[1];
		double z = X[2];
		if ( x*xaxis[2] + y*yaxis[2] + z*zaxis[2] > Z ){
//System.out.println( "(" + x + "," + y + "," + z + ")" );
			throw new Exception();
		}
		float[] out = new float[2];
		
		// shift x and y so that it's rotated around middle of panel
		x -= originX;
		y -= originY;
		
		// compute vertical distance of (x,y,z) from screen
		double zz = x*xaxis[2] + y*yaxis[2] + z*zaxis[2];

		// eye directly above middle of panel
		out[0] = (float)( w/2 + Z*(x*xaxis[0] + y*yaxis[0] + z*zaxis[0])*scale/(Z-zz) );
		out[1] = (float)( h/2 - Z*(x*xaxis[1] + y*yaxis[1] + z*zaxis[1])*scale/(Z-zz) );
		
		return out;
	}
	
	// takes a point (x,y,z) in cartesian coordinates
	// returns the corresponding point on the screen
	public float[] toScreenPoint( double x, double y, double z ) throws Exception{
		if ( x*xaxis[2] + y*yaxis[2] + z*zaxis[2] > Z ){
			throw new Exception();
		}
		float[] out = new float[3];
		
		// shift x and y so that it's rotated around middle of panel
		x -= originX;
		y -= originY;
		
		// compute vertical distance of (x,y,z) from screen
		double zz = x*xaxis[2] + y*yaxis[2] + z*zaxis[2];

		// eye directly above middle of panel
		out[0] = (float)( w/2 + Z*(x*xaxis[0] + y*yaxis[0] + z*zaxis[0])*scale/(Z-zz) );
		out[1] = (float)( h/2 - Z*(x*xaxis[1] + y*yaxis[1] + z*zaxis[1])*scale/(Z-zz) );
		out[2] = (float)zz;
		
		return out;
	}


	// returns coordinates of (x,y,z) after being transformed
	// out is relative to middle of screen.
	public double[] transform( double x, double y, double z ){
		// shift x and y so that it's rotated around middle of panel
		return rotatePoint( x - originX, y - originY, z );
	}
	
	
	public double[] rotatePoint( double[] X ){
		double[] out = new double[3];
		out[0] = X[0]*xaxis[0] + X[1]*yaxis[0] + X[2]*zaxis[0];
		out[1] = X[0]*xaxis[1] + X[1]*yaxis[1] + X[2]*zaxis[1];
		out[2] = X[0]*xaxis[2] + X[1]*yaxis[2] + X[2]*zaxis[2];
		return out;
	}


	public double[] rotatePoint( double x, double y, double z ){
		double[] out = new double[3];
		out[0] = x*xaxis[0] + y*yaxis[0] + z*zaxis[0];
		out[1] = x*xaxis[1] + y*yaxis[1] + z*zaxis[1];
		out[2] = x*xaxis[2] + y*yaxis[2] + z*zaxis[2];
		return out;
	}


	// rotates p around vec by theta radians
	public double[] rotate( double[] p, double theta, Point vec ){
		double[] out = new double[3];
		double l = vec.x*vec.x + vec.y*vec.y;
		out[0] = vec.x*(1-Math.cos(theta))*(vec.x*p[0]+vec.y*p[1])/l + p[0]*Math.cos(theta) + (vec.y*p[2])*Math.sin(theta)/Math.sqrt(l);
		out[1] = vec.y*(1-Math.cos(theta))*(vec.x*p[0]+vec.y*p[1])/l + p[1]*Math.cos(theta) - (vec.x*p[2])*Math.sin(theta)/Math.sqrt(l);
		out[2] = p[2]*Math.cos(theta) + (vec.x*p[1]-vec.y*p[0])*Math.sin(theta)/Math.sqrt(l);
		return out;
	}


	public void keyPressed( KeyEvent ke ){
		int code = ke.getKeyCode();
		if ( code == KeyEvent.VK_SHIFT ){// holding down shift key
			shift = true;
			repaint();
		} else if ( code == KeyEvent.VK_R ){
			radians = !radians;
			newBackground = true;
			repaint();
		}
	}
	
	/* overwritten by
	 Surfaces
	 */
	public void keyReleased( KeyEvent ke ){
		shift = false;
		newBackground = true;
		xaxis[0] = 1; xaxis[1] = 0; xaxis[2] = 0;
		yaxis[0] = 0; yaxis[1] = 1; yaxis[2] = 0;
		zaxis[0] = 0; zaxis[1] = 0; zaxis[2] = 1;
		repaint();
	}

	
	// Overwritten by Surfaces,VolumeViaShells
	public void mouseDragged(MouseEvent me){
		Point p = me.getPoint();
		if ( shift ){ //|| me.getButton() == MouseEvent.BUTTON3 ){			
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
				//b = Math.max( a, P[0] );
				c = P[1];
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
	

	// Overwritten by Surfaces, VolumeViaShells
	public void mousePressed( MouseEvent me ){
		POINT = me.getPoint();
		double xa = POINT.x - w/2 - (a - originX)*scale;
		double xb = POINT.x - w/2 - (b - originX)*scale;
		double y = POINT.y - H;
		double yc = POINT.y - h/2 + (c - originY)*scale;
		if ( xa*xa + y*y < rr ){
			newA = true;
		} else if ( xb*xb + y*y < rr ){
			newB = true;
		} else if ( yc*yc < rr ){
			newC = true;
		}
		requestFocus();
	}
}