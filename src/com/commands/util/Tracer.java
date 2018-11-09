/**
 * File name:            Tracer.java
 *
 * Originally developed: 
 *
 * Create date :         
 *
 * Description:          This Tracer Tools.
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

import com.commands.Test8583;

public class Tracer
{
  public static final boolean ENABLE_DEBUG  = true; // If it is false , delete LOG information while compiling.

  public static void printLine(String str)
  {
    if (Test8583.disablePrintLog())
    { // 不输出LOG
      return;
    }
//  System.out.print(str + "\r\n");
    System.out.println(str);
  }
  public static void printLineForce(String str)
  {// 强制输出LOG
//  System.out.print(str + "\r\n");
    System.out.println(str);
  }
  public static void print(String level, Object obj)
	{
	  printLine(level + obj.toString()); // + "\r\n");
	}
  /**
   * Method print debug message to console
   *
  * @param The string to be format.
   *
   */
  public static void debug(Object obj)
  {
    print("DEBUG: ", obj);
  }

  /**
   * Method print error message to console
   *
  * @param The string to be format.
   *
   */
  public static void error(Object obj)
  {
    print("ERROR: ", obj);
  }

  /**
   * Method print trace message to console
   *
  * @param The string to be format.
   *
   */
  public static void trace(Object obj)
  {
    print("TRACE: ", obj);
  }

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

  public static void dump(byte abyte0[])
  {
    if(abyte0 == null || abyte0.length == 0)
      return;
    else
      dump(abyte0, 0, abyte0.length, true);
  }
  public static void dump(byte abyte0[], int beginIndex, int endIndex, boolean spaceFlag)
  {
    dump(abyte0, beginIndex, endIndex, spaceFlag, true, true, 0);
  }
  public static int dump(byte abyte0[], int beginIndex, int endIndex, boolean spaceFlag, boolean asciiFlag, boolean lineNumberFlag, int linenumber)
  {
    if(abyte0 == null || abyte0.length == 0)
      return 0;

    int totalLine = (endIndex - beginIndex) / 16;
    int lineNumber, q;
    int offset = beginIndex;
    byte byte0;
    StringBuffer stringbuffer = new StringBuffer(6 + (spaceFlag?48:32));
    StringBuffer asciibuffer = new StringBuffer();
    String printString;

    if (linenumber < 0)
      linenumber = 0;
    else
      linenumber = linenumber % 10000;

    for(int i = 0; i <= totalLine; i++, linenumber++)
    {
      if (offset < endIndex) {
        stringbuffer.delete(0, stringbuffer.length());
        asciibuffer.delete(0, asciibuffer.length());
        if (lineNumberFlag) {
          stringbuffer.append("0000: ");
          lineNumber = linenumber;
          for(byte0 = 3; byte0 >=0; byte0--){
            q = (lineNumber * 52429) >>> (16+3);
            stringbuffer.setCharAt(byte0, toHexChar(lineNumber - ((q << 3) + (q << 1)))); // toHexChar(lineNumber-(q*10))
            lineNumber = q;
            if (0 == lineNumber) break;
          }
        }
        for(int j = 0; j < 16; j++, offset++)
        {
          if (offset < endIndex) {
            byte0 = abyte0[offset];
            stringbuffer.append(toHexChar(byte0 >> 4));
            stringbuffer.append(toHexChar(byte0));
            if (spaceFlag)
              stringbuffer.append(' ');
            if (asciiFlag) {
              if (byte0 >= 0x20 && byte0 <= 0x7E)
                asciibuffer.append((char)byte0);
              else
                asciibuffer.append('.');
            }
          } else {
            stringbuffer.append(' ');
            stringbuffer.append(' ');
            if (spaceFlag)
              stringbuffer.append(' ');
          }
        }
        if (asciiFlag)
          printString = stringbuffer.toString() + "; " + asciibuffer.toString();
        else
          printString = stringbuffer.toString();
        printLine(printString);
      } else {
        break;
      }
    }
    printString = null;
    stringbuffer = asciibuffer = null;
    return linenumber;
  }

}
