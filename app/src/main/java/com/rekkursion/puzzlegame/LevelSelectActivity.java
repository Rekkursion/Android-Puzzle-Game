package com.rekkursion.puzzlegame;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.transition.Slide;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import org.w3c.dom.Text;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class LevelSelectActivity extends AppCompatActivity {
    private SeekBar skbDifficultiesSelect;
    private TextView txtvSelectedDifficulty;
    private Button btnGetImageSource;
    private TextView txtvSelectedImageFilename;
    private ImageView imgvPreviewSelectedImage;
    private ImageView imgvHintLinesOfPreviewSelectedImage;
    private Spinner spnSelectScaleType;
    private RadioButton rdbSelectGamingModeSlidingPuzzle;
    private RadioButton rdbSelectGamingModeTraditionalPuzzle;
    private TextView txtvStartButtonAtLevelSelect;
    private TextView txtvStartButtonShadowAtLevelSelect;
    private TextView txtvShowSizeOfOriginalSelectedImage;
    private TextView txtvShowImageTooBigWarning;
    private TextView txtvShowScalingRecommendationBecauseOfHighDifiiculty;
    private ImageView imgvBackToMenuFromLevelSelect;

    private final int REQ_CODE_GET_IMAGE_FROM_EXTERNAL_STORAGE = 10037;
    private final int REQ_CODE_PERMISSION_TO_READ_EXTERNAL_STORAGE = 63;
    private final int REQ_CODE_TO_GAME_ACTIVITY = 5275;

    private final int PROGRESS_AND_REAL_DIFFICULTY_OFFSET = 3;

    private View.OnTouchListener startTextViewAsButtonOnTouchListener = (view, motionEvent) -> {
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                Animation animOptionPressed = AnimationUtils.loadAnimation(LevelSelectActivity.this, R.anim.scale_animation_view_pressed);
                animOptionPressed.setFillAfter(true);
                view.startAnimation(animOptionPressed);
                return true;

            case MotionEvent.ACTION_UP:
                txtvStartButtonAtLevelSelect.setEnabled(false);

                Animation animOptionUnpressed = AnimationUtils.loadAnimation(LevelSelectActivity.this, R.anim.scale_animation_view_unpressed);
                animOptionUnpressed.setFillAfter(true);
                animOptionUnpressed.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {}

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        view.performClick();
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {}
                });
                view.startAnimation(animOptionUnpressed);
                return true;

            default: return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // set transition animations (entering level-select)
        getWindow().setEnterTransition(TransitionInflater.from(this).inflateTransition(R.transition.slide_end));
        getWindow().setReturnTransition(TransitionInflater.from(this).inflateTransition(R.transition.slide_end));

        // set transition animations (returning back)
        getWindow().setExitTransition(TransitionInflater.from(this).inflateTransition(R.transition.slide_start));
        getWindow().setReenterTransition(TransitionInflater.from(this).inflateTransition(R.transition.slide_start));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_select);

        initViews();
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

        txtvStartButtonAtLevelSelect.setEnabled(true);

        super.onResume();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQ_CODE_PERMISSION_TO_READ_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    getImageFromExternalStorage();
                else
                    Toast.makeText(LevelSelectActivity.this, "Cannot get image source without permissions.", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            // back from game-activity
            case REQ_CODE_TO_GAME_ACTIVITY:
                if (resultCode == BackToWhere.BACK_TO_MENU.ordinal())
                    finish();
                break;

            // get image from external storage
            case REQ_CODE_GET_IMAGE_FROM_EXTERNAL_STORAGE:
                Uri imgUri = null;
                String imgFilename = "";
                Bitmap imgBitmap = null;
                try {
                    imgUri = convertUri(data.getData());
                    imgFilename = imgUri.getLastPathSegment();
                    imgBitmap = BitmapFactory.decodeFile(imgUri.getPath());

                    imgvPreviewSelectedImage.setImageBitmap(imgBitmap);
                    imgvPreviewSelectedImage.setBackgroundResource(android.R.color.background_dark);
                    txtvSelectedImageFilename.setText(imgFilename);
                    txtvShowSizeOfOriginalSelectedImage.setText(String.valueOf(imgBitmap.getWidth()) + " x " + String.valueOf(imgBitmap.getHeight()));

                    if ((long) imgBitmap.getWidth() * (long) imgBitmap.getHeight() >= GameManager.IMAGE_TOO_BIG_WARNING_THRESHOLD)
                        txtvShowImageTooBigWarning.setVisibility(View.VISIBLE);
                    else
                        txtvShowImageTooBigWarning.setVisibility(View.GONE);

                    // show hint lines on preview image-view
                    Bitmap linesBitmap = ImageProcessFactory.getHintLinesBitmap(imgvPreviewSelectedImage.getLayoutParams().width, imgvPreviewSelectedImage.getLayoutParams().height, skbDifficultiesSelect.getProgress() + PROGRESS_AND_REAL_DIFFICULTY_OFFSET);
                    imgvHintLinesOfPreviewSelectedImage.setVisibility(View.VISIBLE);
                    imgvHintLinesOfPreviewSelectedImage.setImageBitmap(linesBitmap);

                    // add the animation set to the button shadow
                    txtvStartButtonShadowAtLevelSelect.setEnabled(true);
                    txtvStartButtonShadowAtLevelSelect.startAnimation(MainActivity.getShadowEffectAnimationSet(LevelSelectActivity.this));

                } catch (NullPointerException e) {
                    e.printStackTrace();
                    Toast.makeText(this, "Error happened when getting the file.", Toast.LENGTH_SHORT).show();
                } finally {
                    imgUri = null;
                }

                break;
        }
    }

    private void initViews() {
        skbDifficultiesSelect = findViewById(R.id.skb_difficulties_select);
        txtvSelectedDifficulty = findViewById(R.id.txtv_selected_difficulty);
        btnGetImageSource = findViewById(R.id.btn_get_img_source);
        txtvSelectedImageFilename = findViewById(R.id.txtv_selected_image_filename);
        imgvPreviewSelectedImage = findViewById(R.id.imgv_preview_selected_image);
        imgvHintLinesOfPreviewSelectedImage = findViewById(R.id.imgv_hint_lines_of_preview_selected_image);
        spnSelectScaleType = findViewById(R.id.spn_select_scale_type);
        rdbSelectGamingModeSlidingPuzzle = findViewById(R.id.rdb_select_gaming_mode_sliding_puzzle);
        rdbSelectGamingModeTraditionalPuzzle = findViewById(R.id.rdb_select_gaming_mode_traditional_puzzle);
        txtvStartButtonAtLevelSelect = findViewById(R.id.txtv_start_button_at_level_select);
        txtvStartButtonShadowAtLevelSelect = findViewById(R.id.txtv_start_button_shadow_at_level_select);
        txtvShowSizeOfOriginalSelectedImage = findViewById(R.id.txtv_show_size_of_original_selected_image);
        txtvShowImageTooBigWarning = findViewById(R.id.txtv_show_image_too_big_warning);
        txtvShowScalingRecommendationBecauseOfHighDifiiculty = findViewById(R.id.txtv_show_scaling_recommendation_because_of_high_difficulty);
        imgvBackToMenuFromLevelSelect = findViewById(R.id.imgv_back_to_menu_from_level_select);

        // discover the warnings
        txtvShowImageTooBigWarning.setVisibility(View.GONE);
        txtvShowScalingRecommendationBecauseOfHighDifiiculty.setVisibility(View.GONE);

        // disable shadow at first
        txtvStartButtonShadowAtLevelSelect.setEnabled(false);

        // initial scale type for preview-selected-image which is FIT_CENTER
        imgvPreviewSelectedImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
        spnSelectScaleType.setSelection(0);

        // on-seek-bar-change-listener for selecting difficulties
        skbDifficultiesSelect.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean isFromUser) {
                // change the text of text-view for showing difficulty
                int realDiff = progress + PROGRESS_AND_REAL_DIFFICULTY_OFFSET;
                String valueString = String.valueOf(realDiff);
                txtvSelectedDifficulty.setText(valueString + " × " + valueString);

                // show/discover the recommendation for high difficulty
                txtvShowScalingRecommendationBecauseOfHighDifiiculty.setVisibility(realDiff >= 5 && imgvPreviewSelectedImage.getScaleType() != ImageView.ScaleType.FIT_XY ? View.VISIBLE : View.GONE);

                // show hint lines on preview image-view
                Bitmap linesBitmap = ImageProcessFactory.getHintLinesBitmap(imgvPreviewSelectedImage.getLayoutParams().width, imgvPreviewSelectedImage.getLayoutParams().height, realDiff);
                imgvHintLinesOfPreviewSelectedImage.setImageBitmap(linesBitmap);

                SoundPoolManager.getInstance().play("se_maoudamashii_click.mp3");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        // on-click-listener for loading image
        btnGetImageSource.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                    getImageFromExternalStorage();
                else
                    requestPermissions(new String[] { Manifest.permission.READ_EXTERNAL_STORAGE }, REQ_CODE_PERMISSION_TO_READ_EXTERNAL_STORAGE);

                BackgroundMusicManager.shouldStopPlayingWhenLeaving = false;
                SoundPoolManager.getInstance().play("se_maoudamashii_click.mp3");
            }
        });

        // on-item-selected-listener for selecting scale types
        spnSelectScaleType.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            Map<String, ImageView.ScaleType> scaleTypesDict = new HashMap<>();
            boolean loading = true;

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if (scaleTypesDict.isEmpty()) {
//                    scaleTypesDict.put("center", ImageView.ScaleType.CENTER);
//                    scaleTypesDict.put("centerCrop", ImageView.ScaleType.CENTER_CROP);
//                    scaleTypesDict.put("centerInside", ImageView.ScaleType.CENTER_INSIDE);
                    scaleTypesDict.put("Center", ImageView.ScaleType.FIT_CENTER);
                    scaleTypesDict.put("End", ImageView.ScaleType.FIT_END);
                    scaleTypesDict.put("Start", ImageView.ScaleType.FIT_START);
                    scaleTypesDict.put("Scaling", ImageView.ScaleType.FIT_XY);
                }

                String scaleTypeString = adapterView.getSelectedItem().toString();
                imgvPreviewSelectedImage.setScaleType(scaleTypesDict.getOrDefault(scaleTypeString, ImageView.ScaleType.CENTER));

                // show/discover the scaling recommendation because of high difficulty
                if (scaleTypeString.equals("Scaling"))
                    txtvShowScalingRecommendationBecauseOfHighDifiiculty.setVisibility(View.GONE);
                else {
                    if (skbDifficultiesSelect.getProgress() + PROGRESS_AND_REAL_DIFFICULTY_OFFSET >= 5)
                        txtvShowScalingRecommendationBecauseOfHighDifiiculty.setVisibility(View.VISIBLE);
                    else
                        txtvShowScalingRecommendationBecauseOfHighDifiiculty.setVisibility(View.GONE);
                }

                if (!loading)
                    SoundPoolManager.getInstance().play("se_maoudamashii_click.mp3");
                loading = false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // on-click-listener for starting the game
        txtvStartButtonAtLevelSelect.setOnTouchListener(startTextViewAsButtonOnTouchListener);
        txtvStartButtonAtLevelSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (imgvPreviewSelectedImage.getDrawable() == null ||
                        ((BitmapDrawable) imgvPreviewSelectedImage.getDrawable()).getBitmap() == null) {
                    Snackbar.make(view, R.string.str_please_choose_an_image, Snackbar.LENGTH_SHORT).show();
                    return;
                }

                Bitmap origImageBitmap = ((BitmapDrawable) imgvPreviewSelectedImage.getDrawable()).getBitmap();
                int selectedDifficulty = skbDifficultiesSelect.getProgress() + PROGRESS_AND_REAL_DIFFICULTY_OFFSET;
                GamingModeEnum gamingMode = rdbSelectGamingModeSlidingPuzzle.isChecked() ? GamingModeEnum.SLIDING_MODE : GamingModeEnum.TRADITIONAL_MODE;

                GameManager.getInstance().originalImageBitmap = origImageBitmap;
                GameManager.getInstance().difficulty = selectedDifficulty;
                GameManager.getInstance().gamingMode = gamingMode;
                GameManager.getInstance().selectedScaleTypeString = spnSelectScaleType.getSelectedItem().toString();

                SoundPoolManager.getInstance().play("se_maoudamashii_click_entering.mp3");

                BackgroundMusicManager.shouldStopPlayingWhenLeaving = false;
                BackgroundMusicManager.getInstance(LevelSelectActivity.this).stop();
                BackgroundMusicManager.getInstance(LevelSelectActivity.this).play(BackgroundMusicManager.getInstance(LevelSelectActivity.this).getAssetsFileByDirectoryRandomly("musics" + File.separator + "gaming_theme"), true);

                Intent intentToGameActivity = new Intent(LevelSelectActivity.this, GameActivity.class);
                startActivityForResult(intentToGameActivity, REQ_CODE_TO_GAME_ACTIVITY, ActivityOptions.makeSceneTransitionAnimation(LevelSelectActivity.this).toBundle());
            }
        });

        // back to menu
        imgvBackToMenuFromLevelSelect.setOnClickListener(view -> {
            BackgroundMusicManager.shouldStopPlayingWhenLeaving = false;
            SoundPoolManager.getInstance().play("se_maoudamashii_click_leaving.mp3");
            finishAfterTransition();
        });

        // show full filename of selected image
        txtvSelectedImageFilename.setOnClickListener(view -> {
//            new AlertDialog.Builder(LevelSelectActivity.this)
//                    .setMessage(txtvSelectedImageFilename.getText().toString())
//                    .setNeutralButton("OK", null)
//                    .show();
            Toast.makeText(LevelSelectActivity.this, txtvSelectedImageFilename.getText().toString(), Toast.LENGTH_SHORT).show();
        });

        // init the size of imgv-hint-lines-of-preview-selected-image
        imgvHintLinesOfPreviewSelectedImage.getLayoutParams().width = imgvPreviewSelectedImage.getLayoutParams().width;
        imgvHintLinesOfPreviewSelectedImage.getLayoutParams().height = imgvPreviewSelectedImage.getLayoutParams().height;

        // show/hide hint lines on preview image-view
        imgvHintLinesOfPreviewSelectedImage.setOnClickListener(view -> imgvHintLinesOfPreviewSelectedImage.setVisibility(View.GONE));
        imgvPreviewSelectedImage.setOnClickListener(view -> imgvHintLinesOfPreviewSelectedImage.setVisibility(View.VISIBLE));
    }

    private void getImageFromExternalStorage() {
        Intent intentGetImage = new Intent(Intent.ACTION_GET_CONTENT);
        intentGetImage.setType("image/*");
        startActivityForResult(Intent.createChooser(intentGetImage, "Get image"), REQ_CODE_GET_IMAGE_FROM_EXTERNAL_STORAGE);
    }

    // convert uri from content uri to file uri
    private Uri convertUri(Uri uri) throws NullPointerException {
        if(uri == null)
            throw new NullPointerException();

        Uri newUri;
        if(uri.toString().substring(0, 7).equals("content")) {
            String[] colName = { MediaStore.MediaColumns.DATA };
            Cursor cursor = getContentResolver().query(uri, colName, null, null, null);

            cursor.moveToFirst();
            newUri = Uri.parse("file://" + cursor.getString(0));

            cursor.close();
        }
        else
            newUri = uri;

        return newUri;
    }
}
