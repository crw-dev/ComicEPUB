package jp.crwdev.app.gui;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import jp.crwdev.app.EventObserver;
import jp.crwdev.app.constant.Constant;
import jp.crwdev.app.setting.FileListItem;
import jp.crwdev.app.util.SerializeArrayUtil;

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
				return (column == 0);	// 編集禁止
			}
			
			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			public Class getColumnClass(int col){
				return getValueAt(0, col).getClass();
			}
		};
		
		setModel(mTableModel);
		
		setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		for(int i=0; i<Constant.TABLE_HEADER_FILELIST_COLUMNS.length; i++){
			TableColumn col = getColumnModel().getColumn( i );
			if(i == 0){
				col.setMinWidth(20);
				col.setMaxWidth(40);
			}else{
				col.setMinWidth(200);
			}
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
	
	@SuppressWarnings("unchecked")
	public void loadList(){
		ArrayList<FileListItem> list = SerializeArrayUtil.load("filelist.dat");
		if(list != null){
			for(FileListItem item : list){
				addData(true, item.getName(), item.getPath());
			}
		}
	}
	
	public void saveList(){
		ArrayList<FileListItem> list = getLockedFileItem();
		SerializeArrayUtil.save("filelist.dat", list);
	}
	
	public void clearData(){
		
		ArrayList<FileListItem> items = getLockedFileItem();
		
		mTableModel.setRowCount(0);
		
		for(FileListItem item : items){
			addData(true, item.getName(), item.getPath());
		}
	}
	
	private ArrayList<FileListItem> getLockedFileItem(){
		ArrayList<FileListItem> items = new ArrayList<FileListItem>();
		for(int i=0; i<mTableModel.getRowCount(); i++){
			Boolean lock = (Boolean)mTableModel.getValueAt(i, Constant.TABLE_HEADER_FILELIST_COLUMN_LOCK);
			if(lock){
				String name = (String)mTableModel.getValueAt(i, Constant.TABLE_HEADER_FILELIST_COLUMN_FILENAME);
				String path = (String)mTableModel.getValueAt(i, Constant.TABLE_HEADER_FILELIST_COLUMN_FILEPATH);
				items.add(new FileListItem(name, path));
			}
		}
		return items;
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
							addData(false, file.getAbsolutePath());
						}
					}
					else if(file.isFile()){
						String name = file.getName().toLowerCase();
						if(name.length() >= 3){
							String suffix = name.substring(name.length()-3);
							if(Constant.SUPPORT_INPUT_PREFIX.contains(suffix)){
								addData(false, file.getAbsolutePath());
							}
						}
					}
				}
			}
		};
		mThread.start();
	}
	
	public void addData(boolean lock, String filepath){
		File file = new File(filepath);
		if(file.exists()){
			addData(lock, file.getName(), file.getAbsolutePath());
		}
	}
	
	public void addData(boolean lock, String filename, String path){
		int updateRow = -1;
		Boolean lockValue = new Boolean(lock);
		for(int row = 0; row < mTableModel.getRowCount(); row++){
			if(path.equals(mTableModel.getValueAt(row, Constant.TABLE_HEADER_FILELIST_COLUMN_FILEPATH))){
				updateRow = row;
				lockValue = (Boolean)mTableModel.getValueAt(row, Constant.TABLE_HEADER_FILELIST_COLUMN_LOCK);
				break;
			}
		}
		Object[] record = new Object[]{ lockValue, filename, path };
		if(updateRow >= 0){
			updateData(updateRow, lockValue, filename, path);
		}else{
			mTableModel.addRow(record);
		}
	}
	
	public void updateData(int row, boolean lock, String filename, String path){
		Object[] record = new Object[]{ new Boolean(lock), filename, path };
		for(int col=0; col<record.length; col++){
			mTableModel.setValueAt(record[col], row, col);
		}
	}
	
	
}
