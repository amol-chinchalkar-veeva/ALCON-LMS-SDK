/*
 * --------------------------------------------------------------------
 * UDC:         VpsJobHelper
 * Author:      markarnold @ Veeva
 * Date:        2019-07-25
 * --------------------------------------------------------------------
 * Description: Helper for running jobs
 * --------------------------------------------------------------------
 * Copyright (c) 2019 Veeva Systems Inc.  All Rights Reserved.
 * This code is based on pre-existing content developed and
 * owned by Veeva Systems Inc. and may only be used in connection
 * with the deliverable with which it was provided to Customer.
 * --------------------------------------------------------------------
 */
package com.veeva.vault.custom.classes;

import com.veeva.vault.sdk.api.core.ServiceLocator;
import com.veeva.vault.sdk.api.core.UserDefinedClassInfo;
import com.veeva.vault.sdk.api.core.VaultCollections;
import com.veeva.vault.sdk.api.core.VaultRuntimeException;
import com.veeva.vault.sdk.api.data.Record;
import com.veeva.vault.sdk.api.job.JobParameters;
import com.veeva.vault.sdk.api.job.JobService;

import java.util.List;

@UserDefinedClassInfo
public class VpsJobHelper extends VpsBaseHelper {

	private static final String PARAM_RECORD_USER_ACTION = "record_user_action__v";
	private static final String PARAM_RECORDS = "records";
	private static final String PARAM_USER_ACTION_NAME = "user_action_name";
	public static final int MAX_BATCH_RECORDS = 500;

	private JobService jobService;

	/**
	 * Class to assist in using the JobService
	 */
	public VpsJobHelper() {
		super();
	}

	/**
	 * Executes object record user actions for a list of records
	 * @param recordList
	 * @param userActionName
	 */
	public void callUserActions(List<Record> recordList, String userActionName) {
		getLogService().info("VpsJobHelper.callUserActions userActionName {}", userActionName);

		try {
			if (recordList != null) {
				if (recordList.size() > 0) {
					JobParameters jobParameters = getJobService().newJobParameters(PARAM_RECORD_USER_ACTION);
					jobParameters.setValue(PARAM_USER_ACTION_NAME, userActionName);
					jobParameters.setValue(PARAM_RECORDS, recordList);
					getJobService().run(jobParameters);
				}
			}
		}
		catch (VaultRuntimeException exception) {
			getLogService().error("VpsJobHelper.callUserActions - {}", exception.getMessage());
			throw exception;
		}
	}

	/**
	 * Executes object record user actions for a list of records in batches
	 * @param recordList
	 * @param userActionName
	 */
	public void callUserActionsInBatch(List<Record> recordList, String userActionName) throws VaultRuntimeException {
		getLogService().info("VpsJobHelper.callUserActionsInBatch userActionName {}", userActionName);

		try {
			if (recordList != null) {
				if (recordList.size() > 0) {
					//now save the part records in batches
					List<Record> batchRecords = VaultCollections.newList();
					for (Record r : recordList) {
						if (batchRecords.size() == MAX_BATCH_RECORDS) {
							callUserActions(batchRecords, userActionName);
							batchRecords.clear();
						}
						batchRecords.add(r);
					}
					if (batchRecords.size() > 0) {
						callUserActions(batchRecords, userActionName);
					}
				}
			}
		}
		catch (VaultRuntimeException exception) {
			getLogService().error("VpsJobHelper.callUserActionsInBatch - {}", exception.getMessage());
			throw exception;
		}
	}

	/**
	 * Internal method to get a record service. If a service has already been created
	 * it returns the existing service.
	 * @return JobService
	 */
	public JobService getJobService() {
		//initialize the service on the first call
		if (jobService == null) {
			jobService = ServiceLocator.locate(JobService.class);
		}
		return jobService;
	}
}