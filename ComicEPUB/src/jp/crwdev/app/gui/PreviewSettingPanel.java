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
	private JCheckBox checkOutputResize = new JCheckBox("出力サイズ");

	private SettingPanel mParent = null;
	
	/**
	 * コンストラクタ
	 */
	public PreviewSettingPanel(){
		super();
		initialize();
	}

	public void setComponents(SettingPanel parent, JCheckBox preview, JCheckBox resize, JCheckBox outputResize){
		mParent = parent;
		checkPreview = preview;
		checkResize = resize;
		checkOutputResize = outputResize;
		initialize();
	}
	
	private void initialize(){
	
		checkPreview.setSelected(true);
		checkResize.setSelected(true);
		checkOutputResize.setSelected(false);
		
		MouseAdapter mouseClickAdapter = new MouseAdapter(){
			public void mouseClicked(MouseEvent evt) { 
				updateSettingValues(); 
            } 
		};
		
		checkPreview.addMouseListener(mouseClickAdapter);
		checkResize.addMouseListener(mouseClickAdapter);
		checkOutputResize.addMouseListener(mouseClickAdapter);
	}
	
	public void setPreview(boolean enable){
		checkPreview.setSelected(enable);
	}
	
	public void setResize(boolean enable){
		checkResize.setSelected(enable);
	}
	
	public boolean isPreview(){
		return checkPreview.isSelected();
	}
	
	public boolean isResize(){
		return checkResize.isSelected();
	}

	public boolean isOutputResize(){
		return checkOutputResize.isSelected();
	}

	private void updateSettingValues(){
		if(mParent != null){
			mParent.updateSettingValues();
		}
	}
}
