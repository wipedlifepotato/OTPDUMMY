package com.livingstonei2p.otpdummy;

import java.io.*;
import java.nio.charset.Charset;
import org.apache.commons.codec.binary.Base64;

public class OTP {
    protected File mKeyFile;
    protected byte[] mKey;
    protected /*BigInteger*/ int offset;
    public OTP(String KeyFilePath) throws IOException{
        this.mKeyFile = new File(KeyFilePath);
        //check if file readable
        if( !this.mKeyFile.canRead() ) throw new IOException("Can't open file or this shit dont readable; "
                +KeyFilePath);
    }
    public int getOffset(){
        return this.offset;
    }
    public void setOffset(int offset){
        this.offset=offset;
    }
    public String doCryptDecrypt(String message,boolean isEncrypted) throws FileNotFoundException, IOException{
        //change to hypn for consultation
        RandomAccessFile i = new RandomAccessFile(this.mKeyFile,"r");
        i.seek(offset);

        byte[] messageArray;
        if (isEncrypted) {
            messageArray = Base64.decodeBase64(message);
        }else messageArray = (byte[])message.getBytes(Charset.defaultCharset() );
        byte[] cryptedMessage = new byte[messageArray.length+1];

        int x;
        for(x=0;x<messageArray.length;x++){
            byte tmp=i.readByte();
            cryptedMessage[x]= (byte)((messageArray[x])^(tmp))  ;//0x7f?
            assert (cryptedMessage[x]^(tmp) ) == messageArray[x];
        }
        i.close();
        this.offset = offset + x;
        if(isEncrypted) return new String(cryptedMessage, "UTF-8");
        return Base64.encodeBase64String(cryptedMessage);
    }
    public String doCryptDecrypt(String message) throws FileNotFoundException, IOException{
        return doCryptDecrypt(message, false);
    }
        public void ReSizeFile() throws IOException{
                //resize
        }

}
