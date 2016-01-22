import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import org.nfunk.jep.*;

public class LHospitalGraph extends Graph{

	int type;

	public LHospitalGraph( CalculusApplet applet, String f, String g, double a, int type ){
		super();

		this.applet = applet;
		this.a = a;
		this.type = type;

		F.parseExpression( f );
		G.parseExpression( g );
	}


	public void drawEndpoints( Graphics2D g ){
		drawPointOnXAxis( g, a, Color.red, overA );

		F.addVariable( variable, a );
		G.addVariable( variable, a );
		g.setFont( bold );
		if ( type == 1 ){
			g.setColor( new Color(255,255,255,210) );
			String str = "f(a)/g(a) \u2248 " + (float)(F.getValue()/G.getValue());
			double l = g.getFontMetrics().stringWidth(str);
			g.fill( new Rectangle2D.Double( 0, h-30, l+10, 30 ) );
			g.setColor( Color.red );
			g.drawString( str, 5, (float)(h - 10) );
		} else if ( type == 2 ){
			try{
				Node node = G.differentiate( G.getTopNode(), variable );
				double ga = Double.parseDouble( G.evaluate( node ).toString() );
				node = F.differentiate( F.getTopNode(), variable );
				double fa = Double.parseDouble( F.evaluate( node ).toString() );

				g.setColor( new Color(255,255,255,210) );
				String str = "f'(a)/g'(a) \u2248 " + (float)(fa/ga);
				double l = g.getFontMetrics().stringWidth(str);
				g.fill( new Rectangle2D.Double( 0, h-30, l+10, 30 ) );
				g.setColor( Color.red );
				g.drawString( str, 5, (float)(h - 10) );
			} catch( ParseException e ){
			} catch( Exception e ){
			}
		}
	}


	public void drawFunction( Graphics2D g ){
		if ( type == 1 ){ // draw graphs of functions
			drawFunction( g, G, Color.black );
			drawFunction( g, F, Color.black );
		} else if ( type == 2 ){ // draw graphs of derivatives
			g.setColor( Color.black );
			g.setStroke( curve );
			try{
				Node node = G.differentiate( G.getTopNode(), variable );
				GeneralPath path = new GeneralPath();
				double i = 0.0;
				G.addVariable( variable, originX-w/2/scale );
				double y = Double.parseDouble( G.evaluate( node ).toString() );
				double oldy = y;
				while ( i < w ){
					while ( i<=w && (Double.isNaN(y=Double.parseDouble( G.evaluate( node ).toString() )) || Math.abs(y-originY)>h/scale) ){
						G.addVariable( variable, originX + (++i-w/2)/scale );
					}
					if ( !Double.isNaN( oldy ) && Math.abs(oldy-originY)>h/scale && Math.abs(y-originY)<h/scale ){
						path.moveTo( (float)(i-1), (float)(h/2 - (oldy-originY)*scale) );
						path.lineTo( (float)i, (float)(h/2 - (y-originY)*scale) );
					} else {
						path.moveTo( (float)i, (float)(h/2 - (y-originY)*scale) );
					}
					G.addVariable( variable, originX + (++i-w/2)/scale );
					while ( i<=w && Math.abs(y-originY)<h/scale && !Double.isNaN(y=Double.parseDouble( G.evaluate( node ).toString() )) ){
						path.lineTo( (float)i, (float)(h/2 - (y-originY)*scale) );
						G.addVariable( variable, originX + (++i-w/2)/scale );
					}
				}
				g.draw( path );

				node = F.differentiate( F.getTopNode(), variable );
				path = new GeneralPath();
				i = 0.0;
				F.addVariable( variable, originX-w/2/scale );
				y = Double.parseDouble( F.evaluate( node ).toString() );
				oldy = y;
				while ( i < w ){
					F.addVariable( variable, originX + (i-w/2)/scale );
					while ( i<=w && (Double.isNaN(y=Double.parseDouble( F.evaluate( node ).toString() )) || Math.abs(y-originY)>h/scale) ){
						F.addVariable( variable, originX + (++i-w/2)/scale );
					}
					if ( !Double.isNaN( oldy ) && Math.abs(oldy-originY)>h/scale && Math.abs(y-originY)<h/scale ){
						path.moveTo( (float)(i-1), (float)(h/2 - (oldy-originY)*scale) );
						path.lineTo( (float)i, (float)(h/2 - (y-originY)*scale) );
					} else {
						path.moveTo( (float)i, (float)(h/2 - (y-originY)*scale) );
					}
					F.addVariable( variable, originX + (++i-w/2)/scale );
					while ( i<=w  && Math.abs(y-originY)<h/scale&& !Double.isNaN(y=Double.parseDouble( F.evaluate( node ).toString() )) ){
						path.lineTo( (float)i, (float)(h/2 - (y-originY)*scale) );
						F.addVariable( variable, originX + (++i-w/2)/scale );
					}
				}
				g.draw( path );
			} catch( ParseException e ){
			} catch( Exception e ){
			}
		} 
	}


	public void mousePressed(MouseEvent me){
		POINT = me.getPoint();
		double x = POINT.x - w/2 - (a - originX)*scale;
		double y = POINT.y-H;
		if ( x*x + y*y < rr ){
			newA = true;
		} else {
			newA = false;
		}
		requestFocus();
	}


	public void mouseDragged(MouseEvent me){
		if ( newA ){
			double[] P = toCartesianPoint( me.getPoint().x, me.getPoint().y );
			a = P[0];
			applet.a.setValue( a );
		} else {
			originX -= (me.getPoint().x - POINT.x)/scale;
			originY += (me.getPoint().y - POINT.y)/scale;
			POINT = me.getPoint();
			newBackground = true;
		}
		applet.updateGraphs( this ); 
	}
}