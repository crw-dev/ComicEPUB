package jp.crwdev.app.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;
import java.io.File;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpringLayout;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import jp.crwdev.app.OutputSettingParam;
import jp.crwdev.app.imagefilter.ImageFilterParam;
import jp.crwdev.app.util.InifileProperty;

public class SettingComponent {
	
	private boolean mIsInputFileName = false;
	private String mOutputFileName = "";
	
	// Label
	public JLabel labelImageSize = new JLabel("出力サイズ");
	//public JLabel labelBookType = new JLabel("EPUB種別");
	public JLabel labelFileType = new JLabel("保存形式");
	public JLabel labelGamma = new JLabel("ガンマ補正値");
	public JLabel labelContrast = new JLabel("Co:0");
	public JLabel labelBrightness = new JLabel("Br:0");
	public JLabel labelMessage = new JLabel("");

	// CheckBox
	public JCheckBox filterEnable = new JCheckBox("無変換");
	public JCheckBox filterContrast = new JCheckBox("コントラスト");
	public JCheckBox filterGamma = new JCheckBox("ガンマ補正");
	public JCheckBox filterResize = new JCheckBox("リサイズ"); // 不要？
	public JCheckBox filterGrayscale = new JCheckBox("グレースケール");
	public JCheckBox filterPreview = new JCheckBox("編集モード");
	public JCheckBox filterUnification = new JCheckBox("本文ページ画像サイズ統一");
	public JCheckBox cropFullPage = new JCheckBox("全ページ切り抜き");
	public JCheckBox cropTextPage = new JCheckBox("本文ページ切り抜き");
	public JCheckBox cropPictPage = new JCheckBox("挿絵ページ切り抜き");
	
	public static final int CHECK_ID_ENABLE = 0;
	public static final int CHECK_ID_CONTRAST = 1;
	public static final int CHECK_ID_GAMMA = 2;
	public static final int CHECK_ID_RESIZE = 3;
	public static final int CHECK_ID_GRAYSCALE = 4;
	public static final int CHECK_ID_PREVIEW = 5;
	public static final int CHECK_ID_UNIFICATION = 6;
	public static final int CHECK_ID_CROP_FULL = 7;
	public static final int CHECK_ID_CROP_TEXT = 8;
	public static final int CHECK_ID_CROP_PICT = 9;
	
	private JCheckBox[] checkBoxes = new JCheckBox[]{
		filterEnable,
		filterContrast,
		filterGamma,
		filterResize,
		filterGrayscale,
		filterPreview,
		filterUnification,
		cropFullPage,
		cropTextPage,
		cropPictPage,
	};
	
	// ComboBox
	public JComboBox outputImageSize = new JComboBox();
	public JComboBox outputBookType = new JComboBox();
	public JComboBox outputFileType = new JComboBox();
	
	// Spinner
	public JSpinner cropFullLeft = new JSpinner();
	public JSpinner cropFullRight = new JSpinner();
	public JSpinner cropFullTop = new JSpinner();
	public JSpinner cropFullBottom = new JSpinner();
	
	public JSpinner cropTextLeft = new JSpinner();
	public JSpinner cropTextRight = new JSpinner();
	public JSpinner cropTextTop = new JSpinner();
	public JSpinner cropTextBottom = new JSpinner();

	public JSpinner cropPictLeft = new JSpinner();
	public JSpinner cropPictRight = new JSpinner();
	public JSpinner cropPictTop = new JSpinner();
	public JSpinner cropPictBottom = new JSpinner();
	
	public JSpinner gammaValue = new JSpinner();

	// Slider
	public JSlider contrastValue = new JSlider();
	public JSlider brightnessValue = new JSlider();
	
	// TextField
	public DefaultTextField outputFolder = new DefaultTextField(30, "出力先フォルダ");
	public DefaultTextField outputTitle = new DefaultTextField(12, "タイトル");
	public DefaultTextField outputTitleKana = new DefaultTextField(10, "タイトルカナ");
	public DefaultTextField outputAuthor = new DefaultTextField(12, "作者名");
	public DefaultTextField outputAuthorKana = new DefaultTextField(10, "作者名カナ");
	
	// Button
	public JButton chooseFolderButton = new JButton("出力先フォルダ");
	public JButton convertButton = new JButton("変換");
	public JButton cancelButton = new JButton("Cancel");

	// ProgressBar
	public JProgressBar progressBar = new JProgressBar();
	
	
	private SettingPanel mParent = null;
	
	public SettingComponent(SettingPanel parent){
		mParent = parent;
		addToComponent(parent);
	}
	
