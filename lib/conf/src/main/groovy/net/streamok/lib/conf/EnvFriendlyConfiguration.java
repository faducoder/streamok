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
package net.streamok.lib.conf;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterators;
import org.apache.commons.configuration2.CompositeConfiguration;

import java.util.Iterator;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class EnvFriendlyConfiguration extends CompositeConfiguration {

    @Override
    protected Iterator<String> getKeysInternal() {
        Iterator<String> keys = super.getKeysInternal();
        List<String> envKeys = ImmutableList.copyOf(keys).stream().map(this::envKey).collect(toList());
        Iterators.addAll(envKeys, keys);
        return envKeys.iterator();
    }

    @Override
    protected Object getPropertyInternal(String key) {
        Object property = super.getPropertyInternal(key);
        if(property != null) {
            return property;
        }
        property = super.getPropertyInternal(envKey(key));
        if(property != null) {
            return property;
        } else {
            return null;
        }
    }

    @Override
    protected boolean containsKeyInternal(String key) {
        boolean contains = super.containsKeyInternal(key);
        return contains || super.containsKeyInternal(envKey(key));
    }

    private String envKey(String key) {
        return key.toUpperCase().replaceAll("\\.", "_");
    }

}
