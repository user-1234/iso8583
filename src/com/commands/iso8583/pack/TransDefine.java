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
 * File name:            TransDefine.java
 * 
 * Originally developed: Commands.Wu
 *
 * Create date :         2006-10-23
 * 
 * Description:          This TransDefine Struct interface.
 * 
 * Version:              0.1
 * 
 * Contributors:         
 * 
 * Modifications: 
 * name          version           reasons
 * 
 */
package com.commands.iso8583.pack;

import com.commands.iso8583.pack.iso8583.ISOField;

public class TransDefine implements ISOField
{

  public final static byte TRAN_SALE             = 1 ; // 消费
  public final static byte TRAN_QPBOC            = 2 ; // 电子现金交易上传
  public final static byte TRAN_BALANCE          = 3 ; // 余额查询
  public final static byte TRAN_UPLOAD           = 4 ; // 上传数据
  public final static byte TRAN_LOGIN            = 5 ; // 签到


//==== ISO8583 Define Start ====
  public static final byte REQUEST_MESSAGE_TYPE  = 0;
  public static final byte REQUEST_PROCESS_CODE  = 1;
  public static final byte REQUEST_BIT_MAP       = 2;

  public static byte[] getISO8583Define(int transType, byte dataType)
  {
    switch (transType){
    // Request MessageType, Request ProcessCode, Request BitMap, Response BitMap
    case TRAN_SALE:
    case TRAN_QPBOC:
      switch (dataType){
       case REQUEST_MESSAGE_TYPE : return new byte[]{(byte)'0',(byte)'2',(byte)'0',(byte)'0'};
       case REQUEST_PROCESS_CODE : return new byte[]{(byte)'0',(byte)'0',(byte)'0',(byte)'0',(byte)'0',(byte)'0'};
       case REQUEST_BIT_MAP      : return new byte[]{F02_PAN,F03_PROC,F04_AMOUNT,F11_STAN,F12_TIME,F13_DATE,F14_EXP,F22_POSE,F23,F25_POCC,F26_CAPTURE,F35_TRACK2,F36_TRACK3,F38_AUTH,F39_RSP,F41_TID,F42_ACCID,F46,F49_CURRENCY,F52_PIN,F53_SCI,F55_ICC,F60,F63,F64_MAC};
      }
      break;
    case TRAN_BALANCE:
      switch (dataType){
       case REQUEST_MESSAGE_TYPE : return new byte[]{(byte)'0',(byte)'2',(byte)'0',(byte)'0'};
       case REQUEST_PROCESS_CODE : return new byte[]{(byte)'3',(byte)'1',(byte)'0',(byte)'0',(byte)'0',(byte)'0'};
       case REQUEST_BIT_MAP      : return new byte[]{F02_PAN,F03_PROC,F11_STAN,F14_EXP,F22_POSE,F23,F25_POCC,F26_CAPTURE,F35_TRACK2,F36_TRACK3,F41_TID,F42_ACCID,F49_CURRENCY,F52_PIN,F53_SCI,F55_ICC,F60, F64_MAC};
      }
      break;
    case TRAN_UPLOAD:
      switch (dataType){
       case REQUEST_MESSAGE_TYPE : return new byte[]{(byte)'0',(byte)'3',(byte)'2',(byte)'0'};
       case REQUEST_BIT_MAP      : return new byte[]{F11_STAN,F41_TID,F42_ACCID,F48,F60};
      }
      break;
    }

    return null;
  }
//==== ISO8583 Define End ====

//==== Transaction Property Define Start ====
  public static final byte T_REV            = 0x01; // need reversal
  public static final byte T_NOCAPTURE      = 0x02; // Do not need capture the transaction
  public static final byte T_NORECEIPT      = 0x04; // Do not need print receipt 
  public static final byte T_OFFLINE        = 0x08; // Offline transaction

  public static boolean getTransProperty(int transType, byte property)
  {
    byte propertyDefine = 0;
    switch (transType)
    {
      case TRAN_SALE            : propertyDefine = (byte)T_REV;    
    }
    if ((propertyDefine & property) != 0)
      return true;
    return false;
  }
  //==== Transaction Property Define End ====
}