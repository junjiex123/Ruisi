package xyz.yluo.ruisiapp.checknet;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * Created by free2 on 16-4-13.
 * 检查网络回调
 */
public abstract class CheckNetResponse {
    private Handler handler;
    private Looper looper = null;

    public CheckNetResponse() {
        this(null);
    }

    public CheckNetResponse(Looper looper) {
        this.looper = (looper == null ? Looper.getMainLooper() : looper);
        handler = new ResponderHandler(this, this.looper);
    }

    protected void handleMessage(Message msg) {
        String s =  (String) msg.obj;
        onFinish(msg.what,s);
    }

    public abstract void onFinish(int type,String response);

    final protected void sendFinishMessage(int type,String s) {
        handler.sendMessage(obtainMessage(type, s));
    }

    protected Message obtainMessage(int responseMessageId, Object responseMessageData) {
        return Message.obtain(handler, responseMessageId, responseMessageData);
    }


    /**
     * Avoid leaks by using a non-anonymous handler class.
     */
    private static class ResponderHandler extends Handler {
        private final CheckNetResponse mResponder;

        ResponderHandler(CheckNetResponse mResponder, Looper looper) {
            super(looper);
            this.mResponder = mResponder;
        }

        @Override
        public void handleMessage(Message msg) {
            mResponder.handleMessage(msg);
        }
    }
}
