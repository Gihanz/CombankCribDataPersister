package com.gs.response;

public class SearchResultItem {
	
	public String name;
	public String bureauId;
	public String identifierSource;
	public String identifierValue;
	public String identifierMatched;
	public String surrogate;
	public String surrogateMatched;
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return the bureauId
	 */
	public String getBureauId() {
		return bureauId;
	}
	/**
	 * @param bureauId the bureauId to set
	 */
	public void setBureauId(String bureauId) {
		this.bureauId = bureauId;
	}
	/**
	 * @return the identifierSource
	 */
	public String getIdentifierSource() {
		return identifierSource;
	}
	/**
	 * @param identifierSource the identifierSource to set
	 */
	public void setIdentifierSource(String identifierSource) {
		this.identifierSource = identifierSource;
	}
	/**
	 * @return the identifierValue
	 */
	public String getIdentifierValue() {
		return identifierValue;
	}
	/**
	 * @param identifierValue the identifierValue to set
	 */
	public void setIdentifierValue(String identifierValue) {
		this.identifierValue = identifierValue;
	}
	/**
	 * @return the identifierMatched
	 */
	public String isIdentifierMatched() {
		return identifierMatched;
	}
	/**
	 * @param identifierMatched the identifierMatched to set
	 */
	public void setIdentifierMatched(String identifierMatched) {
		this.identifierMatched = identifierMatched;
	}
	/**
	 * @return the surrogate
	 */
	public String getSurrogate() {
		return surrogate;
	}
	/**
	 * @param surrogate the surrogate to set
	 */
	public void setSurrogate(String surrugate) {
		this.surrogate = surrugate;
	}
	/**
	 * @return the surrogateMatched
	 */
	public String isSurrogateMatched() {
		return surrogateMatched;
	}
	/**
	 * @param surrogateMatched the surrogateMatched to set
	 */
	public void setSurrogateMatched(String surrogateMatched) {
		this.surrogateMatched = surrogateMatched;
	}

}
