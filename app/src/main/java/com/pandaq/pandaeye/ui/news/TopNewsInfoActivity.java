package com.pandaq.pandaeye.ui.news;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.pandaq.pandaeye.R;
import com.pandaq.pandaeye.config.Constants;
import com.pandaq.pandaeye.entity.neteasynews.TopNewsContent;
import com.pandaq.pandaeye.presenter.news.TopNewsInfoPresenter;
import com.pandaq.pandaeye.ui.ImplView.ITopNewsInfoActivity;
import com.pandaq.pandaeye.ui.base.BaseActivity;
import com.pandaq.pandaeye.utils.DensityUtil;
import com.pandaq.pandaeye.utils.PicassoTarget;
import com.pandaq.pandaeye.utils.WebUtils;
import com.pandaq.pandaeye.widget.FiveThreeImageView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.tencent.smtt.sdk.WebView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TopNewsInfoActivity extends BaseActivity implements ITopNewsInfoActivity {

    @BindView(R.id.news_img)
    FiveThreeImageView mNewsImg;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout mToolbarLayout;
    @BindView(R.id.app_bar)
    AppBarLayout mAppBar;
    @BindView(R.id.fab)
    FloatingActionButton mFab;
    @BindView(R.id.wv_topnews_content)
    WebView mWvTopnewsContent;
    @BindView(R.id.cl_topnews_content_parent)
    CoordinatorLayout mClTopnewsContentParent;
    @BindView(R.id.pb_topnews_content)
    ProgressBar mPbTopnewsContent;
    @BindView(R.id.toolbar_title)
    TextView mToolbarTitle;
    private int width;
    private int heigh;
    private TopNewsInfoPresenter mPresenter = new TopNewsInfoPresenter(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_news_info);
        ButterKnife.bind(this);
        initView();
        initData();
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
    }

    private void initView() {
        int[] deviceInfo = DensityUtil.getDeviceInfo(this);
        width = deviceInfo[0];
        heigh = width * 3 / 5;
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAfterTransition();
            }
        });
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        String news_id = bundle.getString(Constants.BUNDLE_KEY_ID);
        String newsImg = bundle.getString(Constants.BUNDLE_KEY_IMG_URL);
        String title = bundle.getString(Constants.BUNDLE_KEY_TITLE);
        loadTopNewsInfo(news_id);
        Target target = new PicassoTarget(this,mNewsImg,mToolbarLayout,mToolbar);
        //不设置的话会有时候不加载图片
        mNewsImg.setTag(target);
        Picasso.with(this)
                .load(newsImg)
                .resize(width, heigh)
                .into(target);
        mToolbarTitle.setText(title);
        mToolbarTitle.setSelected(true);
    }

    @Override
    public void showProgressBar() {
        mPbTopnewsContent.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressBar() {
        mPbTopnewsContent.setVisibility(View.GONE);
    }

    @Override
    public void loadTopNewsInfo(String news_id) {
        mPresenter.loadNewsContent(news_id);
    }

    @Override
    public void loadFail(String errmsg) {
        showSnackBar(mClTopnewsContentParent, Constants.ERRO + errmsg, Snackbar.LENGTH_SHORT);

    }

    @Override
    public void loadSuccess(TopNewsContent topNewsContent) {
        String htmlBody = topNewsContent.getBody();
        String url = WebUtils.buildHtmlForIt(htmlBody, false);
        mWvTopnewsContent.loadDataWithBaseURL(WebUtils.BASE_URL, url, WebUtils.MIME_TYPE, WebUtils.ENCODING, WebUtils.FAIL_URL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPresenter.unSubscribe();
    }

}
