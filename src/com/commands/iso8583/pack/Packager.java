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
 * File name:            Packager.java
 * 
 * Originally developed: Commands.Wu
 *
 * Create date :         2006-10-23
 * 
 * Description:          The Packager class.
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

import com.commands.iso8583.TransList8583;
import com.commands.iso8583.pack.iso8583.HostMessage;
import com.commands.iso8583.pack.iso8583.ISO8583;
import com.commands.iso8583.pack.iso8583.ISOField;
import com.commands.iso8583.pack.iso8583.ISOUtil;
import com.commands.util.StringUtil;
import com.commands.util.Tracer;

public class Packager
{
  public final static boolean PRINT_UNPACK = true; // 测试输出元素使用
  public static String printUnpackData = "";

  private static byte[] F_MessageType = new byte[4];

  private static ISO8583 iso = new ISO8583();
  private static byte[] sendData = null;
  private static int sendDataLength = 0;
  private static int sendDataOffset = 0;
  
  protected static final int ERROR_MESSAGE_ID     = -100;
  protected static final int ERROR_FIELD_DATA     = -101;
  protected static final int ERROR_DATA_NOT_SAME  = -102;
  protected static final int ERROR_PACK_HEAD_DATA = -103;
  

  /**
   * @return Returns the Send data buffer.
   */
  public static byte[] getSendData()
  {
    return sendData;
  }

  /**
   * @return Returns the Send data length.
   */
  public static int getSendDataLength()
  {
    return sendDataLength;
  }
  public static int getSendDataOffset()
  {
    return sendDataOffset;
  }
  public final static byte MAC_SET_DATA    = 1;
  public final static byte MAC_CHECK_DATA  = 2;
  public final static byte MAC_VERIFY      = 3;

  private static byte macState = -1;
  public static void setMacState(byte mac_state)
  {
    macState = mac_state;
  }
  public static byte getMacState()
  {
    return macState;
  }
  public static int geMacDataLength()
  {
    if (sendDataOffset > 0)  // No message head
      return sendDataLength - 8; // Skip Mac result
    else
      return sendDataLength - ISO8583.ISO8583_MSG_START - 8; // Skip message head
  }
  public static int getMacDataOffset()
  {
    return (sendDataOffset > 0?sendDataOffset:ISO8583.ISO8583_MSG_START);
  }
  public static boolean setMacData(byte[] data)
  {
    if (data != null && data.length >= 8) {
      System.arraycopy(data, 0, sendData, sendDataLength - 8, 8);
      return true;
    }
    return false;
  }

