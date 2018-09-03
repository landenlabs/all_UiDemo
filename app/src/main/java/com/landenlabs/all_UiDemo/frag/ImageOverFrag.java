package com.landenlabs.all_UiDemo.frag;

import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.EmbossMaskFilter;
import android.graphics.LinearGradient;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.landenlabs.all_UiDemo.R;
import com.landenlabs.all_UiDemo.Ui;


/**
 * Demonstrate overlapping images with notch cut out of top image.
 *
 * @author Dennis Lang (LanDen Labs)
 * @see <a href="http://landenlabs.com/android"> author's web-site </a>
 */

//noinspection ConstantConditions
public class ImageOverFrag  extends UiFragment
        implements SeekBar.OnSeekBarChangeListener, View.OnClickListener {

    private View mRootView;

    enum Filters { NoFilter, EmbossFilter, BlurFilter }
    private Filters filters = Filters.BlurFilter;

    enum ShaderType { NoShader, GradientShader }
    private ShaderType shaderType = ShaderType.NoShader;

    private SeekBar graySb;
    private SeekBar alphaSb;
    private SeekBar strokeWidthSb;
    private TextView grayLbl;
    private TextView alphaLbl;
    private TextView strokeWidthLbl;

    private View lightHolder;
    private SeekBar lightx;
    private SeekBar lighty;
    private SeekBar lightz;
    private SeekBar radius;
    private SeekBar ambientSb;
    private SeekBar specularSb;
    private TextView lightxLbl;
    private TextView lightyLbl;
    private TextView lightzLbl;
    private TextView radiusLbl;
    private TextView ambientTv;
    private TextView specularTv;

    private CheckBox drawClosedCb;
    private CheckBox shaderGradientCb;

    private final int maxRadius = 100;
    private final int maxXYZ = 10;
    private final int seekMax = 100;

    private int alpha = 255;
    private int gray = 128;
    private int lx = 3;
    private int ly =3;
    private int lz = 3;
    private int rad = 20;
    private float ambient = 0.2f;    // 0..1
    private final float maxAmbient = 1.0f;
    private float specular = 2.0f;    // 0..?
    private final float maxSpecular = 5.0f;
    private final float maxStrokeWidth = 200.0f;
    private float strokeWidth = 60.0f;

    private PorterDuff.Mode portDuffMode = PorterDuff.Mode.MULTIPLY;
    private final Paint.Style paintStyle = Paint.Style.STROKE;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.image_over, container, false);

        setup();
        return mRootView;
    }

    @Override
    public int getFragId() {
        return R.id.images_over_id;
    }

    @Override
    public String getName() {
        return "ImageOver";
    }

    @Override
    public String getDescription() {
        return "??";
    }


    private void setup() {
        final View titleView = Ui.viewById(mRootView, R.id.title);
        Ui.viewById(mRootView, R.id.bottom).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                titleView.animate().alpha(1.0f - titleView.getAlpha()).start();
            }
        });

        graySb = Ui.viewById(mRootView, R.id.gray_sb);
        alphaSb = Ui.viewById(mRootView, R.id.alpha_sb);
        strokeWidthSb = Ui.viewById(mRootView, R.id.stroke_width_sb);

        lightHolder = Ui.viewById(mRootView, R.id.light_holder);
        lightx = Ui.viewById(mRootView, R.id.light_x);
        lighty = Ui.viewById(mRootView, R.id.light_y);
        lightz = Ui.viewById(mRootView, R.id.light_z);
        radius = Ui.viewById(mRootView, R.id.radius);
        ambientSb = Ui.viewById(mRootView, R.id.ambient);
        specularSb = Ui.viewById(mRootView, R.id.specular);


        drawClosedCb = Ui.viewById(mRootView, R.id.draw_closed);
        drawClosedCb.setOnClickListener(this);
        shaderGradientCb = Ui.viewById(mRootView, R.id.shader_gradient);
        shaderGradientCb.setOnClickListener(this);

        graySb.setProgress(gray);
        alphaSb.setProgress(alpha);
        setXYZ(lightx, lx);
        setXYZ(lighty, ly);
        setXYZ(lightz, lz);
        radius.setProgress(seekMax * rad / maxRadius);
        ambientSb.setProgress((int)(seekMax * ambient / maxAmbient));
        specularSb.setProgress((int)(seekMax * specular / maxSpecular));
        strokeWidthSb.setProgress((int)(seekMax * strokeWidth / maxStrokeWidth));

        graySb.setOnSeekBarChangeListener(this);
        alphaSb.setOnSeekBarChangeListener(this);
        lightx.setOnSeekBarChangeListener(this);
        lighty.setOnSeekBarChangeListener(this);
        lightz.setOnSeekBarChangeListener(this);
        radius.setOnSeekBarChangeListener(this);
        ambientSb.setOnSeekBarChangeListener(this);
        specularSb.setOnSeekBarChangeListener(this);
        strokeWidthSb.setOnSeekBarChangeListener(this);

        grayLbl   = Ui.viewById(mRootView, R.id.gray_lbl);
        alphaLbl   = Ui.viewById(mRootView, R.id.alpha_lbl);
        lightxLbl = Ui.viewById(mRootView, R.id.light_x_lbl);
        lightyLbl = Ui.viewById(mRootView, R.id.light_y_lbl);
        lightzLbl = Ui.viewById(mRootView, R.id.light_z_lbl);
        radiusLbl = Ui.viewById(mRootView, R.id.radius_lbl);
        ambientTv = Ui.viewById(mRootView, R.id.ambient_lbl);
        specularTv = Ui.viewById(mRootView, R.id.specular_lbl);
        strokeWidthLbl = Ui.viewById(mRootView, R.id.stroke_width_lbl);

        Ui.viewById(mRootView, R.id.filter_blur).setOnClickListener(this);
        Ui.viewById(mRootView, R.id.filter_emboss).setOnClickListener(this);
        Ui.viewById(mRootView, R.id.filter_none).setOnClickListener(this);

        Ui.viewById(mRootView, R.id.mode_clear).setOnClickListener(this);
        Ui.viewById(mRootView, R.id.mode_xor).setOnClickListener(this);
        Ui.viewById(mRootView, R.id.mode_darken).setOnClickListener(this);
        Ui.viewById(mRootView, R.id.mode_lighten).setOnClickListener(this);
        Ui.viewById(mRootView, R.id.mode_mult).setOnClickListener(this);
        Ui.viewById(mRootView, R.id.mode_add).setOnClickListener(this);
        Ui.viewById(mRootView, R.id.mode_src).setOnClickListener(this);
        Ui.viewById(mRootView, R.id.mode_dst).setOnClickListener(this);

        updateView();
    }

    private void setXYZ(SeekBar seekBar, float value) {
        int iVal = (int)((maxXYZ/2 - value) * seekMax/maxXYZ);
        seekBar.setProgress(iVal);
    }
    private int getXYZ(SeekBar seekBar) {
        return maxXYZ/2 - seekBar.getProgress() *  maxXYZ / seekMax;
    }

    private void updateView() {

        gray = graySb.getProgress();
        int grayColor = 0xff000000 + gray + gray*256 + gray*256*256;
        grayLbl.setBackgroundColor(grayColor);
        grayLbl.setText("Gray: " + String.valueOf(gray));

        alpha = alphaSb.getProgress();
        alphaLbl.setText("Alpha: " + String.valueOf(alpha));
        grayColor = gray + gray*256 + gray*256*256 + alpha*256*256*256;

        lx = getXYZ(lightx);
        ly = getXYZ(lighty);
        lz = getXYZ(lightz);
        strokeWidth = strokeWidthSb.getProgress() * maxStrokeWidth / seekMax;
        rad = radius.getProgress() * maxRadius / seekMax;

        ambient = ambientSb.getProgress() * maxAmbient / seekMax;
        specular = specularSb.getProgress() * maxSpecular / seekMax;

        lightxLbl.setText("X " + lx);
        lightyLbl.setText("Y " + ly);
        lightzLbl.setText("Z " + lz);
        strokeWidthLbl.setText("StrokeWidth " + strokeWidth);
        radiusLbl.setText("Radius " + rad);
        specularTv.setText(String.format("Specual %.1f", specular));
        ambientTv.setText(String.format("Ambient %.1f", ambient));

        // http://stackoverflow.com/questions/1705239/how-should-i-give-images-rounded-corners-in-android
        BitmapDrawable botDrawable = (BitmapDrawable)getResources().getDrawable(R.drawable.paper_pink);
        int imgWidth = botDrawable.getIntrinsicWidth();
        int imgHeight = botDrawable.getIntrinsicHeight();

        Bitmap canvasDrawable = Bitmap.createBitmap(imgWidth, imgHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(canvasDrawable);

        // We're going to apply this paint eventually using a porter-duff xfer mode.
        // This will allow us to only overwrite certain pixels. RED is arbitrary. This
        // could be any color that was fully opaque (alpha = 255)
        Paint xferPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        xferPaint.setColor(Color.RED);

        // --------------- draw round box

        boolean drawRoundedCorners = false;


        //noinspection ConstantConditions
        if (drawRoundedCorners) {
            // We're just reusing xferPaint to paint a normal looking rounded box, the 80.f
            // is the amount we're rounding by.
            canvas.drawRoundRect(new RectF(0, 0, imgWidth, imgHeight), 80.0f, 80.0f, xferPaint);
        }

        // ---------------- draw notch

        float notchCenter = imgWidth * 0.50f;  // Notch center 50% from left edge.
        float notchWidth = notchCenter / 2;

        boolean drawNotch = true;
        //noinspection ConstantConditions,ConstantConditions
        if (drawNotch) {
            Path notchPath = new Path();
            notchPath.moveTo(0, 0);
            notchPath.lineTo(notchCenter - notchWidth, 0);
            notchPath.lineTo(notchCenter, notchWidth*2);
            notchPath.lineTo(notchCenter + notchWidth, 0);
            notchPath.lineTo(imgWidth, 0);
            notchPath.lineTo(imgWidth, imgHeight);
            notchPath.lineTo(0, imgHeight);
            notchPath.close();
            canvas.drawPath(notchPath, xferPaint);
        }

        // Now we apply the 'magic sauce' to the paint
        xferPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));

        Bitmap result = Bitmap.createBitmap(imgWidth, imgHeight, Bitmap.Config.ARGB_8888);
        Canvas resultCanvas = new Canvas(result);
        resultCanvas.drawBitmap(botDrawable.getBitmap(), 0, 0, null);

        // resultCanvas.drawBitmap(canvasDrawable, 0, 0, xferPaint);

        boolean doDrawNotchShadow = true;
        //noinspection ConstantConditions,ConstantConditions
        if (doDrawNotchShadow) {
            drawNotchShadow(resultCanvas, grayColor, notchCenter, notchWidth, imgWidth, strokeWidth);
        }

        resultCanvas.drawBitmap(canvasDrawable, 0, 0, xferPaint);

        ImageView topView = Ui.viewById(mRootView, R.id.imageBot);
        topView.setImageBitmap(result);
    }

    @SuppressWarnings("SuspiciousNameCombination")
    private void drawNotchShadow(Canvas canvas, int color, float notchCenter, float notchWidth,
                                 float pathWidth, float strokeWidth) {

        Path notchPath = new Path();
        notchPath.moveTo(0, 0);
        notchPath.lineTo(notchCenter - notchWidth, 0);
        notchPath.lineTo(notchCenter, notchWidth*2);
        notchPath.lineTo(notchCenter + notchWidth, 0);
        notchPath.lineTo(pathWidth, 0);

        if (drawClosedCb.isChecked()) {
            float shadowWidth = notchWidth * 3;
            notchPath.lineTo(pathWidth, shadowWidth);
            notchPath.lineTo(notchCenter + notchWidth, shadowWidth);
            notchPath.lineTo(notchCenter, shadowWidth + notchWidth * 2);
            notchPath.lineTo(notchCenter - notchWidth, shadowWidth);
            notchPath.lineTo(0, shadowWidth);
            notchPath.close();
        }

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(color);
        paint.setStyle(paintStyle); // Paint.Style.STROKE);
        //    paint.setStrokeJoin(Paint.Join.ROUND);
        //    paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(strokeWidth);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.MULTIPLY));

        float[] lightSrc = new float[]{lx, ly, lz};
        float blurRadius = rad;
        MaskFilter filter = null;


        int shadowWidth = (int)(notchWidth*2);
        Shader rshader;
        switch (shaderType) {
            default:
                break;
            case GradientShader:
                rshader = new LinearGradient(0, 0, 0, shadowWidth, Color.BLACK, Color.WHITE, Shader.TileMode.CLAMP);
                paint.setShader(rshader);
                break;
        }

        switch (filters) {
            case NoFilter:
                break;
            case EmbossFilter:
                filter =  new EmbossMaskFilter(lightSrc, ambient, specular, blurRadius);
                paint.setXfermode(new PorterDuffXfermode(portDuffMode));
                break;
            case BlurFilter:
                filter = new BlurMaskFilter(Math.max(0.5f, blurRadius), BlurMaskFilter.Blur.NORMAL);
                paint.setXfermode(new PorterDuffXfermode(portDuffMode));
                break;
        }

        paint.setMaskFilter(filter);
        canvas.drawPath(notchPath, paint);
    }

    // =============================================================================================
    // Seekbar onProgressChanged

    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        updateView();
    }
    public void onStartTrackingTouch(SeekBar seekBar) {
    }
    public void onStopTrackingTouch(SeekBar seekBar)  {
    }

    // =============================================================================================
    // View  onClick


    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            // ---- Filters
            case R.id.filter_blur:
                filters = Filters.BlurFilter;
                lightHolder.setVisibility(View.GONE);
                break;
            case R.id.filter_emboss:
                filters = Filters.EmbossFilter;
                lightHolder.setVisibility(View.VISIBLE);
                break;
            case R.id.filter_none:
                filters = Filters.NoFilter;
                lightHolder.setVisibility(View.GONE);
                break;

            // ---- Shaders
            case R.id.shader_gradient:
                shaderType = shaderGradientCb.isChecked() ? ShaderType.GradientShader : ShaderType.NoShader;
                break;

            // ---- Duffy mode
            case R.id.mode_add:
                portDuffMode = PorterDuff.Mode.ADD;
                break;
            case R.id.mode_clear:
                portDuffMode = PorterDuff.Mode.CLEAR;
                break;
            case R.id.mode_darken:
                portDuffMode = PorterDuff.Mode.DARKEN;
                break;
            case R.id.mode_lighten:
                portDuffMode = PorterDuff.Mode.LIGHTEN;
                break;
            case R.id.mode_mult:
                portDuffMode = PorterDuff.Mode.MULTIPLY;
                break;
            case R.id.mode_xor:
                portDuffMode = PorterDuff.Mode.XOR;
                break;
            case R.id.mode_src:
                portDuffMode = PorterDuff.Mode.SRC;
                break;
            case R.id.mode_dst:
                portDuffMode = PorterDuff.Mode.DST;
                break;
        }

        updateView();
    }
}
