package e4x.browser.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import e4x.parts.FileHelper;

public class AdvancedPath implements AdvancedFile {
	
	protected Path path;
	
	public AdvancedPath(Path path){
		this.path = path;
	}

	public Long getFilesize(){
		try {
			if(isDirectory()){
				return 0l;
			}
			
			return Files.size(path);
		} catch (IOException e) {
			return 0l;
		}
	}
	
	public String getFilename(){
		return ""+path.getFileName();
	}

	@Override
	public boolean isDirectory() {
		return Files.isDirectory(path);
	}
	

	public static int BASE = 0;
	public static int EXTENSION = 1;

	private String[] getFilename(Path path) {
		return ("" + path.getFileName()).split("\\.(?=[^\\.]+$)");
	}

	@Override
	public Image getIcon() {
		Image image = FileHelper.getImage(path.toFile(), Display.getCurrent());
		return image;
	}

	
}
