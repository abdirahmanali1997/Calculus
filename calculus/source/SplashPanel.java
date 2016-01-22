import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.*;

public class SplashPanel extends JPanel implements MouseListener{

	Graph graph;
	
	double w;
	double h;
	
	boolean newBackground = true;

	public SplashPanel( Graph graph ){
		setBackground( Color.white );
		addMouseListener( this );
		this.graph = graph;
	}

    Image backImage;
	Graphics2D backGraphics;

	public void paintComponent( Graphics graphics ){
		w = getWidth();
		h = getHeight();
		graph.w = w;
		graph.h = h;

		Graphics2D g = (Graphics2D)graphics;
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
		
		if (backImage==null || backImage.getWidth(this) != w || backImage.getHeight(this) != h){
			backImage = this.createImage( (int)w, (int)h );
			backGraphics = (Graphics2D)(backImage.getGraphics());
			backGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
			newBackground = true;
		}

		graph.scale = graph.pixels/graph.units[graph.zoom];

		// draw graph of function f
		if ( newBackground ){
			backGraphics.setColor( getBackground() );
			backGraphics.fillRect( 0, 0, (int)w, (int)h );
			if ( graph.showGrid ) graph.drawGridLines( backGraphics );
			graph.drawAxes( backGraphics );
			graph.drawFunction( backGraphics );
			newBackground = false;
		}
		g.drawImage( backImage, 0, 0, this );
		
		String tmp = graph.applet.stat.getText(); // so that when splashpanel is redrawn, it doesn't change stat.
		graph.draw( g );
		graph.applet.stat.setText( tmp );
		graph.drawEndpoints( g );		

		g.setColor( new Color( 128,128,128,175 ) );
		g.setFont( new Font("Helvetica",Font.BOLD,18) );
		String str = "CLICK ANYWHERE TO BEGIN";
		g.drawString( str, (int)(w-g.getFontMetrics().stringWidth(str)-5), (int)(h-5) );
	}

	public void mouseClicked( MouseEvent me ){
		graph.applet.showFrame();
	}

	public void mouseEntered( MouseEvent me ){
	}

	public void mouseExited( MouseEvent me ){
	}

	public void mousePressed( MouseEvent me ){
	}

	public void mouseReleased(MouseEvent me){
	}	
}