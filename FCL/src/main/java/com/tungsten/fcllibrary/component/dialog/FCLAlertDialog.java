package com.tungsten.fcllibrary.component.dialog;

import android.content.Context;
import android.text.Spanned;

import androidx.annotation.NonNull;

import com.tungsten.fcl.R;

public class FCLAlertDialog extends FCLDialog {

    private static final int POSITIVE = 0;
    private static final int NEUTRAL = 1;
    private static final int EXTRA = 2;
    private static final int NEGATIVE = 3;

    private String titleString;
    private final FCLAlertDialogState state;

    private ButtonListener positiveListener;
    private ButtonListener negativeListener;
    private ButtonListener neutralListener;
    private ButtonListener extraListener;

    public FCLAlertDialog(@NonNull Context context) {
        super(context);
        state = new FCLAlertDialogState(context.getString(R.string.dialog_info), AlertLevel.INFO.ordinal());
        setContentView(FCLAlertDialogCompose.createView(context, state, this::onAction));
    }

    private void onAction(int action) {
        ButtonListener listener = switch (action) {
            case POSITIVE -> positiveListener;
            case NEUTRAL -> neutralListener;
            case EXTRA -> extraListener;
            case NEGATIVE -> negativeListener;
            default -> null;
        };
        if (listener != null) {
            listener.onClick();
        }
        dismiss();
    }

    public void setAlertLevel(AlertLevel alertLevel) {
        state.setAlertLevelCode(alertLevel.ordinal());
        if (titleString == null) {
            int titleResource = alertLevel == AlertLevel.ALERT ? R.string.dialog_alert : R.string.dialog_info;
            state.setTitle(getContext().getString(titleResource));
        }
    }

    public void setTitle(String title) {
        titleString = title;
        state.setTitle(title);
    }

    public void setMessage(String message) {
        state.setMessage(message);
    }

    public void setMessage(CharSequence message) {
        state.setMessage(message);
    }

    public void setMessage(Spanned message) {
        state.setMessage(message);
    }

    public void setPositiveButton(String text, ButtonListener listener) {
        state.setAction(POSITIVE, text);
        positiveListener = listener;
    }

    public void setNegativeButton(String text, ButtonListener listener) {
        state.setAction(NEGATIVE, text);
        negativeListener = listener;
    }

    public void setNeutralButton(String text, ButtonListener listener) {
        state.setAction(NEUTRAL, text);
        neutralListener = listener;
    }

    public void setExtraButton(String text, ButtonListener listener) {
        state.setAction(EXTRA, text);
        extraListener = listener;
    }

    public void useAutoLink() {
        state.setAutoLinkEnabled(true);
    }

    public static class Builder {

        private Context context;
        private FCLAlertDialog dialog;

        public Builder(Context context) {
            this.context = context;
            dialog = new FCLAlertDialog(context);
        }

        public FCLAlertDialog create() {
            return dialog;
        }

        public Builder setAlertLevel(AlertLevel alertLevel) {
            dialog.setAlertLevel(alertLevel);
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            dialog.setCancelable(cancelable);
            return this;
        }

        public Builder setTitle(String title) {
            dialog.setTitle(title);
            return this;
        }

        public Builder setMessage(String message) {
            dialog.setMessage(message);
            return this;
        }

        public Builder setMessage(CharSequence message) {
            dialog.setMessage(message);
            return this;
        }

        public Builder setPositiveButton(ButtonListener listener) {
            dialog.setPositiveButton(context.getString(R.string.dialog_positive), listener);
            return this;
        }

        public Builder setPositiveButton(String text, ButtonListener listener) {
            dialog.setPositiveButton(text, listener);
            return this;
        }

        public Builder setNegativeButton(ButtonListener listener) {
            dialog.setNegativeButton(context.getString(R.string.dialog_negative), listener);
            return this;
        }

        public Builder setNegativeButton(String text, ButtonListener listener) {
            dialog.setNegativeButton(text, listener);
            return this;
        }

        public Builder setNeutralButton(String text, ButtonListener listener) {
            dialog.setNeutralButton(text, listener);
            return this;
        }

        public Builder setExtraButton(String text, ButtonListener listener) {
            dialog.setExtraButton(text, listener);
            return this;
        }

        public Builder useAutoLink() {
            dialog.useAutoLink();
            return this;
        }
    }

    public enum AlertLevel {
        ALERT,
        INFO
    }

    public interface ButtonListener {
        void onClick();
    }

}
