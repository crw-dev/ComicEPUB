package jp.crwdev.app.constant;

public class Constant {

	public static final int PAGETYPE_AUTO = 0;
	public static final int PAGETYPE_TEXT = 1;
	public static final int PAGETYPE_PICT = 2;
	
	public static final String TEXT_PAGETYPE_AUTO = "";
	public static final String TEXT_PAGETYPE_TEXT = "本文";
	public static final String TEXT_PAGETYPE_PICT = "挿絵";

	public static final int SPLITTYPE_NONE = 0;
	public static final int SPLITTYPE_RIGHT_TO_LEFT = 1;
	public static final int SPLITTYPE_LEFT_TO_RIGHT = 2;
	
	public static final String TEXT_SPLITTYPE_NONE = "";
	public static final String TEXT_SPLITTYPE_RIGHT_TO_LEFT = "右→左";
	public static final String TEXT_SPLITTYPE_LEFT_TO_RIGHT = "左→右";
	
	
	public static final String TABLE_HEADER_ENTRYNAME = "ファイル名";
	public static final String TABLE_HEADER_PAGETYPE = "種別";
	public static final String TABLE_HEADER_ROTATE = "回転";
	public static final String TABLE_HEADER_POSITION = "座標";
	public static final String TABLE_HEADER_WIDTH = "幅";
	public static final String TABLE_HEADER_HEIGHT = "高さ";
	//public static final String TABLE_HEADER_SIZE = "サイズ";
	public static final String TABLE_HEADER_SPLITTYPE = "分割";
	
	public static final int TABLE_COLUMN_ENTRYNAME = 0;
	public static final int TABLE_COLUMN_PAGETYPE = 1;
	public static final int TABLE_COLUMN_ROTATE = 2;
	public static final int TABLE_COLUMN_POSITION = 3;
	public static final int TABLE_COLUMN_WIDTH = 4;
	public static final int TABLE_COLUMN_HEIGHT = 5;
	//public static final int TABLE_COLUMN_SIZE = 6;
	public static final int TABLE_COLUMN_SPLITTYPE = 6;
	
	public static final String[] TABLE_HEADER_COLUMNS = new String[]{
		TABLE_HEADER_ENTRYNAME,
		TABLE_HEADER_PAGETYPE,
		TABLE_HEADER_ROTATE,
		TABLE_HEADER_POSITION,
		TABLE_HEADER_WIDTH,
		TABLE_HEADER_HEIGHT,
		//TABLE_HEADER_SIZE,
		TABLE_HEADER_SPLITTYPE,
	};
	
	
	public static String getPageTypeText(int pageType){
		switch(pageType){
		case PAGETYPE_TEXT:
			return TEXT_PAGETYPE_TEXT;
		case PAGETYPE_PICT:
			return TEXT_PAGETYPE_PICT;
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
		return PAGETYPE_AUTO;
	}
	
	public static String getSplitTypeText(int splitType){
		switch(splitType){
		case SPLITTYPE_RIGHT_TO_LEFT:
			return TEXT_SPLITTYPE_RIGHT_TO_LEFT;
		case SPLITTYPE_LEFT_TO_RIGHT:
			return TEXT_SPLITTYPE_LEFT_TO_RIGHT;
		case SPLITTYPE_NONE:
		default:
			return TEXT_SPLITTYPE_NONE;
		}
	}
	public static int getSplitType(String splitType){
		if(splitType.equals(TEXT_SPLITTYPE_RIGHT_TO_LEFT)){
			return SPLITTYPE_RIGHT_TO_LEFT;
		}
		if(splitType.equals(TEXT_SPLITTYPE_LEFT_TO_RIGHT)){
			return SPLITTYPE_LEFT_TO_RIGHT;
		}
		return SPLITTYPE_NONE;
	}
}
