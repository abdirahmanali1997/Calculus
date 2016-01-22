import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

public class LeastSquares extends CalculusApplet{

	public void init(){
		setup( "Least Squares" );
		SpinnerNumberModel snm = (SpinnerNumberModel)(n.getModel());
		snm.setMaximum( new Integer(100) );
		snm.setMinimum( new Integer(2) );
		
		updateMessage = "";

		north.setLayout( new GridLayout(2,1,0,5) );
			// first row
			JPanel row = new JPanel( gridbaglayout );			
				constraints.gridwidth = 1;
				constraints.weightx = 0.0;
				constraints.fill = GridBagConstraints.NONE;
				JLabel label = new JLabel(" x=");
				label.setForeground( Color.blue );
				gridbaglayout.setConstraints( label, constraints );
				row.add( label );			
		
				constraints.weightx = 1.0;
				constraints.fill = GridBagConstraints.HORIZONTAL;
				gridbaglayout.setConstraints( c, constraints );
				row.add( c );
		
				constraints.weightx = 0.0;
				constraints.fill = GridBagConstraints.NONE;
				label = new JLabel(" y=");
				label.setForeground( Color.blue );
				gridbaglayout.setConstraints( label, constraints );
				row.add( label );
		
				constraints.weightx = 1.0;
				constraints.fill = GridBagConstraints.HORIZONTAL;
				gridbaglayout.setConstraints( d, constraints );
				row.add( d );

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
				label = new JLabel( " m=");
				label.setForeground( Color.green );
				gridbaglayout.setConstraints( label, constraints );
				row.add( label );

				constraints.weightx = 1.0;
				constraints.fill = GridBagConstraints.HORIZONTAL;
				gridbaglayout.setConstraints( a, constraints );
				row.add( a );

				constraints.weightx = 0.0;
				constraints.fill = GridBagConstraints.NONE;
				label = new JLabel( " b=");
				label.setForeground( Color.green );
				gridbaglayout.setConstraints( label, constraints );
				row.add( label );

				constraints.weightx = 1.0;
				constraints.fill = GridBagConstraints.HORIZONTAL;
				gridbaglayout.setConstraints( b, constraints );
				row.add( b );
			north.add( row );
		contentpane.add( "North", north );

		contentpane.add( "Center", graph = new LeastSquaresGraph( this, A, B, N ) );
		if ( getParameter("originX") != null ){ 
			graph.originX = Double.parseDouble( getParameter("originX") );
		}
		if ( getParameter("originY") != null ){ 
			graph.originY = Double.parseDouble( getParameter("originY") );
		}
		if ( getParameter("zoom") != null ){ 
			graph.zoom += Integer.parseInt( getParameter("zoom") );
		}

		//choice.setVisible( false );
		choice.addItem("Hide Least Squares Line");
		choice.addItem("Show Least Squares Line");
		//choice.setSelectedItem( getParameter("type") );

		graph.repaint();

		splash = new SplashPanel( new LeastSquaresGraph(this,A,B,N ) );
			splash.graph.originX = graph.originX;
			splash.graph.originY = graph.originY;
			splash.graph.zoom = graph.zoom;
		getContentPane().add( splash );
	}

	
	public void stateChanged( ChangeEvent ce ) {
		try {
			Object obj = ce.getSource();
			if ( obj ==c ){
				if ( LeastSquaresGraph.clickI != -1 && LeastSquaresGraph.I == -1 ) 
					LeastSquaresGraph.x[LeastSquaresGraph.clickI] = c.getValue();
			} else if ( obj == d ){
				if ( LeastSquaresGraph.clickI != -1 && LeastSquaresGraph.I == -1 ) 
					LeastSquaresGraph.y[LeastSquaresGraph.clickI] = d.getValue();
			} else {
				stateChangedDefault( ce );
			}
			graph.repaint();
		} catch ( NumberFormatException nfe ){
		}
	}
}