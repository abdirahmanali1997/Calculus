import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import org.nfunk.jep.*;

class Series{
	double val;
	double sum;
	double ratio;
	double root;
	long n;
	
	Series next;
	Series previous;
	
	public Series( double val, Series previous){
		this.val = val;
		this.previous = previous;
		this.sum = val;
		this.n = 1;
		if ( previous != null ){
			previous.next = this;
			this.sum = previous.sum + val;
			this.n = previous.n + 1;
			if ( previous.val != 0 ) ratio = Math.abs(this.val/previous.val);
			if ( this.n != 0 ) root = Math.pow( Math.abs(this.val), 1/(double)this.n );
		}
	}
}

public class SequencesGraph extends Graph{

	Series markerA;
	Series markerB;
	Series current;
	
	int N;

	public SequencesGraph( CalculusApplet applet, String f, String g, int m, int n ){
		super();

		this.applet = applet;
		this.m = m;
		this.n = n;
		variable = "n";
		pixels = 100.0;

		F.parseExpression( f );
		G.parseExpression( g );
		init();
	}
	
	public void init(){
		F.addVariable( "n", 1 );
		markerA = new Series( F.getValue(), null );
		markerB = markerA;
		current = markerA;
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
		//if ( newBackground || shift ){
		if ( newBackground ){
			backGraphics.setColor( getBackground() );
			backGraphics.fillRect( 0, 0, (int)w, (int)h );
			if ( showGrid ) drawGridLines( backGraphics );
			//if ( !shift ){
				drawAxes( backGraphics );
				drawFunction( backGraphics );
			//}
			draw( backGraphics );
			newBackground = false;
		}
		g.drawImage( backImage, 0, 0, this );
		if ( current != null && newA ){
			Object s = applet.choice.getSelectedItem();
			if ( s.equals( "Show Sequence Only" ) ){
				//drawPoint( g, current.n, current.val, 1.5*r, Color.blue );
				drawPoint( g, current.n, current.val, new Color(0,0,255,150), true );
			} else if ( s.equals( "Sequence and Series" ) ){
				drawPoint( g, current.n, current.sum, Color.red, true );
				drawPoint( g, current.n, current.val, Color.blue, true );
			} else if ( s.equals( "Sequence and Ratio Test" ) ){
				drawPoint( g, current.n, current.ratio, Color.green, true );
				drawPoint( g, current.n, current.val, Color.blue, true );
			} else if ( s.equals( "Sequence and Root Test" ) ){
				drawPoint( g, current.n, current.root, Color.yellow, true );
				drawPoint( g, current.n, current.val, Color.blue, true );
			} else if ( s.equals( "Comparison Test" ) ){
				G.addVariable( "n", current.n );
				drawPoint( g, current.n, G.getValue(), Color.magenta, true );
				drawPoint( g, current.n, current.val, Color.blue, true );
			} else if ( s.equals( "Limit Comparison Test" ) ){
				G.addVariable( "n", current.n );
				drawPoint( g, current.n, current.val/G.getValue(), Color.cyan, true );
				drawPoint( g, current.n, current.val, Color.blue, true );
			}
			newA = false;
		}
		
		//if ( !shift ){	
			drawEndpoints( g );
			drawCrosshair( g );
		//}
	}


		double[] out = { originX + m*(a - w/2)/pixels, originY - n*(b-h/2)/pixels };

