package com.adedom.basicandroid;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.adedom.basicandroid.util.Utility;
import com.adedom.library.Dru;
import com.adedom.library.ExecuteUpdate;
import com.bumptech.glide.Glide;

import java.io.IOException;

public class EditProductActivity extends AppCompatActivity {

    private EditText mEtProductId;
    private EditText mEtName;
    private EditText mEtPrice;
    private EditText mEtQty;
    private ImageView mIvImage;
    private Button mBtOk;
    private Button mBtCancel;
    private Bitmap bitmap;
    private Product product;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        product = getIntent().getParcelableExtra("product");

        mEtProductId = (EditText) findViewById(R.id.et_product_id);
        mEtName = (EditText) findViewById(R.id.et_name);
        mEtPrice = (EditText) findViewById(R.id.et_price);
        mEtQty = (EditText) findViewById(R.id.et_qty);
        mIvImage = (ImageView) findViewById(R.id.iv_image);
        mBtOk = (Button) findViewById(R.id.bt_ok);
        mBtCancel = (Button) findViewById(R.id.bt_cancel);

        mEtProductId.setText(product.getProductId());
        mEtName.setText(product.getName());
        mEtPrice.setText(product.getPrice() + "");
        mEtQty.setText(product.getQty() + "");
        Glide.with(getBaseContext())
                .load(ConnectDB.BASE_IMAGE + product.getImage())
                .into(mIvImage);

        mIvImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.selectImage(EditProductActivity.this, 2345);
            }
        });

        mBtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mBtOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateProduct();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2345 && resultCode == RESULT_OK && data != null) {
            try {
                Uri path = data.getData();
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), path);
                mIvImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void updateProduct() {
        String productId = mEtProductId.getText().toString().trim();
        String name = mEtName.getText().toString().trim();
        String price = mEtPrice.getText().toString().trim();
        String qty = mEtQty.getText().toString().trim();

        if (name.isEmpty()) {
            mEtName.setFocusable(true);
            mEtName.setError("Empty");
            return;
        } else if (price.isEmpty()) {
            mEtPrice.setFocusable(true);
            mEtPrice.setError("Empty");
            return;
        } else if (qty.isEmpty()) {
            mEtQty.setFocusable(true);
            mEtQty.setError("Empty");
            return;
        }

        String image = product.getImage();
        if (bitmap != null) {
            image = Utility.getImageName();
            Utility.uploadImage(image, bitmap);
        }

        String sql = "UPDATE product SET name='" + name + "',price=" + price + ",qty=" + qty
                + ",image='" + image + "' WHERE product_id = '" + productId + "'";
        Dru.connection(ConnectDB.getConnection())
                .execute(sql)
                .commit(new ExecuteUpdate() {
                    @Override
                    public void onComplete() {
                        Toast.makeText(getBaseContext(), "Update success", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }
}