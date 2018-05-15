package com.fugu.support.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fugu.HippoColorConfig;
import com.fugu.HippoConfig;
import com.fugu.R;
import com.fugu.database.CommonData;
import com.fugu.support.Adapter.HippoSupportAdapter;
import com.fugu.support.Utils.Constants;
import com.fugu.support.Utils.SupportKeys;
import com.fugu.support.callback.HippoSupportView;
import com.fugu.support.callback.OnActionTypeCallback;
import com.fugu.support.callback.OnItemListener;
import com.fugu.support.callback.SupportPresenter;
import com.fugu.support.logicImplView.HippoSupportInteractorImpl;
import com.fugu.support.logicImplView.HippoSupportViewImpl;
import com.fugu.support.model.Category;
import com.fugu.support.model.Item;
import com.fugu.support.model.SupportDataList;
import com.fugu.support.model.callbackModel.OpenChatParams;
import com.fugu.support.model.callbackModel.SendQueryChat;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import static com.fugu.constant.FuguAppConstant.FUGU_WEBSITE_URL;
import static com.fugu.database.CommonData.removeLastPath;
import static com.fugu.support.Utils.SupportKeys.SupportKey.DEFAULT_SUPPORT;
import static com.fugu.support.Utils.SupportKeys.SupportKey.SUPPORT_CATEGORY_DATA;
import static com.fugu.support.Utils.SupportKeys.SupportKey.SUPPORT_CATEGORY_ID;
import static com.fugu.support.Utils.SupportKeys.SupportKey.SUPPORT_DB_VERSION;
import static com.fugu.support.Utils.SupportKeys.SupportKey.SUPPORT_TITLE;
import static com.fugu.support.Utils.SupportKeys.SupportKey.SUPPORT_TRANSACTION_ID;

/**
 * Created by Gurmail S. Kang on 29/03/18.
 * @author gurmail
 */

public class HippoSupportFragment extends BaseFragment implements HippoSupportView, OnItemListener, View.OnClickListener {

    private static final String TAG = HippoSupportFragment.class.getSimpleName();
    private HippoSupportAdapter supportAdapter;
    private SupportPresenter supportView;
    private OnActionTypeCallback typeCallback;
    private HippoColorConfig hippoColorConfig;
    private String categoryData;

    private int serverDBVersion = -1;
    private String defaultFaqName = null;
    private String transactionId = null;

    private Toolbar toolbar;

    private View rootView;
    private TextView poweredBy, mNoDataTextView;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    private Gson gson;

