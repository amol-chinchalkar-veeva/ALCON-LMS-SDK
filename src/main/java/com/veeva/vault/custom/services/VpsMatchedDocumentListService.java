/*
 * --------------------------------------------------------------------
 * UserDefinedService:	VpsMatchedDocumentList
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

import com.veeva.vault.custom.model.VpsMatchedDocument;
import com.veeva.vault.sdk.api.core.*;

import java.util.List;

@UserDefinedServiceInfo
public interface VpsMatchedDocumentListService extends UserDefinedService {

	public List<VpsMatchedDocument> getItems();
	public void load(String whereClause);
	public void loadFromDocumentVersion(String versionId);
	public void loadFromDocumentVersion(List<String> versionIds);
	public void loadFromEdlItemList(String edlItemtId, Integer minimumMajorVersion);
	public void loadFromEdlItemList(List<String> edlItemtIds, Integer minimumMajorVersion);
	public List<String> getErrorList();

}