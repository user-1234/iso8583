/**
 * Copyright (c) 2006 BLUEBAMBOO International Inc. 
 *           All rights reserved.
 *           
 * http://www.bluebamboo.com
 *
 * BLUEBAMBOO PROPRIETARY/CONFIDENTIAL.
 *
 */

/** 
 * File name:            ISOUtil.java
 * 
 * Originally developed: Commands.Wu
 *
 * Create date :         2006-10-23
 * 
 * Description:          Various functions needed to pack/unpack ISO-8583 fields.
 * 
 * Version:              0.1
 * 
 * Contributors:         
 * 
 * Modifications: 
 * name          version           reasons
 * 
 */
package com.commands.iso8583.pack.iso8583;



public class ISOUtil
{
  
  public final static char[] digits = {
    '0' , '1' , '2' , '3' , '4' , '5' , '6' , '7' , '8' , '9' , 
    'A' , 'B' , 'C' , 'D' , 'E' , 'F' , 'G' , 'H' , 'I' , 'J' , 
    'K' , 'L' , 'M' , 'N' , 'O' , 'P' , 'Q' , 'R' , 'S' , 'T' ,
    'U' , 'V' , 'W' , 'X' , 'Y' , 'Z'
  };
  
  public final static byte [] BCD_Digit = {
    (byte)0x00, (byte)0x01, (byte)0x02, (byte)0x03, (byte)0x04, (byte)0x05, (byte)0x06, (byte)0x07, (byte)0x08, (byte)0x09,
    (byte)0x10, (byte)0x11, (byte)0x12, (byte)0x13, (byte)0x14, (byte)0x15, (byte)0x16, (byte)0x17, (byte)0x18, (byte)0x19,
    (byte)0x20, (byte)0x21, (byte)0x22, (byte)0x23, (byte)0x24, (byte)0x25, (byte)0x26, (byte)0x27, (byte)0x28, (byte)0x29,
    (byte)0x30, (byte)0x31, (byte)0x32, (byte)0x33, (byte)0x34, (byte)0x35, (byte)0x36, (byte)0x37, (byte)0x38, (byte)0x39,
    (byte)0x40, (byte)0x41, (byte)0x42, (byte)0x43, (byte)0x44, (byte)0x45, (byte)0x46, (byte)0x47, (byte)0x48, (byte)0x49,
    (byte)0x50, (byte)0x51, (byte)0x52, (byte)0x53, (byte)0x54, (byte)0x55, (byte)0x56, (byte)0x57, (byte)0x58, (byte)0x59,
    (byte)0x60, (byte)0x61, (byte)0x62, (byte)0x63, (byte)0x64, (byte)0x65, (byte)0x66, (byte)0x67, (byte)0x68, (byte)0x69,
    (byte)0x70, (byte)0x71, (byte)0x72, (byte)0x73, (byte)0x74, (byte)0x75, (byte)0x76, (byte)0x77, (byte)0x78, (byte)0x79,
    (byte)0x80, (byte)0x81, (byte)0x82, (byte)0x83, (byte)0x84, (byte)0x85, (byte)0x86, (byte)0x87, (byte)0x88, (byte)0x89,
    (byte)0x90, (byte)0x91, (byte)0x92, (byte)0x93, (byte)0x94, (byte)0x95, (byte)0x96, (byte)0x97, (byte)0x98, (byte)0x99,
  }; 
  
  /**
   * @param   number      
   *          long number
   * @param   buf         
   *          the byte array
   * @param   offset
   *          the start position of the byte array
   * @param   length       
   *          convert length
   * @param   bcdFormat       
   *          bcdFormat=true: convert to BCD format
   *          bcdFormat=false: convert to ASCII format
   * @return  really length
   * 
   */
  public static int longToBytes(long number, byte[] buf, int offset, int length, boolean bcdFormat) 
  {
    if (buf == null || offset < 0 || length < 1 || buf.length < (offset + length))
      return -1;
    length = offset + length;
    int charPos = length -1;
    int numberLength = charPos;
    boolean numberLengthFlag = true;
    int index = 0;
    int radix = (bcdFormat?100:10);
    
    if (number > 0) {
      number = -number;
    }
    for(; charPos >= offset; charPos--){
      if (0 != number){
        index = (int)(-(number % radix));
        number = number / radix;
      } else {
        index = 0;
      }
      if (numberLengthFlag && 0 == number){
        numberLengthFlag = !numberLengthFlag;
        numberLength = length - charPos;
      }
      if (bcdFormat){
        buf[charPos] = BCD_Digit[index];
      } else {
        buf[charPos] = (byte)digits[index];
      }
    }

    return numberLength;
  } 
  public static int longToAscii(long number, byte[] buf, int offset, int length)
  {
    return longToBytes(number, buf, offset, length, false);
  }
  public static int longToBcd(long number, byte[] buf, int offset, int length)
  {
    return longToBytes(number, buf, offset, length, true);
  }
  
