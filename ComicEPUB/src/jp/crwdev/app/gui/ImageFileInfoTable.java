package jp.crwdev.app.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.LinkedList;

import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import jp.crwdev.app.BufferedImageIO;
import jp.crwdev.app.EventObserver;
import jp.crwdev.app.EventObserver.OnEventListener;
import jp.crwdev.app.constant.Constant;
import jp.crwdev.app.container.ImageFileInfoSplitWrapper;
import jp.crwdev.app.imagefilter.ImageFilterParam;
import jp.crwdev.app.imagefilter.PageCheckFilter;
import jp.crwdev.app.imagefilter.SplitFilter;
import jp.crwdev.app.interfaces.IImageFileInfo;
import jp.crwdev.app.interfaces.IImageFileInfoList;
import jp.crwdev.app.setting.ImageFilterParamSet;
import jp.crwdev.app.util.ImageFileInfoAsyncTask;
import jp.crwdev.app.util.ImageFileInfoAsyncTask.OnTaskObserver;


public class ImageFileInfoTable extends JTable implements OnEventListener {

	private DefaultTableModel mTableModel = null;
	private ImagePanel mImagePanel = null;
	
	private IImageFileInfoList mInfoList = null;
	private ImageFilterParamSet mBaseFilterParams = null;
	
	public ImageFileInfoTable(){
		initialize();
	}
	
	private void initialize(){
		mTableModel = new DefaultTableModel(Constant.TABLE_HEADER_COLUMNS, 0){
			@Override
			public boolean isCellEditable(int row, int column) {
				return (column != 0);	// 0カラム目の編集禁止
			}
		};

		setModel(mTableModel);
		
		setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		
		for(int i=0; i<Constant.TABLE_HEADER_COLUMNS.length; i++){
	        TableColumn col = getColumnModel().getColumn( i );
	        col.setMinWidth(50);
//	        col.setMaxWidth(200);
		}

		setEventListener();
		
	}
	
	public void finalize(){
		finalizeThread();
	}
	
	private EventObserver mEventSender = null;
	public void setEventObserver(EventObserver observer){
		mEventSender = observer;
	}
	
	public void setImagePanel(ImagePanel panel){
		mImagePanel = panel;
	}
	
	public void setImageFilterParam(ImageFilterParamSet baseParams){
		synchronized(this){
			mBaseFilterParams = baseParams;
		}
	}
	
