package com.commands;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import com.commands.util.StringUtil;
import com.commands.util.Tracer;

public class TcpConnect
{
  public static final boolean ENABLE_TCP_DEBUG  = false; // If it is false , delete LOG information while compiling.

  public static int socketTimeout = 20 * 1000;


  private boolean sendMessage(Socket socket, byte[] sendBuffer)
  {
    try
    {
      boolean isConnected = socket.isConnected() && !socket.isClosed();   
      if (ENABLE_TCP_DEBUG) Tracer.debug("sendMessage isConnected[" + isConnected + "]");

      if (isConnected)
      {
        if (ENABLE_TCP_DEBUG) Tracer.debug("sendMessage sendBuffer:" + StringUtil.toHexString(sendBuffer));

        OutputStream socketOut = socket.getOutputStream();
  
        socketOut.write(sendBuffer);
        socketOut.flush(); // 刷新输出流，使Server马上收到该字符串
        return true;
      }
    } catch (IOException e)
    {
      Tracer.error("发送数据错误:" + e.getMessage());
      e.printStackTrace();
    }
    return false;
  }
  
  private int recvMessage(Socket socket, byte[] recvBuff)
  {
    //接收服务器的反馈 
    int len = -1;
    int offset = 0;

    try
    {
      boolean isConnected = socket.isConnected() && !socket.isClosed();   
      if (ENABLE_TCP_DEBUG) Tracer.debug("recvMessage isConnected[" + isConnected + "]");

      if (isConnected)
      {
        InputStream socketIn = socket.getInputStream();
        
        try{
          socket.setSoTimeout(socketTimeout);
          len = socketIn.read(recvBuff, offset, 1);
          offset = len;
        }catch(SocketTimeoutException e){
          //e.printStackTrace(); 
          if (ENABLE_TCP_DEBUG) Tracer.debug("收到数据！");
          len = 0;
        }
        if (len > 0)
        {// 收到数据接续接收
          try{
            // 收到数据后，重置接收超时，以便于立即收到数据
            len = 0;
            socket.setSoTimeout(200);
            len = socketIn.read(recvBuff, offset, recvBuff.length - offset);
            if (ENABLE_TCP_DEBUG) Tracer.debug("又收到数据长度:" + len);

          }catch(SocketTimeoutException e){
            //e.printStackTrace(); 
            if (ENABLE_TCP_DEBUG) Tracer.debug("等待读超时!");
          } 
          if (len > 0)
          {
            len = offset + len;
            if (ENABLE_TCP_DEBUG) Tracer.debug("一共收到数据长度:" + len);
          }
        }
      }
    } catch (SocketException e1)
    {
      Tracer.error("接收数据错误 SocketException:" + e1.getMessage());
      e1.printStackTrace();
    }
    catch (IOException e)
    {
      Tracer.error("接收数据错误 IOException:" + e.getMessage());
      e.printStackTrace();
    }
    if (ENABLE_TCP_DEBUG) Tracer.debug("recvLength[" + len + "]");
    if (len > 0)
    {
      if (ENABLE_TCP_DEBUG) Tracer.debug("recvMessage recvBuff:" + StringUtil.toHexString(recvBuff, 0, len));
    }
    return len;
  }
  
 
  /**
   * 发送报文
   * @param msgdata 报文
   * @param msglen 报文长度
   * @param seq 顺序号
   * @param ous 输出流
   * @throws IOException
   */
  private boolean send(byte[] msgdata,int msglen, Socket socket)
  {
    //最终发送报文字节数组
    byte[] msgcommand = null;
    msgcommand = new byte[msglen];
    
    System.arraycopy(msgdata, 0, msgcommand, 0, msgdata.length);
      
    return sendMessage(socket, msgcommand);
  }

  public byte[]  socketSend(byte[] sendData, String hostip, int hostport, int timeout) 
  {
    socketTimeout = timeout;
    Socket socket = null;
    byte[] odata= null;
    try
    {
      socket = new Socket();            //此时Socket对象未绑定本地端口,并且未连接远程服务器
      
      socket.setReuseAddress(true);
      SocketAddress remoteAddr = new InetSocketAddress(hostip,hostport);
     
      socket.connect(remoteAddr, socketTimeout); //连接远程服务器, 并且绑定匿名的本地端口
      boolean isConnected = socket.isConnected() && !socket.isClosed();   
      if (ENABLE_TCP_DEBUG) Tracer.debug("socket isConnected[" + isConnected + "]");
    
      //传送报文
  
      if (ENABLE_TCP_DEBUG) Tracer.debug("开始发送数据######################");    
      boolean result = send(sendData, sendData.length, socket);
      if (ENABLE_TCP_DEBUG) Tracer.debug("完成发送数据:" + result + "######################");  
      if (result == false)
      {
        Tracer.error("发送数据错误:");    
      } 
      else {
        //接收服务器的反馈     
        if (ENABLE_TCP_DEBUG) Tracer.debug("开始接收数据######################");    
        byte[] recvBuff = new byte[4096];
        int recvLength = recvMessage(socket, recvBuff);
        if (ENABLE_TCP_DEBUG) Tracer.debug("完成接收数据:" + recvLength + "######################");    
        if (recvLength > 0)
        {
          odata = new byte[recvLength];
          System.arraycopy(recvBuff, 0, odata, 0, recvLength);
          if (ENABLE_TCP_DEBUG) Tracer.debug("发送完成  ，收到数据为:");
          if (ENABLE_TCP_DEBUG) Tracer.dump(odata);
        } else {
          Tracer.error("收到数据为空");
        }

      
      }
    } catch (UnknownHostException e)
    {
      Tracer.error("Socket连接错误 UnknownHostException:" + e.getMessage());
      e.printStackTrace();
    } catch (IOException e)
    {
      Tracer.error("Socket连接错误 IOException:" + e.getMessage());
      e.printStackTrace();
    } finally{
        try
        {
          if(socket != null)
          {
            socket.close(); //关闭Socket
          }
        } catch (IOException e)
        {
          Tracer.error("断开连接错误 IOException:" + e.getMessage());
          e.printStackTrace();
        }

    }
   
    return odata;
    
  }
  
}
