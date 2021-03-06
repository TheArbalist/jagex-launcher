/*
 * Copyright 2008-2009 Sun Microsystems, Inc.  All Rights Reserved.
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

#include <windows.h>

#include "jni.h"
#include "jni_util.h"
#include "jlong.h"

#include "sun_nio_fs_RegistryFileTypeDetector.h"


JNIEXPORT jstring JNICALL
Java_sun_nio_fs_RegistryFileTypeDetector_queryStringValue(JNIEnv* env, jclass this,
    jlong keyAddress, jlong nameAddress)
{
    LPCWSTR lpSubKey= (LPCWSTR)jlong_to_ptr(keyAddress);
    LPWSTR lpValueName = (LPWSTR)jlong_to_ptr(nameAddress);
    LONG res;
    HKEY hKey;
    jstring result = NULL;

    res = RegOpenKeyExW(HKEY_CLASSES_ROOT, lpSubKey, 0, KEY_READ, &hKey);
    if (res == ERROR_SUCCESS) {
        DWORD type;
        BYTE data[255];
        DWORD size = sizeof(data);

        res = RegQueryValueExW(hKey, lpValueName, NULL, &type, (LPBYTE)&data, &size);
        if (res == ERROR_SUCCESS) {
            if (type == REG_SZ) {
                jsize len = wcslen((WCHAR*)data);
                result = (*env)->NewString(env, (const jchar*)&data, len);
            }
        }

        RegCloseKey(hKey);
    }
    return result;
}
