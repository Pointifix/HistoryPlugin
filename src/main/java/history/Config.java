package history;

import java.io.FileReader;
import java.io.IOException;

import mindustry.Vars;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Config {
    JSONObject config;

    public Config() {
        JSONParser jsonParser = new JSONParser();

        try (FileReader reader = new FileReader(Vars.modDirectory.child("history-config.json").path()))
        {
            config = (JSONObject) jsonParser.parse(reader);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    public Object get(Object key) {
        return config.get(key);
    }

    public boolean getBoolean(Object key) {
        return (boolean)config.get(key);
    }

    public int getInt(Object key) {
        return ((Long)config.get(key)).intValue();
    }

    public String getString(Object key) {
        return (String)config.get(key);
    }
}
