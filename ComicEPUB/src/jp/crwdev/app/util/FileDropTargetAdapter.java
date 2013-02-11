package jp.crwdev.app.util;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;

import jp.crwdev.app.constant.Constant;

public class FileDropTargetAdapter extends DropTargetAdapter {

	public interface OnDropListener {
		void onDrop(String filepath);
	}
	
	private OnDropListener mListener = null;

	
	public FileDropTargetAdapter(OnDropListener listener){
		mListener = listener;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void drop(DropTargetDropEvent e) {

		// ドロップ操作を受け入れる．
		e.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);

		// ドロップに関係したTransferableを取得する．
		Transferable trans = e.getTransferable();

		String dropFile = null;
		
		try {
			// ドロップされたファイル一覧のリストを取得する．
			List<File> list = (List<File>) trans.getTransferData(DataFlavor.javaFileListFlavor);
			
			// リストからファイルを一つ一つ取得する．
			for (File file : list) {
				// ファイルパスを取得する．
				String fileName = file.getAbsolutePath();
				
				// テキストエリアにファイル名を追加表示する．
				
				if(file.isDirectory()){
					dropFile = fileName;
				}
				else{
					int dotIndex = fileName.lastIndexOf(".");
					String suffix = "";
					if(dotIndex >= 0){
						suffix = fileName.substring(dotIndex + 1);
					}
					if(Constant.SUPPORT_INPUT_PREFIX.contains(suffix.toLowerCase())){
						dropFile = fileName;
					}else{
						String parent = file.getParent();
						dropFile = parent;
					}
				}
				break;
			}
			
			// ドロップ処理が正常に完了したことを伝える．
			e.dropComplete(true);
			
		} catch (UnsupportedFlavorException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		if(dropFile != null && mListener != null){
			mListener.onDrop(dropFile);
		}
	}

}
