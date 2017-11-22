package gmedia.net.id.cakratvlivestreaming;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import com.maulana.custommodul.ApiVolley;
import com.maulana.custommodul.ItemValidation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import gmedia.net.id.cakratvlivestreaming.Utils.ServerURL;

public class VideoScreen extends AppCompatActivity {

    private static boolean doubleBackToExitPressedOnce;
    private boolean exitState = false;
    private VideoView vvPlayVideo;
    private String link = "";
    private ItemValidation iv = new ItemValidation();
    private ProgressBar pbLoading;
    private Button btnRefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_screen);

        doubleBackToExitPressedOnce = false;
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){

            if(bundle.getBoolean("exit", false)){
                exitState = true;
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                finish();
            }
        }
        initUI();
    }

    private void initUI() {

        vvPlayVideo = (VideoView) findViewById(R.id.vv_stream);
        pbLoading = (ProgressBar) findViewById(R.id.pb_loading);
        btnRefresh = (Button) findViewById(R.id.btn_refresh);

        getLinkRTSP();

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getLinkRTSP();
            }
        });
    }

    private void getLinkRTSP() {

        JSONObject jbody = new JSONObject();
        btnRefresh.setVisibility(View.GONE);

        ApiVolley apiVolley = new ApiVolley(VideoScreen.this, jbody, "GET", ServerURL.getLink, "", "", 0, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                try {
                    JSONObject response = new JSONObject(result);
                    String status = response.getJSONObject("metadata").getString("status");

                    if(iv.parseNullInteger(status) == 200) {

                        JSONArray jsonArray = response.getJSONArray("response");
                        for(int i = 0; i < jsonArray.length();i++){
                            JSONObject jo = jsonArray.getJSONObject(i);
                            playVideo(jo.getString("link"));
                            break;
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    btnRefresh.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onError(String result) {
                btnRefresh.setVisibility(View.VISIBLE);
            }
        });
    }

    private void playVideo(String url){

        btnRefresh.setVisibility(View.GONE);
        vvPlayVideo.stopPlayback();
        vvPlayVideo.clearAnimation();
        vvPlayVideo.suspend();
        vvPlayVideo.setVideoURI(null);

        pbLoading.setVisibility(View.VISIBLE);
        /*MediaController mediaController = new MediaController(ChannelViewScreen.this);
        mediaController.setAnchorView(vvPlayVideo);*/

        Uri uri = Uri.parse(url);
        vvPlayVideo.setVideoURI(uri);
        //vvPlayVideo.setMediaController(mediaController);
        vvPlayVideo.requestFocus();
        //vvPlayVideo.seekTo(100);

        /*if(masterList != null && masterList.size() > 0){

            int x = 0;
            for(CustomItem item : masterList){

                if(item.getItem3().trim().equals(url.trim())) ListChanelAdapter.selectedPosition = x;
                x++;
            }
        }*/

        vvPlayVideo.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mp) {

                pbLoading.setVisibility(View.GONE);
                mp.start();

                fullScreenVideo(1);
                mp.setOnVideoSizeChangedListener(new MediaPlayer.OnVideoSizeChangedListener() {

                    @Override
                    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {

                        mp.start();
                        fullScreenVideo(1);
                    }
                });
            }
        });

        vvPlayVideo.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {

                pbLoading.setVisibility(View.GONE);
                /*Snackbar.make(findViewById(android.R.id.content), "Channel sudah tidak tersedia", Snackbar.LENGTH_LONG)
                        .setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                            }
                        }).show();*/
                vvPlayVideo.stopPlayback();
                vvPlayVideo.clearAnimation();
                vvPlayVideo.suspend();
                vvPlayVideo.setVideoURI(null);
                Toast.makeText(VideoScreen.this, "Tidak dapat memutar channel ini", Toast.LENGTH_LONG).show();
                btnRefresh.setVisibility(View.VISIBLE);
                return true;
            }
        });

        /*if(isFirstLoad){
            isFirstLoad = false;

            vvHandler = new Handler();
            vvRunnable = new Runnable() {
                public void run() {
                    try {
                        if (!vvPlayVideo.isPlaying()) {
                            vvPlayVideo.resume();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    vvHandler.postDelayed(vvRunnable, 1000);
                }
            };

            vvHandler.postDelayed(vvRunnable, 0);
        }*/
    }

    private void fullScreenVideo(double scale)
    {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) vvPlayVideo.getLayoutParams();
        scale = 1;
        double doubleWidth = metrics.widthPixels * scale;
        double doubleHeight = metrics.heightPixels * scale;
        RelativeLayout.LayoutParams newparams = new RelativeLayout.LayoutParams((int) doubleWidth,(int) doubleHeight);
        newparams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
        newparams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        newparams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        newparams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);

        /*params.width =  rvScreenContainer.getMeasuredWidth();
        params.height = rvScreenContainer.getMeasuredHeight();*/

        vvPlayVideo.setLayoutParams(newparams);
    }

    @Override
    public void onBackPressed() {
        // Origin backstage
        if (doubleBackToExitPressedOnce) {
            Intent intent = new Intent(VideoScreen.this, VideoScreen.class);
            intent.putExtra("exit", true);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            //System.exit(0);
        }

        if(!exitState && !doubleBackToExitPressedOnce){
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, getResources().getString(R.string.app_exit), Toast.LENGTH_SHORT).show();
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}
