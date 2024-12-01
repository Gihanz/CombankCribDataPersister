package com.gs.response;

public class ResponseBean {

	public String perHitCost;
	public String errorCode;
	public String error;
	public String statusCode;
	public String status;
	public MultiHit multiHit;
	public SearchResults searchResults;
	
	/**
	 * @return the perHitCost
	 */
	public String getPerHitCost() {
		return perHitCost;
	}
	/**
	 * @param perHitCost the perHitCost to set
	 */
	public void setPerHitCost(String perHitCost) {
		this.perHitCost = perHitCost;
	}
	/**
	 * @return the errorCode
	 */
	public String getErrorCode() {
		return errorCode;
	}
	/**
	 * @param errorCode the errorCode to set
	 */
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	/**
	 * @return the error
	 */
	public String getError() {
		return error;
	}
	/**
	 * @param error the error to set
	 */
	public void setError(String error) {
		this.error = error;
	}
	/**
	 * @return the statusCode
	 */
	public String getStatusCode() {
		return statusCode;
	}
	/**
	 * @param statusCode the statusCode to set
	 */
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	/**
	 * @return the multiHit
	 */
	public MultiHit getMultiHit() {
		return multiHit;
	}
	/**
	 * @param multiHit the multiHit to set
	 */
	public void setMultiHit(MultiHit multiHit) {
		this.multiHit = multiHit;
	}
	/**
	 * @return the searchResults
	 */
	public SearchResults getSearchResults() {
		return searchResults;
	}
	/**
	 * @param searchResults the searchResults to set
	 */
	public void setSearchResults(SearchResults searchResults) {
		this.searchResults = searchResults;
	}
	
}
