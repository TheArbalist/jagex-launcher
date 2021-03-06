/*
 * Copyright 1998-2008 Sun Microsystems, Inc.  All Rights Reserved.
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

package com.sun.tools.example.debug.bdi;

import com.sun.jdi.ThreadGroupReference;
import com.sun.jdi.ThreadReference;
import java.util.List;
import java.util.Iterator;

public class ThreadIterator implements Iterator<ThreadReference> {
    Iterator<ThreadReference> it = null;
    ThreadGroupIterator tgi;

    public ThreadIterator(ThreadGroupReference tg) {
        tgi = new ThreadGroupIterator(tg);
    }

    //### make this package access only?
    public ThreadIterator(List<ThreadGroupReference> tgl) {
        tgi = new ThreadGroupIterator(tgl);
    }

    public boolean hasNext() {
        while (it == null || !it.hasNext()) {
            if (!tgi.hasNext()) {
                return false; // no more
            }
            it = tgi.nextThreadGroup().threads().iterator();
        }
        return true;
    }

    public ThreadReference next() {
        return it.next();
    }

    public ThreadReference nextThread() {
        return next();
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }
}
