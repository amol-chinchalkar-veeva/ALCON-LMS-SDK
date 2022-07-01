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
import com.veeva.vault.sdk.api.data.Record;
import com.veeva.vault.sdk.api.data.RecordService;
import com.veeva.vault.sdk.api.group.Group;
import com.veeva.vault.sdk.api.query.QueryResponse;
import com.veeva.vault.sdk.api.role.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.veeva.vault.custom.services.VpsVQLServiceImpl.*;

@UserDefinedServiceInfo
public class VpsRecordRoleHelperImpl implements VpsRecordRoleHelperService {
	public static final int MAX_BATCH_RECORDS = 500;
	private static final String PROCESS_ERROR = "ERROR";
	private static final String PROCESS_RACECONDITION = "RACE_CONDITION";
	private static final String PROCESS_SUCCESS = "SUCCESS";
	public static final int RETRY_ATTEMPTS_COUNT = 5;
	private static final String OBJ_FIELD_ID = "id";
	private static final String OBJECT_APPLICATION_ROLE = "application_role__v";
	private static final String OBJFIELD_API_NAME = "api_name__v";
	private static final String USER_GROUP_RELATIONSHIP_NAME = "user__sysr";
	private static final String OBJFIELD_ID = "id";
	private static final String OBJFIELD_NAME = "name__v";
	private static final String OBJFIELD_FIRST_NAME = "first_name__sys";
	private static final String OBJFIELD_LAST_NAME = "last_name__sys";
	private static final String OBJECT_USER = "user__sys";
	private static final String OBJFIELD_TEAM_USER = "team_user__v";
	private static final String OBJFIELD_TEAM_ENABLED_RECORD = "team_enabled_record__v";
	private static final String OBJFIELD_TEAM_APPLICATION_ROLE = "team_application_role__v";
	private static final String USER_QUERY = "select id from user__sys where id = '%s' limit 0";

	private RecordRoleService roleRoleService;
	private List<String> errorList = VaultCollections.newList();
	private LogService logService = ServiceLocator.locate(LogService.class);

	/**
	 * Internal method to get a record service. If a service has already been created
	 * it returns the existing service.
	 * @return RecordService
	 */
	public RecordRoleService getRecordRoleService() {
		//initialize the service on the first call
		if (roleRoleService == null) {
			roleRoleService = ServiceLocator.locate(RecordRoleService.class);
		}
		return roleRoleService;
	}

