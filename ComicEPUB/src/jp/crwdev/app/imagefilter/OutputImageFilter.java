/**
 * 出力用全フィルタ
 */
package jp.crwdev.app.imagefilter;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import jp.crwdev.app.interfaces.IImageFilter;
import jp.crwdev.app.setting.ImageFilterParamSet;

public class OutputImageFilter implements IImageFilter {

	/** フィルタリスト */
	protected List<IImageFilter> mFilters = new ArrayList<IImageFilter>();
	/** フィルタパラメータ */
	protected ImageFilterParamSet mBaseFilterParams = new ImageFilterParamSet();

	/**
	 * コンストラクタ
	 */
	public OutputImageFilter(){
		mFilters.add(new AddSpaceFilter());
		mFilters.add(new TransRotateFilter());
		mFilters.add(new SplitFilter());
		mFilters.add(new CropFilter(true));		// 全ページCrop
		mFilters.add(new PageCheckFilter(true));
		mFilters.add(new CropFilter(false));	// Text/Pictページ別Crop
		mFilters.add(new ContrastFilter());
		mFilters.add(new GammaFilter());
		mFilters.add(new GrayscaleFilter());
		mFilters.add(new BlurFilter(true));
		mFilters.add(new ResizeFilter());
//		mFilters.add(new BlurFilter(false));
	}
	
	/**
	 * コンストラクタ
	 * @param param
	 */
	public OutputImageFilter(ImageFilterParamSet params){
		this();
		mBaseFilterParams = params.clone();
	}

	/**
	 * フィルタパラメータを設定する
	 * @param param
	 */
	public void setImageFilterParam(ImageFilterParamSet params){
		mBaseFilterParams = params.clone();
	}
	
	/**
	 * フィルタパラメータを取得する
	 * @return
	 */
	public ImageFilterParamSet getImageFilterParam(){
		return mBaseFilterParams;
	}
	
	/**
	 * フィルタリストを取得する
	 * @return
	 */
	protected List<IImageFilter> getImageFilters(){
		return mFilters;
	}
	
	@Override
	public BufferedImage filter(BufferedImage image, ImageFilterParam param) {
		ImageFilterParam newParam = mBaseFilterParams.createMergedFilterParam(param);

		for(IImageFilter filter : mFilters){
			image = filter.filter(image, newParam);
		}
		
		return image;
	}
}
