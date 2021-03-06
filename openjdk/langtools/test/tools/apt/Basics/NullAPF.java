/*
 * Copyright 2004-2007 Sun Microsystems, Inc.  All Rights Reserved.
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


import com.sun.mirror.apt.*;
import com.sun.mirror.declaration.*;
import com.sun.mirror.type.*;
import com.sun.mirror.util.*;

import java.util.Collection;
import java.util.Set;

public class NullAPF implements AnnotationProcessorFactory {
    static class NullAP implements AnnotationProcessor {
        NullAP(AnnotationProcessorEnvironment ape) {
        }

        public void process() {
            return;
        }
    }

    static Collection<String> supportedTypes;

    static {
        String types[] = {"*"};
        supportedTypes = java.util.Arrays.asList(types);
    }

    /*
     * Processor doesn't examine any options.
     */
    public Collection<String> supportedOptions() {
        return java.util.Collections.emptySet();
    }

    /*
     * All annotation types are supported.
     */
    public Collection<String> supportedAnnotationTypes() {
        return supportedTypes;
    }

    /*
     * Return the same processor independent of what annotations are
     * present, if any.
     */
    public AnnotationProcessor getProcessorFor(Set<AnnotationTypeDeclaration> atds,
                                        AnnotationProcessorEnvironment env) {
        return new NullAP(env);
    }
}
