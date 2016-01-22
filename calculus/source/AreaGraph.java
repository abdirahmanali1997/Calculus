import java.awt.*;
import java.awt.geom.*;
import java.awt.event.*;

public class AreaGraph extends Graph{

	public AreaGraph( CalculusApplet applet, String f, double a, double b, int n ){
		super();

		this.applet = applet;
		this.a = a;
		this.b = b;
		this.n = Math.max( n,0 );
		
		F.parseExpression( f );
	}

	public void draw( Graphics2D g ){
		if ( applet.choice.getSelectedItem().equals("Left Riemann Sums") ){
			draw( g, 0.0 );
		} else if ( applet.choice.getSelectedItem().equals("Right Riemann Sums") ){
			draw( g, 1.0 );
		} else if ( applet.choice.getSelectedItem().equals("Midpoint Rule") ){
			draw( g, 0.5 );
		} else if ( applet.choice.getSelectedItem().equals("Trapezoidal Rule") ){
			drawTrapezoidal( g );
		} else if ( applet.choice.getSelectedItem().equals("Simpson's Rule") ){
			drawSimpson( g );
		} else if ( applet.choice.getSelectedItem().equals("Newton-Cotes Rule") ){
			drawNewtonCotes( g );
		}

		if ( n == 0 ) stat = 0.0;
		if ( stat < 0 ) applet.setStat( "Area \u2248 ", stat, Color.blue );
		else applet.setStat( "Area \u2248 ", stat, Color.red );
	}

	
	private void draw( Graphics2D g, double shift ){
		double min = Math.min( a, b );
		double delta = Math.abs(b-a)/n;
		double y;
		
		if ( newStat ) stat = 0.0;
		
		for ( int i=0; i<n; i++ ){
			F.addVariable( variable, min + (i+shift)*delta );
			y = F.getValue();
			if ( newStat ) stat += y;
			drawRectangle( g, min + i*delta, delta, y );
		}

		if ( newStat ){
			stat *= delta;
			if ( b < a ) stat *= -1;
		}
		newStat = false;
	}
	
	
	// update so that it only draws rectangles that are in the viewable region of the graph
	private void drawRectangle( Graphics2D g, double x, double width, double height ){
		if ( !(Double.isNaN(height) || Double.isInfinite(height) ) ){
			g.setColor( red );
			if ( (b-a)*height < 0 ) g.setColor( yellow );
			double[] upperLeft = toScreenPoint( x, (height>0?height:0) );
			g.fill( new Rectangle2D.Double( upperLeft[0], upperLeft[1], Math.abs(width*scale), Math.abs(height*scale) ) );
		}

		// OLD CODE
		/*	 	
		 if ( !(Double.isNaN(y) || Double.isInfinite(y)) ){
		 y *= scale;
		 g.setColor( red );
		 if ( (a<b && y<0) || (b<a && y>0) ) g.setColor( yellow );
		 P = toScreenPoint( min + i*delta, 0 );
		 // adjust y and P[1] so that only visible portion of rectangle is drawn
		 if ( y > 0 ){
		 if ( P[1] > 0 && P[1]-y < 0 ) y = P[1]+10;
		 } else {
		 if ( P[1] < h && P[1]-y > h ) y = P[1]-h-10;
		 }
		 rect = new Rectangle2D.Double( P[0], P[1] - (y>0?y:0), Math.abs(delta*scale), Math.abs(y) );
		 g.fill( rect );
		 }
		 }
		 */		
	}

