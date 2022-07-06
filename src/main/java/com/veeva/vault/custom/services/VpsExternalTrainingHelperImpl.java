/*
 * --------------------------------------------------------------------
 * UserDefinedService:	VpsExternalFTRTrainingHelper
 * Author:				Amol Chinchalkar @ Veeva
 * Date:				2022-06-30
 *---------------------------------------------------------------------
 * Description:
 *---------------------------------------------------------------------
 * Copyright (c) 2022 Veeva Systems Inc.  All Rights Reserved.
 *		This code is based on pre-existing content developed and
 * 		owned by Veeva Systems Inc. and may only be used in connection
 *		with the deliverable with which it was provided to Customer.
 *---------------------------------------------------------------------
 */
package com.veeva.vault.custom.services;

import com.veeva.vault.custom.model.VpsExternalTrainingModel;
import com.veeva.vault.sdk.api.core.*;
import com.veeva.vault.sdk.api.data.Record;
import com.veeva.vault.sdk.api.data.RecordService;
import com.veeva.vault.sdk.api.query.QueryResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static com.veeva.vault.custom.services.VpsVQLServiceImpl.*;

@UserDefinedServiceInfo
public class VpsExternalTrainingHelperImpl implements VpsExternalTrainingHelperService {
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
    static final String TA_OBJFIELD_TA_STATE = "training_assignment__cr.state__v";
    static final String AD_HOC_TRAINING_TA_OBJ = "ad_hoc_trainingta__c";
    private static final String OBJFIELD_COMMENTS = "comments__c";
    private static final String OBJFIELD_CURRICULUM = "curriculum__v";
    private static final String OBJFIELD_DATE_OF_COMPLETION = "date_of_completion__c";
    private static final String OBJFIELD_REASON = "reason__v";
    private static final String OBJFIELD_REASON_FOR_COMPLETION = "reason_for_completion__c";
    private static final String OBJFIELD_TRAINING_REQUIREMENT = "training_requirement__v";


    /***
     *
     * @param Id
     * @return
     */
    @Override
    public Map<String, List<VpsExternalTrainingModel>> getExternalTrainingAssignmentModels(String Id) {
        VpsVQLService vpsVQLService = ServiceLocator.locate(VpsVQLService.class);
        UserDefinedModelService modelService = ServiceLocator.locate(UserDefinedModelService.class);
        LogService logService = ServiceLocator.locate(LogService.class);
        Map<String, List<VpsExternalTrainingModel>> externalFTRQueueItemsMap = VaultCollections.newMap();
        logService.debug("Start getExternalTrainingAssignmentModels");
        List<VpsExternalTrainingModel> externalTrainingAssignmentsQueueItems = VaultCollections.newList();
        QueryResponse queryResponse = vpsVQLService.query(
                SELECT, vpsVQLService.appendFields(TA_OBJFIELD_COMMENTS, TA_OBJFIELD_TA_STATE, AD_HOC_OBJ_AD_HOC_TRAINING, AD_HOC_OBJFIELD_TRAINING_ASSIGNMENT_ID, TA_OBJFIELD_TRAINING_REQUIREMENT_ID, AD_HOC_OBJFIELD_COMMENTS,
                        AD_HOC_OBJFIELD_COMPLETION_DATE, AD_HOC_OBJFIELD_DATE_OF_TRAINING, AD_HOC_OBJFIELD_INSTRUCTOR, AD_HOC_OBJFIELD_REASON_FOR_COMPLETION, AD_HOC_OBJFIELD_STATUS, AD_HOC_OBJFIELD_TRAINING_METHOD),
                FROM, AD_HOC_TRAINING_TA_OBJ,
                WHERE, AD_HOC_OBJ_AD_HOC_TRAINING + " = '" + Id + "'", AND, TA_OBJFIELD_TA_STATE + "='assigned_state__v'",
                ORDER_BY, AD_HOC_OBJ_AD_HOC_TRAINING
        );

        queryResponse.streamResults().forEach(queryResult -> {
            VpsExternalTrainingModel vpsExternalFTRQueueItem = modelService.newUserDefinedModel(VpsExternalTrainingModel.class);
            /** Ad Hoc Comments **/
            String comments = queryResult.getValue(AD_HOC_OBJFIELD_COMMENTS, ValueType.STRING);
            vpsExternalFTRQueueItem.setComments(comments);
            logService.debug("Ad Hoc Comments:" + comments);
            /** TA Comments**/
            String trainingAssignmentComment = queryResult.getValue(TA_OBJFIELD_COMMENTS, ValueType.STRING);
            vpsExternalFTRQueueItem.setTrainingAssignmentComments(trainingAssignmentComment);
            logService.debug("TA Comments:" + trainingAssignmentComment);
            /** Date of Training **/
            LocalDate dateOfTraining = queryResult.getValue(AD_HOC_OBJFIELD_DATE_OF_TRAINING, ValueType.DATE);
            if (dateOfTraining != null) {
                vpsExternalFTRQueueItem.setDateOfTraining(dateOfTraining.toString());
            }
            logService.debug("Date of Training:" + dateOfTraining);
            /** Date of Completion **/
            LocalDate completionDate = queryResult.getValue(AD_HOC_OBJFIELD_COMPLETION_DATE, ValueType.DATE);
            if (completionDate != null) {
                vpsExternalFTRQueueItem.setCompletionDate(completionDate.toString());
            }
            logService.debug("Date of Completion:" + completionDate);
            /** Reason for Completion **/
            List<String> reasonForCompletion = queryResult.getValue(AD_HOC_OBJFIELD_REASON_FOR_COMPLETION, ValueType.PICKLIST_VALUES);
            vpsExternalFTRQueueItem.setReasonForCompletion(reasonForCompletion.get(0));
            logService.debug("Reason for Completion:" + reasonForCompletion);
            /** Instructor **/
            String instructor = queryResult.getValue(AD_HOC_OBJFIELD_INSTRUCTOR, ValueType.STRING);
            vpsExternalFTRQueueItem.setInstructor(instructor);
            logService.debug("Instructor:" + instructor);
            /** Training Method **/
            List<String> trainingMethod = queryResult.getValue(AD_HOC_OBJFIELD_TRAINING_METHOD, ValueType.REFERENCES);
            if (trainingMethod != null && !trainingMethod.isEmpty()) {
                vpsExternalFTRQueueItem.setTrainingMethod(trainingMethod.get(0));
            }
            logService.debug("Training Method:" + trainingMethod);
            /** Training Assignment ID **/
            String trainingAssignmentID = queryResult.getValue(AD_HOC_OBJFIELD_TRAINING_ASSIGNMENT_ID, ValueType.STRING);
            vpsExternalFTRQueueItem.setTrainingAssignmentID(trainingAssignmentID);
            logService.debug("Training Assignment ID:" + trainingAssignmentID);
            /** Training Requirement ID **/
            String trainingRequirementID = queryResult.getValue(TA_OBJFIELD_TRAINING_REQUIREMENT_ID, ValueType.STRING);
            vpsExternalFTRQueueItem.setTrainingRequirementID(trainingRequirementID);
            logService.debug("Training Requirement ID:" + trainingRequirementID);
            externalTrainingAssignmentsQueueItems.add(vpsExternalFTRQueueItem);
        });
        if (!externalTrainingAssignmentsQueueItems.isEmpty()) {
            externalFTRQueueItemsMap.put(Id, externalTrainingAssignmentsQueueItems);
        }
        logService.debug("End getExternalTrainingAssignmentModels");
        return externalFTRQueueItemsMap;
    }


