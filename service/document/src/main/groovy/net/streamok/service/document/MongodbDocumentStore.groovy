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
import org.bson.types.ObjectId

//import static net.smolok.service.documentstore.mongodb.MongodbMapper.*

class MongodbDocumentStore {

    // Operations implementations

    long count(String collection, QueryBuilder queryBuilder) {
        documentCollection(collection).find(mongodbMapper.mongoQuery(queryBuilder.query)).
                limit(queryBuilder.size).skip(queryBuilder.skip()).sort(mongodbMapper.sortConditions(queryBuilder)).
                count()
    }

    void remove(String collection, String identifier) {
        documentCollection(collection).remove(new BasicDBObject(net.smolok.service.documentstore.mongodb.MongodbMapper.getMONGO_ID, new ObjectId(identifier)))
    }

}
