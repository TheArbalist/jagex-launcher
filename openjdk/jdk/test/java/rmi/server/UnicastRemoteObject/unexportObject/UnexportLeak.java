/*
 * Copyright 2000-2008 Sun Microsystems, Inc.  All Rights Reserved.
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
 */

/* @test
 * @bug 4331349
 * @summary synopsis: unexporting doesn't guarantee that DGC will
 * let go of remote object
 *
 * @author Ann Wollrath
 *
 * @build UnexportLeak
 * @build UnexportLeak_Stub
 * @build Ping
 * @run main/othervm UnexportLeak
 */

import java.lang.ref.*;
import java.rmi.*;
import java.rmi.server.*;
import java.rmi.registry.*;

public class UnexportLeak implements Ping {

    private static int PORT = 2006;

    public void ping() {
    }

    public static void main(String[] args) {
        try {
            System.err.println("\nRegression test for bug 4331349\n");
            LocateRegistry.createRegistry(PORT);
            Remote obj = new UnexportLeak();
            WeakReference wr = new WeakReference(obj);
            UnicastRemoteObject.exportObject(obj);
            LocateRegistry.getRegistry(PORT).rebind("UnexportLeak", obj);
            UnicastRemoteObject.unexportObject(obj, true);
            obj = null;
            flushRefs();
            if (wr.get() != null) {
                System.err.println("FAILED: unexported object not collected");
                throw new RuntimeException(
                    "FAILED: unexported object not collected");
            } else {
                System.err.println("PASSED: unexported object collected");
            }
        } catch (RemoteException e) {
            System.err.println(
                "FAILED: RemoteException encountered: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("FAILED: RemoteException encountered");
        }
    }

    /**
     * Force desparate garbage collection so that all WeakReference instances
     * will be cleared.
     */
    private static void flushRefs() {
        java.util.Vector chain = new java.util.Vector();
        try {
            while (true) {
                int[] hungry = new int[65536];
                chain.addElement(hungry);
            }
        } catch (OutOfMemoryError e) {
        }
    }
}
