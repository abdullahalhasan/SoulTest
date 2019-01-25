package dev.aahasan.soultest.ui;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import dev.aahasan.soultest.R;
import dev.aahasan.soultest.adapters.BoardAdapter;
import dev.aahasan.soultest.models.Pin;
import dev.aahasan.soultest.utils.DataCache;
import dev.aahasan.soultest.utils.ImageCache;
import dev.aahasan.soultest.utils.JsonParser;
import dev.aahasan.soultest.utils.UniversalDownloader;

public class MainActivity extends AppCompatActivity {

    private ProgressBar progress;

    private SwipeRefreshLayout swipeContainer;

    private List<Pin> pins;

    private BoardAdapter boardAdapter;

    private static final String URL = "http://pastebin.com/raw/wgkJgazE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        // Setup refresh listener which triggers new data loading

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override

            public void onRefresh() {
                fetchPins();

            }

        });
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        fetchPins();
    }


    private void init()
    {
        DataCache.getInstance().init(50);
        ImageCache.getInstance().init(6);

        progress = (ProgressBar) findViewById(R.id.progress);


        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);

        pins = new ArrayList<>();

        boardAdapter = new BoardAdapter(R.layout.grid_pin, pins);

        RecyclerView boardView = (RecyclerView) findViewById(R.id.board);
        boardView.setHasFixedSize(true);
        boardView.setLayoutManager(gridLayoutManager);
        boardView.setAdapter(boardAdapter);

    }

    public void fetchPins()
    {
        String result = DataCache.getInstance().getFromCache(URL);

        if(result == null)
        {
            UniversalDownloader downloader = new UniversalDownloader();
            downloader.downloadData(URL, progress, callback);
        }
        else
        {
            callback.onSuccess(result);
        }

    }

    private UniversalDownloader.Callback callback = new UniversalDownloader.Callback()
    {
        @Override
        public void onSuccess(String result) {
            Log.v("Callback success", result);

            JsonParser parser = new JsonParser();

            List<Pin> pins = parser.parsePins(result);

            if(pins != null)
            {
                MainActivity.this.pins.addAll(pins);
                boardAdapter.notifyDataSetChanged();
                swipeContainer.setRefreshing(false);
            }
            else
            {

                Toast.makeText(MainActivity.this, "Something went wrong while fetching data", Toast.LENGTH_SHORT).show();

            }
        }

        @Override
        public void onError(Exception e)
        {

            Toast.makeText(MainActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onDestroy()
    {

        ImageCache.getInstance().tearDown();
        DataCache.getInstance().tearDown();

        super.onDestroy();
    }
}