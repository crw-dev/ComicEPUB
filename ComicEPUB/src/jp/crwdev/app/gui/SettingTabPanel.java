package jp.crwdev.app.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SpringLayout;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import jp.crwdev.app.constant.Constant;
import jp.crwdev.app.imagefilter.ImageFilterParam;
import jp.crwdev.app.setting.ImageFilterParamSet;

public class SettingTabPanel extends JPanel {

	private int mFilterIndex = ImageFilterParamSet.FILTER_INDEX_BASIC;
	
	// Label
//	public JLabel labelGamma = new JLabel("ガンマ補正値");
	public JLabel labelContrast = new JLabel("Co:0");
	public JLabel labelBrightness = new JLabel("Br:0");

	// CheckBox
	public JCheckBox checkEnable = new JCheckBox("無変換");
	public JCheckBox checkBlur = new JCheckBox("ぼかし");
	public JCheckBox checkSharpness = new JCheckBox("シャープネス");
	public JCheckBox checkContrast = new JCheckBox("コントラスト");
	public JCheckBox checkGamma = new JCheckBox("ガンマ補正");
	public JCheckBox checkGrayscale = new JCheckBox("グレースケール");
	public JCheckBox checkCrop = new JCheckBox("切り抜き");
	public JCheckBox checkUnification = new JCheckBox("本文ページサイズ統一");
	public JCheckBox checkAutoCrop = new JCheckBox("自動余白除去");
	
	// Spinner
	public JSpinner spinSharpness = new JSpinner();
	public JSpinner spinGamma = new JSpinner();
	public JSpinner spinCropLeft = new JSpinner();
	public JSpinner spinCropRight = new JSpinner();
	public JSpinner spinCropTop = new JSpinner();
	public JSpinner spinCropBottom = new JSpinner();

	// Slider
	public JSlider sliderContrast = new JSlider();
	public JSlider sliderBrightness = new JSlider();

	private SettingPanel mParent = null;
	
	public SettingTabPanel(SettingPanel parent, int filterIndex){
		super();
		mParent = parent;
		mFilterIndex = filterIndex;
		initialize();
	}
	
	public String getTabTitle(){
		switch(mFilterIndex){
		case ImageFilterParamSet.FILTER_INDEX_COLOR:
			return "カラー";
		case ImageFilterParamSet.FILTER_INDEX_PICT:
			return "挿絵";
		case ImageFilterParamSet.FILTER_INDEX_TEXT:
			return "本文";
		case ImageFilterParamSet.FILTER_INDEX_BASIC:
		default:
			return "未設定";
		}
	}
	
