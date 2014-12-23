package e4x.browser.columns;

import org.eclipse.swt.graphics.Image;

import e4x.browser.cells.CellData;
import e4x.browser.model.AdvancedFile;

public class FilesizeColumn extends AbstractBrowserColumn<Long> {

	@Override
	public CellData<Long> getCell(AdvancedFile file) {
		return new CellData<Long>() {
			@Override
			public Long getContent() {
				return (file.isDirectory() ? 0l : file.getFilesize());
			}

			@Override
			public Image getIcon() {
				return null;
			}

			@Override
			public String getText() {
				return getContent()+" byte";
			}

			@Override
			public AdvancedFile getFile() {
				return file;
			}
		};
	}

	@Override
	public String getName() {
		return "Size";
	}

	@Override
	public int getWidth() {
		return 100;
	}

	@Override
	public int compare(CellData<Long> c1, CellData<Long> c2) {
		Long l1 = c1.getContent();
		Long l2 = c2.getContent();
		return l1.compareTo(l2);
	}


}
