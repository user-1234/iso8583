/**
 * Copyright 2006 - 2008 BLUEBAMBOO International Inc.
 *           All rights reserved.
 *
 *
 * BLUEBAMBOO PROPRIETARY/CONFIDENTIAL.
 *
 */

/**
 * File name:            ISOPackager.java
 *
 * Originally developed: Commands.Wu
 *
 * Create date :         2006-10-23
 *
 * Description:          The ISOPackager class.
 *
 * Version:              0.1
 *
 * Contributors:
 *
 * Modifications:
 * name          version           reasons
 *
 */
package com.commands;

import com.commands.TLVProcess;
import com.commands.iso8583.pack.iso8583.ISO8583;
import com.commands.util.NumberUtil;
import com.commands.util.StringUtil;
import com.commands.util.Tracer;

public class Test8583Unpack
{
  public static StringBuffer dataResult = new StringBuffer();

  private static ISO8583 iso = new ISO8583();



  protected static final int ERROR_BIT_INDEX      = -1 ;
  protected static final int ERROR_BITMAP_DEFINE  = -2 ;
  protected static final int ERROR_DATA_LENGTH    = -3 ;
  protected static final int ERROR_BUFFER_OVERFLOW= -4 ;
  protected static final int ERROR_UNPACK_BUFFER  = -5 ;
  protected static final int ERROR_DATA_IS_NULL   = -6 ;
  protected static final int ERROR_MESSAGE_ID     = -7 ;
  protected static final int ERROR_FIELD_DATA     = -8 ;
  protected static final int ERROR_DATA_NOT_SAME  = -9 ;
  protected static final int ERROR_PACK_HEAD_DATA = -10;
  protected static final int ERROR_TRANS_TYPE     = -11;
  protected static final int ERROR_DATA_TYPE      = -12;
  protected static final int ERROR_DATE_FORMAT    = -13;

  private static String getErrorMessage(int ret)
  {
    switch (ret){
    case ERROR_BIT_INDEX:
      return "数据位图索引错误";
    case ERROR_BITMAP_DEFINE:
      return "数据位图定义错误";
    case ERROR_DATA_LENGTH:
      return "数据长度错误";
    case ERROR_BUFFER_OVERFLOW:
      return "数据缓存溢出";
    case ERROR_UNPACK_BUFFER:
      return "数据解析错误";
    case ERROR_DATA_IS_NULL:
      return "当前数据为空";
    case ERROR_MESSAGE_ID:
      return "数据消息类型错误";
    case ERROR_FIELD_DATA:
      return "数据域错�????";
    case ERROR_DATA_NOT_SAME:
      return "接收数据不一�????";
    case ERROR_PACK_HEAD_DATA:
      return "数据报文头错�????";
    case ERROR_TRANS_TYPE:
      return "交易类型错误";
    case ERROR_DATA_TYPE:
      return "数据类型错误";
    case ERROR_DATE_FORMAT:
      return "日期格式错误";
    }
    return "未知错误(" + ret + ")";
  }

  public static int unpack(byte[] databuf)
  {
    return unpack(databuf, 11);
  }
  public static int unpackFullPack(byte[] databuf)
  {
    if (Tracer.ENABLE_DEBUG) Tracer.printLine("unpack [" + (databuf!=null?"" +databuf.length:"null") + "]");
    if (Tracer.ENABLE_DEBUG) Tracer.dump(databuf);

    int retCode = 0;

    if (databuf == null || databuf.length <= 0)
      retCode = ERROR_DATA_LENGTH;

    if (retCode < 0) {
      if (Tracer.ENABLE_DEBUG) Tracer.printLine("ISOPackager procHead error:" + Test8583Unpack.getErrorMessage(retCode));
      return retCode;
    }
    int i = 0;
    if (Tracer.ENABLE_DEBUG) Tracer.printLine("Length: " + StringUtil.toHexString(databuf, i, i + 2) + " [" + NumberUtil.byte2ToShort(databuf, i) + "]");
    i = i + 2;
    if (Tracer.ENABLE_DEBUG) Tracer.printLine("TPDU  : " + StringUtil.toHexString(databuf, i, i + 5));
    i = i + 5;
    if (Tracer.ENABLE_DEBUG) Tracer.printLine("APDU  : " + StringUtil.toHexString(databuf, i, i + 6));
    i = i + 6;

    return unpackData(databuf, i);
  }

