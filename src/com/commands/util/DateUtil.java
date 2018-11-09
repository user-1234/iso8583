/** 
 * File name:            DateUtil.java
 * 
 * Originally developed: 
 *
 * Create date :         
 * 
 * Description:          ����ʱ����ؼ�顢ת������.
 * 
 * Version:              
 * 
 * Contributors:         
 * 
 * Modifications: 
 * name          version           reasons
 * 
 */

package com.commands.util;

public class DateUtil
{
  
  private final static int CONVERT_START_YEAR = 1970; // ����ת������/��ʱ�ĵ������ʼ���

  
  /**
  *
  * �Ƚ������ַ�����ʽ�����ڴ�С
  *
   * @param date1 (format:YYYYMMDD ��  YYYYMMDDHHMMSS)
   * @param date2 (format:YYYYMMDD ��  YYYYMMDDHHMMSS)
   * @return =0: ������ͬ 
   * @return <0: date1 < data2
   * @return >0: date1 > date2
  *
  */ 
  public static int compareDate(String date1, String date2)
  {
		return Integer.parseInt(date1) - Integer.parseInt(date2);
  }


  /**
    *
    * ����ַ�����ʽ������ֵ�Ƿ�Ϸ�.
    *
    * @param dateYYYYMMDD :Ϊ��Ҫ������ڣ���ʽΪYYYYMMDD.
    * @return true: ����ֵ�Ϸ�
    * @return false: ����ֵ����
    *
    */  
  public static boolean checkDate(String dateYYYYMMDD)
  {
    return checkDate(dateYYYYMMDD, 0);
  }
  public static boolean checkDate(String dateYYYYMMDD, int offset)
  {
    if (dateYYYYMMDD != null && dateYYYYMMDD.length() >= (offset+8))
    {
			/* The legitimacy of date is checked  
				1,3,5,7,8,10,12 month is 31 days, 
				4,6,9,11 month is 30 days, 
				2 month: generic 28, leap year 29 days
			*/ 
			try
      {
			  int i = offset;
        int year = Integer.parseInt(dateYYYYMMDD.substring(i,4));
        int month = Integer.parseInt(dateYYYYMMDD.substring(i+4,6));
        int day = Integer.parseInt(dateYYYYMMDD.substring(i+6,8));
        if (month >= 1 && month <= 12 && day >= 1 && day <= 31)
        {
        	if ((month == 4 || month  == 6 || month == 9 || month  == 11) && day > 30)
        		return false;
        	if (year % 4 == 0 || (year % 100 == 0 && year %4 == 0))
        	{ // leap year check
        		if (month == 2 && day > 29)
        			return false;
        	} else {
        		if (month == 2 && day > 28)
        			return false;
        	} 
        	return true;
        }
      } catch (Exception e)
      {
      }
		}
		
		return false;
  }

  /**
  *
  * ����ַ�����ʽ��ʱ��ֵ�Ƿ�Ϸ�.
  *
  * @param timeHHMMSS :Ϊ��Ҫ���ʱ�䣬��ʽΪHHMMSS.
  * @return true: ʱ��ֵ�Ϸ�
  * @return false: ʱ��ֵ����
  *
  */  
  public static boolean checkTime(String timeHHMMSS)
  {
    return checkTime(timeHHMMSS, 0);
  }
  public static boolean checkTime(String timeHHMMSS, int offset)
  {
    if (timeHHMMSS != null && timeHHMMSS.length() >= (offset+6))
    {
      try
      {
        int hh = Integer.parseInt(timeHHMMSS.substring(offset+0, offset+2));
        int mm = Integer.parseInt(timeHHMMSS.substring(offset+2, offset+4));
        int ss = Integer.parseInt(timeHHMMSS.substring(offset+4, offset+6));
        
        if ((hh >= 0 && hh <= 23) && (mm >= 0 && mm <= 59) && (ss >= 0 && ss <= 59))
          return true;
        else
          return false;
      } catch (Exception e)
      {
      }
    }
  
    return false;
  }

