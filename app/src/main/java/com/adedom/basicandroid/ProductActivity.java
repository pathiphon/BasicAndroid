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

import com.adedom.basicandroid.models.Product;
import com.adedom.basicandroid.util.Utility;
import com.adedom.library.Dru;
import com.adedom.library.ExecuteQuery;
import com.adedom.library.ExecuteUpdate;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ProductActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private FloatingActionButton mFloatingActionButton;
    private ArrayList<Product> mItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("ข้อมูลสินค้า");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));

        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getBaseContext(), InsertProductActivity.class));
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        String sql = "SELECT * FROM product";
        Dru.connection(ConnectDB.getConnection())
                .execute(sql)
                .commit(new ExecuteQuery() {
                    @Override
                    public void onComplete(ResultSet resultSet) {
                        try {
                            mItems = new ArrayList<Product>();
                            while (resultSet.next()) {
                                Product product = new Product(
                                        resultSet.getString(1),
                                        resultSet.getString(2),
                                        resultSet.getInt(3),
                                        resultSet.getInt(4),
                                        resultSet.getString(5),
                                        resultSet.getString(6)
                                );
                                mItems.add(product);
                            }

                            mRecyclerView.setAdapter(new ProductAdapter());
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_product, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.report:
                startActivity(new Intent(getBaseContext(), ReportProductActivity.class));
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    class ProductAdapter extends RecyclerView.Adapter<ProductHolder> {
        @NonNull
        @Override
        public ProductHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_management_product, parent, false);
            return new ProductHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ProductHolder holder, int position) {
            Product product = mItems.get(position);
            holder.tvName.setText(product.getName());
            holder.tvPrice.setText(Utility.toPrice(product.getPrice()));
            holder.tvProductId.setText(product.getProductId());
            holder.tvQty.setText(Utility.toQty(product.getQty()));

            Glide.with(getBaseContext())
                    .load(ConnectDB.BASE_IMAGE + product.getImage())
                    .circleCrop()
                    .into(holder.ivImage);
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }
    }

    class ProductHolder extends RecyclerView.ViewHolder {
        final ImageView ivImage;
        final TextView tvName;
        final TextView tvPrice;
        final TextView tvProductId;
        final TextView tvQty;
        final Button btEdit;

        public ProductHolder(@NonNull View itemView) {
            super(itemView);

            ivImage = (ImageView) itemView.findViewById(R.id.iv_image);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvPrice = (TextView) itemView.findViewById(R.id.tv_price);
            tvProductId = (TextView) itemView.findViewById(R.id.tv_product_id);
            tvQty = (TextView) itemView.findViewById(R.id.tv_qty);
            btEdit = (Button) itemView.findViewById(R.id.bt_edit);

            btEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Product product = mItems.get(getAdapterPosition());
                    startActivity(new Intent(getBaseContext(), EditProductActivity.class)
                            .putExtra("product", product));
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    final Product product = mItems.get(getAdapterPosition());
                    AlertDialog.Builder builder = new AlertDialog.Builder(ProductActivity.this);
                    builder.setTitle("Delete")
                            .setMessage("Are you sure delete " + product.getName() + "?")
                            .setPositiveButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            }).setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            deleteProduct(product.getProductId());
                        }
                    }).show();
                    return false;
                }
            });
        }

        private void deleteProduct(String productId) {
            String sql = "DELETE FROM product WHERE product_id = '" + productId + "'";
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
