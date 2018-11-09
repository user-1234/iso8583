package com.commands;

import java.util.LinkedList;

import com.commands.util.NumberUtil;
import com.commands.util.StringUtil;
import com.commands.util.Tracer;


public class TLVProcess
{

  public static short byte2ToShort(byte[] src, int offset)
  {
    if (null == src || offset < 0 || offset > src.length)
      throw new NullPointerException("invalid byte array ");
    if ((src.length - offset) < 2)
      throw new IndexOutOfBoundsException("invalid len: " + src.length);    
    short number = (short)((src[offset + 0]&0xff)<<8 | (src[offset + 1]&0xff));
    return number;
  }
  
  public static byte[] shortToByte2(short number)
  {
    int tmp_num = number;
    byte[] byte2 = new byte[2];
    
    for (int i = byte2.length - 1; i > -1; i--)
    {
      byte2[i] = new Integer(tmp_num & 0xff).byteValue();
      tmp_num = tmp_num >> 8;
    }
    
    return byte2;
  }
  
  public static short[] TLVParse(byte[] data, int offset)
  {
    if (data != null && offset >= 0 && data.length > (offset + 2))
    {
      int i = offset;
      short tag = 0;
      short valueOffset = 0;
      short valueLength = 0;

   // TAG标签的属性为bit，由16进制表示，占1～2个字节长度。
   // 若tag标签的第一个字节（注：字节排序方向为从左往右数，
   // 第一个字节即为最左边的字节。bit排序规则同理。）的后
   // 四个bit为“1111”，则说明该tag占两个字节，例如“9F33”；
   // 否则占一个字节，例如“95”。
      if ((data[i] & 0x0F) == 0x0F) {
        tag = byte2ToShort(data, i);
        i ++;
      } else {
        tag = (short)(data[i] & 0xFF);
      }
      i ++;
    //子域长度（即L本身）的属性也为bit，占1～3个字节长度。具体编码规则如下：
    //a)当L字段最左边字节的最左bit位（即bit8）为0，表示该L字段占一个字节，
    //  它的后续7个bit位（即bit7～bit1）表示子域取值的长度，采用二进制数
    //  表示子域取值长度的十进制数。例如，某个域取值占3个字节，那么其子域
    //  取值长度表示为“00000011”。所以，若子域取值的长度在1～127字节之间，
    //  那么该L字段本身仅占一个字节。
    //b)当L字段最左边字节的最左bit位（即bit8）为1，表示该L字段不止占一个
    //  字节，那么它到底占几个字节由该最左字节的后续7个bit位（即bit7～bit1）
    //  的十进制取值表示。例如，若最左字节为10000010，表示L字段除该字节外，
    //  后面还有两个字节。其后续字节的十进制取值表示子域取值的长度。例如，
    //  若L字段为“1000 0001 1111 1111”，表示该子域取值占255个字节。所以，
    //  若子域取值的长度在127～255字节之间，那么该L字段本身需占两个字节。
      int ll = 1;
      if ((data[i] & 0x80) == 0x80) {
        ll = data[i] & 0x7F;
        i ++;
      }
      if (ll == 2){
        valueLength = byte2ToShort(data, i);
        i ++;
      } else {
        valueLength = (short)(data[i] & 0xFF);
      }
      i ++;
      valueOffset = (short)i;
      short[] taginfo = new short[3];
      taginfo[0] = tag;
      taginfo[1] = valueOffset;
      taginfo[2] = valueLength;
      return taginfo;
    }
    return null;
  }

  public static int TLVAppend(short tag, byte[] src, int src_offset, byte[] dst, int dst_offset, int length)
  {
    int i = dst_offset;
    byte[] buffer = shortToByte2(tag);
    if ((buffer[0] & 0x0F) == 0x0F) {
      System.arraycopy(buffer, 0, dst, i, 2);
      i ++;
    } else {
      dst[i] = buffer[1];
    }
    i ++;
    if (length <= 0x7F)
    {
      dst[i] = (byte)(length & 0xFF);
    }
    else if (length <= 0xFF)
    {
      dst[i] = (byte)0x81;
      i ++;
      dst[i] = (byte)(length & 0xFF);
    }
    else
    {
      dst[i] = (byte)0x82;
      i ++;
      dst[i] = (byte)(length/256);
      i ++;
      dst[i] = (byte)(length%256);
    }
    i ++;
    System.arraycopy(src, src_offset, dst, i, length);
    i = i + length;

    return i;
  }
  
