package jp.crwdev.app.imagefilter;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;

import com.mortennobel.imagescaling.ImageUtils;

public class GrayscaleOp implements BufferedImageOp {

   	final double Rparam = 0.298912 * 1024;
	final double Gparam = 0.586611 * 1024;
	final double Bparam = 0.114478 * 1024;

	@Override
	public BufferedImage createCompatibleDestImage(BufferedImage src, ColorModel arg1) {
		ColorModel model = src.getColorModel();
		return new BufferedImage(model,
				model.createCompatibleWritableRaster(src.getWidth(), src.getHeight()),
				model.isAlphaPremultiplied(), null);
	}

	@Override
	public BufferedImage filter(BufferedImage src, BufferedImage dest) {
		Dimension dstDimension = new Dimension(src.getWidth(),src.getHeight());
		int dstWidth = dstDimension.width;
		int dstHeight = dstDimension.height;
		
		BufferedImage bufferedImage = doFilter(src, dest, dstWidth, dstHeight);

		return bufferedImage;
	}
	
	private BufferedImage doFilter(BufferedImage srcImg, BufferedImage dest, int dstWidth, int dstHeight){
		
		final int numberOfThreads = 4;
		
		if(	srcImg.getType() == BufferedImage.TYPE_BYTE_BINARY ||
			srcImg.getType() == BufferedImage.TYPE_BYTE_INDEXED ||
			srcImg.getType() == BufferedImage.TYPE_CUSTOM){
			srcImg = ImageUtils.convert(srcImg, srcImg.getColorModel().hasAlpha() ?
					BufferedImage.TYPE_4BYTE_ABGR : BufferedImage.TYPE_3BYTE_BGR);
		}

		int nrChannels = ImageUtils.nrChannels(srcImg);
		@SuppressWarnings("unused")
		int srcWidth = srcImg.getWidth();
		int srcHeight = srcImg.getHeight();
		
		byte[][] workPixels = new byte[srcHeight][dstWidth*nrChannels];

		final int nrChannelsCopy = nrChannels;
		final BufferedImage srcImgCopy = srcImg;
		final byte[][] workPixelsCopy = workPixels;
		Thread[] threads = new Thread[numberOfThreads-1];
		for (int i=1;i<numberOfThreads;i++){
			final int finalI = i;
			threads[i-1] = new Thread(new Runnable(){
				public void run(){
					horizontallyFromSrcToWork(srcImgCopy, workPixelsCopy, finalI, numberOfThreads, nrChannelsCopy);
				}
			});
			threads[i-1].start();
		}
		horizontallyFromSrcToWork(srcImgCopy, workPixelsCopy,0,numberOfThreads, nrChannelsCopy);
		waitForAllThreads(threads);

        byte[] outPixels = new byte[dstWidth*dstHeight*nrChannels];

		BufferedImage out;
		if (dest!=null && dstWidth==dest.getWidth() && dstHeight==dest.getHeight()){
			out = dest;
			int nrDestChannels = ImageUtils.nrChannels(dest);
			if (nrDestChannels != nrChannels){
				throw new RuntimeException("Destination image must be compatible width source image.");
			}
		}
		else{
			out = new BufferedImage(dstWidth, dstHeight, getResultBufferedImageType(nrChannels, srcImg));
		}
		
		for(int y=0; y<dstHeight; y++){
			int dh = y * dstWidth * nrChannels;
			for(int x=0; x<dstWidth; x++){
				int dx = x * nrChannels;
				for(int n=0; n<nrChannels; n++){
					outPixels[dh + dx + n] = workPixels[y][dx + n];
				}
			}
		}

		ImageUtils.setBGRPixels(outPixels, out, 0, 0, dstWidth, dstHeight);

		return out;
	}
	
    private void horizontallyFromSrcToWork(BufferedImage srcImg, byte[][] workPixels, int start, int delta, int nrChannels) {

    	int srcWidth = srcImg.getWidth();
    	int srcHeight = srcImg.getHeight();
    	
		final int[] tempPixels = new int[srcWidth];   // Used if we work on int based bitmaps, later used to keep channel values
		final byte[] srcPixels = new byte[srcWidth*nrChannels]; // create reusable row to minimize memory overhead

		for (int k = start; k < srcHeight; k=k+delta){
			ImageUtils.getPixelsBGR(srcImg, k, srcWidth, srcPixels, tempPixels);
			
			for(int x=0; x<srcWidth; x++){
				int i = x * nrChannels;
				
				int n = 0;
				if(nrChannels == 1){
					n = srcPixels[i];
				}
				else{
					byte b = srcPixels[i + 0];
					byte g = srcPixels[i + 1];
					byte r = srcPixels[i + 2];
		        	n = (((int)( r*Rparam + g*Gparam + b*Bparam )) >> 10);
				}
				for(int c=0; c<nrChannels; c++){
					workPixels[k][i + c] = (byte)(n&0xff);
				}
			}
		}
		
    }
    
	protected int getResultBufferedImageType(int nrChannels, BufferedImage srcImg) {
		return nrChannels == 3 ? BufferedImage.TYPE_3BYTE_BGR :
			(nrChannels == 4 ? BufferedImage.TYPE_4BYTE_ABGR :
				(srcImg.getSampleModel().getDataType() == DataBuffer.TYPE_USHORT ?
						BufferedImage.TYPE_USHORT_GRAY : BufferedImage.TYPE_BYTE_GRAY));
	}

    private void waitForAllThreads(Thread[] threads) {
        try {
            for (Thread t:threads){
                t.join(Long.MAX_VALUE);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    

	@Override
	public Rectangle2D getBounds2D(BufferedImage src) {
        return new Rectangle(0, 0, src.getWidth(), src.getHeight());
	}

	@Override
	public Point2D getPoint2D(Point2D src, Point2D dst) {
		return (Point2D) src.clone();
	}

	@Override
	public RenderingHints getRenderingHints() {
		return null;
	}

}
