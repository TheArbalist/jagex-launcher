/*
 * Copyright 2009 Sun Microsystems, Inc.  All Rights Reserved.
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

package build.tools.charsetmapping;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Formatter;
import java.util.regex.*;
import java.nio.charset.*;
import static build.tools.charsetmapping.CharsetMapping.*;

public class GenerateEUC_TW {

    public static void genEUC_TW(String args[]) throws Exception {
        genClass(new FileInputStream(new File(args[0], "euc_tw.map")),
                 new PrintStream(new File(args[1], "EUC_TWMapping.java"), "ISO-8859-1"),
                 getCopyright(new File(args[3])));
    }

    private static String getCopyright(File f) throws IOException {
        Scanner s = new Scanner(f, "ISO-8859-1");
        StringBuilder sb = new StringBuilder();
        while (s.hasNextLine()) {
            String ln = s.nextLine();
            sb.append(ln + "\n");
            // assume we have the copyright as the first comment
            if (ln.matches("^\\s\\*\\/$"))
                break;
        }
        s.close();
        return sb.toString();
    }

    private static char[] toCharArray(int[] db,
                                      int b1Min, int b1Max,
                                      int b2Min, int b2Max)
    {
        char[] ca = new char[(b1Max - b1Min + 1) * (b2Max - b2Min + 1)];
        int off = 0;
        for (int b1 = b1Min; b1 <= b1Max; b1++) {
            for (int b2 = b2Min; b2 <= b2Max; b2++) {
                ca[off++] = (char)(db[b1 * 256 + b2] & 0xffff);
            }
        }
        return ca;
    }

    private static void toChar(Formatter out, String fmt, char c) {
        switch (c) {
        case '\b':
            out.format("\\b"); break;
        case '\t':
            out.format("\\t"); break;
        case '\n':
            out.format("\\n"); break;
        case '\f':
            out.format("\\f"); break;
        case '\r':
            out.format("\\r"); break;
        case '\"':
            out.format("\\\""); break;
        case '\'':
            out.format("\\'"); break;
        case '\\':
            out.format("\\\\"); break;
        default:
            out.format(fmt, c & 0xffff);
        }
    }

    private static void toString(Formatter out, char[] date, String endStr)
    {
        int off = 0;
        int end = date.length;
        while (off < end) {
            out.format("        \"");
            for (int j = 0; j < 8 && off < end; j++) {
                toChar(out, "\\u%04X", date[off++]);
            }
            if (off == end)
               out.format("\"%s%n", endStr);
            else
               out.format("\" +%n");
        }
    }

    private static char[] toCharArray(byte[] ba,
                                      int b1Min, int b1Max,
                                      int b2Min, int b2Max)
    {
        char[] ca = new char[(b1Max - b1Min + 1) * (b2Max - b2Min + 1)];
        int off = 0;
        for (int b1 = b1Min; b1 <= b1Max; b1++) {
            int b2 = b2Min;
            while (b2 <= b2Max) {
                ca[off++] = (char)(((ba[b1 * 256 + b2++] & 0xff) << 8) |
                                   (ba[b1 * 256 + b2++] & 0xff));
            }
        }
        return ca;
    }

    private static void toCharArray(Formatter out, char[] date) {
        int off = 0;
        int end = date.length;
        while (off < end) {
            out.format("        ");
            for (int j = 0; j < 8 && off < end; j++) {
                toChar(out, "'\\u%04X',", date[off++]);
            }
            out.format("%n");
        }
    }

    private static int initC2BIndex(char[] index) {
        int off = 0;
        for (int i = 0; i < index.length; i++) {
            if (index[i] != 0) {
                index[i] = (char)off;
                off += 0x100;
            } else {
                index[i] = CharsetMapping.UNMAPPABLE_ENCODING;
            }
        }
        return off;
    }

    private static Pattern euctw = Pattern.compile("(?:8ea)?(\\p{XDigit}++)\\s++(\\p{XDigit}++)?\\s*+.*");

    private static void genClass(InputStream is, PrintStream ps, String copyright)
        throws Exception
    {
        // ranges of byte1 and byte2, something should come from a "config" file
        int b1Min = 0xa1;
        int b1Max = 0xfe;
        int b2Min = 0xa1;
        int b2Max = 0xfe;

        try {
            int[][] db = new int[8][0x10000];        // doublebyte
            byte[]  suppFlag = new byte[0x10000];    // doublebyte
            char[]  indexC2B = new char[256];
            char[]  indexC2BSupp = new char[256];

            for (int i = 0; i < 8; i++)
                for (int j = 0; j < 0x10000; j++)
                    db[i][j] = CharsetMapping.UNMAPPABLE_DECODING;

            CharsetMapping.Parser p = new CharsetMapping.Parser(is, euctw);
            CharsetMapping.Entry  e = null;
            while ((e = p.next()) != null) {
                int plane = 0;
                if (e.bs >= 0x10000) {
                    plane = ((e.bs >> 16) & 0xff) - 1;
                    if (plane >= 14)
                        plane = 7;
                    e.bs = e.bs & 0xffff;
                }
                db[plane][e.bs] = e.cp;
                if (e.cp < 0x10000) {
                    indexC2B[e.cp>>8] = 1;
                } else {
                    indexC2BSupp[(e.cp&0xffff)>>8] = 1;
                    suppFlag[e.bs] |= (1 << plane);
                }
            }

            StringBuilder out = new StringBuilder();
            Formatter fm = new Formatter(out);

            fm.format(copyright);
            fm.format("%n// -- This file was mechanically generated: Do not edit! -- //%n");
            fm.format("package sun.nio.cs.ext;%n%n");
            fm.format("class EUC_TWMapping {%n%n");

            // boundaries
            fm.format("    final static int b1Min = 0x%x;%n", b1Min);
            fm.format("    final static int b1Max = 0x%x;%n", b1Max);
            fm.format("    final static int b2Min = 0x%x;%n", b2Min);
            fm.format("    final static int b2Max = 0x%x;%n", b2Max);

            // b2c tables
            fm.format("%n    final static String[] b2c = {%n");
            for (int plane = 0; plane < 8; plane++) {
                fm.format("        // Plane %d%n", plane);
                toString(fm, toCharArray(db[plane],
                                         b1Min, b1Max, b2Min, b2Max),
                         ",");
                fm.format("%n");
            }
            fm.format("    };%n");

            // c2bIndex
            fm.format("%n    static final int C2BSIZE = 0x%x;%n",
                      initC2BIndex(indexC2B));
            fm.format("%n    static char[] c2bIndex = new char[] {%n");
            toCharArray(fm, indexC2B);
            fm.format("    };%n");

            // c2bIndexSupp
            fm.format("%n    static final int C2BSUPPSIZE = 0x%x;%n",
                      initC2BIndex(indexC2BSupp));
            fm.format("%n    static char[] c2bSuppIndex = new char[] {%n");
            toCharArray(fm, indexC2BSupp);
            fm.format("    };%n");

            // suppFlags
            fm.format("%n    static String b2cIsSuppStr =%n");
            toString(fm, toCharArray(suppFlag,
                                     b1Min, b1Max, b2Min, b2Max),
                     ";");

            fm.format("}");
            fm.close();

            ps.println(out.toString());
            ps.close();
        } catch (Exception x) {
            x.printStackTrace();
        }
    }
}
