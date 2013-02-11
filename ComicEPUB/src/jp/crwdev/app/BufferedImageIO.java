/**
 * BufferedImageサポート関数
 */
package jp.crwdev.app;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import com.sun.image.codec.jpeg.ImageFormatException;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageDecoder;

public class BufferedImageIO {

	private static final List<String> SUPPORT_PREFIX = new ArrayList<String>();
	static{
		SUPPORT_PREFIX.add("jpg");
		SUPPORT_PREFIX.add("png");
		SUPPORT_PREFIX.add("gif");
	}

	public static BufferedImage read(InputStream stream, boolean isJpeg){
		if(isJpeg){
			JPEGImageDecoder decoder = JPEGCodec.createJPEGDecoder(stream);
			try {
				BufferedImage image = decoder.decodeAsBufferedImage();
				return prepareBufferedImage(image);
			} catch (ImageFormatException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else{
			try {
				return prepareBufferedImage(ImageIO.read(stream));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	private static BufferedImage prepareBufferedImage(BufferedImage image){
		int type = image.getType();
		if(type == BufferedImage.TYPE_BYTE_INDEXED){
			BufferedImage dest = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
			Graphics2D g = dest.createGraphics();
			g.drawImage(image, 0, 0, null);
			g.dispose();
			return dest;
		}
		return image;
	}
	
	public static boolean write(BufferedImage image, String format, float quality, OutputStream out){
		Iterator writers = ImageIO.getImageWritersByFormatName(format);
		if (writers.hasNext()) {
			try {
	            ImageWriter writer = (ImageWriter)writers.next();
				ImageOutputStream stream = ImageIO.createImageOutputStream(out);
	            writer.setOutput(stream);
	            
	            ImageWriteParam param = writer.getDefaultWriteParam();
	            if (param.canWriteCompressed()) {
	                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
	                param.setCompressionQuality(quality);
	            } else {
	                System.out.println("Compression is not supported.");
	            }
	
	            writer.write(null, new IIOImage(image, null, null), param);
	            writer.dispose();
	            return true;
			}catch(IOException e){
				e.printStackTrace();
			}
		}
		return false;
	}
	
	public static boolean isSupport(String suffix){
		return SUPPORT_PREFIX.contains(suffix.toLowerCase());
	}

}
