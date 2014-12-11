package jp.crwdev.app.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedList;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.MouseInputAdapter;

import jp.crwdev.app.BufferedImageIO;
import jp.crwdev.app.EventObserver;
import jp.crwdev.app.EventObserver.OnEventListener;
import jp.crwdev.app.imagefilter.ImageFilterParam;
import jp.crwdev.app.imagefilter.ResizeFilter;
import jp.crwdev.app.interfaces.IImageFileInfo;
import jp.crwdev.app.interfaces.IImageFileInfoList;

@SuppressWarnings("serial")
public class ThumbnailView extends JFrame implements OnEventListener{
	
//	private JScrollPane mScrollPane;
	private DefaultListModel mModel;
	private JList mList;
	private IImageFileInfoList mInfoList;
	private ImageFileInfoTable mInfoTable;
//	private boolean mWindowSizeMax = false;

	private ThumbnailLoadThread mThread = new ThumbnailLoadThread();
	
	public ThumbnailView(){
		super();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(new Rectangle(200, 200, 120*8+20, 150));
		//setViewSize(mWindowSizeMax);
		
		
		mModel = new DefaultListModel();
		mList = new JList(mModel);
		mList.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		mList.setVisibleRowCount(1);
		mList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		mList.addListSelectionListener(new ListSelectionListener(){
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if(e.getValueIsAdjusting()){
					return;
				}
				int selected = mList.getSelectedIndex();
				if(mInfoTable != null){
					//mInfoTable.selectItem(selected);
					mInfoTable.setRowSelectionInterval(selected, selected);
				}
			}
		});

		mList.setCellRenderer(new ThumbnailRenderer());
		
		JScrollPane scroll = new JScrollPane(mList, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scroll.setWheelScrollingEnabled(true);
//		mScrollPane = scroll;
		//scroll.setBorder(new BevelBorder(BevelBorder.LOWERED));

		SpringLayout layout = new SpringLayout();
		setLayout(layout);
		
		
		Container contentPane = getContentPane();
		contentPane.add(scroll);
		
		layout.putConstraint(SpringLayout.NORTH, scroll, 3, SpringLayout.NORTH, contentPane);
		layout.putConstraint(SpringLayout.SOUTH, scroll, -3, SpringLayout.SOUTH, contentPane);
		layout.putConstraint(SpringLayout.WEST, scroll, 10, SpringLayout.WEST, contentPane);
		layout.putConstraint(SpringLayout.EAST, scroll, -10, SpringLayout.EAST, contentPane);

		
		MouseInputAdapter adapter = new MouseInputAdapter(){
			private Point prev;
			private Point winPos;
			@Override
			public void mouseClicked(MouseEvent e){
				if(javax.swing.SwingUtilities.isRightMouseButton(e)){
					JPopupMenu popup = new JPopupMenu();
					String menuTitle = isAlwaysOnTop() ? "最前面表示しない" : "常に最前面表示";
					JMenuItem item0 = new JMenuItem(menuTitle);
					item0.addActionListener(new ActionListener(){
						@Override
						public void actionPerformed(ActionEvent arg0) {
							setAlwaysOnTop(!isAlwaysOnTop());
						}
					});
					popup.add(item0);
					
//					JMenuItem item1 = new JMenuItem("ウインドウサイズ変更");
//					item1.addActionListener(new ActionListener(){
//						@Override
//						public void actionPerformed(ActionEvent arg0) {
//							setViewSize(!mWindowSizeMax);
//						}
//					});
//					popup.add(item1);
					
					JMenuItem item2 = new JMenuItem("非表示にする");
					item2.addActionListener(new ActionListener(){
						@Override
						public void actionPerformed(ActionEvent arg0) {
							setVisible(false);
						}
					});
					popup.add(item2);
					
					popup.show(e.getComponent(), e.getX(), e.getY());

				}
			}
			@Override
			public void mousePressed(MouseEvent e){
				if(prev == null){
					prev = new Point(e.getX(), e.getY());
				}
			}
			@Override
			public void mouseReleased(MouseEvent e){
				prev = null;
			}
			@Override
			public void mouseDragged(MouseEvent e){
				Window window = ThumbnailView.this;
				if(prev == null){
					prev = new Point(e.getX(), e.getY());
				}
				int x = e.getX() - prev.x;
				int y = e.getY() - prev.y;
				
				winPos = window.getLocation(winPos);
				window.setLocation(winPos.x + x, winPos.y + y);
			}
		};
		
		addMouseListener(adapter);
		addMouseMotionListener(adapter);
		
		setUndecorated(true);
		//setOpacity(0.5f);
//		setBackground(new Color(0, 0, 0));
		getContentPane().setBackground(Color.GRAY);
		//setAlwaysOnTop(true);
		//setVisible(true);
		
		mThread.start();

	}
	
	
	public void setViewSize(boolean maximum){
		
		int width = 800;
		Dimension screenSize = new Dimension(800,600);
			try{
				GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
				GraphicsDevice graphicsDevice = graphicsEnvironment.getDefaultScreenDevice();
				Rectangle rect = graphicsDevice.getDefaultConfiguration().getBounds();
				//screenSize = rect.getSize();
				width = rect.width;
				
				Rectangle desktopBounds = graphicsEnvironment.getMaximumWindowBounds();
				if(maximum){
					screenSize.setSize(desktopBounds.width, desktopBounds.height);
				}
				
			}catch(Exception ex){
				ex.printStackTrace();
			}
		
//		mWindowSizeMax = maximum;
		
		setBounds(width - screenSize.width, screenSize.height-150, screenSize.width, 150);
//		setSize(screenSize.width, 150);
	}
	
