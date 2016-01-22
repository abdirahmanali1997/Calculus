import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

public class CircleArea extends CalculusApplet{

	public void init(){
		setup( "Area of a Circle" );
		SpinnerNumberModel snm = (SpinnerNumberModel)(n.getModel());
		snm.setMinimum( new Integer(3) );

		north.setLayout( new BorderLayout() );
		north.add( "West", choice );
		JPanel east = new JPanel();	
		east.add( new JLabel( " n=" ) );
		east.add( n );
		north.add( "East", east );
		
		south.remove( choice );

		contentpane.add( "North", north );
		
		contentpane.add( "Center", graph = new CircleAreaGraph( this, N ) );
		if ( getParameter("originX") != null ){ 
			graph.originX = Double.parseDouble( getParameter("originX") );
		}
		if ( getParameter("originY") != null ){ 
			graph.originY = Double.parseDouble( getParameter("originY") );
		}
		if ( getParameter("zoom") != null ){ 
			graph.zoom += Integer.parseInt( getParameter("zoom") );
		}
		
		stat2.setVisible( true );

		//choice = new Choice();
			choice.addItem("Inscribed Polygon");
			choice.addItem("Circumscribed Polygon");
			choice.addItem("Show Both");
			choice.addItem("Show Sequences");
				//choice.addItem("Newton-Cotes Rule");
			choice.setSelectedItem( getParameter("type") );
		
		graph.repaint();
		splash = new SplashPanel( new CircleAreaGraph(this,N) );
			splash.graph.originX = graph.originX;
			splash.graph.originY = graph.originY;
			splash.graph.zoom = graph.zoom;
		getContentPane().add( splash );
	}

	public void itemStateChanged( ItemEvent e ){
		Object obj = e.getSource();
		if ( obj == choice ){
			graph.newStat = true;
			if ( choice.getSelectedItem().equals("Show Sequences") ){
				if ( graph.originX*graph.originX + graph.originY*graph.originY < 9 ){
					graph.originX = 40.0;
					graph.originY = Math.PI;
					graph.setZoom(15);
				}
			} else {
				if ( graph.originX*graph.originX + graph.originY*graph.originY > 9 ){
					graph.originX = 0.0;
					graph.originY = 0.0;
					graph.setZoom(11);
				}
			}
			graph.repaint();
		}
	}
}