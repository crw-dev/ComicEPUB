package jp.crwdev.app.gui;

import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

public class PreviewSettingPanel extends JPanel {

	private JCheckBox checkPreview = new JCheckBox("編集モード");
	private JCheckBox checkResize = new JCheckBox("リサイズ");

	private SettingPanel mParent = null;
	
	/**
	 * コンストラクタ
	 */
	public PreviewSettingPanel(){
		super();
		initialize();
	}

	public void setComponents(SettingPanel parent, JCheckBox preview, JCheckBox resize){
		mParent = parent;
		checkPreview = preview;
		checkResize = resize;
		initialize();
	}
	
	private void initialize(){
	
		MouseAdapter mouseClickAdapter = new MouseAdapter(){
			public void mouseClicked(MouseEvent evt) { 
				updateSettingValues(); 
            } 
		};
		
		checkPreview.addMouseListener(mouseClickAdapter);
		checkResize.addMouseListener(mouseClickAdapter);
	}
	
	public boolean isPreview(){
		return checkPreview.isSelected();
	}
	
	public boolean isResize(){
		return checkResize.isSelected();
	}

	private void updateSettingValues(){
		if(mParent != null){
			mParent.updateSettingValues();
		}
	}
}
