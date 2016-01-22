import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import org.nfunk.jep.*;


public class CompoundInterestGraph extends Graph{
	
	int N;

	public CompoundInterestGraph( CalculusApplet applet, double a, double b, int n ){
		super();

		this.applet = applet;
		this.a = a;
		this.b = b;
		this.n = n;
		variable = "n";
		pixels = 100.0;

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
		double y = a*Math.pow(1+b/n,N);
		if ( N > -1 && !Double.isNaN( y ) ){
			drawPoint( g, (double)(N)/n, y, new Color(0,0,255,150), true );
		}
		drawEndpoints( g );
		drawCrosshair( g );
	}


		//double[] out = { originX + m*(a - w/2)/pixels, originY - n*(b-h/2)/pixels };


	public void draw( Graphics2D g ){
		int A = Math.max( 0, (int)(n*originX - n*w/scale/2) );
		int B = Math.max( 0, (int)(n*originX + n*w/scale/2) );
		if ( newStat ){
			init();
			newStat = false;
		}
		

		Series tmp;
		int step = (int)( Math.max(1,10*m/pixels) );
		if ( shift ) step = 1;
		double x = 1 + b/n;
		double y = a*Math.pow(x,A);
		for ( int i=A; i<B+1; i+=1 ){
			drawPoint( g, (double)i/n, y, r, new Color(0,0,255,150) );
			y *= x;
		}

		setStat();
	}


	public void drawEndpoints( Graphics2D g ){
	}

	
	public void drawFunction( Graphics2D g ){
	}

	
	public void setStat(){
		if ( N > 0 ){
			applet.setStat(" A(" + N + ") \u2248 ", a*Math.pow(1+b/n,N), Color.blue );
		}
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

		N = Math.max( 0, (int)(n*originX + n*(me.getPoint().x - w/2)/scale) );
		setStat();
		repaint();
//double[] P = toCartesianPoint( me.getPoint().x, me.getPoint().y ); 
//applet.statusbar.setText( P[0] + "," + P[1] );
	}
}