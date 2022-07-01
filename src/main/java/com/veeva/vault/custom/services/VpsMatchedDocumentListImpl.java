/*
 * --------------------------------------------------------------------
 * UserDefinedService:	VpsMatchedDocumentList
 * Author:				paulkwitkin @ Veeva
 * Date:				2020-10-02
 *---------------------------------------------------------------------
 * Description:
 *---------------------------------------------------------------------
 * Copyright (c) 2020 Veeva Systems Inc.  All Rights Reserved.
 *		This code is based on pre-existing content developed and
 * 		owned by Veeva Systems Inc. and may only be used in connection
 *		with the deliverable with which it was provided to Customer.
 *---------------------------------------------------------------------
 */
package com.veeva.vault.custom.services;

import com.veeva.vault.custom.model.VpsMatchedDocument;
import com.veeva.vault.custom.model.VpsVQLRequest;
import com.veeva.vault.custom.model.VpsVQLResponse;
import com.veeva.vault.sdk.api.core.*;
import com.veeva.vault.sdk.api.json.JsonArray;
import com.veeva.vault.sdk.api.json.JsonObject;
import com.veeva.vault.sdk.api.json.JsonValueType;

import java.util.List;

@UserDefinedServiceInfo
public class VpsMatchedDocumentListImpl implements VpsMatchedDocumentListService {

	private static final String CONNECTION = "connection_api_name_here";
	private static final String APIVER = "put api ver here ex. v21.1";
	private static final String OBJFIELD_EDL_ITEM_EDL = "edl_item__vr.edl__v";
	private static final String OBJFIELD_EDL_ITEM_EXCHANGE_ACTION = "edl_item__vr.exchange_action__c";
	private static final String OBJFIELD_EDL_ITEM_ID = "edl_item__vr.id";
	private static final String OBJFIELD_EDL_ITEM_LEVEL = "edl_item__vr.level__v";
	private static final String OBJFIELD_EDL_ITEM_SITE = "edl_item__vr.site__v";
	private static final String OBJFIELD_EDL_ITEM_STUDY = "edl_item__vr.study__v";
	private static final String OBJFIELD_EDL_ITEM_STUDY_COUNTRY = "edl_item__vr.study_country__v";
	private static final String DOCFIELD_SOURCE_APP_NAME = "matching_documents__vr.source_app_name__c";
	private static final String DOCFIELD_EXTERNAL_ID = "matching_documents__vr.external_id__v";
	private static final String DOCFIELD_ID = "matching_documents__vr.id";
	private static final String DOCFIELD_LEVEL = "matching_documents__vr.level__v";
	private static final String DOCFIELD_MAJOR_VERSION = "matching_documents__vr.major_version_number__v";
	private static final String DOCFIELD_MINOR_VERSION = "matching_documents__vr.minor_version_number__v";
	private static final String DOCFIELD_QC_ISSUE_COMMENTS = "matching_documents__vr.qc_issue_comments__vs";
	private static final String DOCFIELD_QC_ISSUE_TYPE = "matching_documents__vr.qc_issue_type__vs";
	private static final String DOCFIELD_VERSION_ID = "matching_documents__vr.version_id";
	private static final String OBJECT_MATCHED_DOCUMENTS = "matched_documents";


	private List<VpsMatchedDocument> items = VaultCollections.newList();
	private List<String> errorList = VaultCollections.newList();
	public List<VpsMatchedDocument> getItems() {
		return  items;
	}

