package ly.persona.example;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;

import butterknife.BindView;

/**
 * Created by Oleg Tarashkevich on 10/11/2017.
 */

public class BaseActivity extends AppCompatActivity {

    @Nullable @BindView(R.id.custom_param_input_layout) TextInputLayout customParamInputLayout;
    @Nullable @BindView(R.id.custom_param_edit_text) EditText customParamEditText;

    @Nullable @BindView(R.id.info_text_view) public TextView infoTextView;
    @Nullable @BindView(R.id.progress_bar) public ProgressBar progressBar;

    private Toast toast;

    // region Print info
    public void showToast(String text) {
        if (toast != null) {
            toast.cancel();
            toast = null;
        }
        text = getCurrentDate() + " - " + text;
        toast = Toast.makeText(this, text, Toast.LENGTH_LONG);
        toast.show();

        if (infoTextView != null) {
            text = infoTextView.getText().toString() + text + "\n";
            infoTextView.setText(text);
        }
    }

    public String checkInputLayout(boolean checkForEmpty, TextInputLayout textInputLayout, EditText editText) {
        String value = null;
        if (textInputLayout != null && editText != null) {
            value = editText.getText().toString();
            boolean isEmpty = TextUtils.isEmpty(value);
            String error = checkForEmpty && isEmpty ? "Should not be empty" : null;
            textInputLayout.setError(error);
        }

        return value;
    }

    public String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.SSS", Locale.US);
        return dateFormat.format(new java.util.Date());
    }
    // endregion

    protected void sendEmailFile() {

        try {
            File file = new File(getExternalCacheDir() + File.separator + "PersonaLog.txt");

            long legth = file.length();
            if (legth != 0) {

                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                String uriText = "mailto:" + "oleg.tarashkevich@mobexs.com" +
                        "?subject=" + Uri.encode("Personaly logs") +
                        "&body=" + Uri.encode("Text file with logs\n");

                Uri uri = Uri.parse(uriText);
                emailIntent.setData(uri);

                emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                try {
                    Intent new_intent = Intent.createChooser(emailIntent, "Share with");
                    new_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(new_intent);
                } catch (Throwable ex) {
                    Toast.makeText(this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    protected void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    protected void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    protected String getCustomParameter() {
        String param = null;
        if (customParamEditText != null)
            param = customParamEditText.getText().toString();
        return param;
    }
}