  /**
  *
  * ����ǰ����ת���ɾ���1970-01-01 00:00:00����
  *
  * @param dateYYYYMMDD :��Ҫת�����ڣ���ʽΪYYYYMMDD.
  * @return seconds since 1970 00:00:00 on OK, base on RTC time
  *         -1 on error
  *
  */  
  public static long convertDateToDays(byte[] dateYYYYMMDD)
  {
    if (dateYYYYMMDD == null || dateYYYYMMDD.length < 4)
      return -1; //error
    int i = 0;
    int startYear = CONVERT_START_YEAR;
    long total_days;//since January 1, startYear
    byte[] mon_days = new byte[]{31,28,31,30,31,30,31,31,30,31,30,31};
    boolean leap_year = false;  //false = no leap year, true = leap year
    
    int year = NumberUtil.bcdToInt(dateYYYYMMDD, i, 2);
    i = i + 2;
    int month = NumberUtil.bcdToInt(dateYYYYMMDD, i, 1);
    i = i + 1;
    int mday = NumberUtil.bcdToInt(dateYYYYMMDD, i,1);
    i = i + 1;

    //check time first
    if(mday<1 || mday>31
      || month<1 || month>12
      || year<startYear
      )
    {
      return -1;//error
    }

    if(year%4==0 && (year%100!=0 || year%400==0))
    {
      leap_year = true;
    }
    if (month==2) {
      if (leap_year == false && mday>28) return -1;//error
      if (leap_year && mday>29) return -1;//error
    } else {
      if (mday > mon_days[month-1]) return -1;//error
    }

    total_days = 0;
    for(i=1;i<month;i++)
    {
      total_days += mon_days[i-1];
    }
    if(month>2 && leap_year)
    {
      total_days++;
    }
    total_days += mday-1;
    for(i=startYear;i<year;i++)
    {
      total_days += 365;
      if((i%4==0) && ((i%100!=0) || (i%400==0)))
      {
        total_days++;
      }
    }
    return total_days;
  }

  /**
  *
  * ����ǰ�����1970-01-01 00:00:00������ת��Ϊ��������
  *
  * @param dateDays :����1970-01-01 00:00:00������
  * @return ת����������ַ���
  *
  */  
  public static byte[] convertDaysToDate(long dateDays)
  {
    if (dateDays < 1)
      return null;
    byte[] date = new byte[4];
    int yeah = CONVERT_START_YEAR;
    int yearDays = 365;
    long total_days = dateDays;
    
    for(;;)
    {  
      yearDays = 365;
      if((yeah%4==0) && ((yeah%100!=0) || (yeah%400==0)))
      {
        yearDays ++;;
      }
      if (total_days < yearDays)
        break;
      total_days = total_days - yearDays;
      yeah ++;
    }
    byte[] mon_days = new byte[]{31,28,31,30,31,30,31,31,30,31,30,31};
    int month = 1;
    int monthDays = 31;
    for (int i = 0; i < mon_days.length; i ++){
      monthDays = mon_days[i];
      if (i == 1 && yearDays == 366) // February
        monthDays ++;
      if (total_days < monthDays)
        break;
      total_days = total_days - monthDays;
      month ++;
    }
    date[0] = NumberUtil.BCD_Digit[yeah / 100];
    date[1] = NumberUtil.BCD_Digit[yeah % 100];
    date[2] = NumberUtil.BCD_Digit[month];
    date[3] = NumberUtil.BCD_Digit[(int)(total_days + 1)];
    return date;
  }


  /**
  *
  * ����ǰ����ת���ɾ���1970-01-01 00:00:00����
  *
  * @param dateYYYYMMDDHHMMSS :��Ҫת�����ڣ���ʽΪYYYYMMDDHHMMSS.
  * @return seconds since 1970 00:00:00 on OK, base on RTC time
  *         -1 on error
  *
  */  
  public static long convertDateToSecond(byte[] dateYYYYMMDDHHMMSS)
  {
    if (dateYYYYMMDDHHMMSS == null || dateYYYYMMDDHHMMSS.length < 7)
      return -1; //error
    
    long total_days = convertDateToDays(dateYYYYMMDDHHMMSS);//since January 1, 1970
    
    if (total_days < 0)
      return -1; //error
    
    int hour = NumberUtil.bcdToInt(dateYYYYMMDDHHMMSS, 4, 1);
    int minute = NumberUtil.bcdToInt(dateYYYYMMDDHHMMSS, 5, 1);
    int second = NumberUtil.bcdToInt(dateYYYYMMDDHHMMSS, 6, 1);

    //check time first
    if( second<0 || second>59
      || minute<0 || minute>59
      || hour<0 || hour>23 //24-hour mode
      )
    {
      return -1;//error
    }

    long iSecs = total_days*24*60*60+hour*60*60+minute*60+second;
    return iSecs;
  } 
  

  /**
  *
  * ����ǰ�����1970-01-01 00:00:00��������ת��Ϊ��������
  *
  * @param timeSec :����1970-01-01 00:00:00������
  * @return ת����������ַ���
  *
  */  
  public static byte[] convertSecondToDate(long timeSec)
  {
    long days = (timeSec / (3600*24));
    long daySecond = (timeSec % (3600*24));
    int hour = (int)(daySecond / 3600);
    int minute = (int)((daySecond % 3600) / 60);
    int second = (int)(daySecond % 60);
    
    byte[] date = new byte[7];

    System.arraycopy(convertDaysToDate(days), 0, date, 0, 4);

    date[4] = NumberUtil.BCD_Digit[hour];
    date[5] = NumberUtil.BCD_Digit[minute];
    date[6] = NumberUtil.BCD_Digit[second];
    
    return date;
  }

