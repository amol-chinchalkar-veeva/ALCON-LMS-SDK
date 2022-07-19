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

import com.veeva.vault.custom.model.VpsExternalTrainingInfo;
import com.veeva.vault.sdk.api.core.*;
import com.veeva.vault.sdk.api.data.Record;
import com.veeva.vault.sdk.api.data.RecordService;
import com.veeva.vault.sdk.api.query.QueryResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static com.veeva.vault.custom.services.VpsVQLServiceImpl.*;

@UserDefinedServiceInfo
public class VpsMultiAssignmentHelperServiceImpl implements VpsMultiAssignmentHelperService {


    private static final String AD_HOC_OBJFIELD_CREATED_BY = "created_by__v";

    static final String OBJFIELD_TRAINING_ASSIGNMENT_STATE = "training_assignment__cr.state__v";
    static final String ASSIGNED_STATE = "assigned_state__v";
    static final String TRAINING_ASSIGNMENT_OBJ = "training_assignment__v";
    static final String FACILITATED_REQUEST_TRAINING_ASSIGNMENT_OBJ = "facilitated_request_training_assignment__v";


    /**
     * ad_hoc_training__c objects and fields
     **/
    private static final String AD_HOC_TRAINING_OBJ = "ad_hoc_training__c";
    private static final String AD_HOC_OBJFIELD_COMMENTS = "comments__c";
    private static final String AD_HOC_OBJFIELD_COMPLETION_DATE = "completion_date__c";

    private static final String AD_HOC_OBJFIELD_REASON_FOR_COMPLETION = "reason_for_completion__c";
    private static final String OBJFIELD_STATUS = "status__v";
    private static final String AD_HOC_OBJFIELD_TRAINING_ASSIGNMENT_ID = "training_assignment__c";

    static final String TA_OBJFIELD_COMMENTS = "ad_hoc_training__cr.comments__c";

