import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;

public class MeanValue extends CalculusApplet{
	
	public void init(){
		setup( "Mean Value Theorem" );
		
		updateMessage = "";
				
		north.setLayout( new GridLayout(2,1,0,5) );
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
		
		contentpane.add( "Center", graph = new MeanValueGraph( this, F, A, B, C ) );
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
			//choice.addItem("Show Secant Line Only");
			//choice.addItem("Show Tangent Line Only");
			//choice.addItem("Show Both");
			//choice.addItem("Show Neither");

		graph.repaint();

		splash = new SplashPanel( new MeanValueGraph(this,F,A,B,C ) );
			splash.graph.originX = graph.originX;
			splash.graph.originY = graph.originY;
			splash.graph.zoom = graph.zoom;
		getContentPane().add( splash );
	}
}