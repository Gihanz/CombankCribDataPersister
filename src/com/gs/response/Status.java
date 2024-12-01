package com.gs.response;

public enum Status implements StatusCodeConstant {
	
	SINGLE_HIT(CODE_200, "Single Hit response received"),
	MULTI_HIT(CODE_201, "Multi Hit response received"),
	NO_HIT(CODE_202, "No Hit response received"),
	ERROR_HIT(CODE_400, "Error response received"),
	API_ERROR(CODE_401, "API response error"),
	APPLICATION_ERROR(CODE_402, "Application level error");
	
	private final String statusCode;
	private final String status;
	
	Status(String statusCode, String status) {
		this.statusCode = statusCode;
		this.status = status;
	}

	public String getStatusCode() {
		return this.statusCode;
	}

	public String getStatus() {
		return this.status;
	}

}
