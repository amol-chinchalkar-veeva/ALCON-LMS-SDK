/*
 * --------------------------------------------------------------------
 * UserDefinedService:	VpsJobHelper
 * Author:				paulkwitkin @ Veeva
 * Date:				2020-10-02
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
import com.veeva.vault.sdk.api.core.VaultRuntimeException;
import com.veeva.vault.sdk.api.data.Record;

import java.util.List;

@UserDefinedServiceInfo
public interface VpsJobHelperService extends UserDefinedService {
	public void callUserActions(List<Record> recordList, String userActionName);
	public void callUserActionsInBatch(List<Record> recordList, String userActionName) throws VaultRuntimeException;
}
