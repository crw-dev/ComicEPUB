package jp.crwdev.app;

import jp.crwdev.app.gui.MainFrame;

public class MainApp {
	public static void main(String[] args){
		
		new MainFrame();
		
		/*
		//String filepath = "I:\\Android\\sample\\image1.zip";
		String filepath = "I:\\Android\\sample\\original";
		
		
		IImageFileScanner scanner = ImageFileScanner.getFileScanner(filepath);
		
		if(scanner == null){
			System.out.println("could not open. " + filepath);
			return;
		}

		
		ImageFilterParam baseParam = new ImageFilterParam();
		baseParam.setEnable(false);
		baseParam.setEqualize(true);
		baseParam.setResize(true);
		baseParam.setResizeDimension(800,800);
		baseParam.setGrayscale(false);
		baseParam.setFullPageCrop(false);
		baseParam.setFullPageCrop(50,50,50,50);
		baseParam.setDrawCropAreaInPreview(true);
		
//		try {
////		    FileOutputStream fos = new FileOutputStream(new File("I:\\Android\\sample\\serial.txt"));
////		    ObjectOutputStream oos = new ObjectOutputStream(fos);
////		    oos.writeObject(baseParam);
////		    oos.close();
////		    
//		    FileInputStream fis = new FileInputStream(new File("I:\\Android\\sample\\serial.txt"));
//		    ObjectInputStream ois = new ObjectInputStream(fis);
//		    baseParam = (ImageFilterParam) ois.readObject();
//		    ois.close();
//		} catch (Exception e) {
//		}
		
//		ImageFilterParam param = new ImageFilterParam();
//		param.setRotate(true);
//		param.setRotateAngle(10);
//		param.setPageType(Constant.PAGETYPE_TEXT);
//		param.setTextPageCrop(true);
//		param.setTextPageCrop(20,50,20,50);
		
		IImageFileInfoList list = scanner.getImageFileInfoList();
		int size = list.size();
		for(int i=0; i<size; i++){
			IImageFileInfo info = list.get(i);
			BufferedImage image = BufferedImageIO.read(info.getInputStream(), info.isJpeg());
			
			PreviewImageFilter filter1 = new PreviewImageFilter(true);
			filter1.setBaseFilterParam(baseParam);
			BufferedImage dest1 = filter1.filter(image, info.getFilterParam());
			BufferedImageIO.write(dest1, "jpeg", 0.8f, new FileOutputStream(new File("I:\\Android\\sample\\image" + i + "_preview.jpg")));

			PreviewImageFilter filter2 = new PreviewImageFilter(false);
			filter2.setBaseFilterParam(baseParam);
			BufferedImage dest2 = filter2.filter(image, info.getFilterParam());
			BufferedImageIO.write(dest2, "jpeg", 0.8f, new FileOutputStream(new File("I:\\Android\\sample\\image" + i + ".jpg")));
		}
		scanner.close();
		*/
	}
}
