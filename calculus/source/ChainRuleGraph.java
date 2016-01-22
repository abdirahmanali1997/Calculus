import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import org.nfunk.jep.*;

public class ChainRuleGraph extends Graph{

	int type;

	Font bold = new Font("Helvetica",Font.BOLD,18);

	public ChainRuleGraph( CalculusApplet applet, String f, String g, double a, int type ){
		super();

		this.applet = applet;
		this.a = a;
		this.type = type;

		F.parseExpression( f );
		G.parseExpression( g );
	}


	public void draw( Graphics2D g ){
		double[] P = toCartesianPoint( 0, 0 );
		double[] Q = toCartesianPoint( w, 0 );
		double mf;
		double mg;
		double fa;
		double ga;
		double x;
		double y;
		GeneralPath arrow;

		// draw tangent line
		g.setStroke( curve );
		g.setFont( bold );
		if ( type == 1 ){
			G.addVariable( variable, a );
			ga = G.getValue();
			try{
				mg = Double.parseDouble( G.evaluate( G.differentiate( G.getTopNode(), variable ) ).toString() );
				drawLine( g, P[0], ga + mg*(P[0] - a), Q[0], ga + mg*(Q[0] - a), Color.red );
				
				if ( Math.abs(mg) < h/w ){
					x = w/4;					
				} else {
					x = Math.abs(h/4/mg);
				}
				y = mg*x;
				g.setColor( Color.orange );
				g.draw( new Line2D.Double( w/2+x, h/2+y, w/2+x, h/2-y+(mg>0?1:-1)*2) );
				if ( Math.abs(y) > 3 ){
					arrow = new GeneralPath();
					arrow.moveTo( (float)(w/2+x-3), (float)(h/2-y+(mg>0?1:-1)*5) );
					arrow.lineTo( (float)(w/2+x), (float)(h/2-y) );
					arrow.lineTo( (float)(w/2+x+3), (float)(h/2-y+(mg>0?1:-1)*5) );
					g.draw( arrow );
				}
				g.drawString( ""+(float)mg, (float)(w/2+x+5), (float)(h/2+5) );

				g.setColor( Color.blue );
				g.draw( new Line2D.Double( w/2-x, h/2+y, w/2+x-2, h/2+y) );
				if ( x > 3 ){
					arrow = new GeneralPath();
					arrow.moveTo( (float)(w/2+x-5), (float)(h/2+y-3) );
					arrow.lineTo( (float)(w/2+x), (float)(h/2+y) );
					arrow.lineTo( (float)(w/2+x-5), (float)(h/2+y+3) );
					g.draw( arrow );
				}
				if ( mg < 0 ){
					g.drawString( "1", (float)(w/2) - g.getFontMetrics().stringWidth("1")/2, (float)(h/2+y-5) );
				} else {
					g.drawString( "1", (float)(w/2) - g.getFontMetrics().stringWidth("1")/2, (float)(h/2+y+18) );
				}

			} catch( ParseException e ){
			} catch( Exception e ){
			}
		} else if ( type == 2 ){
			G.addVariable( variable, a );
			ga = G.getValue();
			F.addVariable( variable, ga );
			fa = F.getValue();
			try{
				mg = Double.parseDouble( G.evaluate( G.differentiate( G.getTopNode(), variable ) ).toString() );
				if ( Math.abs(mg) < h/w ){
					x = mg*w/4;
				} else {
					x = Math.abs(mg*h/4)/mg;
				}
				mf = Double.parseDouble( F.evaluate( F.differentiate( F.getTopNode(), variable ) ).toString() );
				drawLine( g, P[0], fa + mf*(P[0] - ga), Q[0], fa + mf*(Q[0] - ga), Color.red );
				
				y = x*mf;
				g.setColor( Color.green );
				g.draw( new Line2D.Double( w/2+x, h/2+y, w/2+x, h/2-y+(y>0?1:-1)*2) );
				if ( Math.abs(y) > 3 ){
					arrow = new GeneralPath();
					arrow.moveTo( (float)(w/2+x-3), (float)(h/2-y+(y>0?1:-1)*5) );
					arrow.lineTo( (float)(w/2+x), (float)(h/2-y) );
					arrow.lineTo( (float)(w/2+x+3), (float)(h/2-y+(y>0?1:-1)*5) );
					g.draw( arrow );
				}
				if ( mg < 0 ){
					g.drawString( ""+(float)(mf*mg), (float)(w/2+x-5-g.getFontMetrics().stringWidth(""+(float)(mf*mg))), (float)(h/2+5) );
				} else {
					g.drawString( ""+(float)(mf*mg), (float)(w/2+x+5), (float)(h/2+5) );
				}
				
				g.setColor( Color.orange );
				g.draw( new Line2D.Double( w/2-x, h/2+y, w/2+x, h/2+y) );
				if ( Math.abs(x) > 3 ){
					arrow = new GeneralPath();
					arrow.moveTo( (float)(w/2+x-(x>0?1:-1)*5), (float)(h/2+y-3) );
					arrow.lineTo( (float)(w/2+x), (float)(h/2+y) );
					arrow.lineTo( (float)(w/2+x-(x>0?1:-1)*5), (float)(h/2+y+3) );
					g.draw( arrow );
				}
				if ( mf*mg < 0 ){
					g.drawString( ""+(float)mg, (float)(w/2) - g.getFontMetrics().stringWidth(""+(float)mg)/2, (float)(h/2+y-5) );
				} else {
					g.drawString( ""+(float)mg, (float)(w/2) - g.getFontMetrics().stringWidth(""+(float)mg)/2, (float)(h/2+y+18) );
				}
			} catch( ParseException e ){
			} catch( Exception e ){
			}
		} else if ( type == 3 ){
			G.addVariable( variable, a );
			F.addVariable( variable, G.getValue() );
			fa = F.getValue();
			try{
				mf = Double.parseDouble( G.evaluate( G.differentiate( G.getTopNode(), variable ) ).toString() );
				double ch = ((ChainRule)applet).gofex.h;
				double cw = ((ChainRule)applet).gofex.w;
				if ( Math.abs(mf) < ch/cw ){
					x = cw/4;					
				} else {
					x = Math.abs(ch/4/mf);
				}

				mf *= Double.parseDouble( F.evaluate( F.differentiate( F.getTopNode(), variable ) ).toString() );
				drawLine( g, P[0], fa + mf*(P[0] - a), Q[0], fa + mf*(Q[0] - a), Color.red );

				y = x*mf;
				c = w/2 + (a - originX)*scale;
				d = h/2 - (fa - originY)*scale;
				g.setColor( Color.green );
				g.draw( new Line2D.Double( c+x, d+y, c+x, d-y+(mf>0?1:-1)*2) );
				if ( Math.abs(y) > 3 ){
					arrow = new GeneralPath();
					arrow.moveTo( (float)(c+x-3), (float)(d-y+(mf>0?1:-1)*5) );
					arrow.lineTo( (float)(c+x), (float)(d-y) );
					arrow.lineTo( (float)(c+x+3), (float)(d-y+(mf>0?1:-1)*5) );
					g.draw( arrow );
				}
				g.drawString( ""+(float)mf, (float)(c+x+5), (float)(d+5) );

				g.setColor( Color.blue );
				g.draw( new Line2D.Double( c-x, d+y, c+x-2, d+y) );
				if ( x > 3 ){
					arrow = new GeneralPath();
					arrow.moveTo( (float)(c+x-5), (float)(d+y-3) );
					arrow.lineTo( (float)(c+x), (float)(d+y) );
					arrow.lineTo( (float)(c+x-5), (float)(d+y+3) );
					g.draw( arrow );
				}
				if ( mf < 0 ){
					g.drawString( "1", (float)(c) - g.getFontMetrics().stringWidth("1")/2, (float)(d+y-5) );
				} else {
					g.drawString( "1", (float)(c) - g.getFontMetrics().stringWidth("1")/2, (float)(d+y+18) );
				}
			} catch( ParseException e ){
			} catch( Exception e ){
			}
		}
		applet.stat.setText( "" );
	}

		
	public void drawXLabels( Graphics2D g, float hh ){
		if ( type == 1 ){
			g.draw( new Line2D.Double(w/2 + (a-originX)*scale,H-2,w/2 + (a-originX)*scale,H+2 ) );
			String str = "a";
			g.drawString( str, (float)(w/2 + (a-originX)*scale) - g.getFontMetrics().stringWidth(str)/2, hh );
		} else if ( type == 2 ){
			G.addVariable( variable, a );
			double ga = G.getValue();
			g.draw( new Line2D.Double(w/2 + (ga-originX)*scale,H-2,w/2 + (ga-originX)*scale,H+2 ) );
			String str = "g(a)";
			g.drawString( str, (float)(w/2 + (ga-originX)*scale) - g.getFontMetrics().stringWidth(str)/2, hh );
		}		
	}


