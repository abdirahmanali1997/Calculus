import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.util.*;

public class DoubleIntegralGraph extends Graph3D{

	double[][][] points;
	int[][] order;
	double[] zorder;
	
	double dx;
	double dy;
	
	public DoubleIntegralGraph( CalculusApplet applet, String f, String g, String j, double a, double b, int n ){
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


	public double getValue( double x, double y ){
		F.addVariable( "x", x );
		F.addVariable( "y", y );
		return F.getValue();
	}
	
	public void newPoints(){		
		points = new double[n][n][3];
		order = new int[n*n][2];
		zorder = new double[n*n];
		
		double x;

		G.addVariable( "x", a );
		J.addVariable( "x", a );
		c = Math.min( G.getValue(), J.getValue() );
		d = Math.max( G.getValue(), J.getValue() );
		double min;
		double max;
		for ( double i=Math.min(a,b); i<Math.max(a,b); i+=1/scale ){
			G.addVariable( "x", i );
			J.addVariable( "x", i );
			min = Math.min( G.getValue(), J.getValue() );
			max = Math.max( G.getValue(), J.getValue() );
			if ( !Double.isNaN(min) && min < c ) c = min;
			if ( !Double.isNaN(max) && max > d ) d = max;
		}

		dx = Math.abs(b-a)/n;
		dy = (d-c)/n;
		
		for ( int i=0; i<n; i++ ){
			x = a + i*(b-a)/n;
			G.addVariable( "x", x );
			J.addVariable( "x", x );
			min = Math.min( G.getValue(), J.getValue() );
			max = Math.max( G.getValue(), J.getValue() );
			for ( int j=0; j<n; j++ ){
				points[i][j][0] = x;
				points[i][j][1] = c + j*(d-c)/n;
				points[i][j][2] = 0;
				if ( points[i][j][1] >= min && points[i][j][1] <= max )
					points[i][j][2] = getValue( points[i][j][0] + dx/2, points[i][j][1] + dy/2 );
				
				order[j+n*i][0] = i;
				order[j+n*i][1] = j;
				
				stat += points[i][j][2];
			}
		}
	}

	public void draw( Graphics2D g ){
		if ( newStat ){
			stat = 0;
			newPoints();
			newStat = false;
			stat *= (b-a)*(d-c)/n/n;
			if ( n==0 ) stat = 0;
		}

		if ( showGrid ) drawGridLines( g );
		if ( !shift ) drawAxes( g );
						
		drawBlocks( g );

		if ( stat < 0 ) applet.setStat( "Volume \u2248 ", stat, Color.cyan );
		else applet.setStat( "Volume \u2248 ", stat, Color.red );
	}


	public void drawEndpoints( Graphics2D g ){
		drawPointOnXAxis( g, b, Color.green, overB );
		drawPointOnXAxis( g, a, Color.blue, overA );
	}


	public void drawFunction( Graphics2D g ){
	}
	
	
	public void drawBlocks( Graphics2D g ){
		g.setStroke( new BasicStroke( 0.25f) );

		// sort 
		double[] p;
		for ( int i=0; i<zorder.length; i++ ){
			p = points[order[i][0]][order[i][1]];
			if ( Math.abs(zaxis[2]) < 0.98 ){
				zorder[i] = p[0]*xaxis[2] + p[1]*yaxis[2] + 0*p[2]*zaxis[2];
			} else {
				zorder[i] = 1/((p[0] + dx/2 - originX)*(p[0] + dx/2 - originX) + (p[1] + dy/2 - originY)*(p[1] + dy/2 - originY));
			}
		}
		sort( 0,zorder.length-1,1);
		
		int j;
		for ( int i=0; i<zorder.length; i++ ){
			if ( order[i][0] < n && order[i][1] < n && points[order[i][0]][order[i][1]][2] != 0)
			drawBlock( g, order[i][0], order[i][1] );
		}
	}
	
	
	public void drawBlock( Graphics2D g, int i, int j ){
		double[][] top = { {points[i][j][0], points[i][j][1], points[i][j][2]}, 
						   {points[i][j][0]+dx, points[i][j][1], points[i][j][2]}, 
						   {points[i][j][0]+dx, points[i][j][1]+dy, points[i][j][2]}, 
						   {points[i][j][0], points[i][j][1]+dy, points[i][j][2]} };
		double[][] bot = { {points[i][j][0], points[i][j][1], 0}, 
						{points[i][j][0]+dx, points[i][j][1], 0}, 
						{points[i][j][0]+dx, points[i][j][1]+dy, 0}, 
						{points[i][j][0], points[i][j][1]+dy, 0} };

		if ( points[i][j][2] > 0 ){
			drawPolygon( g, top[0], top[1], top[2], top[3], Color.red ); // points are in counterclockwise order from outside of block
			drawPolygon( g, bot[0], bot[3], bot[2], bot[1], Color.red );
			drawPolygon( g, bot[0], bot[1], top[1], top[0], Color.red );
			drawPolygon( g, bot[1], bot[2], top[2], top[1], Color.red );
			drawPolygon( g, bot[2], bot[3], top[3], top[2], Color.red );
			drawPolygon( g, bot[3], bot[0], top[0], top[3], Color.red );
		} else {
			drawPolygon( g, top[0], top[3], top[2], top[1], Color.cyan );
			drawPolygon( g, bot[0], bot[1], bot[2], bot[3], Color.cyan );
			drawPolygon( g, bot[0], top[0], top[1], bot[1], Color.cyan );
			drawPolygon( g, bot[1], top[1], top[2], bot[2], Color.cyan );
			drawPolygon( g, bot[2], top[2], top[3], bot[3], Color.cyan );
			drawPolygon( g, bot[3], top[3], top[0], bot[0], Color.cyan );
		}
	}
	

	public void drawPolygon( Graphics2D g, double[] ll, double[] lr, double[] ur, double[] ul, Color color ){
		try {
			float[] P = toScreenPoint( ll ); 
			float[] Q = toScreenPoint( lr );
			float[] R = toScreenPoint( ur );
			float[] S = toScreenPoint( ul );

			if ( (Q[0]-P[0])*(S[1]-P[1]) < (Q[1]-P[1])*(S[0]-P[0]) ){ // only draw polygon if visible side is showing
				GeneralPath path = new GeneralPath();
				path.moveTo( P[0], P[1] );
				path.lineTo( Q[0], Q[1] );
				path.lineTo( R[0], R[1] );
				path.lineTo( S[0], S[1] );
		
				g.setColor( Color.black );
				g.draw( path );
		
				g.setColor( getColor( rotatePoint(ll), rotatePoint(lr), rotatePoint(ur), color ) );
				//g.setStroke( new BasicStroke( 1.0f ) );
				g.setStroke( hairline );
				//if ( trans<200 ) g.setStroke( new BasicStroke( 0.125f ) );
				g.fill( path );
			}
		} catch ( Exception e ){
		}
	}



	// sign = 1  means increasing order
	// sign = -1 means decreasing order
	public void sort(int a, int b, int sign){
        int lo = a;
        int hi = b;
        double mid;
		int[] tmp1;
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