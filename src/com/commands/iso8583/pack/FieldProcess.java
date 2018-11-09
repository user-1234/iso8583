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
 * File name:            FieldSet.java
 * 
 * Originally developed: Commands.Wu
 *
 * Create date :         2006-10-23
 * 
 * Description:          The FieldSet class.
 * 
 * Version:              0.1
 * 
 * Contributors:         
 * 
 * Modifications: 
 * name          version           reasons
 * 
 */
package com.commands.iso8583.pack;

import com.commands.iso8583.TransList8583;
import com.commands.iso8583.pack.iso8583.ISO8583;
import com.commands.iso8583.pack.iso8583.ISOField;
import com.commands.util.Tracer;


public class FieldProcess
{
  public static int setF60(ISO8583 iso, TransList8583 trans)
  {
    if(Tracer.ENABLE_DEBUG) Tracer.debug("set Field60, tranState[" + trans.transType + "]");
    if(trans.f60_data != null && trans.f60_data.length > 0)
    {
      iso.setBit(ISOField.F60, trans.f60_data, trans.f60_data.length);
    }
    return 0;
  }
  public static int setF62(ISO8583 iso, TransList8583 trans)
  {
    if (Tracer.ENABLE_DEBUG) Tracer.debug("set setF62, tranState[" + trans.transType + "]");  
    if(trans.f62_data != null && trans.f62_data.length > 0)
    {
      iso.setBit(ISOField.F62, trans.f62_data, trans.f62_data.length);
    }
    return 0;
  }
  public static int setF63(ISO8583 iso, TransList8583 trans)
  {
    if (Tracer.ENABLE_DEBUG) Tracer.debug("set Field63, tranState[" + trans.transType + "]");  
    if(trans.f63_data != null && trans.f63_data.length > 0)
    {
      iso.setBit(ISOField.F63, trans.f63_data, trans.f63_data.length);
    }
    return 0;
  }
  
  public static int procF60(byte[] buffer, TransList8583 trans)
  {
    if (Tracer.ENABLE_DEBUG) Tracer.debug("Start procF60, tranState[" + trans.transType + "]");  
    trans.f60_data = new byte[buffer.length];
    System.arraycopy(buffer, 0, trans.f60_data, 0, buffer.length);

    return buffer.length;
  }
  public static int procF62(byte[] buffer, TransList8583 trans)
  {
    if (Tracer.ENABLE_DEBUG) Tracer.debug("Start procF62, tranState[" + trans.transType + "]");  
    trans.f62_data = new byte[buffer.length];
    System.arraycopy(buffer, 0, trans.f62_data, 0, buffer.length);

    return buffer.length;
  }
  public static int procF63(byte[] buffer, TransList8583 trans)
  {
    if (Tracer.ENABLE_DEBUG) Tracer.debug("Start procF63, tranState[" + trans.transType + "]");  
    trans.f63_data = new byte[buffer.length];
    System.arraycopy(buffer, 0, trans.f63_data, 0, buffer.length);

    return buffer.length;
  }

}