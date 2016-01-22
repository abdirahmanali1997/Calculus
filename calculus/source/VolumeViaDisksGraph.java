import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.*;

public class VolumeViaDisksGraph extends Graph3D{

	public VolumeViaDisksGraph( CalculusApplet applet, String f, double a, double b, double c, int n ){
		super();

		this.applet = applet;
		this.a = a;						// lower limit of integration
		this.b = b;						// upper limit of integration
		this.c = c;						// axis of rotation
		this.n = Math.max( n,0 );		// number of cross-sections
		
		time = 100;

		F.parseExpression( f );			// boundary curve
	}
	
	
	public void draw( Graphics2D g ){
		delta = Math.abs(b-a)/n;
		if ( newStat ) stat = 0.0;

		if ( showGrid ) drawGridLines( g );
		if ( !shift ) drawAxes( g );
		if ( !shift ) drawAxisOfRotation( g );
		if ( !shift ) drawFunction( g );

		// eye focused on middle of panel
		double A = Math.min(a,b) + delta/2.0;
		double B;
		while ( A < Math.max(a,b) && A < Z*(xaxis[2])+originX ){
			F.addVariable( "x", A );
			B = Math.abs(F.getValue()-c);
			if ( newStat ) stat += B*B;
			if ( time == 100 ){
				drawDisk( g, getVerticalDisk(A,B) );
			} else {
				drawDisk( g, A, B, Math.PI*time/50.0 );
				//drawDisk( g, A, B, Math.PI*time/50.0, delta/2 );
			}
			A += delta;
		}
		A = Math.max(a,b) - delta/2.0;
		while ( A > Math.min(a,b) && A > Z*(xaxis[2])+originX ){
			F.addVariable( "x", A );
			B = Math.abs(F.getValue()-c);
			if ( newStat ) stat += B*B;
			if ( time == 100 ){
				drawDisk( g, getVerticalDisk(A,B) );
			} else {
				drawDisk( g, A, B, Math.PI*time/50.0 );
				//drawDisk( g, A, B, Math.PI*time/50.0, delta/2 );
			}
			A -= delta;
		}
		if ( newStat ){
			stat *= time*Math.PI*delta/100.0;
			if ( b < a ) stat *= -1.0;
		}
		newStat = false;
		if ( n == 0 ) stat = 0.0;
		if ( stat < 0 ) applet.setStat( "Volume \u2248 ", stat, Color.blue );
		else applet.setStat( "Volume \u2248 ", stat, Color.red );
	}
}