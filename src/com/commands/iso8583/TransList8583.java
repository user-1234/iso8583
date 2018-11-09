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
 * File name:            TransList.java
 * 
 * Originally developed: Commands.Wu
 *
 * Create date :         2009-9-29
 * 
 * Description:          
 * 
 * Version:              0.1
 * 
 * Contributors:         
 * 
 * Modifications: 
 * name          version           reasons
 * 
 */
package com.commands.iso8583;

public class TransList8583
{
   
  public String tpduAddress = null;
   
  public static final byte SINGLE_KEY  = 1;
  public static final byte DOUBLE_KEY  = 0;
  public byte keyAlgorithm = SINGLE_KEY;

  public static final byte KEY_WITH_CARD_NO     = 0;
  public static final byte KEY_WITHOUT_CARD_NO  = 1;
  public byte keyWithCardNo = KEY_WITH_CARD_NO;
    
  public byte nextProcess = 0; 
  
  
  public String responseCode = null;  // ISO8583 Field 39

  public byte transType;
  public long amount; // ISO8583 Field 4
  
  public int stan = 0; // ISO8583 Field 11: Systems trace audit number
  public int batchNo = 0; // ISO8583 Field 60.2: Åú´ÎºÅ

  public byte[] pose = new byte[2]; // ISO8583 Field 22
  public byte[] card_sn = new byte[2]; // ISO8583 Field 23¿¨Æ¬ÐòÁÐºÅ
  public String cardNumber = null; // ISO8583 Field 2:ASC
  public byte[] expire = null; 		// ISO8583 Field 14:BCD
  public byte[] track2Data = null; // ISO8583 Field 35
  public byte[] track3Data = null; // ISO8583 Field 36  
  public byte[] year = new byte[2];
  public byte[] date = new byte[2]; // ISO8583 Field 13
  public byte[] time = new byte[3]; // ISO8583 Field 12
  public byte[] authID = null; // ISO8583 Field 38:ASC
  public byte[] rrn = null; // ISO8583 Field 37

  // Merchant setting
  public String terminalID = null; // ISO8583 Field 41
  public String merchantID = null; // ISO8583 Field 42

  
  public byte[] pinBlock = null; // ISO8583 Field 52
  
  public int  iccDataLength = 0; // F55 
  public byte[] iccData = null;  // F55 ICC DATAS

  
  public byte[] f60_data = null; // ISO8583 Field 60
  public byte[] f62_data = null; // ISO8583 Field 62
  public byte[] f63_data = null; // ISO8583 Field 63

  public byte[] rsp_mac = new byte[8]; // ISO8583 Field 64

  // Original transaction info
  public byte[] oldAuthID = null; // ASC
  
  
  public void init()
  {   
    responseCode = null; 

    transType = 0;
    amount = 0L; 
    
    stan = 0;
    batchNo = 0;

    pose = new byte[2];

    track2Data = null;
    track3Data = null;
    cardNumber = null;
    expire = null;    // BCD
    year = new byte[2];
    date = new byte[2]; // ISO8583 Field 13
    time = new byte[3]; // ISO8583 Field 12
    
    authID = null; // ASC
    rrn = null; // ISO8583 Field 37
    
    terminalID = null; // ISO8583 Field 41
    merchantID = null; // ISO8583 Field 42

    pinBlock = null; // ISO8583 Field 52
    
    iccDataLength = 0; // F55 
    iccData = null;  // F55 ICC DATAS
    
    f60_data =  null;
    f62_data =  null;
    f63_data =  null;
    
    rsp_mac = new byte[8];

    // original info
    oldAuthID = null; // ASC
  }


  public String getCardNumber()
  {    
    return cardNumber;
  }

  /**
   * Method convert byte[] to Time
   * 
   * @param The
   *          byte[] format: 0xHH 0xMM 0xSS.
   * 
   */
  public void setTime(byte[] time)
  {
    System.arraycopy(time, 0, this.time, 0, this.time.length);
  }
  
  /**
   * Method convert byte[] to Year Date
   * 
   * @param The
   *          byte[] format: 0xCC 0xYY 0xMM 0xDD.
   * 
   */
  public void setYearDate(byte[] yearDate)
  {
    System.arraycopy(yearDate, 0, this.year, 0, this.year.length);
    System.arraycopy(yearDate, 2, this.date, 0, this.date.length);
  }

}
