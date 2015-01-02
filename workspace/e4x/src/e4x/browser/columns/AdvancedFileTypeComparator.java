package e4x.browser.columns;

import java.util.Comparator;

import e4x.browser.model.FileElement;
import e4x.browser.model.ParentElement;

public class AdvancedFileTypeComparator implements Comparator<FileElement> {
	
	private int getValue(FileElement file){
		if(file instanceof ParentElement){
			return 0;
		} else if(file.isDirectory()){
			return 1;
		} else {
			return 2;
		}
	}
	
	
	@Override
	public int compare(FileElement o1, FileElement o2) {
		return getValue(o1) - getValue(o2);
	}

}
