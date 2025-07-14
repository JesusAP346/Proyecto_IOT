package com.example.proyecto_iot.cliente.busqueda;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.viewpager2.widget.ViewPager2;
import com.example.proyecto_iot.R;
import java.util.List;

public class ImageFullscreenDialog extends Dialog {

    private List<String> imageUrls;
    private int currentPosition;
    private ViewPager2 viewPager;
    private LinearLayout indicatorContainer;
    private TextView textPosition;
    private ImageButton btnClose;

    public ImageFullscreenDialog(Context context, List<String> imageUrls, int initialPosition) {
        super(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        this.imageUrls = imageUrls;
        this.currentPosition = initialPosition;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_image_fullscreen);

        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        initializeViews();
        setupViewPager();
        setupIndicators();
        setupClickListeners();
    }

    private void initializeViews() {
        viewPager = findViewById(R.id.viewPagerFullscreen);
        indicatorContainer = findViewById(R.id.indicatorContainerFullscreen);
        textPosition = findViewById(R.id.textPosition);
        btnClose = findViewById(R.id.btnCloseFullscreen);
    }

    private void setupViewPager() {
        FullscreenImageAdapter adapter = new FullscreenImageAdapter(getContext(), imageUrls);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(currentPosition, false);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                currentPosition = position;
                updateIndicators(position);
                updatePositionText();
            }
        });

        updatePositionText();
    }

    private void setupIndicators() {
        indicatorContainer.removeAllViews();

        if (imageUrls.size() <= 1) {
            indicatorContainer.setVisibility(View.GONE);
            return;
        }

        for (int i = 0; i < imageUrls.size(); i++) {
            ImageView indicator = new ImageView(getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(24, 24);
            params.setMargins(8, 0, 8, 0);
            indicator.setLayoutParams(params);
            indicator.setImageResource(R.drawable.ic_indicator_inactive);
            indicatorContainer.addView(indicator);
        }

        updateIndicators(currentPosition);
    }

    private void updateIndicators(int position) {
        for (int i = 0; i < indicatorContainer.getChildCount(); i++) {
            ImageView indicator = (ImageView) indicatorContainer.getChildAt(i);
            if (i == position) {
                indicator.setImageResource(R.drawable.ic_indicator_active);
            } else {
                indicator.setImageResource(R.drawable.ic_indicator_inactive);
            }
        }
    }

    private void updatePositionText() {
        if (imageUrls.size() > 1) {
            textPosition.setText((currentPosition + 1) + " / " + imageUrls.size());
            textPosition.setVisibility(View.VISIBLE);
        } else {
            textPosition.setVisibility(View.GONE);
        }
    }

    private void setupClickListeners() {
        btnClose.setOnClickListener(v -> dismiss());
    }
}