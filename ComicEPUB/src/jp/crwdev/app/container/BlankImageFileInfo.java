package jp.crwdev.app.container;

import java.awt.image.BufferedImage;
import java.io.InputStream;

import jp.crwdev.app.constant.Constant;
import jp.crwdev.app.imagefilter.ImageFilterParam;
import jp.crwdev.app.interfaces.IImageFileInfo;

public class BlankImageFileInfo implements IImageFileInfo {

	@Override
	public String getFileName() {
		return null;
	}

	@Override
	public String getSortString() {
		return null;
	}

	@Override
	public String getFullPath() {
		return null;
	}

	@Override
	public String getFormat() {
		return null;
	}

	@Override
	public int getWidth() {
		return 0;
	}

	@Override
	public int getHeight() {
		return 0;
	}

	@Override
	public long getSize() {
		return 0;
	}

	@Override
	public void update() {
	}

	@Override
	public InputStream getInputStream() {
		return null;
	}

	@Override
	public BufferedImage getImage(boolean preview) {
		return null;
	}

	@Override
	public boolean isJpeg() {
		return false;
	}

	@Override
	public ImageFilterParam getFilterParam() {
		ImageFilterParam param = new ImageFilterParam();
		param.setPageSpread(Constant.PAGESPREAD_AUTO);
		return param;
	}

	@Override
	public void setFilterParam(ImageFilterParam param) {
	}

	@Override
	public void setTocText(String text) {
	}

	@Override
	public String getTocText() {
		return null;
	}

	@Override
	public void setEnable(boolean enable) {
	}

	@Override
	public boolean isEnable() {
		return true;
	}

	@Override
	public void setModify(boolean modify) {
	}

	@Override
	public boolean isModify() {
		return false;
	}

	@Override
	public void setSortOrder(int order) {
	}

	@Override
	public int getSortOrder() {
		return 0;
	}

	@Override
	public boolean isBlankPage() {
		return true;
	}
	
	@Override
	public void release() {
	}

}
