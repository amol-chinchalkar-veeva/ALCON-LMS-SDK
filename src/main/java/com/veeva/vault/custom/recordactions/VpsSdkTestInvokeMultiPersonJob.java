/*
 * --------------------------------------------------------------------
 * RecordAction:	VpsSdkTestInvokeFtrJob
 * Object:			ad_hoc_training__c
 * Author:			Amol Chinchalkar @ Veeva
 * Date:			2022-07-01
 *---------------------------------------------------------------------
 * Description:
 *---------------------------------------------------------------------
 * Copyright (c) 2022 Veeva Systems Inc.  All Rights Reserved.
 *      This code is based on pre-existing content developed and
 *      owned by Veeva Systems Inc. and may only be used in connection
 *      with the deliverable with which it was provided to Customer.
 *---------------------------------------------------------------------
 */
package com.veeva.vault.custom.recordactions;

import com.veeva.vault.sdk.api.action.RecordAction;
import com.veeva.vault.sdk.api.action.RecordActionContext;
import com.veeva.vault.sdk.api.action.RecordActionInfo;
import com.veeva.vault.sdk.api.action.Usage;
import com.veeva.vault.sdk.api.core.ServiceLocator;
import com.veeva.vault.sdk.api.job.JobParameters;
import com.veeva.vault.sdk.api.job.JobRunResult;
import com.veeva.vault.sdk.api.job.JobService;

@RecordActionInfo(object = "ad_hoc_training__c",
        label = "SDK Test Multi Person",
        usages = Usage.LIFECYCLE_ENTRY_ACTION)
public class VpsSdkTestInvokeMultiPersonJob implements RecordAction {

    public boolean isExecutable(RecordActionContext context) {
        return true;
    }

    public void execute(RecordActionContext context) {
        JobService jobService = ServiceLocator.locate(JobService.class);
        JobParameters jobParameters = jobService.newJobParameters("vps_process_multiple_training_completion__c");
        JobRunResult result = jobService.runJob(jobParameters);
        result.getJobId();
    }
}