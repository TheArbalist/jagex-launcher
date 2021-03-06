/*
 * Copyright 2004-2006 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
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
 *
 */

package sun.jvm.hotspot.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;

import java.awt.event.*;

import java.io.*;
import java.util.*;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.*;

import sun.jvm.hotspot.debugger.*;
import sun.jvm.hotspot.runtime.*;
import sun.jvm.hotspot.oops.Oop;
import sun.jvm.hotspot.ui.tree.*;

import sun.jvm.hotspot.ui.action.*;

import com.sun.java.swing.ui.*;
import com.sun.java.swing.action.*;

/**
 * This base class encapsulates many of the events that are fired from
 * the various panels in this directory so they can easily be plugged
 * in to different containing frameworks (HSDB, BugSpot).
 */
public class SAPanel extends JPanel {
    protected List listeners = new ArrayList();


    public SAPanel() {
    }

    public void addPanelListener(SAListener listener) {
        listeners.add(listener);
    }

    public void removePanelListener(SAListener listener) {
        listeners.remove(listener);
    }

    public void showThreadOopInspector(JavaThread t) {
        for (Iterator iter = listeners.iterator(); iter.hasNext(); ) {
            SAListener listener = (SAListener) iter.next();
            listener.showThreadOopInspector(t);
        }
    }

    public void showInspector(Oop oop) {
        showInspector(new OopTreeNodeAdapter(oop, null));
    }

    public void showInspector(SimpleTreeNode node) {
        for (Iterator iter = listeners.iterator(); iter.hasNext(); ) {
            SAListener listener = (SAListener) iter.next();
            listener.showInspector(node);
        }
    }

    public void showThreadStackMemory(JavaThread t) {
        for (Iterator iter = listeners.iterator(); iter.hasNext(); ) {
            SAListener listener = (SAListener) iter.next();
            listener.showThreadStackMemory(t);
        }
    }

    public void showJavaStackTrace(JavaThread t) {
        for (Iterator iter = listeners.iterator(); iter.hasNext(); ) {
            SAListener listener = (SAListener) iter.next();
            listener.showJavaStackTrace(t);
        }
    }

    public void showThreadInfo(JavaThread t) {
        for (Iterator iter = listeners.iterator(); iter.hasNext(); ) {
            SAListener listener = (SAListener) iter.next();
            listener.showThreadInfo(t);
        }
    }

    public void showCodeViewer(Address address) {
        for (Iterator iter = listeners.iterator(); iter.hasNext(); ) {
            SAListener listener = (SAListener) iter.next();
            listener.showCodeViewer(address);
        }
    }


}
