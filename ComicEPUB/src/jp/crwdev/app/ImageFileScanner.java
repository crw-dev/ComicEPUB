package jp.crwdev.app;

import java.util.ArrayList;
import java.util.List;

import jp.crwdev.app.container.folder.FolderImageFileScanner;
import jp.crwdev.app.container.rar.RarFileScanner;
import jp.crwdev.app.container.zip.ZipFileScanner;
import jp.crwdev.app.interfaces.IImageFileInfoList;
import jp.crwdev.app.interfaces.IImageFileScanner;

public class ImageFileScanner implements IImageFileScanner {
	
	private static ImageFileScanner mInstance = null;
	private static IImageFileScanner mCurrentScanner = null;
	private static List<IImageFileScanner> mScanners = null;
	
	public static IImageFileScanner getFileScanner(String filepath){
		if(mInstance == null){
			mInstance = new ImageFileScanner();
		}
		if(ImageFileScanner.mScanners == null){
			return null;
		}
		for(IImageFileScanner scanner : ImageFileScanner.mScanners){
			if(scanner.open(filepath)){
				mCurrentScanner = scanner;
				return scanner;
			}
		}
		return null;
	}
	
	protected ImageFileScanner(){
		mScanners = new ArrayList<IImageFileScanner>();
		mScanners.add(new FolderImageFileScanner());
		mScanners.add(new ZipFileScanner());
		mScanners.add(new RarFileScanner());
	}

	@Override
	public void close() {
		// NOP
	}

	@Override
	public String getOpenFilePath(){
		return mCurrentScanner.getOpenFilePath();
	}
	
	@Override
	public IImageFileInfoList getImageFileInfoList() {
		// NOP
		return null;
	}

	@Override
	public boolean open(String path) {
		// NOP
		return false;
	}

}
