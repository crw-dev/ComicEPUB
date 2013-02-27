package jp.crwdev.app.util;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jp.crwdev.app.constant.Constant;


public class FileListDropTargetAdapter extends DropTargetAdapter {

	public interface OnDropFilesListener {
		void onDrop(List<String> filepath);
	}
	
	private OnDropFilesListener mListener = null;

	public FileListDropTargetAdapter(OnDropFilesListener listener){
		mListener = listener;
	}

	
	@Override
	public void drop(DropTargetDropEvent e) {

		// ドロップ操作を受け入れる．
		e.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);

		// ドロップに関係したTransferableを取得する．
		Transferable trans = e.getTransferable();

		List<String> dropFiles = new ArrayList<String>();
		
		try {
			// ドロップされたファイル一覧のリストを取得する．
			List<File> list = (List<File>) trans.getTransferData(DataFlavor.javaFileListFlavor);
			
			// リストからファイルを一つ一つ取得する．
			for (File file : list) {
				// ファイルパスを取得する．
				String fileName = file.getAbsolutePath();
				
				File settingFile = Constant.getSettingFile(file);
				if(settingFile != null){
					File contentFile = Constant.getContentFile(file);
					String filepath = contentFile.getAbsolutePath();
					if(!dropFiles.contains(filepath)){
						dropFiles.add(filepath);
					}
				}
			}
			
			// ドロップ処理が正常に完了したことを伝える．
			e.dropComplete(true);
			
		} catch (UnsupportedFlavorException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		if(dropFiles != null && dropFiles.size() > 0 && mListener != null){
			mListener.onDrop(dropFiles);
		}
	}

}
