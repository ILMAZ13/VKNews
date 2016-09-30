package com.therishka.androidlab_2;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.therishka.androidlab_2.models.VkFriend;
import com.therishka.androidlab_2.network.RxVk;

import java.util.List;

/**
 * @author Rishad Mustafaev
 */

public class FriendsActivity extends AppCompatActivity {

    ProgressBar mProgress;
    ListView mListView;
    FriendsAdapter mFriendsAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        mProgress = (ProgressBar) findViewById(R.id.loading_view);
        mListView = (ListView) findViewById(R.id.friends_list);
        mFriendsAdapter = new FriendsAdapter(this);
        mListView.setAdapter(mFriendsAdapter);

        getFriendsAndShowThem();
    }

    private void getFriendsAndShowThem() {
        showLoading();
        RxVk api = new RxVk();
        api.getFriends(new RxVk.RxVkListener<List<VkFriend>>() {
            @Override
            public void requestFinished(List<VkFriend> requestResult) {
                mFriendsAdapter.setItems(requestResult);
                showFriends();
            }
        });
    }

    private void showLoading() {
        mListView.setVisibility(View.GONE);
        mProgress.setVisibility(View.VISIBLE);
    }

    private void showFriends() {
        mListView.setVisibility(View.VISIBLE);
        mProgress.setVisibility(View.GONE);
    }

    @SuppressWarnings("WeakerAccess")
    private class FriendsAdapter extends BaseAdapter {

        private List<VkFriend> friendsList;
        private Context mContext;

        public FriendsAdapter(@NonNull Context context) {
            mContext = context;
        }

        public void setItems(@Nullable List<VkFriend> friendsList) {
            this.friendsList = friendsList;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return friendsList != null ? friendsList.size() : 0;
        }

        @Override
        public Object getItem(int i) {
            return friendsList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View itemView = LayoutInflater.from(mContext).inflate(R.layout.friend_item, viewGroup, false);
            TextView name = (TextView) itemView.findViewById(R.id.full_name);
            ImageView avatar = (ImageView) itemView.findViewById(R.id.avatar);
            View isOnline = itemView.findViewById(R.id.is_user_online);

            VkFriend friend = friendsList.get(i);
            Glide.with(mContext).load(friend.getSmallPhotoUrl())
                    .fitCenter()
                    .into(avatar);

            isOnline.setVisibility(friend.isOnline() ? View.VISIBLE : View.GONE);

            name.setText(mContext.getString(R.string.main_activity_user_name,
                    friend.getName(),
                    friend.getSurname()));


            return itemView;
        }
    }
}
