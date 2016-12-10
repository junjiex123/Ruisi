package xyz.yluo.ruisiapp.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import xyz.yluo.ruisiapp.widget.emotioninput.PanelViewRoot;

public class KeyboardUtil {

    public static void showKeyboard(final View view) {
        if (view != null) {
            view.requestFocus();
            InputMethodManager inputManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.showSoftInput(view, 0);
        }

    }

    public static void hideKeyboard(final View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void hideKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void hideKeyboard(Window window) {
        if (window != null) {
            window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }
    }


    private static int LAST_SAVE_KEYBOARD_HEIGHT = 0;

    private static boolean saveKeyboardHeight(final Context context, int keyboardHeight) {
        if (LAST_SAVE_KEYBOARD_HEIGHT == keyboardHeight) {
            return false;
        }

        if (keyboardHeight < 0) {
            return false;
        }

        LAST_SAVE_KEYBOARD_HEIGHT = keyboardHeight;
        Log.d("KeyBordUtil", String.format("save keyboard: %d", keyboardHeight));

        return KeyBoardHeightPreference.save(context, keyboardHeight);
    }

    public static int getKeyboardHeight(final Context context) {
        if (LAST_SAVE_KEYBOARD_HEIGHT == 0) {
            LAST_SAVE_KEYBOARD_HEIGHT = KeyBoardHeightPreference.get(context, DimmenUtils.dip2px(context, 220));
        }
        return LAST_SAVE_KEYBOARD_HEIGHT;
    }

    public static int getValidPanelHeight(final Context context) {
        final int maxPanelHeight = DimmenUtils.dip2px(context, 380);
        final int minPanelHeight = DimmenUtils.dip2px(context, 220);

        int validPanelHeight = getKeyboardHeight(context);

        validPanelHeight = Math.max(minPanelHeight, validPanelHeight);
        validPanelHeight = Math.min(maxPanelHeight, validPanelHeight);
        return validPanelHeight;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public static ViewTreeObserver.OnGlobalLayoutListener attach(final Activity activity, PanelViewRoot target, OnKeyboardShowingListener listener) {
        final ViewGroup contentView = (ViewGroup) activity.findViewById(android.R.id.content);
        final boolean isTranslucentStatus = ViewUtil.isTranslucentStatus(activity);

        // get the screen height.
        final Display display = activity.getWindowManager().getDefaultDisplay();
        final int screenHeight;
        final Point screenSize = new Point();
        display.getSize(screenSize);
        screenHeight = screenSize.y;
        ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener = new KeyboardStatusListener(isTranslucentStatus,
                contentView, target, listener, screenHeight);
        contentView.getViewTreeObserver().addOnGlobalLayoutListener(globalLayoutListener);
        return globalLayoutListener;
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void detach(Activity activity, ViewTreeObserver.OnGlobalLayoutListener l) {
        ViewGroup contentView = (ViewGroup) activity.findViewById(android.R.id.content);
        contentView.getViewTreeObserver().removeOnGlobalLayoutListener(l);
    }

    private static class KeyboardStatusListener implements ViewTreeObserver.OnGlobalLayoutListener {
        private final static String TAG = "KeyboardStatusListener";

        private int previousDisplayHeight = 0;
        private final ViewGroup contentView;
        private final PanelViewRoot panelHeightTarget;
        private final boolean isTranslucentStatus;
        private final int statusBarHeight;
        private boolean lastKeyboardShowing;
        private final OnKeyboardShowingListener keyboardShowingListener;
        private final int screenHeight;

        private boolean isOverlayLayoutDisplayHContainStatusBar = false;

        KeyboardStatusListener(boolean isTranslucentStatus, ViewGroup contentView, PanelViewRoot panelHeightTarget,
                               OnKeyboardShowingListener listener, int screenHeight) {
            this.contentView = contentView;
            this.panelHeightTarget = panelHeightTarget;
            this.isTranslucentStatus = isTranslucentStatus;
            this.statusBarHeight = ViewUtil.getStatusBarHeight(contentView.getContext());
            this.keyboardShowingListener = listener;
            this.screenHeight = screenHeight;
        }

        @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
        @Override
        public void onGlobalLayout() {
            final View userRootView = contentView.getChildAt(0);
            final View actionBarOverlayLayout = (View) contentView.getParent();
            Rect r = new Rect();

            final int displayHeight;
            if (isTranslucentStatus) {
                actionBarOverlayLayout.getWindowVisibleDisplayFrame(r);

                final int overlayLayoutDisplayHeight = (r.bottom - r.top);

                if (!isOverlayLayoutDisplayHContainStatusBar) {
                    isOverlayLayoutDisplayHContainStatusBar = overlayLayoutDisplayHeight == screenHeight;
                }

                if (!isOverlayLayoutDisplayHContainStatusBar) {
                    displayHeight = overlayLayoutDisplayHeight + statusBarHeight;
                } else {
                    displayHeight = overlayLayoutDisplayHeight;
                }

            } else {
                userRootView.getWindowVisibleDisplayFrame(r);
                displayHeight = (r.bottom - r.top);
            }

            calculateKeyboardHeight(displayHeight);
            calculateKeyboardShowing(displayHeight);

            previousDisplayHeight = displayHeight;
        }

        private void calculateKeyboardHeight(final int displayHeight) {
            // first result.
            if (previousDisplayHeight == 0) {
                previousDisplayHeight = displayHeight;
                panelHeightTarget.refreshHeight(KeyboardUtil.getValidPanelHeight(getContext()));
                return;
            }

            int keyboardHeight;
            keyboardHeight = Math.abs(displayHeight - previousDisplayHeight);
            // no change.
            if (keyboardHeight <= DimmenUtils.dip2px(getContext(), 80)) {
                return;
            }

            Log.d(TAG, String.format("pre display height: %d display height: %d keyboard: %d ",
                    previousDisplayHeight, displayHeight, keyboardHeight));
            if (keyboardHeight == this.statusBarHeight) {
                Log.w(TAG, String.format("On global layout change get keyboard height just equal" +
                        " statusBar height %d", keyboardHeight));
                return;
            }

            // save the keyboardHeight
            boolean changed = KeyboardUtil.saveKeyboardHeight(getContext(), keyboardHeight);
            if (changed) {
                final int validPanelHeight = KeyboardUtil.getValidPanelHeight(getContext());
                if (this.panelHeightTarget.getHeight() != validPanelHeight) {
                    this.panelHeightTarget.refreshHeight(validPanelHeight);
                }
            }
        }

        private int maxOverlayLayoutHeight;

        private void calculateKeyboardShowing(final int displayHeight) {
            boolean isKeyboardShowing;
            final View actionBarOverlayLayout = (View) contentView.getParent();
            final int actionBarOverlayLayoutHeight = actionBarOverlayLayout.getHeight() - actionBarOverlayLayout.getPaddingTop();

            final int phoneDisplayHeight = contentView.getResources().getDisplayMetrics().heightPixels;
            if (!isTranslucentStatus && phoneDisplayHeight == actionBarOverlayLayoutHeight) {
                Log.w(TAG, String.format("skip the keyboard status calculate, the current" +
                                " activity is paused. and phone-display-height %d," +
                                " root-height+actionbar-height %d", phoneDisplayHeight,
                        actionBarOverlayLayoutHeight));
                return;

            }
            if (maxOverlayLayoutHeight == 0) {
                isKeyboardShowing = lastKeyboardShowing;
            } else {
                isKeyboardShowing = displayHeight < maxOverlayLayoutHeight - DimmenUtils.dip2px(getContext(), 80);
            }

            maxOverlayLayoutHeight = Math.max(maxOverlayLayoutHeight, actionBarOverlayLayoutHeight);

            if (lastKeyboardShowing != isKeyboardShowing) {
                Log.d(TAG, String.format("displayHeight %d actionBarOverlayLayoutHeight %d " +
                                "keyboard status change: %B",
                        displayHeight, actionBarOverlayLayoutHeight, isKeyboardShowing));
                this.panelHeightTarget.onKeyboardShowing(isKeyboardShowing);
                if (keyboardShowingListener != null) {
                    keyboardShowingListener.onKeyboardShowing(isKeyboardShowing);
                }
            }

            lastKeyboardShowing = isKeyboardShowing;

        }

        private Context getContext() {
            return contentView.getContext();
        }
    }

    public interface OnKeyboardShowingListener {
        void onKeyboardShowing(boolean isShowing);
    }
}