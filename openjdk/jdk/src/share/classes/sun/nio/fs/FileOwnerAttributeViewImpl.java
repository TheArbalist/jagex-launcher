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
 * An implementation of FileOwnerAttributeView that delegates to a given
 * PosixFileAttributeView or AclFileAttributeView object.
 */

final class FileOwnerAttributeViewImpl
    implements FileOwnerAttributeView, DynamicFileAttributeView
{
    private static final String OWNER_NAME = "owner";

    private final FileAttributeView view;
    private final boolean isPosixView;

    FileOwnerAttributeViewImpl(PosixFileAttributeView view) {
        this.view = view;
        this.isPosixView = true;
    }

    FileOwnerAttributeViewImpl(AclFileAttributeView view) {
        this.view = view;
        this.isPosixView = false;
    }

    @Override
    public String name() {
        return "owner";
    }

    @Override
    public Object getAttribute(String attribute) throws IOException {
        if (attribute.equals(OWNER_NAME))
            return getOwner();
        return null;
    }

    @Override
    public void setAttribute(String attribute, Object value)
        throws IOException
    {
        if (attribute.equals(OWNER_NAME)) {
            setOwner((UserPrincipal)value);
            return;
        }
        throw new UnsupportedOperationException("'" + name() + ":" +
                attribute + "' not supported");
    }

    @Override
    public Map<String,?> readAttributes(String[] attributes) throws IOException {
        Map<String,Object> result = new HashMap<String,Object>();
        for (String attribute: attributes) {
            if (attribute.equals("*") || attribute.equals(OWNER_NAME)) {
                result.put(OWNER_NAME, getOwner());
            }
        }
        return result;
    }

    @Override
    public UserPrincipal getOwner() throws IOException {
        if (isPosixView) {
            return ((PosixFileAttributeView)view).readAttributes().owner();
        } else {
            return ((AclFileAttributeView)view).getOwner();
        }
    }

    @Override
    public void setOwner(UserPrincipal owner)
        throws IOException
    {
        if (isPosixView) {
            ((PosixFileAttributeView)view).setOwner(owner);
        } else {
            ((AclFileAttributeView)view).setOwner(owner);
        }
    }
 }
