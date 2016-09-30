package com.therishka.androidlab_2;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.therishka.androidlab_2.models.VkAttachments;
import com.therishka.androidlab_2.models.VkLikes;
import com.therishka.androidlab_2.models.VkNewsItem;
import com.therishka.androidlab_2.models.VkPhoto;
import com.therishka.androidlab_2.network.RxVk;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class NewsActivity extends AppCompatActivity {

    ProgressBar mProgress;
    RecyclerView mRecyclerList;
    RecyclerNewsAdapter mNewsAdapter;
    LinearLayout.LayoutParams llp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        mProgress = (ProgressBar) findViewById(R.id.loading_view);
        mRecyclerList = (RecyclerView) findViewById(R.id.news_list);
        mNewsAdapter = new RecyclerNewsAdapter(this);
        mRecyclerList.setAdapter(mNewsAdapter);
        mRecyclerList.setLayoutManager(new LinearLayoutManager(this));
        llp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        getNewsAndShowThem();
    }

    private void getNewsAndShowThem() {
        showLoading();
        RxVk api = new RxVk();
        api.getNews(new RxVk.RxVkListener<LinkedList<VkNewsItem>>() {
            @Override
            public void requestFinished(LinkedList<VkNewsItem> requestResult) {
                mNewsAdapter.setNewsList(requestResult);
                showNews();
            }
        });
    }

    private void showLoading() {
        mRecyclerList.setVisibility(View.GONE);
        mProgress.setVisibility(View.VISIBLE);
    }

    private void showNews() {
        mRecyclerList.setVisibility(View.VISIBLE);
        mProgress.setVisibility(View.GONE);
    }

    @SuppressWarnings("WeakerAccess")
    private class RecyclerNewsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private List<VkNewsItem> mNewsList;
        private Context mContext;

        public RecyclerNewsAdapter(@NonNull Context context) {
            mContext = context;
        }

        public void setNewsList(@Nullable List<VkNewsItem> NewsList) {
            mNewsList = NewsList;
            notifyDataSetChanged();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_item, parent, false);
            return new NewsViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof NewsViewHolder) {
                VkNewsItem news = mNewsList.get(position);
                ((NewsViewHolder) holder).bind(news, mContext);
                Glide.with(mContext).load(news.getPublisher().getPhoto_200())
                        .fitCenter()
                        .into(((NewsViewHolder) holder).icon);
            }
        }

        @Override
        public int getItemCount() {
            return mNewsList != null ? mNewsList.size() : 0;
        }
    }

    @SuppressWarnings("WeakerAccess")
    private class NewsViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView title;
        TextView date;
        TextView likesCount;
        TextView text;
        ImageView likes;
        LinearLayout ll;
        List<ImageView> imageViewList;
        String fullText;

        public NewsViewHolder(View itemView) {
            super(itemView);
            ll = (LinearLayout) findViewById(R.id.news_news_ll);
            icon = (ImageView)  itemView.findViewById(R.id.news_image);
            title = (TextView)  itemView.findViewById(R.id.news_group_name);
            date = (TextView)   itemView.findViewById(R.id.news_date);
            likesCount = (TextView) itemView.findViewById(R.id.news_likes_n);
            text = (TextView)       itemView.findViewById(R.id.news_news_text);
            likes = (ImageView)     itemView.findViewById(R.id.news_likes);
            imageViewList = new LinkedList<>();
        }

        public void bind(VkNewsItem news, Context mContext) {
            title.setText(news.getPublisher().getName());
            DateFormat df = new SimpleDateFormat("HH:mm dd.MM");
            Date dateF = new Date(news.getDate()*1000);
            date.setText(df.format(dateF));
            VkLikes vkLikes = news.getLikes();

            if(vkLikes != null) {
                likesCount.setText(Integer.toString(vkLikes.getCount()));
                if (vkLikes.isUser_likes()) {
                    likes.setImageResource(R.drawable.ic_favorite_blue_24dp);
                } else {
                    likes.setImageResource(R.drawable.ic_favorite_black_24dp);
                }
            } else {
                likesCount.setText("0");
            }
            String information = news.getText();
            fullText = information;
            if(information != null) {
                if (information.length() > 301) {
                    information = information.substring(0, 300) + "...";
                }
            } else {
                information = "";
            }
            text.setText(information);

            List<VkAttachments> attachments = news.getAttachments();

            clearImages();

            if(attachments != null){
                for(VkAttachments a : attachments){
                    String photo = null;
                    if(a != null){
                        photo = getMaxPhoto(a.getPhoto());
                    }
                    if(photo != null){
                        try {
                            ImageView imageView = new ImageView(mContext);
                            imageView.setLayoutParams(llp);
                            Glide.with(itemView.getContext()).load(photo)
                                    .fitCenter()
                                    .into(imageView);
                            ll.addView(imageView);
                            imageViewList.add(imageView);
                        }catch (Exception e){}
                    }
                }
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    text.setText(fullText);
                }
            });

        }

        public void clearImages(){
            if(imageViewList != null && ll != null){
                for(ImageView k : imageViewList){
                    ll.removeView(k);
                }
                imageViewList.clear();
            }
        }

        public String getMaxPhoto(VkPhoto photo){
            String res = null;
            if(photo != null){
                res = photo.getPhoto_2560();
                if(res == null){
                    res = photo.getPhoto_1280();
                    if(res == null){
                        res = photo.getPhoto_807();
                        if(res == null){
                            res = photo.getPhoto_604();
                            if(res == null){
                                res = photo.getPhoto_130();
                                if(res == null){
                                    res = photo.getPhoto_75();
                                }
                            }
                        }
                    }
                }
            }
            return res;
        }
    }
}
