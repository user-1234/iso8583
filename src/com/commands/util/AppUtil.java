package com.commands.util;

public class AppUtil
{
  
  public static int bufferToInt(byte[] data, int offset, int length)
  {
    try
    {
      if (data == null || data.length <= 0 || offset < 0 || length < 0 || length > 10 || data.length < (offset+length))
        return -1;
      return NumberUtil.parseInt(data, offset, length, 10);
    } catch (Exception e)
    {
      return -1;
    }
  }

  public static int stringToInt(String str)
  {
    try
    {
      if (str == null || str.length() <= 0 || str.length() > 10)
        return -1;
      return Integer.parseInt(str);
    } catch (Exception e)
    {
      return -1;
    }
  }


  /**
   * Protects PAN, Track2, CVC (suitable for logs).
   *
   * <pre>
   * "40000101010001" is converted to "400001____0001"
   * "40000101010001=020128375" is converted to "400001____0001=0201_____"
   * "123" is converted to "___"
   * </pre>
   * @param s string to be protected 
   * @return 'protected' String
   */
  public static String cardProtect(String s, char fillChar)
  {
    return cardProtect(s, fillChar, 6, 4, true);
  }

  public static String cardProtect(String s, char fillChar, int startOffset,
      int displayLength, boolean checkEnd)
  {
    if (s == null || s.length() < startOffset || s.length() < displayLength
        || startOffset < 0 || displayLength < 0)
      return null;
    StringBuffer sb = new StringBuffer();
    int len = s.length();
    int clear = len > startOffset ? startOffset : 0;
    int lastFourIndex = -1;
    if (checkEnd || displayLength > 0)
    {
      lastFourIndex = s.indexOf('=') - displayLength;
      if (lastFourIndex < 0)
      {
        lastFourIndex = s.indexOf('^') - displayLength;
        if (lastFourIndex < 0)
          lastFourIndex = len - displayLength;
      }
    }
    for (int i = 0; i < len; i++)
    {
      if (s.charAt(i) == '=')
        clear = 5;
      else
        if (s.charAt(i) == '^')
        {
          lastFourIndex = 0;
          clear = len - i;
        } else
          if (i == lastFourIndex)
            clear = displayLength;
      sb.append(clear-- > 0 ? s.charAt(i) : fillChar);
    }
    return sb.toString();
  }

  public static String formatAmount(String amount, boolean separator)
  {
    if (amount == null || amount.length() == 0)
      amount = "000";
    boolean minusFlag = false;
    if (amount.charAt(0) == '-') {
      minusFlag = true;
      amount = amount.substring(1, amount.length());
    }
    int i;
    if (amount.length() < 3) {
      if (amount.length() == 2)
        amount = "0" + amount;
      else if (amount.length() == 1)
        amount = "00" + amount;
    }
    StringBuffer s = new StringBuffer();
    int strLen = amount.length();
    for (i = 1; i <= strLen; i++)
    {
      s.insert(0, amount.charAt(strLen - i));
      if (i == 2)  s.insert(0, '.');
      if (i > 3 && ((i % 3) == 0))
      {
        if (separator)
        {
          s.insert(1, ',');
        }
      }
    }
    if (minusFlag)
      s.insert(0, '-');
      
    return s.toString();
  }

  /**
   * prepare long value used as amount for display
   * (implicit 2 decimals)
   * @param amount value
   * @return formated field
   * @exception RuntimeException
   */
  public static String formatAmount(long amount)
  {
    return formatAmount("" + amount, false);
  }

  public static int toAmount(byte[] byte6)
  {
    int amount = -1;
          
    try
    {
      amount = Integer.parseInt(StringUtil.toHexString(byte6, false));
    } catch (NumberFormatException e)
    {
      amount = -1;  // the number too big
    }
    
    return amount;
  }

  public static byte[] toCurrency(long number, boolean bcdFlag)
  {
    byte[] currency =  null;
    
    if (bcdFlag)
    {
      currency = new byte[6];
      currency = StringUtil.hexStringToBytes(StringUtil.fillString("" + number, 12, '0', true));
    }
    else
    {
      currency = new byte[12];
      currency = (StringUtil.fillString("" + number, 12, '0', true)).getBytes();
    }
    
    return currency;
    
  }

