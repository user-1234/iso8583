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
 * File name:            ISO87Pack.java
 * 
 * Originally developed: Commands.Wu
 *
 * Create date :         2006-10-23
 * 
 * Description:          ISO 8583 message format.
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

public class ISO87Pack
{
  public static final ISOTable[] isoTable = {
      /* 000 */new ISOTable((short)  4, ((byte)(ISOTable.F_FIX   + ISOTable.A_N   )) ), // "MESSAGE TYPE"),
      /* 001 */new ISOTable((short)  8, ((byte)(ISOTable.F_FIX   + ISOTable.A_BIN )) ), // "BIT MAP"),
      /* 002 */new ISOTable((short) 19, ((byte)(ISOTable.F_LLVAR + ISOTable.A_N   )) ), // "PAN - PRIMARY ACCOUNT NUMBER"),
      /* 003 */new ISOTable((short)  6, ((byte)(ISOTable.F_FIX   + ISOTable.A_N   )) ), // "PROCESSING CODE"),
      /* 004 */new ISOTable((short) 12, ((byte)(ISOTable.F_FIX   + ISOTable.A_N   )) ), // "AMOUNT, TRANSACTION"),
      /* 005 */new ISOTable((short) 12, ((byte)(ISOTable.F_FIX   + ISOTable.A_N   )) ), // "AMOUNT, SETTLEMENT"),
      /* 006 */new ISOTable((short) 12, ((byte)(ISOTable.F_FIX   + ISOTable.A_N   )) ), // "AMOUNT, CARDHOLDER BILLING"),
      /* 007 */new ISOTable((short) 10, ((byte)(ISOTable.F_FIX   + ISOTable.A_N   )) ), // "TRANSMISSION DATE AND TIME"),
      /* 008 */new ISOTable((short)  8, ((byte)(ISOTable.F_FIX   + ISOTable.A_N   )) ), // "AMOUNT, CARDHOLDER BILLING FEE"),
      /* 009 */new ISOTable((short)  8, ((byte)(ISOTable.F_FIX   + ISOTable.A_N   )) ), // "CONVERSION RATE, SETTLEMENT"),
      /* 010 */new ISOTable((short)  8, ((byte)(ISOTable.F_FIX   + ISOTable.A_N   )) ), // "CONVERSION RATE, CARDHOLDER BILLING"),
      /* 011 */new ISOTable((short)  6, ((byte)(ISOTable.F_FIX   + ISOTable.A_N   )) ), // "SYSTEM TRACE AUDIT  NUMBER"),
      /* 012 */new ISOTable((short)  6, ((byte)(ISOTable.F_FIX   + ISOTable.A_N   )) ), // "TIME, LOCAL TRANSACTION"),
      /* 013 */new ISOTable((short)  4, ((byte)(ISOTable.F_FIX   + ISOTable.A_N   )) ), // "DATE, LOCAL TRANSACTION"),
      /* 014 */new ISOTable((short)  4, ((byte)(ISOTable.F_FIX   + ISOTable.A_N   )) ), // "DATE, EXPIRATION"),
      /* 015 */new ISOTable((short)  4, ((byte)(ISOTable.F_FIX   + ISOTable.A_N   )) ), // "DATE, SETTLEMENT"),
      /* 016 */new ISOTable((short)  4, ((byte)(ISOTable.F_FIX   + ISOTable.A_N   )) ), // "DATE, CONVERSION"),
      /* 017 */new ISOTable((short)  4, ((byte)(ISOTable.F_FIX   + ISOTable.A_N   )) ), // "DATE, CAPTURE"),
      /* 018 */new ISOTable((short)  4, ((byte)(ISOTable.F_FIX   + ISOTable.A_N   )) ), // "MERCHANTS TYPE"),
      /* 019 */new ISOTable((short)  3, ((byte)(ISOTable.F_FIX   + ISOTable.A_N   )) ), // "ACQUIRING  INSTITUTION COUNTRY CODE"),
      /* 020 */new ISOTable((short)  3, ((byte)(ISOTable.F_FIX   + ISOTable.A_N   )) ), // "PAN EXTENDED COUNTRY CODE"),
      /* 021 */new ISOTable((short)  3, ((byte)(ISOTable.F_FIX   + ISOTable.A_N   )) ), // "FORWARDING INSTITUTION COUNTRY CODE"),
      /* 022 */new ISOTable((short)  3, ((byte)(ISOTable.F_FIX   + ISOTable.A_N   )) ), // "POINT OF SERVICE  ENTRY MODE"),
      /* 023 */new ISOTable((short)  3, ((byte)(ISOTable.F_FIX   + ISOTable.A_N   )) ), // "CARD SEQUENCE  NUMBER"),
      /* 024 */new ISOTable((short)  3, ((byte)(ISOTable.F_FIX   + ISOTable.A_AN  )) ), // "NETWORK  INTERNATIONAL  IDENTIFIEER"),
      /* 025 */new ISOTable((short)  2, ((byte)(ISOTable.F_FIX   + ISOTable.A_N   )) ), // "POINT OF SERVICE  CONDITION CODE"),
      /* 026 */new ISOTable((short)  2, ((byte)(ISOTable.F_FIX   + ISOTable.A_N   )) ), // "POINT OF SERVICE PIN CAPTURE CODE"),
      /* 027 */new ISOTable((short)  1, ((byte)(ISOTable.F_FIX   + ISOTable.A_N   )) ), // "AUTHORIZATION  IDENTIFICATION RESP  LEN"),
      /* 028 */new ISOTable((short)  8, ((byte)(ISOTable.F_FIX   + ISOTable.A_N   )) ), // "AMOUNT, TRANSACTION  FEE"),
      /* 029 */new ISOTable((short)  8, ((byte)(ISOTable.F_FIX   + ISOTable.A_N   )) ), // "AMOUNT, SETTLEMENT  FEE"),
      /* 030 */new ISOTable((short)  8, ((byte)(ISOTable.F_FIX   + ISOTable.A_N   )) ), // "AMOUNT, TRANSACTION  PROCESSING FEE"),
      /* 031 */new ISOTable((short)  8, ((byte)(ISOTable.F_FIX   + ISOTable.A_N   )) ), // "AMOUNT, SETTLEMENT  PROCESSING FEE"),
      /* 032 */new ISOTable((short) 11, ((byte)(ISOTable.F_LLVAR + ISOTable.A_N   )) ), // "ACQUIRING  INSTITUTION IDENT  CODE"),
      /* 033 */new ISOTable((short) 11, ((byte)(ISOTable.F_LLVAR + ISOTable.A_N   )) ), // "FORWARDING  INSTITUTION IDENT CODE"),
      /* 034 */new ISOTable((short) 28, ((byte)(ISOTable.F_LLVAR + ISOTable.A_N   )) ), // "PAN EXTENDED"),
      /* 035 */new ISOTable((short) 37, ((byte)(ISOTable.F_LLVAR + ISOTable.A_N   )) ), // "TRACK 2 DATA"),
      /* 036 */new ISOTable((short)104, ((byte)(ISOTable.F_LLLVAR+ ISOTable.A_N   )) ), // "TRACK 3 DATA"),
      /* 037 */new ISOTable((short) 12, ((byte)(ISOTable.F_FIX   + ISOTable.A_AN  )) ), // "RETRIEVAL REFERENCE  NUMBER"),
      /* 038 */new ISOTable((short)  6, ((byte)(ISOTable.F_FIX   + ISOTable.A_AN  )) ), // "AUTHORIZATION IDENTIFICATION RESPONSE"),
      /* 039 */new ISOTable((short)  2, ((byte)(ISOTable.F_FIX   + ISOTable.A_AN  )) ), // "RESPONSE CODE"),
      /* 040 */new ISOTable((short)  3, ((byte)(ISOTable.F_FIX   + ISOTable.A_AN  )) ), // "SERVICE RESTRICTION CODE"),
      /* 041 */new ISOTable((short)  8, ((byte)(ISOTable.F_FIX   + ISOTable.A_ANS )) ), // "CARD ACCEPTOR TERMINAL IDENTIFICACION"),
      /* 042 */new ISOTable((short) 15, ((byte)(ISOTable.F_FIX   + ISOTable.A_ANS )) ), // "CARD ACCEPTOR  IDENTIFICATION CODE" ),"),
      /* 043 */new ISOTable((short) 40, ((byte)(ISOTable.F_FIX   + ISOTable.A_ANS )) ), // "CARD ACCEPTOR  NAME/LOCATION"),
      /* 044 */new ISOTable((short) 25, ((byte)(ISOTable.F_LLVAR + ISOTable.A_ANS )) ), // "ADITIONAL RESPONSE DATA"),
      /* 045 */new ISOTable((short) 76, ((byte)(ISOTable.F_LLVAR + ISOTable.A_ANS )) ), // "TRACK 1 DATA"),
      /* 046 */new ISOTable((short)999, ((byte)(ISOTable.F_LLLVAR+ ISOTable.A_ANS )) ), // "ADITIONAL DATA - ISO"), (CUP Operator ID)
      /* 047 */new ISOTable((short)999, ((byte)(ISOTable.F_LLLVAR+ ISOTable.A_ANS )) ), // "ADITIONAL DATA -  NATIONAL"), (CUP  Operator Text)
      /* 048 */new ISOTable((short)999, ((byte)(ISOTable.F_LLLVAR+ ISOTable.A_N   )) ), // "ADITIONAL DATA - PRIVATE"),
      /* 049 */new ISOTable((short)  3, ((byte)(ISOTable.F_FIX   + ISOTable.A_AN  )) ), // "CURRENCY CODE,  TRANSACTION"),
      /* 050 */new ISOTable((short)  3, ((byte)(ISOTable.F_FIX   + ISOTable.A_AN  )) ), // "CURRENCY CODE, SETTLEMENT"),
      /* 051 */new ISOTable((short)  3, ((byte)(ISOTable.F_FIX   + ISOTable.A_AN  )) ), // "CURRENCY CODE,  CARDHOLDER BILLING"),
      /* 052 */new ISOTable((short)  8, ((byte)(ISOTable.F_FIX   + ISOTable.A_BIN )) ), // "PINBLOCK"),
      /* 053 */new ISOTable((short) 16, ((byte)(ISOTable.F_FIX   + ISOTable.A_N   )) ), // 
      /* 054 */new ISOTable((short)120, ((byte)(ISOTable.F_LLLVAR+ ISOTable.A_AN  )) ), // 
      /* 055 */new ISOTable((short)999, ((byte)(ISOTable.F_LLLVAR+ ISOTable.A_ANS )) ), // 
      /* 056 */new ISOTable((short)999, ((byte)(ISOTable.F_LLLVAR+ ISOTable.A_ANS )) ), 
      /* 057 */new ISOTable((short)999, ((byte)(ISOTable.F_LLLVAR+ ISOTable.A_ANS )) ), // "RESERVED
      /* 058 */new ISOTable((short)999, ((byte)(ISOTable.F_LLLVAR+ ISOTable.A_ANS )) ), // "RESERVED"),
      /* 059 */new ISOTable((short)999, ((byte)(ISOTable.F_LLLVAR+ ISOTable.A_ANS )) ), // "RESERVED
      /* 060 */new ISOTable((short)999, ((byte)(ISOTable.F_LLLVAR+ ISOTable.A_N   )) ), // "RESERVED
      /* 061 */new ISOTable((short)999, ((byte)(ISOTable.F_LLLVAR+ ISOTable.A_N   )) ), // "RESERVED
      /* 062 */new ISOTable((short)999, ((byte)(ISOTable.F_LLLVAR+ ISOTable.A_ANS )) ), // "RESERVED
      /* 063 */new ISOTable((short)999, ((byte)(ISOTable.F_LLLVAR+ ISOTable.A_ANS )) ), // "RESERVED
      /* 064 */new ISOTable((short)  8, ((byte)(ISOTable.F_FIX   + ISOTable.A_BIN )) ), 
    };
}
