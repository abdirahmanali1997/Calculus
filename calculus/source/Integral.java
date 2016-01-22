import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class Integral extends CalculusApplet{
	
	public void init(){
		setup( "The Integral of a Function" );
		SpinnerNumberModel snm = (SpinnerNumberModel)(n.getModel());
		snm.setMaximum( new Integer(20) );
		
		north.setLayout( new GridLayout(2,1) );
		// first row
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
		
		constraints.weightx = 0.0;
		constraints.fill = GridBagConstraints.NONE;
		label = new JLabel(" n=");
		gridbaglayout.setConstraints( label, constraints );
		row.add( label );
		
		gridbaglayout.setConstraints( n, constraints );
		row.add( n );
		north.add( row );
		
		// second row
		row = new JPanel( gridbaglayout );
		constraints.gridwidth = 1;
		constraints.weightx = 0.0;
		constraints.fill = GridBagConstraints.NONE;
		label = new JLabel(" a=");
		label.setForeground( Color.blue );
		gridbaglayout.setConstraints( label, constraints );
		row.add( label );			
		
		constraints.weightx = 1.0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		gridbaglayout.setConstraints( a, constraints );
		row.add( a );
		
		constraints.weightx = 0.0;
		constraints.fill = GridBagConstraints.NONE;
		label = new JLabel(" b=");
		label.setForeground( Color.green );
		gridbaglayout.setConstraints( label, constraints );
		row.add( label );
		
		constraints.weightx = 1.0;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		gridbaglayout.setConstraints( b, constraints );
		row.add( b );
		north.add( row );
		contentpane.add( "North", north );
		
		contentpane.add( "Center", graph = new IntegralGraph( this, F, A, B, N ) );
		if ( getParameter("originX") != null ){ 
			graph.originX = Double.parseDouble( getParameter("originX") );
		}
		if ( getParameter("originY") != null ){ 
			graph.originY = Double.parseDouble( getParameter("originY") );
		}
		if ( getParameter("zoom") != null ){ 
			graph.zoom += Integer.parseInt( getParameter("zoom") );
		}
		
		choice.setVisible( false );

		graph.repaint();

		splash = new SplashPanel( new IntegralGraph(this,F,A,B,N ) );
			splash.graph.originX = graph.originX;
			splash.graph.originY = graph.originY;
			splash.graph.zoom = graph.zoom;
		getContentPane().add( splash );
	}
}