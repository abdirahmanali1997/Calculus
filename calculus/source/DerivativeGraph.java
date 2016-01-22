import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;

import org.nfunk.jep.*;

public class DerivativeGraph extends Graph{

	public DerivativeGraph( CalculusApplet applet, String f, String g, double a ){
		super();
				
		this.applet = applet;
		this.a = a;
		
		F.parseExpression( f );
		G.parseExpression( g );
	}

	public void draw( Graphics2D g ){
		if ( Math.abs( a ) > 0.000000000001 ){
			GeneralPath path = new GeneralPath();
			float i = -1.0f;
			F.addVariable( variable, originX-w/2/scale );
			double t = F.getValue();
			F.addVariable( variable, originX-w/2/scale + a );
			double y = (F.getValue()-t)/a;
			while ( i < w ){
				while ( i<=w && (Double.isNaN(y=(F.getValue()-t)/a) || Math.abs(y-originY)>h/scale) ){
					F.addVariable( variable, originX + (++i-w/2)/scale );
					t = F.getValue();
					F.addVariable( variable, originX + (i-w/2)/scale + a );
				}
				if ( i < w ) path.moveTo( i, (float)(h/2 - (y-originY)*scale) );
				F.addVariable( variable, originX + (++i-w/2)/scale );
				t = F.getValue();
				F.addVariable( variable, originX + (i-w/2)/scale + a );
				while ( i<=w && !Double.isNaN(y=(F.getValue()-t)/a) && Math.abs(y-originY)<h/scale ){					
					path.lineTo( i, (float)(h/2 - (y-originY)*scale) );
					F.addVariable( variable, originX + (++i-w/2)/scale );
					t = F.getValue();
					F.addVariable( variable, originX + (i-w/2)/scale + a );
				}
			}
			g.setColor( Color.red );
			g.setStroke( curve );
			g.draw( path );
		}
		applet.stat.setText( "" ); 
	}


	public void drawEndpoints( Graphics2D g ){
	}


	public void drawFunction( Graphics2D g ){
		drawFunction( g, G, Color.lightGray );
		drawFunction( g, F, Color.black );
	}


	public void mousePressed(MouseEvent me){
		POINT = me.getPoint();
	}
}