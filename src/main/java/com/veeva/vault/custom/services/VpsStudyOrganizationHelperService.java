/*
 * --------------------------------------------------------------------
 * UserDefinedService:	VpsStudyOrganizationHelper
 * Author:				paulkwitkin @ Veeva
 * Date:				2021-02-20
 *---------------------------------------------------------------------
 * Description:
 *---------------------------------------------------------------------
 * Copyright (c) 2021 Veeva Systems Inc.  All Rights Reserved.
 *		This code is based on pre-existing content developed and
 * 		owned by Veeva Systems Inc. and may only be used in connection
 *		with the deliverable with which it was provided to Customer.
 *---------------------------------------------------------------------
 */
package com.veeva.vault.custom.services;

import com.veeva.vault.sdk.api.core.*;
import com.veeva.vault.sdk.api.data.Record;

import java.util.List;

@UserDefinedServiceInfo
public interface VpsStudyOrganizationHelperService extends UserDefinedService {
	public String getStudyOrgLevel(Record studyOrgRecord);
	public List<String> getOrganizationChildren(String orgId);
}