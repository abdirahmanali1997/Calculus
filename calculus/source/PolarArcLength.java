import java.awt.*;
import javax.swing.*;

public class PolarArcLength extends PolarApplet{
		
	public void init(){
		setup( "The Length of a Polar Curve" );
				
		north.setLayout( new GridLayout(2,1) );
			// first row
			JPanel row = new JPanel( gridbaglayout );			
				constraints.fill = GridBagConstraints.NONE;
				constraints.weightx = 0.0;
				JLabel label = new JLabel(" r(t)=");
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
				constraints.weightx = 0.0;
				constraints.fill = GridBagConstraints.NONE;
				label = new JLabel(" a=");
				label.setForeground( Color.blue );
				gridbaglayout.setConstraints( label, constraints);
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
			north.add( row );
		contentpane.add( "North", north );
		
		graph = new PolarArcLengthGraph( this, F, A, B, N );
		rtheta = new Graph();
			rtheta.radians = true;
			rtheta.xlabel = "t";
			rtheta.ylabel = "r(t)";
			rtheta.variable = "t";
			rtheta.applet = this;
			rtheta.a = graph.a;
			rtheta.b = graph.b;
			rtheta.colorA = Color.blue;
			rtheta.colorB = Color.green;
			rtheta.endline = new BasicStroke( 2.0f );
			rtheta.F.parseExpression( F );
		
		JSplitPane center = new JSplitPane( JSplitPane.VERTICAL_SPLIT, true, rtheta, graph );
			center.setDividerLocation( 0 );
			center.setDividerSize( 10 );

		contentpane.add( "Center", center );
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

		splash = new SplashPanel( new PolarArcLengthGraph(this,F,A,B,N ) );
			splash.graph.originX = graph.originX;
			splash.graph.originY = graph.originY;
			splash.graph.zoom = graph.zoom;
		getContentPane().add( splash );
	}
}