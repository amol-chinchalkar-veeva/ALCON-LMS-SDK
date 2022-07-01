/*
 * --------------------------------------------------------------------
 * Job Info:	VpsCreateExternalFTRTraining
 * Author:			Amol Chinchalkar @ Veeva
 * Date:			2022-06-30
 *---------------------------------------------------------------------
 * Description:	Create external training FTR
 *---------------------------------------------------------------------
 * Copyright (c) 2022 Veeva Systems Inc.  All Rights Reserved.
 *		This code is based on pre-existing content developed and
 *		owned by Veeva Systems Inc. and may only be used in connection
 *		with the deliverable with which it was provided to Customer.
 *---------------------------------------------------------------------
 */
package com.veeva.vault.custom.jobs;


import com.veeva.vault.custom.model.VpsExternalTrainingQueueItem;
import com.veeva.vault.custom.services.VpsExternalFTRTrainingHelperService;
import com.veeva.vault.sdk.api.core.ServiceLocator;
import com.veeva.vault.sdk.api.core.UserDefinedModelService;
import com.veeva.vault.sdk.api.core.VaultCollections;
import com.veeva.vault.sdk.api.job.*;

import java.util.List;

@JobInfo(adminConfigurable = true, idempotent = true, isVisible = true)


public class VpsCreateExternalFTRTrainingJob implements Job {

    private static final String AD_HOC_OBJFIELD_COMMENTS = "comments__c";
    private static final String AD_HOC_OBJFIELD_COMPLETION_DATE = "completion_date__c";
    private static final String AD_HOC_OBJFIELD_CREATED_BY = "created_by__v";
    private static final String AD_HOC_OBJFIELD_DATE_OF_TRAINING = "date_of_training__c";
    private static final String AD_HOC_OBJFIELD_DESCRIPTION = "description__c";
    private static final String AD_HOC_OBJFIELD_ID = "id";
    private static final String AD_HOC_OBJFIELD_INSTRUCTOR = "instructor__c";
    private static final String AD_HOC_OBJFIELD_NAME = "name__v";
    private static final String AD_HOC_OBJFIELD_OBJECT_TYPE = "object_type__v";
    private static final String AD_HOC_OBJFIELD_PERSON = "person__c";
    private static final String AD_HOC_OBJFIELD_REASON_FOR_COMPLETION = "reason_for_completion__c";
    private static final String AD_HOC_OBJFIELD_STATUS = "status__v";
    private static final String AD_HOC_OBJFIELD_TITLE = "title__c";
    private static final String AD_HOC_OBJFIELD_TRAINING_ASSIGNMENT_ID = "training_assignment__c";
    private static final String AD_HOC_OBJFIELD_TRAINING_METHOD = "training_method__c";
    private static final String AD_HOC_OBJ_AD_HOC_TRAINING = "ad_hoc_training__c";
    private static final String TA_OBJFIELD_ID = "id";
    private static final String TA_OBJFIELD_LEARNER = "learner__v";
    private static final String TA_OBJFIELD_TRAINING_REQUIREMENT_ID = "training_assignment__cr.training_requirement__v";
    static final String TA_OBJFIELD_COMMENTS = "ad_hoc_training__cr.comments__c";
    private static final String AD_HOC_OBJ_TRAINING_REQUIREMENT = "training_requirement__v";

    @Override
    public JobInputSupplier init(JobInitContext jobInitContext) {
        JobLogger jobLogger = jobInitContext.getJobLogger();
        jobLogger.log("***** Start VpsCreateExternalFTRTrainingJob init***");
        VpsExternalFTRTrainingHelperService vpsExternalFTRTrainingHelperService = ServiceLocator.locate(VpsExternalFTRTrainingHelperService.class);
        List<VpsExternalTrainingQueueItem> externalTrainingQueueItems = vpsExternalFTRTrainingHelperService.getExternalTrainingQueueItems();
        List<JobItem> jobItems = VaultCollections.newList();

        for (VpsExternalTrainingQueueItem externalTrainingQueueItem : externalTrainingQueueItems) {
            JobItem jobItem = jobInitContext.newJobItem();
            if(externalTrainingQueueItem.getReasonForCompletion()!=null){
            jobItem.setValue(AD_HOC_OBJFIELD_REASON_FOR_COMPLETION, externalTrainingQueueItem.getReasonForCompletion());
            }else{
                jobItem.setValue(AD_HOC_OBJFIELD_REASON_FOR_COMPLETION,"");
            }
            if(externalTrainingQueueItem.getCompletionDate()!=null){
            jobItem.setValue(AD_HOC_OBJFIELD_COMPLETION_DATE, externalTrainingQueueItem.getCompletionDate());
            }else {
                jobItem.setValue(AD_HOC_OBJFIELD_COMPLETION_DATE, "");
            }
            jobItem.setValue(AD_HOC_OBJ_TRAINING_REQUIREMENT, externalTrainingQueueItem.getTrainingRequirementID());
            jobItem.setValue(AD_HOC_OBJFIELD_COMMENTS, externalTrainingQueueItem.getComments());
            jobItems.add(jobItem);
        }

        return jobInitContext.newJobInput(jobItems);
    }

    @Override
    public void process(JobProcessContext con) {
        JobLogger jobLogger = con.getJobLogger();
        VpsExternalFTRTrainingHelperService vpsExternalFTRTrainingHelperService = ServiceLocator.locate(VpsExternalFTRTrainingHelperService.class);
        UserDefinedModelService modelService = ServiceLocator.locate(UserDefinedModelService.class);

        jobLogger.log("****** Start VpsCreateExternalFTRTrainingJob process *******");
        List<JobItem> jobItems = con.getCurrentTask().getItems();
        TaskOutput taskOutput = con.getCurrentTask().getTaskOutput();
        for (JobItem jobItem : jobItems) {
            VpsExternalTrainingQueueItem vpsExternalFTRQueueItem = modelService.newUserDefinedModel(VpsExternalTrainingQueueItem.class);
            String reasonForCompletion = jobItem.getValue(AD_HOC_OBJFIELD_REASON_FOR_COMPLETION, JobValueType.STRING);
            vpsExternalFTRQueueItem.setReasonForCompletion(reasonForCompletion);
            String completionDate = jobItem.getValue(AD_HOC_OBJFIELD_COMPLETION_DATE, JobValueType.STRING);
            vpsExternalFTRQueueItem.setCompletionDate(completionDate);
            String trainingRequirementID = jobItem.getValue(AD_HOC_OBJ_TRAINING_REQUIREMENT, JobValueType.STRING);
            vpsExternalFTRQueueItem.setTrainingRequirementID(trainingRequirementID);
            String comments = jobItem.getValue(AD_HOC_OBJFIELD_COMMENTS, JobValueType.STRING);
            vpsExternalFTRQueueItem.setComments(comments);
//            String id = jobItem.getValue(OBJFIELD_ID, JobValueType.STRING);
//            vpsDataRollupQueueItem.setId(id);
            vpsExternalFTRTrainingHelperService.createFTRFromQueueItems(vpsExternalFTRQueueItem);
        }

        taskOutput.setState(TaskState.SUCCESS);
        jobLogger.log("****** End VpsDataQueueItemProcessorJob process *******");

    }

    @Override
    public void completeWithSuccess(JobCompletionContext con) {

    }

    @Override
    public void completeWithError(JobCompletionContext con) {

    }


}