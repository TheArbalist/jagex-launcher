/*
 * Copyright 2008 Sun Microsystems, Inc.  All Rights Reserved.
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

/*
 * @test
 * @bug 6843077
 * @summary new type annotation location: expressions
 * @author Mahmood Ali
 * @compile -source 1.7 Expressions.java
 */
class Expressions {
  void instanceOf() {
    Object o = null;
    boolean a = o instanceof @A String;
    boolean b = o instanceof @B(0) String;
  }

  void instanceOfArray() {
    Object o = null;
    boolean a1 = o instanceof @A String [];
    boolean a2 = o instanceof @B(0) String [];

    boolean b1 = o instanceof String @A [];
    boolean b2 = o instanceof String @B(0) [];
  }

  void objectCreation() {
    new @A String();
    new @B(0) String();
  }

  void objectCreationArray() {
    Object a1 = new @A String [] [] { };
    Object a2 = new @A String [1] [];
    Object a3 = new @A String [1] [2];

    Object b1 = new @A String @B(0) [] [] { };
    Object b2 = new @A String @B(0) [1] [];
    Object b3 = new @A String @B(0) [1] [2];

    Object c1 = new @A String []  @B(0) [] { };
    Object c2 = new @A String [1] @B(0) [];
    Object c3 = new @A String [1] @B(0) [2];

    Object d1 = new @A String @B(0) []  @B(0) [] { };
    Object d2 = new @A String @B(0) [1] @B(0) [];
    Object d3 = new @A String @B(0) [1] @B(0) [2];

    Object rand = new @A String @B(value = 0) [1] @B(value = 0) [2];

  }
}

@interface A { }
@interface B { int value(); }
