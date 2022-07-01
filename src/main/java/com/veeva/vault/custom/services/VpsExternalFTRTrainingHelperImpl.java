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

import com.veeva.vault.custom.model.VpsExternalTrainingQueueItem;
import com.veeva.vault.sdk.api.core.*;
import com.veeva.vault.sdk.api.data.ObjectField;
import com.veeva.vault.sdk.api.data.Record;
import com.veeva.vault.sdk.api.data.RecordService;
import com.veeva.vault.sdk.api.query.QueryResponse;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.veeva.vault.custom.services.VpsVQLServiceImpl.*;

@UserDefinedServiceInfo
public class VpsExternalFTRTrainingHelperImpl implements VpsExternalFTRTrainingHelperService {
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
    static final String TA_OBJFIELD_TA_STATE="training_assignment__cr.state__v";
    static final String AD_HOC_TRAINING_TA_OBJ = "ad_hoc_trainingta__c";
    private static final String OBJFIELD_COMMENTS = "comments__c";
    private static final String OBJFIELD_CURRICULUM = "curriculum__v";
    private static final String OBJFIELD_DATE_OF_COMPLETION = "date_of_completion__c";
    private static final String OBJFIELD_REASON = "reason__v";
    private static final String OBJFIELD_REASON_FOR_COMPLETION = "reason_for_completion__c";
    private static final String OBJFIELD_TRAINING_REQUIREMENT = "training_requirement__v";

    @Override
    public List<VpsExternalTrainingQueueItem> getExternalTrainingQueueItems() {
        VpsVQLService vpsVQLService = ServiceLocator.locate(VpsVQLService.class);
        UserDefinedModelService modelService = ServiceLocator.locate(UserDefinedModelService.class);
        LogService logService = ServiceLocator.locate(LogService.class);

        logService.debug("Start getExternalTrainingQueueItems");
        List<VpsExternalTrainingQueueItem> externalFTRQueueItems = VaultCollections.newList();
        QueryResponse queryResponse = vpsVQLService.query(
                SELECT, vpsVQLService.appendFields(TA_OBJFIELD_COMMENTS,TA_OBJFIELD_TA_STATE, AD_HOC_OBJ_AD_HOC_TRAINING, AD_HOC_OBJFIELD_TRAINING_ASSIGNMENT_ID, TA_OBJFIELD_TRAINING_REQUIREMENT_ID, AD_HOC_OBJFIELD_COMMENTS,
                        AD_HOC_OBJFIELD_COMPLETION_DATE, AD_HOC_OBJFIELD_DATE_OF_TRAINING, AD_HOC_OBJFIELD_INSTRUCTOR, AD_HOC_OBJFIELD_REASON_FOR_COMPLETION, AD_HOC_OBJFIELD_STATUS, AD_HOC_OBJFIELD_TRAINING_METHOD),
                FROM, AD_HOC_TRAINING_TA_OBJ,
                WHERE, AD_HOC_OBJ_AD_HOC_TRAINING + " = 'V9V000000001001'", AND ,TA_OBJFIELD_TA_STATE +"='assigned_state__v'",
                ORDER_BY, AD_HOC_OBJ_AD_HOC_TRAINING
        );
//        logService.info( SELECT, vpsVQLService.appendFields(TA_OBJFIELD_COMMENTS,TA_OBJFIELD_TA_STATE, AD_HOC_OBJ_AD_HOC_TRAINING, AD_HOC_OBJFIELD_TRAINING_ASSIGNMENT_ID, TA_OBJFIELD_TRAINING_REQUIREMENT_ID, AD_HOC_OBJFIELD_COMMENTS,
//                        AD_HOC_OBJFIELD_COMPLETION_DATE, AD_HOC_OBJFIELD_DATE_OF_TRAINING, AD_HOC_OBJFIELD_INSTRUCTOR, AD_HOC_OBJFIELD_REASON_FOR_COMPLETION, AD_HOC_OBJFIELD_STATUS, AD_HOC_OBJFIELD_TRAINING_METHOD),
//                FROM, AD_HOC_TRAINING_TA_OBJ,
//                WHERE, AD_HOC_OBJ_AD_HOC_TRAINING + " = 'V9V000000001001'", AND ,TA_OBJFIELD_TA_STATE +"='assigned_state__v'",
//                ORDER_BY, AD_HOC_OBJ_AD_HOC_TRAINING);
        queryResponse.streamResults().forEach(queryResult -> {
            VpsExternalTrainingQueueItem vpsExternalFTRQueueItem = modelService.newUserDefinedModel(VpsExternalTrainingQueueItem.class);
            String comments = queryResult.getValue(AD_HOC_OBJFIELD_COMMENTS, ValueType.STRING);
            vpsExternalFTRQueueItem.setComments(comments);
            String trainingAssignmentComment = queryResult.getValue(TA_OBJFIELD_COMMENTS, ValueType.STRING);
            vpsExternalFTRQueueItem.setTrainingAssignmentComments(trainingAssignmentComment);

            LocalDate dateOfTraining = queryResult.getValue(AD_HOC_OBJFIELD_DATE_OF_TRAINING, ValueType.DATE);
            if(dateOfTraining!=null ) {
                vpsExternalFTRQueueItem.setDateOfTraining(dateOfTraining.toString());
            }

            LocalDate completionDate = queryResult.getValue(AD_HOC_OBJFIELD_COMPLETION_DATE, ValueType.DATE);
            if(completionDate!=null) {
                vpsExternalFTRQueueItem.setCompletionDate(completionDate.toString());
            }
            String instructor = queryResult.getValue(AD_HOC_OBJFIELD_INSTRUCTOR, ValueType.STRING);
            vpsExternalFTRQueueItem.setInstructor(instructor);
            List<String> trainingMethod = queryResult.getValue(AD_HOC_OBJFIELD_TRAINING_METHOD, ValueType.REFERENCES);
            if(trainingMethod!=null && !trainingMethod.isEmpty()) {
                vpsExternalFTRQueueItem.setTrainingMethod(trainingMethod.get(0));
            }
            String trainingAssignmentID = queryResult.getValue(AD_HOC_OBJFIELD_TRAINING_ASSIGNMENT_ID, ValueType.STRING);
            vpsExternalFTRQueueItem.setTrainingAssignmentID(trainingAssignmentID);
            logService.debug("trainingAssignmentID:" + trainingAssignmentID);
            String trainingRequirementID = queryResult.getValue(TA_OBJFIELD_TRAINING_REQUIREMENT_ID, ValueType.STRING);
            vpsExternalFTRQueueItem.setTrainingRequirementID(trainingRequirementID);
            logService.debug("TrainingRequirementID:" + trainingRequirementID);
            externalFTRQueueItems.add(vpsExternalFTRQueueItem);
        });
        logService.debug("End getExternalTrainingQueueItems");
        return externalFTRQueueItems;
    }

