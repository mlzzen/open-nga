package sp.phone.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.android.arouter.launcher.ARouter;
import com.google.android.material.internal.NavigationMenuView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;

import gov.anzong.androidnga.R;
import gov.anzong.androidnga.activity.ForumListActivity;
import gov.anzong.androidnga.arouter.ARouterConstants;
import gov.anzong.androidnga.base.widget.ViewFlipperEx;
import gov.anzong.androidnga.common.PreferenceKey;
import sp.phone.common.UserManager;
import sp.phone.common.UserManagerImpl;
import sp.phone.mvp.contract.BoardContract;
import sp.phone.mvp.model.entity.Board;
import sp.phone.mvp.presenter.BoardPresenter;
import sp.phone.rxjava.RxEvent;
import sp.phone.ui.adapter.BoardPagerAdapter;
import sp.phone.ui.adapter.FlipperUserAdapter;
import sp.phone.ui.fragment.dialog.AddBoardDialogFragment;
import sp.phone.util.ActivityUtils;


/**
 * 首页的容器
 * Created by Justwen on 2017/6/29.
 */

public class NavigationDrawerFragment extends BaseMvpFragment<BoardPresenter> implements BoardContract.View, AdapterView.OnItemClickListener {

    private ViewPager mViewPager;

    private ViewFlipperEx mHeaderView;

    private TextView mReplyCountView;

    private BoardPagerAdapter mBoardPagerAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerRxBus();
    }

    @Override
    protected void accept(RxEvent rxEvent) {
        if (rxEvent.what == RxEvent.EVENT_SHOW_TOPIC_LIST) {
            mPresenter.showTopicList((Board) rxEvent.obj);
        } else {
            super.accept(rxEvent);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        setupToolbar(toolbar);

        initDrawerLayout(view, toolbar);
        initNavigationView(view);

        mViewPager = view.findViewById(R.id.pager);
        TabLayout tabLayout = view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        tabLayout.setSelectedTabIndicatorColor(tabLayout.getTabTextColors().getDefaultColor());

        super.onViewCreated(view, savedInstanceState);
        mPresenter.loadBoardInfo();
    }

    @Override
    protected BoardPresenter onCreatePresenter() {
        return new BoardPresenter();
    }

    private void initDrawerLayout(View rootView, Toolbar toolbar) {
        DrawerLayout drawerLayout = rootView.findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(getActivity(), drawerLayout, toolbar, R.string.app_name, R.string.app_name);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
    }

    private void initNavigationView(View rootView) {
        NavigationView navigationView = rootView.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this::onOptionsItemSelected);
        MenuItem menuItem = navigationView.getMenu().findItem(R.id.menu_gun);
        NavigationMenuView menuView = (NavigationMenuView) navigationView.getChildAt(0);
        menuView.setVerticalScrollBarEnabled(false);
        View actionView = getLayoutInflater().inflate(R.layout.nav_menu_action_view_gun, null);
        menuItem.setActionView(actionView);
        menuItem.expandActionView();
        mReplyCountView = actionView.findViewById(R.id.reply_count);
        mHeaderView = navigationView.getHeaderView(0).findViewById(R.id.viewFlipper);
        updateHeaderView();
    }

    private void setReplyCount(int count) {
        mReplyCountView.setText(String.valueOf(count));
    }

    @Override
    public void updateHeaderView() {
        FlipperUserAdapter adapter = new FlipperUserAdapter(mPresenter);
        mHeaderView.setAdapter(adapter);
        mHeaderView.setInAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.right_in));
        mHeaderView.setOutAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.right_out));
        mHeaderView.setDisplayedChild(UserManagerImpl.getInstance().getActiveUserIndex());
    }

    @Override
    public void notifyDataSetChanged() {
        mBoardPagerAdapter.notifyDataSetChanged();
    }

    @Override
    public int getCurrentItem() {
        return mViewPager.getCurrentItem();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add:
                gotoForumList();
                break;
            case R.id.menu_add_id:
                showAddBoardDialog();
                break;
            case R.id.menu_login:
                jumpToLogin();
                break;
            default:
                return getActivity().onOptionsItemSelected(item);
        }
        return true;
    }

    private void gotoForumList() {
        Intent intent = new Intent(getActivity(), ForumListActivity.class);
        startActivity(intent);
    }

    @Override
    public void jumpToLogin() {
        ARouter.getInstance().build(ARouterConstants.ACTIVITY_LOGIN).navigation(getActivity(), 1);
    }

    private void showAddBoardDialog() {
        new AddBoardDialogFragment().setOnAddBookmarkListener((name, fid, stid) -> mPresenter.addBoard(fid, name, stid))
                .show(getChildFragmentManager());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ActivityUtils.REQUEST_CODE_LOGIN && resultCode == Activity.RESULT_OK || requestCode == ActivityUtils.REQUEST_CODE_SETTING) {
            mHeaderView.getAdapter().notifyDataSetChanged();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onResume() {
        if (mBoardPagerAdapter == null) {
            mBoardPagerAdapter = new BoardPagerAdapter(getChildFragmentManager(), mPresenter.getBoardCategories());
            mViewPager.setAdapter(mBoardPagerAdapter);
            if (mPresenter.getBookmarkCategory().size() == 0) {
                mViewPager.setCurrentItem(1);
            }
        } else {
            mBoardPagerAdapter.notifyDataSetChanged();
        }
        setReplyCount(PreferenceManager.getDefaultSharedPreferences(getContext()).getInt(PreferenceKey.KEY_REPLY_COUNT, 0));
        UserManager um = UserManagerImpl.getInstance();
        if (um.getUserSize() > 0 && um.getActiveUserIndex() != mHeaderView.getDisplayedChild()) {
            mHeaderView.setDisplayedChild(um.getActiveUserIndex());
        }
        super.onResume();
    }

    @Override
    public int switchToNextUser() {
        mHeaderView.showPrevious();
        return mHeaderView.getDisplayedChild();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String fidString;
        if (parent != null) {
            fidString = (String) parent.getItemAtPosition(position);
            mPresenter.toTopicListPage(position, fidString);
        } else {
            mPresenter.showTopicList((Board) view.getTag());
        }


    }

}
