import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import org.nfunk.jep.*;


public class LimitSequencesGraph extends Graph{
	
	int N;

	public LimitSequencesGraph( CalculusApplet applet, String f, double a, double b, int m ){
		super();

		this.applet = applet;
		this.a = a;
		this.b = b;
		this.m = m;
		variable = "n";
		pixels = 100.0;

		F.parseExpression( f );
		init();
	}
	
	public void init(){
		F.addVariable( "n", 1 );
	}
	

	public void paintComponent( Graphics graphics ){
		w = getWidth();
		h = getHeight();

		Graphics2D g = (Graphics2D)graphics;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		
		if (backImage==null || backImage.getWidth(this) != w || backImage.getHeight(this) != h){
			backImage = this.createImage( (int)w, (int)h );
			backGraphics = (Graphics2D)(backImage.getGraphics());
			backGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
			newBackground = true;
		}

		scale = pixels/units[zoom];

		// draw graph of function f
		if ( newBackground ){
			backGraphics.setColor( getBackground() );
			backGraphics.fillRect( 0, 0, (int)w, (int)h );
			if ( showGrid ) drawGridLines( backGraphics );
			drawAxes( backGraphics );
			drawFunction( backGraphics );
			newBackground = false;
		}
		g.drawImage( backImage, 0, 0, this );

		draw( g );
		//drawPoint( g, current.n, current.val, 1.5*r, Color.blue );
		F.addVariable( "n", N );
		double y = F.getValue();
		if ( N > -1 && !Double.isNaN( y ) ){
			if ( y < a+b && y > a-b )
			drawPoint( g, N, F.getValue(), new Color(0,255,0,150), true );
			else 
			drawPoint( g, N, F.getValue(), new Color(0,0,255,150), true );
		}
		drawEndpoints( g );
		drawCrosshair( g );
	}


		//double[] out = { originX + m*(a - w/2)/pixels, originY - n*(b-h/2)/pixels };


	public void draw( Graphics2D g ){
		int A = Math.max( 1, (int)(originX - m*w/pixels/2) );
		int B = Math.max( 1, (int)(originX + m*w/pixels/2) );

		if ( newStat ){
			init();
			newStat = false;
		}
		
		// draw shaded regions
		g.setColor( new Color(255,255,0,100) );
		//g.fill( new Rectangle2D.Double( 0, h/2 - (a+b-originY)*scale, w, 2*b*scale ) );
		g.fill( new Rectangle2D.Double( 0, h/2 - scale*(a + b - originY), w, 2*b*scale ) );

		// draw remaining lines		
		g.setColor( Color.black );
		g.setStroke( new BasicStroke(0.66f) );		
		double[] P = toScreenPoint( 0, a-b );
		g.draw( new Line2D.Double( 0, P[1], w, P[1] ) );
		P = toScreenPoint( 0, a );
		g.draw( new Line2D.Double( 0, P[1], w, P[1] ) );
		P = toScreenPoint( 0, a+b );
		g.draw( new Line2D.Double( 0, P[1], w, P[1] ) );


		Series tmp;
		int step = (int)( Math.max(1,10*m/pixels) );
		if ( shift ) step = 1;
		
		double y;
		for ( int i=A; i<B+1; i+=step ){
			F.addVariable( "n", i );
			y = F.getValue();
			if ( y < a + b && y > a-b )
				drawPoint( g, i, y, 0.5*r, new Color(0,255,0,150) );
			else 
				drawPoint( g, i, y, 0.5*r, new Color(0,0,255,150) );
		}

		setStat();
	}


	public void drawAxes( Graphics2D g ){
		g.setColor( Color.black );
		g.setStroke( axes );

		double[] P = toScreenPoint( 0, 0 );
		W = P[0];
		if ( P[0] < 20 ) W = 20;
		else if ( P[0] > w - 10 ) W = w - 10;

		H = P[1];
		if ( P[1] < 10 ) H = 10;
		else if ( P[1] > h - 20 ) H = h - 20;

		// draw y-axis
		g.draw( new Line2D.Double(W,0,W,h) );
		
		// draw x-axis
		g.draw( new Line2D.Double(0,H,w,H) );

/*
		// label y-axis
		float ww = (float)(W+4);
		String str;
		for ( int A = (int)(originY/n-h/2/pixels)-1; A < (originY/n+h/2/pixels)+1; A++){
			g.draw( new Line2D.Double( W-2, h/2 - pixels*(A-originY/n), W+2, h/2 - pixels*(A-originY/n) ) );
			if ( A != 0 ){
				str = "" + (int)(A*n);
				if ( W != 1 ) ww = (float)(W-3) - g.getFontMetrics().stringWidth(str);
				g.drawString( str, ww, (float)(h/2 - pixels*(A-originY/n)) + g.getFontMetrics().getHeight()/3 );
			}
		}
*/
		// label y-axis
		float ww = (float)(W+4);
		String str;
		for ( int A = (int)((originY-h/2/scale)/units[zoom])-1; A < (originY+h/2/scale)/units[zoom]+1; A++){
			g.draw( new Line2D.Double( W-2,h/2 - (A*units[zoom]-originY)*scale,W+2,h/2 - (A*units[zoom]-originY)*scale ) );
			if ( A != 0 ){
				if ( (int)(A*units[zoom]) == A*units[zoom] ) str = "" + (int)(A*units[zoom]);
				else str = "" + (float)(A*units[zoom]);
				if ( W != 20 ) ww = Math.max(24, (float)(W-3) - g.getFontMetrics().stringWidth(str) );
				g.drawString( str, ww, (float)(h/2 - (A*units[zoom]-originY)*scale) + g.getFontMetrics().getHeight()/3 );
			}
		}
/*
		ww = (float)(W+4);
		if ( W == w - 1 ) ww = (float)(W-3) - g.getFontMetrics().stringWidth(ylabel);
		g.drawString( ylabel, ww, g.getFontMetrics().getHeight() );
*/
		// label x-axis
		float hh = (float)(H+1)+g.getFontMetrics().getHeight();
		if ( H == h - 1 ){
			hh = (float)(H-4);
		}
		for ( int A = (int)(originX/m-w/2/pixels)-1; A < (originX/m+w/2/pixels)+1; A++){
			g.draw( new Line2D.Double(w/2 + pixels*(A-originX/m),H-2,w/2 + pixels*(A-originX/m),H+2 ) );
			if ( A != 0 ){
				str = "" + (int)(A*m);
				g.drawString( str, (float)(w/2 + pixels*(A-originX/m)) - g.getFontMetrics().stringWidth(str)/2, hh );
			}
		}
	}

