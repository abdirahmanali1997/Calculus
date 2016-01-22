import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

public class JoeRoles extends CalculusApplet{

	public void init(){
		setup( "Area and Arclength of a Circle" );

		north.setLayout( new GridLayout(1,1) );
			// first row
		/*
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
*/
			// second row
			JPanel row = new JPanel( gridbaglayout );
				constraints.gridwidth = 1;
				constraints.weightx = 0.0;
				constraints.fill = GridBagConstraints.NONE;
				JLabel label = new JLabel(" a=");
				label.setForeground( Color.blue );
				gridbaglayout.setConstraints( label, constraints );
				row.add( label );			

				constraints.weightx = 1.0;
				constraints.fill = GridBagConstraints.HORIZONTAL;
				gridbaglayout.setConstraints( a, constraints );
				row.add( a );

				constraints.weightx = 0.0;
				constraints.fill = GridBagConstraints.NONE;
				label = new JLabel(" b=");
				label.setForeground( Color.green );
				gridbaglayout.setConstraints( label, constraints );
				row.add( label );

				constraints.weightx = 1.0;
				constraints.fill = GridBagConstraints.HORIZONTAL;
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
		
		contentpane.add( "Center", graph = new JoeRolesGraph( this, F, A, B, N ) );
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
		//choice = new Choice();
		choice.setVisible( false );
		/*
			choice.addItem("Left Riemann Sums");
			choice.addItem("Right Riemann Sums");
			choice.addItem("Midpoint Rule");
			choice.addItem("Trapezoidal Rule");
			choice.addItem("Simpson's Rule");
			//choice.addItem("Newton-Cotes Rule");
			choice.setSelectedItem( getParameter("type") );
		 */
		
		graph.repaint();
		splash = new SplashPanel( new JoeRolesGraph(this,F,A,B,N) );
			splash.graph.originX = graph.originX;
			splash.graph.originY = graph.originY;
			splash.graph.zoom = graph.zoom;
		getContentPane().add( splash );
	}

	public void itemStateChanged( ItemEvent e ){
		Object obj = e.getSource();
		if ( obj == choice ){
			graph.newStat = true;
			graph.repaint();
		}
	}
}