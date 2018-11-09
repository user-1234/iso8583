package com.commands;

import com.commands.iso8583.TransList8583;
import com.commands.iso8583.pack.Packager;
import com.commands.iso8583.pack.TransDefine;
import com.commands.iso8583.pack.iso8583.HostMessage;
import com.commands.iso8583.pack.iso8583.ISOUtil;
import com.commands.util.ByteUtil;
import com.commands.util.NumberUtil;
import com.commands.util.StringUtil;
import com.commands.util.Tracer;

public class TcpDemo8583
{
  static String hostip = "15.21.6.27";
  static int hostport = 9904;
  static int timeout = 10 * 1000;
  static TcpConnect tcp = new TcpConnect();

  static TransList8583 translist = new TransList8583();
  
  static String masterKey = null;
  static String macKey = null;
  
  public static void sendDemo8583(String msgText)
  {
    translist.init();
    masterKey = null;
    macKey = null;

    translist.tpduAddress = "6000060000";   
    masterKey = "133E07D9193E0EFD";  // 主密钥: 单倍长时，去前面的16个字节
    
    translist.terminalID = "00106853"; // F41: 终端编号
    translist.merchantID = "105152283980004"; // F42: 商户编号

    translist.stan = 88; // F11: 受卡方系统跟踪号，需要每笔交易成功后加1

    String sendStr = msgText;
    // TODO: 测试签到报文
    sendStr = "0089600006000061010000000008000020000000C000120000513030313036383533313035313532323833393830303034001100000022001000803131323230303030303030303030303030303030303030303030303030303030303030303030303130312020202020202020202020202020202020202020202020202020202020202020202020202020";
    byte[] buffer = StringUtil.hexStringToBytes(sendStr);

    translist.transType = TransDefine.TRAN_LOGIN;

    int ret = sendData(buffer, translist);
    
    if (ret > 0)
    {
      unpackSuccess();
    } 
    else if (ret < 0) {
      Tracer.printLine("数据处理失败:" + ret);
      return;
    }
  
  }
  
  private static int sendData(byte[] sendData, TransList8583 translist)
  {
    try
    {    
      byte[] recvData =  null;
           
      recvData = tcp.socketSend(sendData, hostip, hostport, timeout);

      if (recvData != null && recvData.length > (2+11+2+8))
      {
        if (Tracer.ENABLE_DEBUG) Tracer.debug("接收数据成功");
        // 解析返回报文
        int ret = Packager.unpack(recvData, translist);
        if (ret > 0 && "00".equals(translist.responseCode)) {
          if (Packager.getMacState() == Packager.MAC_CHECK_DATA)
          {
            setMAC(Packager.getSendData(),Packager.getMacDataOffset(), Packager.geMacDataLength(), macKey, masterKey);
            return 0; // 需要后续计算MAC等处理
          } else {
            return recvData.length;
          }
        } else {
          Tracer.printLine("ISO8583 data parse fail:" + ret);
          return -2;
        }
      } 
      else {
        if (Tracer.ENABLE_DEBUG) Tracer.debug("签到接收数据错误");
        return -3;
      }

    } catch (Exception e1)
    {
      e1.printStackTrace();
    } finally
    {

    }  
    
    return -1;
  }
  private static void packSuccess()
  {
    if (Tracer.ENABLE_DEBUG) Tracer.debug("ISO8583 pack data length=" + Packager.getSendDataLength());
    if (Tracer.ENABLE_DEBUG) Tracer.dump(Packager.getSendData(), 0, Packager.getSendDataLength(), true);
    String packData = StringUtil.toHexString(Packager.getSendData(), 0, Packager.getSendDataLength());
    StringBuffer sbf = new StringBuffer();
    for (int i = 0; i < packData.length(); i ++)
    {
      if ((i % 24) == 0)
        sbf.append('\n');
      sbf.append(packData.charAt(i));
    }
    Tracer.printLine("ISO8583 pack length=" + Packager.getSendDataLength() + sbf.toString());
    
    byte[] buffer = new byte[Packager.getSendDataLength()];
    System.arraycopy(Packager.getSendData(), 0, buffer, 0, buffer.length);
    

    int ret = sendData(buffer, translist);
    
    if (ret > 0)
    {
      unpackSuccess();
    } 
    else if (ret < 0) {
      Tracer.printLine("处理失败:" + ret);
      return;
    }
  }

