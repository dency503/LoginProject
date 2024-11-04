package com.example.loginproject.UI;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.loginproject.R;
import com.example.loginproject.UI.adapters.ChildRecyclerAdapter;
import com.example.loginproject.UI.adapters.MainRecyclerAdapter;
import com.example.loginproject.UI.viewModels.BudgetVM;
import com.example.loginproject.databinding.ActivityMain2Binding;
import com.example.loginproject.models.Presupuesto;

import java.util.ArrayList;

public class MainActivity2 extends AppCompatActivity  implements ChildRecyclerAdapter.OnItemChildClickListener {
    private ActivityMain2Binding binding;
    private BudgetVM viewModel;
    private MainRecyclerAdapter mainRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMain2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        viewModel = new ViewModelProvider(this).get(BudgetVM.class);

        mainRecyclerAdapter = new MainRecyclerAdapter(new ArrayList<>(), MainActivity2.this);
        binding.mainRecyclerView.setAdapter(mainRecyclerAdapter);
        binding.mainRecyclerView.setHasFixedSize(true);

        viewModel.getBudgetLiveData().observe(this, budgets -> {
            mainRecyclerAdapter.setDataList(budgets);
        });

        binding.imgAgregar.setOnClickListener(v -> {
            AddBudget bottomSheet = new AddBudget();
            bottomSheet.show(getSupportFragmentManager(), "addBudgetFragment");
        });
    }


    @Override
    public void onItemChildClick(Presupuesto mainObject) {
        try {
            if (mainObject.isActivo()) {
                Intent intent = new Intent(MainActivity2.this, DetailBudget.class);
                intent.putExtra("mainBudget", mainObject);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Lista Completada", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error Lista ", Toast.LENGTH_SHORT).show();
        }
    }
}

