import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import org.nfunk.jep.*;
import org.lsmp.djep.djep.*;
import org.lsmp.djep.xjep.*;


public class JoeRolesGraph extends Graph{
	
	double stat2;
	double stat3;

	public JoeRolesGraph( CalculusApplet applet, String f, double a, double b, int n ){
		super();

		this.applet = applet;
		this.a = a;
		this.b = b;
		this.n = Math.max( n,0 );
		
		F.parseExpression( "sqrt(1-x^2)" );
	}

	public void draw( Graphics2D g ){
		drawTrapezoidal( g );
		//drawTrapezoidalX( g );
		//drawTrapezoidalY( g );

		if ( n == 0 ) stat = 0.0;
		if ( stat < 0 ) applet.setStat( "Area \u2248 ", stat, yellow );
		else applet.setStat( "Area \u2248 ", stat, Color.red );
		
		//applet.setStat2( "Arclength: ", Math.acos(a) - Math.acos(b));
		applet.setStat2( "\t Arclength \u2248 ", stat3);
	}

		
	
	
	public void drawFunction( Graphics2D g, DJep F, Color color ){
		double[] P = toScreenPoint( -1, 1 );
		double[] Q = toScreenPoint( 1, -1 );
		g.setColor( Color.lightGray );
		g.setStroke( curve );
		g.draw( new Arc2D.Double( P[0], P[1], Q[0]-P[0], Q[0]-P[0], 0, 180, Arc2D.OPEN) );
		
		/*
		 GeneralPath path = new GeneralPath();
		double i = 0.0;
		double j;
		double k;
		F.addVariable( variable, originX-w/2/scale );
		double y = F.getValue();
		double oldy = y;
		while ( i < w ){
			// find first point that is in viewable portion of xy-plane
			while ( i<=w && (Double.isNaN(y=F.getValue()) || Math.abs(y-originY)>h/scale) ){
				//while ( i<=w && Double.isNaN(y=F.getValue()) ){
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
			while ( i<=w && Math.abs(y-originY)<h/scale && !Double.isNaN(y=F.getValue()) ){
				path.lineTo( (float)i, (float)(h/2 - (y-originY)*scale) );
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
		 */
	}
	
	
	private void drawTrapezoidal( Graphics2D g ){
		double min = Math.min( a, b );
		double delta = Math.abs(b-a)/n;

		if ( newStat ){
			stat = 0.0;
			stat2 = 0.0;
			stat3 = 0.0;
		}
		
		F.addVariable( variable, min );
		double y0 = F.getValue();
		double y1;
		double x;
		
		double[] P = toScreenPoint( min, 0);
		double[] Q = toScreenPoint( min, y0);
		double[] R;
		double[] S;
		double[] T;
		double[] U = toScreenPoint( 0, y0 );
		
		GeneralPath path;
		for ( int i=0; i<n; i++ ){
			x = min + (i+1)*delta;
			//F.addVariable( "x", x );
			//F.getValue();
			y1 = Math.sqrt(1-x*x);
			R = toScreenPoint( x, y1 );
			S = toScreenPoint( x, 0 );
			T = toScreenPoint( 0, y1 );
			
			if ( newStat ){	
				stat += y0+y1;
				stat2 += (y0 - y1)*(min + (i+0.5)*delta);
				stat3 += Math.sqrt( (y1-y0)*(y1-y0) + delta*delta);
			}
			g.setColor( red );
			if ( !(Double.isNaN(y0) || Double.isNaN(y1) || Double.isInfinite(y0) || Double.isInfinite(y1)) ){
				if ( y0*y1 >= 0 ){
					if ( a<b && (y0 < 0 || (y0 == 0 && y1<0 )) ) g.setColor( yellow );
					else if ( a>b && (y0 > 0 || (y0 == 0 && y1>0 )) ) g.setColor( yellow );
					path = new GeneralPath();
					path.moveTo( (float)P[0], (float)P[1] );
					path.lineTo( (float)Q[0], (float)Q[1] );
					path.lineTo( (float)R[0], (float)R[1] );
					path.lineTo( (float)S[0], (float)S[1] );
					g.fill( path );
					
					path = new GeneralPath();
					path.moveTo( (float)U[0], (float)U[1] );
					path.lineTo( (float)Q[0], (float)Q[1] );
					path.lineTo( (float)R[0], (float)R[1] );
					path.lineTo( (float)T[0], (float)T[1] );
					g.fill( path );
					
					path = new GeneralPath();
					path.moveTo( (float)Q[0], (float)Q[1] );
					path.lineTo( (float)R[0], (float)R[1] );
					g.setColor( Color.black );
					g.setStroke( curve );
					g.draw( path );
				} else {
					if ( (a<b && y0<0) || (a>b && y0>0) ) g.setColor ( yellow );
					T = toScreenPoint( min+i*delta+y0*delta/(y0-y1),0);
					path = new GeneralPath();
					path.moveTo( (float)P[0], (float)P[1] );
					path.lineTo( (float)Q[0], (float)Q[1] );
					path.lineTo( (float)T[0], (float)T[1] );
					g.fill( path );
					
					g.setColor ( red );
					if ( (a<b && y0>0) || (a>b && y0<0) ) g.setColor ( yellow );
					path = new GeneralPath();
					path.moveTo( (float)R[0], (float)R[1] );
					path.lineTo( (float)S[0], (float)S[1] );
					path.lineTo( (float)T[0], (float)T[1] );
					g.fill( path );
				}
			}
			y0 = y1;
			P = S;
			Q = R;
			U = T;
		}
		if ( newStat ){
			stat *= delta/2.0;
			if ( b < a ){
				stat *= -1;
				stat2 *= -1;
				stat3 *= -1;
			}
			stat += stat2;
		}
	}
	

	
	
