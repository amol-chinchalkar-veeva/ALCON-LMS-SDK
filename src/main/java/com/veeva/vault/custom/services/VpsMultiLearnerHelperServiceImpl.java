/*
 * --------------------------------------------------------------------
 * UserDefinedService:	VpsMultiLearnerHelperServiceImpl
 * Author:				Amol Chinchalkar @ Veeva
 * Date:				2022-07-19
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

import com.veeva.vault.custom.classes.VpsUtilHelper;
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
public class VpsMultiLearnerHelperServiceImpl implements VpsMultiLearnerHelperService {
    static final String TRAINING_ASSIGNMENT_OBJ = "training_assignment__v";
    static final String FACILITATED_REQUEST_TRAINING_ASSIGNMENT_OBJ = "facilitated_request_training_assignment__v";
    static final String FACILITATED_TRAINING_REQUEST_OBJ = "facilitated_training_request__v";
//    private static final String FTR_OBJFIELD_COMMENTS = "comments__c";
    private static final String FTR_OBJFIELD_DATE_OF_COMPLETION = "date_of_completion__c";
    private static final String FTR_OBJFIELD_REASON_FOR_COMPLETION = "reason_for_completion__c";
    private static final String FTR_OBJFIELD_TRAINING_REQUIREMENT = "training_requirement__v";
    static final String AD_HOC_TRAINING_PERSON_OBJ = "ad_hoc_trainingperson__c";
//    private static final String AD_HOC_TRAINING_PERSON_OBJFIELD_COMMENTS = "comments__c";
//    private static final String AD_HOC_TRAINING_PERSON_OBJFIELD_DATE_OF_COMPLETION = "date_of_completion__c";
//    private static final String AD_HOC_TRAINING_PERSON_OBJFIELD_REASON_FOR_COMPLETION = "reason_for_completion__c";
//    private static final String AD_HOC_TRAINING_PERSON_OBJFIELD_TRAINING_REQUIREMENT = "training_requirement__v";
//    private static final String AD_HOC_TRAINING_PERSON_OBJFIELD_DATE_OF_TRAINING = "date_of_training__c";
//    // private static final String AD_HOC_TRAINING_PERSON_OBJFIELD_TRAINING_REQUIREMENT_ID = "training_assignment__cr.training_requirement__v";
    private static final String AD_HOC_TRAINING_PERSON_OBJFIELD_PERSON_ID = "person__c";
    private static final String AD_HOC_TRAINING_PERSON_OBJFIELD_PERSON_FEDERATED_ID = "person__cr.federated_id__c";
//    private static final String AD_HOC_TRAINING_PERSON_OBJFIELD_PERSON_NAME = "person__cr.name__v";
    private static final String AD_HOC_TRAINING_PERSON_OBJFIELD_AD_HOC_TRAINING = "ad_hoc_training__c";
//    private static final String AD_HOC_TRAINING_PERSON_OBJFIELD_INSTRUCTOR = "instructor__c";
//    private static final String AD_HOC_TRAINING_PERSON_OBJFIELD_TRAINING_METHOD = "training_method__c";
//    private static final String AD_HOC_TRAINING_PERSON_OBJFIELD_TRAINING_ASSIGNMENT_ID = "ad_hoc_training__cr.training_assignment__c";
//    private static final String AD_HOC_TRAINING_PERSON_OBJFIELD_TRAINING_REQUIREMENT_ID = "training_requirement__c";


    /**
     * @param learnersList
     * @param trainingReq
     * @return
     */
    public Map<String, String> getValidLearnersForAssignment(List<String> learnersList, String trainingReq) {
        VpsVQLService vpsVQLService = ServiceLocator.locate(VpsVQLService.class);

        LogService logService = ServiceLocator.locate(LogService.class);
        Map<String, List<VpsExternalTrainingInfo>> multiplePersonMap = VaultCollections.newMap();
        logService.debug("****** Start getExternalTrainingAssignmentModels ******");
        Map<String, String> validLearnersMap = VaultCollections.newMap();
        //select id, learner__v,learner__vr.name__v,learner__vr.federated_id__c from training_assignment__v where training_requirement__v='V160000000OL519' and learner__v='V0C000000000D46'
        QueryResponse queryResponse = vpsVQLService.query(
                SELECT, vpsVQLService.appendFields("id, learner__v"),
                FROM, "training_assignment__v",
                WHERE, "training_requirement__v" + " = '" + trainingReq + "' and learner__v contains(" + VpsUtilHelper.listToString(learnersList, ",", true) + ")"
        );
        queryResponse.streamResults().forEach(queryResult -> {
            String trainingAssignmentID = queryResult.getValue("id", ValueType.STRING);
            List<String> federatedID = queryResult.getValue("learner__v", ValueType.REFERENCES);

            if (!federatedID.isEmpty()) {
                logService.debug("Learner's Federated ID:{}", federatedID);
                logService.debug("Learner's Training Assignment ID:{}", trainingAssignmentID);
                validLearnersMap.put(federatedID.get(0), trainingAssignmentID);
            }
        });
        logService.debug("****** End getValidLearnersForAssignment ******");
        return validLearnersMap;
    }

    /**
     * @param trainingReqID
     * @param dateOfCompletion
     * @param reasonForCompletion
     * @return
     */
    public String createFacilitatedTrainingForLearners(String trainingReqID, String dateOfCompletion, String reasonForCompletion) {
        LogService logService = ServiceLocator.locate(LogService.class);
        RecordService recordService = ServiceLocator.locate(RecordService.class);
        VpsRecordHelperService vpsRecordHelperService = ServiceLocator.locate(VpsRecordHelperService.class);
        List<Record> recordToCreate = VaultCollections.newList();

        List<String> newFacilitatedTrainingIDs = VaultCollections.newList();
        logService.debug("****** Start createFacilitatedTrainingForLearners ******");

        Record newFacilitatedRequest = recordService.newRecord(FACILITATED_TRAINING_REQUEST_OBJ);
        newFacilitatedRequest.setValue(FTR_OBJFIELD_REASON_FOR_COMPLETION, VaultCollections.asList(reasonForCompletion));
        logService.debug("Reason For Completion:{}", reasonForCompletion);
        if (dateOfCompletion != null && !dateOfCompletion.equals("")) {
            newFacilitatedRequest.setValue(FTR_OBJFIELD_DATE_OF_COMPLETION, LocalDate.parse(dateOfCompletion));
        }
        logService.debug("Date Of Completion:{}", dateOfCompletion);
        logService.debug("Training Requirement ID:{}", trainingReqID);
        newFacilitatedRequest.setValue(FTR_OBJFIELD_TRAINING_REQUIREMENT, trainingReqID);
        recordToCreate.add(newFacilitatedRequest);
        //create objects in bulk
        if (!recordToCreate.isEmpty()) {
            newFacilitatedTrainingIDs = vpsRecordHelperService.saveRecordsInBatchReturnIds(recordToCreate, "Failed to create Facilitated Training Request Object:", true);
            //adHocIdFTRMap.put(trainingAssigmentsInFTR, newFacilitatedTrainingIDs);
        }
        logService.debug("****** End createFacilitatedTrainingForLearners ******");
        return newFacilitatedTrainingIDs.get(0);
    }


    /**
     * @param trainingAssignments
     * @param newFacilitatedRequestID
     * @return
     */
    public List<String> addTrainingAssignmentsInFacilitiatedTraining(Map<String, String> trainingAssignments, String newFacilitatedRequestID) {
        RecordService recordService = ServiceLocator.locate(RecordService.class);
        VpsRecordHelperService vpsRecordHelperService = ServiceLocator.locate(VpsRecordHelperService.class);
        LogService logService = ServiceLocator.locate(LogService.class);
        List<Record> recordToCreate = VaultCollections.newList();
        List<String> facilitatedTrainingAndAssignments = VaultCollections.newList();

        logService.debug("****** Start addTrainingAssignmentsForLearners ******");
        for (String learnerFederatedID : trainingAssignments.keySet()) {
            for (String trainingAssignment : trainingAssignments.values()) {
                logService.debug("Facilitated Training IDs:{}", newFacilitatedRequestID);
                logService.debug("Training Assignments:{}", trainingAssignment);
                logService.debug("Learner Federated Id:{}", trainingAssignment);
                Record ftrTARecord = recordService.newRecord(FACILITATED_REQUEST_TRAINING_ASSIGNMENT_OBJ);
                ftrTARecord.setValue(FACILITATED_TRAINING_REQUEST_OBJ, newFacilitatedRequestID);
                ftrTARecord.setValue(TRAINING_ASSIGNMENT_OBJ, trainingAssignment);
                recordToCreate.add(ftrTARecord);

            }
        }
        if (!recordToCreate.isEmpty()) {
            facilitatedTrainingAndAssignments = vpsRecordHelperService.saveRecordsInBatchReturnIds(recordToCreate, "Failed to create Facilitated Training and Assignment Object", true);
        }
        logService.debug(" ****** End addTrainingAssignmentsForLearners ******");
        return facilitatedTrainingAndAssignments;
    }

    /**
     * @param
     * @return
     */
    public void completeFacilitatedTrainingRequest(String facilitatedTrainingID, String actionName) {
        RecordService recordService = ServiceLocator.locate(RecordService.class);
        LogService logService = ServiceLocator.locate(LogService.class);
        logService.debug("****** Start completeFacilitatedTraining ******");
        List<Record> recordToPerformAction = VaultCollections.newList();

        VpsJobHelperService jobHelperService = ServiceLocator.locate(VpsJobHelperService.class);
        Record ftrRecord = recordService.newRecordWithId(FACILITATED_TRAINING_REQUEST_OBJ, facilitatedTrainingID);
        recordToPerformAction.add(ftrRecord);

        if (!recordToPerformAction.isEmpty()) {
            jobHelperService.callUserActions(recordToPerformAction, actionName);
        }

        logService.debug("****** End completeFacilitatedTraining ******");
    }

    /**
     * @param adHocID
     * @return
     */
    public List<String> getLearnerInfo(String adHocID) {
        VpsVQLService vpsVQLService = ServiceLocator.locate(VpsVQLService.class);
        UserDefinedModelService modelService = ServiceLocator.locate(UserDefinedModelService.class);
        LogService logService = ServiceLocator.locate(LogService.class);
        logService.debug("****** Start getLearnerInfo ******");
        List<String> learnerInfo = VaultCollections.newList();

        QueryResponse queryResponse = vpsVQLService.query(
                SELECT, vpsVQLService.appendFields(AD_HOC_TRAINING_PERSON_OBJFIELD_PERSON_FEDERATED_ID, AD_HOC_TRAINING_PERSON_OBJFIELD_PERSON_ID),
                FROM, AD_HOC_TRAINING_PERSON_OBJ,
                WHERE, AD_HOC_TRAINING_PERSON_OBJFIELD_AD_HOC_TRAINING + " = '" + adHocID + "'"

        );
        queryResponse.streamResults().forEach(queryResult -> {
            String federatedID = queryResult.getValue(AD_HOC_TRAINING_PERSON_OBJFIELD_PERSON_FEDERATED_ID, ValueType.STRING);
            learnerInfo.add(federatedID);
            logService.debug("Person Federated ID:" + federatedID);
        });
        logService.debug("****** End getLearnerInfo ******");
        return learnerInfo;
    }
}