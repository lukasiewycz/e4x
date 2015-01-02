package e4x.browser.model;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.HashMap;
import java.util.Map;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.TransformedList;

public class PathListManager implements ListManager<PathElement> {

	protected EventList<Element> list;
	protected PathElement element;
	protected TransformedList<Element, Element> threadSafeList;
	protected Thread thread;

	protected boolean isInit = false;
	protected boolean isConnected = false;

	@Override
	public synchronized void init(EventList<Element> list, PathElement element) {
		if (element == null || !element.isDirectory()) {
			throw new IllegalArgumentException("PathElement is not a directory");
		}

		if (!isInit) {
			this.list = list;
			this.element = element;
			threadSafeList = GlazedLists.threadSafeList(list);
			isInit = true;
		} else {
			throw new IllegalArgumentException();
		}
	}

	@Override
	public synchronized void connect() {
		if (!isInit) {
			throw new IllegalArgumentException("Manager has to be initialized");
		}

		if (!isConnected && thread == null) {
			final Path dir = element.getPath();

			isConnected = true;

			try {
				final WatchService watcher = dir.getFileSystem().newWatchService();
				final WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);

				Map<Path,PathElement> map = new HashMap<Path,PathElement>();
				
				thread = new Thread() {
					public void run() {
						list.clear();
						list.add(new ParentElement());

						try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(dir)) {
							for (Path path : directoryStream) {
								list.getReadWriteLock().writeLock().lock();
								list.add(create(path, map));
								list.getReadWriteLock().writeLock().unlock();
								Thread.sleep(100);
								if (!isConnected) {
									break;
								}
							}
						} catch (IOException ex) {
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

						while (isConnected) {
							for (WatchEvent<?> e : key.pollEvents()) {
								Path path = dir.resolve((Path)e.context());
								
								PathElement pathElement = new PathElement(dir.resolve((Path)e.context()));
								
								if (e.kind() == ENTRY_CREATE) {
									list.getReadWriteLock().writeLock().lock();
									list.add(create(path, map));
									list.getReadWriteLock().writeLock().unlock();
								}
								if (e.kind() == ENTRY_DELETE) {
									list.getReadWriteLock().writeLock().lock();
									list.remove(create(path, map));
									list.getReadWriteLock().writeLock().unlock();
								}
								if (e.kind() == ENTRY_MODIFY) {
									if(map.containsKey(path)){
										list.getReadWriteLock().writeLock().lock();
										map.get(path).fireModify();
										list.getReadWriteLock().writeLock().unlock();
									}
								}
							}
						}
					}
				};
				thread.start();

			} catch (IOException x) {
				System.err.println(x);
			}

		}
	}
	
	private PathElement create(Path path, Map<Path,PathElement> map){
		if(map.containsKey(path)){
			return map.get(path);
		} else {
			PathElement pathElement = new PathElement(path);
			map.put(path, pathElement);
			return pathElement;
		}
	}

	@Override
	public synchronized void disconnect() {
		if (isConnected) {
			isConnected = false;

			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
