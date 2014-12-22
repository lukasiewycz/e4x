package e4x.browser.columns;

import java.util.Comparator;

import org.eclipse.swt.graphics.Image;

import e4x.browser.cells.CellData;
import e4x.browser.model.AdvancedFile;

public class FilesizeColumn implements BrowserColumn<Long> {

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
	public Comparator<CellData<Long>> getComparator() {
		return new Comparator<CellData<Long>>() {
			@Override
			public int compare(CellData<Long> o1, CellData<Long> o2) {
				Long l1 = o1.getContent();
				Long l2 = o2.getContent();
				return l1.compareTo(l2);
			}

		};
	}


}
