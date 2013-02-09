﻿package jp.crwdev.app.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.dnd.DropTarget;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.table.DefaultTableModel;

import jp.crwdev.app.EventObserver;
import jp.crwdev.app.ImageFileInfoList;
import jp.crwdev.app.ImageFileScanner;
import jp.crwdev.app.OutputSettingParam;
import jp.crwdev.app.EventObserver.OnEventListener;
import jp.crwdev.app.container.folder.FolderImageFileWriter;
import jp.crwdev.app.container.zip.ZipImageFileWriter;
import jp.crwdev.app.imagefilter.ImageFilterParam;
import jp.crwdev.app.imagefilter.OutputImageFilter;
import jp.crwdev.app.imagefilter.PreviewImageFilter;
import jp.crwdev.app.interfaces.IImageFileInfoList;
import jp.crwdev.app.interfaces.IImageFileScanner;
import jp.crwdev.app.interfaces.IImageFileWriter;
import jp.crwdev.app.setting.XmlWriter;
import jp.crwdev.app.util.FileDropTargetAdapter;
import jp.crwdev.app.util.FileDropTargetAdapter.OnDropListener;

public class MainFrame extends JFrame implements OnEventListener {

	private IImageFileScanner mCurrentFile = null;
	private String mSettingFilePath = null;
	
	private ImageFileInfoTable mTable;
	private ImagePanel mImagePanel;
	private SettingPanel mSettingPanel;
	
	private ImageFilterParam mBaseFilterParam = new ImageFilterParam();	// global setting
	
	private boolean mIsUnificationTextPage = false;
	
	private EventObserver mEventObserver = new EventObserver();
	
	public MainFrame(){
		 setSize(new Dimension(950,750));
	     setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	     
	     addWindowListener(new WindowAdapter(){
	    	 @Override
	    	 public void windowClosing(WindowEvent e) {
	    		 System.out.println("windowClosing");
	    		 //dispose();
	    		 if(mSettingFilePath != null){
	    			 int ret = showSettingSaveConfirmDialog();
	    			 if(ret == JOptionPane.YES_OPTION){
	    				 saveSettingFile(mSettingFilePath);
	    			 }
	    		 }
	    	 }
	    	 @Override
	    	 public void windowClosed(WindowEvent e) {
	    		 System.out.println("windowClosed");
	    	 }
	     });
	     
	     
	     mIsUnificationTextPage = ImageFilterParam.isUnificationTextPage();
	     
	     // Table
	     ImageFileInfoTable table = new ImageFileInfoTable();
	 
	     JScrollPane scrollTable = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
	     scrollTable.setPreferredSize(new Dimension(200, 800));

	     mTable = table;
		    

	     // ImagePanel
	     ImagePanel imagePanel = new ImagePanel();
	     imagePanel.setBackground(Color.WHITE);
	     //imagePanel.setPreferredSize(new Dimension(600, 800));
	     
	     
	     mTable.setImagePanel(imagePanel);
	     mImagePanel = imagePanel;

	     // SettingPanel
	     SettingPanel settingPanel = new SettingPanel();
	     settingPanel.setPreferredSize(new Dimension(250, 800));
	     //settingPanel.setSize(300,800);
	     //settingPanel.setMinimumSize(new Dimension(300, 800));
	     mSettingPanel = settingPanel;
	     
	     ImageFilterParam defaultParam = settingPanel.getFilterParam();
	     setBaseFilterParam(defaultParam);

	     
	     SpringLayout layout = new SpringLayout();
	     setLayout(layout);
	     
	     Container p = getContentPane();
	     
	     layout.putConstraint(SpringLayout.NORTH, scrollTable, 3, SpringLayout.NORTH, p);
	     layout.putConstraint(SpringLayout.SOUTH, scrollTable, 3, SpringLayout.SOUTH, p);
	     layout.putConstraint(SpringLayout.WEST, scrollTable, 3, SpringLayout.WEST, p);
//	     layout.putConstraint(SpringLayout.EAST, scrollTable, 0, SpringLayout.WEST, imagePanel);
	     
	     layout.putConstraint(SpringLayout.NORTH, imagePanel, 3, SpringLayout.NORTH, p);
	     layout.putConstraint(SpringLayout.SOUTH, imagePanel, 3, SpringLayout.SOUTH, p);
	     layout.putConstraint(SpringLayout.WEST, imagePanel, 3, SpringLayout.EAST, scrollTable);
	     layout.putConstraint(SpringLayout.EAST, imagePanel, -5, SpringLayout.WEST, settingPanel);
	     
	     layout.putConstraint(SpringLayout.NORTH, settingPanel, 3, SpringLayout.NORTH, p);
	     layout.putConstraint(SpringLayout.SOUTH, settingPanel, -3, SpringLayout.SOUTH, p);
//	     layout.putConstraint(SpringLayout.WEST, settingPanel, 5, SpringLayout.WEST, imagePanel);
	     layout.putConstraint(SpringLayout.EAST, settingPanel, -5, SpringLayout.EAST, p);
	     
	     table.setEventObserver(mEventObserver);
	     imagePanel.setEventObserver(mEventObserver);
	     settingPanel.setEventObserver(mEventObserver);
	     mEventObserver.setEventListener(EventObserver.EventTarget_Table, table);
	     mEventObserver.setEventListener(EventObserver.EventTarget_Panel, imagePanel);
	     mEventObserver.setEventListener(EventObserver.EventTarget_Setting, settingPanel);
	     mEventObserver.setEventListener(EventObserver.EventTarget_Main, this);
	     
	     
	     add(scrollTable);
	     add(imagePanel);
	     add(settingPanel);
		    //add(settingPanel,BorderLayout.CENTER);
		    
	     //add(imagePanel);
	     //add(settingPanel);
	     
	     setVisible(true);
	     
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
								int ret = showSettingSaveConfirmDialog();
								if(ret == JOptionPane.YES_OPTION){
									saveSettingFile(mSettingFilePath);
								}
							}
							
