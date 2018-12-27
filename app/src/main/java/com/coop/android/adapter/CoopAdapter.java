package com.coop.android.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coop.android.R;
import com.coop.android.activity.ProjectInfoActivity;
import com.coop.android.model.TransInfoBean;
import com.coop.android.utils.NumUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import zuo.biao.library.util.Log;
import zuo.biao.library.util.StringUtil;

public class CoopAdapter extends RecyclerView.Adapter<CoopAdapter.FeedHolder> {

    private static final String TAG = "FeedAdapter";
    private Context context;
    private List<TransInfoBean> data;

    public CoopAdapter(Context context, List<TransInfoBean> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public FeedHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tran_enter, parent, false);
        return new FeedHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FeedHolder holder, int position) {
        final TransInfoBean bean = data.get(position);
//
//        holder.tv_day.setText(mBackDatas.getStatusBeanList().get(position).getDay_time());
//        holder.tv_week.setText(mBackDatas.getStatusBeanList().get(position).getWeek_time());

//        timeBeanList = mBackDatas.getStatusBeanList().get(position).getTimeBeanList();
//        ListViewAdapter adapter = new ListViewAdapter(context,timeBeanList);
//
        if (position == 0)
            holder.leftLabelIV.setImageResource(R.mipmap.left_label_record_new);
        else
            holder.leftLabelIV.setImageResource(R.mipmap.left_label_record);
        holder.userNameTV.setText(bean.getInveName());
        String projectTime = bean.getCreateTime();
        if (!StringUtil.isEmpty(projectTime)) {
            try {
                Date date = new SimpleDateFormat("yyyy-MM-dd").parse(projectTime);
                String now = new SimpleDateFormat("yyyy年MM月dd日").format(date);
                holder.tranTimeTV.setText(now);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }
        holder.tranTokenTV.setText(NumUtils.formatNum(bean.getTokenNum(), false));
        holder.hasTokenTV.setText(bean.getBalanceAmount());
        holder.tranDescTV.setText(bean.getPayRemark());
        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(ProjectInfoActivity.createIntent(context, bean.getProjectId()));
            }
        });
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class FeedHolder extends RecyclerView.ViewHolder {
        TextView userNameTV, tranTokenTV, tranTimeTV, hasTokenTV, tranDescTV;
        ImageView leftLabelIV;
        LinearLayout rootView;

        public FeedHolder(View itemView) {
            super(itemView);
            userNameTV = itemView.findViewById(R.id.userNameTV);
            tranTimeTV = itemView.findViewById(R.id.tranTimeTV);
            tranTokenTV = itemView.findViewById(R.id.tranTokenTV);
            hasTokenTV = itemView.findViewById(R.id.hasTokenTV);
            leftLabelIV = itemView.findViewById(R.id.leftLabelIV);
            tranDescTV = itemView.findViewById(R.id.tranDescTV);
            rootView = itemView.findViewById(R.id.rootView);
        }
    }
}