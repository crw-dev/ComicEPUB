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
	
	/** タイトル */
	private String mTitle = "";
	/** タイトルカナ */
	private String mTitleKana = "";
	/** 作者名 */
	private String mAuthor = "";
	/** 作者名カナ */
	private String mAuthorKana = "";

	/**
	 * コンストラクタ
	 * @param path
	 * @param filename
	 * @param fileType
	 * @param epubType
	 * @param imageSize
	 */
	public OutputSettingParam(String path, String fileType, String epubType, String imageSize){
		mOutputPath = path;
		mOutputFileType = fileType;
		mOutputEpubType = epubType;
		mOutputImageSize = imageSize;
	}
	
	/**
	 * 出力先フォルダパス設定
	 * @param path
	 */
	public void setOutputPath(String path){
		mOutputPath = path;
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
		if(mOutputFileName != null && !mOutputFileName.isEmpty()){
			return mOutputFileName + getSuffixByFileType();
		}
		else{
			StringBuilder sb = new StringBuilder();
			if(!mAuthor.isEmpty()){
				sb.append("[" + mAuthor + "] ");
			}
			if(!mTitle.isEmpty()){
				sb.append(mTitle);
			}
			if(sb.length() == 0){
				sb.append("NoTitle");
			}
			sb.append(getSuffixByFileType());
			
			return new String(sb);
		}
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
	 * 出力画像サイズを設定
	 * @param width
	 * @param height
	 */
	public void setImageSize(int width, int height){
		if(width <= 0 || height <= 0){
			mOutputImageSize = "リサイズ無し";
		}
		else{
			mOutputImageSize = width + "x" + height;
		}
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
	
	/**
	 * タイトル設定
	 * @param title
	 */
	public void setTitle(String title){
		mTitle = title;
	}
	
	/**
	 * タイトル取得
	 * @return
	 */
	public String getTitle(){
		return mTitle;
	}
	
	/**
	 * タイトルカナ設定
	 * @param title
	 */
	public void setTitleKana(String title){
		mTitleKana = title;
	}
	
	/**
	 * タイトルカナ取得
	 * @return
	 */
	public String getTitleKana(){
		return mTitleKana;
	}
	
	/**
	 * 作者名設定
	 * @param author
	 */
	public void setAuthor(String author){
		mAuthor = author;
	}
	
	/**
	 * 作者名取得
	 * @return
	 */
	public String getAuthor(){
		return mAuthor;
	}
	
	/**
	 * 作者名カナ設定
	 * @param author
	 */
	public void setAuthorKana(String author){
		mAuthorKana = author;
	}
	
	/**
	 * 作者名カナ取得
	 * @return
	 */
	public String getAuthorKana(){
		return mAuthorKana;
	}
}
