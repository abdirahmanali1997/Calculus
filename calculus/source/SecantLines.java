import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

public class SecantLines extends CalculusApplet{

	
	public void init(){
		setup( "Secant Lines and the Slope of a Curve" );
				
		contentpane.add( "Center", graph = new SecantLinesGraph( this, F, A, B ) );

		north.setLayout( new GridLayout(1,1) );
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
				choice2.setForeground( Color.green );
				choice2.addItem( " b=" );
				choice2.addItem( " h=" );
				gridbaglayout.setConstraints( choice2, constraints );
				row.add( choice2 );
				//label = new JLabel( " b=" );
				//label.setForeground( Color.green );
				//gridbaglayout.setConstraints( label, constraints );
				//row.add( label );

				constraints.fill = GridBagConstraints.HORIZONTAL;
				constraints.weightx = 1.0;
				gridbaglayout.setConstraints( b, constraints );
				row.add( b );
			north.add( row );
		contentpane.add( "North", north );
		
		if ( getParameter("originX") != null ){ 
			graph.originX = Double.parseDouble( getParameter("originX") );
		}
		if ( getParameter("originY") != null ){ 
			graph.originY = Double.parseDouble( getParameter("originY") );
		}
		if ( getParameter("zoom") != null ){ 
			graph.zoom += Integer.parseInt( getParameter("zoom") );
		}
		
		choice.addItem("Show Secant Line");
		choice.addItem("Show Tangent Line");
		choice.addItem("Show Both");
		choice.addItem("Show Neither");

		graph.repaint();

		splash = new SplashPanel( new SecantLinesGraph(this,F,A,B ) );
			splash.graph.originX = graph.originX;
			splash.graph.originY = graph.originY;
			splash.graph.zoom = graph.zoom;
		getContentPane().add( splash );
	}

	public void itemStateChanged( ItemEvent e ){
		Object obj = e.getSource();
		if ( obj == choice ){
			updateGraphs( graph );
		} else if ( obj == choice2 ){
			String c2 = choice2.getSelectedItem().toString();
// System.out.println( c2 );
// this seems to be called TWICE when choice2 is changed
// why is it called TWICE
// how can I set these values so that it doesn't matter how many times it is called!
			if ( c2.equals( " h=" ) ){
				//b.setValue( graph.b - graph.a );
			} else if ( c2.equals( " b=" ) ){
				//b.setValue( graph.b + graph.a );
			}
			updateGraphs( graph );
		}
	}

	public void updateGraphs( Graph g ){
		g.repaint();
	}
}