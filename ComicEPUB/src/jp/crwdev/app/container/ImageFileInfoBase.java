/**
 * 画像ファイル情報基本データクラス
 */
package jp.crwdev.app.container;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;

import jp.crwdev.app.imagefilter.ImageFilterParam;
import jp.crwdev.app.interfaces.IImageFileInfo;

public abstract class ImageFileInfoBase implements IImageFileInfo {

	/** 個別フィルタパラメータ */
	protected ImageFilterParam mPrivateFilter = new ImageFilterParam();
	/** 画像フォーマット */
	protected String mFormat;
	/** 画像幅 */
	protected int mWidth;
	/** 画像高さ */
	protected int mHeight;
	/** 画像サイズ */
	protected long mSize;
	
	/**
	 * 基本データの読み込み
	 * @throws Exception getFullPath(),getInputStream()の実装必須
	 */
	@SuppressWarnings({ "unchecked" })
	protected void loadBasicParams() throws Exception{
		String suffix = getSuffix(getFullPath());

		Iterator readers = ImageIO.getImageReadersBySuffix(suffix);
		if (readers.hasNext()) {
            ImageReader reader = (ImageReader)readers.next();
			try {
				//InputStream in = getInputStream();
				//ImageInputStream stream = ImageIO.createImageInputStream(in);
				//reader.setInput(stream);
				mFormat = getFormat(suffix);
				//mWidth = reader.getWidth(0);
				//mHeight = reader.getHeight(0);
				mSize = 0;//in.available();//stream.length();
			//} catch (IOException e) {
			//	throw e;
			} finally {
				reader.dispose();
			}
		}
		else{
			throw new Exception("No image");
		}

	}

	@Override
	public ImageFilterParam getFilterParam(){
		return mPrivateFilter;
	}
	
	@Override
	public void setFilterParam(ImageFilterParam param){
		mPrivateFilter = param;
	}

	@Override
	public String getFormat(){
		return mFormat;
	}
	
	@Override
	public int getWidth(){
		return mWidth;
	}
	
	@Override
	public int getHeight(){
		return mHeight;
	}
	
	@Override
	public long getSize(){
		return mSize;
	}
	
	@Override
	public boolean isJpeg() {
		return "jpeg".equals(mFormat);
	}

	/**
	 * ファイル名から拡張子を取得
	 * @param fileName
	 * @return "."を含まない拡張子。拡張子が無い場合は入力をそのまま返す
	 */
	protected static String getSuffix(String fileName) {
	    if (fileName == null)
	        return null;
	    fileName = fileName.toLowerCase();
	    int point = fileName.lastIndexOf(".");
	    if (point != -1) {
	        return fileName.substring(point + 1);
	    }
	    return fileName;
	}
	
	/**
	 * 拡張子からフォーマットを取得
	 * @param suffix
	 * @return "jpeg" or "png" or "gif"
	 */
	protected static String getFormat(String suffix){
		if(suffix.equalsIgnoreCase("jpg")){
			return "jpeg";
		}
		else if(suffix.equalsIgnoreCase("png")){
			return "png";
		}
		else if(suffix.equalsIgnoreCase("gif")){
			return "gif";
		}
		return "jpeg";
	}


}
