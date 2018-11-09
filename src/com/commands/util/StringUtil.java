/**
 * File name:            StringUtil.java
 *
 * Originally developed: 
 *
 * Create date :         
 *
 * Description:          This String convert process.
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

import java.io.UnsupportedEncodingException;

public class StringUtil
{
  public static final int LCD_WIDTH = 16;

  public static final String specialSaveChars = "=: \t\r\n\f#!";

  /** A table of hex digits */
  public static final char[] hexDigit = { '0', '1', '2', '3', '4', '5', '6',
      '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

  /**
   * Convert a nibble to a hex character
   *
   * @param nibble
   *          the nibble to convert.
   */
  public static char toHexChar(int nibble)
  {
    return hexDigit[(nibble & 0xF)];
  }

  public static String returnString(String str)
  {
    if (null == str)
        return "";
    else
      return str;
  }

  public static String returnString(int intValue)
  {
    if (intValue < 0)
      return "";
    else
      return "" + intValue;
  }

  public static String returnString(byte byteValue)
  {
     return returnString((int)byteValue);
  }

  /**
  * Method trim all space
  *
  * @param The string to be format.
  *
  */
  public static String trimSpace(String oldString)
  {
    if (null == oldString)
      return null;
    if (0 == oldString.length())
      return "";

    StringBuffer sbuf = new StringBuffer();
    int oldLen = oldString.length();
    for(int i = 0; i < oldLen; i++)
    {
      if (' ' != oldString.charAt(i))
        sbuf.append(oldString.charAt(i));
    }
    String returnString = sbuf.toString();
    sbuf = null;
    return returnString;
  }
  /**
   * Method trim space
   *
   * @param oldString The string to be format.
   * @param trimFlag The trim flag,
   *         =0:trim both sides,
   *         >0:trim right sides,
   *         <0:trim left sides,
   *
   */
   public static String trimSpace(String oldString, int trimFlag)
   {
     if (null == oldString)
       return null;
     if (0 == oldString.length())
       return "";

     int length = oldString.length();
     int j = 0;
     for(j = 0; j < length && (oldString.charAt(j) == ' ' || oldString.charAt(j) == '\0'); j++);
     if (trimFlag < 0) // trim left sides
       return (j <= 0 ? oldString : oldString.substring(j));
     for(; j < length && (oldString.charAt(length - 1) == ' ' || oldString.charAt(length - 1) == '\0'); length--);
     if (trimFlag > 0) //trim right sides
       return (length >= oldString.length() ? oldString:oldString.substring(0, length));

     return (j <= 0 && length >= oldString.length() ? oldString : oldString.substring(j, length));
  }
  /**
  * Method convert byte[] to String
  *
  * @param The string to be format.
  *
  */
  public static String toString(byte[] buffer)
  {
    if(null == buffer)
      return null;
    else
      return trimSpace(new String(buffer), 0);
  }

  /**
   * Format buffer into the designated width and height, for example:
   * bufferString = "123456789012345678901234567890"
   * width = 16 , height = 0
   * String[] = {
   * {"1234567890123456"},
   * {"78901234567890"},
   * }
   *
   * @param The string to be format.
   *
   */
  public static String[] buffer2Message(String bufferString, int width, int height)
  {
    int buffLen;
    int i = 0;
    int h, w;
    if (null == bufferString)
      bufferString = "";

    buffLen = bufferString.length();

    if (height < 1 && width > 0)
    {
      if (0 == (buffLen % width))
        h = buffLen / width;
      else
        h = (buffLen / width) + 1;
      w = width;
    } else
    {
      if (height > 0 && width < 1)
      {
        if (0 == (buffLen % height))
          w = buffLen / height;
        else
          w = (buffLen / height) + 1;
        h = height;
      } else
      {
        if (height > 0 && width > 0)
        {
          h = height;
          w = width;
        } else
        {
          return null;
        }
      }
    }

    String[] buff = new String[h];

    for (i = 0; i < h; i++)
    {
      if ((w * (i + 1)) < buffLen)
        buff[i] = bufferString.substring(w * i, w * (i+1));
      else
        if ((w * (i + 1)) >= buffLen && (w * i) < buffLen)
          buff[i] = bufferString.substring(w * i, buffLen);
        else
          buff[i] = "";
    }

    return buff;
  }

  /**
  * Method Format string
  *
  * @param The string to be format.
  *
  */
  public static String[] buffer2Message(String bufferString)
  {
    return buffer2Message(bufferString, LCD_WIDTH, 3);
  }

  public static int stringBytesLength ( String str ) {
    if (null == str || "".equals(str))
      return 0;

    int count = 0;
    int strLen = str.length();
    char ch;

    for (int i = 0; i < strLen; i ++){
      ch = str.charAt(i);
      count ++;
      if (ch > 0x7F)
        count ++;
    }
    return count;
  }
  /**
  * Method fill string
  *
  * @param The string to be format.
  *
  */
  /**
  * 将字符串补充或截取为指定长度的byte数组
   * @param   formatString   需要处理的字符串
   * @param   fillLength     处理后的字符长度
   * @param   fillChar       如果原始字符串长度不够，需要补充的字符
   * @param   leftFillFlag   左补标志，如果leftFillFlag=true，则左补指定的字符
   * 
   * @return  返回指定长度的byte array
  */  public static byte[] fillStringBytes(String formatString, int fillLength, char fillChar, boolean leftFillFlag)
  {
    if (fillLength < 1)
      return null;
    
    if (null == formatString)
    {
      formatString = "";
    }
    
    int strLen = formatString.length();
    byte[] asciiBytes = new byte[fillLength];
    int i;
    for(i = 0; i < fillLength; i ++)
      asciiBytes[i] = (byte)fillChar; // 填充需要补充的字符

    if (strLen > 0)
    {

      
      int count = 0;
      byte[] strBytes;
      try
      {
        strBytes = formatString.getBytes("GBK");
        count = strBytes.length;
      } catch (UnsupportedEncodingException e)
      {
        // TODO: 字符集错误时，使用默认的UNICODE编码
        int ch;
        strBytes = new byte[strLen *2]; 
        
        for (i = 0; i < strLen; i ++){
          ch = (formatString.charAt(i) & 0xFFFF);
          if (ch > 0x7F)
          { // 非ASCII字符，需要占用2个字节，先保存高位字节
            strBytes[count] = (byte)((ch >> 8) & 0xFF);
            count ++;
          }
          strBytes[count] = (byte)(ch & 0xFF);
          count ++;
        }
      } 

      
      if (count >= fillLength)
      { // 当前字符串的bytes长度，比需要的长度长，则截取指定的长度
        System.arraycopy(strBytes, 0, asciiBytes, 0, fillLength);
      } 
      else 
      {
        strLen =  fillLength - count; // 需要补充的字符长度
        if (true == leftFillFlag)
        {  // 左补
          System.arraycopy(strBytes, 0, asciiBytes, strLen, count);
        } else
        {  // 右补
          System.arraycopy(strBytes, 0, asciiBytes, 0, count);
        }      
      }

    }

    return asciiBytes;
  }
  
  /**
  * Method fill string
  *
  * @param The string to be format.
  *
  */
  public static String fillString(String formatString, int length, char fillChar, boolean leftFillFlag)
  {
    if (null == formatString)
    {
      formatString = "";
    }
    int strLen = formatString.length();
    if (strLen >= length)
    {
      if (true == leftFillFlag)  // left fill
        return formatString.substring(strLen - length, strLen);
      else
        return formatString.substring(0, length);
    } else
    {
      StringBuffer sbuf = new StringBuffer();
      int fillLen = length - formatString.length();
      for (int i = 0; i < fillLen; i++)
      {
        sbuf.append(fillChar);
      }

      if (true == leftFillFlag)  // left fill
      {
        sbuf.append(formatString);
      } else
      {
        sbuf.insert(0, formatString);
      }
      String returnString = sbuf.toString();
      sbuf = null;
      return returnString;
    }
  }

  /**
  * Method fill string
  *
  * @param The string to be format.
  *
  */
  public static String fillSpace(String formatString, int length)
  {
    return fillString(formatString, length, ' ', false);
  }

  /**
  * Method Format string
  *
  * @param The string to be format.
  *
  */
  public static String fillZero(String formatString, int length)
  {
    return fillString(formatString, length, '0', true);
  }


  /**
  * Method Format string
  *
  * @param The string to be format.
  *
  */
  public static String formatLine(String formatString, boolean leftFillFlag)
  {
    return fillString(formatString, LCD_WIDTH, ' ', leftFillFlag);
  }


  private static final char[] space8 = { ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' '};
  /**
  * Method fill space , converted String lenth to LCD_WIDTH
  *
  * @param The string to be format.
  *
  */
  public static String fillShowSpace(String formatString)
  {
    if(null == formatString)
      return "";

    if (formatString.length() <= LCD_WIDTH)
    {
      int len = 8 - (formatString.length() / 2);
      StringBuffer sbuf = new StringBuffer();
      sbuf.append(space8, 0, len);
      sbuf.append(formatString);
      sbuf.append(space8, 0, len);
      sbuf.setLength(LCD_WIDTH);

      String returnString = sbuf.toString();
      sbuf = null;
      return returnString;
    } else
    {
      return formatString.substring(0, LCD_WIDTH);
    }
  }

  /**
       * @param s source string (with Hex representation)
       * @return byte array
       */
  public static byte[] hexStringToBytes (String s)
  {
    if (null == s)
      return null;

    return hexStringToBytes (s, 0, s.length());
  }
  /**
   * @param   hexString   source string (with Hex representation)
   * @param   offset      starting offset
   * @param   count       the length
   * @return  byte array
   */
  public static byte[] hexStringToBytes(String hexString, int offset, int count)
  {
    if (null == hexString || offset < 0 || count < 2 || (offset + count) > hexString.length())
      return null;

    byte[] buffer =  new byte[count >> 1];
    int stringLength = offset + count;
    int byteIndex = 0;
    for(int i = offset; i < stringLength; i++)
    {
      char ch = hexString.charAt(i);
      if (ch == ' ')
        continue;
      if (ch == '\r' || ch == '\n')
        continue;
      byte hex = isHexChar(ch);
      if (hex < 0)
        return null;
      int shift = (byteIndex%2 == 1) ? 0 : 4;
      if ((byteIndex>>1) >= buffer.length)
        break;
      buffer[byteIndex>>1] |= hex << shift;
      byteIndex++;
    }
    byteIndex = byteIndex>>1;
    if (byteIndex > 0) {
      if (byteIndex < buffer.length) {
        byte[] newBuff = new byte[byteIndex];
        System.arraycopy(buffer, 0, newBuff, 0, byteIndex);
        buffer = null;
        return newBuff;
      }
    } else {
      buffer = null;
    }
    return buffer;
  }
  /**
   * @param   hexString   source string (with Hex representation)
   * @param   offset      starting offset
   * @param   count       the length
   * @param   buffer
   * @param   bufferOffset
   * @return  buffer length
   */
  public static int hexStringToBytes(String hexString, int offset, int count, byte[] buffer, int bufferOffset)
  {
    if (null == hexString || offset < 0 || count < 2 || (offset + count) > hexString.length())
      return -1;

    int stringLength = offset + count;
    int byteIndex = 0;
    for(int i = offset; i < stringLength; i++)
    {
      char ch = hexString.charAt(i);
      if (ch == ' ')
        continue;
      byte hex = isHexChar(ch);
      if (hex < 0)
        return -1;
      int shift = (byteIndex%2 == 1) ? 0 : 4;
      buffer[bufferOffset + (byteIndex>>1)] |= hex << shift;
      byteIndex++;
    }
    byteIndex = byteIndex>>1;
    return byteIndex;
  }
  public static int hexStringToBytes(String hexString, byte[] buffer, int bufferOffset)
  {
    if (null == hexString || buffer == null || bufferOffset < 0)
      return -1;
    return  hexStringToBytes(hexString, 0, hexString.length(), buffer, bufferOffset);

  }
  public static void appendHex(StringBuffer stringbuffer, byte byte0)
  {
    stringbuffer.append(toHexChar(byte0 >> 4));
    stringbuffer.append(toHexChar(byte0));
  }

  public static String toHexString(byte abyte0[], int beginIndex, int endIndex, boolean spaceFlag)
  {
    if(null == abyte0)
      return null;
    if(0 == abyte0.length)
      return "";
    StringBuffer sbuf = new StringBuffer();
    appendHex(sbuf, abyte0[beginIndex]);
    for(int i = (beginIndex + 1); i < endIndex; i++)
    {
      if (spaceFlag)
        sbuf.append(' ');
      appendHex(sbuf, abyte0[i]);
    }
    String returnString = sbuf.toString();
    sbuf = null;
    return returnString;
  }

  public static String toHexString(byte abyte0[], int beginIndex, int endIndex)
  {
    if(null == abyte0)
      return null;
    return toHexString(abyte0, beginIndex, endIndex, true);
  }

  public static String toHexString(byte abyte0[], boolean spaceFlag)
  {
    if(null == abyte0)
      return null;
    return toHexString(abyte0, 0, abyte0.length, spaceFlag);
  }

  /**
  * Method convert byte[] to HexString
  *
  * @param The string to be format.
  *
  */
  public static String toHexString(byte abyte0[])
  {
    if(null == abyte0)
      return null;
    return toHexString(abyte0, 0, abyte0.length, true);
  }
  public static String toHexString(char achar0)
  {
    return toHexString((byte)achar0);
  }
  public static String toHexString(byte abyte0)
  {
    StringBuffer sbuf = new StringBuffer();
    appendHex(sbuf, abyte0);

    String returnString = sbuf.toString();
    sbuf = null;
    return returnString;
  }

  /**
   * Return true if the string is HexChars(1234567890abcdefABCDEF).
   *
   */
  public static byte isHexChar(char ch)
  {
    if ('a' <= ch && ch <= 'f')
      return (byte)(ch - 'a' + 10);
    if ('A' <= ch && ch <= 'F')
      return (byte)(ch - 'A' + 10);
    if ('0' <= ch && ch <= '9')
      return (byte)(ch - '0');

    return -1;
  }
 /**
  * Method Check String
  *
  * @param The string to be format.
  *
  * @param checkSpaceFlag=false: skip the space.
  *
  */
  public static boolean isHexChar(String hexString, boolean checkSpaceFlag)
  {
    if (null == hexString || 0 == hexString.length())
      return false;

    int hexLen = hexString.length();
    int hexCharCount = 0;
    char ch;
    for(int i = 0; i < hexLen; i++)
    {
      ch = hexString.charAt(i);
      if (ch == ' ') {
        if (checkSpaceFlag) return false;
      } else {
        if (isHexChar(ch) < 0)
          return false;
        else
          hexCharCount++;
      }
    }

    if (hexCharCount % 2 != 0)
      return false;

    return true;
  }
  /**
   * Method Check String
   *
   * @param The string to be format.
   *
   */
  public static boolean isHexChar(String hexString)
  {
    return isHexChar(hexString, true);
  }

  /**
   * Return true if the string is alphanum.
   * <code>{letter digit }</code>
   *
   **/
  public static boolean isLetterNumeric ( String s ) {
      int i = 0, len = s.length();
      while ( i < len && ( Character.isLowerCase(s.charAt(i)) ||
       Character.isUpperCase(s.charAt(i) ) ||
        Character.isDigit(s.charAt(i)) )){
          i++;
      }
      return ( i >= len );
  }
  /**
   * Return true if the string is Numeric.
   * <code>{digit }</code>
   *
   **/
  public static boolean isNumeric ( String s ) {
      int i = 0, len = s.length();
      while ( i < len && (Character.isDigit(s.charAt(i)))){
          i++;
      }
      return ( i >= len );
  }
  
  public static String stringXOR(String str, String key)
  {
    if (str == null || key == null)
      return null;
    
    int strLen = (str == null?0:str.length());
    int keyLen = (key == null?0:key.length());
    if (strLen <= 0 || keyLen <= 0)
      return str;
    
    StringBuffer strBuff = new StringBuffer();
    for (int i = 0; i < strLen; i++)
    {
      strBuff.append((char)((str.charAt(i) ^ key.charAt(i % keyLen)) & 0xFFFF));
    }
    
    return strBuff.toString();
  }
}
