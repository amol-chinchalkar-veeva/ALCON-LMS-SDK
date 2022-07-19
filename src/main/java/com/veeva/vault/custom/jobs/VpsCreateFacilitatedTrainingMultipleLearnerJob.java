/*
 * --------------------------------------------------------------------
 * Job Info:	VpsCreateExternalTrainingMultiplePersonsJob
 * Author:			Amol Chinchalkar @ Veeva
 * Date:			2022-07-11
 *---------------------------------------------------------------------
 * Description:	Single Training Assignment for multiple person
 *---------------------------------------------------------------------
 * Copyright (c) 2022 Veeva Systems Inc.  All Rights Reserved.
 *		This code is based on pre-existing content developed and
 *		owned by Veeva Systems Inc. and may only be used in connection
 *		with the deliverable with which it was provided to Customer.
 *---------------------------------------------------------------------
 */
package com.veeva.vault.custom.jobs;


import com.veeva.vault.custom.classes.VpsVQLHelper;
import com.veeva.vault.custom.services.VpsJobHelperService;
import com.veeva.vault.custom.services.VpsMultiLearnerHelperServiceImpl;
import com.veeva.vault.sdk.api.core.ServiceLocator;
import com.veeva.vault.sdk.api.core.ValueType;
import com.veeva.vault.sdk.api.core.VaultCollections;
import com.veeva.vault.sdk.api.data.Record;
import com.veeva.vault.sdk.api.data.RecordService;
import com.veeva.vault.sdk.api.job.*;
import com.veeva.vault.sdk.api.query.QueryResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@JobInfo(adminConfigurable = true, idempotent = true, isVisible = true)
public class VpsCreateFacilitatedTrainingMultipleLearnerJob implements Job {

    /**
     * adhoc training object Types
     **/
    private static final String OBJTYPE_EXT_TRAINING_MULTIPLE_LEARNER = "external_training_multiple_learners__c";
    private static final String FACILITATED_RECORD_COMPLETE_USER_ACTION = "change_state_to_requested_useraction__c";
    private static final String ADHOC_RECORD_PROCESSED_USER_ACTION = "change_state_to_processed_useraction1__c";

    @Override
    public JobInputSupplier init(JobInitContext jobInitContext) {
        JobLogger jobLogger = jobInitContext.getJobLogger();
        jobLogger.log("****** Start VpsCreateExternalTrainingMultiplePersonsJob ******");
        List<JobItem> jobItems = VaultCollections.newList();

        // Get ID's of all documents need a sharing setting updates
        VpsVQLHelper vqlHelper = new VpsVQLHelper();
        vqlHelper.clearVQL();
        vqlHelper.appendVQL("select  id, training_requirement__c, completion_date__c,reason_for_completion__c ");
        vqlHelper.appendVQL(" from ad_hoc_training__c ");
        vqlHelper.appendVQL("where state__v='inprocess_state__c' ");
        vqlHelper.appendVQL("and name__v like 'EXT%' ");
        vqlHelper.appendVQL("and object_type__vr.api_name__v='" + OBJTYPE_EXT_TRAINING_MULTIPLE_LEARNER + "'");

        QueryResponse queryResponse = vqlHelper.runVQL();
        queryResponse.streamResults().forEach(queryResult -> {
            JobItem jobItem = jobInitContext.newJobItem();
            String recID = queryResult.getValue("id", ValueType.STRING);
            String trainingRequirementID = queryResult.getValue("training_requirement__c", ValueType.STRING);
            LocalDate dateOfCompletion = queryResult.getValue("completion_date__c", ValueType.DATE);
            List<String> reasonForCompletion = queryResult.getValue("reason_for_completion__c", ValueType.PICKLIST_VALUES);
            jobItem.setValue("id", recID);
            jobItem.setValue("training_requirement__c", trainingRequirementID);
            jobItem.setValue("completion_date__c", dateOfCompletion);
            /** Reason for Completion **/
            if (!reasonForCompletion.isEmpty()) {
                jobItem.setValue("reason_for_completion__c", reasonForCompletion.get(0).toString());
            } else {
                jobItem.setValue("reason_for_completion__c", "brief__c");
            }
            jobItems.add(jobItem);
        });
        return jobInitContext.newJobInput(jobItems);
    }