	private void drawTrapezoidal( Graphics2D g ){
		double min = Math.min( a, b );
		double delta = Math.abs(b-a)/n;
		
		if ( newStat ) stat = 0.0;
		
		F.addVariable( variable, min );
		double y0 = F.getValue();
		double y1;
		
		double[] P = toScreenPoint(min,0);
		double[] Q = toScreenPoint(min,y0);
		double[] R;
		double[] S;
		double[] T;
		
		GeneralPath path;
		for ( int i=0; i<n; i++ ){
			F.addVariable( "x", min + (i+1)*delta );
			y1 = F.getValue();
			R = toScreenPoint( min + (i+1)*delta, y1 );
			S = toScreenPoint( min + (i+1)*delta, 0 );
			
			if ( newStat ) stat += y0+y1; 
			g.setColor( red );
if ( !(Double.isNaN(y0) || Double.isNaN(y1) || Double.isInfinite(y0) || Double.isInfinite(y1)) ){
			if ( y0*y1 >= 0 ){
				if ( a<b && (y0 < 0 || (y0 == 0 && y1<0 )) ) g.setColor( yellow );
				else if ( a>b && (y0 > 0 || (y0 == 0 && y1>0 )) ) g.setColor( yellow );
				path = new GeneralPath();
				path.moveTo( (float)P[0], (float)P[1] );
				path.lineTo( (float)Q[0], (float)Q[1] );
				path.lineTo( (float)R[0], (float)R[1] );
				path.lineTo( (float)S[0], (float)S[1] );
				g.fill( path );
			} else {
				if ( (a<b && y0<0) || (a>b && y0>0) ) g.setColor ( yellow );
				T = toScreenPoint( min+i*delta+y0*delta/(y0-y1),0);
				path = new GeneralPath();
				path.moveTo( (float)P[0], (float)P[1] );
				path.lineTo( (float)Q[0], (float)Q[1] );
				path.lineTo( (float)T[0], (float)T[1] );
				g.fill( path );
				
				g.setColor ( red );
				if ( (a<b && y0>0) || (a>b && y0<0) ) g.setColor ( yellow );
				path = new GeneralPath();
				path.moveTo( (float)R[0], (float)R[1] );
				path.lineTo( (float)S[0], (float)S[1] );
				path.lineTo( (float)T[0], (float)T[1] );
				g.fill( path );
			}
}
			y0 = y1;
			P = S;
			Q = R;
		}
		if ( newStat ){
			stat *= delta/2.0;
			if ( b < a ) stat *= -1;
		}
		newStat = false;
	}
	
	// decompose previous function
	// draw a single trapezoid
	private void drawTrapezoid( Graphics2D g, double x, double width, double height0, double height1 ){
	}

