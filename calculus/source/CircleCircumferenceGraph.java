import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;

public class CircleCircumferenceGraph extends Graph{

	public CircleCircumferenceGraph( CalculusApplet applet, int n ){
		super();

		this.applet = applet;
		this.n = Math.max( n,3 );
		
		// used to draw circle (much more accurate than Ellipse2D.Double() )
		F.parseExpression( "sqrt(1-x^2)" );
		G.parseExpression( "-sqrt(1-x^2)" );
	}

	public void draw( Graphics2D g ){
		if ( applet.choice.getSelectedItem().equals("Inscribed Polygon") ){
			draw( g, 1.0 );
			applet.setStat( "", (float)(2*n*Math.sin(Math.PI/n)), Color.red );
			applet.stat2.setForeground( Color.black );
			applet.stat2.setText( " < Circumference " );
			applet.stat3.setText( "" );
		} else if ( applet.choice.getSelectedItem().equals("Circumscribed Polygon") ){
			draw( g, 1/Math.cos(Math.PI/n) );
			applet.stat.setForeground( Color.black );
			applet.stat.setText( " Circumference < " );
			applet.setStat2( "", (float)(2*n*Math.tan(Math.PI/n)), Color.blue );
			applet.stat3.setText( "" );
		} else if ( applet.choice.getSelectedItem().equals("Show Both") ){
			draw( g, 1/Math.cos(Math.PI/n) );
			draw( g, 1.0 );
			applet.setStat( "", (float)(2*n*Math.sin(Math.PI/n)), Color.red );
			applet.stat2.setForeground( Color.black );
			applet.stat2.setText( " < Circumference < " );
			applet.setStat3( "", (float)(2*n*Math.tan(Math.PI/n)), Color.blue );
		} else if ( applet.choice.getSelectedItem().equals("Show Sequences") ){
			drawSequences( g );
		}

	}

	public void draw( Graphics2D g, double rad ){
		double[] P = toScreenPoint(rad,0);
		GeneralPath path = new GeneralPath();
		//path.moveTo(P[0],P[1]); // casting to float is necessary for older versions of java
		path.moveTo((float)P[0],(float)P[1]);

		for ( int i=1; i<n+1; i++ ){
			P = toScreenPoint(rad*Math.cos(2*Math.PI*i/n),rad*Math.sin(2*Math.PI*i/n));
			path.lineTo((float)P[0],(float)P[1]);
		}
		g.setColor( red );
		g.setStroke( curve );
		if ( rad > 1.0 ) g.setColor( blue );
		g.draw( path );
	}
	
	
	public void drawEndpoints( Graphics2D g ){
	}

	
	public void drawFunction( Graphics2D g ){
		if ( !applet.choice.getSelectedItem().equals("Show Sequences") ){
			drawFunction( g, G, Color.black );
			drawFunction( g, F, Color.black );
		}
	}
	
	
	public void drawSequences( Graphics2D g ){
		int A = Math.max( 3, (int)(originX - w/scale/2) );
		int B = Math.max( 3, (int)(originX + w/scale/2) );

//		int step = (int)( Math.max(1,10*m/pixels) );
//		if ( shift ) step = 1;
		for ( int i=A; i<B+1; i+=1 ){
			drawPoint( g, i, 2*i*Math.sin(Math.PI/i), red, i == N );
			drawPoint( g, i, 2*i*Math.tan(Math.PI/i), blue, i == N );
		}
	}
	
	int N = 0;
	public void mouseMoved( MouseEvent me ){
		N = (int)(originX + (me.getPoint().x - w/2)/scale + 0.5 );
		if ( N > 2 && applet.choice.getSelectedItem().equals("Show Sequences") ){
			applet.setStat( "", (float)(2*N*Math.sin(Math.PI/N )), Color.red );
			applet.stat2.setForeground( Color.black );
			applet.stat2.setText( " < Circumference < " );
			applet.stat3.setForeground( Color.blue );
			applet.stat3.setText( applet.formatDouble((float)(2*N*Math.tan(Math.PI/N))) );
			repaint();
		}
	}
	
	public void mousePressed( MouseEvent me ){
		POINT = me.getPoint();
/*		double xa = POINT.x - w/2 - (a - originX)*scale;
		double xb = POINT.x - w/2 - (b - originX)*scale;
		double y = POINT.y-H;
		if ( xa*xa + y*y < rr ){
			newA = true;
		} else if ( xb*xb + y*y < rr ){
			newB = true;
		}*/
		requestFocus();
	}
}