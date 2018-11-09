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
 * File name:            ISO8583.java
 * 
 * Originally developed: Commands.Wu
 *
 * Create date :         2006-10-23
 * 
 * Description:          ISO8583 Data main processing program, 
 *    " setBit " setup field Data, "getBit" parse the field data.
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

import com.commands.iso8583.pack.Packager;
import com.commands.util.StringUtil;
import com.commands.util.Tracer;

public class ISO8583
{
  private static final int ERROR_BIT_INDEX      = -1 ;
  private static final int ERROR_BITMAP_DEFINE  = -2 ;
  private static final int ERROR_BUFFER_OVERFLOW= -3 ;
  private static final int ERROR_UNPACK_BUFFER  = -4 ;
  private static final int ERROR_DATA_IS_NULL   = -5 ;
  private static final int ERROR_FIELD_DATA     = -6 ;

  public static String getErrorMessage(int ret)
  {
    switch (ret){
    case ERROR_BIT_INDEX:
      return "数据位图索引错误";
    case ERROR_BITMAP_DEFINE:
      return "数据位图定义错误";
    case ERROR_BUFFER_OVERFLOW:
      return "数据缓存溢出";
    case ERROR_UNPACK_BUFFER:
      return "数据长度错误";
    case ERROR_DATA_IS_NULL:
      return "数据长度错误";
    case ERROR_FIELD_DATA:
      return "数据长度错误";
    }
    return "未知错误(" + ret + ")";
  }
  
  public static final int MAX_PACK_LENGTH    = 1024;
  public static final int MSG_LEN_LENGTH  = 2; // Message data length
  private static final int TPDU_LENGTH  = 5;  // Transport Protocol Data Unit
  /* 
   * ID | Target Address | Source Address
   * NN |  NN NN | NN NN
  */
  private static final int APDU_LENGTH  = 6; // Application Protocol Data Unit(BCD encoded 6=12/2)
  /* 
   * APDU Define:
   *    Application type|Application version|Test flag|Process request|Reserve
   *       N2           |        N2         |    N1   |     N1        |N6
   *    Application type: 
   *    Application version:
   *    Test flag:0-Normal mode, 1-Test mode
   *    Process request:0-no request,1-------other 
  */
  
  public static int ISO8583_MSG_START  = MSG_LEN_LENGTH + TPDU_LENGTH + APDU_LENGTH;
  private static int BITMAP_START       = ISO8583_MSG_START + 2; // ISO8583_MSG_START + MessageID.length

  private static ISOTable[] isoTable = ISO87Pack.isoTable;
  public byte[] dataBuffer = new byte[MAX_PACK_LENGTH];
  public int dataLength;
  private int dataOffset;
  private byte[] extendBitmap = null;

  public static void setPackType(ISOTable[] newIsoTable)
  {
    if (newIsoTable == null)
      isoTable = ISO87Pack.isoTable;
    else
      isoTable = newIsoTable;
  }
  /**
   * No args constructor
   */
  public ISO8583()
  {
    init();
  }
  
  public void init()
  {
    dataOffset = ISO8583_MSG_START;
    dataLength = ISO8583_MSG_START;

    if(dataBuffer == null || dataBuffer.length != MAX_PACK_LENGTH)
    {
      dataBuffer = new byte[MAX_PACK_LENGTH];
    }
    for (int i = 0; i < MAX_PACK_LENGTH; i++)
    {
      dataBuffer[i] = 0;  //
    }
    extendBitmap = null;
  }
  public void appendMessageStart(int length)
  {
    ISO8583_MSG_START = MSG_LEN_LENGTH + TPDU_LENGTH + APDU_LENGTH + length;
    BITMAP_START = ISO8583_MSG_START + 2;
  }
  public byte[] getDataBuffer()
  {
    return dataBuffer;
  }
  public int getDataLength()
  {
    return dataLength;
  }
  public int getDataOffset()
  {
    return dataOffset;
  }
  
