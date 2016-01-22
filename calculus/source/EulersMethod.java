import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;

public class EulersMethod extends CalculusApplet{
	
	public void init(){
		setup( "Euler's Method" );
				
		north.setLayout( new GridLayout(1,1) );
			// first row
			JPanel row = new JPanel( gridbaglayout );			
				constraints.fill = GridBagConstraints.NONE;
				constraints.weightx = 0.0;
				JLabel label = new JLabel("f(x,y)=");
				gridbaglayout.setConstraints( label, constraints );
				row.add( label );			

				constraints.fill = GridBagConstraints.HORIZONTAL;
				constraints.weightx = 1.0;
				gridbaglayout.setConstraints( f, constraints );
				row.add( f );

				constraints.weightx = 0.0;
				constraints.fill = GridBagConstraints.NONE;
				label = new JLabel(" a=");
				label.setForeground( Color.red );
				gridbaglayout.setConstraints( label, constraints );
				row.add( label );			

				constraints.fill = GridBagConstraints.HORIZONTAL;
				constraints.weightx = 0.5;
				gridbaglayout.setConstraints( a, constraints );
				row.add( a );

				constraints.weightx = 0.0;
				constraints.fill = GridBagConstraints.NONE;
				label = new JLabel( " b=" );
				label.setForeground( Color.red );
				gridbaglayout.setConstraints( label, constraints );
				row.add( label );

				constraints.fill = GridBagConstraints.HORIZONTAL;
				constraints.weightx = 0.5;
				gridbaglayout.setConstraints( b, constraints );
				row.add( b );

				constraints.weightx = 0.0;
				constraints.fill = GridBagConstraints.NONE;
				label = new JLabel( " h=" );
				label.setForeground( Color.black );
				gridbaglayout.setConstraints( label, constraints );
				row.add( label );

				constraints.fill = GridBagConstraints.HORIZONTAL;
				constraints.weightx = 0.5;
				gridbaglayout.setConstraints( c, constraints );
				row.add( c );
			north.add( row );
		contentpane.add( "North", north );
		
		contentpane.add( "Center", graph = new EulersMethodGraph( this, F, A, B, C ) );
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
			choice.addItem("Euler's Method");
			choice.addItem("Modified Euler's Method");
		//choice.add("Show Both");

		graph.repaint();

		splash = new SplashPanel( new EulersMethodGraph(this,F,A,B,C ) );
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
			} else if ( obj == b ){
				graph.b = b.getValue();
			} else if ( obj == c ){
				graph.c = c.getValue();
			} else {
				stateChangedDefault( ce );
			}
			graph.repaint();
		} catch ( NumberFormatException nfe ){
		}
	}
}