  ////////////////////////////////////////////////////////////////////////////////////////
  public static int TestTlvParse(byte[] data)
  {
    if(Tracer.ENABLE_DEBUG) Tracer.debug("ParameterDown procF46");
//    01 06 00 00 01 00 00 00 02 03 00 03 00
    if (data == null)
      return -1; // 数据为空
    if (data.length < 3 )
      return -2; // 数据格式错误

    int i = 0;
    short[] tagInfo = null;
    long num;
    for (; i < data.length; )
    {
      if(Tracer.ENABLE_DEBUG) Tracer.debug("TLVParse start i=" + i);
      tagInfo = TLVParse(data, i);
      if(Tracer.ENABLE_DEBUG) Tracer.debug("tagInfo.length=" + tagInfo.length);
      if(Tracer.ENABLE_DEBUG) Tracer.debug("buffer.length=" + data.length + " tag=" + (tagInfo[1] + tagInfo[2]));
      if (tagInfo != null && tagInfo.length == 3 && data.length >= (tagInfo[1] + tagInfo[2]))
      {
        i = tagInfo[1];
        int length = tagInfo[2];
        if(Tracer.ENABLE_DEBUG)  Tracer.debug("TLVParse tagInfo[0]=" + StringUtil.toHexString(shortToByte2(tagInfo[0]), false)  + " tagInfo[1]=" + tagInfo[1] + " tagInfo[2]=" + tagInfo[2]);
        switch(tagInfo[0])
        {
//        T:0x01 充值限额
//        L:0x06 长度6个字节
//        V:0x00 0x00 0x01 0x00 0x00 0x00
//        BCD格式，单位分，例如10000.00
        case (short)0x01:
          num = NumberUtil.bcdToLong(data, i, length);
          if (Tracer.ENABLE_DEBUG) Tracer.debug("procF46 F_MAX_TOPUP_AMOUNT:" + num);
          break;
//          T:0x02 充值限制笔数
//          L:0x03长度3个字节
//          V:0x00 0x03 0x00
//          BCD格式，300笔
        case (short)0x02:
          num = NumberUtil.bcdToLong(data, i, length);
          if (Tracer.ENABLE_DEBUG) Tracer.debug("procF46 F_MAX_TOPUP_COUNT:" + num);
          break;
        }
        i = tagInfo[1] + tagInfo[2];
        if(Tracer.ENABLE_DEBUG)  Tracer.debug("TLVParse end i=" + i);
      } else {
        return -3;
      }
    }

    return 1;
  }

  public static int TlvUnpack(byte[] data)
  {
    if(Tracer.ENABLE_DEBUG) Tracer.debug("TlvUnpack");
    if (data == null)
      return -1; // 数据为空
    if (data.length < 3 )
      return -2; // 数据格式错误

    int i = 0;
    short[] tagInfo = null;
    for (; i < data.length; )
    {
      tagInfo = TLVParse(data, i);
      if (tagInfo != null && tagInfo.length == 3 && data.length >= (tagInfo[1] + tagInfo[2]))
      {
        i = tagInfo[1];
        if(Tracer.ENABLE_DEBUG)  Tracer.debug(StringUtil.toHexString(shortToByte2(tagInfo[0]), false)  + "|" + StringUtil.fillZero(""+tagInfo[2], 2) + "|" + StringUtil.toHexString(data, i, i + tagInfo[2], false));

        i = tagInfo[1] + tagInfo[2];
      } else {
        return -3;
      }
    }

    return 1;
  }
  ///////////////////////////////////////////////////////////////////////////////////

