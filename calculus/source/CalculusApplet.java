import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.border.*;

public class CalculusApplet extends JApplet implements ActionListener,ItemListener,ChangeListener,DocumentListener{


	char delta = (char)948;
	char epsilon = (char)949;
	char leq = (char)8804;
	char geq = (char)8805;
	
	Graph graph;
	
	SplashPanel splash;

	JTextField f;
	JTextField g;
	JTextField j;

	DSpinner a;
	DSpinner b;
	DSpinner c;
	DSpinner d;

	JSpinner n;
	JSpinner m;

	String F;
	String G;
	String J;
	String updateMessage = "Calculating...";
	
	double A;
	double B;
	double C;
	double D;
	
	int N;
	int M;

	JLabel stat;
	JLabel stat2;
	JLabel stat3;
	static JLabel statusbar;

	JComboBox choice;
	JComboBox choice2;		// used by Secant lines to choose between "b=" or "h=".
	JComboBox colorchoice;

	JButton zoomin;
	JButton zoomout;
	JButton animate;
	
	JSlider zoom;			// a possible upgrade to zoomin/zoomout buttons
	JSlider theta;			// used to control size of washers/disks

	JPanel north;
	JPanel center;
	JPanel south;
	JPanel southpanel;

	Container contentpane;
	GridBagLayout gridbaglayout;
	GridBagConstraints constraints;
	
	JFrame frame;
	
