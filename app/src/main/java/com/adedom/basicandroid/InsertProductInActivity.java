package com.adedom.basicandroid;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.adedom.basicandroid.models.Product;
import com.adedom.library.Dru;
import com.adedom.library.ExecuteQuery;
import com.adedom.library.ExecuteUpdate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class InsertProductInActivity extends AppCompatActivity {

    private EditText mEtProductInNo;
    private Spinner mSpinner;
    private EditText mEtQuantity;
    private EditText mEtPrice;
    private Button mBtCancel;
    private Button mBtOk;
    private ArrayList<Product> mItems;
    private String mProductId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_product_in);

        mEtProductInNo = (EditText) findViewById(R.id.et_product_in_no);
        mSpinner = (Spinner) findViewById(R.id.spinner);
        mEtQuantity = (EditText) findViewById(R.id.et_quantity);
        mEtPrice = (EditText) findViewById(R.id.et_price);
        mBtCancel = (Button) findViewById(R.id.bt_cancel);
        mBtOk = (Button) findViewById(R.id.bt_ok);

        mBtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mBtOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertProductIn();
            }
        });

        mEtProductInNo.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String sql = "SELECT * FROM productin WHERE ProductInNo = '" + charSequence + "'";
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
        String sql = "SELECT product_id , name FROM product";
        Dru.connection(ConnectDB.getConnection())
                .execute(sql)
                .commit(new ExecuteQuery() {
                    @Override
                    public void onComplete(ResultSet resultSet) {
                        try {
                            mItems = new ArrayList<Product>();
                            while (resultSet.next()) {
                                Product product = new Product();
                                product.setProductId(resultSet.getString(1));
                                product.setName(resultSet.getString(2));
                                mItems.add(product);
                            }
                            mSpinner.setAdapter(new ProductAdapter(getBaseContext(), mItems));
                            mSpinner.setSelection(0);
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                });

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Product product = (Product) adapterView.getItemAtPosition(i);
                mProductId = product.getProductId();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void insertProductIn() {
        String productIdNo = mEtProductInNo.getText().toString().trim();
        final String quantity = mEtQuantity.getText().toString().trim();
        final String price = mEtPrice.getText().toString().trim();
        String sql = "INSERT INTO productin VALUES ('" + productIdNo + "','" + mProductId
                + "',CURRENT_DATE()," + quantity + "," + price + ")";
        Dru.connection(ConnectDB.getConnection())
                .execute(sql)
                .commit(new ExecuteUpdate() {
                    @Override
                    public void onComplete() {
                        updateProduct(price, quantity);
                    }
                });
    }

    private void updateProduct(String price, String quantity) {
        String sql = "UPDATE product SET price=" + price + ",qty=qty+" + quantity + " WHERE product_id = '" + mProductId + "'";
        Dru.connection(ConnectDB.getConnection())
                .execute(sql)
                .commit(new ExecuteUpdate() {
                    @Override
                    public void onComplete() {
                        finish();
                        Toast.makeText(getBaseContext(), "Insert success", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    class ProductAdapter extends ArrayAdapter<Product> {

        public ProductAdapter(Context context, ArrayList<Product> countryList) {
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

            TextView tvProductId = convertView.findViewById(android.R.id.text1);
            TextView tvName = convertView.findViewById(android.R.id.text2);

            Product currentItem = getItem(position);

            tvProductId.setText(currentItem.getProductId());
            tvName.setText(currentItem.getName());

            return convertView;
        }
    }

}
