package jp.crwdev.app.gui;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JPanel;
import javax.swing.SpringLayout;
import javax.swing.JCheckBox;
import javax.swing.JTabbedPane;
import javax.swing.JButton;
import javax.swing.JProgressBar;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.DefaultComboBoxModel;


public class SettingPanelLayout extends JPanel {
	protected DefaultTextField textTitle;
	protected DefaultTextField textAuthor;
	protected DefaultTextField textAuthorKana;
	protected DefaultTextField textTitleKana;
	protected DefaultTextField outputFolder;
	protected JCheckBox checkPreview;
	protected JCheckBox checkResize;
	protected JTabbedPane tabbedPane;
	protected JComboBox outputImageSize;
	protected JComboBox outputFileType;
	protected JComboBox outputBookType;
	protected JButton outpuFolderButton;
	protected JButton convertButton;
	protected JButton cancelButton;
	protected JLabel labelMessage;
	protected JProgressBar progressBar;
	
	/**
	 * Create the panel.
	 */
	public SettingPanelLayout() {
		SpringLayout springLayout = new SpringLayout();
		setLayout(springLayout);
		
		checkPreview = new JCheckBox("編集モード");
		springLayout.putConstraint(SpringLayout.NORTH, checkPreview, 3, SpringLayout.NORTH, this);
		springLayout.putConstraint(SpringLayout.WEST, checkPreview, 3, SpringLayout.WEST, this);
		add(checkPreview);
		
		checkResize = new JCheckBox("リサイズ");
		springLayout.putConstraint(SpringLayout.WEST, checkResize, 6, SpringLayout.EAST, checkPreview);
		springLayout.putConstraint(SpringLayout.SOUTH, checkResize, 0, SpringLayout.SOUTH, checkPreview);
		add(checkResize);
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		springLayout.putConstraint(SpringLayout.NORTH, tabbedPane, 6, SpringLayout.SOUTH, checkPreview);
		springLayout.putConstraint(SpringLayout.WEST, tabbedPane, 3, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.SOUTH, tabbedPane, 300, SpringLayout.SOUTH, checkPreview);
		springLayout.putConstraint(SpringLayout.EAST, tabbedPane, -3, SpringLayout.EAST, this);
		add(tabbedPane);
		
		textTitle = new DefaultTextField(10, "タイトル");
		springLayout.putConstraint(SpringLayout.NORTH, textTitle, 6, SpringLayout.SOUTH, tabbedPane);
		springLayout.putConstraint(SpringLayout.WEST, textTitle, 3, SpringLayout.WEST, this);
		add(textTitle);
		textTitle.setColumns(10);
		
		textAuthor = new DefaultTextField(10, "著者名");
		springLayout.putConstraint(SpringLayout.NORTH, textAuthor, 6, SpringLayout.SOUTH, textTitle);
		springLayout.putConstraint(SpringLayout.WEST, textAuthor, 3, SpringLayout.WEST, this);
		add(textAuthor);
		textAuthor.setColumns(10);
		
		textAuthorKana = new DefaultTextField(10, "著者名カナ");
		springLayout.putConstraint(SpringLayout.WEST, textAuthorKana, 182, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.EAST, textAuthor, -6, SpringLayout.WEST, textAuthorKana);
		springLayout.putConstraint(SpringLayout.NORTH, textAuthorKana, 0, SpringLayout.NORTH, textAuthor);
		springLayout.putConstraint(SpringLayout.EAST, textAuthorKana, -3, SpringLayout.EAST, this);
		add(textAuthorKana);
		textAuthorKana.setColumns(10);
		
		textTitleKana = new DefaultTextField(10, "タイトルカナ");
		springLayout.putConstraint(SpringLayout.WEST, textTitleKana, 182, SpringLayout.WEST, this);
		springLayout.putConstraint(SpringLayout.EAST, textTitle, -6, SpringLayout.WEST, textTitleKana);
		springLayout.putConstraint(SpringLayout.NORTH, textTitleKana, 6, SpringLayout.SOUTH, tabbedPane);
		springLayout.putConstraint(SpringLayout.SOUTH, textTitleKana, -6, SpringLayout.NORTH, textAuthorKana);
		springLayout.putConstraint(SpringLayout.EAST, textTitleKana, -3, SpringLayout.EAST, this);
		textTitleKana.setColumns(10);
		add(textTitleKana);
		
		outputImageSize = new JComboBox();
		springLayout.putConstraint(SpringLayout.NORTH, outputImageSize, 6, SpringLayout.SOUTH, textAuthor);
		springLayout.putConstraint(SpringLayout.WEST, outputImageSize, 0, SpringLayout.WEST, checkPreview);
		add(outputImageSize);
		
		outputFileType = new JComboBox();
		springLayout.putConstraint(SpringLayout.NORTH, outputFileType, 6, SpringLayout.SOUTH, outputImageSize);
		springLayout.putConstraint(SpringLayout.WEST, outputFileType, 0, SpringLayout.WEST, checkPreview);
		add(outputFileType);
		
		outputBookType = new JComboBox();
		springLayout.putConstraint(SpringLayout.WEST, outputBookType, 6, SpringLayout.EAST, outputFileType);
		springLayout.putConstraint(SpringLayout.SOUTH, outputBookType, 0, SpringLayout.SOUTH, outputFileType);
		add(outputBookType);
		
		outputFolder = new DefaultTextField(10, "出力先フォルダ");
		springLayout.putConstraint(SpringLayout.NORTH, outputFolder, 6, SpringLayout.SOUTH, outputFileType);
		springLayout.putConstraint(SpringLayout.WEST, outputFolder, 0, SpringLayout.WEST, checkPreview);
		springLayout.putConstraint(SpringLayout.EAST, outputFolder, 0, SpringLayout.EAST, tabbedPane);
		add(outputFolder);
		outputFolder.setColumns(10);
		
		outpuFolderButton = new JButton("出力フォルダ");
		springLayout.putConstraint(SpringLayout.NORTH, outpuFolderButton, 6, SpringLayout.SOUTH, outputFolder);
		springLayout.putConstraint(SpringLayout.WEST, outpuFolderButton, 0, SpringLayout.WEST, checkPreview);
		add(outpuFolderButton);
		
		convertButton = new JButton("変換");
		springLayout.putConstraint(SpringLayout.NORTH, convertButton, 0, SpringLayout.NORTH, outpuFolderButton);
		springLayout.putConstraint(SpringLayout.EAST, convertButton, 0, SpringLayout.EAST, tabbedPane);
		add(convertButton);
		
		cancelButton = new JButton("キャンセル");
		springLayout.putConstraint(SpringLayout.SOUTH, cancelButton, -3, SpringLayout.SOUTH, this);
		springLayout.putConstraint(SpringLayout.EAST, cancelButton, 0, SpringLayout.EAST, tabbedPane);
		add(cancelButton);
		
		progressBar = new JProgressBar();
		springLayout.putConstraint(SpringLayout.WEST, progressBar, 0, SpringLayout.WEST, checkPreview);
		springLayout.putConstraint(SpringLayout.SOUTH, progressBar, 0, SpringLayout.SOUTH, cancelButton);
		springLayout.putConstraint(SpringLayout.EAST, progressBar, -6, SpringLayout.WEST, cancelButton);
		add(progressBar);
		
		labelMessage = new JLabel("New label");
		springLayout.putConstraint(SpringLayout.WEST, labelMessage, 0, SpringLayout.WEST, checkPreview);
		springLayout.putConstraint(SpringLayout.SOUTH, labelMessage, -6, SpringLayout.NORTH, progressBar);
		springLayout.putConstraint(SpringLayout.EAST, labelMessage, 0, SpringLayout.EAST, tabbedPane);
		add(labelMessage);

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
