/*
 * --------------------------------------------------------------------
 * UDC:         VpsAPIClient
 * Author:      markarnold @ Veeva
 * Date:        2019-07-25
 * --------------------------------------------------------------------
 * Description: Helper for running vql queries via api
 * --------------------------------------------------------------------
 * Copyright (c) 2019 Veeva Systems Inc.  All Rights Reserved.
 * This code is based on pre-existing content developed and
 * owned by Veeva Systems Inc. and may only be used in connection
 * with the deliverable with which it was provided to Customer.
 * --------------------------------------------------------------------
 */
package com.veeva.vault.custom.model;

import com.veeva.vault.custom.services.VpsUtilHelperService;
import com.veeva.vault.sdk.api.core.LogService;
import com.veeva.vault.sdk.api.core.ServiceLocator;
import com.veeva.vault.sdk.api.core.UserDefinedClassInfo;

import java.util.List;
import java.util.Set;

@UserDefinedClassInfo
public class VpsVQLRequest {

	private StringBuilder vql;
	private LogService logService = ServiceLocator.locate(LogService.class);

	/**
	 * Class to assist in making using Vault Query Language
	 */
	public VpsVQLRequest() {
		super();
		vql = new StringBuilder();
	}

	/**
	 * Adds a list of values to the VQL query
	 *
	 * @param valueList values to add
	 * @param delimiter delimiter between values (usually a comma)
	 * @param addQuotes adds quotes to values (object ids require quotes; doc ids do not)
	 */
	public void appendList(List<String> valueList, String delimiter, Boolean addQuotes) {
		if (valueList != null) {
			appendVQL(VpsUtilHelperService.listToString(valueList, delimiter, addQuotes));
		}
	}

	/**
	 * Adds a set of values to the VQL query
	 *
	 * @param valueSet values to add
	 * @param delimiter delimiter between values (usually a comma)
	 * @param addQuotes adds quotes to values (object ids require quotes; doc ids do not)
	 */
	public void appendSet(Set<String> valueSet, String delimiter, Boolean addQuotes) {
		if (valueSet != null) {
			appendVQL(VpsUtilHelperService.setToString(valueSet,delimiter,addQuotes));
		}
	}

	/**
	 * Adds input string to the VQL query
	 *
	 * @param queryText value to add
	 */
	public void appendVQL(String queryText) {
		vql.append(queryText);
	}

	/**
	 * clears the current VQL query
	 */
	public void clearVQL() {
		vql.setLength(0);
	}

	/**
	 * @return current vql query
	 */
	public String getVQL() {
		return vql.toString();
	}

	/**
	 * logs the current query
	 */
	public void logVQL() {
		getLogService().info(vql.toString());
	}

	/**
	 * Internal method to get a log service. If a service has already been created
	 * it returns the existing service.
	 * @return LogService
	 */
	protected LogService getLogService() {
		//initialize the service on the first call
		if (logService == null) {
			logService = ServiceLocator.locate(LogService.class);
		}
		return logService;
	}
}