							IImageFileScanner scanner = ImageFileScanner.getFileScanner(filePath);
							mCurrentFile = scanner;
							
							IImageFileInfoList list = scanner.getImageFileInfoList();
							
							String settingFilePath = getSettingFilePath(scanner.getOpenFilePath());
							
							File settingFile = new File(settingFilePath);
							if(settingFile.exists()){
								//TODO: implement setting file reader/writer
								XmlWriter loader = new XmlWriter();
								mSettingFilePath = settingFilePath;
								loader.openLoadSettingFile(mSettingFilePath);
								OutputSettingParam outputParam = mSettingPanel.getOutputSettingParam();
								ImageFilterParam param = new ImageFilterParam();
								loader.loadSetting(outputParam, param, list);
								
								// update setting
								mEventObserver.sendEvent(EventObserver.EventTarget_Setting, EventObserver.EventType_UpdateOutputParam, outputParam);
								mEventObserver.sendEvent(EventObserver.EventTarget_Setting, EventObserver.EventType_UpdateFilterParam, param);
							}
							
							mTable.setImageFileInfoList(list);
					
						}catch(Exception e){
							e.printStackTrace();
						}

						mEventObserver.stopProgress();
					}
				}.start();
			}
	     }));
	     

	}
	
	private void saveSettingFile(String filepath){
		 XmlWriter writer = new XmlWriter();
		 writer.openSaveSettingFile(filepath);
		 writer.writeSetting(null, mSettingPanel.getOutputSettingParam(), mBaseFilterParam, mTable.getImageFileInfoList());
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

	public void setBaseFilterParam(ImageFilterParam param){
		mBaseFilterParam = param.clone();
		mImagePanel.setImageFilterParam(mBaseFilterParam);
		mTable.setImageFilterParam(mBaseFilterParam);
	}

	public void updateBaseFilterParam(ImageFilterParam param) {
		if(mIsUnificationTextPage != ImageFilterParam.isUnificationTextPage()){
			mIsUnificationTextPage = ImageFilterParam.isUnificationTextPage();
			if(mIsUnificationTextPage){
				IImageFileInfoList list = mTable.getImageFileInfoList();
				ImageFilterParam.setUnificationTextPageSize(ImageFileInfoList.getTextPageUnionDimension(list));
			}
			else{
				ImageFilterParam.setUnificationTextPageSize(0, 0);
			}
		}
		setBaseFilterParam(param);
	}

	private void beginConvert(){
		
		new Thread(){
			@Override
			public void run(){
				mEventObserver.startProgress();
				
				ImageFilterParam param = mSettingPanel.getFilterParam();
				OutputSettingParam outSetting = mSettingPanel.getOutputSettingParam();
				
				IImageFileInfoList list = mTable.getImageFileInfoList();
				param.setPreview(false);
				
				Dimension size = outSetting.getImageSize();
				if(size == null || (size.width == 0 || size.height == 0)){
					param.setResize(false);
				}else{
					param.setResize(true);
					param.setResizeDimension(size);
				}
				
				OutputImageFilter imageFilter = new OutputImageFilter(param);
				
				IImageFileWriter writer = outSetting.getImageFileWriter();
				writer.setImageFilter(imageFilter);
				
				File file = new File(outSetting.getOutputPath(), outSetting.getOutputFileName());
				
				if(writer.open(file.getAbsolutePath())){
					writer.write(list);
					writer.close();
				}
				
				mEventObserver.stopProgress();
				
			}
		}.start();
	}
	
	@Override
	public void onEventReceived(int type, int arg1, int arg2, Object obj) {
		switch(type){
		case EventObserver.EventType_UpdateFilterParam:
			updateBaseFilterParam((ImageFilterParam)obj);
			break;
		case EventObserver.EventType_BeginConvert:
			beginConvert();
			break;
		default:
			break;
		}
		
	}
}
