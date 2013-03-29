package jp.crwdev.app.imagefilter;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import jp.crwdev.app.interfaces.IImageFilter;
import jp.crwdev.app.setting.ImageFilterParamSet;

public class MaximumSizeCheckFilter implements IImageFilter {

	/** フィルタリスト */
	protected List<IImageFilter> mFilters = new ArrayList<IImageFilter>();
	/** フィルタパラメータ */
	protected ImageFilterParamSet mBaseFilterParams = new ImageFilterParamSet();

	private Dimension mMaxSize = new Dimension(0, 0);
	
	private IImageFilter mBaseFilter = null;
	
	/**
	 * コンストラクタ
	 */
	public MaximumSizeCheckFilter(){
		mFilters.add(new AddSpaceFilter());
		mFilters.add(new TransRotateFilter());
		mFilters.add(new SplitFilter());
		mFilters.add(new CropFilter(true));		// 全ページCrop
		mFilters.add(new PageCheckFilter(true));
		mFilters.add(new CropFilter(false));	// Text/Pictページ別Crop
		mFilters.add(new ContrastFilter());
		mFilters.add(new GammaFilter());
		mFilters.add(new AutoCropFilter());
		mFilters.add(new GrayscaleFilter());
		//mFilters.add(new BlurFilter(true));
		mFilters.add(new ResizeFilter());
		//mFilters.add(new BlurFilter(false));
	}
	
	/**
	 * フィルタパラメータを設定する
	 * @param param
	 */
	public void setImageFilterParam(ImageFilterParamSet params){
		mBaseFilterParams = params.clone();
		mMaxSize.setSize(0, 0);
	}
	
	public void setImageFilter(IImageFilter filter){
		mBaseFilter = filter;
		mMaxSize.setSize(0, 0);
	}

	public Dimension getMaxSize(){
		return mMaxSize;
	}
	
	@Override
	public BufferedImage filter(BufferedImage image, ImageFilterParam param) {

		if(mBaseFilter != null){
			image = mBaseFilter.filter(image, param);
		}
		else{
			ImageFilterParam newParam = mBaseFilterParams.createMergedFilterParam(param);
	
			for(IImageFilter filter : mFilters){
				image = filter.filter(image, newParam);
			}
		}
		
		int width = image.getWidth();
		int height = image.getHeight();
		
		if(width > mMaxSize.width){
			mMaxSize.width = width;
		}
		if(height > mMaxSize.height){
			mMaxSize.height = height;
		}
		
		return image;
	}

}
