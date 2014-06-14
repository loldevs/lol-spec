package kymmel.jaagup.lol.spec;

import kymmel.jaagup.lol.spec.domain.ByteInputStream;
import kymmel.jaagup.lol.spec.domain.Keyframe;
import kymmel.jaagup.lol.spec.exceptions.PacketParseException;

public class StrictParser {

    protected static short PLAYER_SIGNATURE = (short)0x4BC3,
                           TURRET_SIGNATURE = (short)0x9C4A,
                           ITEMS_SIGNATURE = (short)0xFE70,
                           ABILITIES_SIGNATURE = (short)0x1503;

    public static Keyframe parseKeyframe(ByteInputStream stream) throws PacketParseException {
        Keyframe keyframe = new Keyframe();

        /* ### Keyframe Header ### */
        if(stream.nextByte() != 0x03)
            throw new PacketParseException("Invalid keyframe header.");
        keyframe.setTimeStamp(stream.nextFloat());
        stream.read(0xB);
        while(true) {
            byte marker = stream.nextByte();
            stream.read(0x1);
            short signature = stream.nextShort();
            if(signature == PLAYER_SIGNATURE) {
                if(marker == (byte)0xB3)
                    stream.read(0x1);
                else if(marker == (byte)0x93)
                    stream.read(0x4);
                else
                    throw new PacketParseException("Invalid player header marker.");
                parseKeyframePlayer(stream, keyframe);
            } else {
                break;
            }
        }
        return keyframe;
    }
    private static void parseKeyframePlayer(ByteInputStream stream, Keyframe keyframe) throws PacketParseException {
        Keyframe.Player player = keyframe.new Player();
        byte marker;
        short signature;
        int i;

        /* ### Player header ### */
        player.setEntityId(stream.nextInt() - 0x40000000);
        player.setPlayerId(stream.nextByte());
        stream.read(0xD);

        /* ### Summoner name ### */
        player.setName(stream.nextString(0x80));

        /* ### Champion name ### */
        player.setChampion(stream.nextString(0x10));

        /* ### Unknown data ### */
        stream.read(0x28);

        /* ### Error check 1 ### */
        if(stream.nextInt() - 0x40000000 != player.getEntityId())
            throw new PacketParseException("Error check 1 failed.");

        /* ### Runes ### */
        for(i = 0; i < 30; i++) {
            int rune = stream.nextInt();
            if(rune == 0)
                continue;
            player.addRune(rune);
        }

        /* ### Error check 2 ### */
        int matches = 0;
        for(i = 0; i < 2; i++) {
            if(stream.nextInt() == 0x06496EA8)
                matches++;
        }
        if(matches == 0)
            throw new PacketParseException("Error check 2 failed.");

        /* ### Masteries ### */
        for(i = 0; i < 30; i++) {
            byte talentCoords = stream.nextByte();
            byte talentTree = stream.nextByte();
            stream.read(0x2);
            byte talentPoints = stream.nextByte();
            if(talentCoords == 0) {
                i++;
                break;
            }
            int talentId = 4000 +
                    (talentTree - 0x74) * 100 +
                    ((talentCoords >> 0x04) - 0x03) * 10 +
                    (talentCoords & 0x0F);
            player.setTalent(talentId, talentPoints);
        }
        stream.read(0x190 - i * 0x5);

        /* ### Error check 3 ### */
        if(stream.nextByte() != (byte)0x1E)
            throw new PacketParseException("Error check 3 failed.");
        stream.read(0x1);

        /* ### Items header ### */
        marker = stream.nextByte();
        stream.read(0x1);
        signature = stream.nextShort();
        if(signature != ITEMS_SIGNATURE)
            throw new PacketParseException("Invalid items signature.");
        if(marker == (byte)0xB3)
            stream.read(0x3);
        else
            throw new PacketParseException("Invalid items marker");

        /* ### Items ### */
        for(i = 0; i < 10; i++) {
            short itemId = stream.nextShort();
            stream.read(0x2);
            byte itemSlot = stream.nextByte();
            byte itemQuantity = stream.nextByte();
            byte itemCharges = stream.nextByte();
            player.setItem(itemSlot, player.new Item(
                    itemId, itemSlot, itemQuantity, itemCharges
            ));
        }

        /* ### Item cooldowns ### */
        for(i = 0; i < 10; i++) {
            float itemCooldown = stream.nextFloat();
            player.getItems()[i].setCooldown(itemCooldown);
        }

        /* ### Unknown data & Abilities header ### */
        while(true) {
            marker = stream.nextByte();
            stream.read(0x1);
            signature = stream.nextShort();
            if(marker == (byte)0xA3)
                stream.read(0x4 + signature);
            else if(marker == (byte)0xF3)
                stream.read(signature);
            else if(marker == (byte)0xB3) {
                if(signature == ABILITIES_SIGNATURE)
                    break;
                throw new PacketParseException("Invalid abilities signature");
            }
            else
                throw new PacketParseException("Invalid marker.");
        }

        /* ### Abilities ### */
        while(true) {
            stream.read(0x1);
            byte abilityIndex = stream.nextByte();
            byte abilityLevel = stream.nextByte();
            stream.read(0x1);
            marker = stream.nextByte();
            player.setAbility(abilityIndex, player.new Ability(abilityLevel, -1.0f));
            if(marker == (byte)0xF3)
                stream.read(0x2);
            else if(marker == (byte)0x73) {
                float unused = stream.nextFloat();
                stream.read(0x1);
                if(unused != -1.0f)
                    System.out.println("1: " + unused);
            } else if(marker == (byte)0xB3) {
                stream.read(0x2);
                break;
            } else
                throw new PacketParseException("Invalid ability marker.");
        }

        /* ### String data ### */
        while(true) {
            stream.read(0x4);
            String unused = stream.nextString();
            System.out.println(unused);
            marker = stream.nextByte();
            if(marker == (byte)0xF3)
                stream.read(0x1);
            else if(marker == (byte)0xB3) {
                stream.read(0xD);
                break;
            } else
                throw new PacketParseException("Invalid string data marker.");
        }

        /* ### Unknown data ### */
        while(true) {
            marker = stream.nextByte();
            if(marker == (byte)0xF3) {
                stream.read(0x1);
                stream.read(stream.nextShort());
            } else if(marker == (byte)0x73) {
                stream.read(0x4);
                stream.read(stream.nextShort());
            } else if(marker == (byte)0xB3) {
                stream.read(0xB); //TODO: There might be an extra section
                break;
            } else if(marker == (byte)0x33) {
                stream.read(0xE);
                break;
            } else
                throw new PacketParseException("Invalid unknown data (last) marker.");
        }

        /* ### Footer ### */
        stream.read(0x4);

        /* ### Error check 4 ### */
        if(stream.nextInt() - 0x40000000 != player.getEntityId())
            throw new PacketParseException("Error check 4 failed.");
        stream.read(0x1);

        keyframe.addPlayer(player);
    }

}