  private static final byte[] bitMask = { 
    (byte)0x80, (byte)0x40, (byte)0x20, (byte)0x10,
    (byte)0x08, (byte)0x04, (byte)0x02, (byte)0x01
    };
  private void bitmapSet(int bit)
  { 
    bit --;
    int index = bit / 8;
    if (bit >= 64)
    {
      index = index - 8;
      if (extendBitmap == null)
        extendBitmap = new byte[8];
      extendBitmap[index] = (byte)((extendBitmap[index] | bitMask[bit & 0x07]) & 0xFF);    
    } else {
      index = index + BITMAP_START;
      dataBuffer[index] = (byte)((dataBuffer[index] | bitMask[bit & 0x07]) & 0xFF);    
    }
  }
  public void calcBitmap()
  {
    if (extendBitmap != null)
    {
      // Move the data the 8 bytes backward. 
      System.arraycopy(dataBuffer, BITMAP_START + 8, dataBuffer, BITMAP_START + 8 + 8, MAX_PACK_LENGTH - BITMAP_START - 8);
      // Set the extend bitmap flag
      dataBuffer[BITMAP_START] |= 0x80; 
      // Set the extend bitmap
      System.arraycopy(extendBitmap, 0, dataBuffer, BITMAP_START + 8, extendBitmap.length);
      dataLength = dataLength + 8;
    }
    if (Tracer.ENABLE_DEBUG) Tracer.debug("ISO8583 calcBitmap end[" + dataLength + "][" + StringUtil.toHexString(dataBuffer, 0, dataLength) + "]");   
  }  
  
  public int setBit(byte bitNumber, String fieldData, int length )
  {
    if (fieldData != null)
      return setBit(bitNumber, fieldData.getBytes(), length, false);
    return ERROR_DATA_IS_NULL;
  }
  public int setBit(byte bitNumber, byte[] fieldData, int length)
  {
    return setBit(bitNumber, fieldData, length, false);
  }
  public int setBitBCD(byte bitNumber, byte[] fieldData, int length)
  {
    return setBit(bitNumber, fieldData, length, true);
  }
  public int setBit(byte bitNumber, byte[] fieldData, int length, boolean bcdDataFlag)
  {
    short bit = (short)(bitNumber & 0xFF);
    if (fieldData == null) {
      if (Tracer.ENABLE_DEBUG) Tracer.printLine("ISO8583 setBit Field " + bit + " fieldData=null");
      return ERROR_DATA_IS_NULL;
    }
    
    if (Tracer.ENABLE_DEBUG) Tracer.printLine("ISO8583 setBit Field " + bit + " length=" + length + " dataOffset=" + dataOffset);
    if (Tracer.ENABLE_DEBUG) Tracer.dump(fieldData, 0, (length==0||bcdDataFlag?fieldData.length:length), true);
    if (bit < 0 || isoTable == null || bit > isoTable.length)
      return ERROR_BIT_INDEX; 
    length = (length == 0 ?(bcdDataFlag?fieldData.length * 2:fieldData.length):length);   
    short fieldDataLength = isoTable[bit].getFieldDataLength(length);
    
    // Safety validation on length 
    if (fieldDataLength < 1)
      return ERROR_BITMAP_DEFINE;
    
    byte fieldFormat = isoTable[bit].getFieldFormat();
    if ((dataOffset + fieldDataLength + fieldFormat) > MAX_PACK_LENGTH)
      return ERROR_BUFFER_OVERFLOW;

    if (bit >= 2)
      bitmapSet(bit);
      
    // Switch on the message format 
    switch (fieldFormat)
    {
      case ISOTable.F_LLVAR:    // LL = length of variable data element that follows, 01 through 99
        // Convert length to BCD (1 byte)
        dataBuffer[dataOffset] = ISOUtil.BCD_Digit[length]; 
        dataOffset ++;
        break;
      case ISOTable.F_LLLVAR:   // LLL = length of variable data element that follows, 001 through 999
        // Convert length to BCD (2 bytes)
        dataBuffer[dataOffset] = ISOUtil.BCD_Digit[length / 100];
        dataOffset ++;
        dataBuffer[dataOffset] = ISOUtil.BCD_Digit[length % 100];
        dataOffset ++;
        break;
    }
    
    // Move the Data to the Send Buffer 
    if (isoTable[bit].getFieldAttribute() == ISOTable.A_N) {
      if (bcdDataFlag)
        System.arraycopy(fieldData, 0, dataBuffer, dataOffset, fieldDataLength);
      else
        ISOUtil.asciiToBcd(fieldData, 0, dataBuffer, dataOffset, length, 0);
    } else {
      System.arraycopy(fieldData, 0, dataBuffer, dataOffset, (length < fieldDataLength?length:fieldDataLength));
    }
    if (Tracer.ENABLE_DEBUG) Tracer.printLine("convert Data:");
    if (Tracer.ENABLE_DEBUG) Tracer.dump(dataBuffer, dataOffset, dataOffset + fieldDataLength, true);
    dataOffset += fieldDataLength;
    dataLength = dataOffset;
    
    if (Tracer.ENABLE_DEBUG) Tracer.printLine("dataOffset=" + dataOffset + " fieldDataLength= " + fieldDataLength);
 
    return dataOffset;
  }

