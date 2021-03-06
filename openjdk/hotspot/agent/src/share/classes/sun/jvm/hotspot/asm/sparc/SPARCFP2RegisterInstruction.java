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

package sun.jvm.hotspot.asm.sparc;

import sun.jvm.hotspot.asm.*;

public class SPARCFP2RegisterInstruction extends SPARCInstruction {
    final SPARCFloatRegister rs;
    final SPARCFloatRegister rd;
    final int opf;

    public SPARCFP2RegisterInstruction(String name, int opf, SPARCFloatRegister rs, SPARCFloatRegister rd) {
        super(name);
        this.rs = rs;
        this.rd = rd;
        this.opf = opf;
    }

    public String asString(long currentPc, SymbolFinder symFinder) {
        return getDescription();
    }

    protected String getDescription() {
        StringBuffer buf = new StringBuffer();
        buf.append(getName());
        buf.append(spaces);
        buf.append(rs.toString());
        buf.append(comma);
        buf.append(rd.toString());

        return buf.toString();
    }

    public boolean isFloat() {
        return true;
    }
}
