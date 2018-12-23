/*
 * Copyright (C) 2015 Bilibili
 * Copyright (C) 2015 Zhang Rui <bbcallen@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tv.danmaku.ijk.media.example.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import tv.danmaku.ijk.media.example.R;
import tv.danmaku.ijk.media.example.content.PathCursor;
import tv.danmaku.ijk.media.example.content.PathCursorLoader;
import tv.danmaku.ijk.media.example.eventbus.FileExplorerEvents;
import tv.danmaku.ijk.media.example.tem.FileUtil;
import tv.danmaku.ijk.media.example.tem.Video;
import tv.danmaku.ijk.media.example.widget.adapter.BaseAdapter;
import tv.danmaku.ijk.media.example.widget.adapter.BaseHolder;

/**
 * 文件列表fragment
 */
public class FileListFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String ARG_PATH = "path";

    private static final String TAG = "FileListFragment";

    private TextView mPathView;
    private RecyclerView mFileListView;
    private VideoAdapter mAdapter;
    private String mPath;

    private List<Video> videos = new ArrayList<>();//视频源

    private FileUtil fileUtil;

    private EditText editView;

    /**
     * 创建时候，放入根目录
     * @param path
     * @return
     */
    public static FileListFragment newInstance(String path) {
        FileListFragment f = new FileListFragment();

        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putString(ARG_PATH, path);
        f.setArguments(args);

        return f;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_file_list, container, false);
        mPathView = (TextView) viewGroup.findViewById(R.id.path_view);
        mFileListView = (RecyclerView) viewGroup.findViewById(R.id.file_list_view);
        editView = viewGroup.findViewById(R.id.index_edit);
        mPathView.setVisibility(View.VISIBLE);

        //加载数据
        viewGroup.findViewById(R.id.test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Video> rs = fileUtil.getVideos();
                videos.clear();
                videos.addAll(rs);
                mAdapter.notifyDataSetChanged();
                Log.v(TAG,"测试按钮点击");
                Toast.makeText(getContext(),"获取数据:"+rs.size(),Toast.LENGTH_SHORT).show();
            }
        });

        //点击确定播放文件
        viewGroup.findViewById(R.id.btn_play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String path = editView.getText().toString();
                if (TextUtils.isEmpty(path)){
                    Toast.makeText(getContext(),"请输入文件路径",Toast.LENGTH_SHORT).show();
                    return;
                }
                FileExplorerEvents.getBus().post(new FileExplorerEvents.OnClickFile(path));
            }
        });

        return viewGroup;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Activity activity = getActivity();

        Bundle bundle = getArguments();
        if (bundle != null) {
            mPath = bundle.getString(ARG_PATH);
            mPath = new File(mPath).getAbsolutePath();
            mPathView.setText(mPath);
        }

        fileUtil = new FileUtil(activity);
        mAdapter = new VideoAdapter(videos);
        mFileListView.setAdapter(mAdapter);
        mFileListView.setLayoutManager(new LinearLayoutManager(getContext()));
        getLoaderManager().initLoader(1, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (TextUtils.isEmpty(mPath))
            return null;
        return new PathCursorLoader(getActivity(), mPath);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    class VideoAdapter extends BaseAdapter<Video> implements View.OnClickListener {

        public VideoAdapter(List<Video> data) {
            super(android.R.layout.simple_list_item_2,data);
        }

        @Override
        protected void convert(BaseHolder helper, Video item) {
            TextView text1 = helper.getView(android.R.id.text1);
            TextView text2 = helper.getView(android.R.id.text2);
            text1.setText(item.getName());
            text2.setText(item.getPath());
            helper.itemView.setOnClickListener(this);
            helper.itemView.setTag(R.id.tag_index,helper.getAdapterPosition());
        }

        @Override
        public void onClick(View v) {
            int index = (int) v.getTag(R.id.tag_index);
            Video video = videos.get(index);
            editView.setText(video.getPath());
        }
    }

    /**
     * 跳转到选择文件
     */
    private void selectFile(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("video/*");//设置类型，我这里是任意类型，任意后缀的可以这样写。
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent,1);
    }

}
