package rickelectric.furkmanager.views.iconutil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Observable;

import rickelectric.furkmanager.views.icons.FileIcon;

public class IconArray extends Observable implements List<FileIcon>{

	ArrayList<FileIcon> icons;
	
	public IconArray(){
		super();
		icons=new ArrayList<FileIcon>();
	}
	
	private void stateChanged(){
		setChanged();
		notifyObservers();
	}
	
	public int size() {
		return icons.size();
	}

	public boolean isEmpty() {
		return icons.isEmpty();
	}

	public boolean contains(Object o) {
		return icons.contains(o);
	}

	public Iterator<FileIcon> iterator() {
		return icons.iterator();
	}

	public Object[] toArray() {
		return icons.toArray();
	}

	public <T> T[] toArray(T[] a) {
		return icons.toArray(a);
	}

	@Override
	public boolean add(FileIcon e) {
		if(icons.add(e)){
			stateChanged();
			return true;
		}
		return false;
	}

	@Override
	public boolean remove(Object o) {
		if(icons.remove(o)){
			stateChanged();
			return true;
		}
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return icons.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends FileIcon> c) {
		if(icons.addAll(c)){
			stateChanged();
			return true;
		}
		return false;
	}

	@Override
	public boolean addAll(int index, Collection<? extends FileIcon> c) {
		if(icons.addAll(index, c)){
			stateChanged();
			return true;
		}
		return false;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		if(icons.removeAll(c)){
			stateChanged();
			return true;
		}
		return false;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		if(icons.retainAll(c)){
			stateChanged();
			return true;
		}
		return false;
	}

	@Override
	public void clear() {
		icons.clear();
		stateChanged();
	}

	@Override
	public FileIcon get(int index) {
		return icons.get(index);
	}

	@Override
	public FileIcon set(int index, FileIcon element) {
		FileIcon ico=icons.set(index, element);
		stateChanged();
		return ico;
	}

	@Override
	public void add(int index, FileIcon element) {
		icons.add(index, element);
		stateChanged();
	}

	@Override
	public FileIcon remove(int index) {
		FileIcon ico=icons.remove(index);
		stateChanged();
		return ico;
	}

	@Override
	public int indexOf(Object o) {
		return icons.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return icons.lastIndexOf(o);
	}

	@Override
	public ListIterator<FileIcon> listIterator() {
		return icons.listIterator();
	}

	@Override
	public ListIterator<FileIcon> listIterator(int index) {
		return icons.listIterator(index);
	}

	@Override
	public List<FileIcon> subList(int fromIndex, int toIndex) {
		return icons.subList(fromIndex, toIndex);
	}
	
}