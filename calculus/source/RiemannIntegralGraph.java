import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;

public class RiemannIntegralGraph extends Graph{
	
	double[] xcoors;
	int interval;

	public RiemannIntegralGraph( CalculusApplet applet, String f, double a, double b, int n ){
		super();

		this.applet = applet;
		this.a = a;
		this.b = b;
		this.n = Math.max( n,0 );
		
		xcoors = new double[ 1001 ];
		
		getRandomXcoors();
		
		F.parseExpression( f );
	}

	
	public void draw( Graphics2D g ){
		double delta;
		double y;
		
		stat = 0.0;
		
		for ( int i=0; i<n; i++ ){
			F.addVariable( variable, xcoors[2*i+1] );
			y = F.getValue();
			delta = xcoors[2*i+2]-xcoors[2*i];
			stat += y*delta;
			drawRectangle( g, xcoors[2*i], delta, y, i == interval );
		}
		newStat = false;
		
		if ( b < a ) stat *= -1;
		
		if ( n == 0 ) stat = 0.0;
		if ( stat < 0 ) applet.setStat( "Area \u2248 ", stat, Color.blue );
		else applet.setStat( "Area \u2248 ", stat, Color.red );
	}

	
	public void drawEndpoints( Graphics2D g ){
		if ( interval == -1 ){
			F.addVariable( variable, b );
			drawLine( g, b, 0, b, F.getValue(), colorB );
			F.addVariable( variable, a );
			drawLine( g, a, 0, a, F.getValue(), colorA );
			
			drawPointOnXAxis( g, b, Color.green, overB );
			drawPointOnXAxis( g, a, Color.blue, overA );
		} else {
			F.addVariable( variable, xcoors[2*interval+2] );
			drawLine( g, xcoors[2*interval+2], 0, xcoors[2*interval+2], F.getValue(), colorB );
			
			F.addVariable( variable, xcoors[2*interval] );
			drawLine( g, xcoors[2*interval], 0, xcoors[2*interval], F.getValue(), colorA );
			
			F.addVariable( variable, xcoors[2*interval+1] );
			g.setStroke( dashed );
			drawLine( g, xcoors[2*interval+1], 0, xcoors[2*interval+1], F.getValue(), colorA );

			drawPointOnXAxis( g, xcoors[2*interval+2], Color.green, overB );
			drawPointOnXAxis( g, xcoors[2*interval], Color.blue, overA );
			drawPointOnXAxis( g, xcoors[2*interval+1], Color.red, overC );
		}
	}
	
	
	// update so that it only draws rectangles that are in the viewable region of the graph
	// x, width, height are in Cartesian coordinates/scale
	private void drawRectangle( Graphics2D g, double x, double width, double height ){
		if ( !(Double.isNaN(height) || Double.isInfinite(height) ) ){
			g.setColor( red );
			if ( (b-a)*height < 0 ) g.setColor( yellow );
			double[] upperLeft = toScreenPoint( x, (height>0?height:0) );
			g.fill( new Rectangle2D.Double( upperLeft[0], upperLeft[1], Math.abs(width*scale), Math.abs(height*scale) ) );
		}
	}

	
	private void drawRectangle( Graphics2D g, double x, double width, double height, boolean border ){
		if ( !(Double.isNaN(height) || Double.isInfinite(height) ) ){
			g.setColor( red );
			if ( (b-a)*height < 0 ) g.setColor( yellow );
			double[] upperLeft = toScreenPoint( x, (height>0?height:0) );
			g.fill( new Rectangle2D.Double( upperLeft[0], upperLeft[1], Math.abs(width*scale), Math.abs(height*scale) ) );
			g.setColor( Color.black );
			if ( border ){
				g.setStroke( boldline );
				g.draw( new Rectangle2D.Double( upperLeft[0], upperLeft[1], Math.abs(width*scale), Math.abs(height*scale) ) );
				g.setStroke( endline );
			}
		}
	}
	
	
	// a quick fix to overwrite keyTyped and call getRandomXcoors
	public void keyTyped( KeyEvent ke ){
		getRandomXcoors();
	}
	
	
	public void getRandomXcoors(){
		interval = -1;
		//double delta = (b-a)/n/2;
		xcoors[0] = a;
		for ( int i=1; i<2*n; i++ ){
			//xcoors[i] = a + i*delta;
			xcoors[i] = a + Math.random()*(b-a);
		}
		xcoors[ 2*n ] = b;
		sort( 0, 2*n, 1 );
	}
	
	
	// sign = 1  means increasing order
	// sign = -1 means decreasing order
	public void sort(int a, int b, int sign){
        int lo = a;
        int hi = b;
        double mid;
		double tmp;
		
        if (b>a){
            mid = xcoors[(a+b)/2];
            while(lo<=hi){
                while( (lo<b) && (sign*xcoors[lo]<sign*mid) )  ++lo;
                while( (hi>a) && (sign*xcoors[hi]>sign*mid) )  --hi;
				
                if(lo<=hi){					
					tmp = xcoors[lo];
                    xcoors[lo] = xcoors[hi];
                    xcoors[hi] = tmp;
					
					lo++;
					hi--;					
                }
            }
            if(a<hi) sort(a,hi,sign);
            if(lo<b) sort(lo,b,sign);
        }
    }
	
	
	
