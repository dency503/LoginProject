package com.example.loginproject.UI;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.loginproject.R;
import com.example.loginproject.UI.adapters.DailyExpenseRecyclerAdapter;
import com.example.loginproject.UI.viewModels.DailyExpensesVM;
import com.example.loginproject.databinding.ActivityDetailBudgetBinding;
import com.example.loginproject.models.GastoDia;
import com.example.loginproject.models.Presupuesto;

import java.util.ArrayList;
import java.util.Objects;

public class DetailBudget extends AppCompatActivity {

    private ActivityDetailBudgetBinding binding;
    private DailyExpensesVM viewModel;
    private DailyExpenseRecyclerAdapter mainRecyclerAdapter;

    Presupuesto receivedObject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBudgetBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        receivedObject = (Presupuesto) intent.getSerializableExtra("mainBudget");
        if (receivedObject == null) {
            Toast.makeText(this, "No se recibió ningún presupuesto", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        resetDefaultValues();

        binding.edtCantidad.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                updatedTotal();
            }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });

        binding.edtPrecio.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                updatedTotal();
            }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });

        viewModel = new ViewModelProvider(this).get(DailyExpensesVM.class);
        mainRecyclerAdapter = new DailyExpenseRecyclerAdapter(new ArrayList<>());
        binding.rcvCompras.setAdapter(mainRecyclerAdapter);
        binding.rcvCompras.setHasFixedSize(true);

        viewModel.listenForExpensesChanges(Objects.requireNonNull(receivedObject).getId());
        viewModel.getDailyExpensesLiveData().observe(this, budgets -> {
            mainRecyclerAdapter.setDataList(budgets);
            rebuildAmountAvaliable();
        });

        binding.imvFinalizar.setOnClickListener(v -> {
            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
            dialogo1.setTitle("");
            dialogo1.setMessage("¿Desea finalizar el presupuesto?");
            dialogo1.setCancelable(false);
            dialogo1.setPositiveButton("Confirmar", (dialog, id) -> {
                viewModel.finalizeBudget(
                        receivedObject.getId(),
                        documentReference -> finish(),
                        e -> Toast.makeText(DetailBudget.this, "No se pudo finalizar el presupuesto", Toast.LENGTH_SHORT).show());
            });
            dialogo1.setNegativeButton("Cancelar", (dialog, id) -> {});
            dialogo1.show();
        });

        binding.btnGuardarDia.setOnClickListener(v -> {
            String precioText = binding.edtPrecio.getText().toString();
            String cantidadText = binding.edtCantidad.getText().toString();

            if (!precioText.isEmpty() && !cantidadText.isEmpty()) {
                double actualPrice = Double.parseDouble(precioText);
                int actualAmount = Integer.parseInt(cantidadText);

                GastoDia mObject = new GastoDia(
                        binding.edtArticulo.getText().toString(),
                        actualPrice,
                        actualAmount,
                        getSubtotal(actualPrice, actualAmount),
                        receivedObject.getId());

                viewModel.addDailyExpense(mObject,
                        documentReference -> {
                            resetDefaultValues();
                            rebuildAmountAvaliable();
                            Toast.makeText(this, "Su compra se guardó correctamente", Toast.LENGTH_SHORT).show();
                        },
                        e -> Toast.makeText(this, "No se pudo guardar la compra", Toast.LENGTH_SHORT).show());
            } else {
                Toast.makeText(this, "Por favor, complete ambos campos de cantidad y precio.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void resetDefaultValues() {
        binding.txvPActual.setText("$" + receivedObject.getMonto());
        binding.txvTActual.setText("$0");

        binding.edtArticulo.setText(null);
        binding.edtArticulo.clearFocus();

        binding.edtPrecio.setText(null);
        binding.edtPrecio.clearFocus();

        binding.edtCantidad.setText(null);
        binding.edtCantidad.clearFocus();
    }

    private double getSubtotal(double price, int quantity) {
        return price * quantity;
    }

    private void updatedTotal() {
        String cantidadText = binding.edtCantidad.getText().toString();
        String precioText = binding.edtPrecio.getText().toString();

        if (!cantidadText.isEmpty() && !precioText.isEmpty()) {
            double actualPrice = Double.parseDouble(precioText);
            int actualAmount = Integer.parseInt(cantidadText);
            binding.txvTActual.setText("$" + getSubtotal(actualPrice, actualAmount));
        } else {
            binding.txvTActual.setText("$0");
        }
    }

    private void rebuildAmountAvaliable() {
        double remainingAmount = receivedObject.getMonto() - mainRecyclerAdapter.getActualSubTotalAll();
        binding.txvPActual.setText("$" + String.format("%.2f", remainingAmount));
    }
}
