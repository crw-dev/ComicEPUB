package jp.crwdev.app.gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;

public class ProgressIcon extends JComponent implements Icon {

	private Timer mTimer;
	private final List<Shape> mList;
	private final Stroke mStroke = new BasicStroke(2.0f);
	private final int ICON_SIZE=32;
	private boolean mIsRunning = false;
	private TimerTask mTask = null;

	public ProgressIcon() {
		super();
		mList = new ArrayList<Shape>();
		Shape line= new Line2D.Double(0.0,8.0,0.0,14.0);
		AffineTransform rot = AffineTransform.getRotateInstance(Math.toRadians(30.0));
		AffineTransform trans = AffineTransform.getTranslateInstance(ICON_SIZE/2.0, ICON_SIZE/2.0);
		for(int i=0;i<12;i++){
			line = rot.createTransformedShape(line);
			mList.add(trans.createTransformedShape(line));
		}
		setPreferredSize(new Dimension(ICON_SIZE, ICON_SIZE));
		//setVisible(false);
	}

	public void start() {
		if(mTimer == null){
			mTimer = new Timer();
			setVisible(true);
			mTimer.schedule(mTask = new TimerTask(){
				@Override
				public void run() {
					mList.add(mList.remove(0));
					repaint();
				}
			}, 100, 100);
			mIsRunning = true;
		}
	}

	public void stop() {
		if(mTimer != null){
			mTimer.cancel();
			mTimer = null;
			mIsRunning = false;
			setVisible(false);
		}
	}

	public boolean isRunning(){
		return mIsRunning;
	}
	
	@Override
	public void paintComponent(Graphics g) {
		Graphics2D g2=(Graphics2D)g;
		g2.setColor(getBackground());
		g2.fillRect(0,0,getWidth(),getHeight());
		g2.setStroke(mStroke);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		Iterator<Shape> it = mList.iterator();
		if(mIsRunning) {
			float alpha=0.083f;
			while(it.hasNext()) {
				g2.setColor(new Color(0.6f,0.6f,0.6f,alpha));
				g2.draw((Shape)it.next());
				alpha +=0.083f;
			}
		}else{
			while(it.hasNext()) {
				g2.setColor(new Color(0.6f,0.6f,0.6f));
				g2.draw((Shape)it.next());
			}
		}
	}

	@Override
	public int getIconHeight() {
		return ICON_SIZE;
	}

	@Override
	public int getIconWidth() {
		return ICON_SIZE;
	}

	@Override
	public void paintIcon(Component arg0, Graphics arg1, int arg2, int arg3) {
		SwingUtilities.paintComponent(arg1,this,
				(Container)arg0,new Rectangle(arg2,arg3,ICON_SIZE,ICON_SIZE));
	}


}
