package com.topjohnwu.magisk.ui.superuser;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.topjohnwu.magisk.R;
import com.topjohnwu.magisk.model.adapters.PolicyAdapter;
import com.topjohnwu.magisk.model.entity.Policy;
import com.topjohnwu.magisk.ui.base.BaseFragment;

import java.util.List;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;

public class SuperuserFragment extends BaseFragment {

    @BindView(R.id.recyclerView) RecyclerView recyclerView;
    @BindView(R.id.empty_rv) TextView emptyRv;

    private PackageManager pm;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_superuser, container, false);
        unbinder = new SuperuserFragment_ViewBinding(this, view);

        pm = requireActivity().getPackageManager();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        requireActivity().setTitle(getString(R.string.superuser));
    }

    @Override
    public void onResume() {
        super.onResume();
        displayPolicyList();
    }

    private void displayPolicyList() {
        List<Policy> policyList = app.getDB().getPolicyList();

        if (policyList.size() == 0) {
            emptyRv.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            recyclerView.setAdapter(new PolicyAdapter(policyList, app.getDB(), pm));
            emptyRv.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

}
