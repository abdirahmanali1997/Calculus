import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.util.*;

public class LineIntegralGraph extends Graph3D{

	double[][] points;		// represents (x(t),y(t),f(x,y))
	int[] order;
	double[] zorder;

	public LineIntegralGraph( CalculusApplet applet, String f, String g, String j, double a, double b, int n ){
		super();

		this.applet = applet;
		this.a = a;
		this.b = b;
		this.n = n;

		F.parseExpression( f );
		G.parseExpression( g );
		J.parseExpression( j );

		newPoints();
	}

	public double[] getValue( double t ){
		double[] out = new double[3];
		G.addVariable( "t", t );
		J.addVariable( "t", t );
		out[0] = G.getValue();
		if ( Double.isNaN(out[0]) ) out[0] = 0;
		out[1] = J.getValue();
		if ( Double.isNaN(out[1]) ) out[1] = 0;
		
		F.addVariable( "x", out[0] );
		F.addVariable( "y", out[1] );
		out[2] = F.getValue();
		if ( Double.isNaN(out[2]) ) out[2] = 0;
		return out;
	}
	
	public void newPoints(){		
		points = new double[n+1][3];
		order = new int[n+1];
		zorder = new double[n];
		double[] d;
		points[0] = getValue( a );
		order[0] = 0;
		for ( int i=1; i<n+1; i++ ){
			points[i] = getValue( a + i*(b-a)/n );
			order[i] = i;
			d = diff( points[i], points[i-1] );
			stat += Math.sqrt( d[0]*d[0]  + d[1]*d[1] )*( points[i][2] + points[i-1][2] )/2;
			//stat += Math.sqrt( (points[i][0] - points[i-1][0])*(points[i][0] - points[i-1][0]) 
			//	+ (points[i][1] - points[i-1][1])*(points[i][1] - points[i-1][1]) )*( points[i][2] + points[i-1][2] )/2;
		}
	}

	public void draw( Graphics2D g ){
		if ( newStat ){
			stat = 0;
			newPoints();
			newStat = false;
		}

		if ( showGrid ) drawGridLines( g );
		if ( !shift ) drawAxes( g );
						
		drawRibbon( g );

		if ( stat < 0 ) applet.setStat( "Area \u2248 ", stat, Color.cyan );
		else applet.setStat( "Area \u2248 ", stat, Color.red );
	}

	public void drawEndpoints( Graphics2D g ){
		G.addVariable( "t", b );
		J.addVariable( "t", b );
		double x = G.getValue();
		double y = J.getValue();
		drawPoint3D( g, x, y, 0, Color.green, overB );

		G.addVariable( "t", a );
		J.addVariable( "t", a );
		x = G.getValue();
		y = J.getValue();
		drawPoint3D( g, x, y, 0, Color.blue, overA );
	}

	public void drawFunction( Graphics2D g ){
	}
/*
	public void drawFunction( Graphics2D g ){
		GeneralPath path = new GeneralPath();
		G.addVariable( variable, a );
		J.addVariable( variable, a );
		double x = G.getValue();
		double y = J.getValue();
		double i = a;
		double delta = units[zoom]/(zoom+1)/(zoom+1);
		while ( i < b ){
			while ( i < b && (Double.isNaN(x) || Double.isNaN(y)) ){
				i += delta;
				G.addVariable( variable, i );
				J.addVariable( variable, i );
				x = G.getValue();
				y = J.getValue();
			}
			if ( i<b ) path.moveTo( (float)(w/2+(x-originX)*scale), (float)(h/2-(y-originY)*scale) );
			i += delta;
			G.addVariable( variable, i );
			J.addVariable( variable, i );
			x = G.getValue();
			y = J.getValue();
			while ( i<b && !Double.isNaN(x) && !Double.isNaN(y) ){
				path.lineTo( (float)(w/2+(x-originX)*scale), (float)(h/2-(y-originY)*scale) );
				i += delta;
				G.addVariable( variable, i );
				J.addVariable( variable, i );
				x = G.getValue();
				y = J.getValue();
			}
			if ( i >= b ){
				G.addVariable( variable, b );
				J.addVariable( variable, b );
				x = G.getValue();
				y = J.getValue();
				if ( !Double.isNaN(x) && !Double.isNaN(y) ){
					path.lineTo( (float)(w/2+(x-originX)*scale), (float)(h/2-(y-originY)*scale) );
				}
			}
		}
		drawPoint( g, x, y, Color.black, false );
		g.setColor( Color.black );
		g.setStroke( curve );
		g.draw( path );

		G.addVariable( variable, a );
		J.addVariable( variable, a );
		drawPoint( g, G.getValue(), J.getValue(), Color.black, false );
	}
*/	
	
