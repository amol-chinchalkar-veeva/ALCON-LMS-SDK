/*
 * --------------------------------------------------------------------
 * UDC:         VpsRecordRoleHelper
 * Author:      markarnold @ Veeva
 * Date:        2019-07-25
 * --------------------------------------------------------------------
 * Description: Helper for saving record roles
 * --------------------------------------------------------------------
 * Copyright (c) 2019 Veeva Systems Inc.  All Rights Reserved.
 * This code is based on pre-existing content developed and
 * owned by Veeva Systems Inc. and may only be used in connection
 * with the deliverable with which it was provided to Customer.
 * --------------------------------------------------------------------
 */
package com.veeva.vault.custom.classes;

import com.veeva.vault.sdk.api.core.*;
import com.veeva.vault.sdk.api.role.RecordRoleService;
import com.veeva.vault.sdk.api.role.RecordRoleUpdate;

import java.util.List;
import java.util.Set;

@UserDefinedClassInfo
public class VpsRecordRoleHelper extends VpsBaseHelper {

	public static final int MAX_BATCH_RECORDS = 500;
	private static final String PROCESS_ERROR = "ERROR";
	private static final String PROCESS_RACECONDITION = "RACE_CONDITION";
	private static final String PROCESS_SUCCESS = "SUCCESS";
	public static final int RETRY_ATTEMPTS_COUNT = 5;

	private RecordRoleService roleRoleService;

	/**
	 * Class to assist in creating, updating, and deleting record roles
	 */
	public VpsRecordRoleHelper() {
		super();
	}

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
		updateRolesInBatch(recordList, errorMessage);
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
}