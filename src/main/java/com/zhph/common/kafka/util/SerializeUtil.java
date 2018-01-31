package com.zhph.common.kafka.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SerializeUtil {
	public static Object convertBytes2Obj(byte[] bytes) throws Exception {
		if (bytes == null) {
			System.out.println("error");
			return null;
		}
		Object obj = null;
		ByteArrayInputStream bi = null;
		ObjectInputStream oi = null;
		try {
			bi = new ByteArrayInputStream(bytes);
			oi = new ObjectInputStream(bi);
			obj = oi.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (bi != null) {
				bi.close();
			}
			if (oi != null) {
				oi.close();
			}
		}
		return obj;
	}
	public static byte[] convertObj2Btyes(Object obj) throws Exception {
		byte[] bytes = null;
		if (obj == null) {
			return bytes;
		}
		ByteArrayOutputStream bo = null;
		ObjectOutputStream oo = null;
		try {
			bo = new ByteArrayOutputStream();
			oo = new ObjectOutputStream(bo);
			oo.writeObject(obj);
			bytes = bo.toByteArray();
		} catch (Exception e) {
			throw e;
		} finally {
			if (bo != null) {
				try {
					bo.close();
				} catch (IOException e) {
				}
			}
			if (oo != null) {
				try {
					oo.close();
				} catch (IOException e) {
				}
			}
		}
		return bytes;
	}

}
