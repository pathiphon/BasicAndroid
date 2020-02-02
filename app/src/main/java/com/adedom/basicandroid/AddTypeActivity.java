package com.adedom.basicandroid;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.adedom.library.Dru;
import com.adedom.library.ExecuteQuery;
import com.adedom.library.ExecuteUpdate;

import java.sql.ResultSet;
import java.sql.SQLException;

public class AddTypeActivity extends AppCompatActivity {

    private EditText mEtTypeId;
    private EditText mEtTypeName;
    private Button mBtCancel;
    private Button mBtOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_type);

        mEtTypeId = (EditText) findViewById(R.id.et_type_id);
        mEtTypeName = (EditText) findViewById(R.id.et_name);
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
                addType();
            }
        });

        mEtTypeId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String sql = "SELECT * FROM producttype WHERE ProductTypeID = '" + charSequence + "'";
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

    private void addType() {
        String typeId = mEtTypeId.getText().toString().trim();
        String typeName = mEtTypeName.getText().toString().trim();
        String sql = "INSERT INTO producttype VALUES ('" + typeId + "','" + typeName + "')";
        Dru.connection(ConnectDB.getConnection())
                .execute(sql)
                .commit(new ExecuteUpdate() {
                    @Override
                    public void onComplete() {
                        finish();
                    }
                });
    }
}
