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
 * File name:            ISOField.java
 * 
 * Originally developed: Commands.Wu
 *
 * Create date :         2006-10-23
 * 
 * Description:          The ISOField class.
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


public interface ISOField
{
  public static final byte F00_MSGID                       = (byte)0  ;  
  public static final byte F01_BITMAP                      = (byte)1  ;  
  public static final byte F02_PAN                         = (byte)2  ;  
  public static final byte F03_PROC                        = (byte)3  ;  
  public static final byte F04_AMOUNT                      = (byte)4  ;  
  public static final byte F05                             = (byte)5  ;  
  public static final byte F06                             = (byte)6  ;  
  public static final byte F07                             = (byte)7  ;  
  public static final byte F08                             = (byte)8  ;  
  public static final byte F09                             = (byte)9  ;  
  public static final byte F10                             = (byte)10 ; 
  public static final byte F11_STAN                        = (byte)11 ; 
  public static final byte F12_TIME                        = (byte)12 ; 
  public static final byte F13_DATE                        = (byte)13 ; 
  public static final byte F14_EXP                         = (byte)14 ; 
  public static final byte F15_SETTLE_DATE                 = (byte)15 ; 
  public static final byte F16                             = (byte)16 ; 
  public static final byte F17                             = (byte)17 ; 
  public static final byte F18                             = (byte)18 ; 
  public static final byte F19                             = (byte)19 ; 
  public static final byte F20                             = (byte)20 ; 
  public static final byte F21                             = (byte)21 ; 
  public static final byte F22_POSE                        = (byte)22 ; 
  public static final byte F23                             = (byte)23 ; 
  public static final byte F24_NII                         = (byte)24 ; 
  public static final byte F25_POCC                        = (byte)25 ; 
  public static final byte F26_CAPTURE                     = (byte)26 ; 
  public static final byte F27                             = (byte)27 ; 
  public static final byte F28                             = (byte)28 ; 
  public static final byte F29                             = (byte)29 ; 
  public static final byte F30                             = (byte)30 ; 
  public static final byte F31                             = (byte)31 ; 
  public static final byte F32_ACQUIRER                    = (byte)32 ; 
  public static final byte F33                             = (byte)33 ; 
  public static final byte F34                             = (byte)34 ; 
  public static final byte F35_TRACK2                      = (byte)35 ; 
  public static final byte F36_TRACK3                      = (byte)36 ; 
  public static final byte F37_RRN                         = (byte)37 ; 
  public static final byte F38_AUTH                        = (byte)38 ; 
  public static final byte F39_RSP                         = (byte)39 ; 
  public static final byte F40_                            = (byte)40 ; 
  public static final byte F41_TID                         = (byte)41 ; 
  public static final byte F42_ACCID                       = (byte)42 ; 
  public static final byte F43                             = (byte)43 ; 
  public static final byte F44_ADDITIONAL                  = (byte)44 ; 
  public static final byte F45_TRACK1                      = (byte)45 ; 
  public static final byte F46                             = (byte)46 ; 
  public static final byte F47                             = (byte)47 ; 
  public static final byte F48                             = (byte)48 ; 
  public static final byte F49_CURRENCY                    = (byte)49 ; 
  public static final byte F50                             = (byte)50 ; 
  public static final byte F51                             = (byte)51 ; 
  public static final byte F52_PIN                         = (byte)52 ; 
  public static final byte F53_SCI                         = (byte)53 ; 
  public static final byte F54_TIP                         = (byte)54 ; 
  public static final byte F55_ICC                         = (byte)55 ; 
  public static final byte F56                             = (byte)56 ; 
  public static final byte F57                             = (byte)57 ; 
  public static final byte F58                             = (byte)58 ; 
  public static final byte F59                             = (byte)59 ; 
  public static final byte F60                             = (byte)60 ; 
  public static final byte F61                             = (byte)61 ; 
  public static final byte F62                             = (byte)62 ; 
  public static final byte F63                             = (byte)63 ; 
  public static final byte F64_MAC                         = (byte)64 ; 

}