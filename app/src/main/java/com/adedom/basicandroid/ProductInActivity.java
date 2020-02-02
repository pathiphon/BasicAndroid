package com.adedom.basicandroid;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adedom.basicandroid.models.ProductIn;
import com.adedom.basicandroid.util.Utility;
import com.adedom.library.Dru;
import com.adedom.library.ExecuteQuery;
import com.adedom.library.ExecuteUpdate;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ProductInActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private FloatingActionButton mFloatingActionButton;
    private ArrayList<ProductIn> mItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_in);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("รับสินค้าเข้า");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));

        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getBaseContext(), InsertProductInActivity.class));
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        String sql = "SELECT p.name, image, pi.* FROM productin AS pi INNER JOIN product AS p ON pi.ProductID = p.product_id";
        Dru.connection(ConnectDB.getConnection())
                .execute(sql)
                .commit(new ExecuteQuery() {
                    @Override
                    public void onComplete(ResultSet resultSet) {
                        try {
                            mItems = new ArrayList<ProductIn>();
                            while (resultSet.next()) {
                                ProductIn in = new ProductIn(
                                        resultSet.getString(1),
                                        resultSet.getString(2),
                                        resultSet.getString(3),
                                        resultSet.getString(4),
                                        resultSet.getString(5),
                                        resultSet.getInt(6),
                                        resultSet.getDouble(7)
                                );
                                mItems.add(in);
                            }
                            mRecyclerView.setAdapter(new ProductInAdapter());
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_product_in, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.report_product_in:
                Toast.makeText(getBaseContext(), "Coming soon", Toast.LENGTH_SHORT).show();
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private class ProductInAdapter extends RecyclerView.Adapter<ProductInHolder> {
        @NonNull
        @Override
        public ProductInHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_in, parent, false);
            return new ProductInHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ProductInHolder holder, int position) {
            ProductIn in = mItems.get(position);
            holder.tvName.setText(in.getName());
            holder.tvPrice.setText(Utility.toPrice(in.getPrice()));
            holder.tvProductId.setText(in.getProductId());
            holder.tvQty.setText(Utility.toQty(in.getQuantity()));
            holder.tvProductInNo.setText(in.getProductIdNo());
            holder.tvDateIn.setText(in.getDateIn());

            Glide.with(getBaseContext())
                    .load(ConnectDB.BASE_IMAGE + in.getImage())
                    .circleCrop()
                    .into(holder.ivImage);
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }
    }

    private class ProductInHolder extends RecyclerView.ViewHolder {
        private final ImageView ivImage;
        private final TextView tvName;
        private final TextView tvPrice;
        private final TextView tvProductId;
        private final TextView tvQty;
        private final TextView tvProductInNo;
        private final TextView tvDateIn;
        private final Button btEdit;

        public ProductInHolder(@NonNull View itemView) {
            super(itemView);

            ivImage = (ImageView) itemView.findViewById(R.id.iv_image);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvPrice = (TextView) itemView.findViewById(R.id.tv_price);
            tvProductId = (TextView) itemView.findViewById(R.id.tv_product_id);
            tvQty = (TextView) itemView.findViewById(R.id.tv_qty);
            tvProductInNo = (TextView) itemView.findViewById(R.id.tv_product_in_no);
            tvDateIn = (TextView) itemView.findViewById(R.id.tv_date_in);
            btEdit = (Button) itemView.findViewById(R.id.bt_edit);

            btEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ProductIn productIn = mItems.get(getAdapterPosition());
                    startActivity(new Intent(getBaseContext(), EditProductInActivity.class)
                            .putExtra("product_in", productIn));
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    final ProductIn productIn = mItems.get(getAdapterPosition());
                    AlertDialog.Builder builder = new AlertDialog.Builder(ProductInActivity.this);
                    builder.setTitle("Delete")
                            .setMessage("Are you sure delete " + productIn.getName() + "?")
                            .setPositiveButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            deleteProductIn(productIn.getProductIdNo(), productIn.getProductId(), productIn.getQuantity());
                        }
                    }).show();
                    return false;
                }
            });
        }

        private void deleteProductIn(String productIdNo, final String productId, final int quantity) {
            String sql = "DELETE FROM productin WHERE ProductInNo = '" + productIdNo + "'";
            Dru.connection(ConnectDB.getConnection())
                    .execute(sql)
                    .commit(new ExecuteUpdate() {
                        @Override
                        public void onComplete() {
                            updateProduct(productId, quantity);
                        }
                    });
        }

        private void updateProduct(String productId, int quantity) {
            String sql = "UPDATE product SET qty=qty-" + quantity + " WHERE product_id = '" + productId + "'";
            Dru.connection(ConnectDB.getConnection())
                    .execute(sql)
                    .commit(new ExecuteUpdate() {
                        @Override
                        public void onComplete() {
                            Toast.makeText(getBaseContext(), "Delete success", Toast.LENGTH_SHORT).show();
                            onResume();
                        }
                    });
        }
    }
}