	private void addToComponent(JComponent parent){
		
		filterResize.setSelected(true);
		filterPreview.setSelected(true);
		
		// ProgressBar
		parent.add(progressBar);
		parent.add(labelMessage);
		parent.add(cancelButton);
		
		// Label
		parent.add(labelImageSize);
		//parent.add(labelBookType);
		parent.add(labelFileType);
		parent.add(labelGamma);
		parent.add(labelContrast);
		parent.add(labelBrightness);

		// CheckBox
		parent.add(filterEnable);
		parent.add(filterContrast);
		parent.add(filterGamma);
		parent.add(filterResize);
		parent.add(filterGrayscale);
		parent.add(filterPreview);
		parent.add(filterUnification);
		parent.add(cropFullPage);
		parent.add(cropTextPage);
		parent.add(cropPictPage);
		
		filterEnable.setToolTipText("画像ファイルを変換せずに出力します。（未実装）");
		filterResize.setToolTipText("プレビュー画面内に収まるようにリサイズして表示します。");
		filterPreview.setToolTipText("各種編集操作を行えるようにします。");
		filterUnification.setToolTipText("本文ページのサイズを統一し中央寄せで表示します。");
		
		// ComboBox
		parent.add(outputImageSize);
		parent.add(outputBookType);
		parent.add(outputFileType);
		
		// Spinner
		parent.add(cropFullLeft);
		parent.add(cropFullRight);
		parent.add(cropFullTop);
		parent.add(cropFullBottom);
		
		parent.add(cropTextLeft);
		parent.add(cropTextRight);
		parent.add(cropTextTop);
		parent.add(cropTextBottom);

		parent.add(cropPictLeft);
		parent.add(cropPictRight);
		parent.add(cropPictTop);
		parent.add(cropPictBottom);
		
		parent.add(gammaValue);

		// Slider
		parent.add(contrastValue);
		parent.add(brightnessValue);
		
		// TextField
		parent.add(outputFolder);
		parent.add(outputTitle);
		parent.add(outputTitleKana);
		parent.add(outputAuthor);
		parent.add(outputAuthorKana);
		
		// Button
		parent.add(chooseFolderButton);
		parent.add(convertButton);
		
	}
	
