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

package org.talend.components.netsuite.client;

/**
 * Holds information about NetSuite write response.
 *
 * <p>This data object is simple mirror of NetSuite's native {@code WriteResponse} data object.
 *
 * @param <RefT> type of record ref data object
 */
public class NsWriteResponse<RefT> {

    /** Status of 'write' operation. */
    private NsStatus status;

    /** NetSuite's native record ref data object. */
    private RefT ref;

    public NsWriteResponse() {
    }

    public NsWriteResponse(NsStatus status, RefT ref) {
        this.status = status;
        this.ref = ref;
    }

    public NsStatus getStatus() {
        return status;
    }

    public void setStatus(NsStatus status) {
        this.status = status;
    }

    public RefT getRef() {
        return ref;
    }

    public void setRef(RefT ref) {
        this.ref = ref;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("NsWriteResponse{");
        sb.append("status=").append(status);
        sb.append(", ref=").append(ref);
        sb.append('}');
        return sb.toString();
    }
}
