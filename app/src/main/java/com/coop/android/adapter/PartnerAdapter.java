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

public class PartnerAdapter extends RecyclerView.Adapter<PartnerAdapter.FeedHolder> {

    private static final String TAG = "PartnerAdapter";
    private Context context;
    private List<TransInfoBean> data;

    public PartnerAdapter(Context context, List<TransInfoBean> data) {
        this.context = context;
        this.data = data;
    }

    @Override
    public FeedHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tran_partner, parent, false);
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
        holder.userNameTV.setText(bean.getProjectName());
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
        holder.tranDescTV.setText(bean.getReceiveRemark());
        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(ProjectInfoActivity.createIntent(context,bean.getProjectId()));
            }
        });
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class FeedHolder extends RecyclerView.ViewHolder {
        TextView userNameTV, tranTokenTV, tranTimeTV, tranDescTV;
        ImageView leftLabelIV;
        LinearLayout rootView;

        public FeedHolder(View itemView) {
            super(itemView);
            userNameTV = itemView.findViewById(R.id.userNameTV);
            tranTimeTV = itemView.findViewById(R.id.tranTimeTV);
            tranTokenTV = itemView.findViewById(R.id.tranTokenTV);
            leftLabelIV = itemView.findViewById(R.id.leftLabelIV);
            tranDescTV = itemView.findViewById(R.id.tranDescTV);
            rootView = itemView.findViewById(R.id.rootView);
        }
    }
}