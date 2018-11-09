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
 * File name:            ISOTable.java
 * 
 * Originally developed: Commands.Wu
 *
 * Create date :         2006-10-23
 * 
 * Description:          Basic structure of field.
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


public class ISOTable
{
  protected short fieldLen;
  protected byte fieldType;

  /**
   * @param len -
   *          fieldLen
   * @param type -
   *          fieldType
   */
  public ISOTable(short len, byte type)
  {
    fieldLen = len;
    fieldType = type;
  }


  // ISO 8583 Field Format, mask = 0x03(0000 0011)
  public static final byte F_FIX    = 0x00; // Fixed length
  public static final byte F_LLVAR  = 0x01; // LL = length of variable data element that follows, 01 through 99
  public static final byte F_LLLVAR = 0x02; // LLL = length of variable data element that follows, 001 through 999

  // ISO 8583 Field Attribute, mask = 0xFC(1111 1100)
  public static final byte A_BIN  = 0x00;  // Binary representation of data
  public static final byte A_N    = 0x04;  // numeric digits, 0 through 9(BCD Code)
  public static final byte A_AN   = 0x08;  // alphabetic and numeric characters
  public static final byte A_ANS  = 0x0C;  // alphabetic, numeric and special characters
  
  public static final byte A_X    = 0x10;  // "C" for credit, "D" for debit and shall always be associated 
                       // with a numeric amount data element, i.e., x+ n 16 in amount, 
                       // net reconciliation means prefix "c" or "D" and 16 digits of amount, net reconciliation.

  public static final byte A_P    = 0x20;  // justify type  1 left justify, 0 right justify

  public byte getFieldAttribute()
  {
    return (byte)(fieldType & 0xFC);
  }
  public byte getFieldFormat()
  {
    return (byte)(fieldType & 0x03);
  }
  public short getFieldDataLength(int dataLength)
  {
    return getDataLength(dataLength, false);
  }
  public short getFactualDataLength(int dataLength)
  {
    return getDataLength(dataLength, true);
  }
  private short getDataLength(int dataLength, boolean factualLengthFlag)
  {
    int fieldLength = 0; 
    
    if (getFieldFormat() == F_FIX)  //Fixed length
      fieldLength = fieldLen;
    else
      if (dataLength > fieldLen)
        fieldLength = fieldLen;
      else
        fieldLength = dataLength;
   
  	// Switch on the message type 
  	switch (getFieldAttribute())
  	{
  		case A_N:		// numeric digits, 0 through 9(BCD Code)
  			// Make length even and divide by 2 
  		  if (factualLengthFlag == false)
  		    fieldLength = (fieldLength + 1)/ 2;
  			break;

//  		case A_BIN:	// Binary representation of data
//  			// Divide by 8 
//  			fieldLength = fieldLength / 8;
//  			break;

//  		case A_AN:		// alphabetic and numeric characters
//  		case A_ANS:	// alphabetic, numeric and special characters
//  			fieldLength = fieldLength;
//  			break;
  	}
  	return (short)fieldLength;
  }

}