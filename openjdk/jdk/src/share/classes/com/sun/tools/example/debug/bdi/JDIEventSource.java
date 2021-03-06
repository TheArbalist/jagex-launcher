/*
 * Copyright 1999-2008 Sun Microsystems, Inc.  All Rights Reserved.
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

import com.sun.jdi.*;
import com.sun.jdi.event.*;

import java.util.*;

import com.sun.tools.example.debug.event.*;

import javax.swing.SwingUtilities;

/**
 */
class JDIEventSource extends Thread {

    private /*final*/ EventQueue queue;
    private /*final*/ Session session;
    private /*final*/ ExecutionManager runtime;
    private final JDIListener firstListener = new FirstListener();

    private boolean wantInterrupt;  //### Hack

    /**
     * Create event source.
     */
    JDIEventSource(Session session) {
        super("JDI Event Set Dispatcher");
        this.session = session;
        this.runtime = session.runtime;
        this.queue = session.vm.eventQueue();
    }

    public void run() {
        try {
            runLoop();
        } catch (Exception exc) {
            //### Do something different for InterruptedException???
            // just exit
        }
        session.running = false;
    }

    private void runLoop() throws InterruptedException {
        AbstractEventSet es;
        do {
            EventSet jdiEventSet = queue.remove();
            es = AbstractEventSet.toSpecificEventSet(jdiEventSet);
            session.interrupted = es.suspendedAll();
            dispatchEventSet(es);
        } while(!(es instanceof VMDisconnectEventSet));
    }

    //### Gross foul hackery!
    private void dispatchEventSet(final AbstractEventSet es) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                boolean interrupted = es.suspendedAll();
                es.notify(firstListener);
                boolean wantInterrupt = JDIEventSource.this.wantInterrupt;
                for (JDIListener jl : session.runtime.jdiListeners) {
                    es.notify(jl);
                }
                if (interrupted && !wantInterrupt) {
                    session.interrupted = false;
                    //### Catch here is a hack
                    try {
                        session.vm.resume();
                    } catch (VMDisconnectedException ee) {}
                }
                if (es instanceof ThreadDeathEventSet) {
                    ThreadReference t = ((ThreadDeathEventSet)es).getThread();
                    session.runtime.removeThreadInfo(t);
                }
            }
        });
    }

    private void finalizeEventSet(AbstractEventSet es) {
        if (session.interrupted && !wantInterrupt) {
            session.interrupted = false;
            //### Catch here is a hack
            try {
                session.vm.resume();
            } catch (VMDisconnectedException ee) {}
        }
        if (es instanceof ThreadDeathEventSet) {
            ThreadReference t = ((ThreadDeathEventSet)es).getThread();
            session.runtime.removeThreadInfo(t);
        }
    }

    //### This is a Hack, deal with it
    private class FirstListener implements JDIListener {

        public void accessWatchpoint(AccessWatchpointEventSet e) {
            session.runtime.validateThreadInfo();
            wantInterrupt = true;
        }

        public void classPrepare(ClassPrepareEventSet e)  {
            wantInterrupt = false;
            runtime.resolve(e.getReferenceType());
        }

        public void classUnload(ClassUnloadEventSet e)  {
            wantInterrupt = false;
        }

        public void exception(ExceptionEventSet e)  {
            wantInterrupt = true;
        }

        public void locationTrigger(LocationTriggerEventSet e)  {
            session.runtime.validateThreadInfo();
            wantInterrupt = true;
        }

        public void modificationWatchpoint(ModificationWatchpointEventSet e)  {
            session.runtime.validateThreadInfo();
            wantInterrupt = true;
        }

        public void threadDeath(ThreadDeathEventSet e)  {
            wantInterrupt = false;
        }

        public void threadStart(ThreadStartEventSet e)  {
            wantInterrupt = false;
        }

        public void vmDeath(VMDeathEventSet e)  {
            //### Should have some way to notify user
            //### that VM died before the session ended.
            wantInterrupt = false;
        }

        public void vmDisconnect(VMDisconnectEventSet e)  {
            //### Notify user?
            wantInterrupt = false;
            session.runtime.endSession();
        }

        public void vmStart(VMStartEventSet e)  {
            //### Do we need to do anything with it?
            wantInterrupt = false;
        }
    }
}
