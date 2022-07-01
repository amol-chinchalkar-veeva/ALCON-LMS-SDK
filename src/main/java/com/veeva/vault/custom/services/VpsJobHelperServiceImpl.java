package com.veeva.vault.custom.services;

import com.veeva.vault.sdk.api.core.*;
import com.veeva.vault.sdk.api.data.Record;
import com.veeva.vault.sdk.api.job.JobParameters;
import com.veeva.vault.sdk.api.job.JobService;

import java.util.List;

@UserDefinedServiceInfo
public class VpsJobHelperServiceImpl implements VpsJobHelperService {
	private static final String PARAM_RECORD_USER_ACTION = "record_user_action__v";
	private static final String PARAM_RECORDS = "records";
	private static final String PARAM_USER_ACTION_NAME = "user_action_name";
	public static final int MAX_BATCH_RECORDS = 500;

	/**
	 * Executes object record user actions for a list of records
	 * @param recordList
	 * @param userActionName
	 */
	public void callUserActions(List<Record> recordList, String userActionName) {
		try {
			JobService jobService = ServiceLocator.locate(JobService.class);

			if (recordList != null) {
				if (recordList.size() > 0) {
					JobParameters jobParameters = jobService.newJobParameters(PARAM_RECORD_USER_ACTION);
					jobParameters.setValue(PARAM_USER_ACTION_NAME, userActionName);
					jobParameters.setValue(PARAM_RECORDS, recordList);
					jobService.run(jobParameters);
				}
			}
		}
		catch (VaultRuntimeException exception) {
			throw exception;
		}
	}

	/**
	 * Executes object record user actions for a list of records in batches
	 * @param recordList
	 * @param userActionName
	 */
	public void callUserActionsInBatch(List<Record> recordList, String userActionName) throws VaultRuntimeException {
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
			throw exception;
		}
	}
}
