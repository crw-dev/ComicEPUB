/**
 * INIファイル
 */
package jp.crwdev.app.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


public class InifileProperty {

	private static InifileProperty mInstance = null;
	
	private static final String INIFILE_NAME = "default.ini";
	
	private static final String PROP_OUTPUTDIR = "outputFolder";
	private static final String PROP_IMAGESIZE = "imageSize";
	private static final String PROP_JPEG_QUALITY = "jpegQuality";
	private static final String PROP_INSERT_BLANKPAGE = "insertBlankPage";
	private static final String PROP_ENABLE_FULLSCREEN = "enableFullScreen";
	private static final String PROP_GHOSTSCRIPT = "ghostScriptPath";
	private static final String PROP_DEBUGWINDOW = "debugWindow";
	
	private boolean mIsModified = false;
	
	
	/**
	 * Singletonインスタンス
	 * @return
	 */
	public static InifileProperty getInstance(){
		if(mInstance == null){
			mInstance = new InifileProperty();
		}
		return mInstance;
	}
	
	private Properties mProp = null;
	
	protected InifileProperty(){
		mProp = new Properties();
		
		InputStreamReader istream = null;
		try {
			File file = new File(INIFILE_NAME);
			if(file.exists()){
				istream = new InputStreamReader(new FileInputStream(INIFILE_NAME), "UTF-8");
				mProp.load(istream);
				istream.close();
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(istream != null){
				try {
					istream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		setDefaultProperties();
	}
	
    public void save(){
    	if(!mIsModified){
    		return;
    	}
    	
		FileOutputStream fos = null;
    	try {
    		fos = new FileOutputStream(INIFILE_NAME);
    		OutputStreamWriter writer = new OutputStreamWriter(fos, "UTF-8");
    		
			mProp.store(writer, null);
			
			writer.flush();
    	} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	finally{
    		if(fos != null){
    			try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
    		}
    	}
    }
    
	private void setDefaultProperties(){
		String imageSizes = mProp.getProperty(PROP_IMAGESIZE);
		if(imageSizes == null || imageSizes.isEmpty()){
			imageSizes = "600x800,758x1024,800x1200";
			mProp.setProperty(PROP_IMAGESIZE, imageSizes);
			mIsModified = true;
		}
		
		boolean exist = false;
		String jpegQuality = mProp.getProperty(PROP_JPEG_QUALITY);
		if(jpegQuality == null || jpegQuality.isEmpty()){
			
		}else{
			try {
				Float floatValue = Float.parseFloat(jpegQuality);
				if(floatValue < 0.0f || 1.0f < floatValue){
					exist = false;
				} else {
					exist = true;
				}
			}catch(NumberFormatException e){
			}
		}
		if(!exist){
			mProp.setProperty(PROP_JPEG_QUALITY, "0.8");
			mIsModified = true;
		}
		
		exist = false;
		String insertBlank = mProp.getProperty(PROP_INSERT_BLANKPAGE);
		if(insertBlank == null || insertBlank.isEmpty()){
		}else{
			if(!insertBlank.equalsIgnoreCase("false") && !insertBlank.equalsIgnoreCase("true")){
				
			}else{
				exist = true;
			}
		}
		if(!exist){
			mProp.setProperty(PROP_INSERT_BLANKPAGE, "false");
			mIsModified = true;
		}
		
		exist = false;
		String enableFullScreen = mProp.getProperty(PROP_ENABLE_FULLSCREEN);
		if(enableFullScreen == null || enableFullScreen.isEmpty()){
		}else{
			if(!enableFullScreen.equalsIgnoreCase("false") && !enableFullScreen.equalsIgnoreCase("true")){
				
			}else{
				exist = true;
			}
		}
		if(!exist){
			mProp.setProperty(PROP_ENABLE_FULLSCREEN, "true");
			mIsModified = true;
		}
		
		String ghostScriptPath = mProp.getProperty(PROP_GHOSTSCRIPT);
		if(ghostScriptPath == null || ghostScriptPath.isEmpty()){
			ghostScriptPath = "";
			mProp.setProperty(PROP_GHOSTSCRIPT, "");
			mIsModified = true;
		}
		
	}
	
	public String getOutputFolder(){
		return mProp.getProperty(PROP_OUTPUTDIR, "");
	}
	
	public void setOutputFolder(String path){
		if(path == null){
			path = "";
		}
		mProp.setProperty(PROP_OUTPUTDIR, path);
		mIsModified = true;
	}
	
	public boolean isShowDebugWindow(){
		String value = mProp.getProperty(PROP_DEBUGWINDOW, "false");
		if(value.compareToIgnoreCase("true") == 0){
			return true;
		}else{
			return false;
		}
	}
	
	public List<String> getImageSizeList(){
		List<String> list = new ArrayList<String>();
		
		boolean hasError = false;
		
		String sizes = mProp.getProperty(PROP_IMAGESIZE);
		String[] array = sizes.split(",");
		for(int i=0; i<array.length; i++){
			String value = array[i];
			String[] tmp = value.split("x");
			if(tmp.length == 2){
				try{
					int w = Integer.parseInt(tmp[0]);
					int h = Integer.parseInt(tmp[1]);
					list.add(w + "x" + h);
				}catch(NumberFormatException e){
					hasError = true;
				}
			}
		}
		if(hasError){
			StringBuilder sb = new StringBuilder();
			for(int i=0; i<list.size(); i++){
				if(i==0){
					sb.append(list.get(i));
				}else{
					sb.append("," + list.get(i));
				}
			}
			mProp.setProperty(PROP_IMAGESIZE, new String(sb));
			mIsModified = true;
		}
		
		return list;
	}
	
	public float getJpegQuality(){
		return Float.parseFloat(mProp.getProperty(PROP_JPEG_QUALITY));
	}
	
	public boolean isInsertBlankPage(){
		String value = mProp.getProperty(PROP_INSERT_BLANKPAGE, "false");
		if(value.equalsIgnoreCase("false")){
			return false;
		}else{
			return true;
		}
	}
	
	public boolean isEnableFullScreen(){
		String value = mProp.getProperty(PROP_ENABLE_FULLSCREEN, "true");
		if(value.equalsIgnoreCase("false")){
			return false;
		}else{
			return true;
		}
	}
	
	public String getGhostScriptPath(){
		return mProp.getProperty(PROP_GHOSTSCRIPT);
	}

	public void setGhostScriptPath(String path){
		if(path == null){
			path = "";
		}
		mProp.setProperty(PROP_GHOSTSCRIPT, path);
		mIsModified = true;
	}

}
