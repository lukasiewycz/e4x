package e4x.browser.columns;

import org.eclipse.swt.graphics.Image;

import e4x.browser.cells.CellData;
import e4x.browser.model.FileElement;

public class ExtensionColumn extends AbstractBrowserColumn<String> {

	@Override
	public CellData<String> getCell(FileElement file) {
		return new CellData<String>(){
			@Override
			public String getContent() {
				if(!file.isDirectory()){
					return splitBasenameExtension(file.getFilename())[EXTENSION];
				} else {
					return "";
				}
			}

			@Override
			public Image getIcon() {
				return null;
			}

			@Override
			public String getText() {
				return getContent();
			}

			@Override
			public FileElement getFile() {
				return file;
			}
		};
	}

	@Override
	public String getName() {
		return "Ext";
	}

	@Override
	public int getWidth() {
		return 100;
	}

	@Override
	public int compare(CellData<String> c1, CellData<String> c2) {
		String s1 = c1.getContent().toLowerCase();
		String s2 = c2.getContent().toLowerCase();
		return s1.compareTo(s2);
	}

}
