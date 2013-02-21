package jp.crwdev.app.gui;

import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SpringLayout;

public class ProgressPanel extends JPanel {
	
	public JLabel labelMessage = new JLabel("");
	public JButton cancelButton = new JButton("Cancel");
	public JProgressBar progressBar = new JProgressBar();

	public SettingPanel mParent = null;
	
	/**
	 * コンストラクタ
	 */
	public ProgressPanel(){
		super();
		initialize();
	}
	
	public void setComponents(SettingPanel parent, JLabel message, JProgressBar progress, JButton cancel){
		mParent = parent;
		labelMessage = message;
		progressBar = progress;
		cancelButton = cancel;

		initialize();
	}
	
	private void initialize(){

	}
	
	
	public void startProgress(){
		progressBar.setIndeterminate(true);
	}
	public void stopProgress(){
		progressBar.setIndeterminate(false);
	}
	
	/**
	 * 進捗メッセージ設定
	 * @param message
	 */
	public void setProgressMessage(String message){
		labelMessage.setText(message);
	}
	
	/**
	 * 変換終了イベント
	 */
	public void onFinishConvert(){
		cancelButton.setEnabled(false);
	}

	
}