  public int setHead(int headEnd)
  {
    if (headEnd == ISO8583_MSG_START) {
      dataOffset = 0;
    } else {
      dataOffset = ISO8583_MSG_START;
      dataLength = dataLength - ISO8583_MSG_START;
    }
    return dataLength;
  }
  private byte[] bitData = null;
  public byte[] getBitData()
  {
    return bitData;
  }
  public int getBit(int bitNumber, byte[] databuf, int offset)//, boolean bcdDataFlag)
  {
    if (bitNumber < 0 || bitNumber > isoTable.length)
      return ERROR_BIT_INDEX;        
    byte fieldFormat;
    int fieldDataLength = 0;
    short factualDataLength = 0;
    
    fieldFormat = isoTable[bitNumber].getFieldFormat();
    if ((offset + fieldFormat) > databuf.length)
      return ERROR_UNPACK_BUFFER;
      
    // Switch on the message format 
    switch (fieldFormat)
    {
      case ISOTable.F_LLVAR:    // LL = length of variable data element that follows, 01 through 99
        // Convert BCD to number(1 byte) 
        fieldDataLength = ((databuf[offset] >> 4) & 0x0F) * 10 + (databuf[offset] & 0x0F);
        offset ++;
        break;
      case ISOTable.F_LLLVAR:   // LLL = length of variable data element that follows, 001 through 999
        // Convert BCD to number(2 byte) 
        fieldDataLength = (databuf[offset] & 0x0F);
        offset ++;
        fieldDataLength = fieldDataLength * 100 + ((databuf[offset] >> 4) & 0x0F) * 10 + (databuf[offset] & 0x0F);
        offset ++;
        break;
    }

    factualDataLength = isoTable[bitNumber].getFactualDataLength(fieldDataLength);
    fieldDataLength = isoTable[bitNumber].getFieldDataLength(fieldDataLength);

    if (Tracer.ENABLE_DEBUG) Tracer.printLine("ISO8583 getBit bit=" + bitNumber + " Offset=" + offset + " fieldLength=" + fieldDataLength + " realLength=" + factualDataLength);
    if (Tracer.ENABLE_DEBUG) Tracer.dump(databuf, offset, offset + fieldDataLength, true);
    if (Packager.PRINT_UNPACK) {
      Packager.printUnpackData = Packager.printUnpackData + ""
      + "\nBit-" + bitNumber + ":offset=" + offset + " length=" + fieldDataLength +"[" + factualDataLength +"]\n"
      + StringUtil.toHexString(databuf, offset, offset + fieldDataLength, true);
    }

    if (fieldDataLength >= 0) {
      if ((offset + fieldDataLength) > databuf.length)
        return ERROR_BUFFER_OVERFLOW;
      if (isoTable[bitNumber].getFieldAttribute() == ISOTable.A_N) {
//        if (bcdDataFlag)
//        {
//          bitData = new byte[fieldDataLength];
//          System.arraycopy(databuf, offset, bitData, 0, fieldDataLength);
//        } else
        {
          bitData = new byte[factualDataLength];
          ISOUtil.bcdToAscii(databuf, offset, bitData, 0, bitData.length, 0);
        }
      } else {
        bitData = new byte[fieldDataLength];
        System.arraycopy(databuf, offset, bitData, 0, fieldDataLength);
      }
    } else {
      return ERROR_FIELD_DATA;
    }
    return offset + fieldDataLength;
  }  
}
