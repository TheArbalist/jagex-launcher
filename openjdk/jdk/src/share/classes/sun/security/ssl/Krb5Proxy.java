/*
 * Copyright 2009 Sun Microsystems, Inc. All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation. Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
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

package sun.security.ssl;

import java.security.AccessControlContext;
import java.security.Permission;
import java.security.Principal;
import javax.crypto.SecretKey;
import javax.security.auth.Subject;
import javax.security.auth.login.LoginException;

/**
 * An interface to a subset of the Kerberos APIs to avoid a static dependency
 * on the types defined by these APIs.
 */
public interface Krb5Proxy {

    /**
     * Returns the Subject associated with the client-side of the SSL socket.
     */
    Subject getClientSubject(AccessControlContext acc) throws LoginException;

    /**
     * Returns the Subject associated with the server-side of the SSL socket.
     */
    Subject getServerSubject(AccessControlContext acc) throws LoginException;


    /**
     * Returns the KerberosKeys for the default server-side principal.
     */
    SecretKey[] getServerKeys(AccessControlContext acc) throws LoginException;

    /**
     * Returns the server-side principal name associated with the KerberosKey.
     */
    String getServerPrincipalName(SecretKey kerberosKey);

    /**
     * Returns the hostname embedded in the principal name.
     */
    String getPrincipalHostName(Principal principal);

    /**
     * Returns a ServicePermission for the principal name and action.
     */
    Permission getServicePermission(String principalName, String action);
}