  private static int setHead(ISO8583 iso, TransList8583 trans, boolean withHead)
  {
    if (withHead) {
      int iso8583Length = iso.getDataLength();
      if (iso8583Length < 0)
        return iso8583Length;

      int index = ISO8583.MSG_LEN_LENGTH;
      // TPDU
      ISOUtil.asciiToBcd(trans.tpduAddress.getBytes(), 0, iso.dataBuffer, index, trans.tpduAddress.length()>10?10:trans.tpduAddress.length(), 0);
      index ++;   // TPDU-ID
      index += 2; // TPDU-Target Address
      index += 2; // TPDU-Source Address

      // APDU  
      iso.dataBuffer[index] = (byte)0x61; // IC卡金融支付类应用为
      index ++; // Application Type
      iso.dataBuffer[index] = (byte)0x01;
      index ++; // Application Version
      
      iso.dataBuffer[index] = (byte)(0 << 4); // Test Flag
      iso.dataBuffer[index] = (byte)(iso.dataBuffer[index] | (trans.nextProcess & 0x0F));  // Process Request
      index ++;   // Test Flag + Process Request
      index += 3; // Reserve
      
      iso8583Length =  iso8583Length - ISO8583.MSG_LEN_LENGTH;
      if (ISO8583.MSG_LEN_LENGTH == 2) {
        // Converting short into byte array.
        iso.dataBuffer[1] = new Integer(iso8583Length & 0xff).byteValue();
        iso8583Length = iso8583Length >> 8;
        iso.dataBuffer[0] = new Integer(iso8583Length & 0xff).byteValue();
      }
      
      return iso.setHead(index);
    } else {
      return iso.setHead(0);
    }

  }
  private static int procHead(byte[] databuf, TransList8583 trans, boolean withHead)
  {
    if (withHead) {
      if (databuf != null && databuf.length > ISO8583.ISO8583_MSG_START) {
        int index = ISO8583.MSG_LEN_LENGTH;
                 
        // TPDU
        index ++;   // TPDU-ID
        index += 2; // TPDU-Target Address
        index += 2; // TPDU-Source Address
        
        index ++; // Application Type
        index ++; // Application Version
        
        byte flag = (byte)((databuf[index] >> 4) & 0x0F); // Test Flag
        flag = (byte)(databuf[index] & 0x0F); // Process Request
        if(flag != 0) {
          if(Tracer.ENABLE_DEBUG)Tracer.debug("procHead save nextProcess=" + flag);
          trans.nextProcess=flag;
        } else {
          if(trans.nextProcess != 0)  {
            if(Tracer.ENABLE_DEBUG)Tracer.debug("procHead clear param.nextProcess=" + trans.nextProcess);
            trans.nextProcess = (byte)0x00;
          }
        }
        index ++;   // Test Flag + Process Request
        index += 3; // Reserve
        return index;
      } else {
        return ERROR_PACK_HEAD_DATA;
      }
    }
    return 0;
  }

