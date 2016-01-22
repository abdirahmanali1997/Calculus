import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;

public class CauchyMeanValue extends CalculusApplet{
	
	
	public void init(){
		setup( "Cauchy's Mean Value Theorem" );
		
		updateMessage = "";
				
		north.setLayout( new GridLayout(2,1,0,2) );
			// first row
			JPanel row = new JPanel( gridbaglayout );			
				constraints.gridwidth = 1;
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
				label = new JLabel(" g(x)=");
				gridbaglayout.setConstraints( label, constraints );
				row.add( label );			

				constraints.fill = GridBagConstraints.HORIZONTAL;
				constraints.weightx = 1.0;
				gridbaglayout.setConstraints( g, constraints );
				row.add( g );
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

				constraints.fill = GridBagConstraints.HORIZONTAL;
				constraints.weightx = 1.0;
				gridbaglayout.setConstraints( a, constraints );
				row.add( a );

				constraints.weightx = 0.0;
				constraints.fill = GridBagConstraints.NONE;
				label = new JLabel( " b=" );
				label.setForeground( Color.green );
				gridbaglayout.setConstraints( label, constraints );
				row.add( label );

				constraints.fill = GridBagConstraints.HORIZONTAL;
				constraints.weightx = 1.0;
				gridbaglayout.setConstraints( b, constraints );
				row.add( b );

				constraints.weightx = 0.0;
				constraints.fill = GridBagConstraints.NONE;
				label = new JLabel( " c=" );
				label.setForeground( Color.red );
				gridbaglayout.setConstraints( label, constraints );
				row.add( label );

				constraints.fill = GridBagConstraints.HORIZONTAL;
				constraints.weightx = 1.0;
				gridbaglayout.setConstraints( c, constraints );
				row.add( c );
			north.add( row );
		contentpane.add( "North", north );
		
		contentpane.add( "Center", graph = new CauchyMeanValueGraph( this, F, G, A, B, C ) );
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
		choice.setVisible( false );

		graph.repaint();

		splash = new SplashPanel( new CauchyMeanValueGraph(this,F,G,A,B,C ) );
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
				graph.newBackground = true;
				updateGraphs( graph );
			} else if ( obj == b ){
				graph.b = b.getValue();
				graph.newBackground = true;
				updateGraphs( graph );
			} else if ( obj == c ){
				graph.c = c.getValue();
				graph.newBackground = true;
				updateGraphs( graph );
			} else {
				stateChangedDefault( ce );
			}
		} catch ( NumberFormatException nfe ){
		}
	}
}