	@Override
	public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
		Component c = super.prepareRenderer(renderer, row, column);
		if(isRowSelected(row)){// == getSelectedRow()){
			//c.setBackground(Color.MAGENTA);
		}
		else{
			IImageFileInfo info = mInfoList.get(row);
			if(!info.isEnable()){
				c.setBackground(Color.GRAY);
			}
			else{
				ImageFilterParam param = mInfoList.get(row).getFilterParam();
				if(param.isEdit()){
					c.setBackground(Color.YELLOW);
				}
				else{
					boolean pict = (param.getPageType() == Constant.PAGETYPE_PICT);
					c.setBackground(pict ? Color.ORANGE: getBackground());
				}
			}
		}
		return c;
	}
	
	public void setImageFileInfoList(IImageFileInfoList list){
		mInfoList = null;
		clearData();
		mInfoList = list;
		if(list != null){
			for(int i=0; i<list.size(); i++){
				addData(list.get(i));
			}
		}
		startLoadFileInfoThread();
	}
	
	private void startLoadFileInfoThread(){
		// FileInfoの読み込まれていない情報をスレッドで順次更新する
		//TODO
		ImageFileInfoAsyncTask task = new ImageFileInfoAsyncTask(mInfoList, new OnTaskObserver(){
			@Override
			public void onStart() {
				mEventSender.startProgress();
			}
			@Override
			public void onFinish() {
				mEventSender.stopProgress();
				mEventSender.setProgressMessage("");
				// FileInfo更新通知
				mEventSender.sendEvent(EventObserver.EventTarget_Setting, EventObserver.EventType_FileInfoModified, 0);
			}
			@Override
			public void onProcess(int index, int total, IImageFileInfo info) {
				
				mEventSender.setProgressMessage(String.format("%d / %d", (index+1), total));
				
				try {
					info.update();
				}catch(Exception e){
					e.printStackTrace();
				}catch(OutOfMemoryError e){
					e.printStackTrace();
				}
				
				mTableModel.setValueAt(Integer.toString(info.getWidth()), index, Constant.TABLE_COLUMN_WIDTH);
				mTableModel.setValueAt(Integer.toString(info.getHeight()), index, Constant.TABLE_COLUMN_HEIGHT);
			}
		});
		task.start();
	}
	
	public IImageFileInfoList getImageFileInfoList(){
		return mInfoList;
	}
	
	private int getPageTypeToFilterIndex(int pageType){
		switch(pageType){
		case Constant.PAGETYPE_COLOR:
			return ImageFilterParamSet.FILTER_INDEX_COLOR;
		case Constant.PAGETYPE_PICT:
			return ImageFilterParamSet.FILTER_INDEX_PICT;
		case Constant.PAGETYPE_TEXT:
			return ImageFilterParamSet.FILTER_INDEX_TEXT;
		case Constant.PAGETYPE_AUTO:
		default:
			return ImageFilterParamSet.FILTER_INDEX_BASIC;
		}
	}

	// Table Event
	private void onItemSelected(int index){
		selectItem(index);
	}
	
	private void onItemSelectedInternal(int index){
		if(mInfoList != null && 0 <= index && index < mInfoList.size()){
			IImageFileInfo info = mInfoList.get(index);
			if(info != null){
				InputStream stream = info.getInputStream();
				try {
					BufferedImage image = BufferedImageIO.read(stream, info.isJpeg());
					if(mImagePanel != null){
						mImagePanel.setImage(image, info, index);
					}
				}catch(OutOfMemoryError e){
					mEventSender.setProgressMessage(e.getMessage());
					e.printStackTrace();
					return;
				}
				
				int pageType = info.getFilterParam().getPageType();
				mEventSender.sendEvent(EventObserver.EventTarget_Setting, EventObserver.EventType_SelectTab, getPageTypeToFilterIndex(pageType));
			}
		}
	}
	
	private LinkedList<Integer> mQueue = new LinkedList<Integer>();
	private Object mThreadLock = new Object();
	private Thread mThread = null;
	private boolean mThreadFinish = false;
	public void selectItem(int index){
		if(mThread == null){
			mThread = new Thread(){
				@Override
				public void run(){
					while(!mThreadFinish){
						Integer index = -1;
						synchronized(mThreadLock){
							if(mQueue.isEmpty()){
								try {
									mThreadLock.wait();
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							if(!mQueue.isEmpty()){
								index = mQueue.pop();
							}
						}
						try{
							boolean loop = true;
							while(loop && !mThreadFinish){
								onItemSelectedInternal(index);
								synchronized(mQueue){
									loop = !mQueue.isEmpty();
									if(loop){
										index = mQueue.pop();
									}
								}
							}
						}catch(Exception e){
							mEventSender.setProgressMessage(e.getMessage());
						}catch(OutOfMemoryError e){
							mEventSender.setProgressMessage(e.getMessage());
						}
					}
					mThread = null;
				}
			};
			mThread.setPriority(Thread.MAX_PRIORITY);
			mThread.start();
		}
		synchronized(mThreadLock){
			while(mQueue.size() > 0){
				mQueue.remove();
			}
			mQueue.push(index);
			mThreadLock.notify();
		}
	}
	
	private void finalizeThread(){
		if(mThread != null){
			mThreadFinish = true;
			synchronized(mThreadLock){
				mThreadLock.notify();
			}
			mThread = null;
		}
	}
	
	
	public void selectCurrentItem(){
		int selected = getSelectedRow();
		if(selected < 0){
			selected = 0;
		}
		selectItem(selected);
//		onItemSelected(selected);
	}

	private void onTableCellChanged(int row, int col, String value){
		if(mInfoList != null){
			IImageFileInfo info = mInfoList.get(row);
			if(info != null){
				ImageFilterParam param = info.getFilterParam();
				if(param == null){
					param = new ImageFilterParam();
					info.setFilterParam(param);
				}
				boolean update = false;
				if(col == Constant.TABLE_COLUMN_PAGETYPE){
					int pageType = Constant.getPageType(value);
					if(pageType != param.getPageType()){
						// PageType変更有り
						param.setPageType(pageType);
						
						//TODO
						// FileInfo更新通知
						mEventSender.sendEvent(EventObserver.EventTarget_Setting, EventObserver.EventType_FileInfoModified, 0);
						mEventSender.setModified();

						selectCurrentItem();
						update = true;
					}
				}
				else if(col == Constant.TABLE_COLUMN_PAGESPREAD){
					if(value != param.getPageSpread()){
						// PageSpread変更有り
						param.setPageSpread(value);
						
						//TODO
						// FileInfo更新通知
						mEventSender.sendEvent(EventObserver.EventTarget_Setting, EventObserver.EventType_FileInfoModified, 0);
						mEventSender.setModified();

						selectCurrentItem();
						update = true;
					}
				}
				else if(col == Constant.TABLE_COLUMN_SPLITTYPE){
					int splitType = Constant.getSplitType(value);
					if(splitType != param.getSplitType()){
						int selected = getSelectedRow();
						if(param.getSplitIndex() != 0){
							selected -= param.getSplitIndex();
							if(info instanceof ImageFileInfoSplitWrapper){
								ImageFileInfoSplitWrapper wrap = (ImageFileInfoSplitWrapper)info;
								param = wrap.getFirstSplitInfo().getFilterParam();
							}
						}

						// SplitType変更有り
						param.setSplitType(splitType);
						update = true;
						//TODO:
						renewalList();
						selectItem(selected);
						return;
					}
				}
				else if(col == Constant.TABLE_COLUMN_TOC){
					info.setTocText(value);
					info.setModify(true);
				}
				if(update){
				    //if(mListener != null){
				    //	mListener.onItemSelected(row);
				    //}
					System.out.println("row=" + row + " col=" + col + " value=" + value);
				}
			}
		}
	}
	
	
	// Popup Menu
	private void updateSplitTypeAll(int splitType){
		int size = mInfoList.size();
		//String splitText = Constant.getSplitTypeText(splitType);
		for(int row=0; row<size; row++){
			IImageFileInfo info = mInfoList.get(row);
			info.getFilterParam().setSplitType(splitType);
			//mTableModel.setValueAt(splitText, row, Constant.TABLE_COLUMN_SPLITTYPE);
		}
		renewalList();
	}
	
	private void updateUncheckPageAll(){
		int size = mInfoList.size();
		for(int row=0; row<size; row++){
			IImageFileInfo info = mInfoList.get(row);
			info.getFilterParam().setPageType(Constant.PAGETYPE_AUTO);
			mTableModel.setValueAt(Constant.TEXT_PAGETYPE_AUTO, row, Constant.TABLE_COLUMN_PAGETYPE);
		}		
	}
	
	private void updatePicturePageAll(){
		int size = mInfoList.size();
		for(int row=0; row<size; row++){
			IImageFileInfo info = mInfoList.get(row);
			info.getFilterParam().setPageType(Constant.PAGETYPE_PICT);
			mTableModel.setValueAt(Constant.TEXT_PAGETYPE_PICT, row, Constant.TABLE_COLUMN_PAGETYPE);
		}		
	}
	
	private void updateTextPageAll(){
		int size = mInfoList.size();
		for(int row=0; row<size; row++){
			IImageFileInfo info = mInfoList.get(row);
			info.getFilterParam().setPageType(Constant.PAGETYPE_TEXT);
			mTableModel.setValueAt(Constant.TEXT_PAGETYPE_TEXT, row, Constant.TABLE_COLUMN_PAGETYPE);
		}
	}
	
	private void updatePicturePageCheckAll(){
		if(mInfoList == null){
			return;
		}
		
		ImageFileInfoAsyncTask task = new ImageFileInfoAsyncTask(mInfoList, new OnTaskObserver(){
			@Override
			public void onStart() {
				mEventSender.startProgress();
			}
			@Override
			public void onFinish() {
				mEventSender.stopProgress();
				// FileInfo更新通知
				mEventSender.sendEvent(EventObserver.EventTarget_Setting, EventObserver.EventType_FileInfoModified, 0);
			}
			@Override
			public void onProcess(int index, int total, IImageFileInfo info) {
				
				mEventSender.setProgressMessage(String.format("%d / %d", (index+1), total));
				
				String disableValue = Constant.TEXT_PAGETYPE_PICT;
				int pageType = info.getFilterParam().getPageType();
				if(pageType != Constant.PAGETYPE_PICT){
					InputStream stream = info.getInputStream();
					BufferedImage image = BufferedImageIO.read(stream, info.isJpeg());
					PageCheckFilter checker = new PageCheckFilter(true);
					ImageFilterParam filterParam = info.getFilterParam();
					if(mBaseFilterParams != null){
						synchronized(this){
							filterParam = mBaseFilterParams.get(ImageFilterParamSet.FILTER_INDEX_TEXT).createMergedFilterParam(filterParam);
						}
					}
					try {
						boolean whitePage = checker.isWhiteImage(image, filterParam, true);
						if(!whitePage){
							info.getFilterParam().setPageType(Constant.PAGETYPE_PICT);
							mTableModel.setValueAt(disableValue, index, 1);
						}
					}catch(Exception e){
						mEventSender.setProgressMessage(e.getMessage());
					}catch(OutOfMemoryError e){
						mEventSender.setProgressMessage(e.getMessage());
					}
					
				}
			}
		});
		task.start();

		/*
		int size = mInfoList.size();
		for(int row=0; row<size; row++){
			try {
				IImageFileInfo info = mInfoList.get(row);
				String disableValue = Constant.TEXT_PAGETYPE_PICT;
				int pageType = info.getFilterParam().getPageType();
				if(pageType != Constant.PAGETYPE_AUTO){
					InputStream stream = info.getInputStream();
					BufferedImage image = BufferedImageIO.read(stream, info.isJpeg());
					PageCheckFilter checker = new PageCheckFilter(true, null);
					boolean whitePage = checker.isWhiteImage(image, info.getFilterParam());
					if(!whitePage){
						info.getFilterParam().setPageType(Constant.PAGETYPE_PICT);
						mTableModel.setValueAt(disableValue, row, 1);
					}
				}
			} catch (ZipException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			row++;
		}
		*/
	}
	
	
	public void clearData(){
		mTableModel.setRowCount(0);
	}
	
	public void addData(IImageFileInfo info){
		String[] record = createRecord(info);
		mTableModel.addRow(record);
	}
	
	public void updateData(int row, IImageFileInfo info){
		String[] record = createRecord(info);
		for(int col=0; col<record.length; col++){
			mTableModel.setValueAt(record[col], row, col);
		}
	}
	
	private String[] createRecord(IImageFileInfo info){
		
//		TABLE_HEADER_ENTRYNAME,
//		TABLE_HEADER_PAGETYPE,
//		TABLE_HEADER_ROTATE,
//		TABLE_HEADER_POSITION,
//		TABLE_HEADER_WIDTH,
//		TABLE_HEADER_HEIGHT,
//		TABLE_HEADER_SIZE,
//		TABLE_HEADER_SPLIT,

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

	
	private void setEventListener(){
		getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if(e.getValueIsAdjusting()){
					return;
				}
				int selected = getSelectedRow();
				onItemSelected(selected);
			}
		});
			
		getTableHeader().addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e) {
				if(javax.swing.SwingUtilities.isRightMouseButton(e)){
					int column = getTableHeader().columnAtPoint(e.getPoint());
					if(column == Constant.TABLE_COLUMN_PAGETYPE){
						JPopupMenu popup = new JPopupMenu();
						JMenuItem item0 = new JMenuItem("一括クリア");
						JMenuItem item1 = new JMenuItem("一括挿絵");
						JMenuItem item2 = new JMenuItem("一括本文");
						JMenuItem item3 = new JMenuItem("挿絵チェック");
						item0.addActionListener(new ActionListener(){
							@Override
							public void actionPerformed(ActionEvent arg0) {
								updateUncheckPageAll();
							}
						});
						item1.addActionListener(new ActionListener(){
							@Override
							public void actionPerformed(ActionEvent arg0) {
								updatePicturePageAll();
							}
						});
						item2.addActionListener(new ActionListener(){
							@Override
							public void actionPerformed(ActionEvent arg0) {
								updateTextPageAll();
							}
						});
						item3.addActionListener(new ActionListener(){
							@Override
							public void actionPerformed(ActionEvent arg0) {
								updatePicturePageCheckAll();
							}
						});
						popup.add(item0);
						popup.add(item1);
						popup.add(item2);
						popup.add(item3);
						popup.show(e.getComponent(), e.getX(), e.getY());
					}
					else if(column == Constant.TABLE_COLUMN_SPLITTYPE){
						JPopupMenu popup = new JPopupMenu();
						JMenuItem item0 = new JMenuItem("一括（分割なし）");
						JMenuItem item1 = new JMenuItem("一括（右→左）");
						JMenuItem item2 = new JMenuItem("一括（左→右）");
						JMenuItem item3 = new JMenuItem("一括（順Ｚ 2x2）");
						JMenuItem item4 = new JMenuItem("一括（逆Ｚ 2x2）");
						JMenuItem item5 = new JMenuItem("一括（順Ｚ 3x3）");
						JMenuItem item6 = new JMenuItem("一括（逆Ｚ 3x3）");
						item0.addActionListener(new ActionListener(){
							@Override
							public void actionPerformed(ActionEvent arg0) {
								updateSplitTypeAll(SplitFilter.TYPE_NONE);
							}
						});
						item1.addActionListener(new ActionListener(){
							@Override
							public void actionPerformed(ActionEvent arg0) {
								updateSplitTypeAll(SplitFilter.TYPE_R2L_2);
							}
						});
						item2.addActionListener(new ActionListener(){
							@Override
							public void actionPerformed(ActionEvent arg0) {
								updateSplitTypeAll(SplitFilter.TYPE_L2R_2);
							}
						});
						item3.addActionListener(new ActionListener(){
							@Override
							public void actionPerformed(ActionEvent arg0) {
								updateSplitTypeAll(SplitFilter.TYPE_R2L_2x2);
							}
						});
						item4.addActionListener(new ActionListener(){
							@Override
							public void actionPerformed(ActionEvent arg0) {
								updateSplitTypeAll(SplitFilter.TYPE_L2R_2x2);
							}
						});
						item5.addActionListener(new ActionListener(){
							@Override
							public void actionPerformed(ActionEvent arg0) {
								updateSplitTypeAll(SplitFilter.TYPE_R2L_3x3);
							}
						});
						item6.addActionListener(new ActionListener(){
							@Override
							public void actionPerformed(ActionEvent arg0) {
								updateSplitTypeAll(SplitFilter.TYPE_L2R_3x3);
							}
						});
						popup.add(item0);
						popup.add(item1);
						popup.add(item2);
						popup.add(item3);
						popup.add(item4);
						popup.add(item5);
						popup.add(item6);
						popup.show(e.getComponent(), e.getX(), e.getY());
					}
				}
			}
		});
		
		final JTable table = this;
		this.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e) {
				if(javax.swing.SwingUtilities.isRightMouseButton(e)){
					int column = getTableHeader().columnAtPoint(e.getPoint());
					System.out.println("click col=" + column);
					JPopupMenu popup = new JPopupMenu();
					JMenuItem item0 = new JMenuItem("選択アイテム削除");
					item0.addActionListener(new ActionListener(){
						@Override
						public void actionPerformed(ActionEvent e) {
							int rowCount = table.getRowCount();
							//int listIndex = 0;
							for(int i=0; i<rowCount; i++){
								if(table.isRowSelected(i)){
									mInfoList.get(i).setEnable(false);
									//mInfoList.remove(listIndex);
									//continue;
								}
								//listIndex++;
							}
							
							renewalList();
							//int selected = table.getSelectedRow();
							//deleteItem(selected);
						}
					});
					JMenuItem item1 = new JMenuItem("選択アイテム削除取り消し");
					item1.addActionListener(new ActionListener(){
						@Override
						public void actionPerformed(ActionEvent e) {
							int rowCount = table.getRowCount();
							//int listIndex = 0;
							for(int i=0; i<rowCount; i++){
								if(table.isRowSelected(i)){
									mInfoList.get(i).setEnable(true);
									//mInfoList.remove(listIndex);
									//continue;
								}
								//listIndex++;
							}
							
							renewalList();
							//int selected = table.getSelectedRow();
							//deleteItem(selected);
						}
					});
					JMenuItem item2 = new JMenuItem("選択アイテム種別変更（カラー）");
					item2.addActionListener(new ActionListener(){
						@Override
						public void actionPerformed(ActionEvent e) {
							int rowCount = table.getRowCount();
							//int listIndex = 0;
							for(int i=0; i<rowCount; i++){
								if(table.isRowSelected(i)){
									IImageFileInfo info = mInfoList.get(i);
									info.getFilterParam().setPageType(Constant.PAGETYPE_COLOR);
									//mInfoList.remove(listIndex);
									//continue;
								}
								//listIndex++;
							}
							
							renewalList();
							//int selected = table.getSelectedRow();
							//deleteItem(selected);
						}
					});
					JMenuItem item3 = new JMenuItem("選択アイテム種別変更（挿絵）");
					item3.addActionListener(new ActionListener(){
						@Override
						public void actionPerformed(ActionEvent e) {
							int rowCount = table.getRowCount();
							//int listIndex = 0;
							for(int i=0; i<rowCount; i++){
								if(table.isRowSelected(i)){
									IImageFileInfo info = mInfoList.get(i);
									info.getFilterParam().setPageType(Constant.PAGETYPE_PICT);
									//mInfoList.remove(listIndex);
									//continue;
								}
								//listIndex++;
							}
							
							renewalList();
							//int selected = table.getSelectedRow();
							//deleteItem(selected);
						}
					});
					JMenuItem item4 = new JMenuItem("選択アイテム種別変更（本文）");
					item4.addActionListener(new ActionListener(){
						@Override
						public void actionPerformed(ActionEvent e) {
							int rowCount = table.getRowCount();
							//int listIndex = 0;
							for(int i=0; i<rowCount; i++){
								if(table.isRowSelected(i)){
									IImageFileInfo info = mInfoList.get(i);
									info.getFilterParam().setPageType(Constant.PAGETYPE_TEXT);
									//mInfoList.remove(listIndex);
									//continue;
								}
								//listIndex++;
							}
							
							renewalList();
							//int selected = table.getSelectedRow();
							//deleteItem(selected);
						}
					});
					JMenuItem item5 = new JMenuItem("選択アイテム種別変更（未設定）");
					item5.addActionListener(new ActionListener(){
						@Override
						public void actionPerformed(ActionEvent e) {
							int rowCount = table.getRowCount();
							//int listIndex = 0;
							for(int i=0; i<rowCount; i++){
								if(table.isRowSelected(i)){
									IImageFileInfo info = mInfoList.get(i);
									info.getFilterParam().setPageType(Constant.PAGETYPE_AUTO);
									//mInfoList.remove(listIndex);
									//continue;
								}
								//listIndex++;
							}
							
							renewalList();
							//int selected = table.getSelectedRow();
							//deleteItem(selected);
						}
					});
					popup.add(item0);
					popup.add(item1);
					popup.add(item2);
					popup.add(item3);
					popup.add(item4);
					popup.add(item5);

					popup.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});
		
		mTableModel.addTableModelListener(new TableModelListener(){
			@Override
			public void tableChanged(TableModelEvent event) {
				int row = event.getLastRow();
				int col = event.getColumn();
				if(row >= 0 && col >= 0){
					String value = (String)mTableModel.getValueAt(row, col);
					onTableCellChanged(row, col, value);
				}
			}
		});
		
		// PageType Column
		JComboBox pageBox = new JComboBox(new String[] {
				Constant.TEXT_PAGETYPE_AUTO,
				Constant.TEXT_PAGETYPE_TEXT,
				Constant.TEXT_PAGETYPE_PICT,
				Constant.TEXT_PAGETYPE_COLOR,
		});
		pageBox.setBorder(BorderFactory.createEmptyBorder()); 

		TableColumn pageTypeColum = getColumnModel().getColumn(Constant.TABLE_COLUMN_PAGETYPE);
		pageTypeColum.setCellEditor(new DefaultCellEditor(pageBox));

		
		// SplitType Column
		JComboBox splitBox = new JComboBox(new String[]{
			Constant.TEXT_SPLITTYPE_NONE,
			Constant.TEXT_SPLITTYPE_R2L,
			Constant.TEXT_SPLITTYPE_L2R,
			Constant.TEXT_SPLITTYPE_R2L_2x2,
			Constant.TEXT_SPLITTYPE_L2R_2x2,
			Constant.TEXT_SPLITTYPE_R2L_3x3,
			Constant.TEXT_SPLITTYPE_L2R_3x3,
		});
		splitBox.setBorder(BorderFactory.createEmptyBorder()); 
		
		TableColumn splitTypeColum = getColumnModel().getColumn(Constant.TABLE_COLUMN_SPLITTYPE);
		splitTypeColum.setCellEditor(new DefaultCellEditor(splitBox));
		
		
		// PageSpread Column
		JComboBox spreadBox = new JComboBox(new String[]{
				Constant.PAGESPREAD_AUTO,
				Constant.PAGESPREAD_CENTER,
				Constant.PAGESPREAD_LEFT,
				Constant.PAGESPREAD_RIGHT,
			});
		spreadBox.setBorder(BorderFactory.createEmptyBorder()); 
			
		TableColumn spreadTypeColum = getColumnModel().getColumn(Constant.TABLE_COLUMN_PAGESPREAD);
		spreadTypeColum.setCellEditor(new DefaultCellEditor(spreadBox));
	}
	
	public void deleteItem(int row){
		if(mInfoList != null){
			if(row < 0 || mInfoList.size() <= row){
				return;
			}
			IImageFileInfoList list = mInfoList;
			list.remove(row);
			setImageFileInfoList(list);
		}
	}
	
	public void renewalList(){
		if(mInfoList != null){
			setImageFileInfoList(mInfoList.renew());
			mEventSender.setModified();
		}
	}

	@Override
	public void onEventReceived(int type, int arg1, int arg2, Object obj) {
		switch(type){
		case EventObserver.EventType_UpdateFileInfo:
			IImageFileInfo info = mInfoList.get(arg1);
			updateData(arg1, info);
			break;
		case EventObserver.EventType_MoveInfo:
			int nextIndex = getSelectedRow() + arg1;
			if(nextIndex >= 0 && getRowCount() > nextIndex){
				setRowSelectionInterval(nextIndex, nextIndex);
				scrollRectToVisible(new Rectangle(getCellRect(nextIndex, 0, true)));
			}
			break;
		case EventObserver.EventType_RenewalList:
			renewalList();
			break;
		default:
			break;
		}
	}
}