  public static int pack(TransList8583 trans, boolean reversalFlag, boolean uploadFlag)
  {
    if (Tracer.ENABLE_DEBUG) Tracer.debug("Packager pack start");
    iso.appendMessageStart(0);

    iso.init();
    macState = -1;

    if(Tracer.ENABLE_DEBUG) Tracer.debug("transType = " + trans.transType);

    if (uploadFlag) {
      F_MessageType = TransDefine.getISO8583Define(TransDefine.TRAN_UPLOAD, TransDefine.REQUEST_MESSAGE_TYPE);
    } else if(reversalFlag){
      F_MessageType = "0400".getBytes();
    } else {
      F_MessageType = TransDefine.getISO8583Define(trans.transType, TransDefine.REQUEST_MESSAGE_TYPE);
    }

    if (F_MessageType == null)
      return ERROR_MESSAGE_ID;
    iso.setBit(ISOField.F00_MSGID, F_MessageType, 0);
    
    byte[] buffer = {0x00,0x00,0x00,0x00,0x00,0x00,0x00,0x00};
    iso.setBit(ISOField.F01_BITMAP, buffer, 0);
    
    byte[] currentBitMap = TransDefine.getISO8583Define(trans.transType, TransDefine.REQUEST_BIT_MAP);
    int bitMapLength = currentBitMap.length;    

    for (int i = 0; i < bitMapLength; i++)
    {
      switch (currentBitMap[i])
      {
      case ISOField.F02_PAN:
        iso.setBit(ISOField.F02_PAN, trans.getCardNumber(), 0);
        break;
      case ISOField.F03_PROC:
        iso.setBit(ISOField.F03_PROC, TransDefine.getISO8583Define(trans.transType, TransDefine.REQUEST_PROCESS_CODE), 0);
        break;
      case ISOField.F04_AMOUNT:
        buffer = new byte[6];
        ISOUtil.longToBcd(trans.amount, buffer, 0, buffer.length); // BCD Data
        iso.setBitBCD(ISOField.F04_AMOUNT, buffer, 0);
        break;
      case ISOField.F11_STAN:
        buffer = new byte[3];
        ISOUtil.longToBcd(trans.stan, buffer, 0, buffer.length);  // BCD Data
        iso.setBitBCD(ISOField.F11_STAN, buffer, 0);
        break;
      case ISOField.F12_TIME:
        iso.setBitBCD(ISOField.F12_TIME, trans.time, 0);
        break;
      case ISOField.F13_DATE:
        iso.setBitBCD(ISOField.F13_DATE, trans.date, 0);
        break;
      case ISOField.F14_EXP:
        iso.setBitBCD(ISOField.F14_EXP, trans.expire, 0);
    	  break;
      case ISOField.F22_POSE:
    	  iso.setBitBCD(ISOField.F22_POSE, trans.pose,0);
        break;
      case ISOField.F23:
        iso.setBitBCD(ISOField.F23, trans.card_sn,0);
        break;

      case ISOField.F25_POCC:
    	  if (Tracer.ENABLE_DEBUG) Tracer.debug("Packager pack Field 25");
    	  buffer = new byte[1];
    	  switch( trans.transType )
      	{
      		case TransDefine.TRAN_SALE:
      		  buffer[0] = 0x06;
      			break;
      		default:
      		  buffer[0] = 0x00;
      			break;
      	}
       	iso.setBitBCD( ISOField.F25_POCC, buffer, 0 );
       	break;
      
      case ISOField.F26_CAPTURE:
        if (trans.pinBlock != null && trans.pinBlock.length > 0
            && reversalFlag == false)
        {
//          buffer = "06".getBytes();
          buffer = "12".getBytes();
          iso.setBit(ISOField.F26_CAPTURE, buffer, 0);
        }
    	  break;   	  
      case ISOField.F35_TRACK2:
        if (trans.track2Data != null && trans.track2Data.length > 0)
        {
          iso.setBit(ISOField.F35_TRACK2, trans.track2Data, 0);
        }
        break;
      case ISOField.F36_TRACK3:
        if (trans.track3Data != null && trans.track3Data.length > 0)
        {
          iso.setBit(ISOField.F36_TRACK3, trans.track3Data, 0);
        }
    	  break;
      case ISOField.F37_RRN:
        if (trans.rrn != null && trans.rrn.length > 0 && reversalFlag == false)
        {
          iso.setBit(ISOField.F37_RRN, trans.rrn, 0);
        }
    	  break;
      case ISOField.F38_AUTH:
        if (trans.oldAuthID != null && trans.oldAuthID.length > 0) {
          iso.setBit(ISOField.F38_AUTH, trans.oldAuthID, 0);
        } 
        else if (trans.authID != null && trans.authID.length > 0) {
          iso.setBit(ISOField.F38_AUTH, trans.authID, 0);
        }
        break;
      case ISOField.F39_RSP:
    	  if(reversalFlag){
          trans.responseCode = HostMessage.TRANS_TIMEOUT_ERROR;
    		  iso.setBit(ISOField.F39_RSP, trans.responseCode,0);
    	  }
    	  break;
      case ISOField.F41_TID:
        iso.setBit(ISOField.F41_TID, trans.terminalID, 0);
        break;
      case ISOField.F42_ACCID:
        iso.setBit(ISOField.F42_ACCID, trans.merchantID, 0);
        break;
      case ISOField.F46:
        break;
      case ISOField.F48:
        break;
      case ISOField.F49_CURRENCY:
        buffer = new byte[]{(byte)'1',(byte)'5',(byte)'6',};  // RMB
        iso.setBit(ISOField.F49_CURRENCY, buffer,0);
        break;
      case ISOField.F52_PIN:
        if (trans.pinBlock != null && trans.pinBlock.length > 0
            && reversalFlag == false)
        {
          iso.setBit(ISOField.F52_PIN, trans.pinBlock, 0);
        }
        break;
        
      case ISOField.F53_SCI:
        if (trans.pinBlock != null && trans.pinBlock.length > 0
            && reversalFlag == false)
        {
          buffer = new byte[16];
          if(trans.keyWithCardNo == TransList8583.KEY_WITH_CARD_NO) {
//          PIN encrypt  1：ANSI X9.8 Format（without card number）
//                     2：ANSI X9.8 Format（with card number）
            buffer[0] = (byte)'2'; 
          } else {
            buffer[0] = (byte)'1'; 
          }
          if(trans.keyAlgorithm == TransList8583.DOUBLE_KEY) {
//          加密算法标志   0：单倍长密钥算法
//          6：双倍长密钥算法
//          其他取值：其他加密算法(未用)
            buffer[1] = (byte)'6';
          } else {
            buffer[1] = (byte)'0';
          }
          iso.setBit(ISOField.F53_SCI, buffer, 0);
     	  }
     	  break;
    	  
      case ISOField.F54_TIP:
        break;
      case ISOField.F55_ICC:
        if(trans.iccDataLength > 0)
        {
          iso.setBit(ISOField.F55_ICC, trans.iccData, trans.iccDataLength);
        }
        break;

      case ISOField.F60:
        FieldProcess.setF60(iso, trans);
        break;
      case ISOField.F61:
        break;   	  
      case ISOField.F62:
    	  FieldProcess.setF62(iso, trans);
        break;   	  
      case ISOField.F63:
    	  FieldProcess.setF63(iso, trans );
        break;

      case ISOField.F64_MAC:
      	if(uploadFlag == false)
      	{
      	  // Add null MAC data, and set the 64 of bitmap
      	  buffer = new byte[8];
          iso.setBit(ISOField.F64_MAC, buffer, 0);
          macState = MAC_SET_DATA;
      	}
      	break;
      }
    }
    iso.calcBitmap();
    sendDataLength = setHead(iso, trans, true);
    buffer = null;
    currentBitMap = null;
    if (sendDataLength < 0)
      return sendDataLength;
    sendDataOffset = iso.getDataOffset();
    sendData = iso.getDataBuffer();

    if (Tracer.ENABLE_DEBUG) Tracer.debug("Packager pack sendData[" + sendDataLength + "]");
    return sendDataLength;
  }

  
  public static int unpack(byte[] databuf, TransList8583 trans)
  {   
    byte[] buffer = null;
    
    macState = -1;
    trans.responseCode = "FF";
    int retCode = procHead(databuf, trans, true);
    
    if (retCode < 0) {
      if (Tracer.ENABLE_DEBUG) Tracer.debug("Packager procHead error:" + retCode);
      return retCode;
    }
    if (PRINT_UNPACK) printUnpackData = "";
    if (Tracer.ENABLE_DEBUG) Tracer.debug("F_MessageType=" + StringUtil.toHexString(F_MessageType) + " buffer=" + StringUtil.toHexString(buffer));
//    //TODO: check message ID correct
//    if (databuf.length >= (retCode + 2)) {
//      F_MessageType[2] = (byte)(F_MessageType[2] + 1);
//      buffer = ISOUtil.asciiToBcd(F_MessageType);
//      if (ISOUtil.equalByteArray(databuf, retCode, 2, buffer, 0, 2) == false)
//        return Packager.ERROR_MESSAGE_ID;
//    } else {
//      return ERROR_FIELD_DATA;
//    }   
    retCode += 2; // skip message ID

    /* Get Bitmap bytes */
    int bitNumber = 8;
    if ((databuf[retCode] & 0x80) != 0)
      bitNumber = 16;
            
    byte[] currnetBitMap = new byte[bitNumber];
    System.arraycopy( databuf, retCode, currnetBitMap, 0, bitNumber);
    if (Tracer.ENABLE_DEBUG) Tracer.debug("Packager currnetBitMap bitNumber=" + bitNumber + " dataOffset=" + retCode);
    if (Tracer.ENABLE_DEBUG) Tracer.dump(currnetBitMap);
    retCode += bitNumber;
      
    int i, j, bit;
    byte bitmask;
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
        switch (bit)
        {
        case ISOField.F02_PAN:
          break;

        case ISOField.F03_PROC:
          bit = TransDefine.getISO8583Define(trans.transType, TransDefine.REQUEST_PROCESS_CODE).length;
          if((ISOUtil.compareByteArray(buffer,0,buffer.length, TransDefine.getISO8583Define(trans.transType, TransDefine.REQUEST_PROCESS_CODE),0,bit) != 0)) {
            if(Tracer.ENABLE_DEBUG) Tracer.debug("Processcode is not same");
//            return ERROR_DATA_NOT_SAME;
          }     
          break;

        case ISOField.F04_AMOUNT:
          bit = Integer.parseInt(new String(buffer));
          if(bit != trans.amount)
          {
            if(Tracer.ENABLE_DEBUG) Tracer.debug("Amount is not same");
//            return ERROR_DATA_NOT_SAME;
          }
          break;

        case ISOField.F11_STAN:
          bit = Integer.parseInt(new String(buffer));
          if(bit != trans.stan)
          {
            if(Tracer.ENABLE_DEBUG) Tracer.debug("Stan is not same");
//            return ERROR_DATA_NOT_SAME;
          }
          break;

        case ISOField.F12_TIME:
          ISOUtil.asciiToBcd(buffer, trans.time);
          break;
        case ISOField.F13_DATE:
          ISOUtil.asciiToBcd(buffer, trans.date);
          break;

        case ISOField.F14_EXP:
          break;
          
        case ISOField.F15_SETTLE_DATE:
          break;
          
        case ISOField.F22_POSE:
          break;

        case ISOField.F24_NII:
          break;

        case ISOField.F25_POCC:
          break;

        case ISOField.F26_CAPTURE:
          break;
    		case ISOField.F32_ACQUIRER:
    			break;
        case ISOField.F37_RRN:
          trans.rrn = new byte[buffer.length];
          System.arraycopy(buffer, 0, trans.rrn, 0, buffer.length);
          break;
    		case ISOField.F38_AUTH:
    			if(trans.authID != null)
    			{
            trans.oldAuthID = new byte[6];
            System.arraycopy(trans.authID, 0, trans.oldAuthID, 0, 6);
    			}
    			else{
            trans.authID = new byte[6];
    			}
    			System.arraycopy(buffer, 0, trans.authID, 0, 6);
    			break;
        case ISOField.F39_RSP:
          trans.responseCode = new String(buffer);
          break;
          
        case ISOField.F41_TID:
          if((ISOUtil.compareByteArray(trans.terminalID.getBytes(),0,trans.terminalID.length(), buffer,0,buffer.length) != 0))
          {
            if(Tracer.ENABLE_DEBUG) Tracer.debug("TID is not same");
//            return ERROR_DATA_NOT_SAME;
          }
          break;
        case ISOField.F42_ACCID:
          if((ISOUtil.compareByteArray(trans.merchantID.getBytes(),0,trans.merchantID.length(), buffer,0,buffer.length) != 0))
          {
            if(Tracer.ENABLE_DEBUG) Tracer.debug("MID is not same");
//            return ERROR_DATA_NOT_SAME;
          }
          break;

        case ISOField.F44_ADDITIONAL:
          break;
        case ISOField.F48:
          break;
        case ISOField.F49_CURRENCY:
          break;

        case ISOField.F53_SCI:
          break;

        case ISOField.F54_TIP:
          break;

        case ISOField.F60:
          FieldProcess.procF60(buffer, trans);
          break;
        case ISOField.F61:
          break;
        case ISOField.F62:
          FieldProcess.procF62(buffer, trans);
          break;
        case ISOField.F63:
          FieldProcess.procF63(buffer, trans);
          break;

        case ISOField.F64_MAC:
          macState = MAC_CHECK_DATA;
          System.arraycopy(buffer, 0, trans.rsp_mac, 0, buffer.length);
          break;
        }        
      }
    } 
    buffer = null;
    return retCode;
  }

}