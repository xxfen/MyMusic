package com.xxf.mymusic.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;

import com.xxf.mymusic.bean.Music;

import java.util.ArrayList;
import java.util.List;

/**
 * author：xxf
 */
public class ContentResolverHelper {

    private ContentResolver musicResolver;

    public ContentResolverHelper(Context context) {
        musicResolver = context.getContentResolver();
    }

    public List<Music> getMusic() {

        Cursor cursor = musicResolver.query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null,
                null, null);
        //游标归零
        cursor.moveToFirst();
        List<Music> musicList = new ArrayList<>();
        do {
            int length = cursor.getInt(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));
            if (length / 1000 > 30) {
                String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                String name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                String artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                if (artist.equals("<unknown>")) {
                    // Log.e(TAG, "getMusicData: " + title);
                    if ((title.indexOf(" - ") != -1)) {
                        artist = title.substring(0, title.indexOf(" - "));
                        title = title.substring(title.indexOf(" - ") + 3);
                    } else {
                        artist = "未知歌手";
                    }
                }
                Long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media._ID));
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                 Log.e("", "getMusicData: " + id);
                Music m = new Music();
                m.setAlbum(album);
                m.setArtist(artist);
                m.setTitle(title);
                m.setPath(path);
                m.setId(id);
                m.setLength(length);
                musicList.add(m);
            }
        } while (cursor.moveToNext());
        cursor.close();//游标结束
        return musicList;
    }
}
