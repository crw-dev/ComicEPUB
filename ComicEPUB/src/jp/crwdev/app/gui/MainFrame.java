package jp.crwdev.app.gui;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.dnd.DropTarget;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SpringLayout;

import jp.crwdev.app.EventObserver;
import jp.crwdev.app.OutputSettingParam;
import jp.crwdev.app.EventObserver.OnEventListener;
import jp.crwdev.app.constant.Constant;
import jp.crwdev.app.container.ImageFileInfoList;
import jp.crwdev.app.container.ImageFilePreconverter;
import jp.crwdev.app.container.ImageFileScanner;
import jp.crwdev.app.container.pdf.GhostscriptUtil;
import jp.crwdev.app.imagefilter.AddSpaceFilter;
import jp.crwdev.app.imagefilter.ImageFilterParam;
import jp.crwdev.app.imagefilter.OutputImageFilter;
import jp.crwdev.app.interfaces.IImageFileInfoList;
import jp.crwdev.app.interfaces.IImageFileScanner;
import jp.crwdev.app.interfaces.IImageFileWriter;
import jp.crwdev.app.interfaces.IImageFileWriter.OnProgressListener;
import jp.crwdev.app.setting.ImageFilterParamSet;
import jp.crwdev.app.setting.XmlWriter;
import jp.crwdev.app.util.FileDropTargetAdapter;
import jp.crwdev.app.util.InifileProperty;
import jp.crwdev.app.util.FileDropTargetAdapter.OnDropListener;

public class MainFrame extends JFrame implements OnEventListener {

	private boolean mIsSettingChanged = false;
	
	private IImageFileScanner mCurrentFile = null;
	private IImageFileWriter mFileWriter = null;
	private String mSettingFilePath = null;
	
	private ImageFileInfoTable mTable;
	private ImagePanel mImagePanel;
	private SettingPanel mSettingPanel;
	private ThumbnailView mThumbnailView;
	
	//private ImageFilterParam mBaseFilterParam = new ImageFilterParam();	// global setting
	private ImageFilterParamSet mBaseFilterParams = new ImageFilterParamSet();
	
	//private boolean mIsUnificationTextPage = false;
	
	private EventObserver mEventObserver = new EventObserver();
	private Object mLock = new Object();
	
	public MainFrame(){
		setSize(new Dimension(950,750));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("ComicEPUB");
		
		if(InifileProperty.getInstance().isShowDebugWindow()){
			DebugWindow.initialize();
		}
		
		
		addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e) {
				System.out.println("windowClosing");
				//dispose();
				if(mSettingFilePath != null){
					if(mIsSettingChanged){
						int ret = showSettingSaveConfirmDialog();
						if(ret == JOptionPane.YES_OPTION){
							saveSettingFile(mSettingFilePath);
						}
					}
				}
				InifileProperty.getInstance().save();
				GhostscriptUtil.getInstance().close();
			}
			@Override
			public void windowClosed(WindowEvent e) {
				System.out.println("windowClosed");
				mTable.finalize();
				mImagePanel.finalize();
			}
		});
		
		
		//mIsUnificationTextPage = ImageFilterParam.isUnificationTextPage();
		
		// Table
		ImageFileInfoTable table = new ImageFileInfoTable();
		
		JScrollPane scrollTable = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollTable.setPreferredSize(new Dimension(200, 800));
		
		mTable = table;
		
		mThumbnailView = new ThumbnailView();
		mThumbnailView.setImageFileInfoTable(mTable);

		// ImagePanel
		ImagePanel imagePanel = new ImagePanel();
		imagePanel.setBackground(Color.WHITE);
		//imagePanel.setPreferredSize(new Dimension(600, 800));
		
		mTable.setImagePanel(imagePanel);
		mImagePanel = imagePanel;
		
		// SettingPanel
		SettingPanel settingPanel = new SettingPanel(this);
		settingPanel.setPreferredSize(new Dimension(250, 800));
		//settingPanel.setSize(300,800);
		//settingPanel.setMinimumSize(new Dimension(300, 800));
		mSettingPanel = settingPanel;

		ImageFilterParamSet defaultParam = settingPanel.getImageFilterParamSet();
		setBaseFilterParam(defaultParam);

		
		SpringLayout layout = new SpringLayout();
		setLayout(layout);
		
		Container p = getContentPane();
		
		layout.putConstraint(SpringLayout.NORTH, scrollTable, 3, SpringLayout.NORTH, p);
		layout.putConstraint(SpringLayout.SOUTH, scrollTable, 3, SpringLayout.SOUTH, p);
		layout.putConstraint(SpringLayout.WEST, scrollTable, 3, SpringLayout.WEST, p);
		
		layout.putConstraint(SpringLayout.NORTH, imagePanel, 3, SpringLayout.NORTH, p);
		layout.putConstraint(SpringLayout.SOUTH, imagePanel, 3, SpringLayout.SOUTH, p);
		layout.putConstraint(SpringLayout.WEST, imagePanel, 3, SpringLayout.EAST, scrollTable);
		layout.putConstraint(SpringLayout.EAST, imagePanel, -5, SpringLayout.WEST, settingPanel);
		
		layout.putConstraint(SpringLayout.NORTH, settingPanel, 3, SpringLayout.NORTH, p);
		layout.putConstraint(SpringLayout.SOUTH, settingPanel, -3, SpringLayout.SOUTH, p);
		layout.putConstraint(SpringLayout.EAST, settingPanel, -5, SpringLayout.EAST, p);
		
		table.setEventObserver(mEventObserver);
		imagePanel.setEventObserver(mEventObserver);
		settingPanel.setEventObserver(mEventObserver);
		mEventObserver.setEventListener(EventObserver.EventTarget_Table, table);
		mEventObserver.setEventListener(EventObserver.EventTarget_Panel, imagePanel);
		mEventObserver.setEventListener(EventObserver.EventTarget_Setting, settingPanel);
		mEventObserver.setEventListener(EventObserver.EventTarget_Main, this);
		mEventObserver.setEventListener(EventObserver.EventTarget_Thumbnail, mThumbnailView);
		
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollTable, imagePanel);
		splitPane.setDividerSize(5);
		add(splitPane);
		
		layout.putConstraint(SpringLayout.NORTH, splitPane, 3, SpringLayout.NORTH, p);
		layout.putConstraint(SpringLayout.SOUTH, splitPane, 3, SpringLayout.SOUTH, p);
		layout.putConstraint(SpringLayout.WEST, splitPane, 3, SpringLayout.WEST, p);
		layout.putConstraint(SpringLayout.EAST, splitPane, 0, SpringLayout.WEST, settingPanel);

