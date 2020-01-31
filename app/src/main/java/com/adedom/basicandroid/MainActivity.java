package com.adedom.basicandroid;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adedom.basicandroid.util.Utility;
import com.adedom.library.Dru;
import com.adedom.library.ExecuteQuery;
import com.bumptech.glide.Glide;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ArrayList<Product> items;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ConnectDB.getConnection() == null) {
            Dru.failed(getBaseContext());
        } else {
            Dru.completed(getBaseContext());
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("รายการสินค้า");
        setSupportActionBar(toolbar);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getBaseContext(), 2));

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

                            mRecyclerView.setAdapter(new MainAdapters());
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.management) {
            startActivity(new Intent(getBaseContext(), ProductActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    class MainAdapters extends RecyclerView.Adapter<MainHolder> {
        @NonNull
        @Override
        public MainHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_product, parent, false);
            return new MainHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MainHolder holder, int position) {
            Product product = items.get(position);
            holder.tvName.setText(product.getName());
            holder.tvPrice.setText(Utility.toPrice(product.getPrice()));

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

    class MainHolder extends RecyclerView.ViewHolder {
        final ImageView ivImage;
        final TextView tvName;
        final TextView tvPrice;

        public MainHolder(@NonNull View itemView) {
            super(itemView);

            ivImage = (ImageView) itemView.findViewById(R.id.iv_image);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            tvPrice = (TextView) itemView.findViewById(R.id.tv_price);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Product product = items.get(getAdapterPosition());

                    Bundle bundle = new Bundle();
                    bundle.putParcelable("product", product);

                    ProductDialog dialog = new ProductDialog();
                    dialog.setArguments(bundle);
                    dialog.show(getSupportFragmentManager(), null);
                }
            });
        }
    }
}
