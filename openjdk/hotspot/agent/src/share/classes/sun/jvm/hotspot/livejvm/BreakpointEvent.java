/*
 * Copyright 2002 Sun Microsystems, Inc.  All Rights Reserved.
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

package sun.jvm.hotspot.livejvm;

import sun.jvm.hotspot.oops.*;
import sun.jvm.hotspot.runtime.*;

public class BreakpointEvent extends Event {
  private Oop thread;
  private Oop clazz;
  private JNIid method;
  private int location;

  public BreakpointEvent(Oop thread,
                         Oop clazz,
                         JNIid method,
                         int location) {
    super(Event.Type.BREAKPOINT);
    this.thread = thread;
    this.clazz = clazz;
    this.method = method;
    this.location = location;
  }

  public Oop thread()     { return thread;   }
  public Oop clazz()      { return clazz;    }
  public JNIid methodID() { return method;   }
  public int location()   { return location; }
}
