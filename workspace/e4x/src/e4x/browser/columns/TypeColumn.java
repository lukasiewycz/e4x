package e4x.browser.columns;

import org.eclipse.swt.graphics.Image;

import e4x.browser.cells.CellData;
import e4x.browser.model.AdvancedFile;
import e4x.browser.model.ParentPath;

public class TypeColumn extends AbstractBrowserColumn<Integer> {

	@Override
	public CellData<Integer> getCell(AdvancedFile file) {
		return new CellData<Integer>(){
			@Override
			public Integer getContent() {
				return getValue(file);
			}

			@Override
			public Image getIcon() {
				return null;
			}

			@Override
			public String getText() {
				return ""+getContent();
			}

			@Override
			public AdvancedFile getFile() {
				return file;
			}
		};
	}

	@Override
	public String getName() {
		return "Type";
	}

	@Override
	public int getWidth() {
		return 100;
	}
	
	protected Integer getValue(AdvancedFile file){
		if(file instanceof ParentPath){
			return 0;
		} else if(file.isDirectory()){
			return 1;
		} else {
			return 2;
		}
	}

	@Override
	public int compare(CellData<Integer> c1, CellData<Integer> c2) {
		Integer s1 = c1.getContent();
		Integer s2 = c2.getContent();
		return s1.compareTo(s2);
	}

}