	public void drawEndpoints( Graphics2D g ){
		if ( type == 1 ){
			//drawPointOnXAxis( g, a, Color.blue );
		} else if ( type == 2 ){
		} else if ( type == 3 ){
			//drawPointOnXAxis( g, a, Color.blue );
			G.addVariable( variable, a );
			F.addVariable( variable, G.getValue() );
			drawPoint( g, a, F.getValue(), Color.red, overA );
		}		
	}


	public void drawFunction( Graphics2D g ){
		if ( type == 1 ){
			drawFunction( g, G, Color.black );
		} else if ( type == 2 ){
			drawFunction( g, F, Color.black );
		} else if ( type == 3 ){
			GeneralPath path = new GeneralPath();
			double i = 0.0;
			G.addVariable( variable, originX-w/2/scale );
			F.addVariable( variable, G.getValue() );
			double y = F.getValue();
			double oldy = y;
			while ( i < w ){
				while ( i<=w && (Double.isNaN(y=F.getValue()) || Math.abs(y-originY)>h/scale) ){
					G.addVariable( variable, originX + (++i-w/2)/scale );
					F.addVariable( variable, G.getValue() );
					oldy = y;
				}
				if ( !Double.isNaN( oldy ) && Math.abs(oldy-originY)>h/scale && Math.abs(y-originY)<h/scale ){
					path.moveTo( (float)(i-1), (float)(h/2 - (oldy-originY)*scale) );
					path.lineTo( (float)i, (float)(h/2 - (y-originY)*scale) );
				} else {
					path.moveTo( (float)i, (float)(h/2 - (y-originY)*scale) );
				}
				G.addVariable( variable, originX + (++i-w/2)/scale );
				F.addVariable( variable, G.getValue() );
				while ( i<=w && Math.abs(y-originY)<h/scale && !Double.isNaN(y=F.getValue()) ){					
					path.lineTo( (float)i, (float)(h/2 - (y-originY)*scale) );
					G.addVariable( variable, originX + (++i-w/2)/scale );
					F.addVariable( variable, G.getValue() );
				}
			}
			g.setColor( Color.black );
			g.setStroke( curve );
			g.draw( path );
		} 
	}


	public void mousePressed(MouseEvent me){
		POINT = me.getPoint();
		double x = POINT.x - w/2 - (a - originX)*scale;
		G.addVariable( variable, a );
		F.addVariable( variable, G.getValue() );
		double y = POINT.y - h/2 + (F.getValue() - originY)*scale;
		if ( x*x + y*y < rr ){
			newA = true;
		}
		requestFocus();
	}


	public void mouseDragged(MouseEvent me){
		if ( type == 3 ){
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
			repaint();
		}
	}	
}