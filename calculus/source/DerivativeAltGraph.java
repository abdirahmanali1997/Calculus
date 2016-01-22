import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;

import org.nfunk.jep.*;

public class DerivativeAltGraph extends Graph{
    

	public DerivativeAltGraph( CalculusApplet applet, String f, String g, double a ){
		super();
				
		this.applet = applet;
		this.a = a;
        this.b = a;
        this.c = a;
		
		F.parseExpression( f );
		G.parseExpression( g );
	}

	public void draw( Graphics2D g ){
		F.addVariable( "x", a );
		double fa = F.getValue();
        
        // draw tangent line
		double m;
		double[] P = toCartesianPoint( 0, 0 );
		double[] Q = toCartesianPoint( w, 0 );
		g.setStroke( new BasicStroke(3.0f) );
        try{
            m = Double.parseDouble( F.evaluate( F.differentiate( F.getTopNode(), "x" ) ).toString() );
            drawLine( g, P[0], fa + m*(P[0] - a), Q[0], fa + m*(Q[0] - a), Color.gray );
            applet.setStat( "Slope \u2248 ", m, Color.red );
        } catch( ParseException e ){
        } catch( Exception e ){
        }

        
        // draw derivative from b to c
        try{
            double start = w/2 + (b - originX)*scale;
            double stop = w/2 + (c - originX)*scale;
            GeneralPath path = new GeneralPath();
            F.addVariable( variable, b );
            double y = Double.parseDouble( F.evaluate( F.differentiate( F.getTopNode(), "x" ) ).toString() );
            path.moveTo( start, (float)(h/2 - (y-originY)*scale) );
            while ( start < stop ){
                F.addVariable( variable, (start - w/2)/scale + originX );
                y = Double.parseDouble( F.evaluate( F.differentiate( F.getTopNode(), "x" ) ).toString() );
                path.lineTo( start, (float)(h/2 - (y-originY)*scale) );
                start++;
            }
            g.setColor( Color.red );
            g.setStroke( curve );
            g.draw( path );
        } catch( ParseException e ){
        } catch( Exception e ){
        }
    }


	public void drawEndpoints( Graphics2D g ){
		F.addVariable( "x", a );
		drawPoint( g, a, F.getValue(), Color.red, overA );
        try {
            drawPoint( g, a, Double.parseDouble( F.evaluate( F.differentiate( F.getTopNode(), "x" ) ).toString() ), Color.red, false );
        } catch( ParseException e ){
        } catch( Exception e ){
        }
	}


	public void drawFunction( Graphics2D g ){
		drawFunction( g, G, Color.lightGray );
		drawFunction( g, F, Color.black );
	}


	public void mousePressed(MouseEvent me){        
		POINT = me.getPoint();
		double xa = POINT.x - w/2 - (a - originX)*scale;
		F.addVariable( "x", a );
		double ya = POINT.y - h/2 + (F.getValue() - originY)*scale;
		if ( xa*xa + ya*ya < rr ){
			newA = true;
		}
		requestFocus();
	}
    
	public void mouseDragged(MouseEvent me){
		double[] P = toCartesianPoint( me.getPoint().x, me.getPoint().y );
		if ( newA ){
			a = P[0];
			applet.a.setValue( a );
			applet.updateGraphs( this );
            if ( a > c ) c = a;
            if ( b > a ) b = a;
		} else {
			originX -= (me.getPoint().x - POINT.x)/scale;
			originY += (me.getPoint().y - POINT.y)/scale;
			POINT = me.getPoint();
			newBackground = true;
			repaint();
		}
	}	
}