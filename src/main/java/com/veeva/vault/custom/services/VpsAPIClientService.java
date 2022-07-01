/*
 * --------------------------------------------------------------------
 * UserDefinedService:	VpsAPIClient
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

import com.veeva.vault.custom.model.VpsVQLRequest;
import com.veeva.vault.custom.model.VpsVQLResponse;
import com.veeva.vault.sdk.api.core.*;
import com.veeva.vault.sdk.api.data.Record;
import com.veeva.vault.sdk.api.integration.IntegrationRule;

import java.util.List;
import java.util.Map;
import java.util.Set;

@UserDefinedServiceInfo
public interface VpsAPIClientService extends UserDefinedService {

	public Boolean createBinderFromTemplate(String templateName, Map<String, String> documentMetadata, String apiVersion, String apiConnection);
	public Boolean createDocumentFromTemplate(String templateName, Map<String, String> documentMetadata, String apiVersion, String apiConnection);
	public Map<String,String> getDocumentLifecycleActions(String docId,
														  String majorVersion,
														  String minorVersion, String apiVersion, String apiConnection);
	public List<String> getDocumentUsersAndGroupsFromRole(String docId, String roleApiName, String apiVersion, String apiConnection);
	public Boolean initiateObjectAction(String objectName,
										String userActionName,
										List<String> idList, String apiVersion, String apiConnection);
	public Boolean initiateObjectAction(String objectName,
										String userActionName,
										List<String> idList,
										Boolean rollbackOnError, String apiVersion, String apiConnection);
	public Boolean initiateObjectAction(String objectName,
										String userActionName,
										String objectId, String apiVersion, String apiConnection);
	public Boolean initiateObjectAction(String objectName,
										String userActionName,
										String objectId,
										Boolean rollbackOnError, String apiVersion, String apiConnection);
	public VpsVQLResponse runVQL(VpsVQLRequest vpsVQLRequest, String apiVersion, String apiConnection);
	public Boolean startDocumentWorkflow(String docId,
										 String majorVersion,
										 String minorVersion,
										 String lifecycleActionName,
										 String roleName,
										 Set<String> users,
										 Set<String> groups, String apiVersion, String apiConnection);
	public IntegrationRule getIntegrationRule(String sessionId, String integrationRuleName);
	public List<Record> v2vHttpQueryIntRules(String sourceVault, String targetObjectName, String sourceIdField, String targetIdField, Set<String> ids, IntegrationRule integrationRule, String apiVersion, String apiConnection);
	public Boolean updateObject(String objectName, String jsonBody, String apiVersion, String apiConnection);
}