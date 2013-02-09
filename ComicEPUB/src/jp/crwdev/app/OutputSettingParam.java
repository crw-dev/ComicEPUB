/**
 * 出力設定パラメータ
 */
package jp.crwdev.app;

import java.awt.Dimension;

import jp.crwdev.app.container.folder.FolderImageFileWriter;
import jp.crwdev.app.container.zip.ZipImageFileWriter;
import jp.crwdev.app.interfaces.IImageFileWriter;

public class OutputSettingParam {

	/** 出力フォルダ */
	private String mOutputPath;
	/** 出力ファイル名 */
	private String mOutputFileName;
	/** 出力ファイルタイプ */
	private String mOutputFileType;
	/** EPUB種別 */
	private String mOutputEpubType;
	/** 出力イメージサイズ */
	private String mOutputImageSize;

	/**
	 * コンストラクタ
	 * @param path
	 * @param filename
	 * @param fileType
	 * @param epubType
	 * @param imageSize
	 */
	public OutputSettingParam(String path, String filename, String fileType, String epubType, String imageSize){
		mOutputPath = path;
		mOutputFileName = filename;
		mOutputFileType = fileType;
		mOutputEpubType = epubType;
		mOutputImageSize = imageSize;
	}
	
	/**
	 * 出力先フォルダパス取得
	 * @return
	 */
	public String getOutputPath(){
		return mOutputPath;
	}
	
	/**
	 * 出力ファイル名取得
	 * @return
	 */
	public String getOutputFileName(){
		return mOutputFileName + getSuffixByFileType();
	}
	
	/**
	 * FileWriterを取得
	 * @return
	 */
	public IImageFileWriter getImageFileWriter(){
		if(mOutputFileType.equalsIgnoreCase("zip")){
			return new ZipImageFileWriter();
		}
		else if(mOutputFileType.equalsIgnoreCase("folder")){
			return new FolderImageFileWriter();
		}
		else if(mOutputFileType.equalsIgnoreCase("epub")){
			return null;
		}
		return null;
	}
	
	/**
	 * EPUBのBook typeを取得
	 * @return "book", "magazine", "comic"
	 */
	public String getEpubType(){
		return mOutputEpubType.toLowerCase();
	}
	
	/**
	 * 出力画像サイズを取得
	 * @return nullの場合はリサイズしない
	 */
	public Dimension getImageSize(){
		String[] val = mOutputImageSize.split("x");
		if(val.length == 2){
			try{
				int width = Integer.parseInt(val[0]);
				int height = Integer.parseInt(val[1]);
				return new Dimension(width, height);
			}
			catch(NumberFormatException ex){
				return null;
			}
		}
		return null;
	}
	
	/**
	 * ファイルタイプから拡張子を求める
	 * @return
	 */
	private String getSuffixByFileType(){
		if(mOutputFileType.equalsIgnoreCase("zip")){
			return ".zip";
		}
		else if(mOutputFileType.equalsIgnoreCase("epub")){
			return ".epub";
		}
		else{
			return "";
		}
	}
}