  private final static short[] UPLOAD_TAG_LIST = {
      (short)1, (short) 0x9F26, // ==AC
      (short)1, (short) 0x9F27, // ==CID
      (short)1, (short) 0x9F10, // ==IAD
      (short)1, (short) 0x9F37, // >>Unpredictable Number
      (short)1, (short) 0x9F36, // ==ATC
      (short)1, (short) 0x95, // ==TVR
      (short)1, (short) 0x9A, // ==Transaction Date
      (short)1, (short) 0x9C, // ==Transaction Type
      (short)1, (short) 0x9F02, // >>Transaction Amount
      (short)0, (short) 0x5F2A, // ??Transaction  Currency Code
      (short)1, (short) 0x82, // ==AIP
      (short)0, (short) 0x9F1A, // ??Terminal Country Code
      (short)1, (short) 0x9F03, // ==Amount Other
      (short)0, (short) 0x9F33, // ??Terminal Capabilities
      (short)0, (short) 0x9F1E, // ??IFD
      (short)1, (short) 0x84, // ==Dedicated File Name
      (short)1, (short) 0x9F09, // Terminal Application Version Number: 需要根据卡片AID，在应用参数中查找
      (short)1, (short) 0x9F41, // >>Terminal Sequence Counter
      (short)1, (short) 0x9F34, // CVM
      (short)0, (short) 0x9F35, // ??Terminal Type
      (short)1, (short) 0x9F74, // Electronic Cash Issuer Authorization Code
//      (short)1, (short) 0x8A,   // ARC//20111011恢复
    };
    private final static int UPLOAD_TAG_LIST_LENGTH = UPLOAD_TAG_LIST.length / 2;

    private LinkedList<byte[]> uploadTag = new LinkedList<byte[]>();
    private byte[] uploadData = new byte[1024];

    public void initEmvData()
    {
      for (int i=0; i < UPLOAD_TAG_LIST_LENGTH; i ++)
      {
        if (UPLOAD_TAG_LIST[i*2] == 1) // 检测是否需要初始化
        {
          uploadTag.set(i, null);
        }
      }
    }


    public int setEmvData(short tag, byte[] tagData, int offset, int length)
    {
      byte[] data = new byte[length];
      System.arraycopy(tagData, offset, data, 0, length);
      return setEmvData(tag, data);
    }

    public int setEmvData(short tag, byte[] tagData)
    {
      for (int i=0; i < UPLOAD_TAG_LIST_LENGTH; i ++)
      {
        if (UPLOAD_TAG_LIST[i*2 + 1] == tag)
        {
          uploadTag.set(i, tagData);
          return i;
        }
      }
      return -1;
    }

    public byte[] createIccData()
    {
      if(Tracer.ENABLE_DEBUG) Tracer.debug("EmvParam createIccData start");
      int uploadDataLength = 0;
      byte[] value = null;
      for (int i=0; i < UPLOAD_TAG_LIST_LENGTH; i ++)
      {
        value = uploadTag.get(i);
        if(Tracer.ENABLE_DEBUG) Tracer.debug("EmvParam createIccData [" + StringUtil.toHexString(NumberUtil.shortToByte2(UPLOAD_TAG_LIST[i*2 + 1]), false)+ "][" + StringUtil.toHexString(value, false) + "]");
        if (value == null)
          continue;

        System.arraycopy(value, 0, uploadData, uploadDataLength, value.length);
        uploadDataLength = uploadDataLength + value.length;
        if(Tracer.ENABLE_DEBUG) Tracer.debug("EmvParam createIccData uploadDataLength[" + uploadDataLength + "]");
      }
      byte[] iccData = new byte[uploadDataLength];
      System.arraycopy(uploadData, 0, iccData, 0, uploadDataLength);

      if(Tracer.ENABLE_DEBUG) Tracer.debug("EmvParam createIccData iccData[" + uploadDataLength + "]");
      if(Tracer.ENABLE_DEBUG) Tracer.dump(iccData);

      return iccData;
    }
    ///////////////////////////////////////////////////////////////////////////////////

    public static byte[] TestTlvCreate()
    {
      TLVProcess tlvdata = new TLVProcess();
      
      tlvdata.initEmvData();
      
      tlvdata.setEmvData((short)0x9F26, StringUtil.hexStringToBytes("0001"));
      tlvdata.setEmvData((short)0x9F27, StringUtil.hexStringToBytes("123456"));
      tlvdata.setEmvData((short)0x9F36, StringUtil.hexStringToBytes("00AB"));
      tlvdata.setEmvData((short)0x9F03, StringUtil.hexStringToBytes("0001"));
      tlvdata.setEmvData((short)0x95, StringUtil.hexStringToBytes("01"));
      
      
      return tlvdata.createIccData();
    }

}