  private static void unpackSuccess()
  {
    Tracer.printLine("ISO8583 data parse success" + (Packager.PRINT_UNPACK?Packager.printUnpackData:""));
    if (translist.transType == TransDefine.TRAN_LOGIN)
    {
      
      // 检查MAC Key的check Value，并且截取MAC KEY
      macKey = StringUtil.toHexString(translist.f62_data, false);
      macKey = macKey.trim();   
      byte[] checkValue, respValue;
      int i = 0;
      if (macKey.length() == 48) // TODO: 为了方便，暂时只处理单倍长度
      {// 签到交易返回的 F62 域数据
        i = 24;
        checkValue = DES.calcPinCheckValue(macKey.substring(i, i+16), masterKey);
        i = i + 16;
        respValue = StringUtil.hexStringToBytes(macKey.substring(i, i+8));   
        
        if (ByteUtil.equalByteArray(checkValue, 0, respValue, 0, respValue.length))
        {
          if (Tracer.ENABLE_DEBUG) Tracer.printLine("CheckValue 匹配成功");
          i = 24;
          macKey = macKey.substring(i, i+16);
        } else {
          if (Tracer.ENABLE_DEBUG) Tracer.printLine("CheckValue 匹配错误");
          return;
        }
      }
      
      
      // 设置交易数据的具体值
      translist.transType = TransDefine.TRAN_QPBOC; // Message Type & F03_PROC
      
      translist.batchNo = NumberUtil.parseInt(translist.f60_data, 2, 6, 10); // F60.2: 批次号，需要与签到交易返回的60.2域值一致，否则39域会返回77
//      translist.stan = 88; // F11: 受卡方系统跟踪号，需要每笔交易成功后加1
//      translist.batchNo = 23; // F60.2: 批次号，需要与签到交易返回的60.2域值一致，否则39域会返回77
      translist.setTime(StringUtil.hexStringToBytes("071756")); // F12: 交易时间
      translist.setYearDate(StringUtil.hexStringToBytes("20180515")); // F13: 交易日期
  
      if (translist.transType == TransDefine.TRAN_SALE
          || translist.transType == TransDefine.TRAN_QPBOC)
      { // 12.3.1　基于PBOC借/贷记标准的IC卡离线消费交易
      translist.amount = 100; // F4: 交易金额 12345=123.45
      
      translist.cardNumber = "6236680490000702005"; // F2: 卡号
      translist.expire = StringUtil.hexStringToBytes("2404"); // F14: 有效期
      
      translist.card_sn = StringUtil.hexStringToBytes("0001"); // F23：卡片序列号
      translist.track2Data = "6236680490000702005=24042206021020000".getBytes(); // F35: 二磁道信息
  
      if (translist.transType == TransDefine.TRAN_QPBOC)
      {
      // 电子现金交易无需密码
        translist.pose = StringUtil.hexStringToBytes("0500"); // F22: 050：IC卡读入，没有输入密码；
      } else {
        translist.pinBlock = StringUtil.hexStringToBytes("D1 7E B9 DB F2 17 9C 6B "); // F52
        translist.keyWithCardNo = TransList8583.KEY_WITH_CARD_NO; // F53
        translist.keyAlgorithm = TransList8583.DOUBLE_KEY; // F53      
      }
      
      if (translist.pinBlock != null && translist.pinBlock.length > 0)
        translist.pose = StringUtil.hexStringToBytes("0510"); // F22: 051：IC卡读入，有密码输入；
      else
        translist.pose = StringUtil.hexStringToBytes("0500"); // F22: 050：IC卡读入，没有输入密码；
  
      // 设置55域的IC卡交易数据：注意电子现金交易上传成功，不保证当时就能扣款成功，因为55域的数据要银行系统日终批量后，才能知道55域数据是否正确
      translist.iccData =  StringUtil.hexStringToBytes("9F260857ACE2CF618FF2D09F2701809F101307010103A00002010A0100000000006DA33BA89F37044637A76F9F360201F6950500800468009F10031804259C01009F02060000000001005F2A02015682027C009F1A0201569F03060000000000009F3303E0E1C89F1E0830303030303132338408A0000003330101019F0902008C9F4104000000009F34030203009F3501229F74064543433030318A027931"); // F55
      translist.iccDataLength = (translist.iccData !=null?translist.iccData.length:0);
      
      translist.f60_data = ("36" + StringUtil.fillZero("" +translist.batchNo, 6)).getBytes(); // F60
      translist.f63_data = ("CUP").getBytes(); // F63
      } 
      else  if (translist.transType == TransDefine.TRAN_BALANCE)
      {
      
      translist.cardNumber = "6221682120546663";
      translist.expire = StringUtil.hexStringToBytes("2407"); // F14
      
      translist.pose = StringUtil.hexStringToBytes("0510"); // F22
      translist.card_sn = StringUtil.hexStringToBytes("0001"); // F23
      translist.track2Data = "6221682120546663=24072010000074900000".getBytes();
      
  
      translist.pinBlock = StringUtil.hexStringToBytes("D1 7E B9 DB F2 17 9C 6B "); // F52
      translist.keyWithCardNo = TransList8583.KEY_WITH_CARD_NO; // F53
      translist.keyAlgorithm = TransList8583.DOUBLE_KEY; // F53
  
      translist.iccData =  StringUtil.hexStringToBytes("9F26080F22B8642C86ADCC9F2701809F101307020103A02002010A0100000000006BADBBCB9F3704499898B09F3602005E950500800468009A031708239C01319F02060000000000005F2A02015682027C009F1A0201569F03060000000000009F3303E0E1C89F34034203009F3501229F1E0830303030303132338408A0000003330101029F0902008C9F410400000000"); // F55
      translist.iccDataLength = (translist.iccData !=null?translist.iccData.length:0);
      
      translist.f60_data = ("01" + StringUtil.fillZero("" +translist.batchNo, 6)).getBytes(); // F60
      }
      else {
        Tracer.printLine("交易类型错误");
        return;
      }

    
      int ret = Packager.pack(translist, false, false);
      if (ret > 0) {
        if (Packager.getMacState() == Packager.MAC_SET_DATA)
        {
          setMAC(Packager.getSendData(),Packager.getMacDataOffset(), Packager.geMacDataLength(), macKey, masterKey);
        } else {
          packSuccess();
        }
      } else {
        Tracer.printLine("ISO8583 data process fail:" + ret);
      }
    }
  }

