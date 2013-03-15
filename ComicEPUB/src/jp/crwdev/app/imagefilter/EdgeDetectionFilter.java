package jp.crwdev.app.imagefilter;

import java.awt.image.BufferedImage;

import jp.crwdev.app.interfaces.IImageFilter;

public class EdgeDetectionFilter implements IImageFilter {

	@Override
	public BufferedImage filter(BufferedImage image, ImageFilterParam param) {

		SptialFilter sp = new SptialFilter();
		return sp.filterImage(image);
		
	}

	interface Operator{
		public int operate(int[] p);
	}
	
	public class SptialFilter {
		private Operator op;
		
		public final Operator OP_PREWITT = new Differential(
				new double[]{1,0,-1,1,0,-1,1,0,-1},
				new double[]{1,1,1,0,0,0,-1,-1,-1}
				);
		public final Operator OP_SOBEL = new Differential(
				new double[]{1,0,-1,2,0,-2,1,0,-1},
				new double[]{1,2,1,0,0,0,-1,-2,-1}
				);
		
		/**
		 * 
		 */
		public SptialFilter() {
			op = OP_PREWITT;
		}

		protected BufferedImage filterImage(BufferedImage image) {
			
			BufferedImage consumer = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
			int width = image.getWidth();
			int height = image.getHeight();
			
			for(int x=0; x<width; x++){
				consumer.setRGB(x,0, image.getRGB(x, 0));
				consumer.setRGB(x,height-1, image.getRGB(x, height-1));
			}
			int[] p=new int[width];
			for (int y=1;y<height-1;y++){
				consumer.setRGB(0,y, image.getRGB(0,y));
				for (int x=1;x<width-1;x++){
					int[] px=new int[9];
					px[0]=image.getRGB(x-1,y-1);
					px[1]=image.getRGB(x,y-1);
					px[2]=image.getRGB(x+1,y-1);
					px[3]=image.getRGB(x-1,y);
					px[4]=image.getRGB(x,y);
					px[5]=image.getRGB(x+1,y);
					px[6]=image.getRGB(x-1,y+1);
					px[7]=image.getRGB(x,y+1);
					px[8]=image.getRGB(x+1,y+1);
					p[x]=op.operate(px);
					consumer.setRGB(x,y,p[x]);
				}
				consumer.setRGB(width-1,y, image.getRGB(width-1,y));
			}
			
			return consumer;
		}


		private class Differential implements Operator{
			private double[] opx;
			private double[] opy;
			Differential(double[] x,double[] y){
				opx=x;
				opy=y;
			}

			public int operate(int[] p) {
				int[] rgb=new int[3];
				double[][] val=new double[2][3];
				for(int i=0;i<p.length;i++){
					int[] tmp=getPixelRGBValue(p[i]);
					for(int j=0;j<tmp.length;j++){
						val[0][j] +=(((double)tmp[j])*opx[i]);
						val[1][j] +=(((double)tmp[j])*opy[i]);
					}
				}
				//rgb[0]=p[4];
				for(int i=0;i<rgb.length;i++){
					rgb[i]=(int)Math.sqrt((int)val[0][i]*(int)val[0][i]+
							(int)val[1][i]*(int)val[1][i]);
					if(rgb[i]>255)rgb[i]=255;
					if(rgb[i]<0)rgb[i]=0;
				}
				//return (rgb[0]<<24)+(rgb[1]<<16)+(rgb[2]<<8)+rgb[3];
				return ~((rgb[0]<<16)+(rgb[1]<<8)+rgb[2]);
			}
		}
		
		protected int[] getPixelRGBValue(int val){
			//int t=(val>>24&0xff);
			int r=(val>>16&0xff);
			int g=(val>>8&0xff);
			int b=(val&0xff);
			return new int[]{r, g, b};
		}
	}
}
