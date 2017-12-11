package ly.persona.example;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import ly.persona.sdk.MediaView;
import ly.persona.sdk.NativeAdView;

/**
 * Created by Oleg Tarashkevich on 15/11/2017.
 */

public class NativeAdViewSmall extends NativeAdView {
    public NativeAdViewSmall(Context context) {
        super(context);
    }

    public NativeAdViewSmall(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NativeAdViewSmall(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init() {
        Context context = getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.view_native_small, this, true);
        title = (TextView) view.findViewById(R.id.pn_title);
        description = (TextView) view.findViewById(R.id.pn_description);
        appDeveloper = (TextView) view.findViewById(R.id.pn_app_developer);
        icon = (ImageView) view.findViewById(R.id.pn_icon);
        mediaView = (MediaView) view.findViewById(R.id.pn_media_view);
        button = view.findViewById(R.id.pn_button);
        reviews = (TextView) view.findViewById(R.id.pn_reviews);
        ratingBar = (RatingBar) view.findViewById(R.id.pn_rating_bar);
        privacyPolicy =  view.findViewById(R.id.pn_privacy_policy);
    }
}
