import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import org.nfunk.jep.*;

class DSpinner extends JTextField implements KeyListener, DocumentListener{

	private double max;
	private double min;
	private double step;
	private double value;
	
	JEP F;
	
	ChangeListener changelistener;

	public DSpinner( double value, double min, double max, double step ){
		super( value + "" );
		setColumns( 1 );
		
		this.max = max;
		this.min = min;
		this.step = step;
		this.value = value;
		
		F = new JEP();
		F.addStandardFunctions();
		F.addStandardConstants();
		F.setImplicitMul( true );
		F.setAllowUndeclared(true);
		F.setAllowAssignment(true);
		F.addVariable( "x", 0 );

		addKeyListener( this );
        getDocument().addDocumentListener(this);
		//addTextListener( this );
	}
	
	public void addChangeListener( ChangeListener changelistener ){
		this.changelistener = changelistener;
	}

	public void fireStateChange(){
		if ( changelistener != null ) changelistener.stateChanged( new ChangeEvent( this ) );
	}
	
	public double getValue(){
		return value;
	}
	
	public void nextValue(){
		setValue( value + step );
	}

	public void setMaximum( double max ){
		this.max = max;
	}
	
	public void setMinimum( double min ){
		this.min = min;
	}
	
	public void setStepSize( double step ){
		this.step = step;
	}
	
	public void setText( double value ){
		setText( value + "" );
	}
	
	public double round( double val ){
		String out = "" + val;
		int i = out.indexOf("E");
		if ( i > 0 ){
			return val;
		} else {
			try {
				double s = 0.0;
				i = out.indexOf( "." );
				if ( i > -1 ){
					out += "0000000000";
					int digit = Integer.parseInt( out.substring(i+11,i+12) );
					if ( digit > 5 ) s = 0.00000000011;
					if ( val < 0 ) s *= -1;
					out = out.substring(0,i+11);
					out = "" + (Double.parseDouble(out) + s) + "0000000000";
					out = out.substring(0,i+11);
				}
				return (Double.parseDouble(out));
			} catch ( NumberFormatException nfe ){
				return val;
			}
		}
	}
	
	public void setValue( double value ){
		this.value = round(value);
		if ( value > max ) this.value = max;
		else if ( value < min ) this.value = min;
		setText( this.value );
		fireStateChange();
 	}
	
	public void setValue( Double value ){
		setValue( value.doubleValue() );
	}
	
	public void previousValue(){
		setValue( value - step );
	}

	// DocumentListener
    public void insertUpdate( DocumentEvent ev ) {
		changedUpdate( ev );
    }
    
    public void removeUpdate( DocumentEvent ev ) {
		changedUpdate( ev );
    }
    
    public void changedUpdate( DocumentEvent ev ) {
		try{
			F.parseExpression( getText() );

			if ( getText() == "" || F.hasError() ){
				setForeground( Color.red );
			} else {
				setForeground( Color.black );
				this.value = F.getValue();
				if ( this.value > max ){
					this.value = max;
					setText( max );
				} else if ( this.value < min ){
					this.value = min;
					setText( min );
				}
				fireStateChange();
			}
		} catch ( NumberFormatException nfe ){
			setForeground( Color.red );
		} catch ( Exception ex ) {
		}
    }

	// KeyListener
    public void keyTyped( KeyEvent ke ){
    }

    public void keyPressed( KeyEvent ke ){
		int code = ke.getKeyCode();
		char key = ke.getKeyChar();
		if ( code == 37 ){  // left arrow
		} else if ( code == 38 ){  // up arrow
			nextValue();
		} else if ( code == 39 ){  // right arrow
		} else if ( code == 40 ){  // down arrow
			previousValue();
		}
    }

    public void keyReleased( KeyEvent ke ){
    }
}