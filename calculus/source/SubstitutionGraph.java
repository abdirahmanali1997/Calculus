import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;

public class SubstitutionGraph extends Graph{

	int type;

	public SubstitutionGraph( CalculusApplet applet, String f, double a, double b, int type ){
		super();

		this.applet = applet;
		this.a = a;
		this.b = b;
		this.type = type;
		if ( type == 1 ) this.variable = "x";
		if ( type == 2 ) this.variable = "u";
		
		F.parseExpression( f );
	}


	public void draw( Graphics2D g ){
		double min = Math.min( a, b );
		double max = Math.max( a, b );
		F.addVariable( "x", min );
		double y0 = F.getValue();
		double y1;
		double y2;
		
		double start = Math.max(0,w/2 + (min-originX)*scale);
		double stop = Math.min(w,w/2 + (max-originX)*scale);
		GeneralPath path;
		while ( start <= stop ){
			F.addVariable( variable, originX + (start-w/2)/scale );
			y0 = F.getValue();
			path = new GeneralPath();
			path.moveTo( (float)start, (float)(h/2 + originY*scale) );
			while ( start <= stop && y0 >= 0 && !Double.isNaN(y0) ){
				path.lineTo( (float)start, (float)(h/2 - (y0-originY)*scale) );
				start = start + 1;
				if ( start > stop && start < stop + 1 ) start = stop;
				F.addVariable( variable, originX + (start-w/2)/scale );
				y0 = F.getValue();
			}
			path.lineTo( (float)start, (float)(h/2 + originY*scale) );
			g.setColor( red );
			if ( a > b ) g.setColor( yellow );
			g.fill( path );
			
			while ( start <= stop && Double.isNaN(y0) ){
				start++;
				F.addVariable( variable, originX + (start-w/2)/scale );
				y0 = F.getValue();
			}

			path = new GeneralPath();
			path.moveTo( (float)start, (float)(h/2 + originY*scale) );
			while ( start <= stop && y0 < 0 && !Double.isNaN(y0) ){
				path.lineTo( (float)start, (float)(h/2 - (y0-originY)*scale) );
				start = start + 1;
				if ( start > stop && start < stop + 1 ) start = stop;
				F.addVariable( variable, originX + (start-w/2)/scale );
				y0 = F.getValue();
			}
			path.lineTo( (float)start, (float)(h/2 + originY*scale) );
			g.setColor( yellow );
			if ( a > b ) g.setColor( red );
			g.fill( path );
		}
		
		// program an Adaptive Simpson's Rule for better accuraacy
		// instead of just Simpson's rule
		n = 5000;
		if ( newStat ){ 
			double delta = Math.abs(b-a)/n;
			F.addVariable( variable, min );
			y0 = F.getValue();
			stat = 0.0;
			for ( int i=0; i<n; i++ ){
				F.addVariable( variable, min + (i+0.5)*delta );
				y1 = F.getValue();
				F.addVariable( variable, min + (i+1)*delta );
				y2 = F.getValue();			
				stat += y0+4*y1+y2; 
				y0 = y2;
			}
			stat *= delta/6.0;
			if ( a > b ) stat *= -1;
		}

		applet.stat.setText("");
	}


	public void drawEndpoints( Graphics2D g ){
		g.setStroke( endline );
		F.addVariable( variable, b );
		drawLine( g, b, 0, b, F.getValue(), colorB );
		F.addVariable( variable, a );
		drawLine( g, a, 0, a, F.getValue(), colorA );
		if ( type == 1 ){
			drawPointOnXAxis( g, b, Color.green, overB );
			drawPointOnXAxis( g, a, Color.blue, overA );
		} else if ( type == 2 ){
			drawPointOnXAxis( g, b, Color.magenta, overB );
			drawPointOnXAxis( g, a, Color.red, overA );
		}

		g.setFont( bold );
		g.setColor( new Color(255,255,255,210) );
		String str = "Area \u2248 " + (float)stat;
		double l = g.getFontMetrics().stringWidth(str);
		g.fill( new Rectangle2D.Double( 0, h-30, l+10, 30 ) );
		g.setColor( Color.red );
		if ( stat < 0 ) g.setColor( Color.blue );
		g.drawString( str, 5, (float)(h - 10) );
	}


	public void mouseDragged( MouseEvent me ){
		double[] P = toCartesianPoint( me.getPoint().x, me.getPoint().y );
		if ( newA ){
			a = P[0];
			if ( type == 1 ) applet.a.setValue( a );
			if ( type == 2 ) applet.c.setValue( a );
			applet.stat.setText( "Calculating..." );
		} else if ( newB ) {
			b = P[0];
			if ( type == 1 ) applet.b.setValue( b );
			if ( type == 2 ) applet.d.setValue( b );
			applet.stat.setText( "Calculating..." );
		} else {
			originX -= (me.getPoint().x - POINT.x)/scale;
			originY += (me.getPoint().y - POINT.y)/scale;
			POINT = me.getPoint();
			newBackground = true;
		}
		repaint();
	}	
}