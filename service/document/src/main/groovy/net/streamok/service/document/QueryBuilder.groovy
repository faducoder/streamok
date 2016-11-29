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

public class QueryBuilder implements Serializable {

    private Map<String, Object> query = new HashMap<>()

    private int page = 0

    private int size = 25

    private boolean sortAscending = true;

    private String[] orderBy = new String[0];

    public QueryBuilder(Map<String, Object> query) {
        this.query = query;
    }

    public QueryBuilder() {
    }

    public static QueryBuilder queryBuilder(Map<String, Object> query) {
        return new QueryBuilder(query);
    }

    public static QueryBuilder queryBuilder() {
        return new QueryBuilder();
    }

    // Getters & setters

    public void setQuery(Map<String, Object> query) {
        this.query = query;
    }

    public Map<String, Object> getQuery() {
        query
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public QueryBuilder page(int page) {
        this.page = page;
        return this;
    }

    public int getSize() {
        return size;
    }

    public QueryBuilder size(int size) {
        this.size = size;
        return this;
    }

    public void setSize(int size) {
        this.size = size;
    }

    int skip() {
        page * size
    }

    public boolean isSortAscending() {
        return sortAscending;
    }

    public void setSortAscending(boolean sortAscending) {
        this.sortAscending = sortAscending;
    }

    public QueryBuilder sortAscending(boolean sortAscending) {
        this.sortAscending = sortAscending;
        return this;
    }

    public String[] getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String[] orderBy) {
        this.orderBy = orderBy;
    }

    public QueryBuilder orderBy(String... orderBy) {
        this.orderBy = orderBy;
        return this;
    }

}