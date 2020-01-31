package com.adedom.basicandroid;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.adedom.library.Dru;
import com.adedom.library.ExecuteUpdate;

public class EditProductActivity extends AppCompatActivity {

    private EditText mEtProductId;
    private EditText mEtName;
    private EditText mEtPrice;
    private EditText mEtQty;
    private EditText mEtImage;
    private Button mBtOk;
    private Button mBtCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        Product product = getIntent().getParcelableExtra("product");

        mEtProductId = (EditText) findViewById(R.id.et_product_id);
        mEtName = (EditText) findViewById(R.id.et_name);
        mEtPrice = (EditText) findViewById(R.id.et_price);
        mEtQty = (EditText) findViewById(R.id.et_qty);
        mEtImage = (EditText) findViewById(R.id.et_image);
        mBtOk = (Button) findViewById(R.id.bt_ok);
        mBtCancel = (Button) findViewById(R.id.bt_cancel);

        mEtProductId.setText(product.getProductId());
        mEtName.setText(product.getName());
        mEtPrice.setText(product.getPrice() + "");
        mEtQty.setText(product.getQty() + "");
        mEtImage.setText(product.getImage());

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

    private void updateProduct() {
        String productId = mEtProductId.getText().toString().trim();
        String name = mEtName.getText().toString().trim();
        String price = mEtPrice.getText().toString().trim();
        String qty = mEtQty.getText().toString().trim();
        String image = mEtImage.getText().toString().trim();

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