	public void applyLayout(SpringLayout layout){
//		layout.putConstraint(SpringLayout.WEST, filterEnable, 5, SpringLayout.WEST, mParent);
//		layout.putConstraint(SpringLayout.WEST, filterContrast, 5, SpringLayout.WEST, mParent);
//		layout.putConstraint(SpringLayout.WEST, filterGamma, 5, SpringLayout.WEST, mParent);
//		layout.putConstraint(SpringLayout.WEST, filterResize, 5, SpringLayout.WEST, mParent);
//		layout.putConstraint(SpringLayout.WEST, filterGrayscale, 5, SpringLayout.WEST, mParent);
//		layout.putConstraint(SpringLayout.WEST, filterPreview, 5, SpringLayout.WEST, mParent);
		
		layout.putConstraint(SpringLayout.NORTH, filterEnable, 3, SpringLayout.NORTH, mParent);
		layout.putConstraint(SpringLayout.NORTH, filterGamma, 3, SpringLayout.SOUTH, filterEnable);
		layout.putConstraint(SpringLayout.NORTH, filterGrayscale, 3, SpringLayout.SOUTH, filterGamma);
		layout.putConstraint(SpringLayout.NORTH, filterPreview, 3, SpringLayout.SOUTH, filterGrayscale);
		layout.putConstraint(SpringLayout.NORTH, filterUnification, 3, SpringLayout.SOUTH, filterPreview);
		layout.putConstraint(SpringLayout.NORTH, filterContrast, 3, SpringLayout.SOUTH, filterUnification);
		
		// ResizeをPreviewの右に
		layout.putConstraint(SpringLayout.NORTH, filterResize, 0, SpringLayout.NORTH, filterPreview);
		layout.putConstraint(SpringLayout.WEST, filterResize, 3, SpringLayout.EAST, filterPreview);

		layout.putConstraint(SpringLayout.NORTH, contrastValue, 3, SpringLayout.SOUTH, filterContrast);
		layout.putConstraint(SpringLayout.NORTH, brightnessValue, 3, SpringLayout.SOUTH, contrastValue);
		
		layout.putConstraint(SpringLayout.NORTH, labelContrast, 0, SpringLayout.NORTH, contrastValue);
		layout.putConstraint(SpringLayout.WEST, labelContrast, 3, SpringLayout.EAST, contrastValue);
		layout.putConstraint(SpringLayout.NORTH, labelBrightness, 0, SpringLayout.NORTH, brightnessValue);
		layout.putConstraint(SpringLayout.WEST, labelBrightness, 3, SpringLayout.EAST, brightnessValue);
		
		contrastValue.setMinimum(-255);
		contrastValue.setMaximum(255);
		contrastValue.setValue(0);
		brightnessValue.setMinimum(-255);
		brightnessValue.setMaximum(255);
		brightnessValue.setValue(0);
		contrastValue.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent arg0) {
				int contrast = contrastValue.getValue();
				labelContrast.setText("Co:" + contrast);
			}
		});
		brightnessValue.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent arg0) {
				int brightness = brightnessValue.getValue();
				labelBrightness.setText("Br:" + brightness);
			}
		});
		contrastValue.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseReleased(MouseEvent arg0) {
				updateSettingValues();
			}
		});
		brightnessValue.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseReleased(MouseEvent arg0) {
				updateSettingValues();
			}
		});


		
		gammaValue.setPreferredSize(new Dimension(40, 20));
		gammaValue.setModel(new SpinnerNumberModel(1.0f, 0.0f, 3.0f, 0.1f));
		layout.putConstraint(SpringLayout.NORTH, filterGamma, 3, SpringLayout.SOUTH, filterEnable);
		layout.putConstraint(SpringLayout.VERTICAL_CENTER, gammaValue, 0, SpringLayout.VERTICAL_CENTER, filterGamma);
		layout.putConstraint(SpringLayout.VERTICAL_CENTER, labelGamma, 0, SpringLayout.VERTICAL_CENTER, filterGamma);
		layout.putConstraint(SpringLayout.WEST, gammaValue, 5, SpringLayout.EAST, filterGamma);
		layout.putConstraint(SpringLayout.WEST, labelGamma, 5, SpringLayout.EAST, gammaValue);

		
		layout.putConstraint(SpringLayout.NORTH, outputImageSize, 3, SpringLayout.SOUTH, brightnessValue);
		layout.putConstraint(SpringLayout.NORTH, outputFileType, 3, SpringLayout.SOUTH, outputImageSize);
		layout.putConstraint(SpringLayout.NORTH, outputBookType, 0, SpringLayout.NORTH, outputFileType);

		layout.putConstraint(SpringLayout.WEST, outputImageSize, 5, SpringLayout.WEST, mParent);
		layout.putConstraint(SpringLayout.WEST, outputFileType, 5, SpringLayout.WEST, mParent);
		layout.putConstraint(SpringLayout.WEST, outputBookType, 5, SpringLayout.EAST, outputFileType);

		layout.putConstraint(SpringLayout.WEST, labelImageSize, 5, SpringLayout.EAST, outputImageSize);
		layout.putConstraint(SpringLayout.SOUTH, labelImageSize, 0, SpringLayout.SOUTH, outputImageSize);
		//layout.putConstraint(SpringLayout.WEST, labelBookType, 5, SpringLayout.EAST, outputBookType);
		//layout.putConstraint(SpringLayout.SOUTH, labelBookType, 0, SpringLayout.SOUTH, outputBookType);
		layout.putConstraint(SpringLayout.WEST, labelFileType, 5, SpringLayout.EAST, outputBookType);
		layout.putConstraint(SpringLayout.SOUTH, labelFileType, 0, SpringLayout.SOUTH, outputFileType);

		// filename
		layout.putConstraint(SpringLayout.WEST, outputTitle, 0, SpringLayout.WEST, mParent);
		layout.putConstraint(SpringLayout.WEST, outputTitleKana, 3, SpringLayout.EAST, outputTitle);
		layout.putConstraint(SpringLayout.EAST, outputTitleKana, 0, SpringLayout.EAST, mParent);
		layout.putConstraint(SpringLayout.WEST, outputAuthor, 0, SpringLayout.WEST, mParent);
		layout.putConstraint(SpringLayout.WEST, outputAuthorKana, 3, SpringLayout.EAST, outputAuthor);
		layout.putConstraint(SpringLayout.EAST, outputAuthorKana, 0, SpringLayout.EAST, mParent);
		
		layout.putConstraint(SpringLayout.NORTH, outputTitle, 3, SpringLayout.SOUTH, labelFileType);
		layout.putConstraint(SpringLayout.NORTH, outputTitleKana, 0, SpringLayout.NORTH, outputTitle);
		layout.putConstraint(SpringLayout.NORTH, outputAuthor, 3, SpringLayout.SOUTH, outputTitle);
		layout.putConstraint(SpringLayout.NORTH, outputAuthorKana, 0, SpringLayout.NORTH, outputAuthor);
		
		//TODO