	public void load(String whereClause) {
		items.clear();
		VpsVQLRequest vqlRequest = new VpsVQLRequest();
		vqlRequest.appendVQL("select " + OBJFIELD_EDL_ITEM_ID);
		vqlRequest.appendVQL(", " + OBJFIELD_EDL_ITEM_EDL);
		vqlRequest.appendVQL(", " + OBJFIELD_EDL_ITEM_EXCHANGE_ACTION);
		vqlRequest.appendVQL(", " + OBJFIELD_EDL_ITEM_EXCHANGE_ACTION);
		vqlRequest.appendVQL(", " + OBJFIELD_EDL_ITEM_LEVEL);
		vqlRequest.appendVQL(", " + OBJFIELD_EDL_ITEM_STUDY);
		vqlRequest.appendVQL(", " + OBJFIELD_EDL_ITEM_STUDY_COUNTRY);
		vqlRequest.appendVQL(", " + OBJFIELD_EDL_ITEM_SITE);
		vqlRequest.appendVQL(", " + DOCFIELD_ID);
		vqlRequest.appendVQL(", " + DOCFIELD_EXTERNAL_ID);
		vqlRequest.appendVQL(", " + DOCFIELD_LEVEL);
		vqlRequest.appendVQL(", " + DOCFIELD_MAJOR_VERSION);
		vqlRequest.appendVQL(", " + DOCFIELD_MINOR_VERSION);
		vqlRequest.appendVQL(", " + DOCFIELD_QC_ISSUE_COMMENTS);
		vqlRequest.appendVQL(", " + DOCFIELD_QC_ISSUE_TYPE);
		vqlRequest.appendVQL(", " + DOCFIELD_SOURCE_APP_NAME);
		vqlRequest.appendVQL(", " + DOCFIELD_VERSION_ID);
		vqlRequest.appendVQL(" from " + OBJECT_MATCHED_DOCUMENTS);
		vqlRequest.appendVQL(whereClause);
		VpsAPIClientService apiClient = ServiceLocator.locate(VpsAPIClientService.class);

		VpsVQLResponse vqlResponse = apiClient.runVQL(vqlRequest, APIVER, CONNECTION);

		if (vqlResponse != null) {
			JsonArray data = vqlResponse.getData();
			if (data != null) {
				for (int i = 0; i < data.getSize(); i++) {
					JsonObject matchedDocumentJson = data.getValue(i, JsonValueType.OBJECT);
					VpsMatchedDocument matchedDocument = new VpsMatchedDocument();
					matchedDocument.setEdlItemEdl(matchedDocumentJson.getValue(OBJFIELD_EDL_ITEM_EDL, JsonValueType.STRING));
					matchedDocument.setEdlItemExchangeAction(matchedDocumentJson.getValue(OBJFIELD_EDL_ITEM_EXCHANGE_ACTION, JsonValueType.ARRAY));
					matchedDocument.setEdlItemId(matchedDocumentJson.getValue(OBJFIELD_EDL_ITEM_ID, JsonValueType.STRING));
					matchedDocument.setEdlItemLevel(matchedDocumentJson.getValue(OBJFIELD_EDL_ITEM_LEVEL, JsonValueType.ARRAY));
					matchedDocument.setEdlItemStudy(matchedDocumentJson.getValue(OBJFIELD_EDL_ITEM_STUDY, JsonValueType.STRING));
					matchedDocument.setEdlItemStudyCountry(matchedDocumentJson.getValue(OBJFIELD_EDL_ITEM_STUDY_COUNTRY, JsonValueType.STRING));
					matchedDocument.setEdlItemSite(matchedDocumentJson.getValue(OBJFIELD_EDL_ITEM_SITE, JsonValueType.STRING));
					matchedDocument.setDocumentExternalId(matchedDocumentJson.getValue(DOCFIELD_EXTERNAL_ID, JsonValueType.STRING));
					matchedDocument.setDocumentLevel(matchedDocumentJson.getValue(DOCFIELD_LEVEL, JsonValueType.ARRAY));
					matchedDocument.setDocumentId(matchedDocumentJson.getValue(DOCFIELD_ID, JsonValueType.NUMBER));
					matchedDocument.setDocumentMajorVersion(matchedDocumentJson.getValue(DOCFIELD_MAJOR_VERSION, JsonValueType.NUMBER));
					matchedDocument.setDocumentMinorVersion(matchedDocumentJson.getValue(DOCFIELD_MINOR_VERSION, JsonValueType.NUMBER));
					matchedDocument.setDocumentQCIssueComments(matchedDocumentJson.getValue(DOCFIELD_QC_ISSUE_COMMENTS, JsonValueType.STRING));
					matchedDocument.setDocumentQCIssueType(matchedDocumentJson.getValue(DOCFIELD_QC_ISSUE_TYPE, JsonValueType.ARRAY));
					matchedDocument.setDocumentSourceAppName(matchedDocumentJson.getValue(DOCFIELD_SOURCE_APP_NAME, JsonValueType.ARRAY));
					matchedDocument.setDocumentVersionId(matchedDocumentJson.getValue(DOCFIELD_VERSION_ID, JsonValueType.STRING));
					items.add(matchedDocument);
				}
			}
		}
	}

	public void loadFromDocumentVersion(String versionId) {
		loadFromDocumentVersion(VaultCollections.asList(versionId));
	}

	public void loadFromDocumentVersion(List<String> versionIds) {
		StringBuilder whereClause = new StringBuilder();
		whereClause.append(" where " + DOCFIELD_VERSION_ID + " contains (");
		whereClause.append(VpsUtilHelperService.listToString(versionIds,",",true));
		whereClause.append(")");
		this.load(whereClause.toString());
	}

	public void loadFromEdlItemList(String edlItemtId, Integer minimumMajorVersion) {
		loadFromEdlItemList(VaultCollections.asList(edlItemtId), minimumMajorVersion);
	}

	public void loadFromEdlItemList(List<String> edlItemtIds, Integer minimumMajorVersion) {
		StringBuilder whereClause = new StringBuilder();
		whereClause.append(" where " + OBJFIELD_EDL_ITEM_ID + " contains (");
		whereClause.append(VpsUtilHelperService.listToString(edlItemtIds,",",true));
		whereClause.append(")");
		whereClause.append(" and " + DOCFIELD_MAJOR_VERSION + " >=" + minimumMajorVersion);
		this.load(whereClause.toString());
	}

	public List<String> getErrorList() {
		return errorList;
	}
}