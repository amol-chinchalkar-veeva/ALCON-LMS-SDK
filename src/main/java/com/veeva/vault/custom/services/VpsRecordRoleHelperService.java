/*
 * --------------------------------------------------------------------
 * UserDefinedService:	VpsRecordRoleHelper
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

import com.veeva.vault.sdk.api.core.*;
import com.veeva.vault.sdk.api.role.RecordRole;
import com.veeva.vault.sdk.api.role.RecordRoleChange;
import com.veeva.vault.sdk.api.role.RecordRoleService;
import com.veeva.vault.sdk.api.role.RecordRoleUpdate;

import java.util.List;
import java.util.Map;
import java.util.Set;

@UserDefinedServiceInfo
public interface VpsRecordRoleHelperService extends UserDefinedService {

	public RecordRoleService getRecordRoleService();
	public void logBatchMessage(String message, Integer attempt);
	public void updateRolesInBatch(List<RecordRoleUpdate> recordList, String errorMessage) throws VaultRuntimeException;
	public void updateRolesInBatch(List<RecordRoleUpdate> recordList, String errorMessage, Boolean rollbackOnError) throws VaultRuntimeException;
	public String getApplicationRoleIdFromApiName(String roleApiName);
	public Map<String, List<String>> getRoleDetails(List<RecordRoleChange> roleChanges);
	public List<String> getRoleUsers(RecordRole role);
	public RecordRole getRecordRole(String objectName, String recordId, String roleName);
	public List<String> getUsersForTeamRole(String teamObjectName, String recordId, String roleId);
	public Map<String, String> getIdsForApplicationRoles(List<String> applicationRoles);
	public Map<String, String> getUserNamesForIds(Set<String> users);

}