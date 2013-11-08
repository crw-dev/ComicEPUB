package jp.crwdev.app.util;

import java.awt.Cursor;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DragSource;
import java.util.ArrayList;
import java.util.Vector;

import javax.activation.ActivationDataFlavor;
import javax.activation.DataHandler;
import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.TransferHandler;
import javax.swing.table.DefaultTableModel;

import jp.crwdev.app.constant.Constant;
import jp.crwdev.app.interfaces.IImageFileInfo;
import jp.crwdev.app.interfaces.IImageFileInfoList;
import jp.crwdev.app.interfaces.ISimpleCallback;

public class TableRowTransferHandler extends TransferHandler {
	private int[] rows    = null;
	private int addIndex  = -1; //Location where items were added
	private int addCount  = 0;  //Number of items added.
	private final DataFlavor localObjectFlavor;
	private JComponent source = null;
	private IImageFileInfoList mList = null;
	private ISimpleCallback mCallback = null;
	
	public TableRowTransferHandler(IImageFileInfoList list, ISimpleCallback callback) {
		mList = list;
		mCallback = callback;
		localObjectFlavor = new ActivationDataFlavor(
				int[].class, DataFlavor.javaJVMLocalObjectMimeType, "Array of items");
	}
	
	@Override
	protected Transferable createTransferable(JComponent c) {
		source = c;
		JTable table = (JTable) c;
		rows = table.getSelectedRows();
		return new DataHandler(rows,localObjectFlavor.getMimeType());
	}
	
	@Override
	public boolean canImport(TransferSupport info) {
		JTable t = (JTable)info.getComponent();
		boolean b = info.isDrop()&&info.isDataFlavorSupported(localObjectFlavor);
		//XXX bug?
		t.setCursor(b?DragSource.DefaultMoveDrop:DragSource.DefaultMoveNoDrop);
		return b;
	}
	
	@Override
	public int getSourceActions(JComponent c) {
		return TransferHandler.MOVE;//COPY_OR_MOVE;
	}
	
	@Override
	public boolean importData(TransferSupport info) {
		JTable target = (JTable)info.getComponent();
		JTable.DropLocation dl = (JTable.DropLocation)info.getDropLocation();
		DefaultTableModel model = (DefaultTableModel)target.getModel();
		int index = dl.getRow();
		int max = model.getRowCount();
		if(index<0 || index>max) index = max;
		addIndex = index;
		target.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		try {
			int[] indices =
					(int[])info.getTransferable().getTransferData(localObjectFlavor);
			if(source==target) addCount = indices.length;
			for(int i=0;i<indices.length;i++) {
				int idx = index++;
				IImageFileInfo fileInfo = mList.get(indices[i]);
				model.insertRow(idx, Constant.createRecord(fileInfo));
				mList.insert(idx, fileInfo);
				target.getSelectionModel().addSelectionInterval(idx, idx);
			}
			return true;
		}catch(Exception ufe) { ufe.printStackTrace(); }
		return false;
	}
	
	@Override
	protected void exportDone(JComponent c, Transferable t, int act) {
		cleanup(c, act == MOVE);
	}
	
	private void cleanup(JComponent src, boolean remove) {
		if(remove && rows != null) {
			JTable table = (JTable)src;
			src.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
			DefaultTableModel model = (DefaultTableModel)table.getModel();
			if(addCount > 0) {
				for(int i=0;i<rows.length;i++) {
					if(rows[i]>=addIndex) { rows[i] += addCount; }
				}
			}
			for(int i=rows.length-1;i>=0;i--){
				model.removeRow(rows[i]);
				mList.remove(rows[i]);
			}
			mList.setEnableSort(false);
			if(mCallback != null){
				mCallback.onCallback();
			}
		}
		rows     = null;
		addCount = 0;
		addIndex = -1;
	}
}