	public void draw( Graphics2D g ){
		int A = Math.max( 1, (int)(originX - m*w/pixels/2) );
		int B = Math.max( 1, (int)(originX + m*w/pixels/2) );

		if ( newStat ){
			init();
			newStat = false;
		}
		
		Series tmp;
		int step = (int)( Math.max(1,10*m/pixels) );
		if ( shift ) step = 1;
		Object s = applet.choice.getSelectedItem();
		if ( s.equals( "Show Sequence Only" ) ){
			for ( int i=A; i<B+1; i+=step ){
				F.addVariable( "n", i );
				drawPoint( g, i, F.getValue(), new Color(0,0,255,150), false );
			}
		} else if ( s.equals( "Comparison Test" ) ){
			for ( int i=A; i<B+1; i+=step ){
				F.addVariable( "n", i );
				drawPoint( g, i, F.getValue(), new Color(0,0,255,150), false );
				G.addVariable( "n", i );
				drawPoint( g, i, G.getValue(), new Color(255,0,255,150), false );
			}
		} else if ( s.equals( "Limit Comparison Test" ) ){
			for ( int i=A; i<B+1; i+=step ){
				F.addVariable( "n", i );
				drawPoint( g, i, F.getValue(), new Color(0,0,255,150), false );
				G.addVariable( "n", i );
				drawPoint( g, i, F.getValue()/G.getValue(), new Color(0,255,255,150), false );
			}
		} else {
			if ( markerB.n >= B ){
				// move markerB up the list
				while ( markerB.n > B ){
					markerB = markerB.previous;
				}
			} else {
				// move markerB down the list
				while ( markerB.next != null && markerB.n < B ){
					markerB = markerB.next;
				}
				// create new data if necessary
				while ( markerB.n < B ){
					F.addVariable( "n", (markerB.n + 1) );
					tmp = new Series( F.getValue(), markerB );
					markerB = tmp;
				}
			}
		
			if ( markerA.n <= A ){
				while ( markerA.n < A ){
					markerA = markerA.next;
				}
			} else {
				while ( markerA.n > A ){
					markerA = markerA.previous;
				}
			}
		
			int i;
			if ( s.equals( "Sequence and Series" ) ){
				tmp = markerA;
				while ( tmp != null && tmp.n <= markerB.n ){
					drawPoint( g, tmp.n, tmp.sum, new Color(255,0,0,150), false );
					drawPoint( g, tmp.n, tmp.val, new Color(0,0,255,150), false );
					//tmp = tmp.next;
					i = 0;
					while ( i++<step && tmp != null ) tmp = tmp.next;
				}
			} else if ( s.equals( "Sequence and Ratio Test" ) ){
				tmp = markerA;
				while ( tmp != null && tmp.n <= markerB.n ){
					drawPoint( g, tmp.n, tmp.ratio, new Color(0,255,0,150), false );
					drawPoint( g, tmp.n, tmp.val, new Color(0,0,255,150), false );
					//tmp = tmp.next;
					i = 0;
					while ( i++<step && tmp != null ) tmp = tmp.next;
				}
			} else if ( s.equals( "Sequence and Root Test" ) ){
				tmp = markerA;
				while ( tmp != null && tmp.n <= markerB.n ){
					drawPoint( g, tmp.n, tmp.root, new Color(255,255,0,150), false );
					drawPoint( g, tmp.n, tmp.val, new Color(0,0,255,150), false );
					//tmp = tmp.next;
					i = 0;
					while ( i++<step && tmp != null ) tmp = tmp.next;
				}
			}
		}
		setStat();
	}


	public void drawAxes( Graphics2D g ){
		g.setColor( Color.black );
		g.setStroke( axes );

		double[] P = toScreenPoint( 0, 0 );
		W = P[0];
		if ( P[0] < 10 ) W = 10;
		else if ( P[0] > w - 10 ) W = w - 10;

		H = P[1];
		if ( P[1] < 10 ) H = 10;
		else if ( P[1] > h-20 ) H = h - 20;

		// draw y-axis
		g.draw( new Line2D.Double(W,0,W,h) );
		
		// draw x-axis
		g.draw( new Line2D.Double(0,H,w,H) );

		// label y-axis
		float ww = (float)(W+4);
		String str;
		for ( int A = (int)(originY/n-h/2/pixels)-1; A < (originY/n+h/2/pixels)+1; A++){
			g.draw( new Line2D.Double( W-2, h/2 - pixels*(A-originY/n), W+2, h/2 - pixels*(A-originY/n) ) );
			if ( A != 0 ){
				str = "" + (int)(A*n);
				if ( W != 1 ) ww = Math.max(14,(float)(W-3) - g.getFontMetrics().stringWidth(str));
				g.drawString( str, ww, (float)(h/2 - pixels*(A-originY/n)) + g.getFontMetrics().getHeight()/3 );
			}
		}

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
	}
	
