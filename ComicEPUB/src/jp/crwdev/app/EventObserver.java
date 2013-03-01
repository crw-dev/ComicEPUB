/**
 * 各コンポーネント間でのイベント通知
 */
package jp.crwdev.app;

public class EventObserver {
	
	public interface OnEventListener {
		void onEventReceived(int type, int arg1, int arg2, Object obj);
	}

	// Event Target
	public static final int EventTarget_Table = 0;
	public static final int EventTarget_Panel = 1;
	public static final int EventTarget_Setting = 2;
	public static final int EventTarget_Main = 3;
	
	// Event Type
	public static final int EventType_UpdateFileInfo = 0;	// arg1: rowIndex
	public static final int EventType_UpdateFilterParamSet = 1;	// obj: ImageFilterParamSet
	public static final int EventType_MoveInfo = 2;		// arg1: offset
	public static final int EventType_BeginConvert = 3;	// no param
	public static final int EventType_Progress = 4;		// arg1: 0=stop other=start
	public static final int EventType_UpdateOutputParam = 5;	// obj: OutputSettingParam
	public static final int EventType_ProgressMessage = 6;		// obj: String
	public static final int EventType_FinishConvert = 7;		// no param
	public static final int EventType_CancelConvert = 8;		// no param
	public static final int EventType_UpdateFilterParam = 9;	// arg1: filterIndex, obj: ImageFilterParam
	public static final int EventType_UpdateFilterParamOnlyEnable = 10;	// arg1: filterIndex, obj: ImageFilterParam
	public static final int EventType_FileInfoModified = 11;	// no param
	public static final int EventType_RenewalList = 12;			// no param
	public static final int EventType_SelectTab = 13;			// arg1: tab index
	public static final int EventType_ModifiedSetting = 14;		// no param
	public static final int EventType_PreviewSize = 15;			// arg1: 1:true, 0:false, obj: Dimension

	/** イベントリスナ 4種類  */
	private OnEventListener[] mListeners = new OnEventListener[4];
	
	/**
	 * コンストラクタ
	 */
	public EventObserver(){
		
	}

	/** プログレス開始 */
	public void startProgress(){
		sendEvent(EventObserver.EventTarget_Setting, EventObserver.EventType_Progress, 1);
	}
	/** プログレス終了 */
	public void stopProgress(){
		sendEvent(EventObserver.EventTarget_Setting, EventObserver.EventType_Progress, 0);
	}
	
	/** プログレスメッセージ */
	public void setProgressMessage(String message){
		sendEvent(EventObserver.EventTarget_Setting, EventObserver.EventType_ProgressMessage, message);
	}
	
	/** 編集フラグON */
	public void setModified(){
		sendEvent(EventObserver.EventTarget_Main, EventObserver.EventType_ModifiedSetting, 0);
	}

	/**
	 * イベントリスナ登録
	 * @param target　EventTarget ID
	 * @param listener
	 */
	public void setEventListener(int target, OnEventListener listener){
		if(0 <= target && target < mListeners.length){
			mListeners[target] = listener;
		}
	}
	
	/**
	 * イベント通知
	 * @param target 通知先
	 * @param type イベント種別
	 * @param arg1 引数１
	 */
	public void sendEvent(int target, int type, int arg1){
		sendEvent(target, type, arg1, 0, null);
	}
	
	/**
	 * イベント通知
	 * @param target 通知先
	 * @param type イベント種別
	 * @param arg1 引数１
	 * @param arg2 引数２
	 */
	public void sendEvent(int target, int type, int arg1, int arg2){
		sendEvent(target, type, arg1, arg2, null);
	}
	
	/**
	 * イベント通知
	 * @param target 通知先
	 * @param type イベント種別
	 * @param obj オブジェクト型引数
	 */
	public void sendEvent(int target, int type, Object obj){
		sendEvent(target, type, 0, 0, obj);
	}
	
	/**
	 * イベント通知
	 * @param target 通知先
	 * @param type イベント種別
	 * @param arg1 引数１
	 * @param obj オブジェクト型引数
	 */
	public synchronized void sendEvent(int target, int type, int arg1, Object obj){
		if(0 <= target && target < mListeners.length){
			mListeners[target].onEventReceived(type, arg1, 0, obj);
		}
	}
	
	/**
	 * イベント通知
	 * @param target 通知先
	 * @param type イベント種別
	 * @param arg1 引数１
	 * @param arg2 引数２
	 * @param obj オブジェクト型引数
	 */
	public synchronized void sendEvent(int target, int type, int arg1, int arg2, Object obj){
		if(0 <= target && target < mListeners.length){
			mListeners[target].onEventReceived(type, arg1, arg2, obj);
		}
	}
	
}
