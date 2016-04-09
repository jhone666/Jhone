package com.jhone.demo.utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Gson解析工具类
 *
 * @author jhone
 *
 */
public class GsonUtil {

	private static final String TAG = "GsonUtil";

	private static final String LIST_DEFAULT_TAG = "return_info";
	public static final String OBJ_TAG = "return_info";

	/**
	 * 实体类转化为json
	 *
	 * @param bean
	 * @return
	 */
	public static String bean2json(Object bean) {
		return new Gson().toJson(bean);
	}

	/**
	 *
	 * @param <T>
	 * @param json
	 * @param type
	 *            转化的目标实体类
	 * @return
	 */
	public static <T> T json2bean(String json, Type type)throws JsonSyntaxException {
		return new Gson().fromJson(json, type);
	}

	/**
	 *
	 * @param <T>
	 * @param json
	 * @param type
	 *            转化的目标实体类
	 * @return
	 */
	public static <T> T json2Obj(String json, Class<T> type) throws JsonSyntaxException {

		JsonElement element = new JsonParser().parse(json);
		if (element.isJsonNull()) {
			try {
				return type.newInstance();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		JsonObject jsonObject = element.getAsJsonObject();
		if (jsonObject.isJsonNull() || jsonObject.get(OBJ_TAG).isJsonNull()) {
			try {
				return type.newInstance();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		jsonObject = jsonObject.getAsJsonObject(OBJ_TAG);

		return new Gson().fromJson(jsonObject.toString(), type);
	}

	/**
	 *
	 * @param <T>
	 * @param json
	 * @param arrayElement 数组元素名称
	 * @param type 转换的目标对象类
	 * @return
	 */
	public static <T> List<T> jsonArray2List(String json, String arrayElement, Type type) {

		List<T> t = new ArrayList<T>();
		JsonElement element = new JsonParser().parse(json);
		if (element.isJsonNull() || element.getAsJsonObject().isJsonNull()) {
			return t;
		}

		try {
			JsonArray jsonarray = element.getAsJsonObject().getAsJsonArray(arrayElement);

			if (jsonarray == null) {
				return t;
			}

			for (int i = 0; i < jsonarray.size(); i++) {
				JsonObject obj = jsonarray.get(i).getAsJsonObject();

				t.add((T) json2bean(obj.toString(), type));
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return t;
	}

	/**
	 * 使用默认列表名称
	 * @param <T>
	 * @param json
	 * @param type 转换的目标对象类
	 * @return
	 */
	public static <T> List<T> jsonArray2List(String json, Type type) {

		List<T> t = new ArrayList<T>();

		JsonElement element = new JsonParser().parse(json);
		if (element.isJsonNull() || element.getAsJsonObject().isJsonNull()) {
			return t;
		}

		JsonArray jsonarray = element.getAsJsonObject().getAsJsonArray(LIST_DEFAULT_TAG);
		if (jsonarray == null) {
			return Collections.emptyList();
		}
		for (int i = 0; i < jsonarray.size(); i++) {
			JsonObject obj = jsonarray.get(i).getAsJsonObject();

			t.add((T) json2bean(obj.toString(), type));
		}
		return t;
	}
}
