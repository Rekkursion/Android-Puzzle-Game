package com.rekkursion.puzzlegame;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class LevelSelectActivity extends AppCompatActivity {
    private SeekBar skbDifficultiesSelect;
    private TextView txtvSelectedDifficulty;
    private Button btnGetImageSource;
    private TextView txtvSelectedImageFilename;
    private ImageView imgvPreviewSelectedImage;
    private Spinner spnSelectScaleType;
    private RadioButton rdbSelectGamingModeSlidingPuzzle;
    private RadioButton rdbSelectGamingModeTraditionalPuzzle;
    private Button btnStartAtLevelSelect;

    private final int REQ_CODE_GET_IMAGE_FROM_EXTERNAL_STORAGE = 10037;
    private final int REQ_CODE_PERMISSION_TO_READ_EXTERNAL_STORAGE = 63;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_select);

        initViews();
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
        spnSelectScaleType = findViewById(R.id.spn_select_scale_type);
        rdbSelectGamingModeSlidingPuzzle = findViewById(R.id.rdb_select_gaming_mode_sliding_puzzle);
        rdbSelectGamingModeTraditionalPuzzle = findViewById(R.id.rdb_select_gaming_mode_traditional_puzzle);
        btnStartAtLevelSelect = findViewById(R.id.btn_start_at_level_select);

        // initial scale type for preview-selected-image which is FIT_CENTER
        imgvPreviewSelectedImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
        spnSelectScaleType.setSelection(3);

        // on-seek-bar-change-listener for selecting difficulties
        skbDifficultiesSelect.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean isFromUser) {
                String progressString = String.valueOf(progress);
                txtvSelectedDifficulty.setText(progressString + " x " + progressString);
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
            }
        });

        // on-item-selected-listener for selecting scale types
        spnSelectScaleType.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            Map<String, ImageView.ScaleType> scaleTypesDict = new HashMap<>();

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                if (scaleTypesDict.isEmpty()) {
                    scaleTypesDict.put("center", ImageView.ScaleType.CENTER);
                    scaleTypesDict.put("centerCrop", ImageView.ScaleType.CENTER_CROP);
                    scaleTypesDict.put("centerInside", ImageView.ScaleType.CENTER_INSIDE);
                    scaleTypesDict.put("fitCenter", ImageView.ScaleType.FIT_CENTER);
                    scaleTypesDict.put("fitEnd", ImageView.ScaleType.FIT_END);
                    scaleTypesDict.put("fitStart", ImageView.ScaleType.FIT_START);
                    scaleTypesDict.put("fitXY", ImageView.ScaleType.FIT_XY);
                }

                String scaleTypeString = adapterView.getSelectedItem().toString();
                imgvPreviewSelectedImage.setScaleType(scaleTypesDict.getOrDefault(scaleTypeString, ImageView.ScaleType.CENTER));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        // on-click-listener for starting the game
        btnStartAtLevelSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bitmap origImageBitmap = ((BitmapDrawable)imgvPreviewSelectedImage.getDrawable()).getBitmap();
                int selectedDifficulty = skbDifficultiesSelect.getProgress();
                GamingModeEnum gamingMode = rdbSelectGamingModeSlidingPuzzle.isChecked() ? GamingModeEnum.SLIDING_MODE : GamingModeEnum.TRADITIONAL_MODE;

                GameManager.getInstance().originalImageBitmap = origImageBitmap;
                GameManager.getInstance().difficulty = selectedDifficulty;
                GameManager.getInstance().numOfSplittedBitmaps = selectedDifficulty * selectedDifficulty;
                GameManager.getInstance().gamingMode = gamingMode;

                Intent intentToGameActivity = new Intent(LevelSelectActivity.this, GameActivity.class);
                startActivity(intentToGameActivity);
            }
        });
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
