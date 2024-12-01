package com.gs.response;

import java.util.List;

public class MultiHit {

	private String referenceNumber;
	private List<SearchResultItem> searchResultItem;
	
	/**
	 * @return the referenceNumber
	 */
	public String getReferenceNumber() {
		return referenceNumber;
	}
	/**
	 * @param referenceNumber the referenceNumber to set
	 */
	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}
	/**
	 * @return the multiHitSrchRsltItem
	 */
	public List<SearchResultItem> getSearchResultItem() {
		return searchResultItem;
	}
	/**
	 * @param multiHitSrchRsltItem the multiHitSrchRsltItem to set
	 */
	public void setSearchResultItem(List<SearchResultItem> searchResultItem) {
		this.searchResultItem = searchResultItem;
	}
	
}
