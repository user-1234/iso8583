/** 
 * File name:            UTF8Util.java
 * 
 * Originally developed: 
 *
 * Create date :        
 * 
 * Description:          This UTF8Util class.
 * 
 * Version:              0.1
 * 
 * Contributors:         
 * 
 * Modifications: 
 * name          version           reasons
 * 
 */
package com.commands.util;



public class UTF8Util {

    /* For converter an byte[] to UTF-8 */
    /*
        U-00000000 - U-0000007F:  0xxxxxxx  
        U-00000080 - U-000007FF:  110xxxxx 10xxxxxx  
        U-00000800 - U-0000FFFF:  1110xxxx 10xxxxxx 10xxxxxx  
        U-00010000 - U-001FFFFF:  11110xxx 10xxxxxx 10xxxxxx 10xxxxxx  
        U-00200000 - U-03FFFFFF:  111110xx 10xxxxxx 10xxxxxx 10xxxxxx 10xxxxxx  
        U-04000000 - U-7FFFFFFF:  1111110x 10xxxxxx 10xxxxxx 10xxxxxx 10xxxxxx 10xxxxxx
     *  only consider first three line!!
     *  if the format is illegal:
     *  shorter then valid length, 0x3f instead
     *  longer then valid length, discard
     */
    private static int DEFAULT_CHAR_SIZE=64;
    private static char[] DEFAULT_CHAR_ARRAY=new char[DEFAULT_CHAR_SIZE];
    /**
     * encode the byte[] to char[] in UTF-8
     * @param buf
     * @param cbuf
     * @return      the cbuf valid length
     */
    private static int bytes2charsUTF8(byte[] buf, int offset, int length,char[] cbuf,boolean bigEndian){
        int cpos=0,pos=offset;
        byte b1,b2;
        b1=0;b2=0;
        while(pos<(offset + length)){
            if((buf[pos] & 0x80)==0x00){ //U-00000000 - U-0000007F:  0xxxxxxx
                b1=0;
                b2=buf[pos];
                pos++;
            }else if((buf[pos] & 0xe0)==0xc0){ //U-00000080 - U-000007FF:  110xxxxx 10xxxxxx
                if((buf[pos+1] & 0x80)==0x80){
                    b1=(byte)(((buf[pos] & 0x1f)>>2) & 0xff);
                    b2=(byte)(((buf[pos] & 0x03)<<6) | (buf[pos+1] & 0x3f) & 0xff);
                    pos+=2;
                }else{
                    /* invalid format, use ? instead 
                     * -- 2006-3-29 13:55:32 */
                    b1=0x00;
                    b2=0x3f;
                    pos+=1;
                }
            }else if((buf[pos] & 0xf0)==0xe0){ //U-00000800 - U-0000FFFF:  1110xxxx 10xxxxxx 10xxxxxx
                if(((buf[pos+1] & 0x80)==0x80) && ((buf[pos+2] & 0x80)==0x80)){
                    b1=(byte)((((buf[pos] & 0x0f)<<4) | ((buf[pos+1] & 0x3f)>>2) ) & 0xff);
                    b2=(byte)(((buf[pos+1] & 0x03)<<6) | (buf[pos+2] & 0x3f) & 0xff);
                    pos+=3;
                }else if((buf[pos+1] & 0x80)==0x80){
                    /* invalid format, use ? instead 
                     * -- 2006-3-29 13:55:32 */
                    b1=0x00;
                    b2=0x3f;
                    pos+=2;
                }else{
                    /* invalid format, use ? instead 
                     * -- 2006-3-29 13:55:32 */
                    b1=0x00;
                    b2=0x3f;
                    pos+=1;
                }
            }else{
                b1=0;
                b2=0;
                pos++;
                continue;
            }
            if(bigEndian){
                cbuf[cpos]=(char)(((b1 & 0xff)<<8 | (b2 & 0xff)) & 0xffff);
            }else{
                cbuf[cpos]=(char)(((b2 & 0xff)<<8 | (b1 & 0xff)) & 0xffff);
            }
            cpos++;
        }
        return cpos;
    }
    /**
     * get the length of the bytes in UTF-8
     * rules: 0xxxxxxx or 11xxxxxx is the first the byte of the 
     * @param buf
     * @return
     */
    private static int bytesUTF8len(byte[] buf, int offset, int length){
        int len=0;
        int bufLength = offset + length;
        if (offset >= buf.length) return len;
        if (bufLength > buf.length) bufLength = buf.length;
        for(int i=offset;i<bufLength;i++){
            if(((buf[i])& 0x80)==0x00 
                    || ((buf[i])& 0xc0)==0xc0 ){
                len++;
            }
        }
        return len;
    }
    /**
     * format the byte[] to String in UTF-8 encode
     * @param buf
     * @param bigEndian
     * @return
     */
    public static String bytes2StringUTF8(byte[] buf, int offset, int length, boolean bigEndian){
        int len=bytesUTF8len(buf, offset, length);
        char[] cbuf;
        
        if(len>DEFAULT_CHAR_SIZE){
            cbuf=new char[len];
        }else{
            cbuf=DEFAULT_CHAR_ARRAY;
        }
        len=bytes2charsUTF8(buf, offset, length,cbuf,bigEndian);
        String str=new String(cbuf,0,len);
        cbuf=null;
        return str;
    }
    public static String bytes2StringUTF8(byte[] buf) {
        return bytes2StringUTF8(buf, 0, buf.length, true);
    }

