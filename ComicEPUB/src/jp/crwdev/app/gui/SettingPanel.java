package jp.crwdev.app.gui;


import javax.swing.JPanel;
import javax.swing.SpringLayout;

import jp.crwdev.app.EventObserver;
import jp.crwdev.app.OutputSettingParam;
import jp.crwdev.app.EventObserver.OnEventListener;
import jp.crwdev.app.imagefilter.ImageFilterParam;

public class SettingPanel extends JPanel implements OnEventListener {

	private SettingComponent mComponent = new SettingComponent(this);

	public SettingPanel(){
		SpringLayout layout = new SpringLayout();
		setLayout(layout);

		mComponent.applyLayout(layout);

		mComponent.setValues();
		

	}
	
	private EventObserver mEventSender = null;
	public void setEventObserver(EventObserver observer){
		mEventSender = observer;
	}
	

	public void onUpdateSetting(ImageFilterParam param){
		mEventSender.sendEvent(EventObserver.EventTarget_Main, EventObserver.EventType_UpdateFilterParam, param);
	}
	
	public void startConvert(){
		mEventSender.sendEvent(EventObserver.EventTarget_Main, EventObserver.EventType_BeginConvert, 0);
	}
	
	public void cancelConvert(){
		mEventSender.sendEvent(EventObserver.EventTarget_Main, EventObserver.EventType_CancelConvert, 0);
	}
	
	public ImageFilterParam getFilterParam(){
		return mComponent.getCurrentFilterParam();
	}
	
	public OutputSettingParam getOutputSettingParam(){
		return mComponent.getOutputSettingParam();
	}
	
	public boolean isCheckStatus(int checkboxId){
		return mComponent.isCheckStatus(checkboxId);
	}

	private void startProgress(){
		mComponent.startProgress();
	}
	
	private void stopProgress(){
		mComponent.stopProgress();
	}
	
	@Override
	public void onEventReceived(int type, int arg1, int arg2, Object obj) {

		switch(type){
		case EventObserver.EventType_UpdateFilterParam:
			mComponent.applyFilterParam((ImageFilterParam)obj);
			break;
		case EventObserver.EventType_Progress:
			if(arg1 != 0){
				startProgress();
			}else{
				stopProgress();
			}
			break;
		case EventObserver.EventType_UpdateOutputParam:
			mComponent.applyOutputParam((OutputSettingParam)obj);
			break;
		case EventObserver.EventType_ProgressMessage:
			mComponent.setProgressMessage((String)obj);
			break;
		case EventObserver.EventType_FinishConvert:
			mComponent.onFinishConvert();
			break;
		default:
			break;
		}
	}
	
}
