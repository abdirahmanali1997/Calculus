import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.*;

public class SurfacesGraph extends Graph3D{

	int N;
	int M;
	double[][][] points;
	float[][][] screen;
	int[][] order;
	double[] zorder;
	
	boolean drag = false;

	public SurfacesGraph( CalculusApplet applet, String f, double a, double b, double c, double d, int n ){
		super();

		this.applet = applet;
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
		this.n = Math.max( n,0 );

		Point axis = new Point( 1,0 );
			xaxis = rotate( xaxis, -1.5, axis );
			yaxis = rotate( yaxis, -1.5, axis );
			zaxis = rotate( zaxis, -1.5, axis );

		F.parseExpression( f );

		newPoints();
	}


	public void draw( Graphics2D g ){
		if ( newStat ){
			newPoints();
			newStat = false;
		}

		if ( showGrid ) drawGridLines( g );
		if ( !shift ) drawAxes( g );
		
		toScreenPoints();
		String str = applet.choice.getSelectedItem().toString();
		if ( str.equals( "Wire Frame - Transparent" ) ){
			drawTransparentWireFrame( g );
		} else if ( str.equals( "Opaque" ) ){
			drawSurface( g, 255 );
		} else if ( str.equals( "Transparent" ) ){
			drawSurface( g, 150 );
		} else if ( str.equals( "Wire Frame - Opaque" ) ){
			drawOpaqueWireFrame( g );
		}
		applet.stat.setText("");
	}

	
	public void drawEndpoints( Graphics2D g ){
/*		drawPoint3D( g, a, 0, 0, Color.blue, overA );
		drawPoint3D( g, b, 0, 0, Color.green, overB );
		drawPoint3D( g, 0, c, 0, Color.red, overC );
		drawPoint3D( g, 0, d, 0, Color.magenta, overD );*/
	}


	public void drawFunction( Graphics2D g ){
	}
	
	
	public void drawOpaqueWireFrame( Graphics2D g ){		
		if ( !shift ) drawAxes( g );

		g.setStroke( new BasicStroke( 0.25f) );

		// sort 
		for ( int i=0; i<zorder.length; i++ ){
			zorder[i] = points[order[i][0]][order[i][1]][0]*xaxis[2] + points[order[i][0]][order[i][1]][1]*yaxis[2] + points[order[i][0]][order[i][1]][2]*zaxis[2];
		}
		sort( 0,order.length-1,1);
		
		float[] NE;
		float[] NW;
		float[] SW;
		float[] SE;
		GeneralPath path;
		for ( int i=0; i<order.length; i++ ){
			if ( order[i][0]<M && order[i][1]<N ){
				SW = screen[order[i][0]][order[i][1]];
				SE = screen[order[i][0]+1][order[i][1]];
				NE = screen[order[i][0]+1][order[i][1]+1];
				NW = screen[order[i][0]][order[i][1]+1];

if ( SW!=null && SE!=null && NE!=null && NW!=null ){
				path = new GeneralPath();
				path.moveTo( NW[0], NW[1] );
				path.lineTo( SW[0], SW[1] );
				path.lineTo( SE[0], SE[1] );
				path.lineTo( NE[0], NE[1] );
				path.lineTo( NW[0], NW[1] );
				g.setColor( Color.white );
				g.fill( path );
				g.setColor( (Color)applet.colorchoice.getSelectedItem() );
				g.draw( path );
}
			}
		}
	}


	public void drawSurface( Graphics2D g, int trans ){
		if ( !shift ) drawAxes( g );

		g.setStroke( new BasicStroke( 0.25f) );

		// sort
		for ( int i=0; i<zorder.length; i++ ){
			zorder[i] = points[order[i][0]][order[i][1]][0]*xaxis[2] + points[order[i][0]][order[i][1]][1]*yaxis[2] + points[order[i][0]][order[i][1]][2]*zaxis[2];
		}
		sort( 0,order.length-1,1);
		
		for ( int i=0; i<order.length; i++ ){
			if ( order[i][0]<M && order[i][1]<N ){
				drawSwatch( g, order[i][0], order[i][1], trans );
			}
		}
	}	


