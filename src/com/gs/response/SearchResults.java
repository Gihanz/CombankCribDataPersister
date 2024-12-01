package com.gs.response;

import java.util.List;

public class SearchResults {

	private List<SearchResultItem> searchResultItem;

	/**
	 * @return the searchResultItem
	 */
	public List<SearchResultItem> getSearchResultItem() {
		return searchResultItem;
	}

	/**
	 * @param searchResultItem the searchResultItem to set
	 */
	public void setSearchResultItem(List<SearchResultItem> searchResultItem) {
		this.searchResultItem = searchResultItem;
	}
	
}
