package com.example.tallybook.fragment;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import cn.bmob.v3.http.bean.Init;

public abstract class BaseFragment extends Fragment {

    public Context context;
    public Resources resources;
    public LayoutInflater layoutInflater;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        this.resources = context.getResources();
        this.layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private View rootView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(getLayoutID(),container,false);
        }

        ViewGroup parent = (ViewGroup) rootView.getParent();
        if (parent != null) {
            parent.removeView(rootView);
        }

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        bindEvent();
        initData();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    // VV View类型  id 控件id
    protected <VV extends View> VV findView(View view, @IdRes int id) {
        return view.findViewById(id);
    }

    protected <VV extends View> VV findView(@IdRes int id) {
        return rootView.findViewById(id);
    }

    protected abstract void initData();

    protected abstract void bindEvent();

    protected abstract void initView(View view);

    protected abstract int getLayoutID();
}
