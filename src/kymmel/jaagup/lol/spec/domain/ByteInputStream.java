package kymmel.jaagup.lol.spec.domain;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class ByteInputStream {

    protected byte[] data;
    protected int index;
    protected ByteOrder byteOrder = ByteOrder.nativeOrder();

    public ByteInputStream(byte[] data) {
        this.data = data;
    }

    public ByteOrder getByteOrder() {
        return byteOrder;
    }

    public void setByteOrder(ByteOrder byteOrder) {
        this.byteOrder = byteOrder;
    }

    public void skip(int len) {
        index += len;
    }
    public byte[] read(int len) {
        return Arrays.copyOfRange(data, index, index += len);
    }

    public int available() {
        return data.length - index;
    }

    public int getIndex() {
        return index;
    }

    public byte nextByte() {
        return data[index++];
    }

    public short nextShort() {
        return ByteBuffer.wrap(this.read(2)).order(byteOrder).getShort();
    }

    public int nextInt() {
        return ByteBuffer.wrap(this.read(4)).order(byteOrder).getInt();
    }

    public float nextFloat() {
        return ByteBuffer.wrap(this.read(4)).order(byteOrder).getFloat();
    }

    public int skipTo(byte[][] needles, int limit) {
        int i, j;
        limit = Math.min(available(), limit);
        for(i = 0; i <= limit; i++) {
            for(byte[] needle : needles) {
                if(available() < i + needle.length)
                    break;
                for(j = 0; j < needle.length; j++)
                    if(data[index + i + j] != needle[j])
                        break;
                if(j == needle.length) {
                    skip(i);
                    return j;
                }
            }
        }
        return -1;
    }

    public int skipTo(byte[] needle, int limit) {
        return skipTo(new byte[][]{needle}, limit);
    }

    public String nextString() {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        while(true) {
            byte temp = nextByte();
            buffer.write(temp);
            if(temp == (byte)0x00)
                break;
        }
        try {
            return new String(buffer.toByteArray(), "UTF-8").trim();
        } catch(UnsupportedEncodingException e) {
            return new String(buffer.toByteArray()).trim();
        }
    }

    public String nextString(int len) {
        return new ByteInputStream(read(len)).nextString();
    }

}
