/**
 * Licensed to the Streamok under one or more
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
package net.streamok.service.machinelearningrest

import net.streamok.fiber.node.api.DependencyProvider
import net.streamok.fiber.node.api.Endpoint
import net.streamok.fiber.node.api.OperationDefinition
import net.streamok.fiber.node.api.Service
import net.streamok.service.machinelearningrest.operation.CreateLabelContent
import net.streamok.service.machinelearningrest.operation.GetLabelForContent
import net.streamok.service.machinelearningrest.operation.MultiTrain

class MachineLearningRestService implements Service {
    @Override
    List<OperationDefinition> operations() {
        [new CreateLabelContent(), new GetLabelForContent(), new MultiTrain()]
    }

    @Override
    List<DependencyProvider> dependencies() {
        []
    }

    @Override
    List<Endpoint> endpoints() {
        []
    }
}
