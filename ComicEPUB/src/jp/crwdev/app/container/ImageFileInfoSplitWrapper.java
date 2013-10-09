/**
 * 画像分割用ラッパー
 */
package jp.crwdev.app.container;

import java.io.InputStream;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jp.crwdev.app.imagefilter.ImageFilterParam;
import jp.crwdev.app.interfaces.IImageFileInfo;

public class ImageFileInfoSplitWrapper implements IImageFileInfo {
	
	/** 元データ */
	private IImageFileInfo mBaseInfo = null;
	/** 分割後の個別フィルタパラメータ */
	private ImageFilterParam mParam = null;
	
	/** index=0のWrapperは全ての個別パラメータへの参照を持つ */
	private HashMap<Integer, ImageFileInfoSplitWrapper> mWrapperParams = null;
	/** index=0以外のWrapperはindex=0への参照を持つ */
	private ImageFileInfoSplitWrapper mFirstWrapper = null;
	
	/** TOCテキスト */
	private String mTocText = null;
	
	/** 編集フラグ */
	private boolean mIsModify = false;
	
	/** 有効フラグ */
	boolean mIsEnable = true;
	
	/**
	 * コンストラクタ
	 * @param info 画像ファイル情報元データ
	 * @param splitIndex 分割後のインデックス情報
	 */
	public ImageFileInfoSplitWrapper(IImageFileInfo info, int splitIndex){
		if(info == null){
			throw new InvalidParameterException();
		}
		
		mBaseInfo = info;
		
		mParam = info.getFilterParam().clone();
		//mParam.setSplitType(Constant.SPLITTYPE_NONE);
		mParam.setSplitIndex(splitIndex);
	}
	
	public void setFirstSplitInfo(ImageFileInfoSplitWrapper first){
		mFirstWrapper = first;
	}
	public ImageFileInfoSplitWrapper getFirstSplitInfo(){
		return mFirstWrapper;
	}
	
	public void addRelativeSplitInfo(ImageFileInfoSplitWrapper info){
		if(mWrapperParams == null){
			mWrapperParams = new HashMap<Integer, ImageFileInfoSplitWrapper>();
		}
		ImageFilterParam param = info.getFilterParam();
		mWrapperParams.put(param.getSplitIndex(), info);
	}
	
	public ImageFileInfoSplitWrapper getRelativeSplitInfo(int index){
		return mWrapperParams.get(index);
	}

	public ImageFilterParam getRelativeSplitInfoFilterParam(int index){
		return mWrapperParams.get(index).getFilterParam();
	}
	
	public int getRelativeSplitInfoSize(){
		if(mWrapperParams != null){
			return mWrapperParams.size();
		}else{
			return 0;
		}
	}
	
	@Override
	public void setEnable(boolean enable){
		mIsEnable = enable;
	}
	
	@Override
	public boolean isEnable(){
		return mIsEnable;
	}


	/**
	 * 元ファイル情報取得
	 * @return
	 */
	public IImageFileInfo getBaseFileInfo() {
		return mBaseInfo;
	}

	@Override
	public String getFileName() {
		return mBaseInfo.getFileName();
	}

	@Override
	public String getFullPath() {
		return mBaseInfo.getFullPath();
	}

	@Override
	public String getFormat() {
		return mBaseInfo.getFormat();
	}

	@Override
	public int getWidth() {
		//TODO split mode
		return mBaseInfo.getWidth();
	}

	@Override
	public int getHeight() {
		//TODO split mode
		return mBaseInfo.getHeight();
	}

	@Override
	public long getSize() {
		return mBaseInfo.getSize();
	}

	@Override
	public InputStream getInputStream() {
		return mBaseInfo.getInputStream();
	}

	@Override
	public boolean isJpeg() {
		return mBaseInfo.isJpeg();
	}
	
	@Override
	public void update() {
		//TODO split mode
		mBaseInfo.update();
	}

	@Override
	public ImageFilterParam getFilterParam() {
		return mParam;
	}

	@Override
	public void setFilterParam(ImageFilterParam param) {
		mParam = param;
	}

	@Override
	public void release() {
		// NOP
	}

	@Override
	public void setTocText(String text) {
		mTocText = text;
	}

	@Override
	public String getTocText() {
		return mTocText;
	}

	@Override
	public void setModify(boolean modify){
		mIsModify = modify;
	}

	@Override
	public boolean isModify() {
		return mIsModify;
	}

}
