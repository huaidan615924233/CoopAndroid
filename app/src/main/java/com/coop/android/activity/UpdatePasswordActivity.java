//
//                       _oo0oo_
//                      o8888888o
//                      88" . "88
//                      (| -_- |)
//                      0\  =  /0
//                    ___/`---'\___
//                  .' \|     |// '.
//                 / \|||  :  |||// \
//                / _||||| -:- |||||- \
//               |   | \  -  /// |     |
//               | \_|  ''\---/''  |_/ |
//               \  .-\__  '-'  ___/-. /
//             ___'. .'  /--.--\  `. .'___
//          ."" '<  `.___\_<|>_/___.' >' "".
//         | | :  `- \`.;`\ _ /`;.`/ - ` : | |
//         \  \ `_.   \_ __\ /__ _/   .-` /  /
//     =====`-.____`.___ \_____/___.-`___.-'=====
//                       `=---='
//
//
//     ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
//
//               佛祖保佑         永无BUG
package com.coop.android.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


import com.alibaba.fastjson.JSON;
import com.coop.android.CBaseActivity;
import com.coop.android.R;
import com.coop.android.UserConfigs;
import com.coop.android.http.api.HttpPostApi;
import com.coop.android.model.LoginResponseBean;
import com.coop.android.model.UserInfo;
import com.coop.android.utils.ConstantUtil;
import com.coop.android.utils.Md5EncryptionHelper;
import com.coop.android.utils.SharedPreferencesUtils;
import com.coop.android.utils.ToastUtil;
import com.umeng.analytics.MobclickAgent;

import retrofit_rx.http.HttpManager;
import retrofit_rx.listener.HttpOnNextListener;
import zuo.biao.library.ui.AlertDialog;
import zuo.biao.library.ui.statusbar.StatusBarUtils;
import zuo.biao.library.util.Log;
import zuo.biao.library.util.StringUtil;

/**
 * Created by MR-Z on 2018/12/11.
 */
public class UpdatePasswordActivity extends CBaseActivity implements View.OnClickListener, AlertDialog.OnDialogButtonClickListener {
    public static final String TAG = "UpdatePasswordActivity";
    private int type;
    protected Toolbar toolBar;
    private int countSeconds = 60;//倒计时秒数
    private EditText phoneEV, passwordEV, setPassEV, setPassEVRepeat;
    private TextView getVerifyEV, twoLabelTV;
    private Button setPwdBtn;
    private ImageView showPassIV, showPassIVRepeat;

    /**
     * 启动这个Activity的Intent
     *
     * @param context
     * @return
     */
    public static Intent createIntent(Context context, int type) {
        return new Intent(context, UpdatePasswordActivity.class).putExtra("type", type);
    }

