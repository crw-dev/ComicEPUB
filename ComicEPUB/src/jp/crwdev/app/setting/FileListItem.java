package jp.crwdev.app.setting;

import java.io.Serializable;

@SuppressWarnings("serial")
public class FileListItem implements Serializable {

	private String mName;
	private String mPath;
	
	public FileListItem(String name, String path){
		this.mName = name;
		this.mPath = path;
	}
	
	public String getName(){
		return this.mName;
	}
	
	public String getPath(){
		return this.mPath;
	}
}
