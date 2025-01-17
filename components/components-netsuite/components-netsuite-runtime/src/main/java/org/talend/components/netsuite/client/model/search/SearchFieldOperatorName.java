//============================================================================
//
// Copyright (C) 2006-2023 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
//============================================================================

package org.talend.components.netsuite.client.model.search;

import java.util.Objects;

/**
 * Holds search field operator name data.
 *
 * <p>Full (qualified) name of an operator consists of two parts separated by '.' symbol:
 * <ul>
 *     <li>operator data type ({@link SearchFieldOperatorType#getDataType()})</li>
 *     <li>operator name</li>
 * </ul>
 * Examples:
 * <ul>
 *     <li>{@code SearchLongFieldOperator} - Long.greaterThanOrEqualTo, Long.notLessThan</li>
 *     <li>{@code SearchStringFieldOperator} - String.notEmpty, String.startsWith</li>
 * </ul>
 *
 * @see SearchFieldOperatorType
 */
public class SearchFieldOperatorName {

    /** Name of search data type. */
    private String dataType;

    /** Name of operator. */
    private String name;

    /**
     * Create search field operator name object using given qualified name of an operator.
     *
     * @param qualifiedName operator name
     */
    public SearchFieldOperatorName(String qualifiedName) {
        int i = qualifiedName.indexOf(".");
        if (i == -1) {
            this.dataType = qualifiedName;
            this.name = null;
        } else {
            String thatDataType = qualifiedName.substring(0, i);
            if (thatDataType.isEmpty()) {
                throw new IllegalArgumentException("Invalid operator data type: " + "'" + thatDataType + "'");
            }
            this.dataType = thatDataType;
            String thatName = qualifiedName.substring(i + 1);
            if (thatName.isEmpty()) {
                throw new IllegalArgumentException("Invalid operator name: " + "'" + thatName + "'");
            }
            this.name = thatName;
        }
    }

    /**
     * Create search field operator name object using given data type and name of an operator.
     *
     * @param dataType data type name
     * @param name
     */
    public SearchFieldOperatorName(String dataType, String name) {
        this.dataType = dataType;
        this.name = name;
    }

    public String getDataType() {
        return dataType;
    }

    public String getName() {
        return name;
    }

    /**
     * Get full (qualified) name.
     *
     * @return name
     */
    public String getQualifiedName() {
        return name != null ? dataType + "." + name : dataType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        SearchFieldOperatorName that = (SearchFieldOperatorName) o;
        return Objects.equals(dataType, that.dataType) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dataType, name);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Name{");
        sb.append("dataType='").append(dataType).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", qualifiedName='").append(getQualifiedName()).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
