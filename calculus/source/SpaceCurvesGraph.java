import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.util.*;

public class SpaceCurvesGraph extends Graph3D implements Runnable{

	Thread thread;

	public SpaceCurvesGraph( CalculusApplet applet, String f, String g, String j, double a, double b ){
		super();

		this.applet = applet;
		this.a = a;
		this.b = b;
		variable = "t";
		
		shift = true;

		F.parseExpression( f );
		G.parseExpression( g );
		J.parseExpression( j );
	}
	
	
	public double[] evaluate( double t ){
		F.addVariable( variable, t );
		G.addVariable( variable, t );
		J.addVariable( variable, t );
		double[] out = { F.getValue(), G.getValue(), J.getValue() };
		return out;
	}
	
	
	public void draw( Graphics2D g ){
		if ( showGrid ) drawGridLines( g );
		
		drawFunction( g );
		drawEndpoints( g );

		applet.stat.setText( "" );
	}


	public void drawFunction( Graphics2D g ){
		double[] X;
		int grn;
		
		if ( active ){
			double t = time;
			double s;
			for ( int i=0; i<=10; i++ ){
				s = Math.min( b, Math.max( a, t - 1.0 + i/10.0 ) );
				X = evaluate( s );
				grn = (int)(255*(s-a)/(b-a));
				drawPoint3D( g, X[0], X[1], X[2], new Color(0,grn,255-grn,20*i+55), false );
				//drawPoint( g, x, y, new Color(0,0,0,20*i+55), false );
			}
			t = Math.min( b, t );
			grn = (int)(255*(t-a)/(b-a));
			((SpaceCurves)applet).xoftee.originX = t;
			((SpaceCurves)applet).xoftee.repaint();
			((SpaceCurves)applet).yoftee.originX = t;
			((SpaceCurves)applet).yoftee.repaint();
			((SpaceCurves)applet).zoftee.originX = t;
			((SpaceCurves)applet).zoftee.repaint();
			
			X = evaluate( t );
			g.setStroke( new BasicStroke(2.0f) );
			drawLine( g, X[0], 0, 0, 0, 0, 0, Color.gray );
			drawLine( g, 0, X[1], 0, 0, 0, 0, Color.gray );
			drawLine( g, 0, 0, X[2], 0, 0, 0, Color.gray );

			drawLine( g, X[0], X[1], 0, X[0], 0, 0, Color.gray );
			drawLine( g, X[0], 0, X[2], X[0], 0, 0, Color.gray );

			drawLine( g, X[0], X[1], 0, 0, X[1], 0, Color.gray );
			drawLine( g, 0, X[1], X[2], 0, X[1], 0, Color.gray );

			drawLine( g, X[0], 0, X[2], 0, 0, X[2], Color.gray );
			drawLine( g, 0, X[1], X[2], 0, 0, X[2], Color.gray );
			//drawLine( g, X[0], X[1], X[2], 0, X[1], X[2], Color.gray );
			//drawLine( g, X[0], X[1], X[2], X[0], 0, X[2], Color.gray );
			//drawLine( g, X[0], X[1], X[2], X[0], X[1], 0, Color.gray );

			//drawLine( g, X[0], X[1], X[2], 0, X[1], X[2], Color.gray );
			//drawLine( g, X[0], X[1], X[2], X[0], 0, X[2], Color.gray );
			//drawLine( g, X[0], X[1], X[2], X[0], X[1], 0, Color.gray );

			drawLine( g, X[0], X[1], X[2], 0, X[1], X[2], Color.red );
			drawLine( g, X[0], X[1], X[2], X[0], 0, X[2], Color.orange );
			drawLine( g, X[0], X[1], X[2], X[0], X[1], 0, Color.magenta );
			drawPoint3D( g, X[0], X[1], X[2], new Color(0,grn,255-grn), false);
			//drawPoint( g, x, y, Color.black, false );
		} else {
			GeneralPath path = new GeneralPath();
			X = evaluate( a );
			double i = a;
			double delta = units[zoom]/(zoom+1)/(zoom+1);
			float[] P = new float[2];
			while ( i < b ){
				while ( i < b && (Double.isNaN(X[0]) || Double.isNaN(X[1]) || Double.isNaN(X[2])) ){
					i += delta;
					X = evaluate( i );
				}
				try{
					P = toScreenPoint( X[0], X[1], X[2] );
					if ( i<b ) path.moveTo( P[0], P[1] );
				} catch (Exception e){
					X[2] = Double.NaN;
				}

				i += delta;
				X = evaluate( i );
				while ( i<b && !Double.isNaN(X[0]) && !Double.isNaN(X[1]) && !Double.isNaN(X[2]) ){
					try{
						P = toScreenPoint( X[0], X[1], X[2] );
						path.lineTo( P[0], P[1] );
					} catch (Exception e){
						X[2] = Double.NaN;
					}
					i += delta;
					X = evaluate( i );
				}
				if ( i >= b ){
					X = evaluate( b );
					if ( !Double.isNaN(X[0]) && !Double.isNaN(X[1]) && !Double.isNaN(X[2]) ){
						try{
							P = toScreenPoint( X[0], X[1], X[2] );
							path.lineTo( P[0], P[1] );
						} catch (Exception e){
							X[2] = Double.NaN;
						}
					}
				}
			}
			g.setColor( Color.black );
			g.setStroke( new BasicStroke( 1.0f) );
			g.draw( path );
		}
	}

/*
	public void draw( Graphics2D g ){
		g.setColor( Color.black );
		//g.setStroke( curve );
		g.setStroke( new BasicStroke( 1.0f) );

		F.addVariable( "t", a );
		G.addVariable( "t", a );
		J.addVariable( "t", a );
		float[] P = new float[2];
		float[] Q = new float[2];
		try {
			P = toScreenPoint( F.getValue(), G.getValue(), J.getValue() );
		} catch ( Exception e ){
		}
		for ( double i = a; i<b; i += units[zoom]/(zoom+1)/(zoom+1) ){
			F.addVariable( "t", i );
			G.addVariable( "t", i );
			J.addVariable( "t", i );
			try{
				Q = toScreenPoint( F.getValue(), G.getValue(), J.getValue() );
				//g.setColor( new Color() );
				g.draw( new Line2D.Double(P[0],P[1],Q[0],Q[1]) );
			} catch (Exception e){
			}
			P[0] = Q[0];
			P[1] = Q[1];
			P[2] = Q[2];
		}
		F.addVariable( "t", b );
		G.addVariable( "t", b );
		J.addVariable( "t", b );
		try{
			Q = toScreenPoint( F.getValue(), G.getValue(), J.getValue() );
			g.draw( new Line2D.Double(P[0],P[1],Q[0],Q[1]) );
		} catch (Exception e){
		}

		applet.stat.setText( "" );
	}
*/


