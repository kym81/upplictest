package com.upplic.androidapp.upplictesttask.utils;

import android.util.JsonReader;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class JsonUtils {
    private static final String LOG_TAG = "QM_" + JsonUtils.class.getSimpleName();

    public static HashMap<Integer, Object> getJsonArray(String jsonString, HashMap<String, Integer> fieldsMap, String className, boolean strictFieldsValidation) {
        HashMap<Integer, Object> arrayResult = new HashMap();

        InputStream stream = new ByteArrayInputStream(jsonString.getBytes());

        JsonReader jsonReader;
        try {
            jsonReader = new JsonReader(new InputStreamReader(stream, "UTF-8"));
            ArrayList<Object> list = new ArrayList<>();
            jsonReader.setLenient(true);
            jsonReader.beginArray();
            while (jsonReader.hasNext()) {
                HashMap<Integer, Object> itemResult = getJsonItemForArray(jsonReader, fieldsMap, className, strictFieldsValidation);

                if (null != itemResult.get(AppConstants.MAP_KEY_DATA)) {
                    Object item = itemResult.get(AppConstants.MAP_KEY_DATA);
                    list.add(item);
                } else if (null != itemResult.get(AppConstants.MAP_KEY_FAILURE)) {
                    arrayResult.put(AppConstants.MAP_KEY_FAILURE, itemResult.get(AppConstants.MAP_KEY_FAILURE));
                    arrayResult.put(AppConstants.MAP_KEY_FAILURE_ADD, itemResult.get(AppConstants.MAP_KEY_FAILURE_ADD));
                    return arrayResult;
                }
            }
            jsonReader.endArray();
            arrayResult.put(AppConstants.MAP_KEY_DATA, list);
        } catch (Exception e) {
            arrayResult.put(AppConstants.MAP_KEY_FAILURE, e);
            arrayResult.put(AppConstants.MAP_KEY_FAILURE_ADD, "array = " + jsonString);
            e.printStackTrace();
        }

        return arrayResult;
    }

    private static HashMap<Integer, Object> getJsonItemForArray(JsonReader jsonReader, HashMap<String, Integer> fieldsMap, String className, boolean strictFieldsValidation) {
        HashMap<Integer, Object> result = new HashMap();
        HashMap<String, Object> itemFields= new HashMap();

        String value = jsonReader.toString();

        try {
            jsonReader.beginObject();
                while (jsonReader.hasNext()) {
                    String key_name = jsonReader.nextName();
                    if (null != fieldsMap.get(key_name)) {
                        switch (fieldsMap.get(key_name)) {
                            case AppConstants.READER_NEXT_STRING:
                                itemFields.put(key_name, jsonReader.nextString());
                                break;
                            case AppConstants.READER_NEXT_INT:
                                itemFields.put(key_name, jsonReader.nextInt());
                                break;
                            case AppConstants.READER_NEXT_BOOLEAN:
                                itemFields.put(key_name, jsonReader.nextBoolean());
                                break;
                            case AppConstants.READER_NEXT_LONG:
                                itemFields.put(key_name, jsonReader.nextLong());
                                break;
                            case AppConstants.READER_NEXT_DOUBLE:
                                itemFields.put(key_name, jsonReader.nextDouble());
                                break;
                        }
                    } else if (strictFieldsValidation) {
                        result.put(AppConstants.MAP_KEY_FAILURE, new Throwable("Not valid by strictFieldsValidation! FieldName = " + key_name));
                        return result;
                    } else {
                        jsonReader.skipValue();
                    }
            }
            jsonReader.endObject();

            Class itemClass = Class.forName(className);
            Constructor constructor = itemClass.getConstructor(HashMap.class);
            Object item = constructor.newInstance(itemFields);

            result.put(AppConstants.MAP_KEY_DATA, item);
        } catch (Exception e) {
            result.put(AppConstants.MAP_KEY_FAILURE, e);
            result.put(AppConstants.MAP_KEY_FAILURE_ADD, "item = " + value);
            e.printStackTrace();
        }

        return result;
    }

    public static HashMap<Integer, Object> getJsonItem(String jsonString, HashMap<String, Integer> fieldsMap, String className, boolean strictFieldsValidation) {
        HashMap<Integer, Object> result = new HashMap();
        InputStream stream = new ByteArrayInputStream(jsonString.getBytes());
        JsonReader jsonReader;
        try {
            jsonReader = new JsonReader(new InputStreamReader(stream, "UTF-8"));
            jsonReader.setLenient(true);
            HashMap<Integer, Object> itemResult = getJsonItemForArray(jsonReader, fieldsMap, className, strictFieldsValidation);
            if (null != itemResult.get(AppConstants.MAP_KEY_FAILURE)) {
                result.put(AppConstants.MAP_KEY_FAILURE, itemResult.get(AppConstants.MAP_KEY_FAILURE));
                result.put(AppConstants.MAP_KEY_FAILURE_ADD, itemResult.get(AppConstants.MAP_KEY_FAILURE_ADD));
                return result;
            }
            Object item = itemResult.get(AppConstants.MAP_KEY_DATA);
            result.put(AppConstants.MAP_KEY_DATA, item);
        } catch (Exception e) {
            result.put(AppConstants.MAP_KEY_FAILURE, e);
            result.put(AppConstants.MAP_KEY_FAILURE_ADD, "item = " + jsonString);
            e.printStackTrace();
        }

        return result;
    }

}
