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
import com.veeva.vault.custom.model.VpsExternalTrainingInfo;
import com.veeva.vault.custom.services.VpsMultiAssignmentHelperService;
import com.veeva.vault.custom.services.VpsJobHelperService;
import com.veeva.vault.custom.services.VpsUtilHelperService;
import com.veeva.vault.sdk.api.core.*;
import com.veeva.vault.sdk.api.data.Record;
import com.veeva.vault.sdk.api.data.RecordService;
import com.veeva.vault.sdk.api.job.*;
import com.veeva.vault.sdk.api.query.QueryResponse;

import java.util.List;
import java.util.Map;

@JobInfo(adminConfigurable = true, idempotent = true, isVisible = true)


public class VpsCreateFacilitatedTrainingMultipleAssignmentJob implements Job {


    private static final String FACILITATED_RECORD_COMPLETE_USER_ACTION = "change_state_to_requested_useraction__c";
    private static final String ADHOC_RECORD_INPROCESS_USER_ACTION = "change_state_to_inprocess_useraction__c";
    private static final String ADHOC_RECORD_PROCESSED_USER_ACTION = "change_state_to_processed_useraction1__c";
    /**
     * adhoc training object Types
     **/
    private static final String OBJTYPE_EXT_TRAINING_MULTIPLE_TRAINING = "external_training_multiple_trainings__c";

    @Override
    public JobInputSupplier init(JobInitContext jobInitContext) {
        JobLogger jobLogger = jobInitContext.getJobLogger();
        jobLogger.log("***** Start VpsCreateFacilitatedTrainingMultipleAssignmentJob init***");
        List<JobItem> jobItems = VaultCollections.newList();

        // Get ID's of all documents need a sharing setting updates
        VpsVQLHelper vqlHelper = new VpsVQLHelper();
        vqlHelper.clearVQL();
//        vqlHelper.appendVQL("select  object_type__v,  comments__c,  completion_date__c,  date_of_training__c, " +
//                "description__c, id,instructor__c,  state__v,  name__v,  person__c,  " +
//                "reason_for_completion__c,  status__v,  title__c  from ad_hoc_training__c " +
//                "where state__v='active_state__c'");
//        vqlHelper.appendVQL("select  id from ad_hoc_training__c where state__v='processed_state__c' and name__v='VV-000002'");
        vqlHelper.appendVQL("select  id from ad_hoc_training__c ");
        vqlHelper.appendVQL(" where state__v='inprocess_state__c'");
        vqlHelper.appendVQL("and name__v like 'EXT%' ");
        vqlHelper.appendVQL("and object_type__vr.api_name__v='" + OBJTYPE_EXT_TRAINING_MULTIPLE_TRAINING + "'");
//        vqlHelper.appendVQL("select  id from ad_hoc_training__c where state__v='initial_state__c' and name__v like 'EXT%'");

        QueryResponse queryResponse = vqlHelper.runVQL();

        queryResponse.streamResults().forEach(queryResult -> {
            JobItem jobItem = jobInitContext.newJobItem();
            String recID = queryResult.getValue("id", ValueType.STRING);
            jobItem.setValue("id", recID);
            jobItems.add(jobItem);
        });
        jobLogger.log("***** End VpsCreateFacilitatedTrainingMultipleAssignmentJob init***");
        return jobInitContext.newJobInput(jobItems);

    }

    @Override
    public void process(JobProcessContext con) {

        JobLogger jobLogger = con.getJobLogger();
        VpsMultiAssignmentHelperService vpsMultiAssignmentHelperService = ServiceLocator.locate(VpsMultiAssignmentHelperService.class);
        RecordService recordService = ServiceLocator.locate(RecordService.class);
        jobLogger.log("****** Start VpsCreateFacilitatedTrainingMultipleAssignmentJob process *******");
        List<JobItem> jobItems = con.getCurrentTask().getItems();
        TaskOutput taskOutput = con.getCurrentTask().getTaskOutput();
        Map<List<String>, List<String>> newFacilitatedTrainingIDsAsMap = VaultCollections.newMap();
        List<String> facilitatedTrainAndTrainAssignment = VaultCollections.newList();

        List<Record> adhocTrainingRecords = VaultCollections.newList();
        VpsJobHelperService jobHelperService = ServiceLocator.locate(VpsJobHelperService.class);

        for (JobItem jobItem : jobItems) {
            String adHocTrainingId = jobItem.getValue("id", JobValueType.STRING);
            /** TODO change to inprogress after demo**/
//            Record adHoc= recordService.newRecordWithId("ad_hoc_training__c",adHocTrainingId);
//            adhocTrainingRecords.add(adHoc);
//            jobHelperService.callUserActions(adhocTrainingRecords,ADHOC_RECORD_INPROCESS_USER_ACTION);
            /** TODO change to inprogress after demo**/
            jobLogger.log("Start processing external training ID:" + adHocTrainingId);
            Map<String, List<VpsExternalTrainingInfo>> externalTrainingAssignmentInfo = vpsMultiAssignmentHelperService.getExternalTrainingAssignmentInfo(adHocTrainingId, OBJTYPE_EXT_TRAINING_MULTIPLE_TRAINING);

            if (!VpsUtilHelperService.isCollectionMapNullOrEmpty(externalTrainingAssignmentInfo)) {
                jobLogger.log("Create Facilitated Training Request");
                newFacilitatedTrainingIDsAsMap = vpsMultiAssignmentHelperService.createFacilitatedTrainingForAssignments(externalTrainingAssignmentInfo);

                if (!VpsUtilHelperService.isCollectionMapNullOrEmpty(newFacilitatedTrainingIDsAsMap)) {
                    jobLogger.log("Create Facilitated Training Request and Training Assignment");
                    facilitatedTrainAndTrainAssignment = vpsMultiAssignmentHelperService.addTrainingAssignmentsInFacilitiatedTraining(newFacilitatedTrainingIDsAsMap);

                    if (!facilitatedTrainAndTrainAssignment.isEmpty()) {
                        //get ftr record to perform user actions
                        jobLogger.log("Complete Facilitated Training Request");
                        vpsMultiAssignmentHelperService.completeFacilitatedTrainingRequest(newFacilitatedTrainingIDsAsMap, FACILITATED_RECORD_COMPLETE_USER_ACTION);
                    }
                    else {
                        jobLogger.log("No Facilitated Training Request And Training assignment created for ID:" + adHocTrainingId);
                    }
                }
                else {
                    jobLogger.log("No Facilitated Training Request created for ID:" + adHocTrainingId);
                }
            }
            else {
                jobLogger.log("No Assigned training found for external training ID:" + adHocTrainingId);
            }
            /** change to processed **/
            Record adHocTrain = recordService.newRecordWithId("ad_hoc_training__c", adHocTrainingId);
            adhocTrainingRecords.add(adHocTrain);
            jobHelperService.callUserActions(adhocTrainingRecords, ADHOC_RECORD_PROCESSED_USER_ACTION);
        }
        taskOutput.setState(TaskState.SUCCESS);
        jobLogger.log("****** End VpsCreateFacilitatedTrainingMultipleAssignmentJob process *******");
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