	public void drawFunction( Graphics2D g ){
	}

	public void drawGridLines( Graphics2D g ){
		g.setColor( Color.cyan );
		g.setStroke( gridline );
		for ( double A = (int)(originY/n-h/2/pixels)-1; A < (originY/n+h/2/pixels)+1; A=A+0.2){
			g.draw( new Line2D.Double( 0,h/2 - pixels*(A-originY/n),w,h/2 - pixels*(A-originY/n) ) );
		}
		for ( double A = (int)(originX/m-w/2/pixels)-1; A < (originX/m+w/2/pixels)+1; A=A+0.2){
			g.draw( new Line2D.Double( w/2 + pixels*(A-originX/m),0,w/2 + pixels*(A-originX/m),h ) );
		}
	}


	public double[] toCartesianPoint( double a, double b ){
		double[] out = { originX + m*(a - w/2)/pixels, originY - n*(b-h/2)/pixels };
		return out;
	}

	public double[] toScreenPoint( double a, double b ){
		double[] out = { w/2 + pixels*(a - originX)/m, h/2 - pixels*(b - originY)/n };
		return out;
	}
			
	public void setStat(){
		if ( current != null && N > 0 ){
			applet.stat.setText( "" );
			applet.stat2.setText( "" );
			if ( current.n < N ){
				while ( current.next != null && current.n < N ){
					current = current.next;
				}
			} else if ( current.n > N ){
				while ( current.previous != null && current.n > N ){
					current = current.previous;
				}
			}
			Object s = applet.choice.getSelectedItem();
			if ( s.equals( "Show Sequence Only" ) ){
				F.addVariable( "n", N );
				applet.setStat(" a(" + N + ") \u2248 ", F.getValue(), Color.blue );
			} else if ( s.equals( "Comparison Test" ) ){
				F.addVariable( "n", N );
				applet.setStat(" a(" + N + ") \u2248 ", F.getValue(), Color.blue );
				G.addVariable( "n", N );
				applet.setStat2(" b(" + N + ") \u2248 ", G.getValue(), Color.magenta );
			} else if ( s.equals( "Limit Comparison Test" ) ){
				F.addVariable( "n", N );
				applet.setStat(" a(" + N + ") \u2248 ", F.getValue(), Color.blue );
				G.addVariable( "n", N );
				applet.setStat2(" a(" + N + ")/b(" + N + ") \u2248 ", F.getValue()/G.getValue(), Color.cyan );
			} else if ( s.equals( "Sequence and Series" ) ){
				applet.setStat(" a(" + N + ") \u2248 ", current.val, Color.blue );
				applet.setStat2( " s(" + N + ") \u2248 ", current.sum, Color.red );
			} else if ( s.equals( "Sequence and Ratio Test" ) ){
				applet.setStat(" a(" + N + ") \u2248 ", current.val, Color.blue );
				applet.setStat2( " s(" + N + ") \u2248 ", current.ratio, Color.green );
			} else if ( s.equals( "Sequence and Root Test" ) ){
				applet.setStat(" a(" + N + ") \u2248 ", current.val, Color.blue );
				applet.setStat2( " s(" + N + ") \u2248 ", current.root, Color.yellow );
			}
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
		N = (int)(originX + m*(me.getPoint().x - w/2)/pixels + 0.5 );
		setStat();
		newA = true;
		repaint();
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
	}

	public void mouseDragged(MouseEvent me){
		originX -= m*(me.getPoint().x - POINT.x)/pixels;
		originY += n*(me.getPoint().y - POINT.y)/pixels;
		POINT = me.getPoint();
		newBackground = true;
		repaint();
	}	
}