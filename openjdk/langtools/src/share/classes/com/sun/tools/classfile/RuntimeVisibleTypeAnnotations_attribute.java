/*
 * Copyright 2007-2008 Sun Microsystems, Inc.  All Rights Reserved.
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

package com.sun.tools.classfile;

import java.io.IOException;

/**
 * See JSR 308 specification, section 4.1
 *
 *  <p><b>This is NOT part of any API supported by Sun Microsystems.  If
 *  you write code that depends on this, you do so at your own risk.
 *  This code and its internal interfaces are subject to change or
 *  deletion without notice.</b>
 */
public class RuntimeVisibleTypeAnnotations_attribute extends RuntimeTypeAnnotations_attribute {
    RuntimeVisibleTypeAnnotations_attribute(ClassReader cr, int name_index, int length)
            throws IOException, Annotation.InvalidAnnotation {
        super(cr, name_index, length);
    }

    public RuntimeVisibleTypeAnnotations_attribute(ConstantPool cp, ExtendedAnnotation[] annotations)
            throws ConstantPoolException {
        this(cp.getUTF8Index(Attribute.RuntimeVisibleTypeAnnotations), annotations);
    }

    public RuntimeVisibleTypeAnnotations_attribute(int name_index, ExtendedAnnotation[] annotations) {
        super(name_index, annotations);
    }

    public <R, P> R accept(Visitor<R, P> visitor, P p) {
        return visitor.visitRuntimeVisibleTypeAnnotations(this, p);
    }
}