	private void drawTriangle( Graphics2D g, double x, double width, double height0, double height1 ){
	}

	
	private void drawSimpson( Graphics2D g ){
		double min = Math.min( a, b );
		double delta = Math.abs(b-a)/n;

		if ( newStat ) stat = 0.0;
		F.addVariable( "x", min );
		double y0 = F.getValue();
		double y1;
		double y2;
		double[] P = toScreenPoint( min ,0 );
		double[] Q = toScreenPoint( min, y0 );
		double[] R;
		double[] S;
		double[] T;
		double[] ALPHA;
		double[] BETA;
		
		double A;
		double B;
		double C;
		double alpha;
		double beta;
		double gamma;
		
		GeneralPath path;
		for ( int i=0; i<n; i++ ){
			F.addVariable( "x", min + (i+0.5)*delta );
			y1 = F.getValue();
			R = toScreenPoint( min + (i+0.5)*delta, y1 );

			F.addVariable( "x", min + (i+1)*delta );
			y2 = F.getValue();			
			S = toScreenPoint( min + (i+1)*delta, y2 );
			T = toScreenPoint( min + (i+1)*delta, 0 );
			
			
			A = 2*(y0-2*y1+y2)/delta/delta;
			B = (y2-y0)/delta;
			C = y1; 
			if ( newStat ) stat += y0+4*y1+y2; 
			g.setColor( red );

if ( !(Double.isNaN(y0) || Double.isNaN(y1) || Double.isNaN(y2) || Double.isInfinite(y0) || Double.isInfinite(y1) || Double.isInfinite(y2) ) ){
			if ( B*B-4*A*C < 0 ){
			// parabola doesn't cross x-axis ever
//if ( i==0 ) applet.statusbar.setText("1");
				if ( a < b && ( y0 < 0 || y1 < 0 || y2 < 0 ) ) g.setColor( yellow );
				else if ( a > b && ( y0 > 0 || y1 > 0 || y2 > 0 ) ) g.setColor( yellow );
				path = new GeneralPath();
				path.moveTo( (float)S[0], (float)S[1] );
				path.lineTo( (float)T[0], (float)T[1] );
				path.lineTo( (float)P[0], (float)P[1] );
				path.lineTo( (float)Q[0], (float)Q[1] );
				path.quadTo( (float)R[0], (float)(2*R[1] - (Q[1]+S[1])/2), (float)S[0], (float)S[1] );
				g.fill( path );
			} else {
			// parabola crosses x-axis
				if ( Math.abs(A) < 0.0000000000001 ){ // 10^(-13)
				// parabola is a line
					if ( B == 0 ){ 
//if ( i==0 ) applet.statusbar.setText("2");
					// draw a rectangle
						if ( a < b && ( y0 < 0 || y1 < 0 || y2 < 0 ) ) g.setColor( yellow );
						else if ( a > b && ( y0 > 0 || y1 > 0 || y2 > 0 ) ) g.setColor( yellow );
						path = new GeneralPath();
						path.moveTo( (float)P[0], (float)P[1] );
						path.lineTo( (float)Q[0], (float)Q[1] );
						path.lineTo( (float)S[0], (float)S[1] );
						path.lineTo( (float)T[0], (float)T[1] );
						g.fill( path );
					} else {
					// draw a trapezoid
						if ( y0*y2 > 0  || y0 == 0 || y2 == 0 ){
//if ( i==0 ) applet.statusbar.setText("3");
							if ( a < b && ( y0 < 0 || ( y0 == 0 && y2 < 0 ) ) ) g.setColor( yellow );
							else if ( a > b && ( y0 > 0 || ( y0 == 0 && y2 > 0 ) ) ) g.setColor( yellow );
							path = new GeneralPath();
							path.moveTo( (float)P[0], (float)P[1] );
							path.lineTo( (float)Q[0], (float)Q[1] );
							path.lineTo( (float)S[0], (float)S[1] );
							path.lineTo( (float)T[0], (float)T[1] );
							g.fill( path );
						} else {
//if ( i==0 ) applet.statusbar.setText("4");
							if ( a < b ){
								g.setColor( y0>0 ? red: yellow );
							} else {
								g.setColor( y0<0 ? red: yellow );
							}
							ALPHA = toScreenPoint( a + i*delta + y0*delta/(y0-y2),0);
							path = new GeneralPath();
							path.moveTo( (float)P[0], (float)P[1] );
							path.lineTo( (float)Q[0], (float)Q[1] );
							path.lineTo( (float)ALPHA[0], (float)ALPHA[1] );
							g.fill( path );

							if ( a < b ){
								g.setColor( y0>0 ? yellow: red );
							} else {
								g.setColor( y0<0 ? yellow: red );
							}
							path = new GeneralPath();
							path.moveTo( (float)T[0], (float)T[1] );
							path.lineTo( (float)S[0], (float)S[1] );
							path.lineTo( (float)ALPHA[0], (float)ALPHA[1] );
							g.fill( path );
						}
					}
				} else { 
				// parabola is a parabola
					alpha = Math.max( (-B + Math.sqrt(B*B-4*A*C))/(2*A), (-B - Math.sqrt(B*B-4*A*C))/(2*A) ) ;
					beta = -B/A - alpha;
					ALPHA = toScreenPoint( alpha + min + (i+0.5)*delta, 0 );
					BETA = toScreenPoint( beta + min + (i+0.5)*delta, 0 );
					if ( alpha <= -delta/2 || beta >= delta/2 || (beta <= -delta/2 && alpha >= delta/2 )){  
					// parabola does not cross x-axis on interval
//if ( i==0 ) applet.statusbar.setText("5");
						if ( a < b && ( y0 < 0 || y1 < 0 || y2 < 0 ) ) g.setColor( yellow );
						else if ( a > b && ( y0 > 0 || y1 > 0 || y2 > 0 ) ) g.setColor( yellow );
						path = new GeneralPath();
						path.moveTo( (float)S[0], (float)S[1] );
						path.lineTo( (float)T[0], (float)T[1] );
						path.lineTo( (float)P[0], (float)P[1] );
						path.lineTo( (float)Q[0], (float)Q[1] );
						path.quadTo( (float)R[0], (float)(2*R[1] - (Q[1]+S[1])/2), (float)S[0], (float)S[1] );
						g.fill( path );
					} else if ( alpha > delta/2 ){ //  beta is between a and b
//if ( i==0 ) applet.statusbar.setText("6");
//System.out.println("A:\t" + A );
						if ( a < b ){
							g.setColor( A>0 ? red: yellow );
						} else {
							g.setColor( A<0 ? red: yellow );
						}
						gamma = (beta-delta/2)/2;
						gamma = h/2 - (A*gamma*gamma + B*gamma + C - originY)*scale;
						path = new GeneralPath();
						path.moveTo( (float)BETA[0], (float)BETA[1] );
						path.lineTo( (float)P[0], (float)P[1] );
						path.lineTo( (float)Q[0], (float)Q[1] );
						path.quadTo( (float)(Q[0]/2+BETA[0]/2), (float)(2*gamma - (Q[1]+BETA[1])/2), (float)BETA[0], (float)BETA[1] );
						g.fill( path );

						if ( a < b ){
							g.setColor( A>0 ? yellow: red );
						} else {
							g.setColor( A<0 ? yellow: red );
						}
						gamma = (beta+delta/2)/2;
						gamma = h/2 - (A*gamma*gamma + B*gamma + C - originY)*scale;
						path = new GeneralPath();
						path.moveTo( (float)BETA[0], (float)BETA[1] );
						path.lineTo( (float)T[0], (float)T[1] );
						path.lineTo( (float)S[0], (float)S[1] );
						path.quadTo( (float)(S[0]/2+BETA[0]/2), (float)(2*gamma - (S[1]+BETA[1])/2), (float)BETA[0], (float)BETA[1] );
						g.fill( path );
					} else if ( beta < -delta/2 ){ //  alpha is between a and b
//if ( i==0 ) applet.statusbar.setText("7");
						if ( a < b ){
							g.setColor( A>0 ? yellow: red );
						} else {
							g.setColor( A<0 ? yellow: red );
						}
						gamma = (alpha-delta/2)/2;
						gamma = h/2 - (A*gamma*gamma + B*gamma + C - originY)*scale;
						path = new GeneralPath();
						path.moveTo( (float)ALPHA[0], (float)ALPHA[1] );
						path.lineTo( (float)P[0], (float)P[1] );
						path.lineTo( (float)Q[0], (float)Q[1] );
						path.quadTo( (float)(Q[0]/2+ALPHA[0]/2), (float)(2*gamma - (Q[1]+ALPHA[1])/2), (float)ALPHA[0], (float)ALPHA[1] );
						g.fill( path );

						if ( a < b ){
							g.setColor( A>0 ? red: yellow );
						} else {
							g.setColor( A<0 ? red: yellow );
						}
						gamma = (alpha+delta/2)/2;
						gamma = h/2 - (A*gamma*gamma + B*gamma + C - originY)*scale;
						path = new GeneralPath();
						path.moveTo( (float)ALPHA[0], (float)ALPHA[1] );
						path.lineTo( (float)T[0], (float)T[1] );
						path.lineTo( (float)S[0], (float)S[1] );
						path.quadTo( (float)(S[0]/2+ALPHA[0]/2), (float)(2*gamma - (S[1]+ALPHA[1])/2), (float)ALPHA[0], (float)ALPHA[1] );
						g.fill( path );
					} else { // alpha and beta are between a and b
//if ( i==0 ) applet.statusbar.setText("8");
						// draw from a to beta
						if ( a < b ){
							g.setColor( A>0 ? red: yellow );
						} else {
							g.setColor( A<0 ? red: yellow );
						}
						gamma = (beta-delta/2)/2;
						gamma = h/2 - (A*gamma*gamma + B*gamma + C - originY)*scale;
						path = new GeneralPath();
						path.moveTo( (float)BETA[0], (float)BETA[1] );
						path.lineTo( (float)P[0], (float)P[1] );
						path.lineTo( (float)Q[0], (float)Q[1] );
						path.quadTo( (float)(Q[0]/2+BETA[0]/2), (float)(2*gamma - (Q[1]+BETA[1])/2), (float)BETA[0], (float)BETA[1] );
						g.fill( path );

						// from beta to alpha
						if ( a < b ){
							g.setColor( A>0 ? yellow: red );
						} else {
							g.setColor( A<0 ? yellow: red );
						}
						gamma = (alpha+beta)/2;
						gamma = h/2 - (A*gamma*gamma + B*gamma + C - originY)*scale;
						path = new GeneralPath();
						path.moveTo( (float)ALPHA[0], (float)ALPHA[1] );
						path.lineTo( (float)BETA[0], (float)BETA[1] );
						path.quadTo( (float)(ALPHA[0]/2+BETA[0]/2), (float)(2*gamma - (ALPHA[1]+BETA[1])/2), (float)ALPHA[0], (float)ALPHA[1] );
						g.fill( path );

						// from alpha to b
						if ( a < b ){
							g.setColor( A>0 ? red: yellow );
						} else {
							g.setColor( A<0 ? red: yellow );
						}
						gamma = (alpha+delta/2)/2;
						gamma = h/2 - (A*gamma*gamma + B*gamma + C - originY)*scale;
						path = new GeneralPath();
						path.moveTo( (float)ALPHA[0], (float)ALPHA[1] );
						path.lineTo( (float)T[0], (float)T[1] );
						path.lineTo( (float)S[0], (float)S[1] );
						path.quadTo( (float)(S[0]/2+ALPHA[0]/2), (float)(2*gamma - (S[1]+ALPHA[1])/2), (float)ALPHA[0], (float)ALPHA[1] );
						g.fill( path );
					}
				}
			}
}
			y0 = y2;
			P = T;
			Q = S;
		}
		if ( newStat ){
			stat *= delta/6.0;
			if ( a > b ) stat *= -1;
		}
		newStat = false;
	}

