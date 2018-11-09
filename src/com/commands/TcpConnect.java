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
        socketOut.flush(); // ˢ���������ʹServer�����յ����ַ���
        return true;
      }
    } catch (IOException e)
    {
      Tracer.error("�������ݴ���:" + e.getMessage());
      e.printStackTrace();
    }
    return false;
  }
  
  private int recvMessage(Socket socket, byte[] recvBuff)
  {
    //���շ������ķ��� 
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
          if (ENABLE_TCP_DEBUG) Tracer.debug("�յ����ݣ�");
          len = 0;
        }
        if (len > 0)
        {// �յ����ݽ�������
          try{
            // �յ����ݺ����ý��ճ�ʱ���Ա��������յ�����
            len = 0;
            socket.setSoTimeout(200);
            len = socketIn.read(recvBuff, offset, recvBuff.length - offset);
            if (ENABLE_TCP_DEBUG) Tracer.debug("���յ����ݳ���:" + len);

          }catch(SocketTimeoutException e){
            //e.printStackTrace(); 
            if (ENABLE_TCP_DEBUG) Tracer.debug("�ȴ�����ʱ!");
          } 
          if (len > 0)
          {
            len = offset + len;
            if (ENABLE_TCP_DEBUG) Tracer.debug("һ���յ����ݳ���:" + len);
          }
        }
      }
    } catch (SocketException e1)
    {
      Tracer.error("�������ݴ��� SocketException:" + e1.getMessage());
      e1.printStackTrace();
    }
    catch (IOException e)
    {
      Tracer.error("�������ݴ��� IOException:" + e.getMessage());
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
   * ���ͱ���
   * @param msgdata ����
   * @param msglen ���ĳ���
   * @param seq ˳���
   * @param ous �����
   * @throws IOException
   */
  private boolean send(byte[] msgdata,int msglen, Socket socket)
  {
    //���շ��ͱ����ֽ�����
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
      socket = new Socket();            //��ʱSocket����δ�󶨱��ض˿�,����δ����Զ�̷�����
      
      socket.setReuseAddress(true);
      SocketAddress remoteAddr = new InetSocketAddress(hostip,hostport);
     
      socket.connect(remoteAddr, socketTimeout); //����Զ�̷�����, ���Ұ������ı��ض˿�
      boolean isConnected = socket.isConnected() && !socket.isClosed();   
      if (ENABLE_TCP_DEBUG) Tracer.debug("socket isConnected[" + isConnected + "]");
    
      //���ͱ���
  
      if (ENABLE_TCP_DEBUG) Tracer.debug("��ʼ��������######################");    
      boolean result = send(sendData, sendData.length, socket);
      if (ENABLE_TCP_DEBUG) Tracer.debug("��ɷ�������:" + result + "######################");  
      if (result == false)
      {
        Tracer.error("�������ݴ���:");    
      } 
      else {
        //���շ������ķ���     
        if (ENABLE_TCP_DEBUG) Tracer.debug("��ʼ��������######################");    
        byte[] recvBuff = new byte[4096];
        int recvLength = recvMessage(socket, recvBuff);
        if (ENABLE_TCP_DEBUG) Tracer.debug("��ɽ�������:" + recvLength + "######################");    
        if (recvLength > 0)
        {
          odata = new byte[recvLength];
          System.arraycopy(recvBuff, 0, odata, 0, recvLength);
          if (ENABLE_TCP_DEBUG) Tracer.debug("�������  ���յ�����Ϊ:");
          if (ENABLE_TCP_DEBUG) Tracer.dump(odata);
        } else {
          Tracer.error("�յ�����Ϊ��");
        }

      
      }
    } catch (UnknownHostException e)
    {
      Tracer.error("Socket���Ӵ��� UnknownHostException:" + e.getMessage());
      e.printStackTrace();
    } catch (IOException e)
    {
      Tracer.error("Socket���Ӵ��� IOException:" + e.getMessage());
      e.printStackTrace();
    } finally{
        try
        {
          if(socket != null)
          {
            socket.close(); //�ر�Socket
          }
        } catch (IOException e)
        {
          Tracer.error("�Ͽ����Ӵ��� IOException:" + e.getMessage());
          e.printStackTrace();
        }

    }
   
    return odata;
    
  }
  
}