	public void drawEndpoints( Graphics2D g ){
		double[] X = evaluate( a );
		double[] Y = evaluate( b );
		if ( X[0]*xaxis[2] + X[1]*yaxis[2] + X[2]*zaxis[2] > Y[0]*xaxis[2] + Y[1]*yaxis[2] + Y[2]*zaxis[2] ){ 
			drawPoint3D( g, Y[0], Y[1], Y[2], Color.green, overB );
			drawPoint3D( g, X[0], X[1], X[2], Color.blue, overA );
		} else {
			drawPoint3D( g, X[0], X[1], X[2], Color.blue, overA );
			drawPoint3D( g, Y[0], Y[1], Y[2], Color.green, overB );
		}

/*		F.addVariable( "t", b );
		G.addVariable( "t", b );
		J.addVariable( "t", b );
		g.setStroke( new BasicStroke(2.0f) );
		g.setColor( Color.red);
		g.draw( new Line2D.Double( originX*w + F.getValue()/scale, originY*h - G.getValue()/scale, originX*w, originY*h - G.getValue()/scale ) );
		g.setColor( Color.orange);
		g.draw( new Line2D.Double( originX*w + F.getValue()/scale, originY*h - G.getValue()/scale, originX*w + F.getValue()/scale, originY*h ) );

		drawPoint( g, originX*w + F.getValue()/scale, originY*h - G.getValue()/scale, Color.green );

		F.addVariable( "t", a );
		G.addVariable( "t", a );
		J.addVariable( "t", a );
		g.setStroke( new BasicStroke(2.0f) );
		g.setColor( Color.red);
		g.draw( new Line2D.Double( originX*w + F.getValue()/scale, originY*h - G.getValue()/scale, originX*w, originY*h - G.getValue()/scale ) );
		g.setColor( Color.orange);
		g.draw( new Line2D.Double( originX*w + F.getValue()/scale, originY*h - G.getValue()/scale, originX*w + F.getValue()/scale, originY*h ) );

		drawPoint( g, originX*w + F.getValue()/scale, originY*h - G.getValue()/scale, Color.blue );
*/
	}
	

	public void start(){
		active = true;
		thread = new Thread(this);
		thread.start();
	}
	
	public void run(){
		long start = Calendar.getInstance().getTimeInMillis();
		time = a;
		
		while ( time < b ){
			time = Math.min( b, a + (Calendar.getInstance().getTimeInMillis() - start)/1000.0 );
			try {
				Thread.sleep(20);
			} catch (InterruptedException e){
			}
			repaint();
		}
		while ( time < b + 1 ){
			time = Math.min( b+1, a + (Calendar.getInstance().getTimeInMillis() - start)/1000.0 );
			try {
				Thread.sleep(20);
			} catch (InterruptedException e){
			}
			repaint();
		}
		active = false;
		newBackground = true;
		applet.animate.setEnabled( true );
		
		((SpaceCurves)applet).xoftee.repaint();
		((SpaceCurves)applet).yoftee.repaint();
		((SpaceCurves)applet).zoftee.repaint();
		repaint();
    }


	public void keyReleased( KeyEvent ke ){
		//shift = false;
		newBackground = true;
		xaxis[0] = 1; xaxis[1] = 0; xaxis[2] = 0;
		yaxis[0] = 0; yaxis[1] = 1; yaxis[2] = 0;
		zaxis[0] = 0; zaxis[1] = 0; zaxis[2] = 1;
		repaint();
	}
}