  private static void setMAC(byte[] data, int offset, int dataLength, String macKey, String masterKey)
  { 
    // calculate MAC Data
    if(Tracer.ENABLE_DEBUG) Tracer.debug("setMAC offset=" + offset + " dataLength=" + dataLength);
    if (Tracer.ENABLE_DEBUG) Tracer.dump(data, offset, offset + dataLength, true);

    // 计算MAC值
    byte[] buffer = new byte[8];
    
    if (macKey != null && masterKey != null)
      buffer = DES.calcMacCCB(data, offset, dataLength, macKey, masterKey);
    
    setMACCompleted(buffer);
  }
  public static void setMACCompleted(byte[] buffer)
  {     
    switch(Packager.getMacState())
    {
    case Packager.MAC_SET_DATA:
      Packager.setMacData(buffer);
      packSuccess();
      break;
    case Packager.MAC_CHECK_DATA:
      // check MAC 
      if(ISOUtil.compareByteArray(buffer,0,buffer.length,translist.rsp_mac,0,8) != 0) {
        if (Packager.PRINT_UNPACK) {
          Packager.printUnpackData = Packager.printUnpackData + "\nMAC check error";
        }
      } else 
      {
        if (translist.responseCode != null && translist.responseCode.length() > 0) {
          if (HostMessage.SUCCESS.equals(translist.responseCode) == false) {
            String[] hostMsg = HostMessage.getHostMessage(translist.responseCode);
            if (Packager.PRINT_UNPACK) {
              Packager.printUnpackData = Packager.printUnpackData 
              + "\n" + hostMsg[0] + hostMsg[1]
              + "\n" + hostMsg[2] 
              + "\n" + hostMsg[3];
            }
          }
        }
      }
      unpackSuccess();
      break;
    }
  }


}
