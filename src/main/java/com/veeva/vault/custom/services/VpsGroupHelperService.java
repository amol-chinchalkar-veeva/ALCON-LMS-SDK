/*
 * --------------------------------------------------------------------
 * UserDefinedService:	VpsGroupHelper
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

import java.util.List;
import java.util.Map;

@UserDefinedServiceInfo
public interface VpsGroupHelperService extends UserDefinedService {
	public List<Map<String, String>> getGroupMembers(String groupId, List<String> userAttributes);
}
