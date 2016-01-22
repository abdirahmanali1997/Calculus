import java.awt.*;
import java.awt.geom.*;
import java.awt.image.*;
import javax.swing.*;

public class ColorComboBox extends JComboBox{

		
	static Color rainbow = new Color(1,2,3);
	Color[] colors = { Color.green, Color.cyan, Color.blue, Color.magenta, 
						new Color( 127,0,127 ), // purple
						Color.red, Color.orange, Color.yellow, Color.white,
						new Color( 153,102,51 ), // brown
						Color.black, rainbow
					};

	
	public ColorComboBox(){
		super();
		setModel( new DefaultComboBoxModel( colors ) );
		setRenderer( new Renderer() );
		setMaximumRowCount( colors.length );
		//setSelectedIndex( 5 );
		setSelectedIndex( colors.length-1 );
	}
	
	
	class Renderer extends JLabel implements ListCellRenderer {

		public Renderer() {
			setPreferredSize(new Dimension(25, 16));
            setHorizontalAlignment( CENTER );
            setVerticalAlignment( CENTER );
		}
		
		public Component getListCellRendererComponent( JList list, Object value, int index, boolean isSelected, boolean cellHasFocus){
            setIcon( getIcon((Color)value) );
			setBackground( isSelected ? Color.black : Color.white );
			return this;
		}
		
		public ImageIcon getIcon( Color color ){
			int w = 23;
			int h = 14;
		
			//Image image = wheelpanel.createImage( w, h );
			//Graphics g = image.getGraphics();
			BufferedImage image = new BufferedImage( w, h, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = image.createGraphics();
			
			if ( color != rainbow ){
				g.setColor( color );
				Rectangle2D rect = new Rectangle2D.Double(0,0,w,h);
				g.fill( rect );
				g.setColor( Color.black );
				g.draw( rect );
			} else {
				float u;
				float v;
				int red;
				int grn;
				int blu;
				for ( int i=0; i<w; i++){
					for ( int j=0; j<h; j++ ){
						u = (float)j/h;
						v = (float)i/w;
							

						red = (int)(0*(1-u)*(1-v) + 0*u*(1-v) +255*u*v + 255*(1-u)*v);
						grn = (int)(255*(1-u)*(1-v) + 255*u*(1-v) + 0*u*v + 255*(1-u)*v);
						blu = (int)(0*(1-u)*(1-v) + 255*u*(1-v) + 255*u*v + 0*(1-u)*v);

						image.setRGB(i,j, (255<<24) + (red<<16) + (grn<<8) + blu );
					}
				}
			}
			
			return new ImageIcon( image );
		}
	}
}