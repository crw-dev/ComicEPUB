/**
 * 画像ファイル情報リスト基本クラス
 */
package jp.crwdev.app.container;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;


import jp.crwdev.app.constant.Constant;
import jp.crwdev.app.imagefilter.ImageFilterParam;
import jp.crwdev.app.imagefilter.SplitFilter;
import jp.crwdev.app.interfaces.IImageFileInfo;
import jp.crwdev.app.interfaces.IImageFileInfoList;

public abstract class ImageFileInfoList implements IImageFileInfoList {

	/** 画像ファイル情報リスト */
	protected List<IImageFileInfo> mList = new ArrayList<IImageFileInfo>();
		
	/**
	 * 本文ページの最大サイズを取得
	 * @param list
	 * @return
	 */
	public static Dimension getTextPageUnionDimension(IImageFileInfoList list){
		if(list == null){
			return new Dimension(0,0);
		}
		
		Dimension max = new Dimension(0,0);
		int size = list.size();
		for(int i=0; i<size; i++){
			IImageFileInfo info = list.get(i);
			ImageFilterParam param = info.getFilterParam();
			if(param.getPageType() != Constant.PAGETYPE_PICT){
				int width = info.getWidth();
				int height = info.getHeight();
				if(max.width < width){
					max.width = width;
				}
				if(max.height < height){
					max.height = height;
				}
			}
		}
		return max;
	}
	
	/**
	 * renew()用共通処理。SplitTypeを反映した新しいリストを構築する
	 * @param list
	 * @return
	 */
	protected IImageFileInfoList renewInternal(IImageFileInfoList list){
		
		for(int i=0; i<mList.size(); i++){
			IImageFileInfo info = mList.get(i);
			ImageFilterParam param = info.getFilterParam();
			if(info instanceof ImageFileInfoSplitWrapper){
				// 分割中
				ImageFileInfoSplitWrapper infow = (ImageFileInfoSplitWrapper)info;
				if(param.getSplitIndex() == 0){
					// １つ目が全ての情報を持っている
					int pageType = param.getSplitType();
					IImageFileInfo baseInfo = infow.getBaseFileInfo();
					baseInfo.getFilterParam().setSplitType(pageType);
					if(pageType == SplitFilter.TYPE_NONE){
						// 分割なしに戻す（全ての分割情報は捨てる）
						list.add(baseInfo);
					}
					else{
						// 分割し直す（全ての分割情報は捨てる）
						int splitCount = 1;
						switch(param.getSplitType()){
						case SplitFilter.TYPE_L2R_2:
						case SplitFilter.TYPE_R2L_2:
							splitCount = 2;
							break;
						case SplitFilter.TYPE_L2R_2x2:
						case SplitFilter.TYPE_R2L_2x2:
							splitCount = 4;
							break;
						case SplitFilter.TYPE_L2R_3x3:
						case SplitFilter.TYPE_R2L_3x3:
							splitCount = 9;
							break;
						default:
						}
						
						ImageFileInfoSplitWrapper first = null;
						for(int index=0; index<splitCount; index++){
							ImageFileInfoSplitWrapper wrapInfo = new ImageFileInfoSplitWrapper(baseInfo, index);
							list.add(wrapInfo);

							if(first == null){
								first = wrapInfo;
							}
							first.addRelativeSplitInfo(wrapInfo);
						}
					}
				}
			}else{
				if(param.getSplitType() != SplitFilter.TYPE_NONE){
					// １　→　分割
					int splitCount = 1;
					switch(param.getSplitType()){
					case SplitFilter.TYPE_L2R_2:
					case SplitFilter.TYPE_R2L_2:
						splitCount = 2;
						break;
					case SplitFilter.TYPE_L2R_2x2:
					case SplitFilter.TYPE_R2L_2x2:
						splitCount = 4;
						break;
					case SplitFilter.TYPE_L2R_3x3:
					case SplitFilter.TYPE_R2L_3x3:
						splitCount = 9;
						break;
					default:
					}
					ImageFileInfoSplitWrapper first = null;
					for(int index=0; index<splitCount; index++){
						ImageFileInfoSplitWrapper wrapInfo = new ImageFileInfoSplitWrapper(info, index);
						list.add(wrapInfo);

						if(first == null){
							first = wrapInfo;
						}
						first.addRelativeSplitInfo(wrapInfo);
					}
				}
				else{
					// 分割なし
					list.add(info);
				}
			}
		}
//		for(int i=0; i<mList.size(); i++){
//			IImageFileInfo info = mList.get(i);
//			ImageFilterParam param = info.getFilterParam();
//			if(info instanceof ImageFileInfoSplitWrapper){
//				ImageFileInfoSplitWrapper infow = (ImageFileInfoSplitWrapper)info;
//				if(param.getSplitIndex() == 0){
//					int pageType = param.getSplitType();
//					IImageFileInfo baseInfo = infow.getBaseFileInfo();
//					baseInfo.getFilterParam().setSplitType(pageType);
//					if(pageType == Constant.SPLITTYPE_NONE){
//						list.add(baseInfo);
//					}
//					else{
//						list.add(new ImageFileInfoSplitWrapper(baseInfo, 0));
//						list.add(new ImageFileInfoSplitWrapper(baseInfo, 1));
//					}
//				}
//			}else{
//				if(param.getSplitType() != Constant.SPLITTYPE_NONE){
//					
//					list.add(new ImageFileInfoSplitWrapper(info, 0));
//					list.add(new ImageFileInfoSplitWrapper(info, 1));
//				}
//				else{
//					list.add(info);
//				}
//			}
//		}
		
		return list;
	}

	protected String getSuffix(String fileName) {
	    if (fileName == null)
	        return null;
	    fileName = fileName.toLowerCase();
	    int point = fileName.lastIndexOf(".");
	    if (point != -1) {
	        return fileName.substring(point + 1);
	    }
	    return fileName;
	}
	
	@Override
	public int size() {
		return mList.size();
	}

	@Override
	public IImageFileInfo get(int index) {
		return mList.get(index);
	}

	@Override
	public IImageFileInfo remove(int index) {
		return mList.remove(index);
	}
	
	@Override
	public void clear(){
		mList.clear();
	}

	@Override
	public boolean add(IImageFileInfo info) {
		return mList.add(info);
	}

}
