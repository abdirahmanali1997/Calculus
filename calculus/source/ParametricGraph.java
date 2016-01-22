import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;

public class ParametricGraph extends Graph{

	
	public ParametricGraph(){
		super();
		variable = "t";
		xlabel = "x(t)";
		ylabel = "y(t)";
	}

	public void drawEndpoints( Graphics2D g ){
		F.addVariable( "t", b );
		G.addVariable( "t", b );
		drawPoint( g, F.getValue(), G.getValue(), Color.green, false );

		F.addVariable( "t", a );
		G.addVariable( "t", a );
		drawPoint( g, F.getValue(), G.getValue(), Color.blue, false );
	}


	public void drawFunction( Graphics2D g ){
		double x;
		double y;
		int grn;
		
		if ( active ){
			double t = time;
			double s;
			for ( int i=0; i<=10; i++ ){
				s = Math.min( b, Math.max( a, t - 1.0 + i/10.0 ) );
				F.addVariable( variable, s );
				G.addVariable( variable, s );
				x = F.getValue();
				y = G.getValue();
				grn = (int)(255*(s-a)/(b-a));
				drawPoint( g, x, y, new Color(0,grn,255-grn,20*i+55), false );
				//drawPoint( g, x, y, new Color(0,0,0,20*i+55), false );
			}
			t = Math.min( b, t );
			grn = (int)(255*(t-a)/(b-a));
			((ParametricApplet)applet).xoftee.originX = t;
			((ParametricApplet)applet).xoftee.repaint();
			((ParametricApplet)applet).yoftee.originX = t;
			((ParametricApplet)applet).yoftee.repaint();
			
			F.addVariable( variable, t );
			G.addVariable( variable, t );
			x = F.getValue();
			y = G.getValue();
			g.setStroke( new BasicStroke(2.0f) );
			drawLine( g, x, y, 0, y, Color.red );
			drawLine( g, x, y, x, 0, Color.orange );
			drawPoint( g, x, y, new Color(0,grn,255-grn), false );
			//drawPoint( g, x, y, Color.black, false );
		} else {
			GeneralPath path = new GeneralPath();
			F.addVariable( variable, a );
			G.addVariable( variable, a );
			x = F.getValue();
			y = G.getValue();
			double i = a;
			double delta = units[zoom]/(zoom+1)/(zoom+1);
			while ( i < b ){
				while ( i < b && (Double.isNaN(x) || Double.isNaN(y)) ){
					i += delta;
					F.addVariable( variable, i );
					G.addVariable( variable, i );
					x = F.getValue();
					y = G.getValue();
				}
				if ( i<b ) path.moveTo( (float)(w/2+(x-originX)*scale), (float)(h/2-(y-originY)*scale) );
				i += delta;
				F.addVariable( variable, i );
				G.addVariable( variable, i );
				x = F.getValue();
				y = G.getValue();
				while ( i<b && !Double.isNaN(x) && !Double.isNaN(y) ){
					path.lineTo( (float)(w/2+(x-originX)*scale), (float)(h/2-(y-originY)*scale) );
					i += delta;
					F.addVariable( variable, i );
					G.addVariable( variable, i );
					x = F.getValue();
					y = G.getValue();
				}
				if ( i >= b ){
					F.addVariable( variable, b );
					G.addVariable( variable, b );
					x = F.getValue();
					y = G.getValue();
					if ( !Double.isNaN(x) && !Double.isNaN(y) ){
						path.lineTo( (float)(w/2+(x-originX)*scale), (float)(h/2-(y-originY)*scale) );
					}
				}
			}
			drawPoint( g, x, y, Color.black, false );
			g.setColor( Color.black );
			g.setStroke( curve );
			g.draw( path );

			F.addVariable( variable, a );
			G.addVariable( variable, a );
			drawPoint( g, F.getValue(), G.getValue(), Color.black, false );
		}
	}


	public void drawVerticalLines( Graphics2D g ){
	}

	public void mousePressed(MouseEvent me){
		POINT = me.getPoint();
		requestFocus();
	}

	public void mouseDragged(MouseEvent me){
		Point p = me.getPoint();
		originX -= (me.getPoint().x - POINT.x)/scale;
		originY += (me.getPoint().y - POINT.y)/scale;
		POINT = me.getPoint();
		newBackground = true;
		repaint();
	}	
}