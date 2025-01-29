/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

 package org.apache.commons.codec.net;

 import static org.junit.jupiter.api.Assertions.assertEquals;
 import static org.junit.jupiter.api.Assertions.assertThrows;
 
 import java.nio.charset.StandardCharsets;
 
 import org.apache.commons.codec.CharEncoding;
 import org.apache.commons.codec.DecoderException;
 import org.junit.jupiter.api.Test;
 
 /**
  * URL codec test cases
  */
 public class URLCodecTest {
 
     static final int[] SWISS_GERMAN_STUFF_UNICODE = { 0x47, 0x72, 0xFC, 0x65, 0x7A, 0x69, 0x5F, 0x7A, 0xE4, 0x6D, 0xE4 };
 
     static final int[] RUSSIAN_STUFF_UNICODE = { 0x412, 0x441, 0x435, 0x43C, 0x5F, 0x43F, 0x440, 0x438, 0x432, 0x435, 0x442 };
 
     private String constructString(final int[] unicodeChars) {
         final StringBuilder buffer = new StringBuilder();
         if (unicodeChars != null) {
             for (final int unicodeChar : unicodeChars) {
                 buffer.append((char) unicodeChar);
             }
         }
         return buffer.toString();
     }
 
     @Test
     public void testBasicEncodeDecode() throws Exception {
         final URLCodec urlCodec = new URLCodec();
         final String plain = "Hello there!";
         final String encoded = urlCodec.encode(plain);
         assertEquals("Hello+there%21", encoded, "Basic URL encoding test");
         assertEquals(plain, urlCodec.decode(encoded), "Basic URL decoding test");
         validateState();
     }
 
     @Test
     public void testDecodeInvalid() throws Exception {
         final URLCodec urlCodec = new URLCodec();
         assertThrows(DecoderException.class, () -> urlCodec.decode("%"));
         assertThrows(DecoderException.class, () -> urlCodec.decode("%A"));
         assertThrows(DecoderException.class, () -> urlCodec.decode("%WW"));
         assertThrows(DecoderException.class, () -> urlCodec.decode("%0W"));
         validateState();
     }
 
     @Test
     public void testDecodeInvalidContent() throws DecoderException {
         final String ch_msg = constructString(SWISS_GERMAN_STUFF_UNICODE);
         final URLCodec urlCodec = new URLCodec();
         final byte[] input = ch_msg.getBytes(StandardCharsets.ISO_8859_1);
         final byte[] output = urlCodec.decode(input);
         assertEquals(input.length, output.length);
         for (int i = 0; i < input.length; i++) {
             assertEquals(input[i], output[i]);
         }
         validateState();
     }
 
     @Test
     public void testUTF8RoundTrip() throws Exception {
         final String ru_msg = constructString(RUSSIAN_STUFF_UNICODE);
         final String ch_msg = constructString(SWISS_GERMAN_STUFF_UNICODE);
         final URLCodec urlCodec = new URLCodec();
         validateState();
 
         assertEquals("%D0%92%D1%81%D0%B5%D0%BC_%D0%BF%D1%80%D0%B8%D0%B2%D0%B5%D1%82", urlCodec.encode(ru_msg, CharEncoding.UTF_8));
         assertEquals("Gr%C3%BCezi_z%C3%A4m%C3%A4", urlCodec.encode(ch_msg, CharEncoding.UTF_8));
 
         assertEquals(ru_msg, urlCodec.decode(urlCodec.encode(ru_msg, CharEncoding.UTF_8), CharEncoding.UTF_8));
         assertEquals(ch_msg, urlCodec.decode(urlCodec.encode(ch_msg, CharEncoding.UTF_8), CharEncoding.UTF_8));
         validateState();
     }
 
     private void validateState() {
         // no tests for now.
     }
 }
 