    /**
     * @param con
     */
    @Override
    public void process(JobProcessContext con) {
        JobLogger jobLogger = con.getJobLogger();
        VpsMultiLearnerHelperServiceImpl vpsMultiLearnerHelper = ServiceLocator.locate(VpsMultiLearnerHelperServiceImpl.class);
        RecordService recordService = ServiceLocator.locate(RecordService.class);
        jobLogger.log("****** Start VpsCreateExternalFTRTrainingJob process *******");
        List<JobItem> jobItems = con.getCurrentTask().getItems();
        TaskOutput taskOutput = con.getCurrentTask().getTaskOutput();
        // Map<List<String>, List<String>> facilitatedTrainingIDsAsMap = VaultCollections.newMap();
        List<String> learnersTrainingAssignment = VaultCollections.newList();

        List<Record> adhocTrainingRecords = VaultCollections.newList();
        VpsJobHelperService jobHelperService = ServiceLocator.locate(VpsJobHelperService.class);

        for (JobItem jobItem : jobItems) {
            String recID = jobItem.getValue("id", JobValueType.STRING);
            String trainingRequirementID = jobItem.getValue("training_requirement__c", JobValueType.STRING);
            String dateOfCompletion = jobItem.getValue("completion_date__c", JobValueType.DATE).toString();
            String reasonForCompletion = jobItem.getValue("reason_for_completion__c", JobValueType.STRING);
            /** TODO change to inprogress after demo**/

            /** TODO change to inprogress after demo**/
            jobLogger.log("Start processing external training ID:" + recID);
            List<String> learnerInfo = vpsMultiLearnerHelper.getLearnerInfo(recID);
            if (!learnerInfo.isEmpty()) {
                jobLogger.log("Validate learner has training assigned");
                Map<String, String> learnersForAssignment = vpsMultiLearnerHelper.getValidLearnersForAssignment(learnerInfo, trainingRequirementID);

                //TODO Error handling for invalid persons
                //compare the externalperson list and valid list

                //select id, learner__v,learner__vr.name__v,learner__vr.federated_id__c from training_assignment__v where training_requirement__v='V160000000OL519' and learner__v='V0C000000000D46'
                jobLogger.log("Create Facilitated Training Request");
                if (!isCollectionMapNullOrEmpty(learnersForAssignment)) {
                    String newFacilitatedTrainingID = vpsMultiLearnerHelper.createFacilitatedTrainingForLearners(trainingRequirementID, dateOfCompletion, reasonForCompletion);

                    if (newFacilitatedTrainingID != null && !newFacilitatedTrainingID.equals("")) {
                        jobLogger.log("Create Facilitated Training Request and Training Assignment");
                        learnersTrainingAssignment = vpsMultiLearnerHelper.addTrainingAssignmentsInFacilitiatedTraining(learnersForAssignment, newFacilitatedTrainingID);

                        if (!learnersTrainingAssignment.isEmpty()) {
                            jobLogger.log("Complete Facilitated Training Request");
                            vpsMultiLearnerHelper.completeFacilitatedTrainingRequest(newFacilitatedTrainingID, FACILITATED_RECORD_COMPLETE_USER_ACTION);
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
            /** change to processed **/
            Record adHocTrain = recordService.newRecordWithId("ad_hoc_training__c", recID);
            adhocTrainingRecords.add(adHocTrain);
            jobHelperService.callUserActions(adhocTrainingRecords, ADHOC_RECORD_PROCESSED_USER_ACTION);
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

    protected static boolean isCollectionMapNullOrEmpty(final Map<?, ?> m) {
        return m == null || m.isEmpty();
    }

}