	public void setImageFileInfoTable(ImageFileInfoTable table){
		mInfoTable = table;
	}
	
	public void setImageFileInfoList(IImageFileInfoList list){
		mModel.clear();
		for(ThumbnailPanel panel : mPanelMap.values()){
			panel.release();
		}
		mPanelMap.clear();
		mInfoList = list;
		if(mInfoList != null){
			for(int i=0; i<mInfoList.size(); i++){
				mModel.addElement(mInfoList.get(i));
			}
		}
	}
	
	private HashMap<IImageFileInfo, ThumbnailPanel> mPanelMap = new HashMap<IImageFileInfo, ThumbnailPanel>();
	
	public ThumbnailPanel getThumbnailPanel(IImageFileInfo info){
		if(mPanelMap.containsKey(info)){
			return mPanelMap.get(info);
		}
		else{
			ThumbnailPanel panel = new ThumbnailPanel(info);
			mPanelMap.put(info, panel);
			return panel;
		}
	}
	
	public BufferedImage getThumbnail(IImageFileInfo info){
		InputStream in = info.getInputStream();
		BufferedImage image = null;
		if(in != null){
			image = BufferedImageIO.read(info.getInputStream(), info.isJpeg());
		}
		else{
			image = info.getImage(true);
		}
		ImageFilterParam param = new ImageFilterParam();
		param.setResize(true);
		param.setResizeDimension(120, 120);
		ResizeFilter filter = new ResizeFilter();
		image = filter.filter(image, param);
		return image;
	}
	
	public class ThumbnailLoadThread extends Thread {
		
		private LinkedList<ThumbnailPanel> mQueue = new LinkedList<ThumbnailPanel>();
		private boolean mIsAbort = false;
		private Object mLock = new Object();
		
		@Override
		public void run(){
			ThumbnailPanel panel = null;
			mIsAbort = false;
			while(!mIsAbort){
				try {
					synchronized(mLock){
						mLock.wait();
					}
					
					while(mQueue.size() > 0){
						synchronized(mQueue){
							panel = mQueue.removeLast();
						}
						if(panel != null){
							BufferedImage image = getThumbnail(panel.getImageFileInfo());
							panel.setThumbnail(image);
							mList.repaint();
						}
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		
		public void request(ThumbnailPanel panel){
			synchronized(mQueue){
				if(mQueue.size() > 15){
					while(mQueue.size() > 15){
						ThumbnailPanel removed = mQueue.removeLast();
						if(removed != null){
							removed.setLoading(false);
						}
					}
				}
				mQueue.push(panel);
			}
			synchronized(mLock){
				mLock.notify();
			}
		}
		
		public void release(){
			mIsAbort = true;
			synchronized(mQueue){
				mQueue.clear();
			}
			synchronized(mLock){
				mLock.notify();
			}
		}
	}

	public class ThumbnailPanel extends JPanel {
		
		private static final int PanelWidth = 120;
		private static final int PanelHeight = 126;
		
		private BufferedImage mThumbnail;
		private IImageFileInfo mInfo;
		private boolean mLoading = false;
		
		public ThumbnailPanel(IImageFileInfo info){
			setPreferredSize(new Dimension(PanelWidth, PanelHeight));
			mInfo = info;
		}

		public IImageFileInfo getImageFileInfo(){
			return mInfo;
		}
		
		public void setThumbnail(BufferedImage image){
			mThumbnail = image;
		}
		
		public void setLoading(boolean loading){
			mLoading = loading;
		}
		
		public void release(){
			mThumbnail = null;
			mInfo = null;
		}
		
		@Override
		public void paint(Graphics g) {
			super.paint(g);
			
			if(mThumbnail == null && !mLoading){
				mLoading = true;
				if(mThread != null){
					mThread.request(this);
				}
			}
			if(mThumbnail != null){
				int x = (PanelWidth - mThumbnail.getWidth())/2;
				int y = (PanelHeight - mThumbnail.getHeight())/2;
				
				g.drawImage(mThumbnail, x, y, null);
			}
		}

	}

	public class ThumbnailRenderer implements ListCellRenderer {
		
		@Override
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			
			JPanel panel;
			if(value instanceof IImageFileInfo){
				IImageFileInfo info = (IImageFileInfo)value;
				panel = getThumbnailPanel(info);
			}else{
				panel = new JPanel();
			}
			
			if(isSelected){
				panel.setBackground(Color.MAGENTA);
			}else if(cellHasFocus){
				panel.setBackground(Color.CYAN);
			}else{
				panel.setBackground(Color.LIGHT_GRAY);
			}
			return panel;
		}
		
	}
	

	@Override
	public void onEventReceived(int type, int arg1, int arg2, Object obj) {
		if(type == EventObserver.EventType_UpdateFileInfoList){
			setImageFileInfoList((IImageFileInfoList)obj);
		}
		else if(type == EventObserver.EventType_ShowHide_ThumbnailView){
			setVisible(isVisible() ? false : true);
		}
		
	}
}
