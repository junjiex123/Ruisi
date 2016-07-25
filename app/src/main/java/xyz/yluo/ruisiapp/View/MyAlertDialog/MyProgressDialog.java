package xyz.yluo.ruisiapp.View.MyAlertDialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.Transformation;

import xyz.yluo.ruisiapp.R;


public class MyProgressDialog extends Dialog{
    private View mDialogView;
    private AnimationSet mModalInAnim;
    private AnimationSet mModalOutAnim;
    private boolean mCloseFromCancel;
    private Animation mOverlayOutAnim;

    private Animation operatingAnim, eye_left_Anim, eye_right_Anim;
    private View mouse, eye_left, eye_right;
    private EyelidView eyelid_left, eyelid_right;
    private GraduallyTextView loadingText;
    private String loadText = "L O A D I N G ...";


    public MyProgressDialog(Context context) {
        super(context, R.style.alert_dialog);
        setCancelable(true);
        setCanceledOnTouchOutside(false);
        mModalInAnim = (AnimationSet) OptAnimationLoader.loadAnimation(getContext(), R.anim.dialog_zoom_in);
        mModalOutAnim = (AnimationSet) OptAnimationLoader.loadAnimation(getContext(), R.anim.dialog_zoom_out);
        mModalOutAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mDialogView.setVisibility(View.GONE);
                mDialogView.post(new Runnable() {
                    @Override
                          public void run() {
                    if (mCloseFromCancel) {
                        MyProgressDialog.super.cancel();
                    } else {
                        MyProgressDialog.super.dismiss();
                    }
                }
            });
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

            }
        });


        // dialog overlay fade out
        mOverlayOutAnim = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                WindowManager.LayoutParams wlp = getWindow().getAttributes();
                wlp.alpha = 1 - interpolatedTime;
                getWindow().setAttributes(wlp);
            }
        };
        mOverlayOutAnim.setDuration(120);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_dialog);
        mDialogView = getWindow().getDecorView().findViewById(android.R.id.content);
        operatingAnim = new RotateAnimation(360f, 0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        operatingAnim.setRepeatCount(Animation.INFINITE);
        operatingAnim.setDuration(2000);

        eye_left_Anim = new RotateAnimation(360f, 0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        eye_left_Anim.setRepeatCount(Animation.INFINITE);
        eye_left_Anim.setDuration(2000);

        eye_right_Anim = new RotateAnimation(360f, 0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        eye_right_Anim.setRepeatCount(Animation.INFINITE);
        eye_right_Anim.setDuration(2000);

        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);
        eye_left_Anim.setInterpolator(lin);
        eye_right_Anim.setInterpolator(lin);

        View view = getWindow().getDecorView();
        mouse = view.findViewById(R.id.mouse);
        eye_left = view.findViewById(R.id.eye_left);
        eye_right = view.findViewById(R.id.eye_right);
        eyelid_left = (EyelidView) view.findViewById(R.id.eyelid_left);
        eyelid_left.setColor(Color.parseColor("#d0ced1"));
        eyelid_left.setFromFull(true);
        eyelid_right = (EyelidView) view.findViewById(R.id.eyelid_right);
        eyelid_right.setColor(Color.parseColor("#d0ced1"));
        eyelid_right.setFromFull(true);
        loadingText = (GraduallyTextView) view.findViewById(R.id.graduallyTextView);
        loadingText.setText(loadText);
        operatingAnim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }


                    @Override
                    public void onAnimationEnd(Animation animation) {
                    }


                    @Override
                    public void onAnimationRepeat(Animation animation) {
                        eyelid_left.resetAnimator();
                        eyelid_right.resetAnimator();
                    }
                });
    }


    public MyProgressDialog setLoadingText (String text) {
        loadText = text;
        return this;
    }


    protected void onStart() {
        mDialogView.startAnimation(mModalInAnim);
        mouse.setAnimation(operatingAnim);
        eye_left.setAnimation(eye_left_Anim);
        eye_right.setAnimation(eye_right_Anim);
        eyelid_left.startLoading();
        eyelid_right.startLoading();
        loadingText.startLoading();
    }


    /**
     * The real Dialog.cancel() will be invoked async-ly after the animation finishes.
     */
    @Override
    public void cancel() {
        dismissWithAnimation(true);
    }



    private void dismissWithAnimation(boolean fromCancel) {
        mCloseFromCancel = fromCancel;
        mDialogView.startAnimation(mModalOutAnim);
        loadingText.startAnimation(mOverlayOutAnim);
    }
}