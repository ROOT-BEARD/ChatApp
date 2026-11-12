package com.example.chatapp.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import com.example.chatapp.utilities.PreferenceManager;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.chatapp.R;
import com.example.chatapp.utilities.Constants;
import com.example.chatapp.databinding.ActivitySignUpBinding;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;


public class SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding binding;
    private PreferenceManager preferenceManager;

    private String encodedImage;

    /**
     * runs on creation, sets the binding, sets the content view,
     * creates a new PreferenceManager, and sets the listeners
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.enableEdgeToEdge(getWindow());
        setContentView(R.layout.activity_sign_up);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());

        setListeners();
    }

    /**
     * sets the listeners for the Sign-in button, and the sign-up button.
     * sets up an on click listener for the profile picture that allows the user
     * to select an image from their device.
     *
     */
    private void setListeners() {
        binding.buttonSignIn.setOnClickListener(v -> onBackPressed());

        binding.buttonSignUp.setOnClickListener(v -> {
            if(isValidToSignUp()){
                signUp();
            }
        });

        binding.layoutImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(intent);
        });

    }


    /**
     * creates a toast message with the passed in message
     *
     * @param message the message to display one the toast message
     */
    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    /**
     * handles sign up
     */
    private void signUp(){
        //sets loading to true
        loading(true);



        /*create firebase reference, and a user hashmap, and then adds
        * the text input information to the user hashmap*/
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        HashMap<String, String> user = new HashMap<>();
        user.put(Constants.KEY_NAME, binding.inputName.getText().toString());
        user.put(Constants.KEY_EMAIL, binding.inputEmail.getText().toString());
        user.put(Constants.KEY_PASSWORD, binding.inputPassword.getText().toString());
        user.put(Constants.KEY_IMAGE, encodedImage);

        /*add the user to the Users table, if successful it signs the user in and starts the MainActivity, if not
        it shows an error toast message*/
        db.collection(Constants.KEY_COLLECTION_USERS)
                .add(user)
                .addOnSuccessListener(documentReference -> {
                    loading(false);

                    preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                    preferenceManager.putString(Constants.KEY_NAME,binding.inputName.getText().toString());
                    preferenceManager.putString(Constants.KEY_IMAGE,encodedImage);

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);



                }).addOnFailureListener(exception -> {
                    loading(false);
                    showToast(exception.getMessage());
                });
    }

    /**
     * converts a bitmap image into a string
     *
     * @param bitmap the original bitmap image
     * @return return the image as a Base64 string
     */
    private String encodedImage(Bitmap bitmap){
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight()*previewWidth / bitmap.getWidth();

        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);

        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes,Base64.DEFAULT);
    }

    /**
     * allows the user to pick an image from their device, if successful, it gets a Uri image
     * from the data, and then tries to create a bitmap from it and then encode it into a string.
     * otherwise it prints the FileNotFoundException
     */
    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK){
                    Uri imageUrl = result.getData().getData();
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(imageUrl);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

                        binding.imageProfile.setImageBitmap(bitmap);
                        binding.textAddImage.setVisibility(View.GONE);
                        encodedImage = encodedImage(bitmap);

                    } catch(FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
    );

    /**
     * checks to see if the information that the user as inputed is valid to
     * attempt to sign up or not.
     *
     * @return true if the sign-up information is valid, and false if not
     */
    private Boolean isValidToSignUp(){
        if(encodedImage == null){
            showToast("Please Select your Image");
            return false;
        }
        else if(binding.inputName.getText().toString().trim().isEmpty()){
            showToast("Please Enter your Name");
            return false;
        } else if(binding.inputEmail.getText().toString().trim().isEmpty()){
            showToast("Please Enter your Email");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.getText().toString()).matches()) {
            showToast("Please Enter Valid Email");
            return false;
        } else if(binding.inputPassword.getText().toString().trim().isEmpty()){
            showToast("Please Enter Password");
            return false;
        }else if(binding.inputEmail.getText().toString().trim().isEmpty()){
            showToast("Please Confirm your Password");
            return false;
        }else if(!binding.inputPassword.getText().toString().equals(binding.inputComfirmPassword.getText().toString())){
            showToast("Password & Confirm Password must be the same");
            return false;
        }

        return true;
    }


    /**
     * changes the visibility of the sign-in button and the progress loading bar
     * depending on the passed in boolean
     *
     * @param isLoading whether the button is loading
     */
    private void loading (Boolean isLoading){
        /*if the bar is loading, hide the button and show the loading progress bar,
         * otherwise show the button and hide the bar*/
        if(isLoading){
            binding.buttonSignUp.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.buttonSignUp.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }
}