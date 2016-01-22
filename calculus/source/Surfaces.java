import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Surfaces extends CalculusApplet{
	
	public void init(){
		setup( "Surfaces" );		
		SpinnerNumberModel snm = (SpinnerNumberModel)(n.getModel());
		snm.setMaximum( new Integer(10) );
		snm.setMinimum( new Integer(1) );
		
		north.setLayout( new GridLayout(2,1) );
			// first row
			JPanel row = new JPanel( gridbaglayout );			
				constraints.fill = GridBagConstraints.NONE;
				constraints.weightx = 0.0;
				JLabel label = new JLabel(" f(x,y)=");
				gridbaglayout.setConstraints( label, constraints );
				row.add( label );			

				constraints.fill = GridBagConstraints.HORIZONTAL;
				constraints.weightx = 1.0;
				gridbaglayout.setConstraints( f, constraints );
				row.add( f );

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
/*				constraints.weightx = 0.0;
				constraints.fill = GridBagConstraints.NONE;
				label = new JLabel(" a=");
				label.setForeground( Color.blue );
				gridbaglayout.setConstraints( label, constraints );
				row.add( label );			
*/
				constraints.weightx = 1.0;
				constraints.fill = GridBagConstraints.HORIZONTAL;
				gridbaglayout.setConstraints( a, constraints );
				row.add( a );
				a.setHorizontalAlignment( JTextField.RIGHT );

				constraints.weightx = 0.0;
				constraints.fill = GridBagConstraints.NONE;
				label = new JLabel(leq + "x" + leq);
				label.setForeground( Color.black );
				gridbaglayout.setConstraints( label, constraints );
				row.add( label );			
/*
				constraints.weightx = 0.0;
				constraints.fill = GridBagConstraints.NONE;
				label = new JLabel(" b=");
				label.setForeground( Color.green );
				gridbaglayout.setConstraints( label, constraints );
				row.add( label );
*/
				constraints.weightx = 1.0;
				constraints.fill = GridBagConstraints.HORIZONTAL;
				gridbaglayout.setConstraints( b, constraints );
				row.add( b );
/*
				constraints.weightx = 0.0;
				constraints.fill = GridBagConstraints.NONE;
				label = new JLabel(" c=");
				label.setForeground( Color.red );
				gridbaglayout.setConstraints( label, constraints );
				row.add( label );
*/
				constraints.weightx = 0.0;
				constraints.fill = GridBagConstraints.NONE;
				label = new JLabel(" ");
				label.setForeground( Color.black );
				gridbaglayout.setConstraints( label, constraints );
				row.add( label );			

				constraints.weightx = 1.0;
				constraints.fill = GridBagConstraints.HORIZONTAL;
				gridbaglayout.setConstraints( c, constraints );
				row.add( c );
				c.setHorizontalAlignment( JTextField.RIGHT );

				constraints.weightx = 0.0;
				constraints.fill = GridBagConstraints.NONE;
				label = new JLabel(leq + "y" + leq);
				label.setForeground( Color.black );
				gridbaglayout.setConstraints( label, constraints );
				row.add( label );			
/*
				constraints.weightx = 0.0;
				constraints.fill = GridBagConstraints.NONE;
				label = new JLabel(" d=");
				label.setForeground( Color.magenta );
				gridbaglayout.setConstraints( label, constraints );
				row.add( label );
*/
				constraints.weightx = 1.0;
				constraints.fill = GridBagConstraints.HORIZONTAL;
				gridbaglayout.setConstraints( d, constraints );
				row.add( d );

			north.add( row );
		contentpane.add( "North", north );
		
		contentpane.add( "Center", graph = new SurfacesGraph( this, F, A, B, C, D, N ) );
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
		choice.addItem("Opaque");
		choice.addItem("Transparent");
		choice.addItem("Wire Frame - Opaque");
		choice.addItem("Wire Frame - Transparent");
		//choice.setSelectedItem( getParameter("type") );

		//southpanel.add( "West", colorchoice = new ColorComboBox() );
		colorchoice.setVisible( true );
			colorchoice.addItemListener( this );
			//colorchoice.setSelectedIndex( colorchoice.colors.length-1 );

		graph.repaint();

		splash = new SplashPanel( new SurfacesGraph(this,F,A,B,C,D,N ) );
			splash.graph.originX = graph.originX;
			splash.graph.originY = graph.originY;
			splash.graph.zoom = graph.zoom;
		getContentPane().add( splash );
	}


	public void itemStateChanged( ItemEvent e ){
		Object obj = e.getSource();
		if ( obj == choice ){
			updateGraphs( graph );
		} else if ( obj == colorchoice ){
			updateGraphs( graph );
		}
	}
}