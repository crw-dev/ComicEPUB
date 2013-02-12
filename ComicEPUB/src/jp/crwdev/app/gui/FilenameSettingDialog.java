/**
 * ファイル名設定ダイアログ
 */
package jp.crwdev.app.gui;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

public class FilenameSettingDialog extends JDialog {
	
	/** OKボタン押下フラグ */
	private boolean mIsOk = false;
	/** ファイル名 */
	private String mFileName = "";
	/** ファイル名入力フィールド */
	JTextField mTextField = null;
	
	/**
	 * コンストラクタ
	 * @param defaultText ファイル名初期値
	 */
	public FilenameSettingDialog(String defaultText){
		super();
		
		mIsOk = false;
		mFileName = defaultText;
		
		setTitle("保存ファイル/フォルダ名設定");
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		
		SpringLayout layout = new SpringLayout();
		JPanel panel = new JPanel(layout);
		
		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				mIsOk = true;
				mFileName = mTextField.getText();
				if(!mFileName.isEmpty()){
					dispose();
				}
			}
		});
		
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				mIsOk = false;
				dispose();
			}
		});
		
		JTextField text = new JTextField(20);
		text.setText(defaultText);
		mTextField = text;
		
		panel.add(text);
		panel.add(cancelButton);
		panel.add(okButton);
		
		layout.putConstraint(SpringLayout.WEST, text, 5, SpringLayout.WEST, panel);
		layout.putConstraint(SpringLayout.EAST, text, -5, SpringLayout.EAST, panel);
		layout.putConstraint(SpringLayout.NORTH, text, 5, SpringLayout.NORTH, panel);
		
		layout.putConstraint(SpringLayout.SOUTH, cancelButton, -5, SpringLayout.SOUTH, panel);
		layout.putConstraint(SpringLayout.SOUTH, okButton, -5, SpringLayout.SOUTH, panel);

		layout.putConstraint(SpringLayout.EAST, okButton, -5, SpringLayout.EAST, panel);
		layout.putConstraint(SpringLayout.EAST, cancelButton, -5, SpringLayout.WEST, okButton);
		

		Container c = getContentPane();
		c.add(panel);
		
		setResizable(false);
		setSize(250, 102);
	}

	public boolean isOK(){
		return mIsOk;
	}
	
	public String getFileName(){
		return mFileName;
	}
	
}
