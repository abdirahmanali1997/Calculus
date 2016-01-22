import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import java.util.*;

public class ParametricCurvesGraph extends ParametricGraph implements Runnable{

	Thread thread;

	public ParametricCurvesGraph( CalculusApplet applet, String f, String g, double a, double b ){
		super();

		this.applet = applet;
		this.a = a;
		this.b = b;

		F.parseExpression( f );
		G.parseExpression( g );
	}

	public void draw( Graphics2D g ){
		applet.stat.setText( "" );
	}

	public void drawEndpoints( Graphics2D g ){
		if ( !active ){
			F.addVariable( "t", b );
			G.addVariable( "t", b );
			double x = F.getValue();
			double y = G.getValue();
			g.setStroke( new BasicStroke(2.0f) );
			drawLine( g, x, y, 0, y, Color.red );
			drawLine( g, x, y, x, 0, Color.orange );
			drawPoint( g, x, y, Color.green, false );

			F.addVariable( "t", a );
			G.addVariable( "t", a );
			x = F.getValue();
			y = G.getValue();
			g.setStroke( new BasicStroke(2.0f) );
			drawLine( g, x, y, 0, y, Color.red );
			drawLine( g, x, y, x, 0, Color.orange );
			drawPoint( g, x, y, Color.blue, false );
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
		
		((ParametricApplet)applet).xoftee.repaint();
		((ParametricApplet)applet).yoftee.repaint();
		repaint();
    }
}