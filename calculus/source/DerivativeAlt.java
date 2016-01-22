import java.awt.*;
import javax.swing.*;
import javax.swing.event.*;

public class DerivativeAlt extends CalculusApplet{
	
	public void init(){
		setup( "Graphing the Derivative of a Function" );
				
		north.setLayout( new GridLayout(2,1) );
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

				constraints.fill = GridBagConstraints.NONE;
				constraints.weightx = 0.0;
				label = new JLabel(" f'(x)=");
				gridbaglayout.setConstraints( label, constraints );
				row.add( label );			

				constraints.fill = GridBagConstraints.HORIZONTAL;
				constraints.weightx = 1.0;
				gridbaglayout.setConstraints( g, constraints );
				row.add( g );

			north.add( row );

        // second row
        row = new JPanel( gridbaglayout );
            constraints.gridwidth = 1;
            constraints.weightx = 0.0;
            constraints.fill = GridBagConstraints.NONE;
            label = new JLabel(" a=");
            label.setForeground( Color.red );
            gridbaglayout.setConstraints( label, constraints );
            row.add( label );			
        
            constraints.weightx = 1.0;
            constraints.fill = GridBagConstraints.HORIZONTAL;
            gridbaglayout.setConstraints( a, constraints );
            row.add( a );
        north.add( row );

        contentpane.add( "North", north );
		
		contentpane.add( "Center", graph = new DerivativeAltGraph( this, F, G, A ) );
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

		splash = new SplashPanel( new DerivativeAltGraph(this,F,G,A ) );
			splash.graph.originX = graph.originX;
			splash.graph.originY = graph.originY;
			splash.graph.zoom = graph.zoom;
		getContentPane().add( splash );
	}
    
    
    public void changedUpdate( DocumentEvent ev ){
		try {
            if ( ev.getDocument() == f.getDocument() ){
                if ( f.getText() == "" ){
                    graph.F.parseExpression( "0" );
                } else {
                    graph.F.parseExpression( f.getText() );
                }
                graph.dList = new DerivativeList( graph.F.getTopNode() ); 
                
                if ( graph.F.hasError() ){
                    graph.F.parseExpression( "0" );
                    f.setForeground( Color.red );
                } else {
                    f.setForeground( Color.black );
                }
                graph.newStat = true;
                graph.newBackground = true;
                graph.b = graph.a;
                graph.c = graph.a;
                updateGraphs( graph );
            } else {
                changedUpdateDefault( ev );
            }
		} catch ( NumberFormatException nfe ){
		}
	}            
}