	public void setup( String title ){	
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		
		frame = new JFrame();
			frame.setVisible(false);
			frame.setSize( screen.width/2, screen.height/2);
			frame.setLocation(0,0);
			frame.setTitle( title );
			
		contentpane = frame.getContentPane();
		contentpane.setLayout( new BorderLayout() );
		gridbaglayout = new GridBagLayout();
		constraints = new GridBagConstraints();
		
		f = new JTextField( "", 1 );
		g = new JTextField( "", 1 );
		j = new JTextField( "", 1 );

		a = new DSpinner( 5, -999999.9999999999, 999999.9999999999, 0.001 );
		b = new DSpinner( 5, -999999.9999999999, 999999.9999999999, 0.001 );
		c = new DSpinner( 5, -999999.9999999999, 999999.9999999999, 0.001 );
		d = new DSpinner( 5, -999999.9999999999, 999999.9999999999, 0.001 );
		n = new JSpinner( new SpinnerNumberModel(5,0,10000,1) );
		m = new JSpinner( new SpinnerNumberModel(5,0,10000,1) );
		
		graph = new Graph();

		String s;
		if ( getParameter("f") != null ){
			F = getParameter("f");
			f = new JTextField( F );
			f.getDocument().addDocumentListener(this);
		}
		if ( getParameter("g") != null ){
			G = getParameter("g");
			g = new JTextField( G );
			g.getDocument().addDocumentListener(this);
		}
		if ( getParameter("j") != null ){
			J = getParameter("j");
			j = new JTextField( J );
			j.getDocument().addDocumentListener(this);
		}
		if ( (s = getParameter("a")) != null ){
			a.F.parseExpression( s );
			A = a.F.getValue();
			a.addChangeListener( this );
			a.setValue( A );
			a.setText( s );
		}
		if ( (s = getParameter("b")) != null ){
			b.F.parseExpression( s );
			B = b.F.getValue();
			b.addChangeListener( this );
			b.setValue( B );
			b.setText( s );
		}
		if ( (s = getParameter("c")) != null ){
			c.F.parseExpression( s );
			C = c.F.getValue();
			c.addChangeListener( this );
			c.setValue( C );
			c.setText( s );
		}
		if ( (s = getParameter("d")) != null ){
			d.F.parseExpression( s );
			D = d.F.getValue();
			d.addChangeListener( this );
			d.setValue( D );
			d.setText( s );
		}
		if ( (s = getParameter("n")) != null ){
			N = Integer.parseInt( s );
			n.setValue( new Integer(s) );
			n.addChangeListener( this );
		}
		if ( (s = getParameter("m")) != null ){
			M = Integer.parseInt( s );
			m.setValue( new Integer(s) );
			m.addChangeListener( this );
		}
				
		if ( getParameter("zoom") != null ){ 
			graph.zoom += Integer.parseInt( getParameter("zoom") );
			a.setStepSize( graph.units[graph.zoom]/10.0 );
			b.setStepSize( graph.units[graph.zoom]/10.0 );
			c.setStepSize( graph.units[graph.zoom]/10.0 );
			d.setStepSize( graph.units[graph.zoom]/10.0 );
		}
		
		north = new JPanel();
		north.setBorder( BorderFactory.createEtchedBorder(EtchedBorder.LOWERED) );		
						
		south = new JPanel( gridbaglayout );
			constraints.fill = GridBagConstraints.HORIZONTAL;
			constraints.weightx = 0.0;

			stat = new JLabel( "", JLabel.LEFT );
			stat.setFont( new Font("Helvetica",Font.PLAIN,18) );
			stat.setForeground( Color.red );
			gridbaglayout.setConstraints( stat, constraints );
			south.add( stat );			

			stat2 = new JLabel( "", JLabel.LEFT );
			stat2.setFont( new Font("Helvetica",Font.PLAIN,18) );
			stat2.setForeground( Color.red );
			gridbaglayout.setConstraints( stat2, constraints );
			south.add( stat2 );			
			stat2.setVisible( false );

			constraints.weightx = 1.0;
			stat3 = new JLabel( "", JLabel.LEFT );
			stat3.setFont( new Font("Helvetica",Font.PLAIN,18) );
			stat3.setForeground( Color.red );
			gridbaglayout.setConstraints( stat3, constraints );
			south.add( stat3 );			
			//stat3.setVisible( false );
		
			constraints.fill = GridBagConstraints.NONE;
			constraints.weightx = 0.0;
			
			choice = new JComboBox();
			choice.addItemListener( this );
			gridbaglayout.setConstraints( choice, constraints );
			south.add( choice );
			
			choice2 = new JComboBox();
			choice2.addItemListener( this );
			
			colorchoice = new ColorComboBox();
			colorchoice.addItemListener( this );
			gridbaglayout.setConstraints( colorchoice, constraints );
			south.add( colorchoice );
			colorchoice.setVisible( false );

			theta = new JSlider( JSlider.HORIZONTAL, 0, 100, 100 );
			theta.addChangeListener( this );
			gridbaglayout.setConstraints( theta, constraints );
			south.add( theta );
			theta.setVisible( false );

			zoomin = new JButton("Zoom In");
			zoomin.addActionListener( this );
			gridbaglayout.setConstraints( zoomin, constraints );
			south.add( zoomin );
			
			zoomout = new JButton("Zoom Out");
			zoomout.addActionListener( this );
			gridbaglayout.setConstraints( zoomout, constraints );
			south.add( zoomout );
			
			zoom = new JSlider( JSlider.HORIZONTAL, 0, graph.units.length-1, 1 );
			zoom.setToolTipText( "Zoom" );
			zoom.addChangeListener( this );
			gridbaglayout.setConstraints( zoom, constraints );
			//south.add( zoom );

		southpanel = new JPanel( new BorderLayout() );
			southpanel.add( "Center", south );
			statusbar = new JLabel("");
				statusbar.setPreferredSize( new Dimension(15,15) );
			southpanel.add( "South", statusbar );
	
		contentpane.add( "South", southpanel );
	}
	
	
	public void showFrame(){
		frame.setVisible( true );
	}


	public void start(){
	}


	public void stop(){
		//frame.setVisible( false );
	}
	

