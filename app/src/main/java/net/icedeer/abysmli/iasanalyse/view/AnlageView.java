package net.icedeer.abysmli.iasanalyse.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.v4.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import net.icedeer.abysmli.iasanalyse.R;
import net.icedeer.abysmli.iasanalyse.controller.AppSetting;
import net.icedeer.abysmli.iasanalyse.controller.LogRecorder;
import net.icedeer.abysmli.iasanalyse.httpHandler.DeviceHttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class AnlageView extends View {

    private int anlageImageWidth;
    private int anlageImageHeight;
    private List<JSONObject> mPosition = new ArrayList<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> schedulerHandler;
    private DeviceHttpRequest device_http;
    private List<Integer> inactiveComponents = new ArrayList<>();

    public AnlageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setActivated(true);
        mPosition = getComponentPositions();
        device_http = new DeviceHttpRequest(context, AppSetting.DeviceAddress);

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

        schedulerHandler = scheduler.scheduleAtFixedRate(getComponentsStatusThread, 0, 3, TimeUnit.SECONDS);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Log.i("Detach", "Detached");
        schedulerHandler.cancel(false);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        anlageImageWidth = getWidth();
        anlageImageHeight = (int) (anlageImageWidth * AppSetting.AnlageImageHeightWidthRatio);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth());
    }

    // override onDraw
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // draw the mPath with the mPaint on the canvas when onDraw
        Drawable d = ResourcesCompat.getDrawable(getResources(), R.drawable.abfuellanlage, null);
        d.setBounds(0, 0, anlageImageWidth, anlageImageHeight);
        d.draw(canvas);
        if (!inactiveComponents.isEmpty()) {
            Paint paint = new Paint();
            paint.setColor(Color.RED);
            paint.setAlpha(80);
            List<Integer> relComponents = getRelComponentIDs(inactiveComponents);
            for (int inactiveComponent : inactiveComponents) {
                JSONObject componentPosition = mPosition.get(inactiveComponent - 1);
                try {
                    canvas.drawRoundRect((float) componentPosition.getDouble("x_begin") * anlageImageWidth, (float) componentPosition.getDouble("y_begin") * anlageImageHeight, (float) componentPosition.getDouble("x_end") * anlageImageWidth, (float) componentPosition.getDouble("y_end") * anlageImageHeight, 10, 10, paint);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            for (int relComponent : relComponents) {
                paint.setColor(Color.YELLOW);
                paint.setAlpha(80);
                JSONObject componentPosition = mPosition.get(relComponent - 1);
                try {
                    canvas.drawRoundRect((float) componentPosition.getDouble("x_begin") * anlageImageWidth, (float) componentPosition.getDouble("y_begin") * anlageImageHeight, (float) componentPosition.getDouble("x_end") * anlageImageWidth, (float) componentPosition.getDouble("y_end") * anlageImageHeight, 10, 10, paint);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //override the onTouchEvent
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // TODO: TouchDown Action
                break;
            case MotionEvent.ACTION_MOVE:
                // TODO: TouchMove Action current do
                break;
            case MotionEvent.ACTION_UP:
                try {
                    checkTouchArea(event.getX(), event.getY());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
        return true;
    }

    private void checkTouchArea(float x, float y) throws JSONException {
        for (int i = 1; i <= mPosition.size(); i++) {
            double mRangeXBegin = (mPosition.get(i - 1)).getDouble("x_begin") * anlageImageWidth;
            double mRangeYBegin = (mPosition.get(i - 1)).getDouble("y_begin") * anlageImageHeight;
            double mRangeXEnd = (mPosition.get(i - 1)).getDouble("x_end") * anlageImageWidth;
            double mRangeYEnd = (mPosition.get(i - 1)).getDouble("y_end") * anlageImageHeight;
            if (x > mRangeXBegin && x < mRangeXEnd && y > mRangeYBegin && y < mRangeYEnd) {
                ComponentInfoDialog dialog = new ComponentInfoDialog();
                dialog.setComponentInfo(i, checkComponentStatus(i));
                dialog.setCancelable(true);
                dialog.show(((Activity) getContext()).getFragmentManager(), "AutoConnectDevice");
                break;
            }
        }
    }

    private boolean checkComponentStatus(int componentID) {
        for (int _componentID : inactiveComponents ) {
            if (_componentID == componentID) return false;
        }
        return true;
    }

    private final Runnable getComponentsStatusThread = new Runnable() {
        public void run() {
            device_http.getComponentsStatus(componentsStatusSuccessHandler, componentsStatusErrorHandler);
        }
    };

    private final Response.Listener<JSONArray> componentsStatusSuccessHandler = new Response.Listener<JSONArray>() {
        @Override
        public void onResponse(JSONArray response) {
            try {
                inactiveComponents.clear();
                for (int i = 0; i < response.length(); i++) {
                    if (!response.getJSONObject(i).getString("status").equals("active")) {
                        inactiveComponents.add(response.getJSONObject(i).getInt("component_id"));
                    }
                }
                invalidate();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    private final Response.ErrorListener componentsStatusErrorHandler = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            LogRecorder.Log("Get components states failed!", getContext());
        }
    };

    private List<JSONObject> getComponentPositions() {
        List<JSONObject> mPosition = new ArrayList<>();
        try {
            for (String componentPositionString : getResources().getStringArray(R.array.component_positions)) {
                JSONObject componentPositionJSONObject = new JSONObject(componentPositionString);
                mPosition.add(componentPositionJSONObject);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mPosition;
    }

    private List<Integer> getRelComponentIDs(List<Integer> inactiveComponentIDs) {
        List<Integer> componentIDs = new ArrayList<>();
        for (int componentID : inactiveComponentIDs) {
            int subsystemID = 0;
            for (String rel : getResources().getStringArray(R.array.subsystem_component_rel)) {
                try {
                    JSONObject _rel = new JSONObject(rel);
                    if (_rel.getInt("component_id") == componentID) {
                        subsystemID = _rel.getInt("subsystem_id");
                        break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            for (String rel : getResources().getStringArray(R.array.subsystem_component_rel)) {
                try {
                    JSONObject _rel = new JSONObject(rel);
                    if (_rel.getInt("subsystem_id") == subsystemID && _rel.getInt("component_id") != componentID && !componentIDs.contains(_rel.getInt("component_id"))) {
                        componentIDs.add(_rel.getInt("component_id"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        return componentIDs;
    }
}