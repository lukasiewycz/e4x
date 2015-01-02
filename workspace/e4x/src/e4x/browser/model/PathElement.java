package e4x.browser.model;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import e4x.parts.FileHelper;

public class PathElement implements FileElement {
	
	protected Path path;
	
	public PathElement(Path path){
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
	



	@Override
	public Image getIcon() {
		Image image = FileHelper.getImage(path.toFile(), Display.getCurrent());
		return image;
	}

	
}
