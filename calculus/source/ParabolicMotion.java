import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

public class ParabolicMotion extends CalculusApplet{
	

	public void init(){
		setup( "Projectile Motion" );

		D = 1.0;

		north.setLayout( new GridLayout(2,1,0,5) );
			JPanel row = new JPanel( gridbaglayout );
				constraints.fill = GridBagConstraints.NONE;
				constraints.weightx = 0.0;
				JLabel label = new JLabel(" Initial height (ft) = ");
				label.setForeground( Color.red );
				gridbaglayout.setConstraints( label, constraints );
				row.add( label );			

				constraints.fill = GridBagConstraints.HORIZONTAL;
				constraints.weightx = 1.0;
				gridbaglayout.setConstraints( a, constraints );
				row.add( a );

				constraints.fill = GridBagConstraints.NONE;
				constraints.weightx = 0.0;
				label = new JLabel(" Initial velocity (ft/s) = ");
				gridbaglayout.setConstraints( label, constraints );
				row.add( label );			

				constraints.fill = GridBagConstraints.HORIZONTAL;
				constraints.weightx = 1.0;
				gridbaglayout.setConstraints( b, constraints );
				row.add( b );

			north.add( row );

			// second row
			row = new JPanel( gridbaglayout );
				constraints.gridwidth = 1;
				constraints.weightx = 0.0;
				constraints.fill = GridBagConstraints.NONE;
				label = new JLabel(" Initial angle = ");
				label.setForeground( Color.blue );
				gridbaglayout.setConstraints( label, constraints );
				row.add( label );			

				constraints.weightx = 1.0;
				constraints.fill = GridBagConstraints.HORIZONTAL;
				gridbaglayout.setConstraints( c, constraints );
				row.add( c );

				constraints.weightx = 0.0;
				constraints.fill = GridBagConstraints.NONE;
				choice2.setForeground( Color.black );
				choice2.addItem( "radians" );
				choice2.addItem( "degrees" );
				gridbaglayout.setConstraints( choice2, constraints );
				row.add( choice2 );

/*		label = new JLabel(" radians = ");
				label.setForeground( Color.black );
				gridbaglayout.setConstraints( label, constraints );
				row.add( label );

				constraints.weightx = 1.0;
				constraints.fill = GridBagConstraints.HORIZONTAL;
				gridbaglayout.setConstraints( d, constraints );
				row.add( d );

				constraints.weightx = 0.0;
				constraints.fill = GridBagConstraints.NONE;
				label = new JLabel(" degrees ");
				label.setForeground( Color.black );
				gridbaglayout.setConstraints( label, constraints );
				row.add( label );*/
			north.add( row );
		contentpane.add( "North", north );
		
		graph = new ParabolicMotionGraph( this, A, B, C );
		contentpane.add( "Center", graph );
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
			choice.addItem("Automatic");
			choice.addItem("Manual");
			choice.addItem("Horizontal Distance");
			choice.addItem("Maximum Height");
			choice.addItem("Total Distance");
			choice.addItem("Time of travel");

		southpanel.add( "West", animate = new JButton("Launch") );
			animate.addActionListener( this );

		splash = new SplashPanel( new ParabolicMotionGraph(this,A,B,C ) );
			splash.graph.originX = graph.originX;
			splash.graph.originY = graph.originY;
			splash.graph.zoom = graph.zoom;
		getContentPane().add( splash );

		// reset text for initial angle
		c.setText( getParameter("c") );
	}


	public void stateChanged( ChangeEvent ce ) {
		try {
			Object obj = ce.getSource();
			if ( obj == a ){
				graph.a = a.getValue();
				graph.newBackground = true;
				updateGraphs( graph );
			} else if ( obj == b ){
				graph.b = b.getValue();
				graph.newBackground = true;
				updateGraphs( graph );
			} else if ( obj == c ){
				// note that D is changed when choice2 is changed
				graph.c = c.getValue()/D;
				graph.newBackground = true;
				updateGraphs( graph );
			} else {
				stateChangedDefault( ce );
			}
		} catch ( NumberFormatException nfe ){
		}
	}

	public void actionPerformed( ActionEvent ae ){
		Object obj = ae.getSource();
		if ( obj == zoomin ){
			graph.zoomIn();
			if ( graph.zoom == 0) zoomin.setEnabled( false );
			zoomout.setEnabled( true );
			a.setStepSize( graph.units[graph.zoom]/10.0 );
			b.setStepSize( graph.units[graph.zoom]/10.0 );
			c.setStepSize( graph.units[graph.zoom]/10.0 );
			d.setStepSize( graph.units[graph.zoom]/10.0 );
		} else if ( obj == zoomout ){
			graph.zoomOut();
			if ( graph.zoom == graph.units.length-1 ) zoomout.setEnabled( false );
			zoomin.setEnabled( true );
			a.setStepSize( graph.units[graph.zoom]/10.0 );
			b.setStepSize( graph.units[graph.zoom]/10.0 );
			c.setStepSize( graph.units[graph.zoom]/10.0 );
			d.setStepSize( graph.units[graph.zoom]/10.0 );
		} else if ( obj == animate ){
			((ParabolicMotionGraph)graph).launch();
		}
		updateGraphs( graph );
	}


	public void itemStateChanged( ItemEvent e ){
		Object obj = e.getSource();
		if ( obj == choice ){
			updateGraphs( graph );
		} else if ( obj == choice2 ){
			String c2 = choice2.getSelectedItem().toString();
			if ( c2.equals( "radians" ) ){
				D = 1.0;
				c.setValue( graph.c );
			} else if ( c2.equals( "degrees" ) ){
				D = 180.0/Math.PI;
				c.setValue( graph.c*D );
			}
		}
	}
}