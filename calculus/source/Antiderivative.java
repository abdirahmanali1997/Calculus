import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;

public class Antiderivative extends CalculusApplet{
	
	public void init(){
		setup( "Antiderivatives and Slope Fields" );
				
		north.setLayout( new GridLayout(2,2,0,2) );
			// first row
			JPanel row = new JPanel( gridbaglayout );			
				constraints.gridwidth = 1;
				constraints.fill = GridBagConstraints.NONE;
				constraints.weightx = 0.0;
				JLabel label = new JLabel(" f(x)=");
				gridbaglayout.setConstraints( label, constraints );
				row.add( label );			

				constraints.weightx = 1.0;
				constraints.fill = GridBagConstraints.HORIZONTAL;
				gridbaglayout.setConstraints( f, constraints );
				row.add( f );
			north.add( row );

			row = new JPanel( gridbaglayout );
				constraints.weightx = 0.0;
				constraints.fill = GridBagConstraints.NONE;
				label = new JLabel(" F(x)=");
				gridbaglayout.setConstraints( label, constraints );
				row.add( label );

				constraints.weightx = 1.0;
				constraints.fill = GridBagConstraints.HORIZONTAL;
				gridbaglayout.setConstraints( g, constraints );
				row.add( g );
			north.add( row );

			// second row
			row = new JPanel( gridbaglayout );
				constraints.gridwidth = 1;
				constraints.weightx = 0.0;
				constraints.fill = GridBagConstraints.NONE;
				label = new JLabel(" a=");
				label.setForeground( Color.red );
				gridbaglayout.setConstraints( label, constraints );
				row.add( label );			

				constraints.weightx = 1.0;
				constraints.fill = GridBagConstraints.HORIZONTAL;
				gridbaglayout.setConstraints( a, constraints );
				row.add( a );
			north.add( row );

			row = new JPanel( gridbaglayout );
				constraints.weightx = 0.0;
				constraints.fill = GridBagConstraints.NONE;
				label = new JLabel(" b=");
				label.setForeground( Color.red );
				gridbaglayout.setConstraints( label, constraints );
				row.add( label );			

				constraints.weightx = 1.0;
				constraints.fill = GridBagConstraints.HORIZONTAL;
				gridbaglayout.setConstraints( b, constraints );
				row.add( b );
			north.add( row );
		contentpane.add( "North", north );
		
		contentpane.add( "Center", graph = new AntiderivativeGraph( this, F, G, A, B ) );
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

		splash = new SplashPanel( new AntiderivativeGraph(this,F,G,A,B ) );
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
				updateGraphs( graph, "Graphing..." );
			} else if ( obj == b ){
				graph.b = b.getValue();
				updateGraphs( graph, "Graphing..." );
			} else {
				stateChangedDefault( ce );
			}
		} catch ( NumberFormatException nfe ){
		}
	}
}

