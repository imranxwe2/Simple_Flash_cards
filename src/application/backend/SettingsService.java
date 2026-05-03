package application.backend;

import java.io.*;
import java.util.Properties;

public class SettingsService {

    private static final String DIR = "config";
    private static final String FILE = DIR + "/settings.properties";

    private static final String API_KEY = "openai_api_key";

    // 🔥 SAVE KEY
    public static void saveApiKey(String key) {

        try {
            File dir = new File(DIR);
            if (!dir.exists()) dir.mkdir();

            Properties props = new Properties();
            props.setProperty(API_KEY, key);

            FileOutputStream out = new FileOutputStream(FILE);
            props.store(out, "App Settings");
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 🔥 LOAD KEY
    public static String getApiKey() {

        try {
            File file = new File(FILE);

            if (!file.exists()) return null;

            Properties props = new Properties();

            FileInputStream in = new FileInputStream(file);
            props.load(in);
            in.close();

            return props.getProperty(API_KEY);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}