	public List<String> getErrorList() {
		return errorList;
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

	/**
	 * Stub for logging batch save/delete.
	 */
	public void logBatchMessage(String message, Integer attempt) throws VaultRuntimeException {
		getLogService().info("VpsRecordHelper.logBatchMessage - message {}, attempt {}", message, attempt);
		//add additional logging / notifications here.
	}

	/**
	 * Updates object records for a list of record roles
	 * @param recordList list of records to save
	 * @param errorMessage error message to display after error
	 */
	private void updateRoles(List<RecordRoleUpdate> recordList, String errorMessage) throws VaultRuntimeException {
		updateRoles(recordList, errorMessage);
	}

	/**
	 * Updates object records for a list of record roles
	 * @param recordList list of records to save
	 * @param errorMessage error message to display after error
	 * @param rollbackOnError when true, throw rollback exception on error (after race condition retries)
	 */
	private void updateRoles(List<RecordRoleUpdate> recordList, String errorMessage, Boolean rollbackOnError) throws VaultRuntimeException {
		getLogService().info("VpsRecordRoleHelper.updateRoles");
		try {
			if ((recordList != null) && (recordList.size() > 0)) {

				//this list is used to track the results of the batch process (success vs. error)
				//errors that are non-race conditions are marked as error
				//note: using a list because lambda expressions require final variables
				Set<String> results = VaultCollections.newSet();

				//this list is used to track the number of batch attempts
				//race condition errors are retried up to RETRY_ATTEMPTS_COUNT
				//note: using a list because lambda expressions require final variables
				List<String> batchAttempts = VaultCollections.newList();

				while ((batchAttempts.size() < RETRY_ATTEMPTS_COUNT)
						&& (!results.contains(PROCESS_ERROR))
						&& (!results.contains(PROCESS_SUCCESS))) {

					// process the changes in batch
					// if error occurs
					//		1. log the error
					//		2. if race condition, retry until max retries, then if rollbackOnError=true, throw rollback exception
					//		3. if non race condition and rollbackOnError=true, throw rollback exception
					// if success, mark as success
					getRecordRoleService().batchUpdateRecordRoles(recordList)
							.onErrors(batchOperationErrors -> {
								batchAttempts.add(PROCESS_ERROR);

								batchOperationErrors.stream().forEach(error -> {

									//formulate the error message
									String errorType = error.getError().getType();
									String errMsg = error.getError().getMessage();
									int errPosition = error.getInputPosition();
									String fullMessage = errorMessage + " because of " + errMsg;
									getErrorList().add(fullMessage);

									//handles race conditions for record level locking
									if (errorType.equals(PROCESS_RACECONDITION)) {
										results.add(PROCESS_RACECONDITION);
										logBatchMessage(fullMessage, batchAttempts.size());
										sleep();

										//if we reached the max number of retries and rollebackOnError=true
										//throw rollback exception
										if ((batchAttempts.size() == RETRY_ATTEMPTS_COUNT) && (rollbackOnError)) {
											throw new RollbackException("OPERATION_NOT_ALLOWED", errorMessage + PROCESS_RACECONDITION);
										}
									} else {
										results.add(PROCESS_ERROR);

										if (rollbackOnError) {
											throw new RollbackException("OPERATION_NOT_ALLOWED", fullMessage);
										}
									}
								});
							})
							.onSuccesses(batchOperationSuccess -> {
								batchAttempts.add(PROCESS_SUCCESS);
								results.add(PROCESS_SUCCESS);
							})
							.execute();
				}
			}
		}
		catch (VaultRuntimeException exception) {
			getLogService().error("VpsRecordRoleHelper.updateRoles - {}", exception.getMessage());
			throw exception;
		}
	}

	/**
	 * Updates object records for a list of record roles
	 * Max number of list entries is 10k. SDK will fail with larger lists and calling code is responsible for managing 10k limit
	 * @param recordList list of records to save
	 * @param errorMessage error message to display after error
	 */
	public void updateRolesInBatch(List<RecordRoleUpdate> recordList, String errorMessage) throws VaultRuntimeException {
		updateRolesInBatch(recordList, errorMessage, true);
	}

	/**
	 * Updates object records for a list of record roles
	 * Max number of list entries is 10k. SDK will fail with larger lists and calling code is responsible for managing 10k limit
	 * @param recordList list of records to save
	 * @param errorMessage error message to display after error
	 * @param rollbackOnError when true, throw rollback exception on error (after race condition retries)
	 */
	public void updateRolesInBatch(List<RecordRoleUpdate> recordList, String errorMessage, Boolean rollbackOnError) throws VaultRuntimeException {
		getLogService().info("VpsRecordRoleHelper.updateRolesInBatch");

		try {
			if ((recordList != null) && (recordList.size() > 0)) {
				//Update the record roles in batches
				List<RecordRoleUpdate> batchRecords = VaultCollections.newList();
				for (RecordRoleUpdate r : recordList) {
					if (batchRecords.size() == MAX_BATCH_RECORDS) {
						updateRoles(batchRecords, errorMessage, rollbackOnError);
						batchRecords.clear();
					}
					batchRecords.add(r);
				}
				if (batchRecords.size() > 0) {
					updateRoles(batchRecords, errorMessage, rollbackOnError);
				}
			}
		}
		catch (VaultRuntimeException exception) {
			getLogService().error("VpsRecordRoleHelper.updateRolesInBatch - {}", exception.getMessage());
			throw exception;
		}
	}


	/**
	 * Returns the Vault Id for an application role given the api name
	 * @param roleApiName The api name for the role
	 * @return The Vault Id for the role
	 */
	public String getApplicationRoleIdFromApiName(String roleApiName) {

		getLogService().info("VpsRecordRoleHelper.getApplicationRoleIdFromApiName");
		List<String> ids = VaultCollections.newList();

		try {

			VpsVQLService vqlService = ServiceLocator.locate(VpsVQLService.class);
			QueryResponse queryResponse = vqlService.query(
					SELECT, vqlService.appendFields(
							OBJ_FIELD_ID
					),
					FROM, OBJECT_APPLICATION_ROLE,
					WHERE, OBJFIELD_API_NAME + " = '" + roleApiName + "'"
			);


			queryResponse.streamResults().forEach(queryResult -> {
				ids.add(queryResult.getValue(OBJ_FIELD_ID, ValueType.STRING));

			});
		}
		catch (VaultRuntimeException exception) {
			getLogService().error("VpsRecordRoleHelper.getApplicationRoleIdFromApiName - {}", exception.getMessage());
			throw exception;
		}

		if (!ids.isEmpty())
			return ids.get(0);
		else
			return "";

	}

	public Map<String, List<String>> getRoleDetails(List<RecordRoleChange> roleChanges)
	{
		Map<String, List<String>> roleDetails = VaultCollections.newMap();

		for (RecordRoleChange curRoleChange : roleChanges) {
			List<String> users = curRoleChange.getUsersAdded();
			roleDetails.put(curRoleChange.getRole().getRoleName(), users);
		}

		return roleDetails;
	}

	public List<String> getRoleUsers(RecordRole role)
	{
		List<String> users = role.getUsers();
		List<Group> groups = role.getGroups();

		if (!groups.isEmpty())
		{

			VpsGroupHelperService groupHelper = ServiceLocator.locate(VpsGroupHelperService.class);

			for (Group group : groups) {
				List<String> attrs = VaultCollections.newList();
				attrs.add(USER_GROUP_RELATIONSHIP_NAME + "." + OBJFIELD_ID);
				attrs.add(USER_GROUP_RELATIONSHIP_NAME + "." + OBJFIELD_NAME);
				List<Map<String, String>> usersFromGroup = groupHelper.getGroupMembers(group.getId(), attrs);
				for (Map<String, String> userInGroup : usersFromGroup) {
					String userId = userInGroup.get(USER_GROUP_RELATIONSHIP_NAME + "." + OBJFIELD_ID);
					users.add(userId);
				}
			}
		}

		return users;

	}

	public RecordRole getRecordRole(String objectName, String recordId, String roleName)
	{
		RecordRoleService recordRoleService = getRecordRoleService();
		RecordService recordService = ServiceLocator.locate(RecordService.class);

		Record record = recordService.newRecordWithId(objectName, recordId);
		List<Record> recordList = VaultCollections.newList();
		recordList.add(record);

		RecordRole role = null;
		GetRecordRolesResponse recordRolesResponse = recordRoleService.getRecordRoles(recordList, roleName);
		role = recordRolesResponse.getRecordRole(record, roleName);

		return role;

	}

	public List<String> getUsersForTeamRole(String teamObjectName, String recordId, String roleId)
	{
		VpsVQLService vqlService = ServiceLocator.locate(VpsVQLService.class);
		List<String> userIds = VaultCollections.newList();

		QueryResponse queryResponse = vqlService.query(
				SELECT, vqlService.appendFields(
						OBJFIELD_TEAM_USER
				),
				FROM, teamObjectName,
				WHERE, OBJFIELD_TEAM_ENABLED_RECORD + " = '" + recordId + "' AND " + OBJFIELD_TEAM_APPLICATION_ROLE + " ='" + roleId + "'"
		);

		queryResponse.streamResults().forEach(queryResult -> {
			String userId = queryResult.getValue(OBJFIELD_TEAM_USER, ValueType.STRING);
			userIds.add(userId);
		});

		return userIds;

	}

	public Map<String, String> getIdsForApplicationRoles(List<String> applicationRoles)
	{
		Map<String, String> appIdsMap = VaultCollections.newMap();
		VpsVQLService vqlService = ServiceLocator.locate(VpsVQLService.class);

		//TODO THIS IS USING A LIST.  HOW TO PROCESS THAT?
		QueryResponse queryResponse = vqlService.query(
				SELECT, vqlService.appendFields(
						OBJ_FIELD_ID, OBJFIELD_API_NAME
				),
				FROM, OBJECT_APPLICATION_ROLE,
				WHERE, OBJFIELD_API_NAME + " CONTAINS (" + applicationRoles + ")"
		);

		queryResponse.streamResults().forEach(queryResult -> {
			String apiName = queryResult.getValue(OBJFIELD_API_NAME, ValueType.STRING);
			String roleId = queryResult.getValue(OBJ_FIELD_ID, ValueType.STRING);
			appIdsMap.put(apiName, roleId);
		});

		return appIdsMap;
	}


	public Map<String, String> getUserNamesForIds(Set<String> users)
	{
		Map<String, String> userNames = VaultCollections.newMap();

		VpsVQLService vqlService = ServiceLocator.locate(VpsVQLService.class);

		if (!users.isEmpty())
		{
			//Need to get the user's names
			//select first_name, last_name, from users where id contains (values in my list).

			//TODO THIS IS USING A LIST.  HOW TO PROCESS THAT?
			QueryResponse queryResponse = vqlService.query(
					SELECT, vqlService.appendFields(
							OBJFIELD_FIRST_NAME, OBJFIELD_LAST_NAME, OBJ_FIELD_ID
					),
					FROM, OBJECT_USER,
					WHERE, OBJFIELD_ID + " CONTAINS (" + users + ")"
			);

			queryResponse.streamResults().forEach(queryResult -> {

				String firstName = queryResult.getValue(OBJFIELD_FIRST_NAME, ValueType.STRING);
				String lastName = queryResult.getValue(OBJFIELD_LAST_NAME, ValueType.STRING);
				String userId = queryResult.getValue(OBJ_FIELD_ID, ValueType.STRING);
				userNames.put(userId, firstName + " " + lastName);
			});
		}

		return userNames;

	}

	/**
	 * Method to simulate a delay. Queries the current user.
	 */
	protected void sleep() {
		try {

			RuntimeService runtimeService = ServiceLocator.locate(RuntimeService.class);
			runtimeService.sleep(1000);
		}
		catch (VaultRuntimeException exception) {
			getLogService().error("VpsRecordHelper.sleep - {}", exception.getMessage());
		}
	}
}