import java.awt.*;
import java.awt.geom.*;
import java.util.*;

public class PolarCurvesGraph extends PolarGraph implements Runnable{

	Thread thread;

	public PolarCurvesGraph( CalculusApplet applet, String f, double a, double b ){
		super();

		this.applet = applet;
		this.a = a;
		this.b = b;

		F.parseExpression( f );
	}


	public void draw( Graphics2D g ){
		applet.stat.setText( "" );
	}


	public void drawEndpoints( Graphics2D g ){
		if ( !active ){
			g.setStroke( new BasicStroke(2.0f) );
			F.addVariable( "t", b );
			double val = F.getValue();
			drawLine( g, 0, 0, val*Math.cos(b), val*Math.sin(b), Color.green );

			F.addVariable( "t", a );
			val = F.getValue();
			drawLine( g, 0, 0, val*Math.cos(a), val*Math.sin(a), Color.blue );

			drawArrow( g, 0, 0, Rb*Math.cos(b)/scale, Rb*Math.sin(b)/scale, Color.green, overB );
			drawArrow( g, 0, 0, Rb*Math.cos(a)/scale, Rb*Math.sin(a)/scale, Color.blue, overA );
		}
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
		
		((PolarApplet)applet).rtheta.repaint();
		repaint();
    }
}