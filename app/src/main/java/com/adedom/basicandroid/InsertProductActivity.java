package com.adedom.basicandroid;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.adedom.basicandroid.models.ProductType;
import com.adedom.library.Dru;
import com.adedom.library.ExecuteQuery;
import com.adedom.library.ExecuteUpdate;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class InsertProductActivity extends AppCompatActivity {

    private EditText mEtProductId;
    private EditText mEtName;
    private EditText mEtPrice;
    private EditText mEtQty;
    private Spinner mSpinner;
    private ImageView mIvAdd;
    private ImageView mIvImage;
    private Button mBtOk;
    private Button mBtCancel;
    private Bitmap mBitmap;
    private ArrayList<ProductType> mItems;
    private String mProductTypeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_product);

        mEtProductId = (EditText) findViewById(R.id.et_product_id);
        mEtName = (EditText) findViewById(R.id.et_name);
        mEtPrice = (EditText) findViewById(R.id.et_price);
        mEtQty = (EditText) findViewById(R.id.et_qty);
        mSpinner = (Spinner) findViewById(R.id.spinner);
        mIvAdd = (ImageView) findViewById(R.id.iv_add);
        mIvImage = (ImageView) findViewById(R.id.iv_image);
        mBtOk = (Button) findViewById(R.id.bt_ok);
        mBtCancel = (Button) findViewById(R.id.bt_cancel);

        mIvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getBaseContext(), AddTypeActivity.class));
            }
        });

        mIvImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dru.selectImage(InsertProductActivity.this, 1234);
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
                String sql = "SELECT * FROM product WHERE product_id = '" + charSequence + "'";
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
    protected void onResume() {
        super.onResume();

        setSpinner();
    }

    private void setSpinner() {
        String sql = "SELECT * FROM producttype";
        Dru.connection(ConnectDB.getConnection())
                .execute(sql)
                .commit(new ExecuteQuery() {
                    @Override
                    public void onComplete(ResultSet resultSet) {
                        try {
                            mItems = new ArrayList<ProductType>();
                            while (resultSet.next()) {
                                ProductType type = new ProductType(
                                        resultSet.getString("ProductTypeID"),
                                        resultSet.getString("ProductTypeName")
                                );
                                mItems.add(type);
                            }
                            mSpinner.setAdapter(new ProductTypeAdapter(getBaseContext(), mItems));
                            mSpinner.setSelection(0);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                });

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ProductType type = (ProductType) adapterView.getItemAtPosition(i);
                mProductTypeId = type.getTypeId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1234 && resultCode == RESULT_OK && data != null) {
            try {
                Uri path = data.getData();
                mBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), path);
                mIvImage.setImageBitmap(mBitmap);
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

        String imageName = "empty";
        if (mBitmap != null) {
            imageName = Dru.getImageNameJpg();
            Dru.uploadImage(ConnectDB.BASE_IMAGE, imageName, mBitmap);
        }

        String sql = "INSERT INTO product VALUES ('" + productId + "','" + name + "',"
                + price + "," + qty + ",'" + imageName + "','" + mProductTypeId + "')";
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

    class ProductTypeAdapter extends ArrayAdapter<ProductType> {

        public ProductTypeAdapter(Context context, ArrayList<ProductType> countryList) {
            super(context, 0, countryList);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            return initView(position, convertView, parent);
        }

        @Override
        public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            return initView(position, convertView, parent);
        }

        private View initView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
            }

            TextView tvTypeId = convertView.findViewById(android.R.id.text1);
            TextView tvTypeName = convertView.findViewById(android.R.id.text2);

            ProductType currentItem = getItem(position);

            tvTypeId.setText(currentItem.getTypeId());
            tvTypeName.setText(currentItem.getTypeName());

            return convertView;
        }
    }
}