    /**
     * format the byte[] to String in UNICODE encode
     * @param buf
     * @param bigEndian
     * @return
     */
    public static String bytes2StringUNICODE(byte[] buf, int offset, int length, boolean bigEndian) {
      if (buf != null && offset >= 0 && length >= 2 && buf.length >= (offset + length)){
        int charsLen = length / 2;
        char[] cbuf = new char[charsLen];
        for (int i = 0; i < charsLen; i++) {
          if(bigEndian){
            cbuf[i]=(char)(((buf[i * 2 + offset] & 0xff)<<8 | (buf[i * 2 + 1 + offset] & 0xff)) & 0xffff);
          }else{
            cbuf[i]=(char)(((buf[i * 2 + 1 + offset] & 0xff)<<8 | (buf[i * 2 + offset] & 0xff)) & 0xffff);
          }
        }
        String str=new String(cbuf, 0, charsLen);
        cbuf=null;
        return str;
      }
      
      return null;
    }
    public static String bytes2StringUNICODE(byte[] buf) {
        return bytes2StringUNICODE(buf, 0, buf.length, true);
    }
    
    /* For converter a char[] to UTF-8 encoding byte[] */
    /*
        U-00000000 - U-0000007F:  0xxxxxxx  
        U-00000080 - U-000007FF:  110xxxxx 10xxxxxx  
        U-00000800 - U-0000FFFF:  1110xxxx 10xxxxxx 10xxxxxx  
        U-00010000 - U-001FFFFF:  11110xxx 10xxxxxx 10xxxxxx 10xxxxxx  
        U-00200000 - U-03FFFFFF:  111110xx 10xxxxxx 10xxxxxx 10xxxxxx 10xxxxxx  
        U-04000000 - U-7FFFFFFF:  1111110x 10xxxxxx 10xxxxxx 10xxxxxx 10xxxxxx 10xxxxxx
     *  only consider first three line!!
     *  if the format is illegal:
     *  shorter then valid length, 0x3f instead
     *  longer then valid length, discard
     */
    private static byte[] outputByte=new byte[3];
    private static byte[] DEFAULT_BYTE_ARRAY=new byte[DEFAULT_CHAR_SIZE*3];
    public static int string2BytesUTF8Length(String str){
      return char2ByteUTF8(str,0,str.length(),null,0,0);
  }
    public static byte[] string2BytesUTF8(String str){
        byte[] bufByte;
        if(str.length()>DEFAULT_CHAR_SIZE){
            bufByte=new byte[str.length()*3];
        }else{
            bufByte=DEFAULT_BYTE_ARRAY;
        }
        int byteLen=char2ByteUTF8(str,0,str.length(),bufByte,0,bufByte.length);
        byte[] ret=new byte[byteLen];
        System.arraycopy(bufByte,0,ret,0,byteLen);
        return ret;
    }
    public static int char2ByteUTF8(String input, int inOff, int inEnd, byte[] output,
            int outOff, int outEnd){
        char inputChar;
        int inputSize;
        int outputSize;

        int charOff = inOff;
        int byteOff = outOff;

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
            if (output != null) {
              if (byteOff + outputSize > outEnd) {
                throw new RuntimeException();
              }
              for (int i = 0; i < outputSize; i++) {
                output[byteOff++] = outputByte[i];
              }
            } else {
              byteOff = byteOff + outputSize;
            }
            charOff += inputSize;
        }
        return byteOff - outOff;
    }

}