	public void drawSwatch( Graphics2D g, int i, int j, int trans ){
		float[] P = screen[i][j]; 
		float[] Q = screen[i+1][j];
		float[] R = screen[i+1][j+1];
		float[] S = screen[i][j+1];

		if ( P!=null && Q!=null && R!=null && S!=null ){
			GeneralPath path = new GeneralPath();
			path.moveTo( P[0], P[1] );
			path.lineTo( Q[0], Q[1] );
			path.lineTo( R[0], R[1] );
			path.lineTo( S[0], S[1] );
			path.lineTo( P[0], P[1] );

			Color color = (Color)applet.colorchoice.getSelectedItem();
			if ( color == ColorComboBox.rainbow ){
				float u = (float)i/M;
				float v = (float)j/N;
				int[] rgb = { (int)(255*v), (int)(255*(1-u*v)), (int)(255*u) };
				color = new Color( rgb[0], rgb[1], rgb[2] );
			}

			g.setColor( getColor( rotatePoint(points[i][j]), rotatePoint(points[i+1][j]), rotatePoint(points[i][j+1]), color, trans ) );
			g.setStroke( new BasicStroke( 1.0f ) );
			if ( trans<200 ) g.setStroke( new BasicStroke( 0.125f ) );
			g.fill( path );
			//g.setStroke( new BasicStroke( 0.25f ) );
			//g.setColor( Color.black );
			g.draw( path );
		}
/*
double[] P = rotatePoint( points[i][j] );
double[] Q = rotatePoint( points[i+1][j] );
double[] R = rotatePoint( points[i+1][j+1] );
double[] S = rotatePoint( points[i][j+1] );
double[] T = rotatePoint( originX, originY, 0 );

double[] p = { w/2 + scale*Z*(P[0] - T[0])/(Z - P[2] + T[2]), h/2 - scale*Z*(P[1] - T[1])/(Z - P[1] + T[1]), P[2] - T[2] };
double[] q = { w/2 + scale*Z*(Q[0] - T[0])/(Z - Q[2] + T[2]), h/2 - scale*Z*(Q[1] - T[1])/(Z - Q[1] + T[1]), Q[2] - T[2] };
double[] r = { w/2 + scale*Z*(R[0] - T[0])/(Z - R[2] + T[2]), h/2 - scale*Z*(R[1] - T[1])/(Z - R[1] + T[1]), R[2] - T[2] };
double[] s = { w/2 + scale*Z*(S[0] - T[0])/(Z - S[2] + T[2]), h/2 - scale*Z*(S[1] - T[1])/(Z - S[1] + T[1]), S[2] - T[2] };
			
GeneralPath path = new GeneralPath();
path.moveTo( (float)p[0], (float)p[1] );
path.lineTo( (float)q[0], (float)q[1] );
path.lineTo( (float)r[0], (float)r[1] );
path.lineTo( (float)s[0], (float)s[1] );

g.setColor( getColor( P, Q, S, (Color)applet.colorchoice.getSelectedItem(), trans ) );
g.setStroke( new BasicStroke( 1.0f ) );
if ( trans<200 ) g.setStroke( new BasicStroke( 0.125f ) );
g.draw( path );
g.fill( path );
*/
	}