    /**
     * ad_hoc_trainingperson__c objects and fields
     **/
    static final String AD_HOC_TRAINING_PERSON_OBJ = "ad_hoc_trainingperson__c";
    private static final String AD_HOC_TRAINING_PERSON_OBJFIELD_COMMENTS = "comments__c";
    private static final String AD_HOC_TRAINING_PERSON_OBJFIELD_DATE_OF_COMPLETION = "date_of_completion__c";
    private static final String AD_HOC_TRAINING_PERSON_OBJFIELD_REASON_FOR_COMPLETION = "reason_for_completion__c";
    private static final String AD_HOC_TRAINING_PERSON_OBJFIELD_TRAINING_REQUIREMENT = "training_requirement__v";
    private static final String AD_HOC_TRAINING_PERSON_OBJFIELD_DATE_OF_TRAINING = "date_of_training__c";
    // private static final String AD_HOC_TRAINING_PERSON_OBJFIELD_TRAINING_REQUIREMENT_ID = "training_assignment__cr.training_requirement__v";
    private static final String AD_HOC_TRAINING_PERSON_OBJFIELD_PERSON_ID = "person__c";
    private static final String AD_HOC_TRAINING_PERSON_OBJFIELD_PERSON_FEDERATED_ID = "person__cr.federated_id__c";
    private static final String AD_HOC_TRAINING_PERSON_OBJFIELD_PERSON_NAME = "person__cr.name__v";
    private static final String AD_HOC_TRAINING_PERSON_OBJFIELD_AD_HOC_TRAINING = "ad_hoc_training__c";
    private static final String AD_HOC_TRAINING_PERSON_OBJFIELD_INSTRUCTOR = "instructor__c";
    private static final String AD_HOC_TRAINING_PERSON_OBJFIELD_TRAINING_METHOD = "training_method__c";
    private static final String AD_HOC_TRAINING_PERSON_OBJFIELD_TRAINING_ASSIGNMENT_ID = "ad_hoc_training__cr.training_assignment__c";
    private static final String AD_HOC_TRAINING_PERSON_OBJFIELD_TRAINING_REQUIREMENT_ID = "training_requirement__c";
    /**
     * ad_hoc_trainingta__c objects and fields
     **/
    static final String AD_HOC_TRAINING_TA_OBJ = "ad_hoc_trainingta__c";
    private static final String AD_HOC_TRAINING_TA_OBJFIELD_COMMENTS = "comments__c";
    private static final String AD_HOC_TRAINING_TA_OBJFIELD_DATE_OF_COMPLETION = "date_of_completion__c";
    private static final String AD_HOC_TRAINING_TA_OBJFIELD_REASON_FOR_COMPLETION = "reason_for_completion__c";
    ;
    private static final String AD_HOC_TRAINING_TA_OBJFIELD_DATE_OF_TRAINING = "date_of_training__c";
    private static final String AD_HOC_TRAINING_TA_OBJFIELD_TRAINING_REQUIREMENT_ID = "training_assignment__cr.training_requirement__v";
    private static final String AD_HOC_TRAINING_TA_OBJFIELD_AD_HOC_TRAINING = "ad_hoc_training__c";
    private static final String AD_HOC_TRAINING_TA_OBJFIELD_TRAINING_ASSIGNMENT_ID = "training_assignment__c";
    private static final String AD_HOC_TRAINING_TA_OBJFIELD_INSTRUCTOR = "instructor__c";
    private static final String AD_HOC_TRAINING_TA_OBJFIELD_TRAINING_METHOD = "training_method__c";
    /**
     * facilitated_training_request__v objects and fields
     **/
    static final String FACILITATED_TRAINING_REQUEST_OBJ = "facilitated_training_request__v";
    private static final String FTR_OBJFIELD_COMMENTS = "comments__c";
    private static final String FTR_OBJFIELD_DATE_OF_COMPLETION = "date_of_completion__c";
    private static final String FTR_OBJFIELD_REASON_FOR_COMPLETION = "reason_for_completion__c";
    private static final String FTR_OBJFIELD_TRAINING_REQUIREMENT = "training_requirement__v";
    /**
     * adhoc training object Types
     **/
    private static final String OBJTYPE_EXT_TRAINING_MULTIPLE_LEARNER = "external_training_multiple_learners__c";
    private static final String OBJTYPE_EXT_TRAINING_MULTIPLE_TRAINING = "external_training_multiple_trainings__c";


