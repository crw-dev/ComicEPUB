package jp.crwdev.app.constant;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jp.crwdev.app.imagefilter.ImageFilterParam;
import jp.crwdev.app.imagefilter.SplitFilter;
import jp.crwdev.app.interfaces.IImageFileInfo;

public class Constant {

	public static final List<String> SUPPORT_INPUT_PREFIX = new ArrayList<String>();
	static{
		SUPPORT_INPUT_PREFIX.add("zip");
		SUPPORT_INPUT_PREFIX.add("rar");
		SUPPORT_INPUT_PREFIX.add("pdf");
	}
	
	public static final List<String> SUPPORT_IMAGE_PREFIX = new ArrayList<String>();
	static{
		SUPPORT_IMAGE_PREFIX.add("jpg");
		SUPPORT_IMAGE_PREFIX.add("png");
		SUPPORT_IMAGE_PREFIX.add("bmp");
	}
	
	public static float jpegQuality = 0.8f;

	public static final int PAGETYPE_AUTO = 0;
	public static final int PAGETYPE_TEXT = 1;
	public static final int PAGETYPE_PICT = 2;
	public static final int PAGETYPE_COLOR = 3;
	
	public static final String TEXT_PAGETYPE_AUTO = "";
	public static final String TEXT_PAGETYPE_TEXT = "本文";
	public static final String TEXT_PAGETYPE_PICT = "挿絵";
	public static final String TEXT_PAGETYPE_COLOR = "カラー";

	//public static final int SPLITTYPE_NONE = 0;
	//public static final int SPLITTYPE_RIGHT_TO_LEFT = 1;
	//public static final int SPLITTYPE_LEFT_TO_RIGHT = 2;
	
	public static final String TEXT_SPLITTYPE_NONE = "";
	public static final String TEXT_SPLITTYPE_R2L = "右→左";
	public static final String TEXT_SPLITTYPE_L2R = "左→右";
	public static final String TEXT_SPLITTYPE_R2L_2x2 = "逆Ｚ 2x2";
	public static final String TEXT_SPLITTYPE_L2R_2x2 = "順Ｚ 2x2";
	public static final String TEXT_SPLITTYPE_R2L_3x3 = "逆Ｚ 3x3";
	public static final String TEXT_SPLITTYPE_L2R_3x3 = "順Ｚ 3x3";
	public static final String TEXT_SPLITTYPE_CUSTOM = "カスタム";
	
	public static final String PAGESPREAD_AUTO = "";
	public static final String PAGESPREAD_LEFT = "left";
	public static final String PAGESPREAD_RIGHT = "right";
	public static final String PAGESPREAD_CENTER = "center";
	
	public static final String TABLE_HEADER_ENTRYNAME = "ファイル名";
	public static final String TABLE_HEADER_TOC = "目次";
	public static final String TABLE_HEADER_PAGETYPE = "種別";
	public static final String TABLE_HEADER_PAGESPREAD = "配置";
	public static final String TABLE_HEADER_ROTATE = "回転";
	public static final String TABLE_HEADER_POSITION = "座標";
	public static final String TABLE_HEADER_WIDTH = "幅";
	public static final String TABLE_HEADER_HEIGHT = "高さ";
	//public static final String TABLE_HEADER_SIZE = "サイズ";
	public static final String TABLE_HEADER_SPLITTYPE = "分割";
	
	public static final int TABLE_COLUMN_ENTRYNAME = 0;
	public static final int TABLE_COLUMN_PAGETYPE = 1;
	public static final int TABLE_COLUMN_PAGESPREAD = 2;
	public static final int TABLE_COLUMN_SPLITTYPE = 3;
	public static final int TABLE_COLUMN_TOC = 4;
	public static final int TABLE_COLUMN_ROTATE = 5;
	public static final int TABLE_COLUMN_POSITION = 6;
	public static final int TABLE_COLUMN_WIDTH = 7;
	public static final int TABLE_COLUMN_HEIGHT = 8;
	//public static final int TABLE_COLUMN_SIZE = 6;
	
	public static final String[] TABLE_HEADER_COLUMNS = new String[]{
		TABLE_HEADER_ENTRYNAME,
		TABLE_HEADER_PAGETYPE,
		TABLE_HEADER_PAGESPREAD,
		TABLE_HEADER_SPLITTYPE,
		TABLE_HEADER_TOC,
		TABLE_HEADER_ROTATE,
		TABLE_HEADER_POSITION,
		TABLE_HEADER_WIDTH,
		TABLE_HEADER_HEIGHT,
	};
	
	public static final String TABLE_HEADER_FILELIST_FILENAME = "ファイル/フォルダ名";
	public static final String TABLE_HEADER_FILELIST_FILEPATH = "パス";
	
	public static final int TABLE_HEADER_FILELIST_COLUMN_FILENAME = 0;
	public static final int TABLE_HEADER_FILELIST_COLUMN_FILEPATH = 1;
	
