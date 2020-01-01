package com.byteblogs.helloblog.music.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.byteblogs.common.base.domain.Result;
import com.byteblogs.common.cache.ConfigCache;
import com.byteblogs.common.constant.Constants;
import com.byteblogs.helloblog.music.domain.bo.Music;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MusicUtil {
    public static final String PREFIX_URL = "http://music.163.com/api/playlist/detail?id=" + ConfigCache.getConfig(Constants.CLOUD_MUSIC_ID);
    public static final String PLAY_URL = "http://music.163.com/song/media/outer/url?id=";

    public static String getResponse(HttpURLConnection conn){
        StringBuffer sb = new StringBuffer();
        BufferedReader br=null;
        try{
            conn.setDoOutput(true);  //设置属性
            conn.setDoInput(true);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; Maxthon;)");
            conn.setRequestProperty("cookie", "appver=1.5.0.75771");
            conn.setRequestProperty("referer", "http://music.163.com/");
            conn.connect();   //开启连接
            br = new BufferedReader(new InputStreamReader(conn.getInputStream()));  //获取响应
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        }catch (Exception e){e.printStackTrace();}
        finally {
            if (br!=null){ try { br.close(); } catch (IOException e) { e.printStackTrace(); } }
            if (conn!=null){conn.disconnect();}
        }
        return sb.toString();
    }

    public static List<Music> getAllMusic(JSONArray arr) {
        List<Music> list = new ArrayList<>();
        for (int i = 0; i < arr.size(); i++) {
            JSONObject obj = arr.getJSONObject(i);
            Music music = new Music();
            music.setName(obj.getString("name"));
            music.setUrl(PLAY_URL + obj.getString("id") + ".mp3");
            music.setArtist(obj.getJSONArray("artists").getJSONObject(0).getString("name"));
            music.setCover(obj.getJSONObject("album").getString("blurPicUrl"));
            music.setLrc("");
            list.add(music);
        }
        return list;
    }

    public static List<Music> getPlayList(){
        try{
            URL url = new URL(MusicUtil.PREFIX_URL);// 发起http请求获取歌单信息
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            String result = MusicUtil.getResponse(conn);
            JSONArray arr = JSON.parseObject(result).getJSONObject("result").getJSONArray("tracks");
            return MusicUtil.getAllMusic(arr);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
