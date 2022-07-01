/*
 * --------------------------------------------------------------------
 * UserDefinedService:	VQLService
 * Author:				markarnold @ Veeva
 * Date:				2020-08-28
 *---------------------------------------------------------------------
 * Description:
 *---------------------------------------------------------------------
 * Copyright (c) 2020 Veeva Systems Inc.  All Rights Reserved.
 *		This code is based on pre-existing content developed and
 * 		owned by Veeva Systems Inc. and may only be used in connection
 *		with the deliverable with which it was provided to Customer.
 *---------------------------------------------------------------------
 */
package com.veeva.vault.custom.services;

import com.veeva.vault.sdk.api.core.UserDefinedService;
import com.veeva.vault.sdk.api.core.UserDefinedServiceInfo;
import com.veeva.vault.sdk.api.query.QueryResponse;

@UserDefinedServiceInfo
public interface VpsVQLService extends UserDefinedService {
	String appendFields(String...fields);
	String appendIfTrue(boolean assertion, Object...query);
	String asLongText(String field);
	StringBuilder getQueryAsBuilder(Object... query);
	boolean hasDuplicates(String fieldName, Object... query);
	boolean hasRecords(Object... query);
	QueryResponse query(Object... query);
}