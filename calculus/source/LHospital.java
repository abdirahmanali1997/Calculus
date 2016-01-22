import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

public class LHospital extends CalculusApplet{

	Graph fandg = new Graph();

	public void init(){
		setup( "L'Hospital's Rule" );

		north.setLayout( new GridLayout(1,1) );
			JPanel row = new JPanel( gridbaglayout );
				constraints.fill = GridBagConstraints.NONE;
				constraints.weightx = 0.0;
				JLabel label = new JLabel(" f(x)=");
				gridbaglayout.setConstraints( label, constraints );
				row.add( label );			

				constraints.fill = GridBagConstraints.HORIZONTAL;
				constraints.weightx = 1.0;
				gridbaglayout.setConstraints( f, constraints );
				row.add( f );

				constraints.fill = GridBagConstraints.NONE;
				constraints.weightx = 0.0;
				label = new JLabel(" g(x)=");
				gridbaglayout.setConstraints( label, constraints );
				row.add( label );			

				constraints.fill = GridBagConstraints.HORIZONTAL;
				constraints.weightx = 1.0;
				gridbaglayout.setConstraints( g, constraints );
				row.add( g );

				constraints.fill = GridBagConstraints.NONE;
				constraints.weightx = 0.0;
				label = new JLabel(" a=");
				label.setForeground( Color.red );
				gridbaglayout.setConstraints( label, constraints );
				row.add( label );			

				constraints.fill = GridBagConstraints.HORIZONTAL;
				constraints.weightx = 1.0;
				gridbaglayout.setConstraints( a, constraints );
				row.add( a );
				
			north.add( row );
		contentpane.add( "North", north );
		
		graph = new LHospitalGraph( this, F, G, A, 2 );
		fandg = new LHospitalGraph( this, F, G, A, 1 );
			
		JPanel center = new JPanel( new GridLayout(2,1,0,3) );
			center.add( fandg );
			center.add( graph );

		contentpane.add( "Center", center );
		if ( getParameter("originX") != null ){ 
			graph.originX = Double.parseDouble( getParameter("originX") );
		}
		if ( getParameter("originY") != null ){ 
			graph.originY = Double.parseDouble( getParameter("originY") );
		}
		if ( getParameter("zoom") != null ){ 
			graph.zoom += Integer.parseInt( getParameter("zoom") );
			fandg.zoom = graph.zoom;
		}
		
		//stat2.setVisible( true );
		choice.setVisible( false );

		splash = new SplashPanel( new LHospitalGraph( this, F, G, A, 1 ) );
			splash.graph.originX = graph.originX;
			splash.graph.originY = graph.originY;
			splash.graph.zoom = graph.zoom;
		getContentPane().add( splash );
	}


	public void stateChanged( ChangeEvent ce ) {
		try {
			Object obj = ce.getSource();
			if ( obj == a ){
				graph.a = a.getValue();
				fandg.a = a.getValue();
				graph.repaint();
				fandg.repaint();
			} else {
				stateChangedDefault( ce );
			}
			graph.repaint();
		} catch ( NumberFormatException nfe ){
		}
	}


	public void actionPerformed( ActionEvent ae ){
		Object obj = ae.getSource();
		if ( obj == zoomin ){
			graph.zoomIn();
			fandg.zoomIn();
			if ( graph.zoom == 0) zoomin.setEnabled( false );
			zoomout.setEnabled( true );
			a.setStepSize( graph.units[graph.zoom]/10.0 );
		} else if ( obj == zoomout ){
			graph.zoomOut();
			fandg.zoomOut();
			if ( graph.zoom == graph.units.length-1 ) zoomout.setEnabled( false );
			zoomin.setEnabled( true );
			a.setStepSize( graph.units[graph.zoom]/10.0 );
		}
		updateGraphs( graph );
	}


	public void updateGraphs( Graph g ){
		if ( g == graph ){
			fandg.originX = graph.originX;
			fandg.a = graph.a;
			fandg.newBackground = true;
			fandg.F = graph.F;
			fandg.G = graph.G;

			fandg.repaint();
			graph.repaint();
		} else if ( g == fandg ){
			graph.originX = fandg.originX;
			graph.a = fandg.a;
			graph.newBackground = true;

			fandg.repaint();
			graph.repaint();
		}
	}
}