  /**
   * Convert BCD buffer to ASCII buffer
   * 
   * @param bcd_buf
   *            the bcd bytes array
   * @param bcd_offset
   *            the start position of the bcd bytes array
   * @param ascii_buf
   *            the ascii bytes array
   * @param asc_offset
   *            the start position of the bcd bytes array
   * @param conv_len
   *            Convert result the length of ascii buffer
   * @param type
   *            Padding zero flag: 
   *            type = 0: Padding 0 on the right 
   *            type = 1: Padding 0 on the left
   */
  public static void bcdToAscii(byte[] bcd_buf, int bcd_offset, byte[] ascii_buf, int asc_offset,
			int conv_len, int type)
  {
		int cnt;
		int bcdOffset = bcd_offset;
		int asciiOffset = asc_offset;

		if (conv_len > (bcd_buf.length * 2))
			conv_len = (bcd_buf.length * 2);

		if (((conv_len & 0x01) > 0) && (type > 0)) {
			cnt = 1;
			conv_len++;
		} else
			cnt = 0;

		for (; cnt < conv_len; cnt++, asciiOffset++) {
			ascii_buf[asciiOffset] = (byte) (((cnt & 0x01) > 0) ? (bcd_buf[bcdOffset++] & 0x0f)
					: ((bcd_buf[bcdOffset] & 0xFF) >>> 4));
			ascii_buf[asciiOffset] += (byte) ((ascii_buf[asciiOffset] > 9) ? (65 - 10)
					: 48); // 65 = 'A' 48 = '0'
		}
  }

  /**
   * Convert ASCII buffer to BCD buffer
   * 
   * @param ascii
   *            the ASCII bytes array
   * @return  the BCD bytes array.
   */
  public static byte[] asciiToBcd(byte[] ascii)
  {
    if (ascii != null )
    {
      int byteLen = ascii.length / 2;
      byte[] bcd = new byte[byteLen];
      if (asciiToBcd(ascii, 0, bcd, 0, byteLen * 2, 0))
        return bcd;
    }
    return null;
  }
  /**
   * Convert ASCII buffer to BCD buffer
   * 
   * @param ascii
   *            the ASCII bytes array
   * @param bcd
   *            the BCD bytes array
   */
  public static boolean asciiToBcd(byte[] ascii, byte[] bcd)
  {
    if (ascii != null && bcd != null)
    {
      int byteLen = bcd.length * 2;
      byteLen = (byteLen > ascii.length)?ascii.length:byteLen;
      return asciiToBcd(ascii, 0, bcd, 0, byteLen, 0);
    }
    else
      return false;
  }
  /**
   * Convert ASCII buffer to BCD buffer
   * 
   * @param ascii_buf
   *            the ascii bytes array
   * @param asc_offset
   *            the start position of the bcd bytes array
   * @param bcd_buf
   *            the bcd bytes array
   * @param bcd_offset
   *            the start position of the bcd bytes array
   * @param conv_len
   *            Need to convert the length of ascii buffer  
   * @param type
   *            Padding zero flag: 
   *            type = 0: Padding 0 on the right 
   *            type = 1: Padding 0 on the left
   */

  public static boolean asciiToBcd(byte[] ascii_buf, int asc_offset, byte[] bcd_buf, int bcd_offset,
	      int conv_len, int type)
  {
		if (bcd_offset < 0 || asc_offset < 0 || conv_len < 0
		|| ascii_buf == null || bcd_buf == null 
		|| bcd_offset >= bcd_buf.length || asc_offset >= ascii_buf.length)
			return false;  
	  int cnt;
	    byte ch, ch1;
	    int bcdOffset = bcd_offset;
	    int asciiOffset = asc_offset;

	    if (((conv_len & 0x01) > 0) && (type > 0))
	      ch1 = 0;
	    else
	      ch1 = 0x55;

	    for (cnt = 0; cnt < conv_len; asciiOffset++, cnt++)
	    {
	      if (ascii_buf[asciiOffset] >= 97) // 97 = 'a'
	      {
	        ch = (byte) (ascii_buf[asciiOffset] - 97 + 10); // 97 = 'a'
	      } else
	      {
	        if (ascii_buf[asciiOffset] >= 65) // 65 = 'A'
	          ch = (byte) ((ascii_buf[asciiOffset]) - 65 + 10); // 65 = 'A'
	        else
	          if (ascii_buf[asciiOffset] >= 48) // 48 = '0'
	            ch = (byte) ((ascii_buf[asciiOffset]) - 48); // 48 = '0'
	          else
	            ch = 0;
	      }

	      if (ch1 == 0x55)
	        ch1 = ch;
	      else
	      {
	        // *bcd_buf++=ch1<<4 | ch;
	        bcd_buf[bcdOffset++] = (byte) ((ch1 << 4) | ch);
	        ch1 = 0x55;
	      }
	    }
	    if (ch1 != 0x55)
	      bcd_buf[bcdOffset] = (byte) (ch1 << 4);
	    return true;
  }

  /**
   * Compares two byte array lexicographically..
   * 
   * @param src
   *            the byte array
   * @param srcOffset
   *            the start position of the first byte array
   * @param dst
   *            the byte array
   * @param dstOffset
   *            the start position of the second byte array
   * @return  the value <code>0</code> if the argument string is equal to
   *          this string; a value less than <code>0</code> if this string
   *          is lexicographically less than the string argument; and a
   *          value greater than <code>0</code> if this string is
   *          lexicographically greater than the string argument.
   */
  public static int compareByteArray(byte[] src, int srcOffset, int srcLen, byte[] dst, int dstOffset, int dstLen) 
  {
    if (src == null || srcOffset < 0 || srcLen < 0)
      return Integer.MIN_VALUE;
    if (dst == null || dstOffset < 0 || dstLen < 0)
      return Integer.MIN_VALUE;
      
    int n = Math.min(srcLen, dstLen);
    if ((srcOffset + n) > src.length || (dstOffset + n) > dst.length)
      return Integer.MIN_VALUE;
         
    char c1, c2;
    
    for (int i = 0; i < n; i++) 
    {
      // compare the byte
      c1 = (char)(src[srcOffset + i] & 0xFF);
      c2 = (char)(dst[dstOffset + i] & 0xFF);
      if (c1 != c2) 
      {
        return c1 - c2;
      }
    }

    return srcLen - dstLen;
  }
}
