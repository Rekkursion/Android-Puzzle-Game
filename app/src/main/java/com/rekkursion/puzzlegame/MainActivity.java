package com.rekkursion.puzzlegame;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {
    private final int REQ_CODE_PERMISSION_VIBRATE = 128128;
    private final int REQ_CODE_GOOGLE_SIGN_IN = 196;
    private boolean firstClicked = false;

    public final static long TRANS_ANIM_DURA = 300L;

    private RelativeLayout rlyBodyAtMainActivity;
    private TextView txtvStartButtonAtMainActivity;
    private TextView txtvStartButtonShadowAtMainActivity;
    private ImageView imgvPuzzleGameTitleAtMainActivity;
    private TextView txtvMaoudamashii;

    private Button signInButton;
    private Button signOutButton;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInAccount mGoogleSignInAccount;

    private Animation animScaleLittleWithBouncing;
    private Animation animScaleLargeWithOvershooting;
    private int puzzleGameTitleAnimationStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // set transition animations (returning back)
        getWindow().setExitTransition(TransitionInflater.from(this).inflateTransition(R.transition.slide_start));
        getWindow().setReenterTransition(TransitionInflater.from(this).inflateTransition(R.transition.slide_start));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // request the usage of vibration
        if (checkSelfPermission(Manifest.permission.VIBRATE) != PackageManager.PERMISSION_GRANTED)
            requestPermissions(new String[] { Manifest.permission.VIBRATE }, REQ_CODE_PERMISSION_VIBRATE);

//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestEmail()
//                .requestServerAuthCode(getString(R.string.web_client_id))
//                .build();
//        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        initAnimations();
        initViews();
        initSounds();

        // add the animation set to the button shadow
        txtvStartButtonShadowAtMainActivity.startAnimation(getShadowEffectAnimationSet(MainActivity.this));

        // play bgm
        BackgroundMusicManager.getInstance(this).play("musics" + File.separator + "game_maoudamashii_main_theme.mp3", true);
    }

    @Override
    protected void onPause() {
        if (BackgroundMusicManager.shouldStopPlayingWhenLeaving)
            BackgroundMusicManager.getInstance(this).pause();

        super.onPause();
    }

    @Override
    protected void onResume() {
        if (BackgroundMusicManager.shouldStopPlayingWhenLeaving)
            BackgroundMusicManager.getInstance(this).resume();
        BackgroundMusicManager.shouldStopPlayingWhenLeaving = true;

        firstClicked = false;

        super.onResume();
    }

    @Override
    protected void onDestroy() {
        BackgroundMusicManager.getInstance(this).endAndRelease();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (!firstClicked) {
            Snackbar.make(rlyBodyAtMainActivity, R.string.str_press_again_to_leave, Snackbar.LENGTH_SHORT).show();
            firstClicked = true;
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_CODE_GOOGLE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void initSounds() {
        try {
            List<String> audioList = Arrays.stream(getAssets().list(SoundPoolManager.SOUND_FILES_ROOT_PATH)).collect(Collectors.toList());
            SoundPoolManager.getInstance().initSoundPool(MainActivity.this, audioList);
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "Error happened when getting sound files.", Toast.LENGTH_SHORT).show();
        }
    }

    private void initViews() {
        rlyBodyAtMainActivity = findViewById(R.id.rly_body_at_main_activity);
        txtvStartButtonAtMainActivity = findViewById(R.id.txtv_start_button_at_main_activity);
        txtvStartButtonShadowAtMainActivity = findViewById(R.id.txtv_start_button_shadow_at_main_activity);
        imgvPuzzleGameTitleAtMainActivity = findViewById(R.id.imgv_puzzle_game_title_at_main_activity);
        txtvMaoudamashii = findViewById(R.id.txtv_maoudamashii);
        signInButton = findViewById(R.id.btn_google_log_in);
        signOutButton = findViewById(R.id.btn_google_log_out);

        txtvStartButtonAtMainActivity.setOnClickListener(view -> {
            Animation anim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.scale_animation_view_pressed);
            anim.setFillAfter(false);
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    goToMenuActivity();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
            txtvStartButtonAtMainActivity.startAnimation(anim);

            SoundPoolManager.getInstance().play("se_maoudamashii_click_entering.mp3");
        });

        // set on-click-listener for animations
        imgvPuzzleGameTitleAtMainActivity.setOnClickListener(view -> {
            firstClicked = false;

            if (puzzleGameTitleAnimationStatus == 0) {
                imgvPuzzleGameTitleAtMainActivity.startAnimation(animScaleLittleWithBouncing);
                puzzleGameTitleAnimationStatus = 1;
                SoundPoolManager.getInstance().play("se_maoudamashii_element_thunder04.mp3", 0, 1.2F);
            } else if (puzzleGameTitleAnimationStatus == 1) {
                imgvPuzzleGameTitleAtMainActivity.startAnimation(animScaleLargeWithOvershooting);
                puzzleGameTitleAnimationStatus = 0;
                SoundPoolManager.getInstance().play("se_maoudamashii_onepoint09.mp3", 0, 1.2F);
            }
        });

        // set on-touch-listener for maoudamashii
        txtvMaoudamashii.setOnTouchListener(new View.OnTouchListener() {
            float currentX = 0.0F, currentY = 0.0F;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        currentX = motionEvent.getX();
                        currentY = motionEvent.getY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        view.setTranslationX(view.getTranslationX() + (motionEvent.getX() - currentX));
                        view.setTranslationY(view.getTranslationY() + (motionEvent.getY() - currentY));
                        break;
                }

                return true;
            }
        });

        // google sign in button
        signInButton.setOnClickListener(view -> googleSignIn());

        // google sign out button
        signOutButton.setOnClickListener(view -> googleSignOut());
    }

    private void initAnimations() {
        puzzleGameTitleAnimationStatus = 0;

        animScaleLittleWithBouncing = new ScaleAnimation(1.0F, 0.5F, 1.0F, 0.5F, Animation.RELATIVE_TO_SELF, 0.5F, Animation.RELATIVE_TO_SELF, 0.5F);
        animScaleLittleWithBouncing.setDuration(1500L);
        animScaleLittleWithBouncing.setFillAfter(true);
        animScaleLittleWithBouncing.setInterpolator(new BounceInterpolator());

        animScaleLargeWithOvershooting = new ScaleAnimation(0.5F, 1.0F, 0.5F, 1.0F, Animation.RELATIVE_TO_SELF, 0.5F, Animation.RELATIVE_TO_SELF, 0.5F);
        animScaleLargeWithOvershooting.setDuration(200L);
        animScaleLargeWithOvershooting.setFillAfter(true);
        animScaleLargeWithOvershooting.setInterpolator(new OvershootInterpolator());
    }

    public static AnimationSet getShadowEffectAnimationSet(Context context) {
        // create shadow animation set:

        // get shadow animations
        Animation scaleLargeAnim = AnimationUtils.loadAnimation(context, R.anim.scale_large_and_turn_init_immediately);
        Animation fadeOutAnim = AnimationUtils.loadAnimation(context, R.anim.fade_out_and_turn_init_immediately);

        // build the animation set
        AnimationSet animSetShadowEffect = new AnimationSet(true);
        animSetShadowEffect.addAnimation(scaleLargeAnim);
        animSetShadowEffect.addAnimation(fadeOutAnim);

        return animSetShadowEffect;
    }

    private void goToMenuActivity() {
        BackgroundMusicManager.shouldStopPlayingWhenLeaving = false;
        Intent toMenuActivityIntent = new Intent(MainActivity.this, MenuActivity.class);
        startActivity(toMenuActivityIntent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
    }

    private void googleSignIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, REQ_CODE_GOOGLE_SIGN_IN);
    }

    private void googleSignOut() {
        if (mGoogleSignInClient != null) {
            mGoogleSignInClient.signOut();
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            mGoogleSignInAccount = completedTask.getResult(ApiException.class);
            Toast.makeText(this, "goodo", Toast.LENGTH_SHORT).show();

            // Signed in successfully, show authenticated UI.
            //updateUI(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.e("sign-in", "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(this, "Google login failed.", Toast.LENGTH_SHORT).show();
            //updateUI(null);
        }
    }
}
