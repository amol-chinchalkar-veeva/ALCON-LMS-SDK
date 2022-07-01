/*
 * --------------------------------------------------------------------
 * UserDefinedClass:	VpsExternalTrainingFTRModel
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
package com.veeva.vault.custom.model;

import com.veeva.vault.sdk.api.core.*;
import com.veeva.vault.sdk.api.data.*;

@UserDefinedModelInfo(include = UserDefinedPropertyInclude.NON_NULL)
public interface VpsExternalTrainingQueueItem extends UserDefinedModel {

    @UserDefinedProperty(name = "name__v", aliases = {"name", "name__c"})
    String getName();

    void setName(String name);

    @UserDefinedProperty(name = "comments__c")
    String getComments();

    void setComments(String comments);

    @UserDefinedProperty(name = "completion_date__c")
    String getCompletionDate();

    void setCompletionDate(String completionDate);

    @UserDefinedProperty(name = "date_of_training__c")
    String getDateOfTraining();

    void setDateOfTraining(String dateOfTraining);

    @UserDefinedProperty(name = "instructor__c")
    String getInstructor();

    void setInstructor(String instructor);

    @UserDefinedProperty(name = "reason_for_completion__c")
    String getReasonForCompletion();

    void setReasonForCompletion(String reasonForCompletion);

    @UserDefinedProperty(name = "training_assignment__cr.id")
    String getTrainingAssignmentID();

    void setTrainingAssignmentID(String trainingAssignmentID);

    @UserDefinedProperty(name = "training_assignment__cr.training_requirement__v")
    String getTrainingRequirementID();

    void setTrainingRequirementID(String trainingRequirementID);

    @UserDefinedProperty(name = "training_method__c")
    String getTrainingMethod();

    void setTrainingMethod(String trainingMethod);
//
//    @UserDefinedProperty(name = "queue_item_status__c")
//    String getQueueItemStatus();
//    void setQueueItemStatus(String queueItemStatus);

    @UserDefinedProperty(name = "id")
    String getId();

    void setId(String id);

    @UserDefinedProperty(name = "ta_comments__c")
    String getTrainingAssignmentComments();

    void setTrainingAssignmentComments(String trainingAssignmentComments);
}