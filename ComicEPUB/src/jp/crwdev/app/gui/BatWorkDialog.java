package jp.crwdev.app.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.dnd.DropTarget;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import jp.crwdev.app.EventObserver;
import jp.crwdev.app.OutputSettingParam;
import jp.crwdev.app.constant.Constant;
import jp.crwdev.app.container.ImageFilePreconverter;
import jp.crwdev.app.container.ImageFileScanner;
import jp.crwdev.app.imagefilter.AutoCropFilter;
import jp.crwdev.app.imagefilter.ImageFilterParam;
import jp.crwdev.app.imagefilter.OutputImageFilter;
import jp.crwdev.app.interfaces.IImageFileInfo;
import jp.crwdev.app.interfaces.IImageFileInfoList;
import jp.crwdev.app.interfaces.IImageFileScanner;
import jp.crwdev.app.interfaces.IImageFileWriter;
import jp.crwdev.app.interfaces.IImageFileWriter.OnProgressListener;
import jp.crwdev.app.setting.ImageFilterParamSet;
import jp.crwdev.app.setting.XmlWriter;
import jp.crwdev.app.util.FileListDropTargetAdapter;
import jp.crwdev.app.util.InifileProperty;
import jp.crwdev.app.util.FileListDropTargetAdapter.OnDropFilesListener;

public class BatWorkDialog extends JDialog implements OnDropFilesListener {

	public static final String[] TABLE_HEADER_COLUMNS = new String[]{
		"状態",
		"入力パス",
		"タイトル",
		"著者名",
		"タイトルカナ",
		"著者名カナ",
		"出力パス",
		"出力サイズ",
		"ファイル種別",
		"Book種別",
		"シリーズ名",
		"巻数",
		"シリーズ名カナ",
		"固定サイズ出力",
	};

	private static final int TABLE_INDEX_STATUS = 0;
	private static final int TABLE_INDEX_INPUT_PATH = 1;
	private static final int TABLE_INDEX_TITLE = 2;
	private static final int TABLE_INDEX_AUTHOR = 3;
	private static final int TABLE_INDEX_TITLE_KANA = 4;
	private static final int TABLE_INDEX_AUTHOR_KANA = 5;
	private static final int TABLE_INDEX_OUTPUT_NAME = 6;
	private static final int TABLE_INDEX_IMAGESIZE = 7;
	private static final int TABLE_INDEX_FILETYPE = 8;
	private static final int TABLE_INDEX_BOOKTYPE = 9;
	private static final int TABLE_INDEX_SERIES_TITLE = 10;
	private static final int TABLE_INDEX_SERIES_NUMBER = 11;
	private static final int TABLE_INDEX_SERIES_TITLE_KANA = 12;
	private static final int TABLE_INDEX_OUTPUT_FIXED_SIZE = 13;
	
	
	private final JPanel contentPanel = new JPanel();
	private JTable table;
	private JTextField textFolder;
	private JButton folderButton;
	private JButton convertButton;
	private boolean mIsProcessing = false;
	private boolean mIsCancel = false;
	
	private DefaultTableModel mTableModel = null;
	
	private Object mLock = new Object();
	private IImageFileWriter mFileWriter = null;

	private MainFrame mParent;
	
	public BatWorkDialog(JFrame mParentFrame){
		super(mParentFrame, true);
		
		initialize();
		
		addWindowListener(new WindowAdapter(){
	    	 @Override
	    	 public void windowClosing(WindowEvent e) {
	    		 System.out.println("windowClosing");
	    		 if(mIsProcessing){
	    			 JOptionPane.showMessageDialog(BatWorkDialog.this, "処理中です。");
	    		 }
	    		 else{
	    			 dispose();
	    		 }
	    	 }
		});
		
		new DropTarget(this, new FileListDropTargetAdapter(this));
		

		setTitle("一括変換");
		setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		setVisible(true);
	}
	
