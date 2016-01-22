import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

public class Sequences extends CalculusApplet{
	
	public void init(){
		setup( "Sequences and Series" );

		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
			frame.setSize( screen.width/2, screen.height/2);

		SpinnerNumberModel snm = (SpinnerNumberModel)(n.getModel());
		snm.setMinimum( new Integer(1) );
		snm = (SpinnerNumberModel)(m.getModel());
		snm.setMinimum( new Integer(1) );
				
		north.setLayout( new GridLayout(1,1) );
			// first row
			JPanel row = new JPanel( gridbaglayout );			
				constraints.fill = GridBagConstraints.NONE;
				constraints.weightx = 0.0;
				JLabel label = new JLabel(" a(n)=");
				label.setForeground( Color.blue );
				gridbaglayout.setConstraints( label, constraints );
				row.add( label );			

				constraints.fill = GridBagConstraints.HORIZONTAL;
				constraints.weightx = 1.0;
				gridbaglayout.setConstraints( f, constraints );
				row.add( f );

				constraints.fill = GridBagConstraints.NONE;
				constraints.weightx = 0.0;
				label = new JLabel(" b(n)=");
				label.setForeground( Color.magenta );
				gridbaglayout.setConstraints( label, constraints );
				row.add( label );			

				constraints.fill = GridBagConstraints.HORIZONTAL;
				constraints.weightx = 1.0;
				gridbaglayout.setConstraints( g, constraints );
				row.add( g );

				constraints.weightx = 0.0;
				constraints.fill = GridBagConstraints.NONE;
				label = new JLabel(" x-scale:");
				//label.setForeground( Color.blue );
				gridbaglayout.setConstraints( label, constraints );
				row.add( label );			

				gridbaglayout.setConstraints( a, constraints );
				row.add( m );

				label = new JLabel( " y-scale:" );
				//label.setForeground( Color.green );
				gridbaglayout.setConstraints( label, constraints );
				row.add( label );

				gridbaglayout.setConstraints( b, constraints );
				row.add( n );
			north.add( row );
		contentpane.add( "North", north );
		
		contentpane.add( "Center", graph = new SequencesGraph( this, F, G, M, N ) );
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
		//zoomin.setVisible( false );
		//zoomout.setVisible( false );

		//choice.setVisible( false );
		choice.addItem("Show Sequence Only");
		choice.addItem("Sequence and Series");
		choice.addItem("Comparison Test");
		choice.addItem("Limit Comparison Test");
		choice.addItem("Sequence and Ratio Test");
		choice.addItem("Sequence and Root Test");
		choice.setSelectedItem( getParameter("type") );

		graph.repaint();

		splash = new SplashPanel( new SequencesGraph(this,F,G,M,N ) );
			splash.graph.n = graph.n;
			splash.graph.m = graph.m;
			splash.graph.originX = graph.originX;
			splash.graph.originY = graph.originY;
			splash.graph.zoom = graph.zoom;
		getContentPane().add( splash );
	}

	boolean zoom = false;
	public void actionPerformed( ActionEvent ae ){
		Object obj = ae.getSource();
		if ( obj == zoomin ){
			zoom = true;
			m.setValue( new Integer(Math.max(1,((Integer)m.getValue()).intValue()/2)) );
		} else if ( obj == zoomout ){
			zoom = true;
			m.setValue( new Integer(Math.min(10000,2*((Integer)m.getValue()).intValue())) );
		}
		updateGraphs( graph );
	}

	public void itemStateChanged( ItemEvent e ){
		Object obj = e.getSource();
		if ( obj == choice ){
			graph.newBackground = true;
			graph.repaint();
			String s = choice.getSelectedItem().toString();
			if ( s.equals( "Comparison Test" ) || s.equals( "Limit Comparison Test" ) ){
				g.setEnabled( true );
			} else {
				g.setEnabled( false );
			}
		}
	}

	public void stateChanged( ChangeEvent ce ){
		Object obj = ce.getSource();
		try {
			if ( obj == m ){
				if ( !zoom ) graph.originX = graph.originX*((Integer)m.getValue()).intValue()/graph.m;
				graph.m = ((Integer)m.getValue()).intValue();
				graph.newBackground = true;
				stat.setText( "Calculating..." );
				stat2.setText( "" );
				updateGraphs( graph );
				zoom = false;
			} else if ( obj == n ){
				if ( !zoom ) graph.originY = graph.originY*((Integer)n.getValue()).intValue()/graph.n;
				graph.n = ((Integer)n.getValue()).intValue();
				graph.newBackground = true;
				stat.setText( "Calculating..." );
				stat2.setText( "" );
				updateGraphs( graph );
				zoom = false;
			} else {
				stateChangedDefault( ce );
			}
		} catch ( NumberFormatException nfe ){
		}
	}
}