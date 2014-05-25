package kymmel.jaagup.lol.spec;


import kymmel.jaagup.lol.spec.domain.Keyframe;
import kymmel.jaagup.lol.spec.domain.ByteInputStream;
import kymmel.jaagup.lol.spec.util.ByteUtil;
import kymmel.jaagup.lol.spec.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;

public class Parser {

    public static Keyframe keyframe(byte[] data) throws ParseException {

        ByteInputStream stream = new ByteInputStream(data);
        Keyframe keyframe = new Keyframe();

        int    intMarker;
        byte   byteMarker;

        //Keyframe Header
        stream.skip(0x1);
        keyframe.setTimeStamp(stream.nextFloat());
        stream.skip(0x5);

        //Player Segment
        int seek, i;

        for(int p = 0; p < 12; p++) {

            Keyframe.Player player = keyframe.new Player();

            //Header
            seek = stream.skipTo(new byte[][]{
                    new byte[]{(byte) 0xB3, (byte) 0x00, (byte) 0xC3, (byte) 0x4B, (byte) 0x00},
                    new byte[]{(byte) 0xB3, (byte) 0x01, (byte) 0xC3, (byte) 0x4B, (byte) 0x00},
                    new byte[]{(byte) 0x93, (byte) 0x00, (byte) 0xC3, (byte) 0x4B, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00},
                    new byte[]{(byte) 0x93, (byte) 0x01, (byte) 0xC3, (byte) 0x4B, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00}
            }, 0x100);
            if(seek < 0)
                break;
            System.out.println(ByteUtil.toHexString(stream.nextByte()) + " " + ByteUtil.toHexString(stream.nextByte()));
            stream.skip(seek-0x2);
            intMarker = stream.nextInt();
            player.setEntityId(intMarker - 0x40000000);

            keyframe.addPlayer(player);
            player.setPlayerId(stream.nextInt());
            stream.skip(0xD);

            //Summoner name
            player.setName(stream.nextString(0x80));

            //Champion name
            player.setChampion(stream.nextString(0x10));

            //Unknown data 0
            stream.skip(0x28);

            //Error check 0
            intMarker = stream.nextInt();
            if(intMarker - 0x40000000 != player.getEntityId())
                throw new ParseException(
                        "Failed errorCheck0 at 0x" +
                                Integer.toHexString(stream.getIndex() - 0x4).toUpperCase() +
                                ", expected 0x" +
                                Integer.toHexString(player.getEntityId() + 0x40000000).toUpperCase() +
                                ", got 0x" +
                                Integer.toHexString(intMarker).toUpperCase() +
                                ", iteration " +
                                p,
                        stream.getIndex() - 0x4
                );

            //Runes
            for(i = 0; i < 30; i++) {
                intMarker = stream.nextInt();
                if(intMarker > 0x0)
                    player.addRune(intMarker);
            }

            //Talents header
            for(i = 0; i < 1; i++) {
                if(Arrays.equals(
                        new byte[] { (byte) 0xA8, (byte) 0x6E, (byte) 0x49, (byte) 0x06 },
                        stream.read(0x4)
                ))
                    break;
            }
            if(i > 1)
                throw new ParseException(
                        "Unable to find talents.header at 0x" +
                                Integer.toHexString(stream.getIndex() - 2 * 0x4).toUpperCase() +
                                ", iteration " +
                                p,
                        stream.getIndex() - 2 * 0x4
                );
            stream.skip((1 - i) * 0x4);

            //Talents
            for(i = 0; i < 30; i++) {
                byte talentCoords = stream.nextByte();
                byte talentTree = stream.nextByte();
                stream.skip(0x2);
                byte points = stream.nextByte();
                if(points == (byte)0x0)
                    break;
                int talentId = 4000 +
                        (talentTree - 0x74) * 100 +
                        ((talentCoords >> 0x04) - 0x03) * 10 +
                        (talentCoords & 0x0F);
                player.setTalent(talentId, points);
            }
            while(true) {
                if(stream.nextByte() != (byte)0x00)
                    break;
            }
            stream.skip(0x1);

            //Items header
            seek = stream.skipTo(
                    new byte[][] {
                            new byte[] {
                                    (byte) 0xB3, (byte) 0x00, (byte) 0x70, (byte) 0xFE,
                                    (byte) 0x00, (byte) 0x0C, (byte) 0x01
                            },
                            new byte[] {
                                    (byte) 0xB3, (byte) 0x01, (byte) 0x70, (byte) 0xFE,
                                    (byte) 0x00, (byte) 0x0C, (byte) 0x01
                            }
                    },
                    0x0
            );
            if(seek < 0)
                throw new ParseException(
                        "Unable to find player.items.header at 0x" +
                                Integer.toHexString(stream.getIndex()).toUpperCase() +
                                ", iteration " +
                                p,
                        stream.getIndex()
                );
            stream.skip(seek);

            //Items
            for(i = 0; i < 9; i++) {
                short itemId = stream.nextShort();
                stream.skip(0x2);
                byte itemSlot = stream.nextByte();
                byte itemQuantity = stream.nextByte();
                byte itemCharges = stream.nextByte();
                player.setItem(
                        i,
                        player.new Item(itemId, itemSlot, itemQuantity, itemCharges)
                );
            }
            for(i = 0; i < 9; i++) {
                player.getItems()[i].setCooldown(stream.nextFloat());
            }

            //debug
            //System.out.print("\t" + p + " " + ByteUtils.toHexString(stream.read(0xC)));
            stream.skip(-0xC);
            i = stream.getIndex();

            //Abilities header
            seek = stream.skipTo(new byte[][]{
                    new byte[]{(byte) 0xB3, (byte) 0x00, (byte) 0x03, (byte) 0x15},
                    new byte[]{(byte) 0xB3, (byte) 0x01, (byte) 0x03, (byte) 0x15},
                    new byte[]{(byte) 0xF0, (byte) 0x43, (byte) 0x03, (byte) 0x15}
            }, 0x200);
            if(seek < 0)
                throw new ParseException(
                        "Unable to find player.abilities.header at 0x" +
                                Integer.toHexString(stream.getIndex()).toUpperCase() +
                                ", iteration " +
                                p,
                        stream.getIndex()
                );
            stream.skip(seek);

            //debug
            //System.out.println(": " + Integer.toHexString(stream.getIndex() - i));

            for(i = 0; i < 4; i++) {
                stream.skip(0x1);
                byte abilityIndex = stream.nextByte();
                byte abilityLevel = stream.nextByte();
                stream.skip(0x1);
                byteMarker = stream.nextByte();
                float unknown = -1.0f;
                if(i < 3 && byteMarker == (byte)0xF3)
                    stream.skip(0x1);
                else if(i < 3 && byteMarker == (byte)0x73)
                    unknown = stream.nextFloat();
                else if(i == 3 && byteMarker == (byte)0xB3)
                    stream.skip(0x1);
                else
                    throw new ParseException(
                            "Unexpected byteMarker in player.ability at 0x" +
                                    Integer.toHexString(stream.getIndex()).toUpperCase() +
                                    ", item " +
                                    i +
                                    ", iteration " +
                                    p,
                            stream.getIndex()
                    );
                player.setAbility(
                        abilityIndex,
                        player.new Ability(abilityLevel, unknown)
                );
                stream.skip(0x01);
            }

        }

        return keyframe;

    }

    public static void main(String[] args) throws IOException, NullPointerException {

        File[] files = new File("analysis/keyframes/").listFiles();
        assert files != null;
        long parseTime = 0L;
        int parseNum = 0;
        int failedNum = 0;

        for(File file : files) {

            try {
                System.out.println(file.getName());
                parseNum++;
                long startTime = System.currentTimeMillis();
                Keyframe keyframe = keyframe(FileUtil.readFileBytes(file.getCanonicalPath()));
                parseTime += System.currentTimeMillis() - startTime;
                ArrayList<String> results = new ArrayList<String>();
            } catch(ParseException e) {
                System.err.println("ParseException in " + file.getName());
                System.err.println("\t" + e.getMessage());
                failedNum++;
            }
        }

        System.out.println("Parsing took " + parseTime + "ms for " + parseNum + " keyframes, avg. " +
                parseTime / parseNum + "ms/keyframe, while failing to parse " + failedNum + " keyframes (" +
                ((double)(failedNum) * 100 / parseNum) + "% fail rate)."
        );

    }

}
