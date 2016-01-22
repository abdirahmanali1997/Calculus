import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

public class Substitution extends CalculusApplet{

	Graph right = new Graph();

	public void init(){
		setup( "Methods of Integration: Substitution" );

		north.setLayout( new GridLayout(2,2,0,2) );
			// first row
			JPanel row = new JPanel( gridbaglayout );			
				constraints.gridwidth = 1;
				constraints.fill = GridBagConstraints.NONE;
				constraints.weightx = 0.0;
				JLabel label = new JLabel(" f(g(x))g'(x)=");
				gridbaglayout.setConstraints( label, constraints );
				row.add( label );			

				constraints.weightx = 1.0;
				constraints.fill = GridBagConstraints.HORIZONTAL;
				gridbaglayout.setConstraints( f, constraints );
				row.add( f );
			north.add( row );

			row = new JPanel( gridbaglayout );
				constraints.weightx = 0.0;
				constraints.fill = GridBagConstraints.NONE;
				label = new JLabel(" f(u)=");
				gridbaglayout.setConstraints( label, constraints );
				row.add( label );

				constraints.weightx = 1.0;
				constraints.fill = GridBagConstraints.HORIZONTAL;
				gridbaglayout.setConstraints( g, constraints );
				row.add( g );
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

			row = new JPanel( gridbaglayout );
				constraints.weightx = 0.0;
				constraints.fill = GridBagConstraints.NONE;
				label = new JLabel(" c=");
				label.setForeground( Color.red );
				gridbaglayout.setConstraints( label, constraints );
				row.add( label );			

				constraints.weightx = 1.0;
				constraints.fill = GridBagConstraints.HORIZONTAL;
				gridbaglayout.setConstraints( c, constraints );
				row.add( c );

				constraints.weightx = 0.0;
				constraints.fill = GridBagConstraints.NONE;
				label = new JLabel(" d=");
				label.setForeground( Color.magenta );
				gridbaglayout.setConstraints( label, constraints );
				row.add( label );

				constraints.weightx = 1.0;
				constraints.fill = GridBagConstraints.HORIZONTAL;
				gridbaglayout.setConstraints( d, constraints );
				row.add( d );
			north.add( row );
		contentpane.add( "North", north );
		
		graph = new SubstitutionGraph( this, F, A, B, 1 );
			graph.xlabel = "x";
			graph.ylabel = "f(g(x))g'(x)";
		right = new SubstitutionGraph( this, G, C, D, 2 );
			right.xlabel = "u";
			right.ylabel = "f(u)";
			
		JPanel center = new JPanel( new GridLayout(1,2,0,3) );
			center.add( graph );
			center.add( right );

		contentpane.add( "Center", center );
		if ( getParameter("originX") != null ){ 
			graph.originX = Double.parseDouble( getParameter("originX") );
		}
		if ( getParameter("originY") != null ){ 
			graph.originY = Double.parseDouble( getParameter("originY") );
		}
		if ( getParameter("zoom") != null ){ 
			graph.zoom += Integer.parseInt( getParameter("zoom") );
			right.zoom = graph.zoom;
		}
		
		choice.setVisible( false );
				
		graph.repaint();
		splash = new SplashPanel( new SubstitutionGraph(this,F,A,B,1) );
			splash.graph.originX = graph.originX;
			splash.graph.originY = graph.originY;
			splash.graph.zoom = graph.zoom;
		getContentPane().add( splash );
	}


	public void actionPerformed( ActionEvent ae ){
		Object obj = ae.getSource();
		if ( obj == zoomin ){
			graph.zoomIn();
			right.zoomIn();
			if ( graph.zoom == 0) zoomin.setEnabled( false );
			zoomout.setEnabled( true );
			a.setStepSize( graph.units[graph.zoom]/10.0 );
			b.setStepSize( graph.units[graph.zoom]/10.0 );
			c.setStepSize( graph.units[graph.zoom]/10.0 );
			d.setStepSize( graph.units[graph.zoom]/10.0 );
		} else if ( obj == zoomout ){
			graph.zoomOut();
			right.zoomOut();
			if ( graph.zoom == graph.units.length-1 ) zoomout.setEnabled( false );
			zoomin.setEnabled( true );
			a.setStepSize( graph.units[graph.zoom]/10.0 );
			b.setStepSize( graph.units[graph.zoom]/10.0 );
			c.setStepSize( graph.units[graph.zoom]/10.0 );
			d.setStepSize( graph.units[graph.zoom]/10.0 );
		}
		updateGraphs( graph );
		updateGraphs( right );
	}


	public void itemStateChanged( ItemEvent e ){
		Object obj = e.getSource();
		if ( obj == choice ){
			graph.newStat = true;
			graph.repaint();
		}
	}

	public void stateChanged( ChangeEvent ce ) {
		try {
			Object obj = ce.getSource();
			if ( obj == a ){
				graph.a = a.getValue();
				graph.newStat = true;
				graph.repaint();
			} else if ( obj == b ){
				graph.b = b.getValue();
				graph.newStat = true;
				graph.repaint();
			} else if ( obj == c ){
				right.a = c.getValue();
				right.newStat = true;
				right.repaint();
			} else if ( obj == d ){
				right.b = d.getValue();
				right.newStat = true;
				right.repaint();
			} else {
				stateChangedDefault( ce );
			}
		} catch ( NumberFormatException nfe ){
		}
	}


	public void changedUpdate( DocumentEvent ev ){
		try {
			if ( ev.getDocument() == f.getDocument() ){
				if ( f.getText() == "" ){
					graph.F.parseExpression( "0" );
				} else {
					graph.F.parseExpression( f.getText() );
				}

				if ( graph.F.hasError() ){
					graph.F.parseExpression( "0" );
					f.setForeground( Color.red );
				} else {
					f.setForeground( Color.black );
				}
				graph.newStat = true;
				graph.newBackground = true;
				updateGraphs( graph );
			} else 	if ( ev.getDocument() == g.getDocument() ){
				if ( g.getText() == "" ){
					right.F.parseExpression( "0" );
				} else {
					right.F.parseExpression( g.getText() );
				}

				if ( right.F.hasError() ){
					right.F.parseExpression( "0" );
					g.setForeground( Color.red );
				} else {
					g.setForeground( Color.black );
				}
				right.newStat = true;
				right.newBackground = true;
				updateGraphs( right );
			} else {
				changedUpdateDefault( ev );
			}
		} catch ( NumberFormatException nfe ){
		}
	}
}