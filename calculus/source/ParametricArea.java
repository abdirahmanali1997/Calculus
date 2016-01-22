import java.awt.*;
import javax.swing.*;

public class ParametricArea extends ParametricApplet{

	public void init(){
		setup( "The Area Bounded by a Parametric Curve" );

		north.setLayout( new GridLayout(2,1) );
			// first row
			JPanel row = new JPanel( gridbaglayout );			
				constraints.fill = GridBagConstraints.NONE;
				constraints.weightx = 0.0;
				JLabel label = new JLabel(" x(t)=");
				gridbaglayout.setConstraints( label, constraints );
				row.add( label );			

				constraints.fill = GridBagConstraints.HORIZONTAL;
				constraints.weightx = 1.0;
				gridbaglayout.setConstraints( f, constraints );
				row.add( f );

				constraints.fill = GridBagConstraints.NONE;
				constraints.weightx = 0.0;
				label = new JLabel(" y(t)=");
				gridbaglayout.setConstraints( label, constraints );
				row.add( label );			

				constraints.fill = GridBagConstraints.HORIZONTAL;
				constraints.weightx = 1.0;
				gridbaglayout.setConstraints( g, constraints );
				row.add( g );

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
				constraints.weightx = 0.0;
				constraints.fill = GridBagConstraints.NONE;
				label = new JLabel(" a=");
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
			north.add( row );
		contentpane.add( "North", north );

		graph = new ParametricAreaGraph( this, F, G, A, B, N );
		xoftee = new Graph();
			xoftee.xlabel = "t";
			xoftee.ylabel = "x(t)";
			xoftee.variable = "t";
			xoftee.applet = this;
			xoftee.a = graph.a;
			xoftee.b = graph.b;
			xoftee.colorA = Color.red;
			xoftee.colorB = Color.red;
			xoftee.endline = new BasicStroke( 2.0f );
			xoftee.F.parseExpression( F );
		yoftee = new Graph();
			yoftee.xlabel = "t";
			yoftee.ylabel = "y(t)";
			yoftee.variable = "t";
			yoftee.applet = this;
			yoftee.a = graph.a;
			yoftee.b = graph.b;
			yoftee.colorA = Color.orange;
			yoftee.colorB = Color.orange;
			yoftee.endline = new BasicStroke( 2.0f );
			yoftee.F.parseExpression( G );
			
		JPanel top = new JPanel( new GridLayout(1,2,3,0) );
			top.add( xoftee );
			top.add( yoftee );
		JSplitPane center = new JSplitPane( JSplitPane.VERTICAL_SPLIT, true, top, graph );
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

		splash = new SplashPanel( new ParametricAreaGraph(this,F,G,A,B,N ) );
			splash.graph.originX = graph.originX;
			splash.graph.originY = graph.originY;
			splash.graph.zoom = graph.zoom;
		getContentPane().add( splash );
	}
}