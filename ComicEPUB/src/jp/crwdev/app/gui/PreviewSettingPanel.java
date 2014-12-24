package jp.crwdev.app.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class PreviewSettingPanel extends JPanel {

	private JCheckBox checkPreview;
	private JCheckBox checkSimpleZoom;
	private JCheckBox checkResize;
	private JCheckBox checkOutputResize;
	private JButton fullscreenButton = null;

	private SettingPanel mParent = null;
	
	/**
	 * コンストラクタ
	 */
	public PreviewSettingPanel(){
		super();
		//initialize();
	}

	public void setComponents(SettingPanel parent, JCheckBox preview, JCheckBox zoom, JCheckBox resize, JCheckBox outputResize, JButton fullscreen){
		mParent = parent;
		checkPreview = preview;
		checkSimpleZoom = zoom;
		checkResize = resize;
		checkOutputResize = outputResize;
		fullscreenButton = fullscreen;
		initialize();
	}
	
	private void initialize(){
	
		checkPreview.setSelected(false);
		checkSimpleZoom.setSelected(false);
		checkResize.setSelected(true);
		checkOutputResize.setSelected(false);
		
		checkPreview.setToolTipText("プレビューモードをONにすると出力結果のプレビューが表示されます。");
		checkSimpleZoom.setToolTipText("画像サイズがウインドウサイズより小さい場合拡大表示します。(プレビューモードON時)");
		checkResize.setToolTipText("ウインドウサイズに合わせて画像サイズを変更します。");
		checkOutputResize.setToolTipText("出力サイズで画像を表示します。(※リサイズより優先)");
		
		MouseAdapter mouseClickAdapter = new MouseAdapter(){
			public void mouseClicked(MouseEvent evt) { 
				updateSettingValues(); 
			} 
		};
		
		checkPreview.addMouseListener(mouseClickAdapter);
		checkSimpleZoom.addMouseListener(mouseClickAdapter);
		checkResize.addMouseListener(mouseClickAdapter);
		checkOutputResize.addMouseListener(mouseClickAdapter);
		
		fullscreenButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				mParent.beginFullscreen();
			}
		});
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
	
	public boolean isSimpleZoom(){
		return checkSimpleZoom.isSelected();
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
