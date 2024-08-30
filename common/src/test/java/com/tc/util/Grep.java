/*
 * <pre>
 * &#064;(#)Grep.java  1.3 01/12/13
 *
 * Copyright 2001-2002 Sun Microsystems, Inc. All Rights Reserved.
 * Copyright Super iPaaS Integration LLC, an IBM Company 2024
 *
 * Redistribution and use in source and binary forms, with or
 * without modification, are permitted provided that the following
 * conditions are met:
 *
 * -Redistributions of source code must retain the above copyright
 * notice, this  list of conditions and the following disclaimer.
 *
 * -Redistribution in binary form must reproduce the above copyright
 * notice, this list of conditions and the following disclaimer in
 * the documentation and/or other materials provided with the
 * distribution.
 *
 * Neither the name of Sun Microsystems, Inc. or the names of
 * contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 *
 * This software is provided &quot;AS IS,&quot; without a warranty of any
 * kind. ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND
 * WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY
 * EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY
 * DAMAGES OR LIABILITIES  SUFFERED BY LICENSEE AS A RESULT OF  OR
 * RELATING TO USE, MODIFICATION OR DISTRIBUTION OF THE SOFTWARE OR
 * ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE
 * FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT,
 * SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF
 * THE USE OF OR INABILITY TO USE SOFTWARE, EVEN IF SUN HAS BEEN
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 *
 * You acknowledge that Software is not designed, licensed or
 * intended for use in the design, construction, operation or
 * maintenance of any nuclear facility.
 * </pre>
 *
 * <pre>
 * The original code is found at http://java.sun.com/j2se/1.4.2/docs/guide/nio/example
 *
 * Modified by hhuynh to return list of CharSequence
 * </pre>
 *
 */
package com.tc.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.ref.PhantomReference;
import java.lang.ref.ReferenceQueue;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Search a list of files for lines that match a given regular-expression
 * pattern.  Demonstrates NIO mapped byte buffers, charsets, and regular
 * expressions.
 */
public class Grep {

  // Charset and decoder for ISO-8859-15
  private static Charset        charset     = Charset.forName("ISO-8859-15");
  private static CharsetDecoder decoder     = charset.newDecoder();

  // Pattern used to parse lines
  private static Pattern        linePattern = Pattern.compile(".*\r?\n");

  // Use the linePattern to break the given CharBuffer into lines, applying
  // the input pattern to each line to see if we have a match
  //
  public static List<CharSequence> grep(String pat, CharBuffer cb) {
    Pattern pattern = Pattern.compile(pat);
    List<CharSequence> result = new ArrayList<CharSequence>();
    Matcher lm = linePattern.matcher(cb); // Line matcher
    Matcher pm = null; // Pattern matcher
    while (lm.find()) {
      CharSequence cs = lm.group(); // The current line
      if (pm == null) pm = pattern.matcher(cs);
      else pm.reset(cs);
      if (pm.find()) result.add(cs);
      if (lm.end() == cb.limit()) break;
    }
    return result;
  }

  // Search for occurrences of the input pattern in the given file
  //
  public static List<CharSequence> grep(String pat, File f) throws IOException {
    ReferenceQueue<MappedByteBuffer> refQueue = new ReferenceQueue<>();
    PhantomReference<MappedByteBuffer> ref = null;
    // Open the file and then get a channel from the stream
    try {
      try (FileInputStream fis = new FileInputStream(f);
           FileChannel fc = fis.getChannel()) {

        // Get the file's size and then map it into memory
        int sz = (int)fc.size();
        MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, sz);
        ref = new PhantomReference<>(bb, refQueue);

        // Decode the file into a char buffer
        CharBuffer cb = decoder.decode(bb);
        bb = null;    // Encourages GC

        // Perform the search
        return grep(pat, cb);
      }

    } finally {
      if (ref != null) {
        while (refQueue.poll() != ref) {
          System.gc();
          System.runFinalization();
          try {
            Thread.sleep(100L);
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            break;
          }
        }
      }
    }
  }
}
