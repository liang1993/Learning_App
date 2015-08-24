package com.app.l.learningapp.utils.cacheutils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by liang on 15/8/18.
 * 参考了ASimpleCache,了解了实现一个Android缓存框架的需要实现的功能和结构
 */
public class SimpleCache {
    public static final int TIME_HOUR = 60 * 60;
    public static final int TIME_DAY = TIME_HOUR * 24;
    private static final int MAX_SIZE = 1000 * 1000 * 50;//50mb
    private static final int MAX_COUNT = Integer.MAX_VALUE;//不限制存放数据的数量
    private static Map<String, SimpleCache> mInstanceMap = new HashMap<>();
    private CacheManager cacheManager;

    public static SimpleCache get(Context context) {
        return get(context, "ACache");
    }

    public static SimpleCache get(Context context, String cacheName) {
        File f = new File(context.getCacheDir(), cacheName);
        return get(f, MAX_SIZE, MAX_COUNT);
    }

    public static SimpleCache get(File cacheDir) {
        return get(cacheDir, MAX_SIZE, MAX_COUNT);
    }

    public static SimpleCache get(Context context, long max_size, int max_count) {
        File f = new File(context.getCacheDir(), "ACache");
        return get(f, max_size, max_count);
    }

    //初始化SimpleCache对象
    public static SimpleCache get(File cacheDir, long max_size, int max_count) {
        SimpleCache manager = mInstanceMap.get(cacheDir.getAbsolutePath() + myPid());
        if (manager == null) {
            manager = new SimpleCache(cacheDir, max_size, max_count);
            mInstanceMap.put(cacheDir.getAbsolutePath() + myPid(), manager);
        }
        return manager;
    }

    private static String myPid() {
        return "_" + android.os.Process.myPid();
    }

    /**
     * 私有化构造函数,初始化CacheManager对象
     *
     * @param cacheDir
     * @param max_size
     * @param max_count
     */
    private SimpleCache(File cacheDir, long max_size, int max_count) {
        if (!cacheDir.exists() && !cacheDir.mkdirs()) {
            throw new RuntimeException("can't make dirs in" + cacheDir.getAbsolutePath());
        }
        cacheManager = new CacheManager(cacheDir, max_size, max_count);
    }

    //============String 数据的读写===========

