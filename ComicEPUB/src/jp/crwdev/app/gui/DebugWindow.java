package jp.crwdev.app.gui;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


@SuppressWarnings("serial")
public class DebugWindow extends JFrame {

	private static DebugWindow mInstance;
	
	private JTextArea mTextView;
	private JScrollPane mScrollPane;
	
	public static DebugWindow initialize(){
		if(mInstance == null){
			mInstance = new DebugWindow();
		}
		return mInstance;
	}

	protected DebugWindow(){
		setBounds(100, 100, 600, 450);
		
		JTextArea textarea = new JTextArea("");
		mTextView = textarea;
		
		JScrollPane scrollpane = new JScrollPane();
		mScrollPane = scrollpane;
		scrollpane.setViewportView(textarea);
		
		getContentPane().add(scrollpane, BorderLayout.CENTER);
		setVisible(true);
	}
	
	public static void log(String str){
		if(mInstance != null){
			mInstance.mTextView.append(str + "\n");
			mInstance.mTextView.setAutoscrolls(true);
			JScrollBar bar = mInstance.mScrollPane.getVerticalScrollBar();
			if(bar != null){
				bar.setValue(bar.getMaximum());
			}
		}
	}
	
	public static void log(String tag, String str){
		if(mInstance != null){
			mInstance.mTextView.append(tag + ": " + str + "\n");
		}
	}
	
	public static void clear(){
		if(mInstance != null){
			mInstance.mTextView.setText("");
		}
	}
	

}
