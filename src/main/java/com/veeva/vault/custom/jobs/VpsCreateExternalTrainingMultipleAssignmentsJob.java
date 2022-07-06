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


import com.veeva.vault.custom.classes.VpsVQLHelper;
import com.veeva.vault.custom.model.VpsExternalTrainingModel;
import com.veeva.vault.custom.services.VpsAPIClientService;
import com.veeva.vault.custom.services.VpsExternalTrainingHelperService;
import com.veeva.vault.custom.services.VpsJobHelperService;
import com.veeva.vault.sdk.api.core.*;
import com.veeva.vault.sdk.api.data.Record;
import com.veeva.vault.sdk.api.data.RecordService;
import com.veeva.vault.sdk.api.job.*;
import com.veeva.vault.sdk.api.query.QueryResponse;

import java.util.List;
import java.util.Map;

@JobInfo(adminConfigurable = true, idempotent = true, isVisible = true)


public class VpsCreateExternalTrainingMultipleAssignmentsJob implements Job {

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
    private static final String FACILITATED_RECORD_COMPLETE_USER_ACTION = "change_state_to_requested_useraction__c";
    private static final String ADHOC_RECORD_INPROCESS_USER_ACTION = "change_state_to_inprocess_useraction__c";

    @Override
    public JobInputSupplier init(JobInitContext jobInitContext) {
        JobLogger jobLogger = jobInitContext.getJobLogger();
        jobLogger.log("***** Start VpsCreateExternalFTRTrainingJob init***");
        List<JobItem> jobItems = VaultCollections.newList();

        // Get ID's of all documents need a sharing setting updates
        VpsVQLHelper vqlHelper = new VpsVQLHelper();
        vqlHelper.clearVQL();
//        vqlHelper.appendVQL("select  object_type__v,  comments__c,  completion_date__c,  date_of_training__c, " +
//                "description__c, id,instructor__c,  state__v,  name__v,  person__c,  " +
//                "reason_for_completion__c,  status__v,  title__c  from ad_hoc_training__c " +
//                "where state__v='active_state__c'");
        //vqlHelper.appendVQL("select  id from ad_hoc_training__c where state__v='processed_state__c' and name__v='VV-000002'");
        vqlHelper.appendVQL("select  id from ad_hoc_training__c where state__v='initial_state__c'");

        jobLogger.log("Running document VQL query: " + vqlHelper.getVQL());
        QueryResponse queryResponse = vqlHelper.runVQL();

        queryResponse.streamResults().forEach(queryResult -> {
            JobItem jobItem = jobInitContext.newJobItem();
            String recID = queryResult.getValue("id", ValueType.STRING);
            jobItem.setValue("id", recID);
            jobItems.add(jobItem);
        });
        return jobInitContext.newJobInput(jobItems);

    }

    @Override
    public void process(JobProcessContext con) {

        JobLogger jobLogger = con.getJobLogger();
        VpsExternalTrainingHelperService vpsExternalTrainingHelperService = ServiceLocator.locate(VpsExternalTrainingHelperService.class);
        RecordService recordService = ServiceLocator.locate(RecordService.class);
        jobLogger.log("****** Start VpsCreateExternalFTRTrainingJob process *******");
        List<JobItem> jobItems = con.getCurrentTask().getItems();
        TaskOutput taskOutput = con.getCurrentTask().getTaskOutput();
        Map<List<String>, List<String>> facilitatedTrainingIDsAsMap = VaultCollections.newMap();
        List<String> facilitatedTrainAndTrainAssignment = VaultCollections.newList();
        RuntimeService runtimeService =ServiceLocator.locate(RuntimeService.class);
      //  List<Record> recordToPerformUserAction = VaultCollections.newList();
        VpsJobHelperService jobHelperService = ServiceLocator.locate(VpsJobHelperService.class);

        for (JobItem jobItem : jobItems) {
            String recID = jobItem.getValue("id", JobValueType.STRING);
            jobLogger.log("Start processing external training ID:" + recID);
            Map<String, List<VpsExternalTrainingModel>> externalTrainingAssignmentsMap = vpsExternalTrainingHelperService.getExternalTrainingAssignmentModels(recID);
            if (!isCollectionMapNullOrEmpty(externalTrainingAssignmentsMap)) {
                jobLogger.log("Create Facilitated Training Request");
                facilitatedTrainingIDsAsMap = vpsExternalTrainingHelperService.createFacilitatedTrainingForAssignments(externalTrainingAssignmentsMap);
                if (!isCollectionMapNullOrEmpty(facilitatedTrainingIDsAsMap)) {
                    jobLogger.log("Create Facilitated Training Request and Training Assignment");
                    facilitatedTrainAndTrainAssignment = vpsExternalTrainingHelperService.createFacilitatedReqAndTrainingAssignment(facilitatedTrainingIDsAsMap);
                    if (!facilitatedTrainAndTrainAssignment.isEmpty()) {
                        //get ftr record to perform user actions
                        jobLogger.log("Complete Facilitated Training Request");
                        vpsExternalTrainingHelperService.callUserAction(facilitatedTrainingIDsAsMap, FACILITATED_RECORD_COMPLETE_USER_ACTION);
                     } else {
                        jobLogger.log("No Facilitated Training Request And Training assignment created for ID:" + recID);
                    }
                } else {
                    jobLogger.log("No Facilitated Training Request created for ID:" + recID);
                }
            } else {
                jobLogger.log("No Assigned training found for external training ID:" + recID);
            }

        }
        taskOutput.setState(TaskState.SUCCESS);
        jobLogger.log("****** End VpsDataQueueItemProcessorJob process *******");


    }

    protected static boolean isCollectionMapNullOrEmpty(final Map<?, ?> m) {
        return m == null || m.isEmpty();
    }


    @Override
    public void completeWithSuccess(JobCompletionContext jobCompletionContext) {
        JobLogger logger = jobCompletionContext.getJobLogger();
        logger.log("All tasks completed successfully");
    }

    @Override
    public void completeWithError(JobCompletionContext jobCompletionContext) {
        JobResult result = jobCompletionContext.getJobResult();

        JobLogger logger = jobCompletionContext.getJobLogger();
        logger.log("completeWithError: " + result.getNumberFailedTasks() + "tasks failed out of " + result.getNumberTasks());

        List<JobTask> tasks = jobCompletionContext.getTasks();
        for (JobTask task : tasks) {
            TaskOutput taskOutput = task.getTaskOutput();
            if (TaskState.ERRORS_ENCOUNTERED.equals(taskOutput.getState())) {
                logger.log(task.getTaskId() + " failed with error message " + taskOutput.getValue("firstError", JobValueType.STRING));
            }
        }
    }


}