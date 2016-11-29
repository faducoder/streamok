/**
 * Licensed to the Smolok under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.streamok.service.document

import com.mongodb.BasicDBObject
import com.mongodb.DBObject
import org.apache.commons.lang3.Validate
import org.bson.types.ObjectId
import org.slf4j.LoggerFactory

import static org.slf4j.LoggerFactory.getLogger

/**
 * Converts DocumentStore canonical data (like documents and queries) into MongoDB structures (and reversely).
 */
final class MongodbMapper {

    private static final LOG = org.slf4j.LoggerFactory.getLogger(MongodbMapper.class)

    static final def MONGO_ID = '_id'

    private static final SIMPLE_SUFFIX_OPERATORS = [
            "GreaterThan": '$gt',
            "GreaterThanEqual": '$gte',
            "LessThan": '$lt',
            "LessThanEqual": '$lte',
            "NotIn": '$nin',
            "In": '$in']

    private final String idField

    MongodbMapper() {
        this('id')
    }

    MongodbMapper(String idField) {
        this.idField = idField
    }

    DBObject mongoQuery(Map<String, Object> jsonQuery) {
        def mongoQuery = new BasicDBObject()
        for (String originalKey : jsonQuery.keySet()) {
            String compoundKey = originalKey.replaceAll('(.)_', '$1.');

            String suffixOperator = findFirstMatchOperator(originalKey);
            if (suffixOperator != null) {
                addRestriction(mongoQuery, compoundKey, suffixOperator, SIMPLE_SUFFIX_OPERATORS.get(suffixOperator), jsonQuery.get(originalKey));
                continue;
            }

            if (originalKey.endsWith("Contains")) {
                addRestriction(mongoQuery, compoundKey, "Contains", '$regex', ".*" + jsonQuery.get(originalKey) + ".*");
            } else {
                addRestriction(mongoQuery, compoundKey, '', '$eq', jsonQuery.get(originalKey))
            }
        }
        mongoQuery
    }

    DBObject sortConditions(QueryBuilder queryBuilder) {
        Validate.notNull(queryBuilder, 'Query builder cannot be null.')

        int order = queryBuilder.sortAscending ? 1 : -1

        def orderBy = queryBuilder.orderBy
        def indexOfId = orderBy.findIndexOf {it == idField}
        if(indexOfId > -1) {
            orderBy[indexOfId] = '_id'
        }

        if (orderBy.size() == 0) {
            new BasicDBObject('$natural', order)
        } else {
            def sort = new BasicDBObject()
            orderBy.each {
                sort.put(it, order)
            }
            sort
        }
    }

    DBObject canonicalToMongo(Map<String, Object> document) {
        Validate.notNull(document, 'JSON passed to the conversion cannot be null.')

        def mongoDocument = new BasicDBObject(document)
        def id = mongoDocument.get(idField)
        if (id != null) {
            mongoDocument.removeField(idField);
            mongoDocument.put(MONGO_ID, id.toString());
        }
        mongoDocument
    }

    Map<String, Object> mongoToCanonical(DBObject mongoDocument) {
        Validate.notNull(mongoDocument, 'BSON passed to the conversion cannot be null.')
        LOG.debug('Converting MongoDB document {} into canonical format.', mongoDocument)

        def canonicalDocument = mongoDocument.toMap()
        def id = canonicalDocument.get(MONGO_ID)
        if (id != null) {
            canonicalDocument.remove(MONGO_ID)
            canonicalDocument.put(idField, id.toString());
        }
        canonicalDocument
    }

    // Helpers

    private String findFirstMatchOperator(String originalKey) {
        List<String> matchingSuffixOperators = SIMPLE_SUFFIX_OPERATORS.keySet().findAll{originalKey.endsWith(it)}.toList()
        return matchingSuffixOperators.isEmpty() ? null : matchingSuffixOperators.get(0);
    }

    private void addRestriction(BasicDBObject query, String propertyWithOperator, String propertyOperator, String operator, Object value) {
        def property = propertyWithOperator.replaceAll(propertyOperator + '$', "")
        if(property == idField) {
            property = MONGO_ID
            value = (String) value
        }
        if (query.containsField(property)) {
            BasicDBObject existingRestriction = (BasicDBObject) query.get(property);
            existingRestriction.put(operator, value);
        } else {
            query.put(property, new BasicDBObject(operator, value));
        }
    }

}
