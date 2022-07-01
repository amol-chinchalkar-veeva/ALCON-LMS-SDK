/*
 * --------------------------------------------------------------------
 * UDC:         VpsMapHelper
 * Author:      markarnold @ Veeva
 * Date:        2019-07-28
 * --------------------------------------------------------------------
 * Description: Basic Map Helper
 * --------------------------------------------------------------------
 * Copyright (c) 2019 Veeva Systems Inc.  All Rights Reserved.
 * This code is based on pre-existing content developed and
 * owned by Veeva Systems Inc. and may only be used in connection
 * with the deliverable with which it was provided to Customer.
 * --------------------------------------------------------------------
 */
package com.veeva.vault.custom.classes;

import com.veeva.vault.sdk.api.core.UserDefinedClassInfo;
import com.veeva.vault.sdk.api.core.ValueType;
import com.veeva.vault.sdk.api.core.VaultCollections;
import com.veeva.vault.sdk.api.query.QueryResponse;

import java.util.Map;

@UserDefinedClassInfo
public class VpsMapHelper {

	public VpsMapHelper() {
		super();
	}

	/**
	 * Populates a Map of string key/value pairs
	 * @param objectName Name of object to query
	 * @param keyField key field name
	 * @param valueField value field name
	 * @param whereClause where clause for vql query
	 */
	public static Map<String, String> getMap(String objectName, String keyField, String valueField, String whereClause) {
		return getKeyValueMap(objectName, keyField, valueField, whereClause, false);
	}

	/**
	 * Populates a Map of string key/value pairs and value/key pairs
	 * @param objectName Name of object to query
	 * @param keyField key field name
	 * @param valueField value field name
	 * @param whereClause where clause for vql query
	 */
	public static Map<String, String> getBidirectionalMap(String objectName, String keyField, String valueField, String whereClause) {
		return getKeyValueMap(objectName, keyField, valueField, whereClause, true);
	}

	/**
	 * Populates a Map of string key/value pairs
	 * @param objectName Name of object to query
	 * @param keyField key field name
	 * @param valueField value field name
	 * @param whereClause where clause for vql query
	 * @param bidirectional if true, values are added as keys as well
	 */
	private static Map<String, String> getKeyValueMap(String objectName,
													  String keyField,
													  String valueField,
													  String whereClause,
													  Boolean bidirectional) {
		Map<String, String> resultMap = VaultCollections.newMap();
		resultMap.putAll(resultMap);

		VpsVQLHelper vqlHelper = new VpsVQLHelper();
		vqlHelper.appendVQL("select " + keyField);
		vqlHelper.appendVQL("," + valueField);
		vqlHelper.appendVQL(" from " + objectName);
		vqlHelper.appendVQL(whereClause);

		QueryResponse queryResponse = vqlHelper.runVQL();
		queryResponse.streamResults().forEach(queryResult -> {
			String key = queryResult.getValue(keyField, ValueType.STRING);
			String value = queryResult.getValue(valueField, ValueType.STRING);
			resultMap.put(key, value);

			if (bidirectional) {
				resultMap.put(value, key);
			}
		});

		return resultMap;
	}
}