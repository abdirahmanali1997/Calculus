import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

public class ChainRule extends CalculusApplet{

	Graph gofex = new Graph();
	Graph fofex = new Graph();

	public void init(){
		setup( "The Chain Rule" );

		north.setLayout( new GridLayout(1,1) );
			JPanel row = new JPanel( gridbaglayout );
				constraints.fill = GridBagConstraints.NONE;
				constraints.weightx = 0.0;
				JLabel label = new JLabel(" g(x)=");
				gridbaglayout.setConstraints( label, constraints );
				row.add( label );			

				constraints.fill = GridBagConstraints.HORIZONTAL;
				constraints.weightx = 1.0;
				gridbaglayout.setConstraints( g, constraints );
				row.add( g );

				constraints.fill = GridBagConstraints.NONE;
				constraints.weightx = 0.0;
				label = new JLabel(" f(x)=");
				gridbaglayout.setConstraints( label, constraints );
				row.add( label );			

				constraints.fill = GridBagConstraints.HORIZONTAL;
				constraints.weightx = 1.0;
				gridbaglayout.setConstraints( f, constraints );
				row.add( f );

				constraints.fill = GridBagConstraints.NONE;
				constraints.weightx = 0.0;
				label = new JLabel(" a=");
				label.setForeground( Color.red );
				gridbaglayout.setConstraints( label, constraints );
				row.add( label );			

				constraints.fill = GridBagConstraints.HORIZONTAL;
				constraints.weightx = 1.0;
				gridbaglayout.setConstraints( a, constraints );
				row.add( a );
				
			north.add( row );
		contentpane.add( "North", north );
		
		graph = new ChainRuleGraph( this, F, G, A, 3 );
			graph.xlabel = "x";
			graph.ylabel = "f(g(x))";
		gofex = new ChainRuleGraph( this, F, G, A, 1 );
			gofex.xlabel = "x";
			gofex.ylabel = "g(x)";
		fofex = new ChainRuleGraph( this, F, G, A, 2 );
			fofex.xlabel = "x";
			fofex.ylabel = "f(x)";
			
		JPanel top = new JPanel( new GridLayout(1,2,3,0) );
			top.add( gofex );
			top.add( fofex );
		JSplitPane center = new JSplitPane( JSplitPane.VERTICAL_SPLIT, true, top, graph );
			center.setDividerLocation( 150 );
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
		//graph.repaint();

		splash = new SplashPanel( new ChainRuleGraph( this, F, G, A, 1 ) );
			splash.graph.originX = graph.a;
			graph.G.addVariable( "x", graph.a );
			splash.graph.originY = graph.G.getValue();
			splash.graph.zoom = graph.zoom;
		getContentPane().add( splash );
	}


	public void actionPerformed( ActionEvent ae ){
		Object obj = ae.getSource();
		if ( obj == zoomin ){
			graph.zoomIn();
			gofex.zoomIn();
			fofex.zoomIn();
			if ( graph.zoom == 0) zoomin.setEnabled( false );
			zoomout.setEnabled( true );
			a.setStepSize( graph.units[graph.zoom]/10.0 );
		} else if ( obj == zoomout ){
			graph.zoomOut();
			gofex.zoomOut();
			fofex.zoomOut();
			if ( graph.zoom == graph.units.length-1 ) zoomout.setEnabled( false );
			zoomin.setEnabled( true );
			a.setStepSize( graph.units[graph.zoom]/10.0 );
		}
		updateGraphs( graph );
	}


	public void updateGraphs( Graph g ){
		if ( g == graph ){
			gofex.a = graph.a;
			gofex.F = graph.F;
			gofex.G = graph.G;
			gofex.originX = graph.a;
			graph.G.addVariable( "x", graph.a );
			gofex.originY = graph.G.getValue();
			gofex.newBackground = true;

			fofex.a = graph.a;
			fofex.F = graph.F;
			fofex.G = graph.G;
			fofex.originX = graph.G.getValue();
			graph.F.addVariable( "x", graph.G.getValue() );
			fofex.originY = graph.F.getValue();
			fofex.newBackground = true;

			gofex.repaint();
			fofex.repaint();
			graph.repaint();
		}
	}
}