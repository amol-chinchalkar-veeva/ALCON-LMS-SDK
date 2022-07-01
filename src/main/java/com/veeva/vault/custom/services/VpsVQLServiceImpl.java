/*
 * --------------------------------------------------------------------
 * UserDefinedService:	VQLServiceImpl
 * Author:				markarnold @ Veeva
 * Date:				2020-08-28
 *---------------------------------------------------------------------
 * Description:	VQL
 *---------------------------------------------------------------------
 * Copyright (c) 2020 Veeva Systems Inc.  All Rights Reserved.
 *		This code is based on pre-existing content developed and
 * 		owned by Veeva Systems Inc. and may only be used in connection
 *		with the deliverable with which it was provided to Customer.
 *---------------------------------------------------------------------
 */
package com.veeva.vault.custom.services;

import com.veeva.vault.sdk.api.core.*;
import com.veeva.vault.sdk.api.query.QueryResponse;
import com.veeva.vault.sdk.api.query.QueryService;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

@UserDefinedServiceInfo
public class VpsVQLServiceImpl implements VpsVQLService {

	private static final String DATE_DATE_FORMAT = "yyyy-MM-dd";
	private static final String DATE_UTC_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

	//keywords
	public static final String AND = " AND ";
	public static final String CONTAINS = " CONTAINS ";
	public static final String EQUALS = " = ";
	public static final String FROM = " FROM ";
	public static final String IN = " IN ";
	public static final String LIMIT = " LIMIT ";
	public static final String LONGTEXT = "LONGTEXT";
	public static final String NOT_EQUAL = " != ";
	public static final String NULL = " NULL ";
	public static final String OR = " OR ";
	public static final String ORDER_BY = " ORDER BY ";
	public static final String SELECT = " SELECT ";
	public static final String WHERE = " WHERE ";

	@Override
	public String appendFields(String...fields) {
		if (fields != null) {
			return String.join(", ", VaultCollections.asList(fields));
		}
		return null;
	}

	@Override
	public String appendIfTrue(boolean assertion, Object...query) {
		if (assertion) {
			StringBuilder vqlBuilder = getQueryAsBuilder(query);
			if (vqlBuilder != null) {
				return vqlBuilder.toString();
			}
		}
		return null;
	}

	@Override
	public String asLongText(String field) {
		if (field != null) {
			return LONGTEXT + "(" + field + ")";
		}
		return null;
	}

	@Override
	public StringBuilder getQueryAsBuilder(Object... query) {
		if (query != null) {
			StringBuilder vqlBuilder = new StringBuilder();

			for(int i=0; i < query.length; i++){
				Object queryPart = query[i];
				if (queryPart != null) {
					if (queryPart instanceof List) {
						if (!((List)queryPart).isEmpty()) {
							vqlBuilder.append("'" + String.join("','", (List) queryPart) + "'");
						}
					}
					else if (queryPart instanceof Set) {
						if (!((Set)queryPart).isEmpty()) {
							vqlBuilder.append("'" + String.join("','", (Set) queryPart) + "'");
						}
					}
					else if (queryPart instanceof ZonedDateTime) {
						vqlBuilder.append(((ZonedDateTime)queryPart).format(DateTimeFormatter.ofPattern(DATE_UTC_FORMAT)) );
					}
					else if (queryPart instanceof LocalDate) {
						vqlBuilder.append(((LocalDate)queryPart).format(DateTimeFormatter.ofPattern(DATE_DATE_FORMAT)) );
					}
					else {
						vqlBuilder.append(queryPart);
					}
				}
			}

			return vqlBuilder;
		}
		return null;
	}

	@Override
	public boolean hasDuplicates(String fieldName, Object... query) {
		QueryResponse response = query(query);
		List<String> duplicates = VaultCollections.newList();
		List<String> values = VaultCollections.newList();
		response.streamResults().forEach(queryResult -> {
			String value = queryResult.getValue(fieldName, ValueType.STRING);
			if (!values.contains(value)) {
				values.add(value);
			}
			else {
				duplicates.add(value);
			}
		});
		return duplicates != null && !duplicates.isEmpty();
	}

	@Override
	public boolean hasRecords(Object... query) {
		QueryResponse response = query(query);
		return response.getResultCount() > 0;
	}

	@Override
	public QueryResponse query(Object... query) {

		if (query != null) {
			StringBuilder vqlBuilder = getQueryAsBuilder(query);
			if (vqlBuilder != null && vqlBuilder.length() > 0) {
				LogService logService = ServiceLocator.locate(LogService.class);
				logService.debug(vqlBuilder.toString());
				QueryService queryService = ServiceLocator.locate(QueryService.class);
				return queryService.query(vqlBuilder.toString());
			}
		}

		return null;
	}


}