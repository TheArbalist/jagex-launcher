/*
 * Copyright 2008-2009 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 */

package sun.nio.fs;

import java.nio.file.attribute.*;
import java.util.*;
import java.io.IOException;

/**
 * Base implementation of AclFileAttributeView
 */

abstract class AbstractAclFileAttributeView
    implements AclFileAttributeView, DynamicFileAttributeView
{
    private static final String OWNER_NAME = "owner";
    private static final String ACL_NAME = "acl";

    @Override
    public final String name() {
        return "acl";
    }

    @Override
    public final Object getAttribute(String attribute) throws IOException {
        if (attribute.equals(OWNER_NAME))
            return getOwner();
        if (attribute.equals(ACL_NAME))
            return getAcl();
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public final void setAttribute(String attribute, Object value)
        throws IOException
    {
        if (attribute.equals(OWNER_NAME)) {
            setOwner((UserPrincipal)value);
            return;
        }
        if (attribute.equals(ACL_NAME)) {
            setAcl((List<AclEntry>)value);
            return;
        }
        throw new UnsupportedOperationException("'" + name() + ":" +
                attribute + "' not supported");
    }

    @Override
    public final Map<String,?> readAttributes(String[] attributes)
        throws IOException
    {
        boolean acl = false;
        boolean owner = false;
        for (String attribute: attributes) {
            if (attribute.equals("*")) {
                owner = true;
                acl = true;
                continue;
            }
            if (attribute.equals(ACL_NAME)) {
                acl = true;
                continue;
            }
            if (attribute.equals(OWNER_NAME)) {
                owner = true;
                continue;
            }
        }
        Map<String,Object> result = new HashMap<String,Object>(2);
        if (acl)
            result.put(ACL_NAME, getAcl());
        if (owner)
            result.put(OWNER_NAME, getOwner());
        return Collections.unmodifiableMap(result);
    }
}
