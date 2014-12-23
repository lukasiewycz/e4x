package e4x.browser.columns;

import org.eclipse.swt.graphics.Image;

import e4x.browser.cells.CellData;
import e4x.browser.model.AdvancedFile;

public class BasenameColumn extends AbstractBrowserColumn<String> {

	@Override
	public CellData<String> getCell(AdvancedFile file) {
		return new CellData<String>(){
			@Override
			public String getContent() {
				if(file.isDirectory()){
					return "["+file.getFilename()+"]";
				} else {
					return splitBasenameExtension(file.getFilename())[BASE];
				}
			}

			@Override
			public Image getIcon() {
				return file.getIcon();
			}

			@Override
			public String getText() {
				return getContent();
			}

			@Override
			public AdvancedFile getFile() {
				return file;
			}
		};
	}

	@Override
	public String getName() {
		return "Name";
	}

	@Override
	public int getWidth() {
		return 200;
	}
	
	@Override
	public int compare(CellData<String> c1, CellData<String> c2) {
		String s1 = c1.getContent().toLowerCase();
		String s2 = c2.getContent().toLowerCase();
		return s1.compareTo(s2);
	}
}
