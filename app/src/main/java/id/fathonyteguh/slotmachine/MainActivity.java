package id.fathonyteguh.slotmachine;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    ImageView image1;
    ImageView image2;
    ImageView image3;

    Button start, stop;

    private final int[] RESOURCE = {R.drawable.ic_android, R.drawable.ic_cake,
            R.drawable.ic_check, R.drawable.ic_cutter,
            R.drawable.ic_flag, R.drawable.ic_hand,
            R.drawable.ic_pencil};

    private final String[] TAG = {"android", "cake", "check", "cutter", "flag", "hand", "pencil"};

    private Thread firstThread;
    private Thread secondThread;
    private Thread thirdThread;

    private RandomizerRunnable[] randomizerRunnables;
    private int currentRunnable;

    private boolean isResuming;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isResuming = false;

        randomizerRunnables = new RandomizerRunnable[3];

        image1 = (ImageView) findViewById(R.id.image1);
        image2 = (ImageView) findViewById(R.id.image2);
        image3 = (ImageView) findViewById(R.id.image3);
        start = (Button) findViewById(R.id.start);
        stop = (Button) findViewById(R.id.stop);

        start.setEnabled(true);
        stop.setEnabled(false);

        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startRoll();
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopRoll();
            }
        });
    }

    private void startRoll() {
        currentRunnable = 0;
        randomizerRunnables[0] = new RandomizerRunnable(image1);
        randomizerRunnables[1] = new RandomizerRunnable(image2);
        randomizerRunnables[2] = new RandomizerRunnable(image3);

        int image1Pos, image2Pos, image3Pos;

        if(isResuming) {
            image1Pos = getIndexOnTag(image1.getTag().toString());
            image2Pos = getIndexOnTag(image2.getTag().toString());
            image3Pos = getIndexOnTag(image3.getTag().toString());
        } else {
            image1Pos = (int) (Math.random() * RESOURCE.length);
            image2Pos = (int) (Math.random() * RESOURCE.length);
            image3Pos = (int) (Math.random() * RESOURCE.length);
        }

        randomizerRunnables[0].setCurrentIndex(image1Pos);
        randomizerRunnables[1].setCurrentIndex(image2Pos);
        randomizerRunnables[2].setCurrentIndex(image3Pos);

        image1.setTag(TAG[image1Pos]);
        image1.setTag(TAG[image2Pos]);
        image1.setTag(TAG[image3Pos]);

        firstThread = new Thread(randomizerRunnables[0]);
        secondThread = new Thread(randomizerRunnables[1]);
        thirdThread = new Thread(randomizerRunnables[2]);

        randomizerRunnables[0].setRunning(true);
        randomizerRunnables[1].setRunning(true);
        randomizerRunnables[2].setRunning(true);

        firstThread.start();
        secondThread.start();
        thirdThread.start();

        start.setEnabled(false);
        stop.setEnabled(true);
    }

    private int getIndexOnTag(String tag) {
        int result = -1;
        for(int i = 0; i < TAG.length; i++) {
            if(TAG[i].equalsIgnoreCase(tag)) result = i;
        }
        return result;
    }

    private void stopRoll() {
        if(currentRunnable < randomizerRunnables.length - 1) {
            randomizerRunnables[currentRunnable].setRunning(false);
            currentRunnable++;
        } else {
            isResuming = true;

            randomizerRunnables[currentRunnable].setRunning(false);

            String image1Tag = image1.getTag().toString();
            String image2Tag = image2.getTag().toString();
            String image3Tag = image3.getTag().toString();

            if(image1Tag.equalsIgnoreCase(image2Tag) && image2Tag.equalsIgnoreCase(image3Tag)) {
                Toast.makeText(this, "You win!!!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "You lose. :(", Toast.LENGTH_SHORT).show();
            }

            start.setEnabled(true);
            stop.setEnabled(false);
        }
    }

    private class RandomizerRunnable implements Runnable {

        private int currentIndex = 0;
        private boolean running;
        private ImageView imageView;

        public RandomizerRunnable(ImageView imageView) {
            this.imageView = imageView;
            this.currentIndex = 0;
            this.running = false;
        }

        public void setRunning(boolean running) {
            this.running = running;
        }

        public int getCurrentIndex() {
            return currentIndex;
        }

        public void setCurrentIndex(int currentIndex) {
            this.currentIndex = currentIndex;
        }

        @Override
        public void run() {
            while (running) {
                try {
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            imageView.setImageResource(RESOURCE[currentIndex]);
                            imageView.setTag(TAG[currentIndex]);
                        }
                    });
                    Thread.sleep(150);
                    currentIndex++;
                    if (currentIndex >= RESOURCE.length) currentIndex = 0;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
