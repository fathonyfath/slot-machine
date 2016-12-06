package id.fathonyteguh.slotmachine;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

    private Thread firstThread;
    private Thread secondThread;
    private Thread thirdThread;

    private RandomizerRunnable[] randomizerRunnables;
    private int currentRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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

        randomizerRunnables[0].setCurrentIndex((int) (Math.random() * RESOURCE.length));
        randomizerRunnables[1].setCurrentIndex((int) (Math.random() * RESOURCE.length));
        randomizerRunnables[2].setCurrentIndex((int) (Math.random() * RESOURCE.length));

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

    private void stopRoll() {
        if(currentRunnable < randomizerRunnables.length - 1) {
            randomizerRunnables[currentRunnable++].setRunning(false);
        } else {
            randomizerRunnables[currentRunnable].setRunning(false);
            if(randomizerRunnables[0].getCurrentIndex() == randomizerRunnables[1].getCurrentIndex() &&
                    randomizerRunnables[1].getCurrentIndex() == randomizerRunnables[2].getCurrentIndex()) {
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
                        }
                    });
                    Thread.sleep(50);
                    currentIndex++;
                    if (currentIndex >= RESOURCE.length) currentIndex = 0;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
