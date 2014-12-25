package jp.crwdev.app.gui;

import java.io.File;
import java.io.FileFilter;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import jp.crwdev.app.EventObserver;
import jp.crwdev.app.constant.Constant;

@SuppressWarnings("serial")
public class FileListTable extends JTable {

	
	private DefaultTableModel mTableModel = null;
	
	private EventObserver mEventSender = null;
	public void setEventObserver(EventObserver observer){
		mEventSender = observer;
	}
	

	public FileListTable(){
		initialize();
	}
	
	
	private void initialize(){
		
		mTableModel = new DefaultTableModel(Constant.TABLE_HEADER_FILELIST_COLUMNS, 0){
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;	// 編集禁止
			}
		};
		
		setModel(mTableModel);
		
		setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		for(int i=0; i<Constant.TABLE_HEADER_FILELIST_COLUMNS.length; i++){
			TableColumn col = getColumnModel().getColumn( i );
			col.setMinWidth(200);
		}

		getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if(e.getValueIsAdjusting()){
					return;
				}
				int selected = getSelectedRow();
				if(selected >= 0){
					onItemSelected(selected);
				}
			}
		});

	}
	
	// Table Event
	private void onItemSelected(int index){
		System.out.println("select " + index);
		String filepath = (String)mTableModel.getValueAt(index, Constant.TABLE_HEADER_FILELIST_COLUMN_FILEPATH);
		mEventSender.sendEvent(EventObserver.EventTarget_Main, EventObserver.EventType_OpenFile, (Object)filepath);
	}
	
	
	public void clearData(){
		mTableModel.setRowCount(0);
	}
	
	private Thread mThread = null;
	private boolean mThreadInterrupt = false;
	
	public void scanFolder(String filepath){
		
		clearData();
		
		File file = new File(filepath);
		if(!file.exists()){
			return;
		}
		
		final File folder = file.getParentFile();
		
		mThreadInterrupt = true;
		if(mThread != null){
			try {
				mThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		mThread = new Thread(){
			public void run(){
				mThreadInterrupt = false;
				
				File[] files = folder.listFiles();
				for(File file : files){
					if(mThreadInterrupt){
						break;
					}
					if(file.isDirectory()){
						File[] result = file.listFiles(new FileFilter(){
							@Override
							public boolean accept(File pathname) {
								if(mThreadInterrupt){
									return false;
								}
								String name = pathname.getName().toLowerCase();
								if(name.length() < 3){
									return false;
								}
								String suffix = name.substring(name.length()-3);
								return Constant.SUPPORT_IMAGE_PREFIX.contains(suffix);
							}
							
						});
						if(result != null && result.length > 0){
							addData(file.getAbsolutePath());
						}
					}
					else if(file.isFile()){
						String name = file.getName().toLowerCase();
						if(name.length() >= 3){
							String suffix = name.substring(name.length()-3);
							if(Constant.SUPPORT_INPUT_PREFIX.contains(suffix)){
								addData(file.getAbsolutePath());
							}
						}
					}
				}
			}
		};
		mThread.start();
	}
	
	public void addData(String filepath){
		File file = new File(filepath);
		if(file.exists()){
			addData(file.getName(), file.getAbsolutePath());
		}
	}
	
	public void addData(String filename, String path){
		int updateRow = -1;
		for(int row = 0; row < mTableModel.getRowCount(); row++){
			if(path.equals(mTableModel.getValueAt(row, Constant.TABLE_HEADER_FILELIST_COLUMN_FILEPATH))){
				updateRow = row;
				break;
			}
		}
		Object[] record = new Object[]{ filename, path };
		if(updateRow >= 0){
			updateData(updateRow, filename, path);
		}else{
			mTableModel.addRow(record);
		}
	}
	
	public void updateData(int row, String filename, String path){
		Object[] record = new Object[]{ filename, path };
		for(int col=0; col<record.length; col++){
			mTableModel.setValueAt(record[col], row, col);
		}
	}
	
	
}
