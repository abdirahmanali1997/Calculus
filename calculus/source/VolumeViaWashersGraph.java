import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.*;

public class VolumeViaWashersGraph extends Graph3D{
	

	public VolumeViaWashersGraph( CalculusApplet applet, String f, String g, double a, double b, double c, int n ){
		super();

		this.applet = applet;
		this.a = a;						// lower limit of integration
		this.b = b;						// upper limit of integration
		this.c = c;						// axis of rotation
		this.n = Math.max( n,0 );		// number of cross-sections
		
		time = 100;

		F.parseExpression( f );			// boundary curve
		G.parseExpression( g );			// boundary curve
	}


	public void draw( Graphics2D g ){
		delta = Math.abs(b-a)/n;
		if ( newStat ) stat = 0.0;

		double A = Math.min(a,b) + delta/2.0;
		if ( showGrid ) drawGridLines( g );
		if ( !shift ) drawAxes( g );
		if ( !shift ) drawAxisOfRotation( g );
		if ( !shift ) drawFunction( g );

		// eye focused on middle of panel
		A = Math.min(a,b) + delta/2.0;
		double R;
		double r;
		while ( A < Math.max(a,b) && A < Z*(xaxis[2])+originX ){
			F.addVariable( "x", A );
			G.addVariable( "x", A );
			R = Math.max( Math.abs(F.getValue()-c), Math.abs(G.getValue()-c) );
			r = Math.min( Math.abs(F.getValue()-c), Math.abs(G.getValue()-c) );
			if ( newStat ) stat += R*R - r*r;
			if ( r > 0.001 ){
//This is a temporary fix to crash problem ( instead of r == 0.0 )
				if ( time == 100 ){
					drawWasher( g, getVerticalDisk(A,R), getVerticalDisk(A,r) );
				} else {
					drawWasher( g, A, R, r, Math.PI*time/50.0 );
				}
			} else {
				if ( time == 100 ){
					drawDisk( g, getVerticalDisk(A,R) );
				} else {
					drawDisk( g, A, R, Math.PI*time/50.0 );
				}
			}
			A += delta;
		}
		A = Math.max(a,b) - delta/2.0;
		while ( A > Math.min(a,b) && A > Z*(xaxis[2])+originX ){
			F.addVariable( "x", A );
			G.addVariable( "x", A );
			R = Math.max( Math.abs(F.getValue()-c), Math.abs(G.getValue()-c) );
			r = Math.min( Math.abs(F.getValue()-c), Math.abs(G.getValue()-c) );
			if ( newStat ) stat += R*R - r*r;
			if ( r > 0.001 ){
//This is a temporary fix to crash problem ( instead of r == 0.0 )
				if ( time == 100 ){
					drawWasher( g, getVerticalDisk(A,R), getVerticalDisk(A,r) );
				} else {
					drawWasher( g, A, R, r, Math.PI*time/50.0 );
				}
			} else {
				if ( time == 100 ){
					drawDisk( g, getVerticalDisk(A,R) );
				} else {
					drawDisk( g, A, R, Math.PI*time/50.0 );
				}
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


	public void drawEndPoints( Graphics2D g ){
		g.setStroke( endline );
		F.addVariable( variable, b );
		G.addVariable( variable, b );
		drawLine( g, b, 0, b, F.getValue(), colorB );
		drawLine( g, b, 0, b, G.getValue(), colorB );

		F.addVariable( variable, a );
		G.addVariable( variable, a );
		drawLine( g, a, 0, a, F.getValue(), colorA );
		drawLine( g, a, 0, a, G.getValue(), colorA );
	}


	public void drawFunction( Graphics2D g ){
		drawFunction( g, G, Color.black );
		drawFunction( g, F, Color.black );
	}
}