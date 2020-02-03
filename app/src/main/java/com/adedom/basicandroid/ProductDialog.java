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
import com.adedom.library.Dru;

public class ProductDialog extends DialogFragment {

    private ImageView mIvImage;
    private TextView mTvName;
    private TextView mTvPrice;
    private TextView mTvQty;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_product, null);

        final Product product = getArguments().getParcelable("product");

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("สินค้า");

        mIvImage = (ImageView) view.findViewById(R.id.iv_image);
        mTvName = (TextView) view.findViewById(R.id.tv_name);
        mTvPrice = (TextView) view.findViewById(R.id.tv_price);
        mTvQty = (TextView) view.findViewById(R.id.tv_qty);

        mTvName.setText(product.getName());
        mTvPrice.setText(product.getPrice() + " บาท");
        mTvQty.setText(product.getQty() + " หน่วย");
        Dru.loadImage(mIvImage, ConnectDB.BASE_IMAGE + product.getImage());

        return builder.setView(view).create();
    }
}
