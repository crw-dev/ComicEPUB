package jp.crwdev.app.container.pdf;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;

import jp.crwdev.app.BufferedImageIO;
import jp.crwdev.app.interfaces.IImageFileInfo;
import jp.crwdev.app.interfaces.IImageFileInfoList;
import jp.crwdev.app.interfaces.IImageFileWriter;
import jp.crwdev.app.interfaces.IImageFilter;

public class PdfImageFileWriter implements IImageFileWriter {

	
	/** 出力先ファイル */
	private File mOutputFile = null;
	/** 画像フィルタ */
	private IImageFilter mBaseFilter = null;
	/** 処理中断フラグ */
	private boolean mIsCancel = false;
	
	private String mTitle = "";
	private String mTitleKana = "";
	private String mAuthor = "";
	private String mAuthorKana = "";
	private String mBookType = "book";
	
	/**
	 * コンストラクタ
	 * @param title
	 * @param titleKana
	 * @param author
	 * @param authorKana
	 */
	public PdfImageFileWriter(String title, String titleKana, String author, String authorKana){
		mTitle = title;
		mTitleKana = titleKana;
		mAuthor = author;
		mAuthorKana = authorKana;
	}

	
	@Override
	public boolean open(String filepath) {
		File file = new File(filepath);
		if(file.exists()){
			if(!file.delete()){
				return false;
			}
		}
		
		mOutputFile = file;
		
		return true;
	}

	@Override
	public void setImageFilter(IImageFilter filter) {
		mBaseFilter = filter;
	}

	@Override
	public boolean write(IImageFileInfoList list, OnProgressListener listener) {
		Document document = null;

		PdfWriter writer = null;
		
		try {
			int size = list.size();
			float progressOffset = 100 / (float)size;
			
			for(int i=0; i<size; i++){
				if(mIsCancel){
					return false;
				}
				
				IImageFileInfo info = list.get(i);
			
				BufferedImage image = BufferedImageIO.read(info.getInputStream(), info.isJpeg());
				if(mBaseFilter != null){
					image = mBaseFilter.filter(image, info.getFilterParam());
				}
				
				
				int imageWidth = image.getWidth();
				int imageHeight = image.getHeight();
				
				if(document == null){
					// Open前にサイズ指定しないと１ページ目に反映されない…
					document = new Document(PageSize.A4, 0, 0, 0, 0);
					document.setPageSize(new Rectangle(0, 0, imageWidth, imageHeight));
					
					writer = PdfWriter.getInstance(document, new FileOutputStream(mOutputFile.getAbsoluteFile()));
					document.open();
					
					document.addTitle(mTitle);
					document.addCreator(mAuthor);
					document.addCreationDate();
				}
				else{
					document.setPageSize(new Rectangle(0, 0, imageWidth, imageHeight));
				}

				Image jpeg2pdfImage = Image.getInstance(image, null);
				document.add(jpeg2pdfImage);
				
				if(listener != null){
					listener.onProgress((int)((i+1)*progressOffset), null);
				}
			}
			
			document.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally{
			if(document != null){
				document.close();
				document = null;
			}
			if(writer != null){
				writer.close();
				writer = null;
			}
		}

		return true;
	}

	@Override
	public void close() {
		if(mIsCancel){
			if(mOutputFile != null){
				if(mOutputFile.exists()){
					mOutputFile.delete();
				}
			}
		}
		if(mOutputFile != null){
			mOutputFile = null;
		}
	}

	@Override
	public void cancel() {
		mIsCancel = true;
	}

	@Override
	public String getSuffix() {
		return ".pdf";
	}

}
