/*
 * Copyright 2001 Sun Microsystems, Inc.  All Rights Reserved.
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

package sun.jvm.hotspot.bugspot.tree;

import sun.jvm.hotspot.debugger.cdbg.*;
import sun.jvm.hotspot.ui.tree.SimpleTreeNode;

/** Abstract base class for all adapters for fields of C/C++ objects */

public abstract class FieldTreeNodeAdapter implements SimpleTreeNode {
  private FieldIdentifier id;
  private boolean         treeTableMode;

  /** The identifier may be null, i.e., for the root of the tree */
  public FieldTreeNodeAdapter(FieldIdentifier id, boolean treeTableMode) {
    this.id = id;
    this.treeTableMode = treeTableMode;
  }

  public FieldIdentifier getID() {
    return id;
  }

  /** Defaults to false in subclasses */
  public boolean getTreeTableMode() {
    return treeTableMode;
  }

  public Type getType() {
    return getID().getType();
  }

  public String getName() {
    if (getID() != null) {
      return getID().toString();
    }
    return "";
  }

  public String toString() {
    if (treeTableMode) {
      return getName();
    } else {
      if (getID() != null) {
        return getName() + ": " + getValue();
      } else {
        return getValue();
      }
    }
  }
}
