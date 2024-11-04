package com.example.loginproject.UI;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.loginproject.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity {

    // Variables para gestionar FirebaseAuth y Google SignIn
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInOptions gso;

    Button btnCerraSesion, btnEliminarCuenta;
    Button btnAcercaDe ;
    Button btnMainActivity2;
    Button btnPoliticasDe;
    private TextView userNombre, userEmail, userID;
    private CircleImageView userImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userNombre = findViewById(R.id.userNombre);
        userEmail = findViewById(R.id.userEmail);
        userID = findViewById(R.id.userId);
        userImg = findViewById(R.id.userImagen);
        btnCerraSesion = findViewById(R.id.btnLogout);
        btnEliminarCuenta = findViewById(R.id.btnEliminarCta);
        btnAcercaDe = findViewById(R.id.btnAcerca);
        btnMainActivity2 = findViewById(R.id.btnApps);
        btnPoliticasDe = findViewById(R.id.btnPoliticas);

        // Inicializar Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            // Mostrar datos del usuario
            userID.setText(currentUser.getUid());
            userNombre.setText(currentUser.getDisplayName());
            userEmail.setText(currentUser.getEmail());
            if (currentUser.getPhotoUrl() != null) {
                Glide.with(this).load(currentUser.getPhotoUrl()).into(userImg);
            } else {
                // Puedes establecer una imagen por defecto si no hay foto
                userImg.setImageResource(R.drawable.user1);
            }
        }
        btnAcercaDe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AcercaDeActivity.class);
               startActivity(intent);
            }
        });

        btnMainActivity2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                startActivity(intent);
            }
        });

        btnPoliticasDe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PoliticasDeActivity.class);
                startActivity(intent);
            }
        });
        // Configurar opciones de Google SignIn
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        // Configurar botón de cerrar sesión
        btnCerraSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

        // Configurar botón de eliminar cuenta
        btnEliminarCuenta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if (user != null) {
                    GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(getApplicationContext());

                    if (signInAccount != null) {
                        AuthCredential credential = GoogleAuthProvider.getCredential(signInAccount.getIdToken(), null);

                        if (credential != null) {
                            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        deleteUser(user);
                                    } else {
                                        Log.e("MainActivity", "Error al re-autenticar al usuario.", task.getException());
                                        Toast.makeText(getApplicationContext(), "Re-autenticación fallida. Intenta cerrar sesión e iniciar de nuevo.",
                                                Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        } else {
                            Log.e("MainActivity", "Error: No se pudo obtener las credenciales.");
                        }
                    } else {
                        Log.d("MainActivity", "Error: cuenta de usuario no encontrada.");
                        Toast.makeText(getApplicationContext(), "No se encontró cuenta de Google vinculada.", Toast.LENGTH_LONG).show();
                        final String email = user.getEmail();
                        mostrarDialogoEliminarCuenta(user, email);


                    }
                } else {
                    Log.d("MainActivity", "Error: usuario no autenticado.");
                    Toast.makeText(getApplicationContext(), "No hay usuario autenticado.", Toast.LENGTH_LONG).show();


                }
            }
        });
    }

    // Método para eliminar usuario
    // Método para eliminar al usuario
    private void mostrarDialogoEliminarCuenta(FirebaseUser user,String  email) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmar Eliminación de Cuenta");

        // Configurar el EditText para ingresar la contraseña
        final EditText inputPassword = new EditText(this);
        inputPassword.setHint("Ingresa tu contraseña");
        inputPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        // Añadir el EditText al Dialog
        builder.setView(inputPassword);

        // Configurar los botones
        builder.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String password = inputPassword.getText().toString().trim();
                if (!password.isEmpty()) {
                    AuthCredential credential = EmailAuthProvider.getCredential(email, password);
                    user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                deleteUser(user);
                            } else {
                                Log.e("MainActivity", "Error al re-autenticar al usuario.", task.getException());
                                Toast.makeText(getApplicationContext(), "Re-autenticación fallida. Asegúrate de que la contraseña sea correcta.",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "Por favor, ingresa tu contraseña.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Mostrar el Dialog
        builder.show();
    }

    private void deleteUser(FirebaseUser user) {
        user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d("MainActivity", "Usuario eliminado con éxito.");
                    signOut();
                    Toast.makeText(getApplicationContext(), "Cuenta eliminada exitosamente.", Toast.LENGTH_LONG).show();
                    // Aquí puedes redirigir al usuario a la pantalla de inicio de sesión u otra actividad
                } else {
                    Log.e("MainActivity", "Error al eliminar al usuario.", task.getException());
                    Toast.makeText(getApplicationContext(), "Error al eliminar la cuenta. Intenta nuevamente.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }



    // Método para cerrar sesión
    private void signOut() {
        mAuth.signOut();
        mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Intent loginActivity = new Intent(getApplicationContext(), loginActivity.class);
                loginActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(loginActivity);
                MainActivity.this.finish();
            }
        });
    }
}