import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;

public class TaylorSeries extends CalculusApplet{
	
	public void init(){
		setup( "Taylor Polynomials" );
		SpinnerNumberModel snm = (SpinnerNumberModel)(n.getModel());
		snm.setMaximum( new Integer(50) );
		
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

				constraints.weightx = 0.0;
				constraints.fill = GridBagConstraints.NONE;
				label = new JLabel(" a=");
				label.setForeground( Color.red );
				gridbaglayout.setConstraints( label, constraints );
				row.add( label );			

				constraints.fill = GridBagConstraints.HORIZONTAL;
				constraints.weightx = 1.0;
				a.setForeground( Color.red );
				gridbaglayout.setConstraints( a, constraints );
				row.add( a );

				constraints.weightx = 0.0;
				constraints.fill = GridBagConstraints.NONE;
				label = new JLabel(" b=");
				label.setForeground( Color.green );
				gridbaglayout.setConstraints( label, constraints );
				row.add( label );			

				constraints.fill = GridBagConstraints.HORIZONTAL;
				constraints.weightx = 1.0;
				b.setForeground( Color.green );
				gridbaglayout.setConstraints( b, constraints );
				row.add( b );

				constraints.weightx = 0.0;
				constraints.fill = GridBagConstraints.NONE;
				label = new JLabel(" n=");
				gridbaglayout.setConstraints( label, constraints );
				row.add( label );

				gridbaglayout.setConstraints( n, constraints );
				row.add( n );
			north.add( row );
		contentpane.add( "North", north );
		
		contentpane.add( "Center", graph = new TaylorSeriesGraph( this, F, A, B, N ) );
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
			choice.addItem("Show Taylor Polynomial Only");
			choice.addItem("Show Remainder Only");
			choice.addItem("Show Both");

		graph.repaint();

		splash = new SplashPanel( new TaylorSeriesGraph(this,F,A,B,N ) );
			splash.graph.originX = graph.originX;
			splash.graph.originY = graph.originY;
			splash.graph.zoom = graph.zoom;
		getContentPane().add( splash );
	}


	public void updateGraphs( Graph g ){
		g.repaint();
	}
}