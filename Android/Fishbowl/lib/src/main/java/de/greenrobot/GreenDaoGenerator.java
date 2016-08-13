package de.greenrobot;

import java.io.File;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class GreenDaoGenerator {
    public static void main(String[] args) throws Exception {
        final int version = 1;
        Schema schema = new Schema(version, "com.github.bzsy.fishbowl.sqlite.greendao");

        //tables
        addAirTemperatureEntity(schema);
        addHumidityEntity(schema);
        addLightEntity(schema);
        addImageEntity(schema);

        String curPath = GreenDaoGenerator.class.getResource("/").toString();
        String genPath = curPath.replace("lib/build/classes/main/",
                "app/src/main/java").replace("file:", "");
        File f = new File(genPath);
        if (!f.exists()) {
            f.mkdirs();
        }
        new DaoGenerator().generateAll(schema, genPath);
    }

    private static void addAirTemperatureEntity(Schema schema) {
        Entity AirTemperatureEntity = schema.addEntity("AirTemperatureEntity");
        AirTemperatureEntity.addIdProperty();
        AirTemperatureEntity.addLongProperty("time").notNull();
        AirTemperatureEntity.addFloatProperty("temperature");
        AirTemperatureEntity.addFloatProperty("heat_index");
        AirTemperatureEntity.addIntProperty("device_id");
    }

    private static void addHumidityEntity(Schema schema) {
        Entity HumidityEntity = schema.addEntity("HumidityEntity");
        HumidityEntity.addIdProperty();
        HumidityEntity.addLongProperty("time").notNull();
        HumidityEntity.addFloatProperty("humidity");
        HumidityEntity.addIntProperty("device_id");
    }

    private static void addLightEntity(Schema schema) {
        Entity LightEntity = schema.addEntity("LightEntity");
        LightEntity.addIdProperty();
        LightEntity.addLongProperty("time").notNull();
        LightEntity.addIntProperty("light");
        LightEntity.addIntProperty("device_id");
    }

    private static void addImageEntity(Schema schema) {
        Entity ImageEntity = schema.addEntity("ImageEntity");
        ImageEntity.addIdProperty();
        ImageEntity.addLongProperty("time").notNull();
        ImageEntity.addStringProperty("image_url");
        ImageEntity.addStringProperty("thumbnail_url");
        ImageEntity.addIntProperty("device_id");
    }
}
