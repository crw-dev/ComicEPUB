/**
 * Preview用フィルタ
 */
package jp.crwdev.app.imagefilter;

import java.awt.Dimension;
import java.awt.image.BufferedImage;

import jp.crwdev.app.interfaces.IImageFilter;

public class PreviewImageFilter extends OutputImageFilter {

	//private boolean mIsPreview = true;
	
	/** プレビュー画面サイズ */
	private Dimension mPreviewSize = new Dimension(600, 800);
	/** リサイズフィルタ - Preview画面のサイズ変更で使うのでリストとは別に保持しておく */
	private ResizeFilter mResizeFilter = null;
//	/** 余白追加フィルタ - リストとは別に保持しておく */
//	private AddSpaceFilter mAddSpaceFilter = null;
	
	
	/**
	 * コンストラクタ
	 */
	public PreviewImageFilter(){
		super();
		
		//mIsPreview = true;
		
		setFilterVariables();
	}
	

	/**
	 * プレビューサイズ設定
	 * @param width
	 * @param height
	 */
	public void setPreviewSize(int width, int height){
		if(width <= 20 || height <= 20){
			return;
		}
		mPreviewSize.setSize(width-20, height-20);
	}
	
//	/**
//	 * 余白追加フィルタへターゲットサイズ指定
//	 * @param size
//	 */
//	public void setAddSpaceDimension(Dimension size){
//		mAddSpaceFilter.setTargetSize(size);
//	}

	/**
	 * リサイズ時のスケーリング係数W を取得
	 * @return
	 */
	public double getResizedScaleW(){
		return mResizeFilter.getResizedScaleW();
	}
	/**
	 * リサイズ時のスケーリング係数H を取得
	 * @return
	 */
	public double getResizedScaleH(){
		return mResizeFilter.getResizedScaleH();
	}
	
	/**
	 * フィルタリストから良く使うフィルタを抜き出しておく
	 */
	private void setFilterVariables(){
		for(IImageFilter filter : mFilters){
//			if(filter instanceof AddSpaceFilter){
//				mAddSpaceFilter = (AddSpaceFilter)filter;
//			}
			if(filter instanceof ResizeFilter){
				mResizeFilter = (ResizeFilter)filter;
			}
		}
	}


	@Override
	public BufferedImage filter(BufferedImage image, ImageFilterParam param) {
		
		ImageFilterParam newParam = mBaseFilterParams.createMergedFilterParam(param);
		//newParam.setPreview(mIsPreview);
		//newParam.setResize(true);
		newParam.setResizeDimension(mPreviewSize);
		
		for(IImageFilter filter : mFilters){
			image = filter.filter(image, newParam);
		}
		
		return image;
	}
	
	
}
