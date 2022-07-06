package me.sharpdev.pluginutils;

import com.google.common.base.Objects;
import me.sharpdev.pluginutils.itemmanager.ItemData;
import me.sharpdev.pluginutils.playerdata.PlayerData;

import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.UUID;

public final class TestDataTypes {
    public static class TestItemData extends ItemData {
        public String name = "ItemName";
        public String description = "ItemDescription";

        public TestItemData() { }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TestItemData that = (TestItemData) o;
            return Objects.equal(name, that.name) && Objects.equal(description, that.description);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(name, description);
        }

        public static TestItemData randomData() {
            TestItemData data = new TestItemData();
            data.name = generateRandomString(8);
            data.description = generateRandomString(24);
            return data;
        }
    }

    public static class TestPlayerData extends PlayerData {
        public String name = "PlayerName";
        public String testField = "PlayerTestField";
        public UUID uuid = UUID.randomUUID();

        public TestPlayerData() { }
        public static TestPlayerData randomData() {
            TestPlayerData playerData = new TestPlayerData();
            playerData.name = generateRandomAlphanumericString(16);
            playerData.testField = generateRandomString(18);
            return playerData;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TestPlayerData that = (TestPlayerData) o;
            return Objects.equal(name, that.name) && Objects.equal(testField, that.testField);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(name, testField);
        }
    }

    public static String generateRandomString(int length) {
        byte[] array = new byte[length]; // length is bounded by 7
        new Random().nextBytes(array);
        return new String(array, StandardCharsets.UTF_8);
    }

    public static String generateRandomAlphanumericString(int length) {
        int lLimit = 48;
        int rLimit = 122;

        Random random = new Random();

        return random.ints(lLimit, rLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }
}
