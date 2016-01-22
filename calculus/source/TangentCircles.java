import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

public class TangentCircles extends CalculusApplet{

	public void init(){
		setup( "Tangent Circles and the Curvature of a Function" );
		
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
				label.setForeground( Color.blue );
				gridbaglayout.setConstraints( label, constraints );
				row.add( label );			

				constraints.fill = GridBagConstraints.HORIZONTAL;
				constraints.weightx = 0.5;
				a.setForeground( Color.blue );
				gridbaglayout.setConstraints( a, constraints );
				row.add( a );

				constraints.weightx = 0.0;
				constraints.fill = GridBagConstraints.NONE;
				label = new JLabel( " b=" );
				label.setForeground( Color.green );
				gridbaglayout.setConstraints( label, constraints );
				row.add( label );

				constraints.fill = GridBagConstraints.HORIZONTAL;
				constraints.weightx = 0.5;
				b.setForeground( Color.green );
				gridbaglayout.setConstraints( b, constraints );
				row.add( b );

				constraints.weightx = 0.0;
				constraints.fill = GridBagConstraints.NONE;
				label = new JLabel( " c=" );
				label.setForeground( Color.magenta );
				gridbaglayout.setConstraints( label, constraints );
				row.add( label );

				constraints.fill = GridBagConstraints.HORIZONTAL;
				constraints.weightx = 0.5;
				c.setForeground( Color.magenta );
				gridbaglayout.setConstraints( c, constraints );
				row.add( c );
			north.add( row );
		contentpane.add( "North", north );
		
		contentpane.add( "Center", graph = new TangentCirclesGraph( this, F, A, B, C ) );
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

		splash = new SplashPanel( new TangentCirclesGraph(this,F,A,B,C ) );
			splash.graph.originX = graph.originX;
			splash.graph.originY = graph.originY;
			splash.graph.zoom = graph.zoom;
		getContentPane().add( splash );
	}


	public void updateGraphs( Graph g ){
		g.repaint();
	}
}