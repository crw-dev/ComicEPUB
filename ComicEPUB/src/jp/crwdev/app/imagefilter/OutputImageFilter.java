/**
 * 出力用全フィルタ
 */
package jp.crwdev.app.imagefilter;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import jp.crwdev.app.interfaces.IImageFilter;

public class OutputImageFilter implements IImageFilter {

	/** フィルタリスト */
	protected List<IImageFilter> mFilters = new ArrayList<IImageFilter>();
	/** フィルタパラメータ */
	protected ImageFilterParam mBaseFilterParam = new ImageFilterParam();

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
		mFilters.add(new ResizeFilter());
	}
	
	/**
	 * コンストラクタ
	 * @param param
	 */
	public OutputImageFilter(ImageFilterParam param){
		this();
		mBaseFilterParam = param.clone();
	}

	/**
	 * フィルタパラメータを設定する
	 * @param param
	 */
	public void setImageFilterParam(ImageFilterParam param){
		mBaseFilterParam = param.clone();
	}
	
	/**
	 * フィルタパラメータを取得する
	 * @return
	 */
	public ImageFilterParam getImageFilterParam(){
		return mBaseFilterParam;
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
		
		ImageFilterParam newParam = mBaseFilterParam.createMergedFilterParam(param);
		
		for(IImageFilter filter : mFilters){
			image = filter.filter(image, newParam);
		}
		
		return image;
	}
}