	private void initialize(){
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		//setPreferredSize(new Dimension(300,400));
		
		JPanel checkPanel = new JPanel();
		SpringLayout layout1 = new SpringLayout();
		checkPanel.setLayout(layout1);
		
		
		// 無変換
		checkPanel.add(checkEnable);
	
		layout1.putConstraint(SpringLayout.WEST, checkEnable, 0, SpringLayout.WEST, checkPanel);
		layout1.putConstraint(SpringLayout.NORTH, checkEnable, 0, SpringLayout.NORTH, checkPanel);
		
		// ぼかし
		checkPanel.add(checkBlur);

		layout1.putConstraint(SpringLayout.WEST, checkBlur, 0, SpringLayout.WEST, checkPanel);
		layout1.putConstraint(SpringLayout.NORTH, checkBlur, 0, SpringLayout.SOUTH, checkEnable);

		// シャープネス
		checkPanel.add(checkSharpness);
		
		layout1.putConstraint(SpringLayout.WEST, checkSharpness, 0, SpringLayout.EAST, checkBlur);
		layout1.putConstraint(SpringLayout.NORTH, checkSharpness, 0, SpringLayout.NORTH, checkBlur);
		
		checkPanel.add(spinSharpness);
		layout1.putConstraint(SpringLayout.WEST, spinSharpness, 0, SpringLayout.EAST, checkSharpness);
		layout1.putConstraint(SpringLayout.BASELINE, spinSharpness, 0, SpringLayout.BASELINE, checkSharpness);

		// グレースケール
		checkPanel.add(checkGrayscale);

		layout1.putConstraint(SpringLayout.WEST, checkGrayscale, 0, SpringLayout.WEST, checkPanel);
		layout1.putConstraint(SpringLayout.NORTH, checkGrayscale, 0, SpringLayout.SOUTH, checkBlur);

		//
		Component current = checkGrayscale;
		if(mFilterIndex == ImageFilterParamSet.FILTER_INDEX_TEXT){
			checkPanel.add(checkUnification);

			layout1.putConstraint(SpringLayout.WEST, checkUnification, 0, SpringLayout.WEST, checkPanel);
			layout1.putConstraint(SpringLayout.NORTH, checkUnification, 0, SpringLayout.SOUTH, checkGrayscale);
			
			current = checkUnification;
		}


		
		// ガンマ
		checkPanel.add(checkGamma);
		layout1.putConstraint(SpringLayout.WEST, checkGamma, 0, SpringLayout.WEST, checkPanel);
		layout1.putConstraint(SpringLayout.NORTH, checkGamma, 0, SpringLayout.SOUTH, current);
		
		checkPanel.add(spinGamma);
		layout1.putConstraint(SpringLayout.WEST, spinGamma, 0, SpringLayout.EAST, checkGamma);
		layout1.putConstraint(SpringLayout.BASELINE, spinGamma, 0, SpringLayout.BASELINE, checkGamma);
		
		// コントラスト
		checkPanel.add(checkContrast);
		layout1.putConstraint(SpringLayout.WEST, checkContrast, 0, SpringLayout.WEST, checkPanel);
		layout1.putConstraint(SpringLayout.NORTH, checkContrast, 0, SpringLayout.SOUTH, checkGamma);
		
		checkPanel.add(sliderContrast);
		layout1.putConstraint(SpringLayout.WEST, sliderContrast, 0, SpringLayout.WEST, checkPanel);
		layout1.putConstraint(SpringLayout.EAST, sliderContrast, 180, SpringLayout.WEST, checkPanel);
		layout1.putConstraint(SpringLayout.NORTH, sliderContrast, 0, SpringLayout.SOUTH, checkContrast);

		checkPanel.add(labelContrast);
		layout1.putConstraint(SpringLayout.WEST, labelContrast, 6, SpringLayout.EAST, sliderContrast);
		layout1.putConstraint(SpringLayout.NORTH, labelContrast, 0, SpringLayout.NORTH, sliderContrast);
		
		checkPanel.add(sliderBrightness);
		layout1.putConstraint(SpringLayout.WEST, sliderBrightness, 0, SpringLayout.WEST, checkPanel);
		layout1.putConstraint(SpringLayout.EAST, sliderBrightness, 180, SpringLayout.WEST, checkPanel);
		layout1.putConstraint(SpringLayout.NORTH, sliderBrightness, 4, SpringLayout.SOUTH, sliderContrast);
		
		checkPanel.add(labelBrightness);
		layout1.putConstraint(SpringLayout.WEST, labelBrightness, 6, SpringLayout.EAST, sliderBrightness);
		layout1.putConstraint(SpringLayout.NORTH, labelBrightness, 0, SpringLayout.NORTH, sliderBrightness);
	
		
		// 切り抜き
		checkPanel.add(checkCrop);
		checkPanel.add(spinCropLeft);
		checkPanel.add(spinCropTop);
		checkPanel.add(spinCropRight);
		checkPanel.add(spinCropBottom);
		checkPanel.add(checkAutoCrop);
		
		layout1.putConstraint(SpringLayout.WEST, checkCrop, 0, SpringLayout.WEST, checkPanel);
		layout1.putConstraint(SpringLayout.NORTH, checkCrop, 0, SpringLayout.SOUTH, labelBrightness);
		
		layout1.putConstraint(SpringLayout.WEST, spinCropLeft, 10, SpringLayout.WEST, checkPanel);
		layout1.putConstraint(SpringLayout.NORTH, spinCropLeft, 12, SpringLayout.SOUTH, checkCrop);
		
		layout1.putConstraint(SpringLayout.WEST, spinCropTop, 63, SpringLayout.WEST, checkPanel);
		layout1.putConstraint(SpringLayout.NORTH, spinCropTop, 0, SpringLayout.SOUTH, checkCrop);
		
		layout1.putConstraint(SpringLayout.WEST, spinCropBottom, 63, SpringLayout.WEST, checkPanel);
		layout1.putConstraint(SpringLayout.NORTH, spinCropBottom, 2, SpringLayout.SOUTH, spinCropTop);
		
		layout1.putConstraint(SpringLayout.WEST, spinCropRight, 116, SpringLayout.WEST, checkPanel);
		layout1.putConstraint(SpringLayout.NORTH, spinCropRight, 0, SpringLayout.NORTH, spinCropLeft);

		layout1.putConstraint(SpringLayout.WEST, checkAutoCrop, 3, SpringLayout.EAST, checkCrop);
		layout1.putConstraint(SpringLayout.NORTH, checkAutoCrop, 0, SpringLayout.NORTH, checkCrop);
		
		add(checkPanel);




		spinCropLeft.setPreferredSize(new Dimension(50, 20));
		spinCropTop.setPreferredSize(new Dimension(50, 20));
		spinCropRight.setPreferredSize(new Dimension(50, 20));
		spinCropBottom.setPreferredSize(new Dimension(50, 20));

		spinCropLeft.setModel(new SpinnerNumberModel(0, 0, 1000, 1));
		spinCropTop.setModel(new SpinnerNumberModel(0, 0, 1000, 1));
		spinCropRight.setModel(new SpinnerNumberModel(0, 0, 1000, 1));
		spinCropBottom.setModel(new SpinnerNumberModel(0, 0, 1000, 1));

	
		
		spinGamma.setPreferredSize(new Dimension(40, 20));
		spinGamma.setModel(new SpinnerNumberModel(1.6f, 0.0f, 3.0f, 0.1f));
		
		mOldSharpLevel = 1;
		spinSharpness.setPreferredSize(new Dimension(40, 20));
		spinSharpness.setModel(new SpinnerNumberModel(1, 1, 10, 1));
		
		MouseAdapter mouseClickAdapter = new MouseAdapter(){
			public void mouseClicked(MouseEvent evt) { 
				updateSettingValues(); 
            } 
		};
		
		checkEnable.addMouseListener(mouseClickAdapter);
		checkBlur.addMouseListener(mouseClickAdapter);
		checkSharpness.addMouseListener(mouseClickAdapter);
		checkUnification.addMouseListener(mouseClickAdapter);
		checkGrayscale.addMouseListener(mouseClickAdapter);
		checkGamma.addMouseListener(mouseClickAdapter);
		checkContrast.addMouseListener(mouseClickAdapter);
		checkCrop.addMouseListener(mouseClickAdapter);
		checkAutoCrop.addMouseListener(mouseClickAdapter);
		
		spinGamma.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent arg0) {
				if(checkGamma.isSelected()){
					double gamma = getGammaValue();
					if(mOldGamma != gamma){
						mOldGamma = gamma;
						updateSettingValues();
					}
				}
			}
		});
		
		spinSharpness.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent arg0) {
				if(checkSharpness.isSelected()){
					int sharpLevel = (Integer)spinSharpness.getValue();
					if(mOldSharpLevel != sharpLevel){
						mOldSharpLevel = sharpLevel;
						updateSettingValues();
					}
				}
			}
		});

		
		sliderContrast.setMinimum(-255);
		sliderContrast.setMaximum(255);
		sliderContrast.setValue(0);
		sliderBrightness.setMinimum(-255);
		sliderBrightness.setMaximum(255);
		sliderBrightness.setValue(0);
		sliderContrast.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent arg0) {
				int contrast = sliderContrast.getValue();
				labelContrast.setText("Co:" + contrast);
			}
		});
		sliderBrightness.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent arg0) {
				int brightness = sliderBrightness.getValue();
				labelBrightness.setText("Br:" + brightness);
			}
		});
		sliderContrast.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseReleased(MouseEvent arg0) {
				if(checkContrast.isSelected()){
					updateSettingValues();
				}
			}
		});
		sliderBrightness.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseReleased(MouseEvent arg0) {
				if(checkContrast.isSelected()){
					updateSettingValues();
				}
			}
		});

		
		ChangeListener listener = new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent e) {
				if(checkCrop.isSelected() || checkAutoCrop.isSelected()){
					updateSettingValues();
				}
			}
		};
		
		spinCropLeft.addChangeListener(listener);
		spinCropRight.addChangeListener(listener);
		spinCropTop.addChangeListener(listener);
		spinCropBottom.addChangeListener(listener);
		
	}
	
	private void updateSettingValues(){
		if(mParent != null){
			mParent.updateSettingValues();
		}
	}
	
	public boolean setImageFilterParam(ImageFilterParam param, boolean onlyEnable){
		boolean update = false;
		
		if(param == null){
			return false;
		}
		
		if(onlyEnable){
			if(param.isEnable()){
				checkEnable.setSelected(param.isEnable());
			}
			if(param.isContrast()){
				checkContrast.setSelected(param.isContrast());
			}
			if(param.isGamma()){
				checkGamma.setSelected(param.isGamma());
			}
			if(param.isBlur()){
				checkBlur.setSelected(param.isBlur());
			}
			if(param.isSharpness()){
				checkSharpness.setSelected(param.isSharpness());
			}
			if(param.isGrayscale()){
				checkGrayscale.setSelected(param.isGrayscale());
			}
			if(mFilterIndex == ImageFilterParamSet.FILTER_INDEX_BASIC && param.isFullPageCrop()){
				checkCrop.setSelected(param.isFullPageCrop());
			}
			if(mFilterIndex == ImageFilterParamSet.FILTER_INDEX_COLOR && param.isColorPageCrop()){
				checkCrop.setSelected(param.isColorPageCrop());
			}
			if(mFilterIndex == ImageFilterParamSet.FILTER_INDEX_TEXT && param.isTextPageCrop()){
				checkCrop.setSelected(param.isTextPageCrop());
			}
			if(mFilterIndex == ImageFilterParamSet.FILTER_INDEX_PICT && param.isPictPageCrop()){
				checkCrop.setSelected(param.isPictPageCrop());
			}
			if(mFilterIndex == ImageFilterParamSet.FILTER_INDEX_TEXT && param.isUnificationTextPage()){
				checkUnification.setSelected(param.isUnificationTextPage());
			}
		}else{
			checkEnable.setSelected(param.isEnable());
			checkBlur.setSelected(param.isBlur());
			checkSharpness.setSelected(param.isSharpness());
			checkContrast.setSelected(param.isContrast());
			checkGamma.setSelected(param.isGamma());
			checkGrayscale.setSelected(param.isGrayscale());
			switch(mFilterIndex){
			case ImageFilterParamSet.FILTER_INDEX_COLOR:
				checkCrop.setSelected(param.isColorPageCrop());
				break;
			case ImageFilterParamSet.FILTER_INDEX_PICT:
				checkCrop.setSelected(param.isPictPageCrop());
				break;
			case ImageFilterParamSet.FILTER_INDEX_TEXT:
				checkCrop.setSelected(param.isTextPageCrop());
				break;
			case ImageFilterParamSet.FILTER_INDEX_BASIC:
			default:
				checkCrop.setSelected(param.isFullPageCrop());
				break;
			}
			if(mFilterIndex == ImageFilterParamSet.FILTER_INDEX_TEXT){
				checkUnification.setSelected(param.isUnificationTextPage());
			}
		}
		
		if(param.isBlur()){
			
		}
		if(param.isSharpness()){
			spinSharpness.setValue((int)param.getSharpnessPixels());
		}
		
		if(param.isGamma()){
			spinGamma.setValue((float)param.getGamma());
		}
		if(param.isContrast()){
			sliderContrast.setValue(convContrast(param.getContrast()));
			sliderBrightness.setValue((int)param.getBrightness());
		}

		if(mFilterIndex == ImageFilterParamSet.FILTER_INDEX_BASIC && param.isFullPageCrop()){
			spinCropLeft.setValue(param.getFullPageCropLeft());
			spinCropRight.setValue(param.getFullPageCropRight());
			spinCropTop.setValue(param.getFullPageCropTop());
			spinCropBottom.setValue(param.getFullPageCropBottom());
			if(checkCrop.isSelected()){
				update = true;
			}
		}
		if(mFilterIndex == ImageFilterParamSet.FILTER_INDEX_COLOR && param.isColorPageCrop()){
			spinCropLeft.setValue(param.getColorPageCropLeft());
			spinCropRight.setValue(param.getColorPageCropRight());
			spinCropTop.setValue(param.getColorPageCropTop());
			spinCropBottom.setValue(param.getColorPageCropBottom());
			if(checkCrop.isSelected()){
				update = true;
			}
		}
		if(mFilterIndex == ImageFilterParamSet.FILTER_INDEX_PICT && param.isPictPageCrop()){
			spinCropLeft.setValue(param.getPictPageCropLeft());
			spinCropRight.setValue(param.getPictPageCropRight());
			spinCropTop.setValue(param.getPictPageCropTop());
			spinCropBottom.setValue(param.getPictPageCropBottom());
			if(checkCrop.isSelected()){
				update = true;
			}
		}
		if(mFilterIndex == ImageFilterParamSet.FILTER_INDEX_TEXT && param.isTextPageCrop()){
			spinCropLeft.setValue(param.getTextPageCropLeft());
			spinCropRight.setValue(param.getTextPageCropRight());
			spinCropTop.setValue(param.getTextPageCropTop());
			spinCropBottom.setValue(param.getTextPageCropBottom());
			if(checkCrop.isSelected()){
				update = true;
			}
		}
		
		if(param.isUnificationTextPage()){
			update = true;
		}
		
		return update;
	}
	
	public ImageFilterParam getImageFilterParam(){
		boolean isEnable = checkEnable.isSelected();
		boolean isBlur = checkBlur.isSelected();
		boolean isSharpness = checkSharpness.isSelected();
		boolean isContrast = checkContrast.isSelected();
		boolean isGamma = checkGamma.isSelected();
		boolean isGrayscale = checkGrayscale.isSelected();
		boolean isCrop = checkCrop.isSelected();
		boolean isAutoCrop = checkAutoCrop.isSelected();
		boolean isUnificationTextPage = false;
		if(mFilterIndex == ImageFilterParamSet.FILTER_INDEX_TEXT){
			isUnificationTextPage = checkUnification.isSelected();
		}
			
		// Spinner
		int cropLeft = (Integer)spinCropLeft.getValue();
		int cropRight = (Integer)spinCropRight.getValue();
		int cropTop = (Integer)spinCropTop.getValue();
		int cropBottom = (Integer)spinCropBottom.getValue();
			
			
		double gamma = getGammaValue() / 10;

		// Slider
		int contrast = sliderContrast.getValue();
		int brightness = sliderBrightness.getValue();
			
			
		ImageFilterParam param = new ImageFilterParam();
		
		param.setEnable(isEnable);
		param.setBlur(isBlur);
		param.setSharpness(isSharpness);
		//TODO: blur/shapness value
		param.setSharpnessPixels((Integer)spinSharpness.getValue());
		param.setContrast(isContrast);
		param.setGamma(isGamma);
		param.setGrayscale(isGrayscale);
		switch(mFilterIndex){
		case ImageFilterParamSet.FILTER_INDEX_COLOR:
			param.setColorPageAutoCrop(isAutoCrop);
			param.setColorPageCrop(isAutoCrop ? false : isCrop);
			param.setColorPageCrop(cropLeft, cropTop, cropRight, cropBottom);
			break;
		case ImageFilterParamSet.FILTER_INDEX_PICT:
			param.setPictPageAutoCrop(isAutoCrop);
			param.setPictPageCrop(isAutoCrop ? false : isCrop);
			param.setPictPageCrop(cropLeft, cropTop, cropRight, cropBottom);
			break;
		case ImageFilterParamSet.FILTER_INDEX_TEXT:
			param.setTextPageAutoCrop(isAutoCrop);
			param.setTextPageCrop(isAutoCrop ? false : isCrop);
			param.setTextPageCrop(cropLeft, cropTop, cropRight, cropBottom);
			break;
		case ImageFilterParamSet.FILTER_INDEX_BASIC:
		default:
			param.setFullPageAutoCrop(isAutoCrop);
			param.setFullPageCrop(isAutoCrop ? false : isCrop);
			param.setFullPageCrop(cropLeft, cropTop, cropRight, cropBottom);
			break;
		}
		param.setGamma(gamma);
		param.setContrast(convContrast(contrast));
		param.setBrightness((float)brightness);
			
		if(mFilterIndex == ImageFilterParamSet.FILTER_INDEX_TEXT){
			param.setUnificationTextPage(isUnificationTextPage);
		}

		return param;
	}

	public boolean isUnification(){
		if(mFilterIndex == ImageFilterParamSet.FILTER_INDEX_TEXT){
			return checkUnification.isSelected();
		}else{
			return false;
		}
	}
	
	private int mOldSharpLevel = 1;
	
	private double mOldGamma = 1.0f;
	private double getGammaValue(){
		double gamma = 1.0f;
		try {
			Object val = spinGamma.getValue();
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

}