    /**
     * @param vpsExternalTrainingQueueItemsMap
     */
    public Map<List<String>, List<String>> createFacilitatedTrainingForAssignments(Map<String, List<VpsExternalTrainingModel>> vpsExternalTrainingQueueItemsMap) {
        LogService logService = ServiceLocator.locate(LogService.class);
        RecordService recordService = ServiceLocator.locate(RecordService.class);
        VpsRecordHelperService vpsRecordHelperService = ServiceLocator.locate(VpsRecordHelperService.class);
        List<Record> recordToCreate = VaultCollections.newList();
        List<String> trainingAssigmentsInFTR = VaultCollections.newList();
        Map<List<String>, List<String>> adHocIdFTRMap = VaultCollections.newMap();
        logService.debug("Start createFacilitatedTrainingForAssignments");
        String adHocTrainingID = "";

        for (List<VpsExternalTrainingModel> vpsExternalTrainingQueueItems : vpsExternalTrainingQueueItemsMap.values()) {
            for (VpsExternalTrainingModel vpsExternalTrainingQueueItem : vpsExternalTrainingQueueItems) {
                //create Facilitated Training Requests
                Record ftrRecord = recordService.newRecord("facilitated_training_request__v");
                String comments = vpsExternalTrainingQueueItem.getComments();
                logService.debug("comments: " + comments);
                ftrRecord.setValue(OBJFIELD_COMMENTS, comments);
                String reasonForCompletion = vpsExternalTrainingQueueItem.getReasonForCompletion();

                if (reasonForCompletion == null || reasonForCompletion.equalsIgnoreCase("")) {
                    reasonForCompletion = "brief__c";
                }
                ftrRecord.setValue(OBJFIELD_REASON_FOR_COMPLETION, VaultCollections.asList(reasonForCompletion));

                logService.debug("reasonForCompletion: " + reasonForCompletion);
                String dateOfCompletion = vpsExternalTrainingQueueItem.getCompletionDate();
                if (dateOfCompletion != null && !dateOfCompletion.equals("")) {
                    ftrRecord.setValue(OBJFIELD_DATE_OF_COMPLETION, LocalDate.parse(dateOfCompletion));
                } else {
                    ftrRecord.setValue(OBJFIELD_DATE_OF_COMPLETION, LocalDate.now());
                }
                logService.debug("dateOfCompletion: " + dateOfCompletion);
                String trainingReqID = vpsExternalTrainingQueueItem.getTrainingRequirementID();
                logService.debug("trainingReqID: " + trainingReqID);
                ftrRecord.setValue(OBJFIELD_TRAINING_REQUIREMENT, trainingReqID);
                recordToCreate.add(ftrRecord);
                //store to create FTR to TA record
                trainingAssigmentsInFTR.add(vpsExternalTrainingQueueItem.getTrainingAssignmentID());
                logService.debug("End createFTRFromQueueItems");
            }
        }
        //create objects in bulk
        if (!recordToCreate.isEmpty()) {
            List<String> newFacilitatedTrainingIDs = vpsRecordHelperService.saveRecordsInBatchReturnIds(recordToCreate, "Failed to create FTR for TR:", true);
            adHocIdFTRMap.put(trainingAssigmentsInFTR, newFacilitatedTrainingIDs);
        }
        logService.debug("End createFacilitatedTrainingForAssignments");
        return adHocIdFTRMap;
    }

