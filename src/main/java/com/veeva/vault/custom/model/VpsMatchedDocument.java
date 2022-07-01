/*
 * --------------------------------------------------------------------
 * UDC:         VpsMatchedDocument
 * Author:      markarnold @ Veeva
 * Date:        2019-07-25
 *---------------------------------------------------------------------
 * Description: Matched Document
 *---------------------------------------------------------------------
 * Copyright (c) 2019 Veeva Systems Inc.  All Rights Reserved.
 *      This code is based on pre-existing content developed and
 *      owned by Veeva Systems Inc. and may only be used in connection
 *      with the deliverable with which it was provided to Customer.
 *---------------------------------------------------------------------
 */
package com.veeva.vault.custom.model;

import com.veeva.vault.sdk.api.core.UserDefinedClassInfo;
import com.veeva.vault.sdk.api.core.VaultCollections;
import com.veeva.vault.sdk.api.json.JsonArray;
import com.veeva.vault.sdk.api.json.JsonValueType;

import java.math.BigDecimal;
import java.util.List;

@UserDefinedClassInfo()
public class VpsMatchedDocument {

	String documentExternalId;
	BigDecimal documentId;
	List<String> documentLevel = VaultCollections.newList();
	BigDecimal documentMajorVersion;
	BigDecimal documentMinorVersion;
	String documentQCIssueComments;
	List<String> documentQCIssueType = VaultCollections.newList();
	List<String> documentSourceAppName = VaultCollections.newList();
	String documentVersionId;
	String edlItemEdl;
	List<String> edlItemExchangeAction = VaultCollections.newList();
	String edlItemId;
	List<String> edlItemLevel = VaultCollections.newList();
	String edlItemSite;
	String edlItemStudy;
	String edlItemStudyCountry;


	public VpsMatchedDocument() {
		super();
	}

	public String getDocumentExternalId() {
		return documentExternalId;
	}

	public void setDocumentExternalId(String documentExternalId) {
		this.documentExternalId = documentExternalId;
	}

	public BigDecimal getDocumentId() {
		return documentId;
	}

	public void setDocumentId(BigDecimal documentId) {
		this.documentId = documentId;
	}

	public List<String> getDocumentLevel() {
		return documentLevel;
	}

	public void setDocumentLevel(List<String> documentLevel) {
		this.documentLevel = documentLevel;
	}

	public void setDocumentLevel(JsonArray documentLevel) {
		this.documentLevel.clear();
		if (documentLevel != null) {
			for (int i=0; i < documentLevel.getSize();i++) {
				String itemValue = documentLevel.getValue(i, JsonValueType.STRING);
				this.documentLevel.add(itemValue);
			}
		}
	}

	public BigDecimal getDocumentMajorVersion() {
		return documentMajorVersion;
	}

	public void setDocumentMajorVersion(BigDecimal documentMajorVersion) {
		this.documentMajorVersion = documentMajorVersion;
	}

	public BigDecimal getDocumentMinorVersion() {
		return documentMinorVersion;
	}

	public void setDocumentMinorVersion(BigDecimal documentMinorVersion) {
		this.documentMinorVersion = documentMinorVersion;
	}

	public String getDocumentQCIssueComments() {
		return documentQCIssueComments;
	}

	public void setDocumentQCIssueComments(String documentQCIssueComments) {
		this.documentQCIssueComments = documentQCIssueComments;
	}

	public List<String> getDocumentQCIssueType() {
		return documentQCIssueType;
	}

	public void setDocumentQCIssueType(List<String> documentQCIssueType) {
		this.documentQCIssueType = documentQCIssueType;
	}

	public void setDocumentQCIssueType(JsonArray documentQCIssueType) {
		this.documentQCIssueType.clear();
		if (documentQCIssueType != null) {
			for (int i=0; i < documentQCIssueType.getSize();i++) {
				String itemValue = documentQCIssueType.getValue(i, JsonValueType.STRING);
				this.documentQCIssueType.add(itemValue);
			}
		}
	}

	public List<String> getDocumentSourceAppName() {
		return documentSourceAppName;
	}

	public void setDocumentSourceAppName(List<String> documentSourceAppName) {
		this.documentSourceAppName = documentQCIssueType;
	}

	public void setDocumentSourceAppName(JsonArray documentSourceAppName) {
		this.documentSourceAppName.clear();
		if (documentSourceAppName != null) {
			for (int i=0; i < documentSourceAppName.getSize();i++) {
				String itemValue = documentSourceAppName.getValue(i, JsonValueType.STRING);
				this.documentSourceAppName.add(itemValue);
			}
		}
	}

	public String getDocumentVersionId() {
		return documentVersionId;
	}

	public void setDocumentVersionId(String documentVersionId) {
		this.documentVersionId = documentVersionId;
	}

	public String getEdlItemEdl() {
		return edlItemEdl;
	}

	public void setEdlItemEdl(String edlItemEdl) {
		this.edlItemEdl = edlItemEdl;
	}

	public List<String> getEdlItemExchangeAction() {
		return edlItemExchangeAction;
	}

	public void setEdlItemExchangeAction(List<String> edlItemExchangeAction) {
		this.edlItemExchangeAction = edlItemExchangeAction;
	}

	public void setEdlItemExchangeAction(JsonArray edlItemExchangeAction) {
		this.edlItemExchangeAction.clear();
		if (edlItemExchangeAction != null) {
			for (int i=0; i < edlItemExchangeAction.getSize();i++) {
				String itemValue = edlItemExchangeAction.getValue(i, JsonValueType.STRING);
				this.edlItemExchangeAction.add(itemValue);
			}
		}
	}

	public String getEdlItemId() {
		return edlItemId;
	}

	public void setEdlItemId(String edlItemId) {
		this.edlItemId = edlItemId;
	}

	public List<String> getEdlItemLevel() {
		return edlItemLevel;
	}

	public void setEdlItemLevel(List<String> edlItemLevel) {
		this.edlItemLevel = edlItemLevel;
	}

	public void setEdlItemLevel(JsonArray edlItemLevel) {
		this.edlItemLevel.clear();
		if (edlItemLevel != null) {
			for (int i=0; i < edlItemLevel.getSize();i++) {
				String itemValue = edlItemLevel.getValue(i, JsonValueType.STRING);
				this.edlItemLevel.add(itemValue);
			}
		}
	}

	public String getEdlItemSite() {
		return edlItemSite;
	}

	public void setEdlItemSite(String edlItemSite) {
		this.edlItemSite = edlItemSite;
	}

	public String getEdlItemStudy() {
		return edlItemStudy;
	}

	public void setEdlItemStudy(String edlItemStudy) {
		this.edlItemStudy = edlItemStudy;
	}

	public String getEdlItemStudyCountry() {
		return edlItemStudyCountry;
	}

	public void setEdlItemStudyCountry(String edlItemStudyCountry) {
		this.edlItemStudyCountry = edlItemStudyCountry;
	}

}
