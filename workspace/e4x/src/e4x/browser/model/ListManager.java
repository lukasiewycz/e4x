package e4x.browser.model;

import ca.odell.glazedlists.EventList;

public interface ListManager<E extends Element> {
	
	public void init(EventList<Element> list, E element);
	
	public void connect();
	
	public void disconnect();

}
