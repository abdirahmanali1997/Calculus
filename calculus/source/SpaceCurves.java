import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class SpaceCurves extends ParametricApplet{

	public void init(){
		setup( "Space Curves" );

		north.setLayout( new GridLayout(2,1,0,2) );
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

				constraints.fill = GridBagConstraints.NONE;
				constraints.weightx = 0.0;
				label = new JLabel(" z(t)=");
				gridbaglayout.setConstraints( label, constraints );
				row.add( label );			

				constraints.fill = GridBagConstraints.HORIZONTAL;
				constraints.weightx = 1.0;
				gridbaglayout.setConstraints( j, constraints );
				row.add( j );
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
		
		graph = new SpaceCurvesGraph( this, F, G, J, A, B );
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
		zoftee = new Graph();
			zoftee.xlabel = "t";
			zoftee.ylabel = "z(t)";
			zoftee.variable = "t";
			zoftee.applet = this;
			zoftee.a = graph.a;
			zoftee.b = graph.b;
			zoftee.colorA = Color.magenta;
			zoftee.colorB = Color.magenta;
			zoftee.endline = new BasicStroke( 2.0f );
			zoftee.F.parseExpression( J );

		JPanel top = new JPanel( new GridLayout(1,3,3,0) );
			top.add( xoftee );
			top.add( yoftee );
			top.add( zoftee );
		JSplitPane center = new JSplitPane( JSplitPane.VERTICAL_SPLIT, true, top, graph );
			center.setDividerLocation( 100 );
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
		choice.addItem("Show Unit Tangent Vector");
		choice.addItem("Show Unit Normal Vector");
		choice.addItem("Show Both");
		//graph.repaint();

		//southpanel.add( "West", animate = new JButton("Animate") );
		//	animate.addActionListener( this );

		splash = new SplashPanel( new SpaceCurvesGraph(this,F,G,J,A,B ) );
			splash.graph.originX = graph.originX;
			splash.graph.originY = graph.originY;
			splash.graph.zoom = graph.zoom;
		getContentPane().add( splash );
	}

	public void actionPerformed( ActionEvent ae ){
		Object obj = ae.getSource();
		if ( obj == zoomin ){
			graph.zoomIn();
			xoftee.zoomIn();
			yoftee.zoomIn();
			zoftee.zoomIn();
			if ( graph.zoom == 0) zoomin.setEnabled( false );
			zoomout.setEnabled( true );
		} else if ( obj == zoomout ){
			graph.zoomOut();
			xoftee.zoomOut();
			yoftee.zoomOut();
			zoftee.zoomOut();
			if ( graph.zoom == graph.units.length-1 ) zoomout.setEnabled( false );
			zoomin.setEnabled( true );
		} else if ( obj == animate ){
			((SpaceCurvesGraph)graph).start();
			animate.setEnabled( false );
		}
		updateGraphs( graph );
	}

	public void updateGraphs( Graph g ){
		if ( g == graph ){
			xoftee.a = graph.a;
			xoftee.b = graph.b;
			xoftee.F = graph.F;
			xoftee.newBackground = graph.newBackground;

			yoftee.a = graph.a;
			yoftee.b = graph.b;
			yoftee.F = graph.G;
			yoftee.newBackground = graph.newBackground;

			zoftee.a = graph.a;
			zoftee.b = graph.b;
			zoftee.F = graph.J;
			zoftee.newBackground = graph.newBackground;

			xoftee.repaint();
			yoftee.repaint();
			zoftee.repaint();
			graph.repaint();
		} else if ( g == xoftee ){
			graph.a = xoftee.a;
			graph.b = xoftee.b;
			graph.repaint();

			yoftee.a = graph.a;
			yoftee.b = graph.b;
			yoftee.repaint();

			zoftee.a = graph.a;
			zoftee.b = graph.b;
			zoftee.repaint();
		} else if ( g == yoftee ){
			graph.a = yoftee.a;
			graph.b = yoftee.b;
			graph.repaint();

			xoftee.a = graph.a;
			xoftee.b = graph.b;
			xoftee.repaint();

			zoftee.a = graph.a;
			zoftee.b = graph.b;
			zoftee.repaint();
		} else if ( g == zoftee ){
			graph.a = zoftee.a;
			graph.b = zoftee.b;
			graph.repaint();

			xoftee.a = graph.a;
			xoftee.b = graph.b;
			xoftee.repaint();

			yoftee.a = graph.a;
			yoftee.b = graph.b;
			yoftee.repaint();
		}
	}
}