    public void createFTRFromQueueItems(VpsExternalTrainingQueueItem vpsExternalTrainingQueueItem) {
        LogService logService = ServiceLocator.locate(LogService.class);
        RecordService recordService = ServiceLocator.locate(RecordService.class);
        VpsRecordHelperService vpsRecordHelperService = ServiceLocator.locate(VpsRecordHelperService.class);
        List<Record> recordToCreate =  VaultCollections.newList();
        logService.debug("Start createFTRFromQueueItems");
        Record ftrRecord = recordService.newRecord("facilitated_training_request__v");
        String comments = vpsExternalTrainingQueueItem.getComments();
        logService.debug("comments: " + comments);
        ftrRecord.setValue(OBJFIELD_COMMENTS,comments);
        String reasonForCompletion = vpsExternalTrainingQueueItem.getReasonForCompletion();

        if(reasonForCompletion.equalsIgnoreCase("")){
            reasonForCompletion= "brief__c";
        }
        ftrRecord.setValue(OBJFIELD_REASON_FOR_COMPLETION,VaultCollections.asList(reasonForCompletion));

        logService.debug("reasonForCompletion: " + reasonForCompletion);
        String dateOfCompletion = vpsExternalTrainingQueueItem.getCompletionDate();
        if(dateOfCompletion!=null && !dateOfCompletion.equals("")) {
            ftrRecord.setValue(OBJFIELD_DATE_OF_COMPLETION, LocalDate.parse(dateOfCompletion));
        }else{
            ftrRecord.setValue(OBJFIELD_DATE_OF_COMPLETION, LocalDate.now());
        }
        logService.debug("dateOfCompletion: " + dateOfCompletion);
        String trainingReqID = vpsExternalTrainingQueueItem.getTrainingRequirementID();
        logService.debug("trainingReqID: " + trainingReqID);
        ftrRecord.setValue(OBJFIELD_TRAINING_REQUIREMENT,trainingReqID);
        recordToCreate.add(ftrRecord);
        List<String> newFTRRecords= vpsRecordHelperService.saveRecordsInBatchReturnIds(recordToCreate, "Failed to create FTR for TR: " + trainingReqID, true);

        logService.debug("newFTRRecords.get(0): " + newFTRRecords.get(0));
        //  setDataQueueItemToComplete(vpsExternalTrainingQueueItem);
        logService.debug("End createFTRFromQueueItems");
    }
}