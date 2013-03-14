package jp.crwdev.app.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.File;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import jp.crwdev.app.OutputSettingParam;
import jp.crwdev.app.util.InifileProperty;


public class OutputSettingPanel extends JPanel {

	private boolean mIsInputFileName = false;
	private String mOutputFileName = "";

	// JFrame
	public JFrame mFrame;
	
	// ComboBox
	public JComboBox outputImageSize;
	public JComboBox outputBookType;
	public JComboBox outputFileType;
	
	// TextField
	public JTextField outputFolder;
	public JTextField outputTitle;
	public JTextField outputTitleKana;
	public JTextField outputAuthor;
	public JTextField outputAuthorKana;
	
	// Button
	public JButton chooseFolderButton;
	public JButton convertButton;
	public JButton cancelButton;
	public JButton packageConvertButton;
	
	// CheckBox
	public JCheckBox checkOutputResize;
	
	private SettingPanel mParent = null;
	
	/**
	 * コンストラクタ
	 */
	public OutputSettingPanel(){
		super();
	}
	
	public void setComponents(SettingPanel parent, JComboBox imageSize, JComboBox fileType, JComboBox bookType,
			JTextField title, JTextField titleKana, JTextField author, JTextField authorKana,
			JTextField folder, JButton folderBtn, JButton convertBtn, JButton cancelBtn,
			JCheckBox outputResize, JButton packageConvButton, JFrame parentFrame){
		mParent = parent;
		mFrame = parentFrame;
		outputImageSize = imageSize;
		outputFileType = fileType;
		outputBookType = bookType;
		outputTitle = title;
		outputTitleKana = titleKana;
		outputAuthor = author;
		outputAuthorKana = authorKana;
		outputFolder = folder;
		chooseFolderButton = folderBtn;
		convertButton = convertBtn;
		cancelButton = cancelBtn;
		checkOutputResize = outputResize;
		packageConvertButton = packageConvButton;
		initialize();
	}
	
	private void initialize(){

		
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
		
		outputFileType.setSelectedIndex(0);
		outputBookType.setEnabled(false);

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
						if(mParent != null){
							convertButton.setEnabled(false);
							cancelButton.setEnabled(true);
							mParent.startConvert();
						}
					}
				}
			}
		});
		
		outputImageSize.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				if(e.getActionCommand().equals("comboBoxChanged")){
					if(checkOutputResize.isSelected()){
						mParent.updateSettingValues();
					}
				}
			}
		});
		
		packageConvertButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				mParent.saveSettingFileRequest();
				new BatWorkDialog(mFrame);
			}
		});

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

	public Dimension getOutputImageSize(){
		String imageSize = (String)outputImageSize.getSelectedItem();
		return getImageSize(imageSize);
	}
	
	public Dimension getImageSize(String imageSize){
		String[] val = imageSize.split("x");
		if(val.length == 2){
			try{
				int width = Integer.parseInt(val[0]);
				int height = Integer.parseInt(val[1]);
				return new Dimension(width, height);
			}
			catch(NumberFormatException ex){
				return null;
			}
		}
		return null;
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
	 * 変換終了イベント
	 */
	public void onFinishConvert(){
		convertButton.setEnabled(true);
	}

}