	public void initialize(){
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(3, 3, 3, 3));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout());
		
		table = new JTable();
		JScrollPane scroll = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		contentPanel.add(scroll);
		{
			JPanel buttonPane = new JPanel();
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.X_AXIS));
			{
				textFolder = new JTextField();
				buttonPane.add(textFolder);
				textFolder.setColumns(10);
			}
			{
				folderButton = new JButton("出力フォルダ");
				buttonPane.add(folderButton);
				folderButton.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent arg0) {
						showOutputFolderDialog();
					}
				});
			}
			{
				convertButton = new JButton("変換");
				buttonPane.add(convertButton);
				convertButton.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent arg0) {
						if(mIsProcessing){
							cancelConvert();
						}else{
							beginConvert();
						}
					}
				});
			}
		}
		
		
		mTableModel = new DefaultTableModel(TABLE_HEADER_COLUMNS, 0){
			@Override
			public boolean isCellEditable(int row, int column) {
				return (column > 1);	// 0,1カラム目の編集禁止
			}
			
			@Override
			public Class getColumnClass(int col){
		        return getValueAt(0, col).getClass();
		    }
		};

		table.setModel(mTableModel);
		
		for(int i=0; i<TABLE_HEADER_COLUMNS.length; i++){
	        TableColumn col = table.getColumnModel().getColumn( i );
	        col.setMinWidth(50);
		}
		table.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		
		
		// ImageSize Column
		List<String> imageSizeList = InifileProperty.getInstance().getImageSizeList();
		String[] imageSizeType = new String[imageSizeList.size()+1];
		int i=0;
		for(String size : imageSizeList){
			imageSizeType[i++] = size;
		}
		imageSizeType[i] = "リサイズ無し";
		
		JComboBox imageSizeBox = new JComboBox(imageSizeType);
		TableColumn imageSizeColum = table.getColumnModel().getColumn(TABLE_INDEX_IMAGESIZE);
		imageSizeColum.setCellEditor(new DefaultCellEditor(imageSizeBox));


		// FileType Column
		JComboBox fileTypeBox = new JComboBox(new String[] {
				"zip",
				"epub",
				"pdf",
				"folder"
		});
		fileTypeBox.setBorder(BorderFactory.createEmptyBorder()); 

		TableColumn fileTypeColum = table.getColumnModel().getColumn(TABLE_INDEX_FILETYPE);
		fileTypeColum.setCellEditor(new DefaultCellEditor(fileTypeBox));

		// BookType Column
		JComboBox bookTypeBox = new JComboBox(new String[] {
				"book",
				"magazin",
				"comic"
		});
		bookTypeBox.setBorder(BorderFactory.createEmptyBorder()); 

		TableColumn bookTypeColum = table.getColumnModel().getColumn(TABLE_INDEX_BOOKTYPE);
		bookTypeColum.setCellEditor(new DefaultCellEditor(bookTypeBox));
		
		
		
		mTableModel.addTableModelListener(new TableModelListener(){
			@Override
			public void tableChanged(TableModelEvent event) {
				int row = event.getLastRow();
				int col = event.getColumn();
				if(row >= 0 && col >= 0){
					if(col != TABLE_INDEX_OUTPUT_FIXED_SIZE){
						String value = (String)mTableModel.getValueAt(row, col);
						onTableCellChanged(row, col, value);
					}
				}
			}
		});

	}

	private void onTableCellChanged(int row, int col, String value){
		if(col == TABLE_INDEX_FILETYPE){
			if(!value.equalsIgnoreCase("folder")){
				String filename = (String)mTableModel.getValueAt(row, TABLE_INDEX_OUTPUT_NAME);
				int dotIndex = filename.lastIndexOf(".");
				if(dotIndex >= 0){
					filename = filename.substring(0, dotIndex);
				}
				filename += "." + value;
				
				mTableModel.setValueAt(filename, row, TABLE_INDEX_OUTPUT_NAME);
			}
		}
		else if(col == TABLE_INDEX_SERIES_NUMBER){
			if(!value.isEmpty()){
				try{
					Integer.parseInt(value);
				}catch(NumberFormatException e){
					mTableModel.setValueAt("", row, TABLE_INDEX_SERIES_NUMBER);
				}
			}
		}
	}
	
	private void showOutputFolderDialog(){
		JFileChooser filechooser = new JFileChooser();
		filechooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		File defaultFolder = new File(InifileProperty.getInstance().getOutputFolder());
		if(defaultFolder.exists() && defaultFolder.isDirectory()){
			filechooser.setSelectedFile(defaultFolder);
		}
		int selected = filechooser.showOpenDialog(mParent);
		if(selected == JFileChooser.APPROVE_OPTION){
			 File file = filechooser.getSelectedFile();
			 textFolder.setText(file.getAbsolutePath());
			 InifileProperty.getInstance().setOutputFolder(file.getAbsolutePath());
		}
	}
	
	public void addData(String contentPath, OutputSettingParam output){
		Object[] record = createRecord(contentPath, output);
		mTableModel.addRow(record);
	}
	
	
	private Object[] createRecord(String contentPath, OutputSettingParam output){
		
//		private static final int TABLE_INDEX_INPUT_PATH = 0;
//		private static final int TABLE_INDEX_TITLE = 1;
//		private static final int TABLE_INDEX_AUTHOR = 2;
//		private static final int TABLE_INDEX_TITLE_KANA = 3;
//		private static final int TABLE_INDEX_AUTHOR_KANA = 4;
//		private static final int TABLE_INDEX_OUTPUT_PATH = 5;
//		private static final int TABLE_INDEX_IMAGESIZE = 6;
//		private static final int TABLE_INDEX_FILETYPE = 7;
//		private static final int TABLE_INDEX_BOOKTYPE = 8;
//		
		String title = output.getTitle();
		String titleKana = output.getTitleKana();
		String author = output.getAuthor();
		String authorKana = output.getAuthorKana();
		String filename = output.getOutputFileName(null);
		Dimension size = output.getImageSize();
		String imagesize = size.width + "x" + size.height;
		String fileType = output.getFileType();
		String bookType = output.getEpubType();
		String seriesTitle = output.getSeriesTitle();
		String seriesNumber = Integer.toString(output.getSeriesNumber());
		String seriesTitleKana = output.getSeriesTitleKana();
		Boolean fixedSize = new Boolean(output.isFixedSize());
		
		return new Object[]{"未変換", contentPath, title, author, titleKana, authorKana, filename, imagesize, fileType, bookType,
				seriesTitle, seriesNumber, seriesTitleKana, fixedSize};
	}

	@Override
	public void onDrop(List<String> paths) {
		
		XmlWriter xml = new XmlWriter();
		for(int i=0; i<paths.size(); i++){
			File settingFile = Constant.getSettingFile(new File(paths.get(i)));
			if(settingFile != null){
				if(xml.openLoadSettingFile(settingFile.getAbsolutePath())){
					OutputSettingParam output = new OutputSettingParam("","","","");
					xml.loadSetting(output, null, null);
					addData(paths.get(i), output);
				}
			}
		}
		
	}
	
	
	private void beginConvert(){
		String outputFolder = textFolder.getText();
		if(outputFolder.isEmpty()){
			JOptionPane.showMessageDialog(this, "出力先フォルダが指定されていません。");
			return;
		}
		
		File outFolder = new File(outputFolder);
		if(!outFolder.exists() || !outFolder.isDirectory()){
			JOptionPane.showMessageDialog(this, "出力先にはフォルダを指定してください。");
			return;
		}
		
		int count = mTableModel.getRowCount();
		if(count <= 0){
			return;
		}
		
		table.clearSelection();
		
		mIsProcessing = true;
		mIsCancel = false;
		
		textFolder.setEnabled(false);
		folderButton.setEnabled(false);
		convertButton.setText("キャンセル");
		
		Thread thread = new Thread(){
			public void run(){
				String outputFolder = textFolder.getText();
				int count = mTableModel.getRowCount();
				
				for(int i=0; i<count; i++){
					try {
						doConvert(outputFolder, i);
					}catch(Exception e){
						e.printStackTrace();
					}
					if(mIsCancel){
						break;
					}
				}
				
				textFolder.setEnabled(true);
				folderButton.setEnabled(true);
				convertButton.setText("変換");
				mIsProcessing = false;
			}
		};
		thread.setPriority(3);
		thread.start();
	}
	
	private void cancelConvert(){
		mIsCancel = true;
		synchronized(mLock){
			if(mFileWriter != null){
				mFileWriter.cancel();
			}
		}
	}

	private String getContentPath(int index){
		if(index < 0 || mTableModel.getRowCount() <= index){
			return null;
		}
		String contentPath = (String)mTableModel.getValueAt(index, TABLE_INDEX_INPUT_PATH);
		return contentPath;
	}

	private OutputSettingParam getOutputSettingParam(int index){
		if(index < 0 || mTableModel.getRowCount() <= index){
			return null;
		}
		try {
			String title = (String)mTableModel.getValueAt(index, TABLE_INDEX_TITLE);
			String titleKana = (String)mTableModel.getValueAt(index, TABLE_INDEX_TITLE_KANA);
			String author = (String)mTableModel.getValueAt(index, TABLE_INDEX_AUTHOR);
			String authorKana = (String)mTableModel.getValueAt(index, TABLE_INDEX_AUTHOR_KANA);
			String filename = (String)mTableModel.getValueAt(index, TABLE_INDEX_OUTPUT_NAME);
			String imageSize = (String)mTableModel.getValueAt(index, TABLE_INDEX_IMAGESIZE);
			String fileType = (String)mTableModel.getValueAt(index, TABLE_INDEX_FILETYPE);
			String bookType = (String)mTableModel.getValueAt(index, TABLE_INDEX_BOOKTYPE);
			String seriesTitle = (String)mTableModel.getValueAt(index, TABLE_INDEX_SERIES_TITLE);
			String seriesNumber = (String)mTableModel.getValueAt(index, TABLE_INDEX_SERIES_NUMBER);
			String seriesTitleKana = (String)mTableModel.getValueAt(index, TABLE_INDEX_SERIES_TITLE_KANA);
			Boolean fixedSize = (Boolean)mTableModel.getValueAt(index, TABLE_INDEX_OUTPUT_FIXED_SIZE);
			
			OutputSettingParam param = new OutputSettingParam("", fileType, bookType, imageSize);
			param.setTitle(title);
			param.setTitleKana(titleKana);
			param.setAuthor(author);
			param.setAuthorKana(authorKana);
			param.setOutputFileName(filename);
			
			param.setSeriesTitle(seriesTitle);
			if(!seriesNumber.isEmpty()){
				param.setSeriesNumber(Integer.parseInt(seriesNumber));
			}
			param.setSeriesTitleKana(seriesTitleKana);
		
			param.setFixedSize(fixedSize);
			
			return param;
			
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	private void updateStatus(int index, String status){
		if(index < 0 || mTableModel.getRowCount() <= index){
			return;
		}

		mTableModel.setValueAt(status, index, TABLE_INDEX_STATUS);
	}

	
	private void doConvert(String outputFolder, int index){
		String contentPath = getContentPath(index);
		OutputSettingParam output = getOutputSettingParam(index);
		output.setOutputPath(outputFolder);
		
		updateStatus(index, "変換中");
		
		IImageFileScanner scanner = ImageFileScanner.getFileScanner(contentPath);
		
		IImageFileInfoList list = scanner.getImageFileInfoList();
		
		File settingFile = Constant.getSettingFile(new File(contentPath));
		if(settingFile == null){
			updateStatus(index, "失敗：設定ファイルが見つかりません");
			return;
		}
		
		list.sort();

		//ImageFilterParam param = null;
		ImageFilterParamSet params = new ImageFilterParamSet();
		if(settingFile.exists()){

			XmlWriter loader = new XmlWriter();
			loader.openLoadSettingFile(settingFile.getAbsolutePath());
			// listに変更を反映する
			loader.loadSetting(null, params, list);
		}
		
		params.setPreview(false);
		
		// 出力サイズを設定
		Dimension size = output.getImageSize();
		if(size == null || (size.width == 0 || size.height == 0)){
			params.setResize(false);
		}else{
			params.setResize(true);
			params.setResizeDimension(size);
		}
		
		if(output.isFixedSize()){
			// 固定出力サイズチェック
			OutputImageFilter preconvertFilter = new OutputImageFilter(params, false);
			ImageFilePreconverter checker = new ImageFilePreconverter(preconvertFilter);
			checker.write(list, null);
			Dimension unionSize = checker.getUnionSize();
			params.setFixedSize(unionSize);
		}

		// 基本出力フィルタを生成
		OutputImageFilter imageFilter = new OutputImageFilter(params, output.isFixedSize());
		
		// 出力設定からImageFileWriterを生成
		IImageFileWriter writer = output.getImageFileWriter();
		writer.setImageFilter(imageFilter);

		// 出力ファイル(フォルダ)パスを作成
		File dir = new File(output.getFinalOutputPath());
		File file = new File(dir.getAbsolutePath(), output.getOutputFileName(writer.getSuffix()));
		if(!dir.exists()){
			if(!dir.mkdirs()){
				updateStatus(index, "失敗：出力フォルダを作成できませんでした");
				return;
				//throw new Exception("can't create output folder.");
			}
		}
		
		// 出力ファイル(フォルダ)オープン
		if(writer.open(file.getAbsolutePath())){
			synchronized(mLock){
				mFileWriter = writer; // ここからCancel可能
			}
			// 出力処理開始
			final int rowIndex = index;
			boolean result = writer.write(list, new OnProgressListener(){
				private int preProgress = 0;
				@Override
				public void onProgress(int progress, String message) {
					int val = (progress / 10) * 10;
					if(val != preProgress){
						preProgress = val;
						if(message == null){
							message = progress + "%";
						}
						updateStatus(rowIndex, message);
					}
				}
			});
			// 出力終了
			synchronized(mLock){
				mFileWriter = null;
			}
			writer.close();
			
			if(mIsCancel){
				updateStatus(index, "キャンセル");
			}else{
				if(result){
					updateStatus(index, "変換済み");
				}else{
					updateStatus(index, "変換失敗");
				}
			}
		}
		else{
			updateStatus(index, "失敗：出力先を開けませんでした");
		}
		
		scanner.close();
		scanner = null;
		
		
		
	}
}
