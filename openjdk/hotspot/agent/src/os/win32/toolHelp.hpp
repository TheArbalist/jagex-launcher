/*
 * Copyright 2001 Sun Microsystems, Inc.  All Rights Reserved.
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

#ifndef _TOOLHELP_H_
#define _TOOLHELP_H_

#include <windows.h>
#include <tlhelp32.h>

namespace ToolHelp {
extern "C" {

  ///////////////
  // Snapshots //
  ///////////////
  typedef HANDLE WINAPI
  CreateToolhelp32SnapshotFunc(DWORD dwFlags, DWORD th32ProcessID);

  //////////////////
  // Process List //
  //////////////////
  typedef BOOL WINAPI Process32FirstFunc(HANDLE hSnapshot,
                                         LPPROCESSENTRY32 lppe);

  typedef BOOL WINAPI Process32NextFunc(HANDLE hSnapshot,
                                        LPPROCESSENTRY32 lppe);

  // NOTE: although these routines are defined in TLHELP32.H, they
  // seem to always return false (maybe only under US locales)
  typedef BOOL WINAPI Process32FirstWFunc(HANDLE hSnapshot,
                                          LPPROCESSENTRY32W lppe);

  typedef BOOL WINAPI Process32NextWFunc(HANDLE hSnapshot,
                                         LPPROCESSENTRY32W lppe);

  /////////////////
  // Module List //
  /////////////////
  typedef BOOL WINAPI
  Module32FirstFunc(HANDLE hSnapshot, LPMODULEENTRY32 lpme);

  typedef BOOL WINAPI
  Module32NextFunc (HANDLE hSnapshot, LPMODULEENTRY32 lpme);


  // Routines to load and unload KERNEL32.DLL.
  HMODULE loadDLL();
  // Safe to call even if has not been loaded
  void    unloadDLL();

} // extern "C"
} // namespace "ToolHelp"

#endif // #defined _TOOLHELP_H_
