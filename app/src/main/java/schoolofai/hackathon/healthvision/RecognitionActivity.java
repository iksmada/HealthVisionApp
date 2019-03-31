package schoolofai.hackathon.healthvision;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.widget.Toast;

import java.io.IOException;

public class RecognitionActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recognition);
        Bundle extras = getIntent().getExtras();

        ImageClassifier classifier = null;
        try {
            classifier = new ImageClassifierFloatMobileNet(this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (classifier != null) {

            Bitmap resizedBitmap = centerCrop((Bitmap) extras.get("data"));
            Bitmap imageBitmap = getResizedBitmap(resizedBitmap, classifier.getImageSizeX(), classifier.getImageSizeY());

            SpannableStringBuilder textToShow = new SpannableStringBuilder();
            classifier.classifyFrame(imageBitmap, textToShow);
            float probas[] = classifier.getProbabilities();
            int clazz = -1;
            double proba_d[] = new double[20];
            if (probas.length >= 20) {
                for (int i = 0; i < 20; i++) {
                    proba_d[i] = probas[i];
                }
                clazz = RandomForestClassifier.predict(proba_d);
            }
            String class_name = null;
            if (clazz == 0 ) {
                class_name = "benign";
            } else if (clazz == 1) {
                class_name = "histopatology";
            }

            Toast.makeText(this, textToShow, Toast.LENGTH_LONG).show();
            imageBitmap.recycle();

            setResult(RESULT_OK);
            finish();
        }
    }

    private Bitmap centerCrop(Bitmap srcBmp) {
        Bitmap dstBmp = null;
        if (srcBmp.getWidth() >= srcBmp.getHeight()){

            dstBmp = Bitmap.createBitmap(
                    srcBmp,
                    srcBmp.getWidth()/2 - srcBmp.getHeight()/2,
                    0,
                    srcBmp.getHeight(),
                    srcBmp.getHeight()
            );

        } else {

            dstBmp = Bitmap.createBitmap(
                    srcBmp,
                    0,
                    srcBmp.getHeight()/2 - srcBmp.getWidth()/2,
                    srcBmp.getWidth(),
                    srcBmp.getWidth()
            );
        }

        return dstBmp;
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }
}
