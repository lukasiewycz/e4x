package e4x.browser.columns;

import java.util.Comparator;

import org.eclipse.swt.graphics.Image;

import e4x.browser.cells.CellData;
import e4x.browser.model.AdvancedFile;

public class ExtensionColumn implements BrowserColumn<String> {

	@Override
	public CellData<String> getCell(AdvancedFile file) {
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
			public AdvancedFile getFile() {
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
	public Comparator<CellData<String>> getComparator() {
		return new Comparator<CellData<String>>(){
			@Override
			public int compare(CellData<String> o1, CellData<String> o2) {
				String s1 = o1.getContent().toLowerCase();
				String s2 = o2.getContent().toLowerCase();
				return s1.compareTo(s2);
			}
			
		};
	}
	
	public static int BASE = 0;
	public static int EXTENSION = 1;

	private String[] splitBasenameExtension(String filename) {
		String[] parts = (filename).split("\\.(?=[^\\.]+$)");
		
		if(parts[BASE].length()==0 && parts[EXTENSION].length()>0){
			return new String[]{filename,""};
		} else {
			return parts;
		}
	}

}