//		outputTitle.addFocusListener(new DefaultTextFocusListener("タイトル", outputTitle));
//		outputTitleKana.addFocusListener(new DefaultTextFocusListener("タイトルカナ", outputTitleKana));
//		outputAuthor.addFocusListener(new DefaultTextFocusListener("作者名", outputAuthor));
//		outputAuthorKana.addFocusListener(new DefaultTextFocusListener("作者名カナ", outputAuthorKana));
		
		
		layout.putConstraint(SpringLayout.WEST, outputFolder, 0, SpringLayout.WEST, mParent);
		layout.putConstraint(SpringLayout.EAST, outputFolder, 0, SpringLayout.EAST, mParent);
		layout.putConstraint(SpringLayout.NORTH, outputFolder, 3, SpringLayout.SOUTH, outputAuthor);
		layout.putConstraint(SpringLayout.NORTH, chooseFolderButton, 3, SpringLayout.SOUTH, outputFolder);
		layout.putConstraint(SpringLayout.NORTH, convertButton, 0, SpringLayout.NORTH, chooseFolderButton);
		layout.putConstraint(SpringLayout.WEST, convertButton, 3, SpringLayout.EAST, chooseFolderButton);
		
		cropFullTop.setPreferredSize(new Dimension(50, 20));
		cropFullLeft.setPreferredSize(new Dimension(50, 20));
		cropFullRight.setPreferredSize(new Dimension(50, 20));
		cropFullBottom.setPreferredSize(new Dimension(50, 20));
		cropFullTop.setModel(new SpinnerNumberModel(0, 0, 1000, 1));
		cropFullLeft.setModel(new SpinnerNumberModel(0, 0, 1000, 1));
		cropFullRight.setModel(new SpinnerNumberModel(0, 0, 1000, 1));
		cropFullBottom.setModel(new SpinnerNumberModel(0, 0, 1000, 1));
		layout.putConstraint(SpringLayout.NORTH, cropFullPage, 3, SpringLayout.SOUTH, convertButton);
		layout.putConstraint(SpringLayout.NORTH, cropFullTop, 3, SpringLayout.SOUTH, cropFullPage);
		layout.putConstraint(SpringLayout.NORTH, cropFullLeft, 3, SpringLayout.SOUTH, cropFullTop);
		layout.putConstraint(SpringLayout.NORTH, cropFullRight, 3, SpringLayout.SOUTH, cropFullTop);
		layout.putConstraint(SpringLayout.WEST, cropFullRight, 50, SpringLayout.EAST, cropFullLeft);
		layout.putConstraint(SpringLayout.NORTH, cropFullBottom, 3, SpringLayout.SOUTH, cropFullRight);
		layout.putConstraint(SpringLayout.WEST, cropFullTop, 50, SpringLayout.WEST, mParent);
		layout.putConstraint(SpringLayout.WEST, cropFullBottom, 50, SpringLayout.WEST, mParent);

		cropTextTop.setPreferredSize(new Dimension(50, 20));
		cropTextLeft.setPreferredSize(new Dimension(50, 20));
		cropTextRight.setPreferredSize(new Dimension(50, 20));
		cropTextBottom.setPreferredSize(new Dimension(50, 20));
		cropTextTop.setModel(new SpinnerNumberModel(0, 0, 1000, 1));
		cropTextLeft.setModel(new SpinnerNumberModel(0, 0, 1000, 1));
		cropTextRight.setModel(new SpinnerNumberModel(0, 0, 1000, 1));
		cropTextBottom.setModel(new SpinnerNumberModel(0, 0, 1000, 1));
		layout.putConstraint(SpringLayout.NORTH, cropTextPage, 3, SpringLayout.SOUTH, cropFullBottom);
		layout.putConstraint(SpringLayout.NORTH, cropTextTop, 3, SpringLayout.SOUTH, cropTextPage);
		layout.putConstraint(SpringLayout.NORTH, cropTextLeft, 3, SpringLayout.SOUTH, cropTextTop);
		layout.putConstraint(SpringLayout.NORTH, cropTextRight, 3, SpringLayout.SOUTH, cropTextTop);
		layout.putConstraint(SpringLayout.WEST, cropTextRight, 50, SpringLayout.EAST, cropTextLeft);
		layout.putConstraint(SpringLayout.NORTH, cropTextBottom, 3, SpringLayout.SOUTH, cropTextRight);
		layout.putConstraint(SpringLayout.WEST, cropTextTop, 50, SpringLayout.WEST, mParent);
		layout.putConstraint(SpringLayout.WEST, cropTextBottom, 50, SpringLayout.WEST, mParent);

		cropPictTop.setPreferredSize(new Dimension(50, 20));
		cropPictLeft.setPreferredSize(new Dimension(50, 20));
		cropPictRight.setPreferredSize(new Dimension(50, 20));
		cropPictBottom.setPreferredSize(new Dimension(50, 20));
		cropPictTop.setModel(new SpinnerNumberModel(0, 0, 1000, 1));
		cropPictLeft.setModel(new SpinnerNumberModel(0, 0, 1000, 1));
		cropPictRight.setModel(new SpinnerNumberModel(0, 0, 1000, 1));
		cropPictBottom.setModel(new SpinnerNumberModel(0, 0, 1000, 1));
		layout.putConstraint(SpringLayout.NORTH, cropPictPage, 3, SpringLayout.SOUTH, cropTextBottom);
		layout.putConstraint(SpringLayout.NORTH, cropPictTop, 3, SpringLayout.SOUTH, cropPictPage);
		layout.putConstraint(SpringLayout.NORTH, cropPictLeft, 3, SpringLayout.SOUTH, cropPictTop);
		layout.putConstraint(SpringLayout.NORTH, cropPictRight, 3, SpringLayout.SOUTH, cropPictTop);
		layout.putConstraint(SpringLayout.WEST, cropPictRight, 50, SpringLayout.EAST, cropPictLeft);
		layout.putConstraint(SpringLayout.NORTH, cropPictBottom, 3, SpringLayout.SOUTH, cropPictRight);
		layout.putConstraint(SpringLayout.WEST, cropPictTop, 50, SpringLayout.WEST, mParent);
		layout.putConstraint(SpringLayout.WEST, cropPictBottom, 50, SpringLayout.WEST, mParent);
		
		
		layout.putConstraint(SpringLayout.WEST, cancelButton, 0, SpringLayout.WEST, mParent);
		layout.putConstraint(SpringLayout.SOUTH, cancelButton, 0, SpringLayout.SOUTH, mParent);
		
		layout.putConstraint(SpringLayout.SOUTH, progressBar, 0, SpringLayout.SOUTH, mParent);
		layout.putConstraint(SpringLayout.WEST, progressBar, 2, SpringLayout.EAST, cancelButton);
		layout.putConstraint(SpringLayout.EAST, progressBar, 0, SpringLayout.EAST, mParent);
		
		layout.putConstraint(SpringLayout.SOUTH, labelMessage, 2, SpringLayout.NORTH, progressBar);
		layout.putConstraint(SpringLayout.EAST, labelMessage, 2, SpringLayout.EAST, mParent);
		layout.putConstraint(SpringLayout.WEST, labelMessage, 2, SpringLayout.WEST, progressBar);
		
		
		progressBar.setIndeterminate(false);
	
		cancelButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(mParent != null){
					mParent.cancelConvert();
				}
			}
		});
		cancelButton.setEnabled(false);
		
		
		outputFileType.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				if(e.getActionCommand().equals("comboBoxChanged")){
					String selected = (String)outputFileType.getSelectedItem();
					if(selected.equalsIgnoreCase("epub")){
						outputBookType.setEnabled(true);
					}
					else{
						outputBookType.setEnabled(false);
					}
				}
			}
		});
		
		MouseAdapter mouseClickAdapter = new MouseAdapter(){
			public void mouseClicked(MouseEvent evt) { 
				updateSettingValues(); 
            } 
		};
		
		filterEnable.addMouseListener(mouseClickAdapter);
		filterContrast.addMouseListener(mouseClickAdapter);
		filterGamma.addMouseListener(mouseClickAdapter);
		filterResize.addMouseListener(mouseClickAdapter);
		filterGrayscale.addMouseListener(mouseClickAdapter);
		filterPreview.addMouseListener(mouseClickAdapter);
		filterUnification.addMouseListener(mouseClickAdapter);
		cropFullPage.addMouseListener(mouseClickAdapter);
		cropTextPage.addMouseListener(mouseClickAdapter);
		cropPictPage.addMouseListener(mouseClickAdapter);

		gammaValue.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent arg0) {
				if(filterGamma.isSelected()){
					double gamma = getGammaValue();
					if(mOldGamma != gamma){
						mOldGamma = gamma;
						updateSettingValues();
					}
				}
			}
		});
		
		
		ChangeListener cfAdapter = new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent event) {
				if(cropFullPage.isSelected()){
					updateSettingValues();
				}
			}
		};
		ChangeListener ctAdapter = new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent event) {
				if(cropFullPage.isSelected()){
					updateSettingValues();
				}
			}
		};
		ChangeListener cpAdapter = new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent event) {
				if(cropFullPage.isSelected()){
					updateSettingValues();
				}
			}
		};
		
		cropFullTop.addChangeListener(cfAdapter);
		cropFullLeft.addChangeListener(cfAdapter);
		cropFullRight.addChangeListener(cfAdapter);
		cropFullBottom.addChangeListener(cfAdapter);

		cropTextTop.addChangeListener(ctAdapter);
		cropTextLeft.addChangeListener(ctAdapter);
		cropTextRight.addChangeListener(ctAdapter);
		cropTextBottom.addChangeListener(ctAdapter);

		cropPictTop.addChangeListener(cpAdapter);
		cropPictLeft.addChangeListener(cpAdapter);
		cropPictRight.addChangeListener(cpAdapter);
		cropPictBottom.addChangeListener(cpAdapter);

		
		chooseFolderButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				showOutputFolderDialog();
			}
		});
		
		convertButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(!outputFolder.getText().isEmpty()){
					OutputSettingParam param = getOutputSettingParam();
					boolean ok = true;
					if(!mIsInputFileName){
						mOutputFileName = "";
						param.setOutputFileName("");
						FilenameSettingDialog dialog = new FilenameSettingDialog(param.getOutputFileName(""));
						dialog.setLocationRelativeTo(mParent);
						dialog.setModal(true);
						dialog.setVisible(true);
						if(dialog.isOK()){
							mOutputFileName = dialog.getFileName();
						} else {
							ok = false;
						}
					}					
					if(ok){
						convertButton.setEnabled(false);
						cancelButton.setEnabled(true);
						mParent.startConvert();
					}
				}
			}
		});
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
			 outputFolder.setText(file.getAbsolutePath());
			 InifileProperty.getInstance().setOutputFolder(file.getAbsolutePath());
		}
	}

	public void updateSettingValues(){
		System.out.println("updateSettingValues");
		if(mParent != null){
			mParent.onUpdateSetting(getCurrentFilterParam());
		}
	}
	
	public void setValues(){
		
		gammaValue.setValue(new Double(1.6f));
		
		String defaultFolder = InifileProperty.getInstance().getOutputFolder();
		if(!defaultFolder.isEmpty()){
			outputFolder.setText(defaultFolder);
		}
		
		List<String> imageSizeList = InifileProperty.getInstance().getImageSizeList();
		for(String size : imageSizeList){
			outputImageSize.addItem(size);
		}
		outputImageSize.addItem("リサイズ無し");
		
		outputBookType.addItem("book");
		outputBookType.addItem("magazin");
		outputBookType.addItem("comic");
		
		outputFileType.addItem("zip");
		outputFileType.addItem("folder");
		outputFileType.addItem("epub");
		outputFileType.addItem("pdf");

		cropFullTop.setValue(0);
		cropFullLeft.setValue(0);
		cropFullRight.setValue(0);
		cropFullBottom.setValue(0);

		cropTextTop.setValue(0);
		cropTextLeft.setValue(0);
		cropTextRight.setValue(0);
		cropTextBottom.setValue(0);

		cropPictTop.setValue(0);
		cropPictLeft.setValue(0);
		cropPictRight.setValue(0);
		cropPictBottom.setValue(0);
	}
	
	
	public boolean isCheckStatus(int checkboxId){
		if(checkboxId < 0 || checkBoxes.length <= checkboxId){
			return false; //TODO: throw exception
		}
		return checkBoxes[checkboxId].isSelected();
	}
	
	public ImageFilterParam getCurrentFilterParam(){
		boolean isEnable = filterEnable.isSelected();
		boolean isContrast = filterContrast.isSelected();
		boolean isGamma = filterGamma.isSelected();
		boolean isResize = filterResize.isSelected();
		boolean isGrayscale = filterGrayscale.isSelected();
		boolean isPreview = filterPreview.isSelected();
		boolean isCropFull = cropFullPage.isSelected();
		boolean isCropText = cropTextPage.isSelected();
		boolean isCropPict = cropPictPage.isSelected();
		boolean isUnificationTextPage = filterUnification.isSelected();
		
		
		// Spinner
		int fullLeft = (Integer)cropFullLeft.getValue();
		int fullRight = (Integer)cropFullRight.getValue();
		int fullTop = (Integer)cropFullTop.getValue();
		int fullBottom = (Integer)cropFullBottom.getValue();
		
		int textLeft = (Integer)cropTextLeft.getValue();
		int textRight = (Integer)cropTextRight.getValue();
		int textTop = (Integer)cropTextTop.getValue();
		int textBottom = (Integer)cropTextBottom.getValue();

		int pictLeft = (Integer)cropPictLeft.getValue();
		int pictRight = (Integer)cropPictRight.getValue();
		int pictTop = (Integer)cropPictTop.getValue();
		int pictBottom = (Integer)cropPictBottom.getValue();
		
		double gamma = getGammaValue() / 10;

		// Slider
		int contrast = contrastValue.getValue();
		int brightness = brightnessValue.getValue();
		
		
		ImageFilterParam param = new ImageFilterParam();
		
		param.setEnable(isEnable);
		param.setContrast(isContrast);
		param.setGamma(isGamma);
		param.setGrayscale(isGrayscale);
		param.setResize(isResize);
		param.setPreview(isPreview);
		param.setFullPageCrop(isCropFull);
		param.setTextPageCrop(isCropText);
		param.setPictPageCrop(isCropPict);
		param.setResizeDimension(600, 800);
		param.setFullPageCrop(fullLeft, fullTop, fullRight, fullBottom);
		param.setTextPageCrop(textLeft, textTop, textRight, textBottom);
		param.setPictPageCrop(pictLeft, pictTop, pictRight, pictBottom);
		param.setGamma(gamma);
		param.setContrast(convContrast(contrast));
		param.setBrightness((float)brightness);
		
		param.setUnificationTextPage(isUnificationTextPage);
		
		return param;
	}
	
	public OutputSettingParam getOutputSettingParam(){
		
		// ComboBox
		String imageSize = (String)outputImageSize.getSelectedItem();
		String bookType = (String)outputBookType.getSelectedItem();
		String fileType = (String)outputFileType.getSelectedItem();
		
		// TextField
		String outFolder = outputFolder.getText();
		
		OutputSettingParam param = new OutputSettingParam(outFolder, fileType, bookType, imageSize);
		param.setTitle(outputTitle.getText());
		param.setTitleKana(outputTitleKana.getText());
		param.setAuthor(outputAuthor.getText());
		param.setAuthorKana(outputAuthorKana.getText());
		
		param.setOutputFileName(mOutputFileName);
		
		return param;
	}
	

	private double mOldGamma = 1.0f;
	private double getGammaValue(){
		double gamma = 1.0f;
		try {
			Object val = gammaValue.getValue();
			if(val instanceof Double){
				gamma = (Double)val;
			}
			else if(val instanceof Float){
				gamma = (Float)val;
			}
			else{
				gamma = (Integer)val;
			}
			//gamma = val;
		}catch(Exception e){
			e.printStackTrace();
		}
		return Math.round(gamma*10);
	}
	
	private int convContrast(float scale){
		int value = (int)(255 * scale - 255);
		return value;
	}
	private float convContrast(int scale){
		float value = (scale + 255) / 255.0f;
		return value;
	}
	
