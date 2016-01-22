import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;

import org.nfunk.jep.*;

public class GeneralDerivativeGraph extends Graph{

	public GeneralDerivativeGraph( CalculusApplet applet, String f, String g, double a ){
		super();
				
		this.applet = applet;
		this.a = a;
		
		F.parseExpression( f );
		//G.parseExpression( g );   why isn't this line needed???
	}

	public void draw( Graphics2D g ){
//		if ( Math.abs( a ) > 0.000000000001 ){
			g.setColor( Color.red );
			g.setStroke( curve );

			F.addVariable( variable, originX - w/2/scale );
			double t = F.getValue();
			G.addVariable( variable, originX - w/2/scale );
			G.addVariable( "h", a );
			double z = G.getValue();
			F.addVariable( variable, z );
			double y0 = h/2 - ((F.getValue() - t)/(z - (originX - w/2/scale)) - originY)*scale;
			double y1;
			for ( double i = 0.0; i<w; i += 1.0 ){
				F.addVariable( variable, originX + (i+1-w/2)/scale );
				G.addVariable( variable, originX + (i+1-w/2)/scale );
				t = F.getValue();
				z = G.getValue();
				F.addVariable( variable, z);
				y1 = h/2 - ((F.getValue() - t)/(z - (originX + (i+1-w/2)/scale))- originY)*scale;
				g.draw( new Line2D.Double(i,y0,i+1,y1) );
				y0 = y1;
			}
//		}
		applet.stat.setText( "" ); 
	}

	
	public void drawEndpoints( Graphics2D g ){
	}


	public void mousePressed(MouseEvent me){
		POINT = me.getPoint();
	}
}