	public void drawTransparentWireFrame( Graphics2D g ){		
		float[] NW;
		float[] NE;
		float[] SW;
		float[] SE;

		g.setStroke( new BasicStroke( 0.25f) );
		//g.setColor( new Color(0,0,0,200) );
		g.setColor( (Color)applet.colorchoice.getSelectedItem() );
		for ( int i=0; i<M; i++ ){
			for ( int j=0; j<N; j++ ){
				SW = screen[i][j];
				SE = screen[i+1][j];
				NE = screen[i+1][j+1];
				NW = screen[i][j+1];
				
if ( SW!=null && SE!=null && NE!=null && NW!=null ){
//if ( i == M-1 && j<
				//g.setColor( new Color(0,0,0, Math.min( 255, (int)(100 + 7.5*(points[i][j][0]*xaxis[2] + points[i][j][1]*yaxis[2] + points[i][j][2]*zaxis[2]+10)))) );
				g.draw( new Line2D.Double( SW[0], SW[1], NW[0], NW[1]) );
				g.draw( new Line2D.Double( SW[0], SW[1], SE[0], SE[1]) );
				g.draw( new Line2D.Double( NE[0], NE[1], NW[0], NW[1]) );
				g.draw( new Line2D.Double( NE[0], NE[1], SE[0], SE[1]) );
}
			}
		}
	}


	public void drawVerticalLines( Graphics2D g ){
		g.setColor( Color.black );
		g.setStroke( new BasicStroke(1.0f) );
		
		F.addVariable( "x", a );
		g.draw( new Line2D.Double( originX*w + a*scale, originY*h, originX*w + a*scale, originY*h - F.getValue()*scale ) );

		F.addVariable( "x", b );
		g.draw( new Line2D.Double( originX*w + b*scale, originY*h, originX*w + b*scale, originY*h - F.getValue()*scale ) );
	}
	
	
	public double getValue( double x, double y ){
		F.addVariable( "x", x );
		F.addVariable( "y", y );
		return F.getValue();
	}
	
	
	public void newPoints(){		
		N = (int)( (b-a)*n );
		M = (int)( (d-c)*n );
		points = new double[M+1][N+1][3];
		screen = new float[M+1][N+1][3];
		order = new int[N*M+N+M+1][2];
		zorder = new double[N*M+N+M+1];
		for ( int i=0; i<M+1; i++ ){
			for ( int j=0; j<N+1; j++ ){
				points[i][j][0] = a + j*(b-a)/N;
				points[i][j][1] = c + i*(d-c)/M;
				points[i][j][2] = getValue( points[i][j][0], points[i][j][1] );
				
				order[j+(N+1)*i][0] = i;
				order[j+(N+1)*i][1] = j;
			}
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
 

	public void toScreenPoints(){
		for ( int i=0; i<M+1; i++ ){
			for ( int j=0; j<N+1; j++ ){
				try {
					screen[i][j] = toScreenPoint( points[i][j] );
				} catch ( Exception e ){
					screen[i][j] = null;
				}
			}
		}
	}


	public void mousePressed(MouseEvent me){
		POINT = me.getPoint();
/*		double xa = POINT.x - w/2 - (a - originX)*scale;
		double xb = POINT.x - w/2 - (b - originX)*scale;
		double xc = POINT.y - h/2 + (c - originY)*scale;
		double xd = POINT.y - h/2 + (d - originY)*scale;
		if ( xa*xa + (POINT.y-H)*(POINT.y-H) < rr ){
			newA = true;
		} else if ( xb*xb + (POINT.y-H)*(POINT.y-H) < rr ){
			newB = true;
		} else if ( xc*xc + (POINT.x-W)*(POINT.x-W) < rr ){
			newC = true;
		} else if ( xd*xd + (POINT.x-W)*(POINT.x-W) < rr ){
			newD = true;
		}*/
		requestFocus();
	}


	public void mouseDragged(MouseEvent me){
		drag = true;
		Point p = me.getPoint();
		if ( true || shift ){			
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
				a = P[0];
				applet.a.setValue( a );
				applet.stat.setText( "Calculating..." );
			} else if ( newB ){
				b = P[0];
				applet.b.setValue( b );
				applet.stat.setText( "Calculating..." );
			} else if ( newC ){
				c = P[1];
				applet.c.setValue( c );
				applet.stat.setText( "Calculating..." );
			} else if ( newD ){
				d = P[1];
				applet.d.setValue( d );
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


	public void mouseReleased(MouseEvent me){
		drag = false;
		repaint();
	}	


	public void keyReleased( KeyEvent ke ){
	}
}