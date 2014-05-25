package kymmel.jaagup.lol.spec.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Keyframe {

    public class Player {

        public class Item {

            protected short itemId;
            protected byte  slot,
                            quantity,
                            charges;
            protected float cooldown;

            public Item(short itemId, byte slot, byte quantity, byte charges) {
                this.itemId = itemId;
                this.slot = slot;
                this.quantity = quantity;
                this.charges = charges;
            }

            public void setCooldown(float cooldown) {
                this.cooldown = cooldown;
            }

            public short getItemId() {
                return itemId;
            }

            public byte getSlot() {
                return slot;
            }

            public byte getQuantity() {
                return quantity;
            }

            public byte getCharges() {
                return charges;
            }

            public float getCooldown() {
                return cooldown;
            }
        }

        public class Ability {

            protected int   level;
            protected float unknown;

            public Ability(int level, float unknown) {
                this.level = level;
                this.unknown = unknown;
            }

            public int getLevel() {
                return level;
            }

            public float getUnknown() {
                return unknown;
            }

        }

        protected int playerId;
        protected int entityId;
        protected String summoner;
        protected String champion;
        protected Map<Integer, Integer> runes = new HashMap<Integer, Integer>();
        protected Map<Integer, Integer> talents = new HashMap<Integer, Integer>();
        protected Item[] items = new Item[10];
        protected Ability[] abililities = new Ability[4];

        public void setEntityId(int entityId) {
            this.entityId = entityId;
        }

        public void setName(String summoner) {
            this.summoner = summoner;
        }

        public void setChampion(String champion) {
            this.champion = champion;
        }

        public void addRune(int id) {
            if(runes.containsKey(id))
                runes.put(id, runes.get(id) + 1);
            else
                runes.put(id, 1);
        }

        public void setTalent(int id, int points) {
            talents.put(id, points);
        }

        public void setItem(int index, Item item) {
            this.items[index] = item;
        }

        public void setAbility(int index, Ability ability) {
            this.abililities[index] = ability;
        }

        public int getEntityId() {
            return entityId;
        }

        public String getSummoner() {
            return summoner;
        }

        public String getChampion() {
            return champion;
        }

        public Map<Integer, Integer> getRunes() {
            return runes;
        }

        public Map<Integer, Integer> getTalents() {
            return talents;
        }

        public Item[] getItems() {
            return items;
        }

        public Ability[] getAbililities() {
            return abililities;
        }

        public int getPlayerId() {
            return playerId;
        }

        public void setPlayerId(int playerId) {
            this.playerId = playerId;
        }
    }

    protected float timeStamp;
    protected List<Player> players = new ArrayList<Player>();

    public void setTimeStamp(float timeStamp) {
        this.timeStamp = timeStamp;
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public float getTimeStamp() {
        return timeStamp;
    }

    public List<Player> getPlayers() {
        return players;
    }
}
