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
 * File name:            HostMessage.java
 * 
 * Originally developed: Commands.Wu
 *
 * Create date :         2006-10-23
 * 
 * Description:          Response code to String.
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


public class HostMessage
{
  public final static String SUCCESS            = "00";
  public final static String PASSWORD_WRONG     = "55";
  public final static String MAC_VALIDATE_ERROR = "A0";
  public final static String TRANS_TIMEOUT_ERROR= "98";
  
  public static String[] getHostMessage(String response)
  {
    String [] hostMsg = new String[4];
    int i;
    for (i = 0; i < hostMsg.length; i++)
      hostMsg[i] = "";
    if (response == null || response.length() != 2)
    {
      hostMsg[1] = "MSG_FAIL";
    } else {
      hostMsg[0] = response;
      char code =(char)(((response.charAt(0) & 0xff)<<8 | (response.charAt(1) & 0xff)) & 0xffff);
      switch (code)
      {
      case'\u3030':/*00*/
      hostMsg[1]="MSG_SUCCESS";
      break;
      
      case'\u3031':/*01*/
      hostMsg[1]="MSG_FAIL";
      hostMsg[2]="MSG_CONTACT_ISS";
      break;
      
      case'\u3032':/*02*/
      hostMsg[1]="MSG_FAIL";
      hostMsg[2]="MSG_CONTACT_ISS";
      break;
      
      case'\u3033':/*03*/
      hostMsg[1]="MSG_INVALID_METCH_ID";
      break;
      
      case'\u3034':/*04*/
      hostMsg[1]="MSG_SEQUESTRATE_CARD";
      hostMsg[2]="MSG_CONTACT_ACQ";
      break;
      
      case'\u3035':/*05*/
      hostMsg[1]="MSG_FAIL";
      hostMsg[2]="MSG_CONTACT_ISS";
      break;
      
      case'\u3036':/*06*/
      hostMsg[1]="MSG_FAIL";
      hostMsg[2]="MSG_CONTACT_ISS";
      break;
      
      case'\u3037':/*07*/
      hostMsg[1]="MSG_SEQUESTRATE_CARD";
      hostMsg[2]="MSG_CONTACT_ACQ";
      break;
      
      case'\u3039':/*09*/
      hostMsg[1]="MSG_FAIL";
      hostMsg[2]="MSG_TRY";
      break;
      
      case'\u3132':/*12*/
      hostMsg[1]="MSG_FAIL";
      hostMsg[2]="MSG_TRY";
      break;
      
      case'\u3133':/*13*/
      hostMsg[1]="MSG_INVALID_AMOUNT";
      hostMsg[2]="MSG_TRY";
      break;
      
      case'\u3134':/*14*/
      hostMsg[1]="MSG_INVALID_CARD";
      hostMsg[2]="MSG_CONTACT_ISS";
      break;
      
      case'\u3135':/*15*/
      hostMsg[1]="MSG_NOT_ACCEPT_CARD";
      break;
      
      case'\u3139':/*19*/
      hostMsg[1]="MSG_FAIL";
      hostMsg[2]="MSG_CONTACT_ISS";
      break;
      
      case'\u3230':/*20*/
      hostMsg[1]="MSG_FAIL";
      hostMsg[2]="MSG_CONTACT_ISS";
      break;
      
      case'\u3231':/*21*/
      hostMsg[1]="MSG_FAIL";
      hostMsg[2]="MSG_CONTACT_ISS";
      break;
      
      case'\u3232':/*22*/
      hostMsg[1]="MSG_OPERATION_ERROR";
      hostMsg[2]="MSG_TRY";
      break;
      
      case'\u3233':/*23*/
      hostMsg[1]="MSG_FAIL";
      hostMsg[2]="MSG_CONTACT_ISS";
      break;
      
      case'\u3235':/*25*/
      hostMsg[1]="MSG_FAIL";
      hostMsg[2]="MSG_CONTACT_ISS";
      break;
      
      case'\u3330':/*30*/
      hostMsg[1]="MSG_FAIL";
      hostMsg[2]="MSG_TRY";
      break;
      
      case'\u3331':/*31*/
      hostMsg[1]="MSG_NOT_ACCEPT_CARD";
      break;
      
      case'\u3333':/*33*/
      hostMsg[1]="MSG_EXPIRE_CARD";
      hostMsg[2]="MSG_CONTACT_ISS";
      break;
      
      case'\u3334':/*34*/
      hostMsg[1]="MSG_SEQUESTRATE_CARD";
      hostMsg[2]="MSG_CONTACT_ACQ";
      break;
      
      case'\u3335':/*35*/
      hostMsg[1]="MSG_SEQUESTRATE_CARD";
      hostMsg[2]="MSG_CONTACT_ACQ";
      break;
      
      case'\u3336':/*36*/
      hostMsg[1]="MSG_CARD_WRONG";
      hostMsg[2]="MSG_REPLACE_CARD";
      break;
      
      case'\u3337':/*37*/
      hostMsg[1]="MSG_SEQUESTRATE_CARD";
      hostMsg[2]="MSG_CONTACT_ACQ";
      break;
      
      case'\u3338':/*38*/
      hostMsg[1]="MSG_PASSWORD_ERROR";
      hostMsg[2]="MSG_TIMES_OVER";
      break;
      
      case'\u3339':/*39*/
      hostMsg[1]="MSG_FAIL";
      hostMsg[2]="MSG_CONTACT_ISS";
      break;
      
      case'\u3430':/*40*/
      hostMsg[1]="MSG_FAIL";
      hostMsg[2]="MSG_CONTACT_ISS";
      break;
      
      case'\u3431':/*41*/
      hostMsg[1]="MSG_SEQUESTRATE_CARD";
      hostMsg[2]="MSG_CONTACT_ACQ";
      break;
      
      case'\u3432':/*42*/
      hostMsg[1]="MSG_FAIL";
      hostMsg[2]="MSG_CONTACT_ISS";
      break;
      
      case'\u3433':/*43*/
      hostMsg[1]="MSG_SEQUESTRATE_CARD";
      hostMsg[2]="MSG_CONTACT_ACQ";
      break;
      
      case'\u3434':/*44*/
      hostMsg[1]="MSG_FAIL";
      hostMsg[2]="MSG_CONTACT_ISS";
      break;
      
      case'\u3531':/*51*/
      hostMsg[1]="MSG_BALANCE_SHORTAGE";
      hostMsg[2]="MSG_QUERY";
      break;
      
      case'\u3532':/*52*/
      hostMsg[1]="MSG_FAIL";
      hostMsg[2]="MSG_CONTACT_ISS";
      break;
      
      case'\u3533':/*53*/
      hostMsg[1]="MSG_FAIL";
      hostMsg[2]="MSG_CONTACT_ISS";
      break;
      
      case'\u3534':/*54*/
      hostMsg[1]="MSG_EXPIRE_CARD";
      hostMsg[2]="MSG_CONTACT_ISS";
      break;
      
      case'\u3535':/*55*/
      hostMsg[1]="MSG_PASSWORD_ERROR";
      hostMsg[2]="MSG_TRY";
      break;
      
      case'\u3536':/*56*/
      hostMsg[1]="MSG_FAIL";
      hostMsg[2]="MSG_CONTACT_ISS";
      break;
      
      case'\u3537':/*57*/
      hostMsg[1]="MSG_FAIL";
      hostMsg[2]="MSG_CONTACT_ISS";
      break;
      
      case'\u3538':/*58*/
      hostMsg[1]="MSG_INVALID_TERMINAL";
      hostMsg[2]="MSG_CONTACT_ACQ_OR_CUP";
      break;
      
      case'\u3539':/*59*/
      hostMsg[1]="MSG_FAIL";
      hostMsg[2]="MSG_CONTACT_ISS";
      break;
      
      case'\u3630':/*60*/
      hostMsg[1]="MSG_FAIL";
      hostMsg[2]="MSG_CONTACT_ISS";
      break;
      
      case'\u3631':/*61*/
      hostMsg[1]="MSG_AMOUNT_OVER_SIZE";
      break;
      
      case'\u3632':/*62*/
      hostMsg[1]="MSG_FAIL";
      hostMsg[2]="MSG_CONTACT_ISS";
      break;
      
      case'\u3633':/*63*/
      hostMsg[1]="MSG_FAIL";
      hostMsg[2]="MSG_CONTACT_ISS";
      break;
      
      case'\u3634':/*64*/
      hostMsg[1]="MSG_FAIL";
      hostMsg[2]="MSG_CONTACT_ISS";
      break;
      
      case'\u3635':/*65*/
      hostMsg[1]="MSG_DEBIT_ERROR";
      hostMsg[2]="MSG_TIMES_OVER";
      break;
      
      case'\u3636':/*66*/
      hostMsg[1]="MSG_FAIL";
      hostMsg[2]="MSG_CONTACT_ACQ_OR_CUP";
      break;
      
      case'\u3637':/*67*/
      hostMsg[1]="MSG_SEQUESTRATE_CARD";
      break;
      
      case'\u3638':/*68*/
      hostMsg[1]="MSG_TIMEOUT";
      hostMsg[2]="MSG_TRY";
      break;
      
      case'\u3735':/*75*/
      hostMsg[1]="MSG_PASSWORD_ERROR";
      hostMsg[2]="MSG_TIMES_OVER";
      break;
      
      case'\u3737':/*77*/
      hostMsg[1]="MSG_BATCH_NO_ERROR";
      break;
      
      case'\u3739':/*79*/
      hostMsg[1]="MSG_POS_TERMINAL";
      hostMsg[2]="MSG_REUPLOAD_OFFLINE";
      break;
      
      case'\u3930':/*90*/
      hostMsg[1]="MSG_FAIL";
      hostMsg[2]="MSG_LATER_TRY";
      break;
      
      case'\u3931':/*91*/
      hostMsg[1]="MSG_FAIL";
      hostMsg[2]="MSG_LATER_TRY";
      break;
      
      case'\u3932':/*92*/
      hostMsg[1]="MSG_FAIL";
      hostMsg[2]="MSG_LATER_TRY";
      break;
      
      case'\u3933':/*93*/
      hostMsg[1]="MSG_FAIL";
      hostMsg[2]="MSG_CONTACT_ISS";
      break;
      
      case'\u3934':/*94*/
      hostMsg[1]="MSG_FAIL";
      hostMsg[2]="MSG_LATER_TRY";
      break;
      
      case'\u3935':/*95*/
      hostMsg[1]="MSG_FAIL";
      hostMsg[2]="MSG_LATER_TRY";
      break;
      
      case'\u3936':/*96*/
      hostMsg[1]="MSG_FAIL";
      hostMsg[2]="MSG_LATER_TRY";
      break;
      
      case'\u3937':/*97*/
      hostMsg[1]="MSG_INVALID_TERM_ID";
      hostMsg[2]="MSG_CONTACT_ACQ_OR_CUP";
      break;
      
      case'\u3938':/*98*/
      hostMsg[1]="MSG_TIMEOUT";
      hostMsg[2]="MSG_TRY";
      break;
      
      case'\u3939':/*99*/
      hostMsg[1]="MSG_VERIFY_ERROR";
      hostMsg[2]="MSG_RE_SIGN_IN";
      break;
      
      case'\u4130':/*A0*/
      hostMsg[1]="MSG_MAC_ERROR";
      hostMsg[2]="MSG_RE_SIGN_IN";
      break;
      
      default:
      hostMsg[1] = "MSG_FAIL";
      hostMsg[2] = "MSG_UNKNOWN_ERROR";
      break;
      }
    }
    return hostMsg;
  }


}
