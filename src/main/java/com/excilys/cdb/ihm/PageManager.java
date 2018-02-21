package main.java.com.excilys.cdb.ihm;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import main.java.com.excilys.cdb.model.ModelClass;

public class PageManager {
	
	private final long limit;
	private Function<Object[], Object> getList;
	private long max;
	private long maxPage;
	private long offset;
	private ArrayList<ModelClass> pageData;
	
	public PageManager(long limit, long max, Function<Object[], Object> getList) {
		this.offset = 0;
		this.limit = limit;
		setMax(max);
		this.getList = getList;
		this.pageData = new ArrayList<>();
		this.getItems();
	}
	
	@UserChoice(name = "Get first page")
	public boolean first() {
		offset = 0;
		return getItems();
	}
	
	public long getLimit() {
		return limit;
	}
	
	public long getMax() {
		return max;
	}
	
	public long getMaxPage() {
		return maxPage;
	}
	
	public long getOffset() {
		return offset;
	}

	public long getPage() {
		return (offset / limit) + 1;
	}

	public ArrayList<ModelClass> getPageData() {
		return pageData;
	}

	@UserChoice(name = "Get last page")
	public boolean last() {
		offset = (maxPage - 1) * limit;
		return getItems();
	}

	@UserChoice(name = "Get next page")
	public boolean next() {
		if (offset + limit > max - 1) {
			return false;
		}
		offset += limit;
		return getItems();
	}

	@UserChoice(name = "Get previous page")
	public boolean previous() {
		if (offset - limit < 0) {
			return false;
		}
		
		offset -= limit;
		return getItems();
	}

	public void setMax(long max) {
		this.max = max;
		this.maxPage = (long) Math.ceil(((double) max) / (double) limit);
	}

	public void setOffset(long offset) {
		this.offset = offset;
	}

	private boolean getItems() {
		Object[] parameters = {offset, limit};

		pageData.clear();
		((List<?>) getList.apply(parameters)).forEach(x -> pageData.add((ModelClass) x));
		return true;
	}
	
}
