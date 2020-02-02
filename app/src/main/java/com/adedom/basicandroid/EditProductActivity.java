package com.adedom.basicandroid;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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

import com.adedom.basicandroid.models.Product;
import com.adedom.basicandroid.models.ProductType;
import com.adedom.basicandroid.util.Utility;
import com.adedom.library.Dru;
import com.adedom.library.ExecuteQuery;
import com.adedom.library.ExecuteUpdate;
import com.bumptech.glide.Glide;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class EditProductActivity extends AppCompatActivity {

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
    private Product mProduct;
    private ArrayList<ProductType> mItems;
    private String mProductTypeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        mProduct = getIntent().getParcelableExtra("product");

        mEtProductId = (EditText) findViewById(R.id.et_product_id);
        mEtName = (EditText) findViewById(R.id.et_name);
        mEtPrice = (EditText) findViewById(R.id.et_price);
        mEtQty = (EditText) findViewById(R.id.et_qty);
        mSpinner = (Spinner) findViewById(R.id.spinner);
        mIvAdd = (ImageView) findViewById(R.id.iv_add);
        mIvImage = (ImageView) findViewById(R.id.iv_image);
        mBtOk = (Button) findViewById(R.id.bt_ok);
        mBtCancel = (Button) findViewById(R.id.bt_cancel);

        mEtProductId.setText(mProduct.getProductId());
        mEtName.setText(mProduct.getName());
        mEtPrice.setText(mProduct.getPrice() + "");
        mEtQty.setText(mProduct.getQty() + "");
        Glide.with(getBaseContext())
                .load(ConnectDB.BASE_IMAGE + mProduct.getImage())
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

                            for (int i = 0; i < mItems.size(); i++) {
                                if (mProduct.getProductTypeId().equals(mItems.get(i).getTypeId())) {
                                    mSpinner.setSelection(i);
                                }
                            }

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
        if (requestCode == 2345 && resultCode == RESULT_OK && data != null) {
            try {
                Uri path = data.getData();
                mBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), path);
                mIvImage.setImageBitmap(mBitmap);
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

        String image = mProduct.getImage();
        if (mBitmap != null) {
            image = Utility.getImageName();
            Utility.uploadImage(image, mBitmap);
        }

        String sql = "UPDATE product SET name='" + name + "',price=" + price + ",qty=" + qty
                + ",image='" + image + "',ProductTypeID='" + mProductTypeId
                + "' WHERE product_id = '" + productId + "'";
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