    private ArrayList<Item> supportResponses;
    private ArrayList<String> pathList;
    private String title = "";


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gson = new Gson();
        if(getArguments() != null) {
            if(getArguments().containsKey(DEFAULT_SUPPORT))
                supportResponses = gson.fromJson(getArguments().getString(DEFAULT_SUPPORT), Constants.listType);

            if(getArguments().containsKey(SUPPORT_TITLE))
                title = getArguments().getString(SUPPORT_TITLE);

            if(getArguments().containsKey(SUPPORT_DB_VERSION))
                serverDBVersion = getArguments().getInt(SUPPORT_DB_VERSION);

            if(getArguments().containsKey(SUPPORT_TRANSACTION_ID))
                transactionId = getArguments().getString(SUPPORT_TRANSACTION_ID);

            if(getArguments().containsKey(SUPPORT_CATEGORY_ID))
                defaultFaqName = getArguments().getString(SUPPORT_CATEGORY_ID);

            if(getArguments().containsKey(SUPPORT_CATEGORY_DATA))
                categoryData = getArguments().getString(SUPPORT_CATEGORY_DATA);

        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fugu_support_fragment, container, false);
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        typeCallback = (OnActionTypeCallback) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(typeCallback != null)
            typeCallback = null;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden) {
            removeLastPath();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        toolbar = rootView.findViewById(R.id.my_toolbar);
        /*toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showError();
            }
        });*/
        progressBar = rootView.findViewById(R.id.progress_bar);

        supportAdapter = new HippoSupportAdapter(this);
        recyclerView = rootView.findViewById(R.id.recycler_view);
        recyclerView.setAdapter(supportAdapter);
        mNoDataTextView = rootView.findViewById(R.id.tvNoDataFound);
        hippoColorConfig = CommonData.getColorConfig();
        mNoDataTextView.setTextColor(hippoColorConfig.getHippoThemeColorPrimary());

        poweredBy = rootView.findViewById(R.id.tvPoweredBy);
        supportView = new HippoSupportViewImpl(getActivity(), this, new HippoSupportInteractorImpl());
        initView(serverDBVersion);
    }

    private void initView(int serverDBVersion) {
        if(supportResponses == null)
            supportView.fetchData(defaultFaqName, serverDBVersion);
        else
            setInnerData(supportResponses);

        setPoweredByText(poweredBy);
        poweredBy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(FUGU_WEBSITE_URL));
                startActivity(i);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(supportView != null)
            supportView.onDestroy();
    }



    @Override
    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void setData(SupportDataList supportResponses) {
        if(supportResponses == null || supportResponses.getList() == null || supportResponses.getList().size() == 0) {
            mNoDataTextView.setVisibility(View.VISIBLE);
            setToolbar(toolbar, "Support");
        } else {
            if (supportAdapter != null)
                supportAdapter.setAdapterData((ArrayList<Item>) supportResponses.getList());
            setToolbarTitle(supportResponses.getCategoryName());
            Category category = new Category(supportResponses.getCategoryId(), supportResponses.getCategoryName(), "");
            categoryData = gson.toJson(category);
        }
        setPoweredByText(poweredBy);
    }

    @Override
    public void openChatSupport(OpenChatParams chatParams) {
        HippoConfig.getInstance().openChatByTransactionId(chatParams.getTransactionId(), chatParams.getUserUniqueKey(),
                chatParams.getChannelName(), chatParams.getTagsList(), null, "1", chatParams.getCustomAttributes());
    }

    private void setInnerData(ArrayList<Item> supportResponses) {
        if(supportAdapter != null)
            supportAdapter.setAdapterData(supportResponses);
        setToolbarTitle(title);
    }

    private void setToolbarTitle(String toolbarTitle) {
        setToolbar(toolbar, toolbarTitle);

        pathList = CommonData.getPathList();
        pathList.add(toolbarTitle);
        CommonData.setSupportPath(pathList);
    }

    @Override
    public void showError() {
        Toast.makeText(getActivity(), "Something went wrong. Please try again", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(int actionType, List<Item> items, String title) {
        typeCallback.onActionType((ArrayList<Item>) items, gson.toJson(pathList), title, transactionId, categoryData);
    }

    @Override
    public void onOtherTypeClick(int actionType, Item item) {
        switch (SupportKeys.SupportActionType.get(actionType)) {
            case DESCRIPTION:
                typeCallback.openDetailPage(item, gson.toJson(pathList), transactionId, categoryData);
                break;
            case CHAT_SUPPORT:
                Category category = gson.fromJson(categoryData, Category.class);
                SendQueryChat queryChat = new SendQueryChat(
                        SupportKeys.SupportQueryType.CHAT, category, transactionId,
                        CommonData.getUserUniqueKey(), item.getSupportId(), pathList);

                supportView.openChat(queryChat);
                break;
            case SHOW_CONVERSATION:
                HippoConfig.getInstance().showConversations(getActivity(), "Chat Support");
                break;
            default:

                break;
        }
    }

    @Override
    public void onDescription(Item item) {
        typeCallback.openDetailPage(item, gson.toJson(pathList), transactionId, categoryData);
    }

    @Override
    public void chatSupport(Item item) {
        Category category = gson.fromJson(categoryData, Category.class);
        SendQueryChat queryChat = new SendQueryChat(SupportKeys.SupportQueryType.CHAT, category,
                transactionId, CommonData.getUserUniqueKey(), item.getSupportId(), pathList);
        queryChat.setSubHeader(item.getContent().getSubHeading().getText());

        supportView.openChat(queryChat);
    }

    @Override
    public void showConversaton(Item item) {
        HippoConfig.getInstance().showConversations(getActivity(), "Chat Support");
    }

    @Override
    public void onClick(View view) {
        getActivity().onBackPressed();
    }
}
