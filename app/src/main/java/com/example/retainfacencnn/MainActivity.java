package com.example.retainfacencnn;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileNotFoundException;


public class MainActivity extends AppCompatActivity {


    private static final int SELECT_IMAGE = 1;

    private ImageView imageView;
    private Bitmap bitmap = null;
    private Bitmap yourSelectedImage = null;
    private RetinaFace retinaface = new RetinaFace();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boolean ret_init = retinaface.Init(getAssets());
        if (!ret_init) {
            Log.e("MainActivity", "mobilenetssdncnn Init failed");
        }


        imageView = (ImageView) findViewById(R.id.iv_image);

        Button btn_image = findViewById(R.id.btn_select_image);
        btn_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK);
                i.setType("image/*");
                startActivityForResult(i, SELECT_IMAGE);
            }
        });

        Button btn_cpu = findViewById(R.id.btn_cpu);
        btn_cpu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (yourSelectedImage == null) return;
                float[] face_info = retinaface.Detect(yourSelectedImage);
                if (face_info == null) {
                    Toast.makeText(getApplicationContext(), "未检测到人脸", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.v("MainActivity", String.valueOf(face_info.length));
                showObjects(face_info);

            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();

            try {
                if (requestCode == SELECT_IMAGE) {
                    bitmap = decodeUri(selectedImage);

                    yourSelectedImage = bitmap.copy(Bitmap.Config.ARGB_8888, true);

                    imageView.setImageBitmap(bitmap);
                }
            } catch (FileNotFoundException e) {
                Log.e("MainActivity", "FileNotFoundException");
                return;
            }
        }
    }

    private Bitmap decodeUri(Uri selectedImage) throws FileNotFoundException {
        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o);

        // The new size we want to scale to
        final int REQUIRED_SIZE = 400;

        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp / 2 < REQUIRED_SIZE
                    || height_tmp / 2 < REQUIRED_SIZE) {
                break;
            }
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o2);
    }

    private void showObjects(float[] objects) {
        // draw objects on bitmap
        Bitmap rgba = bitmap.copy(Bitmap.Config.ARGB_8888, true);

        Canvas canvas = new Canvas(rgba);

        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(4);

        for(int i=0;i<objects.length/14;i++){
            canvas.drawRect(objects[i*14], objects[i*14+1], objects[i*14+2], objects[i*14+3], paint);
        }
        canvas.drawPoints(objects, paint);
        imageView.setImageBitmap(rgba);
    }

}
