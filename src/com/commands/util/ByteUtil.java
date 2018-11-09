/** 
 * File name:            ByteUtil.java
 * 
 * Originally developed: 
 *
 * Create date :         
 * 
 * Description:          Array、ASCII、BCD等相关转换处理
 * 
 * Version:              0.1
 * 
 * Contributors:         
 * 
 * Modifications: 
 * name          version           reasons
 * 
 */
package com.commands.util;



public class ByteUtil
{
  /**
  *
  * Method Concatenates the specified byte[].
  *
  * @param number The int value to be converted.
  *
  */
  public static byte[] concatByteArray(byte[] a, byte[] b)
  {
    if (a == null && b == null)
      return null;
    return concatByteArray(a, b, 0, b==null?0:b.length);
  }
  public static byte[] concatByteArray(byte[] a, byte[] b, int offset, int length)
  {
    if (a == null && b == null)
      return null;

    int aL = (a == null?0:a.length);
    int bL = length;
    if (b == null || offset < 0 || length < 1 || b.length < (offset+length))
      bL = 0;
    if (bL == 0)
      return a;
    int len = aL + bL;
    byte[] c = new byte[len];
    
    if (a != null)
      System.arraycopy(a, 0, c, 0, aL);
    if (b != null)
      System.arraycopy(b, 0, c, aL, bL);
  
    return c;
  }  
  /**
   * Compares this byte arrary to the specified object.
   * The result is <code>true</code> if and only if the argument is not
   * <code>null</code> and is a <code>String</code> object that represents
   * the same sequence of characters as this object.
   *
    * @param src
   *            the first byte array
   * @param tag
   *            the second byte array
   *                     against.
   * @return  <code>true</code> if the <code>String </code>are equal;
   *          <code>false</code> otherwise.
   */
  public static boolean equalByteArray(byte[] src, byte[] dst) 
  {
      return equalByteArray(src, 0, src.length, dst, 0, dst.length);
  }
  public static boolean equalByteArray(byte[] src, int srcOffset, byte[] dst, int dstOffset, int length) 
  {
      if (compareByteArray(src, srcOffset, length, dst, dstOffset, length) != 0)
        return false;
      else
        return true;
  }
  public static boolean equalByteArray(byte[] src, int srcOffset, int srcLen, byte[] dst, int dstOffset, int dstLen) 
  {
      if (srcLen != dstLen || compareByteArray(src, srcOffset, srcLen, dst, dstOffset, dstLen) != 0)
        return false;
      else
        return true;
  }
  public static int compareByteArray(byte[] src, byte[] dst) 
  {
    return compareByteArray(src, 0, src.length, dst, 0, dst.length);
  }
  public static int compareByteArray(byte[] src, int srcOffset, byte[] dst, int dstOffset, int length) 
  {
    return compareByteArray(src, srcOffset, length, dst, dstOffset, length);
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

  public static void arraycopy(String src, int srcOffset, byte[] dst, int dstOffset, int length) 
  {
  	if (src == null || dst ==  null)
  	  throw new NullPointerException("invalid byte array ");
  	if ((src.length() < (srcOffset + length)) || (dst.length < (dstOffset + length)))
  	  throw new IndexOutOfBoundsException("invalid length: " + length);
  	  
    for (int i = 0; i < length; i++) 
    {
      dst[dstOffset + i] = (byte)src.charAt(srcOffset + i);
    }
  }

  public static byte[] bcdToAscii(byte[] bcdByte)
  {
    byte[] asciiByte = new byte[bcdByte.length * 2];
    bcdToAscii(bcdByte, 0, asciiByte, 0, asciiByte.length, 0);
    return asciiByte;
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
  public static byte[] asciiToBcd(byte[] ascii)
  {
    if (ascii != null )
    {
      int byteLen = (ascii.length + 1) / 2;
      byte[] bcd = new byte[byteLen];
      if (asciiToBcd(ascii, 0, bcd, 0, ascii.length, 0))
        return bcd;
    }
    return null;
  }
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
  
  public static final byte[] bitMask = { 
    (byte)0x80, (byte)0x40, (byte)0x20,( byte)0x10,
    (byte)0x08, (byte)0x04, (byte)0x02, (byte)0x01
    };
  
  /**
   * Get the byte data bit value of appointing the position 
   * 
   * @param data
   *            the byte data
   * @param index
   *            the position of the byte data  
   * @param fromLeft
   *            Index calculates the mode:
   *            fromLeft = true : Index is from left  
   *            fromLeft = false: Index is from right 
   * @return  the value of bit is <code>1</code>, return true.
   */  
  public static boolean getByteBitValue(byte data, int index, boolean fromLeft)
  {
    if ((data & bitMask[(fromLeft?(index & 0x07):(7 - (index & 0x07)))]) == 0)
      return false;
    else
      return true;
        
  }
  /**
   * Set the byte data bit value of appointing the position 
   * 
   * @param data
   *            the byte data
   * @param index
   *            the position of the byte data  
   * @param fromLeft
   *            Index calculates the mode:
   *            fromLeft = true : Index is from left  
   *            fromLeft = false: Index is from right 
   * @param value
   *            value = true : set the value of bit as 1 
   *            fromLeft = false: set the value of bit as 0
   * @return  set result.
   */  
  public static byte setByteBitValue(byte data, int index, boolean fromLeft, boolean value)
  {
    if (value == false)
      return (byte)((data & ~bitMask[(fromLeft?(index & 0x07):(7 - (index & 0x07)))]) & 0xFF); 
    
    return (byte)((data | bitMask[(fromLeft?(index & 0x07):(7 - (index & 0x07)))]) & 0xFF);    
  }
}