  /**
  *
  * ��ʱ��ת��������
  *
  * @param timeHHMMSS :��Ҫת����ʱ�䣬��ʽΪHHMMSS.
  * @return ת���������
  *
  */  
  public static int convertTimeToSecond(byte[] timeHHMMSS)
  {
    return convertTimeToSecond(timeHHMMSS, 0);
  }
  public static int convertTimeToSecond(byte[] timeHHMMSS, int offset)
  {
    if (timeHHMMSS == null || (timeHHMMSS.length - offset) < 3)
      return -1; //error
    
    int iSecs = 0;

    int hour = NumberUtil.bcdToInt(timeHHMMSS, offset, 1);
    offset++;
    int minute = NumberUtil.bcdToInt(timeHHMMSS, offset, 1);
    offset++;
    int second = NumberUtil.bcdToInt(timeHHMMSS, offset, 1);

    //check time first
    if( second<0 || second>59
      || minute<0 || minute>59
      || hour<0 || hour>23 //24-hour mode
      )
    {
      return -1;//error
    }
    
    iSecs = hour*60*60+minute*60+second;
        
    return iSecs;
  } 
   
  /**
  *
  * ʹ�÷ָ�����ʽ��ʱ��.
  *
  * @param timeHHMMSS :Ϊ��Ҫ��ʽ����ʱ�䣬��ʽΪHHMMSS.
  * @param offset :Ϊ��Ҫ��ʽ����ʱ����ַ�����ʼλ��.
  * @param formatChar :��ʽ���ķָ�����.
  * @return ��ʽ�����ʱ��
  *
  */  
  public static String formatTime(String timeHHMMSS, char formatChar)
  {
    return formatTime(timeHHMMSS, 0, formatChar);
  }
  public static String formatTime(String timeHHMMSS, int offset, char formatChar)
  {
    if (timeHHMMSS == null)
      return "";
    if (timeHHMMSS.length() < (offset + 6))
      return timeHHMMSS;
      
    return timeHHMMSS.substring(offset, offset + 2) + formatChar + timeHHMMSS.substring(offset + 2, offset + 4) + formatChar + timeHHMMSS.substring(offset + 4, offset + 6);
  }
  
  /**
  *
  * ʹ�÷ָ�����ʽ������.
  *
  * @param dateYYYYMMDD :Ϊ��Ҫ��ʽ�������ڣ���ʽΪHHMMSS.
  * @param formatChar :��ʽ���ķָ�����.
  * @return ��ʽ���������
  *
  */  
  public static String formatDate(String dateYYYYMMDD, char formatChar)
  {
    if (dateYYYYMMDD == null)
      return "";
    if (dateYYYYMMDD.length() < 8)
      return dateYYYYMMDD;
      
    return dateYYYYMMDD.substring(0, 4) + formatChar + dateYYYYMMDD.substring(4, 6) + formatChar + dateYYYYMMDD.substring(6, 8);
  }
  
  /**
  *
  * ʹ�÷ָ�����ʽ������ ʱ��.
  *
  * @param dateYYYYMMDD :Ϊ��Ҫ��ʽ�������ڣ���ʽΪHHMMSS.
  * @param formatChar :��ʽ���ķָ�����.
  * @return ��ʽ���������ʱ�䣬��ʽΪYYYY-MM-DD HH:MM:SS
  *
  */  
  public static String formatDateTime(String dateTimeYYYYMMDDHHMMSS)
  {
    if (dateTimeYYYYMMDDHHMMSS == null)
      return "";
    if (dateTimeYYYYMMDDHHMMSS.length() < 8)
      return dateTimeYYYYMMDDHHMMSS;
    String str = dateTimeYYYYMMDDHHMMSS.substring(0, 4) + '-' + dateTimeYYYYMMDDHHMMSS.substring(4, 6) + '-' + dateTimeYYYYMMDDHHMMSS.substring(6, 8);
    if (dateTimeYYYYMMDDHHMMSS.length() < (8 + 6))
      return str;
    else
      return str + ' ' + dateTimeYYYYMMDDHHMMSS.substring(8, 10) + ':' + dateTimeYYYYMMDDHHMMSS.substring(10, 12) + ':' + dateTimeYYYYMMDDHHMMSS.substring(12, 14);
  }

}