	private void drawTrapezoidalX( Graphics2D g ){
		double min = Math.min( a, b );
		double delta = Math.abs(b-a)/n;
		
		if ( newStat ) stat = 0.0;
		
		F.addVariable( variable, min );
		double y0 = F.getValue();
		double y1;
		
		double[] P = toScreenPoint(min,0);
		double[] Q = toScreenPoint(min,y0);
		double[] R;
		double[] S;
		double[] T;
		
		GeneralPath path;
		for ( int i=0; i<n; i++ ){
			F.addVariable( "x", min + (i+1)*delta );
			y1 = F.getValue();
			R = toScreenPoint( min + (i+1)*delta, y1 );
			S = toScreenPoint( min + (i+1)*delta, 0 );
			
			if ( newStat ) stat += y0+y1; 
			g.setColor( red );
if ( !(Double.isNaN(y0) || Double.isNaN(y1) || Double.isInfinite(y0) || Double.isInfinite(y1)) ){
			if ( y0*y1 >= 0 ){
				if ( a<b && (y0 < 0 || (y0 == 0 && y1<0 )) ) g.setColor( yellow );
				else if ( a>b && (y0 > 0 || (y0 == 0 && y1>0 )) ) g.setColor( yellow );
				path = new GeneralPath();
				path.moveTo( (float)P[0], (float)P[1] );
				path.lineTo( (float)Q[0], (float)Q[1] );
				path.lineTo( (float)R[0], (float)R[1] );
				path.lineTo( (float)S[0], (float)S[1] );
				g.fill( path );
				
				path = new GeneralPath();
				path.moveTo( (float)P[0], (float)P[1] );
				path.lineTo( (float)Q[0], (float)Q[1] );
				path.lineTo( (float)R[0], (float)R[1] );
				path.lineTo( (float)S[0], (float)S[1] );
				g.fill( path );
				
			} else {
				if ( (a<b && y0<0) || (a>b && y0>0) ) g.setColor ( yellow );
				T = toScreenPoint( min+i*delta+y0*delta/(y0-y1),0);
				path = new GeneralPath();
				path.moveTo( (float)P[0], (float)P[1] );
				path.lineTo( (float)Q[0], (float)Q[1] );
				path.lineTo( (float)T[0], (float)T[1] );
				g.fill( path );
				
				g.setColor ( red );
				if ( (a<b && y0>0) || (a>b && y0<0) ) g.setColor ( yellow );
				path = new GeneralPath();
				path.moveTo( (float)R[0], (float)R[1] );
				path.lineTo( (float)S[0], (float)S[1] );
				path.lineTo( (float)T[0], (float)T[1] );
				g.fill( path );
			}
}
			y0 = y1;
			P = S;
			Q = R;
		}
		if ( newStat ){
			stat *= delta/2.0;
			if ( b < a ) stat *= -1;
		}
	}
	
	
	private void drawTrapezoidalY( Graphics2D g ){
		double A = Math.sqrt(1-b*b);
		double B = Math.sqrt(1-a*a);
		double min = Math.min( A, B );
		double delta = Math.abs(B-A)/n;
				
		
		double statY = 0.0;

		F.addVariable( variable, min );
		double y0 = F.getValue();
		double y1;
		
		double[] P = toScreenPoint(0,min);
		double[] Q = toScreenPoint(y0,min);
		double[] R;
		double[] S;
		double[] T;
		
		GeneralPath path;
		for ( int i=0; i<n; i++ ){
			F.addVariable( variable, min + (i+1)*delta );
			y1 = F.getValue();
			R = toScreenPoint( y1, min + (i+1)*delta );
			S = toScreenPoint( 0, min + (i+1)*delta );
			
			if ( newStat ) statY += y0+y1; 
			g.setColor( red );
			if ( !(Double.isNaN(y0) || Double.isNaN(y1) || Double.isInfinite(y0) || Double.isInfinite(y1)) ){
				if ( y0*y1 >= 0 ){
					if ( A<B && (y0 < 0 || (y0 == 0 && y1<0 )) ) g.setColor( yellow );
					else if ( A>B && (y0 > 0 || (y0 == 0 && y1>0 )) ) g.setColor( yellow );
					path = new GeneralPath();
					path.moveTo( (float)P[0], (float)P[1] );
					path.lineTo( (float)Q[0], (float)Q[1] );
					path.lineTo( (float)R[0], (float)R[1] );
					path.lineTo( (float)S[0], (float)S[1] );
					g.fill( path );
				} else {
					if ( (A<B && y0<0) || (A>B && y0>0) ) g.setColor ( yellow );
					T = toScreenPoint( min+i*delta+y0*delta/(y0-y1),0);
					path = new GeneralPath();
					path.moveTo( (float)P[0], (float)P[1] );
					path.lineTo( (float)Q[0], (float)Q[1] );
					path.lineTo( (float)T[0], (float)T[1] );
					g.fill( path );
					
					g.setColor ( red );
					if ( (A<B && y0>0) || (A>B && y0<0) ) g.setColor ( yellow );
					path = new GeneralPath();
					path.moveTo( (float)R[0], (float)R[1] );
					path.lineTo( (float)S[0], (float)S[1] );
					path.lineTo( (float)T[0], (float)T[1] );
					g.fill( path );
				}
			}
			y0 = y1;
			P = S;
			Q = R;
		}
		statY *= delta/2.0;
		if ( B < A ) statY *= -1;
		
		stat += statY;
		newStat = false;
	}
}