	/* overwritten by: (WHY AM I OVERWRITING THESE AND NOT OTHERS?)
	 ChainRule, LHospital, ParametricApplet, PolarApplet, SecantLines, SpaceCurves
	 TangentCircles, TaylorSeries
	*/
	// should only be called for major updates in graph (like functions have been change)
	// and background needs to be redrawn (Is that true??!!)
	// actually, it appears that updateGraphs is always called instead of repaint()
	// the only procedure that calls repaint is updateGraphs
	public void updateGraphs( Graph g ){
		updateGraphs( g, updateMessage );
	}

	public void updateGraphs( Graph g, String message ){
		if ( stat != null ) stat.setText( " " + message );
		g.repaint();
	}
	
	
	public void setZoom( int zoom ){
		graph.setZoom( zoom );
		a.setStepSize( graph.units[graph.zoom]/10.0 );
		b.setStepSize( graph.units[graph.zoom]/10.0 );
		c.setStepSize( graph.units[graph.zoom]/10.0 );
		d.setStepSize( graph.units[graph.zoom]/10.0 );
	}
	

	public void setStat( String label, double val ){
		setStat( label, val, Color.black );
	}


	public void setStat( String label, double val, Color color ){
		stat.setForeground( color );
		stat.setText( " " );
		String out = formatDouble( val );
		if ( out != null ) stat.setText( " " + label + out );
	}
/*
	public void setStat( String label, double val, Color color ){
		stat.setForeground( color );
		String out = "" + val;
		if ( out.indexOf("E") > 0 ){
			stat.setText( " " + label +  out );
		} else {
			try {
				double s = 0.0;
				int i = out.indexOf( "." );
				if ( i > -1 ){
					out += "000000000000000";
					if ( Integer.parseInt( out.substring(i+16,i+17) ) > 5 ) s = 0.000000000000001;
					out = out.substring(0,i+16);
				}
				stat.setText( " " + label +  (Double.parseDouble(out)+s) );
			} catch ( NumberFormatException nfe ){
				stat.setText( " " );
			}
		}
	}
*/	
	
	public void setStat2( String label, double val ){
		setStat2( label, val, Color.black );
	}
	
	
	public void setStat2( String label, double val, Color color ){
		stat2.setForeground( color );
		stat2.setText( " " );
		String out = formatDouble( val );
		if ( out != null ) stat2.setText( " " + label + out );
	}

	
	public void setStat3( String label, double val ){
		setStat3( label, val, Color.black );
	}
	
	
	public void setStat3( String label, double val, Color color ){
		stat3.setForeground( color );
		stat3.setText( " " );
		String out = formatDouble( val );
		if ( out != null ) stat3.setText( " " + label + out );
	}
	
	
	public String formatDouble( double val ){
		String out = "" + val;
		if ( out.indexOf("E") > 0 ){
			return out;
		} else {
			try {
				double s = 0.0;
				int i = out.indexOf( "." );
				if ( i > -1 ){
					out += "000000000000000";
					if ( Integer.parseInt( out.substring(i+16,i+17) ) > 5 ) s = 0.000000000000001;
					out = out.substring(0,i+16);
				}
				return "" + (Double.parseDouble(out)+s);
			} catch ( NumberFormatException nfe ){
				return null;
			}
		}
	}

	
	/* overwritten by: 
	 ChainRule, LHospital, ParabolicMotion, ParametricApplet, PolarApplet, Sequences, SpaceCurves, Substitution
	 */
	public void actionPerformed( ActionEvent ae ){
		Object obj = ae.getSource();
		if ( obj == zoomin ){
			graph.zoomIn();
			if ( graph.zoom == 0) zoomin.setEnabled( false );
			zoomout.setEnabled( true );
			a.setStepSize( graph.units[graph.zoom]/10.0 );
			b.setStepSize( graph.units[graph.zoom]/10.0 );
			c.setStepSize( graph.units[graph.zoom]/10.0 );
			d.setStepSize( graph.units[graph.zoom]/10.0 );
		} else if ( obj == zoomout ){
			graph.zoomOut();
			if ( graph.zoom == graph.units.length-1 ) zoomout.setEnabled( false );
			zoomin.setEnabled( true );
			a.setStepSize( graph.units[graph.zoom]/10.0 );
			b.setStepSize( graph.units[graph.zoom]/10.0 );
			c.setStepSize( graph.units[graph.zoom]/10.0 );
			d.setStepSize( graph.units[graph.zoom]/10.0 );
		}
		updateGraphs( graph );
	}


