/*
 * Copyright 2000 Sun Microsystems, Inc.  All Rights Reserved.
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
 * @bug 4357979
 * @summary Verify that ObjectInputStream throws a StreamCorruptedException if
 *          it reads in an out-of-bounds handle value.
 */

import java.io.*;

public class Test {
    public static void main(String[] args) throws Exception {
        String base = System.getProperty("test.src", ".");

        /*
         * Files negativeHandle.ser and tooHighHandle.ser each contain a
         * serialized String object followed by an illegal handle
         */
        File f = new File(base, "negativeHandle.ser");
        ObjectInputStream oin = new ObjectInputStream(new FileInputStream(f));
        oin.readObject();
        try {
            oin.readObject();
            throw new Error("negative handle read should not succeed");
        } catch (StreamCorruptedException ex) {
        }

        f = new File(base, "tooHighHandle.ser");
        oin = new ObjectInputStream(new FileInputStream(f));
        oin.readObject();
        try {
            oin.readObject();
            throw new Error("too-high handle read should not succeed");
        } catch (StreamCorruptedException ex) {
        }
    }
}
