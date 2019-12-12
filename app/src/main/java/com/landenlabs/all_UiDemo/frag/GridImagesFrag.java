package com.landenlabs.all_UiDemo.frag;

/*
 * Copyright (c) 2019 Dennis Lang (LanDen Labs) landenlabs@gmail.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the
 *  following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all copies or substantial
 *  portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN
 * NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 * SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 *  @author Dennis Lang  (3/21/2015)
 *  @see http://landenlabs.com
 *
 */

import android.animation.AnimatorInflater;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.transition.AutoTransition;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.landenlabs.all_UiDemo.ALog.ALog;
import com.landenlabs.all_UiDemo.R;
import com.landenlabs.all_UiDemo.Ui;

/**
 * Demonstrate grid layout of images.
 *
 * @author Dennis Lang (LanDen Labs)
 * @see <a href="http://landenlabs.com/android"> author's web-site </a>
 */

public class GridImagesFrag  extends UiFragment   {

    private View mRootView;
    RadioGroup mRadioGroup;
    int dimPx = 85;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.grid_images, container, false);

        setup();
        return mRootView;
    }

    @Override
    public int getFragId() {
        return R.id.grid_images_id;
    }

    @Override
    public String getName() {
        return "FullScreen";
    }

    @Override
    public String getDescription() {
        return "??";
    }

    private void setup() {

        GridView gridview = Ui.needViewById(mRootView, R.id.gridview);
        gridview.setAdapter(new ImageAdapter(getActivity()));

        /*
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(getActivity(), "" + position,  Toast.LENGTH_SHORT).show();
            }
        });
        */

        mRadioGroup = Ui.needViewById(mRootView, R.id.grid_image_rg);

    }

    class ImageAdapter extends BaseAdapter {
        private final Context mContext;

        ImageAdapter(Context c) {
            mContext = c;
        }

        public int getCount() {
            return mThumbIds.length;
        }

        public Object getItem(int position) {
            return null;
        }

        public long getItemId(int position) {
            return 0;
        }

        // create a new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            if (convertView == null) {
                // if it's not recycled, initialize some attributes
                imageView = new ImageView(mContext);
                imageView.setLayoutParams(new GridView.LayoutParams(dimPx, dimPx));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(8, 8, 8, 8);


                final int pos = position;
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(view.getContext(), "Grid Pos:" + pos,  Toast.LENGTH_SHORT).show();
                        int ckId = mRadioGroup.getCheckedRadioButtonId();

                        try {
                            switch (ckId) {
                                case R.id.grid_image_statelist:
                                    break;
                                case R.id.grid_image_scale1:
                                case R.id.grid_image_scale2:
                                case R.id.grid_image_down:
                                case R.id.grid_image_reset:
                                    scaleImage(view, ckId, pos);
                                    break;
                            }
                        } catch (Exception ex) {
                            ALog.w.msg(ex.getMessage());
                        }
                    }
                });
                if (Build.VERSION.SDK_INT >= 21) {
                    imageView.setStateListAnimator(AnimatorInflater.loadStateListAnimator(mContext, R.animator.press));
                }
            } else {
                imageView = (ImageView) convertView;
            }

            // Image must be set in background to ue stateList animation
            imageView.setBackgroundResource(mThumbIds[position]);
            return imageView;
        }

        private void scaleImage(View view, int id, int pos) {
            ImageView imgView = (ImageView)view;
            imgView.setImageResource(mThumbIds[pos]);
            ViewGroup.LayoutParams params = view.getLayoutParams();
            Transition autoTransition;
            int incr = 80;
            switch (id) {
                case R.id.grid_image_scale1:
                    imgView.setBackgroundColor(Color.RED);
                    params.width += incr;
                    params.height += incr;
                    view.setLayoutParams(params);
                    break;

                case R.id.grid_image_scale2:
                    imgView.setBackgroundColor(Color.GREEN);
                    autoTransition = new AutoTransition();
                    autoTransition.setDuration(3000);

                    // With this overload you can control actual transition animation
                    TransitionManager.beginDelayedTransition((ViewGroup) mRootView, autoTransition);
                    // After `beginDelayedTransition()` function perform changes to the layout
                    // Transitions framework will detect those changes and perform appropriate animations
                    params.width += incr;
                    params.height += incr;
                    view.requestLayout();
                    view.invalidate();
                    break;

                case R.id.grid_image_down:
                    if (params.width > incr) {
                        imgView.setBackgroundColor(Color.GREEN);
                        autoTransition = new AutoTransition();
                        autoTransition.setDuration(3000);

                        // With this overload you can control actual transition animation
                        TransitionManager.beginDelayedTransition((ViewGroup) mRootView, autoTransition);
                        // After `beginDelayedTransition()` function perform changes to the layout
                        // Transitions framework will detect those changes and perform appropriate animations
                        params.width -= incr;
                        params.height -= incr;
                        view.requestLayout();
                        view.invalidate();
                    }
                    break;

                case R.id.grid_image_reset:
                    params.width = dimPx;
                    params.height = dimPx;
                    view.setLayoutParams(params);
                    imgView.setBackgroundColor(Color.TRANSPARENT);
                    break;
            }
        }

        // references to our images
        private final Integer[] mThumbIds = {
                R.drawable.image_a, R.drawable.image_c,
                R.drawable.image_e, R.drawable.image_f,
                R.drawable.image_p, R.drawable.image_s,

                R.drawable.image100, R.drawable.image200,
                R.drawable.image400, R.drawable.image600,

                R.drawable.image_a, R.drawable.image_c,
                R.drawable.image_e, R.drawable.image_f,
                R.drawable.image_p, R.drawable.image_s,

                R.drawable.image_a, R.drawable.image_c,
                R.drawable.image_e, R.drawable.image_f,
                R.drawable.image_p, R.drawable.image_s,

                R.drawable.image_a, R.drawable.image_c,
                R.drawable.image_e, R.drawable.image_f,
                R.drawable.image_p, R.drawable.image_s,


        };
    }
}
