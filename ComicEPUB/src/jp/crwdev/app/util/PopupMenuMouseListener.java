package jp.crwdev.app.util;

import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.text.DefaultEditorKit;

/**
 * テキストフィールドにCut/Copy/Paste/SelectAllのポップアップメニューを追加するマウスリスナー
 *
 */
public class PopupMenuMouseListener implements MouseListener {

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		popupMenu(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		popupMenu(e);
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	private void popupMenu(MouseEvent e) {
		if(e.isPopupTrigger()){
			JComponent c = (JComponent)e.getSource();
			showPopup(c, e.getX(), e.getY());
			c.requestFocus();
			e.consume();
		}
	}
	
	private void showPopup(JComponent c, int x, int y) {
		JPopupMenu menu = new JPopupMenu();
		
		ActionMap am = c.getActionMap();
		
		Action cut = am.get(DefaultEditorKit.cutAction);
		addMenu(menu, "切り取り(X)", cut, 'X', KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_DOWN_MASK));
		
		Action copy = am.get(DefaultEditorKit.copyAction);
		addMenu(menu, "コピー(C)", copy, 'C', KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK));
		
		Action paste = am.get(DefaultEditorKit.pasteAction);
		addMenu(menu, "貼り付け(V)", paste, 'V', KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK));
		
		Action all = am.get(DefaultEditorKit.selectAllAction);
		addMenu(menu, "すべて選択(A)", all, 'A', KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.CTRL_DOWN_MASK));
		
		menu.show(c, x, y);
	}

	protected void addMenu(JPopupMenu pmenu, String text, Action action, int mnemonic, KeyStroke ks) {
		if (action != null) {
			JMenuItem mi = pmenu.add(action);
			if (text != null) {
				mi.setText(text);
			}
			if (mnemonic != 0) {
				mi.setMnemonic(mnemonic);
			}
			if (ks != null) {
				mi.setAccelerator(ks);
			}
		}
	}
}
