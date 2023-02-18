package com.luck_art.go4food;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.luck_art.go4food.databinding.ActivityMainBinding;

import java.util.Arrays;
import java.util.List;

public  class MainActivity extends AppCompatActivity {


	private ActivityMainBinding binding;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initBinding();
		setupListeners();
	}

	private final ActivityResultLauncher<Intent> signInLauncher = registerForActivityResult(
			new FirebaseAuthUIActivityResultContract(),
			new ActivityResultCallback<FirebaseAuthUIAuthenticationResult>() {
				@Override
				public void onActivityResult(FirebaseAuthUIAuthenticationResult result) {
					onSignInResult(result);
				}
			}
	);

	/*private void updateLoginButton(){
		binding.buttonMail.setText(userManager.isCurrentUserLogged() ? getString(R.string.butt) : getString(R.string.button_login_text_not_logged));
	}*/

	private void setupListeners(){
		// Login Button
		binding.buttonSignIn.setOnClickListener(view -> {
			startSignInActivity();
		});
	}

	private void initBinding(){
		binding = ActivityMainBinding.inflate(getLayoutInflater());
		View view = binding.getRoot();
		setContentView(view);
	}



	private void startSignInActivity(){

		// Choose authentication providers
		List<AuthUI.IdpConfig> providers =
				Arrays.asList(
						new AuthUI.IdpConfig.GoogleBuilder().build(),
						new AuthUI.IdpConfig.FacebookBuilder().build(),
						new AuthUI.IdpConfig.TwitterBuilder().build());
		// Launch the activity
				Intent signInIntent = AuthUI.getInstance()
						.createSignInIntentBuilder()
						.setTheme(R.style.Theme_Go4food)
						.setAvailableProviders(providers)
						.setIsSmartLockEnabled(false, true)
						.setLogo(R.drawable.background_connect)
						.build();
				signInLauncher.launch(signInIntent);
	}

	private void showSnackBar( String message){
		Snackbar.make(binding.mainLayout, message, Snackbar.LENGTH_SHORT).show();
	}

	private void onSignInResult(FirebaseAuthUIAuthenticationResult result){
		IdpResponse response = result.getIdpResponse();
		if (result.getResultCode() == RESULT_OK) {
			// Successfully
			FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
		} else {
				// ERRORS
				if (response == null) {
					showSnackBar(getString(R.string.error_authentication_canceled));
				} else if (response.getError()!= null) {
					if(response.getError().getErrorCode() == ErrorCodes.NO_NETWORK){
						showSnackBar(getString(R.string.error_no_internet));
					} else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
						showSnackBar(getString(R.string.error_unknown_error));
					}
				}
			}
		}
	}