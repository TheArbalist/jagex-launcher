/*
 * Copyright 2005-2006 Sun Microsystems, Inc.  All Rights Reserved.
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

#include <stdio.h>
#include <string.h>
#include <jni.h>

#include "jli_util.h"

/*
 * Returns a pointer to a block of at least 'size' bytes of memory.
 * Prints error message and exits if the memory could not be allocated.
 */
void *
JLI_MemAlloc(size_t size)
{
    void *p = malloc(size);
    if (p == 0) {
        perror("malloc");
        exit(1);
    }
    return p;
}

/*
 * Equivalent to realloc(size).
 * Prints error message and exits if the memory could not be reallocated.
 */
void *
JLI_MemRealloc(void *ptr, size_t size)
{
    void *p = realloc(ptr, size);
    if (p == 0) {
        perror("realloc");
        exit(1);
    }
    return p;
}

/*
 * Wrapper over strdup(3C) which prints an error message and exits if memory
 * could not be allocated.
 */
char *
JLI_StringDup(const char *s1)
{
    char *s = strdup(s1);
    if (s == NULL) {
        perror("strdup");
        exit(1);
    }
    return s;
}

/*
 * Very equivalent to free(ptr).
 * Here to maintain pairing with the above routines.
 */
void
JLI_MemFree(void *ptr)
{
    free(ptr);
}

/*
 * Makes a copy of arguments
 */
char**
JLI_CopyArgs(int argc, const char **iargv)
{
    int i;
    char** oargv = (char**)JLI_MemAlloc(sizeof(char*)*(argc+1));
    for (i = 0 ; i < argc+1 ; i++) {
        oargv[i] = (iargv[i] == NULL) ? NULL : JLI_StringDup(iargv[i]);
        if (iargv[i] != NULL && JLI_IsTraceLauncher() == JNI_TRUE) {
            printf("\targv[%d] = '%s'\n",i,iargv[i]);
        }
    }
    return oargv;
}

/*
 * debug helpers we use
 */
static jboolean _launcher_debug = JNI_FALSE;

void
JLI_TraceLauncher(const char* fmt, ...)
{
    va_list vl;
    if (_launcher_debug != JNI_TRUE) return;
    va_start(vl, fmt);
    vprintf(fmt,vl);
    va_end(vl);
}

void
JLI_SetTraceLauncher()
{
   if (getenv("_JAVA_LAUNCHER_DEBUG") != 0) {
        _launcher_debug = JNI_TRUE;
        JLI_TraceLauncher("----_JAVA_LAUNCHER_DEBUG----\n");
   }
}

jboolean
JLI_IsTraceLauncher()
{
   return _launcher_debug;
}

int
JLI_StrCCmp(const char *s1, const char* s2)
{
   return JLI_StrNCmp(s1, s2, JLI_StrLen(s2));
}
