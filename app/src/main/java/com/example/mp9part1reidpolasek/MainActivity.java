package com.example.mp9part1reidpolasek;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.File;
import java.net.URI;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    File photoPath = null;
    Uri photoURI = null;
    FirebaseVisionText theSacredTexts = null;
    int succeeded = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        layoutManager = new LinearLayoutManager(this);
        //recyclerView.setLayoutManager(layoutManager);
    }

    public void onClick(View view){
        Intent takeAPicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File photoFile = null;
        try{
            photoFile = File.createTempFile("001",".jpg",getExternalFilesDir(Environment.DIRECTORY_PICTURES));
            photoURI = FileProvider.getUriForFile(this,"com.example.mp9part1reidpolasek.fileprovider",photoFile);
            takeAPicture.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(takeAPicture, 1);
        }
        catch (Exception e){

        }
        //mAdapter = new recAdapter(myDataset);
        //recyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        try {
            if ((requestCode==1)&(resultCode == RESULT_OK)){
                //File file = new File(currentPhotoPath);
                //Bitmap bmpOut = MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.fromFile(file));
                Bitmap bmpOut = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoURI);
                if (bmpOut != null) {
                    ImageView img= (ImageView) findViewById(R.id.imageView);
                    img.setImageBitmap(bmpOut);
                    //getText(bmpOut);
                    runTextRecognition(bmpOut);
                }
            }

        } catch (Exception error) {
            error.printStackTrace();
        }
    }

    private void runTextRecognition(Bitmap bmp) {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bmp);
        FirebaseVisionTextRecognizer recognizer = FirebaseVision.getInstance()
                .getOnDeviceTextRecognizer();
        recognizer.processImage(image)
                .addOnSuccessListener(
                        new OnSuccessListener<FirebaseVisionText>() {
                            @Override
                            public void onSuccess(FirebaseVisionText texts) {
                                processTextRecognitionResult(texts);
                                succeeded = 3;
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Task failed with an exception
                                succeeded = 4;
                                e.printStackTrace();
                            }
                        });
        //
        Toast.makeText(this,Integer.toString(succeeded), Toast.LENGTH_SHORT).show();
    }

    private void processTextRecognitionResult(FirebaseVisionText texts) {
        List<FirebaseVisionText.TextBlock> blocks = texts.getTextBlocks();
        if (blocks.size() == 0) {
            succeeded = 1;
            return;
        }
        else{
            succeeded = 2;
            return;
        }
        //mGraphicOverlay.clear();
        /*for (int i = 0; i < blocks.size(); i++) {
            List<FirebaseVisionText.Line> lines = blocks.get(i).getLines();
            for (int j = 0; j < lines.size(); j++) {
                List<FirebaseVisionText.Element> elements = lines.get(j).getElements();
                for (int k = 0; k < elements.size(); k++) {
                    Graphic textGraphic = new TextGraphic(mGraphicOverlay, elements.get(k));
                    mGraphicOverlay.add(textGraphic);

                }
            }
        }*/
    }

    /*public void getText(Bitmap inputImage){
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(inputImage);
        FirebaseVisionTextRecognizer recognizer = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
        recognizer.processImage(image)
                .addOnSuccessListener(
                        new OnSuccessListener<FirebaseVisionText>() {
                            @Override
                            public void onSuccess(FirebaseVisionText texts) {
                                //mTextButton.setEnabled(true);
                                //processTextRecognitionResult(texts);
                                theSacredTexts = texts;
                                succeeded = 1;
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                succeeded = -1;
                            }
                        });
        haveText(theSacredTexts);
    }

    public void haveText(FirebaseVisionText texts){
        if (succeeded ==0){
            Toast.makeText(this,"succeeded", Toast.LENGTH_SHORT).show();
        }
        if (succeeded <0){
            Toast.makeText(this,"succeeded", Toast.LENGTH_SHORT).show();
        }
        if (texts != null){
            Toast.makeText(this,"got here", Toast.LENGTH_SHORT).show();
        }
        for (FirebaseVisionText.TextBlock block: texts.getTextBlocks()) {
            String blockText = block.getText();
            Toast.makeText(this,blockText, Toast.LENGTH_SHORT).show();
        }
    }*/
}