  /**
  *
  * Method Check  whether IP address is legal.
  *
  * @param IPString The value to be checked.
  *
  */  
public static boolean checkIP(String IPString)
{
  return checkIP(IPString, false);
}
public static boolean checkIP(String IPString, boolean checkGateway)
  {
    if (IPString != null && IPString.length() == 12)
    {
      try
      {
        int min = checkGateway?0:1;
        int max = checkGateway?256:224;
        int ip = Integer.parseInt(IPString.substring(0,3));
        if (ip < min || ip >= max /*|| ip == 127*/)
          return false;
        min = 0;
        max = 255;
        ip = Integer.parseInt(IPString.substring(3,6));
        if (ip < min || ip > max)
          return false;
        ip = Integer.parseInt(IPString.substring(6,9));
        if (ip < min || ip > max)
          return false;
        min = checkGateway?0:1;
        max = checkGateway?255:254;
        ip = Integer.parseInt(IPString.substring(9,12));
        if (ip < min || ip > max)
          return false;

        return true;
      } catch (Exception e)
      {
      }
    }
    return false;
 }
  /**
  *
  * Method Check  whether IP port is legal.
  *
  * @param portString The value to be checked.
  *
  */  
  public static boolean checkIPPort(int port)
  {
    if (0 < port && port < 65535)
      return true;
    return false;
  }

  public static String toIPAddress(String ip)
  {
    String str = "0.0.0.0";
    try
    {
      if (ip != null && ip.length() >= 12){
        str = "" + Integer.parseInt(ip.substring(0,3))
           + "." + Integer.parseInt(ip.substring(3,6))
           + "." + Integer.parseInt(ip.substring(6,9))
           + "." + Integer.parseInt(ip.substring(9,12));
      }
    } catch (Exception e)
    {
    }
    return str;
  }
 
  /**
  *
  * Method Check  whether ExpireDate is legal.
  *
  * @param expireDate The value to be checked.
  *
  */  
  public static boolean checkExpireDate(String expireDate)
  {
    if (expireDate != null && expireDate.length() == 4)
    {
      try
      {
        int month = Integer.parseInt(expireDate.substring(0,2));
        
        if (month >= 1 && month <= 12)
          return true;
        else
          return false;
      } catch (Exception e)
      {
      }
    }

    return false;
  }
//  /**
//   * get card nember or expire date from track2
//   * @param track2 value
//   * @param expire flag, if expire==true ,will return expire date
//   */    
//  public static String getCardfromTrack2(byte[] track2, boolean expire)
//  {
//    if (track2 == null)
//    {
//      return "";
//    } else
//    {
//       int panStart = -1;
//       int panEnd = -1;
//       String s = null;
//       for (int i = 0; i < track2.length; i++)
//       {
//         if ((track2[i] >= (byte)'0' && track2[i] <= (byte)'9') && panStart == -1)
//         {
//           panStart = i;
//         }
//         if (track2[i] == (byte)'=')
//         {
//             /* Field separator */
//             panEnd = i;
//             break;
//         }
//       }
//       if (panEnd == -1 || panStart == -1)
//       {
//          s = "";
//       }
//       else
//       {
//         if (expire)
//            s = new String(track2, panEnd + 1, 4);
//          else
//            s = new String(track2, panStart, panEnd - panStart);
//       }
//       return s;    
//    }
//  }
//  /**
//   * get card nember or expire date from track1
//   * @param track1 value
//   * @param expire flag, if expire==true ,will return expire date
//   */    
//  public static String getCardfromTrack1(byte[] trackData, boolean expire)
//  {
//    String s = "";
//    
//    if (trackData != null)
//    {
//      int panStart = -1;
//      int nameStart = -1;
//      int nameEnd = -1;
//      int trackLength = trackData.length;
//      for (int i = 0; i < trackLength; i++)
//      {
//        if ((trackData[i] >= (byte) '0' && trackData[i] <= (byte) '9') && panStart == -1)
//          panStart = i;
//        if (trackData[i] == (byte) '^')
//        {
//          /* Field separator */
//          if (nameStart == -1)
//          {
//            nameStart = i + 1;
//          } else
//          {
//            nameEnd = i;
//            break;
//          }
//        }
//      }
//      if (nameEnd != -1 && nameStart != -1)
//      {
////        s = new String(trackData, nameStart, nameEnd - nameStart); // return card name
//        if (panStart != -1)
//        {
//          if (expire)
//            s = new String(trackData, nameEnd + 1, 4);  //return expire date
//          else
//            s = new String(trackData, panStart, nameStart - 1  - panStart); // return card number
//        }
//      }
//    }
//    
//    return s;
//  }

}