//	     add(scrollTable);
//	     add(imagePanel);
		add(settingPanel);
		    //add(settingPanel,BorderLayout.CENTER);
		    
		//add(imagePanel);
		//add(settingPanel);
		
		setVisible(true);
		
		Constant.jpegQuality = InifileProperty.getInstance().getJpegQuality();
		
		//   String filepath = "I:\\Android\\sample\\original";
		
		new DropTarget(this, new FileDropTargetAdapter(new OnDropListener(){
			@Override
			public void onDrop(String filepath) {
				final String filePath = filepath;
				new Thread(){
					public void run(){
						
						mEventObserver.startProgress();
						try {
							if(mCurrentFile != null){
								mCurrentFile.close();
								mCurrentFile = null;
								if(mIsSettingChanged){
									int ret = showSettingSaveConfirmDialog();
									if(ret == JOptionPane.YES_OPTION){
										saveSettingFile(mSettingFilePath);
									}
								}
							}
							
							mIsSettingChanged = false;
							
							IImageFileScanner scanner = ImageFileScanner.getFileScanner(filePath);
							mCurrentFile = scanner;
							
							IImageFileInfoList list = scanner.getImageFileInfoList();
							
							String settingFilePath = getSettingFilePath(scanner.getOpenFilePath());
							mSettingFilePath = settingFilePath;
							
							OutputSettingParam outputParam = mSettingPanel.getOutputSettingParam();
							setOutputParamByFilename(outputParam, scanner.getOpenFilePath());
							
							
							//ImageFilterParam param = null;
							ImageFilterParamSet params = null;
							
							File settingFile = new File(settingFilePath);
							if(settingFile.exists()){
								//TODO: implement setting file reader/writer
								XmlWriter loader = new XmlWriter();
								loader.openLoadSettingFile(mSettingFilePath);
								//param = new ImageFilterParam();
								params = new ImageFilterParamSet();
								// listに変更を反映する
								loader.loadSetting(outputParam, params, list);
								
							}
							// 出力設定を設定画面に反映
							mEventObserver.sendEvent(EventObserver.EventTarget_Setting, EventObserver.EventType_UpdateOutputParam, outputParam);
		
							list.sort();
							mTable.setImageFileInfoList(list);
							//if(mThumbnailView != null){
							//	mThumbnailView.setImageFileInfoList(list);
							//}
							
							if(params != null){
								// 全体設定を反映
								mEventObserver.sendEvent(EventObserver.EventTarget_Setting, EventObserver.EventType_UpdateFilterParamSet, params);
								
								updateBaseFilterParam(params);
							}

					
						}catch(Exception e){
							e.printStackTrace();
						}

						mEventObserver.stopProgress();
						
						mTable.selectItem(0);
					}
				}.start();
			}
	     }));
	     
	     //GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
	     //device.setFullScreenWindow(this);
	}
	
	
	/**
	 * 入力ファイル名から出力ファイル名を自動設定　"[作者名]タイトル.zip"
	 * @param param
	 * @param filepath
	 */
	private void setOutputParamByFilename(OutputSettingParam param, String filepath){
		File file = new File(filepath);
		String name = file.getName();
		
		int dotIndex = name.lastIndexOf(".");
		if(dotIndex >= 0){
			name = name.substring(0, dotIndex);
		}

		String title = null;
		String author = null;
		int start = -1;
		int end = -1;
		if((start = name.indexOf("[")) >= 0){
			end = name.indexOf("]");
			start += 1;
			if(end > start){
				author = name.substring(start, end);
			}
		}
		if(author == null){
			title = name;
		}
		else{
			String text = name.substring(end+1);
			title = text.trim();
		}
		
		if(title != null && !title.isEmpty()){
			param.setTitle(title);

			String[] value = title.split("\\s");
			if(value.length >= 2){
				String seriesTitle = value[0];
				param.setSeriesTitle(seriesTitle);
			}
			try{
				int num = Integer.parseInt(title.replaceAll("[^0-9]", ""));
				param.setSeriesNumber(num);
			}catch(Exception e){
			}
		}
		if(author != null && !author.isEmpty()){
			param.setAuthor(author);
		}
	}
	
	private void saveSettingFile(String filepath){
		 XmlWriter writer = new XmlWriter();
		 writer.openSaveSettingFile(filepath);
		 writer.writeSetting(null, mSettingPanel.getOutputSettingParam(), mBaseFilterParams, mTable.getImageFileInfoList());
	}
	
	private int showSettingSaveConfirmDialog(){
		return JOptionPane.showConfirmDialog(this, "変更を保存しますか？", "確認", JOptionPane.YES_NO_OPTION);
	}
	private String getSettingFilePath(String inputFilePath){
		
		File inputFile = new File(inputFilePath);
		if(inputFile.isDirectory()){
			String dirname = inputFile.getName();
			File outputFile = new File(inputFile.getAbsoluteFile(), dirname + "_setting.xml");
			return outputFile.getAbsolutePath();
		}
		else{
			String dirname = inputFile.getParent();
			String filename = inputFile.getName();
			//TODO:
			int dotIndex = filename.lastIndexOf(".");
			if(dotIndex >= 0){
				filename = filename.substring(0, dotIndex);
			}
			
			File outputFile = new File(dirname, filename + "_setting.xml");
			return outputFile.getAbsolutePath();
		}
	}

	public void setBaseFilterParam(ImageFilterParamSet params){
		mBaseFilterParams = new ImageFilterParamSet();
		for(int i=0; i<params.size(); i++){
			ImageFilterParam param = params.get(i);
			mBaseFilterParams.set(i, param != null ? param.clone() : null);
		}
		mImagePanel.setImageFilterParam(mBaseFilterParams);
		mTable.setImageFilterParam(mBaseFilterParams);
		//mBaseFilterParam = param.clone();
		//mImagePanel.setImageFilterParam(mBaseFilterParam);
		//mTable.setImageFilterParam(mBaseFilterParam);
	}

	public void updateBaseFilterParam(ImageFilterParamSet params) {
		if(params.get(ImageFilterParamSet.FILTER_INDEX_TEXT) != null && params.get(ImageFilterParamSet.FILTER_INDEX_TEXT).isUnificationTextPage()){
			IImageFileInfoList list = mTable.getImageFileInfoList();
			AddSpaceFilter.setUnificationTextPageSize(ImageFileInfoList.getTextPageUnionDimension(list));
		}
		else{
			AddSpaceFilter.setUnificationTextPageSize(0, 0);
		}
		
		boolean isOutputResize = mSettingPanel.isOutputResize();
		Dimension previewSize = mSettingPanel.getOutputImageSize();
		if(previewSize == null && isOutputResize){
			params.setResize(false);
//			mImagePanel.setOutputSizePreview(true, previewSize.width, previewSize.height);
		}
		else if(previewSize != null && isOutputResize){
			params.setResize(true);
			mImagePanel.setOutputSizePreview(true, previewSize.width, previewSize.height);
		}else{
			mImagePanel.setOutputSizePreview(false, 0, 0);
		}
		
		mImagePanel.setSimpleZoom(params.isSimpleZoom());

		setBaseFilterParam(params);
		
		mTable.selectCurrentItem(); // Baseフィルタが更新されたのでカレントイメージも更新する
	}

	private void beginConvert(){
		
		new Thread(){
			@Override
			public void run(){
				try {
					//　ファイルリストを取得
					IImageFileInfoList list = mTable.getImageFileInfoList();
					if(list == null || list.size() == 0){
						return;
					}
					
					mEventObserver.startProgress();

					// 基本変換パラメータ取得
					ImageFilterParamSet params = mSettingPanel.getImageFilterParamSet();
					
					// 出力設定を取得
					OutputSettingParam outSetting = mSettingPanel.getOutputSettingParam();
					
					params.setPreview(false);
					
					// 出力サイズを設定
					Dimension size = outSetting.getImageSize();
					if(size == null || (size.width == 0 || size.height == 0)){
						params.setResize(false);
					}else{
						params.setResize(true);
						params.setResizeDimension(size);
					}
					
					if(outSetting.isFixedSize()){
						// 固定出力サイズチェック
						mEventObserver.sendEvent(EventObserver.EventTarget_Setting, EventObserver.EventType_ProgressMessage, "画像サイズチェック中...");
						OutputImageFilter preconvertFilter = new OutputImageFilter(params, false);
						ImageFilePreconverter checker = new ImageFilePreconverter(preconvertFilter);
						checker.write(list, null);
						Dimension unionSize = checker.getUnionSize();
						params.setFixedSize(unionSize);
					}
					
					// 基本出力フィルタを生成
					OutputImageFilter imageFilter = new OutputImageFilter(params, outSetting.isFixedSize());
					
					// 出力設定からImageFileWriterを生成
					IImageFileWriter writer = outSetting.getImageFileWriter();
					writer.setImageFilter(imageFilter);
					
					// 出力ファイル(フォルダ)パスを作成
					File dir = new File(outSetting.getFinalOutputPath());
					File file = new File(dir.getAbsolutePath(), outSetting.getOutputFileName(writer.getSuffix()));
					if(!dir.exists()){
						if(!dir.mkdirs()){
							throw new Exception("can't create output folder.");
						}
					}
					
					// 出力ファイル(フォルダ)オープン
					if(writer.open(file.getAbsolutePath())){
						synchronized(mLock){
							mFileWriter = writer; // ここからCancel可能
						}
						mEventObserver.sendEvent(EventObserver.EventTarget_Setting, EventObserver.EventType_ProgressMessage, "start");
						// 出力処理開始
						boolean result = writer.write(list, new OnProgressListener(){
							@Override
							public void onProgress(int progress, String message) {
								if(message == null){
									message = progress + "%";
								}
								mEventObserver.sendEvent(EventObserver.EventTarget_Setting, EventObserver.EventType_ProgressMessage, message);
							}
						});
						// 出力終了
						synchronized(mLock){
							mFileWriter = null;
						}
						writer.close();
						
						if(result){
							mEventObserver.sendEvent(EventObserver.EventTarget_Setting, EventObserver.EventType_ProgressMessage, "finish.");
						}else{
							mEventObserver.sendEvent(EventObserver.EventTarget_Setting, EventObserver.EventType_ProgressMessage, "convert failed.");
						}
					}
					else{
						throw new Exception("can't open output file.");
					}
					
				} catch(Exception e) {
					
					e.printStackTrace();
					mEventObserver.sendEvent(EventObserver.EventTarget_Setting, EventObserver.EventType_ProgressMessage, "" + e.getMessage());
					
				} finally {
					synchronized(mLock){
						mFileWriter = null;
					}
					mEventObserver.stopProgress();
					mEventObserver.sendEvent(EventObserver.EventTarget_Setting, EventObserver.EventType_FinishConvert, 0);
				}
			}
		}.start();
	}
	
	private void saveSettingFileRequest(){
		 if(mSettingFilePath != null){
			 if(mIsSettingChanged){
    			 int ret = showSettingSaveConfirmDialog();
    			 if(ret == JOptionPane.YES_OPTION){
    				 saveSettingFile(mSettingFilePath);
    			 }
			 }
		 }
	}
	
	@Override
	public void onEventReceived(int type, int arg1, int arg2, Object obj) {
		switch(type){
		case EventObserver.EventType_UpdateFilterParamSet:
			mIsSettingChanged = true;
			updateBaseFilterParam((ImageFilterParamSet)obj);
			break;
		case EventObserver.EventType_ModifiedSetting:
			mIsSettingChanged = true;
			break;
		case EventObserver.EventType_BeginConvert:
			beginConvert();
			break;
		case EventObserver.EventType_CancelConvert:
			synchronized(mLock){
				if(mFileWriter != null){
					mFileWriter.cancel();
				}
			}
			break;
		case EventObserver.EventType_RequestSaveSetting:
			saveSettingFileRequest();
			break;
		default:
			break;
		}
		
	}
}
