/**
 * Licensed to the Smolok under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.streamok.service.document

import com.mongodb.BasicDBObject
import com.mongodb.DBCollection
import com.mongodb.Mongo
import org.apache.commons.lang3.Validate
import org.bson.types.ObjectId

//import static net.smolok.service.documentstore.mongodb.MongodbMapper.*

class MongodbDocumentStore {

    // Logger

    private static final LOG = org.slf4j.LoggerFactory.getLogger(MongodbDocumentStore.class)

    // Collaborators

    private final Mongo mongo

    private final MongodbMapper mongodbMapper

    // Configuration members

    private final String documentsDbName

    // Constructors

    MongodbDocumentStore(Mongo mongo, MongodbMapper mongodbMapper, String documentsDbName) {
        this.mongo = Validate.notNull(mongo, 'Mongo client expected not to be null.')
        this.mongodbMapper = Validate.notNull(mongodbMapper, 'MongoDB mapper expected not to be null.')
        this.documentsDbName = Validate.notBlank(documentsDbName, 'Documents database name expected not to be blank.')
    }

    // Operations implementations

    List<Map<String, Object>> findMany(String collection, List<String> ids) {
        def mongoIds = new BasicDBObject('$in', ids.collect{new ObjectId(it)})
        def query = new BasicDBObject(net.smolok.service.documentstore.mongodb.MongodbMapper.getMONGO_ID, mongoIds)
        documentCollection(collection).find(query).toArray().collect { mongodbMapper.mongoToCanonical(it) }
    }

    List<Map<String, Object>> find(String collection, QueryBuilder queryBuilder) {
        documentCollection(collection).find(mongodbMapper.mongoQuery(queryBuilder.query)).
                limit(queryBuilder.size).skip(queryBuilder.skip()).sort(mongodbMapper.sortConditions(queryBuilder)).
                toArray().collect{ mongodbMapper.mongoToCanonical(it) }
    }

    long count(String collection, QueryBuilder queryBuilder) {
        documentCollection(collection).find(mongodbMapper.mongoQuery(queryBuilder.query)).
                limit(queryBuilder.size).skip(queryBuilder.skip()).sort(mongodbMapper.sortConditions(queryBuilder)).
                count()
    }

    void remove(String collection, String identifier) {
        documentCollection(collection).remove(new BasicDBObject(net.smolok.service.documentstore.mongodb.MongodbMapper.getMONGO_ID, new ObjectId(identifier)))
    }

    // Helpers

    private DBCollection documentCollection(String collection) {
        mongo.getDB(documentsDbName).getCollection(collection)
    }

}
