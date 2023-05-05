package com.example.myfinal.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myfinal.Model.Msg;
import com.example.myfinal.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class MsgAdapter extends RecyclerView.Adapter {

    Context context;
    ArrayList<Msg> msgArrayList;
    int ITEM_SEND = 1;
    int ITEM_RECEIVE = 2;

    public MsgAdapter(Context context, ArrayList<Msg> msgArrayList) {
        this.context = context;
        this.msgArrayList = msgArrayList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == ITEM_SEND) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_msg_sender, parent,
                    false);
            return new SenderViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.item_msg_receiver, parent,
                    false);
            return new ReceiverViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Msg msg = msgArrayList.get(position);
        if (holder.getClass() == SenderViewHolder.class) {
            SenderViewHolder senderViewHolder = (SenderViewHolder) holder;
            ((SenderViewHolder) holder).tv_sender.setText(msg.getMsg());
        } else {
            ReceiverViewHolder receiverViewHolder = (ReceiverViewHolder) holder;
            ((ReceiverViewHolder) holder).tv_receiver.setText(msg.getMsg());
        }
    }

    @Override
    public int getItemCount() {
        return msgArrayList.size();
    }

    public class ReceiverViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_receiver;

        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_receiver = itemView.findViewById(R.id.tv_receiver);
        }
    }

    public class SenderViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_sender;

        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_sender = itemView.findViewById(R.id.tv_sender);
        }
    }

    @Override
    public int getItemViewType(int position) {
        Msg msg = msgArrayList.get(position);
        if (FirebaseAuth.getInstance().getCurrentUser().getEmail().toString().equals(msg.getId_sender())) {
            return ITEM_SEND;
        } else {
            return ITEM_RECEIVE;
        }
    }
}