    @SuppressLint("HandlerLeak")
    private Handler mCountHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (countSeconds > 0) {
                --countSeconds;
                getVerifyEV.setTextColor(getResources().getColor(R.color.color_969696));
                getVerifyEV.setText("(" + countSeconds + ")后获取验证码");
                mCountHandler.sendEmptyMessageDelayed(0, 1000);
            } else {
                countSeconds = 60;
                getVerifyEV.setTextColor(getResources().getColor(R.color.color_2E26D9));
                getVerifyEV.setText("请重新获取验证码");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);
        intent = getIntent();
        type = intent.getIntExtra("type", ConstantUtil.UPDATEPASSWORD);
        StatusBarUtils.setStatusBarColorDefault(this);
        initView();
        initData();
        initEvent();
    }

    @Override
    public void initView() {
        twoLabelTV = findViewById(R.id.twoLabelTV);
        if (type == ConstantUtil.UPDATEPASSWORD) {
            tvBaseTitle.setText(getString(R.string.txt_title_update_password));
            twoLabelTV.setText(getString(R.string.txt_login_forget_pwd));
        } else {
            tvBaseTitle.setText(getString(R.string.txt_title_set_password));
            twoLabelTV.setText(getString(R.string.txt_login_number_two));
        }
        toolBar = findViewById(R.id.toolbar_img);
        toolBar.setNavigationIcon(R.mipmap.back_left_btn);
        toolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        phoneEV = findViewById(R.id.phoneEV);
        getVerifyEV = findViewById(R.id.getVerifyEV);
        passwordEV = findViewById(R.id.passwordEV);
        setPwdBtn = findViewById(R.id.setPwdBtn);
        setPassEV = findViewById(R.id.setPassEV);
        setPassEVRepeat = findViewById(R.id.setPassEVRepeat);
        showPassIV = findViewById(R.id.showPassIV);
        showPassIVRepeat = findViewById(R.id.showPassIVRepeat);
    }

    @Override
    public void initData() {

    }

    @Override
    public void initEvent() {
        getVerifyEV.setOnClickListener(this);
        setPwdBtn.setOnClickListener(this);
        showPassIV.setOnClickListener(this);
        showPassIVRepeat.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.getVerifyEV:
                if (countSeconds == 60) {
                    String mobile = phoneEV.getText().toString();
                    Log.e(TAG, "mobile==" + mobile);
                    getVerify(mobile);
                } else {
                    ToastUtil.showShortToast(getApplicationContext(), "不能重复发送验证码");
                }
                break;
            case R.id.setPwdBtn:
                setPassword();
                break;
            case R.id.showPassIV:
                if (setPassEV.getInputType() == (InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD)) {
                    setPassEV.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    showPassIV.setImageResource(R.mipmap.password_show_logo);
                } else {
                    setPassEV.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                    showPassIV.setImageResource(R.mipmap.password_hide_logo);
                }
                break;
            case R.id.showPassIVRepeat:
                if (setPassEVRepeat.getInputType() == (InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD)) {
                    setPassEVRepeat.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    showPassIVRepeat.setImageResource(R.mipmap.password_show_logo);
                } else {
                    setPassEVRepeat.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                    showPassIVRepeat.setImageResource(R.mipmap.password_hide_logo);
                }
                break;
            default:
                break;
        }
    }

    public void setPassword() {
        String mobile = phoneEV.getText().toString().trim();
        String verifyCode = passwordEV.getText().toString().trim();
        if ("".equals(mobile)) {
            Log.e(TAG, "mobile=" + mobile);
            new AlertDialog(mContext, "提示", "手机号码不能为空", true, 0, this).show();
        } else if (!StringUtil.isPhone(mobile)) {
            new AlertDialog(mContext, "提示", "请输入正确的手机号码", true, 0, this).show();
        } else if (StringUtil.isEmpty(verifyCode)) {
            new AlertDialog(mContext, "提示", "请输入验证码", true, 0, this).show();
        } else {
            if (StringUtil.isEmpty(setPassEV.getText().toString()) || setPassEV.getText().toString().length() < 6) {
                new AlertDialog(mContext, "提示", getResources().getString(R.string.txt_password_failed), true, 0, this).show();
                return;
            } else if (!setPassEV.getText().toString().equals(setPassEVRepeat.getText().toString())) {
                new AlertDialog(mContext, "提示", getResources().getString(R.string.txt_password_repeat_failed), true, 0, this).show();
                return;
            }
            HttpPostApi postEntity = new HttpPostApi(setPassWordOnNextListener, this, HttpPostApi.SET_PAY_PASSWORD, true);
            postEntity.setPhoneNumber(mobile);
            postEntity.setCode(verifyCode);
            postEntity.setPayPassword(Md5EncryptionHelper.getMD5WithSalt(setPassEV.getText().toString(), Md5EncryptionHelper.SALT));
            postEntity.setSalt(Md5EncryptionHelper.SALT);
            HttpManager manager = HttpManager.getInstance();
            manager.doHttpDeal(postEntity);
        }
    }

    //获取验证码信息，判断是否有手机号码
    private void getVerify(String mobile) {
        if (StringUtil.isEmpty(mobile)) {
            Log.e(TAG, "mobile=" + mobile);
            new AlertDialog(mContext, "提示", "手机号码不能为空", true, 0, this).show();
        } else if (!StringUtil.isPhone(mobile)) {
            new AlertDialog(mContext, "提示", "请输入正确的手机号码", true, 0, this).show();
        } else {
            Log.e(TAG, "输入了正确的手机号");
            requestVerifyCode(mobile);
        }
    }

    //获取验证码信息，进行验证码请求
    private void requestVerifyCode(String mobile) {
        HttpPostApi postEntity = new HttpPostApi(verifyOnNextListener, this, HttpPostApi.GET_VERIFY, true);
        postEntity.setPhoneNumber(mobile);
        postEntity.setType(2);
        HttpManager manager = HttpManager.getInstance();
        manager.doHttpDeal(postEntity);
        startCountBack();
    }

    //获取验证码信息,进行计时操作
    private void startCountBack() {
        ((Activity) mContext).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getVerifyEV.setText(countSeconds + "");
                mCountHandler.sendEmptyMessage(0);
            }
        });
    }

    HttpOnNextListener verifyOnNextListener = new HttpOnNextListener<String>() {

        @Override
        public void onNext(String string, int code) {
            ToastUtil.showShortToast(getApplicationContext(), "验证码已发送!");
        }

        @Override
        public void onError(Throwable e) {
            ToastUtil.showShortToast(getApplicationContext(), e.getMessage());
            Log.e(TAG, getResources().getString(R.string.txt_server_error) + e.getMessage());
        }
    };
    HttpOnNextListener setPassWordOnNextListener = new HttpOnNextListener<String>() {

        @Override
        public void onNext(String string, int code) {
            if (code == 700) {
                ToastUtil.showShortToast(getApplicationContext(), "登录失效，请重新登录!");
                startActivity(LoginActivity.createIntent(mContext, true));
                return;
            }
            if (code == 501) {
                ToastUtil.showShortToast(getApplicationContext(), "验证码错误！");
                return;
            }
            if (code == 502) {
                ToastUtil.showShortToast(getApplicationContext(), "设置支付密码错误！");
                return;
            }
            String userInfo = SharedPreferencesUtils.getUserInfo(mContext);
            LoginResponseBean user = JSON.parseObject(userInfo, LoginResponseBean.class);
            UserInfo userInfoTemp = user.getUser();
            userInfoTemp.setSalt(Md5EncryptionHelper.SALT);
            user.setUser(userInfoTemp);
            userInfo = JSON.toJSONString(user);
            SharedPreferencesUtils.setUserInfo(mContext, userInfo);
            UserConfigs.loadUserInfo(userInfo);
            ToastUtil.showShortToast(getApplicationContext(), "支付密码设置成功!");
            finish();
        }

        @Override
        public void onError(Throwable e) {
            ToastUtil.showShortToast(getApplicationContext(), e.getMessage());
            Log.e(TAG, getResources().getString(R.string.txt_server_error) + e.getMessage());
        }
    };

    @Override
    public void onDialogButtonClick(int requestCode, boolean isPositive) {

    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("设置密码页面");
        MobclickAgent.onResume(this); //统计时长
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("设置密码页面");
        MobclickAgent.onPause(this);
    }
}