	public void drawEndpoints( Graphics2D g ){
		drawPointOnYAxis( g, a, Color.green, overA );
		drawPointOnYAxis( g, a+b, Color.red, overB );
	}
	
	public void drawFunction( Graphics2D g ){
	}

	public void drawGridLines( Graphics2D g ){
		g.setColor( Color.cyan );
		g.setStroke( gridline );
		for ( double A = (int)((originY-h/2/scale)/units[zoom])-1; A < (originY+h/2/scale)/units[zoom]+1; A=A+0.2){
			g.draw( new Line2D.Double( 0,h/2 - (A*units[zoom]-originY)*scale,w,h/2 - (A*units[zoom]-originY)*scale ) );
		}
		for ( double A = (int)(originX/m-w/2/pixels)-1; A < (originX/m+w/2/pixels)+1; A=A+0.2){
			g.draw( new Line2D.Double( w/2 + pixels*(A-originX/m),0,w/2 + pixels*(A-originX/m),h ) );
		}
	}

	public void setStat(){
		if ( N > 0 ){
			applet.stat.setText( "" );
			F.addVariable( "n", N );
			applet.setStat(" a(" + N + ") \u2248 ", F.getValue(), Color.blue );
		}
	}

	public double[] toCartesianPoint( double a, double b ){
		double[] out = { originX + m*(a - w/2)/pixels, originY - (b - h/2)/scale };
		return out;
	}

	public double[] toScreenPoint( double a, double b ){
		//double[] out = { w/2 + pixels*(a - originX)/m, h/2 - pixels*(b - originY)/n };
		double[] out = { w/2 + pixels*(a - originX)/m, h/2 - scale*(b - originY) };
		return out;
	}

	public void keyPressed( KeyEvent ke ){
		int code = ke.getKeyCode();
		if ( code == KeyEvent.VK_SHIFT ){// holding down shift key
			shift = true;
			newBackground = true;
			repaint();
		}
	}

	public void keyReleased( KeyEvent ke ){
		shift = false;
		newBackground = true;
		repaint();
	}

	public void mouseMoved( MouseEvent me ){
		Point p = me.getPoint();
		double xa = p.y - h/2 + scale*(a - originY);
		double xb = p.y - h/2 + scale*(a + b - originY);
		overA = false;
		overB = false;
		if ( xb*xb + (p.x-W)*(p.x-W) < rr ){
			overB = true;
		} else if ( xa*xa + (p.x-W)*(p.x-W) < rr ){
			overA = true;
		}

		N = (int)(originX + m*(me.getPoint().x - w/2)/pixels + 0.5 );
		setStat();
		repaint();
//double[] P = toCartesianPoint( me.getPoint().x, me.getPoint().y ); 
//applet.statusbar.setText( P[0] + "," + P[1] );
	}

	public void mouseClicked( MouseEvent me ){
		POINT = me.getPoint();
		//double[] P = toCartesianPoint( POINT.x, POINT.y );
		if ( me.getClickCount() > 1 ){
			// set center so that P stays in same place
			int z = Math.max( 1, m/2 );
			originX += (m - z)*(POINT.x-w/2)/pixels;
			//originY = P[1] + n*(POINT.y-h/2)/pixels;
			// zoom in on point P
			applet.actionPerformed( new ActionEvent(applet.zoomin,ActionEvent.ACTION_PERFORMED,"zoom") );
		} 
	}

	public void mousePressed(MouseEvent me){
		POINT = me.getPoint();
		if ( overB ){
			newB = true;
		} else if ( overA ){
			newA = true;
		}
		requestFocus();
	}

	public void mouseDragged(MouseEvent me){
		double[] P = toCartesianPoint( me.getPoint().x, me.getPoint().y );
		if ( newA ){
			a = P[1];
			applet.a.setValue( a );
		} else if ( newB ){
			b = Math.max( 0, P[1] - a );
			applet.b.setValue( b );
		} else {
			originX -= m*(me.getPoint().x - POINT.x)/pixels;
			originY += (me.getPoint().y - POINT.y)/scale;
			POINT = me.getPoint();
			newBackground = true;
		}
		repaint();
	}	
}