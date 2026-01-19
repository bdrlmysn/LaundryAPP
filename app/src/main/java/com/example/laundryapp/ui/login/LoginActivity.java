package com.example.laundryapp.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.laundryapp.R;
import com.example.laundryapp.data.db.dao.UserDao;
import com.example.laundryapp.data.db.model.User;
import com.example.laundryapp.ui.home.HomeActivity;
import com.example.laundryapp.util.SessionManager;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername, etPassword;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SessionManager session = new SessionManager(this);
        if (session.isLoggedIn()) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);

        UserDao userDao = new UserDao(this);

        btnLogin.setOnClickListener(v -> {
            String user = etUsername.getText().toString().trim();
            String pass = etPassword.getText().toString().trim();

            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Username dan Password wajib diisi", Toast.LENGTH_SHORT).show();
                return;
            }

            User u = userDao.login(user, pass);
            if (u == null) {
                Toast.makeText(this, "Login gagal. Cek username/password.", Toast.LENGTH_SHORT).show();
                return;
            }

            session.login(u);
            Toast.makeText(this, "Login berhasil (" + u.role + ")", Toast.LENGTH_SHORT).show();

            startActivity(new Intent(this, HomeActivity.class));
            finish();
        });
    }
}
