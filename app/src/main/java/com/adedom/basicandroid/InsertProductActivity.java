package com.adedom.basicandroid;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.adedom.basicandroid.util.Utility;
import com.adedom.library.Dru;
import com.adedom.library.ExecuteQuery;
import com.adedom.library.ExecuteUpdate;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InsertProductActivity extends AppCompatActivity {

    private EditText mEtProductId;
    private EditText mEtName;
    private EditText mEtPrice;
    private EditText mEtQty;
    private ImageView mIvImage;
    private Button mBtOk;
    private Button mBtCancel;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_product);

        mEtProductId = (EditText) findViewById(R.id.et_product_id);
        mEtName = (EditText) findViewById(R.id.et_name);
        mEtPrice = (EditText) findViewById(R.id.et_price);
        mEtQty = (EditText) findViewById(R.id.et_qty);
        mIvImage = (ImageView) findViewById(R.id.iv_image);
        mBtOk = (Button) findViewById(R.id.bt_ok);
        mBtCancel = (Button) findViewById(R.id.bt_cancel);

        mIvImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utility.selectImage(InsertProductActivity.this, 1234);
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
                insertProduct();
            }
        });

        mEtProductId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String productId = mEtProductId.getText().toString().trim();
                String sql = "SELECT * FROM product WHERE product_id = '" + productId + "'";
                Dru.connection(ConnectDB.getConnection())
                        .execute(sql)
                        .commit(new ExecuteQuery() {
                            @Override
                            public void onComplete(ResultSet resultSet) {
                                try {
                                    if (resultSet.next()) {
                                        mBtOk.setEnabled(false);
                                    } else {
                                        mBtOk.setEnabled(true);
                                    }
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1234 && resultCode == RESULT_OK && data != null) {
            try {
                Uri path = data.getData();
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), path);
                mIvImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void insertProduct() {
        String productId = mEtProductId.getText().toString().trim();
        String name = mEtName.getText().toString().trim();
        String price = mEtPrice.getText().toString().trim();
        String qty = mEtQty.getText().toString().trim();

        if (productId.isEmpty()) {
            mEtProductId.setFocusable(true);
            mEtProductId.setError("Empty");
            return;
        } else if (name.isEmpty()) {
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

        String image = "empty";
        if (bitmap != null) {
            image = Utility.getImageName();
            Utility.uploadImage(image, bitmap);
        }

        String sql = "INSERT INTO product(product_id, name, price, qty, image) " +
                "VALUES ('" + productId + "','" + name + "'," + price + "," + qty + ",'" + image + "')";
        Dru.connection(ConnectDB.getConnection())
                .execute(sql)
                .commit(new ExecuteUpdate() {
                    @Override
                    public void onComplete() {
                        Toast.makeText(getBaseContext(), "Insert success", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }
}
