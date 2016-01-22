import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

public class PolarApplet extends CalculusApplet{

	Graph rtheta = new Graph();
	
	public void stateChanged( ChangeEvent ce ) {
		try {
			Object obj = ce.getSource();
			if ( obj == a ){
				graph.a = a.getValue();
				b.setMinimum( graph.a );
				a.setMaximum( graph.b );
				graph.newStat = true;
				graph.newBackground = true;
				updateGraphs( graph );
			} else if ( obj == b ){
				graph.b = b.getValue();
				a.setMaximum( graph.b );
				b.setMinimum( graph.a );
				graph.newStat = true;
				graph.newBackground = true;
				updateGraphs( graph );
			} else {
				stateChangedDefault( ce );
			}
			//graph.repaint();
		} catch ( NumberFormatException nfe ){
		}
	}

	public void actionPerformed( ActionEvent ae ){
		Object obj = ae.getSource();
		if ( obj == zoomin ){
			graph.zoomIn();
			rtheta.zoomIn();
			if ( graph.zoom == 0) zoomin.setEnabled( false );
			zoomout.setEnabled( true );
		} else if ( obj == zoomout ){
			graph.zoomOut();
			rtheta.zoomOut();
			if ( graph.zoom == graph.units.length-1 ) zoomout.setEnabled( false );
			zoomin.setEnabled( true );
		} else if ( obj == animate ){
			((PolarCurvesGraph)graph).start();
			animate.setEnabled( false );
		}
		updateGraphs( graph );
	}

	public void updateGraphs( Graph g ){
		if ( g == graph ){
			rtheta.a = graph.a;
			rtheta.b = graph.b;
			rtheta.F = graph.F;
			rtheta.newBackground = graph.newBackground;
			rtheta.zoom = graph.zoom;
			rtheta.repaint();
			graph.repaint();
		} else if ( g == rtheta ){
			graph.a = rtheta.a;
			graph.b = rtheta.b;
			graph.repaint();
		}
	}
}