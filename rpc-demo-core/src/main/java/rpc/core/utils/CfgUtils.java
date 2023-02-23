package rpc.core.utils;

import rpc.core.enums.CfgNameEnum;
import rpc.core.enums.RpcErrMsgEnum;
import rpc.core.exception.RpcException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class CfgUtils {

    private static final String VALID_CFG_SUFFIX = ".properties";
    private static boolean cfgLoaded = false;
    // file name  ->  properties in the file
    private static final Map<String, Object> propertiesMap = new HashMap<>();


    public static void loadConfigProperties() {
        URL url = Thread.currentThread().getContextClassLoader().getResource("");
        if (url == null) {
            throw new RpcException("get resources url is null");
        }

        File resourceFileDictionary = new File(url.getPath());
        File[] files = resourceFileDictionary.listFiles();
        if (files == null || files.length == 0) {
            return;
        }

        for (File file : files) {
            if (!file.isDirectory() && file.getName().endsWith(VALID_CFG_SUFFIX)) {
                readPropertiesFromFile(file);
            }
        }
        cfgLoaded = true;
    }


    public static void readPropertiesFromFile(File file) {
        Properties res;
        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)) {
            res = new Properties();
            res.load(reader);
        } catch (IOException e) {
            throw new RpcException(RpcErrMsgEnum.LOAD_CFG_ERROR, e);
        }

        Set<Map.Entry<Object, Object>> entrySet = res.entrySet();
        for (Map.Entry<Object, Object> entry : entrySet) {
            propertiesMap.put((String) entry.getKey(), entry.getValue());
        }
    }


    private static Object getCfg(CfgNameEnum cfgNameEnum) {
        if (!cfgLoaded) {
            loadConfigProperties();
        }
        Object rawVal = propertiesMap.get(cfgNameEnum.getName());
        if (rawVal == null) {
            throw new RpcException(String.format("config not found. cfg name: %s", cfgNameEnum.getName()));
        }
        return rawVal;
    }


    public static String getCfgAsStr(CfgNameEnum cfgNameEnum) {
        return String.valueOf(getCfg(cfgNameEnum));
    }


    public static int getCfgAsInt(CfgNameEnum cfgNameEnum) {
        return Integer.parseInt(getCfgAsStr(cfgNameEnum));
    }
}
