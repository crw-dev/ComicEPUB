package jp.crwdev.app.gui;


import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SpringLayout;

import jp.crwdev.app.EventObserver;
import jp.crwdev.app.OutputSettingParam;
import jp.crwdev.app.EventObserver.OnEventListener;
import jp.crwdev.app.imagefilter.ImageFilterParam;
import jp.crwdev.app.setting.ImageFilterParamSet;

public class SettingPanel extends SettingPanelLayout implements OnEventListener {

	//private SettingComponent mComponent = new SettingComponent(this);
	
	private PreviewSettingPanel mPreviewSetting = new PreviewSettingPanel();
	private JTabbedPane mSettingTab;
	private SettingTabPanel[] mSettingTabPanels = new SettingTabPanel[4];
	private OutputSettingPanel mOutputSetting = new OutputSettingPanel();
	private ProgressPanel mProgressPanel = new ProgressPanel();

	public SettingPanel(JFrame parent){
		super(parent);
		
		mPreviewSetting.setComponents(this, checkPreview, checkSimpleZoom, checkResize, checkOutputResize, fullscreenButton);
		
		mSettingTab = this.tabbedPane;
		
		for(int i=0; i<4; i++){
			mSettingTabPanels[i] = new SettingTabPanel(this, i);
			mSettingTab.addTab(mSettingTabPanels[i].getTabTitle(), mSettingTabPanels[i]);
		}

		
		mOutputSetting.setComponents(this, outputImageSize, outputFileType, outputBookType,
										textSeriesTitle, textSeriesTitleKana, textSeriesNumber,
										textTitle, textTitleKana, textAuthor, textAuthorKana,
										outputFolder, outpuFolderButton, convertButton, cancelButton,
										checkOutputResize, packageConvertButton, checkFixedSize, parent);
		mProgressPanel.setComponents(this, labelMessage, progressBar, cancelButton);
		
//		//SpringLayout layout = new SpringLayout();
//		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
//		//setLayout(layout);
//
//		//mComponent.applyLayout(layout);
//
//		//mComponent.setValues();
//		
//		for(int i=0; i<4; i++){
//			mSettingTabPanels[i] = new SettingTabPanel(this, i);
//			mSettingTab.addTab(mSettingTabPanels[i].getTabTitle(), mSettingTabPanels[i]);
//		}
//		
//		mSettingTab.setPreferredSize(new Dimension(250, 300));
//		
//		add(mPreviewSetting);
//		add(mSettingTab);
//		add(Box.createVerticalStrut(10));
//		add(mOutputSetting);
//		add(mProgressPanel);
		
		
	}
	
	
	public ImageFilterParamSet getImageFilterParamSet(){
		//Preview
		boolean preview = mPreviewSetting.isPreview();
		boolean resize = mPreviewSetting.isResize();
		boolean simpleZoom = mPreviewSetting.isSimpleZoom();
		
		//Filter
		ImageFilterParamSet params = new ImageFilterParamSet();
		for(int i=0; i<4; i++){
			params.set(i, mSettingTabPanels[i].getImageFilterParam());
		}
		params.setPreview(preview);
		params.setResize(resize);
		params.setSimpleZoom(simpleZoom);
		
		return params;
	}
	
	private EventObserver mEventSender = null;
	public void setEventObserver(EventObserver observer){
		mEventSender = observer;
	}
	

	public void onUpdateSetting(ImageFilterParamSet params){
		mEventSender.sendEvent(EventObserver.EventTarget_Main, EventObserver.EventType_UpdateFilterParamSet, params);
	}
	
	public void startConvert(){
		mEventSender.sendEvent(EventObserver.EventTarget_Main, EventObserver.EventType_BeginConvert, 0);
	}
	
	public void cancelConvert(){
		mEventSender.sendEvent(EventObserver.EventTarget_Main, EventObserver.EventType_CancelConvert, 0);
	}
	
	public OutputSettingParam getOutputSettingParam(){
		return mOutputSetting.getOutputSettingParam();
	}
	
	public void beginFullscreen(){
		mEventSender.sendEvent(EventObserver.EventTarget_Main, EventObserver.EventType_BeginFullscreen, 0);
	}
	
//	public boolean isCheckStatus(int checkboxId){
//		return mComponent.isCheckStatus(checkboxId);
//	}

	private void startProgress(){
		mProgressPanel.startProgress();
	}
	
	private void stopProgress(){
		mProgressPanel.stopProgress();
	}
	
	private void applyFilterParamSet(ImageFilterParamSet params, boolean enableOnly){
		//TODO:
		ImageFilterParam baseParam = params.get(0);
		if(baseParam != null){
			mPreviewSetting.setPreview(baseParam.isPreview());
			mPreviewSetting.setResize(baseParam.isResize());
		}
		for(int i=0; i<4; i++){
			applyFilterParam(i, params.get(i), enableOnly);
		}
	}
	
	private void applyFilterParam(int filterIndex, ImageFilterParam param, boolean enableOnly){
		//TODO:
		mSettingTabPanels[filterIndex].setImageFilterParam(param, enableOnly);
	}
	
	private void onFileInfoModified(){
		//TODO:
		boolean isUnificationTextPage = mSettingTabPanels[ImageFilterParamSet.FILTER_INDEX_TEXT].isUnification();
		if(isUnificationTextPage){
			updateSettingValues();
		}
	}
	
	public boolean isOutputResize(){
		return mPreviewSetting.isOutputResize();
	}
	
	public Dimension getOutputImageSize(){
		return mOutputSetting.getOutputImageSize();
	}

	/**
	 * 設定に変更が有った場合、メインに通知する
	 */
	public void updateSettingValues(){
		onUpdateSetting(getImageFilterParamSet());
	}
	
	public void saveSettingFileRequest(){
		mEventSender.sendEvent(EventObserver.EventTarget_Main, EventObserver.EventType_RequestSaveSetting, 0);
	}
	
	@Override
	public void onEventReceived(int type, int arg1, int arg2, Object obj) {

		switch(type){
		case EventObserver.EventType_UpdateFilterParamSet:
			applyFilterParamSet((ImageFilterParamSet)obj, false);
			break;
		case EventObserver.EventType_UpdateFilterParam:
			applyFilterParam(arg1, (ImageFilterParam)obj, false);
			break;
		case EventObserver.EventType_UpdateFilterParamOnlyEnable:
			applyFilterParam(arg1, (ImageFilterParam)obj, true);
			break;
		case EventObserver.EventType_Progress:
			if(arg1 != 0){
				startProgress();
			}else{
				stopProgress();
			}
			break;
		case EventObserver.EventType_UpdateOutputParam:
			mOutputSetting.applyOutputParam((OutputSettingParam)obj);
			break;
		case EventObserver.EventType_ProgressMessage:
			mProgressPanel.setProgressMessage((String)obj);
			break;
		case EventObserver.EventType_FinishConvert:
			mOutputSetting.onFinishConvert();
			mProgressPanel.onFinishConvert();
			break;
		case EventObserver.EventType_FileInfoModified:
			onFileInfoModified();
			break;
		case EventObserver.EventType_SelectTab:
			if(0 <= arg1 && arg1 < mSettingTab.getTabCount()){
				mSettingTab.setSelectedIndex(arg1);
			}
			break;
		default:
			break;
		}
	}
	
}