    /**
     * 保存String数据到缓存中
     *
     * @param key   保存的key
     * @param value 保存的String数据
     */
    public void put(String key, String value) {
        File file = cacheManager.newFile(key);
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new FileWriter(file), 1024);
            out.write(value);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            cacheManager.put(file);
        }
    }

    /**
     * 保存String数据到缓存
     *
     * @param key
     * @param value
     * @param saveTime 保存的时间，单位：秒
     */
    public void put(String key, String value, int saveTime) {
        put(key, Utils.newStringWithDateInfo(saveTime, value));
    }

    /**
     * 读取String数据
     *
     * @param key
     * @return
     */
    public String getAsString(String key) {
        File file = cacheManager.get(key);
        if (!file.exists())
            return null;
        boolean removeFile = false;
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(file));
            String readString = "";
            String currentLine;
            while ((currentLine = in.readLine()) != null) {
                readString += currentLine;
            }
            if (!Utils.isDue(readString)) {
                return Utils.clearDateInfo(readString);
            } else {
                removeFile = true;
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (removeFile)
                remove(key);
        }
    }

    //===============JSON数据读写============
    //JSON可以看成特定格式的字符串，读写与字符串类似

    /**
     * 保存JSON数据到缓存
     *
     * @param key
     * @param value
     */
    public void put(String key, JSONObject value) {
        put(key, value.toString());
    }

    /**
     * @param key
     * @param value
     * @param saveTime
     */
    public void put(String key, JSONObject value, int saveTime) {
        put(key, value.toString(), saveTime);
    }

    public JSONObject getAsJSONObject(String key) {
        String JSONString = getAsString(key);
        try {
            JSONObject obj = new JSONObject(JSONString);
            return obj;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
    //===============JSONArray数据读写============
    //同JSONObject

    /**
     * @param key
     * @param value
     */
    public void put(String key, JSONArray value) {
        put(key, value.toString());
    }

    /**
     * @param key
     * @param value
     * @param saveTime
     */
    public void put(String key, JSONArray value, int saveTime) {
        put(key, value.toString(), saveTime);
    }

    /**
     * @param key
     * @return
     */
    public JSONArray getAsJSONArray(String key) {
        String JSONString = getAsString(key);
        try {
            JSONArray obj = new JSONArray(JSONString);
            return obj;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
    //===============byte数据读写============

    /**
     * byte数据保存到缓存
     *
     * @param key
     * @param value
     */
    public void put(String key, byte[] value) {
        File file = cacheManager.newFile(key);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
            out.write(value);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.flush();
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            cacheManager.put(file);
        }
    }

    /**
     * byte数据保存到缓存
     *
     * @param key
     * @param value
     * @param savaTime
     */
    public void put(String key, byte[] value, int savaTime) {
        put(key, Utils.newByteArrayWithDateInfo(savaTime, value));
    }

    /**
     * 获取byte数据
     *
     * @param key
     * @return 返回byte数据，过期了返回null
     */
    public byte[] getAsBinary(String key) {
        RandomAccessFile RAFile = null;
        boolean removeFile = false;
        try {
            File file = cacheManager.get(key);
            if (!file.exists())
                return null;
            RAFile = new RandomAccessFile(file, "r");
            byte[] byteArray = new byte[(int) RAFile.length()];
            RAFile.read(byteArray);
            if (!Utils.isDue(byteArray)) {
                return Utils.clearDateInfo(byteArray);
            } else {
                removeFile = true;
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (RAFile != null) {
                try {
                    RAFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (removeFile) {
                remove(key);
            }
        }

    }

    //===============序列化数据读写============

    /**
     * 保存序列化对象到缓存
     *
     * @param key
     * @param value
     */
    public void put(String key, Serializable value) {
        put(key, value, -1);
    }

    /**
     * 保存序列化对象到缓存
     *
     * @param key
     * @param value
     * @param saveTime
     */
    public void put(String key, Serializable value, int saveTime) {
        ByteArrayOutputStream baos = null;
        ObjectOutputStream oos = null;
        try {
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            oos.writeObject(value);
            byte[] data = baos.toByteArray();
            if (saveTime == -1) {
                put(key, data, saveTime);
            } else {
                put(key, data);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                oos.close();
            } catch (IOException e) {
            }
        }
    }

    /**
     * 获取序列化数据
     *
     * @param key
     * @return
     */
    public Object getAsObject(String key) {
        byte[] data = getAsBinary(key);
        if (data != null) {
            ByteArrayInputStream bais = null;
            ObjectInputStream ois = null;
            try {
                bais = new ByteArrayInputStream(data);
                ois = new ObjectInputStream(bais);
                Object reObject = ois.readObject();
                return reObject;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            } finally {
                try {
                    if (bais != null)
                        bais.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    if (ois != null)
                        ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    //===============bitmap数据读写============
    //将bitmap转成byte数组操作即可

    /**
     * @param key
     * @param value
     */
    public void put(String key, Bitmap value) {
        put(key, Utils.Bitmap2Bytes(value));
    }

    /**
     * @param key
     * @param value
     * @param saveTime
     */
    public void put(String key, Bitmap value, int saveTime) {
        put(key, Utils.Bitmap2Bytes(value), saveTime);
    }

    /**
     * @param key
     * @return
     */
    public Bitmap getAsBitmap(String key) {
        if (getAsBinary(key) == null) {
            return null;
        }
        return Utils.Bytes2Bitmap(getAsBinary(key));
    }
    //===============bitmap数据读写============
    //将drawable转化成bitmap处理即可

    /**
     * @param key
     * @param value
     */
    public void put(String key, Drawable value) {
        put(key, Utils.drawable2Bitmap(value));
    }

    /**
     * @param key
     * @param value
     * @param saveTime
     */
    public void put(String key, Drawable value, int saveTime) {
        put(key, Utils.drawable2Bitmap(value), saveTime);
    }

    /**
     * @param key
     * @return
     */
    public Drawable getAsDrawable(String key) {
        if (getAsBinary(key) == null) {
            return null;
        }
        return Utils.bitmap2Drawable(Utils.Bytes2Bitmap(getAsBinary(key)));
    }
    //=============================================

    /**
     * 获取缓存文件
     * @param key
     * @return
     */
    public File file(String key){
        File f = cacheManager.newFile(key);
        if(f.exists()){
            return f;
        }
        return null;
    }


    /**
     * 删除key
     *
     * @param key
     * @return 是否删除成功
     */
    public boolean remove(String key) {
        return cacheManager.remove(key);
    }

    /**
     * 删除所有数据
     */
    public void clear() {
        cacheManager.clear();
    }

    /**
     * 缓存管理器
     */
    public class CacheManager {
        private final AtomicLong cacheSize;
        private final AtomicInteger cacheCount;
        private final long sizeLimit;
        private final int countLimit;
        private final Map<File, Long> lastUsageDates = Collections.synchronizedMap(new HashMap<File, Long>());
        protected File cacheDir;

        private CacheManager(File cacheDir, long sizeLimit, int countLimit) {
            this.cacheDir = cacheDir;
            this.sizeLimit = sizeLimit;
            this.countLimit = countLimit;
            cacheSize = new AtomicLong();
            cacheCount = new AtomicInteger();
            calculateCacheSizeAndCacheCount();
        }

        /**
         * 根据key获取file
         *
         * @param key
         * @return
         */
        private File newFile(String key) {
            return new File(cacheDir, key.hashCode() + "");
        }

        /**
         * 获取换取文件，同时更新map中该文件的上次使用时间
         *
         * @param key
         * @return
         */
        private File get(String key) {
            File file = newFile(key);
            Long currentTime = System.currentTimeMillis();
            file.setLastModified(currentTime);
            lastUsageDates.put(file, currentTime);

            return file;
        }

        private boolean remove(String key) {
            File image = get(key);
            return image.delete();
        }

        /**
         * 清除缓存
         */
        private void clear() {
            lastUsageDates.clear();
            cacheSize.set(0);
            File[] files = cacheDir.listFiles();
            if (files != null) {
                for (File f : files) {
                    f.delete();
                }
            }
        }

        /**
         * 计算cacheSize和cacheCount
         */
        private void calculateCacheSizeAndCacheCount() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    int size = 0;
                    int count = 0;
                    File[] cachedFiles = cacheDir.listFiles();
                    if (cachedFiles != null) {
                        for (File cachedFile : cachedFiles) {
                            size += cachedFile.length();
                            count += 1;
                            lastUsageDates.put(cachedFile,
                                    cachedFile.lastModified());
                        }
                        cacheSize.set(size);
                        cacheCount.set(count);
                    }
                }
            }).start();
        }


        /**
         * 添加缓存文件
         *
         * @param file
         */

        private void put(File file) {
            int curCacheCount = cacheCount.get();
            //当缓存文件数目大于限制时，需要删除一个缓存文件
            while (curCacheCount + 1 > countLimit) {
                long freedSize = removeNext();
                cacheSize.addAndGet(-freedSize);
                curCacheCount = cacheCount.addAndGet(-1);
            }
            cacheCount.addAndGet(1);

            long valuSize = file.length();
            long curCacheSize = cacheSize.get();
            //当缓存文件的大小大于限制，需要删除一定的缓存文件，可能不止一个
            while (curCacheSize + valuSize > sizeLimit) {
                long freedSize = removeNext();
                curCacheSize = cacheSize.addAndGet(-freedSize);
            }
            cacheSize.addAndGet(valuSize);

            Long currentTime = System.currentTimeMillis();
            file.setLastModified(currentTime);
            lastUsageDates.put(file, currentTime);
        }

        /**
         * 移除旧文件
         *
         * @return 返回删除的缓存文件的大小
         */
        private long removeNext() {
            if (lastUsageDates.isEmpty()) {
                return 0;
            }

            Long oldestUsage = null;
            File mostLongUsedFile = null;
            Set<Map.Entry<File, Long>> entries = lastUsageDates.entrySet();
            //遍历map获取到距上一次使用时间最长的缓存文件
            synchronized (lastUsageDates) {
                for (Map.Entry<File, Long> entry : entries) {
                    if (mostLongUsedFile == null) {
                        mostLongUsedFile = entry.getKey();
                        oldestUsage = entry.getValue();
                    } else {
                        Long lastValueUsage = entry.getValue();
                        if (lastValueUsage < oldestUsage) {
                            oldestUsage = lastValueUsage;
                            mostLongUsedFile = entry.getKey();
                        }
                    }
                }
            }

            long fileSize = mostLongUsedFile.length();
            if (mostLongUsedFile.delete()) {
                lastUsageDates.remove(mostLongUsedFile);
            }
            return fileSize;
        }

    }

    /**
     * 时间计算等工具类
     */
    private static class Utils {
        private static final char mSeparator = ' ';

        /**
         * 判断缓存的String数据是否到期
         *
         * @param str
         * @return true为到期，false为未到期
         */
        private static boolean isDue(String str) {
            return isDue(str.getBytes());
        }

        /**
         * 判断缓存的byte数组是否过期
         *
         * @param data
         * @return true为到期，false为未到期
         */
        private static boolean isDue(byte[] data) {
            String[] strs = getDateInfoFromData(data);
            if (strs != null && strs.length == 2) {
                String saveTimeStr = strs[0];
                //把正确的时间信息截取出来
                while (saveTimeStr.startsWith("0")) {
                    saveTimeStr = saveTimeStr.substring(1, saveTimeStr.length());
                }
                long saveTime = Long.valueOf(saveTimeStr);
                long deleteAfter = Long.valueOf(strs[1]);
                if (System.currentTimeMillis() > saveTime + deleteAfter * 1000) {
                    return true;
                }
            }
            return false;
        }

        /**
         * 新建日期信息返回字符串
         *
         * @param second
         * @param strInfo
         * @return
         */
        private static String newStringWithDateInfo(int second, String strInfo) {
            return createDateInfo(second) + strInfo;
        }

        /**
         * 新建日期信息返回字节数组
         *
         * @param second
         * @param data2
         * @return
         */
        private static byte[] newByteArrayWithDateInfo(int second, byte[] data2) {
            byte[] data1 = createDateInfo(second).getBytes();
            byte[] retdata = new byte[data1.length + data2.length];
            System.arraycopy(data1, 0, retdata, 0, data1.length);
            System.arraycopy(data2, 0, retdata, data1.length, data2.length);
            return retdata;
        }

        /**
         * 去除字符串中的时间信息
         *
         * @param strInfo
         * @return
         */
        private static String clearDateInfo(String strInfo) {
            if (strInfo != null && hasDateInfo(strInfo.getBytes())) {
                strInfo = strInfo.substring(strInfo.indexOf(mSeparator) + 1,
                        strInfo.length());
            }
            return strInfo;
        }

        /**
         * 去除字节数组中的时间信息
         *
         * @param data
         * @return
         */
        private static byte[] clearDateInfo(byte[] data) {
            if (hasDateInfo(data)) {
                return copyOfRange(data, indexOf(data, mSeparator) + 1,
                        data.length);
            }
            return data;
        }

        /**
         * 根据时间来创建日期信息,字符串为13为，不够的用0在左边填充
         *
         * @param second
         * @return
         */
        private static String createDateInfo(int second) {
            String currentTime = System.currentTimeMillis() + "";
            while (currentTime.length() < 13) {
                currentTime = "0" + currentTime;
            }
            return currentTime + "-" + second + mSeparator;
        }

        /**
         * 判断字节数组中是否含有时间信息
         *
         * @param data
         * @return
         */
        private static boolean hasDateInfo(byte[] data) {
            return data != null && data.length > 15 && data[13] == '_'
                    && indexOf(data, mSeparator) > 14;
        }

        /**
         * 判断字符在字节数组中的位置，不存在则返回-1
         *
         * @param data
         * @param c
         * @return
         */
        private static int indexOf(byte[] data, char c) {
            for (int i = 0; i < data.length; i++) {
                if (data[i] == c) {
                    return i;
                }
            }
            return -1;
        }

        /**
         * 从data字节数组中获取时间信息
         *
         * @param data
         * @return String[1]为保存的时间， String[2]为有效时间
         */
        private static String[] getDateInfoFromData(byte[] data) {
            if (hasDateInfo(data)) {
                String saveDate = new String(copyOfRange(data, 0, 13));
                String deleteAfter = new String(copyOfRange(data, 14, indexOf(data, mSeparator)));
                return new String[]{saveDate, deleteAfter};
            }
            return null;
        }

        /**
         * 拷贝字节数组的一部分
         *
         * @param original
         * @param from
         * @param to
         * @return
         */
        private static byte[] copyOfRange(byte[] original, int from, int to) {
            int newLength = to - from;
            if (newLength < 0)
                throw new IllegalArgumentException(from + ">" + to);
            byte[] copy = new byte[newLength];
            System.arraycopy(original, from, copy, 0, Math.min(original.length - from, newLength));
            return copy;
        }

        /**
         * bitmap -> byte[]
         *
         * @param bm
         * @return
         */
        private static byte[] Bitmap2Bytes(Bitmap bm) {
            if (bm == null) {
                return null;
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
            return baos.toByteArray();
        }

        /**
         * byte -> bitmap
         *
         * @param b
         * @return
         */
        private static Bitmap Bytes2Bitmap(byte[] b) {
            if (b.length == 0) {
                return null;
            }
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        }

        /**
         * drawable -> bitmap
         *
         * @param drawable
         * @return
         */
        private static Bitmap drawable2Bitmap(Drawable drawable) {
            if (drawable == null) {
                return null;
            }
            //取drawable的长度
            int w = drawable.getIntrinsicWidth();
            int h = drawable.getIntrinsicHeight();
            //取drawable的颜色格式
            Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ?
                    Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
            //简历对应的bitmap
            Bitmap bitmap = Bitmap.createBitmap(w, h, config);
            //简历对应的画布
            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, w, h);
            //把drawable内容画到画布中;
            drawable.draw(canvas);
            return bitmap;
        }

        /**
         * bitmap -> drawable
         *
         * @param bm
         * @return
         */
        private static Drawable bitmap2Drawable(Bitmap bm) {
            if (bm == null) {
                return null;
            }
            return new BitmapDrawable(bm);
        }

    }


}


