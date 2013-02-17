package jp.crwdev.app.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import jp.crwdev.app.interfaces.ILineHandle;

public class SplitLineSet implements ILineHandle {
	
	/** 縦ライン */
	private List<LineHandle> mLineV = new ArrayList<LineHandle>();
	/** 横ライン */
	private List<LineHandle> mLineH = new ArrayList<LineHandle>();
	
	/**
	 * コンストラクタ
	 */
	public SplitLineSet(){
		setSplitColRow(4,4);
	}
	
	public SplitLineSet(float[] v, float[] h){
		setSplitColRow(v.length-1, h.length-1);
		for(int i=0; i<mLineV.size(); i++){
			mLineV.get(i).setBaseLineOffset(v[i]);
		}
		for(int i=0; i<mLineH.size(); i++){
			mLineH.get(i).setBaseLineOffset(h[i]);
		}
	}
	
	public void setSplitColRow(int col, int row){
		if((col == 1 && row > 1) || (col > 1 && row == 1) || (col > 1 && row > 1)){
			if((col == mLineV.size() - 1) && (row == mLineH.size() - 1)){
				return;
			}
			
			mLineV.clear();
			mLineH.clear();
			
			float offset = -0.5f;
			float add = 1.0f / col;
			for(int i=0; i<col+1; i++){
				mLineV.add(new LineHandle(true, offset, Color.MAGENTA, true));
				offset += add;
			}
			
			offset = -0.5f;
			add = 1.0f / row;
			for(int i=0; i<row+1; i++){
				mLineH.add(new LineHandle(false, offset, Color.MAGENTA, true));
				offset += add;
			}
		}
	}

	public float[] getV(){
		float[] v = new float[mLineV.size()];
		for(int i=0; i<mLineV.size(); i++){
			v[i] = mLineV.get(i).getBaseLineOffset();
		}
		return v;
	}
	
	public float[] getH(){
		float[] h = new float[mLineH.size()];
		for(int i=0; i<mLineH.size(); i++){
			h[i] = mLineH.get(i).getBaseLineOffset();
		}
		return h;
	}
	
	@Override
	public void fixPosition(boolean fixed) {
		// TODO Auto-generated method stub

	}

	@Override
	public void getHandleOffset(Dimension offset) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isDragHandle() {
		for(int i=0; i<mLineV.size(); i++){
			if(mLineV.get(i).isDragHandle()){
				return true;
			}
		}
		for(int i=0; i<mLineH.size(); i++){
			if(mLineH.get(i).isDragHandle()){
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isFixed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void paint(Graphics g, int width, int height, int imageWidth, int imageHeight) {
		for(int i=0; i<mLineV.size(); i++){
			mLineV.get(i).paint(g, width, height, imageWidth, imageHeight);
		}
		for(int i=0; i<mLineH.size(); i++){
			mLineH.get(i).paint(g, width, height, imageWidth, imageHeight);
		}

	}

	@Override
	public void resetPosition() {
		for(int i=0; i<mLineV.size(); i++){
			mLineV.get(i).resetPosition();
		}
		for(int i=0; i<mLineH.size(); i++){
			mLineH.get(i).resetPosition();
		}
	}

	@Override
	public void setBaseLineOffset(float offset) {
		for(int i=0; i<mLineV.size(); i++){
			mLineV.get(i).setBaseLineOffset(offset);
		}
		for(int i=0; i<mLineH.size(); i++){
			mLineH.get(i).setBaseLineOffset(offset);
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		for(int i=0; i<mLineV.size(); i++){
			mLineV.get(i).mouseClicked(e);
		}
		for(int i=0; i<mLineH.size(); i++){
			mLineH.get(i).mouseClicked(e);
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		for(int i=0; i<mLineV.size(); i++){
			mLineV.get(i).mouseEntered(e);
		}
		for(int i=0; i<mLineH.size(); i++){
			mLineH.get(i).mouseEntered(e);
		}
	}

	@Override
	public void mouseExited(MouseEvent e) {
		for(int i=0; i<mLineV.size(); i++){
			mLineV.get(i).mouseExited(e);
		}
		for(int i=0; i<mLineH.size(); i++){
			mLineH.get(i).mouseExited(e);
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		for(int i=0; i<mLineV.size(); i++){
			mLineV.get(i).mousePressed(e);
		}
		for(int i=0; i<mLineH.size(); i++){
			mLineH.get(i).mousePressed(e);
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		for(int i=0; i<mLineV.size(); i++){
			mLineV.get(i).mouseReleased(e);
		}
		for(int i=0; i<mLineH.size(); i++){
			mLineH.get(i).mouseReleased(e);
		}
	}

	private final LineHandleComparator mComparator = new LineHandleComparator();
	
	@Override
	public void mouseDragged(MouseEvent e) {
		
		for(int i=0; i<mLineV.size(); i++){
			LineHandle line = mLineV.get(i);
			line.mouseDragged(e);
			float offset = line.getBaseLineOffset();
			if(offset < -0.5f){
				line.setBaseLineOffset(-0.5f);
			}
			else if(offset > 0.5f){
				line.setBaseLineOffset(0.5f);
			}
			if(line.isDragHandle()){
				break;
			}
		}
		Collections.sort(mLineV, mComparator);
		
		for(int i=0; i<mLineH.size(); i++){
			LineHandle line = mLineH.get(i);
			line.mouseDragged(e);
			float offset = line.getBaseLineOffset();
			if(offset < -0.5f){
				line.setBaseLineOffset(-0.5f);
			}
			else if(offset > 0.5f){
				line.setBaseLineOffset(0.5f);
			}
			if(line.isDragHandle()){
				break;
			}
		}
		Collections.sort(mLineH, mComparator);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		for(int i=0; i<mLineV.size(); i++){
			mLineV.get(i).mouseMoved(e);
		}
		for(int i=0; i<mLineH.size(); i++){
			mLineH.get(i).mouseMoved(e);
		}
	}

	private class LineHandleComparator implements Comparator {

		@Override
		public int compare(Object arg0, Object arg1) {
			LineHandle line0 = (LineHandle)arg0;
			LineHandle line1 = (LineHandle)arg1;
			float offset0 = line0.getBaseLineOffset();
			float offset1 = line1.getBaseLineOffset();
			if(offset0 < offset1){
				return -1;
			}
			else if(offset0 > offset1){
				return 1;
			}
			else{
				return 0;
			}
		}
		
	}
}
