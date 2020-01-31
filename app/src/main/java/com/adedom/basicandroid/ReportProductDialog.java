package com.adedom.basicandroid;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class ReportProductDialog extends DialogFragment {

    private OnAttachListener listener;
    private EditText mEtProductIdStart;
    private EditText mEtProductIdEnd;
    private Button mBtSearch;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_report_product, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("รายงาน");

        mEtProductIdStart = (EditText) view.findViewById(R.id.et_product_id_start);
        mEtProductIdEnd = (EditText) view.findViewById(R.id.et_product_id_end);
        mBtSearch = (Button) view.findViewById(R.id.bt_search);

        mBtSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String productIdStart = mEtProductIdStart.getText().toString().trim();
                String productIdEnd = mEtProductIdEnd.getText().toString().trim();
                listener.onAttach(productIdStart, productIdEnd);
                getDialog().dismiss();
            }
        });

        return builder.setView(view).create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        listener = (OnAttachListener) context;
    }
}
