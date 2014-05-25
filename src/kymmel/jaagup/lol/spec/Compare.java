package kymmel.jaagup.lol.spec;

import kymmel.jaagup.lol.spec.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class Compare {

    public static void main(String[] args) throws IOException {

        ArrayList<byte[]> fileDatas = new ArrayList<byte[]>();
        ArrayList<Integer> changes = new ArrayList<Integer>();

        for(int i = 5; i < 43; i++) {

            if(!new File(String.format("keyframe%02d.id02", i)).exists())
                continue;

            fileDatas.add(FileUtil.readFileBytes(String.format("keyframe%02d.id08", i)));

            if(i > 0)
                for(int j = 0; j < fileDatas.get(i).length; j++)
                    if(fileDatas.get(0)[j] != fileDatas.get(i)[j] && !changes.contains(j))
                        changes.add(j);

        }
        Collections.sort(changes);

        StringBuilder sb = new StringBuilder();
        for(int i : changes)
            sb.append(String.format("%02X ", i));
        System.out.println(sb.toString());

        for(byte[] i : fileDatas) {
            sb = new StringBuilder();
            for(int j : changes)
                sb.append(String.format("%02X ", i[j]));
            System.out.println(sb.toString());
        }


    }

}
