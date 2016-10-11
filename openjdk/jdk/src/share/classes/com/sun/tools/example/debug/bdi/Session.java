/*
 * Copyright 1998-1999 Sun Microsystems, Inc.  All Rights Reserved.
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

import com.sun.jdi.VirtualMachine;
import com.sun.jdi.VMDisconnectedException;
import com.sun.jdi.event.EventSet;

/**
 * Our repository of what we know about the state of one
 * running VM.
 */
class Session {

    final VirtualMachine vm;
    final ExecutionManager runtime;
    final OutputListener diagnostics;

    boolean running = true;  // Set false by JDIEventSource
    boolean interrupted = false;  // Set false by JDIEventSource

    private JDIEventSource eventSourceThread = null;
    private int traceFlags;
    private boolean dead = false;

    public Session(VirtualMachine vm, ExecutionManager runtime,
                   OutputListener diagnostics) {
        this.vm = vm;
        this.runtime = runtime;
        this.diagnostics = diagnostics;
        this.traceFlags = VirtualMachine.TRACE_NONE;
    }

    /**
     * Determine if VM is interrupted, i.e, present and not running.
     */
    public boolean isInterrupted() {
        return interrupted;
    }

    public void setTraceMode(int traceFlags) {
        this.traceFlags = traceFlags;
        if (!dead) {
            vm.setDebugTraceMode(traceFlags);
        }
    }

    public boolean attach() {
        vm.setDebugTraceMode(traceFlags);
        diagnostics.putString("Connected to VM");
        eventSourceThread = new JDIEventSource(this);
        eventSourceThread.start();
        return true;
    }

    public void detach() {
        if (!dead) {
            eventSourceThread.interrupt();
            eventSourceThread = null;
            //### The VM may already be disconnected
            //### if the debuggee did a System.exit().
            //### Exception handler here is a kludge,
            //### Rather, there are many other places
            //### where we need to handle this exception,
            //### and initiate a detach due to an error
            //### condition, e.g., connection failure.
            try {
                vm.dispose();
            } catch (VMDisconnectedException ee) {}
            dead = true;
            diagnostics.putString("Disconnected from VM");
        }
    }
}