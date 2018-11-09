package com.commands;


import java.awt.Button;
import java.awt.Choice;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.commands.iso8583.demo8583;
import com.commands.util.NumberUtil;
import com.commands.util.StringUtil;
import com.commands.util.Tracer;


public class Test8583 extends Frame 
  implements ActionListener
{
  public static String  currentSelected = "";


  private final static String CUP_8583    = "CUP(only 8583)";
  private final static String CUP_8583_FULL = "CUP(Full Pack)";
  private final static String TLVParse     = "TLVParse";
  private final static String ISO8583_UNPACK          = "ISO8583 Unpack";
  private final static String ISO8583_PACK            = "ISO8583 Pack";
  private final static String ISO8583_TCP_TEST        = "建行POS前置8583测试";

  private final static String BL_RUN_FUNC   = "Run";
  private final static String BL_SAVE_FILE  = "Save File";
  private final static String BL_CLEAR      = "Clear";
  private final static String BL_CONVERT    = "Convert";

  public static boolean disablePrintLog()
  {
    return false;
  }
	/**
   *
   */
  private static final long serialVersionUID = 1343434334L;
  final int HEIGHT = 550;
	final int WIDTH = 550;

	private Button unpackButton;
	private Button saveButton;
  private Button clearButton;
  private Button toHexButton;
	private Panel buttonPanel;

	private Panel messagePanel;
	private TextArea messageAreaIn;
	private TextArea messageAreaOut;

	private PrintStream printStream;

	private Choice packTypeChoice;
	
	private boolean clearMessageAreaOut = false;


	/**
	 * Main method. Checks to see if the command line agrument is requesting
	 * usage informaition (-h, -help), if it is, display a usage message and
	 * exit, otherwise create a new <code>SerialDemo</code> and set it visible.
	 */
	public static void main(String[] args) {
		if ((args.length > 0)
				&& (args[0].equals("-h") || args[0].equals("-help"))) {
			System.out.println("usage: java TestPackager [configuration File]");
			System.exit(1);
		}

		Test8583 test8583 = new Test8583(args);
		test8583.setVisible(true);
		test8583.repaint();
	}

	/**
	 * Create new <code>TestTongCard</code> and initilizes it. Parses args to find
	 * configuration file. If found, initial state it set to parameters in
	 * configuration file.
	 *
	 * @param args command line arguments used when program was invoked.
	 */
	public Test8583(String[] args) {
		super("TestPackager");

		// Set up the GUI for the program
		addWindowListener(new CloseHandler(this));

		messagePanel = new Panel();
		messagePanel.setLayout(new GridLayout(2, 1));

		messageAreaIn = new TextArea();
		messagePanel.add(messageAreaIn);

		messageAreaOut = new TextArea();
		messageAreaOut.setEditable(false);
		messagePanel.add(messageAreaOut);

		add(messagePanel, "Center");

		buttonPanel = new Panel();

    packTypeChoice = new Choice();
    packTypeChoice.addItem(CUP_8583);
    packTypeChoice.addItem(CUP_8583_FULL);
    packTypeChoice.addItem(TLVParse);
    packTypeChoice.addItem(ISO8583_UNPACK);
    packTypeChoice.addItem(ISO8583_PACK);
    packTypeChoice.addItem(ISO8583_TCP_TEST);
    
    
    
    packTypeChoice.addItemListener(new ItemListener()
    {
      public void itemStateChanged(ItemEvent e)
      {
        String  current = packTypeChoice.getSelectedItem();
        if (CUP_8583.equals(current))
        {
          clearMessageAreaOut = true;
          if (Tracer.ENABLE_DEBUG) Tracer.debug("[" + packTypeChoice.getSelectedItem() + "]"
              + "ISO8583 Message Data(Nothing [TPDU/APDU])"
              );
        }
        else if (CUP_8583_FULL.equals(current))
        {
          clearMessageAreaOut = true;
          if (Tracer.ENABLE_DEBUG) Tracer.debug("[" + packTypeChoice.getSelectedItem() + "]"
              + "[2字节数据长度][5字节TPDU][6字节APDU][ISO8583 Message Data)"
              );
        }
        else if (ISO8583_UNPACK.equals(current))
        {
          clearMessageAreaOut = true;
          messageAreaIn.setText(demo8583.testIso8583Data); // 设置一个默认值，便于修改
        }

      }
    });
    
    buttonPanel.add(packTypeChoice);

		unpackButton = new Button(BL_RUN_FUNC);
		unpackButton.addActionListener(this);
		buttonPanel.add(unpackButton);

		saveButton = new Button(BL_SAVE_FILE);
		saveButton.addActionListener(this);
		buttonPanel.add(saveButton);

		clearButton = new Button(BL_CLEAR);
		clearButton.addActionListener(this);
		buttonPanel.add(clearButton);

		toHexButton = new Button(BL_CONVERT);
		toHexButton.addActionListener(this);
    buttonPanel.add(toHexButton);


		Panel southPanel = new Panel();

		GridBagLayout gridBag = new GridBagLayout();
		GridBagConstraints cons = new GridBagConstraints();

		southPanel.setLayout(gridBag);

		cons.gridwidth = GridBagConstraints.REMAINDER;
		gridBag.setConstraints(buttonPanel, cons);
		southPanel.add(buttonPanel);

		add(southPanel, "South");


		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		setLocation(screenSize.width / 2 - WIDTH / 2, screenSize.height / 2
				- HEIGHT / 2);

		setSize(WIDTH, HEIGHT);

		// 用自己的重载的OutputStream创建一个PrintStream
    printStream = new PrintStream(new MyOutputStream());
    // 指定标准输出到自己创建的PrintStream
    System.setOut(printStream);
    System.setErr(printStream);
	}

  public class MyOutputStream extends OutputStream{
    public void write(int arg0) throws IOException {
        // 写入指定的字节，忽略
    }

    public void write(byte data[]) throws IOException{
        // 追加一行字符串
      messageAreaOut.append(new String(data));
    }

    public void write(byte data[], int off, int len) throws IOException {
        // 追加一行字符串中指定的部分，这个最重要
      messageAreaOut.append(new String(data, off, len));
        // 移动TextArea的光标到最后，实现自动滚动
      messageAreaOut.setCaretPosition(messageAreaOut.getText().length());
    }
  }

	/**
	 * Responds to the menu items and buttons.
	 */
	public void actionPerformed(ActionEvent e) {
		String cmd = e.getActionCommand();
		
		currentSelected = packTypeChoice.getSelectedItem();

    // Unpack Data.
    if (cmd.equals(BL_RUN_FUNC)) {
      if (clearMessageAreaOut)
      {
        clearMessageAreaOut = false;
        messageAreaOut.setText("");
      }
      
      BL_runFunc(packTypeChoice.getSelectedItem(), messageAreaIn, messageAreaOut);
      return;
    }

    // Saves a configuration file.
    if (cmd.equals(BL_SAVE_FILE)) {
      FileDialog fd = new FileDialog(this, "Save File", FileDialog.SAVE);
      fd.setVisible(true);
      String file = fd.getFile();
      if (file != null) {
        String dir = fd.getDirectory();
        
        BL_SaveFile(dir, file, packTypeChoice.getSelectedItem(), messageAreaIn, messageAreaOut);
      }
      return;
    }

    // Convert.
    if (cmd.equals(BL_CONVERT)) {
      BL_convert(packTypeChoice.getSelectedItem(), messageAreaIn, messageAreaOut);
      return;
    }

		// Clear.
		if (cmd.equals(BL_CLEAR)) {
      messageAreaIn.setText("");
      messageAreaOut.setText("");
          

//      String curStr = "20160720";
//      byte[] curDate = StringUtil.hexStringToBytes(curStr);
//      long day1 = AppUtil.convertDateToDays(curDate, 1970); 
//      
//      curStr = "20160427";
//      curDate = StringUtil.hexStringToBytes(curStr);
//      long day2= AppUtil.convertDateToDays(curDate, 1970); 
//
//      if (Tracer.ENABLE_DEBUG) Tracer.debug("" + day1 + "-" + day2 +"=" + (day1 - day2));
//
//       curStr = "20160720172116";
//       byte[] curTime = StringUtil.hexStringToBytes(curStr);
//       long timeSec = AppUtil.convertDateToSecond(curTime);
//       if (Tracer.ENABLE_DEBUG) Tracer.debug("curStr[" + curStr + "][" + StringUtil.toHexString(NumberUtil.longToByte8(timeSec),4,8,false) + ":" + timeSec+  "]");
//
//       curStr = "20160720172125";
//       curTime = StringUtil.hexStringToBytes(curStr);
//       timeSec = AppUtil.convertDateToSecond(curTime);
//       if (Tracer.ENABLE_DEBUG) Tracer.debug("curStr[" + curStr + "][" + StringUtil.toHexString(NumberUtil.longToByte8(timeSec),4,8,false) + ":" + timeSec+  "]");
//
//       timeSec = 0x571A11E2; // 
//       curStr = StringUtil.toHexString(AppUtil.convertSecondToDate(timeSec), false);
//       if (Tracer.ENABLE_DEBUG) Tracer.debug("curStr[" + curStr + "]" );

//    FtpUtil.getDataFiles("192.168.0.112", 9090, "ftp", "ftp", "", "", "11.txt");
//    FtpUtil.ftpdownLoad("192.168.0.108", "8991", "rain", "rain", "/down/", "D:\\AA\\", "DATFLEMD_FCRJAF0_150_20160123");
//    FtpUtil.ftpdownLoad("192.168.0.112", "9090", "ftp", "ftp", "/", "D:\\AA\\", "111.txt");

      return;
		}


	}


	/**
	 * Cleanly shuts down the applicaion. first closes any open ports and cleans
	 * up, then exits.
	 */
	private void shutdown() {
		System.exit(1);
	}

	/**
	 * Handles closing down system. Allows application to be closed with window
	 * close box.
	 */
	class CloseHandler extends WindowAdapter {

		Test8583 sd;

		public CloseHandler(Test8583 sd) {
			this.sd = sd;
		}

		public void windowClosing(WindowEvent e) {
			sd.shutdown();
		}
	}
	public static void charToUTF8(String input)
	{
	  if (input == null || input.length() < 1)
	    return;
    int inOff= 0;
    int inEnd=input.length();
    char inputChar;
    int inputSize;
    int outputSize;

    int charOff = inOff;
    byte[] outputByte=new byte[3];
    String uft8 = "";

    while (charOff < inEnd) {
        inputChar = input.charAt(charOff);
        if (inputChar < 0x80) {
            outputByte[0] = (byte) inputChar;
            inputSize = 1;
            outputSize = 1;
        } else if (inputChar < 0x800) {
            outputByte[0] = (byte) (0xc0 | ((inputChar >> 6) & 0x1f));
            outputByte[1] = (byte) (0x80 | (inputChar & 0x3f));
            inputSize = 1;
            outputSize = 2;
        } else {
            outputByte[0] = (byte) (0xe0 | ((inputChar >> 12)) & 0x0f);
            outputByte[1] = (byte) (0x80 | ((inputChar >> 6) & 0x3f));
            outputByte[2] = (byte) (0x80 | (inputChar & 0x3f));
            inputSize = 1;
            outputSize = 3;
        }
        uft8 = StringUtil.toHexString(outputByte, 0, outputSize, false);
        Tracer.printLine("["  + inputChar + "]["+ uft8 + "]");

        charOff += inputSize;
    }
	}
	
	 /**
   * 获取当前日期
   * @ author sys
   * @return
   */
  public static Date getDate() {
    Calendar calendar = Calendar.getInstance();
    return calendar.getTime();
  }

	 /**
   * 格式化日期
   * @ author sys
   * @param date
   * @param fmt
   * @return
   * @throws Exception
   */
  public static String formatDate(Date date, String fmt) throws Exception {
    if (date == null) {
      return "";
    }
    SimpleDateFormat myFormat = new SimpleDateFormat(fmt);
    return myFormat.format(date);
  }
  
//  public String getNewDate(String date, String oldDate)
//  {
//    if (date == null || date.length() < 8)
//      return oldDate;
//      
//    if (oldDate == null || oldDate.length() < 8)
//      return date;
//
//    if (date != null && date.length() >= 8)
//    {
//      /* The legitimacy of date is checked  
//        1,3,5,7,8,10,12 month is 31 days, 
//        4,6,9,11 month is 30 days, 
//        2 month: generic 28, leap year 29 days
//      */ 
//      try
//      {
//        int year = Integer.parseInt(date.substring(0,4));
//        int month = Integer.parseInt(date.substring(4,6));
//        int day = Integer.parseInt(date.substring(6,8));
//
//        int oldYear = Integer.parseInt(oldDate.substring(0,4));
//        int oldMonth = Integer.parseInt(oldDate.substring(4,6));
//        int oldDay = Integer.parseInt(oldDate.substring(6,8));
//        if (year < oldYear)
//          year = oldYear;
//        if (month >= 1 && month <= 12 && day >= 1 && day <= 31)
//        {
//          if ((month == 4 || month  == 6 || month == 9 || month  == 11) && day > 30)
//            return false;
//          if (year % 4 == 0 || (year % 100 == 0 && year %4 == 0))
//          { // leap year check
//            if (month == 2 && day > 29)
//              return false;
//          } else {
//            if (month == 2 && day > 28)
//              return false;
//          } 
//          return true;
//        }
//      } catch (Exception e)
//      {
//      }
//    }
//    
//    return false;
//  }
  
  public static void BL_runFunc(String selectedItem, TextArea messageAreaIn, TextArea messageAreaOut)
  {
    String  current = selectedItem;
    byte[] buffer =  null;
    int i;
    try
    {
      buffer = StringUtil.hexStringToBytes(messageAreaIn.getText());
    } catch (Exception eh)
    {
    }
    if (Tracer.ENABLE_DEBUG) Tracer.debug("Unpack Data========[" + current + "]=========");
    if (CUP_8583.equals(current))
    {
      com.commands.iso8583.pack.iso8583.ISO8583.setPackType(com.commands.iso8583.pack.iso8583.ISO87Pack.isoTable);
      com.commands.Test8583Unpack.unpack(buffer, 0);
    }
    else if (CUP_8583_FULL.equals(current))
    {
      com.commands.iso8583.pack.iso8583.ISO8583.setPackType(com.commands.iso8583.pack.iso8583.ISO87Pack.isoTable);
      com.commands.Test8583Unpack.unpackFullPack(buffer);
    }
    else if (TLVParse.equals(current))
    {
      if (buffer != null && buffer.length > 0)
      {
        StringBuffer sbuf = new StringBuffer();
        sbuf.append("\\x");
        StringUtil.appendHex(sbuf, buffer[0]);
        for(i = (1); i < buffer.length; i++)
        {
          sbuf.append("\\x");
          StringUtil.appendHex(sbuf, buffer[i]);
        }
        if (Tracer.ENABLE_DEBUG) Tracer.debug("length= " + buffer.length);
        if (Tracer.ENABLE_DEBUG) Tracer.printLine(sbuf.toString());
      }
      TLVProcess.TestTlvParse(buffer);
    }
    else if (ISO8583_UNPACK.equals(current))
    {
      demo8583.unpack(buffer);
    }
    else if (ISO8583_PACK.equals(current))
    {
      demo8583.pack(messageAreaIn.getText());
    }
    else if (ISO8583_TCP_TEST.equals(current))
    {
      TcpDemo8583.sendDemo8583(messageAreaIn.getText());
    }
    
  }
  public static void BL_SaveFile(String dir, String file, String selectedItem, TextArea messageAreaIn, TextArea messageAreaOut)
  {
    String  current = selectedItem;
    byte[] buffer = null;

    String filename = dir + file;
    FileOutputStream fos = null;
       
    
    try
    {
      fos = new FileOutputStream(filename);
      {
        buffer = messageAreaOut.getText().getBytes();
        fos.write(buffer);
      }
    } catch (Exception e1)
    {
      e1.printStackTrace();
    } finally
    {
      try
      {
        fos.close();
      } catch (Exception e2)
      {
      }
    }
  }

  public static void BL_convert(String selectedItem, TextArea messageAreaIn, TextArea messageAreaOut)
  {
    String  current = selectedItem;
    String data = messageAreaIn.getText();

    int dataLength = 0;
    int i;
       
    data = data.replace('\r', ' ');
    data = data.replace('\n', ' ');
    data = StringUtil.trimSpace(data);
    dataLength = data.length();
    if (Tracer.ENABLE_DEBUG) Tracer.debug("Data length:" + data.length());
    Tracer.printLine(data);
    data = StringUtil.toHexString(data.getBytes(), false);
    Tracer.printLine(StringUtil.toHexString(NumberUtil.shortToByte2((short)dataLength),false) + data);

  }
}
