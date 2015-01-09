package jp.crwdev.app.gui;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JWindow;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import jp.crwdev.app.EventObserver;
import jp.crwdev.app.EventObserver.OnEventListener;
import jp.crwdev.app.constant.Constant;
import jp.crwdev.app.setting.FileListItem;
import jp.crwdev.app.util.InifileProperty;
import jp.crwdev.app.util.SerializeArrayUtil;

@SuppressWarnings("serial")
public class FileListTable extends JTable implements OnEventListener {


	private DefaultTableModel mTableModel = null;

	private HashMap<String, String> mThumbnailMap = new HashMap<String, String>();

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
			if(i == Constant.TABLE_HEADER_FILELIST_COLUMN_LOCK){
				col.setMinWidth(20);
				col.setMaxWidth(40);
			}else{
				col.setMinWidth(100);
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

		if(InifileProperty.getInstance().isEnableFolderListThumbnail()){
			ThumbnailMouseMotionAdapter adapter = new ThumbnailMouseMotionAdapter(this);
			this.addMouseMotionListener(adapter);
			this.addMouseListener(adapter);

			setEventListener();
		}

	}


	public class ThumbnailMouseMotionAdapter extends MouseMotionAdapter implements MouseListener {
		private FileListTable table;
		private Point currentCell;
		private JWindow popupWindow;

		public ThumbnailMouseMotionAdapter(FileListTable table){
			this.table = table;
		}
		@Override
		public void mouseMoved(MouseEvent e) {

			Point p = e.getPoint();
			int row = table.rowAtPoint(p);
			int col = table.columnAtPoint(p);

			if (col != 1){
				currentCell = null;
				return;
			}

			if ((row > -1 && row < table.getRowCount()) && (col > -1 && col < table.getColumnCount())) {

				if(currentCell == null || currentCell.x != col || currentCell.y != row){
					currentCell = new Point(col, row);

					String thumbname = null;
					String filepath = (String)table.getValueAt(row, Constant.TABLE_HEADER_FILELIST_COLUMN_FILEPATH);

					if(mThumbnailMap != null && mThumbnailMap.containsKey(filepath)){
						thumbname = mThumbnailMap.get(filepath);
					}

					if(thumbname != null){

						File file = new File("thumbnail", thumbname);
						if(!file.exists()){
							return;
						}

						Rectangle cellRect = table.getCellRect(row, 1, false);

						if(popupWindow == null){
							popupWindow = new JWindow();
						}else{
							popupWindow.setVisible(false);
						}

						BufferedImage thumbnail;
						try {
							FileInputStream fis = new FileInputStream(file);
							thumbnail = ImageIO.read(fis);
							fis.close();
						} catch (IOException e1) {
							return;
						}
						JLabel label = new JLabel(new ImageIcon(thumbnail));

						Point screenPos = table.getLocationOnScreen();

						popupWindow.setSize(thumbnail.getWidth() , thumbnail.getHeight());
						popupWindow.setLocation(screenPos.x + cellRect.x+cellRect.width, screenPos.y + cellRect.y);
						popupWindow.getContentPane().removeAll();
						popupWindow.getContentPane().add(label);
						popupWindow.addMouseListener(new MouseAdapter() {
							public void mouseClicked(MouseEvent e) {
								popupWindow.setVisible(false);
							}
						});
						popupWindow.setVisible(true);

					}
					else{
						if(popupWindow != null && popupWindow.isVisible()){
							popupWindow.setVisible(false);
						}
					}


				}
			}
			else{
				if(popupWindow != null && popupWindow.isVisible()){
					popupWindow.setVisible(false);
					currentCell = null;
				}
			}

		}


		@Override
		public void mouseExited(MouseEvent e) {
			if(popupWindow != null && popupWindow.isVisible()){
				popupWindow.setVisible(false);
				currentCell = null;
			}
		}
		@Override
		public void mouseClicked(MouseEvent e) {
			// NOP
		}
		@Override
		public void mousePressed(MouseEvent e) {
			// NOP
		}
		@Override
		public void mouseReleased(MouseEvent e) {
			// NOP
		}
		@Override
		public void mouseEntered(MouseEvent e) {
			// NOP
		}

	}

	// Table Event
	private void onItemSelected(int index){
		System.out.println("select " + index);
		String filepath = (String)mTableModel.getValueAt(index, Constant.TABLE_HEADER_FILELIST_COLUMN_FILEPATH);
		if(mThumbnailMap != null && mThumbnailMap.containsKey(filepath)){
			index = -1;	// no need thumbnail
		}
		mEventSender.sendEvent(EventObserver.EventTarget_Main, EventObserver.EventType_OpenFile, index, (Object)filepath);
	}

	@SuppressWarnings("unchecked")
	public void loadList(){

		if(InifileProperty.getInstance().isEnableFolderList()){

			ArrayList<FileListItem> list = SerializeArrayUtil.loadList("filelist.dat");
			if(list != null){
				for(FileListItem item : list){
					addData(true, item.getName(), item.getPath());
				}
			}

			if(InifileProperty.getInstance().isEnableFolderListThumbnail()){

				HashMap<String, String> map = SerializeArrayUtil.loadMap("thumbnail/thumbnail.dat");
				if(map != null){
					mThumbnailMap = map;
				}

			}
		}

	}

	public void saveList(){

		if(InifileProperty.getInstance().isEnableFolderList()){

			ArrayList<FileListItem> list = getLockedFileItem();
			SerializeArrayUtil.save("filelist.dat", list);

			if(InifileProperty.getInstance().isEnableFolderListThumbnail()){
				SerializeArrayUtil.save("thumbnail/thumbnail.dat", mThumbnailMap);
			}
		}
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

	public void addThumbnailMap(String filepath, String thumnail){
		if(mThumbnailMap != null){
			if(mThumbnailMap.containsKey(filepath)){
				mThumbnailMap.remove(filepath);
			}
			mThumbnailMap.put(filepath, thumnail);
		}
	}

	public boolean existThumbnail(String filename){
		if(mThumbnailMap != null){
			return mThumbnailMap.containsKey(filename);
		}
		return false;
	}

	private void setEventListener(){

		final JTable table = this;

		getTableHeader().addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e) {
				if(javax.swing.SwingUtilities.isRightMouseButton(e)){

					if(table.getSelectedRowCount() > 0){

						JPopupMenu popup = new JPopupMenu();
						JMenuItem item0 = new JMenuItem("サムネイル削除");
						item0.addActionListener(new ActionListener(){
							@Override
							public void actionPerformed(ActionEvent e) {

								int rowCount = table.getRowCount();
								for(int i=0; i<rowCount; i++){
									if(table.isRowSelected(i)){
										String filepath = (String)mTableModel.getValueAt(i, Constant.TABLE_HEADER_FILELIST_COLUMN_FILEPATH);
										String thumbnail = mThumbnailMap.remove(filepath);

										if(thumbnail != null){
											File file = new File("thumbnail", thumbnail);
											if(file.exists()){
												if(!file.delete()){
													file.deleteOnExit();
												}
											}
										}
									}
								}
							}
						});

						popup.add(item0);

						popup.show(e.getComponent(), e.getX(), e.getY());
					}

				}
			}
		});

	}

	@Override
	public void onEventReceived(int type, int arg1, int arg2, Object obj) {
		switch(type){
		case EventObserver.EventType_UpdateThumbnail:
			String filepath = (String)mTableModel.getValueAt(arg1, Constant.TABLE_HEADER_FILELIST_COLUMN_FILEPATH);
			addThumbnailMap(filepath, (String)obj);
			break;
		default:
			break;
		}
	}


}
