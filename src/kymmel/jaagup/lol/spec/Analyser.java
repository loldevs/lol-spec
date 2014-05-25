package kymmel.jaagup.lol.spec;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Analyser {

    public static void analyse(byte[] data, String fileName) throws UnsupportedEncodingException {

        int offset = 0x01;

        float timeStamp = ByteBuffer
                .wrap(Arrays.copyOfRange(data, offset, offset + 0x04))
                .order(ByteOrder.LITTLE_ENDIAN)
                .getFloat();

        offset += 0x0F;

        ArrayList<ArrayList<String>> msgs = new ArrayList<ArrayList<String>>();

        for(int j = 0x00; j < 0x0A; j++) {

            boolean stop = false;

            msgs.add(new ArrayList<String>());

            String summonerName, championName;
            int[] itemIds = new int[0x09];
            float[] itemFloats = new float[0x09];
            Map<Byte, Float> abilityFloats = new HashMap<Byte, Float>();

            if(data[offset] == (byte)0xB3)
                offset += 0x17;
            else if(data[offset] == (byte)0x93)
                offset += 0x20;
            else {

                msgs.get(j).add("Unknown byte at " + Integer.toHexString(offset) + " (" + data[offset] + ") (0)");
                stop = true;

            }

            if(stop)
                break;

            byte[] summonerBytes = new byte[0x80];

            for(int i = 0x00; i < 0x80; i++) {

                if(data[offset + i] == (byte)0x00)
                    break;

                summonerBytes[i] = data[offset + i];

            }

            summonerName = new String(summonerBytes, "UTF-8").trim();

            offset += 0x80;

            byte[] championBytes = new byte[0x10];

            for(int i = 0x00; i < 0x10; i++) {

                if(data[offset + i] == (byte)0x00)
                    break;

                championBytes[i] = data[offset + i];

            }

            championName = new String(championBytes, "UTF-8").trim();

            offset += 0x255;

            for(int i = 0x00; i < 0x09; i++) {

                itemIds[i] = ByteBuffer
                        .wrap(Arrays.copyOfRange(data, offset, offset + 0x04))
                        .order(ByteOrder.LITTLE_ENDIAN)
                        .getInt();

                offset += 0x07;

            }

            for(int i = 0x00; i < 0x09; i++) {

                itemFloats[i] = ByteBuffer
                        .wrap(Arrays.copyOfRange(data, offset, offset + 0x04))
                        .order(ByteOrder.LITTLE_ENDIAN)
                        .getFloat();

                if(itemFloats[i] != -1.0f) {

                    msgs.get(j).add(summonerName + " (" + championName + "): Item " + itemIds[i] + " -> " + itemFloats[i] );

                }

                offset += 0x04;

            }

            offset += 0x130;

            while(data[offset] != (byte)0xB3)
                offset++;

            offset += 0x04;

            for(int i = 0x00; i < 0x04; i++) {

                byte ability = data[offset + 0x01];

                if(data[offset + 0x04] == (byte)0xF3 || data[offset + 0x04] == (byte)0xB3)
                    offset += 0x07;

                else if(data[offset + 0x04] == (byte)0x73) {

                    abilityFloats.put(ability,
                            ByteBuffer
                                    .wrap(Arrays.copyOfRange(data, offset + 0x05, offset + 0x09))
                                    .order(ByteOrder.LITTLE_ENDIAN)
                                    .getFloat()
                    );

                    msgs.get(j).add(summonerName + " (" + championName + "): Ability " + ability + " -> " + abilityFloats.get(ability));

                    offset += 0x0A;

                }

                else {

                    msgs.get(j).add("Unknown byte at " + Integer.toHexString(offset + 0x04) + " (" + data[offset] + ") (1)");
                    stop = true;
                    break;

                }

            }

            if(stop)
                break;

            offset += 0x04;

            while(true) {

                if(data[offset] == (byte)0x01)
                    offset += 0x04;

                else {

                    while(true) {

                        if(data[offset] == (byte)0x00)
                            break;

                        offset++;

                    }

                }

                offset++;

                if(data[offset] == (byte)0xF3)
                    offset += 0x06;

                else if(data[offset] == (byte)0xB3)
                    break;

                else {

                    msgs.get(j).add("Unknown byte at " + Integer.toHexString(offset) + " (" + data[offset] + ") (2)");
                    stop = true;
                    break;

                }

            }

            if(stop)
                break;

            offset += 0x0A;

            while(data[offset] == (byte)0x00)
                offset += 0x0D;

            offset += 0x0C;

        }

        boolean headSent = false;

        for(ArrayList<String> list : msgs) {

            if(list.isEmpty())
                break;

            if(!headSent) {

                headSent = true;
                System.out.println(fileName + " (" + timeStamp + ")");

            }

            for(String msg : list)
                System.out.println("\t" + msg);

        }

    }

}