  public static int unpack(byte[] databuf, int offset)
  {
    if (Tracer.ENABLE_DEBUG) Tracer.printLine("unpack [" + (databuf!=null?"" +databuf.length:"null") + "]");
    if (Tracer.ENABLE_DEBUG) Tracer.dump(databuf);

    int retCode = offset;

    if (databuf == null || databuf.length <= offset)
    	retCode = ERROR_DATA_LENGTH;

    if (retCode < 0) {
      if (Tracer.ENABLE_DEBUG) Tracer.printLine("ISOPackager procHead error:" + Test8583Unpack.getErrorMessage(retCode));
      return retCode;
    }
    return unpackData(databuf, retCode);
  }
  public static int unpackData(byte[] databuf, int offset)
  {
    dataResult.delete(0, dataResult.length());
    
    byte[] buffer = null;

    int retCode = offset;

    if (Tracer.ENABLE_DEBUG) Tracer.printLine("F_MessageType=" + StringUtil.toHexString(databuf, retCode, retCode + 2));

    retCode += 2; // skip message ID

    /* Get Bitmap bytes */
    int bitNumber = 8;
    if ((databuf[retCode] & 0x80) != 0)
      bitNumber = 16;

    byte[] currnetBitMap = new byte[bitNumber];
    System.arraycopy( databuf, retCode, currnetBitMap, 0, bitNumber);
    if (Tracer.ENABLE_DEBUG) Tracer.printLine("ISOPackager currnetBitMap bitNumber=" + bitNumber + " dataOffset=" + retCode);
    if (Tracer.ENABLE_DEBUG) Tracer.dump(currnetBitMap);
    retCode += bitNumber;

    int i, j, bit;
    byte bitmask;
    String card = "";
    byte[] processCode = new byte[3];
    if (Tracer.ENABLE_DEBUG)
    {
      String msg = "";
      for (i = 0; i < bitNumber; i++)
      {
        bitmask = (byte) 0x80;
        for (j = 0; j < 8; j++, bitmask = (byte) ((bitmask & 0xFF) >>> 1))
        {
          /* Check bitmap flag */
          if ((currnetBitMap[i] & bitmask) == 0)
            continue;

          /* Count isoTable[] index */
          bit = (i << 3) + j + 2;
          msg = msg + (bit-1) + ",";
        }
      }
      Tracer.printLine("ISOPackager currnetBitMap [" + msg + "]");
    }
    /* format dataBuffer elements to iso */
    for (i = 0; i < bitNumber; i++)
    {
      bitmask = (byte) 0x80;
      for (j = 0; j < 8; j++, bitmask = (byte) ((bitmask & 0xFF) >>> 1))
      {
        if (i == 0 && j == 0)
          continue; // Jumped over the expansion sign of bitmap

        /* Check bitmap flag */
        if ((currnetBitMap[i] & bitmask) == 0)
          continue;

        /* Count isoTable[] index */
        bit = (i << 3) + j + 1;
        retCode = iso.getBit(bit, databuf, retCode);
        if (retCode < 0)
          return retCode;
        buffer = iso.getBitData();
        if (Tracer.ENABLE_DEBUG) Tracer.printLine("ISOPackager bit=" + (bit) + " [" + StringUtil.toString(buffer) + "]");
        switch(bit)
        {
        case 2:
          card = StringUtil.toString(buffer);
          break;
        case 3:
          processCode = StringUtil.hexStringToBytes(StringUtil.toString(buffer));
          if (processCode[0] == (byte)0x88)
          {
            dataResult.append("虚拟账户消费" + "|");
            dataResult.append(StringUtil.toString(StringUtil.hexStringToBytes(card)) + "|");
          } 
          else if (processCode[0] == (byte)0x00)
          {
            dataResult.append("电子钱包消费" + "|");
            dataResult.append(card + "|");
          } 
          else
          {
            dataResult.append("无法�??" + StringUtil.toString(buffer) + "|");
            dataResult.append(card + "|");
          } 
          break;
        case 4: // 金额
          dataResult.append(NumberUtil.parseInt(buffer, 0, buffer.length, 10) + "|");
          break;
        case 11: // pos流水�??
          dataResult.append(StringUtil.toString(buffer) + "|");
          break;
        case 12: // 交易时间
          dataResult.append(StringUtil.toString(buffer) + "|");
          break;
        case 13: // 交易日期
          dataResult.append("2017"+StringUtil.toString(buffer) + "|");
          break;
//        case 14:
//        case 22:
//        case 24:
//        case 25:
        case 37:
//        case 39:
        case 41: // 终端编号
        case 42: // 商户编号
        case 48:
//        case 49:
//        case 54:
        case 61:
          dataResult.append(StringUtil.toString(buffer) + "|");
          break;
        case 55:
          if (processCode[0] == (byte)0x88)
          {
            dataResult.append("" + "|");
          } 
          else if (processCode[0] == (byte)0x00)
          {
            dataResult.append(StringUtil.toHexString(buffer, false) + "|");
          } 
          if (Tracer.ENABLE_DEBUG) TLVProcess.TlvUnpack(buffer);
          break;
        }
      }
    }
    buffer = null;
    return retCode;
  }

}