package com.commands.iso8583;

import com.commands.DES;
import com.commands.iso8583.pack.Packager;
import com.commands.iso8583.pack.TransDefine;
import com.commands.iso8583.pack.iso8583.HostMessage;
import com.commands.iso8583.pack.iso8583.ISOUtil;
import com.commands.util.ByteUtil;
import com.commands.util.StringUtil;
import com.commands.util.Tracer;

public class demo8583
{
 
  static TransList8583 translist = new TransList8583();
  
  static String masterKey = null;
  static String macKey = null;
  

  public static void pack(String iso8583Data)
  {
    translist.init();
    masterKey = null;
    macKey = null;

    translist.tpduAddress = "6000060000";   
    
    masterKey = "133E07D9193E0EFD";  // ����Կ: ������ʱ��ȥǰ���16���ֽ�
    macKey = "43E963C67D649908"; // MAC ������Կ
    
    // ���MAC Key��check Value�����ҽ�ȡMAC KEY
    macKey = "A6682B7C1F32C758D0CF7EA87384A99D8B2EE3FED5CB2CB3";
    macKey = macKey.trim();   
    byte[] checkValue, respValue;
    int i = 0;
    if (macKey.length() == 48) // TODO: Ϊ�˷��㣬��ʱֻ����������
    {// ǩ�����׷��ص� F62 ������
      i = 24;
      checkValue = DES.calcPinCheckValue(macKey.substring(i, i+16), masterKey);
      i = i + 16;
      respValue = StringUtil.hexStringToBytes(macKey.substring(i, i+8));   
      
      if (ByteUtil.equalByteArray(checkValue, 0, respValue, 0, respValue.length))
      {
        if (Tracer.ENABLE_DEBUG) Tracer.printLine("CheckValue ƥ��ɹ�");
        i = 24;
        macKey = macKey.substring(i, i+16);
      } else {
        if (Tracer.ENABLE_DEBUG) Tracer.printLine("CheckValue ƥ�����");
        return;
      }
    }

     // ���ý������ݵľ���ֵ
//    translist.transType = TransDefine.TRAN_SALE; // Message Type & F03_PROC
    translist.transType = TransDefine.TRAN_QPBOC; // Message Type & F03_PROC
//    translist.transType = TransDefine.TRAN_BALANCE; // Message Type & F03_PROC
    
    translist.terminalID = "00106853"; // F41: �ն˱��
    translist.merchantID = "105152283980004"; // F42: �̻����
    translist.stan = 77; // F11: �ܿ���ϵͳ���ٺţ���Ҫÿ�ʽ��׳ɹ����1
    translist.batchNo = 23; // F60.2: ���κţ���Ҫ��ǩ�����׷��ص�60.2��ֵһ�£�����39��᷵��77
    translist.setTime(StringUtil.hexStringToBytes("071756")); // F12: ����ʱ��
    translist.setYearDate(StringUtil.hexStringToBytes("20180515")); // F13: ��������

    if (translist.transType == TransDefine.TRAN_SALE
        || translist.transType == TransDefine.TRAN_QPBOC)
    { // 12.3.1������PBOC��/���Ǳ�׼��IC���������ѽ���
    translist.amount = 100; // F4: ���׽�� 12345=123.45
    
    translist.cardNumber = "6236680490000702005"; // F2: ����
    translist.expire = StringUtil.hexStringToBytes("2404"); // F14: ��Ч��
    
    translist.card_sn = StringUtil.hexStringToBytes("0001"); // F23����Ƭ���к�
    translist.track2Data = "6236680490000702005=24042206021020000".getBytes(); // F35: ���ŵ���Ϣ

    if (translist.transType == TransDefine.TRAN_QPBOC)
    {
    // �����ֽ�����������
      translist.pose = StringUtil.hexStringToBytes("0500"); // F22: 050��IC�����룬û���������룻
    } else {
      translist.pinBlock = StringUtil.hexStringToBytes("D1 7E B9 DB F2 17 9C 6B "); // F52
      translist.keyWithCardNo = TransList8583.KEY_WITH_CARD_NO; // F53
      translist.keyAlgorithm = TransList8583.DOUBLE_KEY; // F53      
    }
    
    if (translist.pinBlock != null && translist.pinBlock.length > 0)
      translist.pose = StringUtil.hexStringToBytes("0510"); // F22: 051��IC�����룬���������룻
    else
      translist.pose = StringUtil.hexStringToBytes("0500"); // F22: 050��IC�����룬û���������룻

    // ����55���IC���������ݣ�ע������ֽ����ϴ��ɹ�������֤��ʱ���ܿۿ�ɹ�����Ϊ55�������Ҫ����ϵͳ���������󣬲���֪��55�������Ƿ���ȷ
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
      Tracer.printLine("�������ʹ���");
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
  }
  
  //TODO: Test Data
  public static String testIso8583Data = ""
      +"00 71 " // ISO8583 Data length
      +"60 00 01 00 00 60 21 00 00 00 00 02 00 30 "
      +"20 04 C0 20 C0 98 11 00 00 00 00 00 00 00 99 99 "
      +"00 00 12 02 10 00 06 34 23 26 88 88 82 10 20 08 "
      +"67 4D 12 12 20 14 54 00 00 31 31 32 32 33 33 34 "
      +"34 31 32 33 34 35 36 37 38 39 30 35 35 35 35 35 "
      +"31 35 36 49 B3 27 32 BA 22 DC 43 20 00 00 00 00 "
      +"00 00 00 00 11 00 00 00 01 00 00 45 46 45 37 39 "
      +"30 35 33 "
      ;
  public static void unpack(byte[] iso8583Data)
  {
    if (Tracer.ENABLE_DEBUG) Tracer.debug("ISO8583 unpack data length=" + iso8583Data.length);
    if (Tracer.ENABLE_DEBUG) Tracer.dump(iso8583Data);
    translist.init();
    translist.transType = TransDefine.TRAN_SALE;
    int ret = Packager.unpack(iso8583Data, translist);
    if (ret > 0) {
      if (Packager.getMacState() == Packager.MAC_CHECK_DATA)
      {
        setMAC(Packager.getSendData(),Packager.getMacDataOffset(), Packager.geMacDataLength(), macKey, masterKey);
      } else {
        unpackSuccess();
      }
    } else {
      Tracer.printLine("ISO8583 data parse fail:" + ret);
    }
  
  }
  
  
  private static void unpackSuccess()
  {
    Tracer.printLine("ISO8583 data parse success" + (Packager.PRINT_UNPACK?Packager.printUnpackData:""));
  }
  private static void setMAC(byte[] data, int offset, int dataLength, String macKey, String masterKey)
  { 
    // calculate MAC Data
    if(Tracer.ENABLE_DEBUG) Tracer.debug("setMAC offset=" + offset + " dataLength=" + dataLength);
    if (Tracer.ENABLE_DEBUG) Tracer.dump(data, offset, offset + dataLength, true);

    //TODO: ����MACֵ
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