	/* overwritten by:
	 Area, ParabolicMotion, SecantLines, Sequences, Substitution, Surfaces
	 */
	public void itemStateChanged( ItemEvent e ){
		Object obj = e.getSource();
		if ( obj == choice ){
			updateGraphs( graph );
		}
	}


	/* overwritten by:
	 Antiderivative, CauchyMeanValue, ChainRule, EulersMethod, LeastSquares,
	 LHospital, LimitSequences, ParabolicMotion, ParametricApplet, PolarApplet, 
	 Sequences, SlopeFields, Substitution, TangentCircles, TaylorSeries
	 */
	public void stateChanged( ChangeEvent ce ){
		try {
			stateChangedDefault( ce );
		} catch ( NumberFormatException nfe ){
		}
	}


	public void stateChangedDefault( ChangeEvent ce ){
		Object obj = ce.getSource();
		//SpinnerNumberModel snm;
		if ( obj == a ){
			graph.a = a.getValue();
			graph.newStat = true;
			updateGraphs( graph );
		} else if ( obj == b ){
			graph.b = b.getValue();
			graph.newStat = true;
			updateGraphs( graph );
		} else if ( obj == c ){
			graph.c = c.getValue();
			graph.newStat = true;
			updateGraphs( graph );
		} else if ( obj == d ){
			graph.d = d.getValue();
			graph.newStat = true;
			updateGraphs( graph );
		} else if ( obj == m ){
			graph.m = ((Integer)m.getValue()).intValue();
			graph.newStat = true;
			stat.setText( "Calculating..." );
			updateGraphs( graph );
		} else if ( obj == n ){
			graph.n = ((Integer)n.getValue()).intValue();
			graph.newStat = true;
			stat.setText( "Calculating..." );
			updateGraphs( graph );
		} else if ( obj == zoom ){
			setZoom( zoom.getValue() );
		} else if ( obj == theta ){
			graph.time = theta.getValue();
			graph.newStat = true;
			updateGraphs( graph );
		}
	}

	// DocumentListener
    public void insertUpdate( DocumentEvent ev ) {
		changedUpdate( ev );
    }

	
    public void removeUpdate( DocumentEvent ev ){
		changedUpdate( ev );
    }
    
	
	/* overwritten by:
	 Sequences, Substitution, DerivativeAlt
	 */
    public void changedUpdate( DocumentEvent ev ){
		try {
			changedUpdateDefault( ev );
		} catch ( NumberFormatException nfe ){
		}
	}
	

	public void changedUpdateDefault( DocumentEvent ev ){
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
			updateGraphs( graph );
		} else if ( ev.getDocument() == g.getDocument() ){
			if ( g.getText() == "" ){
				graph.G.parseExpression( "0" );
			} else {
				graph.G.parseExpression( g.getText() );
			}

			if ( graph.G.hasError() ){
				graph.G.parseExpression( "0" );
				g.setForeground( Color.red );
			} else {
				g.setForeground( Color.black );
			}
			graph.newStat = true;
			graph.newBackground = true;
			updateGraphs( graph );
		} else if ( ev.getDocument() == j.getDocument() ){
			if ( j.getText() == "" ){
				graph.J.parseExpression( "0" );
			} else {
				graph.J.parseExpression( j.getText() );
			}

			if ( graph.J.hasError() ){
				graph.J.parseExpression( "0" );
				j.setForeground( Color.red );
			} else {
				j.setForeground( Color.black );
			}
			graph.newStat = true;
			graph.newBackground = true;
			updateGraphs( graph );
		}
	}
}