package com.therishka.androidlab_2;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.therishka.androidlab_2.models.VkDialog;
import com.therishka.androidlab_2.models.VkDialogResponse;
import com.therishka.androidlab_2.network.RxVk;

import java.util.List;

public class DialogsActivity extends AppCompatActivity {
    List<VkDialog> mDialogList;
    RecyclerView recyclerViewDialog;
    RecyclerDialogsAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialogs);
        RxVk dial = new RxVk();
        recyclerViewDialog = (RecyclerView) findViewById(R.id.dialogs_list);
        adapter = new RecyclerDialogsAdapter(this);
        recyclerViewDialog.setAdapter(adapter);
        recyclerViewDialog.setLayoutManager(new LinearLayoutManager(this));
        dial.getDialogs(new RxVk.RxVkListener<VkDialogResponse>() {
            @Override
            public void requestFinished(VkDialogResponse requestResult) {
                mDialogList = requestResult.getDialogs();
                adapter.setDialogList(mDialogList);

            }
        });
    }

    private class RecyclerDialogsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        private List<VkDialog> mDialodsList;
        private Context mContext;

        public RecyclerDialogsAdapter(Context mContext) {
            this.mContext = mContext;
        }

        public void setDialogList(List<VkDialog> list){
            mDialodsList = list;
            notifyDataSetChanged();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_item, parent, false);
            return new DialogViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            VkDialog dialog = mDialodsList.get(position);
            ((DialogsActivity.DialogViewHolder) holder).bind(dialog);
            Glide.with(mContext).load(dialog.getPhoto())
                    .fitCenter()
                    .into(((DialogsActivity.DialogViewHolder) holder).avatar);
        }

        @Override
        public int getItemCount() {
            return mDialodsList == null ? 0 : mDialodsList.size();
        }
    }

    public class DialogViewHolder extends RecyclerView.ViewHolder{
        private TextView lastMessage;
        private TextView name;
        private ImageView avatar;
        private View isReaded;

        public DialogViewHolder(View itemView) {
            super(itemView);
            this.lastMessage = (TextView) itemView.findViewById(R.id.dialog_last_message);
            this.name = (TextView) itemView.findViewById(R.id.dialog_name);
            this.avatar = (ImageView) itemView.findViewById(R.id.dialog_avatar);
            this.isReaded = itemView.findViewById(R.id.is_readed);
        }

        public void bind(VkDialog dialog){
            if(dialog.is_read()){
                isReaded.setVisibility(View.GONE);
            } else {
                isReaded.setVisibility(View.VISIBLE);
            }
            lastMessage.setText(dialog.getMessage());
            name.setText(dialog.getUsername());
        }
    }
}
