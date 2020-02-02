package com.adedom.basicandroid;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.adedom.basicandroid.models.Product;
import com.adedom.basicandroid.util.Utility;
import com.bumptech.glide.Glide;

public class ProductDialog extends DialogFragment {

    private ImageView mIvImage;
    private TextView mTvName;
    private TextView mTvPrice;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_product, null);

        Product product = getArguments().getParcelable("product");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("สินค้า");

        mIvImage = (ImageView) view.findViewById(R.id.iv_image);
        mTvName = (TextView) view.findViewById(R.id.tv_name);
        mTvPrice = (TextView) view.findViewById(R.id.tv_price);

        mTvName.setText(product.getName());
        mTvPrice.setText(Utility.toPrice(product.getPrice()));

        Glide.with(this)
                .load(ConnectDB.BASE_IMAGE + product.getImage())
                .into(mIvImage);

        return builder.setView(view).create();
    }
}
