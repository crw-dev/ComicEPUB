package jp.crwdev.app.container.epub;

import jp.crwdev.app.container.ImageFileInfoList;
import jp.crwdev.app.container.folder.FolderImageFileInfoList;
import jp.crwdev.app.interfaces.IImageFileInfoList;

public class CopyImageFileInfoList extends ImageFileInfoList {

	
	public CopyImageFileInfoList(IImageFileInfoList src){
		super();
		
		int count = src.size();
		for(int i=0; i<count; i++){
			this.add(src.get(i));
		}
		this.setEnableSort(src.isEnableSort());
	}
	
	public CopyImageFileInfoList(){
		
	}
	
	@Override
	public IImageFileInfoList renew() {
		CopyImageFileInfoList list = new CopyImageFileInfoList();
		
		return renewInternal(list);
	}

}