	public void mouseClicked( MouseEvent me ){
		POINT = me.getPoint();
		double[] P = toCartesianPoint( POINT.x, POINT.y );
		if ( me.getClickCount() == 1 ){
			
			double xi = originX + (POINT.x-w/2)/scale;
			interval = -1;
			while ( interval < n && xcoors[2*interval+2] < xi ){
				interval++;
			}
			if ( interval == n ) interval = -1;
			repaint();
		} else if ( me.getClickCount() > 1 ){
			// set center so that P stays in same place
			int z = Math.max( 0, zoom-1 );
			originX = P[0] - units[z]*(POINT.x-w/2)/pixels;
			originY = P[1] + units[z]*(POINT.y-h/2)/pixels;
			// zoom in on point P
			applet.actionPerformed( new ActionEvent(applet.zoomin,ActionEvent.ACTION_PERFORMED,"zoom") );
		} 
	}

	
	public void mousePressed( MouseEvent me ){
		POINT = me.getPoint();
		double y = POINT.y-H;
		if ( interval == -1 ){
			double xa = POINT.x - w/2 - (a - originX)*scale;
			double xb = POINT.x - w/2 - (b - originX)*scale;
			if ( xa*xa + y*y < rr ){
				newA = true;
			} else if ( xb*xb + y*y < rr ){
				newB = true;
			}
		} else {
			double xa = POINT.x - w/2 - (xcoors[2*interval] - originX)*scale;
			double xc = POINT.x - w/2 - (xcoors[2*interval+1] - originX)*scale;
			double xb = POINT.x - w/2 - (xcoors[2*interval+2] - originX)*scale;
			if ( xc*xc + y*y < rr ){
				newC = true;
			} else if ( xa*xa + y*y < rr ){
				newA = true;
			} else if ( xb*xb + y*y < rr ){
				newB = true;
			}
		}
		repaint();
		requestFocus();
	}
	
	
	public void mouseDragged(MouseEvent me){
		double[] P = toCartesianPoint( me.getPoint().x, me.getPoint().y );
		if ( newA ){
			if ( interval == -1 || interval == 0 ){
				if ( P[0] < xcoors[1] ){
					a = P[0];
					applet.a.setValue( a );
					xcoors[0] = a;
				}
			} else if ( xcoors[2*interval - 1] <= P[0] && P[0] <= xcoors[2*interval+1] ){
				xcoors[2*interval] = P[0];
			}
			newStat = true;
			applet.updateGraphs( this );
		} else if ( newB ){
			if ( interval == -1 || interval == n-1 ){
				if ( P[0] > xcoors[2*n-1] ){
					b = P[0];
					applet.b.setValue( b );
					xcoors[2*n] = b;
				}
			} else if ( xcoors[2*interval+1] <= P[0] && P[0] <= xcoors[2*interval+3] ){
				xcoors[2*interval+2] = P[0];
			}
			newStat = true;
			applet.updateGraphs( this );
		} else if ( newC ){
			c = P[0];
			//applet.c.setValue( c );		
			if ( interval != -1 && xcoors[2*interval] <= c && c <= xcoors[2*interval+2]) 
				xcoors[2*interval+1] = c;
			newStat = true;
			applet.updateGraphs( this );
		} else {
			originX -= (me.getPoint().x - POINT.x)/scale;
			originY += (me.getPoint().y - POINT.y)/scale;
			POINT = me.getPoint();
			newBackground = true;
			repaint();
		}
	}		
}