//	private Timer mTimer = null;
	
	public void startProgress(){
		progressBar.setIndeterminate(true);
//		if(mTimer == null){
//			progressBar.setValue(0);
//			
//			TimerTask task = new TimerTask(){
//				@Override
//				public void run() {
//					int value = progressBar.getValue();
//					if(++value > 100){
//						value = 0;
//					}
//					progressBar.setValue(value);
//				}
//			};
//			
//			Timer timer = new Timer();
//			timer.schedule(task, 100, 200);
//			mTimer = timer;
//		}
	}
	public void stopProgress(){
		progressBar.setIndeterminate(false);
//		if(mTimer != null){
//			mTimer.cancel();
//			mTimer = null;
//		}
	}
	
	public void applyFilterParam(ImageFilterParam param, boolean onlyEnable){
		boolean update = false;
		

		if(onlyEnable){
			if(param.isEnable()){
				filterEnable.setSelected(param.isEnable());
			}
			if(param.isContrast()){
				filterContrast.setSelected(param.isContrast());
			}
			if(param.isGamma()){
				filterGamma.setSelected(param.isGamma());
			}
			if(param.isResize()){
				filterResize.setSelected(param.isResize());
			}
			if(param.isGrayscale()){
				filterGrayscale.setSelected(param.isGrayscale());
			}
			if(param.isPreview()){
				filterPreview.setSelected(param.isPreview());
			}
			if(param.isFullPageCrop()){
				cropFullPage.setSelected(param.isFullPageCrop());
			}
			if(param.isTextPageCrop()){
				cropTextPage.setSelected(param.isTextPageCrop());
			}
			if(param.isPictPageCrop()){
				cropPictPage.setSelected(param.isPictPageCrop());
			}
			if(param.isUnificationTextPage()){
				filterUnification.setSelected(param.isUnificationTextPage());
			}
		}else{
			filterEnable.setSelected(param.isEnable());
			filterContrast.setSelected(param.isContrast());
			filterGamma.setSelected(param.isGamma());
			filterResize.setSelected(param.isResize());
			filterGrayscale.setSelected(param.isGrayscale());
			filterPreview.setSelected(param.isPreview());
			cropFullPage.setSelected(param.isFullPageCrop());
			cropTextPage.setSelected(param.isTextPageCrop());
			cropPictPage.setSelected(param.isPictPageCrop());
			filterUnification.setSelected(param.isUnificationTextPage());
		}
		
		
		if(param.isGamma()){
			gammaValue.setValue((float)param.getGamma());
		}
		if(param.isContrast()){
			contrastValue.setValue(convContrast(param.getContrast()));
			brightnessValue.setValue((int)param.getBrightness());
		}

		if(param.isFullPageCrop()){
			cropFullLeft.setValue(param.getFullPageCropLeft());
			cropFullRight.setValue(param.getFullPageCropRight());
			cropFullTop.setValue(param.getFullPageCropTop());
			cropFullBottom.setValue(param.getFullPageCropBottom());
			if(cropFullPage.isSelected()){
				update = true;
			}
		}
		if(param.isTextPageCrop()){
			cropTextLeft.setValue(param.getTextPageCropLeft());
			cropTextRight.setValue(param.getTextPageCropRight());
			cropTextTop.setValue(param.getTextPageCropTop());
			cropTextBottom.setValue(param.getTextPageCropBottom());
			if(cropTextPage.isSelected()){
				update = true;
			}
		}
		if(param.isPictPageCrop()){
			cropPictLeft.setValue(param.getPictPageCropLeft());
			cropPictRight.setValue(param.getPictPageCropRight());
			cropPictTop.setValue(param.getPictPageCropTop());
			cropPictBottom.setValue(param.getPictPageCropBottom());
			if(cropPictPage.isSelected()){
				update = true;
			}
		}
		
		if(param.isUnificationTextPage()){
			update = true;
		}
		
		if(update){
			updateSettingValues();
		}
	}
	
	public void applyOutputParam(OutputSettingParam param){
		
		// ComboBox
		Dimension size = param.getImageSize();
		int selectedIndex = -1;
		if(size != null && size.width != 0 && size.height != 0){
			String textValue = size.width + "x" + size.height;
			for(int i=0; i<outputImageSize.getItemCount(); i++){
				String value = (String)outputImageSize.getItemAt(i);
				if(textValue.equals(value)){
					selectedIndex = i;
					break;
				}
			}
		}
		if(selectedIndex >= 0){
			outputImageSize.setSelectedIndex(selectedIndex);
		}else{
			outputImageSize.setSelectedIndex(outputImageSize.getItemCount()-1);
		}
		
		//String bookType = (String)outputBookType.getSelectedItem();
		//String fileType = (String)outputFileType.getSelectedItem();
		
		if(!param.getOutputPath().isEmpty()){
			// TextField
			outputFolder.setText(param.getOutputPath());
		}
		
		if(!param.getTitle().isEmpty()){
			outputTitle.setText(param.getTitle());
		}
		if(!param.getTitleKana().isEmpty()){
			outputTitleKana.setText(param.getTitleKana());
		}
		if(!param.getAuthor().isEmpty()){
			outputAuthor.setText(param.getAuthor());
		}
		if(!param.getAuthorKana().isEmpty()){
			outputAuthorKana.setText(param.getAuthorKana());
		}
		
	}
	
	/**
	 * FileInfo更新イベント
	 */
	public void onFileInfoModified(){
		if(filterUnification.isSelected()){
			updateSettingValues();
		}
	}
	
	/**
	 * 変換終了イベント
	 */
	public void onFinishConvert(){
		convertButton.setEnabled(true);
		cancelButton.setEnabled(false);
	}
	
	/**
	 * 進捗メッセージ設定
	 * @param message
	 */
	public void setProgressMessage(String message){
		labelMessage.setText(message);
	}
	
	/**
	 * 背景文字列付きテキストフィールド
	 * @author USER
	 *
	 */
	private class DefaultTextField extends JTextField implements FocusListener {
		
		private String mDefaultText = "";
		private boolean mIsEmpty = true;
		
		public DefaultTextField(int size, String text){
			super(size);
			mDefaultText = text;
			mIsEmpty = true;
			super.setText(mDefaultText);
			super.setForeground(Color.LIGHT_GRAY);
			addFocusListener(this);
		}

		@Override
		public void focusGained(FocusEvent e) {
			if(mIsEmpty){
				super.setText("");
			}
			setForeground(Color.BLACK);
			mIsEmpty = false;
		}

		@Override
		public void focusLost(FocusEvent e) {
			if(super.getText().isEmpty()){
				mIsEmpty = true;
				super.setText(mDefaultText);
				setForeground(Color.LIGHT_GRAY);
			}
		}
		
		@Override
		public void setText(String text){
			super.setText(text);
			if(text.isEmpty()){
				mIsEmpty = true;
				setForeground(Color.LIGHT_GRAY);
			}else{
				mIsEmpty = false;
				setForeground(Color.BLACK);
			}
		}
		
		@Override
		public String getText(){
			if(mIsEmpty){
				return "";
			}
			else{
				return super.getText();
			}
		}
		
	}
	
	private class DefaultTextFocusListener implements FocusListener {

		private String mDefaultText = "";
		private JTextField mField = null;
		private boolean mIsEmpty = true;
		private boolean mIsOwnEdit = false;
		
		public DefaultTextFocusListener(String defaultText, JTextField field){
			mDefaultText = defaultText;
			mField = field;
			mField.setText(mDefaultText);
			mField.setForeground(Color.LIGHT_GRAY);
		}
		
		@Override
		public void focusGained(FocusEvent e) {
			if(mIsEmpty){
				mField.setText("");
			}else{
				
			}
			mField.setForeground(Color.BLACK);
			mIsEmpty = false;
		}

		@Override
		public void focusLost(FocusEvent e) {
			if(mField.getText().isEmpty()){
				mIsEmpty = true;
				mField.setText(mDefaultText);
				mField.setForeground(Color.LIGHT_GRAY);
			}
		}
	
	}
	
}
