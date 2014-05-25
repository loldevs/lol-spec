package kymmel.jaagup.lol.spec;

import kymmel.jaagup.lol.spec.util.FileUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Search {

    public static final byte[] CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_-. ".getBytes();

    public static void main(String[] args) throws IOException {

        /*

        for(int j = 5; j < 44; j++) {

            byte[] fileBytes = IO.readFileBytes("EUN1/750447783/" + j + ".keyframe");
            System.out.println("Keyframe number " + j);

            int[] indexes = substrIndexes(fileBytes, "Turret_".getBytes());

            for(int i = 0; i < indexes.length; i++) {

                int index = indexes[i];
                byte[] data = new byte[180];

                data = Arrays.copyOfRange(fileBytes, index - 5, index + 175);

                IO.writeFile("keyframeTurrets/" + String.format("keyframe%02d.id%02d", j, data[0]), data);

            }

        }

        */

        String[] strings = listStrings(FileUtil.readFileBytes("01"));

        for(String string : strings)
            System.out.println(string);

    }

    public static String[] listStrings(byte[] data) {

        ArrayList<String> strings = new ArrayList<String>();

        ByteArrayOutputStream baos = null;
        int curLen = 0;

        for(int i = 0; i < data.length; i++) {

            boolean isASCII = contains(CHARS, data[i]);

            if(isASCII) {

                if(curLen == 0)
                    baos = new ByteArrayOutputStream();

                curLen++;
                baos.write(data[i]);

            } else {

                if(curLen > 3 && baos != null)
                    strings.add(baos.toString());

                curLen = 0;

            }

        }

        return strings.toArray(new String[strings.size()]);

    }

    public static int[] substrIndexes(byte[] data, byte[] substr) {

        ArrayList<Integer> indexes = new ArrayList<Integer>();

        int i, j;

        for(i = 0; i <= data.length - substr.length; i++) {

            for(j = 0; j < substr.length; j++)
                if(data[i + j] != substr[j])
                    break;

            if(j == substr.length)
                indexes.add(i);

        }

        System.out.println(indexes.size());

        int[] pIndexes = new int[indexes.size()];
        for(i = 0; i < indexes.size(); i++)
            pIndexes[i] = indexes.get(i).intValue();

        return pIndexes;

    }

    public static boolean contains(byte[] array, byte v ) {
        for (byte e : array)
            if (e == v)
                return true;
        return false;
    }

}
