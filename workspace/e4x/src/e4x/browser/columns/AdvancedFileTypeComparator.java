package e4x.browser.columns;

import java.util.Comparator;

import e4x.browser.model.AdvancedFile;
import e4x.browser.model.ParentPath;

public class AdvancedFileTypeComparator implements Comparator<AdvancedFile> {
	
	private int getValue(AdvancedFile file){
		if(file instanceof ParentPath){
			return 0;
		} else if(file.isDirectory()){
			return 1;
		} else {
			return 2;
		}
	}
	
	
	@Override
	public int compare(AdvancedFile o1, AdvancedFile o2) {
		return getValue(o1) - getValue(o2);
	}

}
