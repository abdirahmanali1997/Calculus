import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

public class ParametricApplet extends CalculusApplet{

	Graph xoftee = new Graph();
	Graph yoftee = new Graph();
	Graph zoftee = new Graph();	
	
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
		} catch ( NumberFormatException nfe ){
		}
	}

	// overwritten by SpaceCurves
	public void actionPerformed( ActionEvent ae ){
		Object obj = ae.getSource();
		if ( obj == zoomin ){
			graph.zoomIn();
			xoftee.zoomIn();
			yoftee.zoomIn();
			if ( graph.zoom == 0) zoomin.setEnabled( false );
			zoomout.setEnabled( true );
		} else if ( obj == zoomout ){
			graph.zoomOut();
			xoftee.zoomOut();
			yoftee.zoomOut();
			if ( graph.zoom == graph.units.length-1 ) zoomout.setEnabled( false );
			zoomin.setEnabled( true );
		} else if ( obj == animate ){
			((ParametricCurvesGraph)graph).start();
			animate.setEnabled( false );
		}
		updateGraphs( graph );
	}

	// overwritten by SpaceCurves
	public void updateGraphs( Graph g ){
		if ( g == graph ){
			xoftee.a = graph.a;
			xoftee.b = graph.b;
			xoftee.F = graph.F;
			xoftee.newBackground = graph.newBackground;

			yoftee.a = graph.a;
			yoftee.b = graph.b;
			yoftee.F = graph.G;
			yoftee.newBackground = graph.newBackground;

			xoftee.repaint();
			yoftee.repaint();
			graph.repaint();
		} else if ( g == xoftee ){
			graph.a = xoftee.a;
			graph.b = xoftee.b;
			graph.repaint();

			yoftee.a = graph.a;
			yoftee.b = graph.b;
			yoftee.repaint();
		} else if ( g == yoftee ){
			graph.a = yoftee.a;
			graph.b = yoftee.b;
			graph.repaint();

			xoftee.a = graph.a;
			xoftee.b = graph.b;
			xoftee.repaint();
		}
	}
}