	public void drawRibbon( Graphics2D g ){
		g.setStroke( new BasicStroke( 0.25f) );

		// sort 
		for ( int i=0; i<zorder.length; i++ ){
			zorder[i] = (points[order[i]][0]+points[order[i]+1][0])*xaxis[2]/2 + 
				(points[order[i]][1]+points[order[i]+1][1])*yaxis[2]/2 + 
				(points[order[i]][2]+points[order[i]+1][2])*zaxis[2]/4;
		}
		sort( 0,zorder.length-1,1);
		
		int j;
		double t;
		double[] pt = new double[3];
		for ( int i=0; i<zorder.length; i++ ){
			j = order[i];
			if ( points[j][2] >= 0 && points[j+1][2] >= 0 ){
				drawSwatch( g, j, Color.red );
			} else if ( points[j][2] < 0 && points[j+1][2] < 0 ){
				drawSwatch( g, j, Color.cyan );
			} else {
				t = points[j][2]/(points[j][2]-points[j+1][2]); 
				pt[0] = points[j][0] + t*(points[j+1][0]-points[j][0]);
				pt[1] = points[j][1] + t*(points[j+1][1]-points[j][1]);
			
				if ( points[j][2] >= 0 ){
					drawTriangle( g, points[j], pt, Color.red );
					drawTriangle( g, points[j+1], pt, Color.cyan );
				} else {
					drawTriangle( g, points[j], pt, Color.cyan );
					drawTriangle( g, points[j+1], pt, Color.red );
				}
			}
		}
	}


	public void drawSwatch( Graphics2D g, int i, Color color ){
		try {
			float[] P = toScreenPoint( points[i][0], points[i][1], 0 ); 
			float[] Q = toScreenPoint( points[i][0], points[i][1], points[i][2] );
			float[] R = toScreenPoint( points[i+1][0], points[i+1][1], points[i+1][2] );
			float[] S = toScreenPoint( points[i+1][0], points[i+1][1], 0 );
		
			GeneralPath path = new GeneralPath();
			path.moveTo( P[0], P[1] );
			path.lineTo( Q[0], Q[1] );
			path.lineTo( R[0], R[1] );
			path.lineTo( S[0], S[1] );
			path.lineTo( P[0], P[1] );
		
			g.setColor( Color.black );
			g.draw( new Line2D.Double( P[0], P[1], S[0], S[1]) );
			g.draw( new Line2D.Double( Q[0], Q[1], R[0], R[1]) );

			g.setColor( getColor( rotatePoint(points[i][0],points[i][1],0), rotatePoint(points[i]), rotatePoint(points[i+1]), color ) );
			//g.setStroke( hairline );
			g.setStroke( endline );
			//if ( trans<200 ) g.setStroke( new BasicStroke( 0.125f ) );
			g.draw( path );
			g.fill( path );
		} catch ( Exception e ){
		}
	}


	public void drawTriangle( Graphics2D g, double[] p, double[] q, Color color ){
		try {
			float[] P = toScreenPoint( p[0], p[1], 0 ); 
			float[] Q = toScreenPoint( p );
			float[] R = toScreenPoint( q );
		
			GeneralPath path = new GeneralPath();
			path.moveTo( P[0], P[1] );
			path.lineTo( Q[0], Q[1] );
			path.lineTo( R[0], R[1] );
			path.lineTo( P[0], P[1] );
		
			g.setColor( Color.black );
			g.draw( new Line2D.Double( P[0], P[1], R[0], R[1]) );
			g.draw( new Line2D.Double( Q[0], Q[1], R[0], R[1]) );

			g.setColor( getColor( rotatePoint(p[0],p[1],0), rotatePoint(p), rotatePoint(q), color ) );
			//g.setStroke( hairline );
			g.setStroke( endline );
			//if ( trans<200 ) g.setStroke( new BasicStroke( 0.125f ) );
			g.draw( path );
			g.fill( path );
		} catch ( Exception e ){
		}
	}



	// sign = 1  means increasing order
	// sign = -1 means decreasing order
	public void sort(int a, int b, int sign){
        int lo = a;
        int hi = b;
        double mid;
		int tmp1;
		double tmp2;

        if (b>a){
            mid = zorder[(a+b)/2];
            while(lo<=hi){
                while( (lo<b) && (sign*zorder[lo]<sign*mid) )  ++lo;
                while( (hi>a) && (sign*zorder[hi]>sign*mid) )  --hi;

                if(lo<=hi){
                    tmp1 = order[lo];
                    order[lo] = order[hi];
                    order[hi] = tmp1;
					
					tmp2 = zorder[lo];
                    zorder[lo] = zorder[hi];
                    zorder[hi] = tmp2;
					
					lo++;
					hi--;					
                }
            }
            if(a<hi) sort(a,hi,sign);
            if(lo<b) sort(lo,b,sign);
        }
    }
}