    /**
     *
     * @param Id
     * @param trainingType
     * @return
     */
    @Override
    public Map<String, List<VpsExternalTrainingInfo>> getExternalTrainingAssignmentInfo(String Id, String trainingType) {
        VpsVQLService vpsVQLService = ServiceLocator.locate(VpsVQLService.class);
        UserDefinedModelService modelService = ServiceLocator.locate(UserDefinedModelService.class);
        LogService logService = ServiceLocator.locate(LogService.class);
        Map<String, List<VpsExternalTrainingInfo>> externalFTRQueueItemsMap = VaultCollections.newMap();
        logService.debug("Start getExternalTrainingAssignmentModels");
        List<VpsExternalTrainingInfo> externalTrainingAssignmentsQueueItems = VaultCollections.newList();
        QueryResponse queryResponse = null;


            queryResponse = vpsVQLService.query(
                    SELECT, vpsVQLService.appendFields(AD_HOC_TRAINING_TA_OBJFIELD_COMMENTS, OBJFIELD_TRAINING_ASSIGNMENT_STATE, AD_HOC_TRAINING_TA_OBJFIELD_AD_HOC_TRAINING, AD_HOC_TRAINING_TA_OBJFIELD_TRAINING_ASSIGNMENT_ID, AD_HOC_TRAINING_TA_OBJFIELD_TRAINING_REQUIREMENT_ID,
                            AD_HOC_TRAINING_TA_OBJFIELD_DATE_OF_COMPLETION, AD_HOC_TRAINING_TA_OBJFIELD_DATE_OF_TRAINING, AD_HOC_TRAINING_TA_OBJFIELD_INSTRUCTOR, AD_HOC_TRAINING_TA_OBJFIELD_REASON_FOR_COMPLETION, OBJFIELD_STATUS, AD_HOC_TRAINING_TA_OBJFIELD_TRAINING_METHOD),
                    FROM, AD_HOC_TRAINING_TA_OBJ,
                    WHERE, AD_HOC_TRAINING_TA_OBJFIELD_AD_HOC_TRAINING + " = '" + Id + "'", AND, OBJFIELD_TRAINING_ASSIGNMENT_STATE + "='" + ASSIGNED_STATE + "'",
                    ORDER_BY, AD_HOC_TRAINING_TA_OBJFIELD_AD_HOC_TRAINING
            );
//        } else {
//            queryResponse = vpsVQLService.query(
//                    SELECT, vpsVQLService.appendFields(AD_HOC_TRAINING_PERSON_OBJFIELD_COMMENTS, AD_HOC_TRAINING_PERSON_OBJFIELD_TRAINING_ASSIGNMENT_ID, OBJFIELD_TRAINING_ASSIGNMENT_STATE, AD_HOC_TRAINING_PERSON_OBJFIELD_AD_HOC_TRAINING,
//                            AD_HOC_TRAINING_PERSON_OBJFIELD_DATE_OF_COMPLETION, AD_HOC_TRAINING_PERSON_OBJFIELD_PERSON_ID, AD_HOC_TRAINING_PERSON_OBJFIELD_PERSON_FEDERATED_ID, AD_HOC_TRAINING_PERSON_OBJFIELD_DATE_OF_TRAINING, AD_HOC_TRAINING_PERSON_OBJFIELD_REASON_FOR_COMPLETION, OBJFIELD_STATUS, AD_HOC_TRAINING_PERSON_OBJFIELD_TRAINING_METHOD),
//                    FROM, AD_HOC_TRAINING_PERSON_OBJ,
//                    WHERE, AD_HOC_TRAINING_PERSON_OBJFIELD_AD_HOC_TRAINING + " = '" + Id + "'", AND, OBJFIELD_TRAINING_ASSIGNMENT_STATE + "='" + ASSIGNED_STATE + "'",
//                    ORDER_BY, AD_HOC_TRAINING_PERSON_OBJFIELD_AD_HOC_TRAINING
//            );
//        }

        queryResponse.streamResults().forEach(queryResult -> {
            VpsExternalTrainingInfo vpsExternalFTRQueueItem = modelService.newUserDefinedModel(VpsExternalTrainingInfo.class);
            /** Ad Hoc Comments **/
            String comments = queryResult.getValue(AD_HOC_TRAINING_TA_OBJFIELD_COMMENTS, ValueType.STRING);
            vpsExternalFTRQueueItem.setComments(comments);
            logService.debug("Ad Hoc Comments:" + comments);
            /** TA Comments**/
            String trainingAssignmentComment = queryResult.getValue(TA_OBJFIELD_COMMENTS, ValueType.STRING);
            vpsExternalFTRQueueItem.setTrainingAssignmentComments(trainingAssignmentComment);
            logService.debug("TA Comments:" + trainingAssignmentComment);
            /** Date of Training **/
            LocalDate dateOfTraining = queryResult.getValue(AD_HOC_TRAINING_PERSON_OBJFIELD_DATE_OF_TRAINING, ValueType.DATE);
            if (dateOfTraining != null) {
                vpsExternalFTRQueueItem.setDateOfTraining(dateOfTraining.toString());
            }
            logService.debug("Date of Training:" + dateOfTraining);
            /** Date of Completion **/
            LocalDate completionDate = queryResult.getValue(AD_HOC_TRAINING_TA_OBJFIELD_DATE_OF_COMPLETION, ValueType.DATE);
            if (completionDate != null) {
                vpsExternalFTRQueueItem.setCompletionDate(completionDate.toString());
            }
            logService.debug("Date of Completion:" + completionDate);
            /** Reason for Completion **/
            List<String> reasonForCompletion = queryResult.getValue(AD_HOC_TRAINING_TA_OBJFIELD_REASON_FOR_COMPLETION, ValueType.PICKLIST_VALUES);
            vpsExternalFTRQueueItem.setReasonForCompletion(reasonForCompletion.get(0));
            logService.debug("Reason for Completion:" + reasonForCompletion);
            /** Instructor **/
            String instructor = queryResult.getValue(AD_HOC_TRAINING_TA_OBJFIELD_INSTRUCTOR, ValueType.STRING);
            vpsExternalFTRQueueItem.setInstructor(instructor);
            logService.debug("Instructor:" + instructor);
            /** Training Method **/
            List<String> trainingMethod = queryResult.getValue(AD_HOC_TRAINING_TA_OBJFIELD_TRAINING_METHOD, ValueType.REFERENCES);
            if (trainingMethod != null && !trainingMethod.isEmpty()) {
                vpsExternalFTRQueueItem.setTrainingMethod(trainingMethod.get(0));
            }
            logService.debug("Training Method:" + trainingMethod);
            if (trainingType.equalsIgnoreCase(OBJTYPE_EXT_TRAINING_MULTIPLE_TRAINING)) {
                /** Training Assignment ID **/
                String trainingAssignmentID = queryResult.getValue(AD_HOC_OBJFIELD_TRAINING_ASSIGNMENT_ID, ValueType.STRING);
                vpsExternalFTRQueueItem.setTrainingAssignmentID(trainingAssignmentID);
                logService.debug("Training Assignment ID:" + trainingAssignmentID);
                /** Training Requirement ID **/
                String trainingRequirementID = queryResult.getValue(AD_HOC_TRAINING_TA_OBJFIELD_TRAINING_REQUIREMENT_ID, ValueType.STRING);
                vpsExternalFTRQueueItem.setTrainingRequirementID(trainingRequirementID);
                logService.debug("Training Requirement ID:" + trainingRequirementID);
                externalTrainingAssignmentsQueueItems.add(vpsExternalFTRQueueItem);
            }
            if (trainingType.equalsIgnoreCase(OBJTYPE_EXT_TRAINING_MULTIPLE_LEARNER)) {
                /** Training Assignment ID **/
                String federatedID = queryResult.getValue(AD_HOC_TRAINING_PERSON_OBJFIELD_PERSON_FEDERATED_ID, ValueType.STRING);
                vpsExternalFTRQueueItem.setPersonFederatedID(federatedID);
                logService.debug("Person Federated ID:" + federatedID);
                /** Training Assignment ID **/
                String trainingAssignmentID = queryResult.getValue(AD_HOC_TRAINING_PERSON_OBJFIELD_TRAINING_ASSIGNMENT_ID, ValueType.STRING);
                vpsExternalFTRQueueItem.setTrainingAssignmentID(trainingAssignmentID);
                logService.debug("Training Assignment ID:" + trainingAssignmentID);
            }

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
    public Map<List<String>, List<String>> createFacilitatedTrainingForAssignments(Map<String, List<VpsExternalTrainingInfo>> vpsExternalTrainingQueueItemsMap) {
        LogService logService = ServiceLocator.locate(LogService.class);
        RecordService recordService = ServiceLocator.locate(RecordService.class);
        VpsRecordHelperService vpsRecordHelperService = ServiceLocator.locate(VpsRecordHelperService.class);
        List<Record> recordToCreate = VaultCollections.newList();
        List<String> trainingAssigmentsInFTR = VaultCollections.newList();
        Map<List<String>, List<String>> adHocIdFTRMap = VaultCollections.newMap();
        logService.debug("Start createFacilitatedTrainingForAssignments");
        String adHocTrainingID = "";

        for (List<VpsExternalTrainingInfo> vpsExternalTrainingQueueItems : vpsExternalTrainingQueueItemsMap.values()) {
            for (VpsExternalTrainingInfo vpsExternalTrainingQueueItem : vpsExternalTrainingQueueItems) {
                //create Facilitated Training Requests
                Record ftrRecord = recordService.newRecord(FACILITATED_TRAINING_REQUEST_OBJ);
                String comments = vpsExternalTrainingQueueItem.getComments();
                logService.debug("comments: " + comments);
                ftrRecord.setValue(FTR_OBJFIELD_COMMENTS, comments);

                String reasonForCompletion = vpsExternalTrainingQueueItem.getReasonForCompletion();
                if (reasonForCompletion == null || reasonForCompletion.equalsIgnoreCase("")) {
                    reasonForCompletion = "brief__c";
                }
                ftrRecord.setValue(FTR_OBJFIELD_REASON_FOR_COMPLETION, VaultCollections.asList(reasonForCompletion));

                logService.debug("reasonForCompletion: " + reasonForCompletion);
                String dateOfCompletion = vpsExternalTrainingQueueItem.getCompletionDate();
                if (dateOfCompletion != null && !dateOfCompletion.equals("")) {
                    ftrRecord.setValue(FTR_OBJFIELD_DATE_OF_COMPLETION, LocalDate.parse(dateOfCompletion));
                } else {
                    ftrRecord.setValue(FTR_OBJFIELD_DATE_OF_COMPLETION, LocalDate.now());
                }
                logService.debug("dateOfCompletion: " + dateOfCompletion);
                String trainingReqID = vpsExternalTrainingQueueItem.getTrainingRequirementID();
                logService.debug("trainingReqID: " + trainingReqID);
                ftrRecord.setValue(FTR_OBJFIELD_TRAINING_REQUIREMENT, trainingReqID);
                recordToCreate.add(ftrRecord);
                //store to create FTR to TA record
                trainingAssigmentsInFTR.add(vpsExternalTrainingQueueItem.getTrainingAssignmentID());
                logService.debug("End createFacilitatedTrainingForAssignments");
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

//    /**
//     * @param
//     */
//    public String createFacilitatedTrainingForPersons(String trainingReqID, String dateOfCompletion, String reasonForCompletion) {
//        LogService logService = ServiceLocator.locate(LogService.class);
//        RecordService recordService = ServiceLocator.locate(RecordService.class);
//        VpsRecordHelperService vpsRecordHelperService = ServiceLocator.locate(VpsRecordHelperService.class);
//        List<Record> recordToCreate = VaultCollections.newList();
////        List<String> trainingAssigmentsInFTR = VaultCollections.newList();
////        Map<List<String>, List<String>> adHocIdFTRMap = VaultCollections.newMap();
//        List<String> newFacilitatedTrainingIDs = VaultCollections.newList();
//        logService.debug("Start createFacilitatedTrainingForAssignments");
//        String adHocTrainingID = "";
//        //create Facilitated Training Requests
//        Record ftrRecord = recordService.newRecord(FACILITATED_TRAINING_REQUEST_OBJ);
////        String comments = vpsExternalTrainingQueueItem.getComments();
////        logService.debug("comments: " + comments);
////        ftrRecord.setValue(FTR_OBJFIELD_COMMENTS, comments);
//
//        // String reasonForCompletion = vpsExternalTrainingQueueItem.getReasonForCompletion();
//        if (reasonForCompletion == null || reasonForCompletion.equalsIgnoreCase("")) {
//            reasonForCompletion = "brief__c";
//        }
//        ftrRecord.setValue(FTR_OBJFIELD_REASON_FOR_COMPLETION, VaultCollections.asList(reasonForCompletion));
//
//        logService.debug("reasonForCompletion: " + reasonForCompletion);
//        //String dateOfCompletion = vpsExternalTrainingQueueItem.getCompletionDate();
//        if (dateOfCompletion != null && !dateOfCompletion.equals("")) {
//            ftrRecord.setValue(FTR_OBJFIELD_DATE_OF_COMPLETION, LocalDate.parse(dateOfCompletion));
//        } else {
//            ftrRecord.setValue(FTR_OBJFIELD_DATE_OF_COMPLETION, LocalDate.now());
//        }
//        logService.debug("dateOfCompletion: " + dateOfCompletion);
//        // String trainingReqID = vpsExternalTrainingQueueItem.getTrainingRequirementID();
//        logService.debug("trainingReqID: " + trainingReqID);
//        ftrRecord.setValue(FTR_OBJFIELD_TRAINING_REQUIREMENT, trainingReqID);
//        recordToCreate.add(ftrRecord);
//        //store to create FTR to TA record
//        // trainingAssigmentsInFTR.add(vpsExternalTrainingQueueItem.getTrainingAssignmentID());
//        logService.debug("End createFacilitatedTrainingForAssignments");
////        for (List<VpsExternalTrainingModel> vpsExternalTrainingQueueItems : vpsExternalTrainingQueueItemsMap.values()) {
////            for (VpsExternalTrainingModel vpsExternalTrainingQueueItem : vpsExternalTrainingQueueItems) {
////                //create Facilitated Training Requests
////                Record ftrRecord = recordService.newRecord(FACILITATED_TRAINING_REQUEST_OBJ);
////                String comments = vpsExternalTrainingQueueItem.getComments();
////                logService.debug("comments: " + comments);
////                ftrRecord.setValue(FTR_OBJFIELD_COMMENTS, comments);
////
////                String reasonForCompletion = vpsExternalTrainingQueueItem.getReasonForCompletion();
////                if (reasonForCompletion == null || reasonForCompletion.equalsIgnoreCase("")) {
////                    reasonForCompletion = "brief__c";
////                }
////                ftrRecord.setValue(FTR_OBJFIELD_REASON_FOR_COMPLETION, VaultCollections.asList(reasonForCompletion));
////
////                logService.debug("reasonForCompletion: " + reasonForCompletion);
////                String dateOfCompletion = vpsExternalTrainingQueueItem.getCompletionDate();
////                if (dateOfCompletion != null && !dateOfCompletion.equals("")) {
////                    ftrRecord.setValue(FTR_OBJFIELD_DATE_OF_COMPLETION, LocalDate.parse(dateOfCompletion));
////                } else {
////                    ftrRecord.setValue(FTR_OBJFIELD_DATE_OF_COMPLETION, LocalDate.now());
////                }
////                logService.debug("dateOfCompletion: " + dateOfCompletion);
////                String trainingReqID = vpsExternalTrainingQueueItem.getTrainingRequirementID();
////                logService.debug("trainingReqID: " + trainingReqID);
////                ftrRecord.setValue(FTR_OBJFIELD_TRAINING_REQUIREMENT, trainingReqID);
////                recordToCreate.add(ftrRecord);
////                //store to create FTR to TA record
////                trainingAssigmentsInFTR.add(vpsExternalTrainingQueueItem.getTrainingAssignmentID());
////                logService.debug("End createFacilitatedTrainingForAssignments");
////            }
////        }
//        //create objects in bulk
//        if (!recordToCreate.isEmpty()) {
//            newFacilitatedTrainingIDs = vpsRecordHelperService.saveRecordsInBatchReturnIds(recordToCreate, "Failed to create FTR for TR:", true);
//            //adHocIdFTRMap.put(trainingAssigmentsInFTR, newFacilitatedTrainingIDs);
//        }
//        logService.debug("End createFacilitatedTrainingForAssignments");
//        return newFacilitatedTrainingIDs.get(0);
//    }

    /**
     * @param facilitatedTrainingIDsAsMap
     * @return
     */
    public List<String> addTrainingAssignmentsInFacilitiatedTraining(Map<List<String>, List<String>> facilitatedTrainingIDsAsMap) {
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
                        Record ftrTARecord = recordService.newRecord(FACILITATED_REQUEST_TRAINING_ASSIGNMENT_OBJ);
                        ftrTARecord.setValue(FACILITATED_TRAINING_REQUEST_OBJ, newFacilitatedTrainIDs.get(i));
                        ftrTARecord.setValue(TRAINING_ASSIGNMENT_OBJ, trainingAssignments.get(i));
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
    public void completeFacilitatedTrainingRequest(Map<List<String>, List<String>> facilitatedTrainingIDsAsMap, String actionName) {
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
                Record ftrRecord = recordService.newRecordWithId(FACILITATED_TRAINING_REQUEST_OBJ, FTRID);
                recordToPerformAction.add(ftrRecord);
            }
            if (!recordToPerformAction.isEmpty()) {
                jobHelperService.callUserActions(recordToPerformAction, actionName);
            }
        }
        logService.debug("End callUserAction");
    }



}