	public void drawNewtonCotes( Graphics2D g ){
		double delta = (b-a)/n;

		if ( newStat ) stat = 0.0;
		F.addVariable( "x", a );
		double y0 = F.getValue();
		double y1;
		double y2;
		double y3;
		double[] P = toScreenPoint(a,0);
		double[] Q = toScreenPoint(a,y0);
		double[] R;
		double[] S;
		double[] T;
		double[] U;
		double[] ALPHA;
		double[] BETA;
		double[] GAMMA;
		
		double A;
		double B;
		double C;
		double D;
		double alpha;
		double beta;
		double gamma;
		double lambda;
		
		GeneralPath path;
		for ( int i=0; i<n; i++ ){
			F.addVariable( "x", a + (i+1/3.0)*delta );
			y1 = F.getValue();
			R = toScreenPoint( a + (i+1/3.0)*delta, y1 );

			F.addVariable( "x", a + (i+2/3.0)*delta );
			y2 = F.getValue();			
			S = toScreenPoint( a + (i+2/3.0)*delta, y2 );

			F.addVariable( "x", a + (i+1)*delta );
			y3 = F.getValue();			
			T = toScreenPoint( a + (i+1)*delta, y3 );

			U = toScreenPoint( a + (i+1)*delta, 0 );
			
			
			A = 2*(y0-2*y1+y2)/delta/delta;
			B = (y2-y0)/delta;
			C = y1; 
			if ( newStat ) stat += y0+3*y1+3*y2+y3; 

			g.setColor( red );
			if ( B*B-4*A*C < 0 ){
				if (y0 < 0 || ( y0 == 0 && y2 < 0 ) ) g.setColor( yellow );
//if ( i==0 ) applet.statusbar.setText("1");
				path = new GeneralPath();
				path.moveTo( (float)S[0], (float)S[1] );
				path.lineTo( (float)T[0], (float)T[1] );
				path.lineTo( (float)P[0], (float)P[1] );
				path.lineTo( (float)Q[0], (float)Q[1] );
				path.quadTo( (float)R[0], (float)(2*R[1] - (Q[1]+S[1])/2), (float)S[0], (float)S[1] );
				g.fill( path );
			} else {
				if ( Math.abs(A) < 0.000000000000001 ){
					if ( B == 0 ){ // draw a rectangle
						if ( y0 < 0 || ( y0 == 0 && y2 < 0 ) ) g.setColor( yellow );
//if ( i==0 ) applet.statusbar.setText("2");
						path = new GeneralPath();
						path.moveTo( (float)P[0], (float)P[1] );
						path.lineTo( (float)Q[0], (float)Q[1] );
						path.lineTo( (float)S[0], (float)S[1] );
						path.lineTo( (float)T[0], (float)T[1] );
						g.fill( path );
					} else {  // draw a trapezoid
						if ( y0*y2 > 0  || y0==0 || y2 == 0 ){
							if ( y0 < 0 || ( y0 == 0 && y2 < 0 ) ) g.setColor( yellow );
//if ( i==0 ) applet.statusbar.setText("3");
							path = new GeneralPath();
							path.moveTo( (float)P[0], (float)P[1] );
							path.lineTo( (float)Q[0], (float)Q[1] );
							path.lineTo( (float)S[0], (float)S[1] );
							path.lineTo( (float)T[0], (float)T[1] );
							g.fill( path );
						} else {
							g.setColor( y0>0 ? red: yellow );
//if ( i==0 ) applet.statusbar.setText("4");
							ALPHA = toScreenPoint( a + i*delta + y0*delta/(y0-y2),0);
							path = new GeneralPath();
							path.moveTo( (float)P[0], (float)P[1] );
							path.lineTo( (float)Q[0], (float)Q[1] );
							path.lineTo( (float)ALPHA[0], (float)ALPHA[1] );
							g.fill( path );
				
							g.setColor( y0>0 ? yellow: red );
							path = new GeneralPath();
							path.moveTo( (float)T[0], (float)T[1] );
							path.lineTo( (float)S[0], (float)S[1] );
							path.lineTo( (float)ALPHA[0], (float)ALPHA[1] );
							g.fill( path );
						}
					}
				} else {
					alpha = Math.max( (-B + Math.sqrt(B*B-4*A*C))/(2*A), (-B - Math.sqrt(B*B-4*A*C))/(2*A) ) ;
					beta = -B/A - alpha;
					ALPHA = toScreenPoint( alpha + a + (i+0.5)*delta, 0 );
					BETA = toScreenPoint( beta + a + (i+0.5)*delta, 0 );
					if ( alpha <= -delta/2 || beta >= delta/2 || (beta <= -delta/2 && alpha >= delta/2 )){
						if ( y0 < 0 || ( y0 == 0 && y2 < 0 ) ) g.setColor( yellow );
//if ( i==0 ) applet.statusbar.setText("5");
						path = new GeneralPath();
						path.moveTo( (float)S[0], (float)S[1] );
						path.lineTo( (float)T[0], (float)T[1] );
						path.lineTo( (float)P[0], (float)P[1] );
						path.lineTo( (float)Q[0], (float)Q[1] );
						path.quadTo( (float)R[0], (float)(2*R[1] - (Q[1]+S[1])/2), (float)S[0], (float)S[1] );
						g.fill( path );
					} else if ( alpha > delta/2 ){ //  beta is between a and b
						g.setColor( A>0 ? red: yellow );
//if ( i==0 ) applet.statusbar.setText("6");
						gamma = (beta-delta/2)/2;
						gamma = h/2 - (A*gamma*gamma + B*gamma + C - originY)*scale;
						path = new GeneralPath();
						path.moveTo( (float)BETA[0], (float)BETA[1] );
						path.lineTo( (float)P[0], (float)P[1] );
						path.lineTo( (float)Q[0], (float)Q[1] );
						path.quadTo( (float)(Q[0]/2+BETA[0]/2), (float)(2*gamma - (Q[1]+BETA[1])/2), (float)BETA[0], (float)BETA[1] );
						g.fill( path );

						g.setColor( A>0 ? yellow: red );
						gamma = (beta+delta/2)/2;
						gamma = h/2 - (A*gamma*gamma + B*gamma + C - originY)*scale;
						path = new GeneralPath();
						path.moveTo( (float)BETA[0], (float)BETA[1] );
						path.lineTo( (float)T[0], (float)T[1] );
						path.lineTo( (float)S[0], (float)S[1] );
						path.quadTo( (float)(S[0]/2+BETA[0]/2), (float)(2*gamma - (S[1]+BETA[1])/2), (float)BETA[0], (float)BETA[1] );
						g.fill( path );
					} else if ( beta < -delta/2 ){ //  alpha is between a and b
						g.setColor( A>0 ? yellow: red );
//if ( i==0 ) applet.statusbar.setText("7");
						gamma = (alpha-delta/2)/2;
						gamma = h/2 - (A*gamma*gamma + B*gamma + C - originY)*scale;
						path = new GeneralPath();
						path.moveTo( (float)ALPHA[0], (float)ALPHA[1] );
						path.lineTo( (float)P[0], (float)P[1] );
						path.lineTo( (float)Q[0], (float)Q[1] );
						path.quadTo( (float)(Q[0]/2+ALPHA[0]/2), (float)(2*gamma - (Q[1]+ALPHA[1])/2), (float)ALPHA[0], (float)ALPHA[1] );
						g.fill( path );

						g.setColor( A>0 ? red: yellow );
						gamma = (alpha+delta/2)/2;
						gamma = h/2 - (A*gamma*gamma + B*gamma + C - originY)*scale;
						path = new GeneralPath();
						path.moveTo( (float)ALPHA[0], (float)ALPHA[1] );
						path.lineTo( (float)T[0], (float)T[1] );
						path.lineTo( (float)S[0], (float)S[1] );
						path.quadTo( (float)(S[0]/2+ALPHA[0]/2), (float)(2*gamma - (S[1]+ALPHA[1])/2), (float)ALPHA[0], (float)ALPHA[1] );
						g.fill( path );
					} else { // alpha and beta are between a and b
						// draw from a to beta
						g.setColor( A>0 ? red: yellow );
//if ( i==0 ) applet.statusbar.setText("8");
						gamma = (beta-delta/2)/2;
						gamma = h/2 - (A*gamma*gamma + B*gamma + C - originY)*scale;
						path = new GeneralPath();
						path.moveTo( (float)BETA[0], (float)BETA[1] );
						path.lineTo( (float)P[0], (float)P[1] );
						path.lineTo( (float)Q[0], (float)Q[1] );
						path.quadTo( (float)(Q[0]/2+BETA[0]/2), (float)(2*gamma - (Q[1]+BETA[1])/2), (float)BETA[0], (float)BETA[1] );
						g.fill( path );

						// from beta to alpha
						g.setColor( A>0 ? yellow: red );
						gamma = (alpha+beta)/2;
						gamma = h/2 - (A*gamma*gamma + B*gamma + C - originY)*scale;
						path = new GeneralPath();
						path.moveTo( (float)ALPHA[0], (float)ALPHA[1] );
						path.lineTo( (float)BETA[0], (float)BETA[1] );
						path.quadTo( (float)(ALPHA[0]/2+BETA[0]/2), (float)(2*gamma - (ALPHA[1]+BETA[1])/2), (float)ALPHA[0], (float)ALPHA[1] );
						g.fill( path );

						// from alpha to b
						g.setColor( A>0 ? red: yellow );
						gamma = (alpha+delta/2)/2;
						gamma = h/2 - (A*gamma*gamma + B*gamma + C - originY)*scale;
						path = new GeneralPath();
						path.moveTo( (float)ALPHA[0], (float)ALPHA[1] );
						path.lineTo( (float)T[0], (float)T[1] );
						path.lineTo( (float)S[0], (float)S[1] );
						path.quadTo( (float)(S[0]/2+ALPHA[0]/2), (float)(2*gamma - (S[1]+ALPHA[1])/2), (float)ALPHA[0], (float)ALPHA[1] );
						g.fill( path );
					}
				}
			}

			y0 = y3;
			P = U;
			Q = T;
		}
		if ( newStat ){
			stat *= delta/8.0;
		}
		newStat = false;
	}
}