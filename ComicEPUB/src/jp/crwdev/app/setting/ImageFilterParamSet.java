package jp.crwdev.app.setting;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Hashtable;

import jp.crwdev.app.constant.Constant;
import jp.crwdev.app.imagefilter.ImageFilterParam;


public class ImageFilterParamSet extends ArrayList<ImageFilterParam> {
	
	public static final int FILTER_INDEX_BASIC = 0;
	public static final int FILTER_INDEX_COLOR = 1;
	public static final int FILTER_INDEX_PICT = 2;
	public static final int FILTER_INDEX_TEXT = 3;

	private boolean mIsSimpleZoom = false;
	
	public ImageFilterParamSet(){
		add(new ImageFilterParam());	// basic
		add(null);	// color
		add(null);	// pict
		add(null);	// text
	}
	
	public ImageFilterParamSet clone(){
		ImageFilterParamSet newSet = new ImageFilterParamSet();
		for(int i=0; i<size(); i++){
			ImageFilterParam param = get(i);
			newSet.set(i, param != null ? param.clone() : null);
		}
		return newSet;
	}
	
	public ImageFilterParam createMergedFilterParam(ImageFilterParam param){
		int pageType = param.getPageType();
		switch(pageType){
		case Constant.PAGETYPE_COLOR:
			if(get(FILTER_INDEX_COLOR) != null){
				return get(FILTER_INDEX_COLOR).createMergedFilterParam(param);
			}else{
				return get(FILTER_INDEX_BASIC).createMergedFilterParam(param);
			}
		case Constant.PAGETYPE_PICT:
			if(get(FILTER_INDEX_PICT) != null){
				return get(FILTER_INDEX_PICT).createMergedFilterParam(param);
			}else{
				return get(FILTER_INDEX_BASIC).createMergedFilterParam(param);
			}
		case Constant.PAGETYPE_TEXT:
			if(get(FILTER_INDEX_TEXT) != null){
				return get(FILTER_INDEX_TEXT).createMergedFilterParam(param);
			}else{
				return get(FILTER_INDEX_BASIC).createMergedFilterParam(param);
			}
		case Constant.PAGETYPE_AUTO:
		default:
			return get(FILTER_INDEX_BASIC).createMergedFilterParam(param);
		}
	}
	
	public void setPreview(boolean enable){
		for(int i=0; i<size(); i++){
			ImageFilterParam param = get(i);
			if(param != null){
				param.setPreview(enable);
			}
		}
	}

	public void setResize(boolean enable){
		for(int i=0; i<size(); i++){
			ImageFilterParam param = get(i);
			if(param != null){
				param.setResize(enable);
			}
		}
	}

	public void setResizeDimension(Dimension size){
		for(int i=0; i<size(); i++){
			ImageFilterParam param = get(i);
			if(param != null){
				param.setResizeDimension(size);
			}
		}
	}
	
	public void setSimpleZoom(boolean simpleZoom){
		mIsSimpleZoom = simpleZoom;
	}
	
	public boolean isSimpleZoom(){
		return mIsSimpleZoom;
	}

}
