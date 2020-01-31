package com.adedom.basicandroid;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adedom.basicandroid.util.Utility;
import com.adedom.library.Dru;
import com.adedom.library.ExecuteQuery;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ReportProductActivity extends AppCompatActivity implements OnAttachListener {

    private ArrayList<Product> items;
    private FloatingActionButton mFloatingActionButton;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_product);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("รายงานข้อมูลสินค้า");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mFloatingActionButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getBaseContext()));

        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ReportProductDialog().show(getSupportFragmentManager(), null);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        fetchProduct("", "");
    }

    private void fetchProduct(String productIdStart, String productIdEnd) {
        String sql = "SELECT * FROM product";
        if (!productIdStart.equals("") && !productIdEnd.equals("")) {
            sql = "SELECT * FROM product WHERE product_id BETWEEN '" + productIdStart + "' AND '" + productIdEnd + "'";
        }
        Dru.connection(ConnectDB.getConnection())
                .execute(sql)
                .commit(new ExecuteQuery() {
                    @Override
                    public void onComplete(ResultSet resultSet) {
                        try {
                            items = new ArrayList<Product>();
                            while (resultSet.next()) {
                                Product product = new Product(
                                        resultSet.getString("product_id"),
                                        resultSet.getString("name"),
                                        resultSet.getInt("price"),
                                        resultSet.getInt("qty"),
                                        resultSet.getString("image")
                                );
                                items.add(product);
                            }

                            mRecyclerView.setAdapter(new ReportAdapter());
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAttach(String productIdStart, String productIdEnd) {
        fetchProduct(productIdStart, productIdEnd);
    }

    class ReportAdapter extends RecyclerView.Adapter<ReportHolder> {
        @NonNull
        @Override
        public ReportHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_report, parent, false);
            return new ReportHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ReportHolder holder, int position) {
            Product product = items.get(position);
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
            return items.size();
        }
    }

    class ReportHolder extends RecyclerView.ViewHolder {
        final ImageView ivImage;
        final TextView tvName;
        final TextView tvPrice;
        final TextView tvProductId;
        final TextView tvQty;

        public ReportHolder(@NonNull View itemView) {
            super(itemView);

            ivImage = (ImageView) itemView.findViewById(R.id.iv_image);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvPrice = (TextView) itemView.findViewById(R.id.tv_price);
            tvProductId = (TextView) itemView.findViewById(R.id.tv_product_id);
            tvQty = (TextView) itemView.findViewById(R.id.tv_qty);
        }
    }
}