	public static final String[] TABLE_HEADER_FILELIST_COLUMNS = new String[]{
		TABLE_HEADER_FILELIST_FILENAME,
		TABLE_HEADER_FILELIST_FILEPATH,
	};
	
	
	public static String getPageTypeText(int pageType){
		switch(pageType){
		case PAGETYPE_TEXT:
			return TEXT_PAGETYPE_TEXT;
		case PAGETYPE_PICT:
			return TEXT_PAGETYPE_PICT;
		case PAGETYPE_COLOR:
			return TEXT_PAGETYPE_COLOR;
		case PAGETYPE_AUTO:
		default:
			return TEXT_PAGETYPE_AUTO;
		}
	}
	public static int getPageType(String pageType){
		if(pageType.equals(TEXT_PAGETYPE_TEXT)){
			return PAGETYPE_TEXT;
		}
		if(pageType.equals(TEXT_PAGETYPE_PICT)){
			return PAGETYPE_PICT;
		}
		if(pageType.equals(TEXT_PAGETYPE_COLOR)){
			return PAGETYPE_COLOR;
		}
		return PAGETYPE_AUTO;
	}
	
	public static String getSplitTypeText(int splitType){
		switch(splitType){
		case SplitFilter.TYPE_R2L_2:
			return TEXT_SPLITTYPE_R2L;
		case SplitFilter.TYPE_L2R_2:
			return TEXT_SPLITTYPE_L2R;
		case SplitFilter.TYPE_R2L_2x2:
			return TEXT_SPLITTYPE_R2L_2x2;
		case SplitFilter.TYPE_L2R_2x2:
			return TEXT_SPLITTYPE_L2R_2x2;
		case SplitFilter.TYPE_R2L_3x3:
			return TEXT_SPLITTYPE_R2L_3x3;
		case SplitFilter.TYPE_L2R_3x3:
			return TEXT_SPLITTYPE_L2R_3x3;
		case SplitFilter.TYPE_CUSTOM:
			return TEXT_SPLITTYPE_CUSTOM;
		case SplitFilter.TYPE_NONE:
		default:
			return TEXT_SPLITTYPE_NONE;
		}
	}
	public static int getSplitType(String splitType){
		if(splitType.equals(TEXT_SPLITTYPE_R2L)){
			return SplitFilter.TYPE_R2L_2;
		}
		if(splitType.equals(TEXT_SPLITTYPE_L2R)){
			return SplitFilter.TYPE_L2R_2;
		}
		if(splitType.equals(TEXT_SPLITTYPE_R2L_2x2)){
			return SplitFilter.TYPE_R2L_2x2;
		}
		if(splitType.equals(TEXT_SPLITTYPE_L2R_2x2)){
			return SplitFilter.TYPE_L2R_2x2;
		}
		if(splitType.equals(TEXT_SPLITTYPE_R2L_3x3)){
			return SplitFilter.TYPE_R2L_3x3;
		}
		if(splitType.equals(TEXT_SPLITTYPE_L2R_3x3)){
			return SplitFilter.TYPE_L2R_3x3;
		}
		if(splitType.equals(TEXT_SPLITTYPE_CUSTOM)){
			return SplitFilter.TYPE_CUSTOM;
		}
		return SplitFilter.TYPE_NONE;
	}
	
	public static File getContentFile(File contentPath){
		if(contentPath.isDirectory()){
			return contentPath;
		}
		else{
			String fileName = contentPath.getAbsolutePath();
			int dotIndex = fileName.lastIndexOf(".");
			String suffix = "";
			if(dotIndex >= 0){
				suffix = fileName.substring(dotIndex + 1);
			}
			if(Constant.SUPPORT_INPUT_PREFIX.contains(suffix.toLowerCase())){
				return contentPath;
			}
			else{
				return contentPath.getParentFile();
			}
		}
	}
	public static File getSettingFile(File contentPath){
		if(contentPath.isDirectory()){
			String settingFileName = contentPath.getName() + "_setting.xml";
			File settingFile = new File(contentPath, settingFileName);
			if(settingFile.exists() && !settingFile.isDirectory()){
				return settingFile;
			}
		}
		else{
			String fileName = contentPath.getAbsolutePath();
			int dotIndex = fileName.lastIndexOf(".");
			String suffix = "";
			if(dotIndex >= 0){
				suffix = fileName.substring(dotIndex + 1);
			}
			if(Constant.SUPPORT_INPUT_PREFIX.contains(suffix.toLowerCase())){
				String filenameNoSuffix = fileName;
				if(dotIndex >= 0){
					filenameNoSuffix = fileName.substring(0, dotIndex);
				}
				String settingFileName = filenameNoSuffix + "_setting.xml";
				File settingFile = new File(settingFileName);
				if(settingFile.exists() && !settingFile.isDirectory()){
					return settingFile;
				}
			}else{
				String parent = contentPath.getParent();
				File parentFolder = new File(parent);
				String settingFileName = parentFolder.getName() + "_setting.xml";
				File settingFile = new File(parentFolder, settingFileName);
				if(settingFile.exists() && !settingFile.isDirectory()){
					return settingFile;
				}
			}
		}
		return null;
	}
	
	public static Object[] createRecord(IImageFileInfo info){
		
		ImageFilterParam param = info.getFilterParam();
		String pageType = Constant.getPageTypeText(param.getPageType());
		String rotate = Double.toString(param.getRotateAngle());
		String position = param.getTranslateX() + "," + param.getTranslateY();
		String width = Integer.toString(info.getWidth());
		String height = Integer.toString(info.getHeight());
		//String size = Long.toString(info.getSize());
		String splitType = Constant.getSplitTypeText(param.getSplitType());
		String pageSpread = param.getPageSpread();
		String tocText = info.getTocText();
		
		return new String[]{info.getFileName(), pageType, pageSpread, splitType, tocText, rotate, position, width, height};
	}
}