    /**
     * @param facilitatedTrainingIDsAsMap
     * @return
     */
    public List<String> createFacilitatedReqAndTrainingAssignment(Map<List<String>, List<String>> facilitatedTrainingIDsAsMap) {
        RecordService recordService = ServiceLocator.locate(RecordService.class);
        VpsRecordHelperService vpsRecordHelperService = ServiceLocator.locate(VpsRecordHelperService.class);
        LogService logService = ServiceLocator.locate(LogService.class);
        List<Record> recordToCreate = VaultCollections.newList();
        List<String> facilitatedTrainingAndAssignments = VaultCollections.newList();

        logService.debug("Start createFacilitatedReqAndTrainingAssignment");
        for (List<String> trainingAssignments : facilitatedTrainingIDsAsMap.keySet()) {
            for (List<String> newFacilitatedTrainIDs : facilitatedTrainingIDsAsMap.values()) {

                logService.debug("Total Facilitated Training IDs:" + newFacilitatedTrainIDs.size());
                logService.debug("Total Training Assignments:" + trainingAssignments.size());

                if (trainingAssignments.size() == newFacilitatedTrainIDs.size()) {
                    for (int i = 0; i < trainingAssignments.size(); i++) {
                        Record ftrTARecord = recordService.newRecord("facilitated_request_training_assignment__v");
                        ftrTARecord.setValue("facilitated_training_request__v", newFacilitatedTrainIDs.get(i));
                        ftrTARecord.setValue("training_assignment__v", trainingAssignments.get(i));
                        recordToCreate.add(ftrTARecord);
                    }
                }
            }
        }
        if (!recordToCreate.isEmpty()) {
            facilitatedTrainingAndAssignments = vpsRecordHelperService.saveRecordsInBatchReturnIds(recordToCreate, "Failed to create Facilitated Training and Assignment Object", true);
        }
        logService.debug("End createFacilitatedReqAndTrainingAssignment");
        return facilitatedTrainingAndAssignments;
    }

    /**
     * @param facilitatedTrainingIDsAsMap
     * @return
     */
    public void callUserAction(Map<List<String>, List<String>> facilitatedTrainingIDsAsMap, String actionName) {
        RecordService recordService = ServiceLocator.locate(RecordService.class);
        VpsRecordHelperService vpsRecordHelperService = ServiceLocator.locate(VpsRecordHelperService.class);
        LogService logService = ServiceLocator.locate(LogService.class);
        List<Record> recordToPerformAction = VaultCollections.newList();
        List<String> facilitatedTrainingAndAssignments = VaultCollections.newList();
        VpsJobHelperService jobHelperService = ServiceLocator.locate(VpsJobHelperService.class);
        VpsAPIClientService apiService = ServiceLocator.locate(VpsAPIClientService.class);
        RuntimeService runtimeService = ServiceLocator.locate(RuntimeService.class);
        logService.debug("Start callUserAction");
        //runtimeService.sleep(3000);
        for (List<String> newFacilitatedTrainIDs : facilitatedTrainingIDsAsMap.values()) {
            for (String FTRID : newFacilitatedTrainIDs) {
                logService.debug("FTR ID:" + FTRID);
                Record ftrRecord = recordService.newRecordWithId("facilitated_training_request__v", FTRID);
                recordToPerformAction.add(ftrRecord);
            }
            if (!recordToPerformAction.isEmpty()) {
                jobHelperService.callUserActions(recordToPerformAction, actionName);
            }
        }
        logService.debug("End callUserAction");
    }


}