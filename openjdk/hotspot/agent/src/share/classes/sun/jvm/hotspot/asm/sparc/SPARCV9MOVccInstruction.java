/*
 * Copyright 2002-2005 Sun Microsystems, Inc.  All Rights Reserved.
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

public class SPARCV9MOVccInstruction extends SPARCMoveInstruction
                     implements SPARCV9Instruction {
    final private int conditionFlag; // condition flag used icc, xcc, fccn etc.
    final private int conditionCode;

    public SPARCV9MOVccInstruction(String name, int conditionCode, int conditionFlag,
                                   ImmediateOrRegister source, SPARCRegister rd) {
        super(name, MOVcc, source, rd);
        this.conditionCode = conditionCode;
        this.conditionFlag = conditionFlag;
    }

    protected String getDescription() {
        StringBuffer buf = new StringBuffer();
        buf.append(getName());
        buf.append(spaces);
        buf.append(SPARCV9ConditionFlags.getFlagName(conditionFlag));
        buf.append(comma);
        buf.append(getOperand2String());
        buf.append(comma);
        buf.append(rd.toString());
        return buf.toString();
    }

    public int getConditionCode() {
        return conditionCode;
    }

    public int getConditionFlag() {
        return conditionFlag;
    }

    public String getConditionFlagName() {
        return SPARCV9ConditionFlags.getFlagName(conditionFlag);
    }

    public boolean isConditional() {
        return true;
    }
}
